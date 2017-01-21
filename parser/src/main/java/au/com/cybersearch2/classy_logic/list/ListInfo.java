/**
    Copyright (C) 2016  www.cybersearch2.com.au

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

import java.util.List;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * ListInfo
 * @author Andrew Bowley
 * 19Jan.,2017
 */
public class ListInfo
{
    /** The backing operand list */
    protected ItemList<?> itemList;
    /** Operand to evaluate index. Will be null if index is fixed. */
    protected Operand indexExpression;
    /** Curent index value. Will be constant if indexExpression is null.  */
    protected int index;
    /** Suffix to append to name */
    protected String suffix;
    
    public ListInfo(ItemList<?> itemList, Operand indexExpression)
    {
        this.itemList = itemList;
        this.indexExpression = indexExpression;
    }
    
    public ItemList<?> getItemList()
    {
        return itemList;
    }

    public Operand getIndexExpression()
    {
        return indexExpression;
    }

    public int getIndex()
    {
        return index;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void initialize()
    {
        String name = itemList.getName();
        int index = -1;
        String suffix = null;
        if (!indexExpression.isEmpty() && 
            (indexExpression instanceof IntegerOperand) && 
            Term.ANONYMOUS.equals(indexExpression.getName()))
        {
           index = ((Long)(indexExpression.getValue())).intValue();
           suffix = Long.toString(index);
        }
        else if (indexExpression.isEmpty() && 
                  (indexExpression instanceof Variable))
        {
            if (itemList instanceof AxiomTermList)
            {
                AxiomTermList axiomTermList = (AxiomTermList)itemList;
                List<String> axiomTermNameList = axiomTermList.getAxiomTermNameList();
                if (axiomTermNameList != null)
                {
                    suffix = indexExpression.getName();
                    index = getIndexForName(name, suffix, axiomTermNameList);
                }
                else
                    suffix = indexExpression.toString();

            }
            else // Interpret identifier as a variable name for any primitive list
            {
                indexExpression = new Variable(indexExpression.getQualifiedName());
                suffix = indexExpression.getName();
            }
        }
        if (suffix == null)
            suffix = indexExpression.getName().isEmpty() ? 
                     indexExpression.toString() : 
                     indexExpression.getName();
    }
    
    /**
     * Returns index of item identified by name
     * @param listName Name of list - used only for error reporting
     * @param item Item name
     * @param axiomTermNameList Term names of axiom source
     * @return Index
     */
    protected int getIndexForName(String listName, String item, List<String> axiomTermNameList) 
    {
        for (int i = 0; i < axiomTermNameList.size(); i++)
        {
            if (item.equals(axiomTermNameList.get(i)))
                return i;
        }
        throw new ExpressionException("List \"" + listName + "\" does not have term named \"" + item + "\"");
    }

}
