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
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
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
		init();
	}

    /**
     * Construct a literal LongOperand object
     * @param qname Qualified name
     * @param value Long object
     */
    public IntegerOperand(QualifiedName qname, Integer value) 
    {
        super(qname, value.longValue());
        init();
    }

	/**
	 * Construct a literal LongOperand object
     * @param qname Qualified name
	 * @param value Long object
	 */
	public IntegerOperand(QualifiedName qname, Long value) 
	{
		super(qname, value);
        init();
	}

	/**
	 * Long Expression
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public IntegerOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
        init();
	}

    /**
     * Evaluate value if expression exists
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if ((status == EvaluationStatus.COMPLETE) && !isEmpty())
            // Perform conversion to Long, if required
            setValue(operator.convertObject(value, getValueClass()));
        return status;
    }

    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
	{
		setValue(operator.convertObject(parameter.getValue(), parameter.getValueClass()));
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

    private void init()
    {
        operator = new IntegerOperator();
    }

}
