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
import au.com.cybersearch2.classy_logic.interfaces.Trait;

/**
 * DefaultTrait
 * @author Andrew Bowley
 * 21Apr.,2017
 */
public class DefaultTrait implements Trait
{
    protected OperandType operandType;
    protected Locale locale;

    public DefaultTrait(OperandType operandType)
    {
        this.operandType = operandType;
    }
    
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

    @Override
    public Locale getLocale()
    {
        if (locale == null)
            locale = Locale.getDefault();
        return locale;
    }

    @Override
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    @Override
    public OperandType getOperandType()
    {
        return operandType;
    }

    /**
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
