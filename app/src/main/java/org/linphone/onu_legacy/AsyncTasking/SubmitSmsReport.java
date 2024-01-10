package org.linphone.onu_legacy.AsyncTasking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.ServerSms;
import org.linphone.onu_legacy.Utility.Constants;
import org.linphone.onu_legacy.Utility.IntentStatus;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SubmitSmsReport extends AsyncTask<Void, Void, String> {


    private Context context;
    private ProgressDialog progressDialog;
    private String url, userName, userPassword, deviceId, responseResult;
    private int TIMEOUT_MILLISEC = 5000, statusCode;
    private Database sqLiteDB;
    private SharedPrefManager sharedPrefManager;
    private static final String TAG = "SubmitSmsReport";
    private ArrayList<ServerSms> serverSmsList;
    private ArrayList<IntentStatus> intentStatuseList;
    private String flag;

    public SubmitSmsReport(Context context) {
        this.context = context;
        setupConfig();
        sqLiteDB = new Database(context);
        sharedPrefManager = new SharedPrefManager(context);
    }

    public SubmitSmsReport(Context context, String flag) {
        this.context = context;
        sqLiteDB = new Database(context);
        sharedPrefManager = new SharedPrefManager(context);
        setupConfig();
        this.flag = flag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

//        serverSmsList=sqLiteDB.getSentAndDeliveredServerSmsData();

        Log.e(TAG, flag);

        try {
            Thread.sleep(2000);
            //serverSmsList = sqLiteDB.getSentAndDeliveredServerSmsData();
            serverSmsList = sqLiteDB.getAllOutBox();
            intentStatuseList = sqLiteDB.getAllIntentStatus();

            Log.i(TAG, "SentAndDeliveredServerSms: " + serverSmsList.size());
            Log.i(TAG, "Intent Status: " + intentStatuseList.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            if (responseResult != null) {
                JSONObject responseObject = new JSONObject(responseResult);
                String status = responseObject.getString("status");

                if (status.equals("4000")) {
                    Log.i(TAG, "Delivery report successfully updated");
                    //Toast.makeText(context,"Delivery report successfully updated", Toast.LENGTH_LONG).show();

                    for (ServerSms serverSms : serverSmsList) {
                        int smsStatus = serverSms.getSmsStatus();
                        Log.i(TAG,"After response outbox status: "+serverSms.getSmsId()+";"+smsStatus);

                        if (smsStatus == Constants.DELIVERY_RESULT_OK || smsStatus == Constants.DELIVERY_RESULT_CANCELED
                                || smsStatus == Constants.SENT_RESULT_ERROR_GENERIC_FAILURE || smsStatus == Constants.SENT_RESULT_ERROR_NO_SERVICE
                                || smsStatus == Constants.SENT_RESULT_ERROR_NULL_PDU || smsStatus == Constants.SENT_RESULT_ERROR_RADIO_OFF
                                ||smsStatus==Constants.SMS_SUBMMIT||smsStatus==Constants.SENT_RESULT_OK) {
                            Log.i(TAG,"smsStatus: "+smsStatus+" Deleted outbox id: "+serverSms.getSmsId());
                            sqLiteDB.deleteSmsWithSmsId(serverSms.getSmsId());
                        }
                    }
                    //added by bidyut to clear intent table after sending status to server
                    if (intentStatuseList.size()>0){
                        for (IntentStatus intentStatus: intentStatuseList) {
                            int smsStatus = intentStatus.getStatus();
                            Log.i(TAG,"After response intent status: "+intentStatus.getSmsID()+";"+smsStatus);

                            if (smsStatus == Constants.DELIVERY_RESULT_OK || smsStatus == Constants.DELIVERY_RESULT_CANCELED
                                    || smsStatus == Constants.SENT_RESULT_ERROR_GENERIC_FAILURE || smsStatus == Constants.SENT_RESULT_ERROR_NO_SERVICE
                                    || smsStatus == Constants.SENT_RESULT_ERROR_NULL_PDU || smsStatus == Constants.SENT_RESULT_ERROR_RADIO_OFF
                                    ||smsStatus==Constants.SMS_SUBMMIT||smsStatus==Constants.SENT_RESULT_OK) {
                                Log.i(TAG,"smsStatus: "+smsStatus+" Deleted intent id: "+intentStatus.getSmsID());
                                sqLiteDB.deleteIntentStatusBySmsID(intentStatus.getSmsID());//delete intent status by smsid
                            }
                        }
                    }
                    //adding end

                } else {
                    Log.i(TAG, "Delivery report failed to update");
                    //Toast.makeText(context,"Delivery report failed to update", Toast.LENGTH_LONG).show();
                }
            }
            if (flag.equals("Sequential Phase")) {
                Log.i(TAG,"In Sequential Phase");
                new PullServerSms(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Basic Authentication
            String credentials = userName + ":" + userPassword;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            connection.setRequestProperty("Authorization", "Basic " + credBase64);

            // JSON data
            JSONArray jsonArray = new JSONArray();
            for (ServerSms serverSms : serverSmsList) {
                jsonArray.put(responceObject(serverSms.getSmsStatus(), serverSms.getSmsId(), serverSms.getDeliveryTime()));
                Log.i(TAG, "Delivery report time: " + serverSms.getDeliveryTime());
            }
            Log.i(TAG, "Request json: " + jsonArray.toString());

            // Send JSON data
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(jsonArray.toString());
            writer.flush();

            int statusCode = connection.getResponseCode();
            Log.i(TAG, "Response code: " + statusCode);

            if (statusCode >= 200 && statusCode <= 299) {
                // Read response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                responseResult = response.toString();
                Log.i(TAG, "Response: " + responseResult);
            }

            connection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private void setupConfig() {

        Log.i(TAG, "Set Up Called");
//        url="http://api.onukit.com/6v1/outgoingReport";
        userName = "Onu$erVe9";
        userPassword = "p#@$aS$";
//        deviceId="860906034654787";

        List<Contact> contacts = sqLiteDB.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/outgoingReport";

            }
//            else if (cn.getName().equals("email"))
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
        }


    }

    //added by bidyut to convert report json object

    private JSONObject responceObject(int status, String smsid, String time) {
        JSONObject jsonObject = new JSONObject();
        try {

            if (status == Constants.SENT_RESULT_OK) {
                jsonObject.accumulate("smsID", smsid);
                jsonObject.accumulate("report", "sent");
                jsonObject.accumulate("time", time);
            } else if (status == Constants.DELIVERY_RESULT_OK) {
                jsonObject.accumulate("smsID", smsid);
                jsonObject.accumulate("report", "delivered");
                jsonObject.accumulate("time", time);
            } else if (status == Constants.SMS_SUBMMIT) {
                jsonObject.accumulate("smsID", smsid);
                jsonObject.accumulate("report", "submitted");
                jsonObject.accumulate("time", time);
            }else if (status == Constants.SENT_RESULT_ERROR_GENERIC_FAILURE || status == Constants.SENT_RESULT_ERROR_NO_SERVICE
                    || status == Constants.SENT_RESULT_ERROR_NULL_PDU || status == Constants.SENT_RESULT_ERROR_RADIO_OFF) {
                jsonObject.accumulate("smsID", smsid);
                jsonObject.accumulate("report", "error");
                jsonObject.accumulate("time", time);
            } else if (status == Constants.DELIVERY_RESULT_CANCELED) {
                jsonObject.accumulate("smsID", smsid);
                jsonObject.accumulate("report", "error");
                jsonObject.accumulate("time", time);
            }
        } catch (Exception e) {}
        Log.i(TAG,"Report: "+jsonObject);
        return jsonObject;
    }
}
