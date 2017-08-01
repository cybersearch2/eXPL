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
    /** Flag set false when fact status changes to negative */
    boolean isFact;
	
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
		isFact = true;
	}

    public int getIndex()
    {
        return indexData.getItemIndex();
    }

    public void setIndex(int index)
    {
        // Flag set false when fact status changes to negative
        isFact = !empty;
        int size = delegate.getItemList().getLength();
        if (size == 0) // Cannot set index of empty array
        {
            isFact = false;
            return;
        }
        if (index >= size)
        {
            index = 0;
            isFact = false;
        }
        else if (index < 0)
        {
            index = size - 1;
            isFact = false;
        }
        ((ArrayIndex)indexData).setItemIndex(index);
    }

    public long forward()
    {
        int size = delegate.getItemList().getLength();
        if (size == 0) // Cannot set index of empty array
        {
            isFact = false;
            return -1;
        }
        isFact = true;
        ((ArrayIndex)indexData).setItemIndex(0);
        return 0;
    }

    public long reverse()
    {
        int size = delegate.getItemList().getLength();
        if (size == 0) // Cannot set index of empty array
        {
            isFact = false;
            return -1;
        }
        isFact = true;
        int index = delegate.getItemList().getLength() -1;
        ((ArrayIndex)indexData).setItemIndex(index);
        return index;
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
	    if (value instanceof Null)
	    {
	        backup(id);
	        isFact = false;
	    }
	    if (!isFact)
            empty = true;
	    return status;
	}

}
