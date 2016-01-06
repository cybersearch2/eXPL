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

import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.compile.ParserResources;
import au.com.cybersearch2.classy_logic.expression.ExpressionException;
import au.com.cybersearch2.classy_logic.jpa.JpaEntityCollector;
import au.com.cybersearch2.classy_logic.jpa.JpaSource;
import au.com.cybersearch2.classy_logic.jpa.NameMap;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classy_logic.query.QueryExecutionException;
import au.com.cybersearch2.classydb.DatabaseAdminImpl;
import au.com.cybersearch2.classydb.NativeScriptDatabaseWork;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classyjpa.persist.PersistenceContext;
import au.com.cybersearch2.classyjpa.persist.PersistenceFactory;
import au.com.cybersearch2.classytask.WorkStatus;
import au.com.cybersearch2.classytask.WorkerRunnable;
import dagger.Component;

/**
 * PersistenceAgriculture demonstrates a custom JpaEntityCollector, AgriPercentCollector, used to
 * back a JpaSource axiom source. The database is populated using data in
 * agriculture-land.xpl script (AgriDatabase).
 * @author Andrew Bowley
 * 17 Mar 2015
 */
public class PersistenceAgriculture 
{
    @Singleton
    @Component(modules = AgriModule.class)  
    static interface ApplicationComponent extends ApplicationModule
    {
        void inject(AgriPercentCollector agriPercentCollector);
        void inject(JpaEntityCollector jpaEntityCollector);
        void inject(AgriAxiomProvider agriAxiomProvider);
        void inject(ParserAssembler.ExternalAxiomSource externalAxiomSource);
        void inject(ParserResources parserResources);
        void inject(WorkerRunnable<Boolean> workerRunnable);
        void inject(PersistenceContext persistenceContext);
        void inject(PersistenceFactory persistenceFactory);
        void inject(DatabaseAdminImpl databaseAdminImpl);
        void inject(NativeScriptDatabaseWork nativeScriptDatabaseWork);
    }

    /** Persistence Unit name to look up configuration details in persistence.xml */
    static public final String PU_NAME = "agriculture";

    /**
     * Construct PersistenceHighCities object. It initializes dependency injection
     * and creates a in-memory Cities database.
     * @throws InterruptedException
     */
	public PersistenceAgriculture() 
	{
        ApplicationComponent component = 
                DaggerPersistenceAgriculture_ApplicationComponent.builder()
                .agriModule(new AgriModule())
                .build();
        DI.getInstance(component);
        PersistenceWork setUpWork = new AgriDatabase();
        // Execute work and wait synchronously for completion
        PersistenceContainer container = new PersistenceContainer(PU_NAME);
        try
        {
            WorkStatus status = container.executeTask(setUpWork).waitForTask();
            if (status != WorkStatus.FINISHED)
                throw new PersistenceException("Task to set up database failed");
        }
        catch (InterruptedException e)
        {
            throw new PersistenceException("Error setting up database", e);
        }
	}

	/**
	 * Creates an axiom source which fetches  objects and converts
	 * them into axioms using entity names
	 * <p>Expected results:<br/> 
		</p>	 
	 */
    public Iterator<Axiom> testDataQuery()
    {
        JpaEntityCollector yearPercentCollector = new AgriPercentCollector(PU_NAME);
    	List<NameMap> termNameList = new ArrayList<NameMap>();
    	termNameList.add(new NameMap("country", "country"));
        for (int year = 1962; year < 2011; ++year)
            termNameList.add(new NameMap("y" + year, "Y" + year));
    	JpaSource jpaSource = new JpaSource(yearPercentCollector, "Data", termNameList); 
    	return jpaSource.iterator();
    }


	public static void main(String[] args)
	{
        try 
        {
    		PersistenceAgriculture ariculture = new PersistenceAgriculture();
    		Iterator<Axiom> axiomIterator = ariculture.testDataQuery();
            while (axiomIterator.hasNext())
                System.out.println(axiomIterator.next().toString());
            System.out.println("Done!");
        } 
        catch (ExpressionException e) 
        { 
            e.printStackTrace();
            System.exit(1);
        }
        catch (QueryExecutionException e) 
        {
            e.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
	}

}
