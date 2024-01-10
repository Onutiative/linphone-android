package org.linphone.onu_legacy.Services;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.CallLogPush;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.SaveNewTask;
import org.linphone.onu_legacy.Call_Recording.AudioUploader;
import org.linphone.onu_legacy.AsyncTasking.CheckOnline;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.AsyncTasking.IncomingSmsPoster;
import org.linphone.onu_legacy.Utility.Helper;
import org.linphone.onu_legacy.Utility.Info;
import org.linphone.onu_legacy.SMS_Sender.MsgSender;
import org.linphone.onu_legacy.AsyncTasking.DeliveryReportPoster;
import org.linphone.onu_legacy.Utility.SharedPrefManager;
import org.json.JSONArray;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Created by Rabby on 30/01/2016.
 */
public class BackgroundService extends Service {
    private String TAG ="BackgroundService";
    public int counter = 0, nextCounter = 0, outmsgcount = 0, incomingSms = 0;
    private Context context;
    private Info info;
    private SharedPrefManager sharedPrefManager;
    private RecordJobService recordJobService;
    private Helper helper;

    Database db;
    public BackgroundService(Context applicationContext) {
        super();
        this.context = applicationContext;
        // Log.i(TAG, "**********************************************************************");
        // Log.i(TAG, "here I am!");
    }

