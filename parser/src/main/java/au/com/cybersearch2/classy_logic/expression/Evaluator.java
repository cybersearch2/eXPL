/**
 * 
 */
package au.com.cybersearch2.classy_logic.expression;

import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

import static au.com.cybersearch2.classy_logic.helper.EvaluationUtils.*;

/**
 * Evaluator
 * Performs evaluation of unary and binary expressions.
 * The operator is specified as a String eg. "+", and represented interanally as an enum.
 * As an Operand, it is related to Variable, as value type may be unknown until an evaluation has occured.
 * An Evaluator is allowed to be annoymous, in which case it acts as a simple parameter of local scope.
 * @author Andrew Bowley
 *
 * @since 02/10/2010
 * @see DelegateOperand
 * @see Variable
 */
public class Evaluator extends DelegateOperand
{
	
	/** Right hand operand. If null, then this is a unary postfix expression. */
	protected Operand right;
    /** Left hand operand. If null, then this is a unary prefix expression. */
	protected Operand left;
    /** Operator as an enumerated value */
	protected OperatorEnum operatorEnum;
	/** Short circuit on boolean false result */
	protected boolean shortCircuitOnFalse;
	/** Short circuit on boolean true result */
	protected boolean shortCircuitOnTrue;

	/**
	 * Create Evaluator object for postfix unary expression 
	 * @param leftTerm Left operand
	 * @param operator Text representation of operator
	 */
	public Evaluator(Operand leftTerm, String operator)
	{
		this(leftTerm, operator, (Operand)null);
	}

	/**
	 * Create named Evaluator object for postfix unary expression 
     * @param qname Qualified name of variable
	 * @param leftTerm Left operand
	 * @param operator Text representation of operator
	 */
	public Evaluator(QualifiedName qname, Operand leftTerm, String operator)
	{
		this(qname, leftTerm, operator, (Operand)null);
	}

	/**
	 * Create Evaluator object for prefix unary expression 
	 * @param operator Text representation of operator
	 * @param rightTerm  Right operand
	 */
	public Evaluator(String operator, Operand rightTerm)
	{
		this((Operand)null, operator, rightTerm);
	}

	/**
	 * Create named Evaluator object for prefix unary expression 
     * @param qname Qualified name of variable
	 * @param rightTerm Operand
	 * @param operator 
	 */
	public Evaluator(QualifiedName qname, String operator, Operand rightTerm)
	{
		this(qname, (Operand)null, operator, rightTerm);
	}

	/**
	 * Create Evaluator object for binary expression 
	 * @param leftTerm Left perand
	 * @param operator Text representation of operator
	 * @param rightTerm Right operand
	 */
	public Evaluator(Operand leftTerm, String operator, Operand rightTerm)
	{
		this(QualifiedName.ANONYMOUS, leftTerm, operator, rightTerm);
	}

	/**
	 * Create named Evaluator object for binary expression 
     * @param qname Qualified name of variable
	 * @param leftTerm Operand
	 * @param operator 
	 * @param rightTerm Operand
	 */
	public Evaluator(QualifiedName qname, Operand leftTerm, String operator, Operand rightTerm)
	{
		super(qname);
	    this.right = rightTerm;
	    this.left = leftTerm;
	    operatorEnum = OperatorEnum.convertOperator(operator);
	    // Short circuit adds logic to return false to evaluate().
	    // It applies to operators "&&" and "||".
	    // Note there is a unary form used for short circuit expressions ie. starting with '?'
	    shortCircuitOnFalse = operatorEnum == OperatorEnum.SC_AND; 
	    shortCircuitOnTrue = operatorEnum == OperatorEnum.SC_OR; 
	}

