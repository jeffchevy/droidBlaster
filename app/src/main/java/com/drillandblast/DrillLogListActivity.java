package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class DrillLogListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log_list);

        //final int position = 0;

        Intent process = getIntent();
        final int position = process.getExtras().getInt("key");

        Project project = ProjectListActivity.PROJECTS.get(position);
        ArrayList<DrillLog> drillLogs = project.getDrillLogs();


        final ArrayAdapter arrayAdapter = new ArrayAdapter<DrillLog>(this, R.layout.simple_row, drillLogs);

        ListView listView = (ListView) findViewById(R.id.drill_log_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        Button newDrillLogButton = (Button) findViewById(R.id.new_drill_log_button);

        if (newDrillLogButton != null) {
            newDrillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //project.addDrillLog(new DrillLog("Tom", 3));
                    //Intent toDrillLog = new Intent(DrillLogListActivity.this, GridActivity.class);
                    //startActivity(toDrillLog);
                    //finish();
                    Intent toDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                    toDrillLog.putExtra("key", position);
                    startActivity(toDrillLog);
                    finish();
                }
            });
        }
    }
}