    public BackgroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        info = new Info(getApplicationContext());
        db=new Database(getApplicationContext());
        helper=new Helper(getApplicationContext());
        startTimer();
        // Log.i(TAG,"onStart called");
        onBind(intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        // Log.i(TAG,"Rebind called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Log.i(TAG, "onDestroy!");
        Intent broadcastIntent = new Intent("uk.ac.shef.oak.ActivityRecognition.RestartSensor");
        sendBroadcast(broadcastIntent);
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        //sharedPrefManager.setIsRecordingOn(false);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();
        //initialize the TimerTask's job
        initializeTimerTask();
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        // Log.i(TAG,"Initial time task");
        recordJobService=new RecordJobService(getApplicationContext());//added by bidyut
        timerTask = new TimerTask() {
            public void run() {
                //// Log.i(TAG, "in timer ++++  " + (counter++));
                counter++;
                nextCounter++;
                Database db = new Database(getApplicationContext());
                // Log.i(TAG, "in timer ++++  " + (counter++));
                /*****
                 *****do the undone
                 *****/
                try {
                    // Log.i(TAG, "report Count:" + db.getThreadCount());
                    if (counter == 1) {

                        if (info.isNetworkAvailable()) {
                            if (db.getOutboxCounttow() < 60) {
                                // new PullSmsFromServer(getApplicationContext(), info.getImei()).execute();
                            }
                            // new DeliveryReportPoster(getApplicationContext(), info.getImei()).execute();
                            new CheckOnline(getApplicationContext(), info.getImei()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    } else if (counter == 900) {
                        counter = 0;
                    }
                    if (nextCounter == 3) {
                        nextCounter = 0;
                        try {
                            Intent intent = new Intent("updateData");
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        } catch (Exception e) {
                            Log.i(TAG, "Exception:" + e);
                        }

                        // Log.i(TAG, "Running:" + counter);

                        if (!Integer.toString(db.getSmsCount()).equals("0") && info.isNetworkAvailable()) {
                            List<Contact> contacts = db.getAllsms();
                            for (Contact cn : contacts) {
                                new IncomingSmsPoster(getApplicationContext(), "loc", info.getImei()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }

                        if (db.getOutboxCounttow() > 0 || db.getOutboxCount() > 0) {
                            outmsgcount++;
                            if (outmsgcount > 2 && info.isNetworkAvailable()) {
                                outmsgcount = 0;
                                new MsgSender(getApplicationContext(), info.getImei(), 0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        }
                        if (db.getOutboxCounttow() < 10 && info.getCheckOutgoing().equals("empty") && info.isNetworkAvailable()) {
                            // Log.i(TAG, "Calling send out now . ");
                            //commented for bulk . This flow is old by Rabbi bhai.
                            //new PullSmsFromServer(getApplicationContext(), info.getImei()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        if (db.getThreadCount() > 0 && info.isNetworkAvailable()) {
                            new DeliveryReportPoster(getApplicationContext(), "123").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        // Log.i(TAG,"_____________________________");
                        // Log.i(TAG,"Call count: "+db.getCallCount());
                        if (db.getCallCount() > 0 && info.isRecorder()) {
                            sharedPrefManager = new SharedPrefManager(getApplicationContext());
                            // Log.i(TAG,"Call Count: "+db.getCallCount());
                            List<Contact> contacts = db.getAllCall();
                            String irndID = sharedPrefManager.getLastCallID();
                            String calTypes = "0";
//                            for (Contact cn : contacts) {
//                                irndID = cn.getTrxid();
//                                calTypes = cn.getType();
//                            }
                            //Call recording and file upload operation start

                            if (sharedPrefManager.getIsCallOn()){
                                // Log.i(TAG,"On Call");
                                if (!sharedPrefManager.getIsRecordingOn())
                                {
                                    // Log.i(TAG, "Get trxID for start recording: " + irndID);
                                    recordJobService.startRecording(calTypes,irndID);
                                }
                            }else if (!sharedPrefManager.getIsCallOn()){
                                // Log.i(TAG,"Off call");
                                if (sharedPrefManager.getIsRecordingOn())
                                {
                                    recordJobService.stopRecording(calTypes,irndID);
                                }
                                if (helper.isInternetAvailable()){
                                    List<Contact> callQues = db.getAllCall();
                                    // Log.i(TAG, "Internet available!!!");
                                    String s_audio="";
                                    String s_log="";
                                    String phoneNumber="";
                                    String inTime="";
                                    int notFound_count=0;
                                    for (Contact cn : callQues) {
                                        irndID = cn.getTrxid();
                                        calTypes = cn.getType();
                                        s_log=cn.getStatus_log();
                                        s_audio=cn.getStatus_audio();
                                        phoneNumber=cn.getPhone_number();
                                        inTime=cn.getTime();


                                        // Log.i(TAG, "Get trxID for audio upload: " + irndID);
                                        //new AudioUploader(getApplicationContext(), irndID, calTypes).execute();
                                        if (s_audio.equals("0")){
                                            // Log.i(TAG,"Audio not uploaded");
                                            // Log.i(TAG,">>>>>>>>>>>>>>>>>>>>>>>>>>");
                                            String type=calTypes;
                                            // Log.i(TAG,"CAll Type: "+type);
                                            new AudioUploader(getApplicationContext(), irndID, type).execute();
                                        }else if (s_audio.equals("1")){
                                            // Log.i(TAG,"Audio uploaded");
                                        }else if (s_audio.equals("2")){
                                            notFound_count++;
                                            //db.updateCallQueueForAudioUp(irndID,"0");
                                        }

                                        if (s_log.equals("0")){
                                            // Log.i(TAG,"Call Log not uploaded Phone: "+phoneNumber+"; Time: "+inTime);
                                            new CallLogPush(getApplicationContext(),irndID,phoneNumber,inTime,calTypes).execute();
                                        }else if (s_log.equals("1")){
                                            // Log.i(TAG,"Call Log uploaded");
                                        }
                                        if (s_log.equals("1")&&s_audio.equals("1")){
                                            if (db.deleteCallQueue(irndID)){
                                                // Log.i(TAG,"Deleted!!!");
                                            }else {
                                                // Log.i(TAG,"Not deleted!!!");
                                            }
                                        }
                                    }
                                    // Log.i(TAG,notFound_count+" Files are not found");

                                }else {
                                    // Log.i(TAG,"On internet!");
                                }
                            }
                            //new AudioUploader(getApplicationContext(), irndID, calTypes).execute();
                            //Call recording and file upload operation end
                        }

//                        if(db.getTaskCount()>0 && info.isNetworkAvailable())
//                        {
//                            new SaveNewTask(getApplicationContext(), getTaskArray(),"","","service").execute();
//                        }
                    } else if (nextCounter == 6) {

                    }
                }
                catch (Exception e) {
                    Log.i(TAG,"Exception in timer:" + e.toString());
                }
                // return null;
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Log.i(TAG, "called onBiend");
        return null;
    }


    private JSONArray getTaskArray()
    {
        JSONArray jsonArray = db.getAllTask();
        // Log.d(TAG,jsonArray.toString());
        return jsonArray;
    }
}