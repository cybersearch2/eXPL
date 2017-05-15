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

import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;

/**
 * OperandWalker
 * For each term in a list, follows left and right operand links to visit all descendent operands.
 * Calls OperandVisitor next() method on each visited operand.
 * Applicable for unification or collection of terms into a map.
 * @author Andrew Bowley
 * 16 Dec 2014
 */
public class OperandWalker 
{
	/** Single operand */
    protected Operand operand;
    /** Term list */
    protected List<Operand> operandList;

    /**
     * Construct OperandWalker object to navigate a list of terms.
     * Note Term interface applied to items for compatibility with Template super class Structure, which contains Template terms.
     * @param operandList List of terms to navigate
     */
	public OperandWalker(List<Operand> operandList) 
	{
		this.operandList = operandList;
	}

    /**
     * Construct OperandWalker object
     * @param operand Single term to navigate
     */
	public OperandWalker(Operand operand) 
	{
		this.operand = operand;
	}

	/**
	 * Navigate operand tree for all terms. 
     * The supplied visitor may cut the navigation short causing
     * a false result to be returned.
	 * @param visitor Implementation of OperandVisitor interface 
	 * @return flag set true if entire tree navigated. 
	 */
	public boolean visitAllNodes(OperandVisitor visitor)
	{
		if (operand != null)
			return visit(operand, visitor, 1);
		for (Operand operandItem: operandList)
		{
			if (!visit(operandItem, visitor, 1))
				return false;
		}
		return true;
	}

	/**
	 * Visit a node of the Operand tree. Recursively navigates left and right operands, if any.
	 * @param operand The term being visited
	 * @param visitor Object implementing OperandVisitor interface
	 * @param depth Depth in tree. The root has depth 1.
	 * @return flag set true if entire tree formed by this term is navigated. 
	 */
	public boolean visit(Operand operand, OperandVisitor visitor, int depth)
	{
		if (!visitor.next(operand, depth))
			return false;
		if ((operand.getLeftOperand() != null) &&
		     !visit(operand.getLeftOperand(), visitor, depth + 1))
			return false;
		if ((operand.getRightOperand() != null) &&
			 !visit(operand.getRightOperand(), visitor, depth + 1))
            return false;
		return true;
	}

}
