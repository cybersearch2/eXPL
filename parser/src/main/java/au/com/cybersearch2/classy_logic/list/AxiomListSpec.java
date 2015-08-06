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

import au.com.cybersearch2.classy_logic.expression.AxiomOperand;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.expression.Variable;
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
    protected String listName;
    protected AxiomList axiomList;
    protected int axiomIndex;
    protected int termIndex;
    protected Operand axiomExpression;
    protected Operand termExpression;
    protected String suffix;
    protected AxiomOperand axiomListVariable;
    
    public AxiomListSpec(AxiomList axiomList, Operand axiomExpression, Operand termExpression)
    {
        this.axiomList = axiomList;
        this.axiomExpression = axiomExpression;
        this.termExpression = termExpression;
        this.listName = axiomList.getName();
        init();
    }

    public AxiomListSpec(String listName, AxiomOperand axiomListVariable, Operand axiomExpression, Operand termExpression)
    {
        this.listName = listName;
        this.axiomListVariable = axiomListVariable;
        this.axiomExpression = axiomExpression;
        this.termExpression = termExpression;
        init();
    }
    
    public String getListName()
    {
        return listName;
    }

    public void setListName(String listName)
    {
        this.listName = listName;
    }

    public AxiomList getAxiomList()
    {
        return axiomList;
    }

    public void setAxiomList(AxiomList axiomList)
    {
        this.axiomList = axiomList;
    }

    public int getAxiomIndex()
    {
        return axiomIndex;
    }

    public void setAxiomIndex(int axiomIndex)
    {
        this.axiomIndex = axiomIndex;
    }

    public int getTermIndex()
    {
        return termIndex;
    }

    public void setTermIndex(int termIndex)
    {
        this.termIndex = termIndex;
    }

    public Operand getAxiomExpression()
    {
        return axiomExpression;
    }

    public void setAxiomExpression(Operand axiomExpression)
    {
        this.axiomExpression = axiomExpression;
    }

    public Operand getTermExpression()
    {
        return termExpression;
    }

    public void setTermExpression(Operand termExpression)
    {
        this.termExpression = termExpression;
    }

    public String getSuffix()
    {
        return suffix;
    }

    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

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
        else if (termExpression.isEmpty() && (termExpression instanceof Variable))
        {
            suffix = termExpression.getName();
            if (axiomList != null)
                setTermIndex();
        }
        else
            suffix = termExpression.toString();
    }

    public boolean update()
    {
        if ((axiomListVariable == null) || axiomListVariable.isEmpty())
            return false;
        axiomList = (AxiomList)axiomListVariable.getValue();
        if (suffix.equals(termExpression.getName()))
            setTermIndex();
        return true;
    }

    protected void setTermIndex()
    {
        List<String> axiomTermNameList = axiomList.getAxiomTermNameList();
        if (axiomTermNameList != null)
            termIndex = getIndexForName(listName, suffix, axiomTermNameList);
        else
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
