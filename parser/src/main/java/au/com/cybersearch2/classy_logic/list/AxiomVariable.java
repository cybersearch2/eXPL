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

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;

/**
 * AxiomVariable
 * Variable implementation for accessing AxiomLists and AxiomTermLists
 * @author Andrew Bowley
 * 31May,2017
 */
public class AxiomVariable implements ListItemDelegate
{
    /** Helper for working with AxiomLists and AxiomTermLists */
    protected AxiomListSpec axiomListSpec;
    /** List operand to supply AxiomList object on evaluation */
    protected Operand listOperand;
    /** The list to reference */
    protected ItemList<?> itemList;
    
    /**
     * Construct AxiomVariable object using suppled list operand and index data.
     * The list name is taken from the index data.
     * @param axiomListVariable Operand to supply AxiomList object on evaluation
     * @param indexDataArray Index data - 1 or 2 dimensional
     */
    public AxiomVariable(Operand listOperand, ListItemSpec[] indexDataArray)
    {
        this.listOperand = listOperand;
        axiomListSpec = new AxiomListSpec(indexDataArray);
    }
    
    /**
     * Construct AxiomVariable object using suppled AxiomList and index data
     * @param axiomList Axiom list
     * @param indexDataArray Index data - 1 or 2 dimensional
     */
    public AxiomVariable(AxiomList axiomList, ListItemSpec[] indexDataArray)
    {
        itemList = axiomList;
        axiomListSpec = new AxiomListSpec(axiomList, indexDataArray);
    }
 
    /**
     * Construct AxiomVariable object using suppled AxiomTermList and index data
     * @param axiomTermList Axiom term list
     * @param indexDataArray Index data - 1 or 2 dimensional
     */
    public AxiomVariable(AxiomTermList axiomTermList, ListItemSpec[] indexDataArray)
    {
        itemList = axiomTermList;
        axiomListSpec = new AxiomListSpec(axiomTermList, indexDataArray);
    }
 
   /**
     * getItemList
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#getItemList()
     */
    @Override
    public ItemList<?> getItemList()
    {
        return axiomListSpec.getItemIndex() == -1 ? itemList : axiomListSpec.getAxiomTermList();
    }

    /**
     * unifyTerm
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public Operand getOperand()
    {
        return axiomListSpec.getItemExpression();
    }
    
    /**
     * evaluate
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#evaluate(int)
     */
    @Override
    public int evaluate(int id)
    {   // Resolve parameters for AxiomList or AxiomTermList
        if (listOperand != null) 
        {   // Evaluate list operand
            if (listOperand.isEmpty())
                listOperand.evaluate(id);
            if (listOperand.isEmpty())
                throw new ExpressionException("List \"" + axiomListSpec.getListName() + "\" evaluation failed");
            if ((listOperand.getValueClass() == Null.class) && (listOperand instanceof Cursor))
                itemList = ((Cursor)listOperand).getItemList();
            else if (listOperand.getValueClass() == Axiom.class)
                itemList = wrapAxiom((Axiom)listOperand.getValue());
            else
                itemList = (ItemList<?>) listOperand.getValue();
        }
        axiomListSpec.evaluate(itemList, id);
        // Assume 2 dimensions or AxiomTermList with 1 dimension.
        // Use item index to select value
        int newIndex = axiomListSpec.getItemIndex();
        if (newIndex == -1)
            // Only one dimension with AxiomList.  
            newIndex = axiomListSpec.getAxiomIndex();
        return newIndex;
    }
    
    /**
     * backup
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#backup(int)
     */
    @Override
    public boolean backup(int id) 
    {  
        axiomListSpec.backup(id);
        if (listOperand != null)  
            listOperand.backup(id);
        return true;
    }
    
    /**
     * setItemValue
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#setItemValue(java.lang.Object)
     */
    @Override
    public void setItemValue(Object value)
    {
        if (itemList == null) 
            // Something unexpected has happened - cannot proceed
            return;
        int index = axiomListSpec.getItemIndex(); 
        if (index == -1)
        {
            index = axiomListSpec.getAxiomIndex();
            if (index == -1)
                // Something unexpected has happened - cannot proceed
                return;
            // Assign value to item list
            ((AxiomList)itemList).assignItem(index, (AxiomTermList)value);
        }
        else
        {   
            // An AxiomList containing a single item is unwrapped
            AxiomTermList axiomTermList = axiomListSpec.getAxiomTermList();
            // Update term in axiom referenced by list
            Term term = axiomTermList.getItem(index);
            term.setValue(value);
        }
    }
    
    /**
     * getValue
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#getValue()
     */
    @Override
    public Object getValue()
    {
        int index = axiomListSpec.getItemIndex();
        if (index == -1)
        {
            index = axiomListSpec.getAxiomIndex();
            if (index == -1)
                // Something unexpected has happened - cannot proceed
                return new Null();
        }
        return getValue(index);
    }

    /**
     * getValue(int)
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#getValue(int)
     */
    @Override
    public Object getValue(int selection)
    {
        if (itemList == null) 
            // Something unexpected has happened - cannot proceed
            return new Null();
        int index = axiomListSpec.getItemIndex(); 
        if (index == -1)
        {
            index = axiomListSpec.getAxiomIndex();
            if (index == -1)
                // Something unexpected has happened - cannot proceed
                return new Null();
            // Assign value to item list
            return itemList.hasItem(selection) ? itemList.getItem(selection) : new Null();
        }
        else
        {
            // An AxiomList containing a single item is unwrapped
            Term term = axiomListSpec.getAxiomTermList().getItem(selection);
            return term != null ? term.getValue() : new Null();
        }
    }

    /**
     * append
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#append(java.lang.Object)
     */
    @Override
    public void append(Object value)
    {
        if (value instanceof AxiomTermList)
            axiomListSpec.appendAxiomTermList((AxiomTermList)value);
        else
            axiomListSpec.appendAxiomList((AxiomList)value);
        itemList = axiomListSpec.getItemList();
        int appendIndex = itemList.getLength() - 1;
        if (axiomListSpec.getItemIndex() == -1)
            axiomListSpec.setAxiomIndex(appendIndex);
        else
            axiomListSpec.setItemIndex(appendIndex);
    }

    private ItemList<?> wrapAxiom(Axiom axiom)
    {
        Term term = axiom.getTermByIndex(0);
        if (ItemList.class.isAssignableFrom(term.getValueClass()))
            return (ItemList<?>) term.getValue();
        AxiomArchetype archetype = (AxiomArchetype) axiom.getArchetype();
        AxiomTermList axiomTermList = new AxiomTermList(archetype.getQualifiedName(), archetype.getQualifiedName());
        axiomTermList.getAxiomListener().onNextAxiom(archetype.getQualifiedName(), axiom);
        return axiomTermList;
    }

}
