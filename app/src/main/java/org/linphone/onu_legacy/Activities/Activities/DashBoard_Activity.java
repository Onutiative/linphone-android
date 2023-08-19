package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.linphone.R;
import org.linphone.onu_legacy.Activities.CallLog_Activity;
import org.linphone.onu_legacy.Activities.OutgoingSent_Activity;
import org.linphone.onu_legacy.Activities.PopupCallListActivity;
import org.linphone.onu_legacy.AsyncTasking.CheckOnline;
import org.linphone.onu_legacy.AsyncTasking.FetchPermissions;
import org.linphone.onu_legacy.AsyncTasking.PullServerSms;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.ContactPackage.ContactActivity;
import org.linphone.onu_legacy.MVP.Implementation.PostedSMSPAckage.Posted_Inbox_Activity;
import org.linphone.onu_legacy.MVP.Implementation.SmsSendPackage.SmsActivity;
import org.linphone.onu_legacy.MVP.Implementation.TaskPackage.TaskShowActivity;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet.ContactSummaryData;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet.ContactSummeryGroup;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Address;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Contacts;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Profession;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Profile;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Relation;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.VirtualContact;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.ContactSummaryPullRepository;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.ContactSyncRepository;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.TaskSummaryPullRepository;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskSummaryData;
import org.linphone.onu_legacy.Services.BackgroundService;
import org.linphone.onu_legacy.Utility.Constants;
import org.linphone.onu_legacy.Utility.Helper;
import org.linphone.onu_legacy.Utility.Info;
import org.linphone.onu_legacy.Utility.SharedPrefManager;
import org.linphone.onu_legacy.WebViews.WebViews;
import org.linphone.onuspecific.OnuFunctions;

import com.timqi.sectorprogressview.SectorProgressView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.grabner.circleprogress.CircleProgressView;
//<!--used in 6v3-->

public class DashBoard_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        TaskSummaryPullRepository.TaskSummaryListener, ContactSummaryPullRepository.ContactSummaryListener {


    /******************************************************
     **********All PreDefined Variables*********************
     ******************************************************/
    private SliderLayout mDemoSlider;
    //    private String url1 = "http://user.onukit.com/6v0/downloads/slider/1.jpg";
//    private String url2 = "http://user.onukit.com/6v0/downloads/slider/2.jpg";
//    private String url3 = "http://user.onukit.com/6v0/downloads/slider/3.jpg";
    private ImageView indicator;
    private Info info;
    private Database db;
    public ProgressDialog progressBar;
    private AnimationDrawable rocketAnimation;
    private BackgroundService mSensorService;
    Intent mServiceIntent;
    private TextView appEdition, email, SmsinSuccess, SmsinFailed, SmsinPending, SmsOutSuccess, SmsOutFailed, SmsOutPending,
            CallinSuccess, CallOutSuccess, pendingTaskSubmission, totalTask, pendingTask, totalContact, clientContacts;
    private ImageView callInQuota, callOutQuota, smsInQuota, smsOutQuota;
//    private AdView mAdView;
//    private AdRequest adRequest;
    private HashMap<String, String> url_maps;
    private ImageView refreshBtn;
    private ImageView inSms, inCall, outCall, outSms;
    public String id = "2";
    private Activity activity;
    private Context context;
    public String imei;
    private String uname = null, upass = null, url = null, urlForEdition = null, userId = null, parentID = "0";
    //    private String url = "http://demo.onukit.com/api/contactManagement";
    private static final int PERMISSION_REQUEST_CODE = 11;

    private String TAG = "DashBoard_Activity";
    String user_email, user_pass;

    private String prefName = "onuPref";
    SharedPreferences.Editor prefEditor;
    SharedPreferences sharedPref;

    private boolean syncContact, topBarNotification;
    private DrawerLayout drawerLayout;

    private SectorProgressView outgoingSmsPieChart, incomingSmsPieChart, outgoingCallPieChart, incomingCallPieChart;

    private CircleProgressView outgoingSmsCircleProgressView, incomingSmsCircleProgressView,
            outgoingCallCircleProgressView, incomingCallCircleProgressView, taskProgressView, contactCircleProgressView;

    private CardView outgoingSmsCardView, incomingSmsCardView, outgoingCallCardView, incomingCallCardView, task_card_view, contact_card_view;
    private SharedPrefManager sharedPrefManager;
    private LinearLayout pendingTaskClick;
    private Helper helper;

