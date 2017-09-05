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
import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TermListManager;
import au.com.cybersearch2.classy_logic.terms.LiteralType;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * Archetype
 * Factory class for Axioms and Templates 
 * @author Andrew Bowley
 * 2May,2017
 */
public abstract class Archetype <T extends TermList<P>, P extends Term> implements TermListManager, Comparable<Archetype<T,P>>, Serializable
{
    private static final long serialVersionUID = 7567670630018077130L;
    
    public static List<TermMetaData> EMPTY_LIST;
    public static boolean CASE_INSENSITIVE_NAME_MATCH;
    
    static
    {
        EMPTY_LIST = Collections.emptyList();
    }

    /** Unique name of structure */
    protected QualifiedName structureName;
    /** Structure type defines usage */
    transient protected StructureType structureType;
    /** List of term meta data in same order as terms in structure */
    transient protected List<TermMetaData> termMetaList;
    /** Flag set true if this Structure allows updates. Set true on creation and cleared when all term data complete */
    transient protected boolean isMutable;
    /** Flag set true if all terms anonymous */
    transient protected boolean isAnonymousTerms;
    /** Flag set true if duplicate term names allowed */
    transient protected boolean isDuplicateTermNames;
    
    /**
     * Construct Archetype object
     * @param structureName Qualified name of archetype
     * @param structureType Structure type - axiom, template, choice, archetype
     */
    public Archetype(QualifiedName structureName, StructureType structureType)
    {
        this.structureName = structureName;
        this.structureType = structureType;
        termMetaList = EMPTY_LIST;
        isMutable = true;
        isAnonymousTerms = true;
    }

    /**
     * @return isMutable flag
     */
    public boolean isMutable()
    {
        return isMutable;
    }

    /**
     * Clear isMutable flag
     */
    public void clearMutable()
    {
        isMutable = false;
    }

    /**
     * @return isAnonymousTerms flag
     */
    public boolean isAnonymousTerms()
    {
        return isAnonymousTerms;
    }

    /**
     * Create item containing given terms
     * @param terms List of terms
     * @return T
     */
    public T itemInstance(List<P> terms)
    {
        if (terms != null)
        {
            if (termMetaList.isEmpty())
            {
                createTermMetaList(terms);
                clearMutable();
            }
            else
                checkTerms(terms);
        }
        return newInstance(terms);
    }

    /**
     * @return qualified name
     */
    @Override
    public QualifiedName getQualifiedName()
    {
        return structureName;
    }

    /**
     * @return the name part of qualified name
     */
    @Override
    public String getName()
    {
        return structureName.getName();
    }

    /**
     * getTermCount
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#getTermCount()
     */
    @Override
    public int getTermCount()
    {
        return termMetaList.size();
    }

    /**
     * getNamedTermCount
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#getNamedTermCount()
     */
    @Override
    public int getNamedTermCount()
    {
        int count = 0;
        for (TermMetaData termMetaData: termMetaList)
            if (!termMetaData.isAnonymous())
                ++count;
        return count;
    }

    /**
     * Add term meta data
     * @param termMetaData Term meta data
     * @return index of term
     */
    @Override
    public int addTerm(TermMetaData termMetaData)
    {
        checkMutable(termMetaData);
        if (termMetaData.getIndex() == -1)
            termMetaData.setIndex(getTermCount());
        if (termMetaList.isEmpty())
            termMetaList = new ArrayList<TermMetaData>();
        else if (termMetaList.contains(termMetaData) && 
                (termMetaData.getLiteralType() != LiteralType.unspecified) &&
                !setLiteralType(termMetaData))
            throw new ExpressionException("Term " + termMetaData.getName() + " already exists in " + toString());
        if (termMetaData.getIndex() >= termMetaList.size())
            termMetaList.add(termMetaData);
        if (!termMetaData.isAnonymous())
            isAnonymousTerms = false;
        return termMetaData.getIndex();
    }

    /**
     * checkTerm
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#checkTerm(au.com.cybersearch2.classy_logic.terms.TermMetaData)
     */
    @Override
    public void checkTerm(TermMetaData termMetaData)
    {
        if (isValidMetaData(termMetaData))
            return;
        // Check if type conversion will handle this term
        LiteralType altLiteralType;
        switch(termMetaData.getLiteralType())
        {
        case integer:
            altLiteralType = LiteralType.xpl_double; break;
        case xpl_double:
            altLiteralType = LiteralType.integer; break;
        case decimal:
            altLiteralType = LiteralType.xpl_double; break;
        default:
            altLiteralType = null;
        }
        if (altLiteralType != null)
        {
            TermMetaData altMetaData = new TermMetaData(altLiteralType, termMetaData.getName(), termMetaData.getIndex());
            if (isValidMetaData(altMetaData))
                return;
            if (altLiteralType == LiteralType.integer)
            {
                altMetaData = new TermMetaData(LiteralType.decimal, termMetaData.getName(), termMetaData.getIndex());
                if (isValidMetaData(altMetaData))
                    return;
            }
        }
        throw new ExpressionException("Term " + termMetaData.getName() + " incompatible with definition for " + toString());
    }

