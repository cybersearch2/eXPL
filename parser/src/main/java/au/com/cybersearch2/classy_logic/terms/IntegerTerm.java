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
package au.com.cybersearch2.classy_logic.terms;

import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * IntegerTerm
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class IntegerTerm extends GenericParameter<Long> 
{
	/**
	 * Construct an anonymous IntegerTerm object 
	 * @param value int
	 */
	public IntegerTerm(long value) 
	{
		super(Term.ANONYMOUS, value);

	}

	/**
	 * Construct an anonymous IntegerTerm object
	 * @param value String representaion of integer value
	 */
	public IntegerTerm(String value) 
	{
		super(Term.ANONYMOUS, (value.indexOf('x', 0) == 1 || value.indexOf('X', 0) == 1) ? Long.parseLong(value.substring(2), 16) : Long.parseLong(value));

	}

}
