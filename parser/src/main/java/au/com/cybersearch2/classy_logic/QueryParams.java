package au.com.cybersearch2.classy_logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.axiom.AxiomMapCollection;
import au.com.cybersearch2.classy_logic.axiom.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
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
        			templateList.add(template);
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
}
