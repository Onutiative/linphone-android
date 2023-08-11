package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.TaskConversion;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SaveNewTask extends AsyncTask<Void, Void, String> {


    int TIMEOUT_MILLISEC = 5000;
    Context context;
    ProgressDialog dialog;
    Activity activity;
    private ProgressDialog progressBar;
    private String responseResult,phone,time,from;
    private String url = null, uname = null, upass = null, deviceID = null;
    private JSONArray jsonArray;
    private Database db;
    //private ViewPagerListener listener;
    private TaskConversion taskConversion;

    private static final String TAG="SaveNewTask";

    public SaveNewTask(Context context, JSONArray jsonArray, String phone,String time,String comeFrom)
    {
        this.context = context;
        this.jsonArray=jsonArray;
        this.phone=phone;
        this.time=time;
        this.from=comeFrom;
        db = new Database(context);
        set_app_url();
        Log.i(TAG,"Time: "+time);
        Log.i(TAG,"From: "+from);
        //listener= (ViewPagerListener) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //dialog = ProgressDialog.show(context, "Wait", "Please wait");

        progressBar = ProgressDialog.show(context, "Message", "Data Loading...");
        progressBar.setCancelable(true);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG,"onPostExecute is executed");

        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
        if (responseResult != null) {
            try {
                JSONObject responseObject = new JSONObject(responseResult);

                Log.i(TAG, "Post execute");
                Log.i(TAG,"In Thread PostExecute: "+url);
                Log.i(TAG,responseObject.toString());

                String status = responseObject.getString("status").toString();
                String success = responseObject.getString("success").toString();
                String reason = responseObject.getString("reason").toString();

                //Log.i(TAG,status+" "+success+" "+reason);
                if (status.equals("4000") && success.equals("true") && reason.equals("Valid Data")) {
                    Log.i(TAG,"Will not execute!");
                    db.changeCallStatusBeforeClear();
                    db.clearAllTask();
                    if (from.equals("service")){
                        Log.i(TAG,"Task saved from service");
                    }else {
                        taskConversion=new TaskConversion(context);
                        taskConversion.taskMaking(phone,"",time,"",from);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "Exception: "+e.getMessage());
            }
        }
    }

    @Override
    protected String doInBackground(Void... params) //HTTP
    {
        try {

            String status = null;
            String success = "4000";
            int statusCode = 0;
            String username = uname;
            String password = upass;
            Log.e("Username: ", uname);
            Log.e("password: ", upass);
            //username ="Onu$erVe9";
            //password ="p#@$aS$";
            Log.i("CList", "1 url:" + url);

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);
            Log.i("CList", "2");
            HttpPost httppost = new HttpPost(url);
            Log.i("CList", "3");
            httppost.setHeader("Content-type", "application/json");
            //---------------------Code for Basic Authentication-----------------------
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);
            Log.i("CList", "4");
            Log.i(TAG,"JSON: "+jsonArray.toString());//off by bdn

            StringEntity myStringEntity = new StringEntity(jsonArray.toString(), "UTF-8");
            httppost.setEntity(myStringEntity);
            //--------------execution of httppost
            HttpResponse response = httpclient.execute(httppost);
            String res = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "response: " + res);//off by bdn
            responseResult = res;
            statusCode = response.getStatusLine().getStatusCode();
            // if(status.equals(success))
            if (statusCode >= 200 && statusCode <= 299) {
                Toast.makeText(context, "Sending . . .", Toast.LENGTH_SHORT).show();
                return null;
            } else
                return null;

        } catch (Exception e) {
            Log.i("CList", "exception:" + e);
        }

        return null;
    }

    public void set_app_url() {
        //Toast.makeText(context,"setup url....",Toast.LENGTH_LONG).show();
        List<Contact> contacts = db.getAdminNumber();
        //Toast.makeText(context,contacts.get(1).getName(),Toast.LENGTH_LONG).show();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/inAppPopUP";
                Log.i(TAG,"On make: "+url);
                //http://api1.onuserver.com:8085/v3/incomingSms
            } else if (cn.getName().equals("email"))

            {
                uname = cn.getPhone_number();
                //Log.i(TAG,"Email: "+uname);

            } else if (cn.getName().equals("password"))

            {
                upass = cn.getPhone_number();
                //Log.i(TAG,"Password: "+upass);
            } else if (cn.getName().equals("did")) {
                deviceID = cn.getPhone_number();
                Log.i(TAG,"DID: "+deviceID);
            }
        }
    }

}

