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

import au.com.cybersearch2.classy_logic.compile.ListAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;

/**
 * AxiomContainerOperand
 * Variable Operand to access an item of either an axiom or a term of an axiom
 * @author Andrew Bowley
 * 22Jan.,2017
 */
public class AxiomContainerOperand extends ListVariableOperand implements ParserRunner
{
    protected QualifiedName listName;
    
    /**
     * Construct an AxiomContainerOperand object
     * @param listName List qualified name
     * @param indexExpression Operand which evaluates the list index
     * @param expression2 Second expression for selection or assignment depending on usage
     */
    public AxiomContainerOperand(QualifiedName qname, QualifiedName listName, Operand indexExpression,
            Operand expression2)
    {
        super(qname, listName, indexExpression, expression2);
        this.listName = listName;
    }

    /**
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        if (!listName.getScope().isEmpty() && (parserAssembler.getScope().findScope(listName.getScope()) == null))
        {   // This is a list to return the result of a query call
            final AxiomTermList itemList = listAssembler.getAxiomTerms(listName);
            if (itemList.getAxiomTermNameList().size() == 1)
            {   // Set operand name to name of single term indicated by index expression name
                setName(indexExpression.getName());
                // Dereference single term of returned axiom
                QualifiedName termVarQName = new QualifiedName(listName.getName() + listName.incrementReferenceCount(), listName);
                expression = new TermVariable(termVarQName, itemList, indexExpression);
            }
            else
            {
                expression = listAssembler.newListVariableInstance(itemList, indexExpression);
                setName(expression.getName());
            }
        } 
        else
        {
            expression = newListVariableInstance(parserAssembler);
            setName(expression.getName());
        }
    }
}
