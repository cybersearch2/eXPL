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

import java.util.List;

import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomList
 * @author Andrew Bowley
 * 28 Jan 2015
 */
public class AxiomList extends ArrayItemList<AxiomTermList> 
{
    /** Axiom listener is notified of axiom to add to list */
	protected AxiomListener axiomListener;
	/** Axiom term names */
	protected List<String> axiomTermNameList;
    /** Axiom key */
    protected String key;

	/**
	 * Construct an AxiomList object
	 * @param name Name of axiom list
	 */
	public AxiomList(String name, String key) 
	{
		super(AxiomTermList.class, new Variable(name));
		this.key = key;
	}

    /**
     * Returns list variable to access axiom and term using fixed index and evaluated index respectively
     * @param axiomIndex int
     * @param termExpression Operand which evaluates the axiom term list index value
	 * @param suffix The axiom term identity
     * @return ItemListVariable 
     */
    public AxiomListVariable newVariableInstance(int axiomIndex, Operand termExpression, String suffix)
    {
    	AxiomListVariable axiomListVariable = new AxiomListVariable(this, axiomIndex, suffix);
    	axiomListVariable.setTermExpression(termExpression);
    	return axiomListVariable;
    }

    /**
     * Returns list variable to access axiom and term using 
     * @param axiomExpression Operand which evaluates the axiom list index value
     * @param termIndex int
	 * @param suffix The axiom term identity
     * @return ItemListVariable   
     */
    public AxiomListVariable newVariableInstance(Operand axiomExpression, int termIndex, String suffix)
    {
    	AxiomListVariable axiomListVariable = new AxiomListVariable(this, axiomExpression, suffix);
    	axiomListVariable.setTermIndex(termIndex);
    	return axiomListVariable;
    }

    /**
     * Returns list variable to access axiom and term using 
     * @param axiomIndex int
     * @param termIndex int
	 * @param suffix The axiom term identity
     * @return ItemListVariable 
     */
    public AxiomListVariable newVariableInstance(int axiomIndex, int termIndex, String suffix)
    {
    	AxiomListVariable axiomListVariable = new AxiomListVariable(this, axiomIndex, suffix);
    	axiomListVariable.setTermIndex(termIndex);
    	return axiomListVariable;
    }

    /**
     * Returns list variable to access axiom and term using 
     * @param axiomExpression Operand which evaluates the axiom list index value
     * @param termExpression Operand which evaluates the axiom term list index val
	 * @param suffix The axiom term identity
     * @return ItemListVariable   
     */
    public AxiomListVariable newVariableInstance(Operand axiomExpression, Operand termExpression, String suffix)
    {
    	AxiomListVariable axiomListVariable = new AxiomListVariable(this, axiomExpression, suffix);
    	axiomListVariable.setTermExpression(termExpression);
    	return axiomListVariable;
    }

    /**
     * Returns listener to add Axiom objects to this container
     * @return AxiomListener
     */
	public AxiomListener getAxiomListener()
	{
		return new AxiomListener(){

			@Override
			public void onNextAxiom(Axiom axiom) 
			{
				AxiomTermList axiomListOperand = new AxiomTermList(getName(), axiom.getName());
				axiomListOperand.setAxiom(axiom);
				assignItem(getLength(), axiomListOperand);
			}};
	}

	/**
	 * Returns Axiom key
	 * @return String
	 */
	public String getKey()
	{
		return key;
	}

	/**
	 * @return the axiomTermNameList
	 */
	public List<String> getAxiomTermNameList() 
	{
		return axiomTermNameList;
	}

	/**
	 * @param axiomTermNameList the axiomTermNameList to set
	 */
	public void setAxiomTermNameList(List<String> axiomTermNameList) 
	{
		this.axiomTermNameList = axiomTermNameList;
	}

    /**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		boolean firstTime = true;
		for (int i = 0; i < getLength(); ++i)
		{
			if (firstTime)
				firstTime = false;
			else
				builder.append(System.getProperty("line.separator"));
			builder.append(getItem(i).toString());
		}
		return builder.toString();
	}
}
