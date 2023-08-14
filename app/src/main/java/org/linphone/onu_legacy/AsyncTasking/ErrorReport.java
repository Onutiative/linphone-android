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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
        for (Contact cn : contacts)
        {
            //Toast.makeText(this,cn.getName(),Toast.LENGTH_SHORT).show();
            if (cn.getName().equals("email")  )

            {
                uname=cn.getPhone_number();
            }
            else if (cn.getName().equals("password")  )

            {
                upass=cn.getPhone_number();

            }
        }

        //Database db = new Database(context);
        String username = uname;
        String password = upass;
        Log.v("Jhoro", "Error_doinbak_doinback");
        try {
            Log.v("Jhoro", "Error_doinbak_SendOut");
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);


            HttpPost httppost = new HttpPost(url);
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setHeader("Content-type", "application/json");
            JSONObject jsonParam = new JSONObject();
            //{"command":"set status","sms_id":"441","device_id":"357080054613915","status":"sent",
            // "delivery_time":"2016-03-04 06:48:49","details":"server_response"}

            jsonParam.accumulate("command", command);
            jsonParam.accumulate("delivery_status", "Failed");
            jsonParam.accumulate("delivery_response", errorType);
            jsonParam.accumulate("delivery_time", getDate("yyyy-MM-dd hh:mm:ss"));
            jsonParam.accumulate("sms_id", set_id);
            jsonParam.accumulate("device_id", imei);

            String entity = jsonParam.toString();
            Log.v("Jhoro", "Error_doinbak_Data" + entity);
            httppost.setEntity(new StringEntity(entity, HTTP.UTF_8));
            Log.v("Jhoro", "_P16_");
            HttpResponse response = httpclient.execute(httppost);
            Log.v("Jhoro", "_P17_");
            String response_data = EntityUtils.toString(response.getEntity());
            Log.v("Jhoro", " Error Response:" + response_data);
            return response_data;

        } catch (Exception ex) {
            Log.v("Jhoro", "exeption");
            return null;
        }



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
                //http://api1.onuserver.com:8085/v3/incomingSms
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


