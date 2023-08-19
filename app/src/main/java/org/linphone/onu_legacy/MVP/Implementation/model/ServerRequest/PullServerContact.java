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
import org.json.JSONObject;
import org.json.JSONTokener;

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
        Log.i(TAG,"Background called");

        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);
            HttpPost httppost = new HttpPost(url);
            Log.i(TAG,"URL: "+url);
            httppost.setHeader("Content-type", "application/json");

            //---------------------Code for Basic Authentication-----------------------
            String credentials = userName + ":" + userPassword;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);
            //----------------------------------------------
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("device_id", deviceId);
            jsonObject.accumulate("start","0" );
            jsonObject.accumulate("pull_count", "");

            //----------------------------------------------------------------
            Log.i(TAG,"Request Body: "+jsonObject.toString());
            StringEntity myStringEntity = new StringEntity(jsonObject.toString(), "UTF-8");
            httppost.setEntity(myStringEntity);

            //--------------execution of httppost
            HttpResponse response = httpclient.execute(httppost);
            String res = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "response: " + res);
            responseResult = res;

            statusCode = response.getStatusLine().getStatusCode();

            Log.i(TAG,"Response");

            if (statusCode >= 200 && statusCode <= 299) {

                Log.i(TAG,"Data reached");
            }else {
                Log.i(TAG,"Data can't reached");
            }
        }catch(Exception ex)
        {
            Log.i(TAG,"Exception: "+ex);
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
