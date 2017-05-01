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
package au.com.cybersearch2.classy_logic.expression;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
// java.util.Locale.Category;

import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.CurrencyOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.trait.CurrencyTrait;

/**
 * CurrencyOperandTest
 * 
 * @author Andrew Bowley
 * 10 Mar 2015
 */
public class CurrencyOperandTest 
{
    //Locale.getDefault(/*Category.FORMAT*/)
	final static String NAME = "CurrencyOp";
	static QualifiedName QNAME = QualifiedName.parseName(NAME);
	
	@Test
	public void test_unify_text()
	{
		BigDecimalOperand currencyOperand = new BigDecimalOperand(QNAME);
		currencyOperand.operator = new CurrencyOperator();
		Parameter localAmount = new Parameter(Term.ANONYMOUS, NumberFormat.getCurrencyInstance().format(12345.67));
		assertThat(currencyOperand.unifyTerm(localAmount, 1)).isEqualTo(1);
		assertThat(currencyOperand.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(currencyOperand.getValue()).isEqualTo(new BigDecimal("12345.67"));
	}

	// Fails if code compiled with Java 6
	@Test
	public void test_euro()
	{
		doEuroTest(false);
		doEuroTest(true);
	}
	
	protected void doEuroTest(boolean useCountryOperand)
	{
        //int index = 0;
        BigDecimal expectedResult = new BigDecimal("12345.67");
  		for (Locale locale : getLocalesFromIso4217("EUR")) 
	    {   // Standard format for locale
  		    if (locale.toString().contains("#"))
  		        continue;
            //System.out.println(locale + " ==>" + NumberFormat.getCurrencyInstance(locale).format(12345.67));
  	    	testOperand(locale, NumberFormat.getCurrencyInstance(locale).format(12345.67), expectedResult, useCountryOperand);
	    }
        for (Locale locale : getLocalesFromIso4217("EUR")) 
        {   // Strip currency character and any space
            if (locale.toString().contains("#"))
                continue;
            String euroAmount = NumberFormat.getCurrencyInstance(locale).format(12345.67);
            if (Character.isDigit(euroAmount.charAt(0)))
                euroAmount = euroAmount.substring(0, euroAmount.length() - 2); 
            else if (Character.isDigit(euroAmount.charAt(euroAmount.length() - 1)))
                euroAmount = euroAmount.substring(1, euroAmount.length()).trim(); 
            //System.out.println(locale + " ==>" + euroAmount);
            testOperand(locale, euroAmount, expectedResult, useCountryOperand);
        }
        for (Locale locale : getLocalesFromIso4217("EUR")) 
        {   // Reverse standard format for locale
            if (locale.toString().contains("#"))
                continue;
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
            String euroAmount = numberFormat.format(12345.67);
            String euro = "€";
            if (Character.isDigit(euroAmount.charAt(0)))
                euroAmount = euro + euroAmount.substring(0, euroAmount.length() - 2);
            else if (Character.isDigit(euroAmount.charAt(euroAmount.length() - 1)))
            {
                euroAmount = euroAmount.substring(1, euroAmount.length());
                if (euroAmount.startsWith(" "))
                    euroAmount = euroAmount.trim() + euro;
                else
                    euroAmount = euroAmount.trim() + " " + euro;
             }
            //System.out.println(locale + " ==>" + euroAmount);
            testOperand(locale, euroAmount, expectedResult, useCountryOperand);
        }
        expectedResult = new BigDecimal("12345");
  		for (Locale locale : getLocalesFromIso4217("EUR")) 
	    {
            if (locale.toString().contains("#"))
                continue;
  	    	String testAmount = NumberFormat.getCurrencyInstance(locale).format(12345);
  	    	testOperand(locale, testAmount, expectedResult, useCountryOperand);
	    }
	}
	
	@Test
	public void test_format() throws IOException
	{
        BigDecimal testAmount = new BigDecimal("12345.67");
        File worldCurrencyList = new File("src/test/resources", "world-amount.lst");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(worldCurrencyList), "UTF-8"));
		for (Locale locale: Locale.getAvailableLocales())
		{
			if (!locale.getCountry().isEmpty() && 
				 locale.getVariant().isEmpty())
			{
				String country = locale.getCountry();
				if (locale.getCountry().equals("LU")) // Luxenburg has French and German formats
					country = locale.getLanguage() + "_" + country;
				BigDecimalOperand currencyOperand = new BigDecimalOperand(QNAME, testAmount);
		        currencyOperand.operator = new CurrencyOperator();
		        currencyOperand.operator.getTrait().setLocale(locale);
				//System.out.println(currencyOperand.operator.getTrait().formatValue(currencyOperand.getValue()));
	            String line = reader.readLine();
	            assertThat(currencyOperand.operator.getTrait().formatValue(currencyOperand.getValue())).isEqualTo(line);
			}
		}
		reader.close();
	}
	
	private void testOperand(Locale locale, String amount, BigDecimal expectedResult, boolean useCountryOperand)
	{
		String country = locale.getCountry();
		if (locale.getCountry().equals("LU")) // Luxenburg has French and German formats
			country = locale.getLanguage() + "_" + country;
		//    countryOperand = new StringOperand(QualifiedName.ANONYMOUS, country);
        BigDecimalOperand currencyOperand = new BigDecimalOperand(QNAME);
		CurrencyOperator currencyOperator = new CurrencyOperator();
		CurrencyTrait trait = (CurrencyTrait) currencyOperator.getTrait();
		if (useCountryOperand)
		{
		    Operand countryExpression = new TestVariable("CC", new TestStringOperand("EvalCC", country));
		    CountryOperand countryOperand = new CountryOperand(new QualifiedName(NAME + QNAME.incrementReferenceCount(),QNAME ), trait, countryExpression);
		    currencyOperand.setRightOperand(countryOperand);
		}
		else
            trait.setLocale(trait.getLocaleByCode(country));
		currencyOperand.operator = currencyOperator;
		Parameter localAmount = new Parameter(Term.ANONYMOUS, amount);
		assertThat(currencyOperand.unifyTerm(localAmount, 1)).isEqualTo(1);
		if (currencyOperand.getRightOperand() != null)
		    assertThat(currencyOperand.getRightOperand().evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(currencyOperand.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(currencyOperand.getValue()).isEqualTo(expectedResult);
	}
	
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

}
