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

import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * NullOperand
 * Represents null value. Can be assigned and compared for equality.
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class NullOperand extends ExpressionOperand<Object> implements Operand
{
    /**
     * Construct anonymous NullOperand object
     */
	public NullOperand()
	{
		super(QualifiedName.ANONYMOUS, new Null());
	}

	/**
     * Construct named NullOperand object
     * @param qname Qualified name
	 */
	public NullOperand(QualifiedName qname)
	{
		super(qname, new Null());
	}

    /**
     * Construct named NullOperand object with Null substitute such as Unknown
     * @param qname Qualified name
     */
    public NullOperand(QualifiedName qname, Object substitute)
    {
        super(qname, substitute);
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
			OperatorEnum.NE // "!="
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
	 * Unary numberEvaluation - invalid
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
	{
	    return new Integer(0);
	}

	/**
	 * Binary numberEvaluation - invalid
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
	    return new Integer(0);
	}

	/**
	 * booleanEvaluation - compare to another NullOperand
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
	 */
	@Override
	public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm) 
	{
		boolean calc = false;
		switch (operatorEnum2)
		{
		case EQ:  calc = (leftTerm.getValueClass() == getValueClass()) && (rightTerm.getValueClass() == getValueClass()); break; // "=="
		case NE:  calc = !((leftTerm.getValueClass() == getValueClass()) && (rightTerm.getValueClass() == getValueClass())); break; // "!="
	    default:
		}
		return calc;
	}

	/**
	 * This Operand value is immutable
     * Interface: Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
	 */
	@Override
	public void assign(Term term) 
	{
	    id = term.getId();
	}

}
