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
package au.com.cybersearch2.classy_logic.terms;

import java.math.BigDecimal;

import au.com.cybersearch2.classy_logic.compile.OperandType;
import au.com.cybersearch2.classy_logic.helper.Unknown;
import au.com.cybersearch2.classy_logic.interfaces.Literal;
import au.com.cybersearch2.classy_logic.interfaces.Operand;
import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * TermMetaData
 * Description of term useful for term containers
 * @author Andrew Bowley
 * 3May,2017
 */
public class TermMetaData implements Comparable<TermMetaData>
{
    protected LiteralType literalType;
    protected boolean isAnonymous;
    protected String name;
    protected int index;
    
    public TermMetaData(LiteralType literalType, String name, int index)
    {
        this.literalType = literalType;
        this.name = name;
        this.isAnonymous = name.isEmpty();
        this.index = index;
    }

    public TermMetaData(Term term)
    {
        this(term, -1);
    }

    public TermMetaData(Term term, int index)
    {
        if (term instanceof Literal)
            literalType = ((Literal)term).getLiteralType();
        else if (term instanceof Operand)
        {
            OperandType operandType = ((Operand)term).getOperator().getTrait().getOperandType();
            switch(operandType)
            {
            case INTEGER: literalType = LiteralType.integer; break;
            case BOOLEAN: literalType = LiteralType.xpl_boolean; break;
            case DOUBLE: literalType = LiteralType.xpl_double; break;
            case STRING: literalType = LiteralType.string; break;
            case CURRENCY:
            case DECIMAL: literalType = LiteralType.decimal; break;
            case UNKNOWN:
                if (!term.isEmpty() && (term.getValueClass() == Unknown.class))
                    literalType = LiteralType.unknown; break;
            default:
                literalType = LiteralType.unspecified; break;
            }
        }
        else if (!term.isEmpty())
        {
            if (term.getValueClass() == Long.class)
                literalType = LiteralType.integer;
            else if (term.getValueClass() == Boolean.class)
                literalType = LiteralType.xpl_boolean;
            else if (term.getValueClass() == Double.class)
                literalType = LiteralType.xpl_double;
            else if (term.getValueClass() == String.class)
                literalType = LiteralType.string;
            else if (term.getValueClass() == BigDecimal.class)
                literalType = LiteralType.decimal;
            else if (term.getValueClass() == Unknown.class)
                literalType = LiteralType.unknown;
            else
                literalType = LiteralType.unspecified;
        }
        else
            literalType = LiteralType.unspecified;
        this.name = term.getName();
        this.isAnonymous = name.isEmpty();
        this.index = index;
    }
 
     @Override
    public int compareTo(TermMetaData other)
    {
        if (literalType != other.literalType)
            return literalType.ordinal() - other.literalType.ordinal();
        if (isAnonymous)
            return other.isAnonymous ? index - other.index : 1; 
        return name.compareTo(other.name);
     }

    /**
     * @param index the index to set
     */
    public void setIndex(int index)
    {
        this.index = index;
    }

    /**
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

    public void setLiteralType(LiteralType literalType)
    {
        this.literalType = literalType;
    }
    
    /**
     * @return the literalType
     */
    public LiteralType getLiteralType()
    {
        return literalType;
    }

    /**
     * @return the isAnonymous
     */
    public boolean isAnonymous()
    {
        return isAnonymous;
    }

    public boolean setName(String name)
    {
        if (isAnonymous)
        {
            this.name = name;
            return true;
        }
        return false;
    }
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        if (isAnonymous)
            return literalType.toString() + " " + index;
        return name;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return literalType.hashCode() ^ name.hashCode() ^ index;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if ((obj == null) || !(obj instanceof TermMetaData))
            return false;
        TermMetaData other = (TermMetaData)obj; 
        return (literalType == other.literalType) && name.equals(other.name) && (index == other.index);
    }

}
