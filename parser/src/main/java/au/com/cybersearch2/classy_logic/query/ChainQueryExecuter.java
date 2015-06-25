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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * ChainQueryExecuter
 * @author Andrew Bowley
 * 23 Jan 2015
 */
public class ChainQueryExecuter 
{
	protected Scope scope;
    /** Head of optional query chain */
	protected ChainQuery headChainQuery;
	/** Tail of optional query chain */
	protected ChainQuery tailChainQuery;
    /** The solution is a collection of axioms referenced by name */
    protected Solution solution;
	/** Set of axiom listeners referenced by name */
	protected Map<String, List<AxiomListener>> axiomListenerMap;

	/**
	 * Construct ChainQueryExecuter object
	 * @param scope Query context
	 */
	public ChainQueryExecuter(Scope scope) 
	{
		this.scope = scope;
		solution = new Solution();
		if ((scope != null) && (scope.getAxiomListenerMap() != null))
		{   // Create a copy of the axiom listener map and remove entries as axiom listeners are bound to processors
			axiomListenerMap = new HashMap<String, List<AxiomListener>>();
			axiomListenerMap.putAll(scope.getAxiomListenerMap());
		}
	}

	/**
	 * Returns solution
	 * @return Collection of axioms referenced by name. The axioms reference the templates supplied to the query.
	 */
	public Solution getSolution() 
	{
		return solution;
	}

	/**
	 * Find next solution. Call getSolution() to obtain an unmodifiable version.
	 * @return Flag to indicate solution found
	 */
	public boolean execute()
    {
		if (axiomListenerMap != null)
			bindAxiomListeners();
		if ((headChainQuery == null) ||
			    // Query chain will add to solution or trigger short circuit
			    (headChainQuery.executeQuery(solution) == EvaluationStatus.COMPLETE))
				return true;
		return false;
    }

	/**
	 * Bind axiom listeners to processors. This is a late binding step for any outstanding oubound list variables. 
	 */
	protected void bindAxiomListeners()
	{
		Set<String> keys = axiomListenerMap.keySet();
		for (String key: keys)
		{
        	AxiomSource axiomSource = scope.findAxiomSource(key);
        	// TODO - Log warning if axiom source not found
        	if (axiomSource !=null)
        	{
        	    List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
        	    for (AxiomListener axiomListener: axiomListenerList)
        	        axiomListener.onNextAxiom(axiomSource.iterator().next());
        	}
		}
		// Delete the axiom listener map as binding is complete
		axiomListenerMap = null;
	}
	
	/**
	 * Add chain query
	 * @param axiomEnsemble2 A collection of axiom sources which are referenced by name
	 * @param templateList2 The template sequence. Chain resolved first time all templates solved.
	 */
	public void chain(AxiomCollection axiomEnsemble2, List<Template> templateList2)
	{
		ChainQuery chainQuery = new LogicChainQuery(axiomEnsemble2, templateList2);
		if (axiomListenerMap != null)
			// Bind each axiom listener to a query or the solution depending 
			// on the referenced axiom being available in the axiom ensemble
			for (Template template: templateList2)
			{
				String key = template.getKey();
		        if (axiomListenerMap.containsKey(key))
		        {
		        	AxiomSource axiomSource = axiomEnsemble2.getAxiomSource(key);
		        	List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
		        	if (axiomSource != null)
		        	{
		        		for (AxiomListener axiomListener: axiomListenerList)
		        			chainQuery.setAxiomListener(key, axiomListener);
		        		axiomListenerMap.remove(key);
		        	}
		        }
	        	else
	        	{
	        		key = template.getName();
			        if (axiomListenerMap.containsKey(key))
			        {
			        	List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
		        		for (AxiomListener axiomListener: axiomListenerList)
		        			solution.setAxiomListener(key, axiomListener);
		        		axiomListenerMap.remove(key);
			        }
	        	}

			}
		addChainQuery(chainQuery);
	}

	/**
	 * Add a calculate chain query
	 * @param axiom Optional axiom to initialize Calculator
	 * @param template Template to unify and evaluate
	 */
	public void chainCalculator(Axiom axiom, Template template) 
	{
		if (axiom == null)
			template.setKey("");
		else
			template.setKey(axiom.getName());
		CalculateChainQuery chainQuery = new CalculateChainQuery(axiom, template);
		if (template.isChoice())
		{
			Choice choice = new Choice(template.getName(), scope);
			chainQuery.setChoice(choice);
		}
		if (axiomListenerMap != null)
		{
			String key = template.getName();
	        if (axiomListenerMap.containsKey(key))
	        {
	        	List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
        		for (AxiomListener axiomListener: axiomListenerList)
        			solution.setAxiomListener(key, axiomListener);
        		axiomListenerMap.remove(key);
	        }
		}
		addChainQuery(chainQuery);
	}


	/**
	 * Force reset to initial state
	 */
	public void reset() 
	{
		if (headChainQuery != null)
		{
			ChainQuery chainQuery = headChainQuery;
			do
			{
				chainQuery.reset();
				chainQuery = chainQuery.getNext();
			} while (chainQuery != null);
		}
	}

	/**
	 * Returns iterator to walk the query chain
	 * @return Iterator of generic type ChainQuery
	 */
	public Iterator<ChainQuery> chainQueryIterator()
	{
		List<ChainQuery> chainQueryList = new ArrayList<ChainQuery>();
		ChainQuery query = headChainQuery;
		while (query != null)
		{
			chainQueryList.add(query);
			query = query.getNext();
		}
		return chainQueryList.iterator();
	}

	/**
	 * Returns query as String. Shows empty terms with "?",
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		Iterator<ChainQuery> iterator = chainQueryIterator();
		StringBuilder builder = new StringBuilder();
		boolean firstTime = true;
	    while(iterator.hasNext())
	    {
	    	if (firstTime)
	    		firstTime = false;
	    	else
	    		builder.append(", ");
	    	builder.append(iterator.next().toString().replaceAll("\\<empty\\>", "?")).append(System.getProperty("line.separator"));
	    }
	    return builder.toString();	
	}


	/**
	 * Add chain query
	 * @param chainQuery ChainQuery
	 */
	protected void addChainQuery(ChainQuery chainQuery)
	{
		if (headChainQuery == null)
		{
			headChainQuery = chainQuery;
			tailChainQuery = chainQuery;
		}
		else
		{
			tailChainQuery.setNext(chainQuery);
			tailChainQuery = chainQuery;
		}
	}
	

	/**
	 * Backup to start state
	 */
	public void backupToStart()
	{
		if (headChainQuery != null)
		{
			ChainQuery chainQuery = headChainQuery;
			do
			{
				chainQuery.backupToStart();
				chainQuery = chainQuery.getNext();
			} while (chainQuery != null);
		}
	}

}