	/**
	 * Execute expression and set Evaluator value with result
	 * There is a precedence for errors, in highest to lowest:<br/>
	 * Term is empty<br/>
	 * Number is NaN<br/>
	 * Operation not permited for type of Term value<br/>
	 * Term value is null
	 * 
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
    @Override
	public EvaluationStatus evaluate(int id)
	{
    	boolean leftIsNaN = false;
    	boolean rightIsNaN = false;
    	if (left != null)
    	{   // Left term may trigger short circuit or skip (nothing more to do)
    		EvaluationStatus evaluationStatus = evaluateLeft(id);
    		if (evaluationStatus == EvaluationStatus.SKIP)
    			return EvaluationStatus.COMPLETE;
    		if (evaluationStatus == EvaluationStatus.SHORT_CIRCUIT)
    			return shortCircuitOnTrue || shortCircuitOnFalse ? EvaluationStatus.SHORT_CIRCUIT : EvaluationStatus.SKIP;
    		// Remember if left is not a number
    		leftIsNaN = isNaN(left, operatorEnum);
    	}
    	if (right != null)
    	{
    		// Only evaluate once between backups
    	    // Assume non-empty with id == 0 implies static intialization
    		if (right.isEmpty() || (right.getId() == 0))
    		    right.evaluate(id);
    		// If right hand term is empty, then cannot proceed. Maybe unification failed for this term.
    		if (right.isEmpty())
	   			throw new ExpressionException("Right term is empty");
    		// Remember if right is not a number
   			rightIsNaN = isNaN(right, operatorEnum);
    		if ((!isValidRightOperand(right, operatorEnum) || isInvalidRightUnaryOp(left, operatorEnum)) &&
    			 !((left != null) && isValidStringOperation(left, operatorEnum))) 
    		{   
     			// Operation not permited for type of Term value
    			// NaN has precedence if operation has Numeric result
     			if (!rightIsNaN && ((left == null) || !leftIsNaN))
    				throw new ExpressionException("Cannot evaluate " + (left == null ? unaryRightToString() : binaryToString()));
     		}
    	}
    	// Delegate can be set in advance if result is boolean
    	// Otherwise, delegate will be set on value assigment
    	presetDelegate();
    	// Now perform evaluation, depending on status of left and right terms
		Object result = null;
	   	if (right == null)
	   	{   // Postfix unary operation.
	   	    // Result will automatically be NaN if left Term value is NaN
	   		result = left.getValue();
	   		if (!leftIsNaN)
	   		{
	   			Number post = left.numberEvaluation(operatorEnum, left);
	   			left.setValue(post);
	   		}
	   	}
	   	else if (left == null)
	   	{   // Prefix unary operation.
	   		if (rightIsNaN)
	   			result = right.getValue();
	   		else
	   			result = doPrefixUnary();
	   	}
	   	else
	   	{
	   		if (leftIsNaN)
	   			result = left.getValue();
	   		else if (rightIsNaN)
	   			result = right.getValue();
	   		else
	   		{
		   		result = calculate(left, right, id);
		   		// Assign operation updates left term with result
				switch (operatorEnum)
				{
		   			case PLUSASSIGN: // "+"
		   			case MINUSASSIGN: // "-"
		   			case STARASSIGN: // "*"
		   			case SLASHASSIGN: // "/"
		   			case ANDASSIGN: // "&"
		   			case ORASSIGN: // "|"
		   			case XORASSIGN: // "^"
		   			case REMASSIGN: // "%"
	                left.setValue(result);
	                default:
		   		}
	   		}
	   	}
	   	return setResult(result, id);
	}

	/**
	 * Backup to intial state if given id matches id assigned on unification or given id = 0. 
	 * @param modifierId Identity of caller. 
	 * @return boolean true if backup occurred
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
	 */
	@Override
	public boolean backup(int modifierId)
	{
		boolean backupOccurred = super.backup(modifierId);
		if ((right != null) && right.backup(modifierId)) 
			backupOccurred = true;
		if ((left != null) && left.backup(modifierId))
			backupOccurred = true;
		return backupOccurred;
	}

