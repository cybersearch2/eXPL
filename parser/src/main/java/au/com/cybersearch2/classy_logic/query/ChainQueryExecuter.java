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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.com.cybersearch2.classy_logic.QueryParams;
import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.QualifiedTemplateName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.DebugTarget;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * ChainQueryExecuter
 * Executes a chain query and returns a solution
 * @author Andrew Bowley
 * 23 Jan 2015
 */
public class ChainQueryExecuter implements DebugTarget
{
    /**  Query scope */
	protected Scope queryScope;
    /** Head of optional query chain */
	protected ChainQuery headChainQuery;
	/** Tail of optional query chain */
	protected ChainQuery tailChainQuery;
    /** The solution is a collection of axioms referenced by name */
    protected Solution solution;
	/** Set of axiom listeners referenced by name */
	protected Map<QualifiedName, List<AxiomListener>> axiomListenerMap;
	/** Template chain passed down chain to manage case of more than one query in chain
	 *  eg. repeating same query in different scopes */
	protected Deque<Template> templateChain;
    /** Execution context for debugging */
    protected ExecutionContext context;

	/**
	 * Construct ChainQueryExecuter object
	 * @param queryParams Query parameters
	 */
	public ChainQueryExecuter(QueryParams queryParams) 
	{   
	    templateChain = new ArrayDeque<Template>();
		this.queryScope = queryParams.getScope();
        if ((queryScope != null) && (queryScope.getAxiomListenerMap() != null))
        {   // Create a copy of the axiom listener map and remove entries as axiom listeners are bound to processors
            axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
            axiomListenerMap.putAll(queryScope.getAxiomListenerMap());
        }
    }

	/**
	 * Find next solution. Call getSolution() to obtain an unmodifiable version.
	 * @return Flag to indicate solution found
	 */
	public boolean execute()
    {
		if (axiomListenerMap != null)
			bindAxiomListeners(queryScope);
		if (headChainQuery != null)
		{
            ScopeNotifier scopeNotifier = headChainQuery.getScopeNotifier();
            if (scopeNotifier != null)
                scopeNotifier.notifyScopes();
		    // Query chain will add to solution or trigger short circuit
		    return (headChainQuery.executeQuery(solution, templateChain, context) == EvaluationStatus.COMPLETE);
		}
		return true;
    }

	/**
	 * Add chain query
	 * @param axiomCollection A collection of axiom sources which are referenced by name
	 * @param template Template 
	 */
	public void chain(AxiomCollection axiomCollection, Template template, ScopeNotifier scopeNotifier)
	{
	    LogicChainQuery chainQuery = new LogicChainQuery(axiomCollection, template, scopeNotifier);
		if (axiomListenerMap != null)
			// Bind each axiom listener to a query or the solution depending 
			// on the referenced axiom being available in the axiom collection
			bindAxiomListener(template, chainQuery);
		addChainQuery(chainQuery);
	}

