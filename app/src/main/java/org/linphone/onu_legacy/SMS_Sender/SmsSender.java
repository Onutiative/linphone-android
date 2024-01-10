package org.linphone.onu_legacy.SMS_Sender;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;


import org.linphone.onu_legacy.AsyncTasking.SubmitSmsReport;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.ServerSms;
import org.linphone.onu_legacy.Utility.Constants;
import org.linphone.onu_legacy.Utility.Helper;
import org.linphone.onu_legacy.Utility.IntentStatus;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SmsSender {

    private Context context;
    private Database sqLiteDB;
    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
    ArrayList<ServerSms> serverSmsList;
    private int count, deliveryCounter ;
    private SharedPrefManager sharedPrefManager;
    private boolean flag;
    private int sendCounter =0;
    private int numberOfSms;
    private Helper helper;

    private static final String TAG="SmsSender";

    public SmsSender(Context context) {
        this.context = context;
        sqLiteDB=new Database(context);
        sharedPrefManager=new SharedPrefManager(context);
        helper=new Helper(context);
    }

    public void startSendingSms()
    {
        count=0;
        deliveryCounter=0;
//        Constant.counter=0;
        Log.e(TAG,"Count1 "+count);
        flag=true;
        serverSmsList=sqLiteDB.getProcessedServerSmsData();
        numberOfSms=sqLiteDB.getCountProcessedSms();


       // Toast.makeText(context,"sms sending",Toast.LENGTH_LONG).show();
//_________________________***test***______________
//_________________________***test***______________        // sqLiteDB.copySmsToTemporary(serverSmsList);

        //Toast.makeText(context,"Outbox sms   " +Integer.toString(serverSmsList.size()),Toast.LENGTH_SHORT).show();


        //for testing purpose only
        for(int i=0;i<serverSmsList.size();++i)
        {
            ServerSms serverSms=serverSmsList.get(i);
            Log.i(TAG,"SMS DATA "+i+": "+serverSms.getSmsId()+" "+serverSms.getSmsTo()+" "+
                    serverSms.getSmsBody()+" "+serverSms.getPullTime()+" "+
                    serverSms.getSubmissionTime()+" "+serverSms.getDeliveryTime()+" "+serverSms.getSmsStatus());
        }
        sendSmsWithDeliveryReport();
    }

//    private String getTimeStamp(String dateFormat)
//    {
//        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
//        Calendar calendar = Calendar.getInstance();
//        return formatter.format(calendar.getTime());
//    }

    //______________________________****test****__________________________


    public void sendSmsWithDeliveryReport()
    {
        //for receiving sms sent report
        smsSentReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context arg0, Intent intent)
            {
                String smsId=intent.getStringExtra("smsId");
                String smsTo=intent.getStringExtra("smsTo");
                String smsBody=intent.getStringExtra("smsBody");
                String pullTime=intent.getStringExtra("pullTime");
                String submissionTime=intent.getStringExtra("submissionTime");
                String deliveryTime=helper.getTime();
                Log.i(TAG,"Delivery time: "+deliveryTime);

                ServerSms sms=new ServerSms(smsId,smsTo,smsBody,pullTime,helper.getTime(),deliveryTime,Constants.PENDING);

                sqLiteDB.updateSmsSubmissionTime(smsId,helper.getTime());

                // It sends the Server Sms data to SentSms table.

                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.i(TAG,"RESULT_OK: "+smsId+"; "+smsTo);
                        //sqLiteDB.addNewSentSms(sms);
                        // sqLiteDB.deleteTemporarySmsBySmsID(sms.getSmsId());
                        sqLiteDB.updateSmsStatus(smsId, Constants.SENT_RESULT_OK);
                        sharedPrefManager.setSmsSentSuccess(1);

                        //BDN
                        IntentStatus statusOk = new IntentStatus(smsId,Constants.DELIVERY_RESULT_OK,helper.getTime());
                        sqLiteDB.insertIntentStatus(statusOk);
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        sqLiteDB.updateSmsStatus(smsId, Constants.SENT_RESULT_ERROR_GENERIC_FAILURE);
                        sharedPrefManager.setSmsSentFailure(1);
                        Log.i(TAG,"Generic failure "+smsId);

                        //BDN
                        IntentStatus statusFailure = new IntentStatus(smsId,Constants.SENT_RESULT_ERROR_GENERIC_FAILURE,helper.getTime());
                        sqLiteDB.insertIntentStatus(statusFailure);
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        sqLiteDB.updateSmsStatus(smsId, Constants.SENT_RESULT_ERROR_NO_SERVICE);
                        sharedPrefManager.setSmsSentFailure(1);
                        Log.i(TAG,"No service "+smsId);

                        //BDN
                        IntentStatus statusNoService = new IntentStatus(smsId,Constants.SENT_RESULT_ERROR_NO_SERVICE,helper.getTime());
                        sqLiteDB.insertIntentStatus(statusNoService);
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        sqLiteDB.updateSmsStatus(smsId, Constants.SENT_RESULT_ERROR_NULL_PDU);
                        sharedPrefManager.setSmsSentFailure(1);
                        Log.i(TAG,"Null PDU "+smsId);

                        //BDN
                        IntentStatus statusNullPDU = new IntentStatus(smsId,Constants.SENT_RESULT_ERROR_NULL_PDU,helper.getTime());
                        sqLiteDB.insertIntentStatus(statusNullPDU);
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        sqLiteDB.updateSmsStatus(smsId, Constants.SENT_RESULT_ERROR_RADIO_OFF);
                        sharedPrefManager.setSmsSentFailure(1);
                        Log.i(TAG,"Radio off "+smsId);

                        //BDN
                        IntentStatus statusRadioOff = new IntentStatus(smsId,Constants.SENT_RESULT_ERROR_RADIO_OFF,helper.getTime());
                        sqLiteDB.insertIntentStatus(statusRadioOff);;
                        break;
                }
                count++;
                if(count==sqLiteDB.notprocessedsms())
                {
                    //new SubmitSmsReport(context,"Sequential Phase").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    count=0;
                }

            }
        };//end of broadcast receiver for sent

        context.registerReceiver(smsSentReceiver,new IntentFilter(SENT));
        context.registerReceiver(smsDeliveredReceiver,new IntentFilter(DELIVERED));

        ArrayList<ServerSms> processedSmsList=sqLiteDB.getProcessedServerSmsData();

        for(int i=0;i<processedSmsList.size();i++)
        {
            sendSms(processedSmsList.get(i));
        }

        try {
            Thread.sleep(1000);
            // context.unregisterReceiver(smsSentReceiver);
            if(!sqLiteDB.isemptyprocessed())
            {
                processedSmsList=sqLiteDB.getProcessedServerSmsData();

                for(int i=0;i<processedSmsList.size();i++)
                {
                    sendSms(processedSmsList.get(i));
                }
            }
            else
            {
                context.unregisterReceiver(smsSentReceiver);
            }
        }
        catch(InterruptedException e)
        {}
    }

    public void sendSms(ServerSms sms)
    {

        Log.e(TAG,"Count in Send SMS"+count);
        Log.e(TAG,"Send SMS Executing!");

        Log.e(TAG,sms.getSmsId()+" "+sms.getSmsTo()+" "+sms.getSmsBody());

        SmsManager smsManager = SmsManager.getDefault();

        ArrayList<String> smsParts =smsManager.divideMessage(sms.getSmsBody());
        int numParts = smsParts.size();

        Intent sentIntent = new Intent(SENT);
        Intent deliveredIntent = new Intent(DELIVERED);

        sentIntent.putExtra("smsId",sms.getSmsId());
        sentIntent.putExtra("smsTo",sms.getSmsTo());
        sentIntent.putExtra("smsBody",sms.getSmsBody());
        sentIntent.putExtra("pullTime",sms.getPullTime());
        sentIntent.putExtra("submissionTime",sms.getSubmissionTime());
        sentIntent.putExtra("deliveryTime",sms.getDeliveryTime());
        deliveredIntent.putExtra("smsId",sms.getSmsId());

        ServerSms serverSms=new ServerSms(sms.getSmsId(),sms.getSmsTo(),sms.getSmsBody(),sms.getPullTime(),helper.getTime(),sms.getDeliveryTime(),Constants.PENDING);

        sentPI = PendingIntent.getBroadcast(context, Integer.parseInt(sms.getSmsId()), new Intent(sentIntent), PendingIntent.FLAG_IMMUTABLE);
        deliveredPI = PendingIntent.getBroadcast(context, Integer.parseInt(sms.getSmsId()) + 100, new Intent(deliveredIntent), PendingIntent.FLAG_IMMUTABLE);

        ArrayList<PendingIntent> sentPIs = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPIs = new ArrayList<PendingIntent>();

        //for(int i=0;i<numParts;++i)
        {
            sentPIs.add(sentPI);
            deliveredPIs.add(deliveredPI);
        }
        try {
            Log.i(TAG,"Number: "+sms.getSmsTo()+"; Text: "+smsParts+"; Status: "+sms.getSmsStatus());

            if (sms.getSmsStatus()==Constants.PROCESSED)
            {//only send work for pending
                Log.i(TAG,"Sending Process on Number: "+sms.getSmsTo()+"; Text: "+smsParts+"; Status: "+sms.getSmsStatus());
                smsManager.sendMultipartTextMessage(sms.getSmsTo(), null, smsParts, sentPIs, deliveredPIs);
                sqLiteDB.addNewSentSms(serverSms);//add to sent table
                sqLiteDB.updateSmsDeliveryTime(sms.getSmsId(),helper.getTime());
                sqLiteDB.updateSmsStatus(sms.getSmsId(),Constants.SMS_SUBMMIT);//changed pending to processed

                sharedPrefManager.setSmsSentToOperator(1);
                sendCounter++;
                Log.i(TAG,"Send counter: "+sendCounter+"="+numberOfSms+"of NP "+sqLiteDB.notprocessedsms());
                if(sendCounter==numberOfSms)
                {
                    Log.i(TAG,"Submit report called");
                    new SubmitSmsReport(context,"Sequential Phase").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    sendCounter=0;
                }
            }
        } catch (Exception e) {
            Log.i(TAG,e.toString());

        }

    }
//    public void composeMmsMessage(String message, Uri attachment) {
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setData(Uri.parse("smsto:"));  // This ensures only SMS apps respond
//        intent.putExtra("sms_body", message);
//        intent.putExtra(Intent.EXTRA_STREAM, attachment);
//        if (intent.resolveActivity(context.getPackageManager()) != null) {
//            context.startActivity(intent);
//        }
//    }


}