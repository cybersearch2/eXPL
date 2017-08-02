/**
    Copyright (C) 2017  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * ProxyAxiomOperand
 * @author Andrew Bowley
 * 1Aug.,2017
 */
public class ProxyAxiomOperand extends ExpressionOperand<AxiomList>
{

    /**
     * 
     */
    public ProxyAxiomOperand(QualifiedName qname, AxiomOperand proxy)
    {
        super(qname, proxy);
    }

    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
    {
        expression.assign(parameter);
    }
    
    @Override
    public Operator getOperator()
    {
        return expression.getOperator();
    }

    /**
     * Override toString() to incorporate intialization list
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
     */
    @Override
    public String toString()
    {
        return expression.toString();
    }
}
