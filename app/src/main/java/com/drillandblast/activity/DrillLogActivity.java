package com.drillandblast.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.drillandblast.R;
import com.drillandblast.model.DrillLog;
import com.drillandblast.model.GridCoordinate;
import com.drillandblast.model.Project;
import com.drillandblast.model.UpdateStatus;
import com.drillandblast.project.ProjectAvailableOfflineStatus;
import com.drillandblast.project.ProjectKeep;
import com.drillandblast.project.ProjectSync;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;

import java.util.ArrayList;
import java.util.Date;

public class DrillLogActivity extends BaseActivity implements Validator.ValidationListener {
    private boolean isEdit = false;
    public String token = null;
    public Project project = null;
    public DrillLog drillLog = null;
    private AsyncTask<String, String, UpdateStatus> asyncTask;
    private Validator validator = null;
    private Class<?> next = null;
    private boolean updateSignature = false;
    private String type = "Customer";
    ImageButton customerSignatureButton;
    ImageView customerSignatureImage;
    ImageButton supervisorSignatureButton;
    ImageView supervisorSignatureImage;
    TextView customerNameDate;
    TextView supervisorNameDate;
    boolean gridEditable = true;

    @Required(order=1)
    EditText log_name = null;

    @Required(order=2)
    EditText driller_name = null;

    @Required(order=3)
    EditText pattern_name = null;

    @Required(order=4)
    EditText shot_number = null;

