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
 * IntegerOperand
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public class IntegerOperand extends ExpressionParameter<Integer> 
{

	/**
	 * Construct a variable IntegerOperand object
	 * @param name
	 */
	public IntegerOperand(String name) 
	{
		super(name);
	}

	/**
	 * Construct a literal IntegerOperand object
	 * @param name
	 * @param value Integer object
	 */
	public IntegerOperand(String name, Integer value) 
	{
		super(name, value);
	}

	/**
	 * Integer Expression
	 * @param name
	 * @param expression Operand which evaluates value
	 */
	public IntegerOperand(String name, Operand expression) 
	{
		super(name, expression);

	}

	/**
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
			OperatorEnum.ASSIGN,
			OperatorEnum.LT, // "<"
			OperatorEnum.GT ,// ">"
			OperatorEnum.EQ, // "=="
			OperatorEnum.LE, // "<="
			OperatorEnum.GE, // ">="
			OperatorEnum.NE, // "!="
			OperatorEnum.PLUS,
			OperatorEnum.MINUS,
			OperatorEnum.STAR,
			OperatorEnum.SLASH,
			OperatorEnum.BIT_AND,
			OperatorEnum.BIT_OR,
			OperatorEnum.XOR,
			OperatorEnum.REM,	
			OperatorEnum.INCR,
			OperatorEnum.DECR,
			OperatorEnum.PLUSASSIGN,
			OperatorEnum.MINUSASSIGN,
			OperatorEnum.STARASSIGN,
			OperatorEnum.SLASHASSIGN,
			OperatorEnum.ANDASSIGN,
			OperatorEnum.ORASSIGN,
			OperatorEnum.XORASSIGN,
			OperatorEnum.REMASSIGN	,		
			OperatorEnum.TILDE		
		};
	}

	/**
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
				OperatorEnum.ASSIGN,
				OperatorEnum.LT, // "<"
				OperatorEnum.GT ,// ">"
				OperatorEnum.EQ, // "=="
				OperatorEnum.LE, // "<="
				OperatorEnum.GE, // ">="
				OperatorEnum.NE, // "!="
				OperatorEnum.PLUS,
				OperatorEnum.MINUS,
				OperatorEnum.STAR,
				OperatorEnum.SLASH,
				OperatorEnum.BIT_AND,
				OperatorEnum.BIT_OR,
				OperatorEnum.XOR,
				OperatorEnum.REM,		
				OperatorEnum.INCR,
				OperatorEnum.DECR,
				OperatorEnum.PLUSASSIGN,
				OperatorEnum.MINUSASSIGN,
				OperatorEnum.STARASSIGN,
				OperatorEnum.SLASHASSIGN,
				OperatorEnum.ANDASSIGN,
				OperatorEnum.ORASSIGN,
				OperatorEnum.XORASSIGN,
				OperatorEnum.REMASSIGN
		};
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
		int right = ((Number)(rightTerm.getValue())).intValue();
		int calc = 0;
		switch (operatorEnum2)
		{
		case PLUS:  calc = +right; break;
		case MINUS: calc = -right; break;  
		case TILDE: calc = ~right; break;
		case INCR: calc = ++right; break;
		case DECR: calc = --right; break;
	    default:
		}
	    return new Integer(calc);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		int right = ((Number)(rightTerm.getValue())).intValue();
		int left =  ((Number)(leftTerm.getValue())).intValue();
		int calc = 0;
		switch (operatorEnum2)
		{
		case PLUSASSIGN: // "+="
		case PLUS: 	calc = left + right; break;
		case MINUSASSIGN: // "-="
		case MINUS:  calc = left - right; break;
		case STARASSIGN: // "*="
		case STAR:      calc = left * right; break;
		case SLASHASSIGN: // "/="
		case SLASH:     calc = left / right; break;
		case ANDASSIGN: // "&="
		case BIT_AND:   calc = left & right; break;
		case ORASSIGN: // "|="
		case BIT_OR:    calc = left | right; break;
		case XORASSIGN: // "^="
		case XOR:       calc = left ^ right; break;
		case REMASSIGN: // "%="
		case REM:       calc = left % right; break;
	    default:
		}
	    return new Integer(calc);
	}

	/**
	 * Evaluate relational operation using this Term as the left term
	 * @param leftTerm Term on left
	 * @param operatorEnum2 Operator
	 * @param rightTerm Term on right
	 * @return Boolean result
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		int right = ((Number) rightTerm.getValue()).intValue();
		int left = ((Number) leftTerm.getValue()).intValue();
		boolean calc = false;
		switch (operatorEnum2)
		{
		case LT:  calc = left < right; break;
		case GT:  calc = left > right; break;
		case EQ:  calc = left == right; break; // "=="
		case LE:  calc = left <= right; break; // "<="
		case GE:  calc = left >= right; break; // ">="
		case NE:  calc = left != right; break; // "!="
	    default:
		}
		return calc;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#assign(java.lang.Object)
	 */
	@Override
	public void assign(Object value) 
	{
		setValue((Integer)value);
	}

}
