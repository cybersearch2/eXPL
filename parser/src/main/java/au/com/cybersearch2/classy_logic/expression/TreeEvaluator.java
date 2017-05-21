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
package au.com.cybersearch2.classy_logic.expression;

import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.assignRightToLeft;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.calculateBoolean;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isInvalidLeftUnaryOp;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isInvalidRightUnaryOp;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isNaN;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isValidLeftOperand;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isValidOperand;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isValidRightOperand;
import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.isValidStringOperation;

import java.math.BigDecimal;
import java.util.Locale;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.StringCloneable;
import au.com.cybersearch2.classy_logic.operator.CurrencyOperator;

/**
 * TreeEvaluator
 * Evaluates left and right operand trees, performing on-the-fly type conversion, if required.
 * The right and left operands are set by the super class and are obtained using Operand interface.
 * @author Andrew Bowley
 * 18May,2017
 */
abstract class TreeEvaluator extends DelegateOperand
{
    /** Operator as an enumerated value */
    protected OperatorEnum operatorEnum;
    /** Orientation - binary or unary (prefix or postfix) */
    protected Orientation orientation;
    /** Short circuit on boolean false result */
    protected boolean shortCircuitOnFalse;
    /** Short circuit on boolean true result */
    protected boolean shortCircuitOnTrue;
    /** Left term is not an number */
    protected boolean leftIsNaN;
    /** Right term is not an number */
    protected boolean rightIsNaN;
 
    /**
     * @param qname
     * @param operatorEnum
     */
    protected TreeEvaluator(QualifiedName qname, OperatorEnum operatorEnum, Orientation orientation)
    {
        super(qname);
        this.operatorEnum = operatorEnum;
        this.orientation = orientation;
        // Short circuit adds logic to return false to evaluate().
        // It applies to operators "&&" and "||".
        // Note there is a unary form used for short circuit expressions ie. starting with '?'
        shortCircuitOnFalse = operatorEnum == OperatorEnum.SC_AND; 
        shortCircuitOnTrue = operatorEnum == OperatorEnum.SC_OR; 
    }
    
    /**
     * @return the shortCircuitOnFalse
     */
    public boolean isShortCircuitOnFalse()
    {
        return shortCircuitOnFalse;
    }

    /**
     * @return the shortCircuitOnTrue
     */
    public boolean isShortCircuitOnTrue()
    {
        return shortCircuitOnTrue;
    }

    /**
     * @return the shortCircuitOnTrue
     */
    public boolean isShortCircuit()
    {
        return shortCircuitOnTrue || shortCircuitOnFalse;
    }

    @Override
    public EvaluationStatus evaluate(int id)
    {
        // Left hand operand. If null, then this is a unary prefix expression.
        Operand left = getLeftOperand();
        // Right hand operand. If null, then this is a unary postfix expression. */
        Operand right = getRightOperand();
        // Left term may trigger short circuit or skip (nothing more to do)
        EvaluationStatus evaluationStatus = 
            left == null ? EvaluationStatus.COMPLETE : evaluateLeft(left, right, id);
        if (evaluationStatus == EvaluationStatus.COMPLETE) 
        {
            // Remember if left is not a number
            leftIsNaN = isNaN(left, operatorEnum);
            if (right != null)
                evaluationStatus = evaluateRight(left, right, id);
        }
        return evaluationStatus;
    }

    abstract protected void setLeftOperand(Operand left);
    abstract protected void setRightOperand(Operand right);
    
