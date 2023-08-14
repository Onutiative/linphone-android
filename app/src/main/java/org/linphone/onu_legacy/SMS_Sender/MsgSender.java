package org.linphone.onu_legacy.SMS_Sender;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.linphone.onu_legacy.AsyncTasking.PullSmsFromServer;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Database.Sms;
import org.linphone.onu_legacy.Utility.Info;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Rabby on 2/7/2016.
 */
public class MsgSender extends AsyncTask<Void, Void, String> {
    int TIMEOUT_MILLISEC = 5000;
    Context context;
    public String imei;
    private BroadcastReceiver receiver;
    private BroadcastReceiver dreceiver;
    public String status_sms;
    public int datavalue = 0;
    public boolean smssend = true;
    public String my_msg;
    public String my_time;
    public String reportStat=null;
    public int sent_sms_count;
    public int que = 0;
    private Info info;
    private int sizeOfList;
    private int increment;
    Database db;

    private static final String TAG=MsgSender.class.getSimpleName();


    private Disposable timer;



    //info sms from
    public MsgSender(Context context, String imei, int sent_sms_count) {
        this.context = context;
        this.imei = imei;
        this.sent_sms_count = sent_sms_count;
        this.info=new Info(context);

//info of the
    }

    @Override
    protected void onPreExecute() {
        checkReport();


    }

    @Override
    protected void onPostExecute(String result) {
        Log.i("Jhoro", " debug -15");

        //doing background is the tasks that take-place in the under ground layer
    }

    @Override
    protected String doInBackground(Void... params) {

         db = new Database(context);
        if (db.getOutboxCounttow() > 0 && db.getOutboxCount() < 10) {
            Log.i("Jhoroent", " torrrrrrrrrrr 1");
            int countSms = 0;
            List<Sms> contacts = db.getAllOutboxtow();
            for (Sms cn : contacts) {

                String m_number = cn.getPhoneNumber();
                String m_status = cn.getStatus();
                final String set_msg = cn.getName();
                final String set_number = m_number;
                final String set_id = cn.getTime();
                final String transactionId = cn.getType();
                if (db.getOutboxCount() < 10) {
                    Log.i("Jhoroent", " torrrrrrrrrrr 3");
                    if (db.checkSmsId(set_id) < 1) {
                        db.addOutbox(new Sms(set_msg, m_number, set_id, transactionId, "pending"));
                }
                } else {
                    break;
                }
            }
        }
        if (db.getOutboxCount() > 0 ) {
            smssend = false;
            Log.i("Jhoroent", " debug -1");
            int count = 1;
            final List<Sms> contacts = db.getAllOutbox();
            //for (int i = 0; i < contacts.size(); i++)



            for (Sms cn : contacts)
            {
                String m_number = cn.getPhoneNumber();
                String m_status = cn.getStatus();
                final String set_msg = cn.getName();
                final String set_number = m_number;
                final String set_id = cn.getTime();
                final String number = cn.getType();
                        //incrimental cpy
                       final String inpolodong ="incremental implementation";
                        //incrimental cpy end
                Log.i("Jhoroent", " debug -2 "+m_status);
                //c
                Log.i("Jhoroent", m_number);
                if (m_status.equals("pending") && !m_number.isEmpty()) {

                    Log.i("Jhoroent", "send_sms" + set_id);
                    if(reportStat.equals("on")) {
                        sendSMS(set_number, set_msg, set_id, number); //without report
                    }
                    else
                    {
                        sendSMSreport(set_number, set_msg, set_id, number); //with report
                    }

                }
                else
                {
                    Log.i("Jhoroent", "deleted msg");

                    db.super_shot(check_sent_count(),set_id );
                }

            }

//            sizeOfList= contacts.size();
//            increment=0;
//
//
//
//            timer = Observable.interval(1000L, TimeUnit.MILLISECONDS)
//                    .timeInterval()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<Timed<Long>>() {
//                        @Override
//                        public void accept(@NonNull Timed<Long> longTimed) throws Exception {
//
//                            if(increment==(sizeOfList-1))
//                            {
//                                timer.dispose();
//                            }
//                            else
//                            {
//                                Sms cn=contacts.get(increment);
//                                ++increment;
//
//
//                                String m_number = cn.getPhone_number();
//                                String m_status = cn.getStatus();
//                                final String set_msg = cn.getName();
//                                final String set_number = m_number;
//                                final String set_id = cn.getTime();
//                                final String number = cn.getType();
//                                //incrimental cpy
//                                final String inpolodong ="incremental implementation";
//                                //incrimental cpy end
//                                Log.i("Jhoroent", " debug -2 "+m_status);
//                                //c
//                                Log.i("Jhoroent", m_number);
//                                if (m_status.equals("pending") && !m_number.isEmpty()) {
//
//                                    Log.i("Jhoroent", "send_sms" + set_id);
//                                    if(reportStat.equals("on")) {
//                                        sendSMS(set_number, set_msg, set_id, number);
//                                    }
//                                    else
//                                    {
//                                        sendSMSreport(set_number, set_msg, set_id, number);
//                                    }
//
//                                }
//                                else
//                                {
//                                    Log.i("Jhoroent", "deleted msg");
//
//                                    db.super_shot(check_sent_count(),set_id );
//                                }
//
//
//                            }
//
//
//
//
//                        }
//                    });










            smssend = true;
        } else {
            CallPull();
        }


        return null;
    }


