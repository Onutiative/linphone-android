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

import com.onutiative.onukit.Adapters.PopupCallLogAdapter;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Database.Fruit;
import com.onutiative.onukit.R;

import java.util.ArrayList;
import java.util.List;

public class PopupCallListActivity extends AppCompatActivity {


    private  String type="in";
    private PopupCallLogAdapter adapter;
    private TextView toptext;
    private Button webButton;
    private String TAG="PopupCallListActivity";
    private ImageView incomingCallToHome;

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


//        getSupportActionBar().hide();
        InitCallLog();

    }

    private void InitCallLog() {

//        type = "in";
//        toptext= (TextView) findViewById(R.id.header);

        webButton= (Button) findViewById(R.id.weburl);
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.drawable.dllt);

        ArrayList<Fruit> callLogData= getCallData();
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.popup_call_list);
        adapter = new PopupCallLogAdapter(callLogData);
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
//        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
//                .setContentView(icon)
//                .build();

        incomingCallToHome=findViewById(R.id.incomingCallToHome);
        incomingCallToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PopupCallListActivity.this,DashBoard_Activity.class);
                startActivity(intent);
            }
        });

    }


    public ArrayList<Fruit> getCallData(){

        ArrayList<Fruit> data=new ArrayList<>();
        Database db = new Database(this);
        List<Contact> contacts = db.getAll_calls_with_status(type);

        List<Contact> testContacts=db.getAllCall();
        for (Contact cn : contacts)
        {
            Fruit f=new Fruit(cn.getPhone_number(),cn.getName(),cn.getTime(),cn.getStatus());
            Log.i(TAG,"Phone no: "+cn.getPhone_number());
            //Toast.makeText(this,cn.getPhone_number(),Toast.LENGTH_LONG).show();
            data.add(f);
        }
        return data;
    }
}
