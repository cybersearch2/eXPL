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

import java.util.ArrayList;
import java.util.Iterator;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;

/**
 * ArrayItemList
 * List implementation. Generic types are String, Integer, Double, BigDecimal and Boolean
 * A single Operator object is shared by dependent ItemListVariable objects which take their values from the list.
 * This object resides in an OperandMap never directly interacts with other operands.
 * @author Andrew Bowley
 * 15 Jan 2015
 */
public class ArrayItemList<T> implements ItemList<T> 
{
    /** The list items */
    protected ArrayList<T> valueList;
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;
    /** Qualified name */
    protected QualifiedName qname;
    /** Operand type */
    protected OperandType operandType;

    /**
     * Construct a ListOperand object
     * @param clazz Class of list items 
     * @param qname Qualified name 
     */
	public ArrayItemList(OperandType operandType, QualifiedName qname) 
	{
	    this.operandType = operandType;
		this.qname = qname;
		valueList = new ArrayList<T>();
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
	public void assignItem(int index, T value) 
	{
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

	/**
	 * getItem
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getItem(int)
	 */
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
		final   ArrayList<T> valueList2 = new ArrayList<T>();
		valueList2.addAll(valueList);

		return new Iterable<T>()
		{
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

    @Override
    public void setSourceItem(SourceItem sourceItem)
    {
        this.sourceItem = sourceItem;
    }

    /**
     * toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() 
    {
        return "List <" + operandType.toString().toLowerCase() + ">[" + valueList.size() + "]";
    }

    @Override
    public OperandType getOperandType()
    {
        return operandType;
    }

    @SuppressWarnings("unchecked")
    public void assignItem(int index, Term term)
    {
        assignItem(index, (T) term.getValue());
    }

    @SuppressWarnings("unchecked")
    public void assignObject(int index, Object value)
    {
        assignItem(index, (T)value);
    }
}
