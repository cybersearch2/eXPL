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
import au.com.cybersearch2.classy_logic.interfaces.Term;
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
    protected QualifiedName listName;
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
    protected ListVariableOperand(QualifiedName qname,
                                   QualifiedName listName, 
                                   Operand indexExpression, 
                                   Operand expression2)
    {   // Sub class to assign term name when parser task runs
        super(qname, null, Term.ANONYMOUS);
        this.listName = listName;
        this.indexExpression = indexExpression;
        this.expression2 = expression2;
        assignOnlyOperator = DelegateType.ASSIGN_ONLY.getOperatorFactory().delegate();
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
     * Bypass unification as evaluation is unaffected by this operand's value and
     * any term pairing is unintended
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        return id;
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

    /**
     * getOperator
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getOperator()
     */
    @Override
    public Operator getOperator()
    {
        return expression == null ? assignOnlyOperator : expression.getOperator();
    }

   /**
     * @see au.com.cybersearch2.classy_logic.expression.ExpressionOperand#toString()
     */
    @Override
    public String toString()
    {
        if (empty)
        {
            if (expression != null)
                return listName + "=" + expression.toString();
            return name.isEmpty() ? qname.getName() : name;
        }
        String valueText = ( value == null ? "null" : value.toString());
        return listName + "=" + valueText;
    }

    /**
     * Returns Operand to accesses a list identified by listName field
     * @param parserAssembler Parser assembler belonging to list scope
     * @return list variable from package au.com.cybersearch2.classy_logic.list
     */
    protected Operand newListVariableInstance(ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        QualifiedName qualifiedListName = null;
        ItemList<?> itemList = listAssembler.findItemList(listName);
        if (itemList != null)
        {
            qualifiedListName = itemList.getQualifiedName(); 
            listName = qualifiedListName;
        }
        else 
        {
            Operand operand = parserAssembler.findOperandByName(listName.getName());
            if (operand != null)
                 qualifiedListName = operand.getQualifiedName();
        }
        if (qualifiedListName == null)
        {   // Search for list in global scope, if not already in global scope
            if (!listName.getScope().isEmpty())
            {
                qualifiedListName = listName;
                qualifiedListName.clearScope();
                itemList = listAssembler.findItemList(qualifiedListName);
            }
            if (itemList == null)
                throw new ExpressionException("List \"" + listName + "\" cannot be found");
            listName = qualifiedListName;
        }
        return newListVariableInstance(parserAssembler, qualifiedListName);
    }

    /**
     * Returns Operand which accesses a list identified by qualified name
     * @param parserAssembler Parser assembler belonging to list scope
     * @param qualifiedListName Qualified name of list
     * @return list variable from package au.com.cybersearch2.classy_logic.list
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
        if (expression2 == null) // Single index case is easy
            return listAssembler.newListVariableInstance(itemList, indexExpression);
        axiomListSpec = new AxiomListSpec((AxiomList)itemList, indexExpression, expression2);
        return listAssembler.newListVariableInstance(axiomListSpec);
    }
    
}
