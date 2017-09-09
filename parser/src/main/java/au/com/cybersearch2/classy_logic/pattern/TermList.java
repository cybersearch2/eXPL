/**
    Copyright (C) 2017  www.cybersearch2.com.au

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermListManager;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * TermList
 * @author Andrew Bowley
 * 2May,2017
 */
public abstract class TermList <T extends Term> implements Serializable
{
    private static final long serialVersionUID = -8039301398667216325L;
    
    /** Defines structure features shared by instances with same identity */
    protected TermListManager archetype;
    /** Term list */
    transient protected List<T> termList;
    /** Number of terms in term list */
    transient protected int termCount;
    
    public TermList(TermListManager archetype)
    {
        if (archetype == null)
            throw new IllegalArgumentException("Parameter \"archetype\" is null");
        this.archetype = archetype;
        termList = new ArrayList<T>(archetype.isMutable() ? 10 : archetype.getTermCount());
    }

    /**
     * @return the archetype
     */
    public TermListManager getArchetype()
    {
        return archetype;
    }

    /**
     * Returns the name of the Structure    
     * @return String
     */
    public String getName() 
    {
        return archetype.getName();
    }

    /**
     * Add Term
     * @param term Term object
     */
    public void addTerm(T term)
    {
        TermMetaData termMetaData = archetype.analyseTerm(term, termCount);
        if (archetype.isMutable())
            archetype.addTerm(termMetaData);
        else
            archetype.checkTerm(termMetaData);
        termList.add(term);
        ++termCount;
    }
    
    /**
     * Returns true if all the Terms of this Structure contain a value or there are no Terms.
     * @return boolean
     */
    public boolean isFact()
    {
        if (termList.isEmpty())
            return false;
        for (Term param: termList)
            if (param.isEmpty() || (param.getValueClass() == Unknown.class))
                return false;
        return true;
    }

    /**
     * Returns number of terms in this object
     * @return 0 or greater
     */
    public int getTermCount()
    {
        return termCount;
    }
    
    /**
     * Returns Term referenced by name
     * @param name String
     * @return Term object or null if not found
     */
    public T getTermByName(String name)
    {
       if (name == null)
            throw new IllegalArgumentException("Parameter \"name\" is null");
       int index = archetype.getIndexForName(name);
       if (index == -1)
            return null;
        return getTermByIndex(index);
    }

    /**
     * Returns Term value referenced by name
     * @param name String
     * @return Object or null if term not found
     */
    public Object getValueByName(String name)
    {
       T term = getTermByName(name);
       return term != null ? term.getValue() : null;
    }
    
    /**
     * Returns Term referenced by index
     * @param index Valid index value
     * @return Term object or null if index out of range
     */
    public T getTermByIndex(int index)
    {
        if (termList.isEmpty() || (index >= termList.size()) ||( index < 0))
            return null;
        return termList.get(index);
    }

    /**
     * Returns Term value referenced by index
     * @param index Valid index value
     * @return Object or null if index out of range
     */
    public Object getValueByIndex(int index)
    {
        T term = getTermByIndex(index);
        return term != null ? term.getValue() : null;
    }
    
    /**
     * Set Terms in this archetype. 
     * @param terms Term array
     */
    protected void setTerms(T[] terms) 
    {
        if (terms.length > 0)
        {
            int index = 0;
            for (T term: terms)
                archetype.addTerm(new TermMetaData(term, index++));
        }
    }

    /**
     * Returns display text of name and terms
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getName());
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

}