    @Required(order=5)
    EditText bit_size = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drill_log);
        next = DrillLogListActivity.class;

        validator = new Validator(this);
        validator.setValidationListener(this);

        final Intent process = getIntent();
        String id =  process.getStringExtra("id");
        project = ProjectKeep.getInstance().findById(id);

        customerSignatureImage =(ImageView)findViewById(R.id.customer_signature_view);
        customerNameDate = (TextView) findViewById(R.id.customer_name_date);

        customerSignatureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                next = SignatureActivity.class;
                type = "Customer";
                validator.validate();
            }
        });
        supervisorSignatureImage =(ImageView)findViewById(R.id.supervisor_signature_view);
        supervisorNameDate = (TextView) findViewById(R.id.supervisor_name_date);

        supervisorSignatureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                next = SignatureActivity.class;
                type = "Supervisor";
                validator.validate();
            }
        });

        String drillId =  process.getStringExtra("drillId");
        if (drillId == null) {
            String drillName =  process.getStringExtra("drill.name");
            drillLog = ProjectKeep.getInstance().findDrillLogByName(project, drillName);
        }
        else {
            drillLog = ProjectKeep.getInstance().findDrillLogById(project, drillId);
        }

        getAndroidFields();
        Button gridCoordinatesButton = (Button) findViewById(R.id.hole_grid_button);
        if(drillLog != null) {
            isEdit = true;
            setDrillLogData(drillLog);
        }
        else
        {
            drillLog = new DrillLog();
            drillLog.setDrillerName(ProjectKeep.getInstance().getUserName());
            driller_name.setText(drillLog.getDrillerName());
            drillLog.setGridCoordinates( new ArrayList<GridCoordinate>());
        }

        //final Project project = ProjectListActivity.projects.get(position);
        //DrillLog drillLog = new DrillLog(null, 0, new Date(), new ArrayList<GridCoordinate>());
        String signature = process.getStringExtra("signature");
        if (signature != null && signature.length() > 0)
        {

            String signatureName = process.getStringExtra("signatureName");
            String signaturePerson =  process.getStringExtra("signaturePerson");
            if (signaturePerson != null) {
                if (signaturePerson.equals("Customer")) {
                    drillLog.setCustomerSignature(signature);
                    drillLog.setCustomerSignatureName(signatureName);
                    drillLog.setCustomerSignatureDate(new Date());
                }
                else {
                    drillLog.setSupervisorSignature(signature);
                    drillLog.setSupervisorSignatureName(signatureName);
                    drillLog.setSupervisorSignatureDate(new Date());
                }
                setDrillLogData(drillLog);
                // update the db
                updateSignature= true;
                saveDrillLogData(project);
            }
        }

        setTitle(project.getProjectName());

        if(gridCoordinatesButton !=null) {
            gridCoordinatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    next = GridActivity.class;
                    validator.validate();
                }
            });
        }
    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate our menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.save, menu);

        return true;
    }

    /**
     * This method is called when one of the menu items to selected. These items
     * can be on the Action Bar, the overflow menu, or the standard options menu. You
     * should return true if you handle the selection.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                // Here we might start a background refresh task
                validator.validate();
                return true;

            default:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.putExtra("id", project.getId());
                startActivity(intent);
                return true;
        }
    }

    public void setDrillLogData(DrillLog drillLog){
        boolean customerSigned = false;
        boolean supervisorSigned = false;

        log_name.setText(drillLog.getName());
        driller_name.setText(drillLog.getDrillerName());
        pattern_name.setText(drillLog.getPattern());
        shot_number.setText(drillLog.getShotNumber().toString());
        bit_size.setText(drillLog.getBitSize());
        if (drillLog.getCustomerSignature() != null) {
            setImage(drillLog.getCustomerSignature(), customerSignatureImage);
            customerNameDate.setText(drillLog.getCustomerSignatureName()+" "+drillLog.getCustomerSignatureDate());
            customerSigned = true;
        }
        if (drillLog.getSupervisorSignature() != null) {
            setImage(drillLog.getSupervisorSignature(), supervisorSignatureImage);
            supervisorNameDate.setText(drillLog.getSupervisorSignatureName()+" "+drillLog.getSupervisorSignatureDate());
            supervisorSigned = true;
        }
        if (customerSigned && supervisorSigned) {
            gridEditable = false;
            log_name.setEnabled(false);
            driller_name.setEnabled(false);
            pattern_name.setEnabled(false);
            shot_number.setEnabled(false);
            bit_size.setEnabled(false);
        }
    }

    private void setImage(String signature, ImageView view) {
        byte[] bytes = Base64.decode(signature, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);
        view.setImageBitmap(bitmap);
    }

    private void getAndroidFields() {
        log_name = (EditText) findViewById(R.id.drill_log_name_text_field);
        driller_name = (EditText) findViewById(R.id.driller_name_text_field);
        pattern_name = (EditText) findViewById(R.id.pattern_name_text_field);
        shot_number = (EditText) findViewById(R.id.shot_number_text_field);
        bit_size = (EditText) findViewById(R.id.bit_size_text_field);
    }

    public String saveDrillLogData(Project project)
    {
        String name = log_name.getText().toString();
        String drillerName = driller_name.getText().toString();
        String patternName = pattern_name.getText().toString();
        String shotNumberString = shot_number.getText().toString();
        String bitSize = bit_size.getText().toString();

        drillLog.setName(name);
        drillLog.setDrillerName(drillerName);
        drillLog.setPattern(patternName);
        drillLog.setShotNumber(Integer.valueOf(shotNumberString));
        drillLog.setBitSize(bitSize);
        if (!isEdit){
            project.addDrillLog(drillLog);
        }

        AsyncTaskRunner drillLogTaskRunner = new AsyncTaskRunner();
        asyncTask = drillLogTaskRunner.execute();

        return asyncTask.getStatus().toString();
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, UpdateStatus> {
        Gson gson = new Gson();

        @Override
        protected UpdateStatus doInBackground(String... params) {
            UpdateStatus status = null;
            if (isConnected()) {
                try {
                    String result = ProjectSync.getInstance().updateDrillLog(isEdit, project, drillLog);
                    status = gson.fromJson(result, UpdateStatus.class);
                } catch (Exception e) {
                    status = new UpdateStatus(false, e.getMessage());
                    drillLog.setDirty(true);
                    e.printStackTrace();
                }
            }
            else {
                drillLog.setDirty(true);
                status = new UpdateStatus(true, "Saving offline");
            }

            if (drillLog.isDirty() && project.getId() != null ) {
                ProjectAvailableOfflineStatus.getInstance().setIsAvailableOffline(project.getId(), true);
            }

            if (ProjectAvailableOfflineStatus.getInstance().isAvailableOffline(project.getId())) {
                ProjectKeep.getInstance().saveProjectToFile(project);
            }
            return status;
        }
        @Override
        protected void onPostExecute(UpdateStatus status) {
            // is we are not updating the signature then move to the next activity
            if (!updateSignature) {
                Toast.makeText(getApplicationContext(), status.getMessage(), Toast.LENGTH_SHORT).show();
                if (status.getSuccess()) {
                    nextActivity();
                }
            }
            updateSignature = false;
        }
    }

    public void onValidationSucceeded() {
        saveDrillLogData(project);
    }

    private void nextActivity() {
        Intent toActivity = new Intent(DrillLogActivity.this, next);
        toActivity.putExtra("id", project.getId());
        if (next == GridActivity.class || next == SignatureActivity.class) {
            if (drillLog.getId() == null) {
                toActivity.putExtra("drill.name", drillLog.getName());
            }
            else {
                toActivity.putExtra("drillId", drillLog.getId());
            }
            // do this also
            if (next == SignatureActivity.class) {
                toActivity.putExtra("signaturePerson", type);
            }
            if (next == GridActivity.class) {
                toActivity.putExtra("editable", gridEditable);
            }
        }
        startActivity(toActivity);
        finish();
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
/*
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data.getExtras() != null) {
            Bitmap bp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.PNG,100,bos);
            byte[] bb = bos.toByteArray();
            String image = Base64.encodeToString(bb, Base64.DEFAULT);
            drillLog.setSignature(image);
            customerSignatureImage.setImageBitmap(bp);
        }
    }
*/
}
