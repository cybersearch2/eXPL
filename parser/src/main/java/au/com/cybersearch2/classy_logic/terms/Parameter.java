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
package au.com.cybersearch2.classy_logic.terms;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * Parameter is a simple Term. It may be annonymous or named. It may be empty or assigned a value, which may be null.
 * Sub classes control Parameter construction and the value Java type. 
 * @author Andrew Bowley
 * @since 28/09/2010
 */
public class Parameter implements Term
{
	private static final String INVALID_NAME_MESSAGE = "Parameter \"name\" is null";
	/** Value of Parameter is null by default */
    protected Object value;
    /** Name of parameter is BLANK by default, which means it's annonymous until a name is assigned */
	protected String name;
	/** The Parameter is empty until a value is assign. Beware - null is a VALID value */
	protected boolean empty;
	/** Identity - assigned when performing unification - see backup() for application */
	protected int id;

	/**
	 * Construct a non-empty named Parameter object
	 * @param name String
	 * @param value Value 
	 */
	public Parameter(String name, Object value)
	{
		if (name == null)
			throw new IllegalArgumentException(INVALID_NAME_MESSAGE);
		this.name = name;
		id = 0;
		setValue(value);
	}

    /**
	 * Construct an empty named Parameter object
	 * @param name String
	 */
	public Parameter(String name)
	{
		if (name == null)
			throw new IllegalArgumentException(INVALID_NAME_MESSAGE);
		this.name = name;
		clearValue();
	}

    /**
     * Set Parameter name. 
     * This means the Term is annoymous until a name is assigned to it.
     * @param name String
	 * @throws IllegalStateException if name has already been assigned
     */
	@Override
	public void setName(String name)
    {
		if (name == null)
			throw new IllegalArgumentException(INVALID_NAME_MESSAGE);
		if (!this.name.equals(ANONYMOUS))
			throw new IllegalStateException("Assigning name \"" + name + "\" to Term already named \"" + this.name + "\" not allowed");
		this.name = name;
    }
	
    /**
     * Returns Parameter name
     * @return String
     */
	@Override
	public String getName()
    {
    	return name;
    }

	/**
	 * Returns true if no value has been assigned to this Parameter
	 * @return boolean true if empty
	 */
	@Override
	public boolean isEmpty()
    {
    	return empty;
    }

	/**
	 * Returns Parameter value or null if not assigned
	 * @return Object
	 */
	@Override
	public Object getValue()
	{
		return value;
	}

	/**
	 * Returns id
	 * @return int
	 */
    @Override
	public int getId() 
	{
		return id;
	}

	/**
	 * Backup to intial state if given id matches id assigned on unification or given id = 0. 
	 * @param id Identity of caller. 
	 * @return boolean true if backup occurred
	 * @see #unify(Term otherParam, int id)
	 * @see #evaluate(int id)
	 */
	@Override
	public boolean backup(int id)
	{
		if ((this.id == 0) || ((id != 0) && (this.id != id)))
			return false;
		clearValue();
		return true;
	}
	
	/**
	 * Perform unification with other Term. If successful, two terms will be equivalent.
	 * Determines Term ordering and then delegates to evaluate()  
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see #evaluate(int id)
	 * @see #backup(int id)
	 */
	@Override
	public int unifyTerm(Term otherTerm, int id)
    {
		if (isEmpty() && !otherTerm.isEmpty())
		{
			return unify(otherTerm, id);
		}
		else if (!isEmpty() && otherTerm.isEmpty())
		{
			return otherTerm.unifyTerm(this, id);
		}
		return 0;
    }

	/**
	 * Returns Parameter value class or Null.class if value is null
	 * @return Class object
	 */
	@Override
    public Class<?> getValueClass()
    {
	    return value.getClass();
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (!name.isEmpty())
			return name + " = " + (empty ? "<empty>" : value.toString());
		return  value.toString();
	}

	/**
	 * Equals matches on value. If either or both Parameters have null value, equals() returns false
	 * @see java.lang.Object#equals(Object obj)
	 */
	@Override
    public boolean equals(Object obj) 
	{
		if ((obj == null) || (value.equals(null)))
			return false;
		if (obj instanceof Parameter)
		{
			Parameter other =  (Parameter)obj;
			if (other.value.equals(null))
				return false;
			return value.equals(other.value);
		}
		return false;
	}

	/**
	 * Delegate to perform actual unification with other Term. If successful, two terms will be equivalent. 
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see #backup(int id)
	 */
	public int unify(Term otherTerm, int id)
	{
		this.id = id;
		setValue(otherTerm.getValue());
		return this.id;
    }

	/**
	 * Set Parameter value
	 * GenericParameter has a protected type-specific setValue()
	 * @param value
	 */
	private void setValue(Object value)
	{
		if (value == null)
			value = new Null();
		else
		{
			this.value = value;
			this.empty = false;
		}
	}

	/**
	 * Set value to null, mark Parameter as empty and set id to 0
	 */
	public void clearValue()
	{
		empty = true;
		// Set value to avoid NPE on accidental access despite empty flag being set true
		value = new Null();
		id = 0;
	}
	
	/**
	 * Evaluate value using data gathered during unification.
	 * @param id Identity of caller, which must be provided for backup()
	 * @return EvaluationStatus
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * Update Parameter value - use for assignment operation
     * @param value Object containing new value.
	 * @see au.com.cybersearch2.classy_logic.interfaces.Term#assign(java.lang.Object)
	 */
	@Override
	public void assign(Object value) 
	{
		setValue(value);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() 
	{
		return value == null ? 0 : value.hashCode();
	}

}
