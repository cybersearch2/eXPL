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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

/**
 * AxiomParameterOperator
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class AxiomParameterOperator implements Operator
{
    static Trait AXIOM_PARAMETER_TRAIT;
    
    static
    {
        AXIOM_PARAMETER_TRAIT = new DefaultTrait(OperandType.TERM);
    }
    
    @Override
    public Trait getTrait()
    {
        return AXIOM_PARAMETER_TRAIT;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.expression.NullOperand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return  new OperatorEnum[]
        { 
            OperatorEnum.ASSIGN,
         };
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.expression.NullOperand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return  new OperatorEnum[]
        { 
                OperatorEnum.ASSIGN,
        };
    }

    /**
     * Returns Value for no concatenation operations
     * @return OperatorEnum[]
     */
    @Override
    public OperatorEnum[] getStringOperandOps()
    {
        return EMPTY_OPERAND_OPS;
    }
 
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a number
        return new Integer(0);
    }

    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a number
        return new Integer(0);
    }

    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a boolean
        return Boolean.FALSE;
    }

}
