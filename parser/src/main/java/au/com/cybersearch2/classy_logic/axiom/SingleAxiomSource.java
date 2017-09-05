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

import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Archetype;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * SingleAxiomIterator
 * AxiomSource adapter for a single axiom
 * @author Andrew Bowley
 * 4 Dec 2014
 */
public class SingleAxiomSource implements AxiomSource, Iterator<Axiom>, Iterable<Axiom> 
{
    /** Count up to 1 */
    int count;
    /** The axiom */
    Axiom axiom;

    /**
     * Construct SingleAxiomSource object
     * @param axiom The axiom object
     */
    public SingleAxiomSource(Axiom axiom)
    {
    	this.axiom = axiom;
    }

    /**
     * Returns self 
     * @return this
     */
    public Iterable<Axiom> getIterable() 
    {
        return this;
    }

    public List<String> getAxiomTermNameList()
    {
        return axiom.getArchetype().getTermNameList();
    }

    /**
     * 
     * @see java.util.Iterator#hasNext()
     */
	@Override
    public boolean hasNext()
    {
        return count == 0;
    }

	/**
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
    public Axiom next()
    {
        ++count;
        return axiom;
    }

	/**
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() 
	{
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#iterator()
	 */
	@Override
	public Iterator<Axiom> iterator() 
	{
		return new SingleAxiomSource(axiom);
	}

    @SuppressWarnings("unchecked")
    @Override
    public Archetype<Axiom, Term> getArchetype()
    {
        return (Archetype<Axiom, Term>) axiom.getArchetype();
    }

}
