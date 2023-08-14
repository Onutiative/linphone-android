package org.linphone.onu_legacy.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Activities.Activities.LoginActivity;
import org.linphone.onu_legacy.Services.RecordJobService;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefManager {

    private static final String PREF_NAME = "onuPref";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    private String TAG="SharedPrefManager";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_PERMISSION_SLIDE="PermissionSlide";

    public static final String KEY_POPUP_CHECK="PopupCheck";
    public static final String KEY_POPUP_CLICK_CHECK="PopupClickCheck";
    public static final String KEY_IS_CALL_ON="IsCalledOn";
    public static final String KEY_IS_RECORDING_ON="IsRecordingOn";
    public static final String KEY_INCOMING_PHONE_NUMBER="IncomingPhoneNumber";
    public static final String KEY_LAST_CALL_ID="last_call_id";
    public static final String DEAFULT_STRING="DefaultString";
    public static final String KEY_POPUP_VIBRATION_CHECK="PopupVibrationCheck";
    public static final String KEY_TASK="Task";

    // for bulk sms

    private static final String KEY_SMS_RECEIVED_FROM_SERVER="ReceivedFromServer";
    private static final String KEY_SMS_SENT_TO_OPERATOR="SentToOperator";
    private static final String KEY_SMS_SENT_SUCCESS="SentSuccess";
    private static final String KEY_SMS_SENT_FAILURE="SentFailure";
    private static final String KEY_SMS_DELIVERED_SUCCESS="DeliveredSuccess";
    private static final String KEY_SMS_DELIVERED_FAILURE="DeliveredFailure";

    private static final String KEY_FCM_FETCHOUTBOX_SMS="FetchOutboxSms"; // to test the fetchoutbox command as it comes twice.

    //for call recording by BDN
    private static final String KEY_CALL_RECORDING_FLAG="Recording Flag";
    private static final String KEY_RECORDER_OBJECT="recorder_obj";


//    private static final String KEY_MOBILE_IMEI="MobileImei";


    public SharedPrefManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();

    }


    public void createLoginSession() {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);




        // commit changes
        editor.commit();
    }


    public boolean isLoggedIn() {
        return preferences.getBoolean(IS_LOGIN, false);
    }


    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, DashBoard_Activity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(i);
        }

    }

    public void logoutUser() {
        editor.clear();
        editor.commit();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Toast.makeText(context, "User is successfully logout.", Toast.LENGTH_LONG).show();

    }

    public boolean getPopupStatus()
    {
        return preferences.getBoolean(KEY_POPUP_CHECK, true);
    }

    public void setPopupStatus(boolean statusType)
    {
        editor.putBoolean(KEY_POPUP_CHECK, statusType);

        // commit changes
        editor.commit();

    }

    public void setPermissionSlideStatus(boolean status)
    {
        editor.putBoolean(KEY_PERMISSION_SLIDE,status);
        editor.commit();
    }

    public boolean getPermissionSlideStatus()
    {
        return preferences.getBoolean(KEY_PERMISSION_SLIDE,true);
    }

    public void setPopupClickStatus(boolean status)
    {
        editor.putBoolean(KEY_POPUP_CLICK_CHECK,status);
        editor.commit();
    }

    public boolean getPopupClickStatus()
    {
        return preferences.getBoolean(KEY_POPUP_CLICK_CHECK,true);
    }
////////////////////////////////////////////////
    public void setIsCallOn(boolean status)
    {
        editor.putBoolean(KEY_IS_CALL_ON,status);
        editor.commit();
    }
    public boolean getIsCallOn()
    {
        return preferences.getBoolean(KEY_IS_CALL_ON,false);
    }
