package org.linphone.onu_legacy.AsyncTasking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.Info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by android on 3/29/2016.
 */
public class IncomingSmsPoster extends AsyncTask<Void, Void, String> {

    Context context;
    private String rcvdsms;
    private String rcvdnum;
    private String uniq;
    private String rcvtime;
    private String url;
    private Info info;
    private String location;
    private String imei;
    private String uname;
    private String upass;
    private String parentID;
    int TIMEOUT_MILLISEC = 5000;
    private int  post_sms_count=0;
    private String TAG = "IncomingSmsPoster";



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.i(TAG, "smsposter-1");
        Database db = new Database(context);
        InintData();
        if(!Integer.toString(db.getSmsCount()).equals("0") )
        {
            List<Contact> contacts = db.getAllsms();
            for (Contact cn : contacts)
            {
                //2016-02-17 12:36:13
                rcvdsms = cn.getName();
                rcvdnum = cn.getPhone_number();
                uniq = cn.getTime();
                Long timestamp = Long.parseLong(uniq);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(timestamp);
                Date finaldate = calendar.getTime();
                String smsDate = finaldate.toString();
                rcvtime = info.getDate("yyyy-MM-dd hh:mm:ss");
                //doInBackground();
                Log.i(TAG, "smsposter-2");
            }
        }else {
            Log.i(TAG,"No sms found!");
        }
    }

    public IncomingSmsPoster(Context context, String location, String imei) {
        this.context = context;
        this.location=location;
        this.imei=imei;
        info=new Info(context);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "smsposter-11");
        //Log.i(TAG,"Response: "+result);
        Database dd = new Database(context);
        if(result != null)
        {
            Log.i(TAG, "smsposter-12");
            if(!rcvdsms.equals("onuServer App is Alive") && result.equals("4000"))
            {
                //dd.addContact(new Contact(rcvdsms, rcvdnum, rcvtime));
                dd.deletesms(rcvdsms, uniq);
                post_sms_count++;
                dd.updatePost(Integer.toString(post_sms_count));
                dd.addContact(new Contact(rcvdsms, rcvdnum, rcvtime));
                //last_sent.setText("\nResponse:\n"+rcvtime);
                Log.i(TAG, "smsposter-13");
                //Toast.makeText(context,"posted", Toast.LENGTH_SHORT).show();

                if(!Integer.toString(dd.getSmsCount()).equals("0") && info.isNetworkAvailable())
                {
                    List<Contact> contacts = dd.getAllsms();
                    for (Contact cn : contacts)
                    {

                        new IncomingSmsPoster(context,"loc",info.getImei()).execute();

                    }
                }
            }
            else if( result.equals("4200"))
            {
                dd.deletesms(rcvdsms, uniq);
                dd.deleteAdmin("response", "jhorotek");
                dd.addAdminNumber(new Contact("response",info.getDate("dd-MM-yyyy  hh:mm:ss"),"jhorotek"));
                //last_sent.setText("\nResponse:\n"+rcvtime);
                Log.i(TAG, "smsposter-15");
            }

        }else {
            Log.i(TAG,"Response null");
        }
    }

    public void InintData() {
        post_sms_count=Integer.parseInt( info.postedSmsCount());
        url=info.getUrl()+"/incomingSms";
        uname=info.getUsername();
        upass=info.getPassword();
        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("parent_id")){
                parentID=cn.getPhone_number();
            }
        }
        Log.i(TAG,"URL: "+url);
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.i(TAG, "smsposter-3");
            String status = null;
            String success = "4000";
            int statusCode = 0;
            String username = uname;
            String password = upass;
            Log.i(TAG, "smsposter-4:" + url);
            Log.i(TAG, "backgrounds " + uname + " " + upass);

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);

            // Add Basic Authentication header
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            connection.setRequestProperty("Content-type", "application/json");

            Log.i(TAG, "smsposter-5");

            JSONArray post_param = new JSONArray();
            JSONObject jsonParam = new JSONObject();

            jsonParam.accumulate("sms", rcvdsms);
            jsonParam.accumulate("sms_id", uniq);
            jsonParam.accumulate("mobile", rcvdnum);
            jsonParam.accumulate("sms_received_time", rcvtime);
            jsonParam.accumulate("device_id", imei);
            jsonParam.accumulate("location", location);
            jsonParam.accumulate("parent_id", parentID);

            post_param.put(jsonParam);
            Log.i(TAG, "smsposter-6");

            Log.i(TAG, "my jSon: " + post_param.toString());

            connection.setDoOutput(true);

            Log.i(TAG, "smsposter-7");

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            Log.i(TAG, "Response Code: " + responseCode);
            Log.i(TAG, "Response Message: " + responseMessage);

            if (responseCode >= 200 && responseCode <= 299) {
                Log.i(TAG, "smsposter-9");
                Database db = new Database(context);
                db.deletesms(rcvdsms, uniq);
                return status;
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.i(TAG, "smsposter-10 (Exception)" + ex);
            return null;
        }
    }
}