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
package au.com.cybersearch2.classy_logic.query;

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.TermList;

/**
 * LogicQueryExecuter
 * Performs logic operations according to configuration of axiom sequences and templates.
 * Performs chained queries which add to solutions provided by main query.
 * @author Andrew Bowley
 * 30 Dec 2014
 */
public class LogicQueryExecuter extends ChainQueryExecuter
{

    /**
	 * QuerySolutionHander 
	 * Chains to next query following solution found for prior query in the chain
	 */
	class QuerySolutionHander implements SolutionHandler
	{
		/** Query index. The value 0 represents the head of the chain. */
		int index;
		
		public QuerySolutionHander(int index)
		{
			this.index = index;
		}

		/**
		 * Handle solution found event
		 * @param solution The axiom-containing Solution
		 * @return Flag set true if complete solution found
		 */
		@Override
		public boolean onSolution(Solution solution) 
		{
			// Execute the next query in the chain
    		LogicQuery nextQuery = logicQueryList.get(index+1);
    		Template nextTemplate = templateList.get(index + 1);
    		if (nextQuery.getQueryStatus() == QueryStatus.complete)
    		    return false;
    		{
    		    nextTemplate.backup(true);
    		    solution.remove(nextTemplate.getQualifiedName().toString());
    		}
    		return nextQuery.iterate(solution, nextTemplate, context);
		}
	}

	/** The query components, each working with a sequence of axioms */
    protected List<LogicQuery> logicQueryList;
    /** A collection of axiom sources which are referenced by name */
    protected AxiomCollection axiomCollection;
    /** The template sequence. Each template is assigned to a LogicQuery object */
    protected List<Template> templateList;
    /** Head of SolutionHandler chain. Note all queries except tail are assigned a SolutionHandler */
	protected SolutionHandler headSolutionHandler;

	/**
	 * Construct a LogicQueryExecuter object 
	 * @param queryParams The query parameters
	 */
	public LogicQueryExecuter(QueryParams queryParams) 
	{
		super(queryParams);
		if (queryParams.hasInitialSolution())
		    setSolution(queryParams.getInitialSolution());
	    else
	        setSolution(new Solution());
		// A collection of axiom sources which are referenced by name
		this.axiomCollection = queryParams.getAxiomCollection();
		// The template sequence. Each template is assigned to a LogicQuery object
		this.templateList = queryParams.getTemplateList();
		logicQueryList = new ArrayList<LogicQuery>();
		// Populate logicQueryList
		initialize();
	}
	
	/**
	 * Find next solution. Call getSolution() to obtain an unmodifiable version.
	 * @return Flag to indicate solution found
	 */
	@Override
	public boolean execute()
    {
		// At least one LogicQuery will have been created on construction
		LogicQuery logicQuery = logicQueryList.get(0);
		if (logicQuery.getQueryStatus() == QueryStatus.complete)
			return false;
		do
		{
			switch (logicQuery.getQueryStatus())
			{
			case in_progress:
			    if (executeNext())
			        return true;
			    break;
			case start:
	            ScopeNotifier scopeNotifier = logicQuery.getScopeNotifier();
	            if (scopeNotifier != null)
	                scopeNotifier.notifyScopes();
	            else if (axiomListenerMap != null)
					bindAxiomListeners(queryScope);
				if (logicQuery.iterate(solution, templateList.get(0), context))
				{
					if (super.execute())
						return true;
				}
				break;
			default:
			}
		} while(logicQuery.getQueryStatus() != QueryStatus.start);
		logicQuery.setQueryStatusComplete();
		return false;
    }

    /**
	 * Force reset to initial state
	 */
	@Override
	public void reset() 
	{
		for (int i = 0; i < templateList.size(); i++)
		{
			Template template = templateList.get(i);
			template.reset();
		}
		super.reset();
	}

