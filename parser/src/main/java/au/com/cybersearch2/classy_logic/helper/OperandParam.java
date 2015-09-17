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
 * Wraps Operand object for the purpose of using it as a named parameter/argument.
 * Only Term interface is exposed.
 * @author Andrew Bowley
 * 2 Sep 2015
 */
public class OperandParam implements Term
{
    /** The operand */
    protected Operand operand;
    /** The parameter name, hides Operand name */
    protected String name;

    /**
     * Construct OperandParam object
     * @param name
     * @param operand Operand object
     */
    public OperandParam(String name, Operand operand)
    {
        this.operand = operand;
        this.name = name;
    }

    /**
     * Returns the operand
     * @return Operand object
     */
    public Operand getOperand()
    {
        return operand;
    }
 
    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#getValue()
     */
    @Override
    public Object getValue()
    {
        return operand.getValue();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#getValueClass()
     */
    @Override
    public Class<?> getValueClass()
    {
        return operand.getValueClass();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#setName(java.lang.String)
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#isEmpty()
     */
    @Override
    public boolean isEmpty()
    {
        return operand.isEmpty();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#backup(int)
     */
    @Override
    public boolean backup(int id)
    {
        return operand.backup(id);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        return operand.unifyTerm(otherTerm, id);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#evaluate(int)
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        return operand.evaluate(id);
    }

    /**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
     */
    @Override
    public void assign(Term term)
    {
        operand.assign(term);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#getId()
     */
    @Override
    public int getId()
    {
        return operand.getId();
    }

    /**
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return name.isEmpty() ? operand.getQualifiedName().hashCode() : name.hashCode();
    }

    /**
     * Display only name information
     * @see java.lang.Object#toString()
     */
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

    @Override
    public void setValue(Object value)
    {
    }

    @Override
    public void clearValue()
    {
    }
    
}
