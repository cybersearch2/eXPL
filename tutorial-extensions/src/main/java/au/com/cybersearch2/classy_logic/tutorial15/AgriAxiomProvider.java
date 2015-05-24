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

import javax.inject.Inject;
import javax.persistence.PersistenceException;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import au.com.cybersearch2.classy_logic.JpaProviderHelper;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.jpa.EntityAxiomProvider;
import au.com.cybersearch2.classy_logic.jpa.JpaEntityCollector;
import au.com.cybersearch2.classy_logic.jpa.JpaSource;
import au.com.cybersearch2.classy_logic.jpa.NameMap;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.terms.Parameter;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.EntityManagerLite;
import au.com.cybersearch2.classyjpa.entity.EntityManagerDelegate;
import au.com.cybersearch2.classyjpa.entity.PersistenceDao;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;

/**
 * AgriAxiomProvider
 * An example of an Axiom Provider for more than one Axiom source.
 * The "surface_area_increase" Axiom source has a simple entity collector,
 * but the "Data" axiom source has a custom collector which fetches data in batch mode
 * for scalability and assembles axiom terms from two tables.
 * 
 * @author Andrew Bowley
 * 18 Mar 2015
 */
public class AgriAxiomProvider extends EntityAxiomProvider 
{
	/**
	 * PersistAgri10Year
	 * Set up task to create Agri10Year table in test database. 
	 * This requires fetching Country objects to insert in Agri10Year items.
	 * See AgriDatabase for construction of test database.
	 * @author Andrew Bowley
	 * 23 May 2015
	 */
	class PersistAgri10Year implements PersistenceWork
	{
		protected Agri10Year agri10Year;

		/**
		 * Construct PersistAgri10Year object
		 * @param agri10Year The object to persist
		 */
		public PersistAgri10Year(Agri10Year agri10Year)
		{
			this.agri10Year =agri10Year;
		}

		/**
		 * Use OrmLite to run query to fetch Country object referenced by name in supplied Agri10Year object
		 * @see au.com.cybersearch2.classyjpa.entity.PersistenceWork#doInBackground(au.com.cybersearch2.classyjpa.EntityManagerLite)
		 */
        @Override
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
            // Now we can set the select arg (?) and run the query
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
	
    /** Persistence Unit name to look up configuration details in persistence.xml */
    static public final String PU_NAME = "agriculture";
    /** Axiom source name for percentage surface area over 50 year interval */
    static public final String PERCENT_AXIOM = "Data";
    /** Axiom source name for countries which increased agricultural surface area over 10 year interval */
    static public final String TEN_YEAR_AXIOM = "surface_area_increase";
    /** Identity value for next Agri10YearId object to be created */
    static int agri10YearId;
    
    
    @Inject
    JpaProviderHelper providerHelper;
    
	/**
	 * Construct AgriAxiomProvider object
	 */
	public AgriAxiomProvider() 
	{
	    // Super class will construct TEN_YEAR_AXIOM collector
		super(PU_NAME, new AgriDatabase());
		addEntity(TEN_YEAR_AXIOM, Agri10Year.class);
		addCollector(PERCENT_AXIOM, new AgriPercentCollector(PU_NAME));
		DI.inject(this);
	}

	/**
	 * Returns Axiom Provider identity
	 * @see au.com.cybersearch2.classy_logic.jpa.EntityAxiomProvider#getName()
	 */
	@Override
	public String getName() 
	{
		return "agriculture";
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.jpa.EntityAxiomProvider#getAxiomSource(java.lang.String, java.util.List)
	 */
	@Override
	public AxiomSource getAxiomSource(String axiomName,
			List<String> axiomTermNameList) 
	{
		List<NameMap> nameMapList = null;
		if (axiomTermNameList != null)
			nameMapList = new ArrayList<NameMap>();
		JpaEntityCollector collector = collectorMap.get(axiomName);
		if (PERCENT_AXIOM.equals(axiomName))
		{
			if (axiomTermNameList != null)
				for (String termName: axiomTermNameList)
					nameMapList.add(new NameMap(termName, termName));
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
		}
		else
			throw new IllegalArgumentException("Axiom name \"" + axiomName + "\" not valid for Axiom Provider \"" + getName() + "\"");
     	return new JpaSource(collector, axiomName, nameMapList);
	}

	/**
	 * 
	 * @see au.com.cybersearch2.classy_logic.jpa.EntityAxiomProvider#getAxiomListener()
	 */
	@Override
	public AxiomListener getAxiomListener() 
	{   
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
		    	// Do task of persisting Agri10Year asychronously. (Subject to using multi-connection ConnectionSource).
		    	providerHelper.doWorkAsync(PU_NAME, new PersistAgri10Year(agri10Year), false);
		    	// Change above line for next two to do task synchronously
				//if (providerManager.doWork(PU_NAME, new PersistAgri10Year(agri10Year)) != WorkStatus.FINISHED)
			    //	throw new QueryExecutionException("Error persisting resource " + getName() + " axiom: " + axiom.toString());
			}
		};
	}
	
}
