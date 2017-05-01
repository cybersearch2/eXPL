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

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Scanner;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * BigDecimalTrait
 * @author Andrew Bowley
 * 26Apr.,2017
 */
public class BigDecimalTrait extends NumberTrait<BigDecimal>
{

    public BigDecimalTrait()
    {
        super(OperandType.DECIMAL);
    }

    protected BigDecimalTrait(OperandType operandType)
    {
        super(operandType);
    }

    @Override
    public BigDecimal parseValue(String string)
    {
        // Default to graceful error value
        BigDecimal bigDecimal = BigDecimal.ZERO;
        Scanner scanner = new Scanner(string);
        scanner.useLocale(getLocale());
        if (scanner.hasNextBigDecimal())
            bigDecimal = scanner.nextBigDecimal();
        else if (scanner.hasNextDouble() | scanner.hasNextLong())
            bigDecimal = new BigDecimal(string);
        scanner.close();    
        return bigDecimal;
    }

    @Override
    public BigDecimalOperand cloneFromOperand(StringOperand stringOperand, Operand expression)
    {
        BigDecimalOperand clone = 
            expression == null ? 
            new BigDecimalOperand(stringOperand.getQualifiedName(), BigDecimal.ZERO) :
            new BigDecimalOperand(stringOperand.getQualifiedName(), expression);
        Parameter param = new Parameter(Term.ANONYMOUS, stringOperand.getValue().toString());
        param.setId(stringOperand.getId());
        Locale locale = stringOperand.getOperator().getTrait().getLocale();
        clone.getOperator().getTrait().setLocale(locale);
        clone.assign(param);
        return clone;
    }

}
