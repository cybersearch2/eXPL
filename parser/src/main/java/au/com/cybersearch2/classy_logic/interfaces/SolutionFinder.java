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

import au.com.cybersearch2.classy_logic.pattern.Template;
import au.com.cybersearch2.classy_logic.query.Solution;

/**
 * FindSolution
 * @author Andrew Bowley
 * 12 Jan 2015
 */
public interface SolutionFinder 
{
	/**
	 * Find a solution for specified template
	 * @param solution Resolution of current query managed by QueryExecuter up to this point  
	 * @param template Template used on each iteration
	 * @return Flag to indicate if another solution may be available
	 */
	boolean iterate(Solution solution, Template template);

	/**
	 * Set axiom listener to receive each solution as it is produced
	 * @param axiomListener The axiom listener object
	 */
	void setAxiomListener(AxiomListener axiomListener);
}
