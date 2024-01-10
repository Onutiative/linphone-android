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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RONI on 7/29/2015.
 */
public class DeliveryReportPoster extends AsyncTask<Void, Void, String>  {
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        set_app_status();
    }

    public DeliveryReportPoster(Context context, String imei) {
        this.context = context;
        this.imei=imei;
    }
    @Override
    protected void onPostExecute(String result) {

    }

    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }
    @Override
    protected String doInBackground(Void... params) {

        Database db = new Database(context);
        if (db.getThreadCount() > 0) {
            Log.i("JhoroS", "ReportSend_doinbackground");
            String username = "Onu$erVe9";
            String password = "p#@$aS$";
            Log.i("Jhoro", "doinback");
            try {
                Log.i("Jhoro", "ReportSend");

                URL url = new URL(this.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(TIMEOUT_MILLISEC);
                connection.setReadTimeout(TIMEOUT_MILLISEC);
                connection.setRequestProperty("Content-type", "application/json");

                //---------------------Code for Basic Authentication-----------------------
                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
                connection.setRequestProperty("Authorization", "Basic " + credBase64);
                //---------------------------------Body Data-------------------------------------
                JSONArray jsonArr = new JSONArray();

                List<Contact> contacts = db.getallthread();
                ArrayList<String> mylist = new ArrayList<String>();
                for (Contact cn : contacts) {
                    JSONObject jsonParam = new JSONObject();
                    if (cn.getTime().equals("s")) {
                        jsonParam.accumulate("smsID", cn.getName());
                        jsonParam.accumulate("report", "sent");
                        jsonParam.accumulate("time", cn.getPhone_number());
                    } else if (cn.getTime().equals("e")) {
                        jsonParam.accumulate("smsID", cn.getName());
                        jsonParam.accumulate("report", "error");
                        jsonParam.accumulate("time", cn.getPhone_number());
                    } else if (cn.getTime().equals("d")) {
                        jsonParam.accumulate("smsID", cn.getName());
                        jsonParam.accumulate("report", "delivered");
                        jsonParam.accumulate("time", cn.getPhone_number());
                    }

                    if (mylist.size() < 100) {
                        jsonArr.put(jsonParam);
                        mylist.add(cn.getName()); // this adds an element to the list.
                    } else {
                        break;
                    }

                    Log.i("JhoroS", "listSize:" + mylist.size());
                }

                Log.i("JhoroS", "Status jSon: " + jsonArr.toString());
                String requestBody = jsonArr.toString();

                connection.setDoOutput(true);
                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(requestBody);
                os.flush();
                os.close();

                int statusCode = connection.getResponseCode();
                Log.i("JhoroS", "Status my Status: " + statusCode);
                String responseResult = "";

                if (statusCode >= 200 && statusCode <= 299) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseResult += line;
                    }
                    reader.close();
                    Log.i("Jhoro", "Statu: ");
                    if (responseResult.equals("4000")) {
                        for (int i = 0; i < mylist.size(); i++) {
                            db.deleteThread(mylist.get(i));
                        }
                        //db.removeallthread();
                    }
                    return responseResult;
                } else {
                    return null;
                }

            } catch (Exception ex) {
                Log.i("JhoroS", "exception=" + ex);
                return null;
            }
        } else {
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
                    url = cn.getPhone_number()+"/outgoingReport";
                Log.i("JhoroS", "URL="+url);
            }
            else if (cn.getName().equals("apptype")  )
            {
                user_type=cn.getPhone_number();
            }
            else if (cn.getName().equals("response")  )
            {
                last_response=cn.getPhone_number();
            }
            else if (cn.getName().equals("setUP")  )

            {

                TotalSent=cn.getPhone_number();

            }
            else if (cn.getName().equals("PostedUP")  )

            {
                TotalPost=cn.getPhone_number();

            }
            else if (cn.getName().equals("email")  )

            {
                uname=cn.getPhone_number();

            }
            else if (cn.getName().equals("password")  )

            {
                upass=cn.getPhone_number();

            }

            else
            {

            }
        }
    }

}