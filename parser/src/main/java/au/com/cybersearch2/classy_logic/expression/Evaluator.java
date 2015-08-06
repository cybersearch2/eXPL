/**
 * 
 */
package au.com.cybersearch2.classy_logic.expression;

import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * Evaluator
 * Performs evaluation of unary and binary expressions.
 * The operator is specified as a String eg. "+", and represented interanally as an enum.
 * As an Operand, it is related to Variable, as value type may be unknown until an evaluation has occured.
 * An Evaluator is allowed to be annoymous, in which case it acts as a simple parameter of local scope.
 * @author Andrew Bowley
 *
 * @since 02/10/2010
 * @see DelegateParameter
 * @see Variable
 */
public class Evaluator extends DelegateParameter
{
    /** Not a number */
	private static final String NAN = "NaN"; //Double.valueOf(Double.NaN).toString();
	
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
	 * @param name Name of this term
	 * @param leftTerm Left operand
	 * @param operator Text representation of operator
	 */
	public Evaluator(String name, Operand leftTerm, String operator)
	{
		this(name, leftTerm, operator, (Operand)null);
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
	 * @param name
	 * @param rightTerm Operand
	 * @param operator 
	 */
	public Evaluator(String name, String operator, Operand rightTerm)
	{
		this(name, (Operand)null, operator, rightTerm);
	}

	/**
	 * Create Evaluator object for binary expression 
	 * @param leftTerm Left perand
	 * @param operator Text representation of operator
	 * @param rightTerm Right operand
	 */
	public Evaluator(Operand leftTerm, String operator, Operand rightTerm)
	{
		this(Term.ANONYMOUS, leftTerm, operator, rightTerm);
	}

	/**
	 * Create named Evaluator object for binary expression 
	 * @param name
	 * @param leftTerm Operand
	 * @param operator 
	 * @param rightTerm Operand
	 */
	public Evaluator(String name, Operand leftTerm, String operator, Operand rightTerm)
	{
		super(name);
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
    		leftIsNaN = isNaN(left);
    	}
    	if (right != null)
    	{
    		// Only evaluate once between backups
    		if (right.isEmpty())
    		    right.evaluate(id);
    		// If right hand term is empty, then cannot proceed. Maybe unification failed for this term.
    		if (right.isEmpty())
	   			throw new ExpressionException("Right term is empty");
    		// Remember if right is not a number
   			rightIsNaN = isNaN(right);
    		if ((!isValidRightOperand(right) || isInvalidRightUnaryOp(left)) &&
    			 !((left != null) && isValidStringOperation(left))) 
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
	   			left.assign(post);
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
		   		result = calculate(left, right);
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
	                left.assign(result);
	                default:
		   		}
	   		}
	   	}
	   	return setResult(result, id);
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
   			right.assign(pre);
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
		if (!isValidLeftOperand(left) || isInvalidLeftUnaryOp(right))
		{   // Operation not permited for type of Term value
			// NaN has precedence if operation has Numeric result
			if (!isNaN(left) && ((right == null) || !isNaN(right)))
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
	protected Object calculate(Operand leftTerm, Operand rightTerm) 
	{
		switch (operatorEnum)
		{
		case ASSIGN: // "="
			return assign(leftTerm, rightTerm);
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
			if (isValidStringOperation(leftTerm))
				return ((Concaten<?>)leftTerm).concatenate(rightTerm);
		case MINUS: // "-"
		case STAR: // "*"
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
            boolean leftIsBigDec = leftTerm.getValueClass().equals(BigDecimal.class);
            boolean rightIsBigDec = rightTerm.getValueClass().equals(BigDecimal.class);
            if (leftIsBigDec)
                return leftTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
            else if (rightIsBigDec)
                return rightTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
		    // Prevent conversion of Double to Integer by 
		    // always selecting the Double term to perform the operation
		    boolean leftIsDouble = leftTerm.getValueClass().equals(Double.class);
            boolean rightIsDouble = rightTerm.getValueClass().equals(Double.class);
            if (leftIsDouble || !rightIsDouble)
                return leftTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
            else
                return rightTerm.numberEvaluation(leftTerm, operatorEnum, rightTerm);
		case COMMA: 
		    return new Null(); // Set dummy value so this variable is no longer empty
	    default:
		}
		return null;
	}

	/**
	 * Backup to intial state if given id matches id assigned on unification or given id = 0. 
	 * @param id Identity of caller. 
	 * @return boolean true if backup occurred
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
	 */
	@Override
	public boolean backup(int id)
	{
		boolean backupOccurred = false;
		if (super.backup(id))
			backupOccurred = true;
		if ((right != null) && right.backup(id)) 
			backupOccurred = true;
		if ((left != null) && left.backup(id))
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
	 * Assign a value to this Operand. It may overwrite and existing value
	 * This value will be overwritten on next call to evaluate(), so calling
	 * assign() on an Evaluator is pointless.
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#assign(java.lang.Object)
	 */
	@Override
	public void assign(Object value) 
	{
		setValue(value);
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

	/**
	 * Returns true if left operand permited for operator used in expression
	 * @param leftTerm Left Operand
	 * @return Flag set true to indicate valid left operand
	 */
	protected boolean isValidLeftOperand(Operand leftTerm) 
	{
		for (OperatorEnum operatorEnum2: leftTerm.getLeftOperandOps())
			if (operatorEnum2 == operatorEnum)
				return true;
		if (isValidStringOperation(leftTerm) || 
		    // Comma operator valid if right operand present    
		    ((operatorEnum == OperatorEnum.COMMA) && (right != null)))
			return true;
		return false;
	}

	/**
	 * Returns flag to indicate if supplied term is allowed to perform String operations
	 * @param term
	 * @return boolean
	 */
	protected boolean isValidStringOperation(Operand term)
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
	protected boolean isInvalidRightUnaryOp(Operand leftTerm)
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
	protected boolean isValidRightOperand(Operand rightTerm) 
	{
		for (OperatorEnum operatorEnum2: rightTerm.getRightOperandOps())
			if (operatorEnum2 == operatorEnum)
				return true;
		// Only Comma operator is valid at this point
		return operatorEnum == OperatorEnum.COMMA;
	}

	/**
	 * Returns true if left operand permited for operator used in expression
	 * @param rightTerm Right Operand. If not null then operation is binary
	 * @return Flag set true to indicate invalid left operand
	 */
	protected boolean isInvalidLeftUnaryOp(Operand rightTerm)
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
	protected boolean isNaN(Object number)
	{
		if ((number == null) || (!(number instanceof Number)))
			return true;
		return number.toString().equals(NAN);
	}

	/**
	 * Returns flag true if operand value is NaN 
	 * @param operand Operand to test
	 * @return Flag set true to indicate not a number
	 */
	protected boolean isNaN(Operand operand)
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
	 * @return Value as Object
	 */
	protected Object assign(Operand leftTerm, Operand rightTerm)
	{
		Object value = rightTerm.getValue();
		leftTerm.assign(value);
		// When the value class is not supported as a delegate, substitute a Null object.
		// This is defensive only as Operands are expected to only support Delegate classes
		return DelegateParameter.isDelegateClass(value.getClass()) ? value : new Null();
	}

}