////////////////////////////////////////

    public void setIsRecordingOn(boolean status)
    {
        editor.putBoolean(KEY_IS_RECORDING_ON,status);
        editor.commit();
    }

    public boolean getIsRecordingOn()
    {
        return preferences.getBoolean(KEY_IS_RECORDING_ON,false);
    }

    /////////////////////////////////
    public void setIncomingPhoneNumber(String phoneNumber)
    {
        editor.putString(KEY_INCOMING_PHONE_NUMBER,phoneNumber);
        editor.commit();
    }

    public String getIncomingPhoneNumber()
    {
        return preferences.getString(KEY_INCOMING_PHONE_NUMBER,DEAFULT_STRING);
    }
    /////////////////////////////////////////////////////////
    public void setLastCallID(String phoneNumber)
    {
        editor.putString(KEY_LAST_CALL_ID,phoneNumber);
        editor.commit();
    }

    public String getLastCallID()
    {
        return preferences.getString(KEY_LAST_CALL_ID,DEAFULT_STRING);
    }
    //////////////////////////////////////////////

    public void setPopupVibrationStatus(boolean status)
    {
        editor.putBoolean(KEY_POPUP_VIBRATION_CHECK,status);
        editor.commit();
    }

    public boolean getPopupVibrationStatus()
    {
        return preferences.getBoolean(KEY_POPUP_VIBRATION_CHECK,true);
    }

    public void setTask(String task)
    {
        editor.putString(KEY_TASK,task);
        editor.commit();
    }

    public String getTask()
    {
        return preferences.getString(KEY_TASK,"");
    }



    // getter-setter for bulk sms


    public void setSmsReceivedFromServer(int smsReceived)
    {
        int previousSmsReceived=getSmsReceivedFromServer();
        editor.putInt(KEY_SMS_RECEIVED_FROM_SERVER,previousSmsReceived+smsReceived);
        editor.commit();
    }

    public int getSmsReceivedFromServer()
    {
        return preferences.getInt(KEY_SMS_RECEIVED_FROM_SERVER,0);
    }

    public void setSmsSentToOperator(int smsSentToOperator)
    {
        int previousSentToOperator=getSmsSentToOperator();
        editor.putInt(KEY_SMS_SENT_TO_OPERATOR,previousSentToOperator+smsSentToOperator);
        editor.commit();

    }

    public int getSmsSentToOperator()
    {
        return preferences.getInt(KEY_SMS_SENT_TO_OPERATOR,0);

    }

    public void setSmsSentSuccess(int smsSentSuccess)
    {

        int previousSmsSentSuccess=getSmsSentSuccess();

//        Log.e(TAG," SmsSentSuccess "+String.valueOf(previousSmsSentSuccess+" "+String.valueOf(smsSentSuccess)));

        editor.putInt(KEY_SMS_SENT_SUCCESS,previousSmsSentSuccess+smsSentSuccess);
        editor.commit();
    }

    public int getSmsSentSuccess()
    {
        return preferences.getInt(KEY_SMS_SENT_SUCCESS,0);
    }

    public void setSmsSentFailure(int smsSentFailure)
    {
        int previousSmsSentFailure=getSmsSentFailure();
        editor.putInt(KEY_SMS_SENT_FAILURE,previousSmsSentFailure+smsSentFailure);
        editor.commit();
    }

    public int getSmsSentFailure()
    {
        return preferences.getInt(KEY_SMS_SENT_FAILURE,0);
    }

    public void setSmsDeliveredSuccess(int smsDeliveredSuccess)
    {
        int previousSmsDeliveredSuccess=getSmsDeliveredSuccess();
        editor.putInt(KEY_SMS_DELIVERED_SUCCESS,previousSmsDeliveredSuccess+smsDeliveredSuccess);
        editor.commit();
    }

    public int getSmsDeliveredSuccess()
    {
        return preferences.getInt(KEY_SMS_DELIVERED_SUCCESS,0);
    }

    public void setSmsDeliveredFailure(int smsDeliveredFailure)
    {
        int previousSmsDeliveredFailure=getSmsDeliveredFailure();
        editor.putInt(KEY_SMS_DELIVERED_FAILURE,previousSmsDeliveredFailure+smsDeliveredFailure);
        editor.commit();
    }

    public int getSmsDeliveredFailure()
    {
        return preferences.getInt(KEY_SMS_DELIVERED_FAILURE,0);
    }


    public void setFcmFetchoutboxSms(boolean command)
    {
        editor.putBoolean(KEY_FCM_FETCHOUTBOX_SMS,command).commit();
    }

    public boolean getFcmFetchoutboxSms()
    {
        return preferences.getBoolean(KEY_FCM_FETCHOUTBOX_SMS,true);
    }

    //Call recording flag
    public void setCallRecordingFlag(boolean callRecordingFlag){
        editor.putBoolean(KEY_CALL_RECORDING_FLAG,callRecordingFlag).commit();
    }
    public boolean getCallRecordingFlag(){
        return preferences.getBoolean(KEY_CALL_RECORDING_FLAG,true);
    }
    //Call recording object
    public void setRecordingObj(RecordJobService obj){
        Gson gson=new Gson();
        String obj_value= gson.toJson(obj);
        editor.putString(KEY_RECORDER_OBJECT,obj_value).commit();
    }
    public RecordJobService getRecordingObj(){
        String obj_value= preferences.getString(KEY_RECORDER_OBJECT,null);
        if (obj_value!=null){
            Gson gson=new Gson();
            RecordJobService obj=gson.fromJson(obj_value,RecordJobService.class);
            return obj;
        }else {
            Log.i(TAG,"null string object found!");
            return null;
        }
    }
// ending getter-setter for bulk sms


//    public void setMobileImei(String imei)
//    {
//        editor.putString(KEY_MOBILE_IMEI,imei).commit();
//    }
//
//    public String getMobileImei()
//    {
//        return preferences.getString(KEY_MOBILE_IMEI,"");
//    }


}
