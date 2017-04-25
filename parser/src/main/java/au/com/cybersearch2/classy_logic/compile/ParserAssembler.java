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
import java.util.PriorityQueue;
import java.util.Set;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.ParameterOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.OperandParam;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.AxiomListSource;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;


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
		protected ProviderManager providerManager;
		
		public ExternalAxiomSource(ProviderManager providerManager)
		{
			this.providerManager = providerManager;
		}

	    /**
	     * Returns Axiom Provider specified by name
	     * @param name Axiom Provider qualified name
	     * @return AxiomProvider implementation or null if not found
	     */
		public AxiomProvider getAxiomProvider(QualifiedName name)
		{
			return providerManager.getAxiomProvider(name);
		}
		
	    /**
	     * Returns Axiom Source of specified Axiom Provider and Axiom names
	     * @param name Axiom Provider qualified name
	     * @param axiomName Axiom name
	     * @param axiomTermNameList List of term names constrains which terms are included and their order
	     * @return AxiomSource implementation or null if axiom provider not found
	     * @throws ExpressionException if axiom provider not found
	     */
		public AxiomSource getAxiomSource(QualifiedName name, String axiomName,
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
        protected FunctionManager functionManager;
        
        public ExternalFunctionProvider(FunctionManager functionManager)
        {
        	this.functionManager = functionManager;
        }

        public FunctionProvider<?> getFunctionProvider(String name)
        {
            return functionManager.getFunctionProvider(name);
        }
	}

    private static final List<String> EMPTY_NAME_LIST;

    static
    {
        EMPTY_NAME_LIST = Collections.emptyList();
    }
    
	/** Scope */
    protected Scope scope;
    /** The operands, which are terms placed in expressions */
	protected OperandMap operandMap;
	/** The axioms which are declared within the enclosing scope */ 
	protected Map<QualifiedName, List<Axiom>> axiomListMap;
	/** Container for axioms under construction */
	protected Map<QualifiedName, Axiom> axiomMap;
	/** Axiom term names */
	protected Map<QualifiedName, List<String>> axiomTermNameMap;
	/** The templates */
	protected Map<QualifiedName, Template> templateMap;
	/** The axiom listeners, all belonging to list variables */
	protected Map<QualifiedName, List<AxiomListener>> axiomListenerMap;
    /** AxiomTermLists in template scope */
    protected Map<QualifiedName, AxiomTermList> axiomTermListMap;
	/** Maps qualified axiom name to resource name */
	protected Map<QualifiedName, QualifiedName> axiomResourceMap;
	/** List of Locale listeners which are notified of change of scope */
	protected List<LocaleListener> localeListenerList;
	/** Axioms which are bound to the current scope */
	protected Map<QualifiedName, Axiom> scopeAxiomMap;
	/** Axioms used as parameters */
	protected List<QualifiedName> parameterList;
	/** Optional ProviderManager for external axiom sources */
	protected ProviderManager providerManager;
	/** Optional FunctionManager for library operands */
	protected FunctionManager functionManager;
	/** Tasks delayed until parsing complete */
	protected ArrayList<ParserTask> pendingList;

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
		operandMap = new OperandMap(new QualifiedName(scope.getAlias(), QualifiedName.EMPTY));
	    axiomListMap = new HashMap<QualifiedName, List<Axiom>>();
	    axiomMap = new HashMap<QualifiedName, Axiom>();
	    axiomTermNameMap = new HashMap<QualifiedName, List<String>>();
	    templateMap = new HashMap<QualifiedName, Template>();
	    axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
	    axiomResourceMap = new HashMap<QualifiedName, QualifiedName>();
	    localeListenerList = new ArrayList<LocaleListener>();
	    parameterList = new ArrayList<QualifiedName>();
	    axiomTermListMap = new HashMap<QualifiedName, AxiomTermList>();
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
     * Set resource provider manager
     * @param providerManager ProviderManager object 
     */
    public void setProviderManager(ProviderManager providerManager) 
    {
		this.providerManager = providerManager;
	}

    /**
     * Set function manager
     * @param functionManager FunctionManager object
     */
	public void setFunctionManager(FunctionManager functionManager) 
	{
		this.functionManager = functionManager;
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
     * @return Locale object
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
		parameterList.addAll(parserAssembler.parameterList);
		axiomTermListMap.putAll(parserAssembler.axiomTermListMap);
	}

	/**
	 * Add a new template to this ParserAssembler
     * @param qualifiedTemplateName Qualified template name
	 * @param isCalculator Flag true if template declared a calculator
	 */
	public Template createTemplate(QualifiedName qualifiedTemplateName, boolean isCalculator)
	{
		Template template = new Template(qualifiedTemplateName);
		template.setCalculator(isCalculator);
		templateMap.put(qualifiedTemplateName, template);
		return template;
	}

    /**
     * Add a term to a template
     * @param qualifiedTemplateName Qualified template name
     * @param term Operand object
     */
    public void addTemplate(QualifiedName qualifiedTemplateName, Operand term)
    {
        Template template = templateMap.get(qualifiedTemplateName);
        template.addTerm(term);
    }

	/**
	 * Set template properties - applies only to Calculator
     * @param qualifiedTemplateName Qualified template name
	 * @param properties
	 * @see au.com.cybersearch2.classy_logic.pattern.Template#initialize()
	 */
	public void addTemplate(QualifiedName qualifiedTemplateName, Map<String, Object> properties)
	{
		Template template = templateMap.get(qualifiedTemplateName);
		template.addProperties(properties);
	}

	/**
	 * Returns template with specified qualified name
	 * @param qualifiedTemplateName Qualified template name
	 * @return Template object or null if template not found
	 */
	public Template getTemplate(QualifiedName qualifiedTemplateName)
    {
	    return templateMap.get(qualifiedTemplateName);
    }

	/**
	 * Returns template with specified name
	 * @param textName
	 * @return Template object or null if template not found
	 */
    public Template getTemplate(String textName)
    {
        return templateMap.get(QualifiedName.parseTemplateName(textName));
    }

	/**
	 * Create Variable to contain inner Tempate values and add to OperandMap
	 * @param innerTemplate The inner Template
	 */
	public void addInnerTemplate(Template innerTemplate)
	{
	    // Create Variable to be axiomTermList container. Give it the same name as the inner Template 
	    // so it is qualified by the name of the enclosing Template
	    Variable listVariable = new Variable(innerTemplate.getQualifiedName());
	    // Add variable to OperandMap so it can be referenced from script
	    operandMap.addOperand(listVariable);
	}

	public AxiomTermList getAxiomTermList(QualifiedName qualifiedName)
	{
	    AxiomTermList axiomTermList = axiomTermListMap.get(qualifiedName);
	    if (axiomTermList == null)
	    {
	        axiomTermList = new AxiomTermList(qualifiedName, qualifiedName);
	        axiomTermListMap.put(qualifiedName, axiomTermList);
	    }
	    return axiomTermList;
	}
	
	/**
	 * Add a new axiom to this ParserAssembler
	 * @param qualifiedAxiomName Qualified axiom name
	 * @return flag set true if new axiom declaration
	 */
	public boolean createAxiom(QualifiedName qualifiedAxiomName)
	{   // Create new axiom list if one does not already exist
		List<Axiom> axiomList = axiomListMap.get(qualifiedAxiomName);
		if (axiomList != null)
        {
		    List<String> termNameList = axiomTermNameMap.get(qualifiedAxiomName);  
		    if (termNameList != null)
		        termNameList.clear();
		    axiomList.clear();
		    return false;
		}
        axiomList = new ArrayList<Axiom>();
        axiomListMap.put(qualifiedAxiomName, axiomList);
        return true;
	}

	/**
	 * Add a term to axiom under construction
	 * @param qualifiedAxiomName
	 * @param term Term object
	 */
	public void addAxiom(QualifiedName qualifiedAxiomName, Term term)
	{
		Axiom axiom = axiomMap.get(qualifiedAxiomName);
		if (axiom == null)
		{   // No axiom currently under construction, so create one.
			axiom = new Axiom(qualifiedAxiomName.getName());
			axiomMap.put(qualifiedAxiomName, axiom);
		}
		// Use declared term name, if specified
		List<String> termNameList = axiomTermNameMap.get(qualifiedAxiomName);
		if (termNameList != null)
			axiom.addTerm(term, termNameList);
		else
			axiom.addTerm(term);
	}

	/**
	 * Add scope-bound axiom. 
	 * This axiom will be passed to the query by containing it in
	 * the QueryParams initialSolution object.
	 * The axiom can also be declared in the script as a parameter,
	 * which allows checking that required terms are present.
	 * All scope-bound axioms are removed on scope context reset at the
	 * conclusion of a query.
	 * @param axiom Axiom object
	 * @see au.com.cybersearch2.classy_logic.QueryParams#initialize()
	 * @see #setParameter(au.com.cybersearch2.classy_logic.helper.QualifiedName)
     */
	public void addScopeAxiom(Axiom axiom)
	{
		if (scopeAxiomMap == null)
			scopeAxiomMap = new HashMap<QualifiedName, Axiom>();
		String axiomName = axiom.getName();
		QualifiedName qualifiedAxiomName = QualifiedName.parseGlobalName(axiomName);
		List<String> termNameList = getAxiomTermNameList(qualifiedAxiomName);
		if (termNameList != null)
		{	
		    for (String termName: termNameList)
		    {
		        if (axiom.getTermByName(termName) == null)
		            throw new ExpressionException("Axiom \"" + axiomName + "\" missing term \"" + termName + "\"");
		    }
		}
		scopeAxiomMap.put(qualifiedAxiomName, axiom);
	}
	
	/**
	 * Add name to list of axiom term names
	 * @param qualifiedAxiomName
	 * @param termName
	 */
	public void addAxiomTermName(QualifiedName qualifiedAxiomName, String termName)
	{
		List<String> termNameList = axiomTermNameMap.get(qualifiedAxiomName);
		if (termNameList == null)
		{
			termNameList = new ArrayList<String>();
			axiomTermNameMap.put(qualifiedAxiomName, termNameList);
		}
		termNameList.add(termName);
	}
	
	/**
	 * Get axiom term name by position
	 * @param qualifiedAxiomName
	 * @param position 
	 */
	public String getAxiomTermName(QualifiedName qualifiedAxiomName, int position)
	{
		List<String> termNameList = axiomTermNameMap.get(qualifiedAxiomName);
		if (termNameList == null)
		    return null;
		return termNameList.get(position);
	}
	
	/**
	 * Returns list of axiom term names
	 * @param qualifiedAxionName
	 */
	public List<String> getAxiomTermNameList(QualifiedName qualifiedAxionName)
	{
	    List<String> axiomTermNameList = scope.getGlobalParserAssembler().axiomTermNameMap.get(qualifiedAxionName);
	    if ((axiomTermNameList == null) && !QueryProgram.GLOBAL_SCOPE.equals(scope.getName()))
	        axiomTermNameList = axiomTermNameMap.get(qualifiedAxionName);
	    return axiomTermNameList == null ? EMPTY_NAME_LIST : axiomTermNameList;
	}
	
	/**
	 * Transfer axiom under construction to the list of axioms with same name
	 * @param qualifiedAxiomName
	 */
	public Axiom saveAxiom(QualifiedName qualifiedAxiomName)
	{
		Axiom axiom = axiomMap.get(qualifiedAxiomName);
		List<Axiom> axiomList = axiomListMap.get(qualifiedAxiomName);
		axiomList.add(axiom);
		axiomMap.remove(qualifiedAxiomName);
		return axiom;
	}

    /**
     * Bind resource to axiom provider using qualified axiom name as resource name
     * @param qualifiedBindingName Qualified name of axiom or template
     * @throws ExpressionException if axiom provider not found
     */
    public AxiomProvider bindResource(QualifiedName qualifiedBindingName)
    {
        // Try implicit resource name same as axiom name
        // If this fails, try match to full qualified name as text
        String name = qualifiedBindingName.getName();
        if (name.isEmpty())
            name = qualifiedBindingName.getTemplate();
        QualifiedName resourceName = new QualifiedName(name);
        AxiomProvider axiomProvider = getAxiomProvider(resourceName);
        return (axiomProvider != null) ? 
            bindResource(resourceName, qualifiedBindingName) :
            bindResource(qualifiedBindingName, qualifiedBindingName);
    }
    
    /**
     * Bind resource to axiom provider for given qualified axiom name and remove internal reference
     * @param resourceName Resource qualified name
     * @param qualifiedBindingName Qulified name of axiom or template
     * @throws ExpressionException if axiom provider not found
     */
    public AxiomProvider bindResource(QualifiedName resourceName, QualifiedName qualifiedBindingName)
    {
        // Note resource references axiom by qualified name, which prepends scope name
        AxiomProvider axiomProvider = getAxiomProvider(resourceName);
        if (axiomProvider == null) 
            throw new ExpressionException("Axiom provider \"" + resourceName + "\" not found");
        if (!qualifiedBindingName.getTemplate().isEmpty())
            registerAxiomListener(qualifiedBindingName, axiomProvider.getAxiomListener(qualifiedBindingName.toString()));
        else
            // Remove entry from axiomListMap so the axiom is not regarded as internal
            axiomListMap.remove(qualifiedBindingName);
            
        // Preserve mapping of qualified axiom name to resource name
        axiomResourceMap.put(qualifiedBindingName, resourceName);
        return axiomProvider;
    }

	/**
	 * Register name of axiom to be supplied as a parameter
	 * @param qualifiedAxiomName Qualified name of axiom to which properties apply
	 */
	public void setParameter(QualifiedName qualifiedAxiomName)
	{
	    parameterList.add(qualifiedAxiomName);
	    if (!axiomListMap.containsKey(qualifiedAxiomName))
	        createAxiom(qualifiedAxiomName);
	}

	/**
	 * Returns flag to indicate if the axiom specified by name  is a parameter
	 * @param qualifedAxiomName Qualified name of axiom
	 * @return boolean
	 */
	public boolean isParameter(QualifiedName qualifedAxiomName)
	{
        if (parameterList == null)
            return false;
        return  parameterList.contains(qualifedAxiomName);
	}
	
	/**
	 * Returns axiom source for specified axiom name
	 * @param qualifiedAxiomName Qualified axiom name
	 * @return AxiomSource object
	 */
    public AxiomSource getAxiomSource(QualifiedName qualifiedAxiomName)
    {
        // Scope-bound axioms are passed in query parameters and
        // removed at the end of the query
    	if (scopeAxiomMap != null)
    	{   // Scope axioms are provided in query parameters.
    		Axiom axiom = scopeAxiomMap.get(qualifiedAxiomName);
    		if (axiom != null)
    			return new SingleAxiomSource(axiom);
    	}
    	// Look for list defined in the script
        if ((parameterList == null) || !parameterList.contains(qualifiedAxiomName))
        {   // Axiom is declared in script?
           	List<Axiom> axiomList = axiomListMap.get(qualifiedAxiomName);
        	if (axiomList != null)
        	{   
        	    List<String> terminalNameList = (axiomTermNameMap.get(qualifiedAxiomName));
        	    AxiomListSource axiomListSource = new AxiomListSource(axiomList);
        	    axiomListSource.setAxiomTermNameList(terminalNameList);
        		return axiomListSource;
        	}
        }
        QualifiedName resourceName = axiomResourceMap.get(qualifiedAxiomName);
    	if (resourceName == null)
    		return null;
    	List<String> axiomTermNameList = axiomTermNameMap.get(qualifiedAxiomName);
    	if (axiomTermNameList == null)
    	    axiomTermNameList = Collections.emptyList();
     	return getAxiomProvider(resourceName).getAxiomSource(qualifiedAxiomName.toString(), axiomTermNameList); 
    }

    /**
     * Returns object containing all axiom listeners belonging to this scope
     * @return  Unmodifiable AxiomListener map object
     */
	public Map<QualifiedName, List<AxiomListener>> getAxiomListenerMap()
	{
		return Collections.unmodifiableMap(axiomListenerMap);
	}

	/**
     * Queue task to bind list to it's source which may not yet be declared
	 * @param axiomTermList Axiom term list object
	 */
	public void registerAxiomTermList(final AxiomTermList axiomTermList)
	{
        ParserTask parserTask = addPending(new ParserRunner(){
            @Override
            public void run(ParserAssembler parserAssember)
            {
                parserAssember.bindAxiomList(axiomTermList);
            }});
        // Boost priority so list is processed before any variables which reference it
        parserTask.setPriority(2);
	}

    /**
     * Queue task to bind list to it's source which may not yet be declared
     * @param axiomList The axiom list
     */
    public void registerAxiomList(final AxiomList axiomList) 
    {
        ParserTask parserTask = addPending(new ParserRunner(){
            @Override
            public void run(ParserAssembler parserAssember)
            {
                parserAssember.bindAxiomList(axiomList);
            }});
        // Boost priority so list is processed before any variables which reference it
        parserTask.setPriority(2);
    }
    
	/**
	 * Register axiom term list for local axiom
	 * @param axiomTermList The term list
	 */
	public void registerLocalList(final AxiomTermList axiomTermList)
	{
        QualifiedName qualifiedAxiomName = axiomTermList.getKey();
		axiomTermList.setAxiomTermNameList(axiomTermNameMap.get(qualifiedAxiomName));
		scope.addLocalAxiomListener(qualifiedAxiomName, axiomTermList.getAxiomListener());
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
		for (LocaleListener localeListener: localeListenerList)
			localeListener.onScopeChange(scope);
	}

	/**
	 * Remove all scope-bound axioms
	 */
	public void clearScopeAxioms()
	{
        if (scopeAxiomMap != null)
          scopeAxiomMap.clear();
	}
	
	/**
     * Bind listener and term names for axiom container - AxiomList or AxiomTermList. This method is intended to be invoked in
     * a ParserTask post compilation so the listener target is guaranteed to be parsed.
	 * @param axiomContainer The axiom container
	 */
	public void bindAxiomList(AxiomContainer axiomContainer) 
	{
        AxiomListener axiomListener = axiomContainer.getAxiomListener();
        QualifiedName axiomKey = axiomContainer.getKey();
        // Axiom key identifies the listener target and may point to an axiom source, choice or a query solution.
        QualifiedName qualifiedAxiomName = null;
        QualifiedName qualifiedTemplateName = null;
        boolean isTemplateKey = !axiomKey.getTemplate().isEmpty();
        if (isTemplateKey)
            qualifiedAxiomName = new QualifiedName(axiomKey.getScope(), axiomKey.getTemplate());
        else
            qualifiedAxiomName = new QualifiedName(axiomKey.getScope(), axiomKey.getName());
        // A key in axiom form may point to a choice or query solution, so analysis required.
        // Where there is ambiguity, precedence is given to match on an axiom source  
        Scope targetScope = findSourceScope(qualifiedAxiomName);
        if (targetScope != null)
        {
            qualifiedTemplateName = new QualifiedTemplateName(qualifiedAxiomName.getScope(), qualifiedAxiomName.getName());
            isTemplateKey = false;
        }
        else if (isTemplateKey)
            qualifiedTemplateName = axiomKey;
        else
            qualifiedTemplateName = new QualifiedTemplateName(axiomKey.getScope(), axiomKey.getName());
        if (targetScope == null)
        {
            // Scope may need to change to Global scope if that is where axiom source with same name found
            targetScope= findTemplateScope(qualifiedTemplateName);
            if (targetScope == null)
                throw new ExpressionException("Template \"" + qualifiedTemplateName.toString() + "\" not found");
            isTemplateKey = true;
        }
        if (!isTemplateKey)
        {
            // A choice is detected using a key in template form
            Template template = targetScope.getParserAssembler().getTemplate(qualifiedTemplateName);
            isTemplateKey = (template != null ) && template.isChoice();
        }
        if (!isTemplateKey)
        {   // The final analysis is choose
            List<Axiom> internalAxiomList = targetScope.getParserAssembler().getAxiomList(qualifiedAxiomName);
            if (internalAxiomList != null)
            {
                if (axiomContainer.getOperandType() == OperandType.AXIOM)
                    // Populate axiom list if already created by the script being compiled
                    // No listener required
                    for (Axiom axiom: internalAxiomList)
                       axiomListener.onNextAxiom(axiomKey, axiom);
                else
                {   // Populate term list when axiom set during query evaluation
                    List<AxiomListener> axiomListenerList = getAxiomListenerList(axiomKey);
                    axiomListenerList.add(axiomListener);
                }
                axiomContainer.setAxiomTermNameList(axiomTermNameMap.get(axiomKey));
                return;
            }
        }
        List<AxiomListener> axiomListenerList = getAxiomListenerList(qualifiedTemplateName);
        axiomListenerList.add(axiomListener);
        setAxiomTermNameList(targetScope.getTemplate(qualifiedTemplateName), axiomContainer);
	}

	/**
	 * Returns scope of axiom source specified by qname
	 * @param qname Qualified name
	 * @return Scope object or null if axiom source not found
	 */
	public Scope findSourceScope(QualifiedName qname)
    {
        if (!qname.getTemplate().isEmpty())
            // qname must be in axiom form
            return null;
        if (isQualifiedAxiomName(qname))
            return scope;
        if (!qname.getScope().isEmpty())
            qname.clearScope();
        if (scope.getGlobalParserAssembler().isQualifiedAxiomName(qname))
            return scope.getGlobalScope();
        return null; 
    }

	   /**
     * Returns scope of axiom source specified by qname
     * @param qname Qualified name
     * @return Scope object or null if axiom source not found
     */
    public Scope findTemplateScope(QualifiedName qname)
    {
        if (qname.getTemplate().isEmpty())
            // qname must be in axiom form
            return null;
        Template template = templateMap.get(qname);
        if (template != null)
            return scope;
        if (!qname.getScope().isEmpty())
            qname = new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, qname.getTemplate());
        template = scope.getGlobalParserAssembler().getTemplate(qname);
        if (template != null)
            return scope.getGlobalScope();
        return null; 
    }
    
	/**
	 * Returns flag set true if given qualified name identifies an axiom source
	 * @param qname Qualified axiom name
	 * @return boolean
	 */
	public boolean isQualifiedAxiomName(QualifiedName qname)
    {
        if ((scopeAxiomMap != null) && (scopeAxiomMap.get(qname) != null))
            return true;
        if (axiomListMap.get(qname) != null)
            return true;
        if (axiomResourceMap.get(qname) != null)
            return true;
        return false;
    }

	/**
	 * Returns internally contained axiom list specified by qualified name
	 * @param qname Qualified name
	 * @return Axiom list
	 */
	public List<Axiom> getAxiomList(QualifiedName qname)
	{
	    return axiomListMap.get(qname);
	}
	
    /**
	 * Set axiom term name list from template
	 * @param qualifiedTemplateName Qualified name of template
	 * @param axiomList Axiom list to be updated
	 * @return List of term names
	 */
	public List<String> setAxiomTermNameList(Template template, AxiomContainer axiomContainer)
    {
	    List<String> axiomTermNameList = null;
        axiomTermNameList = new ArrayList<String>();
        for (int i = 0; i < template.getTermCount(); i++)
        {
            Term term = template.getTermByIndex(i);
            if (term.getName().isEmpty())
                break;
            axiomTermNameList.add(term.getName());
        }
        if (axiomTermNameList.size() > 0)
            axiomContainer.setAxiomTermNameList(axiomTermNameList);
        return axiomTermNameList;
    }

	/**
	 * Register axiom list by adding it's axiom listener to this ParserAssembler object
	 * @param qname The qualified name of the axioms inserted into the list
	 * @param axiomListener The axiom listener
	 */
	public void registerAxiomListener(QualifiedName qname, AxiomListener axiomListener) 
	{   // Note to listen for solution notifications, qname must be template name 
		List<AxiomListener> axiomListenerList = getAxiomListenerList(qname);
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
	 * @param outerTemplateName Qualified name of head template
	 * @param innerTemplateName Qualified name of inner template
	 * @return Template object
	 */
	public Template chainTemplate(QualifiedName outerTemplateName, QualifiedName innerTemplateName) 
	{
		Template template = getTemplate(outerTemplateName);
		Template chainTemplate = new Template(innerTemplateName);
		template.setNext(chainTemplate);
		templateMap.put(innerTemplateName, chainTemplate);
		return chainTemplate;
	}

	/**
	 * Returns operand which invokes a function call in script. The function must be
	 * provided in an external library.
	 * The function name must consist of 2 parts. The first is the name of a library.
	 * If the first part is a library name, then the second part is the name of a function in that library.
	 * The type of object returned from the call depends on the library.
	 * @param qname Qualified function name
	 * @param operandParamList List of Operand arguments or null for no arguments
	 * @return CallOperand object
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public Operand getCallOperand(QualifiedName qname, List<OperandParam> operandParamList)
	{
        String library = qname.getTemplate();
        String name = qname.getName();
        if (library.isEmpty())
            throw new ExpressionException("Call name \"" + qname.toString() + "\" is invalid");
        String callName = library + "." + name;
	    if (externalFunctionProvider == null)
	    {
	    	if (functionManager == null)
	    		functionManager = new FunctionManager(){};
	        externalFunctionProvider = new ExternalFunctionProvider(functionManager);
	    }
	    FunctionProvider<?> functionProvider = externalFunctionProvider.getFunctionProvider(library);
	    CallEvaluator<?>callEvaluator = functionProvider.getCallEvaluator(name);
	    if (callEvaluator == null)
	        throw new ExpressionException("Function \"" + name + "\" not supported");
        return new ParameterOperand(QualifiedName.parseName(callName, qname), operandParamList, callEvaluator);
	}

	/**
	 * Returns operand which invokes a query call.
	 * @param qualifiedQueryName Qualified query name - can be qualified by the name of a scope
     * @param operandParamList List of Operand arguments null for no arguments
	 * @param innerTemplate Optional template to recieve query results
	 * @return CallOperand object containing a QueryEvaluator object
	 * @see QueryEvaluator
	 */
    public Operand getQueryOperand(String queryName, QualifiedName qualifiedQueryName, List<OperandParam> operandParamList, Template innerTemplate)
    {
        if ((innerTemplate != null) && !operandMap.hasOperand(innerTemplate.getQualifiedName()))
            addInnerTemplate(innerTemplate);
        QueryEvaluator queryEvaluator = 
             new QueryEvaluator(queryName, qualifiedQueryName, innerTemplate);
        ParserTask parserTask = addPending(queryEvaluator);
        parserTask.setPriority(1);
        String library = queryEvaluator.getLibrayName(this);
        QualifiedName qualifiedCallName = new QualifiedName(library, qualifiedQueryName.getName());
        ParameterOperand<AxiomTermList> parameterOperand = new ParameterOperand<AxiomTermList>(qualifiedCallName, operandParamList, queryEvaluator);
        return parameterOperand;
    }

	/**
	 * Returns axiom provider specified by resource name
	 * @param resourceName AxiomProvider qualified name
	 * @return AxiomProvider object or null if object not found
	 */
	public AxiomProvider getAxiomProvider(QualifiedName resourceName) 
	{
    	if (externalAxiomSource == null)
    	{
    		if (providerManager == null)
    		    providerManager = new ProviderManager(){};
    		externalAxiomSource = new ExternalAxiomSource(providerManager);
    	}
		return externalAxiomSource.getAxiomProvider(resourceName);
	}

	/**
	 * Returns item list specified by qualified name.
	 * @param qname Qualified name of list
	 * @return ItemList object or null if an axiom parameter is named and is not yet set 
	 */
    public ItemList<?> getItemList(QualifiedName qname)
    {
        ItemList<?> itemList = findItemList(qname);
        if (itemList == null)
            throw new ExpressionException("List not found with name \"" + qname.toString() + "\"");
        return itemList;
    }

    /**
     * Returns item list identified by name
     * @param listName
     * @return ItemList
     * @throws ExpressionException if item list not found
     */
    public ItemList<?> getItemList(String listName)
    {
        ItemList<?> itemList = findItemList(listName);
        if (itemList == null)
            itemList = getItemList(QualifiedName.parseName(listName));
        return itemList;
    }
    
    /**
     * Returns item list specified by qualified name.
     * @param qname Qualified name of list
     * @return ItemList object or null if not found
     */
    public ItemList<?> findItemList(QualifiedName qname)
    {
        Scope nameScope = qname.getScope().isEmpty() ? scope.getGlobalScope() : scope.findScope(qname.getScope());
        if (nameScope == null)
            return null;
        OperandMap operandMap = nameScope.getParserAssembler().getOperandMap();
        ItemList<?> itemList = operandMap.getItemList(qname);
        if (itemList == null)
        {
            Operand operand = operandMap.getOperand(qname);
            if ((operand != null) && 
                 !operand.isEmpty() &&
                 (operand.getValueClass() == AxiomTermList.class))
            {
                AxiomTermList axiomTermList = (AxiomTermList)operand.getValue();
                axiomTermNameMap.put(qname, axiomTermList.getAxiomTermNameList());
                return axiomTermList;
            }
       }
       return itemList;
    }

    /**
     * Returns item list specified by name.
     * @param listName Name of list
     * @return ItemList object or null if not found
     */
    public ItemList<?> findItemList(String listName)
    {
        QualifiedName qualifiedListName = QualifiedName.parseName(listName, operandMap.getQualifiedContextname());
        ItemList<?> itemList = findItemList(qualifiedListName);
        if ((itemList == null) && !qualifiedListName.getTemplate().isEmpty())
        {
            qualifiedListName.clearTemplate();
            itemList = findItemList(qualifiedListName);
        }
        if ((itemList == null) && !qualifiedListName.getScope().isEmpty())
        {
            qualifiedListName.clearScope();
            itemList = findItemList(qualifiedListName);
        }
        return itemList;
    }
    
    /**
     * Returns qualified name for name in current context
     * @param name
     * @return QualifiedName object
     */
    public QualifiedName getContextName(String name)
    {
        return QualifiedName.parseName(name, operandMap.getQualifiedContextname());
    }
    
    /**
     * Copy result axiom lists as iterables to supplied container
     * @param listMap Container to receive lists
     */
    public void copyLists(Map<QualifiedName, Iterable<Axiom>> listMap) 
    {
        operandMap.copyLists(listMap);    
        if (!scope.getName().equals(QueryProgram.GLOBAL_SCOPE))
            scope.getGlobalParserAssembler().copyLists(listMap);
    }

    /**
     * Returns operand identified by name
     * @param operandName
     * @return Operand object from same scope or global scope or null if not found
     */
    public Operand findOperandByName(String operandName)
    {
        QualifiedName qualifiedOperandName = QualifiedName.parseName(operandName, operandMap.getQualifiedContextname());
        Operand operand = operandMap.get(qualifiedOperandName);
        if ((operand == null) && !qualifiedOperandName.getTemplate().isEmpty())
        {
            qualifiedOperandName.clearTemplate();
            operand = operandMap.get(qualifiedOperandName);
        }
        if ((operand == null) && !qualifiedOperandName.getScope().isEmpty())
        {
            qualifiedOperandName.clearScope();
            operand = operandMap.get(qualifiedOperandName);
        }
        return operand;
    }

    /**
     * Returns qualified name for resource specified by qualified binding name
     * @param qname Qualidfied name of axiom or template bound to resource
     * @return QualifiedName object or null if not found
     */
    public QualifiedName getResourceName(QualifiedName qname)
    {
        return axiomResourceMap.get(qname);
    }
 
    /**
     * Add ParserTask to pending list
     * @param pending ParserTask object
     */
    public ParserTask addPending()
    {
        ParserTask parserTask = new ParserTask(scope.getName(), operandMap.getQualifiedContextname());
        if (pendingList == null)
            pendingList = new ArrayList<ParserTask>();
        pendingList.add(parserTask);
        return parserTask;
    }

    /**
     * Add Runnable to pending list
     * @param pending Runnable to execute parser task
     */
    public ParserTask addPending(ParserRunner pending)
    {
        ParserTask parserTask = addPending();
        parserTask.setPending(pending);
        return parserTask;
    }

    /**
     * Collect pending parser tasks into priority queue
     */
    public void getPending(PriorityQueue<ParserTask> priorityQueue)
    {
        if (pendingList != null)
        {
            priorityQueue.addAll(pendingList);
            pendingList.clear();
        }
    }

    public Set<QualifiedName> getTemplateNames()
    {
        return templateMap.keySet();
    }

    public Operand addOperand(String name)
    {
        final QualifiedName qualifiedName = QualifiedName.parseName(name);
        if (!qualifiedName.getTemplate().isEmpty())
            throw new ExpressionException("Variable name \"" + name + "\" is invalid");
        String part1 = qualifiedName.getScope();
        if (part1.isEmpty() || !operandMap.getQualifiedContextname().getTemplate().equals(part1))
            return operandMap.addOperand(name, null);
        Operand operand = operandMap.getOperand(qualifiedName);
        if (operand == null)
        {
            operand = new Variable(qualifiedName)
            {
                public EvaluationStatus evaluate(int id)
                {
                    AxiomTermList itemList = getAxiomTermList(qualifiedName);
                    if (itemList.getAxiomTermNameList().size() != 1)
                       throw new ExpressionException("Variable \"" + name + "\" is a list and requires \"[]\" item selection");
                    Term term = itemList.getAxiom().getTermByIndex(0);
                    Parameter parameter = new Parameter(term.getName(), term.getValue());
                    parameter.setId(id);
                    assign(parameter);
                    return EvaluationStatus.COMPLETE;
                }
          };
          operandMap.addOperand(operand);
        }
        return operand;

    }
    
    /**
     * Returns list of axiom listeners for specified key
     * @param qname Qualified name key
     * @return List containing AxiomListener objects or null if list not found
     */
    protected List<AxiomListener> getAxiomListenerList(QualifiedName qname)
    {
        List<AxiomListener> axiomListenerList = axiomListenerMap.get(qname);
        if (axiomListenerList == null)
        {
            axiomListenerList = new ArrayList<AxiomListener>();
            axiomListenerMap.put(qname, axiomListenerList);
        }
        return axiomListenerList;
    }

}
