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
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermListManager;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;

/**
 * AxiomTermList
 * List wrapper over Axiom object implementation. Axiom terms are referenced by index using array notation.
 * An AxiomListVariable is created to participate in item expressions. 
 * This object resides in an OperandMap never directly interacts with other operands.
 * @author Andrew Bowley
 * 19 Jan 2015
 */
public class AxiomTermList implements ItemList<Term>, AxiomContainer 
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
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;
    /** Archetype identity used to detect axiom specification changes */
    protected String archetypeName;
    /** Flag set true if list is exported */
    protected boolean isPublic;

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
                TermListManager axiomArchetype = axiom.getArchetype();
                if ((archetypeName == null) || 
                    !axiomArchetype.toString().equals(archetypeName) ||
                    ((axiomTermNameList != null) && 
                     (axiom.getTermCount() > axiomTermNameList.size())))
                {
                    axiomTermNameList = axiom.getArchetype().getTermNameList();
                    archetypeName = axiomArchetype.toString();
                }
		        if (sourceItem != null)
		            sourceItem.setInformation(AxiomTermList.this.toString());
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
    @Override
	public List<String> getAxiomTermNameList() 
	{
        if (axiomTermNameList == null)
        {
            if (!isEmpty())
                axiomTermNameList = axiom.getArchetype().getTermNameList();
            else
                return Collections.emptyList();
        }
		return axiomTermNameList;
	}

	/**
	 * @param axiomTermNameList the axiomTermNameList to set
	 */
    @Override
	public void setAxiomTermNameList(List<String> axiomTermNameList) 
	{
		this.axiomTermNameList = axiomTermNameList;
	}

	/**
	 * Returns axiom listener
	 * @return AxiomListener object
	 */
    @Override
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
        TermListManager archetype = axiom.getArchetype();
        axiomTermNameList = archetype.getTermNameList();
        archetypeName = archetype.toString();
		if (sourceItem != null)
		    sourceItem.setInformation(toString());
	}

    /**
     * clear
     * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#clear()
     */
    @Override
    public void clear() 
    {
        axiom = EMPTY_AXIOM;
        if (sourceItem != null)
            sourceItem.setInformation(toString());
    }

	/**
	 * getLength
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getLength()
	 */
	@Override
	public int getLength() 
	{
		return axiom.getTermCount();
	}

	/**
	 * getQualifiedName
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getQualifiedName()
	 */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }


	/**
	 * getName
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getName()
	 */
	@Override
	public String getName() 
	{
		return qname.getName();
	}

	/**
	 * isEmpty
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#isEmpty()
	 */
	@Override
	public boolean isEmpty() 
	{
		return (axiom.getTermCount() == 0);
	}

	/**
	 * assignItem
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#assignItem(int, java.lang.Object)
	 */
	@Override
	public void assignItem(int index, Term term) 
	{   
	    if (axiom.getTermCount() == 0)
	    {
	        if (index == 0)
	        {
	            AxiomArchetype archetype = null;
	            if (key.getName().isEmpty())
                    archetype = new AxiomArchetype(new QualifiedName(key.getScope(), key.getTemplate()));
	            else
	                archetype = new AxiomArchetype(key);
	            axiomListener.onNextAxiom(archetype.getQualifiedName(), new Axiom(archetype));
	            axiom.addTerm(term);
	        }
	        else
	            verify(index);
	    }
	    else if (index == axiom.getTermCount())
            axiom.addTerm(term);
	    else
	    {
	        verify(index);
	        axiom.getTermByIndex(index).setValue(term.getValue());
	    }
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
	 * hasItem
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#hasItem(int)
	 */
	@Override
	public boolean hasItem(int index) 
	{
		return (index >= 0) && (index < axiom.getTermCount());
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
	 * iterator
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Term> iterator() 
	{
		return getIterable().iterator();
	}

	/**
	 * getIterable
	 * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#getIterable()
	 */
	@Override
	public Iterable<Term> getIterable() 
	{
		// Use copy of axiom in case clear() is called
        final Axiom axiom2 = new Axiom(axiom.getName());
        for (int i = 0; i < axiom.getTermCount(); i++)
        	axiom2.addTerm(axiom.getTermByIndex(i));
		return new Iterable<Term>()
		{
			@Override
			public Iterator<Term> iterator() 
			{
				return new Iterator<Term>()
				{
		            int index = 0;
		            
					@Override
					public boolean hasNext() 
					{
						return index < axiom2.getTermCount();
					}
		
					@Override
					public Term next() 
					{
						return axiom2.getTermByIndex(index++);
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
     * @return public flag
     */
    @Override
    public boolean isPublic()
    {
        return isPublic;
    }

    /**
     * @param isPublic Public flag
     */
    @Override
    public void setPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    /**
     * setSourceItem
     * @see au.com.cybersearch2.classy_logic.interfaces.SourceInfo#setSourceItem(au.com.cybersearch2.classy_logic.compile.SourceItem)
     */
    @Override
    public void setSourceItem(SourceItem sourceItem)
    {
        this.sourceItem = sourceItem;
    }

    /**
     * toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() 
    {
        StringBuilder builder = new StringBuilder("list<term> ");
        builder.append(qname.toString()).append('(');
        if (!qname.equals(key))
            builder.append(key.toString());
        builder.append(')');
        if (!axiom.equals(EMPTY_AXIOM))
            builder.append('{').append(axiom.getTermCount()).append('}');
        return builder.toString();
    }

    @Override
    public OperandType getOperandType()
    {
        return OperandType.TERM;
    }

    @Override
    public Term[] toArray()
    {
        Term[] array = new Term[axiom.getTermCount()];
        for (int i = 0; i < axiom.getTermCount(); ++i)
            array[i] = axiom.getTermByIndex(i);
        return array;
    }
}
