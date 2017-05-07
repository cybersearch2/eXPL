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
 * TODO - Configure case sensitivity
 * Factory class for Axioms and Templates 
 * @author Andrew Bowley
 * 2May,2017
 */
public abstract class Archetype <T extends TermList<P>, P extends Term> implements TermListManager, Comparable<Archetype<T,P>>
{
    public static List<TermMetaData> EMPTY_LIST;
    public static boolean CASE_INSENSITIVE_NAME_MATCH;
    
    static
    {
        EMPTY_LIST = Collections.emptyList();
    }

    /** Unique name of structure */
    protected QualifiedName structureName;
    /** Structure type defines usage */
    protected StructureType structureType;
    /** List of term meta data in same order as terms in structure */
    protected List<TermMetaData> termMetaList;
    /** Flag set true if this Structure allows updates. Set true on creation and cleared when all term data complete */
    protected boolean isMutable;
    /** Flag set true if all terms anonymous */
    protected boolean isAnonymousTerms;

    public Archetype(QualifiedName structureName, StructureType structureType)
    {
        this.structureName = structureName;
        this.structureType = structureType;
        termMetaList = EMPTY_LIST;
        isMutable = true;
        isAnonymousTerms = true;
    }

    /**
     * @return the structureName
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

    
    @Override
    public int getTermCount()
    {
        return termMetaList.size();
    }

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
        if (!isMutable)
            throw new ExpressionException("Term " + termMetaData.toString() + " cannot be added to locked Structure " + structureName);
        if (termMetaData.getIndex() == -1)
            termMetaData.setIndex(getTermCount());
        if (termMetaList.isEmpty())
            termMetaList = new ArrayList<TermMetaData>();
        else if (termMetaList.contains(termMetaData))
            throw new ExpressionException("Term " + termMetaData.getName() + " already exists in " + toString());
        if (termMetaData.getIndex() < termMetaList.size())
        {
            TermMetaData existingTermMetaData = termMetaList.get(termMetaData.getIndex());
            if (existingTermMetaData != null)
            {
                if (existingTermMetaData.getLiteralType() == LiteralType.unspecified)
                    existingTermMetaData.setLiteralType(termMetaData.getLiteralType());
                else
                    throw new ExpressionException("Term " + termMetaData.getName() + ", type " + termMetaData.getLiteralType() + ", already exists in " + toString());
            }
        }
        else
            termMetaList.add(termMetaData);
        if (!termMetaData.isAnonymous())
            isAnonymousTerms = false;
        return termMetaData.getIndex();
    }

    @Override
    public void checkTerm(TermMetaData termMetaData)
    {
        if (!termMetaList.contains(termMetaData))
        {
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
                if (termMetaList.contains(altMetaData))
                    return;
                if (altLiteralType == LiteralType.integer)
                {
                    altMetaData = new TermMetaData(LiteralType.decimal, termMetaData.getName(), termMetaData.getIndex());
                    if (termMetaList.contains(altMetaData))
                        return;
                }
            }
            throw new ExpressionException("Term " + termMetaData.getName() + " incompatible with definition for " + toString());
       }
    }

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
        int i = 0;
        for (TermMetaData termMetaData: termMetaList)
        {
            String name = CASE_INSENSITIVE_NAME_MATCH ? termMetaData.getName().toUpperCase() : termMetaData.getName();
            if (!termMetaData.isAnonymous() && termName.equals(name))
                return i;
            ++i;
        }
        return -1;
    }
 
    @Override
    public List<String> getAxiomTermNameList()
    {
        List<String> termNameList = new ArrayList<String>(termMetaList.size());
        for (TermMetaData termMetaData: termMetaList)
            termNameList.add(termMetaData.getName());
        return termNameList;
    }

    @Override
    public boolean changeName(int index, String name)
    {
        if ((index < 0) || (index >= getTermCount()))
            return false;
        TermMetaData termMetaData = termMetaList.get(index);
        return termMetaData.setName(name);
    }

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

    private void createTermMetaList(List<P> terms)
    {
        int index = 0;
        for (Term term: terms)
        {
            addTerm(new TermMetaData(term, index++));
        }
    }

    abstract protected T newInstance(List<P> terms);
    
     /**
     * @return the isMutable
     */
    public boolean isMutable()
    {
        return isMutable;
    }

    /**
     * @param isMutable the isMutable to set
     */
    public void clearMutable()
    {
        isMutable = false;
    }

    /**
     * @return the isAnonymousTerms
     */
    public boolean isAnonymousTerms()
    {
        return isAnonymousTerms;
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

}
