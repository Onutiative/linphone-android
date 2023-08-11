package org.linphone.onu_legacy.AsyncTasking;

/**
 * Created by jhorotek on 8/10/2015.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;

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
import org.json.JSONArray;
import org.json.JSONObject;

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
        if(db.getThreadCount() >0)
        {
            Log.i("JhoroS", "ReportSend_doinbackground");
            String username = "Onu$erVe9";
            String password = "p#@$aS$";
            Log.i("Jhoro", "doinback");
            try {
                Log.i("Jhoro", "ReportSend");
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpParams p = new BasicHttpParams();
                HttpClient httpclient = new DefaultHttpClient(p);

                // HttpPost httppost = new HttpPost(url);
                // HttpGet httpget=new HttpGet(url);

                HttpPost httppost = new HttpPost(url);

                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
                httppost.setHeader("Authorization", "Basic " + credBase64);


                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                httppost.setHeader("Content-type", "application/json");
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


                    Log.i("JhoroS", "listSize:"+mylist.size() );



                }

                //db.removeallthread();
                Log.i("JhoroS", "Status jSon: " + jsonArr.toString());
                String entity = jsonArr.toString();
                Log.i("Jhoro", "Data" + entity);
                httppost.setEntity(new StringEntity(entity, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                String res = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(res);
                Log.i("JhoroS", json.toString());
                String status = json.getString("status");  //getting from  jSon body
                int statusCode = response.getStatusLine().getStatusCode();
                Log.i("JhoroS", "Status my Status: " + statusCode);
                // if(status.equals(success))
                if (statusCode >= 200 && statusCode <= 299) {

                    Log.i("Jhoro", "Statu: ");
                    if (status.equals("4000")) {
                        for(int i = 0; i < mylist.size(); i++) {
                           db.deleteThread(mylist.get(i));
                        }
                        //db.removeallthread();
                    }

                    return status;
                } else
                    return null;

            } catch (Exception ex) {
                Log.i("JhoroS", "exeption="+ex);
                return null;
            }
        }
        else
        {
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