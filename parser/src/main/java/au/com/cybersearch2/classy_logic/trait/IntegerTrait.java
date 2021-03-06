/**
    Copyright (C) 2017  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.trait;

import java.util.Locale;
import java.util.Scanner;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.IntegerOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * IntegerTrait
 * Behaviours for localization and specialization of Integer operands
 * @author Andrew Bowley
 * 26Apr.,2017
 */
public class IntegerTrait extends NumberTrait<Long>
{
    /**
     * Construct IntegerTrait object
     */
    public IntegerTrait()
    {
        super(OperandType.INTEGER);
     }

    /**
     * Construct IntegerTrait object with specified operand type
     * @param operandType Operand type
     */
    protected IntegerTrait(OperandType operandType)
    {
        super(operandType);
    }

    /**
     * parseValue
     * @see au.com.cybersearch2.classy_logic.trait.NumberTrait#parseValue(java.lang.String)
     */
    @Override
    public Long parseValue(String string)
    {
        // Fail gracefully
        Long value = Long.valueOf(0L);
        Scanner scanner = new Scanner(string);
        scanner.useLocale(getLocale());
        if (scanner.hasNextLong())
            value = scanner.nextLong();
        scanner.close();    
        return value;
    }

    /**
     * cloneFromOperand
     * @see au.com.cybersearch2.classy_logic.interfaces.StringCloneable#cloneFromOperand(Operand)
     */
    @Override
    public IntegerOperand cloneFromOperand(Operand stringOperand)
    {
        IntegerOperand clone = 
            stringOperand.getLeftOperand() == null ? 
            new IntegerOperand(stringOperand.getQualifiedName(), 0) :
            new IntegerOperand(stringOperand.getQualifiedName(), stringOperand.getLeftOperand());
        Parameter param = new Parameter(Term.ANONYMOUS, stringOperand.getValue().toString());
        param.setId(stringOperand.getId());
        Locale locale = stringOperand.getOperator().getTrait().getLocale();
        clone.getOperator().getTrait().setLocale(locale);
        clone.assign(param);
        return clone;
    }

}
