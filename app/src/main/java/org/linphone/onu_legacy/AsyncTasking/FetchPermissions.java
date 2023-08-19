package org.linphone.onu_legacy.AsyncTasking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.Info;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 3/29/2016.
 */
public class FetchPermissions extends AsyncTask<Void, Void, String> {
    int TIMEOUT_MILLISEC = 5000;
    Context context;
    ProgressDialog dialog;
    Activity activity;
    private String rcvdsms;
    private String rcvdnum;
    private String uniq;
    private String rcvtime;
    private String url;
    private String location;
    private String imei;
    private String uname;
    private String upass;
    private int  post_sms_count=0;
    private Info info;

    private String TAG=FetchPermissions.class.getSimpleName();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    public FetchPermissions(Context context) {
        this.context = context;
        info=new Info(context);
        url=info.getUrl()+"/fetchPermissions";
    }


    @Override
    protected void onPostExecute(String result) {

    }



    @Override
    protected String doInBackground(Void... params) {
        try {
            Log.i("Jhoro", "smsposter-3");
            String status = null;
            String success = "4000";
            int statusCode = 0;
            String username = info.getUsername();
            String password = info.getPassword();

            Log.i("Jhoro", "smsposter-3.1:" + username);
            Log.i("Jhoro", "smsposter-4:" + password);

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
            jsonParam.accumulate("trnxID", info.getDate("ddMMyyyyhhmmss"));
            jsonParam.accumulate("trnxTime", info.getDate("dd-MM-yyyy  hh:mm:ss"));

            // StringEntity myStringEntity = new StringEntity(jsonParam.toString(), "UTF-8");

            Log.i("Jhoro", "my jSon: " + jsonParam);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            Log.i("Jhoro", "smsposter-7");
            // --------------execution of httppost
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode <= 299) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject json = new JSONObject(response.toString());
                    Log.i("Jhoro", "response:" + json.toString());
                    // getting from jSon body
                    statusCode = responseCode;

                    if (statusCode >= 200 && statusCode <= 299) {
                        Log.d(TAG, json.toString());

                        Log.i("Jhoro", "smsposter-9");
                        Database db = new Database(context);
                        db.deleteAdmin("smsIn", "jhorotek");
                        db.deleteAdmin("smsOut", "jhorotek");
                        db.deleteAdmin("callIn", "jhorotek");
                        db.deleteAdmin("callOut", "jhorotek");
                        db.deleteAdmin("recorder", "jhorotek");
                        db.addAdminNumber(new Contact("smsIn", json.getString("sms_in"), "jhorotek"));
                        db.addAdminNumber(new Contact("smsOut", json.getString("sms_out"), "jhorotek"));
                        db.addAdminNumber(new Contact("callIn", json.getString("call_in"), "jhorotek"));
                        db.addAdminNumber(new Contact("callOut", json.getString("call_out"), "jhorotek"));
                        db.addAdminNumber(new Contact("recorder", json.getString("record"), "jhorotek"));

                        Log.e("Fetch Permission", "My status is " + status);

                        return status;
                    } else {
                        return null;
                    }
                }
            } else {
                Log.e(TAG, "HTTP Response Code: " + responseCode);
                return null;
            }
        } catch (Exception ex) {
            Log.i("Jhoro", "smsposter-10 (Exception)" + ex);
            return null;
        }
    }
}