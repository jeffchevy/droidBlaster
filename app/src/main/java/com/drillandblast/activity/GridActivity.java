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
import java.util.HashMap;
import java.util.List;

public class GridActivity extends BaseActivity {
    public Project project = null;
    public DrillLog drillLog = null;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
    HashMap dayColor = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dayColor.put(1, "#33cc33");
        dayColor.put(2, "#ff0000");
        dayColor.put(3, "#0066ff");
        dayColor.put(4, "#ff9900");
        dayColor.put(5, "#ffff00");
        dayColor.put(6, "#cc33ff");
        dayColor.put(7, "#9966ff");
        dayColor.put(8, "#33cc33");
        dayColor.put(9, "#ff0000");
        dayColor.put(10, "#0066ff");
        dayColor.put(11, "#ff9900");
        dayColor.put(12, "#ffff00");
        dayColor.put(13, "#cc33ff");
        dayColor.put(14, "#9966ff");
        dayColor.put(15, "#33cc33");
        dayColor.put(16, "#ff0000");
        dayColor.put(17, "#0066ff");
        dayColor.put(18, "#ff9900");
        dayColor.put(19, "#ffff00");
        dayColor.put(20, "#cc33ff");
        dayColor.put(21, "#9966ff");
        dayColor.put(22, "#33cc33");
        dayColor.put(23, "#ff0000");
        dayColor.put(24, "#0066ff");
        dayColor.put(25, "#ff9900");
        dayColor.put(26, "#ffff00");
        dayColor.put(27, "#cc33ff");
        dayColor.put(28, "#9966ff");
        dayColor.put(29, "#ff9900");
        dayColor.put(30, "#ffff00");
        dayColor.put(31, "#cc33ff");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        TableLayout tbl = (TableLayout) findViewById(R.id.drill_log_table);

        Intent process = getIntent();

        String id =  process.getStringExtra("id");
        boolean editable = process.getBooleanExtra("editable", true);
        project = ProjectKeep.getInstance().findById(id);
        String drillId =  process.getStringExtra("drillId");
        if (drillId == null) {
            String drillName =  process.getStringExtra("drill.name");
            drillLog = ProjectKeep.getInstance().findDrillLogByName(project, drillName);
        }
        else {
            drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);
        }

        int rowCount = 50;
        int colCount = 30;

        setTitle(String.valueOf(drillLog.getName())+" - "+String.valueOf(drillLog.getDrillerName()));
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
                    final GridCoordinate gridCoordinate = new GridCoordinate(null, i, j, 0, "", drillLog.getBitSize(), new Date(), false);
                    textView.setText("     ");

                    if (editable) {
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
                }
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                row.addView(textView);
            }

            tbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        if(drillLog.getGridCoordinates().size()>0)
        {
            Date date = new Date();

            final List<GridCoordinate> gridCoordinates = drillLog.getGridCoordinates();
            for(int k = 0; k<gridCoordinates.size(); k++)
            {
                TableRow tr = (TableRow) tbl.getChildAt(gridCoordinates.get(k).getRow());
                TextView tv = (TextView) tr.getChildAt(gridCoordinates.get(k).getColumn());

                final GridCoordinate gc = gridCoordinates.get(k);
                Calendar cal = Calendar.getInstance();
                cal.setTime(gc.getDate());
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                if(dayColor.containsKey(day) && gc.getIsDrilled()) {
                    tv.setBackgroundColor(Color.parseColor(dayColor.get(day).toString()));
                }

                tv.setText(String.valueOf(gridCoordinates.get(k).getDepth()));
                if (editable) {
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toGridCoordinate = new Intent(GridActivity.this, GridCoordinateActivity.class);
                            toGridCoordinate.putExtra("gridCoordinate", gc);
                            toGridCoordinate.putExtra("id", project.getId());
                            if (drillLog.getId() == null) {
                                toGridCoordinate.putExtra("drill.name", drillLog.getName());
                            } else {
                                toGridCoordinate.putExtra("drillId", drillLog.getId());
                            }
                            startActivity(toGridCoordinate);
                            finish();
                        }
                    });
                }
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
