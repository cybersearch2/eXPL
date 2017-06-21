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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ExpressionOperand - Named typed Parameter, with optional expression to assign a value
 * @author Andrew Bowley
 * @since 28/09/2010
 */
public abstract class ExpressionOperand<T> extends Operand
{
	/** Optional Parameter which evaluates value */
	protected Operand expression;
    /** Qualified name of operand */
    protected QualifiedName qname;
 
    /**
	 * Construct a ExpressionOperand object using given name 
     * @param qname Qualified name
	 * @throws IllegalArgumentException if name is empty
	 */
	protected ExpressionOperand(QualifiedName qname)
	{
		super(qname.getName());
		if (name.isEmpty())
			throw new IllegalArgumentException("Param \"name\" is empty");
		this.qname = qname;
	}

	/**
	 * Construct a non-empty named Variable object, callable only from sub class.
     * @param qname Qualified name
	 * @param value Object of generic type T 
	 */
	protected ExpressionOperand(QualifiedName qname, T value) 
	{
		super(qname.toString(), value);
        this.qname = qname;
	}

	/**
	 * Construct a non-empty named Variable object
     * @param qname Qualified name
	 * @param expression Operand which evaluates value 
	 */
	protected ExpressionOperand(QualifiedName qname, Operand expression) 
	{
		this(qname, expression, qname.getName());
	}

    /**
     * Construct a non-empty named Variable object
     * @param qname Qualified name
     * @param expression Operand which evaluates value 
     * @param name Term name - should be qname.name or empty string for later name change
     */
    protected ExpressionOperand(QualifiedName qname, Operand expression, String name) 
    {
        super(name);
        this.qname = qname;
        this.expression = expression;
    }

    /**
     * Returns qualified name
     * @return QualifiedName object
     */
	@Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

	/**
	 * Returns flag set true if has expression Operand
	 * @return boolean
	 */
	public boolean hasExpression()
	{
	    return expression != null;
	}

    /**
     * Set value, mark Term as not empty
     * @param value Object. If null a Null object will be set and empty status unchanged
     */
	@SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value)
    {
        setTypeValue((T) value);
    }

	   /**
     * Returns value
     * @return Object of generic type T
     */
    @SuppressWarnings("unchecked")
    @Override
    public T getValue()
    {
        return (T) value;
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
		if (expression != null)
			expression.backup(id);
		return super.backup(id);
	}
	
	/**
	 * Execute operation for expression
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	public EvaluationStatus evaluate(int id)
	{
		EvaluationStatus status = EvaluationStatus.COMPLETE;
		if ((expression != null) && empty)
		{
            if (!expression.isEmpty() && (this.id == 0))
                id = 0;
			status = expression.evaluate(id);
			if (!expression.isEmpty())
			{
				this.value = expression.getValue();
			    this.empty = false;
			    this.id = id;
			}
		}
		return status;
	}
	
	/**
	 * Override toString() to incorporate expression
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
	 */
	@Override
	public String toString()
	{
		if (empty)
		{
			if (expression != null)
				return (!name.isEmpty() ? name + "=" : "") + (expression.toString());
			return name;
		}
		String valueText = ( value == null ? "null" : value.toString());
		return (!name.isEmpty() ? name + "=" : "") + valueText;
	}

	/**
	 * Returns expression Operand to an operand visitor
	 * @return Operand object or null if expression not set
	 */
	@Override
	public Operand getLeftOperand() 
	{
		return expression;
	}

	/**
	 * Returns null		
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
	 */
	@Override
	public Operand getRightOperand() 
	{
		return null;
	}

    /**
     * Set value type safe
     * @param value Object of generic type T
     */
    protected void setTypeValue(T value)
    {
        if (value == null)
            this.value = new Null();
        else
            this.value = value;
        this.empty = false;
    }

}
