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

import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.BooleanOperand;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

/**
 * BooleanOperator
 * @see DelegateType#BOOLEAN
 * @see BooleanOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class BooleanOperator implements Operator
{
    /** Localization and specialization */
    protected DefaultTrait trait;

    /** 
     * Construct BooleanOperator object
     */
    public BooleanOperator()
    {
        trait = new DefaultTrait(OperandType.BOOLEAN);
    }

    /**
     * getTrait
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return trait;
    }

    @Override
    public void setTrait(Trait trait)
    {
        if (trait.getOperandType() != OperandType.BOOLEAN)
            throw new ExpressionException(trait.getOperandType().toString() + " is not a compatible operand type");
        this.trait = (DefaultTrait) trait;
    }
    
    /**
     * getRightOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return  new OperatorEnum[]
        { 
            OperatorEnum.EQ, // "=="
            OperatorEnum.NE, // "!="
            OperatorEnum.ASSIGN,
            OperatorEnum.NOT,    // !
            OperatorEnum.SC_OR, // "||"
            OperatorEnum.SC_AND, // "&&"
            OperatorEnum.STAR // * true == 1.0, false = 0.0
        };
    }

    /**
     * getLeftOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return  new OperatorEnum[]
        { 
            OperatorEnum.EQ, // "=="
            OperatorEnum.NE, // "!="
            OperatorEnum.ASSIGN, // "="
            OperatorEnum.SC_OR,  // "||"
            OperatorEnum.SC_AND,  // "&&"
            OperatorEnum.STAR // * true == 1.0, false = 0.0
        };
    }

    /**
     * Returns OperatorEnum values for which this Term is a valid String operand
     * @return OperatorEnum[]
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getConcatenateOps()
     */
     @Override
     public OperatorEnum[] getConcatenateOps()
     {
         return EMPTY_OPERAND_OPS;
     }

    /**
     * numberEvaluation - unary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {   // There is no valid evaluation involving a boolean resulting in a number
        return new Integer(0);
    }

    /**
     * Evaluate a binary expression using this Term as the left term
     * @param leftTerm Term on left
     * @param operatorEnum2 OperatorEnum for one of +, -, *, /, &amp;, |, ^ or % 
     * @param rightTerm Term on right
     * @return sub class of Number with result
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum, Term rightTerm) 
    {   // There is no valid evaluation involving a boolean and another term resulting in a number except *
        boolean leftIsBool = leftTerm.getValueClass() == Boolean.class; 
        boolean rightIsBool = rightTerm.getValueClass() == Boolean.class; 
        BigDecimal right;
        BigDecimal left;
        if (leftIsBool)
            left =  ((Boolean)(leftTerm.getValue())).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
        else
            left = convertObject(leftTerm.getValue());
        if (rightIsBool)
            right =  ((Boolean)(rightTerm.getValue())).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
        else
            right = convertObject(rightTerm.getValue());
        return left.multiply(right);
    }

    /**
     * Evaluate less than (LT) and greater than (GT) using this Boolean as the left term
     * @param operatorEnum2 OperaorEnum.LT or OperaorEnum.GT
     * @param rightTerm Term on right
     * @return Boolean object
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#booleanEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum, Term rightTerm) 
    {   
        boolean right = ((Boolean)(rightTerm.getValue())).booleanValue();
        boolean left = ((Boolean)(leftTerm.getValue())).booleanValue();
        switch (operatorEnum)
        {
        case SC_OR:  return right || left; // "||"
        case SC_AND: return right && left; // "&&"
        case EQ:  return left == right; // "=="
        case NE:  return left != right; // "!="
        default:
        }
        return Boolean.FALSE;
    }

    /**
     * Convert value to BigDecimal, if not already of this type
     * @param object Value to convert
     * @return BigDecimal object
     */
    protected BigDecimal convertObject(Object object)
    {
            if (object instanceof BigDecimal)
                return (BigDecimal)(object);
            else
                return new BigDecimal(object.toString());
    }

}
