package org.linphone.onu_legacy.AsyncTasking;

/**
 * Created by jhorotek on 8/10/2015.
 * not active
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Database.Sms;
import com.onutiative.onukit.SMS_Sender.MsgSender;
import com.onutiative.onukit.Utility.Info;

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
import java.util.Calendar;
import java.util.List;

/**
 * Created by RONI on 7/29/2015.
 */
public class PullSmsFromServer extends AsyncTask<Void, Void, String>  {
    int TIMEOUT_MILLISEC = 5000;
    Context context;
    public boolean go=false;
    public String command="give sms";
    public String set_id ="not set";
    public String set_msg="not set";
    public String set_number="not set";
    public String report_time="not set";
    public int sent_sms_count;
    public String imei;
    public String url;
    public String uname;
    private Info info;
    public String upass;
    public String outbox_notify="no";
    public int count =1;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        set_app_url();
        Log.i("JhoroSendOut", "Sendout_preExecute");
        try {
            Database d = new Database(context);
            d.updateCheckOut("full");
            check_notify();
            Log.i("JhoroSendOut", "Sendout_1");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public PullSmsFromServer(Context context, String imei) {
        this.context = context;
        this.imei=imei;
        info=new Info(context);
    }

    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("JhoroSendOut", "sendOut_post"+result);
        if(result != null)
        {
            try {
                Database ddb = new Database(context);
               // ddb.deleteAdmin("notify", "jhorotek");
                    Log.i("JhoroSendOut", "sendOut_post : 1");
                    JSONArray cast = new JSONArray(result);
                Log.i("JhoroSendOut", "sendOut_post : 2");
                int count=0;
                    for (int i = 0; i < cast.length(); i++)
                    {
                        count++;
                        JSONObject actor = cast.getJSONObject(i);
                        String sms_id = actor.getString("sms_id");
                        String sms = actor.getString("sms_text");
                        String number = actor.getString("mobile_no");
                        String sms_status = actor.getString("status");
                        Log.i("JhoroSendOut", "sendOut_post : 3");

                        // Possible problematic check after proper checking. It needs to be removed!

                        if (sms_status.equals("0"))
                        {
                            ddb.deleteOutboxtow(sms_id);
                            ddb.addOutboxtow(new Sms(sms, number, sms_id,getDate("ssSSS"),"pending"));
                        }
                    }

                if(cast.length() > 0  )
                {
                    Log.i("JhoroSendOut", "test data found more then 1 ");
                    ddb.updateCheckOut("empty");
                    new MsgSender(context, imei, sent_sms_count).execute();
                }
                else
                {
                    ddb.updateCheckOut("full");
                    ddb.deleteAdmin("receiver", "jhorotek");
                    ddb.addAdminNumber(new Contact("receiver","on", "jhorotek"));
                }
                Log.i("JhoroSendOut", "test");
                    super.onPostExecute(result);
            } catch (Exception ex) {
                Log.i("error", ex.getMessage());
            }
        }
        else{
            Log.i("error","null response");
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.i("JhoroSendOut", "sendOut_doinbak"+outbox_notify);
            Database db=new Database(context);
        Log.i("JhoroSendOut", "sendOut_doinbackground");
        String username = uname;
        String password = upass;
        Log.i("JhoroSendOut", "doinback");
        try {
            if(db.getOutboxCounttow() <70) {
                Log.i("JhoroSendOut", "PullSmsFromServer");
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

                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("command", command);
                jsonParam.accumulate("msg", set_msg);
                jsonParam.accumulate("id", set_id);
                jsonParam.accumulate("number", set_number);
                jsonParam.accumulate("pullcount",info.getPullCount() );
                jsonParam.accumulate("device_id", imei);
                //{"command":"give sms","msg":"hello testing","id":"not set","number":"01718355460","imei":"6418758"}

                String entity = jsonParam.toString();
                Log.i("JhoroSendOut", "Data" + entity);
                httppost.setEntity(new StringEntity(entity, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);

                String response_data = EntityUtils.toString(response.getEntity());
                Log.i("JhoroSendOut", "Response:" + response_data);
                return response_data;
            }
            else
            {
                return null;
            }

        } catch (Exception ex)
        {
            Log.i("JhoroSendOut", "exeption:"+ex);
            return null;
        }
    }

    public void check_notify()
    {
        Database db = new Database(context);
        Log.i("JhoroSendOut", "Sendout_checknotify");
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            //Toast.makeText(this,cn.getName(),Toast.LENGTH_SHORT).show();
            if (cn.getName().equals("setUP"))
            {
                Log.i("JhoroSendOut", "response found="+cn.getPhone_number());
                sent_sms_count=Integer.parseInt(cn.getPhone_number());
            }
            else if (cn.getName().equals("email")  )
            {
                uname=cn.getPhone_number();
            }
            else if (cn.getName().equals("password"))
            {
                upass=cn.getPhone_number();
            }
        }
    }

    public void set_app_url()
    {
        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if(cn.getName().equals("Custom_url"))
            {
                if(!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number()+"/pullOutSms";
                //http://api1.onuserver.com:8085/v3/incomingSms
            }
        }
    }

}