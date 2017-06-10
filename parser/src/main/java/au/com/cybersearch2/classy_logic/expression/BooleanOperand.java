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

import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.operator.BooleanOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * BooleanVariable
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class BooleanOperand extends ExpressionOperand<Boolean>
{
    /** Defines operations that an Operand performs with other operands. */
    protected BooleanOperator operator;

    /**
	 * Boolean Variable
     * @param qname Qualified name
	 */
	public BooleanOperand(QualifiedName qname) 
	{
		super(qname);
		init();
	}

	/**
	 * Boolean Literal
     * @param qname Qualified name
	 * @param value
	 */
	public BooleanOperand(QualifiedName qname, Boolean value) 
	{
		super(qname, value);
        init();
	}

	/**
	 * Boolean Expression
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public BooleanOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
        init();
	}

	private void init()
    {
	    operator = new BooleanOperator();
	}

    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
	{
		setValue((Boolean)parameter.getValue());
	}

    /**
     * Convert value to BigDecimal, if not already of this type
     * @param object Value to convert
     * @return BigDecimal object
     */
    protected BigDecimal convertObject(Object object)
    {
            if (object instanceof BigDecimal)
                return (BigDecimal)(object);
            else
                return new BigDecimal(object.toString());
    }

    @Override
    public Operator getOperator()
    {
        return operator;
    }

}
