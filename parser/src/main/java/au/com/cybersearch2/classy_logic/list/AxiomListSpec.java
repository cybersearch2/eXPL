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

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * AxiomListSpec
 * Specification of an AxiomList variable which allows for dynamic assignment
 * of the backing AxiomList
 * @author Andrew Bowley
 * 4 Aug 2015
 */
public class AxiomListSpec
{
    /** Name of axiom list */
    protected String listName;
    /** Backing axiom list */
    protected AxiomList axiomList;
    /** Axiom selection index. Value of -1 means not used */
    protected int axiomIndex;
    /** Term selection index. Value of -1 means not used */
    protected int termIndex;
    /** Compiler operand for axiom selection */
    protected Operand axiomExpression;
    /** Compiler operand for term selection */
    protected Operand termExpression;
    /** Text to append to name of variable */
    protected String suffix;
    /** Compiler operand to supply AxiomList object on evaluation */
    protected Operand axiomListVariable;
 
    /**
     * Construct AxiomListSpec object for case backing AxiomList is available
     * @param axiomList Backing axiom list
     * @param axiomExpression Compiler operand for axiom selection
     * @param termExpression Compiler operand for term selection
     */
    public AxiomListSpec(AxiomList axiomList, Operand axiomExpression, Operand termExpression)
    {
        this.axiomList = axiomList;
        this.axiomExpression = axiomExpression;
        this.termExpression = termExpression;
        this.listName = axiomList.getName();
        init();
    }

    /**
    * Construct AxiomListSpec object for case backing AxiomList needs to be evaluated
     * @param listName Name of axiom list
     * @param axiomListVariable Compiler operand to supply AxiomList object on evaluation
     * @param axiomExpression Compiler operand for axiom selection
     * @param termExpression Compiler operand for term selection
     */
    public AxiomListSpec(String listName, Operand axiomListVariable, Operand axiomExpression, Operand termExpression)
    {
        this.listName = listName;
        this.axiomListVariable = axiomListVariable;
        this.axiomExpression = axiomExpression;
        this.termExpression = termExpression;
        init();
    }
 
    /**
     * Returns name of axiom list
     * @return String
     */
    public String getListName()
    {
        return listName;
    }

    /**
     * Sets name of axiom list
     * @param listName
     */
    public void setListName(String listName)
    {
        this.listName = listName;
    }

    /**
     * Returns backing axiom list
     * @return AxiomList object or null if waiting for evaluation
     */
    public AxiomList getAxiomList()
    {
        return axiomList;
    }

    /**
     * Sets backing axiom list
     * @param axiomList AxionList object
     */
    public void setAxiomList(AxiomList axiomList)
    {
        this.axiomList = axiomList;
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
     * Returns term selection index
     * @return Valid index or -1 if not used
     */
    public int getTermIndex()
    {
        return termIndex;
    }

    /**
     * Returns Compiler operand for axiom selection
     * @return Operand object
     */
    public Operand getAxiomExpression()
    {
        return axiomExpression;
    }

    /**
     * Returns Compiler operand for term selection
     * @return Operand object
     */
    public Operand getTermExpression()
    {
        return termExpression;
    }

    /**
     * Returns Text to append to name of variable
     * @return String
     */
    public String getSuffix()
    {
        return suffix;
    }

    /**
     * Initialize this object. Determines if index values are available or need to be evaluated.
     */
    protected void init()
    {
        axiomIndex = -1;
        termIndex = -1;
        // Check for non-empty Integer operand, which is used for a fixed index
        if (!axiomExpression.isEmpty() && (axiomExpression instanceof IntegerOperand && Term.ANONYMOUS.equals(axiomExpression.getName())))
            axiomIndex = ((Long)(axiomExpression.getValue())).intValue();
        if (!termExpression.isEmpty() && (termExpression instanceof IntegerOperand) && Term.ANONYMOUS.equals(termExpression.getName()))
        {
            termIndex = ((Long)(termExpression.getValue())).intValue();
            suffix = Integer.toString(termIndex);
        }
        else if (termExpression.isEmpty())/* && (termExpression instanceof Variable))*/
        {
            suffix = termExpression.getName();
            if (axiomList != null)
                setTermIndex();
        }
        else
            suffix = termExpression.toString();
    }

    /**
     * Complete any initialization dependent on completion of evaluation
     * @return Flag set true if update occurred
     */
    public boolean update()
    {
        if ((axiomListVariable == null) || axiomListVariable.isEmpty())
            return false;
        axiomList = (AxiomList)axiomListVariable.getValue();
        if (suffix.equals(termExpression.getName()))
            setTermIndex();
        return true;
    }

    /**
     * Set term index for case it is specifed by name. 
     * Requires axiom term name list to be available. 
     */
    protected void setTermIndex()
    {
        List<String> axiomTermNameList = axiomList.getAxiomTermNameList();
        if (axiomTermNameList != null)
            termIndex = getIndexForName(listName, suffix, axiomTermNameList);
        else // Note the following has no effect when called from update()
            suffix = termExpression.toString();
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
