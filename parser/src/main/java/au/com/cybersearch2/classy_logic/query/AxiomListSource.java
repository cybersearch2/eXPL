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
package au.com.cybersearch2.classy_logic.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * AxiomListSource
 * @author Andrew Bowley
 * 5 Dec 2014
 */
public class AxiomListSource  implements AxiomSource, Iterable<Axiom>
{
	/** The axiom list */
    protected List<Axiom> axiomList;
  
    /**
     * Construct an AxiomListSource object
     * @param axiomList The axiom list or null to create a empty AxiomSource
     */
	public AxiomListSource(List<Axiom> axiomList) 
	{
		this.axiomList = axiomList == null ? new ArrayList<Axiom>() : axiomList;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#iterator()
	 */
	@Override
	public Iterator<Axiom> iterator() 
	{
		return axiomList.iterator();
	}

	/**
	 * Returns the axiom list Iterable
	 * @return Iterable of generic type Axiom
	 */
	public Iterable<Axiom> getIterable() 
	{
		return axiomList;
	}

}
