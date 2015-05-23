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
package au.com.cybersearch2.telegen;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import dagger.Module;
import dagger.Provides;
import au.com.cybersearch2.classyapp.JavaTestResourceEnvironment;
import au.com.cybersearch2.classyapp.ResourceEnvironment;
import au.com.cybersearch2.classydb.DatabaseAdminImpl;
import au.com.cybersearch2.classydb.NativeScriptDatabaseWork;
import au.com.cybersearch2.classydb.SQLiteDatabaseSupport;
import au.com.cybersearch2.classydb.DatabaseSupport.ConnectionType;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.EntityManagerLite;
import au.com.cybersearch2.classyjpa.entity.PersistenceContainer;
import au.com.cybersearch2.classyjpa.entity.PersistenceWork;
import au.com.cybersearch2.classyjpa.persist.PersistenceContext;
import au.com.cybersearch2.classyjpa.persist.PersistenceFactory;
import au.com.cybersearch2.entity.Check;
import au.com.cybersearch2.entity.Issue;
import au.com.cybersearch2.entity.TestChecks;
import au.com.cybersearch2.entity.TestIssues;

/**
 * TelegenTest
 * @author Andrew Bowley
 * 22 May 2015
 */
public class TelegenTest
{
    /** Persistence Unit name to look up configuration details in persistence.xml */
    static public final String PU_NAME = "telegen";

    /** Factory object to create "telegen" Persistence Unit implementation */
    @Inject
    PersistenceContext persistenceContext;

    /**
     * TelegenTest
     */
    public TelegenTest()
    {
    }

    @Before
    public void setUp() throws Exception
    {
        // Set up dependency injection, which creates an ObjectGraph from a ManyToManyModule configuration object
        createObjectGraph();
        DI.inject(this);
        persistenceContext.initializeAllDatabases();
    }
    
    @Test
    public void testTableCreation() throws Exception
    {
        TestIssues testIssues = new TestIssues();
        testIssues.setUp(PU_NAME);
        TestChecks testChecks = new TestChecks();
        testChecks.setUp(PU_NAME);
        PersistenceWork verificationWork = new PersistenceWork(){

            @Override
            public void doInBackground(EntityManagerLite entityManager)
            {
                int id = 1;
                for (String[] issueItem: TestIssues.ISSUE_DATA)
                {
                    Issue issue = entityManager.find(Issue.class, Integer.valueOf(id)); 
                    assertThat(issue.getName()).isEqualTo(issueItem[0]);
                    assertThat(issue.getObservation()).isEqualTo(issueItem[1]);
                    ++id;
                }
                id = 1;
                for (String[] checkItem: TestChecks.CHECK_DATA)
                {
                    Check check = entityManager.find(Check.class, Integer.valueOf(id)); 
                    assertThat(check.getName()).isEqualTo(checkItem[0]);
                    assertThat(check.getInstruction()).isEqualTo(checkItem[1]);
                    ++id;
                }
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
        // Execute work and wait synchronously for completion
        PersistenceContainer container = new PersistenceContainer(PU_NAME);
        container.executeTask(verificationWork).waitForTask();
    }

    /**
     * Set up dependency injection, which creates an ObjectGraph from a ManyToManyModule configuration object.
     * Override to run with different database and/or platform. 
     * Refer au.com.cybersearch2.example.AndroidManyToMany in classyandroid module for Android example.
     */
    protected void createObjectGraph()
    {
        new DI(new TelegenTestModule()).validate();
    }
}
