/**
    Copyright (C) 2017  www.cybersearch2.com.au

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

/**
 * OperandFinder
 * @author Andrew Bowley
 * 9Jul.,2017
 */
public class OperandFinder
{
    /** Term list */
    protected List<Operand> operandList;
    protected String name;
    private Operand foundOperand;

    /**
     * 
     */
    public OperandFinder(List<Operand> operandList, String name)
    {
        this.operandList = operandList;
        this.name = name;
    }

    /**
     * Navigate operand tree for all terms. 
     * The supplied visitor may cut the navigation short causing
     * a false result to be returned.
     * @return Operand or null if not found 
     */
    public Operand findNode()
    {
        for (Operand operandItem: operandList)
        {
            if (!visit(operandItem, 1))
                return foundOperand;
        }
        return null;
    }

    /**
     * Visit a node of the Operand tree. Recursively navigates left and right operands, if any.
     * @param operand The term being visited
     * @param depth Depth in tree. The root has depth 1.
     * @return flag set true if entire tree formed by this term is navigated. 
     */
    private boolean visit(Operand operand, int depth)
    {
        if (operand.getName().equals(name))
        {
            foundOperand = operand;
            return false;
        }
        if ((operand.getLeftOperand() != null) &&
             !visit(operand.getLeftOperand(), depth + 1))
            return false;
        if ((operand.getRightOperand() != null) &&
             !visit(operand.getRightOperand(), depth + 1))
            return false;
        return true;
    }
}
