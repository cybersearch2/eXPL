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
package au.com.cybersearch2.classy_logic.expression;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ParameterOperand
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterOperand<R> extends Variable
{
    /** Performs function using parameters contained in expression and returns value */
    protected ParameterList<R> parameterList;
    
    /**
     * Construct a ParameterOperand object which uses parameters in an an Expression operand 
     * and a supplied evaluator object to create it's value
     * @param name Name of Variable
     * @param expression Operand to initialize this Variable upon evaluation
     */
    protected ParameterOperand(String name, Operand expression, CallEvaluator<R> callEvaluator) 
    {
        super(name, expression);
        parameterList = new ParameterList<R>(expression, callEvaluator);
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if (status == EvaluationStatus.COMPLETE)
            setValue(parameterList.evaluate());
        return status;
    }
 
}
