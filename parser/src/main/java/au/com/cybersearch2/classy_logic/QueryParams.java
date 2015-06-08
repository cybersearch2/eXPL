package au.com.cybersearch2.classy_logic;

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.KeyName;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.AxiomMapCollection;
import au.com.cybersearch2.classy_logic.query.QuerySpec;
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
	/** List of optional axioms to add to global scope */
	protected List<Axiom> scopeAxiomList;


	/**
	 * Construct QueryParams object
	 * @param queryProgram The global compiler accumulator
	 * @param scopeName The scope the query applies to
	 * @param queryName Name of query in scope
	 */
	public QueryParams(QueryProgram queryProgram, String scopeName, String queryName)
	{
		scope = queryProgram.getScope(scopeName);
		querySpec = scope.getQuerySpec(queryName);
		if (querySpec == null)
			throw new IllegalArgumentException("Query \"" + queryName + "\" does not exist");
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
		// Set scope axioms in global scope
		if (scopeAxiomList != null)
			for (Axiom axiom: scopeAxiomList)
				scope.getGlobalScope().getParserAssembler().addScopeAxiom(axiom); 
		// Collect query axiom sources and templates
		templateList = new ArrayList<Template>();
		axiomEnsemble = new AxiomMapCollection();
		// Iterate through list of query specification KeyNames
		for (KeyName keyname: querySpec.getKeyNameList())
		{
		    // Collect axiom source
			String axiomKey = keyname.getAxiomKey();
			if (!axiomKey.isEmpty())
				axiomEnsemble.put(axiomKey, scope.getAxiomSource(axiomKey));
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

	/**
	 * Returns 1st query parameter
	 * @return AxiomCollection
	 */
	public AxiomCollection getAxiomCollection() 
	{
		if (axiomEnsemble == null)
			initialize();
		return axiomEnsemble;
	}

	/**
	 * Returns 2nd query parameter
	 * @return List of Template objects
	 */
	public List<Template> getTemplateList() 
	{
		if (templateList == null)
			initialize();
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
	 * Add an axiom populated with supplied parameters to
	 * @param name
	 * @param params
	 * @return Axiom object
	 */
	public Axiom addAxiom(String name, Object... params)
	{
		Axiom axiom = new Axiom(name, params);
		if (scopeAxiomList == null)
			scopeAxiomList = new ArrayList<Axiom>();
		scopeAxiomList.add(axiom);
		return axiom;
	}
}
