package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.R;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

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

public class HomeActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 991;

    ImageView selectContacts;
    EditText selectedContacts;
    EditText smsEditText;
    Button sendSMSButton;

    List<ContactResult> contactList;
    private JSONArray sentSMSArray;

    private String TAG = HomeActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 11;

    public ProgressDialog progressBar;

    private String url=null,uname=null,upass=null;
    private String smsText=null;

    private RelativeLayout relativeLayout;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        view=(View) findViewById(R.id.main_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectContacts = (ImageView) findViewById(R.id.select_contacts);
        selectedContacts = (EditText) findViewById(R.id.selected_contacts);
        smsEditText = (EditText) findViewById(R.id.smsEditText);
        sendSMSButton = (Button) findViewById(R.id.sendSMSButton);

        relativeLayout=(RelativeLayout)findViewById(R.id.main_layout);

        sentSMSArray = new JSONArray();

        set_app_url();

        Log.d(TAG, "Welcome to debug");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashBoard_Activity.class));
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            Log.v("Jhoro", "permission:False");
            if(checkPermission())
            {
                Log.i("CList", "1");
//                progressBar = ProgressDialog.show(SmsToAll_Activity.this, "Sms to All", "Loading...");
//                progressBar.setCancelable(true);
//                new SmsToAll_Activity.HttpEditInfo(SmsToAll_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
            else
            {
                requestPermission();
            }

        }
//        else
//        {
//            progressBar = ProgressDialog.show(SmsToAll_Activity.this, "Contacts", "Loading...");
//            progressBar.setCancelable(true);
//            new SmsToAll_Activity.HttpEditInfo(SmsToAll_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }

        selectContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    new MultiContactPicker.Builder(HomeActivity.this) //Activity/fragment context
                            .theme(R.style.MultiContactPicker_Azure) //Optional - default: MultiContactPicker.Azure
                            .hideScrollbar(false) //Optional - default: false
                            .showTrack(true) //Optional - default: true
                            .searchIconColor(Color.WHITE) //Optional - default: White
                            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                            .handleColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                            .bubbleTextColor(Color.WHITE) //Optional - default: White
                            .showPickerForResult(CONTACT_PICKER_REQUEST);


                } else {
                    Toast.makeText(HomeActivity.this, "Remember to go into settings and enable the contacts permission.", Toast.LENGTH_LONG).show();
                }

            }
        });

        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, smsEditText.getText().toString() + "\n" + selectContacts.toString());

                smsText=smsEditText.getText().toString();

                try {

                   if( !smsText.isEmpty() && isNetworkAvailable() && (contactList.size()>0))
                   {
                       for (int i = 0; i < contactList.size(); ++i)

                       {
                           Log.d(TAG, contactList.get(i).getDisplayName() + "  " + contactList.get(i).getPhoneNumbers());

                           JSONObject singleSMS = new JSONObject();


                           singleSMS.accumulate("sent_time", getDate("yyyy-MM-dd hh:mm:ss"));
                           singleSMS.accumulate("sms_text", smsEditText.getText().toString());
                           singleSMS.accumulate("mobile", contactList.get(i).getPhoneNumbers().get(0));
                           singleSMS.accumulate("smsId", getDate("yyyyMMddhhmmss"));

                           sentSMSArray.put(singleSMS);



                       }

                       Log.d(TAG,sentSMSArray.toString());

                       progressBar = ProgressDialog.show(HomeActivity.this,"Sms To All", "Sending...");
                       progressBar.setCancelable(true);

                       new SendSmsNow(HomeActivity.this).execute();
                   }

                   else if(!isNetworkAvailable())
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
                   }
                   else if(!smsText.isEmpty())
                   {
                       Toast.makeText(HomeActivity.this,"Empty SMS Body ",Toast.LENGTH_LONG).show();
                   }

                   else if(contactList.size()==0)
                   {
                       Toast.makeText(HomeActivity.this,"No contact selected! ",Toast.LENGTH_LONG).show();
                   }




