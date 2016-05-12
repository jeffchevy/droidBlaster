package com.drillandblast.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
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
import com.drillandblast.project.ProjectKeep;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GridActivity extends BaseActivity {
    public Project project = null;
    public DrillLog drillLog = null;
    SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        TableLayout tbl = (TableLayout) findViewById(R.id.drill_log_table);

        Intent process = getIntent();

        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);
        String drillId =  process.getStringExtra("drillId");
        if (drillId == null) {
            String drillName =  process.getStringExtra("drill.name");
            drillLog = ProjectKeep.getInstance().findDrillLogByName(project, drillName);
        }
        else {
            drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);
        }

        int rowCount = 40;
        int colCount = 40;

        setTitle("Drill Log "+String.valueOf(drillLog.getName())+" Driller Name "+String.valueOf(drillLog.getDrillerName()));
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
                    final GridCoordinate gridCoordinate = new GridCoordinate(null, i, j, 0, "", drillLog.getBitSize(), new Date());
                    textView.setText("     ");

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                            toGridCoordinate.putExtra("gridCoordinate", gridCoordinate);
                            toGridCoordinate.putExtra("id", project.getId());
                            // offline drill log does not have an id
                            if (drillLog.getId() == null){
                                toGridCoordinate.putExtra("drill.name", drillLog.getName());
                            }
                            else {
                                toGridCoordinate.putExtra("drillId", drillLog.getId());
                            }
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
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            Date oneDayOld = cal.getTime();

            cal.setTime(date);
            cal.add(Calendar.DATE, -5);

            Date fiveDayOld = cal.getTime();

            final List<GridCoordinate> gridCoordinates = drillLog.getGridCoordinates();
            for(int k = 0; k<gridCoordinates.size(); k++)
            {
                TableRow tr = (TableRow) tbl.getChildAt(gridCoordinates.get(k).getRow());
                TextView tv = (TextView) tr.getChildAt(gridCoordinates.get(k).getColumn());

                final GridCoordinate gc = gridCoordinates.get(k);
                /*if(k>2) {
                    gc.setDate(fiveDayOld);
                }*/

                if(gc.getDate().before(date) && gc.getDate().after(oneDayOld)) {
                    tv.setBackgroundColor(Color.parseColor("#33c33c"));
                }
                else {
                    tv.setBackgroundColor(Color.parseColor("#ffff66"));
                }
                tv.setText(String.valueOf(gridCoordinates.get(k).getDepth()));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                        toGridCoordinate.putExtra("gridCoordinate", gc);
                        toGridCoordinate.putExtra("id", project.getId());
                        if (drillLog.getId() == null) {
                            toGridCoordinate.putExtra("drill.name", drillLog.getName());
                        }
                        else {
                            toGridCoordinate.putExtra("drillId", drillLog.getId());
                        }
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
        if (drillLog.getId() == null) {
            intent.putExtra("drill.name", drillLog.getName());
        }
        else {
            intent.putExtra("drillId", drillLog.getId());
        }
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
}
