/**
 * 
 */
package au.com.cybersearch2.classy_logic.expression;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.OperandVisitor;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.DelegateType;
import au.com.cybersearch2.classy_logic.operator.EvaluatorOperator;
import au.com.cybersearch2.classy_logic.pattern.OperandWalker;

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
public class Evaluator extends TreeEvaluator
{
    static class EvaluationVisitor implements OperandVisitor
    {
        Operand resultOperand;
        String name;
  
        public EvaluationVisitor(String name)
        {
            this.name = name;
        }
 
        public Operand getResultOperand()
        {
            return resultOperand;
        }
        
        @Override
        public boolean next(Operand operand, int depth)
        {
            if (operand.getName().equals(name))
            {
                resultOperand = operand;
                return false;
            }
            return true;
        }
        
    }
    
    /** Right hand operand. If null, then this is a unary postfix expression. */
    protected Operand right;
    /** Left hand operand. If null, then this is a unary prefix expression. */
    protected Operand left;
    /** Flag set true if value set by evaluation */
    protected boolean isValueSet;
    

    /**
	 * Create Evaluator object for prefix/postfix unary expression 
	 * @param term Operand left or right determined by orientation
	 * @param operatorNotation Text representation of operator
	 * @param orientation Unary - prefix/postfix
	 */
	public Evaluator(Operand term, String operatorNotation, Orientation orientation)
	{
		this(QualifiedName.ANONYMOUS, operatorNotation, orientation);
		setUnaryTerm(term);
	}

    /**
	 * Create named Evaluator object for postfix unary expression 
     * @param qname Qualified name of variable
	 * @param term Operand
	 * @param operatorNotation Text representation of operator
     * @param orientation Unary - prefix/postfix
	 */
	public Evaluator(QualifiedName qname, Operand term, String operatorNotation, Orientation orientation)
	{
		this(qname, operatorNotation, orientation);
        setUnaryTerm(term);
	}

	/**
	 * Create Evaluator object for binary expression 
	 * @param leftTerm Left perand
	 * @param operatorNotation Text representation of operator
	 * @param rightTerm Right operand
	 */
	public Evaluator(Operand leftTerm, String operatorNotation, Operand rightTerm)
	{
		this(QualifiedName.ANONYMOUS, operatorNotation, Orientation.binary);
        this.right = rightTerm;
        this.left = leftTerm;
        checkBinaryTerms(operatorNotation);
        postConstruct();
    }

    /**
     * Create named Evaluator object for binary expression 
     * @param qname Qualified name of variable
     * @param leftTerm Left perand
     * @param operatorNotation Text representation of operator
     * @param rightTerm Right operand
     */
    public Evaluator(QualifiedName qname, Operand leftTerm, String operatorNotation, Operand rightTerm)
    {
        this(qname, operatorNotation, Orientation.binary);
        this.right = rightTerm;
        this.left = leftTerm;
        checkBinaryTerms(operatorNotation);
        postConstruct();
    }

	/**
	 * Construct named Evaluator object  
     * @param qname Qualified name of variable
	 * @param operatorNotation Text representation of operator
     * @param orientation Binary or unary - prefix/postfix
	 */
	protected Evaluator(QualifiedName qname, String operatorNotation, Orientation orientation)
	{
		super(qname, OperatorEnum.convertOperator(operatorNotation), orientation);
	    operator = new EvaluatorOperator();
	}

