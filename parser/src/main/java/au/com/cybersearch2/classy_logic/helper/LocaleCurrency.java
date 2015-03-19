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
 * @author Andrew Bowley
 * 8 Mar 2015
 */
public class LocaleCurrency
{
    protected Locale locale;
    protected Currency currency;
    /** Currency fraction digit count */
    protected int numberOfDecimals;

	public LocaleCurrency() 
    {
    	this(Locale.getDefault());
	}

	public LocaleCurrency(Locale locale) 
    {
    	setLocale(locale);
	}

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

    public String format(BigDecimal amount)
    {
    	DecimalFormat numberFormat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
    	symbols.setCurrencySymbol(currency.getCurrencyCode());
    	numberFormat.setDecimalFormatSymbols(symbols);
    	return numberFormat.format(amount);
    }
    
	public void setLocale(Locale locale) 
	{
		this.locale = locale;
    	currency = Currency.getInstance(locale);
	}
	
	public int getFractionDigits()
	{
		return currency.getDefaultFractionDigits();
	}
}
