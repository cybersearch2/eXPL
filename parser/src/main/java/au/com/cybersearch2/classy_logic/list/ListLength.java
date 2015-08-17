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

import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ListLength
 * Operand to evaluate length of a list
 * @author Andrew Bowley
 * 16 Jan 2015
 */
public class ListLength extends Variable 
{
	/** List name. This operand's name has ".length" appended */
	protected String listName;
	/** The list object */
    protected ItemList<?> itemList;
    /** Operand containing a list value */
    protected Operand itemListOperand;
	
	/**
	 * Construct a ListLength object
	 * @param listName
	 * @param itemList The list object
	 */
	public ListLength(String listName, ItemList<?> itemList) 
	{
		super(listName + "." + "length");
		this.listName = listName;
        this.itemList = itemList;
	}

    /**
     * Construct a ListLength object
     * @param listName
     * @param itemListOperand The operand to contain a list object after evaluation
     */
    public ListLength(String listName, Operand itemListOperand) 
    {
        super(listName + "." + "length");
        this.listName = listName;
        this.itemListOperand = itemListOperand;
    }

	/**
	 * Evaluate list length. 
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
	    if (itemListOperand != null)
	    {
	        if (!itemListOperand.isEmpty())
	            itemList = (ItemList<?>)itemListOperand.getValue();
	    }
		setValue(Integer.valueOf(itemList != null ? itemList.getLength() : 0));
		this.id = id;
		return EvaluationStatus.COMPLETE;
	}

}