    private void CallPull() {
        new PullSmsFromServer(context, imei).execute();
    }



    public static String getDate(String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    private void sendSMS(final String phoneNumber, final String message, final String time, final String data) {

        Log.i("Jhoro", " debug -5__" + time);
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        Intent sents = new Intent(SENT);
        sents.putExtra("smsID", time);

        Intent delivers = new Intent(DELIVERED);
        delivers.putExtra("smsID", time);
        Log.i("Jhoro", " debug -5.5______________________________________" + Integer.parseInt(data));
        Database db = new Database(context.getApplicationContext());
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        int mMessageSentTotalParts = parts.size();

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(context, Integer.parseInt(data), new Intent(sents), PendingIntent.FLAG_IMMUTABLE);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, Integer.parseInt(data) + 100, new Intent(delivers), PendingIntent.FLAG_IMMUTABLE);

        for (int j = 0; j < mMessageSentTotalParts; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }
        db.UpdateOutboxProcessed(time);
        my_msg = message;
        my_time = time;
        String saved_id = "0";
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
        db.super_shot(check_sent_count(),my_time );
        if(db.getSentCount() > 100)
        {
            db.delSentOne();
            db.addSent(new Contact(my_msg,phoneNumber,getDate("yyyy-MM-dd hh:mm:ss")));    // chnaged from kk to hh

        }
        else
        {
            db.addSent(new Contact(my_msg,phoneNumber,getDate("yyyy-MM-dd hh:mm:ss")));
        }
        Log.i("Jhoro", " debug -5.6_____________________________" + phoneNumber + "_________" + message);

    }

    private void sendSMSreport(final String phoneNumber, final String message, final String time, final String data) {


        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        Intent sents = new Intent(SENT);
        Intent delivers = new Intent(DELIVERED);
        Log.i("Jhoro", " debug -5__" + time);
        sents.putExtra("smsID", time);
        delivers.putExtra("smsID", time);
        Log.i("Jhoro", " debug -5__" + time);
        Log.i("Jhoro", " debug -5.5______________________________________" + Integer.parseInt(data));
        Database db = new Database(context.getApplicationContext());
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);
        int mMessageSentTotalParts = parts.size();

        ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();

        PendingIntent sentPI = PendingIntent.getBroadcast(context, Integer.parseInt(data), new Intent(sents), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, Integer.parseInt(data) + 100, new Intent(delivers), 0);


        for (int j = 0; j < mMessageSentTotalParts; j++) {
            sentIntents.add(sentPI);
            deliveryIntents.add(deliveredPI);
        }


        db.UpdateOutboxProcessed(time);
        my_msg = message;
        my_time = time;
        String saved_id = "0";
        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
        db.super_shot(check_sent_count(),my_time);
        if(db.getSentCount() > 100)
        {
            db.delSentOne();
            db.addSent(new Contact(my_msg,phoneNumber,getDate("yyyy-MM-dd kk:mm:ss")));

        }
        else
        {
            db.addSent(new Contact(my_msg,phoneNumber,getDate("yyyy-MM-dd kk:mm:ss")));
        }

