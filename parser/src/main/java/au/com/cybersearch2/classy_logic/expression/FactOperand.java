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

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.EvaluationUtils;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.pattern.Axiom;

/**
 * FactOperand
 * @author Andrew Bowley
 * 20 Aug 2015
 */
public class FactOperand extends BooleanOperand
{

    public FactOperand(Operand expression)
    {
        super(getFactName(expression), expression);
        
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    public EvaluationStatus evaluate(int id)
    {
        if ((expression == null) || expression.isEmpty())
            setValue(false);
        else
        {
            Object object = expression.getValue();
            if (object instanceof AxiomTermList)
            {
                AxiomTermList axiomTermList = (AxiomTermList)object;
                Axiom axiom = axiomTermList.getAxiom();
                // isFact() returns true for an empty axiom, which is not what we want
                setValue(axiom.isFact() && (axiom.getTermCount() > 0));
            }
            else if ((object instanceof Number) && object.toString().equals(EvaluationUtils.NAN))
                setValue(false);
            else
                setValue(expression.getValueClass() != Unknown.class);
        }
        this.empty = false;
        this.id = id;
        return EvaluationStatus.COMPLETE;
    }
    
    protected static QualifiedName getFactName(Operand expression)
    {
        return new QualifiedName("is_" + expression.getName() + "_fact", expression.getQualifiedName());
    }

}