    /**
     * analyseTerm
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#analyseTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public TermMetaData analyseTerm(Term term, int index)
    {
        if (index < termMetaList.size())
        {
            TermMetaData termMetaData = termMetaList.get(index);
            if (term.getName().isEmpty() && !termMetaData.isAnonymous())
            {
                term.setName(termMetaData.getName());
                isAnonymousTerms = false;
            }
        }
        return new TermMetaData(term, index);
    }

    /**
     * Returns index for term referenced by name. Default return value is -1 for no match. 
     * @param termName
     * @return Index of item or -1 for no match
     */
    @Override
    public int getIndexForName(String termName)
    {
        if (termName == null)
            return -1;
        int i = 0;
        for (TermMetaData termMetaData: termMetaList)
        {
            String name = CASE_INSENSITIVE_NAME_MATCH ? termMetaData.getName().toUpperCase() : termMetaData.getName();
            String toMatch = CASE_INSENSITIVE_NAME_MATCH ? termName.toUpperCase() : termName;
            if (/*!termMetaData.isAnonymous() &&*/ toMatch.equals(name))
                return i;
            ++i;
        }
        return -1;
    }

    /**
     * getTermNameList
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#getTermNameList()
     */
    @Override
    public List<String> getTermNameList()
    {
        List<String> termNameList = new ArrayList<String>(termMetaList.size());
        for (TermMetaData termMetaData: termMetaList)
            termNameList.add(termMetaData.getName());
        return termNameList;
    }

    /**
     * changeName
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#changeName(int, java.lang.String)
     */
    @Override
    public boolean changeName(int index, String name)
    {
        if ((index < 0) || (index >= getTermCount()))
            return false;
        TermMetaData termMetaData = termMetaList.get(index);
        if (termMetaData.setName(name))
        {
            isAnonymousTerms = false;
            return true;
        }
        return false;
    }

    /**
     * getMetaData
     * @see au.com.cybersearch2.classy_logic.interfaces.TermListManager#getMetaData(int)
     */
    @Override
    public TermMetaData getMetaData(int index)
    {
        if ((index < 0) || (index >= getTermCount()))
            return null;
        return termMetaList.get(index);
        
    }
    
    /**
     * @param isDuplicateTermNames the isDuplicateTermNames to set
     */
    @Override
    public void setDuplicateTermNames(boolean isDuplicateTermNames)
    {
        this.isDuplicateTermNames = isDuplicateTermNames;
    }

    /**
     * compareTo
     * @see java.lang.Comparable#compareTo(Object)
      */
    @Override
    public int compareTo(Archetype<T,P> other)
    {
        return structureName.compareTo(other.structureName);
    }

    /**
     * Check validity of all terms
     * @param terms
     */
    private void checkTerms(List<P> terms)
    {
        if (termMetaList.isEmpty()) // Paranoid check
            throw new IllegalStateException("checkTerms() called before createTermMetaList()");
        int index = 0;
        for (Term term: terms)
        {
            checkTerm(new TermMetaData(term, index++));
        }
    }

    /**
     * Create term metadata list for given terms
     * @param terms List of terms
     */
    private void createTermMetaList(List<P> terms)
    {
        int index = 0;
        for (Term term: terms)
        {
            addTerm(new TermMetaData(term, index++));
        }
    }

    /**
     * Create new T instance given list of terms
     * @param terms List of terms
     * @return T
     */
    abstract protected T newInstance(List<P> terms);
    

    /**
     * Returns flag set true if termMetaList contains given term metadata.
     * Updates Literal type of list item too if unspecified
     * @param termMetaData TermMetaData object
     * @return boolean
     * @see #checkTerm(TermMetaData)
     */
    private boolean isValidMetaData(TermMetaData termMetaData)
    {
        for (TermMetaData item: termMetaList)
            if (item.compareTo(termMetaData) == 0)
            {
                if (item.getLiteralType() == LiteralType.unspecified)
                    item.setLiteralType(termMetaData.getLiteralType());
                return true;
            }
        return false;
    }
 
    protected void checkMutable(TermMetaData termMetaData)
    {
        if (!isMutable)
            throw new ExpressionException("Term " + termMetaData.toString() + " cannot be added to locked " + toString());
    }
    
    /**
     * Sets list item literal type if currently unspecified. Returns flag set true if 
     * operation succeeds.
     * @param termMetaData TermMetaData object
     * @return boolean
     * @see #addTerm(TermMetaData)
     */
    protected boolean setLiteralType(TermMetaData termMetaData)
    {
        for (TermMetaData item: termMetaList)
            if (item.compareTo(termMetaData) == 0)
            {
                if (item.getLiteralType() == LiteralType.unspecified)
                {
                    item.setLiteralType(termMetaData.getLiteralType());
                    return true;
                }
                if (!isDuplicateTermNames)
                    return false;
                return termMetaData.getIndex() != item.getIndex();
            }
        return false;
    }
}
