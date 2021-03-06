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
package au.com.cybersearch2.classy_logic.interfaces;

import java.util.Locale;

import au.com.cybersearch2.classy_logic.compile.OperandType;

/**
 * Trait
 * Behaviours for localization and specialization of operands
 * @author Andrew Bowley
 * 21Apr.,2017
 */
public interface Trait extends TextFormat
{
    /**
     * Set locale
     * @param locale Locale object
     */
    void setLocale(Locale locale);
    
    /**
     * Returns country code of locale 
     * @return String
     */
    String getCountry();
    
    /**
     * Returns locale
     * @return Locale object
     */
    Locale getLocale();
    
    /**
     * Returns locale for given country code
     * @param country Country code (2-character)
     * @return Locale object
     */
    Locale getLocaleByCode(String country);
    
    /**
     * Returns operand type to identify type of operands supported
     * @return OperandType enum
     */
    OperandType getOperandType();
}
