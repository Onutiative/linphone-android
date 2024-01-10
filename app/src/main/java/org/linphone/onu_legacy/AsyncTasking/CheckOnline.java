package org.linphone.onu_legacy.AsyncTasking;

//Active to pull SMS from online

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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RONI on 7/29/2015.
 */
public class CheckOnline extends AsyncTask<Void, Void, String>  {
    int TIMEOUT_MILLISEC = 5000;
    Context context;
    public boolean go=false;
    public String user_type="give sms";
    public String last_response ="not set";
    public String TotalSent="not set";
    public String TotalPost="not set";
    public String report_time="not set";
    public int sent_sms_count;
    public String  url;
    public String imei;
    public String uname;
    public String upass;
    public String outbox_notify="no";
    public int count =1;

    private String TAG=CheckOnline.class.getSimpleName();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        set_app_status();
    }

    public CheckOnline(Context context, String imei) {
        this.context = context;
        this.imei=imei;
    }
    @Override
    protected void onPostExecute(String result) {
        Log.i("JhoroACK","Status post");
        Database dd = new Database(context);
        if(result != null)
        {
            Log.i("JhoroACK", "Status if ok");
            if(result.equals("4000"))
            {
                dd.deleteAdmin("response", "jhorotek");
                dd.addAdminNumber(new Contact("response",getDate("dd-MM-yyyy  kk:mm:ss"),"jhorotek"));
            }
        }
    }

    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }
    @Override
    protected String doInBackground(Void... params) {
        Log.i("JhoroACK", "CheckOnline_doinbackground");
        String username = "Onu$erVe9";
        String password = "p#@$aS$";
        Log.i("JhoroACK", "doinback");

        try {
            Database dd = new Database(context);
            Log.i("JhoroACK", "CheckOnline");

            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");

            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.accumulate("apptype", user_type);
            jsonParam.accumulate("total_sent", TotalSent);
            jsonParam.accumulate("total_posted", TotalPost);
            jsonParam.accumulate("time", getDate("yyyy-MM-dd kk:mm:ss"));
            jsonParam.accumulate("device_id", imei);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode <= 299) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String status = jsonResponse.getString("status");

                    Log.e(TAG, "From Check Online JSON Object: " + jsonResponse);

                    dd.deleteAdmin("smsOutQuota", "jhorotek");
                    dd.deleteAdmin("smsInQuota", "jhorotek");
                    dd.deleteAdmin("callInQuota", "jhorotek");
                    dd.deleteAdmin("callOutQuota", "jhorotek");
                    dd.addAdminNumber(new Contact("smsOutQuota", jsonResponse.getString("sms_out"), "jhorotek"));
                    dd.addAdminNumber(new Contact("smsInQuota", jsonResponse.getString("sms_in"), "jhorotek"));
                    dd.addAdminNumber(new Contact("callInQuota", jsonResponse.getString("call_in"), "jhorotek"));
                    dd.addAdminNumber(new Contact("callOutQuota", jsonResponse.getString("call_out"), "jhorotek"));

                    Log.i("JhoroACK", "Status my Status: " + responseCode + "\n" + jsonParam);
                    return status;
                }
            } else {
                Log.e(TAG, "HTTP Response Code: " + responseCode);
                return null;
            }
        } catch (Exception ex) {
            Log.i("JhoroACK", "Exception: " + ex);
            return null;
        }
    }



    public void set_app_status()
    {

        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if(cn.getName().equals("Custom_url"))
            {
                if(!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number()+"/deviceStatus";
                //http://api1.onukit.com:8085/v3/incomingSms\
            }
            else if (cn.getName().equals("apptype") )
            {
                user_type=cn.getPhone_number();
                Log.i("User_Type: ", user_type);
            }
            else if (cn.getName().equals("response")  )
            {
                last_response=cn.getPhone_number();
                Log.i("Response: ", last_response);
            }
            else if (cn.getName().equals("setUP") )

            {

                TotalSent=cn.getPhone_number();

            }
            else if (cn.getName().equals("PostedUP") )

            {
                TotalPost=cn.getPhone_number();

            }
            else if (cn.getName().equals("email") )

            {
                uname=cn.getPhone_number();

            }
            else if (cn.getName().equals("password") )

            {
                upass=cn.getPhone_number();

            }

            else
            {

            }
        }
    }

}