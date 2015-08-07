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

import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;

/**
 * AxiomOperand
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class AxiomOperand extends ExpressionParameter<AxiomList>implements Concaten<AxiomList>
{
    /**
     * Axiom Variable
     * @param name
     */
    public AxiomOperand(String name) 
    {
        super(name);
    }

    /**
     * Axiom Literal
     * @param name
     * @param value
     */
    public AxiomOperand(String name, AxiomList value) 
    {
        super(name, value);
    }

    /**
     * Axiom Expression
     * @param name
     * @param expression Operand which evaluates value
     */
    public AxiomOperand(String name, Operand expression) 
    {
        super(name, expression);

    }
    /**
     * Update Parameter value - use for assignment operation
     * @param value Object containing new value. Must be AxiomList or sub class
     * @see au.com.cybersearch2.classy_logic.interfaces.Term#assign(java.lang.Object)
     */
    @Override
    public void assign(Object value) 
    {
        AxiomList axiomList = (AxiomList)value;
        setValue(axiomList);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.expression.NullOperand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return  new OperatorEnum[]
        { 
            OperatorEnum.ASSIGN,
         };
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.expression.NullOperand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return  new OperatorEnum[]
        { 
                OperatorEnum.ASSIGN,
        };
    }

    @Override
    public OperatorEnum[] getStringOperandOps()
    {  // Allow concatenation like String
       // Two operands must be congruent, the left hand operand is
       // appended to the right hand one
       return  new OperatorEnum[]
       { 
           OperatorEnum.PLUS,  
           OperatorEnum.PLUSASSIGN
       };
    }


    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a number
        return new Integer(0);
    }

    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a number
        return new Integer(0);
    }

    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a boolean
        return Boolean.FALSE;
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = super.evaluate(id);
        if (isEmpty())
            setValue(new AxiomList(name,name));
        return status;
    }
    

    @Override
    public AxiomList concatenate(Operand rightOperand)
    {
        if (rightOperand.isEmpty()) // Add empty list means no change
            return (AxiomList)getValue();
        if (isEmpty()) // Just assign left to right if this operand is empty
            return (AxiomList)rightOperand.getValue();
        // Check for congruence. Both Operands must be AxiomOperands with
        // AxiomLists containing matching Axioms
        if (!isCongruent(rightOperand))
            throw new ExpressionException("Cannot concatenate " + toString() + " to " + rightOperand.toString());
        AxiomList rightAxiomList = (AxiomList)rightOperand.getValue();
        AxiomList leftAxiomList = (AxiomList)getValue();
        // For efficiency, update the value of this operand as it will be assigned back to it anyway.
        Iterator<AxiomTermList> iterator = rightAxiomList.getIterable().iterator();
        int index = leftAxiomList.getLength();
        while (iterator.hasNext())
            leftAxiomList.assignItem(index++, iterator.next());
        return getValue();
    }

    protected boolean isCongruent(Operand operand)
    {
        if (!(operand.getValueClass() == AxiomList.class))
            return false;
        AxiomList rightAxiomList = (AxiomList)operand.getValue();
        AxiomList leftAxiomList = (AxiomList)getValue();
        List<String> leftTermNames = leftAxiomList.getAxiomTermNameList();
        List<String> rightTermNames = rightAxiomList.getAxiomTermNameList();
        if ((leftTermNames != null) && (rightTermNames != null))
        {
            if (leftTermNames.size() != rightTermNames.size())
                return false;
            int index = 0;
            for (String termName: rightTermNames)
                if (!termName.equalsIgnoreCase(leftTermNames.get(index++)))
                    return false;
        }
        else if ((leftTermNames != null) || (rightTermNames != null))
            return false;
        return true;
    }

}
