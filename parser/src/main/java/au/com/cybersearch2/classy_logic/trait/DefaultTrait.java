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

import java.util.Formatter;
import java.util.Locale;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.Trait;

/**
 * DefaultTrait
 * Provides text formatting sufficient for most operands, including StringOperand.
 * Also supports setting locale by 2-character country code - useful for currency formatting.
 * @author Andrew Bowley
 * 21Apr.,2017
 */
public class DefaultTrait implements Trait
{
    /** Operand type identifies type of operands supported */
    protected OperandType operandType;
    /** Locale - defaults to default locale */
    protected Locale locale;
    /** Country code */
    protected String country;

    /** Construct DefaultTrait object */
    public DefaultTrait(OperandType operandType)
    {
        this.operandType = operandType;
        // Locale is lazy-loaded
        country = "";
    }

    /**
     * formatValue
     * @see au.com.cybersearch2.classy_logic.interfaces.TextFormat#formatValue(java.lang.Object)
     */
    @Override
    public String formatValue(Object value)
    {
        if (locale == null)
            return value.toString();
        Formatter localeFormatter = new Formatter(locale);
        localeFormatter.format("%s", value);
        String formatValue = localeFormatter.toString();
        localeFormatter.close();
        return formatValue;
    }

    /**
     * getLocale
     * @see au.com.cybersearch2.classy_logic.interfaces.Trait#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        if (locale == null)
            setLocale(Locale.getDefault());
        return locale;
    }

    /**
     * setLocale
     * @see au.com.cybersearch2.classy_logic.interfaces.Trait#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale)
    {
        this.locale = locale;
        country = getLocale().getCountry();
    }

    /**
     * getOperandType
     * @see au.com.cybersearch2.classy_logic.interfaces.Trait#getOperandType()
     */
    @Override
    public OperandType getOperandType()
    {
        return operandType;
    }

    /**
     * Get Locale using 2-digit country code
     * If currency format varies with language, then use 
     * language and country code separated by '_' or '-'. eg. 'de_LU'
     * Locale variants and scripts not supported.
     * @param country An ISO 3166 alpha-2 country code 
     */
    @Override
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

    /**
     * @return the country code or empty string if locale not set
     */
    @Override
    public String getCountry()
    {
        return country;
    }

    /**
     * Returns locale languge and country codes
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        Locale currentLocale = locale;
        if (currentLocale == null)
            currentLocale = Locale.getDefault();
        StringBuilder builder = new StringBuilder(currentLocale.getLanguage());
        String country = currentLocale.getCountry();
        if (!country.isEmpty())
            builder.append('-').append(country);
        return builder.toString();
    }

}
