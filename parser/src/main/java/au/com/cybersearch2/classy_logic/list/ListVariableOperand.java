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
import au.com.cybersearch2.classy_logic.compile.OperandMap;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.ExpressionOperand;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Operator;
import au.com.cybersearch2.classy_logic.operator.DelegateType;

/**
 * ListVariableOperand
 * Proxy for operand which accesses a single item of a list. The proxy allows 
 * variable construction to be delayed until the entire script is consumed and ensure dependendencies
 * have been compiled.
 * @author Andrew Bowley
 * 19Jan.,2017
 */
public abstract class ListVariableOperand extends ExpressionOperand<Object> implements Concaten<String>
{
    /** Name of list */
    protected String listName;
    /** Operand which evaluates the list index */
    protected Operand indexExpression = null;
    /** Optional Operand to select term in axiom - only applicable to Axiom lists */
    protected Operand expression2 = null;
    /** Defines operations that an Operand performs with other operands. */
    protected Operator assignOnlyOperator;
    
    /**
     * Construct ListVariableOperand object
     * @param listName Qualified name of list
     * @param listOperandFactory Factory to construct list variable
     * @param indexExpression Operand which evaluates the list index
     * @param expression2 Second expression for selection or assignment depending on usage
     */
    public ListVariableOperand(QualifiedName listName, 
                                Operand indexExpression, 
                                Operand expression2)
    {
        super(getQualifiedName(listName), null);
        this.listName = listName.getName();
        this.indexExpression = indexExpression;
        this.expression2 = expression2;
        assignOnlyOperator = DelegateType.ASSIGN_ONLY.getOperatorFactory().delegate();
    }

    /**
     * Returns Parameter name
     * @return String
     */
    @Override
    public String getName()
    {
        return expression == null ? qname.getName() : expression.getName();
    }

    /**
     * Set Parameter value
     * @param value
     */
    @Override
    public void setValue(Object value)
    {
        super.setValue(value);
        if (!empty)
            expression.assign(this);
    }
    
    /**
     * concatenate
     * @see au.com.cybersearch2.classy_logic.interfaces.Concaten#concatenate(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String concatenate(Operand rightOperand)
    {
        return ((Concaten<String>)expression).concatenate(rightOperand);
    }

    @Override
    public Operator getOperator()
    {
        return expression == null ? assignOnlyOperator : expression.getOperator();
    }

   /**
     * Returns Operand which accesses a list identified by name
     * @param listName Name of list
     * @param param1 Operand of first index
     * @param param2 Operand of second index or null if not specified
     * @return Operand from package au.com.cybersearch2.classy_logic.list
     */
    protected Operand newListVariableInstance(ParserAssembler parserAssembler)
    {
        QualifiedName qualifiedListName = null;
        ItemList<?> itemList = parserAssembler.getListAssembler().findItemList(listName);
        if (itemList != null)
            qualifiedListName = itemList.getQualifiedName(); 
        else 
        {
            Operand operand = parserAssembler.findOperandByName(listName);
            if (operand != null)
                 qualifiedListName = operand.getQualifiedName();
        }
        if (qualifiedListName == null)
            throw new ExpressionException("List \"" + listName + "\" cannot be found");
        return newListVariableInstance(parserAssembler, qualifiedListName);
    }

    /**
     * Returns Operand which accesses a list identified by qualified name
     * @param qualifiedListName Qualified name of list
     * @param param1 Operand of first index
     * @param param2 Operand of second index or null if not specified
     * @return Operand from package au.com.cybersearch2.classy_logic.list
     */
    protected Operand newListVariableInstance(ParserAssembler parserAssembler, QualifiedName qualifiedListName)
    {
        if ((expression2 != null) && (indexExpression instanceof StringOperand))
            throw new ExpressionException("List \"" + qualifiedListName.toString() + "\" cannot be indexed by name");
        ItemList<?> itemList = null;
        AxiomListSpec axiomListSpec = null;
        OperandMap operandMap = parserAssembler.getOperandMap();
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        // When an axiom parameter is specified, then initialization of the list variable must be delayed 
        // until evaluation occurs when running the first query.
        boolean isAxiomListVariable = parserAssembler.isParameter(qualifiedListName) || (operandMap.get(qualifiedListName) != null);
        if (isAxiomListVariable)
        {   // Return dynamic AxiomListVariable instance
            axiomListSpec = new AxiomListSpec(qualifiedListName, operandMap.get(qualifiedListName), indexExpression, expression2);
            return new AxiomListVariable(axiomListSpec);
        }
        // A normal list should be ready to go
        itemList = parserAssembler.getListAssembler().getItemList(qualifiedListName);
        //operandMap.addItemList(listName, itemList);
        if (expression2 == null) // Single index case is easy
            return listAssembler.newListVariableInstance(itemList, indexExpression);
        axiomListSpec = new AxiomListSpec((AxiomList)itemList, indexExpression, expression2);
        return listAssembler.newListVariableInstance(axiomListSpec);
    }
    
   /**
     * Returns generated qualified name based on list name
     * @param name List name
     * @return QualifiedName object
     */
    static protected QualifiedName getQualifiedName(QualifiedName listName)
    {
        return new QualifiedName(listName.getName() + "_var" + Integer.toString(listName.incrementReferenceCount()), listName);
    }
}
