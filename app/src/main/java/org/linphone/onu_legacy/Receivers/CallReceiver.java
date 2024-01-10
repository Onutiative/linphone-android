package org.linphone.onu_legacy.Receivers;

import android.Manifest;
import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import org.linphone.LinphoneApplication;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.CallLogPush;

import org.linphone.onu_legacy.Services.BackgroundService;
import org.linphone.onu_legacy.Services.FloatingViewService;
import org.linphone.onu_legacy.Services.MyJobService;
import org.linphone.onu_legacy.Services.RecordJobService;
import org.linphone.onu_legacy.Utility.Helper;
import org.linphone.onu_legacy.Utility.Info;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by jhorotek on 10/4/2015.
 */
public class CallReceiver extends BroadcastReceiver {
    private static final int JOB_ID = 123 ;
    public String incomingNumber = null;
    private String outgoingNumber=null;

    public String call_url;
    public String imei;
    public String uname, upass, parentID;
    private String calltype = "incoming";
    public boolean calling = false;
    private boolean callOut=false;
    public Info info;
    private Database db;
    private static String trID = "";
    private static final String ACTION_OUT = "android.intent.action.NEW_OUTGOING_CALL";

    private final String TAG = "CallReceiver";

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;

    private static boolean incomingCall;

    private String prefName = "onuPref";
    private SharedPreferences.Editor prefEditor;
    private SharedPreferences sharedPref;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private static String timeStamp = "";
    private SharedPrefManager sharedPrefManager;

    public int receiverCallCount=0;
    private static File audioFile;
    private AudioManager audiomanager;
    private String path;
    private static MediaRecorder mRecorder = null;

    private Context context;
    private static String irndID="0";
    private static String caltype="0";
    private static String savedNumber;  //because the passed incoming is only valid in ringing
    private static boolean isIncoming;
    private static int lastCheckState = TelephonyManager.CALL_STATE_IDLE;
    Date currentTime = Calendar.getInstance().getTime();
    private Helper helper;
    private RecordJobService recordJobService;
    public static RecordJobService jobService;