	/**
	 * Add a calculate chain query
	 * @param axiom Optional axiom to initialize Calculator
	 * @param template Template to unify and evaluate
     * @param scopeNotifier Object to notify of scope change, null if not required
     * @return CalculateChainQuery object to execute calculator
	 */
	public CalculateChainQuery chainCalculator(Axiom axiom, Template template, ScopeNotifier scopeNotifier) 
	{
		if (axiom == null)
		{
		    if (template.getKey() == null)
			     template.setKey("");
		}
		else
			template.setKey(axiom.getName());
		CalculateChainQuery chainQuery = new CalculateChainQuery(axiom, template, scopeNotifier);
        QualifiedName qname = template.getQualifiedName();
		if (template.isChoice())
		{   // Pass scope identified by choice name to Choice constructor
		    Scope choiceScope = qname.getScope().isEmpty() ?
		        queryScope.getGlobalScope() : 
		        queryScope.findScope(qname.getScope());
			Choice choice = new Choice(new QualifiedName(choiceScope.getName(), template.getName()), choiceScope);
			chainQuery.setChoice(choice);
		}
		if (axiomListenerMap != null)
		    bindCalculatorAxiomListener(template);
		addChainQuery(chainQuery);
		return chainQuery;
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

    @Override
    public void setExecutionContext(ExecutionContext context)
    {
        this.context = context;
    }

    /**
	 * Force reset to initial state
	 */
	protected void reset() 
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
	protected Iterator<ChainQuery> chainQueryIterator()
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
     * Backup to start state
     */
    protected void backupToStart()
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

    /**
     * Set initial solution
     * @param solution Solution object - usually empty but can contain initial axioms
     */
    protected void setSolution(Solution solution)
    {
        this.solution = solution;
    }

    /**
     * Returns solution
     * @return Collection of axioms referenced by name. The axioms reference the templates supplied to the query.
     */
    protected Solution getSolution() 
    {
        return solution;
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
     * Bind axiom listeners to processors. This is a late binding step 
     * for any outstanding outbound list variables. 
     */
    protected void bindAxiomListeners(Scope localScope)
    {
        if (axiomListenerMap == null)
            return;
        Set<QualifiedName> keys = axiomListenerMap.keySet();
        for (QualifiedName key: keys)
        {
            if (!key.getTemplate().isEmpty())
                continue;  // Templates are output
            AxiomSource axiomSource = localScope.findAxiomSource(key);
            if ((axiomSource == null) && !localScope.getName().equals(QueryProgram.GLOBAL_SCOPE))
                axiomSource = queryScope.getGlobalScope().findAxiomSource(key);
            // TODO - Log warning if axiom source not found
            if (axiomSource != null)
                bindAxiomSource(axiomSource, key);
        }
        // Delete the axiom listener map as binding is complete
        axiomListenerMap = null;
    }

    /**
     * Bind axiom source to listener collection identified by name
     * @param axiomSource The axiom source 
     * @param key Listener collection identity
     */
    protected void bindAxiomSource(AxiomSource axiomSource, QualifiedName key)
    {
        List<AxiomListener> axiomListenerList = axiomListenerMap.get(key);
        for (AxiomListener axiomListener: axiomListenerList)
        {
            Iterator<Axiom> iterator = axiomSource.iterator();
            if (!iterator.hasNext())
                break;
            Axiom axiom = iterator.next();
            if (axiom != null)
                axiomListener.onNextAxiom(key, axiom);
        }
    }

    /**
     * Bind axiom listeners to solution for calculator
     * @param template Calculator
     */
    protected void bindCalculatorAxiomListener(Template template)
    {
        QualifiedName qname = template.getQualifiedName();
        List<AxiomListener> axiomListenerList = null;
        if (axiomListenerMap.containsKey(qname))
            axiomListenerList = axiomListenerMap.get(qname);
        else if (template.isReplicate()) 
        {
            // TODO - check if scope meant to be cleared on template object
            //qname.clearScope();
            qname = new QualifiedTemplateName(QueryProgram.GLOBAL_SCOPE, qname.getTemplate());
            if (axiomListenerMap.containsKey(qname))
                axiomListenerList = axiomListenerMap.get(qname);
        }
        if (axiomListenerList != null)
        {
            for (AxiomListener axiomListener: axiomListenerList)
                solution.setAxiomListener(qname, axiomListener);
            axiomListenerMap.remove(qname);
        }
    }

    /**
     * Bind each axiom listener to a query or the solution depending on the referenced axiom 
     * being available in the axiom collection belonging to the chain query
     * @param template Chain template
     * @param chainQuery Chain query
     */
    protected void bindAxiomListener(Template template, LogicChainQuery chainQuery)
    {
        String key = template.getKey();
        QualifiedName qname = QualifiedName.parseGlobalName(key);
        if (axiomListenerMap.containsKey(qname))
        {
            AxiomSource axiomSource = chainQuery.getAxiomSource(key);
            List<AxiomListener> axiomListenerList = axiomListenerMap.get(qname);
            if (axiomSource != null)
            {
                for (AxiomListener axiomListener: axiomListenerList)
                    chainQuery.setAxiomListener(qname, axiomListener);
                axiomListenerMap.remove(qname);
            }
        }
        else
        {
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

    /**
     * Copy all axiom listeners in scope to this executer
     * @param scope Scope object
     */
    protected void setAxiomListeners(Scope scope)
    {
        if (scope.getAxiomListenerMap() != null)
        {   // Create a copy of the axiom listener map and remove entries as axiom listeners are bound to processors
            if (axiomListenerMap == null)
                axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
            axiomListenerMap.putAll(scope.getAxiomListenerMap());
        }
    }

    /**
     * Notify all locale listeners that the scope has changed
     * @param templateScope Notification scope
     */
    protected void onScopeChange(Scope templateScope) 
    {
        queryScope.getParserAssembler().onScopeChange(templateScope);
    }
}
