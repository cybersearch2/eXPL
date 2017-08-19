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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.QueryProgram;
import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.compile.ListAssembler;
import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.AxiomContainer;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemDelegate;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.LocaleListener;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.SourceInfo;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.operator.TermOperator;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.pattern.AxiomArchetype;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classy_logic.operator.DelegateType;

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
public class ListItemVariable extends Variable implements ParserRunner, SourceInfo, LocaleListener
{
    /** Index information for value selection  */
    protected ListItemSpec indexData;
    /** Index information for 2 dimension case - select axiom, then select term in axiom */
    protected ListItemSpec arrayData;
    /** Variable implementation for accessing AxiomLists and AxiomTermLists */
    protected ListItemDelegate delegate;
    /** Source item to be updated in parser task when more information available to form description of operand */
    protected SourceItem sourceItem;
    /** Maps list name for particular scope to list */
    protected Map<QualifiedName, ItemList<?>> contextMap;

    /**
     * Create ListItemVariable object which uses a single index to select values
     * @param qname Unique operand name
     * @param indexData  Index information for value selection
     */
    public ListItemVariable(QualifiedName qname, ListItemSpec indexData)
    {
        this(Term.ANONYMOUS, qname, indexData);
    }

    /**
     * Create ListItemVariable object which uses a single index to select values
     * @param name Term name
     * @param qname Unique operand name
     * @param indexData  Index information for value selection
     */
    public ListItemVariable(String name, QualifiedName qname, ListItemSpec indexData)
    {
        super(qname, name);
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
        this(qname, indexDataArray[indexDataArray.length - 1]);
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
    }

    /**
     * Create ListItemVariable object for 2 dimensional list access
     * @param qname Unique operand name
     * @param indexDataArray  Index information for value selection 
     */
    public ListItemVariable(String name, QualifiedName qname, ListItemSpec[] indexDataArray)
    {
        // For 2-dimension case, the first dimension selects an item in the list
        // and the second dimension selects a term within the item
        this(name, qname,  indexDataArray[indexDataArray.length - 1]);
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
    }

    /**
     * Returns operand type of list
     * @return OperandType - may be UNKNOWN if list not available
     */
    public OperandType getListOperandType()
    {
        ItemList<?> itemList =  delegate != null ? delegate.getItemList() : null;
        return itemList != null ? itemList.getOperandType() : OperandType.UNKNOWN;
    }
    
