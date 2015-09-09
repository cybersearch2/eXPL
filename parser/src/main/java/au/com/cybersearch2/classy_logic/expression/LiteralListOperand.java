/**
    Copyright (C) 2015  www.cybersearch2.com.au

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

import java.util.List;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * LiteralListOperand
 * @author Andrew Bowley
 * 8 Sep 2015
 */
public class LiteralListOperand extends Variable
{
    protected List<Parameter> literalList;
    
    public LiteralListOperand(QualifiedName qname, List<Parameter> literalList)
    {
        super(qname);
        this.literalList = literalList;
    }

    /**
     * Evaluate value by trying to match the target value to one of the literal list values.
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    @Override
    public EvaluationStatus evaluate(int id) 
    {
        boolean match = false;
        if (!isEmpty())
        {
            for (Parameter param: literalList)
                if (param.getValue().equals(value))
                {
                    match = true;
                    break;
                }
        }
        this.id = id;
        return match ? EvaluationStatus.COMPLETE : EvaluationStatus.SHORT_CIRCUIT;
    }

}
