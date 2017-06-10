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

import au.com.cybersearch2.classy_logic.compile.AxiomListEvaluator;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListListener;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.operator.AxiomOperator;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomOperand
 * Contains an AxiomList value. 
 * Concatenation operation causes contents of right operand to be appended to the this operand.
 * Assignment is only other operation allowed.
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class AxiomOperand extends ExpressionOperand<AxiomList>
{
    /** Axiom key to use when an empty list is created */
    protected QualifiedName axiomKey;
    /** Creates an AxiomList object on evaluation */
    protected AxiomListEvaluator axiomListEvaluator;
    /** Axiom listener to notify when an axiom list is created/assigned */
    protected AxiomListListener axiomListListener;
    /** Defines operations that an Operand performs with other operands. To be set by super. */
    protected AxiomOperator operator;
    
    /**
     * Axiom list variable
     * @param qname Qualified name
     * @param axiomKey Axiom key to use when an empty list is created
     * @param axiomListListener Axiom listener to notify when an axiom list is created/assigned
     */
    public AxiomOperand(QualifiedName qname, QualifiedName axiomKey, AxiomListListener axiomListListener) 
    {
        super(qname);
        this.axiomKey = axiomKey;
        this.axiomListListener = axiomListListener;
        init();
    }

    /**
     * Axiom list literal
     * @param qname Qualified name
     * @param value Axiom list literal value
     */
    public AxiomOperand(QualifiedName qname, AxiomList value) 
    {
        super(qname, value);
        axiomKey = value.getKey();
        init();
    }

    /**
     * Axiom List
     * @param qname Qualified name
     * @param axiomKey Axiom key to use when an empty list is created
     * @param parameterList Parameter container which creates an AxiomList object on evaluation
     * @param axiomListListener Axiom listener to notify when an axiom list is created
     */
    public AxiomOperand(AxiomListEvaluator axiomListEvaluator, AxiomListListener axiomListListener) 
    {
        super(axiomListEvaluator.getQualifiedName());
        this.axiomKey = axiomListEvaluator.getAxiomKey();
        this.axiomListEvaluator = axiomListEvaluator;
        this.axiomListListener = axiomListListener;
        init();
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
        if (axiomListEvaluator != null)
        {   // Perform static intialisation to a list of axioms
            setValue(axiomListEvaluator.evaluate(id));
            // Do not set id if list is created empty
            if (axiomListEvaluator.size() > 0)
                this.id = id;
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
        if (axiomListEvaluator != null)
            axiomListEvaluator.backup(id);
        return super.backup(id);
    }
    
    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
    {
        super.assign(parameter);
        AxiomList axiomList = (AxiomList)parameter.getValue();
        if (axiomListListener != null)
            axiomListListener.addAxiomList(qname, axiomList);
    }

    /**
     * Override toString() to incorporate intialization list
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
     */
    @Override
    public String toString()
    {
        if (axiomListEvaluator != null)
        {
            StringBuilder builder = new StringBuilder("list<axiom> ");
            builder.append(qname.toString());
            int length = empty ? axiomListEvaluator.size() : ((AxiomList)getValue()).getLength();
            builder.append('[').append(Integer.toString(length)).append(']');
            return builder.toString();
        }
        else
            return super.toString();
    }

    /**
     * getOperator
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getOperator()
     */
    @Override
    public Operator getOperator()
    {
        return operator;
    }

    /**
     * Set operator
     */
    private void init()
    {
        operator = new AxiomOperator();
    }

}
