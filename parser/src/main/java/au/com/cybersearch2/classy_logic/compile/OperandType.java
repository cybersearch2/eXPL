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

/**
 * OperandType
 * eXPL types used for variables and lists.
 * Note there is no LOCAL variable, only a LOCAL list.
 * @author Andrew Bowley
 * 10 Mar 2015
 */
public enum OperandType 
{
    INTEGER,
    BOOLEAN,
    DOUBLE,
    STRING,
    DECIMAL,
    TERM,   // AxiomParameterOperand or AxiomTermList created and passed to registerAxiomList()
    AXIOM,  // AxiomOperand or  AxiomList
    CURRENCY,
    LIST,   // AxiomOperand with ParameterList<AxiomList> to populate it
    UNKNOWN,
    CURSOR,
    APPENDER
}
