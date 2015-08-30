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

import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;

/**
 * AxiomOperand
 * Contains an AxiomList value. 
 * Concatenation operation causes contents of right operand to be appended to the this operand.
 * Assignment is only other operation allowed.
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class AxiomOperand extends ExpressionParameter<AxiomList>implements Concaten<AxiomList>
{
    protected String axiomKey;
    protected ParameterList<AxiomList> parameterList;
    
    /**
     * Axiom Variable
     * @param qname Qualified name
     */
    public AxiomOperand(QualifiedName qname, String axiomKey) 
    {
        super(qname);
        this.axiomKey = axiomKey;
    }

    /**
     * Axiom Literal
     * @param qname Qualified name
     * @param value
     */
    public AxiomOperand(QualifiedName qname, AxiomList value) 
    {
        super(qname, value);
        axiomKey = value.getKey();
    }

    /**
     * Axiom Expression
     * @param qname Qualified name
     * @param expression Operand which evaluates value
     */
    public AxiomOperand(QualifiedName qname, String axiomKey, Operand expression) 
    {
        super(qname, expression);
        this.axiomKey = axiomKey;
    }

    /**
     * Axiom List
     * @param qname Qualified name
     * @param expression Operand which evaluates value
     */
    public AxiomOperand(QualifiedName qname, String axiomKey, ParameterList<AxiomList> parameterList) 
    {
        super(qname, parameterList.getParameters());
        this.axiomKey = axiomKey;
        this.parameterList = parameterList;
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
    @Override
    public EvaluationStatus evaluate(int id)
    {
        EvaluationStatus status = EvaluationStatus.COMPLETE;
        if (expression != null)
        {    
            if (parameterList != null)
            {   // Perform static intialisation to a list of axioms
                status = expression.evaluate(id);
                setValue(parameterList.evaluate());
                // Do not set id as the change is permanent unlees
                // a subsequent evaluation overrides this initialisation
            }
            else 
                status = super.evaluate(id);
            if (isEmpty())
                // If an error occurs populate with an empty list for graceful handling
                setValue(new AxiomList(qname, axiomKey));
        } 
        return status;
    }

    /**
     * concatenate
     * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @Override
    public AxiomList concatenate(Operand rightOperand)
    {
        return AxiomUtils.concatenate(this, rightOperand);
    }

}
