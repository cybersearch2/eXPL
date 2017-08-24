package au.com.cybersearch2.classy_logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.axiom.AxiomMapCollection;
import au.com.cybersearch2.classy_logic.axiom.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.QueryType;
import au.com.cybersearch2.classy_logic.query.Solution;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * QueryParams
 * Composes parameters for query from QuerySpec and supplied Scope.
 * Note the scope may internally reference the global Scope
 * @author Andrew Bowley
 * 9 Jan 2015
 */
public class QueryParams 
{
    public static SolutionHandler DO_NOTHING;
    
    /** Query specification */
	protected QuerySpec querySpec;
    /** 1st query parameter - AxiomCollection object */
	protected AxiomMapCollection axiomEnsemble;
	/** 2nd query parameter - List of Template objects */ 
	protected List<Template> templateList;
	/** Scope object which provides objects to be passed to the query */
	protected Scope scope;
	/** Solution handler (optional). Do-nothing handler applied if none supplied */
	protected SolutionHandler solutionHandler;
	/** Solution (optional). Source of initialization axioms */
	protected Solution initialSolution;
    /** Container for template axiom parameters */
    protected Map<QualifiedName, Axiom> parametersMap;

    static
    {
        DO_NOTHING = new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                return true;
            }};
    }
    
	/**
	 * Construct QueryParams object
	 * @param scope Specified scope
	 * @param querySpec Specifies query parameters as key name sequence(s)
	 */
	public QueryParams(Scope scope, QuerySpec querySpec)
	{
		this.scope = scope;
		this.querySpec = querySpec;
	}

	/**
	 * Initialize these query parameters
	 */
	public void initialize()
	{
	    // Notify locale listeners of change of scope
		scope.notifyChange();
		boolean isStart = (axiomEnsemble == null) || (templateList == null);
		if (isStart)
		{
		    // Collect query axiom sources and templates
		    templateList = new ArrayList<Template>();
		    axiomEnsemble = new AxiomMapCollection();
		    if (querySpec.getQueryType() == QueryType.calculator)
		        postProcessCalculatorType();
		}
		// Iterate through list of query specification KeyNames
		if (isStart || (initialSolution != null))
    		for (KeyName keyname: querySpec.getKeyNameList())
    		{
    		    // Collect axiom source
    			QualifiedName axiomKey = keyname.getAxiomKey();
    			if (!axiomKey.getName().isEmpty())
    			{
    			    AxiomSource axiomSource = scope.findAxiomSource(axiomKey);
    			    if ((axiomSource == null) && (initialSolution != null))
    			    {
    			        Axiom axiom = initialSolution.getAxiom(axiomKey.getName());
    			        if (axiom != null)
    			        {
    			            axiomSource = new SingleAxiomSource(axiom);
    			            scope.getParserAssembler().addScopeAxiom(axiom);
    			        }
    			    }
    			    if (axiomSource == null)
    			        // Trigger source not found exception
    			        scope.getAxiomSource(axiomKey);
    			    else
                        axiomEnsemble.put(axiomKey.getName(), axiomSource);
    			}
        	    if (isStart)
        	    {
            		// Collect template
        			Template template = scope.getTemplate(keyname.getTemplateName());
        			if (template == null)
        				throw new IllegalArgumentException("Template \"" + keyname.getTemplateName().toString() + "\" does not exist");
        			if (!axiomKey.getName().isEmpty()) // Empty axiom key indicates no axiom
        			    // Setting template key here faciltates unification
        				template.setKey(axiomKey.getName());
        			else // Reset template key in case it was set in a previous query
        			    template.setKey(template.getQualifiedName().getTemplate());
                    boolean isReplicate = false;
                    String scopeName = keyname.getTemplateName().getScope();
                    if (!scopeName.isEmpty()) 
                        isReplicate = !scopeName.equals(scope.getName());
                    if (!isReplicate)
        			    templateList.add(template);
                    else
                        templateList.add(new Template(template, keyname.getTemplateName()));

        	    }
    		}
	}

    /**
	 * Returns 1st query parameter
	 * @return AxiomCollection
	 */
	public AxiomCollection getAxiomCollection() 
	{
		return axiomEnsemble;
	}

	/**
	 * Returns 2nd query parameter
	 * @return List of Template objects
	 */
	public List<Template> getTemplateList() 
	{
		return templateList;
	}

	/**
	 * Returns solution handler. Return do-nothing handler if none supplied
	 * @return SolutionHandler object
	 */
	public SolutionHandler getSolutionHandler() 
	{
		return solutionHandler != null ? solutionHandler : DO_NOTHING;
	}

	/**
	 * Set the solution handler
	 * @param solutionHandler the solutionHandler to set
	 */
	public void setSolutionHandler(SolutionHandler solutionHandler) 
	{
		this.solutionHandler = solutionHandler;
	}

	/**
	 * Returns the scope
	 * @return Scope object
	 */
	public Scope getScope() 
	{
		return scope;
	}

	/**
	 * Returns query specification
	 * @return QuerySpec object
	 */
	public QuerySpec getQuerySpec() 
	{
		return querySpec;
	}

    /**
     * Add axiom key / template name pair for calculator query, along with optional properties
     * @param qualifiedTemplateName Qualified name of template to which the properties apply
     * @param properties Calculator properties
     */
    public void putProperties(QualifiedName qualifiedTemplateName, Map<String, Object> properties) 
    {
        if ((properties != null) && (properties.size() > 0))
        {
            Axiom calculatorAxiom = new Axiom(qualifiedTemplateName.getName());
            for (Map.Entry<String, Object> entry: properties.entrySet())
                calculatorAxiom.addTerm(new Parameter(entry.getKey(), entry.getValue()));
            if (parametersMap == null)
                parametersMap = new HashMap<QualifiedName, Axiom>();
            parametersMap.put(qualifiedTemplateName, calculatorAxiom);
        }
    }

    /**
     * Returns properties referenced by template name or null if no properties found
     * @param qualifiedTemplateName Qualified name of template to which the properties apply
     * @return Properties object
     */
    public Axiom getParameter(QualifiedName qualifiedTemplateName) 
    {
        return parametersMap == null ? null : parametersMap.get(qualifiedTemplateName);
    }

    /**
     * Returns flag set true if an initial solution has been provided
     * @return boolean
     */
    public boolean hasInitialSolution()
    {
        return initialSolution != null;
    }
 
    /**
     * Returns initial solution, creating one if it does not exist
     * @return Solution object
     */
    public Solution getInitialSolution()
    {
        if (initialSolution == null)
            initialSolution = new Solution();
        return initialSolution;
    }

    /**
     * Perform query type conversion from calculator to logic if first keyname specifies
     * axiom name   
     */
    protected void postProcessCalculatorType()
    {
        KeyName firstKeyname = querySpec.getKeyNameList().get(0);
        QualifiedName axiomName = firstKeyname.getAxiomKey();
        if (axiomName.getName().isEmpty())
            return;
        // Create new head logic query where axiom and template key names are same
        Template template = createTemplate(axiomName);
        QualifiedName templateName = firstKeyname.getTemplateName();
        Template firstTemplate = scope.getTemplate(templateName);
        firstTemplate.setKey(axiomName.getName());
        querySpec.getKeyNameList().clear();
        querySpec.getKeyNameList().add(new KeyName(axiomName, template.getQualifiedName()));
        querySpec.setQueryType(QueryType.logic);
        // Append new calculator query spec to head query spec.
        QuerySpec chainQuerySpec = querySpec.prependChain();
        // Create new keyname with empty axiom key to indicate get axiom from solution
        KeyName calculateKeyname = new KeyName(firstKeyname.getTemplateName());
        chainQuerySpec.addKeyName(calculateKeyname);
        chainQuerySpec.setQueryType(QueryType.calculator);
        List<Term> termList = querySpec.getProperties(firstTemplate.getName());
        if ((termList != null) && (termList.size() > 0))
           chainQuerySpec.putProperties(calculateKeyname, termList);
    }

    /**
     * Returns template to insert in front of calculator query
     * @param axiomName Name of axiom to pair with
     * @return Template object
     */
    protected Template createTemplate(QualifiedName axiomName)
    {
        // Check for logic query template already exists. Not expected to exist.
        // The template name is taken from the axiom key
        String scopeName = axiomName.getScope();
        if (scopeName.isEmpty())
            scopeName = QueryProgram.GLOBAL_SCOPE; 
        QualifiedName qualifiedTemplateName = new QualifiedTemplateName(scopeName, axiomName.getName());
        Scope axiomScope = scope.getScope(scopeName);
        if (axiomScope == null)
            throw new ExpressionException("Scope \"" + scopeName + "\" not found");
        Template logicTemplate = scope.findTemplate(qualifiedTemplateName);
        if (logicTemplate == null) 
        {
            if (QueryProgram.GLOBAL_SCOPE.equals(scopeName))
            {
                if (!scope.getName().equals(scopeName))
                   logicTemplate = scope.getGlobalTemplateAssembler().getTemplate(new QualifiedTemplateName(scope.getName(), axiomName.getName()));
            }
            else
                // Use global scope template if scope specified
                logicTemplate = scope.getGlobalTemplateAssembler().getTemplate(new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, axiomName.getName()));
        }
        if (logicTemplate == null)
        {
            // Create new logic query template which will populated with terms to match axiom terms at start of query
            logicTemplate = 
                axiomScope.getParserAssembler().getTemplateAssembler()
                .createTemplate(qualifiedTemplateName, false);
            logicTemplate.setKey(axiomName.getName());
        }
        return logicTemplate;
    }
}
