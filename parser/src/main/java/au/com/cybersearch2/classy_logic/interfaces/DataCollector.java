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

import java.util.Collection;


/**
 * DataCollector
 * Interface to persistence system used by axiom providers
 * @author Andrew Bowley
 * 10 Feb 2015
 */
public interface DataCollector  
{
	/**
	 * Returns list of objects from persistence system. 
	 * Note calling thread may be blocked waiting for results
	 * @return Object collection
	 */
	Collection<Object> getData();
	/**
	 * Returns flag set true if a call to getData() may deliver more results.
	 * @return boolean
	 */
	boolean isMoreExpected();
}
