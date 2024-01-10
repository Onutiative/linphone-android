package org.linphone.onu_legacy.Activities.Activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.ServerSms;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import java.util.ArrayList;

public class SmsLogActivity extends AppCompatActivity {


    private Button startSendingSms, showSQLiteData,sendButton, showCount, deleteAllData;
    private Context context;
    private Database sqLiteDB;
    private static final String TAG=SmsLogActivity.class.getSimpleName();
    ArrayList<ServerSms> serverSmsList;
    private int smsReceivedFromServer,smsSentToOperator, smsSentSuccess, smsSentFailure, smsDeliveredSuccess, smsDeliveredFailure;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context=SmsLogActivity.this;
        sqLiteDB=new Database(context);
        sharedPrefManager=new SharedPrefManager(context);

        startSendingSms=(Button)findViewById(R.id.sms_sending_start);   // finding the send sms button
        showSQLiteData=(Button)findViewById(R.id.data_sqlite_show);
        sendButton=(Button)findViewById(R.id.send);
        showCount=(Button)findViewById(R.id.count_show);
        deleteAllData=(Button)findViewById(R.id.data_delete_all);


        showSQLiteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverSmsList=sqLiteDB.getAllServerSmsData();

                Toast.makeText(context, Integer.toString(serverSmsList.size()),Toast.LENGTH_SHORT).show();


                for(int i=0;i<serverSmsList.size();++i)
                {
                    ServerSms serverSms=serverSmsList.get(i);


                    Log.e(TAG,serverSms.getSmsId()+" "+serverSms.getSmsTo()+" "+
                            serverSms.getSmsBody()+" "+serverSms.getPullTime()+" "+
                            serverSms.getSubmissionTime()+" "+serverSms.getDeliveryTime()+" "+serverSms.getSmsStatus());



                }



            }
        });


        showCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                smsReceivedFromServer=sharedPrefManager.getSmsReceivedFromServer();
                smsSentToOperator=sharedPrefManager.getSmsSentToOperator();
                smsSentSuccess=sharedPrefManager.getSmsSentSuccess();
                smsSentFailure=sharedPrefManager.getSmsSentFailure();
                smsDeliveredSuccess=sharedPrefManager.getSmsDeliveredSuccess();
                smsDeliveredFailure=sharedPrefManager.getSmsDeliveredFailure();


                Log.e(TAG, String.valueOf(smsReceivedFromServer)+" "+String.valueOf(smsSentToOperator)+" "+String.valueOf(smsSentSuccess)+" "+String.valueOf(smsSentFailure)+" "+String.valueOf(smsDeliveredSuccess)+" "+String.valueOf(smsDeliveredFailure));
            }
        });

        deleteAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sqLiteDB.deleteAllOutBoxSmsData();
                sharedPrefManager.setFcmFetchoutboxSms(true);
//                sharedPrefManager.clearSharedPrefData();
            }
        });

    }

}
