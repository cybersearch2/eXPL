/**
    Copyright (C) 2016  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.list;

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionOperand;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.ListVariableFactory;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * ItemListVariableOperand
 * @author Andrew Bowley
 * 19Jan.,2017
 */
public class ItemListVariableOperand extends ExpressionOperand<Object> implements Concaten<String>
{
    /** Unique identity generator */
    static protected int referenceCount;

    String listName;
    ListVariableFactory listVariableFactory;
    Operand expression2 = null;
    Operand indexExpression = null;
    

    public ItemListVariableOperand(String listName, 
            ListVariableFactory listVariableFactory, 
                                       Operand indexExpression, 
                                       Operand expression2)
    {
        super(getQualifiedName(listName), null);
        this.listName = listName;
        this.listVariableFactory = listVariableFactory;
        this.indexExpression = indexExpression;
        this.expression2 = expression2;
    }

    /**
     * Returns Parameter name
     * @return String
     */
    @Override
    public String getName()
    {
        return expression == null ? qname.getName() : expression.getName();
    }

    /**
     * Set Parameter value
     * @param value
     */
    @Override
    public void setValue(Object value)
    {
        super.setValue(value);
        if (!empty)
            expression.assign(this);
    }
    
    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return expression.getRightOperandOps();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return expression.getLeftOperandOps();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return expression.numberEvaluation(operatorEnum2, rightTerm);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return expression.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return expression.booleanEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    /**
     * concatenate
     * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String concatenate(Operand rightOperand)
    {
        return ((Concaten<String>)expression).concatenate(rightOperand);
    }

    public void initialize()
    {
        clearValue();
        if (expression == null)
        {
            expression = listVariableFactory.operandInstance(listName, indexExpression, expression2);
//            expression = parserAssembler.setListVariable(listName, indexExpression, assignExpression);
        }
    }
    
    static protected QualifiedName getQualifiedName(String name)
    {
        return new QualifiedName(name + "_var" + ++referenceCount);
    }
}
