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

/**
 * Operator
 * @author Andrew Bowley
 *
 * @since 03/10/2010
 */
public enum OperatorEnum
{
    ASSIGN, // "="
    LT, // "<"
    GT, // ">"
    NOT, // "!"
    TILDE, // "~"
    HOOK, // "?"
    COLON, // ":"
    EQ, // "=="
    LE, // "<="
    GE, // ">="
    NE, // "!="
    SC_OR, // "||"
    SC_AND, // "&&"
    INCR, // "++"
    DECR, // "--"
    PLUS, // "+"
    MINUS, // "-"
    STAR, // "*"
    SLASH, // "/"
    BIT_AND, // "&"
    BIT_OR, // "|"
    XOR, // "^"
    REM, // "%"
    LSHIFT, // "<<"
    RSIGNEDSHIFT, // ">>"
    RUNSIGNEDSHIFT, // ">>>"
    PLUSASSIGN, // "+="
    MINUSASSIGN, // "-="
    STARASSIGN, // "*="
    SLASHASSIGN, // "/="
    ANDASSIGN, // "&="
    ORASSIGN, // "|="
    XORASSIGN, // "^="
    REMASSIGN, // "%="
    LSHIFTASSIGN, // "<<="
    RSIGNEDSHIFTASSIGN, // ">>="
    RUNSIGNEDSHIFTASSIGN, // ">>>="
    ELLIPSIS, // "...",
    COMMA,
    UNKOWN;

	static OperatorMap operatorMap;
    
    static
    {
    	operatorMap = new OperatorMap();
    }
    
	/**
	 * Returns operator enum corresponding to specified text
	 * @param operator
	 * @return OperatorEnum
	 */
	public static OperatorEnum convertOperator(String operator)
	{
		return operatorMap.get(operator);
	}

	/**
	 * Returns operator enum corresponding to specified char
	 * @param operatorCharacter char
	 * @return OperatorEnum
	 */
	public static OperatorEnum convertOperatorChar(char operatorCharacter)
	{
	    return operatorMap.get(operatorCharacter);
	}

    /**
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
	public String toString() 
    {
		return operatorMap.toString(this);
	}

}

