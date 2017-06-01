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
 * ListItemDelegate
 * Delegate for ListItemVariable, where operand details are finalized only when the Parser task is performed
 * @author Andrew Bowley
 * 1Jun.,2017
 */
public interface ListItemDelegate
{

    /**
     * unifyTerm - 
     * If it exists, unify index operand for value selection. 
     * Note that in a 2 dimension case, when the first index is an operand, it must be set by a prior unify-evaluation step
     * The value of this variable is not set directly by unification.
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    int unifyTerm(Term otherTerm, int id);

    /**
     * Evaluate to complete list binding, if using a list operand, and resolve list parameters
     * @param id Identity of caller, which must be provided for backup()
     * @return index for value selection
     */
    int evaluate(int id);

    /**
     * backup
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int)
     */
    boolean backup(int id);

    /**
     * Set item value
     * @param value
     */
    void setItemValue(Object value);

    /**
     * Returns value from item list
     * @return value as object
     */
    Object getValue();

    /**
     * Returns value at specified index in item list
     * @return value as object
     */
    Object getValue(int selection);

    /**
     * Return list being referenced. Not currently used, but kept for potential future use.
     * @return the itemList
     */
    ItemList<?> getItemList();

}