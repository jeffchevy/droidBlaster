package com.drillandblast.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginTaskRunner extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        String result = null;

        int count = params.length;
        if(count==2){
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("email",params[0]));
            postParameters.add(new BasicNameValuePair("password",params[1]));
            String response = null;
            try {
                result = SimpleHttpClient.executeHttpPost("http://10.0.2.2:1337/api/v1/authenticate", postParameters);

            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
        }else{
            result="Invalid number of arguments-"+count;
        }
        return result;
    }
}