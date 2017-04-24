/**
    Copyright (C) 2014  www.cybersearch2.com.au

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

import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

/**
 * BooleanVariable
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class BooleanOperand extends ExpressionOperand<Boolean>
{
    static Trait BOOLEAN_TRAIT;
    
    static
    {
        BOOLEAN_TRAIT = new DefaultTrait(OperandType.BOOLEAN);
    }
    
	/**
	 * Boolean Variable
     * @param qname Qualified name
	 */
	public BooleanOperand(QualifiedName qname) 
	{
		super(qname);
	}

	/**
	 * Boolean Literal
     * @param qname Qualified name
	 * @param value
	 */
	public BooleanOperand(QualifiedName qname, Boolean value) 
	{
		super(qname, value);

	}

	/**
	 * Boolean Expression
     * @param qname Qualified name
	 * @param expression Operand which evaluates value
	 */
	public BooleanOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);

	}

	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return 	new OperatorEnum[]
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

	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
			OperatorEnum.EQ, // "=="
			OperatorEnum.NE, // "!="
			OperatorEnum.ASSIGN, // "="
			OperatorEnum.SC_OR,  // "||"
			OperatorEnum.SC_AND,  // "&&"
            OperatorEnum.STAR // * true == 1.0, false = 0.0
		};
	}

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
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
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
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{   
		boolean right = ((Boolean)(rightTerm.getValue())).booleanValue();
		boolean left = ((Boolean)(leftTerm.getValue())).booleanValue();
		switch (operatorEnum2)
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
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
	 */
	@Override
	public void assign(Term term) 
	{
		setValue((Boolean)term.getValue());
		id = term.getId();
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

    @Override
    public void setTrait(Trait trait)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Trait getTrait()
    {
        return BOOLEAN_TRAIT;
    }

}
