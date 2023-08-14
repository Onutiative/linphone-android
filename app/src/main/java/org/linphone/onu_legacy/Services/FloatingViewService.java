//used in 6v3
package org.linphone.onu_legacy.Services;

/**
 * Created by jhorotek on 6/6/2018.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
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

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Activities.PopupCallListActivity;
import org.linphone.onu_legacy.Activities.Activities.PopupTaskActivity;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

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
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class FloatingViewService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
     WindowManager.LayoutParams params;
     private String TAG="FloatingViewService";
    private int maxX, maxY, lastCheckY;
    private Context context;
    private SharedPrefManager sharedPrefManager;
    private String responseResult;
    private String summaryList, employeeList;
    private String phoneNo, timeStamp=getDate(), callStatus="", callerName="", callerContactName="";
    private String url = null, uname = null, upass = null, deviceID = null;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        context=FloatingViewService.this;

        sharedPrefManager=new SharedPrefManager(context);
        set_app_url();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
             params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                    PixelFormat.TRANSLUCENT);
        }
        else {

             params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,

                    PixelFormat.TRANSLUCENT);
        }

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        Display mdisp = mWindowManager.getDefaultDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        maxX = mdispSize.x;
        maxY = mdispSize.y;
        lastCheckY=maxY-(maxY/5);

        Log.d(TAG,"Highest X and Y "+maxX+" "+maxY);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);

        //Set the view while floating view is expanded.
        //Set the play button.
        ImageView playButton = (ImageView) mFloatingView.findViewById(R.id.play_btn);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing the song.", Toast.LENGTH_LONG).show();
            }
        });

        //Set the next button.
        ImageView nextButton = (ImageView) mFloatingView.findViewById(R.id.next_btn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing next song.", Toast.LENGTH_LONG).show();
            }
        });


        //Set the pause button.
        ImageView prevButton = (ImageView) mFloatingView.findViewById(R.id.prev_btn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatingViewService.this, "Playing previous song.", Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(FloatingViewService.this, DashBoard_Activity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


                //close the service and remove view from the view hierarchy
                stopSelf();
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

                Log.d(TAG,"Motion happening!");

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        Log.d(TAG,params.x+" "+params.y);

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:

                        Log.d(TAG,params.x+" "+params.y);

                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.

                             //   Toast.makeText(FloatingViewService.this, "Clicked.", Toast.LENGTH_LONG).show();

                                if(sharedPrefManager.getPopupClickStatus())
                                {
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
                                        String floatBTN="on";

//                                        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
                                        //timeStamp=getDate();
                                        Intent taskIntent = new Intent(context, PopupTaskActivity.class);
                                        taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        taskIntent.putExtra("summary",summaryList);
                                        taskIntent.putExtra("employee",employeeList);
                                        taskIntent.putExtra("phoneNo", phoneNo);
                                        taskIntent.putExtra("timeStamp", timeStamp);
                                        taskIntent.putExtra("callerName",callerName);
                                        taskIntent.putExtra("floating",floatBTN);

                                        Log.i(TAG,"FloatVS through: "+phoneNo+" "+timeStamp+"\n"+summaryList+"\n"+employeeList);

                                        context.startActivity(taskIntent);

                                    }

                                stopSelf();

                                }
                                else
                                {
                                    stopSelf();
                                    Intent intent = new Intent(FloatingViewService.this, PopupCallListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

//                        Log.d(TAG,params.x+" "+params.y);

                        if(params.y>=lastCheckY)
                        {

                            stopSelf();

                            if(sharedPrefManager.getPopupVibrationStatus()) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(250, VibrationEffect.DEFAULT_AMPLITUDE));
                                } else {
                                    vibrator.vibrate(250);
                                }
                            }

//                            Log.d(TAG,"Popup should be closed!");
//                            if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
                        }

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */


    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
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


            if(responseResult!=null)
            {

                Log.d(TAG,"Post execute "+responseResult.toString());

                try{
                    JSONObject responseObject=new JSONObject(responseResult);

                    Log.d(TAG,"Post execute");

                    summaryList=responseObject.getJSONArray("summary").toString();
                    callerName=responseObject.getString("caller_name").toString();

                    employeeList=responseObject.getJSONArray("employee").toString();

                    String floatBTN="on";

                    //timeStamp=getDate();
                    Intent taskIntent = new Intent(context, PopupTaskActivity.class);
                    taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    taskIntent.putExtra("phoneNo", phoneNo);
                    taskIntent.putExtra("timeStamp", timeStamp);
                    taskIntent.putExtra("summary",summaryList);
                    taskIntent.putExtra("employee",employeeList);
                    taskIntent.putExtra("callerName",callerName);
                    taskIntent.putExtra("floating",floatBTN);

                    Log.i(TAG,"FloatVS through: "+phoneNo+" "+timeStamp+"\n"+summaryList+"\n"+employeeList);

                    context.startActivity(taskIntent);

                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected String doInBackground(Void... params) //HTTP
        {
            try {

                String status = null;
                String success = "4000";
                int statusCode = 0;
                String username = uname;
                String password = upass;
                Log.e("Username: ", uname);
                Log.e("password: ", upass);
                //username ="Onu$erVe9";
                //password ="p#@$aS$";
                Log.i("CList", "1 url:" + url);

                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpParams p = new BasicHttpParams();
                HttpClient httpclient = new DefaultHttpClient(p);
                Log.i("CList", "2");
                HttpPost httppost = new HttpPost(url);
                Log.i("CList", "3");
                httppost.setHeader("Content-type", "application/json");
                //---------------------Code for Basic Authentication-----------------------
                String credentials = username + ":" + password;
                String credBase64 = Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
                httppost.setHeader("Authorization", "Basic " + credBase64);
                Log.i("CList", "4");
                //------------------------------------------
                JSONArray jsonArray = new JSONArray();


                JSONObject jsonObject = new JSONObject();

                jsonObject.accumulate("device_id", deviceID);
                jsonObject.accumulate("caller_msisdn", phoneNo.substring(1));

                jsonArray.put(jsonObject);

                Log.d(TAG,jsonObject.toString());

                //-----------------------------------------------------------------
                StringEntity myStringEntity = new StringEntity(jsonArray.toString(), "UTF-8");
                httppost.setEntity(myStringEntity);
                //--------------execution of httppost
                HttpResponse response = httpclient.execute(httppost);
                String res = EntityUtils.toString(response.getEntity());
                Log.d(TAG, "response: " + res);
                responseResult=res;

//                JSONObject responseObject=new JSONObject(res);
//                summaryList=responseObject.getJSONArray("summary").getJSONObject(0).toString();

//                summaryList=responseObject.getString("summary");
                //   Toast.makeText(context,res,Toast.LENGTH_LONG).show();
                statusCode = response.getStatusLine().getStatusCode();
                // if(status.equals(success))
                if (statusCode >= 200 && statusCode <= 299) {
                    Toast.makeText(context, "Sending . . .", Toast.LENGTH_SHORT).show();
                    return null;
                } else
                    return null;

            } catch (Exception e) {
                Log.i("CList", "exception:" + e);
            }

            return null;
        }
    }

    private String getDate(){
        long timeInMillis = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(cal1.getTime());
    }
}
