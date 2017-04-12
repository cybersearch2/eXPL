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
import au.com.cybersearch2.classy_logic.helper.OperandParam;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListListener;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;

/**
 * AxiomOperand
 * Contains an AxiomList value. 
 * Concatenation operation causes contents of right operand to be appended to the this operand.
 * Assignment is only other operation allowed.
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class AxiomOperand extends ExpressionOperand<AxiomList>implements Concaten<AxiomList>
{
    /** Axiom key to use when an empty list is created */
    protected QualifiedName axiomKey;
    /** Parameter container which creates an AxiomList object on evaluation */
    protected ParameterList<AxiomList> parameterList;
    /** Axiom listener to notify when an axiom list is created/assigned */
    protected AxiomListListener axiomListListener;
    /** Root of Operand tree for unification */
    protected Operand paramsTreeRoot;
    
    /**
     * Axiom Variable
     * @param qname Qualified name
     * @param axiomKey Axiom key to use when an empty list is created
     * @param axiomListListener Axiom listener to notify when an axiom list is created/assigned
     */
    public AxiomOperand(QualifiedName qname, QualifiedName axiomKey, AxiomListListener axiomListListener) 
    {
        super(qname);
        this.axiomKey = axiomKey;
        this.axiomListListener = axiomListListener;
    }

    /**
     * Axiom Literal
     * @param qname Qualified name
     * @param value Axiom list literal value
     */
    public AxiomOperand(QualifiedName qname, AxiomList value) 
    {
        super(qname, value);
        axiomKey = value.getKey();
    }

    /**
     * Axiom Variable with Expression operand to evaluate value
     * @param axiomKey Axiom key to use when an empty list is created
     * @param qname Qualified name
     * @param expression Operand which evaluates value
     * @param axiomListListener Axiom listener to notify when an axiom list is created
     */
    public AxiomOperand(QualifiedName qname, QualifiedName axiomKey, Operand expression, AxiomListListener axiomListListener) 
    {
        super(qname, expression);
        this.axiomKey = axiomKey;
        this.axiomListListener = axiomListListener;
    }

    /**
     * Axiom List
     * @param qname Qualified name
     * @param axiomKey Axiom key to use when an empty list is created
     * @param parameterList Parameter container which creates an AxiomList object on evaluation
     * @param axiomListListener Axiom listener to notify when an axiom list is created
     */
    public AxiomOperand(QualifiedName qname, QualifiedName axiomKey, ParameterList<AxiomList> parameterList, AxiomListListener axiomListListener) 
    {
        super(qname);
        this.axiomKey = axiomKey;
        this.parameterList = parameterList;
        this.axiomListListener = axiomListListener;
        if ((parameterList.getOperandParamList() != null) && 
                !parameterList.getOperandParamList().isEmpty())
            paramsTreeRoot = OperandParam.buildOperandTree(parameterList.getOperandParamList());
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
        {   // Evaluate value using expression operand  
            boolean firstTime = empty;
            status = super.evaluate(id);
            if (firstTime && !empty)
                axiomListListener.addAxiomList(qname, getValue());
        }
        else if (parameterList != null)
        {   // Perform static intialisation to a list of axioms
            setValue(parameterList.evaluate(id));
            // Do not set id as the change is permanent unless
            // a subsequent evaluation overrides this initialisation
            axiomListListener.addAxiomList(qname, getValue());
        }
        if (isEmpty())
            // If an error occurs populate with an empty list for graceful handling
            setValue(new AxiomList(qname, axiomKey));
        return status;
    }

    /**
     * Backup to intial state if given id matches id assigned on unification or given id = 0. 
     * @param id Identity of caller. 
     * @return boolean true if backup occurred
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unify(Term otherParam, int id)
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#evaluate(int id)
     */
    @Override
    public boolean backup(int id)
    {
        if (paramsTreeRoot != null)
            paramsTreeRoot.backup(id);
        return super.backup(id);
    }
    
    /**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
     */
    @Override
    public void assign(Term term) 
    {
        super.assign(term);
        AxiomList axiomList = (AxiomList)term.getValue();
        if (axiomListListener != null)
            axiomListListener.addAxiomList(qname, axiomList);
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

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#getStringOperandOps()
     */
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

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a number
        return new Integer(0);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a number
        return new Integer(0);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2, Term rightTerm)
    {   // There is no valid evaluation involving an axiom resulting in a boolean
        return Boolean.FALSE;
    }

    /**
     * concatenate
     * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @Override
    public AxiomList concatenate(Operand rightOperand)
    {
        AxiomList leftList = (AxiomList)getValue();
        if (leftList.getLength() == 0)
        {
            QualifiedName key;
            if (rightOperand.getValueClass().equals(AxiomList.class))
            {
                AxiomList rightList = (AxiomList)rightOperand.getValue();
                key = rightList.getKey();
            }
            else
            {
                AxiomTermList rightList = (AxiomTermList)rightOperand.getValue();
                key = rightList.getKey();
            }
            leftList.setKey(key);
        }
        return AxiomUtils.concatenate(this, rightOperand);
    }

    /**
     * Returns operand tree fur parameter unification     
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return paramsTreeRoot;
    }
 
    /**
     * Override toString() to incorporate intialization list
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
     */
    @Override
    public String toString()
    {
        if (parameterList != null)
        {
            StringBuilder builder = new StringBuilder("list<axiom> ");
            builder.append(qname.toString());
            int length = empty ? parameterList.getOperandParamList().size() : ((AxiomList)getValue()).getLength();
            builder.append('[').append(Integer.toString(length)).append(']');
            return builder.toString();
        }
        else
            return super.toString();
    }
}
