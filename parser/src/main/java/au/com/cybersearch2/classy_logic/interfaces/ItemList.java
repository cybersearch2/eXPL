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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;

/**
 * ItemList
 * Object collection accessed in script using square brackets array notation
 * @author Andrew Bowley
 * 19 Jan 2015
 */
public interface ItemList<T> extends Iterable<T>, SourceInfo 
{

    /**
     * Returns number of items in list
     * @return int
     */
    int getLength();

    /**
     * Returns name of list
     * @return String
     */
    String getName();

    /**
     * Returns qualified name
     * @return QualifiedName object
     */
    QualifiedName getQualifiedName();

    /**
     * Returns true if list is empty
     * @return boolean
     */
    boolean isEmpty();

    /**
     * Assign value to list item referenced by index. 
     * The list may grow to accommodate new item depending on implementation.
     * @param index int
     * @param value Object
     */
    void assignItem(int index, T value);

    /**
     * Returns item referenced by index
     * @param index int
     * @return Object of generic type T 
     */
	T getItem(int index);

	/**
	 * Returns OperandType of items in this container
	 * @return OperandType enum
	 */
	OperandType getOperandType();
	
	/**
	 * Returns true if index is valid and item exists reference by that index
	 * @param index int
	 * @return boolean
	 */
    boolean hasItem(int index);

    /**
     * Returns implementation of Iterable interface
     * @return Iterable of generice type T
     */
	Iterable<T> getIterable();

	/**
	 * Clear item list
	 */
	void clear();

}
