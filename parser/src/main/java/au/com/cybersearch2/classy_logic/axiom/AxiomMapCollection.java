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

import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classy_logic.interfaces.AxiomCollection;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;

/**
 * AxiomMapCollection
 * A set of AxiomSource objects referenced by name
 * @author Andrew Bowley
 * 9 Jan 2015
 */
public class AxiomMapCollection implements AxiomCollection
{
	/** The AxiomSource map */
	Map<String, AxiomSource> axiomSourceMap;

	/**
	 * Construct an empty AxiomMapCollection object
	 */
	public AxiomMapCollection()
	{
		axiomSourceMap = new HashMap<String, AxiomSource>();
	}

	/**
	 * Construct an AxiomMapCollection object with specified AxiomSource map
	 * @param axiomSourceMap The AxiomSource map
	 */
	public AxiomMapCollection(Map<String, AxiomSource> axiomSourceMap)
	{
		this.axiomSourceMap = axiomSourceMap;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.AxiomCollection#getAxiomSource(java.lang.String)
	 */
	@Override
	public AxiomSource getAxiomSource(final String name) 
	{
		return axiomSourceMap.get(name);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.AxiomCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() 
	{
		return axiomSourceMap.isEmpty();
	}

	/**
	 * Add AxiomSource object
	 * @param axiomKey The AxiomSource name
	 * @param axiomSource The AxiomSource object
	 */
	public void put(String axiomKey, AxiomSource axiomSource) 
	{
		axiomSourceMap.put(axiomKey, axiomSource);
	}

}
