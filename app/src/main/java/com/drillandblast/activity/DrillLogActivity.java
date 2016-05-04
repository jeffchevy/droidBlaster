package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.model.ProjectKeep;

import org.json.JSONObject;

import java.util.ArrayList;

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
        String id =  process.getStringExtra("id");
        String drillId =  process.getStringExtra("drillId");
        project = ProjectKeep.getInstance().findById(id);
        drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);

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
                    toDrillLogList.putExtra("id", project.getId());
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
                    toDrillLogCoordinates.putExtra("id", project.getId());
                    toDrillLogCoordinates.putExtra("drillId", drillLog.getId());
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

        if (!isEdit)
        {
            project.addDrillLog(drillLog);
        }
        drillLog.setName(name);
        drillLog.setDrillerName(drillerName);
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
                }
                else
                {
                    result = SimpleHttpClient.executeHttpPost("drillLogs/"+project.getId(), json, token);
                    JSONObject jsonobject = new JSONObject(result);
                    String id = jsonobject.getString("id");
                    drillLog.setId(id);
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
        intent.putExtra("id", project.getId());
        startActivity(intent);
        return true;
    }
}
