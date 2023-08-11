package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.MVP.Implementation.model.AdminDataClasses.AdminInfo;
import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import com.onutiative.onukit.MVP.Implementation.model.SMSDataClasses.PushScheduleResponse;
import com.onutiative.onukit.MVP.Implementation.model.SMSDataClasses.ScheduleSMSDetails;
import com.onutiative.onukit.MVP.Implementation.model.SMSDataClasses.SendSMSDetails;
import com.onutiative.onukit.Utility.SharedPrefManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

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
            String requestBody = gson.toJson(scheduleSMSDetails);

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
