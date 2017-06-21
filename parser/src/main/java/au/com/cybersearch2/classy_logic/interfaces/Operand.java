/**
    Copyright (C) 2014  www.cybersearch2.com.au

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
package au.com.cybersearch2.classy_logic.interfaces;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.terms.Parameter;

/**
 * Operand 
 * A Term which evaluates binary and unary expressions
 * @author Andrew Bowley
 * 1 Dec 2014
 */
public abstract class Operand extends Parameter
{
    /** Flag set true if operand not visible in solution */
    protected boolean isPrivate;
    /** Identity of container which registered the archetype index */
    protected int archetypeId;
    /** Index of this Operand in the registered archetype */
    protected int archetypeIndex;

    /**
     * @param name
      */
	public Operand(String name)
    {
        super(name);
        archetypeIndex = -1;
    }

    /**
     * @param name
     * @param value
     */
    public Operand(String name, int value)
    {
        super(name, value);
        archetypeIndex = -1;
    }

    /**
     * @param name
     * @param value
     */
    public Operand(String name, Object value)
    {
        super(name, value);
        archetypeIndex = -1;
    }

    /**
	 * Returns qualified name
	 * @return QualifiedName object
	 */
	public abstract QualifiedName getQualifiedName();
	
    /**
     * Assign a value to this Operand derived from a parameter 
     * @param parameter Parameter containing non-null value
     */
	public abstract void assign(Parameter parameter);

    /**
     * Returns left child of Operand
     * @return Operand object or null if there is no child
     */
	public abstract Operand getLeftOperand();
    
    /**
     * Returns right child of Operand
     * @return Operand object or null if there is no child
     */
	public abstract Operand getRightOperand();

	/**
	 * Returns object which defines operations that an Operand performs with other operands
	 * @return Operator object
	 */
	public abstract Operator getOperator();

    /**
     * Set this operand private - not visible in solution
     * @param isPrivate Flag set true if operand not visible in solution
     */
    public void setPrivate(boolean isPrivate)
    {
        this.isPrivate = isPrivate;
    }
    
    /**
     * Returns flag set true if this operand is private
     * @return boolean
     */
    public boolean isPrivate()
    {
        return isPrivate;
    }

    /**
     * setArchetypeId
     */
    public void setArchetypeId(int archetypeId)
    {
        this.archetypeId = archetypeId;
    }

     /**
     * getArchetypeId
     */
    public int getArchetypeId()
    {
        return archetypeId;
    }

     /**
     * Sets index of this Operand in the archetype of it's containing template
     * @param index int value
     */
    public void setArchetypeIndex(int archetypeIndex)
    {
        this.archetypeIndex = archetypeIndex;
    }

    /**
     * Returns index of this Operand in the archetype of it's containing template.
     * @return non-negative number, if set, otherwise -1
     */
    public int getArchetypeIndex()
    {
        return archetypeIndex;
    }
}
