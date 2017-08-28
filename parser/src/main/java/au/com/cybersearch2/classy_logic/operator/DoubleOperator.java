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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.DoubleOperand;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DoubleTrait;

/**
 * DoubleOperator
 * @see DelegateType#DOUBLE
 * @see DoubleOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class DoubleOperator implements Operator, LocaleListener
{
    /** Behaviours for localization and specialization of Double operands */
    private DoubleTrait doubleTrait;
    
    /**
     * 
     */
    public DoubleOperator()
    {
        doubleTrait = new DoubleTrait();
    }

    @Override
    public Trait getTrait()
    {
        return doubleTrait;
    }
    
    @Override
    public void setTrait(Trait trait)
    {
        if (!DoubleTrait.class.isAssignableFrom(trait.getClass()))
            return; //throw new ExpressionException(trait.getClass().getSimpleName() + " is not a compatible Trait");
        doubleTrait = (DoubleTrait) trait;
    }
    
    @Override
    public void onScopeChange(Scope scope)
    {
        doubleTrait.setLocale(scope.getLocale());
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
            OperatorEnum.PLUSASSIGN,
            OperatorEnum.MINUSASSIGN,
            OperatorEnum.STARASSIGN,
            OperatorEnum.SLASHASSIGN
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
                OperatorEnum.PLUSASSIGN,
                OperatorEnum.MINUSASSIGN,
                OperatorEnum.STARASSIGN,
                OperatorEnum.SLASHASSIGN
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
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        double right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        double calc = 0;
        switch (operatorEnum2)
        {
        case PLUS:  calc = +right; break;
        case MINUS: calc = -right; break;  
        default:
        }
        return new Double(calc);
    }

    /**
     * numberEvaluation - binary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        double right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        double left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
        double calc = 0;
        switch (operatorEnum2)
        {
        case PLUSASSIGN: // "+="
        case PLUS:  calc = left + right; break;
        case MINUSASSIGN: // "-="
        case MINUS:  calc = left - right; break;
        case STARASSIGN: // "*="
        case STAR:      calc = left * right; break;
        case SLASHASSIGN: // "/="
        case SLASH:     calc = left / right; break;
        default:
        }
        return new Double(calc);
    }

    /**
     * Evaluate relational operation using this Term as the left term
     * @param leftTerm Term on left
     * @param operatorEnum2 Operator
     * @param rightTerm Term on right
     * @return Boolean result
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#booleanEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        double right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        double left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
        boolean calc = false;
        switch (operatorEnum2)
        {
        case LT:  calc = left < right; break;
        case GT:  calc = left > right; break;
        case EQ:  calc = left == right; break; // "=="
        case LE:  calc = left <= right; break; // "<="
        case GE:  calc = left >= right; break; // ">="
        case NE:  calc = left != right; break; // "!="
        default:
        }
        return calc;
    }

    /**
     * Convert value to double, if not already of this type
     * @param object Value to convert
     * @param clazz Value class
     * @return double
     */
    public double convertObject(Object object, Class<?> clazz)
    {
        if (clazz == Double.class)
            return (Double)object;
        else if (clazz == String.class)
            return doubleTrait.parseValue(object.toString());
        else if (Number.class.isAssignableFrom(clazz))
            return ((Number)object).doubleValue();
        else return Double.NaN;
    }
}
