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
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * SelectionOperand
 * @author Andrew Bowley
 * 2Aug.,2017
 */
public class SelectionOperand extends Variable
{
    /**
     * @param qname
     */
    public SelectionOperand(QualifiedName qname, Template template)
    {
        super(qname, new TemplateOperand(new QualifiedName(qname.getName() + "_choice", qname), template));
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if (status == EvaluationStatus.COMPLETE)
        {
            TemplateOperand templateOperand = (TemplateOperand)expression;
            Object value = templateOperand.getSelection();
            if (value != null)
            {
                setValue(value);
                this.id = id;
            }
        }
        return status;
    }
}
