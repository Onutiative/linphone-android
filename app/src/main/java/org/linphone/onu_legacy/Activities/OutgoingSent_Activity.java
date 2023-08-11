package org.linphone.onu_legacy.Activities;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.onutiative.onukit.Adapters.SentRecycleAdapter;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.Database.Fruit;
import com.onutiative.onukit.Database.ServerSms;
import com.onutiative.onukit.R;
import com.onutiative.onukit.WebViews.WebViews;

import java.util.ArrayList;
import java.util.List;

public class OutgoingSent_Activity extends AppCompatActivity {

    private String id= null;
    //private TextView surah;
    private Database sqliteDB;
    private Context context;
    private ImageView sentSmsToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentbox);
        getSupportActionBar().hide();
        Button weburl= (Button) findViewById(R.id.weburl);
        context=OutgoingSent_Activity.this;
        sentSmsToHome=findViewById(R.id.sentSmsToHome);

        sqliteDB=new Database(context);


        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.dllt);
        //TextView heading= (TextView) findViewById(R.id.surahName);
       // heading.setText("Sent Sms");
//        ArrayList<Fruit> ff= mm();

        ArrayList<ServerSms> serverSmsList=sqliteDB.getAllSentSmsData();

        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rrrr);

//        RecyclerView.Adapter adapter = new SentRecycleAdapter(ff);
        RecyclerView.Adapter adapter = new SentRecycleAdapter(serverSmsList);
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        weburl.setOnClickListener(   new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(OutgoingSent_Activity.this,"Outgoing SMS Sent",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(OutgoingSent_Activity.this, WebViews.class);
                i.putExtra("url","http://user.onuserver.com/6v0/login_from_app/smsOutBox");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OutgoingSent_Activity.this);
                alertDialog.setTitle("Clear Outbox");
                alertDialog.setMessage("Do you want to delete all sms from Outbox ?");
                final TextView input = new TextView(OutgoingSent_Activity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Database db = new Database(OutgoingSent_Activity.this);
                                db.deleteallsent();
                                db.updateSent("0");
                                Toast.makeText(OutgoingSent_Activity.this, "Outbox cleared !", Toast.LENGTH_LONG).show();
                                dialog.cancel();
                                Intent i=new Intent(OutgoingSent_Activity.this,OutgoingSent_Activity.class);
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
        });
        sentSmsToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OutgoingSent_Activity.this,DashBoard_Activity.class);
                startActivity(intent);
            }
        });
    }

    public ArrayList<Fruit> mm(){
        ArrayList<Fruit> data=new ArrayList<>();

        Database db = new Database(this);
        List<Contact> contacts = db.getAllSents();
        for (Contact cn : contacts)
        {
            Fruit f=new Fruit(cn.getPhone_number(),cn.getName(),cn.getTime());
            data.add(f);
        }

        return data;
    }
}
