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
import java.util.List;

import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.Choice;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * CalculateChainQuery
 * Chain query to perform calculation
 * @author Andrew Bowley
 * 12 Jan 2015
 */
public class CalculateChainQuery extends ChainQuery 
{
	/** Optional axiom to initialize Calculator */
	protected Axiom axiom;
	/**Template to unify and evaluate */
	protected Template template;
	/** Optional axiom listener to receive each solution as it is produced */
    protected List<AxiomListener> axiomListenerList;
    /** Choice set if template.isChoice() returns true */
    protected Choice choice;
    protected Runnable scopeNotifier;

	/**
	 * Create a CalculateChainQuery object
	 * @param template Template to unify and evaluate
	 */
	public CalculateChainQuery(Axiom axiom, Template template, Runnable scopeNotifier) 
	{
		this.axiom = axiom;
		this.template = template;
		this.scopeNotifier = scopeNotifier;
	}

	/**
	 * Set Choice
	 * @param choice the choice to set
	 */
	public void setChoice(Choice choice) 
	{
		this.choice = choice;
	}

	/**
 	 * Execute query and if not tail, chain to next.
 	 * Sub classes override this method and call it upon completion to handle the chaining
 	 * @param solution The object which stores the query results
 	 * @param templateChain Template chain to manage same query repeated in different scopes
	 * @return EvaluationStatus enum: SHORT_CIRCUIT, SKIP or COMPLETE
	 */
	@Override
	public EvaluationStatus executeQuery(Solution solution, Deque<Template> templateChain, ExecutionContext context)
	{
	    solution.remove(template.getQualifiedName().toString());
	    if (scopeNotifier != null)
	        scopeNotifier.run();
		Calculator calculator = new Calculator();
		if (axiomListenerList != null)
			for (AxiomListener axiomListener: axiomListenerList)
			    calculator.setAxiomListener(axiomListener);
	    if (template.isChoice())
	    	calculator.setChoice(choice);
        if (next != null)
        {
    	    // A chain is used to detect if a template with same name as head template encountered.
    	    // This may happen for same query repeated in different scopes.
    	    if (templateChain.isEmpty())
    	    {
                templateChain.push(template);
    	    }
    	    else
    	    {
    	        String key = template.getName();
    	        Template head = templateChain.peekLast();
    	        if (head.getName().equals(key))
    	        {   // New query, so reset template chain
    	            while ((head = templateChain.pollLast()) != null)
    	                head.reset();
    	        }
                templateChain.push(template);
    	    }
        }
        else
            templateChain.clear();
		if (axiom == null)
			calculator.iterate(solution, template, context);
		else 
		{
		    Axiom seedAxiom = axiom;
		    if (axiom.getTermCount() == 0)
    		{
    		    // Placeholder axiom to be populated from solution
    		    seedAxiom = solution.getAxiom(axiom.getName());
    		    if (seedAxiom == null)
    		        throw new QueryExecutionException("Calculator \"" + template.getName() + "\" cannot find axiom \"" + axiom.getName() + "\"");
    		}
    		calculator.iterate(seedAxiom, solution, template, context);
		}
		return super.executeQuery(solution, templateChain, context);
 	}

 	/**
	 * Backup to state before previous unification
	 */
	@Override
	protected void backupToStart() 
	{
		template.backup(false);
	}

    @Override
    protected void backup()
    {
        template.backup(false);
    } 

	/**
	 * Force reset to initial state
	 */
	@Override
	protected void reset() 
	{
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
		if (axiomListenerList == null)
			axiomListenerList = new ArrayList<AxiomListener>();
		axiomListenerList.add(axiomListener);
	}

}
