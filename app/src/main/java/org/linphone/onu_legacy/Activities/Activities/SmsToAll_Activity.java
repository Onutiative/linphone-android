package org.linphone.onu_legacy.Activities.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.R;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SmsToAll_Activity extends AppCompatActivity {

    // This static code block is for not crashing in some mobile phone like mine HTC Desire 626


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private TextView totalsms;
    private String url=null,uname=null,upass=null,smstobesent=null;
    private static final int PERMISSION_REQUEST_CODE = 11;
    private Activity activity;
    public ProgressDialog progressBar;
    private View view;
    private Context context;

    private Button buttonSelectContact;

    private RelativeLayout relativeLayout;

    private static final int CONTACT_PICKER_REQUEST = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_to_all);
//        getSupportActionBar().hide();
        view=(View) findViewById(R.id.main_layout);
        activity=this;
        context=SmsToAll_Activity.this;
        set_app_url();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DashBoard_Activity.class));
            }
        });

         totalsms= (TextView) findViewById(R.id.totalnumbers);
        final EditText smstext= (EditText) findViewById(R.id.smsText);
        Button send= (Button) findViewById(R.id.sendbutton);

        relativeLayout=(RelativeLayout)findViewById(R.id.main_layout);


        if (Build.VERSION.SDK_INT >= 23) {
            Log.v("Jhoro", "permission:False");
            if(checkPermission())
            {
                Log.i("CList", "1");
                progressBar = ProgressDialog.show(SmsToAll_Activity.this, "Sms to All", "Loading...");
                progressBar.setCancelable(true);
                new HttpEditInfo(SmsToAll_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
            else
            {
                requestPermission();
            }

        }
        else
        {
            progressBar = ProgressDialog.show(SmsToAll_Activity.this, "Contacts", "Loading...");
            progressBar.setCancelable(true);
            new HttpEditInfo(SmsToAll_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!smstext.getText().toString().isEmpty())
                {
                    if(isNetworkAvailable()) {
                        progressBar = ProgressDialog.show(SmsToAll_Activity.this,"Sms To All", "Contact Syncing......");
                        progressBar.setCancelable(true);
                        smstobesent = smstext.getText().toString();
                        new SendSmsNow(SmsToAll_Activity.this).execute();
                    }
                    else
                    {



                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar snackbar1 = Snackbar.make(relativeLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                                        snackbar1.show();
                                    }
                                });

                        snackbar.show();


                      //  Toast.makeText(SmsToAll_Activity.this,getResources().getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(SmsToAll_Activity.this,"empty sms",Toast.LENGTH_LONG).show();
                }
            }
        });

