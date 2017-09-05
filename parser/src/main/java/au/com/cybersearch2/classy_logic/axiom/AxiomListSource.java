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
package au.com.cybersearch2.classy_logic.axiom;

import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;

/**
 * AxiomListSource
 * @author Andrew Bowley
 * 5 Dec 2014
 */
public class AxiomListSource  implements AxiomSource, Iterable<Axiom>
{
	/** The axiom list */
    protected List<Axiom> axiomList;
    /** The axiom archetype */
    protected Archetype<Axiom,Term> archetype;

    /**
     * Construct an AxiomListSource object
     * @param axiomList The axiom list or null to create a empty AxiomSource
     */
	public AxiomListSource(List<Axiom> axiomList) 
	{
		this.axiomList = axiomList;
	}

    /**
     * Construct an AxiomListSource object with supplied archetype
     * @param axiomList The axiom list or null to create a empty AxiomSource
     * @param archetype The axiom archetype 
     */
    public AxiomListSource(List<Axiom> axiomList, Archetype<Axiom,Term> archetype) 
    {
        this.axiomList = axiomList;
        this.archetype = archetype;
    }

	/**
	 * Returns the axiom list Iterable
	 * @return Iterable of generic type Axiom
	 */
	public Iterable<Axiom> getIterable() 
	{
		return axiomList;
	}

	/**
	 * Returns term name list
	 * @return String list
	 */
    public List<String> getAxiomTermNameList()
    {
        return getArchetype().getTermNameList();
    }

    /**
     * iterator
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#iterator()
     */
    @Override
    public Iterator<Axiom> iterator() 
    {
        return axiomList.iterator();
    }

    /**
     * getArchetype
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#getArchetype()
     */
    @Override
    public Archetype<Axiom, Term> getArchetype()
    {
        if (archetype == null)
            archetype = getAxiomListArchetype();
        return archetype;
    }

    /**
     * Returns axiom archetype form axiom list contents, if available,
     * otherwise returns archetype for an empty axiom
     * @return Archetype object
     */
    @SuppressWarnings("unchecked")
    private Archetype<Axiom, Term> getAxiomListArchetype()
    {
        if (!axiomList.isEmpty())
            return (Archetype<Axiom, Term>) axiomList.get(0).getArchetype();
        AxiomArchetype archetype = new AxiomArchetype(QualifiedName.ANONYMOUS);
        archetype.clearMutable();
        return archetype;
    }
}
