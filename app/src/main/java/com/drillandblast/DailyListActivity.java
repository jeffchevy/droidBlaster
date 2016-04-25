package com.drillandblast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DailyListActivity extends AppCompatActivity {
    static List<DailyLog> dailyLogs = new ArrayList<>();
    private ArrayAdapter arrayAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);

        arrayAdapter = new ArrayAdapter<DailyLog>(this, R.layout.simple_row, dailyLogs);

        ListView listView = (ListView) findViewById(R.id.project_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setTextFilterEnabled(true);

        //when we click on an item in the list, open to view/edit it
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                DailyLog dailyLog = dailyLogs.get(position);
                Intent editDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
                editDailyLog.putExtra("key", position);
                startActivity(editDailyLog);
                finish();
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        Button button = (Button) findViewById(R.id.new_daily_log_button);

        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewDailyLog();
                    //arrayAdapter.add(new Project("Yellowstone", "Jeff", new Date(), 1));
                }
            });
        }

    }
    public void createNewDailyLog(){
        Intent addNewDailyLog = new Intent(DailyListActivity.this, DailyLogActivity.class);
        startActivity(addNewDailyLog);
        finish();
    }

}
