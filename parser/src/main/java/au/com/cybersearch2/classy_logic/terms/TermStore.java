/**
    Copyright (C) 2016  www.cybersearch2.com.au

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

import java.io.Serializable;

import au.com.cybersearch2.classy_logic.interfaces.Term;

/**
 * TermStore
 * Stores Term object state for serialization
 * @author Andrew Bowley
 * 7Jan.,2017
 */
public class TermStore implements Serializable
{
    private static final long serialVersionUID = -1512862620778240664L;
    
    private int id;
    private String name;
    private Object value;

    public TermStore(Term term)
    {
        id = term.getId();
        name = term.getName();
        value = term.getValue();
    }
    
    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public Object getValue()
    {
        return value;
    }
    public void setValue(Object value)
    {
        this.value = value;
    }
}
