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

import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;

/**
 * ItemListOperand
 * @author Andrew Bowley
 * 22Jan.,2017
 */
public class ItemListOperand extends ListVariableOperand implements ParserRunner
{

    public ItemListOperand(String listName, Operand indexExpression,
            Operand expression2)
    {
        super(listName, indexExpression, expression2);
    }

    @Override
    public void run(ParserAssembler parserAssembler)
    {
        OperandMap operandMap = parserAssembler.getOperandMap();
        ItemList<?> itemList = parserAssembler.findItemList(listName);
        if (itemList == null)
        {
            QualifiedName qualifiedListName = QualifiedName.parseName(listName, operandMap.getQualifiedContextname());
            qualifiedListName.clearTemplate();
            itemList = parserAssembler.findItemList(qualifiedListName);
        }
        if (itemList != null)
            expression = setListVariable(operandMap, itemList);
        else
            expression = newListVariableInstance(parserAssembler);
    }

    /**
     * Returns new ItemListVariable instance. 
     * This is wrapped in an assignment evaluator if optional expression parameter needs to be evaluated.
     * @return Operand object
     */
    protected Operand setListVariable(OperandMap operandMap, ItemList<?> itemList) 
    {
        ItemListVariable<?> variable = operandMap.newListVariableInstance(itemList, indexExpression);
        if (expression2 != null)
        {
            if (expression2.isEmpty() || (expression2 instanceof Evaluator))
                // Expression needs evaluation
                return new Evaluator(itemList.getQualifiedName(), variable, "=", expression2);
            // Expression has a value which will be assigned to the list item
            variable.assign(expression2);
        }
        return variable;
    }


}
