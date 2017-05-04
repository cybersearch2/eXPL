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
package au.com.cybersearch2.classy_logic.operator;

import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isNaN;

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * EvaluatorOperator
 * Sub classes DelegateOperator to escape number evaluation with NaN
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class EvaluatorOperator extends DelegateOperator
{
    /**
     * Construct EvaluatorOperator object
     */
    public EvaluatorOperator()
    {
        super();
    }

    /**
     * Evaluate a unary expression 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        if (isNaN(rightTerm.getValue()))
            return new Double("NaN");
        return super.numberEvaluation(operatorEnum2, rightTerm);
    }

    /**
     * Evaluate a binary expression
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        if (isNaN(rightTerm.getValue()) || isNaN(leftTerm.getValue()))
            return new Double("NaN");
        return super.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

}
