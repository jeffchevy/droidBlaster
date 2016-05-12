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
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectKeep;

public class DailyListActivity extends BaseActivity {
    private static final String TAG = "DailyListActivity";
    private ArrayAdapter arrayAdapter = null;
    private Project project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);

        Intent process = getIntent();
        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);

        arrayAdapter = new ArrayAdapter<DailyLog>(this, R.layout.simple_row, project.getDailyLogs());

        ListView listView = (ListView) findViewById(R.id.daily_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DailyLog dailyLog = project.getDailyLogs().get(position);
                Intent editDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
                editDailyLog.putExtra("dailyLogId", dailyLog.getId());
                editDailyLog.putExtra("id", project.getId());
                startActivity(editDailyLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.new_daily_log_button);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addNewDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
                    addNewDailyLog.putExtra("id", project.getId());
                    startActivity(addNewDailyLog);
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
