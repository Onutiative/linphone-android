package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.onutiative.onukit.Activities.PopupTaskActivity;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Database.Task;
import com.onutiative.onukit.MVP.Implementation.TaskPackage.TaskShowActivity;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskConversion extends Service {


    //for task start
    private String responseResult;
    private String summaryList, employeeList;
    private String phoneNo="", timeStamp, callStatus="", callerName="", callerContactName="";
    private String url = null, uname = null, upass = null, deviceID = null;
    private String message;
    private String comeFrom;
    //for task end
    private Context context;
    private String TAG="TaskConversion";
    private String taskID;
    private JSONArray taskArray;
    private int arrayIndex=0;

    public TaskConversion(Context context){
        this.context=context;
        Log.i(TAG,"TaskConversion constructor created");
    }

    //convert sms to task section start
// time format yyyy-MM-dd HH:mm:ss as string
    public void taskMaking(String number,String textMessage, String time,String ID, String from){
        this.timeStamp=time;
        this.message=textMessage;
        this.comeFrom=from;
        this.taskID=ID;

        Log.i(TAG,"Incoming Data: Number: "+number+"; SMS Body: "+message+"; SMS Time: "+time+"; "+comeFrom+"; Task id: "+taskID);
        set_app_url();
        phoneNo=number;
        Log.i(TAG, "Come from: "+comeFrom);
        if(isNetworkAvailable()) {
            // this reassign operation will work for without phone and caller name saved task
            if (phoneNo.isEmpty()){
                summaryList="[]";
                employeeList="[]";
                callerName="";
                phoneNo="";
                Intent taskIntent = new Intent(context, PopupTaskActivity.class);
                taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                taskIntent.putExtra("summary",summaryList);
                taskIntent.putExtra("summaryObj",getAllTask(summaryList));
                taskIntent.putExtra("employee",employeeList);
                taskIntent.putExtra("phoneNo", phoneNo);
                taskIntent.putExtra("timeStamp", timeStamp);
                taskIntent.putExtra("callerName",callerName);
                taskIntent.putExtra("message",message);
                taskIntent.putExtra("from",comeFrom);
                taskIntent.putExtra("taskID",taskID);
                taskIntent.putExtra("arrayIndex",arrayIndex);
                Log.i(TAG,"Number empty and on newTask");
                if (comeFrom.equals("newTask") || comeFrom.equals("reassignTaskListSave")){
                    Intent intent=new Intent(context, TaskShowActivity.class);
                    intent.putExtra("from","All");
                    context.startActivity(intent);
                }else{
                    context.startActivity(taskIntent);
                }
            }else {
                new PopupCallSummary(context).execute();
            }
        }
        else {
            Toast.makeText(context,"No Internet Connection!",Toast.LENGTH_LONG).show();
            summaryList="[]";
            employeeList="[]";
            callerName="";
//          if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
            //timeStamp=getDate();
            Intent taskIntent = new Intent(context, PopupTaskActivity.class);
            taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            taskIntent.putExtra("summary",summaryList);
            taskIntent.putExtra("summaryObj",getAllTask(summaryList));
            taskIntent.putExtra("employee",employeeList);
            taskIntent.putExtra("phoneNo", phoneNo);
            taskIntent.putExtra("timeStamp", timeStamp);
            taskIntent.putExtra("callerName",callerName);
            taskIntent.putExtra("message",message);
            taskIntent.putExtra("from",comeFrom);
            taskIntent.putExtra("taskID",taskID);
            taskIntent.putExtra("arrayIndex",arrayIndex);
            //Log.i(TAG,"Array index: "+arrayIndex+"; Select id: "+taskID+"; Filtered task ID: "+getAllTask(summaryList).get(arrayIndex).getId());
            Log.i(TAG,"TC net through without: "+phoneNo+"\n"+timeStamp+"\n"+summaryList+"\n"+employeeList+"\n"+message);
            context.startActivity(taskIntent);

        }
        stopSelf();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            Log.i(TAG,"PopupCallSummary Execution called");
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG," Post Execution called");
            if(responseResult!=null)
            {
                Log.i(TAG,"Post execute "+responseResult.toString());
                try{

                    JSONObject responseObject=new JSONObject(responseResult);
                    Log.i(TAG,"Post execute");
                    summaryList=responseObject.getJSONArray("summary").toString();
                    callerName=responseObject.getString("caller_name").toString();
                    employeeList=responseObject.getJSONArray("employee").toString();
                    //timeStamp=getDate();
                    if (!comeFrom.equals("newTask")){
                        Intent taskIntent = new Intent(context, PopupTaskActivity.class);
                        taskIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        taskIntent.putExtra("phoneNo", phoneNo);
                        taskIntent.putExtra("timeStamp", timeStamp);
                        //taskIntent.putExtra("summary",summaryList);
                        taskIntent.putExtra("summaryObj",getAllTask(summaryList));
                        taskIntent.putExtra("employee",employeeList);
                        taskIntent.putExtra("callerName",callerName);
                        taskIntent.putExtra("message",message);
                        taskIntent.putExtra("from",comeFrom);
                        taskIntent.putExtra("taskID",taskID);
                        taskIntent.putExtra("arrayIndex",arrayIndex);
                        try{
                            Log.i(TAG,"Array index: "+arrayIndex+"; Select id: "+taskID+"; Filtered task ID: "+getAllTask(summaryList).get(arrayIndex).getId());
                            Log.i(TAG,"TC through with net: "+phoneNo+"\n"+timeStamp+"\n"+summaryList+"\n"+employeeList+"\n"+message);
                        }catch (Exception e){

                        }
                        if (comeFrom.equals("reassign")&& phoneNo.isEmpty()){
                            Intent intent=new Intent(context, TaskShowActivity.class);
                            context.startActivity(intent);
                        }else {
                            context.startActivity(taskIntent);
                        }
                    }else {
                        Intent intent=new Intent(context, TaskShowActivity.class);
                        context.startActivity(intent);
                    }
                }catch(JSONException e)
                {
                    Log.i(TAG,"Exception: "+e.toString());
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
                Log.i(TAG,"Username: "+ uname);
                Log.i(TAG,"password: "+ upass);
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
                Log.i(TAG,jsonObject.toString());

                //-----------------------------------------------------------------
                StringEntity myStringEntity = new StringEntity(jsonArray.toString(), "UTF-8");
                httppost.setEntity(myStringEntity);
                //--------------execution of httppost
                HttpResponse response = httpclient.execute(httppost);
                String res = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "response: " + res);
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
                Log.i(TAG, "Request Exception:" + e);
            }
            return null;
        }
    }

    private String getDate(long time){
        long timeInMillis = time;
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(cal1.getTime());
    }

    private ArrayList<Task> getAllTask(String taskSummary)
    {
        ArrayList<Task> arrayList=new ArrayList<>();

        try {
            taskArray = new JSONArray(taskSummary);
            Log.d(TAG,"JSON Array "+taskArray);

            for(int i=0;i<taskArray.length();++i)
            {
                JSONObject taskObject=taskArray.getJSONObject(i);

                String id="",callTime="",callPurpose="",taskStatus="",callSummary="",asigneeEmail="",insertTime="",estimatedDate="",estimatedTime="";

                if(!taskObject.get("call_time").equals(null))
                {
                    callTime=taskObject.getString("call_time");
                }

                if(!taskObject.get("call_reason").equals(null))
                {
                    callPurpose =taskObject.getString("call_reason");
                }
                if(!taskObject.get("summery_status").equals(null))
                {
                    taskStatus=taskObject.getString("summery_status");
                }
                if(!taskObject.get("call_summery").equals(null))
                {
                    callSummary=taskObject.getString("call_summery");
                }
                if(!taskObject.get("id").equals(null))
                {
                    id=taskObject.getString("id");
                }

                asigneeEmail="";

                if(!taskObject.get("insert_time").equals(null))
                {
                    insertTime=taskObject.getString("insert_time");
                    String[] splitTime = insertTime.split("\\s+");
                    estimatedDate=splitTime[0];
                    estimatedTime=splitTime[1];
                }

                Task task=new Task(id,callTime,callPurpose,taskStatus,callSummary,asigneeEmail,estimatedDate,estimatedTime);
                //Log.i(TAG,"In conversion: task id"+id+" =  "+taskID);
                if (id.equals(taskID)){
                    arrayIndex=i;
                }
                arrayList.add(task);
            }
        }
        catch(JSONException ex)
        {
            ex.printStackTrace();
        }
        return arrayList;
    }

    //convert sms to task section stop
}
