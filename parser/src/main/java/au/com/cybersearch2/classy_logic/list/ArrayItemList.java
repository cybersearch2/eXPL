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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.RightOperand;
import au.com.cybersearch2.classy_logic.operator.DelegateOperator;
import au.com.cybersearch2.classy_logic.operator.DelegateType;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;

/**
 * ArrayItemList
 * List implementation. Generic types are String, Integer, Double, BigDecimal and Boolean
 * A single Operator object is shared by dependent ItemListVariable objects which take their values from the list.
 * This object resides in an OperandMap never directly interacts with other operands.
 * @author Andrew Bowley
 * 15 Jan 2015
 */
public class ArrayItemList<T> implements ItemList<T>, LocaleListener, RightOperand 
{
    /** The list items */
    protected ArrayList<Object> valueList;
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;
    /** Qualified name */
    protected QualifiedName qname;
    /** Defines operations that an Operand performs with other operands. */
    Operator operator;
    /** Default object defines operations based on item class. */
    DelegateOperator delegateOperator;
    /** Operator DelegateType for type of item */
    protected DelegateType delegateType;
    /** List class type */
    protected Class<T> clazz;
    /** Optional operand for Currency type */
    protected Operand rightOperand;

    /**
     * Construct a ListOperand object
     * @param clazz Class of list items 
     * @param qname Qualified name 
     */
	public ArrayItemList(Class<T> clazz, QualifiedName qname) 
	{
	    this.clazz = clazz;
		this.qname = qname;
		valueList = new ArrayList<Object>();
		delegateOperator = new DelegateOperator();
		delegateOperator.setDelegate(clazz);
		operator = delegateOperator;
		delegateType = delegateOperator.getDelegateType();
		if (((delegateType == DelegateType.ASSIGN_ONLY) &&
		    (delegateOperator.getDelegateTypeForClass(clazz) == null)) ||
		    (delegateType == DelegateType.NULL))
		    throw new ExpressionException("Class \"" + clazz.getSimpleName() + "\" not a valid list type");
	}

	public void setOperator(Operator operator)
	{
	    this.operator = operator;
	}
	
	/**
	 * Returns number of items in array
	 * @return int
	 */
	@Override
	public int getLength()
	{
		return valueList.size();
	}

