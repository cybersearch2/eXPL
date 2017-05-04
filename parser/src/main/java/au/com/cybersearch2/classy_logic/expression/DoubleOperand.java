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
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.DoubleOperator;

/**
 * DoubleOperand
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class DoubleOperand extends ExpressionOperand<Double> implements LocaleListener
{
    /** Defines operations that an Operand performs with other operands. To be set by super. */
    protected DoubleOperator operator;

	/**
	 * Construct named DoubleOperand object
     * @param qname Qualified name
	 */
	public DoubleOperand(QualifiedName qname) 
	{
		super(qname);
		init();
	}

	/**
	 * Construct named, non empty DoubleOperand object
     * @param qname Qualified name
	 * @param value Double object
	 */
	public DoubleOperand(QualifiedName qname, Double value) 
	{
		super(qname, value);
        init();
	}

	/**
	 * Construct named DoubleOperand object which delegates to an expression to set value
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public DoubleOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
	    init();
	}

	/**
	 * Complete object construction
	 */
    private void init()
    {
        operator = new DoubleOperator();
    }

    /**
     * Evaluate value if expression exists
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#evaluate(int)
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if ((status == EvaluationStatus.COMPLETE) && !isEmpty())
            // Perform conversion to Double, if required
            setValue(operator.convertObject(value, getValueClass()));
        return status;
    }

	/**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#assign(au.com.cybersearch2.classy_logic.interfaces.Term)
     */
	@Override
	public void assign(Term term) 
	{
		setValue(operator.convertObject(term.getValue(), term.getValueClass()));
	}

	/**
	 * onScopeChange
	 * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
	 */
    @Override
    public void onScopeChange(Scope scope)
    {
        operator.onScopeChange(scope);
    }

    /**
     * getOperator
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getOperator()
     */
    @Override
    public Operator getOperator()
    {
        return operator;
    }
}
