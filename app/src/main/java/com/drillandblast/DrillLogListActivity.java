package com.drillandblast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DrillLogListActivity extends AppCompatActivity {

    public Project project = null;
    public List<DrillLog> drillLogs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log_list);

        Intent process = getIntent();

        project = (Project) process.getSerializableExtra("project");
        drillLogs = project.getDrillLogs();

        if(drillLogs == null || drillLogs.size() < 1) {
            drillLogs = new ArrayList<DrillLog>();
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter<DrillLog>(this, R.layout.simple_row, drillLogs);

        ListView listView = (ListView) findViewById(R.id.drill_log_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DrillLog drillLog = drillLogs.get(position);
                Intent editDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                editDrillLog.putExtra("drillLog", drillLog);
                editDrillLog.putExtra("project", project);
                startActivity(editDrillLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Button newDrillLogButton = (Button) findViewById(R.id.new_drill_log_button);

        if (newDrillLogButton != null) {
            newDrillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                    toDrillLog.putExtra("project", project);
                    startActivity(toDrillLog);
                    finish();
                }
            });
        }
    }
}
