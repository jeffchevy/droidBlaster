package com.drillandblast.project;

import android.os.AsyncTask;

import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.Project;

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
            return "good";
        }
    }

    public synchronized void sync(Project project) {
        this.project = project;
        AsyncTaskRunner projectSaveRunner = new AsyncTaskRunner();
        asyncTask = projectSaveRunner.execute();
    }

    public static String updateProjectHeader(boolean isEdit, Project project) {
        String result;JSONObject json = new JSONObject();
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }
    public static String updateDailyLog(boolean isEdit, Project project, DailyLog dailyLog) {
        String result;JSONObject json = new JSONObject();
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }
}
