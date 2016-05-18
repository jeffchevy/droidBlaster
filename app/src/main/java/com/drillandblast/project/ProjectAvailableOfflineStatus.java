package com.drillandblast.project;

import android.content.Context;
import android.util.Log;

import com.drillandblast.model.Project;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProjectAvailableOfflineStatus implements Serializable {
    private static final String TAG = "ProjectAvailOffline";
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
            final Gson gson = new Gson();
            String json = gson.toJson(instance);

            fos = ProjectKeep.getInstance().getContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
            os = new ObjectOutputStream(fos);
            os.write(json.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fos != null){
                try {
                    os.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static ProjectAvailableOfflineStatus read(){
        ObjectOutputStream os = null;
        FileInputStream fis = null;
        ProjectAvailableOfflineStatus status = null;
        try {
            fis = ProjectKeep.getInstance().getContext().openFileInput(FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            isr.close();
            Gson gson = new Gson();
            instance = gson.fromJson(sb.toString(), ProjectAvailableOfflineStatus.class);
        } catch (Exception e) {
            ProjectKeep.getInstance().getContext().deleteFile(FILENAME);
            Log.d(TAG, "removing file becuase we could not read it!");
            e.printStackTrace();
        }
        finally {
            if (status == null) {
                status = new ProjectAvailableOfflineStatus();
            }
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return status;
    }

}
