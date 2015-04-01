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
package au.com.cybersearch2.classy_logic.tutorial15;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.jpa.JpaEntityCollector;
import au.com.cybersearch2.classy_logic.jpa.JpaSource;
import au.com.cybersearch2.classy_logic.jpa.NameMap;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.EntityManagerLite;
import au.com.cybersearch2.classyjpa.entity.EntityManagerDelegate;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceDao;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classytask.Executable;
import au.com.cybersearch2.classytask.WorkStatus;

/**
 * AgriAxiomProvider
 * @author Andrew Bowley
 * 18 Mar 2015
 */
public class AgriAxiomProvider implements AxiomProvider 
{
	
	class PersistAgri10Year implements PersistenceWork
	{
		protected Agri10Year agri10Year;
		
		public PersistAgri10Year(Agri10Year agri10Year)
		{
			this.agri10Year =agri10Year;
		}
		
	    public void doInBackground(EntityManagerLite entityManager)
	    {
	    	// Use OrmLite query to get Country object from database 
            EntityManagerDelegate delegate = (EntityManagerDelegate)entityManager.getDelegate();
            @SuppressWarnings("unchecked")
            PersistenceDao<Country, Integer> countryDao = 
                    (PersistenceDao<Country, Integer>) delegate.getDaoForClass(Country.class);
            QueryBuilder<Country, Integer> statementBuilder = countryDao.queryBuilder();
            SelectArg selectArg = new SelectArg();
            // build a query with the WHERE clause set to 'country = ?'
            PreparedQuery<Country> preparedQuery = null;
            try
            {
            	statementBuilder.where().eq("country", selectArg);
            	preparedQuery = statementBuilder.prepare();
            }
            catch (SQLException e)
            {
            	throw new PersistenceException("Database error", e);
            }
            // now we can set the select arg (?) and run the query
            selectArg.setValue(agri10Year.getCountryName());
            List<Country> results = countryDao.query(preparedQuery);
            if (results.size() > 0)
            {
	            Country country = results.get(0);
	            entityManager.merge(country);
	            agri10Year.setCountry(country);
		    	entityManager.persist(agri10Year);
            }
            else
            	System.err.println("Cannot find country \"" + agri10Year.getCountryName() + "\"");
	    }

	    @Override
	    public void onPostExecute(boolean success)
	    {
	        if (!success)
	            throw new IllegalStateException("Database error.");
	    }

	    @Override
	    public void onRollback(Throwable rollbackException)
	    {
	    	//throw new IllegalStateException("Database error.", rollbackException);
	    	System.err.println(rollbackException.toString());
	    }
    }
	
    /** Named query to find the percent change in agriculture land for all years */
    static public final String ALL_YEAR_PERCENTS = "all_year_percents";
    /** Persistence Unit name to look up configuration details in persistence.xml */
    static public final String PU_NAME = "agriculture";
    static public final String PERCENT_AXIOM = "Data";
    static public final String TEN_YEAR_AXIOM = "surface_area_increase";

    static int agri10YearId;
    
    protected boolean databaseCreated;
    
    @Inject
    ProviderManager providerManager;
    
	/**
	 * 
	 */
	public AgriAxiomProvider() 
	{
		DI.inject(this);
	}

	@Override
	public String getName() 
	{
		return "agriculture";
	}

	@Override
	public void setResourceProperties(String axiomName,
			Map<String, Object> properties) 
	{
		// Create Agriculture database, if not already done so.
		if (databaseCreated)
			return;
        PersistenceWork setUpWork = new AgriDatabase();
        // Execute work and wait synchronously for completion
        PersistenceContainer container = new PersistenceContainer(PU_NAME);
        try 
        {
			waitForTask(container.executeTask(setUpWork));
			databaseCreated = true;
		} 
        catch (InterruptedException e) 
        {
			e.printStackTrace();
		}
	}

	@Override
	public AxiomSource getAxiomSource(String axiomName,
			List<String> axiomTermNameList) 
	{
		List<NameMap> nameMapList = null;
		if (axiomTermNameList != null)
			nameMapList = new ArrayList<NameMap>();
		JpaEntityCollector collector = null;
		if (PERCENT_AXIOM.equals(axiomName))
		{
			if (axiomTermNameList != null)
				for (String termName: axiomTermNameList)
				{
					nameMapList.add(new NameMap(termName, termName));
				}
	    	collector = new AgriPercentCollector(PU_NAME, providerManager);
		}
		else if (TEN_YEAR_AXIOM.equals(axiomName))
		{
			if (axiomTermNameList != null)
			{
				for (String termName: axiomTermNameList)
				{
					NameMap nameMap = null;
					if (termName.equals("country"))
			    	    nameMap = new NameMap("country", "countryName");
					else if (termName.equals("surface_area"))
		    	        nameMap = new NameMap("surface_area", "surfaceArea");
					else
						nameMap = new NameMap(termName, termName);
					nameMapList.add(nameMap);
				}
			}
	    	collector = new Agri10YearCollector(PU_NAME, providerManager);
		}
		else
			throw new IllegalArgumentException("Axiom name \"" + axiomName + "\" not valid for Axiom Provider \"" + getName() + "\"");
     	return new JpaSource(collector, axiomName, nameMapList);
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
				if (!TEN_YEAR_AXIOM.equals(axiom.getName()))
					return;
				Agri10Year agri10Year = new Agri10Year();
				Parameter countryParam = (Parameter) axiom.getTermByName("country");
				if (countryParam == null)
					throw new ExpressionException("Axiom \"" + axiom.getName() + "\" does not have a term named \"country\"");
		    	agri10Year.setCountry(new Country(countryParam.getValue().toString()));
				//System.out.println(agri10Year.getCountryName() + " " + ++agri10YearCount);
		    	agri10Year.setId(agri10YearId++);
		    	agri10Year.setSurfaceArea((Double)axiom.getTermByName("surface_area").getValue());
		    	providerManager.doWorkAsync(PU_NAME, new PersistAgri10Year(agri10Year), false);
				//if (providerManager.doWork(PU_NAME, new PersistAgri10Year(agri10Year)) != WorkStatus.FINISHED)
			    //	throw new QueryExecutionException("Error persisting resource " + getName() + " axiom: " + axiom.toString());
			}
		};
	}
	
    /**
     * Wait sychronously for task completion
     * @param exe Executable object returned upon starting task
     * @throws InterruptedException Should not happen
     */
    protected WorkStatus waitForTask(Executable exe) throws InterruptedException
    {
    	WorkStatus status = exe.getStatus();
    	if ((status == WorkStatus.FINISHED) || (status == WorkStatus.FAILED))
    		return status;
       synchronized (exe)
        {
            exe.wait();
        }
        return exe.getStatus();
    }

}
