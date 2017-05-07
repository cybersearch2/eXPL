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
package au.com.cybersearch2.classy_logic.interfaces;

/**
 * TermPairList
 * Interface for container for TermPair objects designed to recycle objects for efficiency
 * The container must allow the the list to also be accessed and cleared 
 * @author Andrew Bowley
 * 7May,2017
 */
public interface TermPairList
{
    /**
     * Add operand/term pair to be unified
     * @param term1 Template operand
     * @param term2 Axiom term
     */
    void add(Operand term1, Term term2);
}
