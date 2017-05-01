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
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

/**
 * NullOperator
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class NullOperator extends ExpressionOperator
{
    /** Localization and specialization */
    protected DefaultTrait trait;

    /**
     * 
     */
    public NullOperator()
    {
        super();
        trait = new DefaultTrait(OperandType.UNKNOWN);
    }

    @Override
    public Trait getTrait()
    {
        return trait;
    }

    /**
     * getRightOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return  new OperatorEnum[]
        { 
            OperatorEnum.ASSIGN,
            OperatorEnum.EQ, // "=="
            OperatorEnum.NE // "!="
        };
    }

    /**
     * getLeftOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return  new OperatorEnum[]
        { 
                OperatorEnum.ASSIGN,
                OperatorEnum.EQ, // "=="
                OperatorEnum.NE // "!="
        };
    }

    /**
     * Unary numberEvaluation - invalid
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return new Integer(0);
    }

    /**
     * Binary numberEvaluation - invalid
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return new Integer(0);
    }

    /**
     * booleanEvaluation - compare to another NullOperand
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        boolean calc = false;
        switch (operatorEnum2)
        {
        case EQ:  calc = (leftTerm.getValueClass() == getValueClass()) && (rightTerm.getValueClass() == getValueClass()); break; // "=="
        case NE:  calc = !((leftTerm.getValueClass() == getValueClass()) && (rightTerm.getValueClass() == getValueClass())); break; // "!="
        default:
        }
        return calc;
    }

    private Class<?> getValueClass()
    {
        return Null.class;
    }
}
