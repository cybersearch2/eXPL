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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * BigDecimalOperand
 * @author Andrew Bowley
 * 3 Dec 2014
 */
public class BigDecimalOperand extends ExpressionParameter<BigDecimal> 
{
    
	/**
	 * Construct named, empty BigDecimalOperand object
	 * @param name 
	 */
	public BigDecimalOperand(String name) 
	{
		super(name);
		
	}

	/**
	 * Construct named, non-empty BigDecimalOperand object
	 * @param name
	 * @param value BigDecimal
	 */
	public BigDecimalOperand(String name, BigDecimal value) 
	{
		super(name, value);

	}

	/**
	 * Construct named BigDecimalOperand object which delegates to an expression to set value
	 * @param name
	 * @param expression Operand which evaluates value
	 */
	public BigDecimalOperand(String name, Operand expression) 
	{
		super(name, expression);

	}

	/**
	 * 
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
			OperatorEnum.REM,		
			OperatorEnum.PLUSASSIGN,
			OperatorEnum.MINUSASSIGN,
			OperatorEnum.STARASSIGN,
			OperatorEnum.SLASHASSIGN,
			OperatorEnum.REMASSIGN			
		};
	}

	/**
	 * 
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
				OperatorEnum.REM,		
				OperatorEnum.PLUSASSIGN,
				OperatorEnum.MINUSASSIGN,
				OperatorEnum.STARASSIGN,
				OperatorEnum.SLASHASSIGN,
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
		BigDecimal right = convertObject(rightTerm.getValue());
		BigDecimal calc = BigDecimal.ZERO;
		switch (operatorEnum2)
		{
		case PLUS:  calc = right.plus(); break;
		case MINUS: calc = right.negate(); break;  
	    default:
		}
	    return calc;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		BigDecimal right = convertObject(rightTerm.getValue());
		BigDecimal left = convertObject(leftTerm.getValue());
		BigDecimal calc = BigDecimal.ZERO;
		switch (operatorEnum2)
		{
		case PLUSASSIGN: // "+="
		case PLUS: 	calc = left.add(right); break;
		case MINUSASSIGN: // "-="
		case MINUS:     calc = left.subtract(right); break;
		case STARASSIGN: // "*="
		case STAR:      calc = calculateTimes(left, right); break;
		case SLASHASSIGN: // "/="
		case SLASH:     calc = calculateDiv(left, right); break;
		case REMASSIGN: // "%="
		case REM:       calc = left.remainder(right); break;
	    default:
		}
	    return calc;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		boolean calc = false;
		BigDecimal leftBigDec = convertObject(leftTerm.getValue());
		BigDecimal righttBigDec = convertObject(rightTerm.getValue());
		switch (operatorEnum2)
		{
		case EQ:  calc = leftBigDec.compareTo(righttBigDec) == 0; break; // "=="
		case NE:  calc = leftBigDec.compareTo(righttBigDec) != 0; break; // "!="
		case LT:  calc = leftBigDec.compareTo(righttBigDec) < 0; break; // "<"
		case GT:  calc = leftBigDec.compareTo(righttBigDec) > 0; break; // ">"
		case LE:  calc = leftBigDec.compareTo(righttBigDec) <= 0; break; // "<="
		case GE:  calc = leftBigDec.compareTo(righttBigDec) >= 0; break; // ">="
	    default:
		}
		return calc;
	}

	/**
	 * Evaluate value if expression exists
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id)
	{
		if (expression != null)
		{
			expression.evaluate(id);
			if (!expression.isEmpty())
			{   // Perform conversion to BigDecimal, if required
				setValue(convertObject(expression.getValue()));
				this.id = id;
			}
		}
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#assign(java.lang.Object)
	 */
	@Override
	public void assign(Object value) 
	{
		setValue(convertObject(value));
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

	/**
	 * Binary multiply. Override to adjust rounding. 
	 * @param right BigDecimal object left term
	 * @param left BigDecimal object reight term
	 * @return BigDecimal object
	 */
	protected BigDecimal calculateTimes(BigDecimal right, BigDecimal left)
	{
		return left.multiply(right);
	}

	/**
	 * Binary divide. Override to adjust rounding. 
	 * @param right BigDecimal object left term
	 * @param left BigDecimal object reight term
	 * @return BigDecimal object
	 */
	protected BigDecimal calculateDiv(BigDecimal right, BigDecimal left)
	{
		return left.divide(right, BigDecimal.ROUND_FLOOR);
	}
}
