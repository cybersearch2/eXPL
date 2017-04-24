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
import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.ExpressionOperand;
import au.com.cybersearch2.classy_logic.expression.OperatorEnum;
import au.com.cybersearch2.classy_logic.expression.StringOperand;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Concaten;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.interfaces.Trait;
import au.com.cybersearch2.classy_logic.trait.DefaultTrait;

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
    static Trait LIST_TRAIT;
    
    static
    {
        // The de-referenced list value may be either an axiom or a term
        LIST_TRAIT = new DefaultTrait(OperandType.UNKNOWN);
    }
    
    /** Name of list */
    protected String listName;
    /** Operand which evaluates the list index */
    protected Operand indexExpression = null;
    /** Optional Operand to select term in axiom - only applicable to Axiom lists */
    protected Operand expression2 = null;
    
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
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperandOps()
     */
    @Override
    public OperatorEnum[] getRightOperandOps() 
    {
        return expression.getRightOperandOps();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getLeftOperandOps()
     */
    @Override
    public OperatorEnum[] getLeftOperandOps() 
    {
        return expression.getLeftOperandOps();
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(OperatorEnum operatorEnum2, Term rightTerm) 
    {
        return expression.numberEvaluation(operatorEnum2, rightTerm);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#numberEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Number numberEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return expression.numberEvaluation(leftTerm, operatorEnum2, rightTerm);
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#booleanEvaluation(au.com.cybersearch2.classy_logic.interfaces.Term, au.com.cybersearch2.classy_logic.expression.OperatorEnum, au.com.cybersearch2.classy_logic.interfaces.Term)
     */
    @Override
    public Boolean booleanEvaluation(Term leftTerm, OperatorEnum operatorEnum2,
            Term rightTerm) 
    {
        return expression.booleanEvaluation(leftTerm, operatorEnum2, rightTerm);
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
    public void setTrait(Trait trait)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Trait getTrait()
    {
        return LIST_TRAIT;
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
        ItemList<?> itemList = parserAssembler.findItemList(listName);
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
        // When an axiom parameter is specified, then initialization of the list variable must be delayed 
        // until evaluation occurs when running the first query.
        boolean isAxiomListVariable = parserAssembler.isParameter(qualifiedListName) || (operandMap.get(qualifiedListName) != null);
        if (isAxiomListVariable)
        {   // Return dynamic AxiomListVariable instance
            axiomListSpec = new AxiomListSpec(qualifiedListName, operandMap.get(qualifiedListName), indexExpression, expression2);
            return new AxiomListVariable(axiomListSpec);
        }
        // A normal list should be ready to go
        itemList = parserAssembler.getItemList(qualifiedListName);
        //operandMap.addItemList(listName, itemList);
        if (expression2 == null) // Single index case is easy
            return operandMap.newListVariableInstance(itemList, indexExpression);
        axiomListSpec = new AxiomListSpec((AxiomList)itemList, indexExpression, expression2);
        return operandMap.newListVariableInstance(axiomListSpec);
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
