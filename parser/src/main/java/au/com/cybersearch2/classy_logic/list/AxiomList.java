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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomList
 * @author Andrew Bowley
 * 28 Jan 2015
 */
public class AxiomList extends ArrayItemList<AxiomTermList> implements AxiomContainer 
{
    /** Axiom listener is notified of axiom to add to list */
	protected AxiomListener axiomListener;
	/** Axiom term names */
	protected List<String> axiomTermNameList;
    /** Axiom key */
    protected QualifiedName key;

	/**
	 * Construct an AxiomList object
	 * @param qname Name of axiom list
	 * @param key Axiom key
	 */
	public AxiomList(QualifiedName qname, QualifiedName key) 
	{
		super(AxiomTermList.class, qname);
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
    	axiomListVariable.setTermExpression(termExpression, 0);
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
    	axiomListVariable.setTermIndex(termIndex, 0);
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
    	axiomListVariable.setTermIndex(termIndex, 0);
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
    	axiomListVariable.setTermExpression(termExpression, 0);
    	return axiomListVariable;
    }

    /**
     * Returns listener to add Axiom objects to this container
     * @return AxiomListener
     */
    @Override
	public AxiomListener getAxiomListener()
	{
		return new AxiomListener(){

			@Override
			public void onNextAxiom(QualifiedName qname, Axiom axiom) 
			{
				AxiomTermList axiomListOperand = new AxiomTermList(getQualifiedName(), qname);
				axiomListOperand.setAxiom(axiom);
				assignItem(getLength(), axiomListOperand);
			}};
	}

	/**
	 * Returns Axiom key
	 * @return String
	 */
	@Override
	public QualifiedName getKey()
	{
		return key;
	}

	/**
	 * Allow key to be updated when set to list qname
	 * @param key Qualified name of axioms in list
	 * @return flag set true if name changed
	 */
	public boolean setKey(QualifiedName key)
	{
	    if (this.key.equals(getQualifiedName()))
	    {
	        this.key = key;
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Returns axiom term name list
	 * @return List of axiom term names
	 */
    @Override
	public List<String> getAxiomTermNameList() 
	{
		return axiomTermNameList;
	}

	/**
	 * Set  axiom term name list
	 * @param axiomTermNameList The axiomTermNameList to set
	 */
    @Override
	public void setAxiomTermNameList(List<String> axiomTermNameList) 
	{
		this.axiomTermNameList = axiomTermNameList;
	}

	/**
	 * getItemClass
	 * @see au.com.cybersearch2.classy_logic.list.ArrayItemList#getItemClass()
	 */
    @Override
    public Class<?> getItemClass()
    {
        return Axiom.class;
    }

    /**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("list<axiom> ");
		builder.append(getQualifiedName().toString());
		if (!getQualifiedName().equals(key))
		    builder.append('(').append(key.toString()).append(')');
		if (getLength() > 0)
		{
		    if (getLength() == 1)
		        builder.append(": ").append(getItem(0).toString());
		    else
		        builder.append('[').append(Integer.toString(getLength())).append(']');
		}
		return builder.toString();
	}

    @Override
    public OperandType getOperandType()
    {
        return OperandType.AXIOM;
    }
}
