package com.drillandblast.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
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
import com.drillandblast.model.DailyLog;
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

public class DailyLogActivity extends BaseActivity implements Validator.ValidationListener{
    Validator validator;

    private boolean isEdit = false;
    private Project project = null;
    private DailyLog dailyLog = null;
    private AsyncTask<String, String, String> asyncTask;
    SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

    @Required(order=1)
    EditText drillNumber;

    @Required(order=1)
    EditText gallonsFuel;

    @Required(order=1)
    EditText meterStart;

    @Required(order=1)
    EditText meterEnd;

    @Required(order=1)
    EditText bulkTankPumpedFrom;

    @Required(order=1)
    EditText percussionTime;

    @Required(order=1)
    TextView date;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_log);

        validator = new Validator(this);
        validator.setValidationListener(this);

        Intent process = getIntent();
        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);
        String dailyLogId = process.getStringExtra("dailyLogId");
        dailyLog = ProjectKeep.getInstance().findDailyLogById(project, dailyLogId);

        if(dailyLog != null){
            isEdit = true;
            setDailyLogData(dailyLog);
        }
        else  {
            dailyLog = new DailyLog();
            project.addDailyLog(dailyLog);
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
                // Here we might start a background refresh task

                validator.validate();
                Log.d("app", "Save clicked");

                return true;

            default:
                validator.validate();
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("id", project.getId());
                startActivity(intent);
                return true;
        }
    }
    public void backToDailyLogList(){
        Intent toDailyLogList = new Intent(DailyLogActivity.this, DailyListActivity.class);
        toDailyLogList.putExtra("id", project.getId());
        startActivity(toDailyLogList);
        finish();
    }

    public void setDate()
    {
        TextView date = (TextView) findViewById(R.id.daily_log_date_text_field);
        date.setText(format.format(dailyLog.getDate()));
    }
    public void setDailyLogData(DailyLog dailyLog){


        drillNumber = (EditText) findViewById(R.id.drill_id_text_field);

        gallonsFuel = (EditText) findViewById(R.id.gallons_fuel_text_field);
        date = (TextView) findViewById(R.id.daily_log_date_text_field);
        meterStart = (EditText) findViewById(R.id.meter_start_text_field);
        meterEnd = (EditText) findViewById(R.id.meter_end_text_field);
        bulkTankPumpedFrom = (EditText) findViewById(R.id.bulk_tank_text_field);
        percussionTime = (EditText) findViewById(R.id.percussion_time_text_field);

        String gallons_Fuel =String.valueOf(dailyLog.getGallonsFuel());

        drillNumber.setText(dailyLog.getDrillNum());
        gallonsFuel.setText(gallons_Fuel);
        date.setText(format.format(dailyLog.getDate()));
        meterStart.setText(String.valueOf(dailyLog.getMeterStart()));
        meterEnd.setText(String.valueOf(dailyLog.getMeterEnd()));
        bulkTankPumpedFrom.setText(dailyLog.getBulkTankPumpedFrom());
        percussionTime.setText(dailyLog.getPercussionTime().toString());


    }

    public String saveDailyLog(){
        String drillNumber = ((EditText)findViewById(R.id.drill_id_text_field)).getText().toString();
        String gallonsFuel = ((EditText)findViewById(R.id.gallons_fuel_text_field)).getText().toString();
        String meterStart = ((EditText) findViewById(R.id.meter_start_text_field)).getText().toString();
        String meterEnd = ((EditText) findViewById(R.id.meter_end_text_field)).getText().toString();
        String bulkTankPumpedFrom = ((EditText) findViewById(R.id.bulk_tank_text_field)).getText().toString();
        String percussionTime = ((EditText) findViewById(R.id.percussion_time_text_field)).getText().toString();

        dailyLog.setDrillNum(drillNumber);
        dailyLog.setGallonsFuel(Double.valueOf(gallonsFuel));
        dailyLog.setMeterEnd(Double.valueOf(meterEnd));
        dailyLog.setMeterStart(Double.valueOf(meterStart));
        dailyLog.setBulkTankPumpedFrom(bulkTankPumpedFrom);
        dailyLog.setPercussionTime(Double.valueOf(percussionTime));

        AsyncTaskRunner dailyLogSaveRunner = new AsyncTaskRunner();
        asyncTask = dailyLogSaveRunner.execute();
        return asyncTask.getStatus().toString();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if (isConnected()) {
                try {
                    result = ProjectSync.getInstance().updateDailyLog(isEdit, project, dailyLog);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                dailyLog.setDirty(true);
            }
            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
            }

            return result;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        FragmentManager fm = getSupportFragmentManager();
        newFragment.show(fm, "datePicker");
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar cal = Calendar.getInstance();

            if (dailyLog != null){
                if (dailyLog.getDate() != null) {
                    cal.setTime(dailyLog.getDate());
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
            dailyLog.setStartDate(new Date(c.getTimeInMillis()));// Do something with the date chosen by the user
            setDate();
        }
    }
    public void onValidationSucceeded() {
        Toast.makeText(this, "Validation Succeeded", Toast.LENGTH_SHORT).show();

        Toast.makeText(getApplicationContext(), "Saving", Toast.LENGTH_SHORT).show();
        saveDailyLog();
        backToDailyLogList();

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
