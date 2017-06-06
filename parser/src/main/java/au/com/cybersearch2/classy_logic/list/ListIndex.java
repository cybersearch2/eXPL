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

import java.util.List;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ListIndex
 * @author Andrew Bowley
 * 23May,2017
 */
public class ListIndex extends ArrayIndex
{
    protected String selection;
    
    public ListIndex(QualifiedName qname, String selection)
    {
        super(qname, null, selection);
        this.selection = selection;
    }
    
    public ListIndex(QualifiedName qname, Operand indexExpression)
    {
        super(qname, indexExpression);
    }
 
    public String getSelection()
    {
        return selection;
    }
    
    @Override
    public void assemble(ItemList<?> itemList)
    {
        if (/*(index == -1) &&*/ (selection != null) && (itemList instanceof AxiomContainer))
            setAxiomTermListIndex((AxiomContainer)itemList);
        super.assemble(itemList);
    }
    
    @Override
    public QualifiedName getVariableName()
    {
        return new QualifiedName(getVariableName(qname.getName(), suffix) + qname.incrementReferenceCount(), qname);
    }
    /**
     * Returns variable name given list name and suffix
     * @param listName
     * @param suffix
     * @return String
     */
    protected String getVariableName(String listName, String suffix)
    {
        return listName + "_" + suffix;
    }

    protected void setIntIndex()
    {
        index = ((Long)indexExpression.getValue()).intValue();
        if (indexExpression.getName().isEmpty())
            suffix = getListName() + "." + index;
        else 
            suffix = indexExpression.getName();
    }

    @Override
    protected void setStringIndex(ItemList<?> itemList)
    {
        if (itemList instanceof AxiomTermList)
        {
            suffix = selection = indexExpression.getValue().toString();
            setAxiomTermListIndex((AxiomTermList)itemList);
        }
    }
    
    protected void setAxiomTermListIndex(AxiomContainer axiomContainer)
    {
        List<String> axiomTermNameList = axiomContainer.getAxiomTermNameList();
        if ((axiomTermNameList != null) && !suffix.isEmpty())
            index = getIndexForName(suffix, axiomTermNameList);
        else
            throw new ExpressionException("List \"" + getListName() + "\" term names not available for indexed access");
    }


    /**
     * Returns index of item identified by name
     * @param item Item name
     * @param axiomTermNameList Term names of axiom source
     * @return Index
     */
    protected int getIndexForName(String item, List<String> axiomTermNameList) 
    {
        for (int i = 0; i < axiomTermNameList.size(); i++)
        {
            if (item.equals(axiomTermNameList.get(i)))
                return i;
        }
        throw new ExpressionException("List \"" + getListName() + "\" does not have term named \"" + item + "\"");
    }



}
