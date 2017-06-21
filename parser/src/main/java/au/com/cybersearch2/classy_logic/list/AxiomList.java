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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.TermListIterable;
import au.com.cybersearch2.classy_logic.interfaces.TermListManager;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomList
 * List of AxiomTermList-wrapped Axions 
 * @author Andrew Bowley
 * 28 Jan 2015
 */
public class AxiomList extends ArrayItemList<AxiomTermList> implements AxiomContainer, TermListIterable 
{
    /** Axiom listener is notified of axiom to add to list */
	protected AxiomListener axiomListener;
	/** Axiom term names */
	protected List<String> axiomTermNameList;
    /** Axiom key */
    protected QualifiedName key;
    /** Archetype identity used to detect axiom specification changes */
    protected String archetypeName;

	/**
	 * Construct an AxiomList object
	 * @param qname Name of axiom list
	 * @param key Axiom key, may be same as name of list
	 */
	public AxiomList(QualifiedName qname, QualifiedName key) 
	{
		super(OperandType.AXIOM, qname);
		this.key = key;
	}

	/**
	 * Concatenate given axiom list to this list
	 * @param rightList The list to add, which is on the right in a concatenation expression
	 * @return this list post concatenation
	 */
    public AxiomList concatenate(AxiomList rightList)
    {
        if (rightList.isEmpty())
            // No items to add
            return this;
        if (isEmpty())
            // Adding to empty list, so set axiom key to same as on right 
            setKey(rightList.getKey());
        else
        {   // Check that left and right lists are compatible, which is true if tboth share them same archetype 
            getArchetypeName();
            if ((archetypeName == null) || !archetypeName.equals(rightList.getArchetypeName()))
                // When archetypes cannot be compared or are different, fall back to ensuring term names align
                checkTermNameCongruence(rightList);
        }
        // Update this list and return 
        Iterator<AxiomTermList> iterator = rightList.getIterable().iterator();
        int index = getLength();
        while (iterator.hasNext())
            assignItem(index++, iterator.next());
        return this;
    }
 
    /**
     * Concatenate given axiom term list to this list
     * @param rightList The axiom term list to add, which is on the right in a concatenation expression
     * @return this list post concatenation
     */
    public AxiomList concatenate(AxiomTermList axiomTermList)
    {
        if (axiomTermList.isEmpty())
            // Do not add empty axiom term list
            return this;
        if (!isEmpty())
        {
            // Check that left and right lists are compatible, which is true if tboth share them same archetype 
            getArchetypeName();
            if ((archetypeName == null) || !archetypeName.equals(axiomTermList.archetypeName))
                // When archetypes cannot be compared or are different, fall back to ensuring term names align
                checkTermNameCongruence(axiomTermList);
        }
        // Update this list and return 
        int index = getLength();
            assignItem(index, axiomTermList);
        return this;
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
				TermListManager axiomArchetype = axiom.getArchetype();
				// Set term name list if out of sync 
				if ((archetypeName == null) || 
				     !axiomArchetype.toString().equals(archetypeName) ||
	                 ((axiomTermNameList != null) && 
	                  (axiom.getTermCount() > axiomTermNameList.size())))
				{
				    axiomTermNameList = axiom.getArchetype().getTermNameList();
				    archetypeName = axiomArchetype.toString();
				}
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
        if (axiomTermNameList == null)
        {
            if (getLength() > 0)
                axiomTermNameList = getItem(0).axiomTermNameList;
            else
                return Collections.emptyList();
        }
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

	/**
	 * getOperandType
	 * @see au.com.cybersearch2.classy_logic.list.ArrayItemList#getOperandType()
	 */
    @Override
    public OperandType getOperandType()
    {
        return OperandType.AXIOM;
    }

    /**
     * Ensure term names align
     * @param axiomContainer List with which to compare term names
     * @throws ExpressionException if term names do not match 
     */
    protected void checkTermNameCongruence(AxiomContainer axiomContainer)
    {
        List<String> rightNames = axiomContainer.getAxiomTermNameList();
        int size = getAxiomTermNameList().size();
        boolean isCongruent = size == rightNames.size();
        if (isCongruent)
            for (int i = 0; i < axiomTermNameList.size(); i++)
            {
                if (!axiomTermNameList.get(i).equals(rightNames.get(i)))
                {
                    isCongruent = false;
                    break;
                }
            }
        if (!isCongruent)
            throw new ExpressionException("Cannot concatenate " + toString() + " to " + axiomContainer.toString());
    }

    /**
     * Returns current archetype name
     * @return name of the first item's archetype or empty string if list is empty
     */
    protected String getArchetypeName()
    {
        if (archetypeName == null)
        {
            if (getLength() > 0)
                archetypeName = getItem(0).archetypeName;
            else
                return "";
        }
        return archetypeName;
    }
}
