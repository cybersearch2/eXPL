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
package au.com.cybersearch2.classy_logic.helper;

import au.com.cybersearch2.classy_logic.expression.DelegateOperand;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * EvaluationUtils
 * @author Andrew Bowley
 * 3 Sep 2015
 */
public class EvaluationUtils
{
    /** Not a number */
    public  static final String NAN = "NaN"; //Double.valueOf(Double.NaN).toString();
    
   /**
     * Returns true if left operand permited for operator used in expression
     * @param leftTerm Left Operand
     * @return Flag set true to indicate valid left operand
     */
    public static boolean isValidLeftOperand(Operand leftTerm, Operand rightTerm, OperatorEnum operatorEnum) 
    {
        for (OperatorEnum operatorEnum2: leftTerm.getLeftOperandOps())
            if (operatorEnum2 == operatorEnum)
                return true;
        if (isValidStringOperation(leftTerm, operatorEnum) || 
            // Comma operator valid if right operand present    
            ((operatorEnum == OperatorEnum.COMMA) && (rightTerm != null)))
            return true;
        return false;
    }

    /**
     * Returns flag to indicate if supplied term is allowed to perform String operations
     * @param term
     * @return boolean
     */
    public static boolean isValidStringOperation(Operand term, OperatorEnum operatorEnum)
    {
        for (OperatorEnum operatorEnum2: term.getStringOperandOps())
            if (operatorEnum2 == operatorEnum)
                return true;
        return false;
    }
    
    /**
     * Returns true if right operand permited for operator used in expression
     * @param leftTerm Left Operand. If not null then operation is binary
     * @return Flag set true to indicate invalid right operand
     */
    public static boolean isInvalidRightUnaryOp(Operand leftTerm, OperatorEnum operatorEnum)
    {
        if (leftTerm != null)
            return false;
        if ((operatorEnum == OperatorEnum.INCR) || 
             (operatorEnum == OperatorEnum.DECR) ||
             (operatorEnum == OperatorEnum.NOT) ||
             (operatorEnum == OperatorEnum.TILDE) || 
             (operatorEnum == OperatorEnum.PLUS) || 
             (operatorEnum == OperatorEnum.MINUS))
            return false;
        return true;
    }

    /**
     * Returns true if right operand permited for operator used in expression
     * @param rightTerm Right Operand
     * @return Flag set true to indicate valid right operand
     */
    public static boolean isValidRightOperand(Operand rightTerm, OperatorEnum operatorEnum) 
    {
        for (OperatorEnum operatorEnum2: rightTerm.getRightOperandOps())
            if (operatorEnum2 == operatorEnum)
                return true;
        // Only Comma operator is valid at this point
        return operatorEnum == OperatorEnum.COMMA;
    }

    public static boolean isValidOperand(Operand term, OperatorEnum operatorEnum, OperatorEnum[] operatorEnums) 
    {
        for (OperatorEnum operatorEnum2: operatorEnums)
            if (operatorEnum2 == operatorEnum)
                return true;
        return false;
    }
    
    /**
     * Returns true if left operand permited for operator used in expression
     * @param rightTerm Right Operand. If not null then operation is binary
     * @return Flag set true to indicate invalid left operand
     */
    public static boolean isInvalidLeftUnaryOp(Operand rightTerm, OperatorEnum operatorEnum)
    {
        if (rightTerm != null)
            return false;
        if ((operatorEnum == OperatorEnum.INCR) || 
            (operatorEnum == OperatorEnum.DECR) ||
            (operatorEnum == OperatorEnum.SC_AND) ||
            (operatorEnum == OperatorEnum.SC_OR))
            return false;
        return true;
    }


    /**
     * Returns flag true if number is not suitable for Number evaluation. 
     * Checks for number converted to double has value = Double.NaN
     * @param number Object value perporting to be Number subclass
     * @return Flag set true to indicate not a number
     */
    public static boolean isNaN(Object number)
    {
        if ((number == null) || (!(number instanceof Number || number instanceof Boolean)))
            return true;
        return number.toString().equals(NAN);
    }

    /**
     * Returns flag true if operand value is NaN 
     * @param operand Operand to test
     * @return Flag set true to indicate not a number
     */
    public static boolean isNaN(Operand operand, OperatorEnum operatorEnum)
    {
        if (operand.isEmpty())
            return false;
        Object value = operand.getValue();
        switch (operatorEnum)
        {
        case ASSIGN: // "="
        case PLUS: // "+"
        case MINUS: // "-"
        case STAR: // "*"
        case SLASH: // "/"
        case BIT_AND: // "&"
        case BIT_OR: // "|"
        case XOR: // "^"
        case REM: // "%"
        case TILDE: // "~"
        case INCR:
        case DECR:
        case PLUSASSIGN: // "+"
        case MINUSASSIGN: // "-"
        case STARASSIGN: // "*"
        case SLASHASSIGN: // "/"
        case ANDASSIGN: // "&"
        case ORASSIGN: // "|"
        case XORASSIGN: // "^"
        case REMASSIGN: // "%"
            return (value instanceof Number) && value.toString().equals(NAN);
        default:
        }
        return false;
    }

    /**
     * Assign right term value to left term
     * @param leftTerm Left Operand
     * @param rightTerm Riht Operand
     * @param modificationId Modification version
     * @return Value as Object
     */
    public static Object assignRightToLeft(Operand leftTerm, Operand rightTerm, int modificationId)
    {
        Object value = rightTerm.getValue();
        leftTerm.assign(rightTerm);
        // When the value class is not supported as a delegate, substitute a Null object.
        // This is defensive only as Operands are expected to only support Delegate classes
        return DelegateOperand.isDelegateClass(rightTerm.getValueClass()) ? value : new Null();
    }

    /**
     * Calculate a number using a boolean term converted to 1.0 for true and 0.0 for false.
     * At least one parameter is expected to contain a Boolean object
     * @param leftTerm Left operand
     * @param rightTerm Right operand
     * @return Number object (actualy BigDecimal)
     */
    public static Number calculateBoolean(Operand leftTerm, Operand rightTerm)
    {
        if (leftTerm.getValueClass() == Boolean.class)
            return leftTerm.numberEvaluation(leftTerm, OperatorEnum.STAR, rightTerm);
        return rightTerm.numberEvaluation(leftTerm, OperatorEnum.STAR, rightTerm);
    }
}
