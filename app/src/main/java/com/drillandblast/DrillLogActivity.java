package com.drillandblast;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.drillandblast.http.SimpleHttpClient;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DrillLogActivity extends AppCompatActivity {
    private boolean isEdit = false;
    public String token = null;
    public Project project = null;
    public DrillLog drillLog = null;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log);

        final Intent process = getIntent();
        project = (Project) process.getSerializableExtra("project");
        drillLog = (DrillLog) process.getSerializableExtra("drillLog");

        Button saveButton = (Button) findViewById(R.id.save_drill_log_button);
        Button gridCoordinatesButton = (Button) findViewById(R.id.hole_grid_button);
        //final Project project = ProjectListActivity.projects.get(position);
        //DrillLog drillLog = new DrillLog(null, 0, new Date(), new ArrayList<GridCoordinate>());
        if(drillLog != null) {
            isEdit = true;
            setDrillLogData(drillLog);
        }
        else
        {
            drillLog = new DrillLog(null,"","" );
            drillLog.setGridCoordinates( new ArrayList<GridCoordinate>());
        }

        if(saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDrillLogData(project);
                    Intent toDrillLogList = new Intent(DrillLogActivity.this, DrillLogListActivity.class);
                    toDrillLogList.putExtra("project", project);
                    startActivity(toDrillLogList);
                    finish();
                }
            });
        }

        if(gridCoordinatesButton !=null) {
            gridCoordinatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLogCoordinates = new Intent(DrillLogActivity.this, GridActivity.class);
                    toDrillLogCoordinates.putExtra("project", project);
                    toDrillLogCoordinates.putExtra("drillLog", drillLog);
                    startActivity(toDrillLogCoordinates);
                    finish();
                }
            });
        }
    }
    public void setDrillLogData(DrillLog drillLog){
        EditText log_name = (EditText) findViewById(R.id.drill_log_name_text_field);
        EditText driller_name = (EditText) findViewById(R.id.driller_name_text_field);

        log_name.setText(drillLog.getName());
        driller_name.setText(drillLog.getDrillerName());
    }

    public String saveDrillLogData(Project project)
    {
        EditText log_name = (EditText) findViewById(R.id.drill_log_name_text_field);
        EditText driller_name = (EditText) findViewById(R.id.driller_name_text_field);
        String name = log_name.getText().toString();
        String drillerName = driller_name.getText().toString();

        AsyncTaskRunner drillLogTaskRunner = new AsyncTaskRunner();
        asyncTask = drillLogTaskRunner.execute(name, drillerName);

        return asyncTask.getStatus().toString();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String result = null;

            JSONObject json = new JSONObject();
            String response = null;
            try {
                json.put("name", params[0]);
                json.put("drillerName", params[1]);

                if (isEdit)
                {
                    result = SimpleHttpClient.executeHttpPut("drillLogs/"+project.getId()+"/"+drillLog.getId(), json, token);
                    drillLog.setName(params[0]);
                    drillLog.setDrillerName(params[1]);
                }
                else
                {
                    result = SimpleHttpClient.executeHttpPost("drillLogs/"+project.getId(), json, token);
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
}
