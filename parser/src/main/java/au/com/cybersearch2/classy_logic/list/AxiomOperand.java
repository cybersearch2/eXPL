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
package au.com.cybersearch2.classy_logic.list;

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;

/**
 * AxiomOperand
 * Variable Operand to access an item of either an axiom or a term of an axiom
 * @author Andrew Bowley
 * 22Jan.,2017
 */
public class AxiomOperand extends ListVariableOperand implements ParserRunner
{
    /**
     * Construct an AxiomOperand object
     * @param listName
     * @param indexExpression
     * @param expression2
     */
    public AxiomOperand(String listName, Operand indexExpression,
            Operand expression2)
    {
        super(listName, indexExpression, expression2);
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        expression = newListVariableInstance(parserAssembler);
    }


}
