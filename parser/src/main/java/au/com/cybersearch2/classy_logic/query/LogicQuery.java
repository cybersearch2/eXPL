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
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.SolutionFinder;
import au.com.cybersearch2.classy_logic.interfaces.SolutionHandler;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.OperandWalker;
import au.com.cybersearch2.classy_logic.pattern.SolutionPairer;
import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;

/**
 * LogicQuery
 * Performs logic operation of unification between a sequence of axioms and a template.
 * Each solution is aggregated with the overall solution being prepared by the QueryExecuter.
 * @author Andrew Bowley
 * 30 Dec 2014
 * @see QueryExecuter
 */
public class LogicQuery implements SolutionFinder
{
	/** Handler to call when a solution is found or null if last query in tail */
    protected SolutionHandler solutionHandler;
    /** Query status - start or in_prgress */
    protected QueryStatus queryStatus;
    /** Iterator over axiom sequence */
    protected Iterator<Axiom> axiomIterator;
    /** Source of axiom sequence */
    protected AxiomSource axiomSource;
    /** Axiom listener is notified of axiom sourced each iteration */
    protected List<AxiomListener> axiomListenerList;
    /** Pairs axiom terms in a Solution object with terms in a template */
    protected SolutionPairer pairer;
  
    /**
     * Construct QueryLogic object
     * @param axiomSource Source of axiom sequence
     * @param solutionHandler Handler to call when a solution is found 
     */
	public LogicQuery(AxiomSource axiomSource, SolutionHandler solutionHandler) 
	{
		this.axiomSource = axiomSource;
		this.solutionHandler = solutionHandler;
		queryStatus = QueryStatus.start;
	}

    /**
     * Construct QueryLogic object
     * @param axiomSource Source of axiom sequence
      */
	public LogicQuery(AxiomSource axiomSource) 
	{
		this(axiomSource, null);
	}

	/**
	 * Find a solution for specified template
	 * @param solution Container to aggregate results  
	 * @param template Structure to pair with axiom sequence
	 * @return Flag to indicate if another solution may be available
	 */
	@Override
	public boolean iterate(Solution solution, Template template)
	{
	    boolean emptyTemplate = false;
		if (queryStatus == QueryStatus.start)
		{   // Start from beginning of axiom sequence
			axiomIterator = axiomSource.iterator();
			if ((axiomIterator.hasNext()))
			{    
				queryStatus = QueryStatus.in_progress; 
				emptyTemplate = (template.getTermCount() == 0);
			}
			else
			   // When AxiomSource is empty, allow unification solely with solution
				return unifySolution(solution, template) &&
					    completeSolution(solution, template);
			
		}
		// Iterate through axioms to find solution
		while (axiomIterator.hasNext())
		{
			Axiom axiom = axiomIterator.next();
			if (axiomListenerList != null)
				for (AxiomListener axiomListener: axiomListenerList)
					axiomListener.onNextAxiom(axiom);
			if (emptyTemplate && template.getName().equals(axiom.getName()) && template.getKey().equals(axiom.getName()))
	        {
	            for (int i = 0; i < axiom.getTermCount(); i++)
	            {
	                Term term = axiom.getTermByIndex(i);
	                if (!term.getName().equals(Term.ANONYMOUS))
	                    template.addTerm(new Variable(new QualifiedName(term.getName(), QualifiedName.ANONYMOUS)));
	            }
	            emptyTemplate = false;
	        }
			// Unify use to not happen if keys don't match, but scope names broke this rule
			//if (!axiom.getName().equals(template.getKey()))
			//	throw new QueryExecutionException("Axiom key \"" + axiom.getName() + "\" does not match Template key \"" + template.getKey() + "\"");
			if (axiom.unifyTemplate(template, solution) &&
				completeSolution(solution, template))
				return true;
			template.backup(true);
		}
		queryStatus = QueryStatus.start;
		return false;
	}

	/**
	 * Unify template with solution.
	 * @param solution Container to aggregate results  
	 * @param template Structure to pair with axiom sequence
	 * @return Flag to indicate if the query is resolved
	 */
	protected boolean unifySolution(Solution solution, Template template)
    {
		if (solution.size() > 0)
		{
			OperandWalker walker = template.getOperandWalker();
			if (pairer == null)
				pairer = new SolutionPairer(solution, template.getQualifiedName());
			else
				pairer.setSolution(solution);
			if (walker.visitAllNodes(pairer))
			{
				// Proceed with unification term by term
				for (TermPair termPair: pairer.getPairList())
					termPair.term1.unifyTerm(termPair.term2, template.getId());
				return true;
			}
		}
		return false;
    }

	/**
	 * Complete finding solution following successful unification
	 * @param solution Container to aggregate results  
	 * @param template Structure to pair with axiom sequence
	 * @return Flag to indicate if the query is resolved
	 */
	protected boolean completeSolution(Solution solution, Template template)
	{
		try
		{
			// evaluate() may result in a short circuit exit flagged by returning false
			// isfact() flags true if each term of the template is non-empty
			if ((template.evaluate() == EvaluationStatus.COMPLETE) && template.isFact())
			{
			    String solutionKey = template.getQualifiedName().toString();
				solution.put(solutionKey, template.toAxiom());
				if ((solutionHandler == null) ||
				     solutionHandler.onSolution(solution))
					return true;
				solution.remove(solutionKey);
			}
		}
		catch (ExpressionException e)
		{   // evaluate() exceptions are thrown by Evaluator objects 
			throw new QueryExecutionException("Error evaluating: " + template.toString(), e);
		}
		return false;
	}
	
	/**
	 * Returns query status
	 * @return QueryStatus
	 */
	protected QueryStatus getQueryStatus() 
	{
		return queryStatus;
	}

	/**
	 * Set query status to "complete" to stop any further query processing
	 */
	public void setQueryStatusComplete() 
	{
		queryStatus = QueryStatus.complete;
	}

	/**
	 * Set axiom listener to receive each solution as it is produced
	 * @param axiomListener The axiom listener object
	 */
	@Override
	public void setAxiomListener(AxiomListener axiomListener) 
	{
		if (axiomListenerList == null)
			axiomListenerList = new ArrayList<AxiomListener>();
		axiomListenerList.add(axiomListener);
	}
}