	/**
	 * Returns query as String. Shows empty terms with "?",
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return toString(templateList);
	}

	/**
	 * Execute next iteration of current query
	 * @return flag set true if solution found
	 */
	protected boolean executeNext()
    {
        int next = logicQueryList.size() - 1;
        LogicQuery nextQuery = null;
        Template nextTemplate = null;
        while (next >= 0)
        {
            nextQuery = logicQueryList.get(next);
            if (nextQuery.getQueryStatus() == QueryStatus.in_progress)
            {
                nextTemplate = templateList.get(next);
                nextTemplate.backup(true);
                solution.remove(nextTemplate.getQualifiedName().toString());
                super.backupToStart();
                ScopeNotifier scopeNotifier = nextQuery.getScopeNotifier();
                if (scopeNotifier != null)
                    scopeNotifier.notifyScopes();
                //else if (axiomListenerMap != null)
                //    bindAxiomListeners(queryScope);
                if (nextQuery.iterate(solution, nextTemplate, context))
                {
                    return super.execute();
                }
                backupToStart(next);
            }
            --next;
        }
        return false;
    }

	/**
	 * Initialize the LogicQuery object list. All but the last object requires a solution handler.
	 */
	protected void initialize()
	{
		for (int i = 0; i < templateList.size(); i++)
		{   // Use the template key to reference the corresponding axiom source
			Template template = templateList.get(i);
			String key = template.getKey();
			//System.out.println("Name = " + template.getName() + ", key = " + template.getKey());
            QualifiedName qname = QualifiedName.parseGlobalName(key);
			LogicQuery logicQuery = null;
			if (i < templateList.size() - 1)
			{   // Create solution handler which causes the next LogicQuery object in the chain
				// to find a solution.
				final int index = i;
				logicQuery = new LogicQuery(axiomCollection.getAxiomSource(key), 
						                    new QuerySolutionHander(index));
			}
			else
				logicQuery = new LogicQuery(axiomCollection.getAxiomSource(key));

	        String scopeName = template.getQualifiedName().getScope();
	        if (!scopeName.isEmpty()) 
	        {
	            ScopeNotifier scopeNotifier = QueryLauncher.getScopeNotification(this, queryScope, queryScope.findScope(scopeName));
	            logicQuery.setScopeNotifier(scopeNotifier);
	        }
			
			logicQueryList.add(logicQuery);
			if (axiomListenerMap != null)
			{
                if (axiomListenerMap.containsKey(qname))
                {
    	        	List<AxiomListener> axiomListenerList = axiomListenerMap.get(qname);
            		for (AxiomListener axiomListener: axiomListenerList)
            			logicQuery.setAxiomListener(axiomListener);
	        		axiomListenerMap.remove(qname);
                }
                qname = template.getQualifiedName();
            	if (axiomListenerMap.containsKey(qname))
            	{
    	        	List<AxiomListener> axiomListenerList = axiomListenerMap.get(qname);
            		for (AxiomListener axiomListener: axiomListenerList)
            			solution.setAxiomListener(qname, axiomListener);
	        		axiomListenerMap.remove(qname);
            	}
			}
		}
		if (templateList.size() > 1)
			headSolutionHandler = new QuerySolutionHander(0);
	}

	/**
	 * Backup to start state
	 * @param start Index of first template index to backup
	 */
	protected void backupToStart(int start)
	{
		for (int i = start; i < templateList.size(); i++)
		{
			Template template = templateList.get(i);
			template.backup(false);
		}
	}

	/**
	 * Returns text representation of query where empty terms are expressed as '?' 
	 * @param structureList List of templates contained in query
	 * @return String
	 */
	static protected String toString(List<? extends TermList<?>> termLists)
	{
		StringBuilder builder = new StringBuilder();
		boolean firstTime = true;
	    for (TermList<?> termList: termLists)
	    {
	    	if (firstTime)
	    		firstTime = false;
	    	else
	    		builder.append(", ");
	    	builder.append(termList.toString().replaceAll("\\<empty\\>", "?"));
	    }
	    return builder.toString();	
	}



}
