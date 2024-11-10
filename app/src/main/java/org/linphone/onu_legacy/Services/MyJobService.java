package org.linphone.onu_legacy.Services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Activities.PopupCallListActivity;
import org.linphone.onu_legacy.Activities.Activities.PopupTaskActivity;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MyJobService extends Worker {


    private WindowManager mWindowManager;
    private View mFloatingView;
    WindowManager.LayoutParams params;
    private Context context;
    private String TAG="MyJobService";
    private int maxX, maxY, lastCheckY;
    private String url = null, uname = null, upass = null, deviceID = null;
    public ProgressDialog progressBar;
    private String responseResult;
    private String summaryList, employeeList;
    private String phoneNo, timeStamp="", callStatus="", callerName="", callerContactName="";

    private SharedPrefManager sharedPrefManager;

    public MyJobService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        context= MyJobService.this.getApplicationContext();
        sharedPrefManager=new SharedPrefManager(context);

        set_app_url();

        Log.i(TAG,"Job Service Started");

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mFloatingView = LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.layout_floating_widget, null);

                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                        PixelFormat.TRANSLUCENT);


                params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
                params.x = 0;
                params.y = 100;

                mWindowManager = (WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

                mWindowManager.addView(mFloatingView, params);

                Display mdisp = mWindowManager.getDefaultDisplay();
                Point mdispSize = new Point();
                mdisp.getSize(mdispSize);
                maxX = mdispSize.x;
                maxY = mdispSize.y;
                lastCheckY=maxY-(maxY/5);

                Log.i(TAG,"Highest X and Y "+maxX+" "+maxY);


                //The root element of the collapsed view layout
                final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
                //The root element of the expanded view layout
                final View expandedView = mFloatingView.findViewById(R.id.expanded_container);





                //Set the close button
//        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
//        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //close the service and remove the from from the window
//                stopSelf();
//            }
//        });



                //Set the view while floating view is expanded.
                //Set the play button.
                ImageView playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Playing the song.", Toast.LENGTH_LONG).show();
                    }
                });


                //Set the next button.
                ImageView nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Playing next song.", Toast.LENGTH_LONG).show();
                    }
                });


                //Set the pause button.
                ImageView prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
                prevButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Playing previous song.", Toast.LENGTH_LONG).show();
                    }
                });


                //Set the close button
                ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        collapsedView.setVisibility(View.VISIBLE);
                        expandedView.setVisibility(View.GONE);
                    }
                });


                //Open the application on thi button click
                ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
                openButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Open the application  click.
                        Intent intent = new Intent(context, DashBoard_Activity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);


                        //close the service and remove view from the view hierarchy
                        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
                        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
                        dispatcher.cancel("my-unique-tag");
                    }
                });

                //Drag and move floating view using user's touch action.
                mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
                    private int initialX;
                    private int initialY;
                    private float initialTouchX;
                    private float initialTouchY;


                    @Override
                    public boolean onTouch(View v, MotionEvent event) {


//                Log.d(TAG,"Motion Happening!");


                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:

                                //remember the initial position.
                                initialX = params.x;
                                initialY = params.y;

                                //get the touch location
                                initialTouchX = event.getRawX();
                                initialTouchY = event.getRawY();
                                return true;
                            case MotionEvent.ACTION_UP:
                                int Xdiff = (int) (event.getRawX() - initialTouchX);
                                int Ydiff = (int) (event.getRawY() - initialTouchY);
                                //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                                //So that is click event.
                                if (Xdiff < 10 && Ydiff < 10) {
                                    if (isViewCollapsed()) {
                                        //When user clicks on the image view of the collapsed layout,
                                        //visibility of the collapsed layout will be changed to "View.GONE"
                                        //and expanded view will become visible.
                                        //  Toast.makeText(context, "Clicked.", Toast.LENGTH_LONG).show();

                                        if(sharedPrefManager.getPopupClickStatus())
                                        {
                                            if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
                                            phoneNo=sharedPrefManager.getIncomingPhoneNumber();
                                            if(isNetworkAvailable())
                                            {
                                                Log.i(TAG,"On network popup operation execute");
                                                new PopupCallSummary(context).execute();
                                            }
                                            else
                                            {
                                                Toast.makeText(context,"No Internet Connection!",Toast.LENGTH_LONG).show();

                                                summaryList="[]";
                                                employeeList="[]";
                                                callerName="";
                                                Intent taskIntent = new Intent(context, PopupTaskActivity.class);
                                                taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                taskIntent.putExtra("summary",summaryList);
                                                taskIntent.putExtra("employee",employeeList);
                                                taskIntent.putExtra("phoneNo", phoneNo);
                                                taskIntent.putExtra("timeStamp", timeStamp);
                                                taskIntent.putExtra("callerName",callerName);

                                                Log.i(TAG,"Through to task: "+phoneNo+" "+timeStamp+"\n"+summaryList+"\n"+employeeList);

                                                context.startActivity(taskIntent);
                                            }
                                        }
                                        else
                                        {
                                            Intent intent = new Intent(context, PopupCallListActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);

                                            if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
                                        }
//                                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
//                                dispatcher.cancel("my-unique-tag");

//                                collapsedView.setVisibility(View.GONE);
//                                expandedView.setVisibility(View.VISIBLE);
                                    }
                                }
                                return true;
                            case MotionEvent.ACTION_MOVE:
                                //Calculate the X and Y coordinates of the view.
                                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                                params.y = initialY + (int) (event.getRawY() - initialTouchY);

//                        Log.d(TAG,params.x+" "+params.y);
//                        Log.d(TAG,event.getRawX()+" "+event.getRawY());

                                if(params.y>=lastCheckY)
                                {
                                    Log.d(TAG,"Popup should be closed!");
                                    if (mFloatingView != null) mWindowManager.removeView(mFloatingView);


                                    if(sharedPrefManager.getPopupVibrationStatus()) {

                                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
                                        } else {
                                            vibrator.vibrate(250);
                                        }
                                    }
                                }
                                //Update the layout with new X & Y coordinate
                                mWindowManager.updateViewLayout(mFloatingView, params);
                                return true;
                        }
                        return false;
                    }
                });




            }

            private Context getApplicationContext() {
                return context;
            }
        });
        // mWindowManager.addView(mFloatingView, params);
        return Result.success();

    }

