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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.compile.SourceItem;
import au.com.cybersearch2.classy_logic.debug.ExecutionContext;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.expression.Variable;
import au.com.cybersearch2.classy_logic.helper.EvaluationStatus;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.pattern.Template;

/**
 * DynamicList
 * List of items evaluated at runtime. Allows lists containing scope-dependent values and/or expressions.
 * The list always starts at an index value of zero and all items must be contiguous.
 * @author Andrew Bowley
 * 7Jun.,2017
 */
public class DynamicList<T> implements ItemList<T>
{
    static class DynamicListIterable<T> implements Iterable<T>
    {
        List<T> itemList;
        
        @SuppressWarnings("unchecked")
        public DynamicListIterable(List<Term> termList)
        {
            itemList = new ArrayList<T>(termList.size());
            for (Term term: termList)
                itemList.add((T) term.getValue());
        }

        @Override
        public Iterator<T> iterator()
        {
            return itemList.iterator();
        }
    }
    
    /** Parameter template evaluates item values */
    protected Template template;
    /** Qualified name */
    protected QualifiedName qname;
    /** Operand type */
    protected OperandType operandType;
    /** Source item to be updated in parser task */
    protected SourceItem sourceItem;
    
    /**
     * Construct a DynamicList object
     * @param operandType Operand type of list items 
     * @param qname Qualified name 
     * @param template Parameter template evaluates item values
     */
    public DynamicList(OperandType operandType, QualifiedName qname, Template template)
    {
        this.operandType = operandType;
        this.qname = qname;
        this.template = template;
    }

    /**
     * Evaluate Terms of this Template
     * @return EvaluationStatus
     */
     public EvaluationStatus evaluate(ExecutionContext executionContext)
     {
         return template.evaluate(executionContext);
     }
 
    /**
     * Backup from last unification.
     * @param partial Flag to indicate backup to before previous unification or backup to start
     * @return Flag to indicate if this Structure is ready to continue unification
     */
    public boolean backup(boolean partial)
    {
        return template.backup(partial);
    }
    
    @Override
    public Iterator<T> iterator()
    {
        return new DynamicListIterable<T>(template.toArray()).iterator();
    }

    @Override
    public void setSourceItem(SourceItem sourceItem)
    {
        this.sourceItem = sourceItem;
    }

    @Override
    public int getLength()
    {
        return template.getTermCount();
    }

    @Override
    public String getName()
    {
        return qname.getName();
    }

    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

    @Override
    public boolean isEmpty()
    {
        return template.getTermCount() == 0;
    }

    /**
     * assignItem -Can only overwrite existing value and append to end of list
     * @see au.com.cybersearch2.classy_logic.interfaces.ItemList#assignItem(int, java.lang.Object)
     */
    @Override
    public void assignItem(int index, T value)
    {
        if ((index < 0) || (index > getLength()))
            throw new ExpressionException("Index " + index + " invalid");
        if (index == getLength())
        {
            Variable var = new Variable(new QualifiedName(getName() + qname.incrementReferenceCount(), qname));
            var.setValue(value);
            template.addTerm(var);
            if (sourceItem != null)
                sourceItem.setInformation(toString());
        }
        else
            template.getTermByIndex(index).setValue(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getItem(int index)
    {
        if (hasItem(index))
            return (T) template.getTermByIndex(index).getValue();
        return null;
    }

    @Override
    public OperandType getOperandType()
    {
        return operandType;
    }

    @Override
    public boolean hasItem(int index)
    {
        return (index >= 0) && (index < getLength());
    }

    @Override
    public Iterable<T> getIterable()
    {
        return new DynamicListIterable<T>(template.toArray());
    }

    @Override
    public void clear()
    {
        if (sourceItem != null)
            sourceItem.setInformation(toString());
    }

    /**
     * toString
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() 
    {
        return "List <" + operandType.toString().toLowerCase() + ">[" + getLength() + "]";
    }

}
