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

import au.com.cybersearch2.classy_logic.compile.AxiomTermListEvaluator;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;
import au.com.cybersearch2.classy_logic.operator.TermOperator;

/**
 * TermOperand
 * Contains an AxiomList value. 
 * Concatenation operation causes contents of right operand to be appended to the this operand.
 * Assignment is only other operation allowed.
 * @author Andrew Bowley
 * 5 Aug 2015
 */
public class TermOperand extends ExpressionOperand<AxiomTermList>
{
    /** Axiom key to use when an empty list is created */
    protected QualifiedName axiomKey;
    /** Creates an AxiomList object on evaluation */
    protected AxiomTermListEvaluator axiomTermListEvaluator;
    /** Defines operations that an Operand performs with other operands. To be set by super. */
    protected TermOperator operator;

    /**
     * Axiom term list variable with evaluator to initialze it
     */
    public TermOperand(AxiomTermListEvaluator axiomTermListEvaluator) 
    {
        super(axiomTermListEvaluator.getQualifiedName());
        this.axiomKey = axiomTermListEvaluator.getAxiomKey();
        this.axiomTermListEvaluator = axiomTermListEvaluator;
        operator = new TermOperator();
    }

    /**
     * Axiom term list operand
     */
    public TermOperand(AxiomTermList axiomTermList) 
    {
        super(axiomTermList.getQualifiedName());
        this.axiomKey = axiomTermList.getKey();
        setValue(axiomTermList);
        operator = new TermOperator();
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
        // Perform static intialisation to a list of axioms
        if (axiomTermListEvaluator != null)
        {
            // Only set id if axiom term list is created non-empty
            setValue(axiomTermListEvaluator.evaluate(id));
            if (axiomTermListEvaluator.size() > 0)
                 this.id = id;
            if (isEmpty())
                // If an error occurs populate with an empty list for graceful handling
                setValue(new AxiomTermList(qname, axiomKey));
        }
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
        if (axiomTermListEvaluator != null)
        {
            axiomTermListEvaluator.backup(id);
            return super.backup(id);
        }
        return false;
    }
    
    /**
     * Override toString() to incorporate intialization list
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
     */
    @Override
    public String toString()
    {
        if (empty)
            return axiomTermListEvaluator.toString();
        return super.toString();
    }

    @Override
    public Operator getOperator()
    {
        return operator;
    }
}
