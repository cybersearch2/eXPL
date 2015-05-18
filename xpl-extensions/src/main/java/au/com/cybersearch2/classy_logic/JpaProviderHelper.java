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
package au.com.cybersearch2.classy_logic;

import java.util.HashMap;
import java.util.Map;

import au.com.cybersearch2.classyjpa.EntityManagerLite;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classytask.Executable;
import au.com.cybersearch2.classytask.WorkStatus;

/**
 * ProviderManager
 * Contains Axiom Providers and performs their persistence work.
 * @author Andrew Bowley
 * 6 Mar 2015
 */
public class JpaProviderHelper
{
	/** Map Persistence Containers to Persistence Unit names */
	protected Map<String, PersistenceContainer> containerMap;
	/** Aggregate count of errors to track asynchronous work progress */
	protected int errorCount;
	
	/**
	 * Construct ProviderManager object
	 */
	public JpaProviderHelper() 
	{
	    super();
		containerMap = new HashMap<String, PersistenceContainer>();
	}

	/**
	 * Reset error count
	 */
	public void resetErrorCount()
	{
		errorCount = 0;
	}

	/**
	 * Returns aggregate error count
	 * @return int
	 */
	int getErrorCount()
	{
		return errorCount;
	}

	/**
	 * Perform persistence work synchronously
	 * @param persistenceUnit Persistence Unit name
	 * @param persistenceWork Work unit
	 * @return WorkStatus FINISHED or FAILED
	 */
	public WorkStatus doWork(String persistenceUnit, PersistenceWork persistenceWork)
	{
		return doWork(persistenceUnit, persistenceWork, false);
	}
	
	/**
	 * Perform persistence work synchronously
	 * @param persistenceUnit Persistence Unit name
	 * @param persistenceWork Work unit
	 * @param userTransactionMode Flag set true if user controls transactions
	 * @return WorkStatus FINISHED or FAILED
	 */
	public WorkStatus doWork(String persistenceUnit, PersistenceWork persistenceWork, boolean userTransactionMode)
	{
		WorkStatus workStatus = null;
        try 
        {
			workStatus = getContainer(persistenceUnit, userTransactionMode).
					executeTask(persistenceWork).
					waitForTask();
		} 
        catch (InterruptedException e) 
        {
        	workStatus = WorkStatus.FAILED;
		}
        return workStatus;
	}

	/**
	 * Perform persistence work asynchronously. Use error count to determine success.
	 * Note default JDBC datasource does not support asynchronous operation and falls
	 * back to synchronous.
	 * @param persistenceUnit Persistence Unit name
	 * @param persistenceWork Work unit
	 * @return WorkStatus Status at point of return. Expect PENDING or RUNNING. May be FINISHED or FAILED.
	 */
	public WorkStatus doWorkAsync(String persistenceUnit, final PersistenceWork persistenceWork, boolean userTransactionMode)
	{
		PersistenceContainer persistenceContainer = getContainer(persistenceUnit, userTransactionMode);
		PersistenceWork workAsync = new PersistenceWork(){

			@Override
			public void doInBackground(EntityManagerLite entityManager) 
			{
				persistenceWork.doInBackground(entityManager);
			}

			@Override
			public void onPostExecute(boolean success) 
			{
				if (!success)
					errorCount++;
				persistenceWork.onPostExecute(success);
			}

			@Override
			public void onRollback(Throwable rollbackException) 
			{
				errorCount++;
			   persistenceWork.onRollback(rollbackException);			
		    }
		};
		Executable exe = persistenceContainer.executeTask(workAsync);
		return exe.getStatus();
	}

	/**
	 * Returns PersistenceContainer dedicated to particular Persistence-Unit/User-Transaction-Mode combination. 
	 * @param persistenceUnit Persistence Unit name
	 * @param userTransactionMode Flag set true if user-controlled transactions
	 * @return PersistenceContainer object
	 */
	protected PersistenceContainer getContainer(String persistenceUnit, boolean userTransactionMode)
	{
		String key = persistenceUnit + "." + userTransactionMode;
		PersistenceContainer persistenceContainer = containerMap.get(key);
		if (persistenceContainer == null)
			synchronized(containerMap)
			{
				persistenceContainer = containerMap.get(key);
				if (persistenceContainer == null)
				{
					persistenceContainer = new PersistenceContainer(persistenceUnit);
					if (userTransactionMode)
						persistenceContainer.setUserTransactionMode(true);
					containerMap.put(persistenceUnit, persistenceContainer);
				}
			}
		return persistenceContainer;
	}
	
}
