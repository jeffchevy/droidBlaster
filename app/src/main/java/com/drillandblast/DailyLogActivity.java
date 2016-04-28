package com.drillandblast;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.drillandblast.http.SimpleHttpClient;

import org.json.JSONObject;

public class DailyLogActivity extends AppCompatActivity {
    public boolean isEdit = false;
    public int position = 0;
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
        project = (Project) process.getSerializableExtra("project");

        if(editDailyLog(process)){
            dailyLog = (DailyLog) process.getSerializableExtra("dailyLog");
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
        toDailyLogList.putExtra("project", project);

        startActivity(toDailyLogList);
        finish();
    }

    public boolean editDailyLog(Intent intent){
        isEdit = false;
        if(intent.hasExtra("dailyLog")){
            isEdit = true;
        }
        return isEdit;
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

                result = SimpleHttpClient.executeHttpPut("http://192.168.1.16:1337/api/v1/dailyLogs/"+project.getId()+"/"+dailyLog.getId(), json, token);

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }
    }


}
