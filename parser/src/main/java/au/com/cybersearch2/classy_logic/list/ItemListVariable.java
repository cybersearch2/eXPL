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
package au.com.cybersearch2.classy_logic.list;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.GenericParameter;

/**
 * ItemListVariable
 * Operand which provides access to a backing ItemList. 
 * The generic type T is same as ItemList item type.
 * The list index may be fixed or evaluated. The value may also be fixed or evaluated.
 * @author Andrew Bowley
 * 15 Jan 2015
 * @see au.com.cybersearch2.classy_logic.list.ArrayItemList
 * @see au.com.cybersearch2.classy_logic.list.AxiomTermList
 */
public class ItemListVariable<T> extends GenericParameter<T> implements Operand 
{
	/** The backing operand list */
    protected ItemList<?> itemList;
    /** Proxy to provide Operand evaluation */
	protected Operand proxy;
	/** Operand to evaluate index. Will be null if index is fixed. */
	protected Operand indexExpression;
	/** Curent index value. Will be constant if indexExpression is null.  */
	protected int index;

	/**
	 * Construct a fixed index ItemListVariable object
	 * @param itemList The backing operand list
	 * @param proxy Proxy to provide Operand evaluation
	 * @param index The index value to select the list item
	 * @param suffix To append to name
	 */
	public ItemListVariable(ItemList<?> itemList, Operand proxy, int index, String suffix) 
	{   // Use convention list name appended with '.' + index
		super(itemList.getName() + "." + suffix);
		this.itemList = itemList;
        this.proxy = proxy;
        this.index = index;
        if (itemList.hasItem(index))
			onIndexSet(index);
	}

	/**
	 * Construct an evaluated index ItemListVariable object
	 * @param itemList The backing operand list
	 * @param proxy Proxy to provide Operand evaluation
	 * @param indexExpression Operand to evaluate index
	 * @param suffix To append to name
	 */
	public ItemListVariable(ItemList<?> itemList, Operand proxy, Operand indexExpression, String suffix) 
	{
		super(itemList.getName() + "." + suffix);
		this.itemList = itemList;
        this.proxy = proxy;
        this.indexExpression = indexExpression;
        index = -1; // Set index to invalid value to avoid accidental uninitialised list access
	}

	/**
	 * Assign a value and set the delegate
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#assign(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void assign(Object value2) 
	{
        setValue((T)value2);
	    itemList.assignItem(index, value2);
	}

	/**
	 * Evaluate index if expression provided
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id)
	{
		if (indexExpression != null)
		{   // Evaluate index. The resulting value must be a sub class of Number to be usable as an index.
			indexExpression.evaluate(id);
			if (indexExpression.isEmpty())
				throw new ExpressionException("Index for list \"" +itemList.getName() + "\" is empty" );
			if (!(indexExpression.getValue() instanceof Number))
				throw new ExpressionException("\"" +itemList.getName() + "[" + indexExpression.getValue().toString() + "]\" is not a valid value" );
			index = ((Number)(indexExpression.getValue())).intValue();
			onIndexSet(index);
			this.id = id;
		}
		else if (empty)
		{
			onIndexSet(index);
			this.id = id;
		}
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * Handle index evaluation event. Set value to list item selected by index.
	 * @param index int
	 */
	protected void onIndexSet(int index)
	{
		setValue(index);
	}

	/**
	 * Set the value to list item selected by index
	 * @param index int
	 */
	@SuppressWarnings("unchecked")
	protected void setValue(int index)
	{   // Index should be valid, but check for safety
		if (itemList.hasItem(index))
		{
			T item = (T)itemList.getItem(index);
			setValue((item));
		}
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return proxy.getRightOperandOps();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return proxy.getLeftOperandOps();
	}

 	/**
 	 * Returns OperatorEnum values for which this Term is a valid String operand
 	 * @return OperatorEnum[]
 	 */
	 @Override
     public OperatorEnum[] getStringOperandOps()
     {
	     return proxy.getStringOperandOps();
     }

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
		return proxy.numberEvaluation(operatorEnum2, rightTerm);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
			Term rightTerm) 
	{
		return proxy.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
			Term rightTerm) 
	{
		return proxy.booleanEvaluation(leftTerm, operatorEnum2, rightTerm);
	}

	/**
	 * Returns value
	 * @return Object of generic type T
	 */
    @Override
    public T getValue()
    {
		if (((indexExpression != null) && indexExpression.isEmpty()) || !itemList.hasItem(index))
			return null; // index is not valid, so cannot reference list item
		// Refresh from list item in case it has changed from last update
		return getItemValue();
    }

	@SuppressWarnings("unchecked")
	protected T getItemValue()
	{
		T oldValue = super.getValue();
		T itemValue = (T)itemList.getItem(index);
		if (!itemValue.equals(oldValue))
			setValue(itemValue);

	    return itemValue;
	}
	
	/**
	 * Perform unification with other Term. If successful, two terms will be equivalent.
	 * Determines Term ordering and then delegates to evaluate()  
	 * @param otherTerm Term with which to unify
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Identity passed in param "id" or zero if unification failed
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int id)
	 */
	@Override
	public int unifyTerm(Term otherTerm, int id)
    {
		if (indexExpression != null)
	    // Unification not possible as list item is not selected until evaluation
			return 0;
		int result = super.unifyTerm(otherTerm, id);
		if (result == id)
		    itemList.assignItem(index, value);
		return result;
    }

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int)
	 */
	@Override
	public boolean backup(int id) 
	{   // Do not backup list item as unification still works regardless of whether it is empty or not.
		// Setting the list item to null, which is the only backup option, also risks NPE.
		// Backup index expression too as it always evaluates index
		if (indexExpression != null)
			indexExpression.backup(id);
		return super.backup(id);
	}

	/**
	 * Returns index expression Operand to an operand visitor
	 * @return Operand object or null if expression not set
	 */
	@Override
	public Operand getLeftOperand() 
	{
		return indexExpression;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
	 */
	@Override
	public Operand getRightOperand() 
	{
		return null;
	}

}