    //private ServiceHandler serviceHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        receiverCallCount++;
        Log.i(TAG,"Receiver count: "+receiverCallCount);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPrefManager = new SharedPrefManager(context);
        helper=new Helper(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"READ_PHONE_STATE Permission not granted!");
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG,"READ_CALL_LOG Permission not granted!");
            return;
        }

        Log.i(TAG, "Call Receiver:" + intent.getAction().toString());
        Log.i(TAG, "Call Receivers:" + intent.getStringExtra(TelephonyManager.EXTRA_STATE));
        String number = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        Log.i(TAG ,"Call Receiver:" + number);

        //set_app_url(context);
        info = new Info(context);
        db = new Database(context);

        if (info.isCallIn()) {
            if (intent.getAction().equals(ACTION_OUT)) {
                lastState = TelephonyManager.CALL_STATE_OFFHOOK;
                outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.i(TAG,"Outgoing no: "+outgoingNumber);
                if (!callOut) {
                    callOut = true;
                    calltype = "outgoing";
                    int i = Integer.parseInt(info.getOutcallCount());
                    i++;
                    db.updateOutcallCount(i);
                    db.deleteAdmin("call", outgoingNumber);
                    //db.deleteAllCall();
                    trID = helper.makeTrxid();
                    //trID=getDate("yyyyMMddhhmmSS");
                    //trID = getDate("ymmhhss");
                    timeStamp=getDate("yyyy-MM-dd hh:mm:ss");
                    db.addCallInQueue(new Contact(outgoingNumber,outgoingNumber,timeStamp,calltype,trID,"0","0"));
                    //db.addCallInQueue(new Contact(trID, calltype, "0"));
                    db.add_in_out_calls(new Contact("out", outgoingNumber, timeStamp));
                    Log.i(TAG,"Call data "+trID+"; Type: "+calltype+ "; Time: "+timeStamp+"; Phone: "+outgoingNumber);
                    //new CallLogSend(context).execute();
                    new CallLogPush(context,trID,outgoingNumber,timeStamp,calltype).execute();
                }
            } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //incoming log push section
                ////////////////////////////////////////
                incomingCall = false;
                lastState = TelephonyManager.CALL_STATE_RINGING;
                // This code will execute when the phone has an incoming call
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.i(TAG,"Incoming no: "+incomingNumber);
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                // imei = tm.getDeviceId();
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (info.getCallblock().equals("on")) {
                    disconnectPhoneItelephony(context);
                }
                if (!calling && incomingNumber!=null) {
                    calling = true;
                    Log.i(TAG,"Calling status.......");
                    int i=Integer.parseInt(info.getIncallCount());
                    i++;
                    db.updateIncallCount(i);
                    db.deleteAdmin("call",incomingNumber);
                    //db.deleteAllCall();
                    //trID=getDate("yyyyMMddhhmmSS");
                    trID = helper.makeTrxid();
                    timeStamp=getDate("yyyy-MM-dd hh:mm:ss");
                    Log.i(TAG,timeStamp);
                    db.addCallInQueue(new Contact(incomingNumber,incomingNumber,timeStamp,calltype,trID,"0","0"));
//                    db.add_in_out_calls(new Contact("in",incomingNumber,getDate("yyyy-MM-dd kk:mm:ss")));
                    Log.i(TAG,"Call data "+trID+"; Type: "+calltype+ "; Time: "+timeStamp+"; Phone: "+incomingNumber);
                    db.add_in_out_calls_with_status(new Contact("in",incomingNumber,timeStamp,"incoming"));
                    sharedPrefManager.setPopupClickStatus(true);
                    sharedPrefManager.setIncomingPhoneNumber(incomingNumber);
                    new CallLogPush(context,trID,incomingNumber,timeStamp,calltype).execute();
                    //new CallLogSend(context).execute();
                }
                /// Pop-up Coding Started
            if (sharedPrefManager.getPopupStatus())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                } else {
                    initializeView(context);
                }
            }
            } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE) && (lastState == TelephonyManager.CALL_STATE_RINGING) && (!incomingCall) )
                {
                    //miscall log push section
                    ////////////////////////////////////////
                    sharedPrefManager.setPopupClickStatus(false);
                    incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    // Intent i=new Intent(context, MyJobService.class);
                    Log.i(TAG, "Call type: missed call");
                    if (!calling) {
                        calling = true;
                        calltype = "missed";
                        db.updateCalllogForMissed(trID,calltype);
                        db.updateCallLogWithStatusForMissed(timeStamp,calltype);
                        Log.i(TAG,"Call data "+trID+"; Type: "+calltype+ "; Time: "+timeStamp+"; Phone: "+incomingNumber);
                        new CallLogPush(context,trID,incomingNumber,timeStamp,calltype).execute();
                    }
                } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && (lastState == TelephonyManager.CALL_STATE_RINGING))
                    {
                        incomingCall=true;
                        lastState=TelephonyManager.CALL_STATE_RINGING;
                    } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)&& incomingCall)
                        {
                         sharedPrefManager.setPopupClickStatus(false);
                        }
        }

        Log.i(TAG,"Calltype before recorder on: "+calltype);
        if (calltype.equals("incoming") || calltype.equals("missed"))
        {
            if (!intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //Log.i(TAG,"CAll ON");
                sharedPrefManager.setLastCallID(trID);
                sharedPrefManager.setIsCallOn(true);
                /////service calling
                try{
                    if (!sharedPrefManager.getIsCallOn()){
                        BackgroundService mSensorService = new BackgroundService(context);
                        Intent mServiceIntent = new Intent(context, mSensorService.getClass());
                        if (isMyServiceRunning(BackgroundService.class)){
                            Log.i(TAG,"Service alive!");
                            //context.stopService(mServiceIntent);
                        }else {
                            Log.i(TAG,"Service not alive!");
                            context.startService(mServiceIntent);
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG,"Service all in receiver Exception: "+e.toString());
                }
                ///////////////////
            } else {
                //Log.i(TAG,"CAll OFF");
                sharedPrefManager.setIsCallOn(false);
            }
        }else {
            if (intent.getAction().equals(ACTION_OUT)){
                //sharedPrefManager.setIsCallOn(true);
                Log.i(TAG,"OUTGOING CAll ON");
            }else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Log.i(TAG,"OUTGOING CAll OFF");
            }
        }

        if (sharedPrefManager.getIsCallOn()){
            Log.i(TAG,"On Call");
            recordJobService=new RecordJobService(context);

        }else {
            Log.i(TAG,"Off Call");
//            if (recordJobService!=null){
//                Log.i(TAG,"Object not null!");
//            }else {
//                Log.i(TAG,"Object null!");
//            }
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void initializeView(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Constraints constraints = new Constraints.Builder()
                   .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build();

            OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(MyJobService.class)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance(context).enqueue(myWorkRequest);
        } else {
            context.startService(new Intent(context, FloatingViewService.class));
        }
    }


    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
//    private void disconnectPhoneItelephony(Context context)
//    {
//        ITelephony telephonyService;
//        TelephonyManager telephony = (TelephonyManager)
//                context.getSystemService(Context.TELEPHONY_SERVICE);
//        try
//        {
//            Class c = Class.forName(telephony.getClass().getName());
//            Method m = c.getDeclaredMethod("getITelephony");
//            m.setAccessible(true);
//            telephonyService = (ITelephony) m.invoke(telephony);
//            telephonyService.endCall();
//        }
//        catch (Exception e)
//        {
//            Log.i(TAG, "Call Exception:"+e);
//            //whenever a call exception occurs ,
//        }
//    }

    private void disconnectPhoneItelephony(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TelecomManager telecomManager = context.getSystemService(TelecomManager.class);
            if (telecomManager != null) {
                telecomManager.endCall();
            }
        } else {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class clazz = null;
            try {
                clazz = Class.forName(telephonyManager.getClass().getName());
                Method method = clazz.getDeclaredMethod("getITelephony");
                method.setAccessible(true);
                Object telephonyInterface = method.invoke(telephonyManager);
                Class telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
                Method endCallMethod = telephonyInterfaceClass.getDeclaredMethod("endCall");
                endCallMethod.invoke(telephonyInterface);
            } catch (Exception e) {
                Log.i(TAG, "Call Exception:"+e);
            }
        }
    }
}

