package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.drillandblast.R;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectActivity extends BaseActivity {
    private static final String TAG = "ProjectActivity";
    public boolean isEdit = false;
    public int position = 0;
    public Project project = null;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent process = getIntent();

        String id = process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);
        if (project != null) {
            isEdit = true;
            setProjectData(project);
        }
        else {
            project = new Project();
        }

        Button saveButton = (Button) findViewById(R.id.save_project_button);
        Button drillLogButton = (Button) findViewById(R.id.drill_log_button);
        Button dailyLogButton = (Button) findViewById(R.id.daily_log_button);
        Button syncButton = (Button) findViewById(R.id.project_sync);

        if (drillLogButton != null) {
            drillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLogList = new Intent(ProjectActivity.this, DrillLogListActivity.class);
                    toDrillLogList.putExtra("id", project.getId());
                    startActivity(toDrillLogList);
                    finish();
                }
            });
        }
        if (dailyLogButton != null) {
            dailyLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDailyLogList = new Intent(ProjectActivity.this, DailyListActivity.class);
                    //toDailyLogList.putExtra("key", position);
                    toDailyLogList.putExtra("id", project.getId());
                    startActivity(toDailyLogList);
                    finish();
                }
            });
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveProject();
                    backToProjectList();
                }
            });
        }
        if (syncButton != null) {
            syncButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Project Dirty: "+project.isDirty());
                    ProjectSync.getInstance().sync(project);
                }
            });
        }
        Switch mySwitch = (Switch) findViewById(R.id.project_offline);

        //set the switch to ON
        mySwitch.setChecked(ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId()));
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ProjectKeep.getInstance().saveProjectToFile(project);
                }else{
                    ProjectKeep.getInstance().removeFile(project);
                }
                ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), isChecked);
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
                    result = ProjectSync.updateProjectHeader(isEdit, project);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                project.setDirty(true);
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
                startActivity(intent);
                return true;
    }
}
