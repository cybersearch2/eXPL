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
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.DelegateType;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * Variable
 * Operand without a generic type and not assigned a value until after construction.
 * When non-empty (and the value is not null), it implements the Operand interface 
 * by delegating to an Operand matching the type of the value. If empty (or the value is null)
 * delegation is to a NullOperand instance.
 * @author Andrew Bowley
 * 11 Dec 2014
 */
public class Variable extends DelegateOperand
{
	/** Optional Parameter which evaluates value */
	protected Operand expression;

	/**
	 * Construct a Variable object
	 * @param qname Qualified name
	 */
	public Variable(QualifiedName qname) 
	{
		this(qname, qname.getName());
    }

    /**
     * Construct a Variable object with specified term name
     * @param qname Qualified name
     */
    public Variable(QualifiedName qname, String termName) 
    {
        super(qname, termName);
    }

	/**
	 * Construct a Variable object which uses an Expression operand to evaluate it's value
     * @param qname Qualified name of variable
	 * @param expression Operand to initialize this Variable upon evaluation
	 */
	public Variable(QualifiedName qname, Operand expression) 
	{
		this(qname);
		this.expression = expression;
	}

    /**
     * @return the delegateType
     */
    public DelegateType getDelegateType()
    {
        return operator.getDelegateType();
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
	 * Execute operation for expression
	 * @param id Identity of caller, which must be provided for backup()
	 * @return EvaluationStatus
	 */
	public EvaluationStatus evaluate(int id)
	{
		EvaluationStatus status = EvaluationStatus.COMPLETE;
		if (expression != null)
		{
		    if (expression.isEmpty())
		        status =  expression.evaluate(id);
		    // Only use expression value if empty or 
		    // same id used as last time value was set on this variable
			if (!expression.isEmpty() && empty || (id == this.id))
			{
				setValue(expression.getValue());
			    this.id = id;
			}
		}
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
        if (expression != null)
            expression.backup(id);
        // Only backup if forced (id = 0) or same id as used for evaluation
        if ((id == 0) || (id == this.id))
        {
            super.backup(id);
            // Set id back to zero to recreate initial empty state
            this.id = 0;
            return true;
        }
        return false;
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
     * Override toString() to report &lt;empty&gt;, null or value
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

}
