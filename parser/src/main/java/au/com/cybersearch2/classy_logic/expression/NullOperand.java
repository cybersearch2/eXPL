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

import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.operator.NullOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * NullOperand
 * Represents null value. Can be assigned and compared for equality.
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class NullOperand extends ExpressionOperand<Object>
{
    /** Defines operations that an Operand performs with other operands. */
    protected NullOperator operator;
    
    /**
     * Construct anonymous NullOperand object
     */
	public NullOperand()
	{
		super(QualifiedName.ANONYMOUS, new Null());
		init();
	}

    /**
     * Construct named NullOperand object
     * @param qname Qualified name
	 */
	public NullOperand(QualifiedName qname)
	{
		super(qname, new Null());
        init();
	}

    /**
     * Construct named NullOperand object with Null substitute such as Unknown
     * @param qname Qualified name
     */
    public NullOperand(QualifiedName qname, Object substitute)
    {
        super(qname, substitute);
        init();
    }

    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
	{
	}

    @Override
    public Operator getOperator()
    {
        return operator;
    }

    private void init()
    {
        operator = new NullOperator();
    }

}
