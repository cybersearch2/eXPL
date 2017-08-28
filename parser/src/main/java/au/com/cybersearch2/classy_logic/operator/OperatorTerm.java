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
package au.com.cybersearch2.classy_logic.operator;

import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * OperatorTerm
 * Parameter with Operator field for recreating variables such as Currency 
 * which depend on operator for correct behavour
 * @author Andrew Bowley
 * 28Aug.,2017
 */
public class OperatorTerm extends Parameter
{
    protected Operator operator;
    
    /**
     * @param name
     * @param value
     */
    public OperatorTerm(String name, Object value, Operator operator)
    {
        super(name, value);
        this.operator = operator;
    }

    /**
     * @return the operator
     */
    public Operator getOperator()
    {
        return operator;
    }

}
