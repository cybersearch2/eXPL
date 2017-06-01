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
    protected String suffix;
    protected int index;
    protected Operand indexExpression;
    protected QualifiedName qname;

    public ArrayIndex(QualifiedName qname, Operand indexExpression)
    {
        this.qname = qname;
        this.indexExpression = indexExpression;
        OperandType operandType = indexExpression.getOperator().getTrait().getOperandType();
        if (!indexExpression.isEmpty())
        {
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
        {
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
                }
                if (suffix.isEmpty())
                    // This is not expected
                    suffix = operand.toString();
            }
        }
    }
    
    public ArrayIndex(QualifiedName qname, Operand indexExpression, String suffix)
    {
        this.qname = qname;
        this.indexExpression = indexExpression;
        index = -1;
        this.suffix = suffix;
    }

    public ArrayIndex(QualifiedName qname, int index, String suffix)
    {
        this.qname = qname;
        this.index = index;
        this.suffix = suffix;
    }

    @Override
    public void assemble(ItemList<?> itemList)
    {
    }
    
    @Override
    public boolean evaluate(ItemList<?> itemList, int id)
    {
        if (indexExpression != null)
        {   // Evaluate index. The resulting value must be a sub class of Number to be usable as an index.
            indexExpression.evaluate(id);
            if (indexExpression.isEmpty())
                throw new ExpressionException("Index for list \"" + getListName() + "\" is empty" );
            index = -1;
            OperandType operandType = indexExpression.getOperator().getTrait().getOperandType();
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

    @Override
    public String getListName()
    {
        return qname.getName();
    }

    @Override
    public QualifiedName getQualifiedListName()
    {
        return qname; 
    }

    @Override
    public int getItemIndex()
    {
        return index;
    }

    @Override
    public Operand getItemExpression()
    {
        return indexExpression;
    }

    @Override
    public String getSuffix()
    {
        return suffix;
    }
    
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

    protected void setIntIndex()
    {
        index = ((Long)indexExpression.getValue()).intValue();
        if (indexExpression.getName().isEmpty())
            suffix = getListName() + "." + index;
        else 
            suffix = indexExpression.getName();
    }

    protected void setStringIndex(ItemList<?> itemList)
    {
        // Default index to 0 
        // Override if string indexes supported
        index = 0;
    }
}
