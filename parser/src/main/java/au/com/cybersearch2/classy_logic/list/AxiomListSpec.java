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
 * Assists list variable with evaluation of list operand (AxiomList) and parameter resolution for
 * one or two dimensions. Also unwraps list containing a single item.
  * @author Andrew Bowley
 * 4 Aug 2015
 */
public class AxiomListSpec implements ListItemSpec
{
    /** Name of axiom list */
    protected QualifiedName qualifiedListName;
    /** The list to be accessed. if a list operand is provided, then setting this field is delayed until evaluation. */
    protected ItemList<?> itemList;
    /** Index information for value selection  */
    protected ListItemSpec indexData;
    /** Index information for 2 dimension case - select axiom, then select term in axiom */
    protected ListItemSpec arrayData;
    /** Axiom selection index. Value of -1 means not available or not used */
    protected int axiomIndex;
    /** Term selection index. Value of -1 means not available or not used */
    protected int termIndex;
    /** Item extracted or produced in most recent evaluation. May be empty. It is a valid selection value only if termIndex is valid. */
    protected AxiomTermList axiomTermList;
    
    /**
     * Construct AxiomListSpec object using suppled AxiomList and index data
     * @param axiomList Backing axiom list
     * @param indexDataArray Index data - 1 or 2 dimensional
     */
    public AxiomListSpec(AxiomList axiomList, ListItemSpec[] indexDataArray)
    {
        this.itemList = axiomList;
        indexData = indexDataArray.length == 1 ? indexDataArray[0] : indexDataArray[1];
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
        this.qualifiedListName = 
            (axiomList != null) ? 
            axiomList.getQualifiedName() : 
            indexData.getQualifiedListName();
        // Initialize index fields. Determines what type of selection has been evaluated:
        // Both indexes valid = 2-dimensional selection
        // Only axiomIndex valid - axiom selected from AxiomList
        // Only termIndex valid = term selected from AxiomTermList
        // Both indexes invalid = no value available.
        axiomIndex = arrayData != null ? arrayData.getItemIndex() : indexData.getItemIndex();
        termIndex = arrayData != null ? indexData.getItemIndex() : -1;
     }

    /**
     * Construct AxiomListSpec object using suppled list operand and index data.
     * The list name is taken from the index data.
     * @param indexDataArray Index data - 1 or 2 dimensional
     */
    public AxiomListSpec(ListItemSpec[] indexDataArray)
    {
        this((AxiomList)null, indexDataArray);
    }
 
    /**
     * getListName
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getListName()
     */
    @Override
    public String getListName()
    {
        return qualifiedListName.getName();
    }

    /**
     * getQualifiedListName
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getQualifiedListName()
     */
    @Override
    public QualifiedName getQualifiedListName()
    {
        return qualifiedListName;
    }

   /**
     * Returns axiom list, if one supplied, otherwise, axiom list or axiom term list depending on outcome of most recent evaluation
     * @return ItemList object or possibly null if using list operand and waiting for evaluation
     */
    public ItemList<?> getItemList()
    {
        return itemList;
    }

    /**
     * Returns item extracted or produced in most recent evaluation. This is a selection value if termIndex is valid. 
     * @return AxiomTermList object. This may be empty, but never null.
     */
    public AxiomTermList getAxiomTermList()
    {
        if (itemList == null)
            return getEmptyAxiomTermList();
        return (axiomTermList != null) ? axiomTermList :  ((AxiomList)itemList).getItem(axiomIndex);
    }

    /**
     * Set axiom index
     * @param appendIndex
     */
    public void setAxiomIndex(int axiomIndex)
    {
        this.axiomIndex = axiomIndex;
    }
    
    /**
     * Returns axiom selection index
     * @return Valid index or -1 if not used
     */
    public int getAxiomIndex()
    {
        return axiomIndex;
    }

    @Override
    public void setItemIndex(int itemIndex)
    {
        termIndex = itemIndex;
    }

   /**
     * getItemIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getItemIndex()
     */
    @Override
    public int getItemIndex()
    {
        return termIndex;
    }

    /**
     * Returns operand to evaluate axiom selection value for either 1 and 2 dimension cases
     * @return Operand object
     */
    public Operand getAxiomExpression()
    {
        return arrayData != null ? arrayData.getItemExpression() : indexData.getItemExpression();
    }

    /**
     * getItemExpression
     * Returns operand to evaluate term selection value. Applies to 2 dimension case only.
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getItemExpression()
     */
    @Override
    public Operand getItemExpression()
    {
        return indexData.getItemExpression();
    }

    @Override
    public void setSuffix(String suffix)
    {
        indexData.setSuffix(suffix);
    }
    
    /**
     * getSuffix
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getSuffix()
     */
    @Override
    public String getSuffix()
    {
        return indexData.getSuffix();
    }

    /**
     * getVariableName
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getVariableName()
     */
    @Override
    public QualifiedName getVariableName()
    {
        return indexData.getVariableName();
    }

    /**
     * assemble
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#assemble(au.com.cybersearch2.classy_logic.interfaces.ItemList)
     */
    @Override
    public void assemble(ItemList<?> itemList)
    {   // Note arrayData is assumed to not require assembly as it only deals with integer selection values
        indexData.assemble(itemList);
    }

    /**
     * evaluate
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#evaluate(au.com.cybersearch2.classy_logic.interfaces.ItemList, int)
     */
    @Override
    public boolean evaluate(ItemList<?> itemList, int id)
    {
        // The itemList object may be AxiomList or AxiomTermList depending on usage
        this.itemList = itemList;
        axiomTermList = (itemList instanceof AxiomTermList) ? (AxiomTermList)itemList: null;
        boolean isAxiomList = false;
        if (arrayData != null)
        {   // 2 - dimensional if AxiomList passed, otherwise invalidate axiom index
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
        {   // Unwrap AxiomList if it contains only a single item
            // Flag list is axiom array if the list contains more than one item
            // 
            if (axiomTermList == null)
            {   // indexData selects AxiomTermList item 
                AxiomList axiomList = ((AxiomList)itemList);
                isAxiomList = axiomList.getLength() > 1;
                if (isAxiomList)
                    termIndex = -1;
                if (axiomList.isEmpty())
                    // This is not expected. Prevent NPE.
                    axiomTermList = getEmptyAxiomTermList();
                else
                    // The item is only a sample if isAxiomList is true
                    axiomTermList = axiomList.getItem(0);
            }
        }
        // Note: assemble() invoked because referenced axiom may local axiom which changes when scope changes
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
        // Set correct field to select value. For an axiom list, this is axiomIndex.
        if (isAxiomList)
            axiomIndex = indexData.getItemIndex();
        else
            termIndex = indexData.getItemIndex();
        return true;
    }

    /**
     * backup
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate#backup(int)
     */
    public boolean backup(int id) 
    {  
        boolean flag = false;
        if (indexData.getItemExpression() != null)
             flag = indexData.getItemExpression().backup(id);
        if (arrayData != null)
        {
            if (arrayData.getItemExpression() != null)
                flag = arrayData.getItemExpression().backup(id);
        }
        return flag;
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

    /**
     * Returns empty axiom term list
     * @return AxiomTermList object
     */
    private AxiomTermList getEmptyAxiomTermList()
    {
        QualifiedName qname = new QualifiedName(getListName() + "_item", qualifiedListName);
        return new AxiomTermList(qname, qname);
    }

}
