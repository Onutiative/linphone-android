package org.linphone.onu_legacy.Activities;
//<!--used in 6v3-->

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.linphone.LinphoneApplication;
import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Adapters.PopupCallLogAdapter;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.Fruit;
import org.linphone.R;
import org.linphone.onuspecific.OnuFunctions.GetCallLogs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PopupCallListActivity extends AppCompatActivity {


    private String type = "in";
    private PopupCallLogAdapter adapter;
    private TextView toptext;
    private Button webButton;
    private String TAG = "PopupCallListActivity";
    private ImageView incomingCallToHome;
    LinphoneApplication.Companion companion = LinphoneApplication.Companion;
    CallLog[] callLogs = companion.getCoreContext().getCore().getCallLogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_call_list);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(),DashBoard_Activity.class));
//            }
//        });


//        try {
//            Objects.requireNonNull(getSupportActionBar()).hide();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
        InitCallLog();

    }

    private void InitCallLog() {

//        type = "in";
//        toptext= (TextView) findViewById(R.id.header);

        webButton = (Button) findViewById(R.id.weburl);
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.dllt);

        ArrayList<Fruit> callLogData = getCallData();
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.popup_call_list);
        adapter = new PopupCallLogAdapter(callLogData);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
//        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
//                .setContentView(icon)
//                .build();

        incomingCallToHome = findViewById(R.id.incomingCallToHome);
        incomingCallToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupCallListActivity.this, DashBoard_Activity.class);
                startActivity(intent);
            }
        });

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

                if(callType.equals("INCOMING")) {
                    Fruit f = new Fruit(phoneNumber, callerName, callDate.toString(), callType);
                    // Log.i(TAG, "Phone no: " + phoneNumber);
                    //Toast.makeText(this,cn.getPhone_number(),Toast.LENGTH_LONG).show();
                    data.add(f);
                }
            }
        }
        return data;
    }
}
