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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.BigDecimalOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.helper.LocaleCurrency;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.StringCloneable;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.CurrencyOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * CurrencyTrait
 * Behaviours for localization and specialization of Currency operands
 * @author Andrew Bowley
 * 21Apr.,2017
 */
public class CurrencyTrait extends BigDecimalTrait implements StringCloneable
{
    /** Currency implementation for specific locale */
    protected LocaleCurrency localeCurrency;

    /**
     * Construct CurrencyTrait object
     */
    public CurrencyTrait()
    {
        super(OperandType.CURRENCY);
        localeCurrency = new LocaleCurrency();
    }

    /**
     * Returns fraction digits for locale currency
     * @return int
     */
    public int getFractionDigits()
    {
        return localeCurrency.getFractionDigits();
    }

    /**
     * Returns BigDecimal representation of an amount specified as text.
     * Relaxes Java's strict format requirements to allow reasonable variations.
     * @param value Text to parse
     * @return BigDecimal object
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#parseValue(java.lang.String)
     */
    @Override
    public BigDecimal parseValue(String value)
    {
        return localeCurrency.parse(value);
    }
    
    /**
     * Returns text representation of amount specified in text
     * @param amount Currency value in type compatible with NumberFormat eg. BigDecimal
     * @return String
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#formatValue(java.lang.Object)
     */
    @Override
    public String formatValue(Object value)
    {
        return localeCurrency.format(value);
    }

    /**
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale)
    {
        super.setLocale(locale);
        localeCurrency.setLocale(locale);
    }

    /**
     * getOperandType
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#getOperandType()
     */
    @Override
    public OperandType getOperandType()
    {
        return operandType;
    }

    /**
     * cloneFromOperand
     * @see au.com.cybersearch2.classy_logic.trait.BigDecimalTrait#cloneFromOperand(au.com.cybersearch2.classy_logic.expression.StringOperand, au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @Override
    public BigDecimalOperand cloneFromOperand(StringOperand stringOperand, Operand expression)
    {
        CurrencyOperator currencyOperator = new CurrencyOperator();
        BigDecimalOperand clone = 
                expression == null ? 
                new BigDecimalOperand(stringOperand.getQualifiedName(), BigDecimal.ZERO) :
                new BigDecimalOperand(stringOperand.getQualifiedName(), expression);
        Parameter param = new Parameter(Term.ANONYMOUS, stringOperand.getValue().toString());
        param.setId(stringOperand.getId());
        Locale locale = stringOperand.getOperator().getTrait().getLocale();
        if (!locale.getCountry().equals(currencyOperator.getTrait().getLocale().getCountry()))
            currencyOperator.setLocaleByCode(locale.getCountry());
        clone.setOperator(currencyOperator);
        clone.assign(param);
        return clone;
    }

}
