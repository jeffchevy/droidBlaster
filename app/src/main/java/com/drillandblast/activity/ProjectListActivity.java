package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ProjectListActivity extends BaseActivity {
    //I don't like the use of static here, but it will work for now.
    private static final String TAG = "ProjectListActivity";
    private ArrayAdapter arrayAdapter = null;
    private Project project = null;
    ListView listView = null;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * This method is called when one of the menu items to selected. These items
     * can be on the Action Bar, the overflow menu, or the standard options menu. You
     * should return true if you handle the selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                // Here we might start a background refresh task
                Toast.makeText(getApplicationContext(), "Refreshing list", Toast.LENGTH_SHORT).show();
                AsyncTaskRunner projectListTaskRunner = new AsyncTaskRunner();
                projectListTaskRunner.execute();
                Log.d("app", "Refresh clicked");
                return true;

            case R.id.menu_settings:
                Intent settings = new Intent(ProjectListActivity.this, SettingsActivity.class);
                startActivity(settings);
                finish();
                // Here we would open up our settings activity
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting onCreate");

        Intent process = getIntent();
        setContentView(R.layout.activity_project_list);

        listView = (ListView) findViewById(R.id.project_list_view);
        listView.setTextFilterEnabled(true);
        // check to see if we need to get data
        if (ProjectKeep.getInstance().size() <= 0) {
            arrayAdapter = new ArrayAdapter<Project>(this, R.layout.simple_row, new ArrayList<Project>());
            AsyncTaskRunner projectListTaskRunner = new AsyncTaskRunner();
            projectListTaskRunner.execute();
        }
        else{
            arrayAdapter = new ArrayAdapter<Project>(this, R.layout.simple_row, ProjectKeep.getInstance().findAll());
            listView.setAdapter(arrayAdapter);
        }



        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            project = (Project)arrayAdapter.getItem(position);
            Intent editProject = new Intent(ProjectListActivity.this, ProjectActivity.class);
            editProject.putExtra("id", project.getId());
            startActivity(editProject);
            finish();
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(),
                    ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
        }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.new_project_button);

        if (button != null) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected()) {
                        createNewProject();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),
                                "No connectivity!", Toast.LENGTH_SHORT).show();

                    }
                    //arrayAdapter.add(new Project("Yellowstone", "Jeff", new Date(), 1));
                }
            });
        }
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String data = null;
            if (isConnected()) {
                ProjectSync.getInstance().sync();
            } else {
                ProjectSync.getInstance().readFromDisk();
            }
            return data;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            arrayAdapter.clear();
            arrayAdapter.addAll(ProjectKeep.getInstance().findAll());
            listView.setAdapter(arrayAdapter);
            Log.d(TAG, "onPostExecute: Finished");
        }
    }

    //if we click on the new drill log button, we navigate to the form to add a new project
    public void createNewProject(){
        Intent addNewProject = new Intent(ProjectListActivity.this, ProjectActivity.class);
        startActivity(addNewProject);
        finish();
    }
}
