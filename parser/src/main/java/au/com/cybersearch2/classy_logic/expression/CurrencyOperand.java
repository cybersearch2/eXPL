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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.TextFormat;
import au.com.cybersearch2.classy_logic.trait.CurrencyTrait;

/**
 * CurrencyOperand
 * @author Andrew Bowley
 * 8 Mar 2015
 */
public class CurrencyOperand extends BigDecimalOperand implements TextFormat, LocaleListener
{
	/** Operand to evaluate currency country */
	protected Operand countryOperand;
	
	/**
	 * Construct CurrencyOperand object for specified locale
     * @param qname Qualified name
     * @param countryOperand Optional operand for setting fixed locale
	 */
	public CurrencyOperand(QualifiedName qname, Operand countryOperand) 
	{
		super(qname);
		if (countryOperand != null)
		    setCountyOperand(countryOperand);
		else
		    trait = new CurrencyTrait(trait.getOperandType());
	}

    /**
     * Construct CurrencyOperand object for specified value and locale
     * @param qname Qualified name
	 * @param value The value
     * @param countryOperand Optional operand for setting fixed locale
	 */
	public CurrencyOperand(QualifiedName qname, BigDecimal value, Operand countryOperand) 
	{
		super(qname, value);
        if (countryOperand != null)
            setCountyOperand(countryOperand);
        else
            trait = new CurrencyTrait(trait.getOperandType());
	}

	/**
     * Construct CurrencyOperand object with given expression Operand and specified locale
     * @param qname Qualified name
	 * @param expression Operand to evaluate value
     * @param countryOperand Optional operand for setting fixed locale
	 */
	public CurrencyOperand(QualifiedName qname, Operand expression, Operand countryOperand) 
	{
		super(qname, expression);
        if (countryOperand != null)
            setCountyOperand(countryOperand);
        else
            trait = new CurrencyTrait(trait.getOperandType());
	}

	/**
	 * formatValue
	 * @see au.com.cybersearch2.classy_logic.interfaces.TextFormat#formatValue()
	 */
	@Override
	public String formatValue(Object value)
	{   // Refresh locale-dependent currency component in case it has changed
	    updateLocale();
		return trait.formatValue(value);
	}
	
	/**
	 * Evaluate value using data gathered during unification.
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
		EvaluationStatus status = evaluateExpression(id);
		if (countryOperand != null) 
		{
			if (countryOperand.isEmpty())
				countryOperand.evaluate(id);
			updateLocale();
		}
		if (getValueClass().equals(String.class))
			value = getCurrencyTrait().parseValue(value.toString());
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
		int scale = getCurrencyTrait().getFractionDigits();
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
	 * TODO - 
	 * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
	 */
	@Override
	public void onScopeChange(Scope scope) 
	{
	    if (getCountry().isEmpty() && (countryOperand == null))
	        trait.setLocale(scope.getLocale());
	}

    /**
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
     */
    @Override
    public String toString()
    {
        return !getCountry().isEmpty() ? getCountry() + " " + super.toString() : super.toString();
    }

   /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    protected EvaluationStatus evaluateExpression(int id)
    {
        EvaluationStatus status = EvaluationStatus.COMPLETE;
        if (expression != null)
        {
            status = expression.evaluate(id);
            if (!expression.isEmpty())
            {
                this.value = expression.getValue();
                this.empty = false;
                this.id = id;
            }
        }
        return status;
    }
    

    private void setCountyOperand(Operand countryOperand)
    {
        this.countryOperand = countryOperand;
        CurrencyTrait currencyTrait = new CurrencyTrait(trait.getOperandType());
        if (!countryOperand.isEmpty())
            currencyTrait.setLocale(currencyTrait.getLocaleByCode(countryOperand.getValue().toString()));
        trait = currencyTrait;
    }

    private void updateLocale()
    {
        if ((countryOperand != null)  && !countryOperand.isEmpty())
            trait.setLocale(getCurrencyTrait().getLocaleByCode(countryOperand.getValue().toString()));
    }

    private CurrencyTrait getCurrencyTrait()
    {
        return (CurrencyTrait)trait;
    }
    
    private String getCountry()
    {
        return getCurrencyTrait().getCountry();
    }
}