	/**
 	 * Evaluate a unary expression 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
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
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		if (isNaN(rightTerm.getValue()) || isNaN(leftTerm.getValue()))
			return new Double("NaN");
		return super.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
	}

	/**
	 * Returns left child of Operand
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperand()
	 */
	@Override
	public Operand getLeftOperand() 
	{
		return left;
	}

	/**
	 * Returns right child of Operand
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
	 */
	@Override
	public Operand getRightOperand() 
	{
		return right;
	}

	/**
	 * Returns what is to be evaluated, if empty, otherwise the value
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		if (empty)
		{   // Return evaluation to perform
			if (left == null)
				return  unaryRightToString();
			else if (right == null)
				return unaryLeftToString();
			return binaryToString();
		}
		// Return value
		return super.toString();
	}

    /**
     * Perform prefix unary operation with right term: ++, --, !,  ~, + or -
     * @return Object Result
     */
    protected Object doPrefixUnary() 
    {
        if ((operatorEnum == OperatorEnum.INCR) || (operatorEnum == OperatorEnum.DECR))
        {   // ++ or --
            Number pre = right.numberEvaluation(operatorEnum, right);
            right.setValue(pre);
            return pre;
        }
        else if (operatorEnum == OperatorEnum.NOT)
        {   // !
            boolean flag = true;
            if (right.getValue() instanceof Boolean)
                flag = ((Boolean)(right.getValue())).booleanValue();
            return new Boolean(!flag);
        }
        else if ((operatorEnum == OperatorEnum.TILDE) || (operatorEnum == OperatorEnum.PLUS) || (operatorEnum == OperatorEnum.MINUS))
            // ~ is unary so left is ignored
            return right.numberEvaluation(operatorEnum, right);
        return null;
    }

    /**
     * Evaluate left term.
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus enum: SHORT_CIRCUIT, SKIP or COMPLETE
     */
    protected EvaluationStatus evaluateLeft(int id) 
    {
        // Only evaluate once between backups
        if (left.isEmpty())
           left.evaluate(id);
        if (left.isEmpty() && operatorEnum != OperatorEnum.ASSIGN)
            // If left hand term is empty, then cannot proceed. Maybe unification failed for this term.
            throw new ExpressionException("Left term is empty");
        if (!isValidLeftOperand(left, right, operatorEnum) || isInvalidLeftUnaryOp(right, operatorEnum))
        {   // Operation not permited for type of Term value
            // NaN has precedence if operation has Numeric result
            if (!isNaN(left, operatorEnum) && ((right == null) || !isNaN(right, operatorEnum)))
                throw new ExpressionException("Cannot evaluate " + (right == null ? unaryLeftToString() : binaryToString()));
        }
        // Short circuit logic applies only to left term
        if (shortCircuitOnFalse) // Operator &&
        {
            if ("false".equals(left.getValue().toString()))
            {   // false && means short circuit
                if (right == null) // Unary && does not assign a value
                {
                    this.id = id;
                    return EvaluationStatus.SHORT_CIRCUIT;
                }
                else
                {   // Nothing more to do
                    setResult(Boolean.FALSE, id);
                    return EvaluationStatus.SKIP;
                }
            }
            else if (right == null)
            {   // Nothing more to do
                this.id = id;
                return EvaluationStatus.SKIP;
            }
        }
        else if (shortCircuitOnTrue ) // Operator ||
        {   // true || means short circuit
            if ("true".equals(left.getValue().toString()))
            {
                if (right == null) // Unary || does not assign a value
                {
                    this.id = id;
                    return EvaluationStatus.SHORT_CIRCUIT;
                }
                else
                {   // Nothing more to do
                    setResult(Boolean.TRUE, id);
                    return EvaluationStatus.SKIP;
                }
            }
            else if (right == null)
            {   // Nothing more to do
                this.id = id;
                return EvaluationStatus.SKIP;
            }
        }
        return EvaluationStatus.COMPLETE;
    }

