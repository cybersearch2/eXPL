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
package au.com.cybersearch2.classy_logic.helper;

import java.util.List;

import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * OperandParam
 * @author Andrew Bowley
 * 2 Sep 2015
 */
public class OperandParam implements Term
{
    protected Operand operand;
    protected String name;

    public OperandParam(String name, Operand operand)
    {
        this.operand = operand;
        this.name = name;
    }
    
    public Operand getOperand()
    {
        return operand;
    }
    
    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Object getValue()
    {
        return operand.getValue();
    }

    @Override
    public Class<?> getValueClass()
    {
        return operand.getValueClass();
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public boolean isEmpty()
    {
        return operand.isEmpty();
    }

    @Override
    public boolean backup(int id)
    {
        return operand.backup(id);
    }

    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        return operand.unifyTerm(otherTerm, id);
    }

    @Override
    public EvaluationStatus evaluate(int id)
    {
        return operand.evaluate(id);
    }

    @Override
    public void assign(Object value)
    {
        operand.assign(value);
    }

    @Override
    public int getId()
    {
        return operand.getId();
    }

    @Override
    public int hashCode()
    {
        return name.isEmpty() ? operand.getQualifiedName().hashCode() : name.hashCode();
    }

    @Override
    public String toString()
    {
        return name.isEmpty() ? operand.getQualifiedName().toString() : name.toString();
    }

    /**
     * Build parameter tree for unification
     */
    public static Operand buildOperandTree(List<OperandParam> operandParamList)
    {
        int index = 0;
        Operand[] params = new Operand[2];
        params[0] = operandParamList.get(index++).getOperand();
        while (true)
        {
            if (index == operandParamList.size())
                break;
            params[1] = operandParamList.get(index++).getOperand();
            params[0] = new Evaluator(params[0], ",", params[1]);
        }
        return params[0];
    }
    
}
