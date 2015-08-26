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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * BooleanVariable
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class BooleanOperand extends ExpressionParameter<Boolean>
{

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
			OperatorEnum.SC_AND // "&&"
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
			OperatorEnum.SC_AND  // "&&"
		};
	}

	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{   // There is no valid evaluation involving a boolean resulting in a number
	    return new Integer(0);
	}

	/**
	 * Evaluate a binary expression using this Term as the left term
	 * @param operatorEnum2 OperatorEnum for one of +, -, *, /, &, |, ^ or % 
	 * @param rightTerm Term on right
	 * @return sub class of Number with result
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{   // There is no valid evaluation involving a boolean and another term resulting in a number
	    return new Integer(0);
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

	@Override
	public void assign(Object value) 
	{
		setValue((Boolean)value);
	}

}