    /**
     * Set value and determine evaluation return value
     * @param result Value to set
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true to continue, false to short circuit
     */
    protected EvaluationStatus setResult(Object result, int id) 
    {
        boolean continueFlag = true;
        boolean trueResult = false;
        boolean falseResult = false;
        if (result != null)
        {
            setValue(result);
            
            this.id = id;
            // Check for boolean result
            switch(operatorEnum)
            {
            case NOT:// "!"
            case LT: // "<"
            case GT: // ">"
            case EQ: // "=="
            case LE: // "<="
            case GE: // ">="
            case NE: // "!="
            case SC_OR: // "||"
            case SC_AND: // "&&"
            boolean flag = ((Boolean)result).booleanValue();
                trueResult = flag;
                falseResult = !flag;
                break;
            default:
            }
            if (shortCircuitOnTrue && trueResult)
                continueFlag = false;
            else if (shortCircuitOnFalse && falseResult)
                continueFlag = false;
        }
        return continueFlag ? EvaluationStatus.COMPLETE : EvaluationStatus.SHORT_CIRCUIT;
    }

    /**
     * Set delegate in advance if result is boolean
     */
    protected void presetDelegate() 
    {
        switch (operatorEnum)
        {
        case NOT:// "!"
        case LT: // "<"
        case GT: // ">"
        case EQ: // "=="
        case LE: // "<="
        case GE: // ">="
        case NE: // "!="
        case SC_OR: // "||"
        case SC_AND: // "&&"
            setDelegate(Boolean.class);
        default:
        }
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
                    rightTerm.booleanEvaluation(leftTerm, operatorEnum, leftTerm) :
                    leftTerm.booleanEvaluation(leftTerm, operatorEnum, rightTerm);
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
                return leftTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
            else if (rightIsBigDec)
                return rightTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
            // Prevent conversion of Double to Integer by 
            // always selecting the Double term to perform the operation
            boolean leftIsDouble = leftTerm.getValueClass() == Double.class;
            boolean rightIsDouble = rightTerm.getValueClass() == Double.class;
            if (leftIsDouble || !rightIsDouble)
                return leftTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
            else
                return rightTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
        case COMMA: // Comma operator builds a tree of operands instead of performing a calculation
            return new Null(); // Set dummy value so this variable is no longer empty
        default:
        }
        return null;
    }

	/**
	 * Represent a binary evaluator as a String
	 * @return String
	 */
	protected String binaryToString() 
	{
		// By default, show left by name, if empty, otherwise by value
		String leftTerm = (left.isEmpty() ? left.getName() : left.getValue().toString()); 
		if (left.getName().isEmpty() && left.isEmpty())
			// Possibly recurse if left is an Evaluator with no name and empty
			leftTerm = left.toString();
		// By default, show right by name, if empty, otherwise by value
		String rightTerm = (right.isEmpty() ? right.getName() : right.getValue().toString()); 
		if (right.getName().isEmpty() && right.isEmpty())
			// Possibly recurse if right is an Evaluator with no name and empty
			rightTerm = right.toString();
		return leftTerm  + operatorEnum.toString() + rightTerm;
	}

	/**
	 * Represent a postfix unary evaluator as a String
	 * @return String
	 */
	protected String unaryLeftToString() 
	{
		if (shortCircuitOnFalse || shortCircuitOnTrue)
			return getName() + (shortCircuitOnFalse ? "?" : ":") + left.toString();
		if (left.isEmpty())
		{
			if (left.getName().isEmpty())
				return super.toString();
			return left.getName() + operatorEnum.toString();
		}
		return left.getValue() + operatorEnum.toString();
	}

	/**
	 * Represent a prefix unary evaluator as a String
	 * @return String
	 */
	protected String unaryRightToString() 
	{
		if (right.isEmpty())
			return operatorEnum.toString() + (right.getName().isEmpty() ? right.toString() : right.getName());
		return operatorEnum.toString() + right.getValue();
	}

}
