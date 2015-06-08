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
package au.com.cybersearch2.classy_logic.terms;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * NumberTerm
 * @author Andrew Bowley
 * 6 Jun 2015
 */
public class NumberTerm extends GenericParameter<Number> implements LocaleListener
{
    /** The value to parse */
    protected String formated;
    /** The locale */
    protected Locale locale;

    /**
     * Construct an anonymous NumberTerm object 
     * @param formated Decimal number to parse 
     * @param locale Initial locale - can be updated through LocaleListener interface
     */
    public NumberTerm(String formated, Locale locale)
    {
        super(Term.ANONYMOUS, Integer.valueOf(0));
        this.formated = formated;
        this.locale = locale;
        setValue();
    }

    @Override
    public void onScopeChange(Scope scope) 
    {
        locale = scope.getLocale();
        setValue();
    }

    protected void setValue()
    {
        NumberFormat numberFormat = NumberFormat.getInstance(locale);
        try
        {
            value = numberFormat.parse(formated);
        }
        catch (ParseException e)
        {
            throw new ExpressionException("Error parsing \"" + formated + "\" for locale \"" + locale + "\"", e);
        }
    }
}
