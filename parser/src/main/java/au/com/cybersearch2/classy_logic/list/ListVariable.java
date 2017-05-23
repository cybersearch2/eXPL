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

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.terms.GenericParameter;

/**
 * ListVariable
 * @author Andrew Bowley
 * 23May,2017
 */
public abstract class ListVariable<T> extends GenericParameter<T> implements Operand
{
    /** Qualified name of operand */
    protected QualifiedName qname;
    /** Flag set true if operand not visible in solution */
    protected boolean isPrivate;
    /** Index of this Operand in the archetype of it's containing template */
    private int archetypeIndex;

    protected ListVariable(QualifiedName qname, String name)
    {
        super(name);
        this.qname = qname;
        archetypeIndex = -1;
    }

    abstract protected T getItemValue();
    
    /**
     * Returns qualified name of this operamd
     * @return QualifiedName object
     */
    @Override
    public QualifiedName getQualifiedName()
    {
        return qname;
    }

    /**
     * Set this operand private - not visible in solution
     * @param isPrivate Flag set true if operand not visible in solution
     */
    @Override
    public void setPrivate(boolean isPrivate)
    {
        this.isPrivate = isPrivate;
    }
    
    /**
     * Returns flag set true if this operand is private
     * @return
     */
    @Override
    public boolean isPrivate()
    {
        return isPrivate;
    }
    
    /**
     * setIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#setIndex(int)
     */
    @Override
    public void setArchetypeIndex(int archetypeIndex)
    {
        this.archetypeIndex = archetypeIndex;
    }

    /**
     * getIndex
     * @see au.com.cybersearch2.classy_logic.interfaces.Operand#getIndex()
     */
    @Override
    public int getArchetypeIndex()
    {
        return archetypeIndex;
    }
    
}
