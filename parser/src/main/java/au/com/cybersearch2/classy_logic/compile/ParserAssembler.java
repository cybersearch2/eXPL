/**
 * 
 */
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.CallOperand;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.AxiomListSource;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomListSpec;
import au.com.cybersearch2.classy_logic.list.AxiomListVariable;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classyinject.DI;


/**
 * ParserAssembler
 * Collects and organizes information gathered compiling an XPL script.
 * Contained are the details gathered from parsing all statements within a specified scoped.
 * @author Andrew Bowley
 *
 * @since 15/10/2010
 */
public class ParserAssembler implements LocaleListener
{
    /**
     * ExternalAxiomSource
     * Binds client-supplied ProviderManager object. 
     * Allows dependency injection to be avoided if external axiom sources are not used. 
     * @author Andrew Bowley
     * 4 Aug 2015
     */
	public static class ExternalAxiomSource
	{
		@Inject
		ProviderManager providerManager;
		public ExternalAxiomSource()
		{
            DI.inject(this); 
		}

		public AxiomProvider getAxiomProvider(String name)
		{
			return providerManager.getAxiomProvider(name);
		}
		
		public AxiomSource getAxiomSource(String name, String axiomName,
				List<String> axiomTermNameList) 
		{
	        return providerManager.getAxiomSource(name, axiomName, axiomTermNameList);
		}
	}

	/**
	 * 
	 * ExternalFunctionProvider
     * Binds client-supplied FunctionManager object. 
     * Allows dependency injection to be avoided if external functions are not used. 
	 * @author Andrew Bowley
	 * 4 Aug 2015
	 */
	public static class ExternalFunctionProvider
	{
        @Inject
        FunctionManager functionManager;
        public ExternalFunctionProvider()
        {
            DI.inject(this); 
        }

        public FunctionProvider getFunctionProvider(String name)
        {
            return functionManager.getFunctionProvider(name);
        }
	}
	
	/** Scope */
    protected Scope scope;
    /** The operands, which are terms placed in expressions */
	protected OperandMap operandMap;
	/** The axioms which are declared within the enclosing scope */ 
	protected Map<String, List<Axiom>> axiomListMap;
	/** Container for axioms under construction */
	protected Map<String, Axiom> axiomMap;
	/** Axiom term names */
	protected Map<String, List<String>> axiomTermNameMap;
	/** The templates */
	protected Map<String, Template> templateMap;
	/** The axiom listeners, all belonging to list variables */
	protected Map<String, List<AxiomListener>> axiomListenerMap;
	/** Maps qualified axiom name to resource name */
	protected Map<String, String> axiomResourceMap;
	/** List of Locale listeners which are notified of change of scope */
	protected List<LocaleListener> localeListenerList;
	/** Axioms which are bound to the current scope */
	protected Map<String, Axiom> scopeAxiomMap;
	/** Axioms used as parameters */
	protected List<String> parameterList;

	/** Axiom provider connects to persistence back end */
	ExternalAxiomSource externalAxiomSource;
	/** External function provider for exection plug ins */
	ExternalFunctionProvider externalFunctionProvider;
	
	/**
	 * Construct a ParserAssembler object 
	 * @param scope The name of the enclosing scope 
	 */
	public ParserAssembler(Scope scope)
	{
		this.scope = scope;
		operandMap = new OperandMap();
	    axiomListMap = new HashMap<String, List<Axiom>>();
	    axiomMap = new HashMap<String, Axiom>();
	    axiomTermNameMap = new HashMap<String, List<String>>();
	    templateMap = new HashMap<String, Template>();
	    axiomListenerMap = new HashMap<String, List<AxiomListener>>();
	    axiomResourceMap = new HashMap<String, String>();
	    localeListenerList = new ArrayList<LocaleListener>();
	    parameterList = new ArrayList<String>();
	}
	
	/**
	 * Returns object containing all operands and item lists
	 * @return OperandMap object
	 */
    public OperandMap getOperandMap()
    {
    	return operandMap;
    }

    /**
     * Returns enclosing scope
     * @return Scope object
     */
    public Scope getScope()
    {
    	return scope;
    }
 
    /**
     * Returns Locale of enclosing scope
     * @return
     */
    public Locale getScopeLocale()
    {
    	return scope.getLocale();
    }
    
