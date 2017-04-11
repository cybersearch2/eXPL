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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;

/**
 * ArrayItemList
 * List implementation. Generic types are String, Integer, Double, BigDecimal and Boolean
 * A proxy Operand is supplied to participate in item expressions. 
 * The proxy is shared by dependent ItemListVariable objects which take their values from the list.
 * This object resides in an OperandMap never directly interacts with other operands.
 * @author Andrew Bowley
 * 15 Jan 2015
 */
public class ArrayItemList<T> implements ItemList<T> 
{
	/**
	 * VariableFactory
	 * Constructs list variable to match list type
	 */
	interface VariableFactory
	{
		/** Construct a list variable for a fixed list item */
		ItemListVariable<?> newVariableInstance(ItemList<?> operandList, Operand proxy, int index, String suffix, int id);
		/** Construct a list variable for a dynamically selected item */
		ItemListVariable<?> newVariableInstance(ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id);
	}
	
	/** Map value class to ItemListVariable class factory */
    protected static Map<Class<?>, VariableFactory> factorylassMap;
 
    /** List class type */
    protected Class<T> clazz;
    /** Operand delegate to provide evaluation functionality */
    protected Operand proxy;
    /** The list items */
    protected ArrayList<Object> valueList;
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;
 
    static
    {
    	factorylassMap = new HashMap<Class<?>,  VariableFactory>();
    	factorylassMap.put(String.class, new VariableFactory(){

			@Override
			public ItemListVariable<String> newVariableInstance(ItemList<?> operandList, Operand proxy, 
					int index, String suffix, int id) {
				return new ItemListVariable<String>(operandList, proxy, index, suffix);
			}

			@Override
			public ItemListVariable<String> newVariableInstance(
					ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id) {
				return new ItemListVariable<String>(operandList, proxy, expression, suffix);
			}});
    	factorylassMap.put(Long.class, new VariableFactory(){

			@Override
			public ItemListVariable<Integer> newVariableInstance(ItemList<?> operandList, Operand proxy, 
					int index, String suffix, int id) {
				return new ItemListVariable<Integer>(operandList, proxy, index, suffix);
			}

			@Override
			public ItemListVariable<Integer> newVariableInstance(
					ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id) {
				return new ItemListVariable<Integer>(operandList, proxy, expression, suffix);
			}});
    	factorylassMap.put(Boolean.class, new VariableFactory(){

			@Override
			public ItemListVariable<Boolean> newVariableInstance(ItemList<?> operandList, Operand proxy, 
					int index, String suffix, int id) {
				return new ItemListVariable<Boolean>(operandList, proxy, index, suffix);
			}

			@Override
			public ItemListVariable<Boolean> newVariableInstance(
					ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id) {
				return new ItemListVariable<Boolean>(operandList, proxy, expression, suffix);
			}});
    	factorylassMap.put(Double.class, new VariableFactory(){

			@Override
			public ItemListVariable<Double> newVariableInstance(ItemList<?> operandList, Operand proxy, 
					int index, String suffix, int id) {
				return new ItemListVariable<Double>(operandList, proxy, index, suffix);
			}

			@Override
			public ItemListVariable<Double> newVariableInstance(
					ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id) {
				return new ItemListVariable<Double>(operandList, proxy, expression, suffix);
			}});
    	factorylassMap.put(BigDecimal.class, new VariableFactory(){

			@Override
			public ItemListVariable<BigDecimal> newVariableInstance(ItemList<?> operandList, Operand proxy, 
					int index, String suffix, int id) {
				return new ItemListVariable<BigDecimal>(operandList, proxy, index, suffix);
			}

			@Override
			public ItemListVariable<BigDecimal> newVariableInstance(
					ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id) {
				return new ItemListVariable<BigDecimal>(operandList, proxy, expression, suffix);
		    }});
    	factorylassMap.put(AxiomTermList.class, new VariableFactory(){

			@Override
			public AxiomArrayVariable newVariableInstance(ItemList<?> operandList, Operand proxy, 
					int index, String suffix, int id) {
				return new AxiomArrayVariable(operandList, proxy, index, suffix);
			}

			@Override
			public AxiomArrayVariable newVariableInstance(
					ItemList<?> operandList, Operand proxy, Operand expression, String suffix, int id) {
				return new AxiomArrayVariable(operandList, proxy, expression, suffix);
		    }});
    }
    
    /**
     * Construct a ListOperand object
     * @param clazz Class of list items 
     * @param proxy Operand delegate to provide evaluation functionality 
     */
	public ArrayItemList(Class<T> clazz, Operand proxy) 
	{
		this.clazz = clazz;
		this.proxy = proxy;
		valueList = new ArrayList<Object>();
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
        return proxy.getQualifiedName();
    }

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getName()
	 */
	@Override
	public String getName() 
	{
		return proxy.getName();
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
		if (!clazz.isInstance(value))
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

	/**
	 * newVariableInstance
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(int, java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemListVariable<T> newVariableInstance(int index, String suffix, int id)
	{
		return (ItemListVariable<T>) factorylassMap.get(clazz).newVariableInstance(this, proxy, index, suffix, id);
	}

	/**
	 * newVariableInstance
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(au.com.cybersearch2.classy_logic.interfaces.Operand, java.lang.String, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ItemListVariable<T> newVariableInstance(Operand expression, String suffix, int id)
	{
		return (ItemListVariable<T>) factorylassMap.get(clazz).newVariableInstance(this, proxy, expression, suffix, id);
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
