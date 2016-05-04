package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.model.ProjectKeep;

import org.json.JSONObject;

import java.util.List;

public class GridCoordinateActivity extends AppCompatActivity {
    private boolean isEdit = false;
    public String token = null;
    public DrillLog drillLog = null;
    public Project project = null;
    public GridCoordinate gridCoordinate = null;
    private AsyncTask<String, String, String> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_coordinate);

        Intent process = getIntent();
        gridCoordinate = (GridCoordinate) process.getSerializableExtra("gridCoordinate");
        if (gridCoordinate.getId() != null)
        {
            isEdit = true;
        }
        String id =  process.getStringExtra("id");
        String drillId =  process.getStringExtra("drillId");
        project = ProjectKeep.getInstance().findById(id);
        drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);
        token = process.getStringExtra("token");

        TextView depth = (TextView) findViewById(R.id.depth_text_field);
        depth.setText(String.valueOf(gridCoordinate.getDepth()));
        TextView bitSize = (TextView) findViewById(R.id.bit_size_text_field);
        bitSize.setText(String.valueOf(gridCoordinate.getBitSize()));
        TextView comment = (TextView) findViewById(R.id.comment_text_field);
        comment.setText(gridCoordinate.getComment().toString());

        Button saveButton = (Button) findViewById(R.id.save_coordinate_button);

        if(saveButton !=null) {
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDrillCoordinateData(drillLog,gridCoordinate);
                }
            });
        }
    }

    public String saveDrillCoordinateData(DrillLog drillLog, GridCoordinate gridCoordinate)
    {
        TextView depth = (TextView) findViewById(R.id.depth_text_field);
        TextView bitSize = (TextView) findViewById(R.id.bit_size_text_field);
        TextView comment = (TextView) findViewById(R.id.comment_text_field);

        gridCoordinate.setDepth(Double.parseDouble(depth.getText().toString()));
        gridCoordinate.setBitSize(Double.parseDouble(bitSize.getText().toString()));
        gridCoordinate.setComment(comment.getText().toString());

        AsyncTaskRunner holeTaskRunner = new AsyncTaskRunner();
        asyncTask = holeTaskRunner.execute();

        if (isEdit){
            // find the grid in the list and update it.  normally we could just update the object
            // but because we are serializing the object the reference is lost
            List<GridCoordinate> grids = drillLog.getGridCoordinates();
            for(int i=0; i<grids.size(); i++)
            {
                GridCoordinate gc = (GridCoordinate) grids.get(i);
                if (gc.getId().equalsIgnoreCase(gridCoordinate.getId()))
                {
                    drillLog.getGridCoordinates().set(i, gridCoordinate);
                }
            }
        }
        else
        {
            drillLog.getGridCoordinates().add(gridCoordinate);
        }
        return asyncTask.getStatus().toString();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra("project", project);
        intent.putExtra("drillLog", drillLog);
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String result = null;

            JSONObject json = new JSONObject();
            String response = null;
            try {
                json.put("x", gridCoordinate.getRow());
                json.put("y", gridCoordinate.getColumn());
                json.put("z", gridCoordinate.getDepth());
                json.put("comments", gridCoordinate.getComment());
                json.put("bitSize", gridCoordinate.getBitSize());

                if (isEdit)
                {
                    result = SimpleHttpClient.executeHttpPut("holes/"+project.getId()+"/"+drillLog.getId()+"/"+gridCoordinate.getId(), json, token);
                }
                else
                {
                    result = SimpleHttpClient.executeHttpPost("drillLogs/"+project.getId()+"/"+drillLog.getId(), json, token);
                    JSONObject jsonobject = new JSONObject(result);
                    String id = jsonobject.getString("id");
                    gridCoordinate.setId(id);
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            return result;
        }
        protected void onPostExecute(String result) {
            Intent toDrillLogCoordinates = new Intent(GridCoordinateActivity.this, GridActivity.class);
            toDrillLogCoordinates.putExtra("drillId", drillLog.getId());
            toDrillLogCoordinates.putExtra("id", project.getId());
            startActivity(toDrillLogCoordinates);
            finish();
        }

    }

}
