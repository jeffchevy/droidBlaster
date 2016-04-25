package com.drillandblast;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);



        TableLayout tbl = (TableLayout) findViewById(R.id.drill_log_table);
        //final ArrayList<GridCoordinate> gridCoordinates = new ArrayList<GridCoordinate>();
        //TableLayout.LayoutParams tblparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);


        Intent process = getIntent();

        final int projectPosition = process.getExtras().getInt("project");
        Project project = ProjectListActivity.projects.get(projectPosition);

        final int drillLogPosition = process.getExtras().getInt("drillLogPosition");
        DrillLog drillLog = project.getDrillLog(drillLogPosition);



        int rowCount = 4;
        int colCount = 4;
        int rowNumber = 0;

        for(int i=0; i<rowCount;i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


            for(int j=0; j<colCount; j++) {
                final GridCoordinate gridCoordinate = new GridCoordinate(i, j, 0, "", 0);
                //gridCoordinate.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                //row.addView(gridCoordinate);
                TextView textView = new TextView(this);
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                textView.setText(gridCoordinate.toString());
                row.addView(textView);
                //gridCoordinates.add(gridCoordinate);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                        toGridCoordinate.putExtra("gridCoordinate", gridCoordinate);
                        toGridCoordinate.putExtra("project", projectPosition);
                        toGridCoordinate.putExtra("drillLog", drillLogPosition);
                        startActivity(toGridCoordinate);
                        finish();
                    }
                });
            }

            rowNumber++;
            tbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        /*if(process.hasExtra("gridCoordinate"))
        {
            GridCoordinate gridCoordinate = (GridCoordinate) process.getSerializableExtra("gridCoordinate");
            TableRow tr = (TableRow) tbl.getChildAt(gridCoordinate.getRow());
            TextView tv = (TextView) tr.getChildAt(gridCoordinate.getColumn());

            tv.setText(String.valueOf(gridCoordinate.getDepth()));
        }*/

        if(project.getDrillLog(drillLogPosition).getGridCoordinates().size()>0)
        {
            final ArrayList<GridCoordinate> gridCoordinates = drillLog.getGridCoordinates();
            for(int k = 0; k<gridCoordinates.size(); k++)
            {
                TableRow tr = (TableRow) tbl.getChildAt(gridCoordinates.get(k).getRow());
                TextView tv = (TextView) tr.getChildAt(gridCoordinates.get(k).getColumn());

                tv.setText(String.valueOf(gridCoordinates.get(k).getDepth()));

                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                        //toGridCoordinate.putExtra("gridCoordinate", gridCoordinates.get(k));
                        toGridCoordinate.putExtra("project", projectPosition);
                        toGridCoordinate.putExtra("drillLog", drillLogPosition);
                        startActivity(toGridCoordinate);
                        finish();
                    }
                });
            }
        }

    }
}
