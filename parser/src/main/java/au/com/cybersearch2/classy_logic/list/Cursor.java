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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.operator.DelegateType;

/**
 * Cursor
 * Operand to navigate a list
 * @author Andrew Bowley
 * 16 Jan 2015
 */
public class Cursor extends ListItemVariable
{
	/** List name */
	protected QualifiedName listName;
	/** The list object */
    protected ItemList<?> itemList;
    /** Operand containing a list value */
    protected Operand itemListOperand;
    protected int cursorIndex;
    /** Flag set false when fact status changes to negative */
    protected boolean isFact;
    protected boolean isForward;
	
	/**
	 * Construct a Cursor object
	 * @param qname Qualified name of Variable - list name with "_length" appended 
	 * @param listName List name in text format
	 * @param itemList The list object
	 */
	public Cursor(QualifiedName qname, QualifiedName listName, ArrayIndex arrayIndex) 
	{
		super(qname, arrayIndex);
		this.listName = listName;
		cursorIndex = -1;
		isFact = true;
		isForward = true;
        setDelegateType(DelegateType.CURSOR);
	}

    public boolean isFact()
    {
        return isFact;
    }

    public int getIndex()
    {
        return cursorIndex;
    }

    public Object setIndex(int index)
    {
        if (!isFact)
            return null;
        int size = delegate.getItemList().getLength();
        int diff = index - cursorIndex;
        if (isForward && (diff == -1))
        {
            reverse();
            return getItemValue();
        }
        else if (!isForward && (diff == 1))
        {
            forward();
            return getItemValue();
        }
        else
            cursorIndex = index;
        index = indexData.getItemIndex() + diff;
        isForward = diff >=0;
        // Flag set false when fact status changes to negative
        isFact = !empty;
        if (size == 0) // Cannot set index of empty array
        {
            isFact = false;
            return null;
        }
        if (index >= size) 
        {
            cursorIndex = -1;
            index = 0;
            isFact = false;
        }
        else if (index < 0)
        {
            cursorIndex = size;
            index = size - 1;
            isFact = false;
        }
        Object value = getItemValue();
        ((ArrayIndex)indexData).setItemIndex(index);
        return value;
    }

    public long forward()
    {
        isForward = true;
        int size = delegate.getItemList().getLength();
        if (size == 0) // Cannot set index of empty array
        {
            isFact = false;
            return -1;
        }
        cursorIndex = -1;
        ((ArrayIndex)indexData).setItemIndex(0);
        isFact = true;
        return 0;
    }

    public long reverse()
    {
        isForward = false;
        int size = delegate.getItemList().getLength();
        if (size == 0) // Cannot set index of empty array
        {
            isFact = false;
            return -1;
        }
        cursorIndex = size;
        ((ArrayIndex)indexData).setItemIndex(size - 1);
        isFact = true;
        return size -1;
    }
    
	/**
	 * Evaluate list item. 
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
	    EvaluationStatus status = super.evaluate(id);
	    if (status == EvaluationStatus.COMPLETE)
	        setId(id);
	    return status;
	}

    /**
     * backup
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int)
     */
    @Override
    public boolean backup(int id) 
    {  
        if (!isFact)
        {
            if ((id != this.id))
            {
                if (isForward)
                    forward();
                else
                    reverse();
            }
            return true;
        }
        return super.backup(id);
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.list.ListItemVariable#toString()
     */
    @Override
    public String toString()
    {
        if (!isFact)
            return Boolean.FALSE.toString();
        return super.toString();
    }

    @Override
    public Object getValue()
    {
        Object cursorValue = super.getValue();
        if ((rightOperand != null) && (cursorValue.getClass() != Null.class))
        {
            rightOperand.setValue(value);
            rightOperand.evaluate(id);
            cursorValue = rightOperand.getValue();
        }
        return cursorValue;
    }

}