    /**
     * Add contents of another ParserAssembler to this object
     * @param parserAssembler Other ParserAssembler object
     */
	public void addAll(ParserAssembler parserAssembler) 
	{
		operandMap.putAll(parserAssembler.operandMap);
		axiomListMap.putAll(parserAssembler.axiomListMap);
		axiomMap.putAll(parserAssembler.axiomMap);
		axiomTermNameMap.putAll(parserAssembler.axiomTermNameMap);
		templateMap.putAll(parserAssembler.templateMap);
		axiomListenerMap.putAll(parserAssembler.getAxiomListenerMap());
		axiomResourceMap.putAll(parserAssembler.axiomResourceMap);
		if (parserAssembler.parameterList != null)
		    parameterList.addAll(parserAssembler.parameterList);
	}

	/**
	 * Add a new template to this ParserAssembler
	 * @param templateName
	 */
	public Template createTemplate(String templateName)
	{
		Template template = new Template(templateName);
		templateMap.put(templateName, template);
		return template;
	}

	/**
	 * Add a term to a template
	 * @param templateName
	 * @param term Operand object
	 */
	public void addTemplate(String templateName, Operand term)
	{
		Template template = templateMap.get(templateName);
		template.addTerm((Term)term);
	}

	/**
	 * Set template properties - applies only to Calculator
	 * @param templateName
	 * @param properties
	 * @see au.com.cybersearch2.classy_logic.pattern.Template#initialize()
	 */
	public void addTemplate(String templateName, Map<String, Object> properties)
	{
		Template template = templateMap.get(templateName);
		template.addProperties(properties);
	}

	/**
	 * Returns template with specified name
	 * @param name
	 * @return Template object or null if template not found
	 */
	public Template getTemplate(String name)
    {
	    return templateMap.get(name);
    }

	/**
	 * Add a new axiom to this ParserAssembler
	 * @param axiomName
	 */
	public void createAxiom(String axiomName)
	{
		List<Axiom> axiomList = new ArrayList<Axiom>();
		axiomListMap.put(axiomName, axiomList);
	}

