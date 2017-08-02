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
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.DelegateOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * MacroOperand
 * Converts a literal value ato desired type
 * @author Andrew Bowley
 * 3Aug.,2017
 */
public class MacroOperand extends Operand
{
    Object literalValue;
    
    /**
     * Construct MacroOperand for given value
     * @param literalValue Value to convert
     */
    public MacroOperand(Object literalValue)
    {
        super(Term.ANONYMOUS, literalValue);
        this.literalValue = literalValue;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getQualifiedName()
     */
    @Override
    public QualifiedName getQualifiedName()
    {
        return QualifiedName.ANONYMOUS;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#assign(au.com.cybersearch2.classy_logic.terms.Parameter)
     */
    @Override
    public void assign(Parameter parameter)
    {
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperand()
     */
    @Override
    public Operand getLeftOperand()
    {
        return null;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand()
    {
        return null;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getOperator()
     */
    @Override
    public Operator getOperator()
    {
        DelegateOperator operator = new DelegateOperator();
        operator.setDelegate(literalValue.getClass());
        return operator;
    }
}
