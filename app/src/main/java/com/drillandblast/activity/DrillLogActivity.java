package com.drillandblast.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DrillLogActivity extends BaseActivity {
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
        project = ProjectKeep.getInstance().findById(id);

        String drillId =  process.getStringExtra("drillId");
        if (drillId == null) {
            String drillName =  process.getStringExtra("drill.name");
            drillLog = ProjectKeep.getInstance().findDrillLogByName(project, drillName);
        }
        else {
            drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);
        }

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
            drillLog = new DrillLog();
            drillLog.setGridCoordinates( new ArrayList<GridCoordinate>());
            project.addDrillLog(drillLog);
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
                    if (drillLog.getId() == null) {
                        toDrillLogCoordinates.putExtra("drill.name", drillLog.getName());
                    }
                    else {
                        toDrillLogCoordinates.putExtra("drillId", drillLog.getId());
                    }
                    startActivity(toDrillLogCoordinates);
                    finish();
                }
            });
        }
    }
    public void setDrillLogData(DrillLog drillLog){
        EditText log_name = (EditText) findViewById(R.id.drill_log_name_text_field);
        EditText driller_name = (EditText) findViewById(R.id.driller_name_text_field);
        EditText pattern_name = (EditText) findViewById(R.id.pattern_name_text_field);
        EditText shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        EditText bit_size = (EditText) findViewById(R.id.bit_size_text_field);

        log_name.setText(drillLog.getName());
        driller_name.setText(drillLog.getDrillerName());
        pattern_name.setText(drillLog.getPattern());
        shot_number.setText(drillLog.getShotNumber().toString());
        bit_size.setText(drillLog.getBitSize());
    }

    public String saveDrillLogData(Project project)
    {
        EditText log_name = (EditText) findViewById(R.id.drill_log_name_text_field);
        EditText driller_name = (EditText) findViewById(R.id.driller_name_text_field);
        EditText pattern_name = (EditText) findViewById(R.id.pattern_name_text_field);
        EditText shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        EditText bit_size = (EditText) findViewById(R.id.bit_size_text_field);

        String name = log_name.getText().toString();
        String drillerName = driller_name.getText().toString();
        String patternName = pattern_name.getText().toString();
        String shotNumberString = shot_number.getText().toString();
        String bitSize = bit_size.getText().toString();

        drillLog.setName(name);
        drillLog.setDrillerName(drillerName);
        drillLog.setPattern(patternName);
        drillLog.setShotNumber(Integer.valueOf(shotNumberString));
        drillLog.setBitSize(bitSize);
        AsyncTaskRunner drillLogTaskRunner = new AsyncTaskRunner();
        asyncTask = drillLogTaskRunner.execute();

        return asyncTask.getStatus().toString();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if (isConnected()) {
                try {
                    result = ProjectSync.getInstance().updateDrillLog(isEdit, project, drillLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                drillLog.setDirty(true);
            }
            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
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
