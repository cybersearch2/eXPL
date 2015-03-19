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

import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * StringOperand
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class StringOperand  extends ExpressionParameter<String> 
{

	/**
	 * String Expression
	 * @param name
	 * @param expression Parameter which evaluates value
	 */
	public StringOperand(String name, Operand expression) 
	{
		super(name, expression);
		
	}

	public StringOperand(String name, String value) 
	{
		super(name, value);
	}

	public StringOperand(String name) 
	{
		super(name);
	}

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

	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
	    return new Integer(0);
	}

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

	@Override
	public void assign(Object value) 
	{
		setValue(value.toString());
	}

}
