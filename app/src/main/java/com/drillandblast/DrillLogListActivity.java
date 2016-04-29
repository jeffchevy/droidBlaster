package com.drillandblast;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drillandblast.http.SimpleHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DrillLogListActivity extends AppCompatActivity {
    private static final String TAG = "DrillLogListActivity";
    private ArrayAdapter arrayAdapter = null;
    public List<DrillLog> drillLogs = new ArrayList<>();
    private String token = null;
    private Project project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log_list);

        Intent process = getIntent();

        project = (Project) process.getSerializableExtra("project");
        token = process.getStringExtra("project");

        AsyncTaskRunner drillLogTaskRunner = new AsyncTaskRunner();
        drillLogTaskRunner.execute();
        arrayAdapter = new ArrayAdapter<DrillLog>(this, R.layout.simple_row, drillLogs);

        ListView listView = (ListView) findViewById(R.id.drill_log_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DrillLog drillLog = drillLogs.get(position);
                Intent editDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                editDrillLog.putExtra("drillLog", drillLog);
                editDrillLog.putExtra("project", project);
                startActivity(editDrillLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Button newDrillLogButton = (Button) findViewById(R.id.new_drill_log_button);

        if (newDrillLogButton != null) {
            newDrillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                    toDrillLog.putExtra("project", project);
                    startActivity(toDrillLog);
                    finish();
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
//                HttpGet httpGet = new HttpGet("http://10.0.2.2:1337/api/v1/project");
                HttpGet httpGet = new HttpGet(SimpleHttpClient.baseUrl+"drillLogs/"+project.getId());
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
            try {
                arrayAdapter.clear();
                JSONArray jsonarray = new JSONArray(result);
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String id = (String) getValue(jsonobject, "_id");
                    String drillerName = (String) getValue(jsonobject, "drillerName");
                    String name = (String) getValue(jsonobject,"name");

                    DrillLog drillLog = new DrillLog(id, drillerName, name);
                    drillLogs.add(drillLog);
                }
                arrayAdapter.notifyDataSetChanged();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        private Object getValue(JSONObject jsonObject, String name){
            Object result= null;
            try{
                result = jsonObject.getString(name);
            } catch (Exception ex) {}
            return result;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("project", project);
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
}
