
//used in 6v3
package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import org.linphone.onu_legacy.AsyncTasking.FetchImage;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SignUp_Activity extends AppCompatActivity {
    public String user_email;


//    GoogleCloudMessaging gcm;


    private final String TAG = "SignUp_Activity";


    String regid;
    String PROJECT_NUMBER = "907204599757";
    public String Version = null, brand = null, model = null;
    public String objectID = "null";
    public String user_password;
    public String user_mobile;
    public String imei;
    private Context context=this;
    public ProgressDialog progressBar;
    //    public static String url = "http://api1.onuserver.com:8085/5v1/userActivation";
    public static String url = "https://api.onukit.com/6v4/userActivation";
    Animation zoom;
    private RelativeLayout relativeLayout;

    private String generatedToken=null;
    private CheckBox iAgreeCheckbox;
    private View view;
    private TextView policyLink,termsServiceLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);
        view=findViewById(android.R.id.content);
        iAgreeCheckbox = findViewById(R.id.agreeCheckBox);
        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        //user_email="";
        if (extras != null) {
            user_email = extras.getString("mail");
        }

        relativeLayout=findViewById(R.id.signUpActivity);
        policyLink=findViewById(R.id.policyLink);
        termsServiceLink=findViewById(R.id.termsServiceLink);

        termsServiceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://onukit.com/terms_of_service/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        policyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://onukit.com/user-policy/"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        checkstatus();
        Database db = new Database(this);
        zoom = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoomin);


        //"email":"sample@yahoo.com","mobile":"01718355460","password":"88888","device_id":"357080054613915","oid":"478h7fghbg7848"}

        final EditText email = (EditText) findViewById(R.id.email);
        final TextView emailText = (TextView) findViewById(R.id.emailText);

        //data initialized to be programmed.
        final EditText password = (EditText) findViewById(R.id.password);
        final EditText retype_password = (EditText) findViewById(R.id.retype_password);
        final EditText mobile_number = (EditText) findViewById(R.id.mobile_number);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        retype_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (!user_email.equals("5")) {

            password.requestFocus();
        } else {
            email.requestFocus();
        }

        TextView user_imei = (TextView) findViewById(R.id.user_imei);
        checkstatus();
        Button cancle = (Button) findViewById(R.id.cancle);
        final Button activate = (Button) findViewById(R.id.activate);
        activate.setEnabled(false);
        activate.setBackgroundColor(Color.parseColor("#2000AEF0"));

        iAgreeCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activate.isEnabled()){
                    activate.setEnabled(false);
                    activate.setBackgroundColor(Color.parseColor("#2000AEF0"));
                }else{
                    activate.setEnabled(true);
                    activate.setBackgroundColor(Color.parseColor("#1a99d5"));
                }
            }
        });

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        //imei = tm.getDeviceId();
        imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        db.deleteAdmin("did", "jhorotek");
        db.addAdminNumber(new Contact("did", imei, "jhorotek"));
        Log.v("Device ID: ", imei);
        user_imei.setText("IMEI:" + imei);

        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    email.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        email.setBackgroundDrawable(getResources().getDrawable(R.drawable.round));
                    }
                }
            });
            password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    password.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        password.setBackgroundDrawable(getResources().getDrawable(R.drawable.round));
                    }
                }
            });
            retype_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    retype_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        retype_password.setBackgroundDrawable(getResources().getDrawable(R.drawable.round));
                    }
                }
            });
            mobile_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mobile_number.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        mobile_number.setBackgroundDrawable(getResources().getDrawable(R.drawable.round));
                    }
                }
            });

        } else {
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    email.setBackground(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        email.setBackground(getResources().getDrawable(R.drawable.round));
                    }
                }

            });
            password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    password.setBackground(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        password.setBackground(getResources().getDrawable(R.drawable.round));
                    }
                }
            });
            retype_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    retype_password.setBackground(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        retype_password.setBackground(getResources().getDrawable(R.drawable.round));
                    }
                }
            });
            mobile_number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mobile_number.setBackground(getResources().getDrawable(R.drawable.rounds));
                    if (!hasFocus) {
                        mobile_number.setBackground(getResources().getDrawable(R.drawable.round));
                    }
                }
            });
        }


        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("Jhoro", "ClickActive :1");
                if (email.getText().toString().trim().equals("")) {
                    Log.i("Jhoro", "ClickActive :3");
                    Toast.makeText(getApplicationContext(), "email is empty ! ", Toast.LENGTH_SHORT).show();
                }
                //info inside the worner prototype
                else if (password.getText().toString().trim().equals("")) {
                    Log.i("Jhoro", "ClickActive :4");
                    Toast.makeText(getApplicationContext(), "password is empty ! ", Toast.LENGTH_SHORT).show();

                } else if (retype_password.getText().toString().trim().equals("")) {

                } else if (mobile_number.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "mobile number is empty ! ", Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.getText().toString().trim().equals(retype_password.getText().toString().trim())) {

                        Toast.makeText(getApplicationContext(), "Passwords don't match ", Toast.LENGTH_SHORT).show();
                        onStart();
                    } else {
                        if (isNetworkAvailable()) {
                            //need to work for validation
                            //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                            //if (email.getText().toString().trim().toLowerCase().matches(emailPattern)) {
                                //checking the validity
                                try {
                                    progressBar = ProgressDialog.show(SignUp_Activity.this, "Registration", "Loading...");
                                    progressBar.setCancelable(true);
                                } catch (Exception e) {
                                    Log.i("Jhoro", "" + e);
                                }
                            user_email = email.getText().toString();
                                if (user_email.isEmpty()){
                                    Toast.makeText(context,"Input your email!",Toast.LENGTH_SHORT).show();
                                    return;
                                }
//                                if (!user_email.equals("5")) {
//                                    user_email = emailText.getText().toString();
//                                } else {
//                                    user_email = email.getText().toString();
//                                }
                                //user email chek for the on force methodology inside.
                                user_password = password.getText().toString();
                                if (user_password.isEmpty()){
                                    Toast.makeText(context,"Input your password!",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //user_password=md5(user_password);
                                user_mobile = mobile_number.getText().toString();
                               // db.addUser(user_email,user_password);
                                checkstatus();
                                if (!objectID.equals("null")) {
                                    checkstatus();
                                    new ActivationInfo(getApplicationContext()).execute();
                                    // Toast.makeText(getApplication(), objectID, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplication(), "oid:" + objectID, Toast.LENGTH_LONG).show();
                                }
//                            }
//                            else
//                            {
//                                Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
//                            }
                            //check if end
                        } else {

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

                         //   Toast.makeText(getApplication(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUp_Activity.this, LoginActivity.class);
                i.putExtra("data", "70");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SignUp_Activity.this, LoginActivity.class);
        i.putExtra("data", "70");
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class ActivationInfo extends AsyncTask<Void, Void, String> {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        public ActivationInfo(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String result) {

       //     Toast.makeText(getApplication(), "Result is "+result, Toast.LENGTH_LONG).show();

            if (result != null) {

        //        Toast.makeText(getApplication(), "Result is "+result, Toast.LENGTH_LONG).show();

                Database db = new Database(context);


                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                    String activation = json.getString("isActive");
                    String apptype = json.getString("apptype");
                    String outbox = json.getString("outbox");
                    String number_validation = json.getString("isValid_number");
                    Log.i(TAG, "Response: "+result);
                    if(json.getString("status").equals("4200"))
                    {
                        //reason
                        Toast.makeText(context, json.getString("reason"), Toast.LENGTH_LONG).show();
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                    }

                    else if (number_validation.equals("true")) {
                        db.deleteAdmin("isActive", "jhorotek");
                        db.deleteAdmin("did", "jhorotek");
                        db.deleteAdmin("apptype", "jhorotek");
                        db.deleteAdmin("outbox", "jhorotek");
                        db.deleteAdmin("setUP", "jhorotek");
                        db.deleteAdmin("PostedUP", "jhorotek");
                        db.deleteAdmin("email", "jhorotek");
                        db.deleteAdmin("password", "jhorotek");
                        db.deleteAdmin("inbox", "jhorotek");
                        db.deleteAdmin("report", "jhorotek");
                        db.deleteAdmin("nout", "jhorotek");
                        db.deleteAdmin("ncall", "jhorotek");
                        db.deleteAdmin("oid", "jhorotek");
                        db.deleteAdmin("AsmsId", "jhorotek");
                        db.deleteAdmin("DsmsId", "jhorotek");
                        db.deleteAdmin("aboutText", "jhorotek");
                        db.deleteAdmin("pullcount", "jhorotek");
                        db.addAdminNumber(new Contact("pullcount", "20", "jhorotek"));
                        db.addAdminNumber(new Contact("aboutText", json.getString("about"), "jhorotek"));
                        db.addAdminNumber(new Contact("AsmsId", "00000", "jhorotek"));
                        db.addAdminNumber(new Contact("couuntIncall","0", "jhorotek"));
                        db.addAdminNumber(new Contact("couuntOutcall","0", "jhorotek"));
                        db.addAdminNumber(new Contact("smsIn","on", "jhorotek"));
                        db.addAdminNumber(new Contact("smsOut","on", "jhorotek"));
                        db.addAdminNumber(new Contact("callIn","on", "jhorotek"));
                        db.addAdminNumber(new Contact("callOut","on", "jhorotek"));
                        db.addAdminNumber(new Contact("recorder","on", "jhorotek"));
                        db.addAdminNumber(new Contact("DsmsId", "00000", "jhorotek"));
                        db.addAdminNumber(new Contact("email", user_email, "jhorotek"));
                        db.addAdminNumber(new Contact("password", user_password, "jhorotek"));
                        db.addAdminNumber(new Contact("PostedUP", "0", "jhorotek"));
                        db.addAdminNumber(new Contact("setUP", "0", "jhorotek"));
                        db.addAdminNumber(new Contact("isActive", activation, "jhorotek"));
                        db.addAdminNumber(new Contact("did", imei, "jhorotek"));
                        db.addAdminNumber(new Contact("apptype", apptype, "jhorotek"));
                        db.addAdminNumber(new Contact("outbox", outbox, "jhorotek"));
                        db.addAdminNumber(new Contact("inbox", "off", "jhorotek"));
                        db.addAdminNumber(new Contact("callblock", "off", "jhorotek"));
                        db.addAdminNumber(new Contact("report", "off", "jhorotek"));
                        db.addAdminNumber(new Contact("checkOut", "empty", "jhorotek"));
                        db.addAdminNumber(new Contact("oid", objectID, "jhorotek"));
                        db.addAdminNumber(new Contact("nout", "00-00-0000 00:00:00", "jhorotek"));
                        db.addAdminNumber(new Contact("lastin", "00-00-0000 00:00:00", "jhorotek"));
                        db.addAdminNumber(new Contact("ncall", "00-00-0000 00:00:00", "jhorotek"));
                        db.addAdminNumber(new Contact("user_detail", user_mobile, user_email));
                        db.addAdminNumber(new Contact("user_id", json.getString("user_id"), "jhorotek"));
                        db.addAdminNumber(new Contact("id", json.getString("id"), "jhorotek"));
                        db.addAdminNumber(new Contact("parent_id", json.getString("parentId"), "jhorotek"));
                        db.addAdminNumber(new Contact("Custom_url", "https://api.onukit.com:/6v4", "Onuserver"));
                        db.addAdminNumber(new Contact("contact_url", "https://api.onukit.com/contact/0v1/", "Onuserver"));
                        //db.addAdminNumber(new Contact("Custom_url", "http://172.16.136.80/api4", "Onuserver"));
                        // setAlarm();
                        new FetchImage(context).execute();

                        checkstatus();
                    } else {
                        Toast.makeText(context, "Invalid Mobile Number", Toast.LENGTH_LONG).show();
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("Jhoro", " Exception :"+e);
                    //Toast.makeText(context,""+e, Toast.LENGTH_LONG).show();
                    Log.i(TAG,"Exception: "+e.toString());
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

            } else {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                Toast.makeText(getApplication(), "Could not connect.", Toast.LENGTH_LONG).show();
            }
        }



        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            try {
                String status = null;
                String success = "4000";
                int statusCode = 0;
                String username = "Onu$erVe9";
                String password = "p#@$aS$";
                Log.i("Jhoro", "background");

                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpParams p = new BasicHttpParams();
                HttpClient httpclient = new DefaultHttpClient(p);


                //String url = "https://www.mydomainic.com/api/bkash-central/demo/add/via-sms/";
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("demo", "demo"));
                HttpClient httpClient = new DefaultHttpClient();
                String paramsString = URLEncodedUtils.format(nameValuePairs,
                        "UTF-8");
                HttpPost httppost = new HttpPost(url);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                httppost.setHeader("Content-type", "application/json");

                //---------------------Code for Basic Authentication-----------------------
                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
                httppost.setHeader("Authorization", "Basic " + credBase64);
                //----------------------------------------------------------------------
                //old jSonCode--------------------
//                String entity = "{\"mobile\":\""+rcvdnum+"\",\"sms\":\""+rcvdsms+"\",\"transaction_id\" :\""+uniq+"\",\"receive_time\":\""+rcvtime+"\"}";
//                httppost.setEntity(new StringEntity(entity ,"UTF-8"));
                //----------------------------------
                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("email", user_email);
                jsonParam.accumulate("password", user_password);
                jsonParam.accumulate("mobile", user_mobile);
                jsonParam.accumulate("device_id", imei);
                jsonParam.accumulate("oid", objectID);
                jsonParam.accumulate("accountCreateFlag", "0");
                jsonParam.accumulate("thirdPartyUserData", "null");
                jsonParam.accumulate("version", Version);
                jsonParam.accumulate("brand", brand);
                jsonParam.accumulate("model", model);

                // 5. set json to StringEntity
                //URLEncoder.encode(jsonParam.toString(),"UTF-8")
                StringEntity myStringEntity = new StringEntity(jsonParam.toString(), "UTF-8");
                Log.i(TAG, "Activation JSON Request:" + jsonParam.toString());
                httppost.setEntity(myStringEntity);

                //--------------execution of httppost
                HttpResponse response = httpclient.execute(httppost);
                Log.i("Jhoro", "Activation: 2");
                String res = EntityUtils.toString(response.getEntity());
                Log.i("Jhoro", "Activation: 3" + res);
                JSONObject json = new JSONObject(res);
                Log.i("Jhoro", "Activation: 4");
                Log.i("Jhoro", json.toString());
                status = json.getString("status");
                String activation = json.getString("isActive");
                statusCode = response.getStatusLine().getStatusCode();
                Log.i("Jhoro", "Activation: " + statusCode);
                Log.i("Jhoro", "Satus Code: " + status);

                // if(status.equals(success))
                if (statusCode >= 200 && statusCode <= 299) {


                    return res;
                } else
                    return null;


            } catch (Exception ex) {

                Log.i("Jhoro", " Exception :"+ex);

                return null;
            }
        }
    }

    public void checkstatus() {

        brand = Build.MANUFACTURER;
        model = Build.PRODUCT;

        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Version = packageinfo.versionName.toString();
        Log.i("Jhoro", "checkStatus 1");
        Database db = new Database(getApplicationContext());
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true")) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                Intent i = new Intent(SignUp_Activity.this, DashBoard_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }

        }
        Log.i("Jhoro", "checkStatus 2");
        if (isNetworkAvailable()) {
            Log.i("Jhoro", "checkStatus 3");
            // getRegId();
        }

    }

//    public void getRegId() {
//
//        getFCMToken();
//
//        regid=generatedToken;
//
//        objectID = regid;
//    }

//    private void getFCMToken()
//    {
//        // Get token
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
//                            return;
//                        }
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//
//                        generatedToken=token;
//
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
////                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
//
////                        setToken(msg);
//                    }
//                });
//
//    }

    public void setToken(String token)
    {
        generatedToken=token;
    }

}
