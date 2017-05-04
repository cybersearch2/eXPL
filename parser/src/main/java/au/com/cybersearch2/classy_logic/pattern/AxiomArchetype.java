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
 * @author Andrew Bowley
 * 3May,2017
 */
public class AxiomArchetype extends Archetype<Axiom, Term>
{
    private static final Unknown UNKNOWN =  new Unknown();
    
    public AxiomArchetype(QualifiedName structureName)
    {
        super(structureName, StructureType.axiom);
    }

    protected Axiom newInstance()
    {
        Axiom axiom = new Axiom(this, Collections.emptyList());
        return axiom;
    }

   @Override
    protected Axiom newInstance(List<Term> terms)
    {
        Axiom axiom = new Axiom(this, terms);
        return axiom;
    }

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

    public int addTermName(String termName)
    {
        return addTerm(new TermMetaData(new LiteralParameter(termName, UNKNOWN, LiteralType.unspecified)));
    }

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
