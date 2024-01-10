package org.linphone.onu_legacy.Receivers;

/**
 * Created by jhorotek on 9/2/2015.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONObject;
import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.AsyncTasking.IncomingSmsPoster;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.NetworkUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public String registration="null";
    public String rcvdnum;
    public String rcvdsms;
    public String rcvtime;
    public String uniq;
    public String url;
    public String imei;
    public String uname;
    public String upass;
    public String location="null";
    public int post_sms_count;
    //
    @Override
    public void onReceive(final Context context, final Intent intent) {

        try {
            String status = NetworkUtil.getConnectivityStatusString(context);
            Database db = new Database(context);
            if (!status.equals("Not connected to Internet")) {
                //set_app_url(context);
                List<Contact> contactt = db.getAdminNumber();
                for (Contact cn : contactt) {
                    if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true"))
                    {
                        registration = "true";
                        if (!Integer.toString(db.getSmsCount()).equals("0"))
                        {
                            new IncomingSmsPoster(context,location,imei).execute();
                        }
                    }


                }
            }
        }catch (Exception e){
            Log.v("Jhoro","Exception");
        }

        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }

    public class HttpEditInfo extends AsyncTask<Void, Void, String>
    {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;


        @Override
        protected void onPreExecute() {
        super.onPreExecute();
        //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        Database dd = new Database(context);
        if(!Integer.toString(dd.getSmsCount()).equals("0")) {
            List<Contact> smses = dd.getAllsms();
            for (Contact cn : smses) {
                rcvdsms = cn.getName();
                rcvdnum = cn.getPhone_number();
                uniq = cn.getTime();
                Long timestamp = Long.parseLong(uniq);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();
                rcvtime = smsDate;
                Log.v("Jhoro", "done");
                doInBackground();
            }
        }
    }

        public HttpEditInfo(Context context) {
            this.context = context;
        }
        @Override
        protected void onPostExecute(String result)
        {

            Log.v("Jhoro", "post");
            Database dd = new Database(context);
            Intent i = new Intent(context, DashBoard_Activity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            //context.startActivity(i);

            if(result != null) {



                Log.v("Jhoro", "if ok");
                if(result.equals("4000"))
                {
                    //dd.addContact(new Contact(rcvdsms, rcvdnum, rcvtime));
                    dd.deletesms(rcvdsms, uniq);
                    post_sms_count++;
                    dd.updatePost(Integer.toString(post_sms_count));
                    Toast.makeText(context, result, Toast.LENGTH_SHORT).show();

                    if(!Integer.toString(dd.getSmsCount()).equals("0")) {
                        List<Contact> smses = dd.getAllsms();
                        for (Contact cn : smses) {
                            rcvdsms = cn.getName();
                            rcvdnum = cn.getPhone_number();
                            uniq = cn.getTime();
                            Long timestamp = Long.parseLong(uniq);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(timestamp);
                            Date finaldate = calendar.getTime();
                            String smsDate = finaldate.toString();
                            rcvtime = smsDate;
                            Log.v("Jhoro", "done");
                            new HttpEditInfo(context).execute();
                        }
                    }

                }
                else if(result.equals("4200")){
                    dd.deletesms(rcvdsms, rcvtime);



                }

                //super.onPostExecute(result);

            }
            else
            {


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

                Log.v("Jhoro", "background");
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                        .readTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                        .build();

                List<Pair<String, String>> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new Pair<>("demo", "demo"));

                StringBuilder paramsBuilder = new StringBuilder();
                for (Pair<String, String> pair : nameValuePairs) {
                    if (paramsBuilder.length() > 0) {
                        paramsBuilder.append("&");
                    }
                    paramsBuilder.append(pair.first)
                            .append("=")
                            .append(URLEncoder.encode(pair.second, "UTF-8"));
                }

                String fullUrl = url + "?" + paramsBuilder.toString();

                RequestBody requestBody = new FormBody.Builder()
                        .add("sms", rcvdsms)
                        .add("device_id", imei)
                        .add("sms_id", uniq)
                        .add("mobile", rcvdnum)
                        .add("sms_received_time", rcvtime)
                        .add("location", location)
                        .build();

                Request request = new Request.Builder()
                        .url(fullUrl)
                        .post(requestBody)
                        .header("Content-type", "application/json")
                        .header("Authorization", "Basic " + getBase64Credentials(username, password))
                        .build();

                Response response = client.newCall(request).execute();
                String res = response.body().string();
                JSONObject json = new JSONObject(res);
                Log.v("Jhoro", json.toString());
                status = json.getString("status");
                statusCode = response.code();
                Log.v("Jhoro", "my Status: " + statusCode);

                if (statusCode >= 200 && statusCode <= 299) {
                    Database db = new Database(context);
                    db.deletesms(rcvdsms, rcvtime);
                    return status;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                Log.v("hhtp", " Exception ");
                return null;
            }
        }

    }

    private String getBase64Credentials(String username, String password) {
        String credentials = username + ":" + password;
        return Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
    }

    public void set_app_url(Context context)
    {
        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if(cn.getName().equals("Custom_url")) {
                if(!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number();
            }
            if (cn.getName().equals("PostedUP")  )

            {
                post_sms_count=Integer.parseInt(cn.getPhone_number());

            }
            if (cn.getName().equals("email")  )

            {
                uname = cn.getPhone_number();

            }
            if (cn.getName().equals("password")  )

            {
                upass=cn.getPhone_number();

            }
        }

    }



}