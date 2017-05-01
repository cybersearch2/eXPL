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

import java.util.Locale;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.operator.DelegateType;

/**
 * CountryOperand
 * @author Andrew Bowley
 * 8 Mar 2015
 */
public class CountryOperand extends ExpressionOperand<String> 
{
    /** Trait of operand to be set by this operand */
    protected Trait targetTrait;
    /** Current country setting */
    protected String country;
    protected Operator operator;
    
	/**
	 * Construct CountryOperand object for specified locale
     * @param qname Qualified name
     * @param targetTrait Trait of operand to be set by this operand
     * @param expression Operand assigned to evaluate country
	 */
	public CountryOperand(QualifiedName qname, Trait targetTrait, Operand expression) 
	{
		super(qname, expression);
		this.targetTrait = targetTrait;
		country = targetTrait.getCountry();
		operator = DelegateType.ASSIGN_ONLY.getOperatorFactory().delegate();
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
	    if ((status == EvaluationStatus.COMPLETE) && !empty)
	        updateLocale();
		return status;
	}

    @Override
    public Operator getOperator()
    {
        return operator;
    }
    
    /**
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
     */
    @Override
    public String toString()
    {
        return country.isEmpty() ? expression.toString() : country;
    }

    protected void updateLocale()
    {
        String newCountry = getValue().toString();
        if (!newCountry.equals(country))
        {
            Locale locale = targetTrait.getLocaleByCode(newCountry);
            targetTrait.setLocale(locale);
            country = newCountry;
        }
    }

}
