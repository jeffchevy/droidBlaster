package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GridCoordinateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_coordinate);

        Intent process = getIntent();
        final GridCoordinate gridCoordinate = (GridCoordinate) process.getSerializableExtra("gridCoordinate");
        final int projectIndex = process.getExtras().getInt("project");
        final int drillLogIndex = process.getExtras().getInt("drillLog");

        final Project project = ProjectListActivity.projects.get(projectIndex);
        final DrillLog drillLog = project.getDrillLog(drillLogIndex);

        TextView depth = (TextView) findViewById(R.id.depth_text_field);
        depth.setText(String.valueOf(gridCoordinate.getDepth()));
        TextView bitSize = (TextView) findViewById(R.id.bit_size_text_field);
        bitSize.setText(String.valueOf(gridCoordinate.getBitSize()));
        TextView comment = (TextView) findViewById(R.id.comment_text_field);
        depth.setText(gridCoordinate.getComment());

        Button saveButton = (Button) findViewById(R.id.save_coordinate_button);

        if(saveButton !=null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDrillCoordinateData(drillLog,gridCoordinate);
                    Intent toDrillLogCoordinates = new Intent(GridCoordinateActivity.this, GridActivity.class);
                    toDrillLogCoordinates.putExtra("project", projectIndex);
                    toDrillLogCoordinates.putExtra("drillLog", drillLogIndex);
                    toDrillLogCoordinates.putExtra("gridCoordinate", gridCoordinate);
                    startActivity(toDrillLogCoordinates);
                    finish();
                }
            });
        }
    }

    public void saveDrillCoordinateData(DrillLog drillLog, GridCoordinate gridCoordinate)
    {
        TextView depth = (TextView) findViewById(R.id.depth_text_field);
        TextView bitSize = (TextView) findViewById(R.id.bit_size_text_field);
        TextView comment = (TextView) findViewById(R.id.comment_text_field);

        gridCoordinate.setDepth(Double.parseDouble(depth.getText().toString()));
        gridCoordinate.setBitSize(Double.parseDouble(depth.getText().toString()));
        gridCoordinate.setComment(comment.toString());

        drillLog.getGridCoordinates().add(gridCoordinate);

    }
}