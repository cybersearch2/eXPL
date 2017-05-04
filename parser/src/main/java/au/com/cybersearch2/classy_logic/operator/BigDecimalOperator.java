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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.BigDecimalTrait;

/**
 * BigDecimalOperator
 * @see DelegateType#DECIMAL
 * @see BigDecimalOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class BigDecimalOperator extends ExpressionOperator implements LocaleListener
{
    /** Behaviours for localization and specialization of Decimal operands */
    protected BigDecimalTrait bigDecimalTrait;

    /** Construct BigDecimalOperator object */
    public BigDecimalOperator()
    {
        bigDecimalTrait = new BigDecimalTrait();
    }

    /**
     * getTrait
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return bigDecimalTrait;
    }
 
    /**
     * onScopeChange
     * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(Scope)
     */
    @Override
    public void onScopeChange(Scope scope)
    {
        bigDecimalTrait.setLocale(scope.getLocale());
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
            OperatorEnum.ASSIGN,
            OperatorEnum.LT, // "<"
            OperatorEnum.GT ,// ">"
            OperatorEnum.EQ, // "=="
            OperatorEnum.LE, // "<="
            OperatorEnum.GE, // ">="
            OperatorEnum.NE, // "!="
            OperatorEnum.PLUS,
            OperatorEnum.MINUS,
            OperatorEnum.STAR,
            OperatorEnum.SLASH,
            OperatorEnum.REM,       
            OperatorEnum.PLUSASSIGN,
            OperatorEnum.MINUSASSIGN,
            OperatorEnum.STARASSIGN,
            OperatorEnum.SLASHASSIGN,
            OperatorEnum.REMASSIGN          
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
                OperatorEnum.ASSIGN,
                OperatorEnum.LT, // "<"
                OperatorEnum.GT ,// ">"
                OperatorEnum.EQ, // "=="
                OperatorEnum.LE, // "<="
                OperatorEnum.GE, // ">="
                OperatorEnum.NE, // "!="
                OperatorEnum.PLUS,
                OperatorEnum.MINUS,
                OperatorEnum.STAR,
                OperatorEnum.SLASH,
                OperatorEnum.REM,       
                OperatorEnum.PLUSASSIGN,
                OperatorEnum.MINUSASSIGN,
                OperatorEnum.STARASSIGN,
                OperatorEnum.SLASHASSIGN,
                OperatorEnum.REMASSIGN          
        };
    }

    /**
     * numberEvaluation - unary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        BigDecimal right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        BigDecimal calc = BigDecimal.ZERO;
        switch (operatorEnum2)
        {
        case PLUS:  calc = right.plus(); break;
        case MINUS: calc = right.negate(); break;  
        default:
        }
        return calc;
    }

    /**
     * numberEvaluation - binary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        BigDecimal right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        BigDecimal left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
        BigDecimal calc = BigDecimal.ZERO;
        switch (operatorEnum2)
        {
        case PLUSASSIGN: // "+="
        case PLUS:  calc = left.add(right); break;
        case MINUSASSIGN: // "-="
        case MINUS:     calc = left.subtract(right); break;
        case STARASSIGN: // "*="
        case STAR:      calc = calculateTimes(left, right); break;
        case SLASHASSIGN: // "/="
        case SLASH:     calc = calculateDiv(left, right); break;
        case REMASSIGN: // "%="
        case REM:       calc = left.remainder(right); break;
        default:
        }
        return calc;
    }

    /**
     * booleanEvaluation
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#booleanEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        boolean calc = false;
        BigDecimal leftBigDec = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
        BigDecimal righttBigDec = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        switch (operatorEnum2)
        {
        case EQ:  calc = leftBigDec.compareTo(righttBigDec) == 0; break; // "=="
        case NE:  calc = leftBigDec.compareTo(righttBigDec) != 0; break; // "!="
        case LT:  calc = leftBigDec.compareTo(righttBigDec) < 0; break; // "<"
        case GT:  calc = leftBigDec.compareTo(righttBigDec) > 0; break; // ">"
        case LE:  calc = leftBigDec.compareTo(righttBigDec) <= 0; break; // "<="
        case GE:  calc = leftBigDec.compareTo(righttBigDec) >= 0; break; // ">="
        default:
        }
        return calc;
    }

    /**
     * Convert value to BigDecimal, if not already of this type
     * @param object Value to convert
     * @param clazz Value class
     * @return BigDecimal object
     */
    public BigDecimal convertObject(Object object, Class<?> clazz)
    {
        if (clazz == BigDecimal.class)
            return (BigDecimal)(object);
        else if (clazz == String.class)
            return bigDecimalTrait.parseValue(object.toString());
        else
            try
            {
                return new BigDecimal(object.toString());
            }
            catch (NumberFormatException e)
            {
                throw new ExpressionException(object.toString() + " is not convertible to a Decimal type");    
            }
    }

    /**
     * Binary multiply. Override to adjust rounding. 
     * @param right BigDecimal object left term
     * @param left BigDecimal object reight term
     * @return BigDecimal object
     */
    protected BigDecimal calculateTimes(BigDecimal right, BigDecimal left)
    {
        return left.multiply(right);
    }

    /**
     * Binary divide. Override to adjust rounding. 
     * @param right BigDecimal object left term
     * @param left BigDecimal object reight term
     * @return BigDecimal object
     */
    protected BigDecimal calculateDiv(BigDecimal right, BigDecimal left)
    {
        return left.divide(right, BigDecimal.ROUND_FLOOR);
    }


}
