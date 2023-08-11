package org.linphone.onu_legacy.AsyncTasking;

//Active for pulling sms from server

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Database.ServerSms;
import com.onutiative.onukit.SMS_Sender.SmsSender;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

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

        Log.i(TAG,"Background called");

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

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("command", "give sms");
            jsonObject.accumulate("device_id", deviceId);
            jsonObject.accumulate("pullcount", "20");
            StringEntity myStringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
            httppost.setEntity(myStringEntity);
            //--------------execution of httppost
            HttpResponse response = httpclient.execute(httppost);
            String res = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "response: " + res);
            responseResult = res;
            statusCode = response.getStatusLine().getStatusCode();

            Log.i(TAG,"Response");

            if (statusCode >= 200 && statusCode <= 299) {
                //Toast.makeText(context, "Data safely reached!", Toast.LENGTH_LONG).show();
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }


    private void setUp()
    {

//        Log.e(TAG,"Set Up Called");
//
//        url="http://api.onuserver.com/6v1/pullOutSms";
//        userName="demo@onuserver.com";
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
//                //http://api1.onuserver.com:8085/v3/incomingSms
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
