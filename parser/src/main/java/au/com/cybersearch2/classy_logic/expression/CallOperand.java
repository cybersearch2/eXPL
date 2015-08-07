package au.com.cybersearch2.classy_logic.expression;
/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * CallOperand
 * @author Andrew Bowley
 * 30 Jul 2015
 */
public class CallOperand extends ParameterOperand
{
    
    public CallOperand(String name, CallEvaluator callEvaluator, Operand expression)
    {
        super(name, expression, callEvaluator);
    }

}
