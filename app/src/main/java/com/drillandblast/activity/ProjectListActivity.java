package com.drillandblast.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.drillandblast.model.ProjectKeep;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity {
    //I don't like the use of static here, but it will work for now.
    private static final String TAG = "ProjectListActivity";
    private ArrayAdapter arrayAdapter = null;
    static List<Project> projects = new ArrayList<>();
    private Project project = null;
    private String token = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Starting onCreate");

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Log.d(TAG, "network:"+isConnected);

        Intent process = getIntent();
        token = process.getStringExtra("token");

        setContentView(R.layout.activity_project_list);

        if (isConnected) {
            AsyncTaskRunner projectListTaskRunner = new AsyncTaskRunner();
            projectListTaskRunner.execute();
        }
        arrayAdapter = new ArrayAdapter<Project>(this, R.layout.simple_row, projects);

        ListView listView = (ListView) findViewById(R.id.project_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            project = projects.get(position);
            Intent editProject = new Intent(ProjectListActivity.this, ProjectActivity.class);
            editProject.putExtra("id", project.getId());
            editProject.putExtra("token", token);
            startActivity(editProject);
            finish();
            // When clicked, show a toast with the TextView text
            Toast.makeText(getApplicationContext(),
                    ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
        }
        });

        Button button = (Button) findViewById(R.id.new_project_button);

        if (button != null) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewProject();
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
            try {
                //------------------>>

//              HttpGet httpGet = new HttpGet("http://10.0.2.2:1337/api/v1/project");
                HttpGet httpGet = new HttpGet(SimpleHttpClient.baseUrl+"project");

                httpGet.setHeader("token", token);
                Log.d(TAG, "doInBackground: " + httpGet.getURI().toString());
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response = httpclient.execute(httpGet);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();
                Log.d(TAG, "doInBackground: "+status);

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    data = EntityUtils.toString(entity);

                }
            } catch (Exception e) {
                e.printStackTrace();
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
            List<Project> projects = ProjectKeep.getInstance().fromJson(result);
            arrayAdapter.addAll(projects);
        }
    }

    //if we click on the new drill log button, we navigate to the form to add a new project
    public void createNewProject(){
        Intent addNewProject = new Intent(ProjectListActivity.this, ProjectActivity.class);
        startActivity(addNewProject);
        finish();
    }
}