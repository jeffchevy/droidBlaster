package com.drillandblast.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectKeep {
    private static final String TAG = "ProjectKeep";
    private static ProjectKeep instance;

    List<Project> projects = null;
    Map<String, Project> projectMap = null;

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
    public Project findById(String id){
        return projectMap.get(id);
    }

    public DrillLog findDrillLogById(Project project, String drillId) {
        if (project != null) {
            for (DrillLog log : project.getDrillLogs()) {
                if (log.getId().equalsIgnoreCase(drillId)) {
                    return log;
                }
            }
        }
        return null;
    }
    public DailyLog findDailyLogById(Project project, String dailyId) {
        if (project != null) {
            for (DailyLog log : project.getDailyLogs()) {
                if (log.getId().equalsIgnoreCase(dailyId)) {
                    return log;
                }
            }
        }
        return null;
    }

    public List<Project> fromJson(String json) {
        projects.clear();
        projectMap.clear();
        try {
            JSONArray jsonarray = new JSONArray(json);
            if (jsonarray != null) {
                for (int i = 0; i < jsonarray.length(); i++) {
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    String id = (String) getValue(jsonobject, "_id");
                    String customer = (String) getValue(jsonobject, "customer");
                    String drillersName = (String) getValue(jsonobject, "drillersName");
                    String shotNumber = (String) getValue(jsonobject, "shotNumber");
                    String contractorsName = (String) getValue(jsonobject, "contractorsName");
                    String jobName = (String) getValue(jsonobject, "jobName");

                    Project project = new Project(id, jobName, contractorsName, null, Double.valueOf(shotNumber), drillersName, 13D, new ArrayList<DrillLog>(), new ArrayList<DailyLog>());

                    if (jsonobject.get("drillLogs") instanceof JSONArray) {
                        JSONArray drillLogArray = jsonobject.getJSONArray("drillLogs");
                        for (int j = 0; j < drillLogArray.length(); j++) {
                            JSONObject drillLog = drillLogArray.getJSONObject(j);
                            String drillId = (String) getValue(drillLog, "_id");
                            String drillerName = (String) getValue(drillLog, "drillerName");
                            String name = (String) getValue(drillLog,"name");
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
                                String bitSize = (String) getValue(holesObject, "bitSize");
                                GridCoordinate gridCoordinate = new GridCoordinate(holeId, Integer.valueOf(x), Integer.valueOf(y), Double.valueOf(z), comments, 0);
                                holes.add(gridCoordinate);
                            }

                            DrillLog drill = new DrillLog(drillId, drillerName, name);
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

                            DailyLog daily = new DailyLog(dailyId, drillNumber, Double.valueOf(gallonsPumped), dateStr, Integer.valueOf(hourMeterStart),
                                    Integer.valueOf(hourMeterEnd), bulkTankPumpedFrom, percussionTime);
                            project.addDailyLog(daily);
                        }
                    }
                    projects.add(project);
                    projectMap.put(project.getId(), project);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }

    private Object getValue(JSONObject jsonObject, String name){
        Object result= null;
        try{
            result = jsonObject.getString(name);
        } catch (Exception ex) {}
        return result;
    }
    public void saveProjectToFile(Project project, Context ctx){
        ObjectOutputStream os = null;
        FileOutputStream fos = null;
        try {
            fos = ctx.openFileOutput("testproject", Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(project);
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
    public Project readProjectFromFile(Context ctx){
        Project project = null;
        ObjectOutputStream os = null;
        FileOutputStream fos = null;
        try {
            FileInputStream fis = ctx.openFileInput("testproject");
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

}