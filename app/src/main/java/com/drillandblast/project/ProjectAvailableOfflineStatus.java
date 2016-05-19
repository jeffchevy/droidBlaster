package com.drillandblast.project;

import android.content.Context;
import android.content.SharedPreferences;
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
        if(instance == null) {
            instance = new ProjectAvailableOfflineStatus();
        }
        return instance;
    }

    public boolean isAvailableOffline(String projectId) {
        boolean result = ProjectKeep.getInstance().getContext().getSharedPreferences("ProjectStatus", Context.MODE_PRIVATE).getBoolean(projectId, false);
        return result;
    }
    public void setIsAvailableOffline(String projectId, Boolean status) {
        SharedPreferences.Editor editor = ProjectKeep.getInstance().getContext().getSharedPreferences("ProjectStatus", Context.MODE_PRIVATE).edit();
        editor.putBoolean(projectId, status);
        editor.apply();
    }

    public void clear() {
        projectAvailableOfflineStatus.clear();
    }
}
