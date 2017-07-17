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
 * Extends ArrayIndex to provide support for string indexes
 * @author Andrew Bowley
 * 23May,2017
 */
public class ListIndex extends ArrayIndex
{
    /** Selection string */
    protected String selection;

    /**
     * Construct ListIndex with specified selection value
     * @param qname Qualified list name
     * @param selection Selection value
     */
    public ListIndex(QualifiedName qname, String selection)
    {
        super(qname, null, selection);
        this.selection = selection;
    }

    /**
     * Construct ListIndex with specified index operand
     * @param qname Qualified list name
     * @param indexExpression Index evaluation operand
     */
    public ListIndex(QualifiedName qname, Operand indexExpression)
    {
        super(qname, indexExpression);
    }
 
    /**
     * Returns selection string
     * @return String
     */
    public String getSelection()
    {
        return selection;
    }
 
    /**
     * assemble
     * @see au.com.cybersearch2.classy_logic.list.ArrayIndex#assemble(au.com.cybersearch2.classy_logic.interfaces.ItemList)
     */
    @Override
    public void assemble(ItemList<?> itemList)
    {
        if ((selection != null) && (itemList instanceof AxiomContainer))
            setAxiomTermListIndex((AxiomContainer)itemList);
        super.assemble(itemList);
    }
    
    /**
     * getVariableName
     * @see au.com.cybersearch2.classy_logic.list.ArrayIndex#getVariableName()
     */
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

    /**
     * setIntIndex
     * @see au.com.cybersearch2.classy_logic.list.ArrayIndex#setIntIndex()
     */
    @Override
    protected void setIntIndex()
    {
        Object object = indexExpression.getValue();
        index = (object instanceof Long) ? ((Long)object).intValue() : ((Integer)object).intValue();
        if (indexExpression.getName().isEmpty())
            suffix = getListName() + "." + index;
        else 
            suffix = indexExpression.getName();
    }

    /**
     * setStringIndex
     * @see au.com.cybersearch2.classy_logic.list.ArrayIndex#setStringIndex(au.com.cybersearch2.classy_logic.interfaces.ItemList)
     */
    @Override
    protected void setStringIndex(ItemList<?> itemList)
    {
        if (itemList instanceof AxiomTermList)
        {
            suffix = selection = indexExpression.getValue().toString();
            setAxiomTermListIndex((AxiomTermList)itemList);
        }
    }

    /**
     * Set axiom term names
     * @param axiomContainer Axiom list or axiom term list
     */
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
