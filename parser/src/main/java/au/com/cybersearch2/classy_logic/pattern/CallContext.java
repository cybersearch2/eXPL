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
package au.com.cybersearch2.classy_logic.pattern;

import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * CallContext
 * Preserves state of template so it can be reused during query evaluation
 * @author Andrew Bowley
 * 2 Mar 2015
 */
public class CallContext 
{
    /** Names of lists which are empty at time of object construction */
    protected List<String> emptyListNames;
    /** Template used for call */
    protected Template template;
    /** Tree of operand contexts used to save and restore initial state */
    protected List<OperandContext> contextRootList;
    /** Next CallContext object in stack */
    protected CallContext next;

	/**
	 * Construct CallContext object
	 * @param template Template to be preserved
	 */
	public CallContext(Template template) 
	{
		this.template = template;
		contextRootList = new ArrayList<OperandContext>();
        OperandContext operandContext = null;
 		for (int i = 0; i < template.getTermCount(); i++)
		{
		    Term term = template.getTermByIndex(i);
		    OperandContext nextOperandContext = new OperandContext(term);
		    if (i == 0)
	            contextRootList.add(nextOperandContext);
		    else
		        operandContext.setNext(nextOperandContext);
            operandContext = nextOperandContext;
		    visit(term, operandContext);
		}
        template.reset();
	}

	/**
	 * Returns next CallContext object in the call stack
	 * @return CallContext object or null
	 */
	public CallContext getNext()
    {
        return next;
    }

	/**
	 * Set next CallContext object in the call stack
	 * @param next CallContext object
	 */
    public void setNext(CallContext next)
    {
        this.next = next;
    }

    /**
	 * Restore template initial state by clearing all operands and then assigning default values
	 */
	public void restoreContext()
	{
	    for (OperandContext  operandContext: contextRootList)
	        operandContext.restore();
	}

	/**
	 * Visit term in evaluation tree to build context tree
	 * @param term Term object
	 * @param operandContext OperandObject associated with term
	 */
    protected void visit(Term term, OperandContext operandContext)
    {
        if (!(term instanceof Operand))
            return;
        Operand operand = (Operand)term;
        Operand left = operand.getLeftOperand();
        if (left != null)
        {
            OperandContext leftOperandContext = new OperandContext(left);
            operandContext.setLeft(leftOperandContext);
            visit(left, leftOperandContext);
        }
        Operand right = operand.getRightOperand();
        if (right != null)
        {
            OperandContext rightOperandContext = new OperandContext(right);
            operandContext.setRight(rightOperandContext);
            visit(right, rightOperandContext);
        }
    }
}
