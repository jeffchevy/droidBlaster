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
import java.util.List;

public class ProjectActivity extends AppCompatActivity {
    public boolean isEdit = false;
    public int position = 0;
    public String token;
    public Project project = null;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent process = getIntent();
        token = process.getStringExtra("token");

        project = (Project) process.getSerializableExtra("project");
        if (project != null) {
            isEdit = true;
            setProjectData(project);
        }


        Button saveButton = (Button) findViewById(R.id.save_project_button);
        Button drillLogButton = (Button) findViewById(R.id.drill_log_button);
        Button dailyLogButton = (Button) findViewById(R.id.daily_log_button);

        if (drillLogButton != null) {
            drillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLogList = new Intent(ProjectActivity.this, DrillLogListActivity.class);
                    toDrillLogList.putExtra("token", token);
                    toDrillLogList.putExtra("project", project);
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
                    toDailyLogList.putExtra("token", token);
                    toDailyLogList.putExtra("project", project);
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

        AsyncTaskRunner projectSaveRunner = new AsyncTaskRunner();
        asyncTask = projectSaveRunner.execute(projectName, drillName, contractorName, shot_number.getText().toString(), bit_Size.getText().toString());
        return asyncTask.getStatus().toString();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {

            String result = null;

            JSONObject json = new JSONObject();
            String response = null;
            try {
                json.put("jobName", params[0]);
                json.put("drillerName", params[1]);
                json.put("contractorsName", params[2]);
                json.put("shotNumber", params[3]);
                json.put("bitSize", params[4]);

                if (isEdit)
                {
                    result = SimpleHttpClient.executeHttpPut("project/"+project.getId(), json, token);
                }
                else
                {
                    result = SimpleHttpClient.executeHttpPost("project", json, token);
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }
    }

}
