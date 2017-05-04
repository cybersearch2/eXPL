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
package au.com.cybersearch2.classy_logic.operator;

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.ExpressionOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;

/**
 * ExpressionOperator
 * Base class for Operator classes which evaluate using an expression of an ExpressionOperand object
 * @see ExpressionOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public abstract class ExpressionOperator implements Operator
{
    /** Optional Parameter which evaluates value */
    protected Operand expression;

    /**
     * @return the expression
     */
    public Operand getExpression()
    {
        return expression;
    }

    /**
     * @param expression the expression to set
     */
    public void setExpression(Operand expression)
    {
        this.expression = expression;
    }

    /**
     * Returns OperatorEnum values for which this Term is a valid String operand
     * @return OperatorEnum[]
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getStringOperandOps()
     */
     @Override
     public OperatorEnum[] getStringOperandOps()
     {
         if (expression != null)
             return expression.getOperator().getStringOperandOps();
         return EMPTY_OPERAND_OPS;
     }
}
