package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.AdminDataClasses.AdminInfo;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses.SendSMSDetails;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PushSMStoServerForSending extends AsyncTask<Void, Void, String> {
    private Context context;
    //now this URL used for test
    //next we will set this as dynamic
    //private final String BASE_URL ="http://api.onukit.com/6v1.1";
    private String url, userName, userPassword, deviceId, responseResult;
    private int TIMEOUT_MILLISEC = 5000, statusCode;
    private Database sqLiteDB;

    private String TAG = "PushSMStoServerForSending";
    private SharedPrefManager sharedPrefManager;

    private List<ContactDetails> contacts = new ArrayList<>();
    private List<SendSMSDetails> smsDetailsList;
    private ProgressDialog dialog;


    public PushSMStoServerForSending(Context context, List<SendSMSDetails> smsDetailsList) {
        Log.i(TAG, "Constructor called");
        this.context = context;
        this.smsDetailsList=smsDetailsList;
        sqLiteDB = new Database(context);
        sharedPrefManager = new SharedPrefManager(context);
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

                Object json = new JSONTokener(responseResult).nextValue();

                if (json instanceof JSONObject) {
                    Log.i(TAG, "JSONObject is here");
                    JSONObject jsonObject = new JSONObject(responseResult);

                    String message = jsonObject.getString("response");
                    Log.i(TAG, "No data to pull");
                    //Toast.makeText(context,message, Toast.LENGTH_LONG).show();
                    // again try to submit report so that updated sent or delivered report can be submitted to server.

                } else if (json instanceof JSONArray) {
                    Log.i(TAG, "JSONArray is here");
                    JSONArray jsonArray = new JSONArray(responseResult);
                    Log.i(TAG, jsonArray.toString());

                    sharedPrefManager.setSmsReceivedFromServer(jsonArray.length());

                    if (jsonArray.length() > 0) {
                        // i am working in here
                        for (int i = 0; i < jsonArray.length(); ++i)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String contactId = jsonObject.getString("status");
                            String appUserId = jsonObject.getString("detail");
                            String userDeviceId = jsonObject.getString("reason");
                            String contactName = jsonObject.getString("count");
                        }
                        Log.i(TAG, "Total contact: " + contacts.size());
                        //set operation after pushing
                    }
                }
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
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
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
            String requestBody = gson.toJson(smsDetailsList);

            OutputStream os = connection.getOutputStream();
            byte[] input = requestBody.getBytes("UTF-8");
            os.write(input, 0, input.length);

            int statusCode = connection.getResponseCode();
            Log.i(TAG, "Response Code: " + statusCode);

            StringBuilder responseResult = new StringBuilder();
            if (statusCode >= 200 && statusCode <= 299) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    responseResult.append(line);
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
        for (AdminInfo info : adminInfos) {
            if (info.getDataKey().equals("Custom_url")) {
                if (!info.getSecondValue().equals(""))
                    url = info.getSecondValue() + "/outgoingApi";
            } else
            //url=BASE_URL+"/contact/getContactApi";
            if (info.getDataKey().equals("email")) {
                userName = info.getSecondValue();
            } else if (info.getDataKey().equals("password")) {
                userPassword = info.getSecondValue();
            } else if (info.getDataKey().equals("did")) {
                deviceId = info.getSecondValue();
            }
        }
    }

}
