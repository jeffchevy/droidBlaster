package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.json.JSONObject;

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
//                    saveProjectToFile(project);
//                    Project temp = readProjectFromFile();
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
        EditText start_date = (EditText) findViewById(R.id.start_date_text_field);
        EditText shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        EditText drillerName = (EditText) findViewById(R.id.driller_name_text_field);
        EditText bit_Size = (EditText) findViewById(R.id.bit_size_text_field);

        String shotNumber = String.valueOf(project.getShotNumber());
        String bitSize =String.valueOf(project.getBitSize());

        drillerName.setText(project.getDrillerName());
        project_name.setText(project.getProjectName());
        contractor_name.setText(project.getContractorName());
        //bug: need to figure out how to use our date as a string and place it in the start date field
        //start_date.setText(project.getStartDate());
        shot_number.setText(shotNumber);


    }

    public String saveProject(){
        EditText project_name = (EditText) findViewById(R.id.project_name_text_field);
        EditText contractor_name = (EditText) findViewById(R.id.contractor_name_text_field);
        EditText start_date = (EditText) findViewById(R.id.start_date_text_field);
        EditText shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        EditText drillerName = (EditText) findViewById(R.id.driller_name_text_field);
        EditText bit_Size = (EditText) findViewById(R.id.bit_size_text_field);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        String drillName = drillerName.getText().toString();
        String projectName = project_name.getText().toString();
        String contractorName = contractor_name.getText().toString();
        String date = start_date.getText().toString();
        Date startDate = new Date();
        try{
            startDate = formatter.parse(date);
        }
        catch(Exception e){

        }
        // save project information
        project.setProjectName(projectName);
        project.setDrillerName(drillName);
        project.setContractorName(contractorName);
        project.setShotNumber(Double.valueOf(shot_number.getText().toString()));
        try {
            project.setBitSiZe(Double.valueOf(bit_Size.getText().toString()));
        }
        catch (Exception ex) {
            Log.d("Bitsize: ", bit_Size.getText().toString());
            ex.printStackTrace();
        }

        AsyncTaskRunner projectSaveRunner = new AsyncTaskRunner();
        asyncTask = projectSaveRunner.execute(projectName, drillName, contractorName, shot_number.getText().toString(), bit_Size.getText().toString());
        return asyncTask.getStatus().toString();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String result = null;

            if (isConnected()) {
                result = ProjectSync.updateProjectHeader(isEdit, project);
            }
            else {
                project.setDirty(true);
            }
            ProjectKeep.getInstance().saveProjectToFile(project);
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
