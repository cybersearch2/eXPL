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

import au.com.cybersearch2.classy_logic.ProviderManager;
import au.com.cybersearch2.classy_logic.jpa.JpaEntityCollector;
import au.com.cybersearch2.classy_logic.jpa.QueryForAllGenerator;
import au.com.cybersearch2.classyjpa.persist.PersistenceAdmin;
import au.com.cybersearch2.classyjpa.persist.PersistenceContext;

/**
 * Agri10YearCollector extends JpaEntityCollector to create an external axiom source
 * for Agri10Year axioms translated from Agri10Year JPA entity objects. The data is
 * obtained from "all_agri_10_year" named query.
 * @author Andrew Bowley
 * 10 Feb 2015
 */
public class Agri10YearCollector extends JpaEntityCollector 
{
    /** Named query to find all cities */
    static public final String ALL_AGRI_10_YEAR = "all_agri_10_year";

    /** Factory object to create "cities" Persistence Unit implementation */
    protected PersistenceContext persistenceContext;

    /**
     * Construct a Agri10YearCollector object
     * @param persistenceUnit
     */
	public Agri10YearCollector(String persistenceUnit, ProviderManager providerManager) 
	{
		super(persistenceUnit, providerManager);
		// JpaEntityCollector needs the name of the query to fetch all cities 
		this.namedJpaQuery = ALL_AGRI_10_YEAR;
        persistenceContext = new PersistenceContext();
		setUp(persistenceUnit);
	}

	/**
	 * Set up the named query which uses utility class QueryForAllGenerator
	 * @param persistenceUnit
	 */
	protected void setUp(String persistenceUnit)
	{
        // Get Interface for JPA Support, required to create named queries
        PersistenceAdmin persistenceAdmin = persistenceContext.getPersistenceAdmin(persistenceUnit);
        QueryForAllGenerator allEntitiesQuery = 
                new QueryForAllGenerator(persistenceAdmin);
        persistenceAdmin.addNamedQuery(Agri10Year.class, ALL_AGRI_10_YEAR, allEntitiesQuery);
	}

}