    /**
     * Evaluate left term.
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus enum: SHORT_CIRCUIT, SKIP or COMPLETE
     */
    protected EvaluationStatus evaluateLeft(Operand left, Operand right, int id) 
    {
         // Only evaluate once between backups
        if (left.isEmpty())
            left.evaluate(id);
        if (left.isEmpty() && operatorEnum != OperatorEnum.ASSIGN)
            // If left hand term is empty, then cannot proceed. Maybe unification failed for this term.
            //throw new ExpressionException("Left term is empty");
            return EvaluationStatus.FAIL;
        if (!isValidLeftOperand(left, right, operatorEnum) || isInvalidLeftUnaryOp(right, operatorEnum))
        {   // TreeEvaluator not permited for type of Term value
            // NaN has precedence if operation has Numeric result
            if (!isNaN(left, operatorEnum) && ((right == null) || !isNaN(right, operatorEnum)))
                //throw new ExpressionException("Cannot evaluate " + (right == null ? unaryLeftToString() : binaryToString()));
                return EvaluationStatus.FAIL;
        }
        // Short circuit logic applies only to left term
        if (shortCircuitOnFalse) // Operator &&
        {
            if ("false".equals(left.getValue().toString()))
            {   // false && means short circuit
                if (right == null) // Unary && does not assign a value
                    return EvaluationStatus.SHORT_CIRCUIT;
                else
                    // Nothing more to do
                    return EvaluationStatus.SKIP;
            }
            else if (right == null)
                // Nothing more to do
                return EvaluationStatus.SKIP;
        }
        else if (shortCircuitOnTrue ) // Operator ||
        {   // true || means short circuit
            if ("true".equals(left.getValue().toString()))
            {
                if (right == null) // Unary || does not assign a value
                    return EvaluationStatus.SHORT_CIRCUIT;
                else
                    // Nothing more to do
                    return EvaluationStatus.SKIP;
            }
            else if (right == null)
                // Nothing more to do
                return EvaluationStatus.SKIP;
        }
        return EvaluationStatus.COMPLETE;
    }

    /**
     * Evaluate right term
     * @param left Left term
     * @param right Right term
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus enum
     */
    protected EvaluationStatus evaluateRight(Operand left, Operand right, int id)
    {
        // Only evaluate once between backups
        // Assume non-empty with id == 0 implies static intialization
        if (right.isEmpty() || (right.getId() == 0))
            right.evaluate(id);
        // If right hand term is empty, then cannot proceed. Maybe unification failed for this term.
        if (right.isEmpty())
            return EvaluationStatus.FAIL;
            // throw new ExpressionException("Right term is empty");
        if (orientation == Orientation.binary)
        {
            performOnFlyConversion(left, right);
            // Read back terms in case conversion occurred
            left = getLeftOperand();
            right = getRightOperand();
        }
        // Remember if right is not a number
        rightIsNaN = isNaN(right, operatorEnum);
        if ((!isValidRightOperand(right, operatorEnum) || isInvalidRightUnaryOp(left, operatorEnum)) &&
             !((left != null) && isValidStringOperation(left, operatorEnum))) 
        {   
            // TreeEvaluator not permited for type of Term value
            // NaN has precedence if operation has Numeric result
            if (!rightIsNaN && ((left == null) || !leftIsNaN))
                return EvaluationStatus.FAIL;
               //throw new ExpressionException("Cannot evaluate " + (left == null ? unaryRightToString() : binaryToString()));
        }
        return EvaluationStatus.COMPLETE;
    }

    /**
     * Calculate binary expression
     * @param leftTerm
     * @param rightTerm
     * @return Result as Object
     */
    protected Object calculate(Operand leftTerm, Operand rightTerm, int modificationId) 
    {
        switch (operatorEnum)
        {
        case ASSIGN: // "="
            return assignRightToLeft(leftTerm, rightTerm, modificationId);
        case LT: // "<"
        case GT: // ">"
        case EQ: // "=="
        case LE: // "<="
        case GE: // ">="
        case NE: // "!="
        case SC_OR: // "||"
        case SC_AND: // "&&"
            return rightTerm.getValue().equals(null) ? // == or != null
                    rightTerm.getOperator().booleanEvaluation(leftTerm, operatorEnum, leftTerm) :
                    leftTerm.getOperator().booleanEvaluation(leftTerm, operatorEnum, rightTerm);
        case PLUS: // "+"
        case PLUSASSIGN: // "+"
            if (isValidStringOperation(leftTerm, operatorEnum))
                return ((Concaten<?>)leftTerm).concatenate(rightTerm);
        case STAR: // "*"
            if ((leftTerm.getValueClass() == Boolean.class) || (rightTerm.getValueClass() == Boolean.class))
                return calculateBoolean(leftTerm, rightTerm);
        case MINUS: // "-"
        case SLASH: // "/"
        case BIT_AND: // "&"
        case BIT_OR: // "|"
        case XOR: // "^"
        case REM: // "%"
        case MINUSASSIGN: // "-"
        case STARASSIGN: // "*"
        case SLASHASSIGN: // "/"
        case ANDASSIGN: // "&"
        case ORASSIGN: // "|"
        case XORASSIGN: // "^"
        case REMASSIGN: // "%"
            // Prevent conversion of BigDecimal to Integer or Double by 
            // always selecting the BigDecimal term to perform the operation
            boolean leftIsBigDec = leftTerm.getValueClass() == BigDecimal.class;
            boolean rightIsBigDec = rightTerm.getValueClass() == BigDecimal.class;
            if (leftIsBigDec)
                return leftTerm.getOperator().numberEvaluation(leftTerm, operatorEnum, rightTerm);
            else if (rightIsBigDec)
                return rightTerm.getOperator().numberEvaluation(leftTerm, operatorEnum, rightTerm);
            // Prevent conversion of Double to Integer by 
            // always selecting the Double term to perform the operation
            boolean leftIsDouble = leftTerm.getValueClass() == Double.class;
            boolean rightIsDouble = rightTerm.getValueClass() == Double.class;
            if (leftIsDouble || !rightIsDouble)
                return leftTerm.getOperator().numberEvaluation(leftTerm, operatorEnum, rightTerm);
            else
                return rightTerm.getOperator().numberEvaluation(leftTerm, operatorEnum, rightTerm);
        case COMMA: // Comma operator builds a tree of operands instead of performing a calculation
            return new Null(); // Set dummy value so this variable is no longer empty
        default:
        }
        return null;
    }

