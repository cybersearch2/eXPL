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

/**
 * AssignOnlyOperand
 * Delegate for variables which only allow assignment and equality comparisons
 * @author Andrew Bowley
 * 4 Feb 2015
 */
public class AssignOnlyOperand extends NullOperand 
{
	/**
	 * Construct AssignOnlyOperand object
     * @param qname Qualified name
	 */
	public AssignOnlyOperand(QualifiedName qname) 
	{
		super(qname);

	}

	/**
	 * getRightOperandOps
	 * @see au.com.cybersearch2.classy_logic.expression.NullOperand#getRightOperandOps()
	 */
	@Override
	public OperatorEnum[] getRightOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
			OperatorEnum.ASSIGN,
			OperatorEnum.EQ,
			OperatorEnum.NE
		};
	}

	/**
	 * getLeftOperandOps
	 * @see au.com.cybersearch2.classy_logic.expression.NullOperand#getLeftOperandOps()
	 */
	@Override
	public OperatorEnum[] getLeftOperandOps() 
	{
		return 	new OperatorEnum[]
		{ 
				OperatorEnum.ASSIGN,
				OperatorEnum.EQ,
				OperatorEnum.NE
		};
	}

}
