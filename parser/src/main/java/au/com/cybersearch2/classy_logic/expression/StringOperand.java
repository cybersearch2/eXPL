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
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * StringOperand
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class StringOperand  extends ExpressionParameter<String> implements Concaten<String>
{

	/**
	 * Construct StringOperand with given expression Operand
     * @param qname Qualified name
	 * @param expression The Operand which evaluates value
	 */
	public StringOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
		
	}

	/**
     * Construct StringOperand with given value
     * @param qname Qualified name
	 * @param value The value
	 */
	public StringOperand(QualifiedName qname, String value) 
	{
		super(qname, value);
	}

	/**
     * Construct StringOperand
     * @param qname Qualified name
	 */
	public StringOperand(QualifiedName qname) 
	{
		super(qname);
	}

	/**
	 * getRightOperandOps
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
			OperatorEnum.ASSIGN,
			OperatorEnum.EQ, // "=="
			OperatorEnum.NE
		};
	}

	/**
	 * getLeftOperandOps
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
				OperatorEnum.ASSIGN,
				OperatorEnum.EQ, // "=="
				OperatorEnum.NE // "!="
		};
	}

	/**
	 * getStringOperandOps
	 * @see au.com.cybersearch2.classy_logic.expression.ExpressionParameter#getStringOperandOps()
	 */
	 @Override
     public OperatorEnum[] getStringOperandOps()
     {
		return 	new OperatorEnum[]
		{ 
			OperatorEnum.PLUS,
		    OperatorEnum.PLUSASSIGN
		};
     }

	/**
	 * Evaluate a unary expression using this Term
	 * @param operatorEnum2 OperatorEnum for one of +, - or ~ 
	 * @return generic Parameter which implements Operand. The genericy type will be a sub class of Number.
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
	    return new Integer(0);
	}

	/**
	 * Binary numberEvaluation
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
	    return new Integer(0);
	}

	/**
	 * booleanEvaluation
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		boolean calc = false;
		switch (operatorEnum2)
		{
		case EQ:  calc = leftTerm.getValue().equals(rightTerm.getValue()); break; // "=="
		case NE:  calc = !leftTerm.getValue().equals(rightTerm.getValue()); break; // "!="
	    default:
		}
		return calc;
	}

	/**
	 * assign
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#assign(java.lang.Object)
	 */
	@Override
	public void assign(Object value) 
	{
		setValue(value.toString());
	}

	/**
	 * concatenate
	 * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
	 */
    @Override
    public String concatenate(Operand rightOperand)
    {
        return value.toString() + rightOperand.getValue().toString();
    }

}
