package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectActivity extends BaseActivity {
    private static final String TAG = "ProjectActivity";
    public boolean isEdit = false;
    public int position = 0;
    public Project project = null;
    private boolean saveToDisk = false;
    private AsyncTask<String, String, String> asyncTask;

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                // Here we would open up our settings activity
                saveProject();
                return true;
            default:
                Intent intent = NavUtils.getParentActivityIntent(this);
                startActivity(intent);
                return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent process = getIntent();

        saveToDisk = false;
        String id = process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);
        if (project != null) {
            isEdit = true;
            setProjectData(project);
        }
        else {
            project = new Project();
            project.setDailyLogs(new ArrayList<DailyLog>());
            project.setDrillLogs(new ArrayList<DrillLog>());
        }

        Button drillLogButton = (Button) findViewById(R.id.drill_log_button);
        Button dailyLogButton = (Button) findViewById(R.id.daily_log_button);

        if (drillLogButton != null) {
            drillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (project.getId() != null) {
                        Intent toDrillLogList = new Intent(ProjectActivity.this, DrillLogListActivity.class);
                        toDrillLogList.putExtra("id", project.getId());
                        startActivity(toDrillLogList);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Project has not been saved!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (dailyLogButton != null) {
            dailyLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (project.getId() != null) {
                        Intent toDailyLogList = new Intent(ProjectActivity.this, DailyListActivity.class);
                        //toDailyLogList.putExtra("key", position);
                        toDailyLogList.putExtra("id", project.getId());
                        startActivity(toDailyLogList);
                        finish();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Project has not been saved!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        Switch mySwitch = (Switch) findViewById(R.id.project_offline);

        boolean offline = (project != null && project.getId() != null) ? ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId()) : false;
        Log.d(TAG, "Project Offline status: "+offline);
        //set the switch to ON
        mySwitch.setChecked(offline);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (project.getId() != null) {
                    if (!isChecked) {
                        ProjectKeep.getInstance().removeFile(project);
                    }
                    ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), isChecked);
                } else {
                    saveToDisk = isChecked;
                }
            }
        });
    }

    public void backToProjectList(){
        Intent toProjectList = new Intent(ProjectActivity.this, ProjectListActivity.class);
        startActivity(toProjectList);
        finish();
    }

    //set the data for a given project in our arrayList to all the text fields in the form
    public void setProjectData(Project project){

        EditText project_name = (EditText) findViewById(R.id.project_name_text_field);
        EditText contractor_name = (EditText) findViewById(R.id.contractor_name_text_field);

        project_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "Text changed: "+s);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        project_name.setText(project.getProjectName());
        contractor_name.setText(project.getContractorName());
        //bug: need to figure out how to use our date as a string and place it in the start date field
        //start_date.setText(project.getStartDate());
    }

    public String saveProject(){
        EditText project_name = (EditText) findViewById(R.id.project_name_text_field);
        EditText contractor_name = (EditText) findViewById(R.id.contractor_name_text_field);

        String projectName = project_name.getText().toString();
        String contractorName = contractor_name.getText().toString();

        project.setProjectName(projectName);
        project.setContractorName(contractorName);
        // save project information
        AsyncTaskRunner projectSaveRunner = new AsyncTaskRunner();
        asyncTask = projectSaveRunner.execute();
        return asyncTask.getStatus().toString();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            if (isConnected()) {
                try {
                    result = ProjectSync.getInstance().updateProjectHeader(isEdit, project);
                    if (!isEdit) {
                        ProjectKeep.getInstance().addProject(project);
                    }
                } catch (Exception e) {
                    try {
                        ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), true);
                        ProjectKeep.getInstance().saveProjectToFile(project);
                        JSONObject json = new JSONObject();
                        json.put("success", false);
                        json.put("message", e.getMessage());
                        result = json.toString();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                }
            }
            else {
                project.setDirty(true);
            }
            // if saveToDisk is true then we coulnd't save the file before becuase we didn't know the id
            // becuase it was not save before so do it now
            if (saveToDisk && project.getId() != null ) {
                ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), true);
            }

            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
            }
            return result;
        }
        protected void onPostExecute(String result) {
            String message = getResultMessage(result);
            if (isUpdateSuccessful(result)) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (result != null) {
                    backToProjectList();
                }
            }
            else {
                String text = (message == null) ? "Error Saving" : message;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }


    }
}
