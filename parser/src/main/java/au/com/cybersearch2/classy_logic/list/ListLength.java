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

import au.com.cybersearch2.classy_logic.compile.ListAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;

/**
 * ListLength
 * Operand to evaluate length of a list
 * @author Andrew Bowley
 * 16 Jan 2015
 */
public class ListLength extends Variable implements ParserRunner
{
	/** List name */
	protected QualifiedName listName;
	/** The list object */
    protected ItemList<?> itemList;
    /** Operand containing a list value */
    protected Operand itemListOperand;
	
	/**
	 * Construct a ListLength object
	 * @param qname Qualified name of Variable - list name with "_length" appended 
	 * @param listName List name in text format
	 * @param itemList The list object
	 */
	public ListLength(QualifiedName qname, QualifiedName listName) 
	{
		super(qname);
		this.listName = listName;
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

    @Override
    public void run(ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        itemList = listAssembler.findItemList(listName);
        if (itemList == null)
        {
            QualifiedName targetName = listAssembler.getAxiomListMapping(listName);
            itemListOperand = parserAssembler.getOperandMap().getOperand(targetName);
            if (itemListOperand == null)
                itemListOperand = parserAssembler.findOperandByName(targetName.getName());
            if (itemListOperand == null)
            {
                if (!listName.getScope().isEmpty())
                {
                    listName.clearScope();
                    itemList = listAssembler.findItemList(listName);
                }
                if (itemList == null)
                    throw new ExpressionException("List \"" + listName + "\" cannot be found");
            }
        }
    }
}
