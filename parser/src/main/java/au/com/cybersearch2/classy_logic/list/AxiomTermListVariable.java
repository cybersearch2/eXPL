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

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomListVariable
 * Variable to reference an axiom in an axiom list by index or expression.
 * The class of an item can be any valid class for a term.
 * @author Andrew Bowley
 * 19 Jan 2015
 */
public class AxiomTermListVariable extends ItemListVariable<Object> implements Concaten<String>
{
    protected AxiomTermList axiomTermList;
    
	/**
	 * Construct an AxiomTermListVariable instance
	 * @param axiomTermList The axiom term list being referenced
	 * @param proxy Operand object to perform evaluation
	 * @param index Position of term in axiom
	 * @param suffix To append to name
     * @param id Id of owner to be assigned to variable
	 */
	public AxiomTermListVariable(AxiomTermList axiomTermList, Operand proxy, int index, String suffix, int id) 
	{
		super(axiomTermList, proxy, index, suffix);
		this.axiomTermList = axiomTermList;
		this.id = id;
	}

	/**
	 * Construct an AxiomTermListVariable instance
	 * @param axiomTermList The axiom term list being referenced
	 * @param proxy Operand object to perform evaluation
	 * @param indexExpression Operand object to evaluate position of term in axiom
	 * @param suffix To append to name
     * @param id Id of owner to be assigned to variable
	 */
	public AxiomTermListVariable(AxiomTermList axiomTermList, Operand proxy,
			Operand indexExpression, String suffix, int id) 
	{
		super(axiomTermList, proxy, indexExpression, suffix);
        this.axiomTermList = axiomTermList;
		this.id = id;
	}
	
	/**
	 * Set value from term in backing axiom	at specified position and update proxy with this term if 
	 * class of value has changed.
	 * @see au.com.cybersearch2.classy_logic.list.ItemListVariable#onIndexSet(int)
	 */
	@Override
	protected void onIndexSet(int index)
	{
		setValue(index);
		Term term = (Term) itemList.getItem(index);
     	if (proxy.isEmpty() || (term.getValueClass() != proxy.getValueClass()))
     		// Assign a value to set the proxy delegate
    		proxy.setValue(term.getValue());
	}

	/**
	 * Set value from term in backing axiom	at specified position  
	 * @see au.com.cybersearch2.classy_logic.list.ItemListVariable#setValue(int)
	 */
	@Override
	protected void setValue(int index)
	{
		if ((index < 0) || (index >= itemList.getLength()))
			throw new ExpressionException("\"" + getName() +"\" index " + index + " out of bounds");
		Term term = (Term) itemList.getItem(index);
		setValue(term.getValue());
	}

	/**
	 * Returns value of term at current index. Retrieves term from backing axiom in case the value has been updated.
	 * @see au.com.cybersearch2.classy_logic.list.ItemListVariable#getItemValue()
	 */
	@Override
	protected Object getItemValue()
	{
		Object oldValue = value;
		Term term = (Term) itemList.getItem(index);
		Object itemValue = term.getValue();
		if (!itemValue.equals(oldValue))
			setValue(itemValue);

	    return itemValue;
	}

    /**
     * Returns index for item referenced by name. Default return value is -1 for no match. 
     * @param itemName
     * @return Index of item or -1 for no match or feature not supported
     */
    @Override
    protected int getIndexForName(String itemName)
    {
        Axiom axiom = axiomTermList.getAxiom();
        if (axiomTermList.getAxiomTermNameList() != null)
        {
            for (String termName: axiomTermList.getAxiomTermNameList())
                if (termName.equals(itemName))
                    return index;
        }
        else if (axiom != null)
        {
            for (int index = 0; index < axiom.getTermCount(); ++index)
                if (axiom.getTermByIndex(index).getName().equals(itemName))
                    return index;
        }
        return -1;
    }


	/**
	 * concatenate
	 * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
	 */
    @Override
    public String concatenate(Operand rightOperand)
    {
        return getItemValue().toString() + rightOperand.getValue().toString();
    }

}
