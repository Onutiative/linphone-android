package org.linphone.onu_legacy.AsyncTasking;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Utility.Info;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 3/29/2016.
 */
public class FetchImage extends AsyncTask<Void, Void, String> {
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
    private String TAG="FetchImage";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    public FetchImage(Context context) {
        this.context = context;
        info=new Info(context);
        url=info.getUrl()+"/fetchSliderImage";
        Log.i(TAG,"Image URL: "+url);
    }


    @Override
    protected void onPostExecute(String result) {

    }



    @Override
    protected String doInBackground(Void... params) //HTTP
    {
        try {
            Log.i("Jhoro", "fetchImageo-3");
            String status=null;
            String success="4000";
            int statusCode =0;
            String username =info.getUsername();
            String password =info.getPassword();

            Log.i("Jhoro", "fetchImageo-3.1:"+username);
            Log.i("Jhoro", "fetchImageo-4:"+password);

            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpParams p = new BasicHttpParams();
            HttpClient httpclient = new DefaultHttpClient(p);

            //String url = "https://www.mydomainic.com/api/bkash-central/demo/add/via-sms/";
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("demo","demo"));
            HttpClient httpClient = new DefaultHttpClient();
            String paramsString = URLEncodedUtils.format(nameValuePairs, "UTF-8");
            HttpPost httppost = new HttpPost(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            httppost.setHeader("Content-type", "application/json");

            Log.i("Jhoro", "fetchImageo-5");
            //---------------------Code for Basic Authentication-----------------------
            String credentials = username + ":" + password;
            String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
            httppost.setHeader("Authorization", "Basic " + credBase64);
            //----------------------------------------------------------------------
            //old jSonCode--------------------
            // String entity = "{\"mobile\":\""+rcvdnum+"\",\"sms\":\""+rcvdsms+"\",\"transaction_id\" :\""+uniq+"\",\"receive_time\":\""+rcvtime+"\"}";
            // httppost.setEntity(new StringEntity(entity ,"UTF-8"));
            //----------------------------------
            JSONObject jsonParam = new JSONObject();

            jsonParam.accumulate("trnxID", info.getDate("ddMMyyyyhhmmss"));
            jsonParam.accumulate("trnxTime", info.getDate("dd-MM-yyyy  hh:mm:ss") );

            //URLEncoder.encode(jsonParam.toString(),"UTF-8")
            StringEntity myStringEntity = new StringEntity( jsonParam.toString(),"UTF-8");

            Log.i("Jhoro", "my jSon: " + jsonParam.toString());

            httppost.setEntity(myStringEntity);
            Log.i("Jhoro", "fetchImageo-7");
            //--------------execution of httppost
            HttpResponse response = httpclient.execute(httppost);

            String res= EntityUtils.toString(response.getEntity());
            Log.i("Jhoro", "response:"+res);
            //JSONObject json =new JSONObject(res);
            JSONArray cast = new JSONArray(res);
            Database db=new Database(context);
            Log.i("Jhoro", "fetchImageo-8:");
            db.deleteAdmin("img", "jhorotek");
            for (int i = 0; i < cast.length(); i++)
            {

                JSONObject actor = cast.getJSONObject(i);
                    db.addAdminNumber(new Contact("img",actor.getString("imageUrl"), "jhorotek"));
                Log.i("Jhoro", "fetchImageCount-"+i);

            }
            db.deleteAdmin("imgcount", "jhorotek");
            db.addAdminNumber(new Contact("imgcount",Integer.toString(cast.length()), "jhorotek"));

          
                return null;
        } catch (Exception ex) {
            Log.i("Jhoro", "fetchImageo-10 (Exception)"+ex);
            //tosting(ex.toString());
            //Toast.makeText(context,"(Exception)"+ex, Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}