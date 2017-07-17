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
	}

    public int getIndex()
    {
        return indexData.getItemIndex();
    }

    public void setIndex(int index)
    {
        ((ArrayIndex)indexData).setItemIndex(index);
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
	        backup(id);
	    return status;
	}
}
