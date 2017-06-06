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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.RightOperand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.BigDecimalOperator;

/**
 * BigDecimalOperand
 * @author Andrew Bowley
 * 3 Dec 2014
 */
public class BigDecimalOperand extends ExpressionOperand<BigDecimal> implements LocaleListener, RightOperand 
{
    /** Defines operations that an Operand performs with other operands. */
    protected BigDecimalOperator operator;
    /** Optional operand for Currency type */
    protected Operand rightOperand;
    
	/**
	 * Construct named, empty BigDecimalOperand object
     * @param qname Qualified name
	 */
	public BigDecimalOperand(QualifiedName qname) 
	{
		super(qname);
		init();
	}

	/**
	 * Construct named, non-empty BigDecimalOperand object
     * @param qname Qualified name
	 * @param value BigDecimal
	 */
	public BigDecimalOperand(QualifiedName qname, BigDecimal value) 
	{
		super(qname, value);
        init();
	}

	/**
	 * Construct named BigDecimalOperand object which delegates to an expression to set value
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public BigDecimalOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
        init();
	}

	/**
	 * Override operator to customize behaviour
	 * @param operator Compatible operator
	 */
    public void setOperator(BigDecimalOperator operator)
    {
        this.operator = operator;
    }
    
    /**
     * Evaluate value if expression exists
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        if (rightOperand != null)
            rightOperand.evaluate(id);
        EvaluationStatus status = super.evaluate(id);
        if ((status == EvaluationStatus.COMPLETE) && !isEmpty())
            // Perform conversion to BigDecimal, if required
            setValue(operator.convertObject(value, getValueClass()));
        return status;
    }

    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int id)
    {
        if (rightOperand != null)
            rightOperand.backup(id);
        return super.backup(id);
    }
    
	/**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
	 */
	@Override
	public void assign(Term term) 
	{
		setValue(operator.convertObject(term.getValue(), term.getValueClass()));
		//id = term.getId();
	}

    /**
     * Returns null     
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return rightOperand;
    }
    
    @Override
    public void onScopeChange(Scope scope)
    {
        operator.onScopeChange(scope);
    }

    @Override
    public Operator getOperator()
    {
        return operator;
    }

    @Override
    public void setRightOperand(Operand rightOperand)
    {
        this.rightOperand = rightOperand;
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
     */
    @Override
    public String toString()
    {
        if (operator.getTrait().getOperandType() == OperandType.CURRENCY)
        {
            String country = operator.getTrait().getCountry();
            if (!country.isEmpty())
                return country + " " + super.toString();
            else if (rightOperand != null)
                return rightOperand.toString() + " " + super.toString();
        }
        return super.toString();
    }

    private void init()
    {
        operator = new BigDecimalOperator();
    }


}