    /**
     * When number operation is to be performed with string on one side, 
     * convert the string to a number of same type as opposite.
     * Also convert decimal to currency for binary currency operation.
     * Note that left or right operand in super class may be replaced by conversion.
     */
    protected void performOnFlyConversion(Operand left, Operand right)
    {
        if (!performStringConversion(left, right))
            performDecimalConversion(left, right);
    }

    /**
     * Perform string conversion to number type, if appropriate
     * @param left Left term
     * @param right Right term
     * @return flag set true in conversion occurred
     */
    protected boolean performStringConversion(Operand left, Operand right)
    {
        boolean isLeftString = left instanceof StringOperand;
        boolean isRightString = right instanceof StringOperand;
        if ((!isLeftString && !isRightString) || (isLeftString && isRightString))
            return (isLeftString && isRightString);
        if (isRightString)
        {
            if (isValidOperand(left, operatorEnum, left.getOperator().getLeftOperandOps()) &&
                (left.getOperator().getTrait() instanceof StringCloneable))
            {
                StringCloneable stringCloneable = (StringCloneable)left.getOperator().getTrait();
                StringOperand stringOperand = (StringOperand)right;
                setRightOperand(stringCloneable.cloneFromOperand(stringOperand));
            }
            return true;
        }
        if (isValidOperand(right, operatorEnum, right.getOperator().getRightOperandOps()) &&
             (right.getOperator().getTrait() instanceof StringCloneable))
        {
            StringCloneable stringCloneable = (StringCloneable)right.getOperator().getTrait();
            StringOperand stringOperand = (StringOperand)left;
            setLeftOperand(stringCloneable.cloneFromOperand(stringOperand));
        }
        return true;
    }

    /**
     * Perform decimal conversion to currency type, if appropriate
     * @param left Left term
     * @param right Right term
     * @return flag set true in conversion occurred
     */
    protected boolean performDecimalConversion(Operand left, Operand right)
    {
        boolean isLeftDecimal = left instanceof BigDecimalOperand;
        boolean isRightDecimal = right instanceof BigDecimalOperand;
        if ((!isLeftDecimal && !isRightDecimal) || (isLeftDecimal && isRightDecimal))
            return (isLeftDecimal && isRightDecimal);
        if (isRightDecimal)
        {
            if (left.getOperator().getTrait().getOperandType() == OperandType.CURRENCY)
                convertToCurrency((BigDecimalOperand)left);
        }
        else
        {
            if (right.getOperator().getTrait().getOperandType() == OperandType.CURRENCY)
                convertToCurrency((BigDecimalOperand)right);
        }
       return true;
    }

    /**
     * Conver given decimal to currency type
     * @param bigDecimalOperand Decimal term to convert
     */
    protected void convertToCurrency(BigDecimalOperand bigDecimalOperand)
    {
        Locale locale = bigDecimalOperand.getOperator().getTrait().getLocale();
        CurrencyOperator currencyOperator = new CurrencyOperator();
        bigDecimalOperand.setOperator(currencyOperator);
        currencyOperator.getTrait().setLocale(locale);
    }

}
