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
package au.com.cybersearch2.classy_logic.tutorial14;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.jpa.JpaSource;
import au.com.cybersearch2.classy_logic.jpa.NameMap;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;

/**
 * CitiesAxiomProvider
 * @author Andrew Bowley
 * 18 Mar 2015
 */
public class CitiesAxiomProvider implements AxiomProvider 
{
    /** Named query to find all cities */
    static public final String ALL_CITIES = "all_cities";
    /** Persistence Unit name to look up configuration details in persistence.xml */
    static public final String PU_NAME = "cities";

	/**
	 * 
	 */
	@Override
	public String getName() 
	{
		return "cities";
	}

	@Override
	public void setResourceProperties(String axiomName,
			Map<String, Object> properties) 
	{
        PersistenceWork setUpWork = new CitiesDatabase();
        // Execute work and wait synchronously for completion
        PersistenceContainer container = new PersistenceContainer(PU_NAME);
        try 
        {
			container.executeTask(setUpWork).waitForTask();
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
    	CityCollector cityCollector = new CityCollector(PU_NAME);
		List<NameMap> nameMapList = new ArrayList<NameMap>();
		for (String termName: axiomTermNameList)
		{
			nameMapList.add(new NameMap(termName, termName));
		}
    	return new JpaSource(cityCollector, axiomName, nameMapList);
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
	
}
