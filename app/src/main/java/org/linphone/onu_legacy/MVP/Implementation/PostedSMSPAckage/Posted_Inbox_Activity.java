package org.linphone.onu_legacy.MVP.Implementation.PostedSMSPAckage;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.WebViews.WebViews;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

public class Posted_Inbox_Activity extends AppCompatActivity implements
        View.OnClickListener{


    private String id= null;
    private PostedRecycleAdapter smsAdapter;
    private ImageView home;
    private RecyclerView smsRecyclerList ;
    private FloatingActionButton actionButton;
    private String TAG="Posted_Inbox_Activity";
    private Database db;
    private ImageView goOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_posted);
        db=new Database(this);
        try{
            getSupportActionBar().hide();
            Init();
        }catch (Exception e)
        {
            Log.i("error",e.toString());
        }
    }

    private void Init() {
        home= findViewById(R.id.inboxSmsToHome);

        smsRecyclerList = (RecyclerView) findViewById(R.id.postSMSlist);
        smsAdapter = new PostedRecycleAdapter(this,db.getAllPostedInbox());

        smsRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        smsRecyclerList.setAdapter(smsAdapter);
        goOnline=findViewById(R.id.goOnline);

        //Information about action button
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.dllt);
        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearInboxSMS();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Posted_Inbox_Activity.this, DashBoard_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        goOnline.setOnClickListener(this);
    }
    private void ClearInboxSMS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Posted_Inbox_Activity.this);
        alertDialog.setTitle("Clear Inbox");
        alertDialog.setMessage("Do you want to delete all sms from Inbox ?");
        final TextView input = new TextView(Posted_Inbox_Activity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.icon);
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Database db = new Database(Posted_Inbox_Activity.this);
                        db.deleteAll();
                        //db.updateSent("0");
                        Toast.makeText(Posted_Inbox_Activity.this, "Inbox cleared !", Toast.LENGTH_LONG).show();
                        dialog.cancel();
                        Intent i=new Intent(Posted_Inbox_Activity.this, Posted_Inbox_Activity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Double currentSpeed = intent.getDoubleExtra("currentSpeed", 20);
            smsAdapter.notifyData(db.getAllPostedInbox());
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.goOnline:
                Intent i = new Intent(Posted_Inbox_Activity.this, WebViews.class);
                i.putExtra("url","http://user.onuserver.com/6v0/massage/inbox");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                break;
        }
    }
}
