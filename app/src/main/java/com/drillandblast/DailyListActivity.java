package com.drillandblast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DailyListActivity extends AppCompatActivity {
    private static final String TAG = "DailyListActivity";
    static List<DailyLog> dailyLogs = new ArrayList<>();
    private ArrayAdapter arrayAdapter = null;
    private String token = null;
    private Project project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);

        Intent process = getIntent();
        token = process.getStringExtra("token");
        project = (Project) process.getSerializableExtra("project");

        int position = process.getExtras().getInt("key");
//        project = ProjectListActivity.projects.get(position);

        AsyncTaskRunner dailyListTaskRunner = new AsyncTaskRunner();
        dailyListTaskRunner.execute();
        arrayAdapter = new ArrayAdapter<DailyLog>(this, R.layout.simple_row, dailyLogs);

        ListView listView = (ListView) findViewById(R.id.daily_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DailyLog dailyLog = dailyLogs.get(position);
                Intent editDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
                editDailyLog.putExtra("dailyLog", dailyLog);
                editDailyLog.putExtra("project", project);
                editDailyLog.putExtra("token", token);
                startActivity(editDailyLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Button button = (Button) findViewById(R.id.new_daily_log_button);

        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewDailyLog();
                    //arrayAdapter.add(new Project("Yellowstone", "Jeff", new Date(), 1));
                }
            });
        }

    }
    public void createNewDailyLog(){
        Intent addNewDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
        startActivity(addNewDailyLog);
        finish();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String data = null;
            try {
                //------------------>>
//                HttpGet httpGet = new HttpGet("http://10.0.2.2:1337/api/v1/project");
                HttpGet httpGet = new HttpGet("http://192.168.1.16:1337/api/v1/dailyLogs/"+project.getId());
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
                    String drillNumber = (String) getValue(jsonobject, "drillNumber");
                    String gallonsPumped = (String) getValue(jsonobject,"gallonsPumped");
                    String bulkTankPumpedFrom = (String) getValue(jsonobject,"bulkTankPumpedFrom");
                    String hourMeterStart = (String) getValue(jsonobject,"hourMeterStart");
                    String hourMeterEnd = (String) getValue(jsonobject, "hourMeterEnd");
                    String percussionTime = (String) getValue(jsonobject, "percussionTime");
                    String dateStr = (String) getValue(jsonobject, "date");

                    DailyLog dailyLog = new DailyLog(id, drillNumber, Double.valueOf(gallonsPumped), dateStr, Integer.valueOf(hourMeterStart),
                            Integer.valueOf(hourMeterEnd), bulkTankPumpedFrom, percussionTime );
                    dailyLogs.add(dailyLog);
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

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }
        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
