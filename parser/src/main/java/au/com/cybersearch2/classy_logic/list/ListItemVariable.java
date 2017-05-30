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
import au.com.cybersearch2.classy_logic.helper.Null;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.interfaces.RightOperand;
import au.com.cybersearch2.classy_logic.interfaces.SourceInfo;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * ListItemVariable
 * @author Andrew Bowley
 * 28May,2017
 */
public class ListItemVariable extends Variable implements RightOperand,  ParserRunner, SourceInfo
{
    /** The list */
    protected ItemList<?> itemList;
    protected ListItemSpec indexData;
    protected ListItemSpec arrayData;
    protected AxiomListSpec axiomListSpec;
    protected int index;
    /** Optional operand for Currency type */
    protected Operand rightOperand;
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;

    public ListItemVariable(QualifiedName qname, ListItemSpec indexData)
    {
        super(qname, Term.ANONYMOUS);
        this.indexData = indexData;
        index = -1;
    }

    public ListItemVariable(QualifiedName qname, ListItemSpec[] indexDataArray)
    {
        this(qname, indexDataArray.length == 1 ? indexDataArray[0] : indexDataArray[1]);
        if (indexDataArray.length > 1)
            arrayData = indexDataArray[0];
    }
    
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        QualifiedName listName = indexData.getQualifiedListName();
        itemList = listAssembler.findItemList(listName); 
        if ((itemList == null) && 
             !listName.getScope().isEmpty() && 
             (parserAssembler.getScope().findScope(listName.getScope()) == null))
        {
            AxiomTermList axiomTermList = listAssembler.getAxiomTerms(listName);
            itemList = axiomTermList;
            if (axiomTermList.getAxiomTermNameList().size() == 1)
            {
                expression = assembleQueryTerm(axiomTermList);
                setName(indexData.getSuffix());
                if (sourceItem != null)
                    sourceItem.setInformation(expression.toString());
                return;
            }
        }
        if (itemList == null)
        {
            QualifiedName qualifiedListName = QualifiedName.parseName(indexData.getListName(), parserAssembler.getQualifiedContextname());
            qualifiedListName.clearTemplate();
            itemList = listAssembler.findItemList(qualifiedListName);
        }
        if (itemList == null)
        {
            Operand listOperand = parserAssembler.findOperandByName(indexData.getListName());
            if (listOperand != null) // TODO - || parserAssembler.isParameter(listName))
            {
                ListItemSpec[] indexDataArray = 
                    arrayData == null ?
                    new ListItemSpec[] { indexData } :
                    new ListItemSpec[] { arrayData, indexData };
                axiomListSpec = new AxiomListSpec(listOperand, indexDataArray); 
            }
            else
                itemList = findGlobalItemList(listName, parserAssembler);
        }
        if ((itemList == null) && (axiomListSpec == null))
            throw new ExpressionException("List \"" + indexData.getListName() + "\" cannot be found");
        if (itemList != null)
            indexData.assemble(itemList);
        if ((arrayData != null) && (axiomListSpec == null))// Item list is AxiomList
            axiomListSpec = new AxiomListSpec((AxiomList)itemList, new ListItemSpec[] { arrayData, indexData });
        setName(indexData.getSuffix());
        if (sourceItem != null)
            sourceItem.setInformation(toString());
    }

    /**
     * Evaluate index if expression provided
     * @param id Identity of caller, which must be provided for backup()
     * @return Flag set true if evaluation is to continue
     */
    @Override
    public EvaluationStatus evaluate(int id)
    {
        if (rightOperand != null)
            rightOperand.evaluate(id);
        super.evaluate(id);
        if (!empty)
            return EvaluationStatus.COMPLETE;
        int newIndex = -1;
        if (axiomListSpec != null)
        {
            Operand axiomListVariable = axiomListSpec.getAxiomListVariable();
            if (axiomListVariable != null) 
            {
                if (axiomListVariable.isEmpty())
                    axiomListVariable.evaluate(id);
                if (axiomListVariable.isEmpty())
                    throw new ExpressionException("List \"" + axiomListSpec.getListName() + "\" evaluation failed");
                itemList = (ItemList<?>) axiomListVariable.getValue();
                axiomListSpec.evaluate(itemList, id);
            }
            else
                axiomListSpec.evaluate(itemList, id);
            newIndex = axiomListSpec.getAxiomIndex();
            if (newIndex == -1)
                newIndex = axiomListSpec.getItemIndex();
        }
        else
        {
            // Note: indexData.assemble() is always invoked because scope local axioms may change when scope changes
            indexData.assemble(itemList);
            indexData.evaluate(itemList, id);
            newIndex = indexData.getItemIndex();
        }
        if (empty && (newIndex != -1))
        {
            this.id = id;
            onIndexSet(newIndex);
        }
        return EvaluationStatus.COMPLETE;
    }

    /**
     * Skip unification, only evaluate
     */
    @Override
    public int unifyTerm(Term otherTerm, int id)
    {
        boolean isAxiomList = axiomListSpec != null;
        Operand indexExpression = 
            isAxiomList ? 
            axiomListSpec.getItemExpression() :
            indexData.getItemExpression();
        if (indexExpression != null)
        {
            indexExpression.unifyTerm(otherTerm, id);
            if (!indexExpression.isEmpty() && !isAxiomList)
            {
                indexData.evaluate(itemList, id);
                if (itemList.hasItem(indexData.getItemIndex()))
                {
                    index = indexData.getItemIndex();
                    onIndexSet(index);
                    this.id = id;
                    return id;
                }
            }
        }
        return 0;
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#backup(int)
     */
    @Override
    public boolean backup(int id) 
    {   // Do not backup list item as unification still works regardless of whether it is empty or not.
        // Setting the list item to null, which is the only backup option, also risks NPE.
        // Backup index expression too as it always evaluates index
        if (rightOperand != null)
            rightOperand.backup(id);
        boolean isAxiomList = axiomListSpec != null;
        Operand indexExpression = 
            isAxiomList ? 
            axiomListSpec.getAxiomExpression() :
            indexData.getItemExpression();
        if (indexExpression != null)
            indexExpression.backup(id);
        if (arrayData != null)
        {
            indexExpression = axiomListSpec.getItemExpression();
            if (indexExpression != null)
                indexExpression.backup(id);
        }
        if (isAxiomList && (axiomListSpec.getAxiomListVariable() != null))  
            axiomListSpec.getAxiomListVariable().backup(id);
        return super.backup(id);
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

    @Override
    public void setRightOperand(Operand rightOperand)
    {
        this.rightOperand = rightOperand;
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
     * Set Parameter value
     * @param value
     */
    @Override
    public void setValue(Object value)
    {
        Term term = (value instanceof Term) ? (Term)value : null;
        if (term != null)
            value = term.getValue();
        if (!empty && this.value.equals(value))
            return;
        super.setValue(value);
        if (!empty && (itemList != null) && (index != -1))
        {
            Object itemValue = itemList.hasItem(index) ? itemList.getItem(index) : null;
            boolean proceed = (itemValue == null);
            boolean isAxiomTermList = 
                ((index != -1) && (itemList instanceof AxiomTermList)) || 
                (axiomListSpec != null) && (axiomListSpec.getItemIndex() != -1);
            if (!isAxiomTermList)
            {
                if (!proceed)
                {
                    if (itemValue instanceof Term)
                        itemValue = ((Term)itemValue).getValue();
                    proceed = !itemValue.equals(value);
                }
                if (proceed)
                    itemList.assignItem(indexData.getItemIndex(), value);
            }
            else
            {
                AxiomTermList axiomTermList = null;
                if (itemList instanceof AxiomTermList)
                {
                    axiomTermList = (AxiomTermList)itemList;
                    term = axiomTermList.getItem(index);
                }
                else
                {
                    axiomTermList = axiomListSpec.getAxiomTermList();
                    int index = axiomListSpec.getItemIndex();
                    term = axiomTermList.getItem(index);
                }
                term.setValue(value);
            }
        }
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
     * Handle index evaluation event. Set value to list item selected by index.
     * @param index int
     */
    protected void onIndexSet(int newIndex)
    {
        setValue(newIndex);
    }

    /**
     * Set the value to list item selected by index
     * @param index int
     */
    protected void setValue(int newIndex)
    {   
        index = newIndex;
        boolean isAxiomTermList = (axiomListSpec != null) && (axiomListSpec.getItemIndex() != -1);
        if (isAxiomTermList)
        {
            AxiomTermList axiomTermList = axiomListSpec.getAxiomTermList();
            Term term = axiomTermList.getItem(axiomListSpec.getItemIndex());
            super.setValue(term.getValue());
        }
        else
        {
            // Index should be valid, but check for safety
            if (!itemList.hasItem(newIndex))
                return;
            Object item = itemList.getItem(index);
                 if (item instanceof Term)
                    super.setValue(((Term)item).getValue());
                else
                    super.setValue(item);
        }
    }

    /**
     * Returns value
     * @return value as object
     */
    @Override
    public Object getValue()
    {
        boolean isAxiomList = (axiomListSpec != null) && (axiomListSpec.getItemIndex() != -1);
        if (isAxiomList || ((index == -1) && !isEmpty()))
            return value;
        if ((index == -1) || !itemList.hasItem(index))
            return new Null(); // index is not valid, so cannot reference list item
        // Refresh from list item in case it has changed from last update
        return getItemValue();
    }

    /**
     * @see au.com.cybersearch2.classy_logic.terms.Parameter#toString()
     */
    @Override
    public String toString()
    {
        if ((index != -1) && (itemList != null) && !itemList.isEmpty())
        {   
            return getItemValue().toString();
        }
        else if ((index == -1) && !empty)
            return value.toString();
        else if (name.isEmpty())
        {
            StringBuilder builder = new StringBuilder(indexData.getListName());
            if (arrayData != null)
                builder.append('.').append(arrayData.getSuffix());
            builder.append('.').append(indexData.getSuffix());
            return builder.toString();
        }
        return super.toString();
    }

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
    
    protected Object getItemValue()
    {
        Object oldValue = super.getValue();
        Object itemValue = itemList.getItem(index);
        boolean isAxiomList = (axiomListSpec != null) && (axiomListSpec.getItemIndex() != -1);
        if (!isAxiomList)
        {
            if (itemValue instanceof Term)
                itemValue = ((Term)itemValue).getValue();
            if (!itemValue.equals(oldValue))
                super.setValue(itemValue);
        }
        else
        {
            AxiomTermList axiomTermList = axiomListSpec.getAxiomTermList();
            Term term = axiomTermList.getItem(axiomListSpec.getItemIndex());
            itemValue = term.getValue();
        }
        return itemValue;
    }
    
    protected ItemList<?> findGlobalItemList(QualifiedName listName, ParserAssembler parserAssembler)
    {
        ListAssembler listAssembler = parserAssembler.getListAssembler();
        QualifiedName qualifiedListName = listName;
        if (!listName.getScope().isEmpty())
             qualifiedListName.clearScope();
        return listAssembler.findItemList(qualifiedListName);
    }
}
