package com.drillandblast.project;

import android.content.Context;

import com.drillandblast.model.Project;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProjectAvailableOfflineStatus implements Serializable {
    public static final String FILENAME = "AvailableOfflineStatus";
    private static ProjectAvailableOfflineStatus instance;

    Map<String, Boolean> projectAvailableOfflineStatus = new HashMap<>();

    public static synchronized ProjectAvailableOfflineStatus getInstance(){
        if(instance == null){
            instance = read();
        }
        return instance;
    }

    public boolean isAvailableOffline(String projectId) {
        Boolean status = projectAvailableOfflineStatus.get(projectId);
        boolean result = (status == null) ? false : status;
        return result;
    }
    public void setIsAvailableOffline(String projectId, boolean status) {
        projectAvailableOfflineStatus.put(projectId, status);
        save();
    }

    public void clear() {
        projectAvailableOfflineStatus.clear();
        save();
    }
    private void save(){
        ObjectOutputStream os = null;
        FileOutputStream fos = null;
        try {
            fos = ProjectKeep.getInstance().getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.writeObject(instance);
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
    private static ProjectAvailableOfflineStatus read(){
        ObjectOutputStream os = null;
        FileOutputStream fos = null;
        ProjectAvailableOfflineStatus status = null;
        try {
            FileInputStream fis = ProjectKeep.getInstance().getContext().openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            status = (ProjectAvailableOfflineStatus) is.readObject();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (status == null) {
                status = new ProjectAvailableOfflineStatus();
            }
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return status;
    }

}
