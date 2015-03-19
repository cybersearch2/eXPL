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
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * Group
 * A sequence of Operands which are assigned values when the owning Operand is evaluated.
 * @author Andrew Bowley
 * 10 Jan 2015
 * @see au.com.cybersearch2.classy_logic.expression.RegExOperand
 */
public class Group 
{
	/** Operand list. Position is significant. */
    protected List<Operand> groupList;
    /** Name of Group - expected to be same as name of owning Operand */
    protected String name;

	/**
	 * Construct a Group object
	 * @param name
	 */
	public Group(String name) 
	{
		groupList = new ArrayList<Operand>();
	}

	/**
	 * Adds operand to group
	 * @param operand
	 */
	public void addGroup(Operand operand)
	{
		groupList.add(operand);
	}

	/**
	 * Returns list of operands
	 * @return Operand List
	 */
	public List<Operand> getGroupList()
	{
		return Collections.unmodifiableList(groupList);
	}

	/**
	 * Returns group name
	 * @return String
	 */
	public String getName()
	{
		return name;
	}
}
