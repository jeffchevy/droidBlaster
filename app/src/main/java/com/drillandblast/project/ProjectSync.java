package com.drillandblast.project;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

public class ProjectSync {
    private static final String TAG = "ProjectSync";
    private static ProjectSync instance;
    AsyncTask<String, String, String> asyncTask;
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private Object updateMutex;
    SimpleDateFormat serverDateFormat = null;

    private ProjectSync(){

        updateMutex = new Object();
        serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    }

    public static synchronized ProjectSync getInstance(){
        if(instance == null){
            instance = new ProjectSync();
        }
        return instance;
    }

    public void push(HashSet<String> updateFailList)
    {
        List<Project> projects = ProjectKeep.getInstance().readFiles();

        for (Project project: projects) {
            try {
                // update cloud
                checkAndUpdate(project);
                // remove file
                ProjectKeep.getInstance().removeFile(project);
            } catch (Exception e) {
                updateFailList.add(project.getId());
                e.printStackTrace();
            }
        }
    }
    public void pull(HashSet<String> updateFailList)
    {
        // get new data from cloud
        List<Project> projects = getProjectData(updateFailList);

        // update our in memory storage
        ProjectKeep.getInstance().refreshProjectData(projects);

        // write out data to local disk if it is an offline project
        for(Project project: projects) {
            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
            }
        }
    }
    public void readFromDisk() {
        List<Project> projects = ProjectKeep.getInstance().readFiles();

        // update our in memory storage
        ProjectKeep.getInstance().refreshProjectData(projects);
    }

    public void sync() {
        Log.i(TAG, "Starting Sync");
        HashSet<String> updateFailList = new HashSet<>();
        synchronized (updateMutex) {
            push(updateFailList);
            pull(updateFailList);
        }
        Log.i(TAG, "Finished Sync");
    }

    private void checkAndUpdate(Project project) throws Exception {
        if (project.isDirty()) {
            boolean projectHeaderIsEdit = project.getId() != null ? true : false;
            updateProjectHeader(projectHeaderIsEdit, project);
            project.setDirty(false);
        }
        if (project.getDailyLogs() != null) {
            for (DailyLog log : project.getDailyLogs()) {
                if (log.isDirty()) {
                    boolean dailyLogIsEdit = log.getId() != null ? true : false;
                    updateDailyLog(dailyLogIsEdit, project, log);
                    log.setDirty(false);
                }
            }
        }
        if (project.getDrillLogs() != null)
        {
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
    }


    public String updateProjectHeader(boolean isEdit, Project project) throws Exception {
        String result;JSONObject json = new JSONObject();
        json.put("projectName", project.getProjectName());
        json.put("contractorName", project.getContractorName());

        if (isEdit) {
            result = SimpleHttpClient.executeHttpPut("project/" + project.getId(), json, ProjectKeep.getInstance().getToken());
        } else {
            result = SimpleHttpClient.executeHttpPost("project", json, ProjectKeep.getInstance().getToken());
            JSONObject jsonobject = new JSONObject(result);
            String projectJson = jsonobject.getString("project");
            JSONObject proj = new JSONObject(projectJson);
            String id = proj.getString("_id");
            project.setId(id);
            Log.d(TAG, proj.toString());
        }

        return result;
    }
    public String updateDailyLog(boolean isEdit, Project project, DailyLog dailyLog) throws Exception {
        String result;
        JSONObject json = new JSONObject();
        json.put("drillNumber", dailyLog.getDrillNum());
        json.put("gallonsPumped", dailyLog.getGallonsFuel());
        json.put("bulkTankPumpedFrom", dailyLog.getBulkTankPumpedFrom());
        json.put("hourMeterStart", dailyLog.getMeterStart());
        json.put("hourMeterEnd", dailyLog.getMeterEnd());
        json.put("date", serverDateFormat.format(dailyLog.getDate()));
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
    public String updateDrillLog(boolean isEdit, Project project, DrillLog drillLog) throws Exception {
        String result = null;
        JSONObject json = new JSONObject();
        json.put("name", drillLog.getName());
        json.put("drillerName", drillLog.getDrillerName());
        json.put("pattern", drillLog.getPattern());
        json.put("shotNumber", drillLog.getShotNumber());
        json.put("bitSize", drillLog.getBitSize());
        json.put("customerSignature", drillLog.getCustomerSignature());
        json.put("customerSignatureName", drillLog.getCustomerSignatureName());
        String customerSignatureDateStr = (drillLog.getCustomerSignatureDate() == null) ? null : serverDateFormat.format(drillLog.getCustomerSignatureDate());
        json.put("customerSignatureDate", customerSignatureDateStr);
        json.put("supervisorSignature", drillLog.getSupervisorSignature());
        json.put("supervisorSignatureName", drillLog.getSupervisorSignatureName());
        String supervisorSignatureDateStr = (drillLog.getSupervisorSignatureDate() == null) ? null : serverDateFormat.format(drillLog.getSupervisorSignatureDate());
        json.put("supervisorSignatureDate", supervisorSignatureDateStr);

        if (isEdit && drillLog.getId() != null) {
            result = SimpleHttpClient.executeHttpPut("drillLogs/" + project.getId() + "/" + drillLog.getId(), json, ProjectKeep.getInstance().getToken());
        } else {
            result = SimpleHttpClient.executeHttpPost("drillLogs/" + project.getId(), json, ProjectKeep.getInstance().getToken());
            JSONObject jsonobject = new JSONObject(result);
            String id = jsonobject.getString("id");
            drillLog.setId(id);
        }

        return result;
    }
    public String updateDrillCoordinate(boolean isEdit, Project project, DrillLog drillLog, GridCoordinate gridCoordinate) throws Exception {
        String result = null;

        JSONObject json = new JSONObject();
        String response = null;
        json.put("x", gridCoordinate.getRow());
        json.put("y", gridCoordinate.getColumn());
        json.put("z", gridCoordinate.getDepth());
        json.put("date", serverDateFormat.format(gridCoordinate.getDate()));
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
    public List<Project> getProjectData(HashSet<String> ignoreList) {
        Log.i(TAG, "Starting getProjectData()");
        List<Project> projects = new ArrayList<>();
        try {
            HttpGet httpGet = new HttpGet(SimpleHttpClient.baseUrl+"activeProjects");

            httpGet.setHeader("token", ProjectKeep.getInstance().getToken());
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse response = httpclient.execute(httpGet);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();
            Log.d(TAG, "getProjectData - url: " + httpGet.getURI().toString()+ " status="+status);

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String json = EntityUtils.toString(entity);

                if (json != null) {
                    JSONArray jsonarray = new JSONArray(json);
                    if (jsonarray != null) {
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            Project project = null;

                            // read from json and only add project if it does not error out
                            try {
                                project = getProjectFromJson(jsonobject);

                                // if we had a problem updating the project on the push then we don't want to overrite the data
                                // we we query the server
                                if (!ignoreList.contains(project.getId())) {
                                    projects.add(project);
                                }
                                else {
                                    Log.d(TAG, "Ignoring project: "+project.getId()+ " because of push issue!");
                                }
                            } catch (Exception ex)
                            {
                                Log.e(TAG, ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Finished getProjectData()");
        return projects;
    }
    @NonNull
    private Project getProjectFromJson(JSONObject jsonobject) throws Exception {
        String id = (String) getValue(jsonobject, "_id");
        String contractorName = (String) getValue(jsonobject, "contractorName");
        String projectName = (String) getValue(jsonobject, "projectName");

        Project project = new Project(id, projectName, contractorName,new ArrayList<DrillLog>(), new ArrayList<DailyLog>());
        project.setDirty(false);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(UTC);

        String drillLogs = (String) getValue(jsonobject, "drillLogs");
        if (drillLogs != null)
        {
            if (jsonobject.get("drillLogs") instanceof JSONArray) {
                JSONArray drillLogArray = jsonobject.getJSONArray("drillLogs");
                for (int j = 0; j < drillLogArray.length(); j++) {
                    JSONObject drillLog = drillLogArray.getJSONObject(j);
                    String drillId = (String) getValue(drillLog, "_id");
                    String drillerName = (String) getValue(drillLog, "drillerName");
                    String name = (String) getValue(drillLog, "name");
                    String pattern = (String) getValue(drillLog,"pattern");
                    String shotNumber = (String) getValue(drillLog,"shotNumber");
                    String bitSize = (String) getValue(drillLog,"bitSize");
                    String customerSignature = (String) getValue(drillLog, "customerSignature");
                    String customerSignatureName = (String) getValue(drillLog, "customerSignatureName");
                    String customerSignatureDateStr = (String) getValue(drillLog, "customerSignatureDate");
                    if (customerSignatureDateStr != null && customerSignatureDateStr.equals("null")) {
                        customerSignatureDateStr = null;
                    }
                    String supervisorSignature = (String) getValue(drillLog, "supervisorSignature");
                    String supervisorSignatureName = (String) getValue(drillLog, "supervisorSignatureName");
                    String supervisorSignatureDateStr = (String) getValue(drillLog, "supervisorSignatureDate");
                    if (supervisorSignatureDateStr != null && supervisorSignatureDateStr.equals("null")) {
                        supervisorSignatureDateStr = null;
                    }

                    String holesStr = (String)getValue(drillLog,"holes");
                    JSONArray holesArray = new JSONArray(holesStr);
                    List<GridCoordinate> holes = new ArrayList<>();
                    for (int k = 0; k < holesArray.length(); k++) {
                        JSONObject holesObject = holesArray.getJSONObject(k);
                        String holeId = (String) getValue(holesObject, "_id");
                        String x = (String) getValue(holesObject, "x");
                        String y = (String) getValue(holesObject, "y");
                        String z = (String) getValue(holesObject, "z");
                        String comments = (String) getValue(holesObject, "comments");
                        String holeBitSize = (String) getValue(holesObject, "bitSize");
                        String dateStr = (String) getValue(holesObject, "date");

                        Date date = sdf.parse(dateStr);

                        GridCoordinate gridCoordinate = new GridCoordinate(holeId, Integer.valueOf(x), Integer.valueOf(y), Double.valueOf(z), comments, holeBitSize, date);
                        gridCoordinate.setDirty(false);
                        holes.add(gridCoordinate);
                    }

                    DrillLog drill = new DrillLog(drillId, drillerName, name, pattern, Integer.valueOf(shotNumber), bitSize);
                    drill.setDirty(false);
                    drill.setGridCoordinates(holes);
                    drill.setCustomerSignature(customerSignature);
                    drill.setCustomerSignatureName(customerSignatureName);
                    if (customerSignatureDateStr != null) {
                        if (customerSignatureDateStr != null && customerSignatureDateStr.length() > 0) {
                            drill.setCustomerSignatureDate(sdf.parse(customerSignatureDateStr));
                        }
                    }
                    drill.setSupervisorSignature(supervisorSignature);
                    drill.setSupervisorSignatureName(supervisorSignatureName);
                    if (supervisorSignatureDateStr != null && supervisorSignatureDateStr.length() > 0) {
                        drill.setSupervisorSignatureDate(sdf.parse(supervisorSignatureDateStr));
                    }
                    project.addDrillLog(drill);
                }
            }
        }
        String dailyLogs = (String) getValue(jsonobject, "dailyLogs");
        if (dailyLogs != null) {
            if (jsonobject.get("dailyLogs") instanceof JSONArray) {
                JSONArray dailyLogArray = jsonobject.getJSONArray("dailyLogs");
                for (int m = 0; m < dailyLogArray.length(); m++) {
                    JSONObject dailyLog = dailyLogArray.getJSONObject(m);

                    String dailyId = (String) getValue(dailyLog, "_id");
                    String drillNumber = (String) getValue(dailyLog, "drillNumber");
                    String gallonsPumped = (String) getValue(dailyLog, "gallonsPumped");
                    String bulkTankPumpedFrom = (String) getValue(dailyLog, "bulkTankPumpedFrom");
                    String hourMeterStart = (String) getValue(dailyLog, "hourMeterStart");
                    String hourMeterEnd = (String) getValue(dailyLog, "hourMeterEnd");
                    String percussionTime = (String) getValue(dailyLog, "percussionTime");
                    String dateStr = (String) getValue(dailyLog, "date");

                    Date date = sdf.parse(dateStr);

                    DailyLog daily = new DailyLog(dailyId, drillNumber, Double.valueOf(gallonsPumped), date, Double.valueOf(hourMeterStart),
                            Double.valueOf(hourMeterEnd), bulkTankPumpedFrom, Double.valueOf(percussionTime));
                    daily.setDirty(false);
                    project.addDailyLog(daily);
                }
            }
        }
        return project;
    }
    private Object getValue(JSONObject jsonObject, String name){
        Object result= null;
        try{
            result = jsonObject.getString(name);
        } catch (Exception ex) {}
        return result;
    }

}
