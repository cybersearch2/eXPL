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

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.list.Cursor;
import au.com.cybersearch2.classy_logic.trait.CursorTrait;

/**
 * CursorrOperator
 * @see DelegateType.CURSOR
 * @see IntegerOperand
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class CursorOperator implements Operator
{
    /** Behaviours for localization and specialization of Integer operands */
    private CursorTrait cursorTrait;
    
    /**
     * Construct IntegerOperator object
     */
    public CursorOperator()
    {
        super();
        cursorTrait = new CursorTrait();
    }

    /**
     * getTrait
     * @see au.com.cybersearch2.classy_logic.interfaces.Operator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return cursorTrait;
    }

    /**
     * getRightOperandOps
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        //return EMPTY_OPERAND_OPS;
        return  new OperatorEnum[]
        { 
                OperatorEnum.PLUS,
                OperatorEnum.MINUS,
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
                OperatorEnum.INCR,
                OperatorEnum.DECR,
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
     * Unary numberEvaluation - unary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term cursorTerm) 
    {
        Variable cursorOperand = (Variable)cursorTerm;
        Cursor cursor = (Cursor)cursorOperand.getRightOperand();
        int right = cursor.getIndex();
        long calc = 0;
        switch (operatorEnum2)
        {
        case INCR: calc = ++right; break;
        case DECR: calc = --right; break;
        case PLUS: return new Long(cursor.forward());
        case MINUS: return new Long(cursor.reverse());
        default:
        }
        cursor.setIndex(right);
        return new Long(calc);
    }

    /**
     * Binary numberEvaluation - binary
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(Term, OperatorEnum, Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return Long.valueOf(0L);
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
        return false;
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
            return cursorTrait.parseValue(object.toString());
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
            return cursorTrait.parseValue(object.toString()).intValue();
        else if (Number.class.isAssignableFrom(clazz))
            return ((Number)object).intValue();
        else return 0;
    }
}
