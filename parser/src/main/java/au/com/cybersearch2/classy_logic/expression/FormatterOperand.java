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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * Formatter
 * @author Andrew Bowley
 * 10 Mar 2015
 */
public class FormatterOperand extends StringOperand implements LocaleListener
{
	/** The locale */
	protected Locale locale;
	
	/**
     * @param qname Qualified name
	 * @param operand The Operand containing the value to format
	 * @param locale Initial locale - can be updated through LocaleListener interface
	 */
	public FormatterOperand(QualifiedName qname, Operand operand, Locale locale) 
	{
		super(qname, operand);
         this.locale = locale;
	}

	/**
	 * Evaluate formatted value. 
	 * @param id Identity of caller, which must be provided for backup()
	 * @return Flag set true if evaluation is to continue
	 */
	@Override
	public EvaluationStatus evaluate(int id) 
	{
	    EvaluationStatus status = super.evaluate(id);
	    if (status != EvaluationStatus.COMPLETE)
	        return status;
	    Object expressionValue = expression.getValue();
	    String formatValue;
	    if ((expressionValue instanceof Unknown) || 
	        (expressionValue instanceof Null) ||
	        (expressionValue instanceof Double && ((Double)expressionValue).isNaN()))
            formatValue = Unknown.UNKNOWN;
        else
            try
    	    {
                 formatValue= expression.getOperator().getTrait().formatValue(expressionValue);
    	    }
    	    catch(IllegalArgumentException e)
    	    {
                formatValue = Unknown.UNKNOWN;
    	    }
		setValue(formatValue);
		this.id = id;
		return EvaluationStatus.COMPLETE;
	}

	/**
	 * onScopeChange
	 * @see au.com.cybersearch2.classy_logic.interfaces.LocaleListener#onScopeChange(au.com.cybersearch2.classy_logic.Scope)
	 */
	@Override
	public void onScopeChange(Scope scope) 
	{
		locale = scope.getLocale();
	}

}
