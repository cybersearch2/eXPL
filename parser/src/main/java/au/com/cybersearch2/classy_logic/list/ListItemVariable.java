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

import au.com.cybersearch2.classy_logic.compile.ListAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.RightOperand;
import au.com.cybersearch2.classy_logic.interfaces.SourceInfo;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * ListItemVariable
 * A Variable operand which accesses a list. 
 * Allows new items to be inserted in array lists.
 * Allows AxiomTermList terms to be referenced by name.
 * The list is identified by name and binding to the list occurs
 * either within a parser task, after all lists have been created,
 * or later, at evaluation in the case of dynamically generated lists.  
 * @author Andrew Bowley
 * 28May,2017
 */
public class ListItemVariable extends Variable implements RightOperand,  ParserRunner, SourceInfo
{
    /** Index information for value selection  */
    protected ListItemSpec indexData;
    /** Index information for 2 dimension case - select axiom, then select term in axiom */
    protected ListItemSpec arrayData;
    /** Variable implementation for accessing AxiomLists and AxiomTermLists */
    protected ListItemDelegate delegate;
    /** Optional operand for specialization eg. Currency */
    protected Operand rightOperand;
    /** Source item to be updated in parser task when more information available to form description of operand */
    protected SourceItem sourceItem;

    /**
     * Create ListItemVariable object which uses a single index to select values
     * @param qname Unique operand name
     * @param indexData  Index information for value selection
     */
    public ListItemVariable(QualifiedName qname, ListItemSpec indexData)
    {
        super(qname, Term.ANONYMOUS);
        this.indexData = indexData;
    }

    /**
     * Create ListItemVariable object for 2 dimensional list access
     * @param qname Unique operand name
     * @param indexDataArray  Index information for value selection 
     */
    public ListItemVariable(QualifiedName qname, ListItemSpec[] indexDataArray)
    {
        // For 2-dimension case, the first dimension selects an item in the list
        // and the second dimension selects a term within the item
        this(qname, indexDataArray.length == 1 ? indexDataArray[0] : indexDataArray[1]);
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
    }

    /**
     * Run parser task after creation of all lists so they can be found by name
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        QualifiedName listName = indexData.getQualifiedListName();
        ItemList<?> itemList = null;
        // Check if the list name mathches one for a calculator query - 
        // template name used in place of scope name
        if (!listName.getScope().isEmpty() && 
             (parserAssembler.getScope().findScope(listName.getScope()) == null))
        {   // Use the list name to search for an AxiomTermList
            AxiomTermList axiomTermList = listAssembler.getAxiomTerms(listName);
            itemList = axiomTermList;
            // Check for special case of a list with a single item
            if ((axiomTermList != null) && (axiomTermList.getAxiomTermNameList().size() == 1))
            {   // Unwrap axiom term list to select single term result
                // The value is evaluated as an expression, bypassing the item list altogether
                expression = assembleQueryTerm(axiomTermList);
                setName(indexData.getSuffix());
                if (sourceItem != null)
                    sourceItem.setInformation(expression.toString());
                setNonDelegate(listName);
                return;
            }
        }
        else
        {   // Look up list by name from item lists
            itemList = listAssembler.findItemList(listName); 
            String contexScopeName = parserAssembler.getQualifiedContextname().getScope();
            if ((itemList == null) && !contexScopeName.equals(parserAssembler.getScope().getName()))
            {   // Search for item list using context scope
                QualifiedName qualifiedListName = new QualifiedName(contexScopeName, indexData.getListName());
                itemList = listAssembler.findItemList(qualifiedListName);
            }
        }
        if (itemList == null)
        {   // Search for an operand in the current scope with same name as the list name
            // If found, the operand will be an AxiomList operand, which creates a list upon evaluation.
            Operand listOperand = parserAssembler.findOperandByName(indexData.getListName());
            if (listOperand != null) // TODO - || parserAssembler.isParameter(listName))
            {   // Use a helper to evaluate the list operand and resolve list parameters
                ListItemSpec[] indexDataArray = 
                    arrayData == null ?
                    new ListItemSpec[] { indexData } :
                    new ListItemSpec[] { arrayData, indexData };
                delegate = new AxiomVariable(listOperand, indexDataArray);
                // Note itemList will remain null until evaluation occurs
            }
            else // If all else fails, look for the list using global scope version of name
                itemList = findGlobalItemList(listName, parserAssembler);
        }
        if ((itemList == null) && (delegate == null))
            throw new ExpressionException("List \"" + indexData.getListName() + "\" cannot be found");
        if (itemList != null)
            // Assemble index data to ensure if a name index is provided, it is valid.
            // This operation is repeated at evaluation in case the list is modified from now 
            // eg. local axiom, where term location may change with scope.
            indexData.assemble(itemList);
        if ((arrayData != null) && (delegate == null))
            // Two dimension case requires AxiomList helper
            delegate = new AxiomVariable((AxiomList)itemList, new ListItemSpec[] { arrayData, indexData });
        if (delegate == null)
            delegate = new ItemVariable(itemList, indexData);
        // Set term name of this variable now at last opportunity. Append the suffix of the index used to select the value.
        // The suffix is formed using available data, and may be a name required by an index operand to achieve unification
        setName(indexData.getSuffix());
        if (sourceItem != null)
            sourceItem.setInformation(toString());
    }

    /**
     * unifyTerm - 
     * If it exists, unify index operand for value selection. 
     * Note that in a 2 dimension case, when the first index is an operand, it must be set by a prior unify-evaluation step
     * The value of this variable is not set directly by unification.
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        return delegate.unifyTerm(otherTerm, id);
    }

    /**
     * Evaluate to complete list binding, if using a list operand, and resolve list parameters
     * @param id Identity of caller, which must be provided for backup()
     * @return Evaluation status of COMPLETE
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        if (rightOperand != null)
            // Specialization
            rightOperand.evaluate(id);
        // If TermVariable set as expression, it will evaluate and set the value 
        super.evaluate(id);
        if (!empty)
            return EvaluationStatus.COMPLETE;
        int newIndex = delegate.evaluate(id);
        if (empty && (newIndex != -1))
        {   // Update current index and update value too if index in range
            this.id = id;
            setValue(newIndex);
        }
        return EvaluationStatus.COMPLETE;
    }

    /**
     * backup
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int)
     */
    @Override
    public boolean backup(int id) 
    {  
        // Backup everything that may be touched by unification or evaluation
        // Note itemList and index fields are not changed, so getValue() will still return the same value as before.
        // However, this variable will be in a "empty" state, so getValue() should not be invoked.
        if (rightOperand != null)
            rightOperand.backup(id);
        delegate.backup(id);
        return super.backup(id);
    }

