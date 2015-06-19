package au.com.cybersearch2.telegen;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.AndroidPersistenceEnvironment;
import au.com.cybersearch2.classytask.BackgroundTask;
import au.com.cybersearch2.classytask.WorkStatus;
import au.com.cybersearch2.classywidget.PropertiesListAdapter;
import au.com.cybersearch2.classywidget.PropertiesListAdapter.Value;
import au.com.cybersearch2.telegen.interfaces.TelegenLauncher;

/**
 * Tele-Genious MainActivity
 * @author Andrew Bowley
 * 19 Jun 2015
 */
@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity 
{
    public enum MainStatus
    {
       CREATE,
       INIT,
       START,
       ISSUES,
       CHECKS
    }
    
    private static final String TAG = "MainActivity";

    /** Status tracking for maintenance only */
    protected MainStatus mainStatus;
    
    protected Dialog dialog;
    /** Display list view */
    protected DisplayListFragment displayListFragment;
    /** Adapter for display list view */
    protected PropertiesListAdapter adapter;
    
    /** Android Persistence implementation which has a custom feature of providing the underlying SQLiteOpenHelper */
    @Inject @Named(TelegenApplication.PU_NAME) 
    AndroidPersistenceEnvironment androidPersistenceEnvironment;
    /** Logic engine */
    @Inject
    TelegenLogic telegenLogic;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        mainStatus = MainStatus.CREATE;
        // Wait for application start which initializes persistence
        final TelegenLauncher telegenLauncher = (TelegenLauncher)getApplication();
        // Prepare display list view
        setContentView(R.layout.display_list_fragment);
        displayListFragment = getIssuesListFragment();
        adapter = new PropertiesListAdapter(this);
        displayListFragment.setListAdapter(adapter);
        // Complete initialization in background
        BackgroundTask starter =  new BackgroundTask(this)
        {
            /**
             * The background task
             * @see au.com.cybersearch2.classytask.BackgroundTask#loadInBackground()
             */
            @Override
            public Boolean loadInBackground()
            {
                mainStatus = MainStatus.INIT;
                WorkStatus status = telegenLauncher.waitForApplicationSetup();
                if (status != WorkStatus.FINISHED)
                    return Boolean.FALSE;
                DI.inject(MainActivity.this);
                return Boolean.TRUE;
            }

            @Override
            public void onLoadComplete(Loader<Boolean> loader, Boolean success)
            {
                if (!success)
                    displayToast("Telegen failed to start due to unexpected error");
                else
                    startActivity();
            }};
         starter.onStartLoading();
     }

    protected void startActivity()
    {
        mainStatus = MainStatus.START;
        BackgroundTask issuesLoader = new BackgroundTask(this){
            List<Value> issuesList;
            
            @Override
            public Boolean loadInBackground()
            {
                issuesList = telegenLogic.getIssues();
                return Boolean.TRUE;
            }

            @Override
            public void onLoadComplete(Loader<Boolean> loader, Boolean success)
            {
                displayIssues(issuesList);
            }
        };
        issuesLoader.onStartLoading();
    }

    /**
     * Returns fragment which diplays a list
     * @return DisplayListFragment
     */
    protected DisplayListFragment getIssuesListFragment()
    {
        return (DisplayListFragment) getSupportFragmentManager().findFragmentById(R.id.display_list_fragment);
    }

    /**
     * onCreateOptionsMenu
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
    }

    /**
     * onNewIntent
     * @see android.support.v4.app.FragmentActivity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);      
        setIntent(intent);
        parseIntent(intent);
    }

    /**
     * Parse intent
     * @param intent
     */
    protected void parseIntent(Intent intent)
    {
        if (Intent.ACTION_MAIN.equals(intent.getAction())) 
            startActivity();
    }
 
    /**
     * Display issues as a list
     * @param issuesList Backing list for display list view
     */
    protected void displayIssues(final List<Value> issuesList)
    {
        mainStatus = MainStatus.ISSUES;
        adapter.changeData(issuesList);
        displayListFragment.getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                startChecksDialog(issuesList.get(position));
             }
        });
    }

    /**
     * Start troubleshooting dialog
     * @param issue Value object containing selected issue
     */
    protected void startChecksDialog(final Value issue)
    {
        BackgroundTask responder =  new BackgroundTask(this) 
        {   // Bundle supplies title, context and context to dialog
            Bundle args = new Bundle();
            
            @Override
            public Boolean loadInBackground()
            {
                String title = issue.getName();
                String context = issue.getValue();
                String check = telegenLogic.getFirstCheck(title);
                args.putString(DisplayDetailsDialog.KEY_CONTENT, check);
                args.putString(DisplayDetailsDialog.KEY_TITLE, title);
                args.putString(DisplayDetailsDialog.KEY_CONTEXT, context);
                return Boolean.TRUE;
            } 
            
            @Override
            public void onLoadComplete(Loader<Boolean> loader, Boolean success)
            {
                mainStatus = MainStatus.CHECKS;
                dialog = showDialog(args);
            }
        };
        responder.onStartLoading();
    }
  
    /**
     * Show dialog
     * @param args Bundle containing parameters to pass to dialog
     * @return Dialog object
     */
    protected Dialog showDialog(Bundle args) 
    {
        DisplayDetailsDialog newFragment = new DisplayDetailsDialog();
        newFragment.setArguments(args);
        newFragment.show(getSupportFragmentManager(), "dialog");
        return newFragment.getDialog();
    }

    /**
     * Display toast, which is an alert message that pops up briefly
     * @param text Message
     */
    protected void displayToast(String text)
    {
        Log.e(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();    
    }
}

