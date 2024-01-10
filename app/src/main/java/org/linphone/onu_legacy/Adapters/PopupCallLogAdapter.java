package org.linphone.onu_legacy.Adapters;
//<!--used in 6v3-->

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.linphone.onu_legacy.Activities.Activities.PopupTaskActivity;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.Fruit;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.TaskConversion;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class PopupCallLogAdapter extends RecyclerView.Adapter<PopupCallLogAdapter.Holder> {

    private ArrayList<Fruit> arrayList = new ArrayList<>();
    private int mPreviousPosition = 0;

    private String STATUS_CODE;

    private Context context;
    private String url = null, uname = null, upass = null, deviceID = null;
    public ProgressDialog progressBar;
    private TaskConversion taskConversion;

    public String callerMsisdn;
    private String TAG="PopupCallLogAdapter";
    private String summaryList, employeeList;
    private String phoneNo, timeStamp, callStatus, callerName="", callerContactName="";

    private String responseResult;


    public PopupCallLogAdapter(ArrayList<Fruit> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.popup_call_list_row, parent, false);
        Holder holder = new Holder(view);
        context = parent.getContext();
        set_app_url();
        //Log.i(TAG,"Hey I'm Logging!");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Fruit fruit = arrayList.get(position);
        holder.index.setText(fruit.getName());
        STATUS_CODE = fruit.getAge();
        holder.lines.setText(fruit.getGame());
        holder.status.setText(fruit.getStatus());

        if(!getContactName(fruit.getName()).trim().equals("")) {
            holder.callerContactNameText.setText(" (" + getContactName(fruit.getName()) + ")");
        }
        else {
            holder.callerContactNameText.setText("");
        }
        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
        }
        mPreviousPosition = position;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView index, surah, lines, status, callerContactNameText;

        public Holder(View itemView) {

            super(itemView);
            index = (TextView) itemView.findViewById(R.id.t1);
            lines = (TextView) itemView.findViewById(R.id.t3);
            status=(TextView)itemView.findViewById(R.id.call_status);
            callerContactNameText=(TextView)itemView.findViewById(R.id.caller_contact_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
       //     Toast.makeText(v.getContext(), "Phone no " + index.getText().toString(), Toast.LENGTH_LONG).show();
            callerMsisdn = index.getText().toString();
            phoneNo=index.getText().toString();
            timeStamp=lines.getText().toString();
            if(isNetworkAvailable()) {
                taskConversion=new TaskConversion(context);
                taskConversion.taskMaking(phoneNo,"",timeStamp,"","incoming", null);
            } else {
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
                Log.i(TAG,"Intent pass by click: "+phoneNo+" "+timeStamp+"\n"+summaryList+"\n"+employeeList);
                context.startActivity(taskIntent);
            }
        }
    }

    public void set_app_url() {
        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/getSummary";
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
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getContactName(String phoneNumber)
    {
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName="";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }
        return contactName;
    }
}