    /**
     * Assign a value and id to this Term from another term 
     * @param term Term containing non-null value and id to set
     */
    @Override
    public void assign(Term term) 
    {
        setValue(term);
        id = term.getId();
    }

    /**
     * Set value
     * @param value
     */
    @Override
    public void setValue(Object value)
    {
        // Value is wrapped in a Term if coming from assign()
        Term term = (value instanceof Term) ? (Term)value : null;
        if (term != null)
            value = term.getValue();
        // Check if new value same as old value
        if (!empty && this.value.equals(value))
            return;
        // Set term value - easy
        super.setValue(value);
        delegate.setItemValue(value);
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

    /**
     * Returns value from item list and also updates term value if out of sync from 
     * having more than one variable accessing the same item.
     * @return value as object
     */
    @Override
    public Object getValue()
    {
        if (!(delegate instanceof ItemVariable))
            return value;
        // Refresh from list item in case it has changed from last update
        return getItemValue();
    }

    /**
     * getRightOperand
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getRightOperand()
     */
    @Override
    public Operand getRightOperand() 
    {
        return rightOperand;
    }

    /**
     * setRightOperand
     * @see au.com.cybersearch2.classy_logic.interfaces.RightOperand#setRightOperand(au.com.cybersearch2.classy_logic.interfaces.Operand)
     */
    @Override
    public void setRightOperand(Operand rightOperand)
    {
        this.rightOperand = rightOperand;
    }
    
    /**
     * toString
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
     */
    @Override
    public String toString()
    {
        if (!empty)
            // Item value available to report   
            return delegate.getValue().toString();
        else if (name.isEmpty())
        {   // Use index data to build 2 or 3 part name to report empty state
            StringBuilder builder = new StringBuilder(indexData.getListName());
            if (arrayData != null)
                builder.append('.').append(arrayData.getSuffix());
            builder.append('.').append(indexData.getSuffix());
            return builder.toString();
        }
        // Use default name plus assigned value if expression field not null
        return super.toString();
    }

    /**
     * Set the term value to list item selected by index
     * @param index Selection value - must be valid
     */
    protected void setValue(int newIndex)
    {   
        super.setValue(delegate.getValue(newIndex));
    }

    /**
     * Returns TermVariable operand to unwrap single item of calculator query return list
     * @param axiomTermList The return list
     * @return TermVariable object
     */
    protected TermVariable assembleQueryTerm(AxiomTermList axiomTermList)
    {   // This is a list to return the result of a query call
        indexData.assemble(axiomTermList);
        {   // Set operand name to name of single term indicated by index expression name
            Operand indexExpression = indexData.getItemExpression();
            setName(indexExpression.getName());
            // Dereference single term of returned axiom
            QualifiedName listName = axiomTermList.getQualifiedName();
            QualifiedName termVarQName = new QualifiedName(listName.getName() + listName.incrementReferenceCount(), listName);
            return new TermVariable(termVarQName, axiomTermList, indexExpression);
        }
    } 

    /**
     * Returns current item value
     * @return Object
     */
    protected Object getItemValue()
    {
        Object oldValue = super.getValue();
        Object itemValue = delegate.getValue();
        if (itemValue instanceof Term)
            itemValue = ((Term)itemValue).getValue();
        if (!itemValue.equals(oldValue))
            super.setValue(itemValue);
        return itemValue;
    }
 
    /**
     * Searches for and returns item list using global scope version of name
     * @param listName Name of list with non-global scope part
     * @param parserAssembler Parser assembler in context scope
     * @return ItemList object or null if list not found
     */
    protected ItemList<?> findGlobalItemList(QualifiedName listName, ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        QualifiedName qualifiedListName = listName;
        if (!listName.getScope().isEmpty())
             qualifiedListName.clearScope();
        return listAssembler.findItemList(qualifiedListName);
    }

    /**
     * Create empty delegate when expression evaluates the value
     */
    private void setNonDelegate(final QualifiedName qname)
    {
        delegate = new ListItemDelegate(){

            @Override
            public ItemList<?> getItemList()
            {
                ArrayItemList<Object> itemList = new ArrayItemList<Object>(Object.class, qname);
                itemList.assignItem(0, value);
                return itemList;
            }

            @Override
            public int unifyTerm(Term otherTerm, int id)
            {
                return 0;
            }

            @Override
            public int evaluate(int id)
            {
                return 0;
            }

            @Override
            public boolean backup(int id)
            {
                return false;
            }

            @Override
            public void setItemValue(Object value)
            {
                
                ListItemVariable.this.value = value;
                empty = false;
            }

            @Override
            public Object getValue()
            {
                return value;
            }

            @Override
            public Object getValue(int selection)
            {
                return value;
            }};
    }


}
