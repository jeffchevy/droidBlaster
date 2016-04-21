package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class GridCoordinateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_coordinate);

        Intent process = getIntent();
        int gridEntryIndex = process.getExtras().getInt("gridEntry");
        int projectIndex = process.getExtras().getInt("project");
        int drillLogIndex = process.getExtras().getInt("drillLog");

        Project project = ProjectListActivity.projects.get(projectIndex);
        DrillLog drillLog = project.getDrillLog(drillLogIndex);
//        GridCoordinate gridCoordinate = drillLog.getGridCoordinates().get(gridEntryIndex);

//        Toast.makeText(getApplicationContext(),
//                (gridCoordinate.toString()), Toast.LENGTH_SHORT).show();
    }
}