	/**
	 * Complete construction after right and left terms set according to orientation
	 */
	protected void postConstruct()
	{
	    // Set operator for reflexive operations, in which case, delegation not required and
	    // undesired when Evaluator value is set by unification prior to evaluation
	    if (orientation == Orientation.binary)
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
            case LSHIFTASSIGN:
            case RSIGNEDSHIFTASSIGN:
            case RUNSIGNEDSHIFTASSIGN:
                if ((left != null) && (left.getOperator().getTrait().getOperandType() != OperandType.UNKNOWN))
                    operator.setProxy(left.getOperator());
            default:
            }
        // Delegate can be set in advance if result is boolean
        // Otherwise, delegate will be set on value assigment
        presetDelegate();
	}
	
	/**
	 * Execute expression and set Evaluator value with result
	 * There is a precedence for errors, in highest to lowest:
	 * Term is empty
	 * Number is NaN
	 * TreeEvaluator not permited for type of Term value
	 * Term value is null
	 * 
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
    @Override
	public EvaluationStatus evaluate(int id)
	{
        EvaluationStatus evaluationStatus = super.evaluate(id);
        switch (evaluationStatus)
        {
        case SKIP: // Operator && or ||
            if (right != null)
            {
                if ((operatorEnum == OperatorEnum.HOOK) || (operatorEnum == OperatorEnum.COLON))
                    setResult(right.getValue(), id);
                else
                    // Binary && and || assigns a value
                    setResult(shortCircuitOnTrue, id);
            }
            else
            {
                if (empty)
                {
                    if (getName().isEmpty())
                        setValue(left.getValue());
                    else
                    {
                        OperandWalker walker = new OperandWalker(left);
                        EvaluationVisitor visitor = new EvaluationVisitor(getName());
                        walker.visitAllNodes(visitor);
                        Operand resultOperand = visitor.getResultOperand();
                        if (resultOperand == null)
                            resultOperand = left;
                        setValue(resultOperand.getValue());
                    }
                }
                isValueSet = true;
                this.id = id;
            }
            return EvaluationStatus.COMPLETE;
        case SHORT_CIRCUIT: // Left term evaluates to trigger short circuit - false for && and true for ||
            this.id = id;
            return isShortCircuit() ? EvaluationStatus.SHORT_CIRCUIT : EvaluationStatus.SKIP;
        case FAIL:
            if ((left != null) && left.isEmpty() && (operatorEnum != OperatorEnum.ASSIGN))
                throw new ExpressionException("Left term is empty");            
            throw new ExpressionException("Cannot evaluate " + toString());
        default:
        }
    	// Now perform evaluation, depending on status of left and right terms
        Object result = null;
    	switch (orientation)
    	{
    	case binary:
    	    result = evaluateBinary(id); break;
    	case unary_prefix:
    	    result = evaluatePreFix(); break;
    	case unary_postfix:
    	    result = evaluatePostFix(); break;
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
	    isValueSet = false;
		boolean backupOccurred = super.backup(modifierId);
		if ((right != null) && right.backup(modifierId)) 
			backupOccurred = true;
		if ((left != null) && left.backup(modifierId))
			backupOccurred = true;
		return backupOccurred;
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
		if (!isValueSet && !(!empty && isShortCircuit()))
		{   // Return evaluation to perform
			if (orientation == Orientation.unary_prefix)
				return  unaryRightToString();
			else if (orientation == Orientation.unary_postfix)
				return unaryLeftToString();
			return binaryToString();
		}
		// Return value
		return super.toString();
	}

	/**
	 * Evaluate binary operation
	 * @param id Modificatiion id
	 * @return result object
	 */
	protected Object evaluateBinary(int id)
    {
        Object result = null;
        if (!leftIsNaN && !rightIsNaN)
        {
            // Delegate calculation to sub class
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
                case LSHIFTASSIGN:
                case RSIGNEDSHIFTASSIGN:
                case RUNSIGNEDSHIFTASSIGN:
                left.setValue(result);
                default:
            }
        }
        else if (leftIsNaN)
            result = left.getValue();
        else 
            result = right.getValue();
        return result;
    }

	/**
	 * Evaluate prefix unary operation
     * @return result object
	 */
    protected Object evaluatePreFix()
    {
        Object result = null;
        // Prefix unary operation.
        if (rightIsNaN)
            result = right.getValue();
        else
            result = doPrefixUnary();
        return result;
    }
    
    /**
     * Evaluate postfix unary operation
     * @return result object
     */
    protected Object evaluatePostFix()
    {
        // Postfix unary operation.
        // Result will automatically be NaN if left Term value is NaN
        Object result = left.getValue();
        if (!leftIsNaN)
        {
            Number post = left.getOperator().numberEvaluation(operatorEnum, left);
            left.setValue(post);
        }
        else if (left.getOperator().getTrait().getOperandType() == OperandType.CURSOR)
        {
            int modificationId = left.getId();
            left.backup(modificationId);
            left.evaluate(modificationId);
        }
        return result;
    }

    /**
     * Perform prefix unary operation with right term: ++, --, !,  ~, + or -
     * @return Object Result
     */
    protected Object doPrefixUnary() 
    {
        if ((operatorEnum == OperatorEnum.INCR) || (operatorEnum == OperatorEnum.DECR))
        {   // ++ or --
            Number pre = right.getOperator().numberEvaluation(operatorEnum, right);
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
            return right.getOperator().numberEvaluation(operatorEnum, right);
        return null;
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
            isValueSet = true;
            this.id = id;
            // Check for boolean result
            switch(operatorEnum)
            {
            case SC_OR: // "||"
            case SC_AND: // "&&"
                if (orientation == Orientation.binary)
                    break;
            case NOT:// "!"
            case LT: // "<"
            case GT: // ">"
            case EQ: // "=="
            case LE: // "<="
            case GE: // ">="
            case NE: // "!="
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
        {
            if (operator.getDelegateType() != DelegateType.BOOLEAN)
                operator.setDelegateType(DelegateType.BOOLEAN);
        }
        default:
        }
    }

    /**
     * Set left or right term to given operand according to orientation
     * @param term The operand to set
     * @throws ExpressionException if orientation set to binary
     */
    protected void setUnaryTerm(Operand term)
    {
        switch (orientation)
        {
        case unary_prefix:
            right = term;
            break;
        case unary_postfix:
            left = term;
            break;
        case binary:
            throw new ExpressionException("Invalid Evaluator binary orientation where unary required");
        }
    }

    /**
     * setLeftOperand
     * @see au.com.cybersearch2.classy_logic.expression.TreeEvaluator#setLeftOperand(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @Override
    protected void setLeftOperand(Operand left)
    {
        this.left = left;
    }
 
    /**
     * setRightOperand
     * @see au.com.cybersearch2.classy_logic.expression.TreeEvaluator#setRightOperand(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @Override
    protected void setRightOperand(Operand right)
    {
        this.right = right;
    }
 
    /**
     * Check for null binary terms
     * @param operator Operator in text format
     * @throws ExpressionException if null term encountered
     */
    protected void checkBinaryTerms(String operator)
    {
        if (orientation == Orientation.binary) 
        {
            String invalidTerm = null;
            if (left == null)
                invalidTerm = "left";
            if (right == null)
                invalidTerm = "right";
            if (invalidTerm != null)
                throw new ExpressionException("Binary operator \"" + operator + "\" cannot have null " + invalidTerm + " term");
        }
    }

	/**
	 * Represent a binary evaluator as a String
	 * @return String
	 */
	protected String binaryToString() 
	{
		// By default, show left by name, if empty, otherwise by value
		String leftTerm = (left.isEmpty() ? formatLeft() : left.getValue().toString()); 
		if (left.getName().isEmpty() && left.isEmpty())
			// Possibly recurse if left is an Evaluator with no name and empty
			leftTerm = left.toString();
		// By default, show right by name, if empty, otherwise by value
		String rightTerm = (right.isEmpty() ? formatRight() : right.getValue().toString()); 
		if (right.getName().isEmpty() && right.isEmpty())
			// Possibly recurse if right is an Evaluator with no name and empty
			rightTerm = right.toString();
		return leftTerm  + operatorEnum.toString() + rightTerm;
	}

	protected String formatLeft()
	{
        if ((operatorEnum == OperatorEnum.HOOK) || (operatorEnum == OperatorEnum.COLON))
            return left.toString();
        return left.getName();
	}
	
    protected String formatRight()
    {
        if ((operatorEnum == OperatorEnum.HOOK) || (operatorEnum == OperatorEnum.COLON))
            return right.toString();
        return right.getName();
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
