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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;

/**
 * Operand 
 * A Term which evaluates binary and unary expressions
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public interface Operand extends Term 
{
	/**
	 * Returns qualified name
	 * @return QualifiedName object
	 */
	QualifiedName getQualifiedName();
	
    /**
     * Returns left child of Operand
     * @return Operand object or null if there is no child
     */
    Operand getLeftOperand();
    
    /**
     * Returns right child of Operand
     * @return Operand object or null if there is no child
     */
    Operand getRightOperand();

	/**
	 * Set this operand private - not visible in solution
	 * @param isPrivate Flag set true if operand not visible in solution
	 */
	void setPrivate(boolean isPrivate);
	
	/**
	 * Returns flag set true if this operand is private
	 * @return
	 */
	boolean isPrivate();

	/**
	 * Returns object which defines operations that an Operand performs with other operands
	 * @return Operator object
	 */
	Operator getOperator();

    /**
     * Sets index of this Operand in the archetype of it's containing template
     * @param index int value
     */
    void setArchetypeIndex(int index);
 
    /**
     * Returns index of this Operand in the archetype of it's containing template.
     * @return non-negative number, if set, otherwise -1
     */
    int getArchetypeIndex();
}
