package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.Project;
import com.drillandblast.model.ProjectKeep;

import org.json.JSONObject;

public class DailyLogActivity extends AppCompatActivity {
    private boolean isEdit = false;
    public String token = null;
    private Project project = null;
    private DailyLog dailyLog = null;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_log);

        Intent process = getIntent();
        token = process.getStringExtra("token");
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
        toDailyLogList.putExtra("token", token);
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

            JSONObject json = new JSONObject();
            String response = null;
            try {
                json.put("drillNumber", params[0]);
                json.put("gallonsPumped", params[1]);
                json.put("bulkTankPumpedFrom", params[2]);
                json.put("hourMeterStart", params[3]);
                json.put("hourMeterEnd", params[4]);
                json.put("percussionTime", params[5]);

                if (isEdit)
                {
                    result = SimpleHttpClient.executeHttpPut("dailyLogs/"+project.getId()+"/"+dailyLog.getId(), json, token);
                }
                else
                {
                    result = SimpleHttpClient.executeHttpPost("dailyLogs/"+project.getId(), json, token);
                    JSONObject jsonobject = new JSONObject(result);
                    String id = jsonobject.getString("id");
                    dailyLog.setId(id);
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
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
