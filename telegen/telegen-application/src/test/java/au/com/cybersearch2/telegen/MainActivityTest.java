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

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowDialog;
import org.robolectric.util.ActivityController;
import org.robolectric.util.SimpleFuture;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.cybersearch2.classyjpa.AndroidPersistenceEnvironment;
import au.com.cybersearch2.classywidget.ListItem;
import au.com.cybersearch2.telegen.MainActivity.MainStatus;
import au.com.cybersearch2.telegen.entity.TestChecks;
import au.com.cybersearch2.telegen.entity.TestIssues;

/**
 * MainActivityTest
 * @author Andrew Bowley
 * 14/05/2014
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest
{
    @Implements(value = SystemClock.class, callThroughByDefault = true)
    public static class MyShadowSystemClock {
        public static long elapsedRealtime() {
            return 0;
        }
    }

    @Implements(AsyncTaskLoader.class)
    public static class MyShadowAsyncTaskLoader<D> 
    {
          @RealObject private AsyncTaskLoader<D> realLoader;
          private SimpleFuture<D> future;

          public void __constructor__(Context context) {
            BackgroundWorker worker = new BackgroundWorker();
            future = new SimpleFuture<D>(worker) {
              @Override protected void done() {
                try {
                  final D result = get();
                  Robolectric.getForegroundThreadScheduler().post(new Runnable() {
                    @Override public void run() {
                      realLoader.deliverResult(result);
                    }
                  });
                } catch (InterruptedException e) {
                  // Ignore
                }
              }
            };
          }

          @Implementation
          public void onForceLoad() {
              Robolectric.getBackgroundThreadScheduler().post(new Runnable() {
              @Override
              public void run() {
                future.run();
              }
            });
          }

          private final class BackgroundWorker implements Callable<D> {
            @Override public D call() throws Exception {
              return realLoader.loadInBackground();
            }
          }
    }
   
    public static final long ID = 1L;
    static String DATABASE_NAME = "telegen.db";
    private ActivityController<MainActivity> controller;
    private MainActivity mainActivity;

    @Before
    public void setUp() 
    {
        TestTelegenApplication testTelegenApplication = TestTelegenApplication.getTestInstance();
        testTelegenApplication.startup();// Initialize database
        testTelegenApplication.waitForApplicationSetup();
        Robolectric.getForegroundThreadScheduler().pause();
        Robolectric.getBackgroundThreadScheduler().pause();
    }

    @After
    public void tearDown() 
    {
        controller.destroy();
    }
    

    private Intent getNewIntent()
    {
        return new Intent(RuntimeEnvironment.application, MainActivity.class);
    }
    
    @Config(shadows = { MyShadowSystemClock.class, MyShadowAsyncTaskLoader.class })
    @Test
    public void test_OnCreate() throws Exception
    {
        controller = Robolectric.buildActivity(MainActivity.class);
        mainActivity = controller.create().start().visible().get();
        assertThat(mainActivity.mainStatus).isEqualTo(MainStatus.CREATE);
        
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(mainActivity.mainStatus).isEqualTo(MainStatus.INIT);
        
        assertThat(mainActivity.telegenLogic).isNotNull();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(mainActivity.mainStatus).isEqualTo(MainStatus.START);
        
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(mainActivity.mainStatus).isEqualTo(MainStatus.ISSUES);
        
        assertThat(mainActivity.adapter.getCount()).isEqualTo(TestIssues.ISSUE_DATA.length);
        int index = 0;
        for (int i = 0; i < TestIssues.ISSUE_DATA.length; i++)
        {
            ListItem value = (ListItem)mainActivity.adapter.getItem(i);
            String[] issueItem = TestIssues.ISSUE_DATA[index++];
            assertThat(value.getName()).isEqualTo(issueItem[0]);
            assertThat(value.getValue()).isEqualTo(issueItem[1]);
        }
        assertThat(mainActivity.displayListFragment.isVisible()).isTrue();
        assertThat(mainActivity.displayListFragment.getListView().getCount()).isEqualTo(TestIssues.ISSUE_DATA.length);
        OnItemClickListener onItemClickListener = mainActivity.displayListFragment.getListView().getOnItemClickListener();
        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().pause();
        // position = 0
        onItemClickListener.onItemClick(null, null, 0, 0);
        Robolectric.getBackgroundThreadScheduler().runOneTask();
        assertThat(mainActivity.telegenLogic.currentCheck).isEqualTo(TestChecks.CHECK_DATA[0][0]);
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(mainActivity.mainStatus).isEqualTo(MainStatus.CHECKS);
        
        ShadowDialog dialog = Shadows.shadowOf(ShadowDialog.getLatestDialog());
        assertThat(dialog.getTitle()).isEqualTo(DisplayDetailsDialog.DIALOG_TITLE);
        assertThat(dialog.isCancelableOnTouchOutside()).isTrue();
        TextView tv1 = (TextView) ShadowDialog.getLatestDialog().findViewById(R.id.detail_title);
        assertThat(tv1.getText()).isEqualTo(TestIssues.ISSUE_DATA[0][0]);
        TextView tv2 = (TextView) ShadowDialog.getLatestDialog().findViewById(R.id.detail_context);
        assertThat(tv2.getText()).isEqualTo(TestIssues.ISSUE_DATA[0][1]);
        TextView tv3 = (TextView) ShadowDialog.getLatestDialog().findViewById(R.id.detail_content);
        assertThat(tv3.getText()).isEqualTo(TestChecks.CHECK_DATA[0][1]);
        AndroidPersistenceEnvironment androidPersistenceEnvironment = mainActivity.androidPersistenceEnvironment;
        assertThat(androidPersistenceEnvironment).isNotNull();
        SQLiteOpenHelper sqlLiteOpenHelper = androidPersistenceEnvironment.getSQLiteOpenHelper();
        assertThat(sqlLiteOpenHelper).isNotNull();
        assertThat(sqlLiteOpenHelper.getDatabaseName()).isEqualTo(DATABASE_NAME);
        SQLiteDatabase sqliteDatabase = null;
        try
        {
            sqliteDatabase = sqlLiteOpenHelper.getWritableDatabase();
            assertThat(sqliteDatabase.isDatabaseIntegrityOk()).isTrue();
            assertThat(sqliteDatabase.getVersion()).isEqualTo(1);
        }
        finally
        {
            if ((sqliteDatabase != null) && sqliteDatabase.isOpen())
                sqliteDatabase.close();
        }
    }
 
}
