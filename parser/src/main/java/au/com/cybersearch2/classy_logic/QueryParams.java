package au.com.cybersearch2.classy_logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.AxiomMapCollection;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * QueryParams
 * Composes parameters for query from QuerySpec and supplied Scope.
 * Note the scope may internally reference the global Scope
 * @author Andrew Bowley
 * 9 Jan 2015
 */
public class QueryParams 
{
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
    /** Properties for calculations referenced by template name */
    protected Map<String, Map<String, Object>> propertiesMap;


	/**
	 * Construct QueryParams object
	 * @param scope Specified scope
	 * @param querySpec Specifies query parameters as key name sequence(s)
	 */
	public QueryParams(Scope scope, QuerySpec querySpec)
	{
		this.scope = scope;
		this.querySpec = querySpec;
        propertiesMap = new HashMap<String, Map<String, Object>>();
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
    			String axiomKey = keyname.getAxiomKey();
    			if (!axiomKey.isEmpty())
    			{
    			    AxiomSource axiomSource = scope.findAxiomSource(axiomKey);
    			    if ((axiomSource == null) && (initialSolution != null))
    			    {
    			        Axiom axiom = initialSolution.getAxiom(axiomKey);
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
                        axiomEnsemble.put(axiomKey, axiomSource);
    			}
        	    if (isStart)
        	    {
            		// Collect template
        			String templateName = keyname.getTemplateName();
        			Template template = scope.getTemplate(templateName);
        			if (template == null)
        				throw new IllegalArgumentException("Template \"" + templateName + "\" does not exist");
        			if (!axiomKey.isEmpty()) // Empty axiom key indicates no axiom
        			    // Setting template key here faciltates unification
        				template.setKey(axiomKey);
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
		return solutionHandler != null ? solutionHandler : new SolutionHandler(){

            @Override
            public boolean onSolution(Solution solution)
            {
                return true;
            }};
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
     * @param templateName Name of template to which the properties apply
     * @param properties Calculator properties
     */
    public void putProperties(String templateName, Map<String, Object> properties) 
    {
        if ((properties != null) && (properties.size() > 0))
            propertiesMap.put(templateName, properties);
    }

    /**
     * Returns properties referenced by template name or null if no properties found
     * @param templateName Template name of calculator
     * @return Properties object
     */
    public Map<String, Object> getProperties(String templateName) 
    {
        return propertiesMap.get(templateName);
    }

    /**
     * Clears properties referenced by template name or null if no properties found
     * @param templateName Template name of calculator
     * @return Properties object
     */
    public void clearProperties(String templateName) 
    {
        Map<String, Object> properties = propertiesMap.get(templateName);
        if (properties != null)
            properties.clear();
    }

    public boolean hasInitialSolution()
    {
        return initialSolution != null;
    }
    
    public Solution getInitialSolution()
    {
        if (initialSolution == null)
            initialSolution = new Solution();
        return initialSolution;
    }
}
