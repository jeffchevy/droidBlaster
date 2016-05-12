package com.drillandblast.project;

import android.content.Context;
import android.util.Log;

import com.drillandblast.model.DailyLog;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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

    public List<Project> findAll() {
        return this.projects;
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
    public DailyLog findDailyLogByNum(Project project, String num) {
        if (project != null) {
            for (DailyLog log : project.getDailyLogs()) {
                if (log.getDrillNum() != null && log.getDrillNum().equalsIgnoreCase(num)) {
                    return log;
                }
            }
        }
        return null;
    }

    public void refreshProjectData(List<Project> projects) {
        // set the new list
        this.projects = projects;
        // clean out old map
        projectMap.clear();
        for ( Project project: this.projects) {
            if (project.getId() != null) {
                projectMap.put(project.getId(), project);
            }
        }
    }
    public void addProject(Project project) {
        projects.add(project);
        projectMap.put(project.getId(), project);
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
        if (project.getId() == null) {
            return;
        }
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
        List<Project> results = new ArrayList<>();
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
            results.add(project);
        }
        return results;
    }
}