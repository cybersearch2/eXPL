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
package au.com.cybersearch2.classy_logic.compile;

import au.com.cybersearch2.classy_logic.helper.QualifiedName;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;

/**
 * ParserTask
 * Executes code to complete compilation. Self ordering by priority.
 * @author Andrew Bowley
 * 20Jan.,2017
 */
public class ParserTask implements ParserRunner, Comparable<ParserTask>
{
    public enum Priority
    {
        fix,
        variable,
        list
    }
    
    /** Pending task */
    protected ParserRunner pending;
    /** Qualified name of enclosing scope/template context */
    protected QualifiedName qualifiedContextname;
    /** Name of scope owning task */
    protected String scopeName;
    /** Priority - lowest is 0 */
    protected int priority;
 
    /**
     * Construct ParserTask object
     * @param scopeName Name of scope owning task
     * @param qualifiedContextname Qualified name of enclosing scope/template context
     */
    public ParserTask(String scopeName, QualifiedName qualifiedContextname)
    {
        this.scopeName = scopeName;
        this.qualifiedContextname = qualifiedContextname;
    }

    /**
     * Returns scope name
     * @return String
     */
    public String getScopeName()
    {
        return scopeName;
    }

    /**
     * Set task to run
     * @param pending Runnable object
     */
    public void setPending(ParserRunner pending)
    {
        this.pending = pending;
    }

    /**
     * Set priority - 0 is lowest and default
     * @param priority
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Returns qualified name of enclosing scope/template context
     * @return QualifiedName object
     */
    public QualifiedName getQualifiedContextname()
    {
        return qualifiedContextname;
    }
 
    /**
     * Execute task
     */
    @Override
    public void run(ParserAssembler parserAssembler)
    {
        if (pending != null)
            pending.run(parserAssembler);
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ParserTask other)
    {
        return other.priority - priority;
    }
}