//
//        buttonSelectContact=(Button)findViewById(R.id.buttonSelectContact);
//        buttonSelectContact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//Toast.makeText(SmsToAll_Activity.this,"dsakjfl",Toast.LENGTH_SHORT).show();
//
//
//            }
//        });



    }





    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class HttpEditInfo extends AsyncTask<Void, Void, String>
    {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;


        @Override
        protected void onPreExecute() {
            Log.i("CList", "2");
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        public HttpEditInfo(Context context) {
            this.context = context;
        }
        @Override
        protected void onPostExecute(String result)
        {


        }
        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            try{
                Log.i("CList", "3-1");
                int numbercount=0;
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);
                Log.i("CList", "3");
                if (cur.getCount() > 0) {
                    while (cur.moveToNext())
                    {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                        {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext())
                            {

                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if(phoneNo.length() > 10) {
                                    Log.i("CList", "c:" + name + " Phone No: " + phoneNo);
                                    numbercount++;
                                }
                            }
                            pCur.close();
                        }
                    }

                    UpdateCount(numbercount);
                    set_app_url();

                }
                else
                {
                    Log.i("CList", "4");
                    UpdateCount(0);
                    set_app_url();
                }

            }catch(Exception e)
            {

            }
            return null;
        }
    }



    public class SendSmsNow extends AsyncTask<Void, Void, String>
    {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        public SendSmsNow(Context context) {
            this.context = context;
        }
        @Override
        protected void onPostExecute(String result)
        {

            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        }
        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            try{

                String status=null;
                String success="4000";
                int statusCode =0;
                String username =uname;
                String password =upass;
                Log.e("Username: ", uname);
                Log.e("password: ", upass);
                //username ="Onu$erVe9";
                //password ="p#@$aS$";
                Log.i("CList","1 url:"+url);

                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpParams p = new BasicHttpParams();
                HttpClient httpclient = new DefaultHttpClient(p);
                Log.i("CList","2");
                HttpPost httppost = new HttpPost(url);
                Log.i("CList","3");
                httppost.setHeader("Content-type", "application/json");
                //---------------------Code for Basic Authentication-----------------------
                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
                httppost.setHeader("Authorization", "Basic " + credBase64);
                Log.i("CList","4");
                //------------------------------------------
                JSONArray smsess = new JSONArray();
                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);
                if (cur.getCount() > 0) {
                    while (cur.moveToNext())
                    {
                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                        {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                    new String[]{id}, null);
                            while (pCur.moveToNext())
                            {

                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if(phoneNo.length() > 10)
                                {
                                    JSONObject jsonParam = new JSONObject();
                                    jsonParam.accumulate("sent_time", getDate("yyyy-MM-dd hh:mm:ss"));
                                    jsonParam.accumulate("sms_text", smstobesent);
                                    jsonParam.accumulate("mobile", phoneNo);
                                    jsonParam.accumulate("smsId", getDate("yyyyMMddhhmmss"));
                                    Log.i("CList", "Name:" + name + " Phone No: " + phoneNo);
                                    smsess.put(jsonParam);
                                }
                                Log.i("CList","5");
                            }
                            pCur.close();
                        }
                    }
                    Log.i("CList","All:"+ smsess.toString());
                }
                //-----------------------------------------------------------------
                StringEntity myStringEntity = new StringEntity( smsess.toString(),"UTF-8");
                httppost.setEntity(myStringEntity);
                //--------------execution of httppost
                HttpResponse response = httpclient.execute(httppost);
                String res= EntityUtils.toString(response.getEntity());
                Log.i("CList","response:"+res);
                statusCode = response.getStatusLine().getStatusCode();
                // if(status.equals(success))
                if(statusCode>=200 && statusCode<=299)
                {
                    Toast.makeText(context,"Sending . . .",Toast.LENGTH_SHORT).show();
                    return null;
                }
                else
                    return null;

            }catch(Exception e)
            {
                Log.i("CList","exception:"+e);
            }

            return null;
        }
    }

    private void UpdateCount(int numbercounts)
    {
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
        totalsms.setText("Total "+numbercounts+" Numbers");

    }

    public void set_app_url()
    {
        Database db = new Database(SmsToAll_Activity.this);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if(cn.getName().equals("Custom_url"))
            {
                if(!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number()+"/outgoingApi";
                //http://api1.onuserver.com:8085/v3/incomingSms
            }

            else if (cn.getName().equals("email")  )

            {
                uname=cn.getPhone_number();

            }
            else if (cn.getName().equals("password")  )

            {
                upass=cn.getPhone_number();
            }
        }
    }

    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }
    public void ContactList()
    {

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext())
            {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext())
                    {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Log.i("CList","Name:"+ name +" Phone No: " +phoneNo);
                    }
                    pCur.close();
                }
            }
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(SmsToAll_Activity.this, android.Manifest.permission.READ_CONTACTS);


        if (result == PackageManager.PERMISSION_GRANTED  ){

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){
        Log.v("Jhoro", "request permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.READ_CONTACTS)){

            Toast.makeText(context,"GPS permission allows us to access contacts data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(activity,new String[]{android.Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_CODE);
            Log.v("Jhoro", "request permission 1");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Snackbar.make(view,"Permission Granted, Now you can access contacts data.",Snackbar.LENGTH_LONG).show();
                    progressBar = ProgressDialog.show(SmsToAll_Activity.this, "Contacts", "Loading...");
                    progressBar.setCancelable(true);
                    new HttpEditInfo(SmsToAll_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else
                {
                    Snackbar.make(view,"Permission Denied, You cannot access contacts data.",Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

}
