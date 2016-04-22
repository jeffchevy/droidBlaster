package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectActivity extends AppCompatActivity {
    public boolean isEdit = false;
    public int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent process = getIntent();
        if(editProject(process)){
            position = process.getExtras().getInt("key");
            setProjectData(position);
        }

        Button saveButton = (Button) findViewById(R.id.save_project_button);
        Button drillLogButton = (Button) findViewById(R.id.drill_log_button);
        Button dailyLogButton = (Button) findViewById(R.id.daily_log_button);

        if (drillLogButton != null) {
            drillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLogList = new Intent(ProjectActivity.this, DrillLogListActivity.class);
                    toDrillLogList.putExtra("key", position);
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
                    toDailyLogList.putExtra("key", position);
                    startActivity(toDailyLogList);
                    finish();
                }
            });
        }

        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEdit){
                        ProjectListActivity.projects.set(position, createProject());
                        backToProjectList();
                    }
                    else {
                        addProject();
                    }

                }
            });
        }
    }

    public void addProject(){
        ProjectListActivity.projects.add(createProject());
        backToProjectList();
        //arrayAdapter.add(new Project("Yellowstone", "Jeff", new Date(), 1));
    }

    public void backToProjectList(){
        Intent toProjectList = new Intent(ProjectActivity.this, ProjectListActivity.class);
        startActivity(toProjectList);
        finish();
    }

    public boolean editProject(Intent intent){
        isEdit = false;
        if(intent.hasExtra("key")){
            isEdit = true;
        }
        return isEdit;
    }

    //set the data for a given project in our arrayList to all the text fields in the form
    public void setProjectData(int position){
        Project project = ProjectListActivity.projects.get(position);

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

    public Project createProject(){
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
        double shotNumber = Double.parseDouble(shot_number.getText().toString());

        double bitSize = Double.parseDouble(bit_Size.getText().toString());

        List<DrillLog> drillLogs = new ArrayList<>();

        List<DailyLog> dailyLogs = new ArrayList<>();

        return new Project(projectName, contractorName, startDate, shotNumber, drillName, bitSize, drillLogs, dailyLogs);
    }
}
