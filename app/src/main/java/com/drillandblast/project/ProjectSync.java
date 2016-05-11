package com.drillandblast.project;

import android.os.AsyncTask;

import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectSync {
    private static final String TAG = "ProjectKeep";
    private static ProjectSync instance;
    AsyncTask<String, String, String> asyncTask;
    private Project project = null;

    private ProjectSync(){
    }

    public static synchronized ProjectSync getInstance(){
        if(instance == null){
            instance = new ProjectSync();
        }
        return instance;
    }

    public synchronized void sync(Project project) {
        this.project = project;
        AsyncTaskRunner projectSaveRunner = new AsyncTaskRunner();
        asyncTask = projectSaveRunner.execute();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "Successful";
            try {
                checkAndUpdate();
                // go and get anything that the server has
                Project latestProject = ProjectKeep.getInstance().getLatestProjectFromServer(project);
            }
            catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
            finally {
                // no matter what happens make sure to save off what has already been done
                ProjectKeep.getInstance().saveProjectToFile(project);
            }

            return result;
        }
    }
    private void checkAndUpdate() throws Exception {
        if (project.isDirty()) {
            boolean projectHeaderIsEdit = project.getId() != null ? true : false;
            updateProjectHeader(projectHeaderIsEdit, project);
            project.setDirty(false);
        }
        for(DailyLog log : project.getDailyLogs())
        {
            if (log.isDirty())
            {
                boolean dailyLogIsEdit = log.getId() != null ? true : false;
                updateDailyLog(dailyLogIsEdit, project, log);
                log.setDirty(false);
            }
        }
        for (DrillLog log: project.getDrillLogs()) {
            if (log.isDirty())
            {
                boolean drillLogIsEdit = log.getId() != null ? true : false;
                updateDrillLog(drillLogIsEdit, project, log);
                log.setDirty(false);
            }
            for (GridCoordinate gridCoordinate : log.getGridCoordinates()) {
                if (gridCoordinate.isDirty())
                {
                    boolean coordinateIsEdit = gridCoordinate.getId() != null ? true : false;
                    updateDrillCoordinate(coordinateIsEdit, project, log, gridCoordinate);
                    gridCoordinate.setDirty(false);
                }
            }
        }
    }


    public static String updateProjectHeader(boolean isEdit, Project project) throws Exception {
        String result;JSONObject json = new JSONObject();
        json.put("projectName", project.getProjectName());
        json.put("contractorName", project.getContractorName());

        if (isEdit) {
            result = SimpleHttpClient.executeHttpPut("project/" + project.getId(), json, ProjectKeep.getInstance().getToken());
        } else {
            result = SimpleHttpClient.executeHttpPost("project", json, ProjectKeep.getInstance().getToken());
        }

        return result;
    }
    public static String updateDailyLog(boolean isEdit, Project project, DailyLog dailyLog) throws Exception {
        String result;
        JSONObject json = new JSONObject();
        json.put("drillNumber", dailyLog.getDrillNum());
        json.put("gallonsPumped", dailyLog.getGallonsFuel());
        json.put("bulkTankPumpedFrom", dailyLog.getBulkTankPumpedFrom());
        json.put("hourMeterStart", dailyLog.getMeterStart());
        json.put("hourMeterEnd", dailyLog.getMeterEnd());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        json.put("date", sdf.format(dailyLog.getDate()));
        json.put("percussionTime", dailyLog.getPercussionTime());

        if (isEdit)
        {
            result = SimpleHttpClient.executeHttpPut("dailyLogs/"+project.getId()+"/"+dailyLog.getId(), json, ProjectKeep.getInstance().getToken());
        }
        else
        {
            result = SimpleHttpClient.executeHttpPost("dailyLogs/"+project.getId(), json, ProjectKeep.getInstance().getToken());
            JSONObject jsonobject = new JSONObject(result);
            String id = jsonobject.getString("id");
            dailyLog.setId(id);
        }
        return result;
    }
    public static String updateDrillLog(boolean isEdit, Project project, DrillLog drillLog) throws Exception {
        String result = null;
        JSONObject json = new JSONObject();
        json.put("name", drillLog.getName());
        json.put("drillerName", drillLog.getDrillerName());
        json.put("pattern", drillLog.getPattern());
        json.put("shotNumber", drillLog.getShotNumber());
        json.put("bitSize", drillLog.getBitSize());

        if (isEdit) {
            result = SimpleHttpClient.executeHttpPut("drillLogs/" + project.getId() + "/" + drillLog.getId(), json, ProjectKeep.getInstance().getToken());
        } else {
            result = SimpleHttpClient.executeHttpPost("drillLogs/" + project.getId(), json, ProjectKeep.getInstance().getToken());
            JSONObject jsonobject = new JSONObject(result);
            String id = jsonobject.getString("id");
            drillLog.setId(id);
        }

        return result;
    }
    public static String updateDrillCoordinate(boolean isEdit, Project project, DrillLog drillLog, GridCoordinate gridCoordinate) throws Exception {
        String result = null;

        JSONObject json = new JSONObject();
        String response = null;
        json.put("x", gridCoordinate.getRow());
        json.put("y", gridCoordinate.getColumn());
        json.put("z", gridCoordinate.getDepth());
        json.put("comments", gridCoordinate.getComment());
        json.put("bitSize", gridCoordinate.getBitSize());

        if (isEdit) {
            result = SimpleHttpClient.executeHttpPut("holes/" + project.getId() + "/" + drillLog.getId() + "/" + gridCoordinate.getId(), json, ProjectKeep.getInstance().getToken());
        } else {
            result = SimpleHttpClient.executeHttpPost("drillLogs/" + project.getId() + "/" + drillLog.getId(), json, ProjectKeep.getInstance().getToken());
            JSONObject jsonobject = new JSONObject(result);
            String id = jsonobject.getString("id");
            gridCoordinate.setId(id);
        }
        return result;
    }
}
