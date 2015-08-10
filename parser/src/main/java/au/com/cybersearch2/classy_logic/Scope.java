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

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QuerySpec;

/**
 * Scope
 * A namespace for axioms, templates and queries
 * @author Andrew Bowley
 * 28 Dec 2014
 */
public class Scope 
{
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
	 */
	public Scope(Map<String, Scope> scopeMap, String name, Map<String, Object> properties) 
	{
		this.scopeMap = scopeMap;
		this.name = name;
		if (properties != null)
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
				//	throw new ExpressionException("Scope \"" + name + "\" invalid Locale settings", e);
				//}
		}
		if (locale == null)
	        // Uncomment following if SE7 supported
		    locale = Locale.getDefault(/*Locale.Category.FORMAT*/);
		querySpecMap = new HashMap<String, QuerySpec>();
		parserAssembler = new ParserAssembler(this);
	}
	
	/**
	 * @return the locale
	 */
	public Locale getLocale() 
	{
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public void setLocale(Locale locale) 
	{
		this.locale = locale;
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
    public AxiomSource getAxiomSource(String axiomKey)
    {
		AxiomSource axiomSource = findAxiomSource(axiomKey);
		if (axiomSource == null)
			throw new IllegalArgumentException("Axiom \"" + axiomKey + "\" does not exist");
		return axiomSource;
    }

    /**
     * Returns axiom source for specified axiom name
     * @param axiomKey
     * @return AxiomSource object or null if it does not exist in scope
     */
    public AxiomSource findAxiomSource(String axiomKey)
    {
        AxiomSource axiomSource = parserAssembler.getAxiomSource(axiomKey);
        if ((axiomSource == null) && (!name.equals(QueryProgram.GLOBAL_SCOPE)))
            axiomSource = getGlobalParserAssembler().getAxiomSource(axiomKey);
        return axiomSource;
    }

	/**
	 * Returns template with specified name
	 * @param name
	 * @return Template object or null if template not found
	 */
	public Template getTemplate(String name)
    {
		Template template = findTemplate(name);
		if (template == null)
			throw new IllegalArgumentException("Template \"" + name + "\" does not exist");
	    return template;
    }

    /**
     * Returns template with specified name
     * @param name
     * @return Template object or null if template not found
     */
    public Template findTemplate(String name)
    {
        Template template = parserAssembler.getTemplate(name);
        if ((template == null) && (!name.equals(QueryProgram.GLOBAL_SCOPE)))
            template = getGlobalParserAssembler().getTemplate(name);
        return template;
    }

    /**
     * Returns object containing all axiom listeners belonging to this scope
     * @return  Unmodifiable AxiomListener map object
     */
	public Map<String, List<AxiomListener>> getAxiomListenerMap()
	{
		Map<String, List<AxiomListener>> axiomListenerMap = null;
		if ((!name.equals(QueryProgram.GLOBAL_SCOPE)) && (getGlobalParserAssembler().getAxiomListenerMap().size() > 0))
			axiomListenerMap = getGlobalParserAssembler().getAxiomListenerMap();
		if (parserAssembler.getAxiomListenerMap().size() > 0)
		{
			if (axiomListenerMap != null)
			{
				Map<String, List<AxiomListener>> newAxiomListenerMap = new HashMap<String, List<AxiomListener>>();
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
		ItemList<?> itemList = parserAssembler.getOperandMap().getItemList(listName);
		if ((itemList == null) && (!name.equals(QueryProgram.GLOBAL_SCOPE)))
			itemList = getGlobalParserAssembler().getOperandMap().getItemList(listName);	
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
	public Map<String, Iterable<Axiom>> getListMap() 
	{
		Map<String, Iterable<Axiom>> listMap = new HashMap<String, Iterable<Axiom>>();
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
        {
        	getGlobalParserAssembler().getOperandMap().copyLists("", listMap);
    		parserAssembler.getOperandMap().copyLists(name, listMap);
        }
        else
    		parserAssembler.getOperandMap().copyLists("", listMap);
		return listMap;
	}

    public Map<String, Axiom> getAxiomMap()
    {
        Map<String, Axiom> axiomMap = new HashMap<String, Axiom>();
        if (!name.equals(QueryProgram.GLOBAL_SCOPE))
        {
            getGlobalParserAssembler().getOperandMap().copyAxioms("", axiomMap);
            parserAssembler.getOperandMap().copyAxioms(name, axiomMap);
        }
        else
            parserAssembler.getOperandMap().copyAxioms("", axiomMap);
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
		//	Locale.Builder builder = new Locale.Builder();
		//	builder.setLanguage(language);
		//	if (region != null)
		//	{
		//		builder.setRegion(region.toString());
		//		if (variant != null)
		//			builder.setVariant(variant.toString());
		//	}
		//	builder.setScript(script.toString());
		//	locale = builder.build();
		//}
		if (locale == null)
			throw new ExpressionException("Scope \"" + name + "\" invalid Locale settings combination for language " + language);
		return locale;
	}

	/**
	 * Add locale listener for local axiom identified by key. The supplied axiom listener is
	 * notified of every change of scope.
	 * @param key Local axiom id
	 * @param axiomListener The local axiom listener
	 */
	public void addLocalAxiomListener(final String key, final AxiomListener axiomListener) 
	{
		// Register locale listener with Global scope in which all local axioms must be declared
		final ParserAssembler parserAssembler = getGlobalScope().getParserAssembler();
		LocaleListener localeListener = new LocaleListener(){

			@Override
			public void onScopeChange(Scope scope) 
			{
				AxiomSource axiomSource = parserAssembler.getAxiomSource(key);
				Iterator<Axiom> iterator = axiomSource.iterator();
				Axiom defaultAxiom = null;
				Axiom localAxiom = null;
				while (iterator.hasNext())
				{
					Axiom axiom = iterator.next();
					if (defaultAxiom == null)
						defaultAxiom = axiom;
					if (axiom.getTermByIndex(0).getValue().toString().equals(scope.getName()))
					{
						localAxiom = axiom;
						break;
					}
				}
				if (defaultAxiom == null)
					throw new ExpressionException("Axiom source \"" + key + "\" is empty");
				if (localAxiom == null)
					localAxiom = defaultAxiom;
				axiomListener.onNextAxiom(localAxiom);
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


}
