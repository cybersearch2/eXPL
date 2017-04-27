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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
// java.util.Locale.Category;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.LocaleCurrencyTest;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * CurrencyOperandTest
 * @author Andrew Bowley
 * 10 Mar 2015
 */
public class CurrencyOperandTest 
{

	final static String NAME = "CurrencyOp";
	static QualifiedName QNAME = QualifiedName.parseName(NAME);
	
	@Test
	public void test_unify_text()
	{
		CurrencyOperand currencyOperand = new CurrencyOperand(QNAME, null); //Locale.getDefault(/*Category.FORMAT*/));
		Parameter localAmount = new Parameter(Term.ANONYMOUS, NumberFormat.getCurrencyInstance().format(12345.67));
		assertThat(currencyOperand.unifyTerm(localAmount, 1)).isEqualTo(1);
		assertThat(currencyOperand.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(currencyOperand.getValue()).isEqualTo(new BigDecimal("12345.67"));
	}

	@Ignore // TODO - Fix test to run now with JDK6 compile
	@Test
	public void test_euro()
	{
		doEuroTest(false);
		doEuroTest(true);
	}
	
	protected void doEuroTest(boolean useCountryOperand)
	{
        int index = 0;
        BigDecimal expectedResult = new BigDecimal("12345.67");
        for (int i = 0; i < 3; i++)
        {
	  		for (Locale locale : LocaleCurrencyTest.getLocalesFromIso4217("EUR")) 
		    {
	  			//if (!locale.getScript().isEmpty())
		    	//System.out.println(localeCurrency.parse(LocaleCurrencyTest.EURO_SAMPLES[index]));
	  	    	testOperand(locale, LocaleCurrencyTest.EURO_SAMPLES[index++], expectedResult, useCountryOperand);
		    }
        }
        expectedResult = new BigDecimal("12345");
  		for (Locale locale : LocaleCurrencyTest.getLocalesFromIso4217("EUR")) 
	    {
  			//if (!locale.getScript().isEmpty())
  			//	continue;
  	    	String testAmount = NumberFormat.getCurrencyInstance(locale).format(12345);
	    	System.out.println(testAmount);
	    	//System.out.println(localeCurrency.parse(LocaleCurrencyTest.EURO_SAMPLES[index]));
  	    	testOperand(locale, testAmount, expectedResult, useCountryOperand);
	    }
	}
	
	@Test
	public void test_format()
	{
        BigDecimal testAmount = new BigDecimal("12345.67");
		for (Locale locale: Locale.getAvailableLocales())
		{
			if (!locale.getCountry().isEmpty() && 
				 /*locale.getScript().isEmpty() && */
				 locale.getVariant().isEmpty())
			{
				String country = locale.getCountry();
				if (locale.getCountry().equals("LU")) // Luxenburg has French and German formats
					country = locale.getLanguage() + "_" + country;
				CurrencyOperand currencyOperand = new CurrencyOperand(QNAME, testAmount, new StringOperand(QualifiedName.ANONYMOUS, country)); // Locale.getDefault(/*Category.FORMAT*/));
				//assertThat(currencyOperand.formatValue(currencyOperand.getValue()).contains(Currency.getInstance(locale).getCurrencyCode())).isTrue();
				System.out.println(Currency.getInstance(locale).getCurrencyCode());
				System.out.println(currencyOperand.formatValue(currencyOperand.getValue()));
			}
		}
	}
	
	private void testOperand(Locale locale, String amount, BigDecimal expectedResult, boolean useCountryOperand)
	{
		String country = locale.getCountry();
		if (locale.getCountry().equals("LU")) // Luxenburg has French and German formats
			country = locale.getLanguage() + "_" + country;
		Operand countryOperand = null;
		if (useCountryOperand)
			countryOperand = new TestVariable("CC", new TestStringOperand("EvalCC", country));
		else
		    countryOperand = new StringOperand(QualifiedName.ANONYMOUS, country);
        CurrencyOperand currencyOperand = new CurrencyOperand(QNAME, countryOperand);
		Parameter localAmount = new Parameter(Term.ANONYMOUS, amount);
		assertThat(currencyOperand.unifyTerm(localAmount, 1)).isEqualTo(1);
		assertThat(currencyOperand.evaluate(1)).isEqualTo(EvaluationStatus.COMPLETE);
		assertThat(currencyOperand.getValue()).isEqualTo(expectedResult);
	}
}
