/**
    Copyright (C) 2015  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.pattern;

import au.com.cybersearch2.classy_logic.interfaces.Term;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * OperandContext
 * Leaf component of tree to preserve Term states
 * @author Andrew Bowley
 * 17 Aug 2015
 */
public class OperandContext
{
    protected int id;
    protected Object value;
    protected OperandContext left;
    protected OperandContext right;
    protected Term term;
    protected OperandContext next;

    /**
     * Create OperandContext object
     * @param term Term to preserve
     */
    public OperandContext(Term term)
    {
        this.term = term;
        id = term.getId();
        if (!term.isEmpty())
            value = term.getValue();
    }

    /**
     * Returns Term id
     * @return int
     */
    public int getId()
    {
        return id;
    }

    /**
     * Returns Term value
     * @return Object or null if term is empty
     */
    public Object getValue()
    {
        return value;
    }

    /**
     * Returns left term OperandContext
     * @return OperandContext object or null
     */
    public OperandContext getLeft()
    {
        return left;
    }

    /**
     * Returns right term OperandContext
     * @return OperandContext object or null
     */
    public OperandContext getRight()
    {
        return right;
    }

    /**
     * Returns next term OperandContext
     * @return OperandContext object or null
     */
    public OperandContext getNext()
    {
        return next;
    }

    /**
     * Sets left term OperandContext
     * @param left Left OperandContext
     */
    public void setLeft(OperandContext left)
    {
        this.left = left;
    }

    /**
     * Sets right term OperandContext
     * @param right Right OperandContext
     */
    public void setRight(OperandContext right)
    {
        this.right = right;
    }

    /**
     * Sets next term OperandContext
     * @param next OperandContext
     */
    public void setNext(OperandContext next)
    {
        this.next = next;
    }

    /**
     * Restore term to original state. 
     * Propagates to tree and siblings with this OperandContext as root
     */
    public void restore()
    {
        if (value == null)
        {   // Term was empty
            if (!term.isEmpty())
                term.clearValue();
        }
        else
        {   // Term had value
            Parameter param = new Parameter(term.getName(), value);
            param.setId(id);
            term.assign(param);
        }
        if (left != null)
            left.restore();
        if (right != null)
            right.restore();
        if (next != null)
            next.restore();
    }
}
