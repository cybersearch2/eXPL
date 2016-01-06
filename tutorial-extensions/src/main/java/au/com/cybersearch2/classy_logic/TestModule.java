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

import javax.inject.Singleton;

import au.com.cybersearch2.classyapp.ResourceEnvironment;
import au.com.cybersearch2.classydb.DatabaseSupport.ConnectionType;
import au.com.cybersearch2.classydb.SQLiteDatabaseSupport;
import au.com.cybersearch2.classyjpa.persist.PersistenceFactory;
import au.com.cybersearch2.classytask.ThreadHelper;
import dagger.Module;
import dagger.Provides;

/**
 * TestModule
 * @author Andrew Bowley
 * 17 Mar 2015
 */
@Module(/*injects= {
        ParserAssembler.ExternalAxiomSource.class,
		ParserResources.class,
		WorkerRunnable.class,
		PersistenceFactory.class,
		NativeScriptDatabaseWork.class,
		PersistenceContext.class,
		DatabaseAdminImpl.class }*/)
public class TestModule 
{
    @Provides @Singleton ThreadHelper provideSystemEnvironment()
    {
        return new TestSystemEnvironment();
    }
    
    @Provides @Singleton ResourceEnvironment provideResourceEnvironment()
    {
        return new JavaTestResourceEnvironment("src/main/resources");
    }

    @Provides @Singleton PersistenceFactory providePersistenceModule()
    {
        return new PersistenceFactory(new SQLiteDatabaseSupport(ConnectionType.memory));
    }
    
    @Provides @Singleton ProviderManager provideProviderManager()
    {
        return new ProviderManager();
    }
}
