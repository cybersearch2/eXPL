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

import static org.fest.assertions.api.Assertions.assertThat;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Currency;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

/**
 * LocaleCurrencyTest
 * @author Andrew Bowley
 * 9 Mar 2015
 */
public class LocaleCurrencyTest 
{
	public static Collection<Locale> getLocalesFromIso4217(String iso4217code) 
	{
	    Collection<Locale> returnValue = new LinkedList<Locale>();
	    for (Locale locale : NumberFormat.getAvailableLocales()) 
	    {
	        String code = NumberFormat.getCurrencyInstance(locale).getCurrency().getCurrencyCode();
	        if (iso4217code.equals(code)) 
	            returnValue.add(locale);
	    }  
	    return returnValue;
	}

	public static final String[] EURO_SAMPLES =
	{
	"12 345,67 €",
	"€12,345.67",
	"12.345,67 €",
	"€ 12.345,67",
	"€ 12.345,67",
	"12.345,67 €",
	"12.345,67 €",
	"€ 12.345,67",
	"12 345,67 €",
	"12.345,67 €",
	"€ 12.345,67",
	"€ 12.345,67",
	"12.345,67 €",
	"€12,345.67",
	"€ 12.345,67",
	"€12,345.67",
	"€12,345.67",
	"12.345,67 €",
	"12 345,67 €",
	"12.345,67 €",
	"12 345,67 €",
	"€12.345,67",
	"12 345,67 €",

	"12 345,67",
	"12,345.67",
	"12.345,67",
	"12.345,67",
	"12.345,67",
	"12.345,67",
	"12.345,67",
	"12.345,67",
	"12 345,67",
	"12.345,67",
	"12.345,67",
	"12.345,67",
	"12.345,67",
	"12,345.67",
	"12.345,67",
	"12,345.67",
	"12,345.67",
	"12.345,67",
	"12 345,67",
	"12.345,67",
	"12 345,67",
	"12.345,67",
	"12 345,67",

	"12 345,67€",
	"€ 12,345.67",
	"12.345,67€",
	"€12.345,67",
	"€12.345,67",
	"12.345,67€",
	"12.345,67€",
	"€12.345,67",
	"12 345,67€",
	"12.345,67€",
	"€12.345,67",
	"€12.345,67",
	"12.345,67€",
	"€ 12,345.67",
	"€12.345,67",
	"€ 12,345.67",
	"€ 12,345.67",
	"12.345,67€",
	"12 345,67€",
	"12.345,67€",
	"12 345,67€",
	"€ 12.345,67",
	"12 345,67€",
};
	
	@Test
	public void test_euro() 
	{
	    //System.out.println(getLocalesFromIso4217("USD"));
	    //System.out.println(getLocalesFromIso4217("EUR"));
	    for (Locale locale : getLocalesFromIso4217("EUR")) 
	    {  // locale + "=>" + 
  			if (!locale.getVariant().isEmpty())
  				continue;
	        System.out.println(locale + "=>" + NumberFormat.getCurrencyInstance(locale).format(12345.67));
	    }
	}
	
	@Test
	public void test_world() 
	{
	    for (Locale locale : Locale.getAvailableLocales()) 
	    {  // locale + "=>" + 
			//if (!locale.getCountry().isEmpty() && 
			//		 locale.getScript().isEmpty() &&
			//		 locale.getVariant().isEmpty())
 	            System.out.println(locale + "? " + locale.getVariant() + " =>" + NumberFormat.getCurrencyInstance(locale).format(12345.67));
	    }
	}
	
    @Ignore // TODO - Fix now compiled with JDK6
	@Test
	public void test_convert()
	{
		LocaleCurrency localeCurrency = new LocaleCurrency();
		//System.out.println(localeCurrency.parse("$12.34").toString());
		//System.out.println(localeCurrency.format(new BigDecimal("12.34")).toString());
        int index = 0;
        for (int i = 0; i < 3; i++)
        {
	  		for (Locale locale : getLocalesFromIso4217("EUR")) 
		    {
	  			if (/*!locale.getScript().isEmpty() ||*/ !locale.getVariant().isEmpty())
	  				continue;
	  	    	localeCurrency.setLocale(locale);
		    	//System.out.println(EURO_SAMPLES[index]);
		    	//System.out.println(localeCurrency.parse(EURO_SAMPLES[index++]));
	  	    	assertThat(localeCurrency.parse(EURO_SAMPLES[index++]).toString()).isEqualTo("12345.67");
		    }
        }
	}
    
	@Test
	public void generate_axioms()
	{
		Random random = new Random();
		for (Locale locale: Locale.getAvailableLocales())
		{
			if (!locale.getCountry().isEmpty() && 
				 /*locale.getScript().isEmpty() &&*/
				 locale.getVariant().isEmpty())
			{
				String country = locale.getCountry();
				if (locale.getCountry().equals("LU")) // Luxenburg has French and German formats
					country = locale.getLanguage() + "_" + country;
		    	//DecimalFormat decformat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
		    	//DecimalFormatSymbols symbols = decformat.getDecimalFormatSymbols();
				String testValue = "" + random.nextInt(10000);
				Currency currency = Currency.getInstance(locale);
				if (currency.getDefaultFractionDigits() > 0)
					testValue += "." + buildFraction(currency.getDefaultFractionDigits());
				System.out.println("(\"" + country + "\", \"" + testValue + "\"),");
			}
		}
	}

	private String buildFraction(int digits)
	{
		Random random = new Random();
		String randomValue = "" + random.nextInt(10 * digits);
		StringBuilder builder = new StringBuilder(randomValue);
		for (int i = digits; i > randomValue.length(); --i)
			builder.insert(0,  "0");
		return builder.toString();
	}
}
