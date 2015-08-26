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
package au.com.cybersearch2.classy_logic.interfaces;

import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;

/**
 * Operand 
 * A Term which evaluates binary and unary expressions
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public interface Operand extends Term
{

	public static OperatorEnum[] EMPTY_OPERAND_OPS = new OperatorEnum[0];

	/**
	 * Returns qualified name
	 * @return QualifiedName object
	 */
	QualifiedName getQualifiedName();
	
    /**
	 * Returns OperatorEnum values for which this Term is a valid right operand
	 * @return OperatorEnum[]
	 */
    OperatorEnum[] getRightOperandOps();
	
	/**
	 * Returns OperatorEnum values for which this Term is a valid left operand
	 * @return OperatorEnum[]
	 */
     OperatorEnum[] getLeftOperandOps();

 	/**
 	 * Returns OperatorEnum values for which this Term is a valid String operand
 	 * @return OperatorEnum[]
 	 */
     OperatorEnum[] getStringOperandOps();

 	/**
 	 * Evaluate a unary expression 
 	 * @param operatorEnum2 OperatorEnum for one of +, -, ~. ++ or -- 
	 * @param rightTerm The term, always on right except for post inc/dec
	 * @return Class derived from Number.
 	 */
 	Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm);
 	
	/**
	 * Evaluate a binary expression
	 * @param leftTerm Term on left
	 * @param operatorEnum2 OperatorEnum for one of +, -, *, /, &, |, ^ or % 
	 * @param rightTerm Term on right
	 * @return Class derived from Number.
	 */
	Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm);
	
	/**
	 * Evaluate comparison 
	 * @param leftTerm Term on left
	 * @param operatorEnum2 OperaorEnum.LT or OperaorEnum.GT
	 * @param rightTerm Term on right
	 * @return BooleanOperand result
	 */
	Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm);

	/**
	 * Returns left child of Operand
	 * @return Operand object or null if there is no child
	 */
	Operand getLeftOperand();
	
	/**
	 * Returns right child of Operand
	 * @return Operand object or null if there is no child
	 */
	Operand getRightOperand();

}
