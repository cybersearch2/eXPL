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

import java.lang.reflect.Method;

import javax.inject.Singleton;

import org.robolectric.RuntimeEnvironment;
import org.robolectric.TestLifecycleApplication;

import android.app.Application;
import au.com.cybersearch2.classy_logic.compile.ParserAssembler;
import au.com.cybersearch2.classy_logic.jpa.JpaEntityCollector;
import au.com.cybersearch2.classyapp.ApplicationContext;
import au.com.cybersearch2.classyapp.ApplicationLocale;
import au.com.cybersearch2.classydb.DatabaseAdminImpl;
import au.com.cybersearch2.classydb.NativeScriptDatabaseWork;
import au.com.cybersearch2.classyinject.ApplicationModule;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.persist.PersistenceContext;
import au.com.cybersearch2.classyjpa.persist.PersistenceFactory;
import au.com.cybersearch2.classytask.WorkStatus;
import au.com.cybersearch2.telegen.interfaces.TelegenLauncher;
import dagger.Component;

/**
 * TestTelegenApplication
 * @author Andrew Bowley
 * 14/04/2014
 */
public class TestTelegenApplication extends Application implements TestLifecycleApplication, TelegenLauncher
{
    @Singleton
    @Component(modules = TelegenApplicationModule.class)  
    static interface ApplicationComponent extends ApplicationModule
    {
        void inject(TelegenStartup telegenStartup);
        void inject(TelegenLogic telegenLogic);
        void inject(MainActivity mainActivity);
        void inject(ParserAssembler.ExternalAxiomSource externalAxiomSource);
        void inject(DisplayDetailsDialog displayDetailsDialog);
        void inject(ApplicationLocale ApplicationLocale);
        void inject(PersistenceContext persistenceContext);
        void inject(PersistenceFactory persistenceFactory);
        void inject(NativeScriptDatabaseWork nativeScriptDatabaseWork);
        void inject(DatabaseAdminImpl databaseAdminImpl);
        void inject(JpaEntityCollector jpaEntityCollector);
        void inject(ApplicationContext applicationContext);
    }
    
    public static final String TAG = "TestTelegenApplication";
    public static final String PU_NAME = "telegen";
    private static TestTelegenApplication singleton;
    protected TelegenStartup startup;
 
    public TestTelegenApplication()
    {
        singleton = this;
        RuntimeEnvironment.application = singleton;
        startup = new TelegenStartup();
    }

    @Override 
    public void onCreate() 
    {
        super.onCreate();
    }

    @Override
    public void beforeTest(Method method) 
    {
    }

    @Override
    public void prepareTest(Object test) 
    {
    }

    @Override
    public void afterTest(Method method) 
    {
    }

    public static TestTelegenApplication getTestInstance()
    {
        if (singleton == null)
            throw new IllegalStateException("TestTelegenApplication called while not initialized");
        return singleton;
    }
    
    public void init()
    {
        ApplicationComponent component = 
                DaggerTestTelegenApplication_ApplicationComponent.builder()
                .telegenApplicationModule(new TelegenApplicationModule(this))
                .build();
        DI.getInstance(component);
    }
    
    public void startup()
    {
        startup.start(this);
    }
    
    @Override
    public WorkStatus waitForApplicationSetup()
    {
        return startup.waitForApplicationSetup();
    }
}