	/**
	 * getQualifiedName
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getQualifiedName()
	 */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getName()
	 */
	@Override
	public String getName() 
	{
		return qname.getName();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#isEmpty()
	 */
	@Override
	public boolean isEmpty() 
	{
		return valueList.isEmpty();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#assignItem(int, java.lang.Object)
	 */
	@Override
	public void assignItem(int index, Object value) 
	{
	    DelegateType valueDelegationType = delegateOperator.getDelegateTypeForClass(value.getClass());
		if (valueDelegationType != delegateType)
			throw new ExpressionException("Cannot assign type " + value.getClass().getName() + " to List " + getName());
		if (index < valueList.size())
			valueList.set(index, value);
		else
		{
			valueList.ensureCapacity(index + 1);
			for (int i = valueList.size(); i < index; i++)
				valueList.add(null);
			valueList.add(index, value);
	        if (sourceItem != null)
	            sourceItem.setInformation(toString());
		}
	}

    @Override
    public void onScopeChange(Scope scope)
    {
        operator.getTrait().setLocale(scope.getLocale());
    }

	/**
	 * newVariableInstance
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(int, java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemListVariable<T> newVariableInstance(int index, String suffix, int id)
	{
	    ItemListVariable<?> itemListVariable = null;
	    switch(delegateType)
	    {
	    case STRING:
	        itemListVariable = new ItemListVariable<String>(this, operator, index, suffix);
            break;
	    case INTEGER:
            itemListVariable = new ItemListVariable<Long>(this, operator, index, suffix);
            break;
	    case DOUBLE:
            itemListVariable = new ItemListVariable<Double>(this, operator, index, suffix);
            break;
	    case DECIMAL:
            itemListVariable = new ItemListVariable<BigDecimal>(this, operator, index, suffix);
            break;
	    case BOOLEAN:
            itemListVariable = new ItemListVariable<Boolean>(this, operator, index, suffix);
            break;
	    case ASSIGN_ONLY:
	        itemListVariable = new AxiomArrayVariable(this, index, suffix);
	        break;
	    default:
            // This is not expected to happen
            throw new IllegalArgumentException(delegateType.toString() + " not allowed for item type");
        }
        if (rightOperand != null)
        {   // The right operand is shared by all variables. First variable to evaluate will set the value
            // which will be retained until it is cleared by the template which did the evaluation.
            itemListVariable.setRightOperand(rightOperand);
        }
	    return (ItemListVariable<T>) itemListVariable;
	}

	/**
	 * newVariableInstance
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(au.com.cybersearch2.classy_logic.interfaces.Operand, java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemListVariable<T> newVariableInstance(Operand expression, String suffix, int id)
	{
        ItemListVariable<?> itemListVariable = null;
        switch(delegateType)
        {
        case STRING:
            itemListVariable = new ItemListVariable<String>(this, operator, expression, suffix);
            break;
        case INTEGER:
            itemListVariable = new ItemListVariable<Long>(this, operator, expression, suffix);
            break;
        case DOUBLE:
            itemListVariable = new ItemListVariable<Double>(this, operator, expression, suffix);
            break;
        case DECIMAL:
            itemListVariable = new ItemListVariable<BigDecimal>(this, operator, expression, suffix);
            break;
        case BOOLEAN:
            itemListVariable = new ItemListVariable<Boolean>(this, operator, expression, suffix);
            break;
        case ASSIGN_ONLY:
            itemListVariable = new AxiomArrayVariable(this, expression, suffix);
            break;
        default:
            // This is not expected to happen
            throw new IllegalArgumentException(delegateType.toString() + " not allowed for item type");
        }
        if (rightOperand != null)
        {   // The right operand is shared by all variables. First variable to evaluate will set the value
            // which will be retained until it is cleared by the template which did the evaluation.
            itemListVariable.setRightOperand(rightOperand);
        }
        return (ItemListVariable<T>) itemListVariable;
	}

	/**
	 * getItem
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getItem(int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getItem(int index) 
	{
		T item = (T) valueList.get(index);
		if (item == null)
			throw new ExpressionException(getName() + " item " + index + " not found");
		return item;
	}

	/**
	 * hasItem
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#hasItem(int)
	 */
	@Override
	public boolean hasItem(int index) 
	{
		if (index < 0)
			return false;
		return index < valueList.size() ? valueList.get(index) != null : false;
	}

	/**
	 * iterator
	 * @see java.lang.Iterable#iterator()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() 
	{
		return (Iterator<T>) valueList.iterator();
	}

	/**
	 * getIterable
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getIterable()
	 */
	@Override
	public Iterable<T> getIterable() 
	{
		/** Copy of the list items so original can be cleared */
		final   ArrayList<Object> valueList2 = new ArrayList<Object>();
		valueList2.addAll(valueList);

		return new Iterable<T>()
		{

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<T> iterator() 
			{   // Return iterator pointing to first non-null member of list
				Iterator<T> iter = (Iterator<T>)valueList2.listIterator();
				for (int index = 0; index < valueList2.size(); index++)
				{
					if (valueList2.get(index) != null)
					    break;
					iter.next();
				}
				return  iter;
			}
		};
	}

	/**
	 * clear
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#clear()
	 */
	@Override
	public void clear() 
	{
		valueList.clear();
        if (sourceItem != null)
            sourceItem.setInformation(toString());
	}

	/**
	 * getItemClass
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getItemClass()
	 */
    @Override
    public Class<?> getItemClass()
    {
        return clazz;
    }

    @Override
    public void setSourceItem(SourceItem sourceItem)
    {
        this.sourceItem = sourceItem;
    }

    @Override
    public void setRightOperand(Operand rightOperand)
    {
        this.rightOperand = rightOperand;
    }
    
    /**
     * toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() 
    {
        return "List <" + clazz.getSimpleName() + ">[" + valueList.size() + "]";
    }
}
