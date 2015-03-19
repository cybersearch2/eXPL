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
package au.com.cybersearch2.classy_logic.jpa;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.Query;

import au.com.cybersearch2.classy_logic.interfaces.DataCollector;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classyjpa.EntityManagerLite;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classytask.Executable;

/**
 * JpaEntityCollector
 * 
 * Base class for executing JPA queries to deliver data for axiom providers.
 * Performs persistence work to execute named query identified by field "namedJpaQuery".
 * However, method doInBackground() can be overriden to do something different.
 * Supports paging and this is enabled by setting "maxResults" field to a positive number.
 * @author Andrew Bowley
 * 10 Feb 2015
 */
public abstract class JpaEntityCollector implements DataCollector, PersistenceWork 
{
	/** Named query to be performed if doInBackground() is not overriden */
    protected String namedJpaQuery;
    /** List of objects to be translated into an axiom source */
    protected Collection<Object> data;
    /** JPA container to execute named query */
    protected PersistenceContainer container;
    /** Maximum number of objects to return from a single query */
    protected int maxResults;
    /** The start position of the first result, numbered from 0 */
    protected int startPosition;
    /** flag set true if a call to getData() may deliver more results */
    protected boolean moreExpected;

    /**
     * Construct a JpaEntityCollector object
     * @param persistenceUnit Name of persistence unit defined in persistence.xml configuration file
     */
    public JpaEntityCollector(String persistenceUnit)
    {
        container = new PersistenceContainer(persistenceUnit);
    }
    
	/**
	 * Returns flag set true if a call to getData() may deliver more results.
	 * @return boolean
	 */
    @Override
	public boolean isMoreExpected() 
	{
		return moreExpected;
	}

    /**
     * Get data using JPA entity manager
     * @see au.com.cybersearch2.classyjpa.entity.PersistenceWork#doInBackground(au.com.cybersearch2.classyjpa.EntityManagerLite)
     */
	@SuppressWarnings("unchecked")
	@Override
	public void doInBackground(EntityManagerLite entityManager) 
	{
        Query query = entityManager.createNamedQuery(namedJpaQuery);
        if (maxResults > 0)
        {   // Paging enabled
        	query.setMaxResults(maxResults);
        	query.setFirstResult(startPosition);
        }
        data = (Collection<Object>) query.getResultList();
        if (maxResults > 0)
        {   // Advance start position or 
        	// clear "moreExpected" flag if no more results avaliable
        	if (data.size() > 0)
        	{
        		startPosition += data.size();
        		moreExpected = true;
        	}
        	else
        		moreExpected = false;
        }
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classyjpa.entity.PersistenceWork#onPostExecute(boolean)
	 */
	@Override
	public void onPostExecute(boolean success) 
	{
        if (!success)
            throw new IllegalStateException("Database set up failed. Check console for error details.");
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classyjpa.entity.PersistenceWork#onRollback(java.lang.Throwable)
	 */
	@Override
	public void onRollback(Throwable rollbackException) 
	{
        throw new IllegalStateException("Database set up failed. Check console for stack trace.", rollbackException);
	}

	/**
	 * Returns list of objects from persistence system. 
	 * Note calling thread may be blocked waiting for results
	 * @return Object collection
	 */
	@Override
	public Collection<Object> getData() 
	{
		try 
		{
			waitForTask(container.executeTask(this));
		} 
		catch (InterruptedException e) 
		{
			throw new QueryExecutionException("Work for query \"" + namedJpaQuery + "\" interrupted", e);
		}
		return data == null ? Collections.emptyList() : data;
	}

	/**
	 * Returns limit set on number of results a query will produce
	 * @return Limit number or 0 if no limit
	 */
	public int getMaxResults() 
	{
		return maxResults;
	}

	/**
	 * Set limit on number of results a query will produce
	 * @param maxResults int greater than 0
	 */
	public void setMaxResults(int maxResults) 
	{
		this.maxResults = maxResults;
	}

    /**
     * Wait sychronously for task completion
     * @param exe Executable object returned upon starting task
     * @throws InterruptedException Should not happen
     */
    protected void waitForTask(Executable exe) throws InterruptedException
    {
        synchronized (exe)
        {
            exe.wait();
        }
    }

}
