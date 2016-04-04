package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ProjectListActivity extends AppCompatActivity {
    //I don't like the use of static here, but it will work for now.
    static final Project teton = new Project("Teton", "Zach", new Date(), 1);
    static final Project wasatch = new Project("Wasatch", "Brent", new Date(), 1);
    static final Project yellowstone = new Project("Yellowstone", "Tyler", new Date(), 1);
    static final Project glacier = new Project("Glacier", "Jeff", new Date(), 1);
    static final ArrayList<Project> PROJECTS = new ArrayList<Project>(Arrays.asList(teton, wasatch, yellowstone, glacier));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        final ArrayAdapter arrayAdapter = new ArrayAdapter<Project>(this, R.layout.simple_row, PROJECTS);

        ListView listView = (ListView) findViewById(R.id.project_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Project project = PROJECTS.get(position);
                Intent editProject = new Intent(ProjectListActivity.this, ProjectActivity.class);
                editProject.putExtra("key", position);
                startActivity(editProject);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Button button = (Button) findViewById(R.id.new_project_button);

        if (button != null) {
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewProject();
                    //arrayAdapter.add(new Project("Yellowstone", "Jeff", new Date(), 1));
                }
            });
        }
    }

    //if we click on the new drill log button, we navigate to the form to add a new project
    public void createNewProject(){
        Intent addNewProject = new Intent(ProjectListActivity.this, ProjectActivity.class);
        startActivity(addNewProject);
        finish();
    }
}
