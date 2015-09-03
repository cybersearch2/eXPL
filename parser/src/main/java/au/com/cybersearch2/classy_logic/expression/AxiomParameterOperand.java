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

import au.com.cybersearch2.classy_logic.helper.AxiomUtils;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.OperandParam;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.CallEvaluator;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.terms.GenericParameter;

/**
 * AxiomParameterOperand
 * Populates an axiom from a parameter list after the list operands have been evaluated.
 * As this class extends AxiomOperand it supports AxiomList concatenation and assignment.
 * @author Andrew Bowley
 * 9 Aug 2015
 */
public class AxiomParameterOperand extends GenericParameter<AxiomTermList> implements Operand
{
    /** Qualified name of operand */
    protected QualifiedName qname;
    /** Collects parameters from an Operand tree and passes them to a supplied function object */
    protected ParameterList<AxiomTermList> parameterList;
    /** Name of axiom list to be generated */
    protected QualifiedName axiomName;
    /** The axiom key */  
    protected String axiomKey;
    /** Root of Operand tree for unification */
    protected Operand paramsTreeRoot;

    /**
     * Construct an AxiomParameterOperand object
     * @param qname Qualified name
     * @param axiomKey Key of axiom list to be generated
     * @param argumentExpression Operand containing parameters as a tree of operands
     */
    public AxiomParameterOperand(QualifiedName qname, String axiomKey, List<OperandParam> initializeList)
    {
        super(qname.getName());
        this.qname = qname;
        this.axiomKey = axiomKey;
        this.axiomName = new QualifiedName(axiomKey, qname);
        if ((initializeList != null) && !initializeList.isEmpty())
        {
            parameterList = new ParameterList<AxiomTermList>(initializeList, axiomGenerator());
            paramsTreeRoot = OperandParam.buildOperandTree(initializeList);
        }
    }

    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

    /**
     * Execute operation for expression
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        if (parameterList == null)
        {
            AxiomTermList axiomTermList = new AxiomTermList(axiomName, axiomKey);
            setValue(axiomTermList);
            return EvaluationStatus.COMPLETE;
        }
        setValue(parameterList.evaluate(id));
        this.id = id;
        return EvaluationStatus.COMPLETE;
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
     * Returns Value for no concatenation operations
     * @return OperatorEnum[]
     */
    @Override
    public OperatorEnum[] getStringOperandOps()
    {
        return Operand.EMPTY_OPERAND_OPS;
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
     * Returns operand tree fur parameter unification     
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return paramsTreeRoot;
    }
    
    @Override
    public Operand getLeftOperand()
    {
        return null;
    }

    /**
     * Returns an object which implements CallEvaluator interface returning an AxiomList
     * given a list of terms to marshall into an axiom
     * @return CallEvaluator object of generic return type AxiomList
     */
    protected CallEvaluator<AxiomTermList> axiomGenerator() 
    {
        return new CallEvaluator<AxiomTermList>(){

            @Override
            public String getName()
            {
                return name;
            }

            @Override
            public AxiomTermList evaluate(List<Term> argumentList)
            {
                return AxiomUtils.marshallAxiomTerms(axiomName, axiomName.getName(), argumentList);
            }};
    }

}
