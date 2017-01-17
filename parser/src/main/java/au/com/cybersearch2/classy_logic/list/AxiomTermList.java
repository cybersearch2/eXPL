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

import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomTermList
 * List wrapper over Axiom object implementation. Axiom terms are referenced by index using array notation.
 * An AxiomListVariable is created to participate in item expressions. 
 * This object resides in an OperandMap never directly interacts with other operands.
 * @author Andrew Bowley
 * 19 Jan 2015
 */
public class AxiomTermList implements ItemList<Object> 
{
	/** Qualified name of list */
	protected QualifiedName qname;
    /** Axiom key */
    protected QualifiedName key;
    /** The Axiom being wrapped */
    protected Axiom axiom;
    /** Axiom listener is notified of axiom received each iteration */
	protected AxiomListener axiomListener;
	/** Axiom term names */
	protected List<String> axiomTermNameList;

	/** Empty Axiom constant */
	static Axiom EMPTY_AXIOM;

	static
	{
		EMPTY_AXIOM = new Axiom("*");
	}
	
	/**
	 * Construct an AxiomTermList object. The initial axiom is empty until axiomListener is notified.
	 * @param qname Qualified name of list
	 * @param key Qualified name of Axiom
	 */
	public AxiomTermList(QualifiedName qname, QualifiedName key) 
	{
		this.qname = qname;
		this.key = key;
		axiom = EMPTY_AXIOM;
		axiomListener = new AxiomListener(){

			@Override
			public void onNextAxiom(QualifiedName qname, Axiom nextAxiom) 
			{
				axiom = nextAxiom;
			}};
	}

	/**
	 * Returns Axiom key
	 * @return String
	 */
	public QualifiedName getKey()
	{
		return key;
	}

	/**
	 * Returns backing axiom
	 * @return Axiom object
	 */
	public Axiom getAxiom()
	{
	    return axiom;
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
	 * Returns axiom listener
	 * @return AxiomListener object
	 */
	public AxiomListener getAxiomListener()
	{
		return axiomListener;
	}

	/**
	 * Sets axiom. An alternative to receiving it by axiom listener.
	 * @param axiom Axiom object
	 */
	public void setAxiom(Axiom axiom)
	{
		this.axiom = axiom;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getLength()
	 */
	@Override
	public int getLength() 
	{
		return axiom.getTermCount();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getQualifiedName()
	 */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }


	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getName()
	 */
	@Override
	public String getName() 
	{
		return qname.getName();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#isEmpty()
	 */
	@Override
	public boolean isEmpty() 
	{
		return (axiom.getTermCount() == 0);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#assignItem(int, java.lang.Object)
	 */
	@Override
	public void assignItem(int index, Object value) 
	{   
		verify(index);
		axiom.getTermByIndex(index).setValue(value);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(int, java.lang.String, int)
	 */
	@Override
	public ItemListVariable<Object> newVariableInstance(int index, String suffix, int id) 
	{
		return AxiomUtils.newVariableInstance(this, index, suffix, id);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#newVariableInstance(au.com.cybersearch2.classy_logic.interfaces.Operand, java.lang.String, int)
	 */
	@Override
	public ItemListVariable<Object> newVariableInstance(Operand expression, String suffix, int id) 
	{
		// Assign a value to set the delegate must be delayed until the expression is evaluated
		return AxiomUtils.newVariableInstance(this, expression, suffix, id);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getItem(int)
	 */
	@Override
	public Term getItem(int index) 
	{
		return axiom.getTermByIndex(index);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#hasItem(int)
	 */
	@Override
	public boolean hasItem(int index) 
	{
		return (index >= 0) && (index < axiom.getTermCount());
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return axiom.toString();
	}

	/**
	 * Verify axiom has been assigned to this list and index is in bounds
	 */
	public void verify(int index) 
	{
		if (index >= axiom.getTermCount() || (index < 0))
			throw new IllegalStateException("AxiomTermList \"" + qname.toString() +"\" index " + index + " out of bounds");
	}

	/**
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Object> iterator() 
	{
		return getIterable().iterator();
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getIterable()
	 */
	@Override
	public Iterable<Object> getIterable() 
	{
		// Use copy of axiom in case clear() is called
        final Axiom axiom2 = new Axiom(axiom.getName());
        for (int i = 0; i < axiom.getTermCount(); i++)
        	axiom2.addTerm(axiom.getTermByIndex(i));
		return new Iterable<Object>()
		{
			@Override
			public Iterator<Object> iterator() 
			{
				return new Iterator<Object>()
				{
		            int index = 0;
		            
					@Override
					public boolean hasNext() 
					{
						return index < axiom2.getTermCount();
					}
		
					@Override
					public Object next() 
					{
						return axiom2.getTermByIndex(index++).getValue();
					}
		
					@Override
					public void remove() 
					{
					}
				};
			}
		};
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#clear()
	 */
	@Override
	public void clear() 
	{
		axiom = EMPTY_AXIOM;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getItemClass()
	 */
    @Override
    public Class<?> getItemClass()
    {
        return Term.class;
    }

}
