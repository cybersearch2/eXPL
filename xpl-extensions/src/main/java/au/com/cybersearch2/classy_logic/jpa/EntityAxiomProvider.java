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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.cybersearch2.classy_logic.interfaces.AxiomListener;
import au.com.cybersearch2.classy_logic.interfaces.AxiomProvider;
import au.com.cybersearch2.classy_logic.interfaces.AxiomSource;
import au.com.cybersearch2.classy_logic.pattern.Axiom;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classyjpa.persist.PersistenceContext;

/**
 * EntityAxiomProvider
 * @author Andrew Bowley
 * 23 May 2015
 */
public class EntityAxiomProvider implements AxiomProvider
{
    /** The Collector to fetch all rows in the database entity table */
    protected JpaEntityCollector jpaEntityCollector;
    /** The AxiomProvider identity */
    protected String name;
    /** The optional task to set up the entity table. Intended for testing use only */
    protected PersistenceWork setUpTask;
    protected boolean databaseCreated;

    /**
     * EntityAxiomProvider
     * @param persistenceUnit Name of persistence unit defined in persistence.xml configuration file
     * @param entityClass Class of entity to be collected
     */
    public EntityAxiomProvider(String persistenceUnit, Class<?> entityClass)
    {
        this(persistenceUnit, entityClass, null);
    }

    /**
     * EntityAxiomProvider
     * @param persistenceUnit Name of persistence unit defined in persistence.xml configuration file
     * @param entityClass Class of entity to be collected
     * @param setUpTask PersistenceWork object to perform one-time initialization
     */
    public EntityAxiomProvider(String persistenceUnit, Class<?> entityClass, PersistenceWork setUpTask)
    {
        if (setUpTask == null)
            databaseCreated = true;
        else
            this.setUpTask = setUpTask;
        jpaEntityCollector = new JpaEntityCollector(persistenceUnit, entityClass);
        name = entityClass.getSimpleName();
    }

    /**
     * Returns Axiom Provider identity, which is the entity class name
     */
    @Override
    public String getName() 
    {
        return name;
    }

    /**
     * Initialize Axiom Provider
     * @param axiomName Axiom key (not used)
     * @param properties Optional properties to add to the persistence unit properties
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#setResourceProperties(java.lang.String, java.util.Map)
     */
    @Override
    public void setResourceProperties(String axiomName, Map<String, Object> properties) 
    {
        String puName = jpaEntityCollector.getPersistenceUnit();
        // If properties provide, add them to the persistence unit's properties
        if (properties != null)
            new PersistenceContext().getPersistenceAdmin(puName).getProperties().putAll(properties);
        if ((setUpTask != null) && !databaseCreated)
        {
            // Execute work and wait synchronously for completion
            PersistenceContainer container = new PersistenceContainer(puName);
            try 
            {
                container.executeTask(setUpTask).waitForTask();
                databaseCreated = true;
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @see au.com.cybersearch2.classy_logic.interfaces.AxiomProvider#getAxiomSource(java.lang.String, java.util.List)
     */
    @Override
    public AxiomSource getAxiomSource(String axiomName,
            List<String> axiomTermNameList) 
    {
        List<NameMap> nameMapList = new ArrayList<NameMap>();
        for (String termName: axiomTermNameList)
        {
            nameMapList.add(new NameMap(termName, termName));
        }
        return new JpaSource(jpaEntityCollector, axiomName, nameMapList);
    }

    @Override
    public boolean isEmpty() 
    {
        return !databaseCreated;
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
