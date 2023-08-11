package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.SharedPrefManager;

public class PermissionResultActivity extends AppCompatActivity {


    private int callPermissionResult, smsPermissionResult, contactPermissionResult, storagePermissionResult, microphonePermissionResult;
    private boolean popupPermissionResult;
    private Context context;
    private Button finishButton;
    private String TAG=PermissionResultActivity.class.getSimpleName();
    private ImageView callPermissionCross, smsPermissionCross, contactPermissionCross,
            storagePermissionCross, microphonePermissionCross, popupPermissionCross,
            callPermissionTick, smsPermissionTick, contactPermissionTick,
            storagePermissionTick, microphonePermissionTick, popupPermissionTick;

    private TextView decisionText;
    private SharedPrefManager sharedPrefManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=PermissionResultActivity.this;
        sharedPrefManager=new SharedPrefManager(context);


        finishButton=(Button)findViewById(R.id.button_finish);
        callPermissionCross=(ImageView)findViewById(R.id.call_permission_cross);
        smsPermissionCross=(ImageView)findViewById(R.id.sms_permission_cross);
        contactPermissionCross=(ImageView)findViewById(R.id.contact_permission_cross);
        storagePermissionCross=(ImageView)findViewById(R.id.storage_permission_cross);
        microphonePermissionCross=(ImageView)findViewById(R.id.microphone_permission_cross);
        popupPermissionCross=(ImageView)findViewById(R.id.draw_all_over_apps_permission_cross);

        callPermissionTick=(ImageView)findViewById(R.id.call_permission_tick);
        smsPermissionTick=(ImageView)findViewById(R.id.sms_permission_tick);
        contactPermissionTick=(ImageView)findViewById(R.id.contact_permission_tick);
        storagePermissionTick=(ImageView)findViewById(R.id.storage_permission_tick);
        microphonePermissionTick=(ImageView)findViewById(R.id.microphone_permission_tick);
        popupPermissionTick=(ImageView)findViewById(R.id.draw_all_over_apps_permission_tick);

        decisionText=(TextView)findViewById(R.id.text_decision);

        callPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        smsPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        contactPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        storagePermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        microphonePermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            popupPermissionResult= Settings.canDrawOverlays(context);
        }
        else
        {
            popupPermissionResult=true;
        }


        if(callPermissionResult==0)
        {
            callPermissionCross.setVisibility(View.GONE);
            callPermissionTick.setVisibility(View.VISIBLE);
        }
        if(smsPermissionResult==0)
        {
            smsPermissionCross.setVisibility(View.GONE);
            smsPermissionTick.setVisibility(View.VISIBLE);
        }
        if(contactPermissionResult==0)
        {
            contactPermissionCross.setVisibility(View.GONE);
            contactPermissionTick.setVisibility(View.VISIBLE);
        }
        if(storagePermissionResult==0)
        {
            storagePermissionCross.setVisibility(View.GONE);
            storagePermissionTick.setVisibility(View.VISIBLE);
        }
        if(microphonePermissionResult==0)
        {
            microphonePermissionCross.setVisibility(View.GONE);
            microphonePermissionTick.setVisibility(View.VISIBLE);
        }
        if(popupPermissionResult)
        {
            popupPermissionCross.setVisibility(View.GONE);
            popupPermissionTick.setVisibility(View.VISIBLE);
        }

        if(callPermissionResult==0 && smsPermissionResult==0 && contactPermissionResult==0 && storagePermissionResult==0
                && microphonePermissionResult==0 && popupPermissionResult)
        {
            decisionText.setText(getString(R.string.permission_success));
        }


        if(!sharedPrefManager.getPermissionSlideStatus())
        {
            finishButton.setText("Go To DASHBOARD");
        }

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG,"Call Permission Result "+callPermissionResult+" SMS Permission Result "+smsPermissionResult+
                        " Contact Permission Result "+contactPermissionResult+" Storage Permission Result "+storagePermissionResult+
                        " Microphone Permission Result "+microphonePermissionResult+" Popup Permission Result "+popupPermissionResult);

                if(sharedPrefManager.getPermissionSlideStatus())
                {
                    //First time after installing.
                    sharedPrefManager.setPermissionSlideStatus(false);
                    Intent intent =new Intent(context, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    //Accessing from dashboard.
                    Intent intent =new Intent(context,DashBoard_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void callPermission(View view) {
        goSlider(1);
    }

    public void smsPermission(View view) {
        goSlider(2);
    }

    public void contactPermission(View view) {
        goSlider(3);
    }

    public void recordPermission(View view) {
        goSlider(4);
    }

    public void storagePermission(View view) {
        goSlider(5);
    }

    public void popupPermission(View view) {
        goSlider(6);
    }

    void goSlider(int slideNo){
        Intent intent = new Intent(PermissionResultActivity.this,RuntimePermissionActivity.class);
        intent.putExtra("sliderNo",slideNo);
        intent.putExtra("from","permissionResult");
        startActivity(intent);
    }
}
