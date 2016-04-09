package com.drillandblast.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProjectListTaskRunner extends AsyncTask<String, Void, Boolean> {

    private String token= null;
    private static final String TAG = "ProjectListTaskRunner";

    public ProjectListTaskRunner(String token) {
        super();
        this.token = token;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {

            //------------------>>
            HttpGet httpGet = new HttpGet("http://10.0.2.2:1337/api/v1/drillLog");
            httpGet.setHeader("token", token);
            Log.d(TAG, "doInBackground: " + httpGet.getURI().toString());
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse response = httpclient.execute(httpGet);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();
            Log.d(TAG, "doInBackground: "+status);

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);


                JSONObject json = new JSONObject(data);
                Log.d(TAG, "JSON object: " + json.toString());

                return true;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return false;
    }

    protected void onPostExecute(Boolean result) {

    }
}