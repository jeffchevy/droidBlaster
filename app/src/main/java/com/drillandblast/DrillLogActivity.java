package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DrillLogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log);

        Intent process = getIntent();
        final int position = process.getExtras().getInt("key");



        Button saveButton = (Button) findViewById(R.id.save_drill_log_button);

        if(saveButton != null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDrillLogData(position);
                    Intent toDrillLogList = new Intent(DrillLogActivity.this, DrillLogListActivity.class);
                    toDrillLogList.putExtra("key", position);
                    startActivity(toDrillLogList);
                    finish();
                }
            });
        }
    }

    public void saveDrillLogData(int position)
    {
        Project project = ProjectListActivity.PROJECTS.get(position);

        EditText driller_name = (EditText) findViewById(R.id.driller_name_text_field);
        EditText drill_number = (EditText) findViewById(R.id.drill_number_text_field);
        EditText drill_date = (EditText) findViewById(R.id.drill_date_text_field);
        //EditText shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        //EditText bit_Size = (EditText) findViewById(R.id.bit_size_text_field);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String drillerName = driller_name.getText().toString();
        String drillNumber = drill_number.getText().toString();
        double drillId = Double.parseDouble(drillNumber);
        String date = drill_date.getText().toString();
        Date drillDate = new Date();
        try{
            drillDate = formatter.parse(date);
        }
        catch(Exception e){

        }
        //String shotNumber = String.valueOf(project.getShotNumber());
        //String bitSize =String.valueOf(project.getBitSize());
        DrillLog drillLog = new DrillLog(drillerName, drillId, drillDate);
        project.addDrillLog(drillLog);
    }
}
