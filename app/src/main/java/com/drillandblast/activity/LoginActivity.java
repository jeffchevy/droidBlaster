package com.drillandblast.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.http.SimpleHttpClient;
import com.drillandblast.project.ProjectKeep;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener{

    Validator validator;

    @Required(order = 1)
    EditText email;


    @Required(order = 1)
    EditText password;

    TextView finalResult;
    Button ok;
    private AsyncTask<String, String, String> asyncTask;
    private String response;
    private static Context context;
    private String token = null;
    private String userName = null;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LoginActivity.context = getApplicationContext();
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.et_un);
        email.setText("test@example.com");
        password = (EditText) findViewById(R.id.et_pw);
        password.setText("test");
        ok = (Button) findViewById(R.id.btn_login);
        finalResult = (TextView) findViewById(R.id.tv_error);
        Boolean successValue = false;

        validator =new Validator(this);
        validator.setValidationListener(this);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                validator.validate();
                String result = null;
                LoginTaskRunner runner = new LoginTaskRunner();
                String userName = LoginActivity.this.email.getText().toString();
                String password = LoginActivity.this.password.getText().toString();
                asyncTask = runner.execute(userName, password);
                try {
                    String asyncResultText = asyncTask.get();
                    try {
                        JSONObject json = new JSONObject(asyncResultText);
                        Boolean successValue = (Boolean)json.get("success");
                        token = (String)json.get("token");
                        userName = (String)json.get("username");

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
                        ProjectKeep.getInstance().setUserName(userName);

                        // MY_PREFS_NAME - a static String variable like:
                        //public static final String MY_PREFS_NAME = "MyPrefsFile";
                        SharedPreferences.Editor editor = getSharedPreferences("file", Context.MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.putString("username", userName);
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

    //Validation
    //To perform the operation, when all input field satisfy the validation rule.
    @Override
    public void onValidationSucceeded() {
        Toast.makeText(this, "Validation Succeeded", Toast.LENGTH_SHORT).show();
    }

    public void onValidationFailed(View view, Rule<?> rule) {

        final String failureMessage = rule.getFailureMessage();
        if (view instanceof EditText) {
            EditText failed = (EditText) view;
            failed.requestFocus();
            failed.setError(failureMessage);
        } else {
            Toast.makeText(getApplicationContext(), failureMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void nextActivity(){
        Intent next = new Intent(LoginActivity.this, ProjectListActivity.class);
        startActivity(next);
        finish();

    }
    public class LoginTaskRunner extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {

            String result = null;

            int count = params.length;
            if(count==2){
                JSONObject json = new JSONObject();
                String response = null;
                try {
                    json.put("email", params[0]);
                    json.put("password",params[1]);
                    result = SimpleHttpClient.executeHttpPost("authenticate", json, null);
//                result = SimpleHttpClient.executeHttpPost("http://localhost:1337/api/v1/authenticate", postParameters);

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
}
