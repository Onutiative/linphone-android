package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.TaskConversion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.GZIPInputStream;

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
        try {
            progressBar = ProgressDialog.show(context, "Message", "Data Loading...");
            progressBar.setCancelable(true);
        }catch (Exception e){
            Log.i(TAG,"Exception: "+e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG,"onPostExecute is executed");

        try {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }catch (Exception e){
            Log.i(TAG,"Exception: "+e.getMessage());
        }

        Log.i(TAG,"Response: "+responseResult==null?"null":responseResult);

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
                        taskConversion.taskMaking(phone,"",time,"",from, null);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i(TAG, "Exception: "+e.getMessage());
            }
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            String status = null;
            String success = "4000";
            int statusCode = 0;
            String username = uname;
            String password = upass;

            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            // Set connection properties
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Add Basic Authentication header
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            connection.setRequestProperty("Authorization", "Basic " + credBase64);

            // Enable input/output streams for the request
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Send JSON data in the request body
            OutputStream os = connection.getOutputStream();
            os.write(jsonArray.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            // Get the response code
            statusCode = connection.getResponseCode();

            if (statusCode >= 200 && statusCode <= 299) {
                // Get response input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                // Read the response line by line
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // update the responseResult global variable
                responseResult = response.toString();

                // Parse the JSON response
                JSONObject responseObject = new JSONObject(response.toString());

                int response_status = responseObject.getInt("status");
                String response_reason = responseObject.getString("reason");

                String toast_message;
                if (response_status == 4000) {
                    toast_message = "Task Saved Successfully. " + response_reason;
                } else {
                    toast_message = "Task Not Saved. " + response_reason;
                }

                // Show toast on the UI thread
                Log.i(TAG, "HTTP Request Succeeded with status code: " + statusCode);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        // return to Dashboard if if (response_status == 4000)
                        if (response_status == 4000 && !from.equals("service")) {
                            Toast.makeText(context, toast_message, Toast.LENGTH_LONG).show();
                            // sleep for 1.5 seconds
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(context, org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity.class);
                            // add flags to clear the activity stack
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                });
            } else {
                // Handle unsuccessful response
                String toast_message = "HTTP Request Failed with status code: " + statusCode;
                Log.e(TAG, toast_message + statusCode);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!from.equals("service")) {
                            Toast.makeText(context, toast_message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
            // Show toast on the UI thread
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(!from.equals("service")) {
                        Toast.makeText(context, "Failed | Exception: " + e, Toast.LENGTH_LONG).show();
                    }
                }
            });
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
                //http://api1.onukit.com:8085/v3/incomingSms
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

