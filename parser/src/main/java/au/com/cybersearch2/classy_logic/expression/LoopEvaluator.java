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
package au.com.cybersearch2.classy_logic.expression;

import java.util.Date;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.query.Calculator;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * LoopEvaluator
 * Operand which performs evaluation a sequence of operands. 
 * A short circuit causes a return to the start (ie. expression prefixed with '?' evaluates to 'false') 
 * On each iteration, a backup clears the results of the previous evaluations. 
 * A 2 second timer operates to break an infinite loop.
 * This is a NullOperand as any result will be returned in an already declared parameter. 
 * @author Andrew Bowley
 * 23 Jan 2015
 */
public class LoopEvaluator extends BooleanOperand
{
	/** The Operand sequence to be evaluated is contained in a template */
	protected Template template;
	/** Flag whether run once or loop */
	protected boolean runOnce;

	/**
	 * Construct a LoopEvaluator object
	 * @param template Container for the Operand sequence to be evaluated
	 */
	public LoopEvaluator(Template template) 
	{
		this(template, false);
	}

	public LoopEvaluator(Template template, boolean runOnce) 
	{
		super(new QualifiedName(runOnce ? "run_once" : "loop", template.getQualifiedName()));
		this.template = template;
		this.runOnce = runOnce; 
	}

	/**
	 * Evaluate loop
	 * @param id Not used as evaluation and backup are local only
	 * @return Flag set true
	 */
	@Override
	public EvaluationStatus evaluate(int id)
	{
	    this.id = id;
		long start = new Date().getTime();
		long timeoutMsecs = Calculator.CALCULATION_TIMEOUT_SECS * 1000;
		int count = 0;
		while (true)
		{
			EvaluationStatus evaluationStatus = template.evaluate();
			if (runOnce || (evaluationStatus == EvaluationStatus.SHORT_CIRCUIT))
			{
				setValue(Boolean.TRUE); // Value indicates successful completion
				return EvaluationStatus.COMPLETE;
			}
			// Only backup local changes
			template.backup(true);
			if (++count == 10)
			{
				long now = new Date().getTime();
				if (now - start >= timeoutMsecs)
				{
					setValue(Boolean.FALSE); // Value indicates timeout
					throw new QueryExecutionException("Calculation aborted after " + Calculator.CALCULATION_TIMEOUT_SECS + " seconds");
				}
				count = 0;
			}
		}
	}

	/**
	 * Backup to intial state if given id matches id assigned on unification or given id = 0. 
	 * @param id Not used. 
	 * @return Flag set true
	 */
	@Override
	public boolean backup(int id)
	{   
		super.backup(id);
		// Changes managed locally
		return template.backup(true);
	}

	/**
	 * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
	 */
	@Override
	public String toString() 
	{
		return template.toString();
	}

	
}
