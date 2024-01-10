package org.linphone.onu_legacy.AsyncTasking;

//Active for pulling sms from server

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.ServerSms;
import org.linphone.onu_legacy.SMS_Sender.SmsSender;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class PullServerSms extends AsyncTask<Void, Void, String> {

    private Context context;
    private String url,userName,userPassword,deviceId, responseResult;
    private int TIMEOUT_MILLISEC = 5000, statusCode;
    private Database sqLiteDB;

    private static final String TAG="PullServerSms";
    private SmsSender smsSender;
    private SharedPrefManager sharedPrefManager;

    public PullServerSms(Context context) {


        Log.i(TAG,"Constructor called");

        this.context=context;
//        setUp();
        sqLiteDB=new Database(context);
        smsSender=new SmsSender(context);
        sharedPrefManager=new SharedPrefManager(context);
        setUp();
        Log.i(TAG,"Constructor ending");

    }

    @Override
    protected void onPreExecute() {

        Log.i(TAG,"Pre execute called1");
        super.onPreExecute();

        Log.i(TAG,"Pre execute called2");

//        progressDialog = ProgressDialog.show(context, "Message", "Data Loading...");
//        progressDialog.setCancelable(true);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.i(TAG,"Post Execute Called");

//        if (progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }


        try {

//            Object json = new JSONTokener(responseResult).nextValue();
//            if (json instanceof JSONObject)
//            {
//                Log.e(TAG,"JSONObject is here");
//            }
//            else if (json instanceof JSONArray)
//            {
//                Log.e(TAG,"JSONArray is here");
//            }


            if (responseResult != null) {

                Object json = new JSONTokener(responseResult).nextValue();

                if (json instanceof JSONObject)
                {

                    Log.i(TAG,"JSONObject is here");
                    JSONObject jsonObject=new JSONObject(responseResult);

                    String message=jsonObject.getString("response");

                    Log.i(TAG,"No data to pull");
                    //Toast.makeText(context,message, Toast.LENGTH_LONG).show();

                    // again try to submit report so that updated sent or delivered report can be submitted to server.

                    new SubmitSmsReport(context,"Last Phase").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
                else if (json instanceof JSONArray)
                {
                    Log.i(TAG,"JSONArray is here");
                    JSONArray jsonArray = new JSONArray(responseResult);
                    Log.i(TAG,jsonArray.toString());

                    sharedPrefManager.setSmsReceivedFromServer(jsonArray.length());

                    if (jsonArray.length() > 0) {

                        for (int i = 0; i < jsonArray.length(); ++i) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String smsId = jsonObject.getString("sms_id");
                            String smsText = jsonObject.getString("sms_text");
                            String mobileNo = jsonObject.getString("mobile_no");
                            String status = jsonObject.getString("status");
                            String deviceId = jsonObject.getString("device_id");
                            Log.e(TAG, smsId + " " + smsText + " " + mobileNo + " " + status + " " + deviceId);

                            ServerSms serverSms=new ServerSms();

                            serverSms.setSmsId(smsId);
                            serverSms.setSmsTo(mobileNo);
                            serverSms.setSmsBody(smsText);
                            serverSms.setPullTime(getTimeStamp("yyyy-MM-dd hh:mm:ss"));
                            serverSms.setSubmissionTime("null");
                            serverSms.setDeliveryTime("null");
                            serverSms.setSmsStatus(Integer.parseInt(status));

                            Log.i(TAG,"Pull Status "+i+": "+serverSms);

                            if (mobileNo.isEmpty()||smsText.isEmpty()){
                                Log.i(TAG,"Warning: No phone number or text added");
                            }else {
                                sqLiteDB.addNewSms(serverSms);
                            }
                        }
                        smsSender.startSendingSms();
                    }
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.i(TAG, "Background called");

        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");

            String credentials = userName + ":" + userPassword;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("command", "give sms");
            jsonObject.accumulate("device_id", deviceId);
            jsonObject.accumulate("pullcount", "20");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
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
                Log.i(TAG, "response: " + res);
                responseResult = res;
                statusCode = responseCode;

                Log.i(TAG, "Response");

                // Here you can perform any further processing with the response data
                // and handle status codes as needed.

            } else {
                Log.e(TAG, "HTTP Response Code: " + responseCode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }



    private void setUp()
    {

//        Log.e(TAG,"Set Up Called");
//
//        url="http://api.onukit.com/6v1/pullOutSms";
//        userName="demo@onukit.com";
//        userPassword="welcome@2018";
//        deviceId="860906034654787";


        List<Contact> contacts = sqLiteDB.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/pullOutSms";

            } else if (cn.getName().equals("email"))

            {
                userName = cn.getPhone_number();

            } else if (cn.getName().equals("password"))

            {
                userPassword = cn.getPhone_number();
            } else if (cn.getName().equals("did")) {
                deviceId = cn.getPhone_number();
            }
        }


    }

    private String getTimeStamp(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

//    public void set_app_url() {
//
//        List<Contact> contacts = sqLiteDB.getAdminNumber();
//        for (Contact cn : contacts) {
//            if (cn.getName().equals("Custom_url")) {
//                if (!cn.getPhone_number().equals(""))
//                    url = cn.getPhone_number() + "/pullOutSms";
//                //http://api1.onukit.com:8085/v3/incomingSms
//            } else if (cn.getName().equals("email"))
//
//            {
//                userName = cn.getPhone_number();
//
//            } else if (cn.getName().equals("password"))
//
//            {
//                userPassword = cn.getPhone_number();
//            } else if (cn.getName().equals("did")) {
//                deviceId = cn.getPhone_number();
//            }
//        }
//    }

}
