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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.LiteralParameter;
import au.com.cybersearch2.classy_logic.terms.LiteralType;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.terms.TermMetaData;

/**
 * AxiomArchetype
 * Axiom factory which type checks data passed to construct axioms
 * @author Andrew Bowley
 * 3May,2017
 */
public class AxiomArchetype extends Archetype<Axiom, Term>
{
    private static final Unknown UNKNOWN ;
    public static TermList<Term> EMPTY_AXIOM;
    
    static
    {
        UNKNOWN =  new Unknown();
        EMPTY_AXIOM = new TermList<Term>(new AxiomArchetype(QualifiedName.parseGlobalName("*"))){};
    }
    

    /**
     * Construct AxiomArchetype
     * @param structureName Qualified name which uniquely identifies the axioms being produced - must have a name part
     */
    public AxiomArchetype(QualifiedName structureName)
    {
        super(structureName, StructureType.axiom);
        if (structureName.getName().isEmpty())
            throw new IllegalArgumentException("Axiom qualified name must have a name part");
    }

    /**
     * Create default Axiom instance
     * @return Axiom object
     */
    protected Axiom newInstance()
    {
        Axiom axiom = new Axiom(this, Collections.emptyList());
        return axiom;
    }

    /**
     * Create Axiom instance
     * @return Axiom object
     * @see au.com.cybersearch2.classy_logic.pattern.Archetype#newInstance(java.util.List)
     */
    @Override
    protected Axiom newInstance(List<Term> terms)
    {
        Axiom axiom = new Axiom(this, terms);
        return axiom;
    }

    /**
     * Create Axiom instance
     * @param values Objects to populate axiom - may be Terms
     * @return
     */
    public Axiom itemInstance(Object... values)
    {
        List<Term> terms = new ArrayList<Term>();
        if ((values != null)&& (values.length > 0))
        {
            for (Object datum: values)
            {
                if (datum instanceof Term)
                    terms.add((Term) datum);
                else
                    terms.add(new Parameter(Term.ANONYMOUS, datum));
            }
        }
        return newInstance(terms);
    }

    /**
     * Add term to archetype, specifying only name. Archetype must be in mutable state to succeed.
     * @param termName Name of term
     * @return Term index
     */
    public int addTermName(String termName)
    {
        return addTerm(new TermMetaData(new LiteralParameter(termName, UNKNOWN, LiteralType.unspecified)));
    }

    /**
     * Returns meta-data for term specified by index
     * @param index Term index
     * @return TermMetaData object
     */
    public TermMetaData getMetaDataByIndex(int index)
    {
        if ((index >= 0) && (index < getTermCount()))
            return termMetaList.get(index);
        return null;
    }
 
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Archetype " + structureName.toString();
    }

}
