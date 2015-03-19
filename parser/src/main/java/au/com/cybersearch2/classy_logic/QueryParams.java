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

/**
 * QueryParams
 * Composes parameters for query from QuerySpec and supplied Scope.
 * Note the scope may internally reference the global Scope
 * @author Andrew Bowley
 * 9 Jan 2015
 */
public class QueryParams 
{
	protected QuerySpec querySpec;
    /** 1st query parameter - AxiomCollection object */
	protected AxiomMapCollection axiomEnsemble;
	/** 2nd query parameter - List of Template objects */ 
	protected List<Template> templateList;
	/** Scope object which provides objects to be passed to the query */
	protected Scope scope;
	protected SolutionHandler solutionHandler;
	protected List<Axiom> scopeAxiomList;


	/**
	 * Construct QueryParams object
	 * @param scopeName
	 * @param queryName
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
	
	public void initialize()
	{
		scope.notifyChange();
		if (scopeAxiomList != null)
			for (Axiom axiom: scopeAxiomList)
				scope.getGlobalScope().getParserAssembler().addScopeAxiom(axiom); 
		templateList = new ArrayList<Template>();
		axiomEnsemble = new AxiomMapCollection();
		for (KeyName keyname: querySpec.getKeyNameList())
		{
			String axiomKey = keyname.getAxiomKey();
			if (!axiomKey.isEmpty())
				axiomEnsemble.put(axiomKey, scope.getAxiomSource(axiomKey));
			String templateName = keyname.getTemplateName();
			Template template = scope.getTemplate(templateName);
			if (template == null)
				throw new IllegalArgumentException("Template \"" + templateName + "\" does not exist");
			if (!axiomKey.isEmpty()) // Empty axiom key indicates no axiom
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
	 * @return the solutionHandler
	 */
	public SolutionHandler getSolutionHandler() 
	{
		return solutionHandler;
	}

	/**
	 * @param solutionHandler the solutionHandler to set
	 */
	public void setSolutionHandler(SolutionHandler solutionHandler) 
	{
		this.solutionHandler = solutionHandler;
	}

	/**
	 * @return the scope
	 */
	public Scope getScope() 
	{
		return scope;
	}

	/**
	 * @return the querySpec
	 */
	public QuerySpec getQuerySpec() 
	{
		return querySpec;
	}

	public Axiom addAxiom(String name, Object... params)
	{
		Axiom axiom = new Axiom(name, params);
		if (scopeAxiomList == null)
			scopeAxiomList = new ArrayList<Axiom>();
		scopeAxiomList.add(axiom);
		return axiom;
	}
}
