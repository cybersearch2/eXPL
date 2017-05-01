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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.RightOperand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.terms.GenericParameter;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

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
public class ItemListVariable<T> extends GenericParameter<T> implements Operand, RightOperand 
{
    static Trait LIST_TRAIT;
    
    static
    {
        // The de-referenced list value may be either an axiom or a term
        LIST_TRAIT = new DefaultTrait(OperandType.UNKNOWN);
    }
    
    /** Qualified name of operand */
    protected QualifiedName qname;
	/** The backing operand list */
    protected ItemList<?> itemList;
    /** Proxy to provide Operand evaluation */
	protected Operator proxy;
	/** Operand to evaluate index. Will be null if index is fixed. */
	protected Operand indexExpression;
	/** Curent index value. Will be constant if indexExpression is null.  */
	protected int index;
    /** Flag set true if operand not visible in solution */
    protected boolean isPrivate;
    /** Optional operand for Currency type */
    protected Operand rightOperand;

	/**
	 * Construct a fixed index ItemListVariable object
	 * @param itemList The backing operand list
	 * @param delegateType Operator delegate type
	 * @param index The index value to select the list item
	 * @param suffix To append to name
	 */
	public ItemListVariable(ItemList<?> itemList, Operator operator, int index, String suffix) 
	{   // Use convention list name appended with '_' + suffix, where suffix depends on type of index
		super(getVariableName(itemList.getName(), suffix));
		this.itemList = itemList;
        this.proxy = operator;
        this.index = index;
        this.qname = getVariableName(itemList, suffix);
        if (itemList.hasItem(index))
			onIndexSet(index);
	}

    /**
	 * Construct an evaluated index ItemListVariable object
	 * @param itemList The backing operand list
     * @param delegateType Operator delegate type
	 * @param indexExpression Operand to evaluate index
	 * @param suffix To append to name
	 */
	public ItemListVariable(ItemList<?> itemList, Operator operator, Operand indexExpression, String suffix) 
	{
		super(getVariableName(itemList.getName(), suffix));
		this.itemList = itemList;
        this.proxy = operator;
        this.indexExpression = indexExpression;
        this.qname = getVariableName(itemList, suffix);
        index = -1; // Set index to invalid value to avoid accidental uninitialised list access
	}

    /**
     * Returns qualified name of this operamd
     * @return QualifiedName object
     */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }
	/**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void assign(Term term) 
	{
        setValue((T)term.getValue());
        id = term.getId();
	    itemList.assignItem(index, term.getValue());
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
			index = -1;
			if (indexExpression.getValueClass() == String.class)
			    index = getIndexForName(indexExpression.getValue().toString());
			else if (indexExpression.getValue() instanceof Number)
	            index = ((Number)(indexExpression.getValue())).intValue();
			if (index == -1)    
				throw new ExpressionException("\"" +itemList.getName() + "[" + indexExpression.getValue().toString() + "]\" is not a valid value" );
			onIndexSet(index);
			this.id = id;
		}
		else if (empty && itemList.hasItem(index))

		{
			onIndexSet(index);
			this.id = id;
		}
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * Returns index for item referenced by name. Default return value is -1 for no match. 
	 * @param itemName
	 * @return Index of item or -1 for no match or feature not supported
	 */
	protected int getIndexForName(String itemName)
    {
        return -1;
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
		return rightOperand;
	}

    @Override
    public void setRightOperand(Operand rightOperand)
    {
        this.rightOperand = rightOperand;
    }
    
    /**
     * Set this operand private - not visible in solution
     * @param isPrivate Flag set true if operand not visible in solution
     */
    @Override
    public void setPrivate(boolean isPrivate)
    {
        this.isPrivate = isPrivate;
    }
    
    /**
     * Returns flag set true if this operand is private
     * @return
     */
    @Override
    public boolean isPrivate()
    {
        return isPrivate;
    }
    
    @Override
    public Operator getOperator()
    {
        return proxy;
    }

	/**
	 * Returns variable name given list name and suffix
	 * @param listName
	 * @param suffix
	 * @return String
	 */
    protected static String getVariableName(String listName, String suffix)
    {
        return listName + "_" + suffix;
    }

    /**
     * Returns qualified variable name given item list and suffix
     * @param itemList2 The item list
     * @param suffix
     * @return QualifiedName object
     */
    public static QualifiedName getVariableName(ItemList<?> itemList2, String suffix)
    {
        return new QualifiedName(getVariableName(itemList2.getName(), suffix), itemList2.getQualifiedName());
    }

}
