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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.OperandParam;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * ParameterList
 * Contains named parameters which are passed to a function which creates an object dependent on these parameters.
 * Note that the ower of the parameter list is responsible for performing unification on the Operand objects it contains.
 * @author Andrew Bowley
 * 7 Aug 2015
 */
public class ParameterList<R>
{
    protected static List<Term> EMPTY_TERM_LIST;
    /** Performs function using parameters collected after query evaluation and returns value */
    protected CallEvaluator<R> callEvaluator;
    /** List of Operand arguments or null for no arguments */
    protected List<OperandParam> operandParamList;
 
    static
    {
        EMPTY_TERM_LIST = Collections.emptyList();
    }
    
    /**
     * Construct a ParameterList object which uses parameters in an an Expression operand 
     * and a supplied evaluator object to create it's value
     * @param operandParamList List of Operand arguments or null for no arguments
     * @param callEvaluator Executes function using parameters and returns object of generic type
     */
    public ParameterList(List<OperandParam> operandParamList, CallEvaluator<R> callEvaluator) 
    {
        this.operandParamList = operandParamList;
        this.callEvaluator = callEvaluator;
    }

    /**
     * Returns list of parameters
     * @return
     */
    public List<OperandParam> getOperandParamList()
    {
        return operandParamList;
    }

    /**
     * Perform function using parameters
     * @return Object of generic type
     */
    public R evaluate(int id)
    {
        if ((operandParamList == null) || operandParamList.isEmpty())
            return callEvaluator.evaluate(EMPTY_TERM_LIST);
        final List<Term> argumentList = new ArrayList<Term>(operandParamList.size());
        if ((operandParamList != null) && !operandParamList.isEmpty())
        {   
            for (OperandParam operandParam: operandParamList)
            {
                Operand operand = operandParam.getOperand();
                if (operand.isEmpty())
                    operand.evaluate(id);
                argumentList.add(operandParam);
            }
        }
        return callEvaluator.evaluate(argumentList);
    }

    public void backup(int id)
    {
        if ((operandParamList != null) && !operandParamList.isEmpty())
        {   
            for (OperandParam operandParam: operandParamList)
            {
                Operand operand = operandParam.getOperand();
                operand.backup(id);
            }
        }
    }
}
