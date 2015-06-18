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
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.NameParser;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.ChainQueryExecuter;
import au.com.cybersearch2.classy_logic.query.QueryExecuter;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.QueryType;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * QueryProgram
 * The object which collects the results of parsing an XPL script.
 * Contains scopes, each scope a combination of operands, axioms, templates and queries in a single namespace.
 * There is also a global scope which contains a combination of operands, axioms, templates, but no queries.
 * @author Andrew Bowley
 * 27 Dec 2014
 */
public class QueryProgram 
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
		scopes.put(GLOBAL_SCOPE, new Scope(GLOBAL_SCOPE));
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
	 * Returns global scope
	 * @return Scope
	 */
	public Scope getGlobalScope()
	{
		return scopes.get(GLOBAL_SCOPE);
	}

	/**
	 * Returns named scope. The scope will be created if it does not exist.
	 * @param name
	 * @return Scope
	 */
	public Scope getScope(String name)
	{
		Scope scope = scopes.get(name);
		if (scope == null)
			throw new IllegalArgumentException("Scope \"" + name + "\" does not exist");
		return scope;
	}

    /**
     * Add supplied scope to this object. Duplicates not permitted.
     * @param scope Scope object
     */
	public void addScope(Scope scope)
	{
		String name = scope.getName();
		if (scopes.containsKey(name))
			throw new ExpressionException("Duplicate scope: \"" + name + "\"");
		scopes.put(name, scope);
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
		QueryParams queryParams = new QueryParams(this, scopeName, queryName);
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
		ScopeContext scopeContext = scope.getContext();
		Map<String, Iterable<Axiom>> listMap = null;
		Map<String, Axiom> axiomMap = null;
		try
		{
			executeQueryParams(queryParams);
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

	/**
     * Execute query by specification
	 * @param queryParams QueryParams
	 */
	protected void executeQueryParams(QueryParams queryParams)
	{
		Scope scope = queryParams.getScope();
		QuerySpec querySpec = queryParams.getQuerySpec();
		SolutionHandler solutionHandler = queryParams.getSolutionHandler();
		ChainQueryExecuter headQuery = null;
		boolean isCalculation = false;
		if (querySpec.getQueryType() != QueryType.calculator)
			headQuery =	new QueryExecuter(queryParams);
		else
		{   // QueryParams need to be initialized to set up parameter axioms
			queryParams.initialize();
			headQuery =	new ChainQueryExecuter(scope);
			headQuery.chainCalculator(getCalculatorAxiom(scope, querySpec), getCalculatorTemplate(scope, querySpec));
			isCalculation = true;
		}
		// Chained queries are optional
		if (querySpec.getQueryChainList() != null)
			for (QuerySpec chainQuerySpec: querySpec.getQueryChainList())
			{
				if (chainQuerySpec.getQueryType() == QueryType.calculator)
				{   // Calculator uses a single template
					headQuery.chainCalculator(getCalculatorAxiom(scope, chainQuerySpec), getCalculatorTemplate(scope, chainQuerySpec));
				}
				else
				{
					QueryParams chainQueryParams = new QueryParams(scope, chainQuerySpec);
					headQuery.chain(chainQueryParams.getAxiomCollection(), chainQueryParams.getTemplateList());
				}
			}
		while (headQuery.execute())
		{
			if ((solutionHandler != null) && !solutionHandler.onSolution(headQuery.getSolution()) || isCalculation)
				break;
		}
		// Reset all query templates so they can be recycled
		headQuery.reset();
	}
	
	/**
	 * Returns the single template for a Calculator query referenced as the first template in the supplied specification
	 * @param scope Current scope
	 * @param querySpec Calculator type query specification
	 * @return Template object which is initialized with properties, if any, in the query specification  
	 */
	protected Template getCalculatorTemplate(Scope scope, QuerySpec querySpec)
	{   // Calculator uses a single template
		Template template = scope.getTemplate(getCalculatorKeyName(querySpec).getTemplateName());
		Map<String, Object> properties = querySpec.getProperties(template.getName()); 
		if (properties != null)
			template.addProperties(properties);
		return template;
	}

	/**
	 * Returns Calculator axiom from supplied scope
	 * @param scope Scope
	 * @param querySpec QuerySpec
	 * @return Axiom object
	 */
	protected Axiom getCalculatorAxiom(Scope scope, QuerySpec querySpec)
	{
		String axiomKey = getCalculatorKeyName(querySpec).getAxiomKey();
		if (!axiomKey.isEmpty())
		{
		    Axiom axiom = null;
		    AxiomSource source = scope.findAxiomSource(axiomKey);
		    if (source == null)
                // Return empty axiom as placeholder for axiom to come from solution
                axiom = new Axiom(axiomKey);
		    else
		        axiom = source.iterator().next();
            return axiom;  
		}
		return null;
	}

	/**
	 * Returns key name from Calculator query specification
	 * @param querySpec
	 * @return KyeName object
	 * @throws IllegalArgumentException if not exactly 1 key name specified
	 */
	protected KeyName getCalculatorKeyName(QuerySpec querySpec)
	{
		List<KeyName> keyNameList = querySpec.getKeyNameList();
		if (keyNameList.size() != 1)
			throw new IllegalArgumentException("Calculator querySpec does not contain single KeyName as expected");
		return keyNameList.get(0);
	}

}