//                    for (int i = 0; i < contactList.size(); ++i)
//
//                    {
//                        Log.d(TAG, contactList.get(i).getDisplayName() + "  " + contactList.get(i).getPhone_numbers());
//
//                        JSONObject singleSMS = new JSONObject();
//
//
//                        singleSMS.accumulate("sent_time", getDate("yyyy-MM-dd hh:mm:ss"));
//                        singleSMS.accumulate("sms_text", smsEditText.getText().toString());
//                        singleSMS.accumulate("mobile", contactList.get(i).getPhone_numbers().get(0));
//                        singleSMS.accumulate("smsId", getDate("yyyyMMddhhmmss"));
//
//                        sentSMSArray.put(singleSMS);
//
//
//
//                    }
//
//                    Log.d(TAG,sentSMSArray.toString());
//
//                    progressBar = ProgressDialog.show(HomeActivity.this,"Sms To All", "Sending...");
//                    progressBar.setCancelable(true);
//
//                    new HomeActivity.SendSmsNow(HomeActivity.this).execute();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


//        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            new MultiContactPicker.Builder(HomeActivity.this) //Activity/fragment context
//                    .theme(R.style.MultiContactPicker_Azure) //Optional - default: MultiContactPicker.Azure
//                    .hideScrollbar(false) //Optional - default: false
//                    .showTrack(true) //Optional - default: true
//                    .searchIconColor(Color.WHITE) //Optional - default: White
//                    .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
//                    .handleColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
//                    .bubbleColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
//                    .bubbleTextColor(Color.WHITE) //Optional - default: White
//                    .showPickerForResult(CONTACT_PICKER_REQUEST);
//        }else{
//            Toast.makeText(HomeActivity.this, "Remember to go into settings and enable the contacts permission.", Toast.LENGTH_LONG).show();
//        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                contactList = MultiContactPicker.obtainResult(data);

//                for (int i = 0; i < results.size(); ++i)
//                    Toast.makeText(HomeActivity.this, results.get(i).getDisplayName(), Toast.LENGTH_SHORT).show();

//                Log.d(TAG, results.toString());

//                for(int i=0;i<results.size();++i)
//                    Log.d(TAG, results.get(i).getDisplayName()+"  "+results.get(i).getPhone_numbers());

                selectedContacts.setText(contactList.size() + " Contacts Selected");


            } else if (resultCode == RESULT_CANCELED) {
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static String getDate(String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }


    private void requestPermission(){
        Log.v("Jhoro", "request permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.READ_CONTACTS)){

//            Toast.makeText(HomeActivity.this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

            Toast.makeText(HomeActivity.this,"Contact permission is needed to access and save your contacts. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(HomeActivity.this,new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_CODE);
            Log.v("Jhoro", "request permission 1");
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



//                JSONArray smsess = new JSONArray();
//                ContentResolver cr = getContentResolver();
//                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
//                        null, null, null, null);
//                if (cur.getCount() > 0) {
//                    while (cur.moveToNext())
//                    {
//                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
//                        {
//                            Cursor pCur = cr.query(
//                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                    null,
//                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
//                                    new String[]{id}, null);
//                            while (pCur.moveToNext())
//                            {
//
//                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                if(phoneNo.length() > 10)
//                                {
//                                    JSONObject jsonParam = new JSONObject();
//                                    jsonParam.accumulate("sent_time", getDate("yyyy-MM-dd hh:mm:ss"));
//                                    jsonParam.accumulate("sms_text", smstobesent);
//                                    jsonParam.accumulate("mobile", phoneNo);
//                                    jsonParam.accumulate("smsId", getDate("yyyyMMddhhmmss"));
//                                    Log.i("CList", "Name:" + name + " Phone No: " + phoneNo);
//                                    smsess.put(jsonParam);
//                                }
//                                Log.i("CList","5");
//                            }
//                            pCur.close();
//                        }
//                    }
//                    Log.i("CList","All:"+ smsess.toString());
//                }




                //-----------------------------------------------------------------
                StringEntity myStringEntity = new StringEntity( sentSMSArray.toString(),"UTF-8");
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


    public void set_app_url()
    {
        Database db = new Database(HomeActivity.this);
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

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_CONTACTS);


        if (result == PackageManager.PERMISSION_GRANTED  ){

            return true;

        } else {

            return false;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Snackbar.make(view,"Permission Granted, Now you can access contacts data.",Snackbar.LENGTH_LONG).show();
//                    progressBar = ProgressDialog.show(HomeActivity.this, "Contacts", "Loading...");
//                    progressBar.setCancelable(true);
//                    new SmsToAll_Activity.HttpEditInfo(HomeActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else
                {
                    Snackbar.make(view,"Permission Denied, You cannot access contacts data.",Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }

}

