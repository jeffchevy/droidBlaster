package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.json.JSONObject;

public class DailyLogActivity extends BaseActivity {
    private boolean isEdit = false;
    private Project project = null;
    private DailyLog dailyLog = null;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_log);

        Intent process = getIntent();
        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);
        String dailyLogId = process.getStringExtra("dailyLogId");
        dailyLog = ProjectKeep.getInstance().findDailyLogById(project, dailyLogId);

        if(dailyLog != null){
            isEdit = true;
            setDailLogData(dailyLog);
        }

        Button saveButton = (Button) findViewById(R.id.save_daily_log_button);

        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDailyLog();
                    backToDailyLogList();
                }
            });
        }

    }

    public void backToDailyLogList(){
        Intent toDailyLogList = new Intent(DailyLogActivity.this, DailyListActivity.class);
        toDailyLogList.putExtra("id", project.getId());

        startActivity(toDailyLogList);
        finish();
    }

    public void setDailLogData(DailyLog dailyLog){

        EditText drillNumber = (EditText) findViewById(R.id.drill_id_text_field);
        EditText gallonsFuel = (EditText) findViewById(R.id.gallons_fuel_text_field);
        EditText date = (EditText) findViewById(R.id.daily_log_date_text_field);
        EditText meterStart = (EditText) findViewById(R.id.meter_start_text_field);
        EditText meterEnd = (EditText) findViewById(R.id.meter_end_text_field);
        EditText bulkTankPumpedFrom = (EditText) findViewById(R.id.bulk_tank_text_field);
        EditText percussionTime = (EditText) findViewById(R.id.percussion_time_text_field);

        String gallons_Fuel =String.valueOf(dailyLog.getGallonsFuel());
        String dateString =String.valueOf(dailyLog.getDate());

        drillNumber.setText(dailyLog.getDrillNum());
        gallonsFuel.setText(gallons_Fuel);
        percussionTime.setText(dailyLog.getPercussionTime());
        date.setText(dateString);
        meterStart.setText(String.valueOf(dailyLog.getMeterStart()));
        meterEnd.setText(String.valueOf(dailyLog.getMeterEnd()));
        bulkTankPumpedFrom.setText(dailyLog.getBulkTankPumpedFrom());
        percussionTime.setText(dailyLog.getPercussionTime());


    }

    public String saveDailyLog(){
        String drillNumber = ((EditText)findViewById(R.id.drill_id_text_field)).getText().toString();
        String gallonsFuel = ((EditText)findViewById(R.id.gallons_fuel_text_field)).getText().toString();
        String dateString = ((EditText) findViewById(R.id.daily_log_date_text_field)).getText().toString();
        String meterStart = ((EditText) findViewById(R.id.meter_start_text_field)).getText().toString();
        String meterEnd = ((EditText) findViewById(R.id.meter_end_text_field)).getText().toString();
        String bulkTankPumpedFrom = ((EditText) findViewById(R.id.bulk_tank_text_field)).getText().toString();
        String percussionTime = ((EditText) findViewById(R.id.percussion_time_text_field)).getText().toString();

        if (!isEdit) {
            dailyLog = new DailyLog();
            project.addDailyLog(dailyLog);
        }
        dailyLog.setDrillNum(drillNumber);
        dailyLog.setGallonsFuel(Double.valueOf(gallonsFuel));
        dailyLog.setStartDate(dateString);
        dailyLog.setMeterEnd(Integer.valueOf(meterEnd));
        dailyLog.setMeterStart(Integer.valueOf(meterStart));
        dailyLog.setBulkTankPumpedFrom(bulkTankPumpedFrom);
        dailyLog.setPercussionTime(percussionTime);

        AsyncTaskRunner dailyLogSaveRunner = new AsyncTaskRunner();
        asyncTask = dailyLogSaveRunner.execute(drillNumber, gallonsFuel, bulkTankPumpedFrom, meterStart, meterEnd, percussionTime);
        return asyncTask.getStatus().toString();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if (isConnected()) {
                result = ProjectSync.getInstance().updateDailyLog(isEdit, project, dailyLog);
            }
            else {
                dailyLog.setDirty(true);
            }
            return result;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
}
