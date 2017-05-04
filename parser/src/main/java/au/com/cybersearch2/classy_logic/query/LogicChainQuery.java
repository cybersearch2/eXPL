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
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * LogicChainQuery
 * Chain query to perform logic operation
 * @author Andrew Bowley
 * 12 Jan 2015
 */
public class LogicChainQuery extends ChainQuery
{
	/** List of templates to unify and evaluate */
	protected List<Template> templateList;
	/** A set of AxiomSource objects referenced by name */
 	protected AxiomCollection axiomCollection;
	/** Optional axiom listener to receive each solution as it is produced */
	protected Map<QualifiedName, List<AxiomListener>> axiomListenerMap;

	/**
	 * Create LogicChainQuery object
	 * @param axiomCollection A set of AxiomSource objects referenced by name
	 * @param templateList List of templates to unify and evaluate
	 */
 	public LogicChainQuery(AxiomCollection axiomCollection,	List<Template> templateList) 
    {
		this.templateList = templateList;
		this.axiomCollection = axiomCollection;
	}

	/**
 	 * Execute query and if not tail, chain to next.
 	 * Sub classes override this method and call it upon completion to handle the chaining
 	 * @param solution The object which stores the query results
     * @param templateChain Template chain to manage same query repeated in different scopes
	 * @return EvaluationStatus enum: SHORT_CIRCUIT, SKIP or COMPLETE
	 */
	@Override
	public EvaluationStatus executeQuery(Solution solution, Deque<Template> templateChain)
	{
		for (Template template: templateList)
		{
			String key = template.getKey();
			AxiomSource axiomSource = axiomCollection.getAxiomSource(key);
			if  (axiomSource == null)
				axiomSource = new EmptyAxiomSource();
			LogicQuery query = new LogicQuery(axiomSource);
			if ((axiomListenerMap != null) && axiomListenerMap.containsKey(template.getKey()))
				for (AxiomListener axiomListener: axiomListenerMap.get(template.getKey()))
					query.setAxiomListener(axiomListener);
			if (!query.iterate(solution, template))
				return EvaluationStatus.SHORT_CIRCUIT;
		}
		return super.executeQuery(solution, templateChain);
 	}

	/**
	 * Returns query as String. Shows empty terms with "?",
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return QueryExecuter.toString(templateList);
	}

 	/**
	 * Backup to state before previous unification
	 */
	@Override
	protected void backupToStart() 
	{
		for (Template template: templateList)
			template.backup(false);
	}

	/**
	 * Force reset to initial state
	 */
	@Override
	protected void reset() 
	{
		for (Template template: templateList)
			template.reset();
	}

	/**
	 * Set axiom listener to receive each solution as it is produced
	 * @param qname Reference to axiom by qualified name
	 * @param axiomListener The axiom listener object
	 */
	@Override
	void setAxiomListener(QualifiedName qname, AxiomListener axiomListener) 
	{
		List<AxiomListener> axiomListenerList = null;
		if (axiomListenerMap == null)
			axiomListenerMap = new HashMap<QualifiedName, List<AxiomListener>>();
		else
			axiomListenerList = axiomListenerMap.get(qname);
		if (axiomListenerList == null)
		{
			axiomListenerList = new ArrayList<AxiomListener>();
			axiomListenerMap.put(qname, axiomListenerList);
		}
		axiomListenerList.add(axiomListener);
	}
	
}
