package org.linphone.onu_legacy.Activities.Activities;
//<!--used in 6v3-->

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onutiative.onukit.Adapters.NotificationRecycleAdapter;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Database.Fruits;
import com.onutiative.onukit.Database.Sms;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.Info;

import java.util.ArrayList;
import java.util.List;

public class NotificationList_Activity extends AppCompatActivity {

    private Info info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        getSupportActionBar().hide();
        info=new Info(this);
        ArrayList<Fruits> ff= mm();
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rrrr);
        RecyclerView.Adapter adapter = new NotificationRecycleAdapter(ff);
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        TextView NotifyOut= (TextView) findViewById(R.id.NotifyOut);
        TextView NotifyCall= (TextView) findViewById(R.id.NotifyCall);
        TextView Respondd= (TextView) findViewById(R.id.Respondd);

        NotifyOut.setText(info.getNotifyout());
        NotifyCall.setText(info.getNotifycall());
        Respondd.setText(info.getLastSent());

    }


    public ArrayList<Fruits> mm(){
        ArrayList<Fruits> data=new ArrayList<>();
        Database db = new Database(this);
        List<Sms> contacts = db.getAllNotification();
        for (Sms cn : contacts)
        {
            Fruits f=new Fruits(cn.getPhoneNumber(),cn.getName(),cn.getTime(),cn.getStatus());
            data.add(f);
        }
        return data;
    }
}