//    @Override
//    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
//
//        Toast.makeText(context,"Stop Job Called",Toast.LENGTH_LONG).show();
//
//        if (mFloatingView != null) {mWindowManager.removeView(mFloatingView);}
//        return false;
//    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void set_app_url() {
        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/getSummary";
                //http://api1.onuserver.com:8085/v3/incomingSms
            } else if (cn.getName().equals("email"))

            {
                uname = cn.getPhone_number();

            } else if (cn.getName().equals("password"))

            {
                upass = cn.getPhone_number();
            } else if (cn.getName().equals("did")) {
                deviceID = cn.getPhone_number();
            }
        }
    }




    public class PopupCallSummary extends AsyncTask<Void, Void, String> {
        int TIMEOUT_MILLISEC = 5000;
        Context context;
        ProgressDialog dialog;
        Activity activity;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        public PopupCallSummary(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String result) {

//            if (progressBar.isShowing()) {
//                progressBar.dismiss();
//            }

            Log.i(TAG,"Post execute "+responseResult.toString());

            if(responseResult!=null)
            {
                try{
                    JSONObject responseObject=new JSONObject(responseResult);

                    Log.i(TAG,"Post execute");

                    summaryList=responseObject.getJSONArray("summary").toString();
                    callerName=responseObject.getString("caller_name").toString();

                    employeeList=responseObject.getJSONArray("employee").toString();

                    Intent taskIntent = new Intent(context, PopupTaskActivity.class);
                    taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    taskIntent.putExtra("phoneNo", phoneNo);
                    taskIntent.putExtra("timeStamp", timeStamp);
                    taskIntent.putExtra("summary",summaryList);
                    taskIntent.putExtra("employee",employeeList);
                    taskIntent.putExtra("callerName",callerName);
                    // add FLAG_ACTIVITY_NEW_TASK
                    taskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Log.i(TAG,"Through to task: "+phoneNo+" "+timeStamp+"\n"+summaryList+"\n"+employeeList);

                    context.startActivity(taskIntent);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String username = uname;
                String password = upass;
//                Log.e("Username: ", uname);
//                Log.e("password: ", upass);
                Log.i("CList", "1 url:" + url);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                        .readTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS)
                        .build();

                Log.i("CList", "2");

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("device_id", deviceID);
                jsonObject.accumulate("caller_msisdn", phoneNo.substring(1));

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);

                Log.d(TAG, jsonObject.toString());

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonArray.toString());

                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .header("Content-type", "application/json")
                        .header("Authorization", "Basic " + credBase64)
                        .build();

                Log.i("CList", "4");

                Response response = client.newCall(request).execute();
                String res = response.body().string();
                Log.d(TAG, "response: " + res);
                responseResult = res;

                int statusCode = response.code();
                if (statusCode >= 200 && statusCode <= 299) {
                    Toast.makeText(context, "Sending . . .", Toast.LENGTH_SHORT).show();
                    return null;
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.i("CList", "exception:" + e);
            }

            return null;
        }
    }
}