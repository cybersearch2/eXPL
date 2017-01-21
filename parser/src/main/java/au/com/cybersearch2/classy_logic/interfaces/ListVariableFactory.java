/**
    Copyright (C) 2016  www.cybersearch2.com.au

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
 * ListVariableFactory
 * Constructs ListVariableOperand object
 * @author Andrew Bowley
 * 20Jan.,2017
 */
public interface ListVariableFactory
{
    /**
     * Returns ListVariableOperand object instance
     * @param listName The name of the list
     * @param indexExpression Operand to select item
     * @param expression2 Optional operand for 2nd index or assignment
     * @return Operand object
     */
    Operand operandInstance(String listName, Operand indexExpression, Operand expression2);
}
