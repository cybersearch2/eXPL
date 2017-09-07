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
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserContext;
import au.com.cybersearch2.classy_logic.compile.ParserTask;
import au.com.cybersearch2.classy_logic.compile.TemplateAssembler;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ResourceProvider;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.parser.ParseException;
import au.com.cybersearch2.classy_logic.parser.QueryParser;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryLauncher;
import au.com.cybersearch2.classy_logic.query.QuerySpec;

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
	
    static final char DOT = '.';
    static final String NULL_KEY_MESSAGE = "Null key passed to name parser";
	
	/** Named scopes */
	protected Map<String, Scope> scopes;
	/** Optional ProviderManager for external axiom sources */
	protected ProviderManager providerManager;
	/** Optional FunctionManager for library operands */
	protected FunctionManager functionManager;
	/** Resource path base */
	protected File resourceBase;
	/** List of open resource providers */
	protected Map<String,ResourceProvider> openProvidersMap;

	/**
	 * Default QueryProgram constructor
	 */
	public QueryProgram() 
	{
		this(null, null);
	}
	
	/**
	 * Construct QueryProgram object for resource binding
	 * @param providerManager Resource provider aggregator
	 */
	public QueryProgram(ProviderManager providerManager) 
	{
		this(providerManager, null);
	}
	
    /**
     * Construct QueryProgram object with function support
     * @param FunctionManager function aggregator
     */
	public QueryProgram(FunctionManager functionManager) 
	{
		this(null, functionManager);
	}
	
	/**
     * Construct QueryProgram object for resource binding and function support
     * @param providerManager Resource provider aggregator
     * @param FunctionManager function aggregator
	 */
	public QueryProgram(ProviderManager providerManager, FunctionManager functionManager) 
	{
		this.providerManager = providerManager;
		this.functionManager = functionManager;
		// Default resource location is CWD
		if ((providerManager != null) && (providerManager.getResourceBase() != null))
			resourceBase = providerManager.getResourceBase();
		else
			resourceBase = new File(".");
		// Scope container provides intra-scope access
		scopes = new HashMap<String, Scope>();
		// Create global scope
		Scope globalScope = new Scope(scopes, GLOBAL_SCOPE, Scope.EMPTY_PROPERTIES);
		injectScope(globalScope);
		scopes.put(GLOBAL_SCOPE, globalScope);
	}

	/**
	 * setExecutionContext
	 * @see au.com.cybersearch2.classy_logic.query.QueryLauncher#setExecutionContext(au.com.cybersearch2.classy_logic.debug.ExecutionContext)
	 */
    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        super.setExecutionContext(context);
        // Set execution context in debug targets registered with scopes
        for (Scope scope: scopes.values())
            scope.setExecutionContext(context);
    }
    
    /**
     * Returns File set to resource path
     * @return File object
     */
    public File getResourceBase() 
    {
		return resourceBase;
	}
 
    /**
     * Set  resource path File
     * @param resourceBase File object set to resource path
     */
	public void setResourceBase(File resourceBase) 
	{
		this.resourceBase = resourceBase;
	}

	/**
	 * Compile eXPL program
	 * @param script eXPL program
	 * @throws ExpressionException if parse error encountered
	 */
	public ParserContext parseScript(String script) 
    {
	    InputStream stream = new ByteArrayInputStream(script.getBytes());
        QueryParser queryParser = new QueryParser(stream);
        ParserContext context = new ParserContext(this);
        try
        {
            queryParser.input(context);
            return context;
        }
        catch (ParseException e)
        {
            throw new ExpressionException("Error compiling script: " + e.getMessage(), e);
        }
    }

    /**
     * Returns new Scope instance
     * @param scopeName
     * @param properties Optional properties eg. Locale
     * @return Scope object
     * @throws ExpressionException if global scope name requested or a scope exists with the same name
     */
    public Scope scopeInstance(String scopeName, Map<String, Object> properties)
    {
        if (scopeName.equals(GLOBAL_SCOPE))
        {   // Global properties are updateable
            Scope globalScope = scopes.get(GLOBAL_SCOPE);
            if ((properties != null) && !properties.isEmpty())
                 globalScope.updateProperties(properties);
            return globalScope;
        }
        if (scopes.get(scopeName) != null)
            throw new ExpressionException("Scope named \"" + scopeName + "\" already exists");
        Scope newScope = new Scope(scopes, scopeName, properties);
        scopes.put(scopeName, newScope);
        injectScope(newScope);
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
     * Returns QueryParams object for name in global scope 
     * @param queryName Name of query in scope
     */
    public QueryParams getQueryParams(String queryName)
    {
        return getQueryParams(GLOBAL_SCOPE, queryName);
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
		if (context != null)
		{
		    for (Scope contextScope: scopes.values())
		        context.init(contextScope);
		}
		try
		{
		    launch(queryParams);
			listMap = scope.getListMap();
			axiomMap = scope.getAxiomMap();
		}
		finally
		{
			scopeContext.resetScope();
	        if (openProvidersMap != null)
	        {
	            for (Map.Entry<String,ResourceProvider> entry: openProvidersMap.entrySet())
	            {
	                try
	                {
	                    entry.getValue().close();
	                }
	                catch(RuntimeException e)
	                {
	                    e.printStackTrace();
	                }
	            }
	        }
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
		return executeQuery(getScopePart(queryName), getNamePart(queryName), solutionHandler);
	}

	/**
	 * Execute query identified by name, potentially qualified with scope.
	 * @param queryName
	 */
	public Result executeQuery(String queryName) 
	{
		return executeQuery(queryName, QueryParams.DO_NOTHING);
	}

	/**
	 * Execute query identified by name in named scope.
	 * @param scopeName
	 * @param queryName
	 */
	public Result executeQuery(String scopeName, String queryName)
	{
		return executeQuery(scopeName, queryName, QueryParams.DO_NOTHING);

	}

    /**
     * Open resource with properties, if supplied
     * @param resourceName Resource name
     * @param properties Properties specific to the resource. May be empty.
     * @throws ExpressionException if open of provider fails
     */
    public ResourceProvider openResource(String resourceName,  Map<String, Object> properties) throws ExpressionException
    {
        ResourceProvider resourceProvider = null;
        if (providerManager != null)
            resourceProvider = providerManager.getResourceProvider(QualifiedName.parseName(resourceName));
        if (resourceProvider == null)
            throw new ExpressionException("Resource \"" + resourceName + "\"not found");
        resourceProvider.open(properties);
        if (openProvidersMap == null)
            openProvidersMap = new HashMap<String,ResourceProvider>();
        openProvidersMap.put(resourceName, resourceProvider);
        return resourceProvider;
    }

    /**
     * Run all parser tasks, which completes compilation after entire program has been input
     */
    public void runPending()
    {   
        PriorityQueue<ParserTask> priorityQueue = new PriorityQueue<ParserTask>();
        for (Scope scope: scopes.values())
            scope.getParserAssembler().getPending(priorityQueue);
        
        ParserTask parserTask = priorityQueue.poll();
        while (parserTask !=null)
        {
            Scope scope = scopes.get(parserTask.getScopeName());
            ParserAssembler parserAssembler = scope.getParserAssembler();
            QualifiedName savedQName = parserAssembler.getQualifiedContextname();
            parserAssembler.setQualifiedContextname(parserTask.getQualifiedContextname());
            parserTask.run(parserAssembler);
            parserAssembler.setQualifiedContextname(savedQName);
            parserTask = priorityQueue.poll();
        }
        // Run parser task for every non-empty template. For each operand in the template, 
        // the task walks the operand tree and adds all terms to the archetype which belong
        // to the template name space.
        for (Scope scope: scopes.values())
        {
            TemplateAssembler templateAssembler = scope.getParserAssembler().getTemplateAssembler();
            templateAssembler.doParserTask();
        }
    }

    /**
     * Set scope resource binding and function support aggregators
     * @param scope Scope object to set
     */
    private void injectScope(Scope scope)
    {
        ParserAssembler parserAssembler = scope.getParserAssembler();
        if (providerManager != null)
            parserAssembler.setProviderManager(providerManager);
        if (functionManager != null)
            parserAssembler.setFunctionManager(functionManager);
    }
    
    /**
     * Returns name part of key
     * @param key
     * @return String
     */
    public static String getNamePart(String key)
    {
        if (key == null)
            throw new IllegalArgumentException(NULL_KEY_MESSAGE);
        int dot = key.lastIndexOf(DOT);
        if (dot != -1)
            return key.substring(dot + 1);
        return key;
    }

    /**
     * Returns scope part of key or name of Global Scope if not in key
     * @param key
     * @return String
     */
    public static String getScopePart(String key)
    {
        if (key == null)
            throw new IllegalArgumentException(NULL_KEY_MESSAGE);
        int dot = key.indexOf(DOT);
        if (dot != -1)
            return key.substring(0, dot);
        return QueryProgram.GLOBAL_SCOPE;
    }

}
