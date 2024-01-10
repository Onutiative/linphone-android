package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.AdminDataClasses.AdminInfo;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses.PushScheduleResponse;
import org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses.ScheduleSMSDetails;
import org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses.SendSMSDetails;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PushScheduledSMS extends AsyncTask<Void, Void, String> {
    private Context context;
    //now this URL used for test
    //next we will set this as dynamic
    private String url, userName, userPassword, deviceId, responseResult;
    private int TIMEOUT_MILLISEC = 5000, statusCode;
    private Database sqLiteDB;

    private String TAG = "PushScheduledSMS";
    private SharedPrefManager sharedPrefManager;

    private List<ContactDetails> contacts = new ArrayList<>();
    private ScheduleSMSDetails scheduleSMSDetails;
    private ProgressDialog dialog;
    private RequestResponseListener listener;


    public PushScheduledSMS(Context context, ScheduleSMSDetails scheduleSMSDetails) {
        Log.i(TAG, "Constructor called");
        this.context = context;
        this.scheduleSMSDetails=scheduleSMSDetails;
        sqLiteDB = new Database(context);
        sharedPrefManager = new SharedPrefManager(context);
        listener= (RequestResponseListener) context;
        setUp();
        Log.i(TAG, "Constructor ending");
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Connecting to server....");
        dialog.show();

        Log.i(TAG, "Pre execute called1");
        super.onPreExecute();
        Log.i(TAG, "Pre execute called2");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.i(TAG, "Post Execute Called");
        dialog.dismiss();
        try {
            if (responseResult != null) {
                Gson gson = new GsonBuilder().create();
                PushScheduleResponse response = gson.fromJson(responseResult,PushScheduleResponse.class);
                Log.i(TAG,"Response Result: "+responseResult);
                Log.i(TAG,"Response: "+response.getMessage());

                Toast.makeText(context,"SMS set "+response.getDetail()+"!",Toast.LENGTH_SHORT).show();
                listener.onSuccessRequest("SMS set "+response.getDetail()+"!");
            }else {
                listener.onFailureRequest("SMS not set!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //already made my modifications in doInBackGround
    @Override
    protected String doInBackground(Void... voids) {
        Log.i(TAG, "Background called");

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestProperty("Content-type", "application/json");

            //---------------------Code for Basic Authentication-----------------------
            String credentials = userName + ":" + userPassword;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            //---------------------------------Body Data-------------------------------------
            Gson gson = new GsonBuilder().create();
            String requestBody = gson.toJson(scheduleSMSDetails);

            //---------------------------------------------------------------------------
            Log.i(TAG, "Request Body: " + requestBody);
            connection.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(requestBody);
            os.flush();
            os.close();

            int statusCode = connection.getResponseCode();
            Log.i(TAG, "Response Code: " + statusCode);
            responseResult = "";

            if (statusCode >= 200 && statusCode <= 299) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseResult += line;
                }
                reader.close();
                Log.i(TAG, "Data reached");
            } else {
                Log.i(TAG, "Data can't be reached");
            }

            connection.disconnect();
        } catch (Exception ex) {
            Log.i(TAG, "Exception: " + ex);
            ex.printStackTrace();
        }
        return null;
    }


    private void setUp() {
        List<AdminInfo> adminInfos = sqLiteDB.getAdminInformation();
        //url = BASE_URL + "/schedule/outboxScheduleApi";
        for (AdminInfo info : adminInfos) {
            if (info.getDataKey().equals("Custom_url")) {
                if (!info.getSecondValue().equals("")){
                    url = info.getSecondValue() + "/schedule/outboxScheduleApi";
                }
            } else if (info.getDataKey().equals("email")) {
                    userName = info.getSecondValue();
                } else if (info.getDataKey().equals("password")) {
                    userPassword = info.getSecondValue();
                } else if (info.getDataKey().equals("did")) {
                    deviceId = info.getSecondValue();
                }
        }
    }

    public interface RequestResponseListener{
        void onSuccessRequest(String msg);
        void onFailureRequest(String msg);
    }

}