    /**
     * Run parser task after creation of all lists so they can be found by name
     * @see au.com.cybersearch2.classy_logic.interfaces.ParserRunner#run(au.com.cybersearch2.classy_logic.compile.ParserAssembler)
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        QualifiedName listName = indexData.getQualifiedListName();
        if (listName.getScope().equals("scope"))
        {   // Context list only supported in global scope
            String scopeName = parserAssembler.getScope().getName();
            if (scopeName.equals(QueryProgram.GLOBAL_SCOPE))
            {
                assembleContextVariable(listName.getName(), parserAssembler);
                return;
            }
            // Assign scope permanently when not in global scope
            listName = new QualifiedName(scopeName, QualifiedName.EMPTY, listName.getName());
        }
        String listScopeName = listName.getScope();
        Scope listScope = null;
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        if (!listScopeName.isEmpty() && !parserAssembler.getScope().getAlias().equals(listScopeName))
        {
            listScope = parserAssembler.getScope().findScope(listScopeName);
            if (listScope != null)
                listAssembler = listScope.getParserAssembler().getListAssembler();
        }
        // Check if list name is for cursor
        Operand operand = null;
        if (listScope == null)
            operand = parserAssembler.getOperandMap().getOperand(parserAssembler.getContextName(listName.getName()));
        Cursor cursor = null;
        if ((operand != null) && (operand instanceof Cursor))
        {
            cursor = (Cursor) operand;
            listName = cursor.getListName();
            if (arrayData == null)
                indexData.setCursor(cursor);
            else
                arrayData.setCursor(cursor);
        }
        ItemList<?> itemList = null;
        // Check if the list name mathches one for a query call - 
        // template name used in place of scope name
        if (!listName.getScope().isEmpty() && 
             (parserAssembler.getScope().findScope(listName.getScope()) == null))
        {   // Use the list name to search for an AxiomTermList
            AxiomTermList axiomTermList = listAssembler.getAxiomTerms(listName);
            itemList = axiomTermList;
            // Check for special case of a list with a single item
            if ((axiomTermList != null) && (axiomTermList.getAxiomTermNameList().size() == 1))
            {   // The value is evaluated from the list returned by the call 
                ListItemSpec[] indexDataArray = 
                    arrayData == null ?
                    new ListItemSpec[] { indexData } :
                    new ListItemSpec[] { arrayData, indexData };
                delegate = new AxiomVariable(assembleCallReturn(axiomTermList), indexDataArray);
                // Note itemList will remain null until evaluation occurs
            }
        }
        else
            itemList = findItemListByName(listName, listAssembler, parserAssembler);
        if (itemList == null)
        {   
            Operand listOperand = operand;
            if (operand == null)
            {
              // Search for an operand in the current scope with same name as the list name
              // If found, the operand will be an AxiomList operand, which creates a list upon evaluation.
              QualifiedName targetName = listAssembler.getAxiomListMapping(listName);
              listOperand = parserAssembler.getOperandMap().getOperand(targetName);
              if (listOperand == null)
                  listOperand = parserAssembler.findOperandByName(targetName.getName());
            }
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
            setDelegate(itemList);
        // Set term name of this variable now at last opportunity. Append the suffix of the index used to select the value.
        // The suffix is formed using available data, and may be a name required by an index operand to achieve unification
        if (name.isEmpty())
            setName(indexData.getSuffix());
        if (sourceItem != null)
            sourceItem.setInformation(toString());
    }

    /**
     * unifyTerm - 
     * The variable is not set by unification. However, if the index is an operand, then it is set for value selection by
     * assigning it as the left operand.
     * Note that in a 2 dimension case, when the first index is an operand, it must be set by a prior unify-evaluation step
     * The value of this variable is not set directly by unification.
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#unifyTerm(au.com.cybersearch2.classy_logic.interfaces.Term, int)
     */
    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        return 0; 
    }

    /**
     * Returns left child of Operand
     * @return Operand object or null if there is no child
     */
    public Operand getLeftOperand()
    {
        return delegate != null ? delegate.getOperand() : null;
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
        if (delegate != null)
            delegate.backup(id);
        return super.backup(id);
    }

    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
    @Override
    public void assign(Parameter parameter)
    {
        setValue(parameter);
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
        // Set term value - first
        setTermValue(value);
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
     * Handle notification of change of scope
     * @param scope The new scope which will assigned a particular locale
     */
    @Override
    public void onScopeChange(Scope scope)
    {
        super.onScopeChange(scope);
        if (contextMap != null)
        {
            QualifiedName listName = new QualifiedName(scope.getName(), indexData.getQualifiedListName().getName());
            ItemList<?> itemList = contextMap.get(listName);
            if (itemList != null)
            {
                setDelegate(itemList);
                // Set term name of this variable now at last opportunity. Append the suffix of the index used to select the value.
                // The suffix is formed using available data, and may be a name required by an index operand to achieve unification
                if (name.isEmpty())
                    setName(indexData.getSuffix());
                if (sourceItem != null)
                    sourceItem.setInformation(toString());
            }
        }
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
        setTermValue(delegate.getValue(newIndex));
    }

    /**
     * Append given value to list
     * @param value Object to append
     */
    protected void append(Object value)
    {
        delegate.append(value);
    }
    
    /**
     * Returns CallReturnVariable operand to unwrap single item of query call return list
     * @param axiomTermList The return list
     * @return TermVariable object
     */
    protected CallReturnVariable assembleCallReturn(AxiomTermList axiomTermList)
    {   // This is a list to return the result of a query call
            // Dereference single term of returned axiom
        QualifiedName listName = axiomTermList.getQualifiedName();
        QualifiedName termVarQName = new QualifiedName(listName.getName() + listName.incrementReferenceCount(), listName);
        return new CallReturnVariable(termVarQName, axiomTermList);
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
            setTermValue(itemValue);
        return itemValue;
    }

    /**
     * Set value with possible operator change for axiom term list assignment
     * @param value
     */
    protected void setTermValue(Object value)
    {
        // Preset operator if value type is AxiomTermList, or wrong operator will be set
        DelegateType delegateType = getDelegateType();
        // Change of operator for axiom term list is blocked if permanent delegate in place (ie. is cursor)
        if (!operator.isProxyAssigned() && (delegateType == DelegateType.ASSIGN_ONLY) && (value instanceof AxiomTermList))
            operator.setProxy(new TermOperator());
        super.setValue(value);
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
     * Set delegate to handle evaluate events
     * @param itemList Item list to be accessed by this variable
     */
    private void setDelegate(ItemList<?> itemList)
    {
        // Assemble index data to ensure if a name index is provided, it is valid.
        // This operation is repeated at evaluation in case the list is modified from now 
        // eg. local axiom, where term location may change with scope.
        indexData.assemble(itemList);
        if ((arrayData != null) && (delegate == null))
            // Two dimension case requires AxiomList helper
        {
            if (itemList instanceof AxiomList)
                delegate = new AxiomVariable((AxiomList)itemList, new ListItemSpec[] { arrayData, indexData });
            else
                delegate = new AxiomVariable((AxiomTermList)itemList, new ListItemSpec[] { arrayData, indexData });
        }
        if (delegate == null)
            delegate = itemVariableInstance(itemList);
    }
    
    /**
     * Assemble context lists
     * @param contextListName Name of list
     * @param parserAssembler ParserAssembler for current scope
     */
    private void assembleContextVariable(String contextListName, ParserAssembler parserAssembler)
    {
        Scope scope = parserAssembler.getScope();
        // Context lists are all declared in the global scope
        Scope globalScope = scope.getGlobalScope();
        ParserAssembler globalParserAssembler = globalScope.getParserAssembler();
        ListAssembler listAssembler = globalParserAssembler.getListAssembler();
        // Find all context lists and map by name in contextMap
        boolean globalListExists = false;
        for (String scopeName: globalScope.getScopeNames())
        {
            QualifiedName key = new QualifiedName(scopeName, contextListName);
            List<Axiom> axiomList = listAssembler.getAxiomItems(key);
            if (axiomList == null)
            {
                AxiomSource axiomSource = globalScope.getScope(scopeName).getParserAssembler().getAxiomSource(key);
                if (axiomSource == null)
                    axiomSource = globalParserAssembler.getAxiomSource(key);
                if (axiomSource == null)
                    // No context list in this scope
                    continue;
                axiomList = new ArrayList<Axiom>();
                Iterator<Axiom> iterator = axiomSource.iterator();
                while (iterator.hasNext())
                    axiomList.add(iterator.next());
            }
            ItemList<?> itemList = listAssembler.findItemList(key);
            if (itemList == null) 
                // Create axiom list to access context list
                itemList = createAxiomList(globalScope, key, axiomList);
            if (contextMap == null)
                contextMap = new HashMap<QualifiedName, ItemList<?>>();
            contextMap.put(key, itemList);
            if (key.getScope().isEmpty())
                globalListExists = true;
        }
        if (contextMap == null)
            throw new ExpressionException("Context list \"" + contextListName + "\"  is not found in any scope");
        // Register this object as a locale listener to handle change of scope
        parserAssembler.registerLocaleListener(this);
        // Default to global scope if global list defined
        if (globalListExists)
            onScopeChange(globalScope);
    }

    /**
     * Create axiom list to access context list
     * @param globalScope Global scope
     * @param key List name used as key to map item list
     * @param axiomList Context list
     * @return ItemList object
     */
    private ItemList<?> createAxiomList(Scope globalScope, QualifiedName key, List<Axiom> axiomList)
    {
        AxiomArchetype archetype = globalScope.getGlobalAxiomAssembler().getAxiomArchetype(key);
        AxiomContainer axiomContainer = (arrayData != null) ? new AxiomList(key, key) : new AxiomTermList(key, key);
        if (archetype != null)
            axiomContainer.setAxiomTermNameList(archetype.getTermNameList());
        globalScope.getGlobalListAssembler().setAxiomContainer(axiomContainer, axiomList);
        return (ItemList<?>) axiomContainer;
    }

    /**
     * Find item list by name
     * @param listName Name of list
     * @param parserAssembler ParserAssembler for current scope
     * @return ItemList object
     */
    private ItemList<?> findItemListByName(QualifiedName listName, ListAssembler listAssembler, ParserAssembler parserAssembler)
    {
        // Look up list by name from item lists
        ItemList<?> itemList = listAssembler.findItemList(listName); 
        String contextScopeName = parserAssembler.getQualifiedContextname().getScope();
        if ((itemList == null) && !contextScopeName.equals(parserAssembler.getScope().getName()))
        {   // Search for item list using context scope
            QualifiedName qualifiedListName = new QualifiedName(contextScopeName, indexData.getListName());
            itemList = listAssembler.findItemList(qualifiedListName);
        }
        if (itemList == null)
        {
            List<Axiom> axiomList = listAssembler.getAxiomItems(listName);
            if (axiomList != null)
                // Create axiom list to access context list
                itemList = createAxiomList(parserAssembler.getScope().getGlobalScope(), listName, axiomList);
        }
        return itemList;
    }

    @SuppressWarnings("unchecked")
    private ItemVariable<?> itemVariableInstance(ItemList<?> itemList)
    {
        switch (itemList.getOperandType())
        {
        case INTEGER:
            return new ItemVariable<Long>((ItemList<Long>)itemList, indexData);
        case DOUBLE:
            return new ItemVariable<Double>((ItemList<Double>)itemList, indexData);
        case BOOLEAN:
            return new ItemVariable<Boolean>((ItemList<Boolean>)itemList, indexData);
        case STRING:
            return new ItemVariable<String>((ItemList<String>)itemList, indexData);
        case DECIMAL:
        case CURRENCY:
            return new ItemVariable<BigDecimal>((ItemList<BigDecimal>)itemList, indexData);
        case TERM:
            return new ItemVariable<Term>((AxiomTermList)itemList, indexData);
        case AXIOM:
            return new ItemVariable<AxiomTermList>((AxiomList)itemList, indexData);
        default:
       }
        // Not expected
       throw new ExpressionException("List " + qname.toString() + " type " + itemList.getOperandType() + " not supported");
    }

}
