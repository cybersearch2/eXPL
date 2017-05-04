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

import au.com.cybersearch2.classy_logic.interfaces.Literal;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * BooleanTerm
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class BooleanTerm extends GenericParameter<Boolean> implements Literal 
{

	/**
	 * Construct a BooleanTerm object
	 * @param value
	 */
	public BooleanTerm(boolean value) 
	{
		super(Term.ANONYMOUS, value);
	}

    @Override
    public LiteralType getLiteralType()
    {
        return LiteralType.xpl_boolean;
    }
}
