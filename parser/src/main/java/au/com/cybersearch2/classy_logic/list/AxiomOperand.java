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
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * AxiomOperand
 * Variable Operand to access an item of either an axiom or a term of an axiom
 * @author Andrew Bowley
 * 22Jan.,2017
 */
public class AxiomOperand extends ListVariableOperand implements ParserRunner
{
    protected QualifiedName listName;
    
    /**
     * Construct an AxiomOperand object
     * @param listName
     * @param indexExpression
     * @param expression2
     */
    public AxiomOperand(QualifiedName listName, Operand indexExpression,
            Operand expression2)
    {
        super(listName, indexExpression, expression2);
        this.listName = listName;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        if (!listName.getScope().isEmpty() && (parserAssembler.getScope().findScope(listName.getScope()) == null))
        {
            final AxiomTermList itemList = parserAssembler.getAxiomTermList(listName);
            if (itemList.getAxiomTermNameList().size() == 1)
            {
                // Dereference single term of returned axiom
                // throw new ExpressionException(
                Variable var = new Variable(new QualifiedName(listName.getName() + listName.incrementReferenceCount(), listName))
                {
                    public EvaluationStatus evaluate(int id)
                    {
                        indexExpression.evaluate(id);
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
                };
                //parserAssembler.getOperandMap().addOperand(operand);
                expression = var;
                return;
            }
            expression = parserAssembler.getOperandMap().newListVariableInstance(itemList, indexExpression);
        }
        else
            expression = newListVariableInstance(parserAssembler);
    }


}
