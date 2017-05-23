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
package au.com.cybersearch2.classy_logic.compile;

import java.util.ArrayList;
import java.util.PriorityQueue;

import au.com.cybersearch2.classy_logic.Scope;
import au.com.cybersearch2.classy_logic.interfaces.ParserRunner;
import au.com.cybersearch2.classy_logic.list.AxiomList;
import au.com.cybersearch2.classy_logic.list.AxiomTermList;

/**
 * ParserTaskQueue
 * @author Andrew Bowley
 * 13May,2017
 */
public class ParserTaskQueue
{
    /** Tasks delayed until parsing complete */
    protected ArrayList<ParserTask> pendingList;

    public ParserTaskQueue()
    {
        pendingList = new ArrayList<ParserTask>();
    }
    
    /**
     * Add ParserTask to pending list
     * @param pending ParserTask object
     */
    public ParserTask addPending(Scope scope)
    {
        ParserTask parserTask = new ParserTask(scope.getName(), scope.getParserAssembler().getQualifiedContextname());
        pendingList.add(parserTask);
        return parserTask;
    }

    /**
     * Add Runnable to pending list
     * @param pending Runnable to execute parser task
     */
    public ParserTask addPending(ParserRunner pending, Scope scope)
    {
        ParserTask parserTask = addPending(scope);
        parserTask.setPending(pending);
        return parserTask;
    }

    /**
     * Collect pending parser tasks into priority queue
     */
    public void getPending(PriorityQueue<ParserTask> priorityQueue)
    {
        if (pendingList != null)
        {
            priorityQueue.addAll(pendingList);
            pendingList.clear();
        }
    }

    /**
     * Queue task to bind list to it's source which may not yet be declared
     * @param axiomTermList Axiom term list object
     */
    public void registerAxiomTermList(final AxiomTermList axiomTermList, Scope scope)
    {
        ParserTask parserTask = addPending(new ParserRunner(){
            @Override
            public void run(ParserAssembler parserAssember)
            {
                parserAssember.bindAxiomList(axiomTermList);
            }}, scope);
        // Boost priority so list is processed before any variables which reference it
        parserTask.setPriority(ParserTask.Priority.list.ordinal());
    }

    /**
     * Queue task to bind list to it's source which may not yet be declared
     * @param axiomList The axiom list
     */
    public void registerAxiomList(final AxiomList axiomList, Scope scope) 
    {
        ParserTask parserTask = addPending(new ParserRunner(){
            @Override
            public void run(ParserAssembler parserAssember)
            {
                parserAssember.bindAxiomList(axiomList);
            }}, scope);
        // Boost priority so list is processed before any variables which reference it
        parserTask.setPriority(ParserTask.Priority.list.ordinal());
    }
    
}
