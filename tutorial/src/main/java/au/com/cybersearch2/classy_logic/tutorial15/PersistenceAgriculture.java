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

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.jpa.JpaSource;
import au.com.cybersearch2.classy_logic.jpa.NameMap;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;

/**
 * PersistenceHighCities demonstrates how to source axioms from a database using
 * Classy Tools JPA implementation. 
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
        container.executeTask(setUpWork).waitForTask();
	}

	/**
	 * Creates an axiom source which fetches  objects and converts
	 * them into axioms using entity names
	 * <p>Expected results:<br/> 
		</p>	 
	 */
    public void testDataQuery()
    {
    	List<NameMap> termNameList = new ArrayList<NameMap>();
     	AgriPercentCollector agriPercentCollector = new AgriPercentCollector(PU_NAME, new ProviderManager());
    	termNameList.clear();
    	termNameList.add(new NameMap("country", "country"));
		for (int year = 1962; year < 2012; ++year)
			termNameList.add(new NameMap("y" + year, "Y" + year));
    	JpaSource perCentSource = new JpaSource(agriPercentCollector, "Data", termNameList); 
    	Iterator<Axiom> axiomIterator = perCentSource.iterator();
    	while (axiomIterator.hasNext())
    		System.out.println(axiomIterator.next().toString());
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

}
