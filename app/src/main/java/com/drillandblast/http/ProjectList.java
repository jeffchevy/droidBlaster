package com.drillandblast.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by a3k18zz on 4/6/2016.
 */
public class ProjectList  extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = "ProjectList";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... urls) {
        try {

            //------------------>>
            HttpGet httppost = new HttpGet("http://api.openweathermap.org/data/2.5/station/find?lat=55&lon=37&cnt=30&APPID=20d9e84dee0027674854c747af46e557");
            Log.d(TAG, "doInBackground: "+httppost.getURI().toString());
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);

            // StatusLine stat = response.getStatusLine();
            int status = response.getStatusLine().getStatusCode();
            Log.d(TAG, "doInBackground: "+status);

            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);


                JSONObject jsono = new JSONObject(data);
                Log.d(TAG, "JSON object: " + jsono.toString());
//                JSONArray geonames = jsono.getJSONArray("geonames");
//                for (int i=0;i<geonames.length();i++)
//                {
//                    JSONObject city = geonames.getJSONObject(i);
//                    Log.d(TAG, "doInBackground: "+city.get("name"));
//                }

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