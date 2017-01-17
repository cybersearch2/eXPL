/**
    Copyright (C) 2014  www.cybersearch2.com.au

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/> */
package au.com.cybersearch2.classy_logic;

import java.util.Collections;
import java.util.HashMap;
//import java.util.IllformedLocaleException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.VariableType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.QueryType;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * Scope
 * A namespace for axioms, templates and queries
 * @author Andrew Bowley
 * 28 Dec 2014
 */
public class Scope 
{
    /** scope literal */
    static final protected String SCOPE = "scope";
    /** Scope name - must be unique to all scopes */
    protected String name;
    /** Map QuerySpec objects to query name */
    protected Map<String, QuerySpec> querySpecMap;
    /** Local pattern assembler */
    protected ParserAssembler parserAssembler;
    /** Scopes container */
    protected Map<String, Scope> scopeMap;
    /** A scope locale can be different to the system default */
    protected Locale locale;
    
    static protected Map<String, Object> EMPTY_PROPERTIES;

    static
    {
        EMPTY_PROPERTIES = Collections.emptyMap();
    }
    
    /**
     * Construct a Scope object with access to the global scope
     * @param scopeMap Scopes container
     * @param name Scope name - must be unique to all scopes
     * @param properties Scope properties 
     */
    public Scope(Map<String, Scope> scopeMap, String name, Map<String, Object> properties) 
    {
        this.scopeMap = scopeMap;
        this.name = name;
        boolean hasProperties = (properties != null) && (properties.size() > 0);
        if (hasProperties)
        {
            Object language = properties.get(QueryProgram.LANGUAGE);
            if (language != null)
                // Uncomment following if SE7 supported
                //try 
                //{
                    locale = getLocale(properties, language.toString());
                //}
                //catch (IllformedLocaleException e)
                //{
                //  throw new ExpressionException("Scope \"" + name + "\" invalid Locale settings", e);
                //}
        }
        if (locale == null)
            // Uncomment following if SE7 supported
            locale = Locale.getDefault(/*Locale.Category.FORMAT*/);
        querySpecMap = new HashMap<String, QuerySpec>();
        parserAssembler = new ParserAssembler(this);
        if (properties == null)
            properties = Collections.emptyMap();
        if (QueryProgram.GLOBAL_SCOPE.equals(name) && properties.isEmpty())
        {
            properties = new HashMap<String, Object>();
            properties.put("language", locale.getLanguage());
            properties.put("region", locale.getCountry());
        }
        if (!properties.isEmpty())
            addScopeList(properties);
    }
    
    /**
     * Returns locale of scopet
     * @return Locale object
     */
    public Locale getLocale() 
    {
        return locale;
    }

    /**
     * Set locale of this scope
     * @param locale Locale objedt
     */
    public void setLocale(Locale locale) 
    {
        this.locale = locale;
    }

