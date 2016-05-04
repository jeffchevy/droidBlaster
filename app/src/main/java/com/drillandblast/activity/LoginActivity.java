package com.drillandblast.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.drillandblast.R;
import com.drillandblast.http.LoginTaskRunner;
import com.drillandblast.project.ProjectKeep;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText userName;
    EditText password;
    TextView finalResult;
    Button ok;
    private AsyncTask<String, String, String> asyncTask;
    private String response;
    private static Context context;


    private String token = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginActivity.context = getApplicationContext();
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.et_un);
        userName.setText("test@example.com");
        password = (EditText) findViewById(R.id.et_pw);
        password.setText("test");
        ok = (Button) findViewById(R.id.btn_login);
        finalResult = (TextView) findViewById(R.id.tv_error);
        Boolean successValue = false;

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String result = null;
                LoginTaskRunner runner = new LoginTaskRunner();
                String userName = LoginActivity.this.userName.getText().toString();
                String password = LoginActivity.this.password.getText().toString();
                asyncTask = runner.execute(userName, password);
                try {
                    String asyncResultText = asyncTask.get();
                    try {
                        JSONObject json = new JSONObject(asyncResultText);
                        Boolean successValue = (Boolean)json.get("success");
                        token = (String)json.get("token");
                        if (!successValue)
                        {
                            result = "Login failed!";
                        }
                        else
                        {
                            result = "Login successful";
                        }

                        // save off for future use
                        ProjectKeep.getInstance().setToken(token);

                        // MY_PREFS_NAME - a static String variable like:
                        //public static final String MY_PREFS_NAME = "MyPrefsFile";
                        SharedPreferences.Editor editor = getSharedPreferences("file", Context.MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.apply();
                        if (successValue)
                        {
                            nextActivity();
                        }

                    } catch(Exception ex){
                        result = asyncResultText;
                    }
                } catch (Exception e1) {
                    result = e1.getMessage();
                }
                finalResult.setText(result);
            }
        });
    }
    private void nextActivity(){
        Intent next = new Intent(LoginActivity.this, ProjectListActivity.class);
        startActivity(next);
        finish();

    }
}
