package org.linphone.onu_legacy.Activities;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

import org.linphone.LinphoneApplication;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Adapters.CallLogAdapter;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.Fruit;
import org.linphone.R;
import org.linphone.onu_legacy.WebViews.WebViews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CallLog_Activity extends AppCompatActivity implements
        View.OnClickListener{

    private  String type="in";
    private CallLogAdapter adapter;
    private TextView toptext;
    private Button webButton;
    private ImageView sentSmsToHome;
    private String TAG = "CallLog_Activity";

    LinphoneApplication.Companion companion = LinphoneApplication.Companion;
    CallLog[] callLogs = companion.getCoreContext().getCore().getCallLogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_show);
        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        } catch (Exception e){
            e.printStackTrace();
        }
        InitCallLog();
    }

    private void InitCallLog() {

        type = getIntent().getExtras().getString("type");
        //toptext= (TextView) findViewById(R.id.header);
        sentSmsToHome=findViewById(R.id.sentSmsToHome);
//        if(!type.equals("in"))
//        {
//            toptext.setText("Outgoing Calls");
//        }
        webButton= (Button) findViewById(R.id.weburl);
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.dllt);
        ArrayList<Fruit> callLogData= getCallData();
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.call_log_list);
        adapter = new CallLogAdapter(callLogData);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        webButton.setOnClickListener(this);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearCallData();
            }
        });
        sentSmsToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallLog_Activity.this, DashBoard_Activity.class);
                startActivity(intent);
            }
        });
    }

    private void ClearCallData() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CallLog_Activity.this);
        alertDialog.setTitle("Clear Call Log");
        if(type.equals("in"))
        {
            alertDialog.setMessage("Do you want to delete all log from Incoming call ?");
        }
        else
        {
            alertDialog.setMessage("Do you want to delete all log from Outgoing call ?");
        }

        final TextView input = new TextView(CallLog_Activity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.onukit_logo2);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Database db = new Database(CallLog_Activity.this);
                        db.delete_call_log(type);
                        //Toast.makeText(CallLog_Activity.this, "Data cleared !", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        Intent i=new Intent(CallLog_Activity.this,CallLog_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra("type",type);
                        startActivity(i);
                    }

                });
        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }

                });

        alertDialog.show();
    }

    public ArrayList<Fruit> getCallData() {

        ArrayList<Fruit> data = new ArrayList<>();

        for (CallLog callLog : callLogs) {
            {
                Address remoteAddress = callLog.getRemoteAddress();
                String phoneNumber = remoteAddress.getUsername();
                String callerName = remoteAddress.getDisplayName(); // Get the caller's name

                long timestamp = callLog.getStartDate();
                Date callDate = new Date(timestamp * 1000L); // The timestamp is in seconds, convert it to milliseconds

                Call.Dir direction = callLog.getDir();
                // Log.d(TAG, "getCallData: " + direction);
                String callType;
                if (direction == Call.Dir.Incoming) {
                    callType = "INCOMING";
                } else if (direction == Call.Dir.Outgoing) {
                    callType = "OUTGOING";
                } else {
                    callType = "MISSED";
                }

                if(callType.equals("OUTGOING")) {
                    Fruit f = new Fruit(phoneNumber, callerName, callDate.toString(), callType);
                    // Log.i(TAG, "Phone no: " + phoneNumber);
                    //Toast.makeText(this,cn.getPhone_number(),Toast.LENGTH_LONG).show();
                    data.add(f);
                }
            }

        }
        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weburl:
                Intent i = new Intent(CallLog_Activity.this, WebViews.class);
                if (type.equals("in")) {
//                    Toast.makeText(CallLog_Activity.this,"Call log incoming",Toast.LENGTH_SHORT).show();
                    i.putExtra("url", "https://user.onukit.com/6v0/login_from_app/incomingCalls");
                } else {
//                    Toast.makeText(CallLog_Activity.this,"Call log outgoing",Toast.LENGTH_SHORT).show();
                    i.putExtra("url", "https://user.onukit.com/6v0/login_from_app/outgoingCalls");
                }
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
    }
}