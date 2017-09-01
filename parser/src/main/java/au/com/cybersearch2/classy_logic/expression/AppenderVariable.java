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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.list.Appender;

/**
 * AppenderVariable
 * Performs concatenation
 * @author Andrew Bowley
 * 3Aug.,2017
 */
public class AppenderVariable extends Variable
{

    /**
     * Construct AppenderVariable object
     * @param qname Qualified name of variable
     * @param termName Term name
     * @param expression Concatenation expression
     */
    public AppenderVariable(QualifiedName qname, String termName, Operand expression)
    {
        super(qname, termName, expression);

    }

    /**
     * Set exression to evaluate value to append to list
     * @param expression Operand object
     */
    public void setExpression(Operand expression)
    {
        this.expression = expression;
    }
    
    /**
     * evaluate
     * @see au.com.cybersearch2.classy_logic.expression.Variable#evaluate(int)
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        Appender appender = (Appender)rightOperand;
        if (appender.isEmpty())
            appender.evaluate(id);
        EvaluationStatus status = super.evaluate(id);
        if (expression == null)
        {   // Not used in concatenation expression, so just return appender value
            setValue(appender.getValue());
            return EvaluationStatus.COMPLETE;
        }
        if (status == EvaluationStatus.COMPLETE)
            appender.append(value);
        return status;
    }
}
