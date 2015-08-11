/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

/**
 * CallEvaluator
 * Performs function using parameters collected after query evaluation and returns value
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public interface CallEvaluator<R>
{
    /**
     * Returns name of function. Must be unique in context.
     * @return String
     */
    String getName();
    /**
     * Perform function 
     * @param argumentList List of terms
     * @return Object of generic type
     */
    R evaluate(List<Term> argumentList);
}