        Log.i("Jhoro", " debug -5.6_____________________________" + phoneNumber + "_________" + message);
        if(info.isReceiver()) {
            try {
                db.deleteAdmin("receiver", "jhorotek");
                db.addAdminNumber(new Contact("receiver","off", "jhorotek"));
                context.getApplicationContext().registerReceiver(receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent arg) {
                        Database db = new Database(context.getApplicationContext());
                        Log.i("JhoroSmSReport", " SENT:" + arg.getStringExtra("smsID"));
                        switch (getResultCode()) {

                            case Activity.RESULT_OK:
                                if (db.AdminSmsId(arg.getStringExtra("smsID")) < 1) {
                                    //db.super_shot(check_sent_count(), arg.getStringExtra("smsID"));
                                    db.updateSmsId(arg.getStringExtra("smsID"));
                                    db.addthread(new Contact(arg.getStringExtra("smsID"), getDate("yyyy-MM-dd kk:mm:ss"), "s"));
                                }
                                break;

                            case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Log.i("JhoroSmSReport", "RESULT_ERROR_GENERIC_FAILURE");
                                if (db.AdminSmsId(arg.getStringExtra("smsID")) < 1) {
                                    //db.super_shot(check_sent_count(), arg.getStringExtra("smsID"));

                                    db.updateSmsId(arg.getStringExtra("smsID"));
                                    //db.updateSent(Integer.toString(check_sent_count() - 2));
                                    db.addthread(new Contact(arg.getStringExtra("smsID"), getDate("yyyy-MM-dd kk:mm:ss"), "e"));
                                }
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:

                                Log.i("JhoroSmSReport", "RESULT_ERROR_NO_SERVICE");
                                if (db.AdminSmsId(arg.getStringExtra("smsID")) < 1) {
                                    //db.super_shot(check_sent_count(), arg.getStringExtra("smsID"));

                                    db.updateSmsId(arg.getStringExtra("smsID"));
                                    //db.updateSent(Integer.toString(check_sent_count() - 2));
                                    db.addthread(new Contact(arg.getStringExtra("smsID"), getDate("yyyy-MM-dd kk:mm:ss"), "e"));
                                }
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:

                                Log.i("JhoroSmSReport", "RESULT_ERROR_NULL_PDU");
                                if (db.AdminSmsId(arg.getStringExtra("smsID")) < 1) {

                                    //db.super_shot(check_sent_count(), arg.getStringExtra("smsID"));
                                    db.updateSmsId(arg.getStringExtra("smsID"));
                                    //db.updateSent(Integer.toString(check_sent_count() - 2));
                                    db.addthread(new Contact(arg.getStringExtra("smsID"), getDate("yyyy-MM-dd kk:mm:ss"), "e"));
                                }
                                break;
                            case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
                                Log.i("JhoroSmSReport", "RESULT_ERROR_RADIO_OFF");
                                if (db.AdminSmsId(arg.getStringExtra("smsID")) < 1) {
                                    //db.super_shot(check_sent_count(), arg.getStringExtra("smsID"));
                                    db.updateSmsId(arg.getStringExtra("smsID"));
                                   // db.updateSent(Integer.toString(check_sent_count() - 2));
                                    db.addthread(new Contact(arg.getStringExtra("smsID"), getDate("yyyy-MM-dd kk:mm:ss"), "e"));
                                }

                                break;
                        }

                    }
                }, new IntentFilter(SENT));
                context.getApplicationContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i("JhoroSmSReport", " DELIVERED:" + intent.getStringExtra("smsID"));
                        Database dsb = new Database(context.getApplicationContext());
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                if (dsb.AdminDeliverySmsId(intent.getStringExtra("smsID")) < 1) {
                                    dsb.updateDeliveredSmsId(intent.getStringExtra("smsID"));
                                    // new DeliveryReport(context, imei, intent.getStringExtra("smsID")).execute();
                                    dsb.deleteOutboxtow(intent.getStringExtra("smsID"));

                                    Log.i("JhoroSmSReport", " DELIVEREDS:" + intent.getStringExtra("smsID"));
                                    dsb.addthread(new Contact(intent.getStringExtra("smsID"), getDate("yyyy-MM-dd kk:mm:ss"), "d"));


                                }

                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(context, "sms_not_delivered",
                                        Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                }, new IntentFilter(DELIVERED));
            } catch (Exception ex) {
                ex.printStackTrace();
                datavalue = 0;


            }
        }


    }

    public int check_sent_count() {
        Database db = new Database(context);
        int sent_sms_counts = 0;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("setUP")) {
                Log.i("Jhoro", "response found=" + cn.getPhone_number());
                sent_sms_counts = Integer.parseInt(cn.getPhone_number()) + 1;
            }
        }
        return sent_sms_counts;
    }

    public void checkReport() {


        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {

            if (cn.getName().equals("report") )
            {
                reportStat=cn.getPhone_number();

            }

        }

    }
}