	/**
	 * Add a term to axiom under construction
	 * @param axiomName
	 * @param term Term object
	 */
	public void addAxiom(String axiomName, Term term)
	{
		Axiom axiom = axiomMap.get(axiomName);
		if (axiom == null)
		{   // No axiom currently under construction, so create one.
			axiom = new Axiom(axiomName);
			axiomMap.put(axiomName, axiom);
		}
		// Use declared term name, if specified
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList != null)
			axiom.addTerm(term, termNameList);
		else
			axiom.addTerm(term);
	}

	/**
	 * Add scope-bound axiom
	 * @param axiom Axiom object
	 */
	public void addScopeAxiom(Axiom axiom)
	{
		if (scopeAxiomMap == null)
			scopeAxiomMap = new HashMap<String, Axiom>();
		String axiomName = axiom.getName();
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList != null)
		{	
			Axiom termNameAxiom = new Axiom(axiomName);
			int index = 0;
		    for (String termName: termNameList)
		    {
		    	if (index == axiom.getTermCount())
		    		break;
		    	termNameAxiom.addTerm(new Parameter(termName, axiom.getTermByIndex(index++).getValue()));
		    }
			scopeAxiomMap.put(axiomName, termNameAxiom);
		}
		else
			scopeAxiomMap.put(axiomName, axiom);
	}
	
	/**
	 * Add name to list of axiom term names
	 * @param axiomName
	 * @param termName
	 */
	public void addAxiomTermName(String axiomName, String termName)
	{
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList == null)
		{
			termNameList = new ArrayList<String>();
			axiomTermNameMap.put(axiomName, termNameList);
		}
		termNameList.add(termName);
	}
	
	/**
	 * Get axiom term name by position
	 * @param axiomName
	 * @param position 
	 */
	public String getAxiomTermName(String axiomName, int position)
	{
		List<String> termNameList = axiomTermNameMap.get(axiomName);
		if (termNameList == null)
		    return null;
		return termNameList.get(position);
	}
	
	/**
	 * Returns list of axiom term names
	 * @param axiomName
	 */
	public List<String> getAxiomTermNameList(String axiomName)
	{
		return axiomTermNameMap.get(axiomName);
	}
	
	/**
	 * Transfer axiom under construction to the list of axioms with same name
	 * @param axiomName
	 */
	public Axiom saveAxiom(String axiomName)
	{
		Axiom axiom = axiomMap.get(axiomName);
		List<Axiom> axiomList = axiomListMap.get(axiomName);
		axiomList.add(axiom);
		axiomMap.remove(axiomName);
		return axiom;
	}

	/**
	 * Set resource properties for axiom and remove internal reference
	 * @param resourceName Resource name
	 * @param axiomName Name of axiom to which properties apply
	 * @param properties Properties specific to the resource. May be empty.
	 */
	public AxiomProvider setResourceProperties(String resourceName, String axiomName, Map<String, Object> properties)
	{
		// Note resource references axiom by qualified name, which prepends scope name
		String qualifiedAxiomName = getQualifiedName(axiomName);
		AxiomProvider axiomProvider = getAxiomProvider(resourceName);
		axiomProvider.setResourceProperties(qualifiedAxiomName, properties);
		// Preserve mapping of qualified axiom name to resource name
		axiomResourceMap.put(qualifiedAxiomName, resourceName);
		// Remove entry from axiomListMap so the axiom is not regarded as internal
		axiomListMap.remove(axiomName);
		return axiomProvider;
	}

	/**
	 * Register name of axiom to be supplied as a parameter
	 * @param axiomName Name of axiom to which properties apply
	 */
	public void setParameter(String axiomName)
	{
	    if (parameterList == null)
	        parameterList = new ArrayList<String>();
	    parameterList.add(axiomName);
	    // Create a variable to permit setting the parameter in a script
	    operandMap.addOperand(axiomName, null);
	}

	/**
	 * Returns flag to indicate if the axiom specified by name  is a parameter
	 * @param axiomName
	 * @return boolean
	 */
	public boolean isParameter(String axiomName)
	{
        if (parameterList == null)
            return false;
        return  parameterList.contains(axiomName);
	}
	
	/**
	 * Returns axiom source for specified axiom name
	 * @param axiomName
	 * @return AxiomSource object
	 */
    public AxiomSource getAxiomSource(String axiomName)
    {
    	if (scopeAxiomMap != null)
    	{   // Scope axioms are provided in query parameters.
    		Axiom axiom = scopeAxiomMap.get(axiomName);
    		if (axiom != null)
    			return new SingleAxiomSource(axiom);
    	}
        if ((parameterList == null) || !parameterList.contains(axiomName))
        {   // Axiom is declared in script?
           	List<Axiom> axiomList = axiomListMap.get(axiomName);
        	if (axiomList != null)
        	{   
        	    AxiomListSource axiomListSource = new AxiomListSource(axiomList);
        	    axiomListSource.setAxiomTermNameList(axiomTermNameMap.get(axiomName));
        		return axiomListSource;
        	}
        }
    	String qualifiedName = getQualifiedName(axiomName);
    	String resourceName = axiomResourceMap.get(qualifiedName);
    	if (resourceName == null)
    		return null;
    	List<String> axiomTermNameList = axiomTermNameMap.get(qualifiedName);
    	if (axiomTermNameList == null)
    	    axiomTermNameList = Collections.emptyList();
     	return getAxiomProvider(resourceName).getAxiomSource( qualifiedName, axiomTermNameList); 
    }

    /**
     * Returns object containing all axiom listeners belonging to this scope
     * @return  Unmodifiable AxiomListener map object
     */
	public Map<String, List<AxiomListener>> getAxiomListenerMap()
	{
		return Collections.unmodifiableMap(axiomListenerMap);
	}

	/**
	 * Register axiom term list by adding it's axiom listener to this ParserAssembler object
	 * @param axiomTermList The term list
	 */
	public void registerAxiomTermList(AxiomTermList axiomTermList)
	{
		AxiomListener axiomListener = axiomTermList.getAxiomListener();
		String key = axiomTermList.getKey();
		List<AxiomListener> axiomListenerList = getAxiomListenerList(key);
		axiomListenerList.add(axiomListener);
		axiomTermList.setAxiomTermNameList(axiomTermNameMap.get(key));
	}

	/**
	 * Register axiom term list for local axiom
	 * @param axiomTermList The term list
	 */
	public void registerLocalList(AxiomTermList axiomTermList)
	{
		String key = axiomTermList.getKey();
		axiomTermList.setAxiomTermNameList(axiomTermNameMap.get(axiomTermList.getKey()));
		scope.addLocalAxiomListener(key, axiomTermList.getAxiomListener());
	}

	/**
	 * Register locale listener to be notified when the scope changes
	 * @param localeListener LocaleListener object
	 */
	public void registerLocaleListener(LocaleListener localeListener)
	{
		localeListenerList.add(localeListener);
	}

	/**
	 * Notify all locale listeners that the scope has changed
	 * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
	 */
	@Override
	public void onScopeChange(Scope scope) 
	{
	    // TODO - Investigate whento clear scope axioms
		//if (scopeAxiomMap != null)
		//	scopeAxiomMap.clear();
		for (LocaleListener localeListener: localeListenerList)
			localeListener.onScopeChange(scope);
	}

	/**
	 * Returns list of axiom listeners for specified key
	 * @param key
	 * @return List containing AxiomListener objects 
	 */
	protected List<AxiomListener> getAxiomListenerList(String key)
	{
		List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
		if (axiomListenerList == null)
		{
			axiomListenerList = new ArrayList<AxiomListener>();
			axiomListenerMap.put(key, axiomListenerList);
		}
		return axiomListenerList;
	}
	
	/**
	 * Register axiom list by adding it's axiom listener to this ParserAssembler object
	 * @param axiomList The axiom list
	 */
	public void registerAxiomList(AxiomList axiomList) 
	{
		AxiomListener axiomListener = axiomList.getAxiomListener();
        String key = axiomList.getKey();
        boolean isChoice = templateMap.containsKey(key) &&
                            templateMap.get(key).isChoice();
        List<Axiom> internalAxiomList = axiomListMap.get(key);
        // Populate list if already created by the script being compiled
        if (!isChoice && (internalAxiomList != null))
        {
            for (Axiom axiom: internalAxiomList)
                axiomListener.onNextAxiom(axiom);
        }
        else
        {
		    List<AxiomListener> axiomListenerList = getAxiomListenerList(key);
		    axiomListenerList.add(axiomListener);
        }
		List<String> axiomTermNameList = axiomTermNameMap.get(key);
		if (axiomTermNameList != null)
		{
	        axiomList.setAxiomTermNameList(axiomTermNameList);
	        return;
		}
		setAxiomTermNameList(key, axiomList);
	}

	/**
	 * Set axiom term name list from template
	 * @param templateName Name of template
	 * @param axiomList Axiom list to be updated
	 * @return
	 */
	public List<String> setAxiomTermNameList(String templateName, AxiomList axiomList)
    {
	    List<String> axiomTermNameList = null;
        Template template = templateMap.get(templateName);
        if (template != null)
        {
            axiomTermNameList = new ArrayList<String>();
            for (int i = 0; i < template.getTermCount(); i++)
            {
                Term term = template.getTermByIndex(i);
                if (term.getName().isEmpty())
                    break;
                axiomTermNameList.add(term.getName());
            }
            if (axiomTermNameList.size() > 0)
                axiomList.setAxiomTermNameList(axiomTermNameList);
        }
        return axiomTermNameList;
    }

	/**
	 * Register axiom list by adding it's axiom listener to this ParserAssembler object
	 * @param axiomKey The name of the axioms inserted into the list
	 * @param axiomListener The axiom listener
	 */
	public void registerAxiomListener(String axiomKey, AxiomListener axiomListener) 
	{
		List<AxiomListener> axiomListenerList = getAxiomListenerList(axiomKey);
		axiomListenerList.add(axiomListener);
	}

	/**
	 * Returns the given name prepended with the scope name and a dot 
	 * unless this ParserAssembler is enclosing the Global scope.
	 * Required for axiomProvider which is possibly shared by more than one scope.
	 * @param name Simple name
	 * @return Qualified name
	 */
	protected String getQualifiedName(String name)
	{
		return QueryProgram.GLOBAL_SCOPE.equals(scope.getName()) ? name : (scope.getName() + "." + name);
	}

	/**
	 * Create new template and add to head template chain
	 * @param templateName Name of head template
	 * @param chainName Name of template to add to chain
	 */
	public void chainTemplate(String templateName, String chainName) 
	{
		Template template = getTemplate(templateName);
		while (template.getNext() != null)
			template = template.getNext();
		Template chainTemplate = new Template(chainName);
		template.setNext(chainTemplate);
		templateMap.put(chainName, chainTemplate);
	}

	/**
	 * Returns operand which invokes a function call in script. The function can be
	 * provided in an external library or a script query with optional parameters.
	 * The function name must consist of 2 parts. The first is the name of a library or scope.
	 * If the first part is a library name, then the second part is the name of a function in that library.
	 * If the first part is a scope name, then the second part is the name of a query in that library.
	 * The type of object returned from the call is an AxiomList which must be declared as a parameter
	 * beforehand.<br/>
	 * <code axiom my_result(function_called, wanted_value): parameter;</code> 
	 * @param name Function name
	 * @param argumentExpression Operand with one or more arguments contained in it or null for no arguments
	 * @return Operand object
	 */
	public Operand getCallOperand(String name, Operand argumentExpression)
	{
        String[] parts = name.split("\\.");
        if (parts.length != 2)
            throw new ExpressionException("Call name \"" + name + "\" is invalid");
        String library = parts[0];
        final String function = parts[parts.length - 1];
        CallEvaluator callEvaluator = null;
        Scope functionScope = scope.findScope(library);
        if (functionScope != null)
        {
            if (functionScope == scope)
                throw new ExpressionException("Query \"" + function + "\" in scope \"" + library + "\" not allowed in own scope");
            QuerySpec querySpec = functionScope.getQuerySpec(function);
            if (querySpec == null)
                throw new ExpressionException("Query \"" + function + "\" not found in scope \"" + library + "\"");
            QueryParams queryParams = new QueryParams(functionScope, querySpec);
            callEvaluator = new QueryEvaluator(queryParams);
        }
        else
        {
    	    if (externalFunctionProvider == null)
    	    {
    	        externalFunctionProvider = new ExternalFunctionProvider();
    	    }
    	    FunctionProvider functionProvider = externalFunctionProvider.getFunctionProvider(library);
    	    callEvaluator = functionProvider.getCallEvaluator(function);
    	    if (callEvaluator == null)
    	        throw new ExpressionException("Function \"" + function + "\" not supported");
        }
	    return new CallOperand(library + "." + function, callEvaluator, argumentExpression);
	}
	
	/**
	 * Returns axiom provider specified by resource name
	 * @param resourceName
	 * @return AxiomProvider object
	 */
	protected AxiomProvider getAxiomProvider(String resourceName) 
	{
    	if (externalAxiomSource == null)
    		externalAxiomSource = new ExternalAxiomSource();
		return externalAxiomSource.getAxiomProvider(resourceName);
	}

	/**
	 * Returns item list specified by name.
	 * @param name Name of list
	 * @return ItemList object or null if an axiom parameter is named and is not yet set 
	 */
    public ItemList<?> getItemList(String name)
    {
        Scope globalScope = scope.getGlobalScope();
        ItemList<?> itemList = globalScope.getParserAssembler().getOperandMap().getItemList(name);
        if ((itemList == null) && (scope != globalScope))
            itemList = scope.getParserAssembler().getOperandMap().getItemList(name);
        if (itemList == null)
        {
            if ((parameterList == null) || parameterList.contains(name))
            {
                Variable operand = (Variable) operandMap.getOperand(name);
                if (!operand.isEmpty())
                {
                    AxiomList axiomList = (AxiomList)operand.getValue();
                    axiomTermNameMap.put(name, axiomList.getAxiomTermNameList());
                    return axiomList;
                }
                else
                    return null;
            }
            throw new ExpressionException("List not found with name \"" + name + "\"");
        }
        return itemList;
    }

    /**
     * Returns Operand which accesses a list
     * @param listName
     * @param param1 Operand of first index
     * @param param2 Operand of second index or null if not specified
     * @return Operand from package au.com.cybersearch2.classy_logic.list
     */
    public Operand newListVariableInstance(String listName, Operand param1, Operand param2)
    {
        if ((param2 != null) && (param1 instanceof StringOperand))
            throw new ExpressionException("List \"" + listName + "\" cannot be indexed by name");
        ItemList<?> itemList = null;
        AxiomListSpec axiomListSpec = null;
        // When an axiom parameter is specified, then initialization of the list variable must be delayed 
        // until evaluation occurs when running the first query.
        boolean isAxiomListVariable = isParameter(listName);
        if (!isAxiomListVariable)
        {   // A normal list should be ready to go
            itemList = getItemList(listName);
            //operandMap.addItemList(listName, itemList);
            if (param2 == null) // Single index case is easy
                return operandMap.newListVariableInstance(itemList, param1);
        }
        else if (param2 == null) 
        {  // As an axiom parameter will normally be contained in a list containing a single item,
           // default axiom index to 0 if missing
           param2 = param1;
           param1 = new IntegerOperand("0", Integer.valueOf(0));
        }
        if (isAxiomListVariable)
        {   // Return dynamic AxiomListVariable instance
            axiomListSpec = new AxiomListSpec(listName, (Variable) operandMap.get(listName), param1, param2);
            return new AxiomListVariable(axiomListSpec);
        }
        axiomListSpec = new AxiomListSpec((AxiomList)itemList, param1, param2);
        return operandMap.newListVariableInstance(axiomListSpec);
    }

}
