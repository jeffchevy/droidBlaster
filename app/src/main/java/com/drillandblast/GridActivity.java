package com.drillandblast;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class GridActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        TableLayout tbl = (TableLayout) findViewById(R.id.drill_log_table);
        //TableLayout.LayoutParams tblparams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);

        Intent process = getIntent();

        final int projectPosition = process.getExtras().getInt("project");
        Project project = ProjectListActivity.projects.get(projectPosition);

        DrillLog drillLog = new DrillLog(null, 0, new Date(), new ArrayList<GridCoordinate>());

        int rowCount = 200;
        int colCount = 240;
        int rowNumber = 0;

        for(int i=0; i<rowCount;i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


            for(int j=0; j<colCount; j++) {
                //GridCoordinate gridCoordinate = new GridCoordinate(i, j, 0, "", 2);
                //gridCoordinate.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                //row.addView(gridCoordinate);
                TextView textView = new TextView(this);
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                textView.setText("0");
                row.addView(textView);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                        //toDrillLogList.putExtra("x", rowNumber);
                        startActivity(toGridCoordinate);
                        finish();
                    }
                });
            }

            rowNumber++;
            tbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }
}
