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
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.expression.Evaluator;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.SourceInfo;

/**
 * ItemListOperand
 * @author Andrew Bowley
 * 22Jan.,2017
 */
public class ItemListOperand extends ListVariableOperand implements ParserRunner, SourceInfo
{
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;

    /**
     * Construct an ItemListOperand object
     * @param listName List qualified name
     * @param indexExpression Operand which evaluates the list index
     * @param expression2 Second expression for assignment operation or null
     */
    public ItemListOperand(QualifiedName listName, Operand indexExpression,
            Operand expression2)
    {
        super(listName, indexExpression, expression2);
    }

    /**
     * Run parser task
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        ItemList<?> itemList = listAssembler.findItemList(listName);
        if (itemList == null)
        {
            QualifiedName qualifiedListName = QualifiedName.parseName(listName, parserAssembler.getQualifiedContextname());
            qualifiedListName.clearTemplate();
            itemList = listAssembler.findItemList(qualifiedListName);
        }
        if (itemList != null)
            expression = setListVariable(listAssembler, itemList);
        else
            expression = newListVariableInstance(parserAssembler);
        setName(expression.getName());
        if (sourceItem != null)
            sourceItem.setInformation(toString());
    }

    /**
     * Returns new ItemListVariable instance. 
     * This is wrapped in an assignment evaluator if optional expression parameter needs to be evaluated.
     * @return Operand object
     */
    protected Operand setListVariable(ListAssembler listAssembler, ItemList<?> itemList) 
    {
        ItemListVariable<?> variable = listAssembler.newListVariableInstance(itemList, indexExpression);
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

    /**
     * setSourceItem
     * @see au.com.cybersearch2.classy_logic.interfaces.SourceInfo#setSourceItem(au.com.cybersearch2.classy_logic.compile.SourceItem)
     */
    @Override
    public void setSourceItem(SourceItem sourceItem)
    {
        this.sourceItem = sourceItem;
    }


}
