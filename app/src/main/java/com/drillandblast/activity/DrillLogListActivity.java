package com.drillandblast.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectKeep;

public class DrillLogListActivity extends BaseActivity {
    private static final String TAG = "DrillLogListActivity";
    private ArrayAdapter arrayAdapter = null;
    private Project project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log_list);

        Intent process = getIntent();

        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);

        arrayAdapter = new ArrayAdapter<DrillLog>(this, R.layout.simple_row, project.getDrillLogs());

        ListView listView = (ListView) findViewById(R.id.drill_log_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DrillLog drillLog = project.getDrillLogs().get(position);
                Intent editDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                if (drillLog.getId() == null)
                {
                    editDrillLog.putExtra("drill.name", drillLog.getName());
                }
                else {
                    editDrillLog.putExtra("drillId", drillLog.getId());
                }
                editDrillLog.putExtra("id", project.getId());
                startActivity(editDrillLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton newDrillLogButton = (FloatingActionButton) findViewById(R.id.new_drill_log_button);

        if (newDrillLogButton != null) {
            newDrillLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toDrillLog = new Intent(DrillLogListActivity.this, DrillLogActivity.class);
                    toDrillLog.putExtra("id", project.getId());
                    startActivity(toDrillLog);
                    finish();
                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("id", project.getId());
        startActivity(intent);
        return true;
    }
}
