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

import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.LexiconSource;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.SingleAxiomSource;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyjpa.EntityManagerLite;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classytask.Executable;

/**
 * TestAxiomProvider
 * @author Andrew Bowley
 * 11 Feb 2015
 */
public class TestAxiomProvider extends ProviderManager implements AxiomProvider
{
	static private String localeCountry;
	
	static
	{
		localeCountry = "";
	}

	@Override
	public void setResourceProperties(String axiomName,
			Map<String, Object> properties) 
	{
	}

	@Override
	public AxiomSource getAxiomSource(String axiomName,
			List<String> axiomTermNameList) 
	{
		AxiomSource axiomSource = null;
		if ("lexicon".equals(axiomName))
			axiomSource = new LexiconSource(axiomName, axiomTermNameList);
		if ("locale".equals(axiomName))
		{
			Axiom localeAxiom = new Axiom("locale", new Parameter("locale", localeCountry));
			axiomSource = new SingleAxiomSource(localeAxiom);
		}
		return axiomSource;
	}

	@Override
	public AxiomProvider getAxiomProvider(String name)
	{
		return this;
	}

	@Override
	public boolean isEmpty() 
	{
		return false;
	}

	@Override
	public AxiomListener getAxiomListener() 
	{   // Do-nothing listener for read-only provider
		return new AxiomListener()
		{
			@Override
			public void onNextAxiom(Axiom axiom) 
			{
			}
		};
	}

	public static void setlocaleCountry(String countryCode)
	{
		localeCountry = countryCode;
	}
	
    protected PersistenceWork setUpWork(final List<Object> data) 
    {
        return new PersistenceWork()
        {
	        @Override
	        public void doInBackground(EntityManagerLite entityManager)
	        {
	        	for (Object object: data)
	            	entityManager.persist(object);
	        }
	
	        @Override
	        public void onPostExecute(boolean success)
	        {
	            if (!success)
	                throw new IllegalStateException("Database set up failed. Check console for error details.");
	        }
	
	        @Override
	        public void onRollback(Throwable rollbackException)
	        {
	            throw new IllegalStateException("Database set up failed. Check console for stack trace.", rollbackException);
	        }
        };
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

	@Override
	public String getName() 
	{
		return "TestProvider";
	}


}
