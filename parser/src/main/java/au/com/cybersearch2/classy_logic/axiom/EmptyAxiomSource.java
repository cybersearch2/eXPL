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
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * EmptyAxiomSource
 * @author Andrew Bowley
 * 2 Jan 2015
 */
public class EmptyAxiomSource implements AxiomSource 
{

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.AxiomSource#iterator()
	 */
	@Override
	public Iterator<Axiom> iterator() {
		return new Iterator<Axiom>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Axiom next() {
				return null;
			}

			@Override
			public void remove() {
			}};
	}

    @Override
    public List<String> getAxiomTermNameList()
    {
        return AxiomListSource.EMPTY_LIST;
    }

}
