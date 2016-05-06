package com.drillandblast.project;

import android.os.AsyncTask;

import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;

import org.json.JSONException;
import org.json.JSONObject;

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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "Successful";
            try {
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
                // go and get anything that the server has
                Project latestProject = ProjectKeep.getInstance().getLatestProjectFromServer(project);
                ProjectKeep.getInstance().saveProjectToFile(project);
            }
            catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }

            return result;
        }
    }

    public synchronized void sync(Project project) {
        this.project = project;
        AsyncTaskRunner projectSaveRunner = new AsyncTaskRunner();
        asyncTask = projectSaveRunner.execute();
    }

    public static String updateProjectHeader(boolean isEdit, Project project) throws Exception {
        String result;JSONObject json = new JSONObject();
        json.put("jobName", project.getProjectName());
        json.put("drillerName", project.getDrillerName());
        json.put("contractorsName", project.getContractorName());
        json.put("shotNumber", project.getShotNumber());
        json.put("bitSize", project.getBitSize());

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
