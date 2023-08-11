package org.linphone.onu_legacy.Activities.Activities;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.Info;
import com.onutiative.onukit.Utility.SharedPrefManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Setting_Activity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    public String inboxStat = null;
    public String reportStat = null;
    public String callreportStat = null;
    public String user_type = null;
    private TextView pullCount;
    private Info info;
    public ImageView Check;
    public ImageView reportCheck;
    public ImageView callblockCheck;

    public Switch appBarNotificationSwitch,voiceRecorderSwitch,smsSwitch,callSwitch,reportSwitch;
    public Switch incomingCallblockSwitch, popupStatusSwitch, popupVibrationSwitch;

    private String prefName = "onuPref";
    private SharedPreferences.Editor prefEditor;
    SharedPreferences sharedPref;
    private SharedPrefManager sharedPrefManager;
    private Context context;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static final String TAG = "Setting_Activity";


    private final int NOTIFICATION_ID = 1245;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        getSupportActionBar().hide();

//        Check = (ImageView) findViewById(R.id.checkBox);
//        reportCheck = (ImageView) findViewById(R.id.reportcheck);
//        callblockCheck = (ImageView) findViewById(R.id.callcheckBox);
        pullCount = (TextView) findViewById(R.id.countpull);

//        incomingCallblockSwitch=(Switch)findViewById(R.id.incomingCallblockSwitch) ;
        info = new Info(Setting_Activity.this);

        context = Setting_Activity.this;
        sharedPrefManager = new SharedPrefManager(context);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        Log.d(TAG, DebugDB.getAddressLog());
