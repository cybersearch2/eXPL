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

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ItemList;
import au.com.cybersearch2.classy_logic.interfaces.ListItemSpec;
import au.com.cybersearch2.classy_logic.interfaces.Operand;

/**
 * ArrayIndex - Allows selection only by integer index values
 * @author Andrew Bowley
 * 23May,2017
 */
public class ArrayIndex implements ListItemSpec
{
    /** Name to identify item - deduced from operand components if not specified */
    protected String suffix;
    /** Selection value */
    protected int index;
    /** Optional offset */
    protected int offset;
    /** Index operand - literal if not empty, may be null */
    protected Operand indexExpression;
    /** Qualified name of list */
    protected QualifiedName qname;
    /** Cursor, if set, provides offset */
    protected Cursor cursor;

    /**
     * Construct ArrayIndex object using supplied index operand
     * @param qname Qualified name of list
     * @param indexExpression Index evaluation operand
     */
    public ArrayIndex(QualifiedName qname, Operand indexExpression)
    {
        this.qname = qname;
        this.indexExpression = indexExpression;
        offset = getOffset();
        OperandType operandType = indexExpression.getOperator().getTrait().getOperandType();
        if (!indexExpression.isEmpty())
        {   // Set index according to literal type, either integer or string
            if (operandType == OperandType.INTEGER)
                setIntIndex();
            else if (operandType == OperandType.STRING)
                // NOTE: Override setStringIndex() if string is a valid index type 
                suffix = indexExpression.getValue().toString();
            else
                throw new ExpressionException("List \"" + getListName() + "\" has invalid index \"" + indexExpression.getValue().toString() + "\"");
        }
        else
            index = -1;
        if (suffix == null)
        {   // Deduce suffix from first non-empty name navigating left operand branch
            suffix = indexExpression.getName();
            if (suffix.isEmpty())
            {
                Operand operand = indexExpression.getLeftOperand();
                while (operand != null)
                {
                    if (!operand.getName().isEmpty())
                    {
                        suffix = operand.getName();
                        break;
                    }
                    operand = operand.getLeftOperand();
                }
                if (suffix.isEmpty())
                    // This is not expected
                    suffix = indexExpression.toString();
            }
        }
    }
 
    /**
     * Construct ArrayIndex object using supplied index operand and suffix
     * @param qname Qualified name of list
     * @param indexExpression Index evaluation operand
     * @param suffix Name to identify item
     */
    public ArrayIndex(QualifiedName qname, Operand indexExpression, String suffix)
    {
        this.qname = qname;
        this.indexExpression = indexExpression;
        offset = getOffset();
        index = -1;
        this.suffix = suffix;
    }

    /**
     * Construct ArrayIndex object using supplied index and suffix
     * @param qname Qualified name of list
     * @param index Index value to select item
     * @param suffix Name to identify item
     */
    public ArrayIndex(QualifiedName qname, int index, String suffix)
    {
        this.qname = qname;
        this.index = index;
        this.suffix = suffix;
        offset = getOffset();
    }

    /**
     * @param cursor the cursor to set
     */
    public void setCursor(Cursor cursor)
    {
        this.cursor = cursor;
    }

    /**
     * setItemIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#setItemIndex(int)
     */
    @Override
    public void setItemIndex(int index)
    {
        this.index = index;
    }

    /**
     * assemble
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#assemble(au.com.cybersearch2.classy_logic.interfaces.ItemList)
     */
    @Override
    public void assemble(ItemList<?> itemList)
    {
    }
 
    /**
     * evaluate
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#evaluate(au.com.cybersearch2.classy_logic.interfaces.ItemList, int)
     */
    @Override
    public boolean evaluate(ItemList<?> itemList, int id)
    {
        offset = getOffset();
        if (indexExpression != null)
        {   // Evaluate index. The resulting value must be a sub class of Number to be usable as an index.
            indexExpression.evaluate(id);
            if (indexExpression.isEmpty())
                throw new ExpressionException("Index for list \"" + getListName() + "\" is empty" );
            index = -1;
            OperandType operandType = indexExpression.getOperator().getTrait().getOperandType();
            if (operandType == OperandType.CURSOR)
                operandType = ((Cursor)indexExpression).getListOperandType();
            if (operandType == OperandType.STRING)
                setStringIndex(itemList);
            if (operandType == OperandType.INTEGER)
                setIntIndex();
            if (index == -1)    
                throw new ExpressionException("List \"" + getListName() + "\" has invalid index \"" + indexExpression.getValue().toString() + "\"");
            return true;
        }
        return false;
    }

    /**
     * getListName
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getListName()
     */
    @Override
    public String getListName()
    {
        return qname.getName();
    }

    /**
     * getQualifiedListName
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getQualifiedListName()
     */
    @Override
    public QualifiedName getQualifiedListName()
    {
        return qname; 
    }

    /**
     * getItemIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getItemIndex()
     */
    @Override
    public int getItemIndex()
    {
        return index + offset;
    }

    /**
     * getItemExpression
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getItemExpression()
     */
    @Override
    public Operand getItemExpression()
    {
        return indexExpression;
    }

    /**
     * setSuffix
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#setSuffix(java.lang.String)
     */
    @Override
    public void setSuffix(String suffix)
    {
        this.suffix = suffix;
    }

    /**
     * getSuffix
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getSuffix()
     */
    @Override
    public String getSuffix()
    {
        return suffix;
    }

    /**
     * getVariableName
     * @see au.com.cybersearch2.classy_logic.interfaces.ListItemSpec#getVariableName()
     */
    @Override
    public QualifiedName getVariableName()
    {
        return new QualifiedName(getVariableName(qname.getName(), suffix) + qname.incrementReferenceCount(), qname);
    }
    
    /**
     * Returns variable name given list name and suffix
     * @param listName
     * @param suffix
     * @return String
     */
    protected String getVariableName(String listName, String suffix)
    {
        return listName + "_" + suffix;
    }

    /**
     * Set index from index expression, converting from long to int at same time
     */
    protected void setIntIndex()
    {
        index = ((Long)indexExpression.getValue()).intValue();
        if (indexExpression.getName().isEmpty())
            suffix = getListName() + "." + index;
        else 
            suffix = indexExpression.getName();
    }

    /**
     * Default setStringIndex implementation does nothing in this base class
     * @param itemList Target list
     */
    protected void setStringIndex(ItemList<?> itemList)
    {
        // Default index to 0 
        // Override if string indexes supported
        index = 0;
    }

    protected int getOffset()
    {
        return cursor == null ? 0 : cursor.getPosition();
    }
}
