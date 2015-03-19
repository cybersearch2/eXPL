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
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.terms.GenericParameter;

/**
 * ExpressionParameter - Named typed Parameter, with optional expression to assign a value
 * @author Andrew Bowley
 * @since 28/09/2010
 */
public abstract class ExpressionParameter<T> extends GenericParameter<T> implements Operand
{
	/** Optional Parameter which evaluates value */
	protected Operand expression;
	
	/**
	 * Construct a ExpressionParameter object using given name 
	 * @param name String
	 * @throws IllegalArgumentException if name is empty
	 */
	protected ExpressionParameter(String name)
	{
		super(name);
		if (name.isEmpty())
			throw new IllegalArgumentException("Param \"name\" is empty");
	}

	/**
	 * Construct a non-empty named Variable object, callable only from sub class.
	 * @param name String
	 * @param value Object of generic type T 
	 */
	protected ExpressionParameter(String name, T value) 
	{
		super(name, value);
	}

	/**
	 * Construct a non-empty named Variable object
	 * @param name String
	 * @param expression Operand which evaluates value 
	 */
	protected ExpressionParameter(String name, Operand expression) 
	{
		super(name);
		this.expression = expression;
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
	 * Override toString() to report &lt;empty&gt;, null or value
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
	 */
	@Override
	public String toString()
	{
		if (empty)
		{
			if (expression != null)
				return (!name.isEmpty() ? name + " = " : "") + (expression.toString());
			return name;
		}
		String valueText = ( value == null ? "null" : value.toString());
		return (!name.isEmpty() ? name + " = " : "") + valueText;
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
 	 * Returns OperatorEnum values for which this Term is a valid String operand
 	 * @return OperatorEnum[]
 	 */
	 @Override
     public OperatorEnum[] getStringOperandOps()
     {
	     return Operand.EMPTY_OPERAND_OPS;
     }
}
