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

import java.util.TreeMap;

/**
 * OperatorMap
 * Converts between operator as a String and as an enum.
 * Also supports operator as a Char for single character operators
 * @author Andrew Bowley
 * 30 Nov 2014
 */
public class OperatorMap 
{
	/** Maps Char to enum */
	static class OperatorChar
	{
		char operator;
		OperatorEnum operatorEnum;
		
		public OperatorChar(char operator, OperatorEnum operatorEnum)
		{
			this.operator = operator;
			this.operatorEnum = operatorEnum;
		}
	}

	/** Maps String to enum */
	static class OperatorString
	{
		String operator;
		OperatorEnum operatorEnum;
		
		public OperatorString(String operator, OperatorEnum operatorEnum)
		{
			this.operator = operator;
			this.operatorEnum = operatorEnum;
		}

		public OperatorString(OperatorChar operatorChar)
		{
			this.operator = Character.toString(operatorChar.operator);
			this.operatorEnum = operatorChar.operatorEnum;
		}
    }

	/** Initialisation Char data */
	protected OperatorChar[] operatorChars= new OperatorChar[]
	{
		new OperatorChar('=', OperatorEnum.ASSIGN), 
		new OperatorChar('<', OperatorEnum.LT),
		new OperatorChar('>', OperatorEnum.GT),
		new OperatorChar('!', OperatorEnum.NOT),
		new OperatorChar('~', OperatorEnum.TILDE),
		new OperatorChar('+', OperatorEnum.PLUS),
		new OperatorChar('-', OperatorEnum.MINUS),
		new OperatorChar('*', OperatorEnum.STAR),
		new OperatorChar('/', OperatorEnum.SLASH),
		new OperatorChar('&', OperatorEnum.BIT_AND),
		new OperatorChar('|', OperatorEnum.BIT_OR),
		new OperatorChar('^', OperatorEnum.XOR),
		new OperatorChar('%', OperatorEnum.REM),
        new OperatorChar(',', OperatorEnum.COMMA)
	};

	/** Initialisation String data */
	protected OperatorString[] operatorStrings = new OperatorString[]
	{
	    new OperatorString("==", OperatorEnum.EQ),
	    new OperatorString("<=", OperatorEnum.LE),
	    new OperatorString(">=", OperatorEnum.GE),
	    new OperatorString("!=", OperatorEnum.NE),
	    new OperatorString("||", OperatorEnum.SC_OR),
	    new OperatorString("&&", OperatorEnum.SC_AND),
	    new OperatorString("++", OperatorEnum.INCR),
	    new OperatorString("--", OperatorEnum.DECR),
	    new OperatorString("+=", OperatorEnum.PLUSASSIGN),
	    new OperatorString("-=", OperatorEnum.MINUSASSIGN),
	    new OperatorString("*=", OperatorEnum.STARASSIGN),
	    new OperatorString("/=", OperatorEnum.SLASHASSIGN),
	    new OperatorString("&=", OperatorEnum.ANDASSIGN),
	    new OperatorString("|=", OperatorEnum.ORASSIGN),
	    new OperatorString("^=", OperatorEnum.XORASSIGN),
	    new OperatorString("%=", OperatorEnum.REMASSIGN)
	};

	/** Tree map for all operators for fast look up */
	protected TreeMap<String, OperatorEnum> operatorMap;
	
	/**
	 * Construct an OperatorMap object
	 */
	public OperatorMap() 
	{
		operatorMap = new TreeMap<String, OperatorEnum>();
		for (OperatorString operatorString: operatorStrings)
		{
			operatorMap.put(operatorString.operator, operatorString.operatorEnum);
		}

		for (OperatorChar operatorChar: operatorChars)
		{
			operatorMap.put(Character.toString(operatorChar.operator), operatorChar.operatorEnum);
		}
    }

	/**
	 * Returns Operator as enum
	 * @param operator String
	 * @return OperatorEnum
	 */
	public OperatorEnum get(String operator) 
	{
		if (operator == null)
			throw new IllegalArgumentException("Parameter \"operator\" is null");
		OperatorEnum operatorEnum = operatorMap.get(operator);
		if (operatorEnum == null)
			return OperatorEnum.UNKOWN;
		return operatorEnum;
	}

	/**
	 * Returns Operator as enum
	 * @param operator char
	 * @return OperatorEnum
	 */
	public OperatorEnum get(char operator) 
	{
		OperatorEnum operatorEnum = operatorMap.get(Character.toString(operator));
		if (operatorEnum == null)
			return OperatorEnum.UNKOWN;
		return operatorEnum;
	}

	/**
	 * Returns String value of Operator enum
	 * @param operatorEnum OperatorEnum
	 * @return String
	 */
	public String toString(OperatorEnum operatorEnum)
	{
		for (OperatorString operatorString: operatorStrings)
		{
			if (operatorEnum == operatorString.operatorEnum)
				return operatorString.operator;
		}

		for (OperatorChar operatorChar: operatorChars)
		{
			if (operatorEnum == operatorChar.operatorEnum)
			    return Character.toString(operatorChar.operator);
		}
		return "?";
	}

}
