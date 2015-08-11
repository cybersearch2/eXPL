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
 * Variable which is set using a supplied function object and parameters contained in an Operand tree
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterOperand<R> extends Variable
{
    /** Collects parameters from an Operand tree and passes them to a supplied function object */
    protected ParameterList<R> parameterList;
    
    /**
     * Construct a ParameterOperand object
     * @param name Name of Variable
     * @param parameters Root of Operand parameter tree or null if no parameters
     * @param callEvaluator Executes function using parameters and returns object of generic type
     */
    public ParameterOperand(String name, Operand parameters, CallEvaluator<R> callEvaluator) 
    {
        // Note the value set when the super class evaluates will be subsequently overriden.
        super(name, parameters);
        parameterList = new ParameterList<R>(parameters, callEvaluator);
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if (status == EvaluationStatus.COMPLETE)
            setValue(parameterList.evaluate());
        return status;
    }
 
}
