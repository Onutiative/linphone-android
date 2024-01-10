package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;
//not in used

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.AdminDataClasses.AdminInfo;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PullServerContact extends AsyncTask<Void, Void, String> {
    private Context context;
    //now this URL used for test
    //next we will set this as dynamic
    private final String BASE_URL ="http://api.onukit.com/6v1.1";
    private String url="",userName,userPassword,deviceId, responseResult;
    private int TIMEOUT_MILLISEC = 5000, statusCode;
    private Database sqLiteDB;
    private boolean selectionOption;

    private static final String TAG="PullServerContact";
    private SharedPrefManager sharedPrefManager;

    private List<ContactDetails> contacts=new ArrayList<>();
    private ContactListener listener;
    private ProgressDialog dialog;

    public PullServerContact(Context context,boolean selectionOption) {
        Log.i(TAG,"Constructor called");
        this.context=context;
        this.selectionOption=selectionOption;
//        setUp();
        sqLiteDB=new Database(context);
        sharedPrefManager=new SharedPrefManager(context);
        setUp();
        Log.i(TAG,"Constructor ending");
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Pulling contacts....");
        dialog.show();

        Log.i(TAG,"Pre execute called1");
        super.onPreExecute();

        Log.i(TAG,"Pre execute called2");
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();

        Log.i(TAG,"Post Execute Called");

        try {
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

                }
                else if (json instanceof JSONArray)
                {
                    Log.i(TAG,"JSONArray is here");
                    JSONArray jsonArray = new JSONArray(responseResult);
                    Log.i(TAG,jsonArray.toString());

                    sharedPrefManager.setSmsReceivedFromServer(jsonArray.length());

                    if (jsonArray.length() > 0) {
                        // i am working in here
                        for (int i = 0; i < jsonArray.length(); ++i) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String contactId = jsonObject.getString("id");
                            String appUserId = jsonObject.getString("app_user_id");
                            String userDeviceId = jsonObject.getString("user_device_id");
                            String contactName = jsonObject.getString("contact_name");
                            String contactValue = jsonObject.getString("contact_value");
                            String syncData = jsonObject.getString("sync_date");

                            Log.e(TAG, contactId + "; " + appUserId + "; " + userDeviceId + "; "
                                    + contactName + "; " + contactValue + "; "+syncData);

                            ContactDetails contact;
                            if (selectionOption){
                                 //contact=new ContactDetails(contactId,appUserId,userDeviceId,contactName,contactValue,syncData,false);
                            }else {
                                //contact=new ContactDetails(contactId,appUserId,userDeviceId,contactName,contactValue,syncData);
                            }

                            //contacts.add(contact);
                        }
                        Log.i(TAG,"Total contact: "+contacts.size());
                        listener = (ContactListener) context;
                        listener.toContactAdapter(contacts,selectionOption);
                    }
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //already made my modifications in doInBackGround
    @Override
    protected String doInBackground(Void... voids) {
        Log.i(TAG, "Background called");

        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
            connection.setConnectTimeout(TIMEOUT_MILLISEC);
            connection.setReadTimeout(TIMEOUT_MILLISEC);
            connection.setRequestMethod("POST");

            String credentials = userName + ":" + userPassword;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", "Basic " + credBase64);
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);

            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("device_id", deviceId);
            jsonObject.accumulate("start", "0");
            jsonObject.accumulate("pull_count", "");

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            String res = "";
            if (responseCode >= 200 && responseCode <= 299) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        res += line;
                    }
                }
                Log.i(TAG, "response: " + res);
                responseResult = res;

                statusCode = responseCode;

                Log.i(TAG, "Response");

                if (statusCode >= 200 && statusCode <= 299) {
                    Log.i(TAG, "Data reached");
                } else {
                    Log.i(TAG, "Data can't be reached");
                }
            } else {
                Log.e(TAG, "HTTP Response Code: " + responseCode);
            }
        } catch (Exception ex) {
            Log.i(TAG, "Exception: " + ex);
            ex.printStackTrace();
        }
        return null;
    }



    private void setUp()
    {

        List<AdminInfo> adminInfos = sqLiteDB.getAdminInformation();
        for (AdminInfo info : adminInfos) {
//            if (info.getDataKey().equals("Custom_url")) {
//                if (!info.getSecondValue().equals(""))
//                    url = info.getSecondValue() + "/pullOutSms";
//            } else

            url=BASE_URL+"/contact/getContactApi";
            if (info.getDataKey().equals("email"))
            {
                userName = info.getSecondValue();
            } else if (info.getDataKey().equals("password"))
            {
                userPassword = info.getSecondValue();
            } else if (info.getDataKey().equals("did")) {
                deviceId = info.getSecondValue();
            }
        }
    }

    private String getTimeStamp(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public interface ContactListener{
        public void toContactAdapter(List<ContactDetails> contactList,boolean selectionOption);
    }

}
