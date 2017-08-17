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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
//import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.classy_logic.axiom.LocalAxiomListener;
import au.com.cybersearch2.classy_logic.compile.AxiomAssembler;
import au.com.cybersearch2.classy_logic.compile.ListAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserTask;
import au.com.cybersearch2.classy_logic.compile.TemplateAssembler;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
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
public class Scope implements DebugTarget 
{
    /** scope literal */
    static final protected String SCOPE = "scope";
    /** Region locale key literal */
    static final protected String REGION_KEY = "region";
    /** Language locale key literal */
    static final protected String LANGUAGE_KEY = "language";
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
    /** List of registerd debug targets */
    protected List<DebugTarget> debugTargetList;
    
    static protected Map<String, Object> EMPTY_PROPERTIES;

    static
    {
        EMPTY_PROPERTIES = Collections.emptyMap();
    }
    
    /**
     * Construct a Scope object
     * @param scopeMap Scopes container for access to all scopes
     * @param name Scope name - must be unique to all scopes
     * @param properties Scope properties - may be empty
     */
    public Scope(Map<String, Scope> scopeMap, String name, Map<String, Object> properties) 
    {
        this.scopeMap = scopeMap;
        this.name = name;
        if (!setLocale(properties))
            // Uncomment following if SE7 supported
            locale = Locale.getDefault(/*Locale.Category.FORMAT*/);
        querySpecMap = new HashMap<String, QuerySpec>();
        parserAssembler = new ParserAssembler(this);
        if (properties == null)
            properties = Collections.emptyMap();
        if (QueryProgram.GLOBAL_SCOPE.equals(name) && properties.isEmpty())
        {   // Global scope is assigned the default locale if not set by properties
            properties = new HashMap<String, Object>();
            properties.put(LANGUAGE_KEY, locale.getLanguage());
            properties.put(REGION_KEY, locale.getCountry());
        }
        if (!properties.isEmpty())
            addScopeList(properties);
        debugTargetList = new ArrayList<DebugTarget>();
    }
 
