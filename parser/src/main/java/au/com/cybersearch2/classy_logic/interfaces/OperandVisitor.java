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
package au.com.cybersearch2.classy_logic.interfaces;

/**
 * OperandVisitor
 * @author Andrew Bowley
 * 16 Dec 2014
 */
public interface OperandVisitor 
{
	/**
	 * Visit next term
	 * @param term Term object
	 * @param depth Depth in Operand tree
	 * @return Flag set true to continue
	 */
	boolean next(Term term, int depth);
}
