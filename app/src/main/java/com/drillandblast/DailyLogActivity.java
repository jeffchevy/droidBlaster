package com.drillandblast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
            setProjectData(position);
        }

        Button saveButton = (Button) findViewById(R.id.save_daily_log_button);




        if (saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isEdit){
                        DailyListActivity.dailyLogs.set(position, createDailyLog());
                        backToProjectList();
                    }
                    else {
                        addProject();
                    }

                }
            });
        }

    }

    public void addProject(){
        DailyListActivity.dailyLogs.add(createDailyLog());
        backToProjectList();
        //arrayAdapter.add(new Project("Yellowstone", "Jeff", new Date(), 1));
    }

    public void backToProjectList(){
        Intent toProjectList = new Intent(DailyLogActivity.this, DailyListActivity.class);
        startActivity(toProjectList);
        finish();
    }

    public boolean editProject(Intent intent){
        isEdit = false;
        if(intent.hasExtra("key")){
            isEdit = true;
        }
        return isEdit;
    }

    public void setProjectData(int position){
        DailyLog dailyLog = DailyListActivity.dailyLogs.get(position);

        EditText drillNumber = (EditText) findViewById(R.id.drill_id_text_field);
        EditText gallonsFuel = (EditText) findViewById(R.id.gallons_fuel_text_field);
        EditText date = (EditText) findViewById(R.id.start_date_text_field);
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
        meterStart.setText(dailyLog.getMeterStart());
        meterEnd.setText(dailyLog.getMeterEnd());
        bulkTankPumpedFrom.setText(dailyLog.getBulkTankPumpedFrom());
        percussionTime.setText(dailyLog.getPercussionTime());


    }

    public DailyLog createDailyLog(){
        String drillNumber = ((EditText)findViewById(R.id.drill_id_text_field)).getText().toString();
        String gallonsFuel = ((EditText)findViewById(R.id.gallons_fuel_text_field)).getText().toString();
        String dateString = ((EditText) findViewById(R.id.daily_log_date_text_field)).getText().toString();
        String meterStart = ((EditText) findViewById(R.id.meter_start_text_field)).getText().toString();
        String meterEnd = ((EditText) findViewById(R.id.meter_end_text_field)).getText().toString();
        String bulkTankPumpedFrom = ((EditText) findViewById(R.id.bulk_tank_text_field)).getText().toString();
        String percussionTime = ((EditText) findViewById(R.id.percussion_time_text_field)).getText().toString();

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


        Date date = new Date();
        try{
            date = formatter.parse(dateString);
        }
        catch(Exception e){

        }

        return new DailyLog(Integer.valueOf(drillNumber).intValue(),
                            Double.valueOf(gallonsFuel).doubleValue(),
                            date,
                            Integer.valueOf(meterStart).intValue(),
                            Integer.valueOf(meterEnd).intValue(),
                            bulkTankPumpedFrom,
                            percussionTime);
    }

}