    /**
     * Complete construction of a query specification according to type of query and position in chain. 
     * If the need is detected, a new head query specification will be returned. This is used when a 
     * calculator is found as the head query and a logic query needs to be inserted before it to feed
     * the calculator axioms one by one.
     * Intended only for use by compiler. 
     * @param querySpec Query specification under construction
     * @param firstKeyname Keyname object at head of query chain
     * @param keynameCount Number of keynames in chain so far
     * @param properties Query parameters. Optional, so may be empty
     * @return QuerySpec The query specification object passed as a parameter or a new head query specifiection object  
     */
    public QuerySpec buildQuerySpec(QuerySpec querySpec, KeyName firstKeyname, int keynameCount, Map<String, Object> properties)
    {
           QualifiedName templateName = firstKeyname.getTemplateName();
           Template firstTemplate = getTemplate(templateName);
           if (!firstTemplate.isCalculator())
               // If the head query is not a calculator, then the build is complete
               return querySpec;
           // Now deal with the specifics of a calculator query
           // Query type
           querySpec.setQueryType(QueryType.calculator);
           // Query parameters specified as properties
           if (properties.size() > 0)
              querySpec.putProperties(firstKeyname, properties);
           String axiomName = firstKeyname.getAxiomKey().getName();
           // Check if logic query needs to be inserted in front of head calculator
           if (!querySpec.isHeadQuery() || axiomName.isEmpty())
              return querySpec;
           // Create new head logic query where axiom and template key names are same
           // The original query specification will be discarded
           QuerySpec headQuerySpec = new QuerySpec(querySpec.getName());   
           headQuerySpec.addKeyName(new KeyName(axiomName, axiomName));
           // Append new calculator query spec to head query spec.
           QuerySpec chainQuerySpec = headQuerySpec.chain();
           // Create new keyname with empty axiom key to indicate get axiom from solution
           KeyName calculateKeyname = new KeyName(firstKeyname.getTemplateName());
           chainQuerySpec.addKeyName(calculateKeyname);
           chainQuerySpec.setQueryType(QueryType.calculator);
           if (properties.size() > 0)
              chainQuerySpec.putProperties(calculateKeyname, properties);
           firstTemplate.setKey(axiomName);
           // Check for logic query template already exists. Not expected to exist.
           // The template name is taken from the axiom key
           QualifiedName qualifiedTemplateName = new QualifiedTemplateName(getAlias(), axiomName);
           Template logicTemplate = findTemplate(qualifiedTemplateName);
           if ((logicTemplate == null) && !getAlias().isEmpty())
               // Use global scope template if scope specified
               logicTemplate = getGlobalParserAssembler().getTemplate(new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, axiomName));
           if (logicTemplate != null)
               return headQuerySpec;
           // Create new logic query template which will populated with terms to match axiom terms at start of query
           logicTemplate = parserAssembler.createTemplate(qualifiedTemplateName, false);
           logicTemplate.setKey(axiomName);
           return headQuerySpec;
        }
    
    /**
     * Add specification of query Axiom(s) and Template(s) names
     * @param querySpec QuerySpec object
     */
    public void addQuerySpec(QuerySpec querySpec)
    {
        querySpecMap.put(querySpec.getName(), querySpec);
    }

    /**
     * Returns scope name
     * @return String
     */
    public String getName() 
    {
        return name;
    }

    /**
     * Returns name of scope or alias if global scope
     * @return Alias
     */
    public String getAlias()
    {
        return name.equals(QueryProgram.GLOBAL_SCOPE) ? QualifiedName.EMPTY : name;
    }
    
    /**
     * Returns scope specified by name.
     * @param name
     * @return Scope object
     * @throws IllegalArgumentException if scope does not exist
     */
    public Scope getScope(String name)
    {
        Scope scope = scopeMap.get(name);
        if (scope == null)
            throw new IllegalArgumentException("Scope \"" + name + "\" does not exist");
        return scope;
    }

    /**
     * Returns scope specified by name.
     * @param name
     * @return Scope object or null if not foune
     */
    public Scope findScope(String name)
    {
        return scopeMap.get(name);
    }


    /**
     * Returns local parser assembler
     * @return ParserAssembler object
     */
    public ParserAssembler getParserAssembler() 
    {
        return parserAssembler;
    }

    /**
     * Returns Query specification referenced by name
     * @param querySpecName
     * @return QuerySpec object
     */
    public QuerySpec getQuerySpec(String querySpecName) 
    {
        return querySpecMap.get(querySpecName);
    }

    /**
     * Returns query specification map
     * @return Map with name keys and QuerySpec values
     */
    public Map<String, QuerySpec> getQuerySpecMap()
    {
        return Collections.unmodifiableMap(querySpecMap);
    }

    /**
     * Returns axiom source for specified axiom name
     * @param axiomKey
     * @return AxiomSource object
     */
    public AxiomSource getAxiomSource(QualifiedName axiomKey)
    {
        AxiomSource axiomSource = findAxiomSource(axiomKey);
        if (axiomSource == null)
            throw new IllegalArgumentException("Axiom \"" + axiomKey.toString() + "\" does not exist");
        return axiomSource;
    }

    /**
     * Returns axiom source for specified axiom name
     * @param axiomKey Axiom qualified name
     * @return AxiomSource object or null if it does not exist in scope
     */
    public AxiomSource findAxiomSource(QualifiedName axiomKey)
    {
        String scopeName = axiomKey.getScope();
        if (!scopeName.isEmpty() && (scopeMap.get(scopeName) == null))
            throw new ExpressionException("Scope \"" + scopeName + "\"  in axiom key \"" + axiomKey.toString() + "\" is not found");
        AxiomSource axiomSource = parserAssembler.getAxiomSource(axiomKey);
        if (axiomSource != null)
            return axiomSource;
        QualifiedName qname = QualifiedName.parseName(axiomKey.getName(), parserAssembler.getOperandMap().getQualifiedContextname());
        axiomSource = parserAssembler.getAxiomSource(qname);
        if ((axiomSource == null) && !qname.getTemplate().isEmpty())
        {
            qname.clearTemplate();
            axiomSource = parserAssembler.getAxiomSource(qname);
        }
        if ((axiomSource == null) && (!name.equals(QueryProgram.GLOBAL_SCOPE)))
        {
            axiomSource = getGlobalParserAssembler().getAxiomSource(qname);
            if (axiomSource == null)
            {
                qname = QualifiedName.parseGlobalName(axiomKey.getName());
                axiomSource = getGlobalParserAssembler().getAxiomSource(qname);
            }
        }
        return axiomSource;
    }

    /**
     * Returns template with specified name. Will search first in own scope and then in global scope if not found
     * @param templateName Name of template
     * @return Template object or null if template not found
     */
    public Template getTemplate(QualifiedName templateName)
    {
        Template template = findTemplate(templateName);
        if ((template == null) && !templateName.getScope().isEmpty())
            // Use global scope template if scope specified
            template = getGlobalParserAssembler().getTemplate(new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, templateName.getTemplate()));
        if (template == null)
            throw new IllegalArgumentException("Template \"" + templateName.toString() + "\" does not exist");
        return template;
    }

    /**
     * Returns template with specified name
     * @param templateName
     * @return Template object or null if template not found
     */
    public Template findTemplate(QualifiedName templateName)
    {
        String scopeName = templateName.getScope();
        if (!scopeName.isEmpty() && (scopeMap.get(scopeName) == null))
            throw new ExpressionException("Scope \"" + scopeName + "\"  in template key \"" + templateName.toString() + "\" is not found");
        Template template = parserAssembler.getTemplate(templateName);
        if ((template == null) && templateName.getScope().isEmpty())
        {
            QualifiedName localname = new QualifiedTemplateName(name, templateName.getTemplate());
            template = parserAssembler.getTemplate(localname);
        }
        if (template == null)
            template = getGlobalParserAssembler().getTemplate(templateName);
        if (template == null)
        {   // Create template for resource binding, if one exists
            AxiomProvider axiomProvider = null;
            QualifiedName globalResourceName = null;
            QualifiedName resourceName = parserAssembler.getResourceName(templateName);
            if (resourceName != null) 
            {
                axiomProvider = parserAssembler.getAxiomProvider(resourceName);
                if (axiomProvider != null)
                    return createResourceTemplate(templateName, parserAssembler);
            }
            else if (!templateName.getScope().isEmpty())
            {
                globalResourceName =  getGlobalParserAssembler().getResourceName(templateName);
                if (globalResourceName != null) 
                    axiomProvider =  getGlobalParserAssembler().getAxiomProvider(globalResourceName);
                if (axiomProvider != null)
                    return createResourceTemplate(templateName, getGlobalParserAssembler());
            }
        }
        return template;
    }

    /**
     * Returns object containing all axiom listeners belonging to this scope
     * @return  Unmodifiable AxiomListener map object
     */
    public Map<QualifiedName, List<AxiomListener>> getAxiomListenerMap()
    {
        Map<QualifiedName, List<AxiomListener>> axiomListenerMap = null;
        if ((!name.equals(QueryProgram.GLOBAL_SCOPE)) && (getGlobalParserAssembler().getAxiomListenerMap().size() > 0))
            axiomListenerMap = getGlobalParserAssembler().getAxiomListenerMap();
        if (parserAssembler.getAxiomListenerMap().size() > 0)
        {
            if (axiomListenerMap != null)
            {
                Map<QualifiedName, List<AxiomListener>> newAxiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
                newAxiomListenerMap.putAll(axiomListenerMap);
                axiomListenerMap = newAxiomListenerMap;
                axiomListenerMap.putAll(parserAssembler.getAxiomListenerMap());
            }
            else
                axiomListenerMap = parserAssembler.getAxiomListenerMap();
        }
        return axiomListenerMap == null ? null : Collections.unmodifiableMap(axiomListenerMap);
    }

    /**
     * Returns ItemList for specified list name 
     * @param listName
     * @return ItemList object
     */
    public ItemList<?> getItemList(String listName) 
    {
        // Look first in local scope, then if not found, try global scope
        String scopeName = name.equals(QueryProgram.GLOBAL_SCOPE) ? QualifiedName.EMPTY : name;
        QualifiedName qualifiedListName = new QualifiedName(scopeName, listName);
        ItemList<?> itemList = parserAssembler.getOperandMap().getItemList(qualifiedListName);
        if ((itemList == null) && !scopeName.isEmpty())
        {
            qualifiedListName = new QualifiedName(listName);
            itemList = getGlobalParserAssembler().getOperandMap().getItemList(qualifiedListName);
        }
        return itemList;
    }

    /**
     * Returns context of this scope
     * @param isFunctionScope Flag to indicate function scope
     * @return ScopeContext object
     */
    public ScopeContext getContext(boolean isFunctionScope) 
    {
        return new ScopeContext(this, isFunctionScope);
    }

    /**
     * Returns global scope
     * @return Scope, which is this object if global scope not already set
     */
    public Scope getGlobalScope() 
    {
        return name.equals(QueryProgram.GLOBAL_SCOPE) ? this : scopeMap.get(QueryProgram.GLOBAL_SCOPE);
    }

    /**
     * Returns map which provides access to result lists as iterables
     * @return Container which maps fully qualified name to list iterable
     */
    public Map<QualifiedName, Iterable<Axiom>> getListMap() 
    {
        Map<QualifiedName, Iterable<Axiom>> listMap = new HashMap<QualifiedName, Iterable<Axiom>>();
        parserAssembler.copyLists(listMap);
        return listMap;
    }

    /** 
     * Returns container with axioms in scope 
     * @return Container which maps QualifiedName to Axiom
     */
    public Map<QualifiedName, Axiom> getAxiomMap()
    {
        Map<QualifiedName, Axiom> axiomMap = new HashMap<QualifiedName, Axiom>();
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
        {
            getGlobalParserAssembler().getOperandMap().copyAxioms(axiomMap);
            parserAssembler.getOperandMap().copyAxioms(axiomMap);
        }
        else
            parserAssembler.getOperandMap().copyAxioms(axiomMap);
        return axiomMap;
    }

    /**
     * Returns Local specified by properties and language code
     * @param properties Script, region and variant values 
     * @param language Langauge code eg. "de" for Germany
     * @return Locale object
     * @throws ExpressionException if locale parameters are invalid
     */
    protected Locale getLocale(Map<String, Object> properties, String language)
    {
        // Uncomment following if SE7 supported
        //Object script = properties.get(QueryProgram.SCRIPT);
        Object region = properties.get(QueryProgram.REGION);
        Object variant = properties.get(QueryProgram.VARIANT);
        Locale locale = null;
        //if (script == null)
        //{
            if ((region == null) && (variant == null))
                 locale = new Locale(language);
            else if (region != null)
            {
                if (variant == null)
                     locale = new Locale(language, region.toString());
                else
                     locale = new Locale(language, region.toString(), variant.toString());
            }
        //}
        //else
        //{
        //  Locale.Builder builder = new Locale.Builder();
        //  builder.setLanguage(language);
        //  if (region != null)
        //  {
        //      builder.setRegion(region.toString());
        //      if (variant != null)
        //          builder.setVariant(variant.toString());
        //  }
        //  builder.setScript(script.toString());
        //  locale = builder.build();
        //}
        if (locale == null)
            throw new ExpressionException("Scope \"" + name + "\" invalid Locale settings combination for language " + language);
        return locale;
    }

    /**
     * Add locale listener for local axiom identified by key. The supplied axiom listener is
     * notified of every change of scope.
     * @param qualifiedAxiomName Local axiom qualified name
     * @param axiomListener The local axiom listener
     */
    public void addLocalAxiomListener(final QualifiedName qualifiedAxiomName, final AxiomListener axiomListener) 
    {
        // Register locale listener with Global scope in which all local axioms must be declared
        final ParserAssembler parserAssembler = getGlobalScope().getParserAssembler();
        LocaleListener localeListener = new LocaleListener(){

            /**
             * Assign backing axiom to local list. If no axiom source is found, create one
             * in which each item is "unknown"
             */
            @Override
            public void onScopeChange(Scope scope) 
            {
                Axiom localAxiom = null;
                QualifiedName qname = new QualifiedName(scope.getName(), qualifiedAxiomName.getName());
                AxiomSource axiomSource = scope.getParserAssembler().getAxiomSource(qname);
                if (axiomSource == null)
                    axiomSource = parserAssembler.getAxiomSource(qname);
                if (axiomSource == null)
                {
                    axiomSource = getGlobalParserAssembler().getAxiomSource(qualifiedAxiomName);
                    if (axiomSource != null)
                        localAxiom = createUnknownAxiom(qname.toString(), axiomSource.getAxiomTermNameList());
                    else
                        throw new ExpressionException("Axiom source \"" + qualifiedAxiomName.toString() + "\" not found");
                }
                if (localAxiom == null)
                {
                    Iterator<Axiom> iterator = axiomSource.iterator();
                    if (iterator.hasNext())
                        localAxiom = iterator.next();
                    else
                        localAxiom = createUnknownAxiom(qname.toString(), axiomSource.getAxiomTermNameList());
                }
                axiomListener.onNextAxiom(qualifiedAxiomName, localAxiom);
            }
        };
        // If the local is declared inside a scope, then it's scope never changes and a LocaleListener is not required
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
            localeListener.onScopeChange(this);
        else
            parserAssembler.registerLocaleListener(localeListener); 
    }

    /**
     * Notify change of scope
     */
    public void notifyChange() 
    {
        // Notify Locale listeners in Global scope of scope locale
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
            getGlobalParserAssembler().onScopeChange(this);
    }

    /**
     * Returns ParserAssembler belonging to the global scope
     * @return ParserAssembler object
     */
    public ParserAssembler getGlobalParserAssembler()
    {
        return scopeMap.get(QueryProgram.GLOBAL_SCOPE).getParserAssembler();
    }

    /**
     * Create placeholder axiom with "unknown" items
     * @param axiomName
     * @param termNameList List of term names
     * @return Axiom object
     */
    private Axiom createUnknownAxiom(String axiomName, List<String> termNameList)
    {
        Axiom axiom = new Axiom(axiomName);
        Unknown unknown = new Unknown();
        for (String termName: termNameList)
            axiom.addTerm(new Parameter(termName, unknown));
        return axiom;
    }

    /**
     * Add "scope" builtin term list to access scope properties
     * @param properties Scope properties
     */
    private void addScopeList(Map<String, Object> properties)
    {   // Add axiom to ParserAssembler
        QualifiedName qname = new QualifiedName(name, SCOPE);
        parserAssembler.createAxiom(qname);
        for (String termName: properties.keySet())
        {
            parserAssembler.addAxiom(qname, new Parameter(termName, properties.get(termName)));
            parserAssembler.addAxiomTermName(qname, termName);
        }
        parserAssembler.saveAxiom(qname);
        // Create scope term list 
        VariableType varType = new VariableType(OperandType.TERM);
        varType.setProperty(VariableType.AXIOM_KEY, qname);
        AxiomTermList localList = new AxiomTermList(qname, qname);
        parserAssembler.registerLocalList(localList);
        parserAssembler.getOperandMap().addItemList(qname, localList);
    }
 
    /**
     * Create template for for term list bound to resource.
     * The termplate terms are simply named variables.
     * An axiom declaration must exist with same name as template.
     * Preconditions: A template with specified name does not already exist
     * and a term name list is defined for axiom declaration.
     * @param templateName Qualified template name
     * @param parserAssembler ParserAssembler object for scope identified in temmplate name
     * @return Template object
     */
    private Template createResourceTemplate(QualifiedName templateName, ParserAssembler parserAssembler)
    {   // Create qualified axiom  name 
        QualifiedName qualifiedAxiomName = new QualifiedName(templateName.getScope(),  templateName.getTemplate());
        // Get term names
        List<String> termNameList = parserAssembler.getAxiomTermNameList(qualifiedAxiomName);
        if ((termNameList == null) && !templateName.getScope().isEmpty())
        {
            qualifiedAxiomName.clearScope();
            termNameList = parserAssembler.getAxiomTermNameList(qualifiedAxiomName);
        }
        if ((termNameList != null) && !termNameList.isEmpty())
        {
            Template template = parserAssembler.createTemplate(templateName, false);
            for (String termName: termNameList)
            {
                QualifiedName qname = parserAssembler.getContextName(termName);
                template.addTerm(new Variable(qname));
            }
            return template;
        }
        return null;
    }
}
