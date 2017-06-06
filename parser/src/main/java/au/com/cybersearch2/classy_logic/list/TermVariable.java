/**
    Copyright (C) 2017  www.cybersearch2.com.au

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

import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * TermVariable
 * Evaluates to resolve axiom term list which accesses a single-term axiom
 * @author Andrew Bowley
 * 22May,2017
 */
public class TermVariable extends Variable
{
    /** Axiom term list to reference */
    protected AxiomTermList itemList;
    /** Operand which evaluates the list index */
    protected Operand indexExpression;
    
    /**
     * Construct TermVariable object
     * @param qname Qualified name
     * @param itemList Axiom term list to reference
     * @param indexExpression Operand which evaluates the list index
     */
    public TermVariable(QualifiedName qname, AxiomTermList itemList, Operand indexExpression)
    {
        super(qname);
        this.itemList = itemList;
        this.indexExpression = indexExpression;
    }

    /**
     * Evaluate term value after unification stage has set index expression.
     * @param id Identity of caller, which must be provided for backup()
     * @return EvaluationStatus
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        indexExpression.evaluate(id);
        this.id = id;
        boolean isIntegerIndex = indexExpression.getValueClass() == Long.class;
        Term term = itemList.getAxiom().getTermByIndex(0);
        if (term.getValueClass() == AxiomList.class)
        {
            if (!isIntegerIndex)
                throw new ExpressionException("List \"" + term.getName() + "\" cannot be indexed by " + indexExpression.toString());
            int index = ((Long)indexExpression.getValue()).intValue();
            AxiomList axiomList = (AxiomList)term.getValue();
            Parameter parameter = new Parameter(axiomList.getName(), axiomList.getItem(index));
            parameter.setId(id);
            assign(parameter);
        }
        else if (term.getValueClass() == AxiomTermList.class)
        {
            AxiomTermList axiomTermList = (AxiomTermList)term.getValue();
            Axiom axiom = axiomTermList.getAxiom();
            Term item = null;
            String termName = null;
            if (isIntegerIndex)
            {
                int index = ((Long)indexExpression.getValue()).intValue();
                item = axiom.getTermByIndex(index);
                termName = itemList.getAxiomTermNameList().get(index);
            }
            else
            {
                termName = Term.ANONYMOUS.equals(term.getName()) ? term.getValue().toString() : term.getName();
                item = axiom.getTermByName(termName);
            }
            if (item == null)
                throw new ExpressionException("Axiom \"" + term.getName() + "\" cannot be indexed by " + indexExpression.toString());
            Parameter parameter = new Parameter(termName, item.getValue());
            parameter.setId(id);
            assign(parameter);
        }
        else
            throw new ExpressionException("\"" + term.getName() + "\" cannot be indexed by " + indexExpression.toString());
        return EvaluationStatus.COMPLETE;
    }
    
    @Override
    public boolean backup(int id)
    {
        return super.backup(id);
    }
}
