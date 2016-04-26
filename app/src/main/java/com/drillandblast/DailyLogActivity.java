package com.drillandblast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DailyLogActivity extends AppCompatActivity {
    public boolean isEdit = false;
    public int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_log);

        Intent process = getIntent();
        if(editProject(process)){
            position = process.getExtras().getInt("key");
            setDailLogData(position);
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
        startActivity(toDailyLogList);
        finish();
    }

    public boolean editProject(Intent intent){
        isEdit = false;
        if(intent.hasExtra("key")){
            isEdit = true;
        }
        return isEdit;
    }

    public void setDailLogData(int position){
        DailyLog dailyLog = DailyListActivity.dailyLogs.get(position);

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

    public DailyLog saveDailyLog(){
        String drillNumber = ((EditText)findViewById(R.id.drill_id_text_field)).getText().toString();
        String gallonsFuel = ((EditText)findViewById(R.id.gallons_fuel_text_field)).getText().toString();
        String dateString = ((EditText) findViewById(R.id.daily_log_date_text_field)).getText().toString();
        String meterStart = ((EditText) findViewById(R.id.meter_start_text_field)).getText().toString();
        String meterEnd = ((EditText) findViewById(R.id.meter_end_text_field)).getText().toString();
        String bulkTankPumpedFrom = ((EditText) findViewById(R.id.bulk_tank_text_field)).getText().toString();
        String percussionTime = ((EditText) findViewById(R.id.percussion_time_text_field)).getText().toString();

        return new DailyLog(drillNumber,
                            Double.valueOf(gallonsFuel).doubleValue(),
                            dateString,
                            Integer.valueOf(meterStart).intValue(),
                            Integer.valueOf(meterEnd).intValue(),
                            bulkTankPumpedFrom,
                            percussionTime);
    }

}
