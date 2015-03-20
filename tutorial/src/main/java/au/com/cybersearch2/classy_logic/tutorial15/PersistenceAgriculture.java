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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.cybersearch2.classy_logic.jpa.JpaSource;
import au.com.cybersearch2.classy_logic.jpa.NameMap;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classytask.Executable;

/**
 * PersistenceHighCities demonstrates how to source axioms from a database using
 * Classy Tools JPA implementation. Two test cases contrast default names taken
 * from the entity class with names defined by the client.
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class PersistenceAgriculture 
{
    /** Named query to find the percent change in agriculture land for all years */
    static public final String ALL_YEAR_PERCENTS = "all_year_percents";
    /** Persistence Unit name to look up configuration details in persistence.xml */
    static public final String PU_NAME = "agriculture";

    /**
     * Construct PersistenceHighCities object. It initializes dependency injection
     * and creates a in-memory Cities database.
     * @throws InterruptedException
     */
	public PersistenceAgriculture() throws InterruptedException 
	{
		new DI(new AgriModule()).validate();
        PersistenceWork setUpWork = new AgriDatabase();
        // Execute work and wait synchronously for completion
        PersistenceContainer container = new PersistenceContainer(PU_NAME);
        waitForTask(container.executeTask(setUpWork));
	}

	/**
	 * Creates an axiom source which fetches  objects and converts
	 * them into axioms using entity names
	 * <p>Expected results:<br/> 
		</p>	 
	 */
    public void testDataQuery()
    {
    	Agri10YearCollector Agri10YearCollector = new Agri10YearCollector(PU_NAME);
    	List<NameMap> termNameList = new ArrayList<NameMap>();
    	termNameList.add(new NameMap("country", "countryName"));
    	termNameList.add(new NameMap("surface_area", "surfaceArea"));
    	JpaSource jpaSource = new JpaSource(Agri10YearCollector, "surface_area_increase", termNameList); 
    	Iterator<Axiom> axiomIterator = jpaSource.iterator();
    	while (axiomIterator.hasNext())
    		System.out.println(axiomIterator.next().toString());
    	/*
    	AgriPercentCollector agriPercentCollector = new AgriPercentCollector(PU_NAME);
    	List<String> termNameList = new ArrayList<String>();
    	termNameList.add("country");
		for (int year = 1962; year < 2012; ++year)
			termNameList.add("Y" + year);
    	JpaSource jpaSource = new JpaSource(agriPercentCollector, "Data", termNameList); 
    	Iterator<Axiom> axiomIterator = jpaSource.iterator();
    	while (axiomIterator.hasNext())
    		System.out.println(axiomIterator.next().toString());
    		*/
    	System.out.println("Done!");
    }


	public static void main(String[] args)
	{
		try 
		{
			PersistenceAgriculture ariculture = new PersistenceAgriculture();
			ariculture.testDataQuery();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
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
