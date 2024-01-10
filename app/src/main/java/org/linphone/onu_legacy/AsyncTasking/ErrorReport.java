package org.linphone.onu_legacy.AsyncTasking;

/**
 * Created by jhorotek on 8/10/2015.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RONI on 7/29/2015.
 */
public class ErrorReport extends AsyncTask<Void, Void, String>  {
    int TIMEOUT_MILLISEC = 5000;
    Context context;
    public boolean go=false;
    public String command="set status";
    public String set_id ="not set";
    public String set_msg="not set";
    public String set_number="not set";
    public String report_time="not set";
    public String imei;
    public String url;
    public String errorType;
    public String uname;
    public String upass;
    public String outbox_notify="no";
    public int sent_sms_count;
    public int count =1;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        set_app_url();

        Log.v("Jhoro", "Delivery_doinbak:entry");


    }
    public ErrorReport(Context context, String imei, String sid,String error)
    {
        this.context = context;
        this.imei=imei;
        this.errorType=error;

        this.set_id=sid;
        Log.v("Jhoro", "Delivery_Constructor");
    }


    @Override
    protected void onPostExecute(String result) {

        if(result != null)
        {
            try {
                Database db = new Database(context);
                db.deleteOutbox(set_id);
                db.deleteOutboxtow(set_id);
                Log.v("Jhoro", "Deleting data >>>>"+set_id);
                super.onPostExecute(result);
            } catch (Exception ex)
            {
                Log.v("error", ex.getMessage());
            }
        }
        else{
            Log.v("error","null response");
        }
    }
    @Override
    protected String doInBackground(Void... params) {
        Database db = new Database(context);
        Log.v("Jhoro", "Sendout_checknotify");
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("email")) {
                uname = cn.getPhone_number();
            } else if (cn.getName().equals("password")) {
                upass = cn.getPhone_number();
            }
        }

        String username = uname;
        String password = upass;
        Log.v("Jhoro", "Error_doinbak_doinback");
        try {
            Log.v("Jhoro", "Error_doinbak_SendOut");

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);

            // Add Basic Authentication header
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            connection.setRequestProperty("Content-type", "application/json");

            JSONObject jsonParam = new JSONObject();
            jsonParam.accumulate("command", command);
            jsonParam.accumulate("delivery_status", "Failed");
            jsonParam.accumulate("delivery_response", errorType);
            jsonParam.accumulate("delivery_time", getDate("yyyy-MM-dd hh:mm:ss"));
            jsonParam.accumulate("sms_id", set_id);
            jsonParam.accumulate("device_id", imei);

            String entity = jsonParam.toString();
            Log.v("Jhoro", "Error_doinbak_Data" + entity);

            connection.setDoOutput(true);
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(entity);
            os.flush();
            os.close();

            int statusCode = connection.getResponseCode();
            String response_data = null;

            if (statusCode >= 200 && statusCode <= 299) {
                // Successful response
                InputStream inputStream = connection.getInputStream();
                response_data = convertStreamToString(inputStream);
                inputStream.close();
                Log.v("Jhoro", " Error Response:" + response_data);
                return response_data;
            } else {
                // Handle unsuccessful response
                Log.e("Jhoro", "HTTP Request Failed with status code: " + statusCode);
            }
        } catch (Exception ex) {
            Log.v("Jhoro", "exception");
        }
        return null;
    }

    private String convertStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }


    public void set_app_url()
    {

        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if(cn.getName().equals("Custom_url"))
            {
                if(!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number()+"/pullOutSms";
                //http://api1.onukit.com:8085/v3/incomingSms
            }

        }
    }

    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }




}


