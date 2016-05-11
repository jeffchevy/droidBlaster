package com.drillandblast.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
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
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.json.JSONObject;

import java.util.List;

public class GridCoordinateActivity extends BaseActivity {
    private boolean isEdit = false;
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
        project = ProjectKeep.getInstance().findById(id);
        String drillId =  process.getStringExtra("drillId");
        if (drillId == null) {
            String drillName =  process.getStringExtra("drill.name");
            drillLog = ProjectKeep.getInstance().findDrillLogByName(project, drillName);
        }
        else {
            drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);
        }

        setTitle("Drill Hole Row "+String.valueOf(gridCoordinate.getRow())+" Column "+String.valueOf(gridCoordinate.getColumn()));
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
        gridCoordinate.setBitSize(bitSize.getText().toString());
        gridCoordinate.setComment(comment.getText().toString());

        AsyncTaskRunner holeTaskRunner = new AsyncTaskRunner();
        asyncTask = holeTaskRunner.execute();

        if (isEdit){
            // find the grid in the list and update it.  normally we could just update the object
            // but because we are serializiOffng the object the reference is lost
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
        intent.putExtra("id", project.getId());
        if (drillLog.getId() == null) {
            intent.putExtra("drill.name", drillLog.getName());
        }
        else {
            intent.putExtra("drillId", drillLog.getId());
        }
        //NavUtils.navigateUpTo(this, intent);
        startActivity(intent);
        return true;
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            String result = null;
            if (isConnected()) {
                try {
                    result = ProjectSync.getInstance().updateDrillCoordinate(isEdit, project, drillLog, gridCoordinate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                gridCoordinate.setDirty(true);
            }
            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
            }
            return result;
        }
        protected void onPostExecute(String result) {
            Intent toDrillLogCoordinates = new Intent(GridCoordinateActivity.this, GridActivity.class);
            toDrillLogCoordinates.putExtra("id", project.getId());
            if (drillLog.getId() == null) {
                toDrillLogCoordinates.putExtra("drill.name", drillLog.getName());
            }
            else{
                toDrillLogCoordinates.putExtra("drillId", drillLog.getId());
            }
            startActivity(toDrillLogCoordinates);
            finish();
        }

    }

}
