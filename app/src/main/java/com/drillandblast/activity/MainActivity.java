package com.drillandblast.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.http.NetworkStateReceiver;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.drillandblast.sync";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.drillandblast";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    public static Account mAccount = null;
    // A content resolver for accessing the provider
    ContentResolver mResolver;
    String token = null;
    String userName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProjectKeep.getInstance().setContext(this);

        /* this is used for data sync to android framework
        mAccount = CreateSyncAccount(this);
        mResolver = getContentResolver();
        ContentResolver.addPeriodicSync( mAccount,AUTHORITY,Bundle.EMPTY, 60L);
        */
        // so we know if we are connected
        NetworkStateReceiver.setState(isConnected());

        // if we already have a valid login just use that and move on
        Map<String, ?> map = getSharedPreferences("file", Context.MODE_PRIVATE).getAll();
        token = (String)map.get("token");
        userName = (String)map.get("username");
        if (token != null)
        {
            ProjectKeep.getInstance().setToken(token);
            ProjectKeep.getInstance().setUserName(userName);
            AsyncTaskRunner runner = new AsyncTaskRunner();
            runner.execute();
        }
        else
        {
            nextActivity(LoginActivity.class);
        }

    }
    private void nextActivity( Class<?> cls){
        Intent next = new Intent(MainActivity.this, cls);
        startActivity(next);
        finish();
    }
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if (isConnected()) {

                HttpGet httpGet = new HttpGet(SimpleHttpClient.baseUrl + "test");

                httpGet.setHeader("token", ProjectKeep.getInstance().getToken());
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse response = null;
                try {
                    response = httpclient.execute(httpGet);
                    int status = response.getStatusLine().getStatusCode();
                    Log.d(TAG, "test - url: " + httpGet.getURI().toString() + " status=" + status);

                    if (status == 200) {
                        HttpEntity entity = response.getEntity();
                        String json = EntityUtils.toString(entity);
                        JSONObject jsonobject = new JSONObject(json);
                        Log.d(TAG, json);
                        String success = jsonobject.getString("success");
                        result = success;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // StatusLine stat = response.getStatusLine();
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.equalsIgnoreCase("false")) {
                nextActivity(LoginActivity.class);
            }
            else {
                nextActivity(ProjectListActivity.class);
            }

        }

    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        final int uid = Binder.getCallingUid();
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

}
