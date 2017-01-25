package com.drillandblast.activity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.drillandblast.R;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.ExcelField;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.model.UpdateStatus;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;
import com.google.gson.Gson;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zachary on 1/19/2017.
 */
public class ManagerGridActivity extends BaseActivity {
    public Project project = null;
    public DrillLog drillLog = null;
    public HashMap coordinates = null;
    public List<GridCoordinate> gridCoordinates = null;
    private AsyncTask<String, String, UpdateStatus> asyncTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_grid);

        gridCoordinates = new ArrayList<GridCoordinate>();

        final TableLayout tbl = (TableLayout) findViewById(R.id.manager_drill_log_table);

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

        coordinates = new HashMap();

        TableLayout.LayoutParams tableRowParams=
                new TableLayout.LayoutParams
                        (TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);

        for(int i=0; i<rowCount;i++) {
            final TableRow row = new TableRow(this);

            row.setLayoutParams(tableRowParams);
            for(int j=0; j<colCount; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(i);
                sb.append(j);
                final ExcelField editText = new ExcelField(this, sb.toString(), 0, i, j);
                int sbId = Integer.parseInt(sb.toString());


                TextView textView = new TextView(this);
                TableRow.LayoutParams tableCellParams = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                View v = null;

                if(j==0 && i==0) {
                    textView.setText("    ");
                    v = textView;
                }
                else if(j==0) {
                    textView.setText(String.valueOf(i));
                    v = textView;
                }
                else if(i==0) {
                    textView.setText(String.valueOf(j));
                    v = textView;
                }
                else {
                    editText.setText("");
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String idToSearchFor = editText.getExcelId();
                            editText.setDepth(Double.parseDouble(editText.getText().toString()));
                            if(coordinates.containsKey(Integer.parseInt(idToSearchFor))){
                                coordinates.put(editText.getExcelId(), editText);
                                System.out.println(editText.toString());
                            }
                            else {
                                coordinates.put(editText.getExcelId(), editText);
                                System.out.println(editText.toString());
                            }
                        }
                    });
                    v = editText;
                }
                v.setPadding(8,4,8,4);
                textView.setGravity(Gravity.CENTER);
                row.addView(v);
            }

            tbl.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

        if(drillLog != null) {
            if(drillLog.getManagerGridCoordinates() != null && drillLog.getManagerGridCoordinates().size()>0) {
                Collection<ExcelField> excelFields = drillLog.getManagerGridCoordinates().values();
                for(ExcelField ef : excelFields) {
                    TableRow tr = (TableRow) tbl.getChildAt(ef.getRow());
                    if(tr.getChildAt(ef.getColumn())instanceof ExcelField) {
                        ExcelField existingExcelField = (ExcelField) tr.getChildAt(ef.getColumn());
                        existingExcelField.setText(String.valueOf(ef.getDepth()));
                    }
                }
            }
            if(drillLog.getGridCoordinates() !=null && drillLog.getGridCoordinates().size() > 0) {
                final List<GridCoordinate> gridCoordinates = drillLog.getGridCoordinates();
                for(int k = 0; k<gridCoordinates.size(); k++)
                {
                    TableRow tr = (TableRow) tbl.getChildAt(gridCoordinates.get(k).getRow());

                    if(tr.getChildAt(gridCoordinates.get(k).getColumn())instanceof ExcelField) {
                        ExcelField existingExcelField = (ExcelField) tr.getChildAt(gridCoordinates.get(k).getColumn());
                        existingExcelField.setText(String.valueOf(gridCoordinates.get(k).getDepth()));
                    }
                }
            }
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }

    /**
     * This method is called when one of the menu items to selected. These items
     * can be on the Action Bar, the overflow menu, or the standard options menu. You
     * should return true if you handle the selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                if(drillLog.getId() != null) {
                    saveManagerGrid();
                    return true;
                }
            default:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("id", project.getId());
                if (drillLog.getId() == null) {
                    intent.putExtra("drill.name", drillLog.getName());
                }
                else {
                    intent.putExtra("drillId", drillLog.getId());
                }
                startActivity(intent);
                return true;
        }
    }

    public String saveManagerGrid(){
        drillLog.setManagerGridCoordinates(coordinates);
        Collection<ExcelField> managerEnteredCoordinates = coordinates.values();
        for(ExcelField ef : managerEnteredCoordinates) {
            if(ef.getDepth() > 0) {
                GridCoordinate gc = new GridCoordinate(null, ef.getRow(), ef.getColumn(), ef.getDepth(), "", null, new Date(), false);
                gridCoordinates.add(gc);
            }
        }
        drillLog.setGridCoordinates(gridCoordinates);
        AsyncTaskRunner holeTaskRunner = new AsyncTaskRunner();
        asyncTask = holeTaskRunner.execute();
        return asyncTask.getStatus().toString();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, UpdateStatus> {
        Gson gson = new Gson();

        @Override
        protected UpdateStatus doInBackground(String... params) {
            UpdateStatus status = null;
            if (isConnected()) {
                List<GridCoordinate> savedGridCoordinates = drillLog.getGridCoordinates();
                for(int i = 0; i<savedGridCoordinates.size(); i++) {
                    GridCoordinate gridCoordinate = savedGridCoordinates.get(i);
                    if(isConnected()){
                        try {
                            String result = ProjectSync.getInstance().updateDrillCoordinate(false, project, drillLog, gridCoordinate);
                            status = gson.fromJson(result, UpdateStatus.class);
                        } catch (Exception e) {
                            status = new UpdateStatus(false, e.getMessage());
                            gridCoordinate.setDirty(true);
                            e.printStackTrace();
                        }
                    }
                    else {
                        gridCoordinate.setDirty(true);
                        status = new UpdateStatus(true, "Saving offline");
                    }
                    if (gridCoordinate.isDirty() && project.getId() != null ) {
                        ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), true);
                    }

                    if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                        ProjectKeep.getInstance().saveProjectToFile(project);
                    }
                }
            }
            return status;
        }
        protected void onPostExecute(UpdateStatus status) {
            Toast.makeText(getApplicationContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
            if (status.getSuccess()) {
                nextActivity();
            }
        }

    }
    public void nextActivity() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("id", project.getId());
        if (drillLog.getId() == null) {
            intent.putExtra("drill.name", drillLog.getName());
        }
        else {
            intent.putExtra("drillId", drillLog.getId());
        }
        startActivity(intent);
    }
}