    /**
     * Returns set of scope names
     * @return String set
     */
    public Set<String> getScopeNames()
    {
        return scopeMap.keySet();
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
     * Update scope properties - only applicable to global scope
     * @param properties Key=value pairs
     */
    public void updateProperties(Map<String, Object> properties)
    {
        setLocale(properties);
        if (!properties.containsKey(LANGUAGE_KEY))
            properties.put("language", locale.getLanguage());
        if (!properties.containsKey(REGION_KEY))
            properties.put("region", locale.getCountry());
        addScopeList(properties);
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
     * @param termList Query parameters. Optional, so may be empty
     * @return QuerySpec The query specification object passed as a parameter or a new head query specifiection object  
     */
    public QuerySpec buildQuerySpec(QuerySpec querySpec, KeyName firstKeyname, int keynameCount, List<Term> termList)
    {
           QualifiedName templateName = firstKeyname.getTemplateName();
           Template firstTemplate = getTemplate(templateName);
           if (firstTemplate.isCalculator())
           {
               // Now deal with the specifics of a calculator query
               // Query type
               querySpec.setQueryType(QueryType.calculator);
               // Query parameters specified as properties
               if (termList.size() > 0)
                  querySpec.putProperties(firstKeyname, termList);
           }
           return querySpec;
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
        boolean isTemplateName = false;
        if (scopeName.isEmpty())
            scopeName = QueryProgram.GLOBAL_SCOPE;
        else
            isTemplateName = isTemplateName(scopeName);
        Scope axiomScope = scopeMap.get(scopeName);
        if ((axiomScope == null) && !isTemplateName)
            throw new ExpressionException("Scope \"" + scopeName + "\"  in axiom key \"" + axiomKey.toString() + "\" is not found");
        AxiomSource axiomSource = null;
        if (axiomScope != null)
            axiomSource = axiomScope.getParserAssembler().getAxiomSource(axiomKey);
        if ((axiomSource == null) && (!scopeName.equals(name) || isTemplateName))
            axiomSource = parserAssembler.getAxiomSource(axiomKey);
        if (axiomSource != null)
            return axiomSource;
        QualifiedName qname = QualifiedName.parseName(axiomKey.getName(), parserAssembler.getQualifiedContextname());
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
     * Returns flag set true if supplied name is the name of a template
     * @param templateName Name to check
     * @return boolean
     */
    public boolean isTemplateName(String templateName)
    {
        boolean isTemplateKey = false;
        for (QualifiedName qame: parserAssembler.getTemplateAssembler().getTemplateNames())
            if (qame.getTemplate().equals(templateName))
            {
                isTemplateKey = true;
                break;
            }
        if (!isTemplateKey && !name.equals(QueryProgram.GLOBAL_SCOPE))
        {
            for (QualifiedName qame: getGlobalTemplateAssembler().getTemplateNames())
                if (qame.getTemplate().equals(templateName))
                {
                    isTemplateKey = true;
                    break;
                }
        }
        return isTemplateKey;
    }

    /**
     * Returns template with specified name. Will search first in own scope and then in global scope if not found
     * @param templateName Name of template
     * @return Template object or null if template not found
     */
    public Template getTemplate(QualifiedName templateName)
    {
        String scopeName = templateName.getScope();
        if (scopeName.isEmpty())
            scopeName = QueryProgram.GLOBAL_SCOPE;
        Scope templateScope = getScope(scopeName);
        Template template = null;
        if (templateScope != null)
        {
            template = templateScope.findTemplate(templateName);
            if ((template == null) && !scopeName.isEmpty() && !scopeName.equals(name))
                template = findTemplate(templateName);
            if ((template == null) && !scopeName.isEmpty())
               // Use global scope template if scope specified
               template = getGlobalTemplateAssembler().getTemplate(new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, templateName.getTemplate()));
        }
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
        Template template = parserAssembler.getTemplateAssembler().getTemplate(templateName);
        if ((template == null) && templateName.getScope().isEmpty())
        {
            QualifiedName localname = new QualifiedTemplateName(name, templateName.getTemplate());
            template = parserAssembler.getTemplateAssembler().getTemplate(localname);
        }
        if (template == null)
            template = getGlobalTemplateAssembler().getTemplate(templateName);
        if (template == null)
        {   // Create template for resource binding, if one exists
            AxiomProvider axiomProvider = null;
            QualifiedName globalResourceName = null;
            QualifiedName resourceName = parserAssembler.getResourceName(templateName);
            if (resourceName != null) 
            {
                axiomProvider = parserAssembler.getAxiomProvider(resourceName);
                if (axiomProvider != null)
                    return parserAssembler.createResourceTemplate(templateName);
            }
            else if (!templateName.getScope().isEmpty())
            {
                globalResourceName =  getGlobalParserAssembler().getResourceName(templateName);
                if (globalResourceName != null) 
                    axiomProvider =  getGlobalParserAssembler().getAxiomProvider(globalResourceName);
                if (axiomProvider != null)
                    return getGlobalParserAssembler().createResourceTemplate(templateName);
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
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
        {
            ListAssembler globalListAssembler = getGlobalListAssembler(); 
            if (globalListAssembler.getAxiomListenerMap().size() > 0)
                axiomListenerMap = globalListAssembler.getAxiomListenerMap();
        }
        Map<QualifiedName, List<AxiomListener>> localListenerMap = 
            parserAssembler.getListAssembler().getAxiomListenerMap();
        if (localListenerMap.size() > 0)
        {
            if (axiomListenerMap != null)
            {
                Map<QualifiedName, List<AxiomListener>> newAxiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
                newAxiomListenerMap.putAll(axiomListenerMap);
                axiomListenerMap = newAxiomListenerMap;
                axiomListenerMap.putAll(localListenerMap);
            }
            else
                axiomListenerMap = localListenerMap;
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
        return parserAssembler.getListAssembler().getItemList(scopeName, listName);
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
        parserAssembler.getListAssembler().copyLists(listMap);
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
            getGlobalListAssembler().copyAxioms(axiomMap);
            parserAssembler.getListAssembler().copyAxioms(axiomMap);
        }
        else
            parserAssembler.getListAssembler().copyAxioms(axiomMap);
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
    public void addLocalAxiomListener(QualifiedName qualifiedAxiomName, AxiomListener axiomListener) 
    {
        // Register locale listener with Global scope in which all local axioms must be declared
        final ParserAssembler parserAssembler = getGlobalScope().getParserAssembler();
        LocalAxiomListener localeListener = new LocalAxiomListener(qualifiedAxiomName, axiomListener);
        // If the local is declared inside a scope, then it's scope never changes and a LocaleListener is not required
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
            localeListener.onScopeChange(this);
        else
        {
            parserAssembler.registerLocaleListener(localeListener); 
            ParserTask parserTask = parserAssembler.addPending(localeListener);
            parserTask.setPriority(ParserTask.Priority.list.ordinal());
        }
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
     * Returns ParserAssembler belonging to the global scope
     * @return ParserAssembler object
     */
    public ListAssembler getGlobalListAssembler()
    {
        return getGlobalParserAssembler().getListAssembler();
    }

    /**
     * Returns TemplateAssembler belonging to the global scope
     * @return TemplateAssembler object
     */
    public TemplateAssembler getGlobalTemplateAssembler()
    {
        return getGlobalParserAssembler().getTemplateAssembler();
    }

    /**
     * Returns ParserAssembler belonging to the global scope
     * @return ParserAssembler object
     */
    public AxiomAssembler getGlobalAxiomAssembler()
    {
        return getGlobalParserAssembler().getAxiomAssembler();
    }

    public void addDebugTarget(DebugTarget debugTarget)
    {
        debugTargetList.add(debugTarget);
    }
    
    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        for (DebugTarget debugTarget: debugTargetList)
            debugTarget.setExecutionContext(context);
    }
    
    /**
     * Add "scope" builtin term list to access scope properties
     * @param properties Scope properties
     */
    private void addScopeList(Map<String, Object> properties)
    {   // Add axiom to ParserAssembler
        QualifiedName qname = new QualifiedName(name, SCOPE);
        List<Axiom> axiomList = parserAssembler.getListAssembler().getAxiomItems(qname);
        AxiomTermList localList = null;
        Axiom localAxiom = null;
        if (axiomList == null)
        {
            parserAssembler.getListAssembler().createAxiomItemList(qname);
            AxiomAssembler axiomAssembler = parserAssembler.getAxiomAssembler();
            for (String termName: properties.keySet())
                axiomAssembler.addAxiom(qname, new Parameter(termName, properties.get(termName)));
            localAxiom = axiomAssembler.saveAxiom(qname);
            localList = new AxiomTermList(qname, qname);
        }
        else
        {
            localAxiom = axiomList.get(0);
            for (String termName: properties.keySet())
            {
                Term term = localAxiom.getTermByName(termName);
                if (term != null)
                    term.setValue(properties.get(termName));
                else
                    localAxiom.addTerm(new Parameter(termName, properties.get(termName)));
            }
            localList = parserAssembler.getListAssembler().getAxiomTermList(qname);
            localList.getAxiomListener().onNextAxiom(qname, localAxiom);
            return;
        }
        parserAssembler.registerLocalList(localList);
        parserAssembler.getListAssembler().addItemList(qname, localList);
    }
 
    /**
     * Set scope locale from properties
     * @param properties
     * @return flag set true if properties are valid
     */
    private boolean setLocale(Map<String,Object> properties)
    {
        boolean hasProperties = (properties != null) && !properties.isEmpty();
        if (hasProperties)
        {
            Object language = properties.get(QueryProgram.LANGUAGE);
            if (language != null)
                // Uncomment following if SE7 supported
                //try 
                //{
                    locale = getLocale(properties, language.toString());
                    return true;
                //}
                //catch (IllformedLocaleException e)
                //{
                //  throw new ExpressionException("Scope \"" + name + "\" invalid Locale settings", e);
                //}
        }
        return false;
    }


}
