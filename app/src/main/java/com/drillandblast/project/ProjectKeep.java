package com.drillandblast.project;

import android.content.Context;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ProjectKeep {
    private static final String TAG = "ProjectKeep";
    private static ProjectKeep instance;
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    List<Project> projects = null;
    Map<String, Project> projectMap = null;
    Context context = null;
    String token = null;

    private ProjectKeep(){
        this.projects = new ArrayList<>();
        this.projectMap = new HashMap<>();
    }

    public static synchronized ProjectKeep getInstance(){
        if(instance == null){
            instance = new ProjectKeep();
        }
        return instance;
    }
    public int size() {
        return projects.size();
    }
    public Project findById(String id){
        return projectMap.get(id);
    }

    public DrillLog findDrillLogById(Project project, String drillId) {
        if (project != null) {
            for (DrillLog log : project.getDrillLogs()) {
                if (log.getId() != null && log.getId().equalsIgnoreCase(drillId)) {
                    return log;
                }
            }
        }
        return null;
    }
    public DrillLog findDrillLogByName(Project project, String drillName) {
        if (project != null) {
            for (DrillLog log : project.getDrillLogs()) {
                if (log.getName() != null && log.getName().equalsIgnoreCase(drillName)) {
                    return log;
                }
            }
        }
        return null;
    }

    public DailyLog findDailyLogById(Project project, String dailyId) {
        if (project != null) {
            for (DailyLog log : project.getDailyLogs()) {
                if (log.getId() != null && log.getId().equalsIgnoreCase(dailyId)) {
                    return log;
                }
            }
        }
        return null;
    }

    public List<Project> getAllProjectsfromJson(String json) throws Exception {
        projects.clear();
        projectMap.clear();
            if (json != null) {
                JSONArray jsonarray = new JSONArray(json);
                if (jsonarray != null) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject jsonobject = jsonarray.getJSONObject(i);
                        Project project = getProjectFromJson(jsonobject);
                        projects.add(project);
                        projectMap.put(project.getId(), project);
                    }
                }
            }
        return projects;
    }

    @NonNull
    private Project getProjectFromJson(JSONObject jsonobject) throws Exception {
        String id = (String) getValue(jsonobject, "_id");
        String contractorName = (String) getValue(jsonobject, "contractorName");
        String projectName = (String) getValue(jsonobject, "projectName");

        Project project = new Project(id, projectName, contractorName,new ArrayList<DrillLog>(), new ArrayList<DailyLog>());
        project.setDirty(false);

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
                    GridCoordinate gridCoordinate = new GridCoordinate(holeId, Integer.valueOf(x), Integer.valueOf(y), Double.valueOf(z), comments, holeBitSize);
                    gridCoordinate.setDirty(false);
                    holes.add(gridCoordinate);
                }

                DrillLog drill = new DrillLog(drillId, drillerName, name, pattern, Integer.valueOf(shotNumber), bitSize);
                drill.setDirty(false);
                drill.setGridCoordinates(holes);
                project.addDrillLog(drill);
            }
        }
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

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                sdf.setTimeZone(UTC);
                Date date = sdf.parse(dateStr);

                DailyLog daily = new DailyLog(dailyId, drillNumber, Double.valueOf(gallonsPumped), date, Double.valueOf(hourMeterStart),
                        Double.valueOf(hourMeterEnd), bulkTankPumpedFrom, Double.valueOf(percussionTime));
                daily.setDirty(false);
                project.addDailyLog(daily);
            }
        }
        return project;
    }
    Project getLatestProjectFromServer(Project project) throws Exception {
        Project latestProject = null;
        String jsonData = null;
            //------------------>>
//              HttpGet httpGet = new HttpGet("http://10.0.2.2:1337/api/v1/project");
            HttpGet httpGet = new HttpGet(SimpleHttpClient.baseUrl+"project/"+project.getId());

            httpGet.setHeader("token", getToken());
            Log.d(TAG, "doInBackground: " + httpGet.getURI().toString());
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse response = httpclient.execute(httpGet);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                jsonData = EntityUtils.toString(entity);

            }
            JSONObject projectJson = new JSONObject(jsonData);
            // replace out the project ref for the object we just got from the server
            // this will give us any updates from the server
            latestProject = getProjectFromJson(projectJson);

            // make sure the new project is referenced in the list
            for (Iterator<Project> iter = projects.iterator(); iter.hasNext();) {
                Project checkProject = iter.next();
                if (latestProject.getId().equalsIgnoreCase(checkProject.getId())) {
                    iter.remove();
                    projects.add(latestProject);
                    projectMap.put(latestProject.getId(), latestProject);
                }

            }

        return latestProject;
    }
    private void addProject(Project project) {
        projects.add(project);
        projectMap.put(project.getId(), project);
    }

    private Object getValue(JSONObject jsonObject, String name){
        Object result= null;
        try{
            result = jsonObject.getString(name);
        } catch (Exception ex) {}
        return result;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void saveProjectToFile(Project project){
        ObjectOutputStream os = null;
        FileOutputStream fos = null;
        try {
            Log.d(TAG, "Start writing file:"+project.getId()+"-project");
            fos = context.openFileOutput(project.getId()+"-project", Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(project);
            Log.d(TAG, "End writing file:"+project.getId()+"-project");
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Project readProjectFromFile(String filename){
        Project project = null;
        ObjectOutputStream os = null;
        FileOutputStream fos = null;
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream is = new ObjectInputStream(fis);
            project = (Project) is.readObject();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return project;
    }
    public void removeFile(Project project)
    {
        try {
            boolean deleted = context.deleteFile(project.getId()+"-project");
            Log.d(TAG, "Delete File: "+project.getId()+"-project"+"  was deleted: "+deleted);
        } catch( Exception ex) {
            ex.printStackTrace();
        }
    }
    public List<Project> readFiles(){
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("-project");
            }
        };
        File file = context.getFilesDir();
        File files[] = file.listFiles(filter);
        for (File thefile: files) {
            Log.d(TAG, "File: "+thefile.getName());
            Project project = readProjectFromFile(thefile.getName());

            // make sure we got a project and that it is not already added
            if (project != null) {
                if (projectMap.get(project.getId()) == null) {
                    addProject(project);
                }
            }
        }
        return projects;
    }
}