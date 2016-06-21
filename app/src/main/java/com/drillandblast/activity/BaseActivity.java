package com.drillandblast.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;

import com.drillandblast.project.ProjectKeep;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseActivity extends AppCompatActivity {
    public String getToken() {
        return ProjectKeep.getInstance().getToken();
    }
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
    public boolean isUpdateSuccessful(String result) {
        Boolean successValue = new Boolean(false);
        JSONObject json = null;
        try {
            json = new JSONObject(result);
            successValue = (Boolean)json.get("success");
        } catch (JSONException e) {
            // eat exception
        }
        return successValue;
    }

    public String getResultMessage(String result) {
        JSONObject json = null;
        String message = null;
        try {
            json = new JSONObject(result);
            message = (String)json.get("message");
        } catch (JSONException e) {
            // eat exception
        }
        return message;
    }

}
