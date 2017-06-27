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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.operator.IntegerOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * LongOperand
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class IntegerOperand extends ExpressionOperand<Long> implements LocaleListener
{
    /** Defines operations that an Operand performs with other operands. */
    protected IntegerOperator operator;

	/**
	 * Construct a variable LongOperand object
     * @param qname Qualified name
	 */
	public IntegerOperand(QualifiedName qname) 
	{
		super(qname);
        operator = new IntegerOperator();
	}

    /**
     * Construct a literal LongOperand object
     * @param qname Qualified name
     * @param value Long object
     */
    public IntegerOperand(QualifiedName qname, Integer value) 
    {
        this(qname);
        super.setValue(value.longValue());
    }

	/**
	 * Construct a literal LongOperand object
     * @param qname Qualified name
	 * @param value Long object
	 */
	public IntegerOperand(QualifiedName qname, Long value) 
	{
		this(qname);
        super.setValue(value);
	}

	/**
	 * Long Expression
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public IntegerOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
        operator = new IntegerOperator();
	}

    /**
     * Set value, mark Term as not empty
     * @param value Object. If null a Null object will be set and empty status unchanged
     */
    @Override
    public void setValue(Object value)
    {
        setTypeValue(operator.convertObject(value, value.getClass()));
    }

    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
	{
		setValue(parameter.getValue());
	}

    @Override
    public void onScopeChange(Scope scope)
    {
        operator.getTrait().setLocale(scope.getLocale());
    }

    @Override
    public Operator getOperator()
    {
        return operator;
    }
}
