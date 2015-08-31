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

import java.util.List;

import au.com.cybersearch2.classy_logic.pattern.Axiom.TermPair;

/**
 * UnificationPairer
 * @author Andrew Bowley
 * 23 Dec 2014
 */
public interface UnificationPairer extends OperandVisitor 
{
	/**
	 * Process two terms, matched by name or list order, according to their status.
	 * Both terms will be added the internal pair list if the first term is empty. 
	 * @param operand Operand term belonging to a Template 
	 * @param otherTerm Term belonging to an Axiom - must be non-empty
	 * @return Flag set true unless exit condition detected, which depends on implementation
	 */
	boolean pairTerms(Operand operand, Term otherTerm);
	
	/**
	 * Returns list of Term pairs to be unified
	 * @return List&lt;TermPair&gt;
	 */
	List<TermPair> getPairList();
}
