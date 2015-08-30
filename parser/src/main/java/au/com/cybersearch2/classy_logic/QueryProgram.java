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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.NameParser;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryLauncher;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * QueryProgram
 * The object which collects the results of parsing an XPL script.
 * Contains scopes, each scope a combination of operands, axioms, templates and queries in a single namespace.
 * There is also a global scope which contains a combination of operands, axioms, templates, but no queries.
 * @author Andrew Bowley
 * 27 Dec 2014
 */
public class QueryProgram extends QueryLauncher
{
	/** The global scope is accessible from all scopes */
	static public final String GLOBAL_SCOPE = "global";
    /**
     * Sets the Locale language which must be well-formed or an exception is thrown.
     *
     * <p>The typical language value is a two or three-letter language
     * code as defined in ISO639.
     */
	static public final String LANGUAGE = "language";
    /**
     * Sets the Locale script which must be well-formed or an
     * exception is thrown.
     *
     * <p>The typical script value is a four-letter script code as defined by ISO 15924.
     */
	static public final String SCRIPT = "script";
    /**
    * Sets the Locale region wich must be well-formed or an exception is thrown.
    *
    * <p>The typical region value is a two-letter ISO 3166 code or a
    * three-digit UN M.49 area code.
    *
    * <p>The country value in the <code>Locale</code> created by the
    * Locale <code>Builder</code> is always normalized to upper case.
    */
	static public final String REGION = "region";
    /**
     * Sets the Locale variant which must consist of one or more well-formed
     * subtags, or an exception is thrown.
     *
     * <p><b>Note:</b> The Locale <code>Builder</code> checks if <code>variant</code>
     * satisfies the IETF BCP 47 variant subtag's syntax requirements,
     * and normalizes the value to lowercase letters.  
     * */
	static public final String VARIANT = "variant";
	
	/** Named scopes */
	protected Map<String, Scope> scopes;
	
	/**
	 * Construct a QueryProgram object. It is initially empty and populated by QueryParser
	 * @see au.com.cybersearch2.classy_logic.parser.QueryParser
	 */
	public QueryProgram() 
	{
		scopes = new HashMap<String, Scope>();
		scopes.put(GLOBAL_SCOPE, new Scope(scopes, GLOBAL_SCOPE, Scope.EMPTY_PROPERTIES));
	}

    /**
     * Construct a QueryProgram object compiled to specified script. 
     * @see au.com.cybersearch2.classy_logic.parser.QueryParser
     */
    public QueryProgram(String script) 
    {
       this();
       InputStream stream = new ByteArrayInputStream(script.getBytes());
       QueryParser queryParser = new QueryParser(stream);
       try
       {
            queryParser.input(this);
       }
       catch (ParseException e)
       {
            throw new ExpressionException("Error compiling script: " + e.getMessage(), e);
       }
    }

    /**
     * Construct a new Scope instance
     * @param scopeName
     * @param properties Optional properties eg. Locale
     * @return Scope object
     * @throws ExpressionException if global scope name requested or a scope exists with the same name
     */
    public Scope scopeInstance(String scopeName, Map<String, Object> properties)
    {
        if (scopeName.equals(GLOBAL_SCOPE))
            throw new ExpressionException("Scope name \"" + GLOBAL_SCOPE + "\" is reserved");
        if (scopes.get(scopeName) != null)
            throw new ExpressionException("Scope named \"" + scopeName + "\" already exists");
       Scope newScope = new Scope(scopes, scopeName, properties);
       scopes.put(scopeName, newScope);
       return newScope;
    }

	/**
	 * Returns global scope
	 * @return Scope
	 */
	public Scope getGlobalScope()
	{
		return scopes.get(GLOBAL_SCOPE);
	}

	/**
	 * Returns scope specified by name.
	 * @param name
	 * @return Scope object
	 * @throws IllegalArgumentException if scope does not exist
	 */
	public Scope getScope(String name)
	{
		Scope scope = scopes.get(name);
		if (scope == null)
			throw new IllegalArgumentException("Scope \"" + name + "\" does not exist");
		return scope;
	}

    /**
     * Returns QueryParams object for specified by scope and name
     * @param scopeName The scope the query applies to
     * @param queryName Name of query in scope
     */
	public QueryParams getQueryParams(String scopeName, String queryName)
	{
        Scope scope = getScope(scopeName);
        QuerySpec querySpec = scope.getQuerySpec(queryName);
        if (querySpec == null)
            throw new IllegalArgumentException("Query \"" + queryName + "\" does not exist");
        return new QueryParams(scope, querySpec);
	}
	
	/**
	 * Execute query identified by name in named scope.
	 * @param scopeName
	 * @param queryName
	 * @param solutionHandler Handler to process each Solution generated by the query 
     * @return Result object containing any result lists generated by the query
	 */
	public Result executeQuery(String scopeName, String queryName, SolutionHandler solutionHandler)
	{
		QueryParams queryParams = getQueryParams(scopeName, queryName);
		queryParams.setSolutionHandler(solutionHandler);
		return executeQuery(queryParams);
	}

	/**
	 * Execute query framed with query parameters
	 * @param queryParams The query parameters, including the query specification
	 * @return Result object containing any result lists generated by the query
	 */
	public Result executeQuery(QueryParams queryParams)
	{
		Scope scope = queryParams.getScope();
		ScopeContext scopeContext = scope.getContext(false);
		Map<QualifiedName, Iterable<Axiom>> listMap = null;
		Map<QualifiedName, Axiom> axiomMap = null;
		try
		{
		    launch(queryParams);
			listMap = scope.getListMap();
			axiomMap = scope.getAxiomMap();
		}
		finally
		{
			scopeContext.resetScope();
		}
		return new Result(listMap, axiomMap);
	}

	/**
	 * Execute query identified by name, potentially qualified with scope.
	 * Use provided solution handler
	 * @param queryName
	 * @param solutionHandler Handler to process each Solution generated by the query 
     * @return Result object containing any result lists generated by the query
	 */
	public Result executeQuery(String queryName, SolutionHandler solutionHandler) 
	{
		return executeQuery(NameParser.getScopePart(queryName), NameParser.getNamePart(queryName), solutionHandler);
	}

	/**
	 * Execute query identified by name, potentially qualified with scope.
	 * @param queryName
	 */
	public Result executeQuery(String queryName) 
	{
		return executeQuery(queryName, new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				return true;
			}});
	}

	/**
	 * Execute query identified by name in named scope.
	 * @param scopeName
	 * @param queryName
	 */
	public Result executeQuery(String scopeName, String queryName)
	{
		return executeQuery(scopeName, queryName, new SolutionHandler(){
			@Override
			public boolean onSolution(Solution solution) {
				return true;
			}});

	}



}
