package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.linphone.LinphoneApplication;
import org.linphone.R;
import org.linphone.activities.main.MainActivity;
import org.linphone.onu_legacy.AsyncTasking.FetchImage;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.onuspecific.OnuFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    // This static code block is for not crashing in some mobile phone like me
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final String TAG = "LoginActivity";
    public static final String OPEN_DASHBOARD = "open_dashboard";
    private static final int RC_SIGN_IN = 9001;
    public String id = "2";
    public String user_email, user_pass, user_number, User_data, imei;
    public String Version = null, brand = null, model = null;

//    GoogleCloudMessaging gcm;

    String regid;
//    String PROJECT_NUMBER = "907204599757";

    String PROJECT_NUMBER = "710819726531";

    public String objectID = "null";
    public ProgressDialog progressBar;
    public String user_password;

    //    public static String url="http://api1.onukit.com:8085/5v1/userActivation";
    public static String url = "https://api.onukit.com/6v4/userActivation";
    public static String urlForLogin = "https://api.onukit.com/6v4/login";
    //public static String urlForLogin = "https://api.onukit.com/6v4/testLogin";
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private TextView passwordToggle;
    private ProgressDialog mProgressDialog;
    public TextView forgotPass;
    public LinearLayout footer;
    public String user_mobile;
    private Context context;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;
    Animation zoom;
    public String user_email_frgt = "null";
    int number;
    int setType;
    public static final String USEREMAIL = "email";
    public static final String USERPASS = "pass";

    private LinearLayout linearLayout;
    private String generatedToken = null;
    private static final int request_code = 11;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> listPermissionsNeeded;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private String prefName = "onuPref";
    private SharedPreferences.Editor prefEditor;
    SharedPreferences sharedPref;
    SharedPreferences sharedPref_onu_creds;
    SharedPreferences.Editor prefEditor_onu_creds;
    private SharedPrefManager sharedPrefManager;
    private static final int GET_ACCOUNTS_PERMISSION = 100;
    private static final int READ_EXTERNAL_STORAGE_PERMISSION = 200;
    private static final int RECEIVE_SMS = 300;
    private static final int READ_PHONE_STATE = 400;
    private boolean permissionFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        } catch (Exception e){
            e.printStackTrace();
        }

        view = (View) findViewById(R.id.main_layout);

        context = LoginActivity.this;

        Log.i(TAG, "SignUpGoogle Activity Created!");

        sharedPrefManager = new SharedPrefManager(context);

        Log.i(TAG,"IME in androidx: "+Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.password);
        passwordToggle = (TextView) findViewById(R.id.passwordToggle);
        TextView newUser = findViewById(R.id.buttons);
        Button login = (Button) findViewById(R.id.login);
        forgotPass = findViewById(R.id.forget);

        linearLayout = (LinearLayout) findViewById(R.id.main_layout);
        //    requestManualRuntimePermission();
        setType = 1;
//        TextView textView = (TextView) signInButton.getChildAt(0);
//        textView.setText("SignIn With Google");

        passwordToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setType == 1) {
                    setType = 0;
                    password.setTransformationMethod(null);
                    if (password.getText().length() > 0)
                        password.setSelection(password.getText().length());
                    passwordToggle.setBackgroundResource(R.drawable.black_eye);
                } else {
                    setType = 1;
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    if (password.getText().length() > 0) ;
                    password.setSelection(password.getText().length());
                    passwordToggle.setBackgroundResource(R.drawable.eye);
                }
            }
        });

        activity = this;
        if (Build.VERSION.SDK_INT >= 23) {
            Log.v("Jhoro", "permission:False");
            if (checkPermission()) {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                //String uniqueID = UUID.randomUUID().toString();
                //imei = tm.getDeviceId();
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } else {
                //          requestPermission();  // No need to hassle again for runtime permission
            }
        } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //imei = tm.getDeviceId();
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        Log.v("Jhoro", "sign in :1");
        // Views
        Intent intent = getIntent();

        if (intent.hasExtra("data")) {
            Bundle extras = getIntent().getExtras();
            id = extras.getString("data");
        }

        final Database db = new Database(this);
        zoom = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.zoomin);
        mStatusTextView = (TextView) findViewById(R.id.status);
        footer = (LinearLayout) findViewById(R.id.foot);

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, AboutActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]
        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

