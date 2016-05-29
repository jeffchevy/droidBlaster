package com.drillandblast.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GridCoordinateActivity extends BaseActivity implements Validator.ValidationListener{
    private Validator validator = null;
    private boolean isEdit = false;
    public DrillLog drillLog = null;
    public Project project = null;
    public GridCoordinate gridCoordinate = null;
    private AsyncTask<String, String, String> asyncTask;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

    @Required(order=1)
    TextView depth = null;
    @Required(order=2)
    TextView bitSize = null;
    TextView comment = null;
    @Required(order=3)
    TextView date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_grid_coordinate);
        validator = new Validator(this);
        validator.setValidationListener(this);

        Intent process = getIntent();
        gridCoordinate = (GridCoordinate) process.getSerializableExtra("gridCoordinate");
        if (gridCoordinate.getId() != null || gridCoordinate.isDirty() == true)
        {
            isEdit = true;
        }
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
        getAndroidFields();
        setTitle("Drill Hole Row "+String.valueOf(gridCoordinate.getRow())+" Column "+String.valueOf(gridCoordinate.getColumn()));
        if(gridCoordinate.getDepth() != 0) {
            depth.setText(String.valueOf(gridCoordinate.getDepth()));
        }
        else {
            depth.setText("");
        }
        bitSize.setText(String.valueOf(gridCoordinate.getBitSize()));
        comment.setText(gridCoordinate.getComment().toString());
        date.setText(format.format(gridCoordinate.getDate()));
    }

    public String saveDrillCoordinateData(DrillLog drillLog, GridCoordinate gridCoordinate)
    {
        gridCoordinate.setDepth(Double.parseDouble(depth.getText().toString()));
        gridCoordinate.setBitSize(bitSize.getText().toString());
        gridCoordinate.setComment(comment.getText().toString());

        AsyncTaskRunner holeTaskRunner = new AsyncTaskRunner();
        asyncTask = holeTaskRunner.execute();

        if (isEdit){
            // find the grid in the list and update it.  normally we could just update the object
            // but because we are serializiOffng the object the reference is lost
            List<GridCoordinate> grids = drillLog.getGridCoordinates();
            for(int i=0; i<grids.size(); i++)
            {
                GridCoordinate gc = (GridCoordinate) grids.get(i);
                // we will have the case where the id of the coordinate is not know
                // until we sync with the server.  in the mean time we will update it by the coordinates
                if (gridCoordinate.getId() == null) {
                    if (gc.getRow() == gridCoordinate.getRow() && gc.getColumn() == gridCoordinate.getColumn()) {
                        drillLog.getGridCoordinates().set(i, gridCoordinate);
                    }
                }
                else {
                    if (gc.getId().equalsIgnoreCase(gridCoordinate.getId()))
                    {
                        drillLog.getGridCoordinates().set(i, gridCoordinate);
                    }
                }
            }
        }
        else
        {
            drillLog.getGridCoordinates().add(gridCoordinate);
        }
        return asyncTask.getStatus().toString();

    }

    private void getAndroidFields() {
        depth = (TextView) findViewById(R.id.depth_text_field);
        bitSize = (TextView) findViewById(R.id.bit_size_text_field);
        comment = (TextView) findViewById(R.id.comment_text_field);
        date = (TextView) findViewById(R.id.drill_coordinate_date_text_field);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.save, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                // Here we might start a background refresh task
                Log.d("app", "Save clicked");
                validator.validate();
                return true;

            default:
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fm = getSupportFragmentManager();
        newFragment.show(fm, "datePicker");
    }

    public void setDate()
    {
        TextView date = (TextView) findViewById(R.id.drill_coordinate_date_text_field);
        date.setText(format.format(gridCoordinate.getDate()));
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar cal = Calendar.getInstance();

            if (gridCoordinate != null){
                if (gridCoordinate.getDate() != null) {
                    cal.setTime(gridCoordinate.getDate());
                }
            }
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            gridCoordinate.setDate(new Date(c.getTimeInMillis()));// Do something with the date chosen by the user
            setDate();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String result = null;
            if (isConnected()) {
                try {
                    result = ProjectSync.getInstance().updateDrillCoordinate(isEdit, project, drillLog, gridCoordinate);
                } catch (Exception e) {
                    ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), true);
                    ProjectKeep.getInstance().saveProjectToFile(project);
                    e.printStackTrace();
                }
            }
            else {
                gridCoordinate.setDirty(true);
            }
            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
            }
            return result;
        }
        protected void onPostExecute(String result) {
            String message = getResultMessage(result);
            if (isUpdateSuccessful(result)) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (result != null) {
                    nextActivity();
                }
            }
            else {
                String text = (message == null) ? "Error Saving" : message;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }

    }
    public void nextActivity() {
        Intent toDrillLogCoordinates = new Intent(GridCoordinateActivity.this, GridActivity.class);
        toDrillLogCoordinates.putExtra("id", project.getId());
        if (drillLog.getId() == null) {
            toDrillLogCoordinates.putExtra("drill.name", drillLog.getName());
        }
        else{
            toDrillLogCoordinates.putExtra("drillId", drillLog.getId());
        }
        startActivity(toDrillLogCoordinates);
        finish();
    }

    public void onValidationSucceeded() {
        Toast.makeText(getApplicationContext(), "Saving", Toast.LENGTH_SHORT).show();
        saveDrillCoordinateData(drillLog,gridCoordinate);
    }

    public void onValidationFailed(View view, Rule<?> rule) {

        final String failureMessage = rule.getFailureMessage();
        if (view instanceof EditText) {
            EditText failed = (EditText) view;
            failed.requestFocus();
            failed.setError(failureMessage);
        } else {
            Toast.makeText(getApplicationContext(), failureMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
