package com.drillandblast.http;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.drillandblast.model.Project;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import java.util.List;
import java.util.Set;

public class NetworkStateReceiver extends BroadcastReceiver {
    static Boolean online = new Boolean(true);
    public void onReceive(Context context, Intent intent) {
        Log.d("app","Network connectivity change");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        Log.d("app", "status="+isConnected);
        if  (online == false && isConnected) {
            Log.d("app", "*************************************  SYNC **************************************************");
            List<Project> projects = ProjectKeep.getInstance().readFiles();
            for (Project project : projects) {
                ProjectSync.getInstance().sync(project);
            }
            online = true;
        }
        else {
            online = isConnected;
        }
    }
}