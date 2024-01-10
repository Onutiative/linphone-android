package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.Helper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CallLogPush extends AsyncTask<Void, Void, String> {
    int TIMEOUT_MILLISEC = 5000;
    private Context context;
    private Database db;
    private String trID;
    private Helper helper;
    private String TAG="CallLogPush";
    private String callType;
    private String phoneNumber;
    private String inTime;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //dialog = ProgressDialog.show(context, "Wait", "Please wait");
    }
    public CallLogPush(Context context,String trID,String phoneNumber,String inTime,String callType) {
        this.context = context;
        db= new Database(context);
        this.trID=trID;
        this.phoneNumber=phoneNumber;
        this.callType=callType;
        helper=new Helper(context);
        this.inTime=inTime;
    }
    @Override
    protected void onPostExecute(String result)
    {
        if(result != null)
        {
            Log.i(TAG, "Call if ok");
            if(result.equals("4000"))
            {
            }
        }
        else
        {
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String status = null;
            String success = "4000";
            int statusCode = 0;
            String username = helper.getUserData().getUsername();
            String password = helper.getUserData().getPassword();
            String call_url = helper.getUserData().getBase_url() + "/callLog";
//            Log.i(TAG, "Username: " + username);
//            Log.i(TAG, "Password: " + password);

            Log.i(TAG, "trnxID: " + trID);

            URL urlObj = new URL(call_url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");

            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.accumulate("callerMsisdn", phoneNumber);
            jsonParam.accumulate("transactionId", trID);
            jsonParam.accumulate("device_id", helper.getUserData().getDeviceID());
            jsonParam.accumulate("callType", callType);
            jsonParam.accumulate("parent_id", helper.getUserData().getParent_id());
            jsonParam.accumulate("inTime", inTime);
            Log.i(TAG, "Request Body JSON: " + jsonParam.toString());

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String res = "";
            if (responseCode >= 200 && responseCode <= 299) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        res += line;
                    }
                }
                Log.i(TAG, "Call data up response: " + res);
                JSONObject json = new JSONObject(res);
                status = json.getString("status");
                statusCode = responseCode;
                if (statusCode >= 200 && statusCode <= 299) {
                    Log.d(TAG, "Status is OK " + json.toString());
                    db.updateCallQueueForLogUp(trID, "1");
                    return status;
                }
            } else {
                Log.e(TAG, "HTTP Response Code: " + responseCode);
            }
        } catch (Exception ex) {
            Log.d(TAG, "Exception here " + ex.getMessage());
        }
        return null;
    }

}
