/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * AxiomListSpec
 * Specification of an AxiomList variable which allows for dynamic assignment
 * of the backing AxiomList
 * @author Andrew Bowley
 * 4 Aug 2015
 */
public class AxiomListSpec implements ListItemSpec
{
    /** Name of axiom list */
    protected QualifiedName qualifiedListName;
    /** The list */
    protected ItemList<?> itemList;
    protected ListItemSpec indexData;
    protected ListItemSpec arrayData;
    /** Axiom selection index. Value of -1 means not available or not used */
    protected int axiomIndex;
    /** Term selection index. Value of -1 means not available or not used */
    protected int termIndex;
    /** Compiler operand to supply AxiomList object on evaluation */
    protected Operand axiomListVariable;
    /** Flag set true if assigned to an AxiomTermList */
    protected boolean isTermList;
    protected AxiomTermList axiomTermList;
    
    /**
     * Construct AxiomListSpec object for case backing AxiomList is available
     * @param axiomList Backing axiom list
     * @param indexDataArray 
     */
    public AxiomListSpec(AxiomList axiomList, ListItemSpec[] indexDataArray)
    {
        this.itemList = axiomList;
        indexData = indexDataArray.length == 1 ? indexDataArray[0] : indexDataArray[1];
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
        this.qualifiedListName = axiomList.getQualifiedName();
        init();
    }

    /**
    * Construct AxiomListSpec object for case backing AxiomList needs to be evaluated
     * @param qualifiedListName Qualified nName of axiom list
     * @param axiomListVariable Compiler operand to supply AxiomList object on evaluation
     * @param axiomExpression Compiler operand for axiom selection
     * @param termExpression Compiler operand for term selection
     */
   public AxiomListSpec(Operand axiomListVariable, ListItemSpec[] indexDataArray)
   {
        this.axiomListVariable = axiomListVariable;
        indexData = indexDataArray.length == 1 ? indexDataArray[0] : indexDataArray[1];
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
        this.qualifiedListName = indexData.getQualifiedListName();
        init();
    }
 
    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getListName()
     */
    @Override
    public String getListName()
    {
        return qualifiedListName.getName();
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getQualifiedListName()
     */
    @Override
    public QualifiedName getQualifiedListName()
    {
        return qualifiedListName;
    }

   /**
     * Returns backing axiom list
     * @return AxiomList object or null if waiting for evaluation
     */
    public ItemList<?> getItemList()
    {
        return itemList;
    }

    public AxiomTermList getAxiomTermList()
    {
        if (itemList == null)
            return getEmptyAxiomTermList();
        return (axiomTermList != null) ? axiomTermList :  ((AxiomList)itemList).getItem(axiomIndex);
    }
    
    /**
     * Returns axiom selection index
     * @return Valid index or -1 if not used
     */
    public int getAxiomIndex()
    {
        return axiomIndex;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getItemIndex()
     */
    @Override
    public int getItemIndex()
    {
        return termIndex;
    }

    public Operand getAxiomListVariable()
    {
        return axiomListVariable;
    }
    
    /**
     * Returns Compiler operand for axiom selection
     * @return Operand object
     */
    public Operand getAxiomExpression()
    {
        return arrayData != null ? arrayData.getItemExpression() : indexData.getItemExpression();
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getItemExpression()
     */
    @Override
    public Operand getItemExpression()
    {
        return arrayData != null ? indexData.getItemExpression() : null;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getSuffix()
     */
    @Override
    public String getSuffix()
    {
        return indexData.getSuffix();
    }

    /**
     * Initialize this object. Determines if index values are available or need to be evaluated.
     */
    protected void init()
    {
        axiomIndex = arrayData != null ? arrayData.getItemIndex() : indexData.getItemIndex();
        termIndex = arrayData != null ? indexData.getItemIndex() : -1;
    }

    /**
     * Returns item index given name of item
     * @param itemName
     * @param axiomTermNameList List of axiom term names
     * @return int
     */
    protected int getIndexByName(String itemName, List<String> axiomTermNameList)
    {
        for (int i = 0; i < axiomTermNameList.size(); i++)
        {
            // Strip term name down to name only
            String termName = axiomTermNameList.get(i);
            if (itemName.equals(termName))
                return i;
        }
        return -1;
    }

    @Override
    public QualifiedName getVariableName()
    {
        return null;
    }

    @Override
    public void assemble(ItemList<?> itemList)
    {
        indexData.assemble(itemList);
    }

    @Override
    public boolean evaluate(ItemList<?> itemList, int id)
    {
        this.itemList = itemList;
        axiomTermList = (itemList instanceof AxiomTermList) ? (AxiomTermList)itemList: null;
        // Note: indexData.assemble() is always invoked because scope local axioms may change when scope changes
        boolean isAxiomList = false;
        if (arrayData != null)
        {
            if (axiomTermList == null)
            {
                arrayData.evaluate(itemList, id);
                axiomIndex = arrayData.getItemIndex();
                axiomTermList = ((AxiomList)itemList).getItem(axiomIndex);
            }
            else
                axiomIndex = -1;
        }
        else
        {
            if (axiomTermList == null)
            {   // indexData selects AxiomTermList item 
                AxiomList axiomList = ((AxiomList)itemList);
                isAxiomList = axiomList.getLength() > 1;
                if (isAxiomList)
                    termIndex = -1;
                if (axiomList.isEmpty())
                    axiomTermList = getEmptyAxiomTermList();
                else
                    axiomTermList = axiomList.getItem(0);
            }
        }
        if (axiomTermList.isEmpty())
        {
            assemble(itemList);
            indexData.evaluate(itemList, id);
        }
        else
        {
            assemble(axiomTermList);
            indexData.evaluate(axiomTermList, id);
        }
        if (isAxiomList)
            axiomIndex = indexData.getItemIndex();
        else
            termIndex = indexData.getItemIndex();
        return true;
    }

    private AxiomTermList getEmptyAxiomTermList()
    {
        QualifiedName qname = new QualifiedName(getListName() + "_item", qualifiedListName);
        return new AxiomTermList(qname, qname);
    }

}
