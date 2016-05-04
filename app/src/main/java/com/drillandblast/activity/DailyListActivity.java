package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.Project;
import com.drillandblast.model.ProjectKeep;

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
    private ArrayAdapter arrayAdapter = null;
    private String token = null;
    private Project project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);

        Intent process = getIntent();
        token = process.getStringExtra("token");
        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);

        arrayAdapter = new ArrayAdapter<DailyLog>(this, R.layout.simple_row, project.getDailyLogs());

        ListView listView = (ListView) findViewById(R.id.daily_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DailyLog dailyLog = project.getDailyLogs().get(position);
                Intent editDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
                editDailyLog.putExtra("dailyLogId", dailyLog.getId());
                editDailyLog.putExtra("id", project.getId());
                editDailyLog.putExtra("token", token);
                startActivity(editDailyLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });
        Button backButton = (Button) findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent next = new Intent(DailyListActivity.this, ProjectListActivity.class);
                    next.putExtra("id", project.getId());
                    next.putExtra("token", token);
                    startActivity(next);
                    finish();
                }
            });
        }

        Button button = (Button) findViewById(R.id.new_daily_log_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addNewDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
                    addNewDailyLog.putExtra("id", project.getId());
                    addNewDailyLog.putExtra("token", token);
                    startActivity(addNewDailyLog);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("id", project.getId());
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
}
