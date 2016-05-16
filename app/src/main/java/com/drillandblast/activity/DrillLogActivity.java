package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.ArrayList;

public class DrillLogActivity extends BaseActivity implements Validator.ValidationListener {
    private boolean isEdit = false;
    public String token = null;
    public Project project = null;
    public DrillLog drillLog = null;
    private AsyncTask<String, String, String> asyncTask;
    private Validator validator = null;
    private Class<?> next = null;

    @Required(order=1)
    EditText log_name = null;

    @Required(order=2)
    EditText driller_name = null;

    @Required(order=3)
    EditText pattern_name = null;

    @Required(order=4)
    EditText shot_number = null;

    @Required(order=5)
    EditText bit_size = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log);
        next = DrillLogListActivity.class;

        validator = new Validator(this);
        validator.setValidationListener(this);

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

        setTitle(project.getProjectName());
//                String.valueOf(drillLog.getName())+" - "+String.valueOf(drillLog.getDrillerName()));

        Button gridCoordinatesButton = (Button) findViewById(R.id.hole_grid_button);
        //final Project project = ProjectListActivity.projects.get(position);
        //DrillLog drillLog = new DrillLog(null, 0, new Date(), new ArrayList<GridCoordinate>());
        getAndroidFields();

        if(drillLog != null) {
            isEdit = true;
            setDrillLogData(drillLog);
        }
        else
        {
            drillLog = new DrillLog();
            drillLog.setDrillerName(ProjectKeep.getInstance().getUserName());
            driller_name.setText(drillLog.getDrillerName());
            drillLog.setGridCoordinates( new ArrayList<GridCoordinate>());
        }


        if(gridCoordinatesButton !=null) {
            gridCoordinatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next = GridActivity.class;
                    validator.validate();
                }
            });
        }
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.save, menu);

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
            case R.id.menu_save:
                // Here we might start a background refresh task
                validator.validate();
                return true;

            default:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("id", project.getId());
                startActivity(intent);
                return true;
        }
    }

    public void setDrillLogData(DrillLog drillLog){

        log_name.setText(drillLog.getName());
        driller_name.setText(drillLog.getDrillerName());
        pattern_name.setText(drillLog.getPattern());
        shot_number.setText(drillLog.getShotNumber().toString());
        bit_size.setText(drillLog.getBitSize());
    }

    private void getAndroidFields() {
        log_name = (EditText) findViewById(R.id.drill_log_name_text_field);
        driller_name = (EditText) findViewById(R.id.driller_name_text_field);
        pattern_name = (EditText) findViewById(R.id.pattern_name_text_field);
        shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        bit_size = (EditText) findViewById(R.id.bit_size_text_field);
    }

    public String saveDrillLogData(Project project)
    {
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
        if (!isEdit){
            project.addDrillLog(drillLog);
        }

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

    public void onValidationSucceeded() {
        Toast.makeText(getApplicationContext(), "Saving", Toast.LENGTH_SHORT).show();
        saveDrillLogData(project);
        nextActivity();
    }

    private void nextActivity() {
        Intent toActivity = new Intent(DrillLogActivity.this, next);
        toActivity.putExtra("id", project.getId());
        toActivity.putExtra("id", project.getId());
        if (next == GridActivity.class) {
            if (drillLog.getId() == null) {
                toActivity.putExtra("drill.name", drillLog.getName());
            }
            else {
                toActivity.putExtra("drillId", drillLog.getId());
            }
        }
        startActivity(toActivity);
        finish();
    }

    public void onValidationFailed(View view, Rule<?> rule) {

        final String failureMessage = rule.getFailureMessage();
        if (view instanceof EditText) {
            EditText failed = (EditText) view;
            failed.requestFocus();
            failed.setError(failureMessage);
        } else {
            Toast.makeText(getApplicationContext(), failureMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
