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
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
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
	/** List name. This operand's name has "_length" appended */
	protected QualifiedName qualifiedListName;
	/** The list object */
    protected ItemList<?> itemList;
    /** Operand containing a list value */
    protected Operand itemListOperand;
	
	/**
	 * Construct a ListLength object
	 * @param qname Qualified name of Variable
	 * @param itemList The list object
	 */
	public ListLength(QualifiedName qname, ItemList<?> itemList) 
	{
		super(getLengthName(qname));
		this.qualifiedListName = qname;
        this.itemList = itemList;
	}

    /**
     * Construct a ListLength object
     * @param qname Qualified name of Variable
     * @param itemListOperand The operand to contain a list object after evaluation
     */
    public ListLength(QualifiedName qname, Operand itemListOperand) 
    {
        super(getLengthName(qname));
        this.qualifiedListName = qname;
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

    protected static QualifiedName getLengthName(QualifiedName qname)
    {
        return new QualifiedName(qname.getName().toString() + "_length", qname);
    }

}
