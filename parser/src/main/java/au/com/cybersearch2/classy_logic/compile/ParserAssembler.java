/**
 * 
 */
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

import au.com.cybersearch2.classy_logic.FunctionManager;
import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.axiom.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.CallOperand;
import au.com.cybersearch2.classy_logic.expression.TermOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.FunctionProvider;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.list.ListType;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QueryEvaluator;
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
	/** Scope */
    protected Scope scope;
    /** The operands, which are terms placed in expressions */
	protected OperandMap operandMap;
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
	/** Lists container */
	protected ListAssembler listAssembler;
	/** Template builder and container */
	protected TemplateAssembler templateAssembler;
    /** Template builder and container */
    protected AxiomAssembler axiomAssembler;
    /** Qualified name of enclosing scope/template context */
    protected QualifiedName qualifiedContextname;
    /** Parser task queue */
    protected ParserTaskQueue parserTaskQueue;

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
		qualifiedContextname = new QualifiedName(scope.getAlias(), QualifiedName.EMPTY);
		operandMap = new OperandMap();
	    axiomResourceMap = new HashMap<QualifiedName, QualifiedName>();
	    localeListenerList = new ArrayList<LocaleListener>();
	    parameterList = new ArrayList<QualifiedName>();
	    listAssembler = new ListAssembler(scope);
	    templateAssembler = new TemplateAssembler(scope);
	    axiomAssembler = new AxiomAssembler(scope);
        parserTaskQueue = new ParserTaskQueue();
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
     * Returns qualified name of enclosing scope/template
     * @return QualifiedName ovject 
     */
    public QualifiedName getQualifiedContextname()
    {
        return qualifiedContextname;
    }

    /**
     * Set qualified name of enclosing scope/template context  
     * @param qualifiedContextname
     */
    public void setQualifiedContextname(QualifiedName qualifiedContextname)
    {
        this.qualifiedContextname = qualifiedContextname;
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

    public ListAssembler getListAssembler()
    {
        return listAssembler;
    }

    public TemplateAssembler getTemplateAssembler()
    {
        return templateAssembler;
    }
    
    public AxiomAssembler getAxiomAssembler()
    {
        return axiomAssembler;
    }
    
    /**
     * Add contents of another ParserAssembler to this object
     * @param parserAssembler Other ParserAssembler object
     */
	public void addAll(ParserAssembler parserAssembler) 
	{
		axiomResourceMap.putAll(parserAssembler.axiomResourceMap);
		parameterList.addAll(parserAssembler.parameterList);
		listAssembler.addAll(parserAssembler.listAssembler);
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
		scopeAxiomMap.put(qualifiedAxiomName, axiom);
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
            listAssembler.removeAxiomItems(qualifiedBindingName);
            
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
	    if (!listAssembler.existsKey(ListType.axiom_item, qualifiedAxiomName))
	        listAssembler.createAxiomItemList(qualifiedAxiomName);
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
           	List<Axiom> axiomList = listAssembler.getAxiomItems(qualifiedAxiomName);
        	if (axiomList != null)
        		return axiomAssembler.createAxiomSource(qualifiedAxiomName, axiomList);
        }
        QualifiedName resourceName = axiomResourceMap.get(qualifiedAxiomName);
    	if (resourceName == null)
    		return null;
    	List<String> axiomTermNameList = axiomAssembler.findTermNameList(qualifiedAxiomName);
     	return getAxiomProvider(resourceName).getAxiomSource(qualifiedAxiomName.toString(), axiomTermNameList); 
    }

    /**
     * Create template for for term list bound to resource.
     * The termplate terms are simply named variables.
     * An axiom declaration must exist with same name as template.
     * Preconditions: A template with specified name does not already exist
     * and a term name list is defined for axiom declaration.
     * @param templateName Qualified template name
     * @return Template object
     */
    public Template createResourceTemplate(QualifiedName templateName)
    {   // Create qualified axiom  name 
        QualifiedName qualifiedAxiomName = new QualifiedName(templateName.getScope(),  templateName.getTemplate());
        // Get axiom archetype
        AxiomArchetype axiomArchetype = axiomAssembler.getAxiomArchetype(qualifiedAxiomName);
        if ((axiomArchetype == null) && !templateName.getScope().isEmpty())
        {
            qualifiedAxiomName.clearScope();
            axiomArchetype = axiomAssembler.getAxiomArchetype(qualifiedAxiomName);
        }
        if (axiomArchetype != null)
        {
            List<String> termNameList = axiomArchetype.getTermNameList();
            if (!termNameList.isEmpty())
            {
                Template template = templateAssembler.createTemplate(templateName, false);
                for (String termName: termNameList)
                {
                    QualifiedName qname = getContextName(termName);
                    template.addTerm(new Variable(qname));
                }
                return template;
            }
        }
        return null;
    }

    public Evaluator createReflexiveEvaluator(final Operand term, String operator, Operand assignExpression)
    {
        final Evaluator evaluator = new Evaluator(getContextName(term.getName()), term, operator, assignExpression);
        if (term.getName().isEmpty())
        {
            ParserRunner nameFixer = new ParserRunner(){

                @Override
                public void run(ParserAssembler parserAssembler)
                {
                    evaluator.setName(term.getName());
                }};
            ParserTask parserTask = parserTaskQueue.addPending(nameFixer , scope);
            parserTask.setPriority(ParserTask.Priority.fix.ordinal());
        }
        return evaluator;
    }
    
	/**
     * Queue task to bind list to it's source which may not yet be declared
	 * @param axiomTermList Axiom term list object
	 */
	public void registerAxiomTermList(final AxiomTermList axiomTermList)
	{
        parserTaskQueue.registerAxiomTermList(axiomTermList, scope);
	}

    /**
     * Queue task to bind list to it's source which may not yet be declared
     * @param axiomList The axiom list
     */
    public void registerAxiomList(final AxiomList axiomList) 
    {
        parserTaskQueue.registerAxiomList(axiomList, scope);
    }
    
	/**
	 * Register axiom term list for local axiom
	 * @param axiomTermList The term list
	 */
	public void registerLocalList(final AxiomTermList axiomTermList)
	{
        QualifiedName qualifiedAxiomName = axiomTermList.getKey();
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
		if (this.scope != scope)
		    scope.getParserAssembler().onScopeChange(scope);
        Template scopeTemplate = templateAssembler.getTemplate(new QualifiedTemplateName(scope.getAlias(), "scope"));
        if (scopeTemplate != null)
        {
            scopeTemplate.backup(true);
            while ((scopeTemplate = scopeTemplate.getNext()) != null)
                scopeTemplate.evaluate(null);
        }
        scopeTemplate = this.scope.getGlobalTemplateAssembler().getTemplate(new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, "scope"));
        if (scopeTemplate != null)
        {
            scopeTemplate.backup(true);
            while ((scopeTemplate = scopeTemplate.getNext()) != null)
                scopeTemplate.evaluate(null);
        }
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
            targetScope= templateAssembler.findTemplateScope(qualifiedTemplateName);
            if (targetScope == null)
                throw new ExpressionException("Template \"" + qualifiedTemplateName.toString() + "\" not found");
            isTemplateKey = true;
        }
        Template template = null;
        if (!isTemplateKey)
        {
            // A choice is detected using a key in template form
            template = 
                targetScope.getParserAssembler()
                .templateAssembler
                .getTemplate(qualifiedTemplateName);
            isTemplateKey = (template != null ) && template.isChoice();
        }
        if (!isTemplateKey)
        {   // The final analysis is axiom (term) list which can be set now
            AxiomArchetype archetype = axiomAssembler.getAxiomArchetype(qualifiedAxiomName);
            if (archetype != null)
                axiomContainer.setAxiomTermNameList(archetype.getTermNameList());
            List<Axiom> internalAxiomList = 
                targetScope.getParserAssembler()
                    .getListAssembler()
                    .getAxiomItems(qualifiedAxiomName);
            if (internalAxiomList != null)
            {
                listAssembler.setAxiomContainer(axiomContainer, internalAxiomList);
                return;
            }
        }
        else 
        {
            if (template == null)
                template = 
                targetScope.getParserAssembler()
                .templateAssembler
                .getTemplate(qualifiedTemplateName);
            if ((template == null) && (targetScope.getName() != QueryProgram.GLOBAL_SCOPE))
            {
                QualifiedName globalName = new QualifiedTemplateName(scope.getGlobalScope().getAlias(), qualifiedTemplateName.getTemplate());
                template = 
                scope.getGlobalTemplateAssembler()
                .getTemplate(globalName);
            }
            if (template != null)
                axiomContainer.setAxiomTermNameList(template.getArchetype().getTermNameList());
        }
        listAssembler.add(qualifiedTemplateName, axiomListener);
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
	 * Returns flag set true if given qualified name identifies an axiom source
	 * @param qname Qualified axiom name
	 * @return boolean
	 */
	public boolean isQualifiedAxiomName(QualifiedName qname)
    {
        if ((scopeAxiomMap != null) && (scopeAxiomMap.get(qname) != null))
            return true;
        if (listAssembler.existsKey(ListType.axiom_item, qname))
            return true;
        if (axiomResourceMap.get(qname) != null)
            return true;
        return false;
    }

	/**
	 * Register axiom list by adding it's axiom listener to this ParserAssembler object
	 * @param qname The qualified name of the axioms inserted into the list
	 * @param axiomListener The axiom listener
	 */
	public void registerAxiomListener(QualifiedName qname, AxiomListener axiomListener) 
	{   // Note to listen for solution notifications, qname must be template name 
	    listAssembler.add(qname, axiomListener);
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
     * Returns operand which invokes an external function call. 
     * The function name must consist of 2 parts. The first is the name of a library.
     * The type of object returned from the call depends on the library.
     * @param qname Qualified function name
     * @param parametersTemplate Operand arguments packaged in an inner template or null for no arguments
     * @return CallOperand object
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Operand getCallOperand(QualifiedName qname, Template parametersTemplate)
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
        CallOperand callOperand = new CallOperand(QualifiedName.parseName(callName, qname), parametersTemplate, callEvaluator);
        scope.addDebugTarget(callOperand);
        return callOperand;
    }

    /**
     * Macro to invoke function and return value in anonymous parameter
     * @param library Function library
     * @param name Function name
     * @param termList List of call parameters, may be empty
     * @return Parameter object
     */
    public Parameter callFunction(String library, String name,  List<Term> termList)
    {
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
        Object object = callEvaluator.evaluate(termList);
        return new Parameter(Term.ANONYMOUS, object);
    }

    /**
     * Returns operand which invokes a calculator query.
     * @param queryName Name used to identify query
     * @param qualifiedQueryName Qualified query name - can be qualified by the name of a scope
     * @param parameterTemplate Template to evaluate parameters passed to calculator
     * @param innerTemplate Template to receive solution
     * @return TermOperand object
     */
    public Operand getQueryOperand(
            String queryName, 
            QualifiedName qualifiedQueryName,
            Template parameterTemplate,
            Template innerTemplate)
    {
        if ((innerTemplate != null) && !operandMap.hasOperand(innerTemplate.getQualifiedName()))
            addInnerTemplate(innerTemplate);
        QueryEvaluator queryEvaluator = 
             new QueryEvaluator(queryName, qualifiedQueryName, innerTemplate);
        scope.addDebugTarget(queryEvaluator);
        ParserTask parserTask = parserTaskQueue.addPending(queryEvaluator, scope);
        parserTask.setPriority(ParserTask.Priority.list.ordinal());
        String library = queryEvaluator.getLibrayName(this);
        QualifiedName qualifiedCallName = new QualifiedName(library, qualifiedQueryName.getName());
        AxiomTermListEvaluator evaluator = new AxiomTermListEvaluator(qualifiedCallName, queryEvaluator, parameterTemplate);
        return new TermOperand(evaluator);
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
     * Returns qualified name for name in current context
     * @param name
     * @return QualifiedName object
     */
    public QualifiedName getContextName(String name)
    {
        return QualifiedName.parseName(name, qualifiedContextname);
    }
    
    /**
     * Returns operand identified by name
     * @param operandName
     * @return Operand object from same scope or global scope or null if not found
     */
    public Operand findOperandByName(String operandName)
    {
        QualifiedName qualifiedOperandName = QualifiedName.parseName(operandName, qualifiedContextname);
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
     * Add Runnable to pending list
     * @param pending Runnable to execute parser task
     */
    public ParserTask addPending(ParserRunner pending)
    {
        return parserTaskQueue.addPending(pending, scope);
    }

    /**
     * Collect pending parser tasks into priority queue
     */
    public void getPending(PriorityQueue<ParserTask> priorityQueue)
    {
        parserTaskQueue.getPending(priorityQueue);
    }

    /**
     * Add operand to operand map, handling special case for self template variable 
     * @param name
     * @return
     */
    public Operand addOperand(String name)
    {
        final QualifiedName qualifiedName = QualifiedName.parseName(name);
        if (!qualifiedName.getTemplate().isEmpty())
            throw new ExpressionException("Variable name \"" + name + "\" is invalid");
        String part1 = qualifiedName.getScope();
        if (part1.isEmpty() || !qualifiedContextname.getTemplate().equals(part1))
            return operandMap.addOperand(name, null, qualifiedContextname);
        Operand operand = operandMap.getOperand(qualifiedName);
        if (operand == null)
        {
            operand = new Variable(qualifiedName)
            {
                public EvaluationStatus evaluate(int id)
                {
                    AxiomTermList itemList = listAssembler.getAxiomTerms(qualifiedName);
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

}
