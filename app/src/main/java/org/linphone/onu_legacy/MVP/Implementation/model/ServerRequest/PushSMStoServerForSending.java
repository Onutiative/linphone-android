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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("Content-type", "application/json");
            //---------------------Code for Basic Authentication-----------------------
            String credentials = userName + ":" + userPassword;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);
            //---------------------------------Body Data-------------------------------------

            Gson gson = new GsonBuilder().create();
            String requestBody = gson.toJson(smsDetailsList);

//            JSONArray jsonArray= new JSONArray();
//            for (int i=0;i<smsDetailsList.size();i++){
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.accumulate("sent_time", smsDetailsList.get(i).getSent_time());
//                jsonObject.accumulate("sms_text", smsDetailsList.get(i).getSms_text());
//                jsonObject.accumulate("mobile", smsDetailsList.get(i).getMobile());
//                jsonObject.accumulate("smsId", smsDetailsList.get(i).getSmsId());
//                jsonArray.put(jsonObject);
//            }
            //---------------------------------------------------------------------------
            Log.i(TAG,"Request Body: "+requestBody);
            StringEntity myStringEntity = new StringEntity(requestBody, "UTF-8");
            httppost.setEntity(myStringEntity);
            //--------------execution of httppost
            HttpResponse response = httpclient.execute(httppost);
            String res = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "response: " + res);
            responseResult = res;
            statusCode = response.getStatusLine().getStatusCode();
            Log.i(TAG, "Response");

            if (statusCode >= 200 && statusCode <= 299) {

                Log.i(TAG, "Data reached");
            } else {
                Log.i(TAG, "Data can't reached");
            }
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
