package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.Helper;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CallLogPush extends AsyncTask<Void, Void, String> {
    int TIMEOUT_MILLISEC = 5000;
    private Context context;
    private Database db;
    private String trID;
    private Helper helper;
    private String TAG="CallLogPush";
    private String callType;
    private String phoneNumber;
    private String inTime;

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //dialog = ProgressDialog.show(context, "Wait", "Please wait");
    }
    public CallLogPush(Context context,String trID,String phoneNumber,String inTime,String callType) {
        this.context = context;
        db= new Database(context);
        this.trID=trID;
        this.phoneNumber=phoneNumber;
        this.callType=callType;
        helper=new Helper(context);
        this.inTime=inTime;
    }
    @Override
    protected void onPostExecute(String result)
    {
        if(result != null)
        {
            Log.i(TAG, "Call if ok");
            if(result.equals("4000"))
            {
            }
        }
        else
        {
        }
    }

    @Override
    protected String doInBackground(Void... params)
    {
        try {
            String status=null;
            String success="4000";
            int statusCode =0;
            String username =helper.getUserData().getUsername();
            String password =helper.getUserData().getPassword();
            String call_url=helper.getUserData().getBase_url()+"/callLog";
            Log.i(TAG,"USernsme: "+username);
            Log.i(TAG,"Password: "+password);

            Log.i(TAG, "trnxID: "+trID);
//                int TempId=Integer.parseInt(trID)-1;
//                int TempId1=Integer.parseInt(trID)-2;
//                int TempId3=Integer.parseInt(trID)-3;

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);

            //String url = "https://www.mydomainic.com/api/bkash-central/demo/add/via-sms/";
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("demo","demo"));
            HttpClient httpClient = new DefaultHttpClient();
            String paramsString = URLEncodedUtils.format(nameValuePairs,
                    "UTF-8");
            HttpPost httppost = new HttpPost(call_url);
            Log.i(TAG,"URL: "+call_url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setHeader("Content-type", "application/json");

            //---------------------Code for Basic Authentication-----------------------
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);
            //----------------------------------------------------------------------

            JSONObject jsonParam = new JSONObject();
            //if( dd.lastCallfind(Integer.toString(TempId)) == 0 && dd.lastCallfind(Integer.toString(TempId1)) == 0 && dd.lastCallfind(Integer.toString(TempId3)) == 0)
            {
                jsonParam.accumulate("callerMsisdn", phoneNumber );
                jsonParam.accumulate("transactionId", trID);
                jsonParam.accumulate("device_id", helper.getUserData().getDeviceID());
                jsonParam.accumulate("callType", callType);
                jsonParam.accumulate("parent_id", helper.getUserData().getParent_id());
                jsonParam.accumulate("inTime",inTime );
            }
            Log.i(TAG, "Request Body JSON: " + jsonParam.toString());
            StringEntity myStringEntity = new StringEntity( jsonParam.toString(),"UTF-8");

            httppost.setEntity(myStringEntity);
            HttpResponse response = httpclient.execute(httppost);
            String res= EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Call data up response: "+res);
            JSONObject json =new JSONObject(res);
            status=json.getString("status");  //getting from  jSon body
            statusCode = response.getStatusLine().getStatusCode();
            if(statusCode>=200 && statusCode<=299)
            {
                Log.d(TAG,"Status is OK "+json.toString());
                db.updateCallQueueForLogUp(trID,"1");
                return status;
            }
            else
                return null;
        } catch (Exception ex)
        {
            Log.d(TAG,"Exception here "+ex.getMessage());
            return null;
        }
    }
}