    private TaskSummaryPullRepository taskSummaryPullRepository;
    private ContactSummaryPullRepository contactSummaryPullRepository;

    /******************************************************
     **********All Override functions*********************
     ******************************************************/


    // The notification id for Notification is 1245. If you want to clear this, you've to use this id number.
    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refreshbtn:
                Button_click_refresh();
                break;
            case R.id.incomingcall:
//                Intent i_a = new Intent(DashBoard_Activity.this, CallLog_Activity.class);
                //  This part is commented for just a moment.
                Intent i_a = new Intent(DashBoard_Activity.this, PopupCallListActivity.class);
                i_a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i_a.putExtra("type", "in");
                startActivity(i_a);
                break;
            case R.id.outgoingcall:
                Intent i_b = new Intent(DashBoard_Activity.this, CallLog_Activity.class);
                i_b.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i_b.putExtra("type", "out");
                startActivity(i_b);
                break;
            case R.id.outgoingsms:
                Intent i_d = new Intent(DashBoard_Activity.this, OutgoingSent_Activity.class);
                i_d.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i_d);
                break;
//            case R.id.infoButton:
//                Button_info();
//                break;
            case R.id.indicator:
                AlertInfo();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        sharedPref = getSharedPreferences(prefName, MODE_PRIVATE);

        topBarNotification = sharedPref.getBoolean("topBarNotification", true);
        context = DashBoard_Activity.this;
        sharedPrefManager = new SharedPrefManager(context);
        helper = new Helper(context);

        //Database db=new Database(this);

        Log.e(TAG, "Dashboard Initialized!");

        InitDashBoard();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(DashBoard_Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 210);
            //return;
            openAlarDialog();
        } else {
            new CheckOnline(this, info.getImei()).execute();
        }
        //new CheckOnline(this, info.getImei()).execute();
        info = new Info(DashBoard_Activity.this);

        prefEditor = (SharedPreferences.Editor) getSharedPreferences(prefName, MODE_PRIVATE).edit();

        syncContact = sharedPref.getBoolean("contactDialog", true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Intent intent = getIntent();

        user_email = intent.getStringExtra(LoginActivity.USEREMAIL);
        user_pass = intent.getStringExtra(LoginActivity.USERPASS);

        Map<String, String> userCredentials = new OnuFunctions().getUserCredentials();
        user_email = userCredentials.get("username");
        user_pass = userCredentials.get("password");

        outgoingSmsCardView = (CardView) findViewById(R.id.outgoing_sms_card_view);
        incomingSmsCardView = (CardView) findViewById(R.id.incoming_sms_card_view);
        outgoingCallCardView = (CardView) findViewById(R.id.outgoing_call_card_view);
        incomingCallCardView = (CardView) findViewById(R.id.incoming_call_card_view);
        task_card_view = findViewById(R.id.task_card_view);
        pendingTaskClick=findViewById(R.id.pendingTaskClick);
        contact_card_view = findViewById(R.id.contact_card_view);

        outgoingSmsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent outoingSmsIntent = new Intent(DashBoard_Activity.this, OutgoingSent_Activity.class);
//                Intent outoingSmsIntent = new Intent(DashBoard_Activity.this, SmsLogActivity.class);
                outoingSmsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(outoingSmsIntent);
            }
        });

        incomingSmsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent incomingSmsIntent = new Intent(DashBoard_Activity.this, Posted_Inbox_Activity.class);
                incomingSmsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(incomingSmsIntent);
                Log.i(TAG, "Incomming SMS card clicked");
            }
        });

        outgoingCallCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent outgoingCallIntent = new Intent(DashBoard_Activity.this, CallLog_Activity.class);
                outgoingCallIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                outgoingCallIntent.putExtra("type", "out");
                startActivity(outgoingCallIntent);
            }
        });

        incomingCallCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   Intent i_a = new Intent(DashBoard_Activity.this, CallLog_Activity.class);
                //  This part is commented for just a moment.
                Intent incomingCallIntent = new Intent(DashBoard_Activity.this, PopupCallListActivity.class);
                incomingCallIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                incomingCallIntent.putExtra("type", "in");
                startActivity(incomingCallIntent);
            }
        });
        task_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard_Activity.this, TaskShowActivity.class);
                intent.putExtra("taskType","All");
                startActivity(intent);
            }
        });
        pendingTaskClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard_Activity.this, TaskShowActivity.class);
                intent.putExtra("taskType","Pending");
                startActivity(intent);
            }
        });
        contact_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoard_Activity.this, ContactActivity.class);
                intent.putExtra("selection", false);
                startActivity(intent);
            }
        });

        if (intent.hasExtra("noInternet")) {
            Snackbar snackbar = Snackbar
                    .make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });
            snackbar.show();
        }
        if (intent.hasExtra("data")) {
            Bundle extras = getIntent().getExtras();
            id = extras.getString("data");
        }
        set_app_url();
        // First time after installing or Log in into app.
        // So, when syncContact is false alert dialog will not be prompted!
        if (syncContact) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"Contact permission not granted");
                int PERMISSION_REQUEST_CONTACT=304;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_CONTACT);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS,
                                Manifest.permission.GET_ACCOUNTS},
                        PERMISSION_REQUEST_CONTACT);
            }else {
                syncContact();
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashBoard_Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 210);
        }else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //imei = tm.getDeviceId();
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.v("Device ID: ", imei);
            db.deleteAdmin("did", "jhorotek");
            db.addAdminNumber(new Contact("did", imei, "jhorotek"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 210) {
            ////////////////
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //return;
                openAlarDialog();
            }else {
                new CheckOnline(this, info.getImei()).execute();
//                imei = tm.getDeviceId();
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.i(TAG,"Permission Result: IME "+ imei);
                db.deleteAdmin("did", "jhorotek");
                db.addAdminNumber(new Contact("did", imei, "jhorotek"));
            }
        }
        if (requestCode==304 && grantResults.length>0){
            Log.i(TAG,"Grant Result: "+grantResults[0]);
            syncContact();
        }
    }

    private void openAlarDialog(){
        final androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
        alertDialog.setTitle("Onukit Account Policy");
        alertDialog.setMessage(R.string.account_policy_text);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("I Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(DashBoard_Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 210);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(0);
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InitDashBoard();
    }

    public void syncContact() {

            Log.i(TAG,"Contact permission granted");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoard_Activity.this);
            alertDialog.setTitle("OnuKit");
            alertDialog.setMessage("Do you want to Sync Your Contacts?");
            alertDialog.setIcon(R.drawable.onukit_logo2);
            new HttpEditInfo(DashBoard_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("JhoroContactList", "CList");
                            if (isNetworkAvailable()) {
                                progressBar = ProgressDialog.show(DashBoard_Activity.this, "Contacts", "Contact Syncing...");
                                progressBar.setCancelable(true);
                                progressBar.show();
                                //getPhoneContactAndSyncProcess();
                                new SendNumber(DashBoard_Activity.this).execute();
                                prefEditor.putBoolean("contactDialog",false);
                                prefEditor.commit();

                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                                        .setAction("RETRY", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Snackbar snackbar1 = Snackbar.make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                                                snackbar1.show();
                                            }
                                        });
                                snackbar.show();
                                // Toast.makeText(DashBoard_Activity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // I do 'false' here to stop the alert dialog in dashboard!
                            prefEditor.putBoolean("contactDialog",false);
                            prefEditor.commit();

                            dialog.cancel();
                        }
                    });
            alertDialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.dashboard) {

        } else if (id == R.id.setting) {
            Intent i = new Intent(DashBoard_Activity.this, Setting_Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else if (id == R.id.recentsms) {
            Recent_sms();
        }else if (id==R.id.contactDwr){
            //Toast.makeText(this,"Click on contact",Toast.LENGTH_SHORT).show();
            //new PullServerContact(this,false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            Intent intent = new Intent(DashBoard_Activity.this,ContactActivity.class);
            intent.putExtra("selection",false);
            startActivity(intent);

        } else if (id == R.id.smstoall) {
            Intent i = new Intent(DashBoard_Activity.this, SmsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
        else if (id == R.id.about) {
            Intent i = new Intent(DashBoard_Activity.this, AboutActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        else if (id == R.id.webdashboard) {
            Intent i = new Intent(DashBoard_Activity.this, WebViews.class);

            i.putExtra("url", "http://user.onukit.com/6v0/login_from_app/dashboard");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else if (id == R.id.logout) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoard_Activity.this);
            alertDialog.setTitle("OnuKit");
            alertDialog.setMessage("Do you want to Logout OnuKit ?");
            final TextView input = new TextView(DashBoard_Activity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.onukit_logo2);
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("JhoroLogout", "Called");
                            //new DeactivateDevice(DashBoard_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            Database db = new Database(DashBoard_Activity.this);
                            db.deleteAllAdmin();
                            db.deleteAll();
                            db.deletealloutbox();
                            db.deleteallsent();
                            db.deleteallMsg();
                            db.deleteAllCall();
                            db.deletealloutboxtow();
                            db.deleteAllsms();

                            db.removeallthread(); //Deleting all threads from TABLE_THREAD
                            db.removeall_calls(); // Deleting all incoming and outgoing calls from TABLE_DELETE
                            db.clearAllTask();   // Deleting all the tasks from TABLE_TASK
                            db.removeAll_calls_queue();
                            // these two line for Bulk Sms
                            db.deleteAllOutBoxSmsData();   // delete all ServerSms from TABLE_OUTBOXSMS
                            db.deleteALlSentSms();    // delete all ServerSms from TABLE_SENTSMS
                            // Removing the notification from notification bar.
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1245);
                            sharedPrefManager.logoutUser();
                            //After clearing the shared preferances I'm setting the value false for not showing the RuntimePermissionActivity.
                            sharedPrefManager.setPermissionSlideStatus(false);
                        }
                    });
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        } else if (id == R.id.syncData) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"Contact permission not granted");
                int PERMISSION_REQUEST_CONTACT=304;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_CONTACT);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS,
                                Manifest.permission.GET_ACCOUNTS},
                        PERMISSION_REQUEST_CONTACT);
            }else {
                syncContact();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
//    public void contactSync(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoard_Activity.this);
//        alertDialog.setTitle("OnuKit");
//        alertDialog.setMessage("Do you want to Sync Your Contacts?");
//        alertDialog.setIcon(R.drawable.onukit_logo2);
//        new HttpEditInfo(DashBoard_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        alertDialog.setPositiveButton("YES",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.i("JhoroContactList", "CList");
//                        if (isNetworkAvailable()) {
//                            progressBar = ProgressDialog.show(DashBoard_Activity.this, "Contacts", "Sending...");
//                            progressBar.setCancelable(true);
//                            //getPhoneContactAndSyncProcess();
//                            new SendNumber(DashBoard_Activity.this).execute();
//                            //Toast.makeText(getApplicationContext(), "Successfully Send", Toast.LENGTH_LONG).show();
//                        } else {
//                            Snackbar snackbar = Snackbar
//                                    .make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
//                                    .setAction("RETRY", new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            Snackbar snackbar1 = Snackbar.make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
//                                            snackbar1.show();
//                                        }
//                                    });
//
//                            snackbar.show();
//                        }
//                    }
//                });
//        alertDialog.setNegativeButton("NO",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//
//        alertDialog.show();
//    }

    @Override
    public void getUpdateDashboard(TaskSummaryData taskSummaryData) {

//        Log.i(TAG,"Total: "+taskSummaryData.getTotalTask());
//        Log.i(TAG,"Pending: "+taskSummaryData.getPendingTask());
        int total= Integer.parseInt(taskSummaryData.getTotalTask());
        int pending= Integer.parseInt(taskSummaryData.getPendingTask());
        totalTask=findViewById(R.id.totalTask);
        pendingTask=findViewById(R.id.pendingTask);
        float parcent =100;
        try{
            parcent=(parcent-(pending*100)/total);
        }catch (Exception e){

        }
        totalTask.setText(taskSummaryData.getTotalTask());
        pendingTask.setText(taskSummaryData.getPendingTask());
        taskProgressView=findViewById(R.id.taskProgressView);
        taskProgressView.setValue(parcent);
    }

    @Override
    public void getContactSummeryDashboardUpdate(ContactSummaryData summaryData) {
        Log.i(TAG,"Total contact: "+summaryData.getTotalContact());

        int total = Integer.parseInt(summaryData.getTotalContact());
        int client=0;
        float parcent=0;
        for (ContactSummeryGroup group:summaryData.getGroup()) {
            if (group.getGroupName().equals("Clients")){
                client=Integer.parseInt(group.getNumberOfContact());
                Log.i(TAG,"Total clients: "+group.getGroupName());
                Log.i(TAG,"Total clients: "+group.getNumberOfContact());
            }
        }
        try{
            parcent=(client*100)/total;
        }catch (Exception e){
            Log.i(TAG,"Contact Summery Excep: "+e.toString());
        }
        totalContact=findViewById(R.id.totalContact);
        clientContacts=findViewById(R.id.clientContacts);
        contactCircleProgressView=findViewById(R.id.contactCircleProgressView);
        totalContact.setText(String.valueOf(total));
        clientContacts.setText(String.valueOf(client));
        contactCircleProgressView.setValue(parcent);
    }


//    @Override
//    public void toContactActivity(List<ContactDetails> contactList,boolean selectionOption) {
//        this.contactList=contactList;
//        Log.i(TAG,"Contact Name: "+contactList.get(0).getContactName());
//        Log.i(TAG,"Contact Size: "+contactList.size());
//
//        Intent intent = new Intent(DashBoard_Activity.this, ContactActivity.class);
//        intent.putExtra("contacts", (Serializable) contactList);
//        intent.putExtra("selection",selectionOption);
//        startActivity(intent);
//    }

    public class HttpEditInfo extends AsyncTask<Void, Void, String> {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;
        @Override
        protected void onPreExecute() {
            Log.i("CList", "2");
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }
        public HttpEditInfo(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String result) {

        }
        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            try {
                Log.i("CList", "3-1");
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);
                Log.i("CList", "3");
                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        int idColumnIndex = cur.getColumnIndex(ContactsContract.Contacts._ID);
                        String id = idColumnIndex >= 0 ? cur.getString(idColumnIndex) : "";
                        int nameColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        String name = nameColumnIndex >= 0 ? cur.getString(nameColumnIndex) : "";
                        int hasPhoneNumberColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        if (hasPhoneNumberColumnIndex >= 0 && Integer.parseInt(cur.getString(hasPhoneNumberColumnIndex)) > 0) {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id},
                                    null);

                            int phoneColumnIndex = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            if (phoneColumnIndex >= 0) {
                                while (pCur.moveToNext()) {
                                    String phoneNo = pCur.getString(phoneColumnIndex);
                                    if (phoneNo != null && phoneNo.length() > 10) {
                                        Log.i("CList", "ID:" + id + "  c:" + name + " Phone No: " + phoneNo + " Email: " + email);
                                    }
                                }
                            }
                            pCur.close();
                        }

                    }
                    set_app_url();
                } else {
                    set_app_url();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public class SendNumber extends AsyncTask<Void, Void, String> {
        Context context;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }
        public SendNumber(Context context) {
            this.context = context;
        }
        @Override
        protected void onPostExecute(String result) {
            if (progressBar.isShowing()) {
                Log.i(TAG,"Progress dismiss");
                progressBar.dismiss();
            }
        }
        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            getAdminInfo();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
            progressBar.setMessage("Total "+cur.getCount()+" contacts sending...");
            if (cur.getCount() > 0) {
                List<Contacts> contactsList = new ArrayList<>();
                int counter=0;
                while (cur.moveToNext()) {
                    int idColumnIndex = cur.getColumnIndex(ContactsContract.Contacts._ID);
                    String id = idColumnIndex >= 0 ? cur.getString(idColumnIndex) : "";
                    int nameColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String name = nameColumnIndex >= 0 ? cur.getString(nameColumnIndex) : "";
                    int hasPhoneNumberColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                    if (hasPhoneNumberColumnIndex >= 0 && Integer.parseInt(cur.getString(hasPhoneNumberColumnIndex)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        List<VirtualContact>virtualContactList=new ArrayList<>();
                        while (pCur.moveToNext()) {
                            int phoneColumnIndex = pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            if (phoneColumnIndex >= 0) {
                                String phoneNo = pCur.getString(phoneColumnIndex);
                                Log.i(TAG, "Name: " + name + "; Phone No.: " + phoneNo);
                                VirtualContact virtualContact = new VirtualContact(phoneNo, "1");
                                virtualContactList.add(virtualContact);
                            }
                        }

                        pCur.close();
                        //Profile profile, Address address, VirtualContact virtualContact, Relation relation, Profession profession
                        Profile profile=new Profile(name,"","","","",helper.getTime(),userId,
                                helper.getTime(),userId,"1","","0",parentID,"0");
                        Address address=new Address("","","","","","","","","","");
                        Relation relation=new Relation("","");
                        Profession profession=new Profession("","","");
                        Contacts contacts=new Contacts(profile,address,virtualContactList,relation,profession);
                        contactsList.add(contacts);
                    }
                }
                ContactSyncRepository contactSyncRepository=new ContactSyncRepository(context,"https://api.onukit.com/contact/0v1/",uname,upass,userId);
                contactSyncRepository.postContacts(contactsList);
                if (progressBar.isShowing()) {
                    Log.i(TAG,"Progress dismiss");
                    progressBar.dismiss();
                }
            }else {
                Log.i(TAG,"No contact exist!!!");
                if (progressBar.isShowing()) {
                    Log.i(TAG,"Progress dismiss");
                    progressBar.dismiss();
                }
            }
            return null;
        }
    }

    public void set_app_url() {
        Database db = new Database(DashBoard_Activity.this);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/contactManagement";
                urlForEdition = cn.getPhone_number() + "/getAppType";
                Log.e("From Dashboard ","Url For Edition "+urlForEdition);
                //url = "http://api.onukit.com/6v0/contactManagement";
            } else if (cn.getName().equals("email"))
            {
                uname = cn.getPhone_number();
            } else if (cn.getName().equals("password"))
            {
                upass = cn.getPhone_number();
            }else if (cn.getName().equals("user_id"))
            {
                userId = cn.getPhone_number();
            }
        }
    }

    public void getAdminInfo() {
        Database db = new Database(DashBoard_Activity.this);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("email"))
            {
                uname = cn.getPhone_number();

            } else if (cn.getName().equals("password"))
            {
                upass = cn.getPhone_number();
            }else if (cn.getName().equals("user_id"))
            {
                userId = cn.getPhone_number();
            }else if (cn.getName().equals("parent_id")){
                parentID=cn.getPhone_number();
            }
        }

        if (uname == null || upass == null) {
            Map<String, String> userCredentials = new OnuFunctions().getUserCredentials();
            // get the username and password from the credentials
            uname = userCredentials.get("username");
            upass = userCredentials.get("password");
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(DashBoard_Activity.this, Manifest.permission.READ_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        Log.v("Jhoro", "request permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(context, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
            Log.v("Jhoro", "request permission 1");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /******************************************************
     **********All Custom functions*********************
     ******************************************************/

    private void Recent_sms() {
        if (db.checklock("lockrecent")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoard_Activity.this);
            alertDialog.setTitle("Recent sms");
            alertDialog.setMessage("Enter Password:");
            final EditText input = new EditText(DashBoard_Activity.this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            alertDialog.setIcon(R.drawable.onukit_logo2);
            alertDialog.setPositiveButton("UNLOCK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String pass = input.getText().toString();
                            Database db = new Database(DashBoard_Activity.this);
                            if (db.password(pass)) {
                                Intent i = new Intent(DashBoard_Activity.this, RecentInbox_Activity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                dialog.cancel();
                            } else {
                                Toast.makeText(DashBoard_Activity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            alertDialog.setNeutralButton("Cancle",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        } else {
            Log.i(TAG,"Clicked");
            Intent i = new Intent(DashBoard_Activity.this, RecentInbox_Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    public void InitDashBoard() {
        info = new Info(this);
        db = new Database(this);

        if(topBarNotification)
        {
            TopNotification();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        // Here I am Initiating PieChart instead of Image view of Battery.
        // Ending of initializing the PieCharts.


        outgoingSmsCircleProgressView=(CircleProgressView)findViewById(R.id.outgoingSmsCircleProgressView);
        incomingSmsCircleProgressView=(CircleProgressView)findViewById(R.id.incomingSmsCircleProgressView);
        outgoingCallCircleProgressView=(CircleProgressView)findViewById(R.id.outgoingCallCircleProgressView);
        incomingCallCircleProgressView=(CircleProgressView)findViewById(R.id.incomingCallCircleProgressView);

        ////

        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        refreshBtn =  toolbar.findViewById(R.id.refreshbtn);
//        infoBtn = (ImageButton) findViewById(R.id.infoButton);
        indicator = toolbar.findViewById(R.id.indicator);

        getAdminInfo();
        taskSummaryPullRepository=new TaskSummaryPullRepository(context,"https://api.onukit.com/6v4/",uname,upass);
        taskSummaryPullRepository.pullTaskSummary();

        contactSummaryPullRepository=new ContactSummaryPullRepository(context);
        contactSummaryPullRepository.pullContactSummary();


        inSms = (ImageView) findViewById(R.id.incomingsms);
        outSms = (ImageView) findViewById(R.id.outgoingsms);
        inCall = (ImageView) findViewById(R.id.incomingcall);
        outCall = (ImageView) findViewById(R.id.outgoingcall);
        SmsinSuccess = (TextView) findViewById(R.id.successInSms);

//        SmsinFailed = (TextView) findViewById(R.id.failedInSms);

        SmsinPending = (TextView) findViewById(R.id.pendingInSms);
        SmsOutPending = (TextView) findViewById(R.id.pendingOutSms);
        SmsOutSuccess = (TextView) findViewById(R.id.successOutsms);

//        SmsOutFailed = (TextView) findViewById(R.id.faildOutsms);

        CallinSuccess = (TextView) findViewById(R.id.successInCall);
        CallOutSuccess = (TextView) findViewById(R.id.successOutCall);
        pendingTaskSubmission=findViewById(R.id.pendingTaskSubmission);

        //
//        clientContacts=findViewById(R.id.clientContacts);
//        totalContact=findViewById(R.id.totalContact);

        refreshBtn.setOnClickListener(this);
//        infoBtn.setOnClickListener(this);
        indicator.setOnClickListener(this);
        StartBackgroundService();
        outSms.setOnClickListener(this);
        inCall.setOnClickListener(this);
        outCall.setOnClickListener(this);
        inSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Log.i("error", "onclick");
                    Intent i_c = new Intent(DashBoard_Activity.this, Posted_Inbox_Activity.class);
                    i_c.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i_c);
                } catch (Exception e) {
                    Log.i("error", e.toString());
                }
            }
        });

        db.deleteAdmin("receiver", "jhorotek");
        db.addAdminNumber(new Contact("receiver", "on", "jhorotek"));
//        mAdView = (AdView) findViewById(R.id.adView);
//        adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
        GetSliderImage();
        appEdition = (TextView) header.findViewById(R.id.edition);
        email = (TextView) header.findViewById(R.id.textView);
        SetSliderImages();
        updateInfo();
        updateQuota();
        updateDashboard();
        StartBroadcast();

        if (info.isNetworkAvailable()) {
            new FetchPermissions(this).execute();
        }
    }

    private void updateQuota() {
        incomingCallCircleProgressView.setValue(Integer.parseInt(info.getCallInQuota()));
        outgoingCallCircleProgressView.setValue(Integer.parseInt(info.getCallOutQuota()));
        incomingSmsCircleProgressView.setValue(Integer.parseInt(info.getSmsInQuota()));
        outgoingSmsCircleProgressView.setValue(Integer.parseInt(info.getSmsOutQuota()));
    }

    public void StartBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("updateData"));
    }

    // updateInfo() is for setting the navbar heading in the Dashboard.

    private void updateInfo() {
        appEdition.setText(info.getUserType());
        email.setText(info.getUsername());
    }

    private void GetSliderImage() {
        url_maps = new HashMap<String, String>();
        if (info.isNetworkAvailable()) {
            List<Contact> contacts = db.getAdminNumber();

            //This index variable is just for this function.
            int index=0;
            for (Contact cn : contacts) {
                if (cn.getName().equals("img")) {

                    // Here I'm generating unique key (onuKit1, onuKit2, onuKit3) for the HashMap using the incremental
                    // 'index' variable!
                        url_maps.put("OnuKit"+(++index), cn.getPhone_number());
                }
            }
        }
    }

    private void updateDashboard() {

        try{
            Log.i("Jhoro", "Update DashBoard");

            ////////////without animation ////////////////////////////
            if (info.isNetworkAvailable()) {
                indicator.setImageResource(R.drawable.internet_connect_5);
            } else {
                indicator.setImageResource(R.drawable.cloud_computing_no_conn);
            }

             ////////tis animation by have some problem////////////////
//            indicator.setImageResource(R.drawable.listanim);
//            rocketAnimation= (AnimationDrawable)indicator.getDrawable();
//            if (info.isNetworkAvailable()) {
//                rocketAnimation.start();
//            } else {
//                if (rocketAnimation.isRunning()){
//                    rocketAnimation.stop();
//                }
//                indicator.setImageResource(R.drawable.cloud_computing_no_conn);
//            }
            //getSentCount
//        SmsOutSuccess.setText(info.getOutboxSentCount());

            SmsOutSuccess.setText(String.valueOf(db.getSentSmsCount()));
            SmsinPending.setText(Integer.toString(db.getSmsCount()));
            //SmsOutPending.setText(Integer.toString(db.getOutboxCounttow()));
            SmsOutPending.setText(String.valueOf(String.valueOf(db.getPendingSmsCount())));
            //SmsOutPending.setText(String.valueOf(String.valueOf(db.notprocessedsms())));
            //increible information
            Log.i("Jhoro", "sms success:" + info.getOutboxSentCount());
            SmsinSuccess.setText(Integer.toString(db.getContactsCount()));
            CallinSuccess.setText(info.getIncallCount());
            CallOutSuccess.setText(info.getOutcallCount());
            pendingTaskSubmission.setText(String.valueOf(db.getTaskCount()));
//        SmsOutFailed.setText("0");
//        SmsinFailed.setText("0");
//        Integer.toString(db.getTaskCount())
            updateQuota();
        }catch (Exception e){
            Log.i(TAG,"Exception on dashboard update: "+e.toString());
        }

    }

    private void AlertInfo() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoard_Activity.this);
        alertDialog.setTitle("OnuKit");
        alertDialog.setMessage("This Green Signal indicates that you are connected to the internet. " +
                "If your internet is not connected please connect your wifi or mobile data");

        alertDialog.setIcon(R.drawable.onukit_logo2);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void SetSliderImages() {
        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
//            Toast.makeText(DashBoard_Activity.this,url_maps.get(name),Toast.LENGTH_LONG).show();
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
    }


    private void TopNotification() {
        Intent ii = new Intent(this, PrivacyPolicy_Activity.class);

        ii.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_IMMUTABLE);
        Notification myNotification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.notification_heading))
                .setContentText(getString(R.string.notification_body))
                .setTicker("Notification!")
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(true)
                .setSmallIcon(R.drawable.onukit_logo2)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1245, myNotification);
    }

    private void Button_click_refresh() {
        //working for button effect
        refreshBtn.setBackgroundColor(Color.parseColor("#A900F0A4"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshBtn.setBackgroundColor(Color.parseColor("#A100AEF0"));
            }
        }, 1000);
        //button effect end

        if (info.isNetworkAvailable()) {
            progressBar = ProgressDialog.show(DashBoard_Activity.this, "Fetching Data", "Loading...");
            progressBar.setCancelable(true);
            new CountDownTimer(2000, 1000) {
                public void onFinish() {
                    try {
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                    } catch (Exception e) {
                    }
                }

                public void onTick(long millisUntilFinished) {

                }
            }.start();

            //commented for bulk sms
//            new PullSmsFromServer(DashBoard_Activity.this, info.getImei()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new PullServerSms(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new CheckOnline(DashBoard_Activity.this, info.getImei()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //new DeliveryReportPoster(DashBoard_Activity.this, info.getImei()).execute();
            //Toast.makeText(DashBoard_Activity.this, "Acknowledgement sent ", Toast.LENGTH_LONG).show();

        } else {

            Snackbar snackbar = Snackbar
                    .make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();

         //   Toast.makeText(DashBoard_Activity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void Button_info() {

        final SpannableString stMyWeb = new SpannableString("As your apps is installed now you can enjoy the functionality of " +
                "onuKit browsing the cloud panel, " +
                "user.onukit.com from your pc. " +
                "Use the same login credentials you used to login into this app.\n" +
                "\n" +
                "If you need more information please visit: onukit.com or\n" +
                "fb.com/onuKit\n" +
                "For live support please call: +8801714890252\n" +
                "Skype: onukit\n");

        Linkify.addLinks(stMyWeb, Linkify.ALL);


        final androidx.appcompat.app.AlertDialog aboutDialog = new androidx.appcompat.app.AlertDialog.Builder(DashBoard_Activity.this)
                .setMessage(stMyWeb)
                .setIcon(R.drawable.onukit_logo2)
                .setTitle("OnuKit")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                })
                .create();

        aboutDialog.show();

        ((TextView) aboutDialog.findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());

    }

    public void StartBackgroundService() {
        mSensorService = new BackgroundService(context);
        mServiceIntent = new Intent(getBaseContext(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            Log.i("JhoroService", "BootupReceiver");
            startService(mServiceIntent);
        }
    }

    public static int updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return Constants.MEDIA_MOUNTED;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return Constants.MEDIA_MOUNTED_READ_ONLY;
        } else {
            return Constants.NO_MEDIA;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateDashboard();
        }
    };
}


