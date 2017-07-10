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
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.IntegerTrait;

/**
 * IntegerOperator
 * @see DelegateType.INTEGER
 * @see IntegerOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class IntegerOperator extends ExpressionOperator implements LocaleListener
{
    /** Behaviours for localization and specialization of Integer operands */
    private IntegerTrait integerTrait;
    
    /**
     * Construct IntegerOperator object
     */
    public IntegerOperator()
    {
        super();
        integerTrait = new IntegerTrait();
    }

    /**
     * getTrait
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return integerTrait;
    }

    /**
     * onScopeChange
     * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
     */
    @Override
    public void onScopeChange(Scope scope)
    {
        integerTrait.setLocale(scope.getLocale());
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
            OperatorEnum.BIT_AND,
            OperatorEnum.BIT_OR,
            OperatorEnum.XOR,
            OperatorEnum.REM,   
            OperatorEnum.LSHIFT,
            OperatorEnum.RSIGNEDSHIFT,
            OperatorEnum.RUNSIGNEDSHIFT,
            OperatorEnum.INCR,
            OperatorEnum.DECR,
            OperatorEnum.PLUSASSIGN,
            OperatorEnum.MINUSASSIGN,
            OperatorEnum.STARASSIGN,
            OperatorEnum.SLASHASSIGN,
            OperatorEnum.ANDASSIGN,
            OperatorEnum.ORASSIGN,
            OperatorEnum.XORASSIGN,
            OperatorEnum.REMASSIGN,       
            OperatorEnum.LSHIFTASSIGN, // "<<="
            OperatorEnum.RSIGNEDSHIFTASSIGN, // ">>="
            OperatorEnum.RUNSIGNEDSHIFTASSIGN, // ">>>="
            OperatorEnum.TILDE      
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
                OperatorEnum.BIT_AND,
                OperatorEnum.BIT_OR,
                OperatorEnum.XOR,
                OperatorEnum.REM,       
                OperatorEnum.LSHIFT,
                OperatorEnum.RSIGNEDSHIFT,
                OperatorEnum.RUNSIGNEDSHIFT,
                OperatorEnum.INCR,
                OperatorEnum.DECR,
                OperatorEnum.PLUSASSIGN,
                OperatorEnum.MINUSASSIGN,
                OperatorEnum.STARASSIGN,
                OperatorEnum.SLASHASSIGN,
                OperatorEnum.ANDASSIGN,
                OperatorEnum.ORASSIGN,
                OperatorEnum.XORASSIGN,
                OperatorEnum.REMASSIGN,
                OperatorEnum.LSHIFTASSIGN, // "<<="
                OperatorEnum.RSIGNEDSHIFTASSIGN, // ">>="
                OperatorEnum.RUNSIGNEDSHIFTASSIGN // ">>>="
        };
    }

    /**
     * Unary numberEvaluation - unary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        int right = convertIntObject(rightTerm.getValue(), rightTerm.getValueClass());
        long calc = 0;
        switch (operatorEnum2)
        {
        case PLUS:  calc = +right; break;
        case MINUS: calc = -right; break;  
        case TILDE: calc = ~right; break;
        case INCR: calc = ++right; break;
        case DECR: calc = --right; break;
        default:
        }
        return new Long(calc);
    }

    /**
     * Binary numberEvaluation - binary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        long right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        long left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
        long calc = 0;
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
        case ANDASSIGN: // "&="
        case BIT_AND:   calc = left & right; break;
        case ORASSIGN: // "|="
        case BIT_OR:    calc = left | right; break;
        case XORASSIGN: // "^="
        case XOR:       calc = left ^ right; break;
        case REMASSIGN: // "%="
        case REM:       calc = left % right; break;
        case LSHIFT:
        case LSHIFTASSIGN: calc = left << right; break;
        case RSIGNEDSHIFT: 
        case RSIGNEDSHIFTASSIGN: calc = left >> right; break;
        case RUNSIGNEDSHIFT: 
        case RUNSIGNEDSHIFTASSIGN: calc = left >>> right; break;
        default:
        }
        return new Long(calc);
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
        long right = convertObject(rightTerm.getValue(), rightTerm.getValueClass());
        long left = convertObject(leftTerm.getValue(), leftTerm.getValueClass());
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
     * Convert value to long, if not already of this type
     * @param object Value to convert
     * @param clazz Value class
     * @return long
     */
    public long convertObject(Object object, Class<?> clazz)
    {
        if (clazz == Long.class)
            return (Long)object;
        else if (clazz == String.class)
            return integerTrait.parseValue(object.toString());
        else if (Number.class.isAssignableFrom(clazz))
            return ((Number)object).longValue();
        else return 0L;
    }

    /**
     * Convert value of integer type to long, if not already of this type
     * @param object Value to convert
     * @param clazz Value class
     * @return long
     */
    protected int convertIntObject(Object object, Class<?> clazz)
    {
        if (clazz == Long.class)
            return ((Long)object).intValue();
        else if (clazz == String.class)
            return integerTrait.parseValue(object.toString()).intValue();
        else if (Number.class.isAssignableFrom(clazz))
            return ((Number)object).intValue();
        else return 0;
    }
}
