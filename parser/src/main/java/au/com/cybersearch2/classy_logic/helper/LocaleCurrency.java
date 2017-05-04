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
package au.com.cybersearch2.classy_logic.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import au.com.cybersearch2.classy_logic.expression.ExpressionException;

/**
 * LocaleCurrency
 * Support locale-specific currency operations
 * @author Andrew Bowley
 * 8 Mar 2015
 */
public class LocaleCurrency
{
    /** The locale */
    protected Locale locale;
    /** The currency as specified by the platform */
    protected Currency currency;
    /** Currency fraction digit count */
    protected int numberOfDecimals;

    /**
     * Construct LocaleCurrency object for default locale
     */
	public LocaleCurrency() 
    {
    	this(Locale.getDefault());
	}

    /**
     * Construct LocaleCurrency object for specified locale
     * @param locale The locale
     */
	public LocaleCurrency(Locale locale) 
    {
    	setLocale(locale);
	}

	/**
	 * Returns BigDecimal representation of an amount specified as text.
	 * Relaxes Java's strict format requirements to allow reasonable variations.
	 * @param currencyAsText
	 * @return BigDecimal object
	 */
    public BigDecimal parse(String currencyAsText)
    {
    	String originalCurrencyAsText = currencyAsText;
    	DecimalFormat decformat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
    	DecimalFormatSymbols symbols = decformat.getDecimalFormatSymbols();
    	String currencySymbol = symbols.getCurrencySymbol();
    	//if (currency.getCurrencyCode().equals("EUR"))
    	//{
	    	// If amount has currency symbol, then strip if off
	        // so it can be placed to conform with Java's formatter expectations 
	    	int mark = currencyAsText.indexOf(currencySymbol);
	    	int symLength = currencySymbol.length();
	    	if (mark == 0)
	    		currencyAsText = currencyAsText.substring(symLength).trim();
	    	else if (mark > 0)
	    		currencyAsText = currencyAsText.substring(0, mark).trim();
	    	// Fix grouping character, if different from expected
	    	// Move a cursor from first digit until non-digit encountered
	    	int cursor = Character.isDigit(currencyAsText.charAt(0)) ? 0 : 1;
	    	int decPoint = currencyAsText.indexOf(symbols.getDecimalSeparator());
	    	// Stop if decimal point or end of amount reached
	    	int endOfGroups = decPoint == -1 ? currencyAsText.length() : decPoint;
	    	while (cursor != endOfGroups)
	    	{
	    	    char groupChar = currencyAsText.charAt(cursor++);
	    	    if (!Character.isDigit(groupChar))
	    	    {
	    	        if (groupChar != symbols.getGroupingSeparator())
	    	        {
	    	            char[] charArray = currencyAsText.toCharArray();
	    	            for (int i = 0; i < charArray.length; i++)
	    	                if (charArray[i] == groupChar)
	    	                    charArray[i] = symbols.getGroupingSeparator();
	    	            currencyAsText = String.copyValueOf(charArray);
	    	        }
	    	        break;
	    	    }
	    	}
	    	// Now add currency smybol according to expected format
			String sample = decformat.format(12345.67);
			mark = sample.indexOf(currencySymbol);
			String sep = mark == 0 ? sample.substring(symLength,symLength+1) : sample.substring(sample.length() - symLength - 1, sample.length() - symLength);
			//System.out.println(locale + ": " + sample + " - \"" + sep + "\"");
			boolean hasSep = !Character.isDigit(sep.charAt(0));
			if (hasSep)
			{
				int pos = currencyAsText.indexOf(sep);
				hasSep = (mark == 0) ? pos != 0 : pos != currencyAsText.length() - 1;
			}
			if (!hasSep )
	    		currencyAsText = mark == 0 ? 
	    				         currencySymbol + currencyAsText.trim() :
	    				         currencyAsText.trim() +currencySymbol;
	    	else
	    		currencyAsText = mark == 0 ? 
				         currencySymbol + sep + currencyAsText.trim() :
				         currencyAsText.trim() + sep + currencySymbol;
    	//}
     	Number amount;
     	//System.out.println(currencyAsText);
		try 
		{
			amount = decformat.parse(currencyAsText);
		} 
		catch (ParseException e) 
		{
			throw new ExpressionException("Locale " + locale.getLanguage() + "-" + locale.getCountry() + " currency format of amount \"" + originalCurrencyAsText + "\" invalid", e);
		}
    	return new BigDecimal(amount.toString());
    }

    /**
     * Returns text representation of amount specified in text
     * @param amount Currency value in type compatible with NumberFormat eg. BigDecimal
     * @return String
     */
    public String format(Object amount)
    {
    	DecimalFormat numberFormat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
    	symbols.setCurrencySymbol(currency.getCurrencyCode());
    	numberFormat.setDecimalFormatSymbols(symbols);
    	return numberFormat.format(amount);
    }

    /**
     * Set locale
     * @param locale The locale
     */
	public void setLocale(Locale locale) 
	{
		this.locale = locale;
    	currency = Currency.getInstance(locale);
	}

	/**
	 * Returns fraction digits for locale currency
	 * @return int
	 */
	public int getFractionDigits()
	{
		return currency.getDefaultFractionDigits();
	}
}
