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

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.StringOperator;

/**
 * StringOperand
 * @author Andrew Bowley
 * 8 Dec 2014
 */
public class StringOperand  extends ExpressionOperand<String> implements LocaleListener
{
    /** Defines operations that an Operand performs with other operands. To be set by super. */
    protected StringOperator operator;

	/**
	 * Construct StringOperand with given expression Operand
     * @param qname Qualified name
	 * @param expression The Operand which evaluates value
	 */
	public StringOperand(QualifiedName qname, Operand expression) 
	{
		super(qname, expression);
		init();
	}

    /**
     * Construct StringOperand with given value
     * @param qname Qualified name
	 * @param value The value
	 */
	public StringOperand(QualifiedName qname, String value) 
	{
		super(qname, value);
        init();
	}

	/**
     * Construct StringOperand
     * @param qname Qualified name
	 */
	public StringOperand(QualifiedName qname) 
	{
		super(qname);
        init();
	}

	/**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
	 */
	@Override
	public void assign(Term term) 
	{
		setValue(term.getValue().toString());
		id = term.getId();
	}

    @Override
    public void onScopeChange(Scope scope)
    {
        operator.onScopeChange(scope);
    }

    @Override
    public Operator getOperator()
    {
        return operator;
    }

    private void init()
    {
        operator = new StringOperator();
    }

}
