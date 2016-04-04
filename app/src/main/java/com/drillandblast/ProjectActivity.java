package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEdit){
                        ProjectListActivity.PROJECTS.set(position, createProject());
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
        ProjectListActivity.PROJECTS.add(createProject());
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
        Project project = ProjectListActivity.PROJECTS.get(position);

        EditText project_name = (EditText) findViewById(R.id.project_name_text_field);
        EditText contractor_name = (EditText) findViewById(R.id.contractor_name_text_field);
        EditText start_date = (EditText) findViewById(R.id.start_date_text_field);
        EditText shot_number = (EditText) findViewById(R.id.shot_number_text_field);

        String shotNumber = String.valueOf(project.getShotNumber());

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

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

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

        return new Project(projectName, contractorName, startDate, shotNumber);
    }
}
