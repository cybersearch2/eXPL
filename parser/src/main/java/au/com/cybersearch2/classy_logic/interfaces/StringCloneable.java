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
 * StringCloneable
 * Support for conversion of text in format to suit locale to a number
 * @author Andrew Bowley
 * 27Apr.,2017
 */
public interface StringCloneable
{
    /**
     * Create an instance of same type as self and 
     * set it to the parsed value contained in given StringOperand
     * @param stringOperand The operand containing the text value
     * @return Operand of same type as self
     */
    Operand cloneFromOperand(Operand stringOperand);
}
