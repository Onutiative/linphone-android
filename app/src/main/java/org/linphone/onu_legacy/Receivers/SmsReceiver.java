package org.linphone.onu_legacy.Receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.linphone.LinphoneApplication;
import org.linphone.onu_legacy.AsyncTasking.IncomingSmsPoster;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.Utility.Info;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jhorotek on 7/6/2015.
 */
public class SmsReceiver extends BroadcastReceiver {


    public int count = 1;
    public int signal = 1;
    public String registration = "null";
    public String rcvdnum;
    public String rcvdsms;
    public String rcvtime;
    public String rrcvtime;
    public String uniq;
    public String url;
    public String imei;
    public String uname;
    public String upass;
    public String inboxStat;
    public String location = "null";
    public int post_sms_count;
    private Info info;
    private String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        checkInboxActive(context);
        info = new Info(context);
        if (info.isSmsIn()) {
            try {
                //set_app_url(context);
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                // imei = tm.getDeviceId();
                imei = Settings.Secure.getString(LinphoneApplication.coreContext.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            Database db = new Database(context);
            db.deleteAdmin("lastin", "jhorotek");
            db.addAdminNumber(new Contact("lastin", getDate("dd-MM-yyyy  hh:mm:ss"), "jhorotek"));

            Bundle b = intent.getExtras();
            Object[] pdu = (Object[]) b.get("pdus");
            StringBuilder sb = new StringBuilder();
            SmsMessage sms = null;

            for (int i = 0; i < pdu.length; i++) {
                sms = SmsMessage.createFromPdu((byte[]) pdu[i]);
                sb.append(sms.getMessageBody());
            }
            rcvdnum = sms.getOriginatingAddress();
            rcvdsms = sb.toString();
            //String times=getDate("dd/MM/yyyy  hh:mm:ss:SSS");
            Log.i(TAG, "SMS received-----" + inboxStat);
            long times = sms.getTimestampMillis();
            String sms_receive_time = Long.toString(times);
            rcvtime = sms_receive_time.replaceAll("\\W", "");
            if (inboxStat.equals("off")) {
                db.addsms(new Contact(rcvdsms, rcvdnum, rcvtime));
                List<Contact> contactt = db.getAdminNumber();
                for (Contact cn : contactt) {
                    if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true")) {
                        if (isNetworkAvailable(context)) {
                            new IncomingSmsPoster(context, location, imei).execute();
                        }
                    }
                }
                List<Contact> contacts = db.getAdminNumber();
                for (Contact cn : contacts) {
                    if (cn.getName().equals("switch") && cn.getPhone_number().equals("on")) {
                        Toast.makeText(context, "Delete sms on", Toast.LENGTH_LONG).show();
                        abortBroadcast();
                        Log.i(TAG, "SMS delete");
                    }
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "Error");
        }
    }

    }


    public void checkInboxActive(Context cont) {
        Database db = new Database(cont);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("inbox") ) {
                inboxStat=cn.getPhone_number();
            }
        }
    }

    public static String getDate(String dateFormat)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    //data send  in the air
    private boolean isNetworkAvailable(Context cont) {
        ConnectivityManager connectivityManager = (ConnectivityManager) cont.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void set_app_url(Context context)
            {
                Database db = new Database(context);
                List<Contact> contacts = db.getAdminNumber();
                for (Contact cn : contacts)
                {
                    if(cn.getName().equals("Custom_url"))
                    {
                        if(!cn.getPhone_number().equals(""))
                            url = cn.getPhone_number();
                        Log.i(TAG,"URL: "+url);
                    }
                    if (cn.getName().equals("PostedUP")  )

                    {
                        post_sms_count=Integer.parseInt(cn.getPhone_number());

                    }
                    if (cn.getName().equals("email")  )

                    {
                         uname = cn.getPhone_number();

                    }
                    if (cn.getName().equals("password")  )

                    {
                        upass=cn.getPhone_number();

                    }
                }

            }

}
