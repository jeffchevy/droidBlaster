package com.drillandblast.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.drillandblast.R;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.model.ProjectKeep;

import java.util.List;

public class GridActivity extends AppCompatActivity {
    public Project project = null;
    public DrillLog drillLog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        TableLayout tbl = (TableLayout) findViewById(R.id.drill_log_table);

        Intent process = getIntent();

        String id =  process.getStringExtra("id");
        String drillId =  process.getStringExtra("drillId");
        project = ProjectKeep.getInstance().findById(id);
        drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);

        int rowCount = 40;
        int colCount = 40;


        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        for(int i=0; i<rowCount;i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(tableRowParams);
            for(int j=0; j<colCount; j++) {
                TextView textView = new TextView(this);
                TableRow.LayoutParams tableCellParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                textView.setPadding(8,4,8,4);
                textView.setGravity(Gravity.CENTER);

                if(j==0 && i==0) {
                    textView.setText("    ");
                    textView.setBackgroundColor(Color.parseColor("#cccccc"));
                }
                else if(j==0) {
                    textView.setText(String.valueOf(i));
                    textView.setBackgroundColor(Color.parseColor("#cccccc"));
                }
                else if(i==0) {
                    textView.setText(String.valueOf(j));
                    textView.setBackgroundColor(Color.parseColor("#cccccc"));
                }
                else {
                    final GridCoordinate gridCoordinate = new GridCoordinate(null, i, j, 0, "", 0);
                    textView.setText("     ");

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                            toGridCoordinate.putExtra("gridCoordinate", gridCoordinate);
                            toGridCoordinate.putExtra("id", project.getId());
                            toGridCoordinate.putExtra("drillLogId", drillLog.getId());
                            startActivity(toGridCoordinate);
                            finish();
                        }
                    });
                }
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                row.addView(textView);
            }

            tbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        if(drillLog.getGridCoordinates().size()>0)
        {
            final List<GridCoordinate> gridCoordinates = drillLog.getGridCoordinates();
            for(int k = 0; k<gridCoordinates.size(); k++)
            {
                TableRow tr = (TableRow) tbl.getChildAt(gridCoordinates.get(k).getRow());
                TextView tv = (TextView) tr.getChildAt(gridCoordinates.get(k).getColumn());

                tv.setText(String.valueOf(gridCoordinates.get(k).getDepth()));
                final GridCoordinate gc = gridCoordinates.get(k);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                        toGridCoordinate.putExtra("gridCoordinate", gc);
                        toGridCoordinate.putExtra("id", project.getId());
                        toGridCoordinate.putExtra("drillId", drillLog.getId());
                        startActivity(toGridCoordinate);
                        finish();
                    }
                });
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("id", project.getId());
        intent.putExtra("drillId", drillLog.getId());
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
}