//        showDebugDBAddressLogToast(context);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashBoard_Activity.class));
            }
        });


        sharedPref = getSharedPreferences(prefName, MODE_PRIVATE);


        prefEditor = (SharedPreferences.Editor) getSharedPreferences(prefName, MODE_PRIVATE).edit();


        //TextView reactivation= (TextView) findViewById(R.id.reactivation);
        TextView deactivation = (TextView) findViewById(R.id.deactivation);
        TextView aboutOnu = (TextView) findViewById(R.id.abtOnu);
        TextView adminNumber = (TextView) findViewById(R.id.adminNumber);
        TextView changeUrl = (TextView) findViewById(R.id.changurl);
        TextView clearData = (TextView) findViewById(R.id.clearall);
        TextView statusReport = (TextView) findViewById(R.id.statusReport);
        TextView permissionOverView = findViewById(R.id.permission_view);

        appBarNotificationSwitch = (Switch) findViewById(R.id.icTopNotificationSwitch);
        voiceRecorderSwitch=(Switch)findViewById(R.id.voiceRecorderSwitch);
        popupStatusSwitch = (Switch) findViewById(R.id.app_popup_switch);
        popupVibrationSwitch = (Switch) findViewById(R.id.app_popup_vibration_switch);
        smsSwitch=(Switch)findViewById(R.id.smsSwitch);
        callSwitch=(Switch)findViewById(R.id.callSwitch);
        reportSwitch=(Switch)findViewById(R.id.reportSwitch);
        checkActive();


        appBarNotificationSwitch.setChecked(sharedPref.getBoolean("topBarNotification", true));
        popupStatusSwitch.setChecked(sharedPrefManager.getPopupStatus());
        popupVibrationSwitch.setChecked(sharedPrefManager.getPopupVibrationStatus());
        voiceRecorderSwitch.setChecked(sharedPrefManager.getCallRecordingFlag());

        //incoming sms block
        smsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Database db = new Database(Setting_Activity.this);
                //updateAdminInbox
                if (isChecked) {
                    db.updateAdminInbox("on");
                } else {
                    db.updateAdminInbox("off");
                }
                checkActive();

            }
        });
        //incoming call block
        callSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("Jhoro", "check callblock________________" + callreportStat);

                //  Toast.makeText(Setting_Activity.this,"Clicked from checkbox",Toast.LENGTH_SHORT).show();

                Database db = new Database(Setting_Activity.this);

                //updateAdminInbox
                if (isChecked) {
                    db.updateCallBlock("on");
                } else {
                    db.updateCallBlock("off");
                }
                checkActive();
            }
        });

        //turn off delevery report
        reportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.v("JhoroSmSReport", "check report________________" + reportStat);
                Database db = new Database(Setting_Activity.this);
                //updateAdminInbox
                if (isChecked) {
                    Log.v("JhoroSmSReport", "check report off________________" + reportStat);
                    db.updateAdminReport("on");
                } else {
                    Log.v("JhoroSmSReport", "check report on________________" + reportStat);
                    db.updateAdminReport("off");
                }
                checkActive();

            }
        });

        //recorder operation
        voiceRecorderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    sharedPrefManager.setCallRecordingFlag(true);
                    if (sharedPrefManager.getCallRecordingFlag()){
                        Log.i(TAG,"Switch ON");
                        Toast.makeText(context, "Call recorder feature is enabled.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    sharedPrefManager.setCallRecordingFlag(false);
                    if (!sharedPrefManager.getCallRecordingFlag()){
                        Log.i(TAG,"Switch OFF");
                        Toast.makeText(context, "Call recorder feature is disabled.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        appBarNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
//                    Toast.makeText(Setting_Activity.this,"True",Toast.LENGTH_SHORT).show();

                    Intent ii = new Intent(Setting_Activity.this, PrivacyPolicy_Activity.class);


                    ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent contentIntent = PendingIntent.getActivity(Setting_Activity.this, 0, ii, 0);
                    Notification myNotification = new NotificationCompat.Builder(Setting_Activity.this)
                            .setContentTitle(getString(R.string.notification_heading))
                            .setContentText(getString(R.string.notification_body))
                            .setTicker("Notification!")
                            .setContentIntent(contentIntent)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setOngoing(true)
                            .setSmallIcon(R.drawable.onukit_logo2)
                            .build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(NOTIFICATION_ID, myNotification);


                    prefEditor.putBoolean("topBarNotification", true);
                    prefEditor.commit();


                } else {
//                    Toast.makeText(Setting_Activity.this,"False",Toast.LENGTH_SHORT).show();

                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(NOTIFICATION_ID);


                    prefEditor.putBoolean("topBarNotification", false);
                    prefEditor.commit();
                }

            }
        });

        popupStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {


                        //If the draw over permission is not available open the settings screen
                        //to grant the permission.
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                    } else {
                        sharedPrefManager.setPopupStatus(true);
                        Toast.makeText(context, "Popup feature is enabled.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    sharedPrefManager.setPopupStatus(false);
                    Toast.makeText(context, "Popup feature is disabled.", Toast.LENGTH_LONG).show();
                }
            }
        });

        popupVibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    sharedPrefManager.setPopupVibrationStatus(true);
                    Toast.makeText(context, "Vibration on popup closing - enabled.", Toast.LENGTH_LONG).show();
                } else {
                    sharedPrefManager.setPopupVibrationStatus(false);
                    Toast.makeText(context, "Vibration on popup closing - disabled.", Toast.LENGTH_LONG).show();
                }

            }
        });


        clearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting_Activity.this);
                alertDialog.setTitle("OnuKit");
                alertDialog.setMessage("You want to clear all previous data from OnuKit ?");
                final TextView input = new TextView(Setting_Activity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Database db = new Database(Setting_Activity.this);
                                db.deleteallsent();
                                db.deleteAll();
                            }
                        });
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });


        adminNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting_Activity.this);
                alertDialog.setTitle("Admin Number");
                alertDialog.setMessage("Enter Your Number:\n[example: number1,number2,...]");
                final EditText input = new EditText(Setting_Activity.this);
                Database db = new Database(Setting_Activity.this);
                List<Contact> contacts = db.getAdminNumber();
                for (Contact cn : contacts) {
                    if (cn.getName().equals("Admin numbers"))
                        input.setText(cn.getPhone_number());
                    //Log.v("Jhoro", "sms sendng");
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);
                alertDialog.setPositiveButton("SAVE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String Admin_number = input.getText().toString();
                                Database db = new Database(Setting_Activity.this);
                                //db.deleteAllAdmin();
                                db.deleteAdmin("Admin numbers", "Onuserver Cannot send data");
                                db.addAdminNumber(new Contact("Admin numbers", Admin_number, "Onuserver Cannot send data"));
                                //Toast.makeText(Setting_Activity.this, "Admin Number:\n" + Admin_number, Toast.LENGTH_LONG).show();
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.setNeutralButton("SHOW",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String a_number = null;
                                Database db = new Database(Setting_Activity.this);
                                List<Contact> contacts = db.getAdminNumber();
                                for (Contact cn : contacts) {
                                    a_number = cn.getPhone_number();
                                    if (cn.getName().equals("Admin numbers"))
                                        Toast.makeText(Setting_Activity.this, "Admin Number:\n" + a_number, Toast.LENGTH_LONG).show();
                                    //Log.v("Jhoro", "sms sendng");
                                }//123
                                //Toast.makeText(Set.this, "Admin Number:\n"+a_number, Toast.LENGTH_LONG).show();
                                dialog.cancel();
                            }
                        });
                alertDialog.show();

            }
        });

        changeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_type.equals("Enterprise")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting_Activity.this);
                    alertDialog.setTitle("Change URL");
                    alertDialog.setMessage("");
                    final EditText input = new EditText(Setting_Activity.this);
                    Database db = new Database(Setting_Activity.this);
                    List<Contact> contacts = db.getAdminNumber();
                    for (Contact cn : contacts) {
                        if (cn.getName().equals("Custom_url"))
                            input.setText(cn.getPhone_number());
                            Log.e("Custom URL ", " Custom URL is " + cn.getPhone_number());
                            //Log.v("Jhoro", "sms sendng");
                    }//123
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setIcon(R.drawable.onukit_logo2);
                    alertDialog.setPositiveButton("SAVE",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String custom_url = input.getText().toString();
                                    Database db = new Database(Setting_Activity.this);
                                    //db.deleteAllAdmin();
                                    db.deleteAdmin("Custom_url", "Onuserver");
                                    db.addAdminNumber(new Contact("Custom_url", custom_url, "Onuserver"));
                                    //Toast.makeText(Setting_Activity.this, "your url:\n" + custom_url, Toast.LENGTH_LONG).show();
                                }
                            });


                    alertDialog.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                    alertDialog.setNeutralButton("DEFAULT",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String a_number = null;
                                    Database db = new Database(Setting_Activity.this);
                                    db.deleteAdmin("Custom_url", "Onuserver");
                                    db.addAdminNumber(new Contact("Custom_url", "http://api.onuserver.com/6v1", "Onuserver"));
                                   // Toast.makeText(Setting_Activity.this, "your url:\nhttp://api.onuserver.com/6v1", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                } else {
                    Database db = new Database(Setting_Activity.this);
                    List<Contact> contacts = db.getAdminNumber();
                    for (Contact cn : contacts) {
                        if (cn.getName().equals("Custom_url")) {
                            Log.e("Custom URL ", " Custom URL is " + cn.getPhone_number());
                            // Toast.makeText(Setting_Activity.this," Custom URL is "+cn.getPhone_number(),Toast.LENGTH_SHORT).show();
                        }
                        //Log.v("Jhoro", "sms sendng");
                    }

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting_Activity.this);
                    alertDialog.setTitle("Change URL");
                    alertDialog.setMessage("Update to Enterprize edition for this option.");
                    final TextView input = new TextView(Setting_Activity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setIcon(R.drawable.onukit_logo2);
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }

                            });

                    alertDialog.show();
                    //incridibully the logic workes here for the first thing to be continuied
                }

            }
        });

        permissionOverView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Setting_Activity.this, PermissionResultActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        deactivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting_Activity.this);
                alertDialog.setTitle("OnuKit");
                alertDialog.setMessage("Do you want to De-Activate OnuKit ?");
                final TextView input = new TextView(Setting_Activity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("JhoroDeactive", "called");
                                new DeactivateDevice(Setting_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();

            }
        });


        statusReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Setting_Activity.this, NotificationList_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        aboutOnu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Setting_Activity.this);
                alertDialog.setTitle("Outbox Pull count");
                alertDialog.setMessage("");
                final EditText input = new EditText(Setting_Activity.this);
                Database db = new Database(Setting_Activity.this);
                List<Contact> contacts = db.getAdminNumber();
                for (Contact cn : contacts) {
                    if (cn.getName().equals("pullcount"))
                        input.setText(cn.getPhone_number());

                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);

                alertDialog.setPositiveButton("SAVE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String pullcount = input.getText().toString();
                                if (pullcount.matches("\\d+(?:\\.\\d+)?")) {
                                    Database db = new Database(Setting_Activity.this);
                                    db.deleteAdmin("pullcount", "Onuserver");
                                    db.addAdminNumber(new Contact("pullcount", pullcount, "Onuserver"));
                                } else {
                                    Toast.makeText(Setting_Activity.this, "count should be number", Toast.LENGTH_LONG).show();

                                }
                                checkActive();


                            }
                        });

                alertDialog.setNegativeButton("CANCLE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });


                alertDialog.show();


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.


//            if (resultCode == RESULT_OK) {
//                sharedPrefManager.setPopupStatus(true);
//                Toast.makeText(context,"Draw over other app permission granted. Popup feature enabled.",Toast.LENGTH_LONG).show();
//            } else { //Permission is not available
//                Toast.makeText(this,
//                        "Draw over other app permission not granted.",
//                        Toast.LENGTH_SHORT).show();
//                popupStatusSwitch.setChecked(false);
//
////                finish();
//            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {

                sharedPrefManager.setPopupStatus(true);
                popupStatusSwitch.setChecked(true);
                Toast.makeText(context, "Draw over other app permission granted. Popup feature enabled.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Draw all over the apps permission granted!");

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) { //Permission is not available
                Toast.makeText(this, "Draw all over the apps permission not granted!", Toast.LENGTH_LONG).show();
                sharedPrefManager.setPopupStatus(false);
                popupStatusSwitch.setChecked(false);
                Log.d(TAG, "Draw all over the apps permission not granted!");


            }


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void checkActive() {

        pullCount.setText(info.getPullCount());
        Database db = new Database(getApplicationContext());
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {

            if (cn.getName().equals("inbox")) {
                inboxStat = cn.getPhone_number();
                if (inboxStat.equals("on")) {
                    //Check.setBackgroundResource(R.drawable.checkboxon);
                    smsSwitch.setChecked(true);
                } else {
                    //Check.setBackgroundResource(R.drawable.checkboxoff);
                    smsSwitch.setChecked(false);
                }
                //Check
            } else if (cn.getName().equals("report")) {
                reportStat = cn.getPhone_number();
                if (reportStat.equals("on")) {
                    //reportCheck.setBackgroundResource(R.drawable.checkboxon);
                    reportSwitch.setChecked(true);
                } else {
                    //reportCheck.setBackgroundResource(R.drawable.checkboxoff);
                    reportSwitch.setChecked(false);
                }
                //Check
            } else if (cn.getName().equals("callblock")) {
                callreportStat = cn.getPhone_number();
                if (callreportStat.equals("on")) {
                    //callblockCheck.setBackgroundResource(R.drawable.checkboxon);
                    callSwitch.setChecked(true);
//                    incomingCallblockSwitch.setChecked(true);
                } else {
                    //callblockCheck.setBackgroundResource(R.drawable.checkboxoff);
                    callSwitch.setChecked(false);
//                    incomingCallblockSwitch.setChecked(false);
                }
                //Check
            } else if (cn.getName().equals("apptype")) {

                user_type = cn.getPhone_number();
            }
        }

    }


    public class DeactivateDevice extends AsyncTask<Void, Void, String> {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        Database db;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Log.i("JhoroDeactive", "called pre execute");
        }

        public DeactivateDevice(Context context) {
            this.context = context;
            this.db = new Database(context);

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("JhoroDeactive", "call_post");

            if (result != null) {
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                db.deleteAllAdmin();
                db.deleteAll();
                db.deletealloutbox();
                db.deleteallsent();
                db.deleteallMsg();
                db.deleteAllCall();
                db.deletealloutboxtow();
                db.deleteAllsms();

                Intent i = new Intent(Setting_Activity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(context, "Unable to Connect", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String status = null;
                String success = "4000";
                int statusCode = 0;
                String username = info.getUsername();
                String password = info.getPassword();


                Log.i("JhoroDeactive", "call_background");
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpParams p = new BasicHttpParams();
                HttpClient httpclient = new DefaultHttpClient(p);

                //String url = "https://www.mydomainic.com/api/bkash-central/demo/add/via-sms/";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("demo", "demo"));
                HttpClient httpClient = new DefaultHttpClient();
                String paramsString = URLEncodedUtils.format(nameValuePairs,
                        "UTF-8");
                HttpPost httppost = new HttpPost(info.getUrl() + "/deactiveDevice");
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                httppost.setHeader("Content-type", "application/json");

                //---------------------Code for Basic Authentication-----------------------
                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
                httppost.setHeader("Authorization", "Basic " + credBase64);
                //----------------------------------------------------------------------

                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("trnxID", info.getDate("kkyyyyMMddkkmmss"));
                jsonParam.accumulate("deviceID", info.getImei());
                jsonParam.accumulate("trnxTime", info.getDate("yyyy-MM-dd kk:mm:ss"));


                StringEntity myStringEntity = new StringEntity(jsonParam.toString(), "UTF-8");
                Log.i("JhoroDeactive", "call_my jSon: " + jsonParam.toString());
                httppost.setEntity(myStringEntity);
                HttpResponse response = httpclient.execute(httppost);
                String res = EntityUtils.toString(response.getEntity());
                Log.i("JhoroDeactive", "now" + res);
                JSONObject json = new JSONObject(res);
                Log.i("JhoroDeactive", json.toString());
                status = json.getString("status");  //getting from  jSon body
                statusCode = response.getStatusLine().getStatusCode();
                Log.i("JhoroDeactive", "Call_my Status: " + statusCode);
                if (statusCode >= 200 && statusCode <= 299) {
                    Log.i("JhoroDeactive", "Statu: ");
                    if (status.equals("4000"))
                        return json.getString("reason");
                } else
                    return null;
            } catch (Exception ex) {
                Log.i("JhoroDeactive", " Exception: " + ex);
                return null;
            }
            return null;
        }

    }



//    public static void showDebugDBAddressLogToast(Context context) {
//        if (BuildConfig.DEBUG) {
//            try {
//                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
//                Method getAddressLog = debugDB.getMethod("getAddressLog");
//                Object value = getAddressLog.invoke(null);
//                Toast.makeText(context, (String) value, Toast.LENGTH_LONG).show();
//            } catch (Exception ignore) {
//
//            }
//        }
//    }




}
