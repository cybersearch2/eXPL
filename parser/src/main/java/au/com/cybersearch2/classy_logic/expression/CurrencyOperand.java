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
import java.util.Locale;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.LocaleCurrency;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.TextFormat;

/**
 * CurrencyOperand
 * @author Andrew Bowley
 * 8 Mar 2015
 */
public class CurrencyOperand extends BigDecimalOperand implements TextFormat, LocaleListener
{
    /** Currency implementation for specific locale */
	protected LocaleCurrency localeCurrency;
	/** Operand to evaluate currency country */
	protected Operand countryOperand;
	
	/**
	 * Construct CurrencyOperand object for specified locale
     * @param qname Qualified name
     * @param locale The locale
	 */
	public CurrencyOperand(QualifiedName qname, Locale locale) 
	{
		super(qname);
		this.localeCurrency = new LocaleCurrency();
		localeCurrency.setLocale(locale);
	}

	/**
     * Construct CurrencyOperand object for specified value and locale
     * @param qname Qualified name
	 * @param value The value
	 * @param locale The locale
	 */
	public CurrencyOperand(QualifiedName qname, BigDecimal value, Locale locale) 
	{
		super(qname, value);
		this.localeCurrency = new LocaleCurrency();
		localeCurrency.setLocale(locale);
	}

	/**
     * Construct CurrencyOperand object with given expression Operand and specified locale
     * @param qname Qualified name
	 * @param expression Operand to evaluate value
     * @param locale The locale
	 */
	public CurrencyOperand(QualifiedName qname, Operand expression, Locale locale) 
	{
		super(qname, expression);
		this.localeCurrency = new LocaleCurrency();
		localeCurrency.setLocale(locale);
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
	 * formatValue
	 * @see au.com.cybersearch2.classy_logic.interfaces.TextFormat#formatValue()
	 */
	@Override
	public String formatValue()
	{   // Refresh locale-dependent currency component in case it has changed
	    if ((countryOperand != null)  && !countryOperand.isEmpty())
	        setCountry(countryOperand.getValue().toString());
		return localeCurrency.format(getValue());
	}
	
	/**
	 * Set Locale using 2-digit country code
	 * If currency format varies with language, then use 
	 * language and country code separated by '_' or '-'. eg. 'de_LU'
	 * Locale variants and scripts not supported.
     * @param country An ISO 3166 alpha-2 country code 
	 */
	public void setCountry(String country) 
	{
		String[] parts = country.split("_|-");
		if (parts.length == 2)
		{
			localeCurrency.setLocale(new Locale(parts[0], parts[1]));
			return;
		}
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
		localeCurrency.setLocale(matchedByCountry);
	}

	/**
	 * Evaluate value using data gathered during unification.
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
		EvaluationStatus status = super.evaluate(id);
		if (countryOperand != null) 
		{
			if (countryOperand.isEmpty())
				countryOperand.evaluate(id);
			setCountry(countryOperand.getValue().toString());
		}
		if (getValueClass().equals(String.class))
			value = localeCurrency.parse(value.toString());
		return status;
	}

	/**
	 * Backup to intial state if given id matches id assigned on unification or given id = 0. 
	 * @param id Identity of caller. 
	 * @return boolean true if backup occurred
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
	 * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
	 */
	@Override
	public boolean backup(int id)
	{
		if (countryOperand != null)
			countryOperand.backup(id);
		return super.backup(id);
	}
	

	/**
	 * Returns country operand, if set		
	 * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
	 */
	@Override
	public Operand getRightOperand() 
	{
		return countryOperand;
	}

	/**
	 * Binary multiply. Override to adjust rounding. 
	 * @param right BigDecimal object left term
	 * @param left BigDecimal object reight term
	 * @return BigDecimal object
	 */
	@Override
	protected BigDecimal calculateTimes(BigDecimal right, BigDecimal left)
	{
		BigDecimal newAmount = left.multiply(right);
	    // Gets the default number of fraction digits used with this currency.
	    // For example, the default number of fraction digits for the Euro is 2,
	    // while for the Japanese Yen it's 0.
	    // In the case of pseudo-currencies, such as IMF Special Drawing Rights,
	    // -1 is returned.
		int scale = localeCurrency.getFractionDigits();
		if (scale >= 0)
			newAmount = newAmount.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		return newAmount;
	}

	/**
	 * Binary divide. Override to adjust rounding. 
	 * @param right BigDecimal object left term
	 * @param left BigDecimal object reight term
	 * @return BigDecimal object
	 */
	@Override
	protected BigDecimal calculateDiv(BigDecimal right, BigDecimal left)
	{
		return left.divide(right, BigDecimal.ROUND_HALF_EVEN);
	}

	/**
	 * onScopeChange
	 * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
	 */
	@Override
	public void onScopeChange(Scope scope) 
	{
		localeCurrency.setLocale(scope.getLocale());
	}
}
