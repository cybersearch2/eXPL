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
package au.com.cybersearch2.classy_logic.operator;

import java.math.BigDecimal;
import java.util.Locale;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.CurrencyTrait;

/**
 * CurrencyOperator
 * Operator for BigDecimalOperand which holds an amount in a particular currency
 * @see BigDecimalOperator
 * @author Andrew Bowley
 * 28Apr.,2017
 */
public class CurrencyOperator extends BigDecimalOperator implements LocaleListener
{
    /** Behaviours for localization and specialization of currency operands */
    private CurrencyTrait currencyTrait;

    /**
     * Construct CurrencyOperator object
     */
    public CurrencyOperator()
    {
        super();
        currencyTrait = new CurrencyTrait();
        // CurrencyTrait is a sub class of BigDecimalTrait
        bigDecimalTrait = currencyTrait;
    }

    /**
     * getTrait
     * @see au.com.cybersearch2.classy_logic.operator.BigDecimalOperator#getTrait()
     */
    @Override
    public Trait getTrait()
    {
        return currencyTrait;
    }
 
    /**
     * onScopeChange
     * @see au.com.cybersearch2.classy_logic.operator.BigDecimalOperator#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
     */
    @Override
    public void onScopeChange(Scope scope) 
    {
        currencyTrait.setLocale(scope.getLocale());
    }

    /**
     * Returns locale for given country code
     * @param country Country code (2 character)
     * @return Locale object
     */
    public Locale getLocaleByCode(String country) 
    {
        return currencyTrait.getLocaleByCode(country);
    }

    /**
     * Returns country code
     * @return String
     */
    public String getCountry()
    {
        return currencyTrait.getCountry();
    }

    /**
     * Returns BigDecimal representation of an amount specified as text.
     * Relaxes Java's strict format requirements to allow reasonable variations.
     * @param value Text to parse
     * @return BigDecimal object
     */
    public BigDecimal parseValue(String value)
    {
        return currencyTrait.parseValue(value);
    }
    
    /**
     * Returns text representation of amount specified in text
     * @param amount Currency value in type compatible with NumberFormat eg. BigDecimal
     * @return String
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#formatValue(java.lang.Object)
     */
    public String formatValue(Object value)
    {
        return currencyTrait.formatValue(value);
    }
    
    /**
     * Set locate according to given country code
     * @param countryCode
     */
    public void setLocaleByCode(String countryCode)
    {
        Locale locale = getLocaleByCode(countryCode);
        currencyTrait.setLocale(locale);
    }

   /**
     * Binary multiply. Override to adjust rounding. 
     * @param right BigDecimal object left term
     * @param left BigDecimal object reight term
     * @return BigDecimal object
     * @see au.com.cybersearch2.classy_logic.operator.BigDecimalOperator#calculateTimes(java.math.BigDecimal, java.math.BigDecimal)
     */
    @Override
    protected BigDecimal calculateTimes(BigDecimal right, BigDecimal left)
    {
        BigDecimal newAmount = left.multiply(right);
        // Gets the default number of fraction digits used with this currency.
        // For example, the default number of fraction digits for the Euro is 2,
        // while for the Japanese Yen it's 0.
        // In the case of pseudo-currencies, such as IMF Special Drawing Rights,
        // -1 is returned.
        int scale = currencyTrait.getFractionDigits();
        if (scale >= 0)
            newAmount = newAmount.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
        return newAmount;
    }

    /**
     * Binary divide. Override to adjust rounding. 
     * @param right BigDecimal object left term
     * @param left BigDecimal object reight term
     * @return BigDecimal object
     * @see au.com.cybersearch2.classy_logic.operator.BigDecimalOperator#calculateDiv(java.math.BigDecimal, java.math.BigDecimal)
     */
    @Override
    protected BigDecimal calculateDiv(BigDecimal right, BigDecimal left)
    {
        return left.divide(right, BigDecimal.ROUND_HALF_EVEN);
    }

}
