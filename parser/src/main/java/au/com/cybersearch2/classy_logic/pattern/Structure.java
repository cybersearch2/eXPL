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
package au.com.cybersearch2.classy_logic.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * Structure
 * Holds the terms to be paired in pattern matching by unification.
 * The Axiom subclass holds the data for unification and the
 * Template subclass holds the variables to be paired with the data.
 * 
 * @author Andrew Bowley
 * @since 28/09/2010
 * @see Axiom
 */
public class Structure
{
	protected static final String NAME_INVALID_MESSAGE = "Parameter \"name\" is null or empty";
	protected static final String TERMS_NULL_MESSAGE = "Parameter \"terms\" is null";
    public static List<Term> EMPTY_TERM_LIST;

    static
    {
        EMPTY_TERM_LIST = Collections.emptyList();
    }
    
	/** The Structure name is required for unification */
    protected String name;
    /** Terms to be paired on unification by position. */
	protected List<Term> termList;
    /** Terms to be paired on unification by name. */
	protected Map<String, Term> termMap;

	/**
	 * Create Structure as super class. 
	 * One or more Terms must be added to put this object into a valid state.
	 *  
	 * @param name String
	 */
	protected Structure(String name)
	{
		if ((name == null) || name.isEmpty())
		    throw new IllegalArgumentException(NAME_INVALID_MESSAGE);
		this.name = name;
		termList = EMPTY_TERM_LIST;
	}
	
	/**
	 * Construct a named Structure object with terms containing values
	 * @param name String
	 * @param terms Object array. Nulls allowed.
	 */
	public Structure(String name, Object...terms)
	{
		this(name);
		if (terms == null)
		    throw new IllegalArgumentException(TERMS_NULL_MESSAGE);
		setTerms(terms);
	}

	/**
	 * Construct a named Structure object with terms
	 * In Prolog this is known as an Named Functor
	 * @param name String
	 * @param terms Term array
	 */
	public Structure(String name, Term... terms)
	{
		this(name);
		if (terms == null)
		    throw new IllegalArgumentException(TERMS_NULL_MESSAGE);
		setTerms(terms);
	}

	/**
	 * Construct a non-empty named Structure object with terms
	 * In Prolog this is known as an Named Functor
	 * @param name String
	 * @param terms Term list
	 */
	public Structure(String name, List<Term> terms)
	{
		this(name);
		if (terms == null)
		    throw new IllegalArgumentException(TERMS_NULL_MESSAGE);
		setTerms(terms);
	}

    /**
     * Returns the name of the Structure	
     * @return String
     */
	public String getName() 
	{
		return name;
	}

	/**
	 * Returns true if all the Terms of this Structure contain a value or there are no Terms.
	 * @return boolean
	 */
	public boolean isFact()
	{
		for (Term param: termList)
			if (param.isEmpty())
				return false;
		return true;
	}

	/**
	 * Returns number of terms in this object
	 * @return 0 or greater
	 */
	public int getTermCount()
	{
		return termList.size();
	}
	
	/**
	 * Returns display text of name and terms
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(name);
		if (!termList.isEmpty())
		{
			builder.append('(');
			boolean firstTime = true;
			for (Term param: termList)
			{
				if (firstTime)
					firstTime = false;
				else
					builder.append(", ");
				builder.append(param.toString());
			}
			builder.append(')');
		}
		else
			builder.append("()");
		return builder.toString();
	}

	/**
	 * Returns Term referenced by name
	 * @param name String
	 * @return Term object or null if not found
	 */
    public Term getTermByName(String name)
    {
		Term result = null;
    	if (termMap == null)
    		return result;
    	if (name == null)
    		throw new IllegalArgumentException("Parameter \"name\" is null");
	    return termMap.get(name.toUpperCase());
    }

    /**
     * Returns Term referenced by index
     * @param index Valid index value
     * @return Term object or null if index out of range
     */
    public Term getTermByIndex(int index)
    {
    	if (termList.isEmpty() || (index >= termList.size()) ||( index < 0))
    		return null;
    	return termList.get(index);
    }
    
	/**
	 * Set Terms from supplied Term list
	 * @param terms2 Term list
	 */
	protected void setTerms(List<Term> terms2) 
	{
		if (terms2.size() > 0)
		{
			this.termList = new ArrayList<Term>();
			for (Term param: terms2)
				addTerm(param);
		}
	}

	/**
	 * Set Terms from supplied Term array
	 * @param terms2 Term array
	 */
	protected void setTerms(Term[] terms2) 
	{
		if (terms2.length > 0)
		{
			this.termList = new ArrayList<Term>();
			for (Term param: terms2)
				addTerm(param);
		}
	}

	/**
	 * Set Anonymous Terms using values from supplied Object array.
	 * Any Object in the array not of type Term id converted to
	 * an anonymous Term with value set to the object. 
	 * @param data Object array of values
	 */
	protected void setTerms(Object[] data) 
	{
		if (data.length > 0)
		{
			this.termList = new ArrayList<Term>();
			for (Object datum: data)
				if (datum instanceof Term)
					addTerm((Term) datum);
				else
					addTerm(new Parameter(Term.ANONYMOUS, datum));
		}
	}

	/**
	 * Add a parameter
	 * @param param Term object
	 */
	public void addTerm(Term param)
	{
		if (termList.isEmpty()) // First time after name-only constructor invoked
			termList = new ArrayList<Term>();
		termList.add(param);
		// If the parameter is named, add it to the term map as well
		if (termMap == null)
			termMap = new HashMap<String, Term>();
		OperandWalker operandWalker = new OperandWalker(param);
		OperandVisitor visitor = new OperandVisitor()
		{

			@Override
			public boolean next(Term term, int depth) 
			{
				if (!Term.ANONYMOUS.equals(term.getName()))
					termMap.put(term.getName().toUpperCase(), term);
				return true;
			}
		};
		operandWalker.visitAllNodes(visitor);
	}
}
