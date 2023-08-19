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
            String username ="Onu$erVe9";
            String password ="p#@$aS$";
        Log.i("JhoroACK", "doinback");
        try {
            Database dd = new Database(context);
            Log.i("JhoroACK", "CheckOnline");
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams,TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);

            Log.e(TAG,"From Check Online "+url);

            HttpPost httppost = new HttpPost(url);
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);


            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setHeader("Content-type", "application/json");

            JSONObject jsonParam = new JSONObject();

            jsonParam.accumulate("apptype", user_type);
            jsonParam.accumulate("total_sent", TotalSent);
            jsonParam.accumulate("total_posted", TotalPost);
            jsonParam.accumulate("time", getDate("yyyy-MM-dd kk:mm:ss"));
            jsonParam.accumulate("device_id", imei);
            //{"command":"give sms","msg":"hello testing","id":"not set","number":"01718355460","imei":"6418758"}
            Log.i("JhoroACK", "Status jSon: " + jsonParam.toString());
            String entity = jsonParam.toString();
            Log.i("JhoroACK", "Data"+entity);
            httppost.setEntity(new StringEntity(entity, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(httppost);
            String res=EntityUtils.toString(response.getEntity());
            JSONObject json =new JSONObject(res);
            Log.i("JhoroACK", json.toString());
            String status=json.getString("status");

            Log.e(TAG,"From Check Online JSON Object"+json);

            dd.deleteAdmin("smsOutQuota", "jhorotek");
            dd.deleteAdmin("smsInQuota", "jhorotek");
            dd.deleteAdmin("callInQuota", "jhorotek");
            dd.deleteAdmin("callOutQuota", "jhorotek");
            dd.addAdminNumber(new Contact("smsOutQuota",json.getString("sms_out"),"jhorotek"));
            dd.addAdminNumber(new Contact("smsInQuota",json.getString("sms_in"),"jhorotek"));
            dd.addAdminNumber(new Contact("callInQuota",json.getString("call_in"),"jhorotek"));
            dd.addAdminNumber(new Contact("callOutQuota",json.getString("call_out"),"jhorotek"));

            //getting from  jSon body
            int statusCode = response.getStatusLine().getStatusCode();
            Log.i("JhoroACK", "Status my Status: " + statusCode+"\n"+jsonParam);
            // if(status.equals(success))
            if(statusCode>=200 && statusCode<=299)
            {

                Log.i("JhoroACK", "Statu: ");

                return status;
            }
            else
                return null;

        } catch (Exception ex)
        {
            Log.i("JhoroACK", "exeption:"+ex);
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