//        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
        // signInButton.setScopes(gso.getScopeArray());

        // setGooglePlusButtonText(signInButton, "SignIn With Google");

        // [END customize_button]

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkstatus();

        Log.d(TAG, "Check point 1");

        sharedPref = getSharedPreferences(prefName, MODE_PRIVATE);
        prefEditor = (SharedPreferences.Editor) getSharedPreferences(prefName, MODE_PRIVATE).edit();

        sharedPref_onu_creds = getSharedPreferences("onukit_creds", MODE_PRIVATE);
        prefEditor_onu_creds = (SharedPreferences.Editor) getSharedPreferences("onukit_creds", MODE_PRIVATE).edit();

        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        Log.d(TAG, "Phone state " + ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE));

        Log.d(TAG, "Check point 2");

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
        }
//new account create
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    openAlarDialog();
                }else {
                    Intent i = new Intent(LoginActivity.this, SignUp_Activity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("mail", "5");
                    startActivity(i);
                }
            }
        });
//login button click
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar = ProgressDialog.show(LoginActivity.this, "Logging In", "Loading...");
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    openAlarDialog();
                }else {
                    //imei = tm.getDeviceId();
                    imei=Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Log.d(TAG,"Login button executed");
                    Log.i("Jhoro", "ClickActive :1");
                    if (email.getText().toString().trim().equals("")) {
                        Log.i("Jhoro", "ClickActive :3");
                        Toast.makeText(getApplicationContext(), "email is empty ! ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //info inside the worner prototype
                    else if (password.getText().toString().trim().equals("")) {
                        Log.i("Jhoro", "ClickActive :4");
                        Toast.makeText(getApplicationContext(), "password is empty ! ", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (isNetworkAvailable()) {
                            //user_email = email.getText().toString();
                            //user_password = password.getText().toString();
                            //need to work for validation
                            //String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                            //if (email.getText().toString().trim().toLowerCase().matches(emailPattern)){
                            try {
                                progressBar = ProgressDialog.show(LoginActivity.this, "Logging In", "Loading...");
                                progressBar.setCancelable(true);
                            } catch (Exception e) {
                                Log.i("Jhoro", "" + e);
                            }
                            user_email = email.getText().toString();
                            user_password = password.getText().toString();
                            Log.i(TAG, "email: "+ user_email);
                            Log.i(TAG,"pass: "+user_password);
                            checkstatus();
                            Log.i(TAG,"oid: "+objectID);
                            try{
                                if (!objectID.equals(null) && !objectID.equals("null")) {
                                    checkstatus();
                                    new ActivationInfoLogin(getApplicationContext()).execute();
                                    // Toast.makeText(getApplication(), objectID, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplication(), "oid:" + objectID, Toast.LENGTH_LONG).show();

                                    Log.i(TAG, "Object ID is " + objectID);
                                }
                            }catch (Exception e){
                                Log.i(TAG, "Exception in Object ID: "+e.toString());
                            }
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(linearLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Snackbar snackbar1 = Snackbar.make(linearLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                                            snackbar1.show();
                                        }
                                    });
                            snackbar.show();
                        }
                    }
                }
            }
        });
    }

    private void openAlarDialog(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Onukit Account Policy");
        alertDialog.setMessage(R.string.account_policy_text);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("I Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestManualRuntimePermission();
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void requestManualRuntimePermission() {
        Log.i(TAG,"Manual Permission");
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE,},
                READ_PHONE_STATE);

        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                GET_ACCOUNTS_PERMISSION);

        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_PERMISSION);

        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.RECEIVE_SMS},
                RECEIVE_SMS);
    }

    private void initializeView() {
        Toast.makeText(context, "Allow over the apps granted!", Toast.LENGTH_LONG).show();
        Intent in=new Intent(context, LoginActivity.class);
        startActivity(in);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    private boolean checkAndRequestPermissions(ArrayList<String> permissions, int request_code) {

        if (permissions.size() > 0) {
            listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissions.size(); i++) {
                int hasPermission = ContextCompat.checkSelfPermission(activity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), request_code);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (id.equals("70")) {
            revokeAccess();
        }
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Jhoro", "sign in :2" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.v("Jhoro", "sign in :3");
            handleSignInResult(result);
        }


        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                    Intent i = new Intent(this, LoginActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    initializeView();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    // [END onActivityResult]

    // [START handleSignInResult]

    private void handleSignInResult(GoogleSignInResult result) {
        Log.v("Jhoro", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            progressBar = ProgressDialog.show(LoginActivity.this, "Registration", "Loading...");
            progressBar.setCancelable(true);
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            JSONObject jsonData = new JSONObject();
            try {
                user_email = acct.getEmail();
                jsonData.accumulate("email", acct.getEmail());
                jsonData.accumulate("name", acct.getDisplayName());
                //jsonData.accumulate("name", acct.getDisplayName());
                jsonData.accumulate("id", acct.getId());
                jsonData.accumulate("token", acct.getIdToken());
                jsonData.accumulate("photoUri", acct.getPhotoUrl());
                jsonData.accumulate("AurhCode", acct.getServerAuthCode());
                User_data = jsonData.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (id.equals("1")) {
                revokeAccess();
                id = "1";

                registerNow();
            }
            Log.v("Jhoro", "sign in :4");
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getEmail()));
            Log.v("Jhoro", "sign in :5");
            //updateUI(true);
        } else {

            Log.v("Jhoro", "Result false in handleSignInResult ");
            if (!isNetworkAvailable()) {
                Toast.makeText(LoginActivity.this, "Google Signin Faled!!!", Toast.LENGTH_LONG).show();
            }
            // Signed out, show unauthenticated UI.
            // updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    Log.v("Jhoro", "sign in click:1");
                    id = "1";
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    Log.v("Jhoro", "sign in click :2");


                    OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                    if (opr.isDone()) {
                        // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                        // and the GoogleSignInResult will be available instantly.
                        Log.v("Jhoro", "Got cached sign-in");
                        GoogleSignInResult result = opr.get();
                        handleSignInResult(result);
                    } else {

                        opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                            @Override
                            public void onResult(GoogleSignInResult googleSignInResult) {
                                //hideProgressDialog();
                                handleSignInResult(googleSignInResult);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Jhoro", e.toString());
                }
                // return null;
            }
        };

        thread.start();

        //startActivity(new Intent(this,LoginSelection_Activity.class));
    }
    // [END signIn]
    // [START signOut]

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    // [END signOut]
    //onuserver is a tiny sms server for all but it is not that social in that way
    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Log.v("Jhoro", "On connection Failed ");
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    public void registerNow() {
        // user_pass=getDate("MSSShhmmsddMMsSSS");
        user_number = "null";

        Random rand = new Random();
        number = rand.nextInt();
        user_pass = Integer.toString(number);
        new ActivationInfo(getApplicationContext()).execute();

        Log.d("Random Number", user_pass);
        //takeTovken();
        //checkstatus();

    }

    public class ActivationInfo extends AsyncTask<Void, Void, String> {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;
        String result = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        public ActivationInfo(Context context) {
            this.context = context;
        }
        public void setResult(String res) {
            this.result = res;
        }

        public String getResult() {
            return this.result;
        }

        @Override
        protected void onPostExecute(String result) {
            result = getResult();
            setResult(null);
            if (result != null && !result.equals("")) {
                Database db = new Database(context);
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                    String activation = json.getString("isActive");
                    String apptype = json.getString("apptype");
                    String outbox = json.getString("outbox");
                    String number_validation = json.getString("isValid_number");
                    Log.v("Jhoro", "post");

                    if (number_validation.equals("true")) {
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
                        db.deleteAdmin("gmail", "jhorotek");
                        db.deleteAdmin("AsmsId", "jhorotek");
                        db.deleteAdmin("DsmsId", "jhorotek");
                        db.deleteAdmin("aboutText", "jhorotek");
                        db.deleteAdmin("user_id","jhorotek");

                        db.addAdminNumber(new Contact("aboutText", json.getString("about"), "jhorotek"));
                        db.addAdminNumber(new Contact("pullcount", "20", "jhorotek"));
                        db.addAdminNumber(new Contact("couuntIncall", "0", "jhorotek"));
                        db.addAdminNumber(new Contact("couuntOutcall", "0", "jhorotek"));
                        db.addAdminNumber(new Contact("AsmsId", "00000", "jhorotek"));
                        db.addAdminNumber(new Contact("smsIn", "on", "jhorotek"));
                        db.addAdminNumber(new Contact("smsOut", "on", "jhorotek"));
                        db.addAdminNumber(new Contact("callIn", "on", "jhorotek"));
                        db.addAdminNumber(new Contact("callOut", "on", "jhorotek"));
                        db.addAdminNumber(new Contact("recorder", "on", "jhorotek"));
                        db.addAdminNumber(new Contact("DsmsId", "00000", "jhorotek"));
                        db.addAdminNumber(new Contact("email", user_email, "jhorotek"));
                        db.addAdminNumber(new Contact("password", user_pass, "jhorotek"));
                        db.addAdminNumber(new Contact("PostedUP", "0", "jhorotek"));
                        db.addAdminNumber(new Contact("setUP", "0", "jhorotek"));
                        db.addAdminNumber(new Contact("isActive", activation, "jhorotek"));
                        db.addAdminNumber(new Contact("did", imei, "jhorotek"));
                        db.addAdminNumber(new Contact("apptype", apptype, "jhorotek"));
                        db.addAdminNumber(new Contact("outbox", outbox, "jhorotek"));
                        db.addAdminNumber(new Contact("inbox", "off", "jhorotek"));
                        db.addAdminNumber(new Contact("report", "off", "jhorotek"));
                        db.addAdminNumber(new Contact("callblock", "off", "jhorotek"));
                        db.addAdminNumber(new Contact("checkOut", "empty", "jhorotek"));
                        db.addAdminNumber(new Contact("oid", objectID, "jhorotek"));
                        db.addAdminNumber(new Contact("gmail", "gmail", "jhorotek"));
                        db.addAdminNumber(new Contact("nout", "00-00-0000 00:00:00", "jhorotek"));
                        db.addAdminNumber(new Contact("lastin", "00-00-0000 00:00:00", "jhorotek"));
                        db.addAdminNumber(new Contact("ncall", "00-00-0000 00:00:00", "jhorotek"));
                        db.addAdminNumber(new Contact("user_detail", user_number, user_email));
                        Log.i(TAG,"User ID: "+json.getString("user_id"));
                        db.addAdminNumber(new Contact("user_id", json.getString("user_id"), "jhorotek"));
                        try {
                            db.addAdminNumber(new Contact("id", json.getString("id"), "jhorotek"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        db.addAdminNumber(new Contact("Custom_url", "https://api.onukit.com/6v4", "Onuserver"));
                        db.addAdminNumber(new Contact("contact_url", "https://api.onukit.com/contact/0v1/", "Onuserver"));

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
                }
            } else {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                Toast.makeText(getApplication(), "Could not connect.", Toast.LENGTH_LONG).show();
            }
        }

        public void networkStuff(){
            Request request = new OnuFunctions.UserActivation(user_email, user_password, user_number, objectID, context=getApplicationContext()).performActivation();
            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                    // Handle success
                    Log.d("OnuFunctions", "onResponse - Status : " + response.code());
                    Log.d("OnuFunctions", "onResponse - Status : " + response.body().string());

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // check when client is done, then set waitForServerAnswer's value to false
                            findViewById(R.id.wait_layout_onuactivation).setVisibility(View.GONE);
                            // return null;
                        }
                    });
                    ResponseBody requestBody = (ResponseBody) response.body();
                    // get string from body
                    setResult(requestBody.string());
                }

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    // Handle failure
                    Log.d("OnuFunctions", "onFailure - Failed to login user: " + e);
                }

            });
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
//                String res = "";
                Log.v("Jhoro", "background");

                networkStuff();

                while (getResult() == null) {
                    // wait for server answer
                    Log.d("OnuFunctions", "Waiting for server answer...");
                    // wait 1 second
                    Thread.sleep(1000);
                }

                return getResult();


            } catch (Exception ex) {

                Log.v("Jhoro", " Exception: " + ex);


                return null;
            }
        }
    }



    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    public class ActivationInfoLogin extends AsyncTask<Void, Void, String> {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;
        String result = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        public ActivationInfoLogin(Context context) {
            this.context = context;
        }

        public void setResult(String res) {
            this.result = res;
        }

        public String getResult() {
            return this.result;
        }

        public void networkStuff(){
            Request request = new OnuFunctions.UserLogin(user_email, user_password, objectID, context=getApplicationContext()).performLogin();
            OkHttpClient client = new OkHttpClient();
            Log.i(TAG,"Login Request... ");

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            // check when client is done, then set waitForServerAnswer's value to false
                            findViewById(R.id.wait_layout_onulogin).setVisibility(View.GONE);
                            // return null;
                        }
                    });
                    Log.d("OnuFunctions", "onFailure - Failed to login user: " + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    prefEditor_onu_creds.putString("username", null);
                    prefEditor_onu_creds.putString("password", null);
                    prefEditor_onu_creds.apply();

//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            // check when client is done, then set waitForServerAnswer's value to false
//                            findViewById(R.id.wait_layout_onulogin).setVisibility(View.GONE);
//                        }
//                    });

                    ResponseBody requestBody = response.body();

                    // log status code
                    Log.d("OnuFunctions", "Response code: " + response.code());

                    // check status code
                    if (response.code() != 200) {
                        // Show a toast message
                        Log.d("OnuFunctions", "response.code != 200 | Failed to login user");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Server error! " + response.code(), Toast.LENGTH_SHORT).show();
                                // return null;
                            }
                        });
                    }

                    // Handle response
                    if (!response.isSuccessful()) {
                        // The request failed
                        Log.d("OnuFunctions", "response.isSuccessful == false | Failed to activate user");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Request Error! Try Again!", Toast.LENGTH_SHORT).show();
                                // return null;
                            }
                        });
                    }

                    setResult(requestBody != null ? requestBody.string() : "");
                }
            });

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG,"Login Response JSON: "+result);
            if (result != null && !result.equals("")) {
                Database db = new Database(context);
                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                    //      Toast.makeText(context, json.toString(), Toast.LENGTH_LONG).show();
                    if(json.getString("status").equals("4000")) {

                        Log.i(TAG, "Login JSON Data " + json.toString());
                        String LogInActivity = json.getString("isActive");
                        String apptype = json.getString("apptype");
                        Log.i(TAG,"User Type: "+ apptype);
                        String outbox = json.getString("outbox");
                        String number_validation = json.getString("isValid_number");
                        Log.i("Jhoro", "post");
                        if (json.getString("status").equals("4200")) {
                            //reason
                            Toast.makeText(context, json.getString("reason"), Toast.LENGTH_LONG).show();
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                            }
                        } else if (number_validation.equals("true")) {
                            prefEditor_onu_creds.putString("username", Base64.encodeToString(user_email.getBytes(), Base64.DEFAULT));
                            prefEditor_onu_creds.putString("password", Base64.encodeToString(user_password.getBytes(), Base64.DEFAULT));
                            prefEditor_onu_creds.apply();

                            db.deleteAllAdmin();
                            db.addAdminNumber(new Contact("did", imei, "jhorotek"));
                            db.addAdminNumber(new Contact("pullcount", "20", "jhorotek"));
                            db.addAdminNumber(new Contact("aboutText", json.getString("about"), "jhorotek"));
                            db.addAdminNumber(new Contact("AsmsId", "00000", "jhorotek"));
                            db.addAdminNumber(new Contact("couuntIncall", "0", "jhorotek"));
                            db.addAdminNumber(new Contact("couuntOutcall", "0", "jhorotek"));
                            db.addAdminNumber(new Contact("smsIn", "on", "jhorotek"));
                            db.addAdminNumber(new Contact("smsOut", "on", "jhorotek"));
                            db.addAdminNumber(new Contact("callIn", "on", "jhorotek"));
                            db.addAdminNumber(new Contact("callOut", "on", "jhorotek"));
                            db.addAdminNumber(new Contact("recorder", "on", "jhorotek"));
                            db.addAdminNumber(new Contact("DsmsId", "00000", "jhorotek"));
                            db.addAdminNumber(new Contact("email", user_email, "jhorotek"));
                            db.addAdminNumber(new Contact("password", user_password, "jhorotek"));
                            db.addAdminNumber(new Contact("PostedUP", "0", "jhorotek"));
                            db.addAdminNumber(new Contact("setUP", "0", "jhorotek"));
                            db.addAdminNumber(new Contact("isActive", "true", "jhorotek"));
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
                            Log.i(TAG,"User ID: "+json.getString("user_id"));
                            db.addAdminNumber(new Contact("user_id", json.getString("user_id"), "jhorotek"));
                            db.addAdminNumber(new Contact("parent_id", json.getString("parentId"), "jhorotek"));
                            try {
                                db.addAdminNumber(new Contact("id", json.getString("id"), "jhorotek"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            db.addAdminNumber(new Contact("Custom_url", "https://api.onukit.com/6v4", "Onuserver"));
                            db.addAdminNumber(new Contact("contact_url", "https://api.onukit.com/contact/0v1/", "Onuserver"));
                            //db.addAdminNumber(new Contact("Custom_url", "http://172.16.136.80/api4", "Onuserver"));
                            // setAlarm();
                            new FetchImage(context).execute();
                            checkstatus();
                        } else {
                            Toast.makeText(context, "Could not Log in .", Toast.LENGTH_LONG).show();
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                            }
                        }
                    }
                    else if(json.getString("status").equals("4200"))
                    {
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                        }
                        Toast.makeText(context,json.getString("reason"),Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("Jhoro", " Exception :" + e);
                    //        Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }
            } else {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
//                Toast.makeText(getApplication(), "Could not connect.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            try {
                String status = null;
                String success = "4000";
                int statusCode = 0;
                String username = user_email;
                String password = user_password;
                Log.i(TAG, "User Name: "+username+"; PAssword: "+password);

                try {
                    networkStuff();
                } catch (Exception e) {
                    Log.i(TAG, "ActivationInfoLogin networkStuff Exception :" + e);
                    e.printStackTrace();
                }

                while (getResult() == null) {
                    Log.i(TAG, "Waiting for result");
                    Thread.sleep(1000);
                }

                return getResult();
            } catch (Exception ex) {
                Log.i(TAG, "ActivationInfoLogin Exception :" + ex);
                return null;
            }
        }
    }

    public void forgotPass(View view) {
        //Toast.makeText(context, "Forgot Password Button Clicked", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Forgot Password executed!");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Enter Email address :");
        alertDialog.setMessage("");
        final EditText input = new EditText(LoginActivity.this);
        input.setText(user_email);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.onukit_logo2);

        alertDialog.setPositiveButton("CONFIRM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().isEmpty()) {
                            user_email_frgt = input.getText().toString();
                            dialog.cancel();
                            //checkstatus();
                            new ForgetPassAPi(getApplicationContext(), user_email_frgt).execute();

                            Toast.makeText(context, "Action Taken Successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Empty Email", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent i = new Intent(LoginActivity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
        alertDialog.show();
    }

    public class ForgetPassAPi extends AsyncTask<Void, Void, String> {
        private static final int TIMEOUT_MILLISEC = 5000;
        private Context context;
        private String user_email_frgt;
        private String TAG = "ForgetPassAPi";

        public ForgetPassAPi(Context context, String user_email_frgt) {
            this.context = context;
            this.user_email_frgt = user_email_frgt;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = "Onu$erVe9";
                String password = "p#@$aS$";
                Log.i(TAG, "background");

                URL urlObj = new URL("http://api.onukit.com/6v4/passForget");
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

                connection.setConnectTimeout(TIMEOUT_MILLISEC);
                connection.setReadTimeout(TIMEOUT_MILLISEC);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Basic " + getBase64Credentials(username, password));
                connection.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.accumulate("email", user_email_frgt);
                jsonParam.accumulate("trnxID", getDate("yysssyysMMD"));
                jsonParam.accumulate("time", getDate("yyyy-MM-dd hh:mm:ss"));

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode <= 299) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        JSONObject json = new JSONObject(response.toString());
                        String status = json.getString("status");
                        return status;
                    }
                } else {
                    Log.e(TAG, "HTTP Response Code: " + responseCode);
                    return null;
                }
            } catch (Exception ex) {
                Log.i(TAG, "ForgetPassAPi Exception :" + ex);
                return null;
            }
        }

        private String getDate(String format) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
            return dateFormat.format(new Date());
        }

        private String getBase64Credentials(String username, String password) {
            String credentials = username + ":" + password;
            return Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.equals("4000")) {
                    Toast.makeText(context, "Check your email pls .", Toast.LENGTH_LONG).show();
                }
            }
            // else {
            // Toast.makeText(getApplication(), "Could not connect.", Toast.LENGTH_LONG).show();
            // }
        }
    }



    private void IshowProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void IhideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void getRegId() {

        getFCMToken();

        regid = generatedToken;
        objectID = regid;

        // load oid from shared preference
        SharedPreferences pref = getApplicationContext().getSharedPreferences("onukit_creds", 0);
        String oid = pref.getString("oid", null);
        if (oid != null) {
            objectID = oid;
        }

    }


    public void checkstatus() {

        Log.d(TAG,"Check Status executed!");

        brand = Build.MANUFACTURER;
        model = Build.PRODUCT;
        //info of the user to be combined
        PackageInfo packageinfo = null;
        try {
            packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Version = packageinfo.versionName.toString();
        Database db = new Database(getApplicationContext());
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true")) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra(USEREMAIL, user_email);
                i.putExtra(USERPASS, user_password);
                i.putExtra(SplashScreen_Activity.ACTIVITY_CLUE, OPEN_DASHBOARD);
                // Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                startActivity(i);
                finish();
            }
        }
        if (isNetworkAvailable()) {

//            Log.e(TAG,"From Check Status");

            getRegId();
        }

        Log.d(TAG,"Check Status ended!");

    }


    public static String getDate(String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final EditText textBox_mail =
                (EditText) findViewById(R.id.email);

        final EditText textBox_pass =
                (EditText) findViewById(R.id.password);
        CharSequence userText = textBox_mail.getText();
        CharSequence userPass = textBox_pass.getText();
        outState.putCharSequence("mail", userText);
        outState.putCharSequence("pass", userPass);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);

        final EditText textBox_mail =
                (EditText) findViewById(R.id.email);

        final EditText textBox_pass =
                (EditText) findViewById(R.id.password);

        CharSequence userText =
                savedState.getCharSequence("mail");
        CharSequence userPass =
                savedState.getCharSequence("pass");

        textBox_mail.setText(userText);
        textBox_pass.setText(userPass);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int results = ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
        int resultss = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);

        if (result == PackageManager.PERMISSION_GRANTED && results == PackageManager.PERMISSION_GRANTED && resultss == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;
        }
    }


    private void requestPermission() {

        Log.v("Jhoro", "request permission");
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_PHONE_STATE)) {
            Toast.makeText(context, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
            Log.v("Jhoro", "request permission 1");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    //imei = tm.getDeviceId();
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    Snackbar.make(view, "Permission Granted, Now you can access all features.", Snackbar.LENGTH_LONG).show();

                } else {

                    Snackbar.make(view, "Permission Denied, You cannot access all features.", Snackbar.LENGTH_LONG).show();

                }
                break;

            case 23:
                if (grantResults.length > 0) {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions = new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, listPermissionsNeeded.get(i))) {
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            } else {
                                Log.i(TAG, "Go to settings and enable permissions");
                                Toast.makeText(activity, "Go to settings and enable permission :" + listPermissionsNeeded.get(i), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if (pending_permissions.size() > 0) {
                        ActivityCompat.requestPermissions(activity, pending_permissions.toArray(new String[pending_permissions.size()]), request_code);

                    } else {
                        Toast.makeText(activity, "All Permissions Granted .", Toast.LENGTH_LONG).show();
                    }
                }
                break;


        }
    }

    private void getFCMToken() {

        // Get token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w("Firebase", "Fetching FCM registration token failed", task.getException());
                    return;
                }
                // Do something with the token if successful
                String token = task.getResult();

                // save to shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("onukit_creds", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("oid", token);
                editor.apply();

                Log.d("Firebase", token);
                setToken(token);
            }
        });






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
//                        generatedToken = token;
//                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.i(TAG, msg);
////                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
////                        setToken(msg);
//                    }
//                });
    }

    public void setToken(String token) {
        generatedToken = token;

    }
}