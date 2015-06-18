package au.com.cybersearch2.telegen;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import au.com.cybersearch2.classyinject.DI;
import au.com.cybersearch2.classyjpa.AndroidPersistenceEnvironment;
import au.com.cybersearch2.classywidget.PropertiesListAdapter;
import au.com.cybersearch2.classywidget.PropertiesListAdapter.Value;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity 
{
    protected Dialog dialog;
    protected DisplayListFragment displayListFragment;
    protected PropertiesListAdapter adapter;
    protected List<Value> fieldList;
    protected TelegenLogic telegenLogic;
    
    /** Android Persistence implementation which has a custom feature of providing the underlying SQLiteOpenHelper */
    @Inject @Named(TelegenApplication.PU_NAME) 
    AndroidPersistenceEnvironment androidPersistenceEnvironment;

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
        setContentView(R.layout.display_list_fragment);
        displayListFragment = getIssuesListFragment();
        adapter = new PropertiesListAdapter(this);
        displayListFragment.setListAdapter(adapter);
        DI.inject(this);
        BackgroundTask starter =  new BackgroundTask(this)
        {
            @Override
            public Boolean loadInBackground()
            {
                telegenLogic = new TelegenLogic();
                fieldList = telegenLogic.getIssues();
                return Boolean.TRUE;
            }
            
            @Override
            public void onLoadComplete(Loader<Boolean> loader, Boolean success)
            {
                displayIssues();
            }
       };
       starter.onStartLoading();
     }

    protected DisplayListFragment getIssuesListFragment()
    {
        return (DisplayListFragment) getSupportFragmentManager().findFragmentById(R.id.display_list_fragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);      
        setIntent(intent);
        parseIntent(intent);
    }

    protected void parseIntent(Intent intent)
    {
        if (Intent.ACTION_MAIN.equals(intent.getAction())) 
            displayIssues();
    }
    

    
    protected void displayIssues()
    {
        adapter.changeData(fieldList);
        displayListFragment.getListView().setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final BackgroundTask responder =  new BackgroundTask(MainActivity.this)
                {
                    Bundle args = new Bundle();
                    
                    @Override
                    public Boolean loadInBackground()
                    {
                        String title = fieldList.get(position).getName();
                        String context = fieldList.get(position).getValue();
                        String check = telegenLogic.getFirstCheck(title);
                        args.putString(DisplayDetailsDialog.KEY_CONTENT, check);
                        args.putString(DisplayDetailsDialog.KEY_TITLE, title);
                        args.putString(DisplayDetailsDialog.KEY_CONTEXT, context);
                        return Boolean.TRUE;
                    } 
                    
                    @Override
                    public void onLoadComplete(Loader<Boolean> loader, Boolean success)
                    {
                        dialog = showDialog(getSupportFragmentManager(), args);
                    }
                };
                responder.onStartLoading();
             }
        });
    }

    protected Dialog showDialog(FragmentManager fragmentManager, Bundle args) 
    {
        DisplayDetailsDialog newFragment = new DisplayDetailsDialog();
        newFragment.setArguments(args);
        newFragment.show(fragmentManager, "dialog");
        return newFragment.getDialog();
    }

}

