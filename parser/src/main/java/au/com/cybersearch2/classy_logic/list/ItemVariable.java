/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ItemVariable
 * Variable implementation for accessing array lists
 * @author Andrew Bowley
 * 1Jun.,2017
 */
public class ItemVariable<T> implements ListItemDelegate
{
    /** The list to reference */
    protected ItemList<T> itemList;
    /** Index information for value selection  */
    protected ListItemSpec indexData;

    /**
     * @param itemList The list to reference 
     */
    public ItemVariable(ItemList<T> itemList, ListItemSpec indexData)
    {
        this.itemList = itemList;
        this.indexData = indexData;
    }

    /**
     * unifyTerm
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        Operand indexExpression = indexData.getItemExpression();
        if (indexExpression != null)
            return indexExpression.unifyTerm(otherTerm, id);
        return 0;
    }
    
    @Override
    public ItemList<T> getItemList()
    {
        return itemList;
    }

    /**
     * evaluate
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#evaluate(int)
     */
    @Override
    public int evaluate(int id)
    {   // Resolve parameters for array list
        indexData.assemble(itemList);
        indexData.evaluate(itemList, id);
        return indexData.getItemIndex();
    }

    /**
     * backup
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#backup(int)
     */
    @Override
    public boolean backup(int id)
    {
        Operand indexExpression = indexData.getItemExpression();
        if (indexExpression != null)
            return indexExpression.backup(id);
        return false;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#setItemValue(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setItemValue(Object value)
    {
        int index = indexData.getItemIndex();
        if (index != -1)
        {   // Assign value to item list = hard
            // Save old item, if available, to check if same as new value
            Object itemValue = itemList.hasItem(index) ? itemList.getItem(index) : null;
            // Use flag to signal whether update required
            boolean proceed = (itemValue == null);
            if (!proceed)
            {   // Check if update required by comparing new item to old one
                if (itemValue instanceof Term)
                    itemValue = ((Term)itemValue).getValue();
                proceed = !itemValue.equals(value);
            }
            if (proceed)
            {
                if (itemList.getOperandType() == OperandType.TERM)
                    itemList.assignItem(indexData.getItemIndex(), (T) new Parameter(Term.ANONYMOUS, value));
                else
                    itemList.assignItem(indexData.getItemIndex(), (T)value);
            }
        }
    }

    /**
     * getValue
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#getValue()
     */
    @Override
    public Object getValue()
    {
        return getValue(indexData.getItemIndex());
    }

    /**
     * getValue(int)
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#getValue(int)
     */
    @Override
    public Object getValue(int selection)
    {
        // Index should be valid, but check for safety
        if (!itemList.hasItem(selection))
            return new Null();
        Object item = itemList.getItem(selection);
        if (item instanceof Term)
            return((Term)item).getValue();
        return item;
    }

}
