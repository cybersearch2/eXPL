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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.LocaleCurrency;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * CurrencyTrait
 * @author Andrew Bowley
 * 21Apr.,2017
 */
public class CurrencyTrait extends DefaultTrait
{
    /** Currency implementation for specific locale */
    protected LocaleCurrency localeCurrency;
    /** Operand to evaluate currency country */
    protected Operand countryOperand;

    public CurrencyTrait(OperandType operandType, Locale locale)
    {
        super(operandType);
        localeCurrency = new LocaleCurrency();
        setLocale(locale);
    }

    /**
     * Set Operand to evaluate currency country
     * @param countryOperand the countryOperand to set
     */
    public void setCountryOperand(Operand countryOperand) 
    {
        this.countryOperand = countryOperand;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#formatValue(java.lang.Object)
     */
    @Override
    public String formatValue(Object value)
    {
        if (isCountryCodeSet())
            getLocaleByCode(countryOperand.getValue().toString());
        return localeCurrency.format(value);
    }

    /**
     * @see au.com.cybersearch2.classy_logic.trait.DefaultTrait#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale)
    {
        if (!isCountryCodeSet())
        {
            super.setLocale(locale);
            localeCurrency.setLocale(locale);
        }
        
    }

    /**
     * Get Locale using 2-digit country code
     * If currency format varies with language, then use 
     * language and country code separated by '_' or '-'. eg. 'de_LU'
     * Locale variants and scripts not supported.
     * @param country An ISO 3166 alpha-2 country code 
     */
    public Locale getLocaleByCode(String country) 
    {
        String[] parts = country.split("_|-");
        if (parts.length == 2)
            return new Locale(parts[0], parts[1]);
        // Match to first Locale found by country. 
        // Language is usually irrelevant for currency.
        Locale matchedByCountry = null;
        for (Locale locale: Locale.getAvailableLocales())
            if (locale.getCountry().equals(country) && 
                /*locale.getScript().isEmpty() && */
                locale.getVariant().isEmpty())
            {
                matchedByCountry = locale;
                break;
            }
        if (matchedByCountry == null)
            throw new ExpressionException(country + " is not a valid ISO 3166 alpha-2 country code");
        //System.out.println("Locale " + matchedByCountry.getLanguage() + "-" + matchedByCountry.getCountry());
        return matchedByCountry;
    }

    protected boolean isCountryCodeSet()
    {
        return (countryOperand != null)  && !countryOperand.isEmpty();
    }

}
