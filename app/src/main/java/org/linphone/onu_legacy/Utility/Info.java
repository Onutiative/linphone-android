//used on 6v3
package org.linphone.onu_legacy.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 1/19/2017.
 */

public class Info {

    private Context context;
    private Database db;

    public Info(Context con) {
        this.context = con;
        db = new Database(context);
    }

    public String getDate(String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void ShowToast(String ex) {
        Toast.makeText(context, ex, Toast.LENGTH_SHORT).show();
    }

    public String getImei() {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        }
        return tm.getDeviceId().toString();
    }

    public String getCheckOutgoing() {

        String chekout="full";
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
           if (cn.getName().equals("checkOut"))
            {
                Log.i("JhoroMain", "checkOut ________________"+cn.getPhone_number());
                chekout=cn.getPhone_number();
            }
        }
        return chekout;
    }

    public String getUsername()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
             if (cn.getName().equals("email")  )
            {
                uname=cn.getPhone_number();
            }
        }
        return uname;
    }

    public String postedSmsCount()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("PostedUP")  )

            {
                uname=cn.getPhone_number();

            }
        }
        return uname;
    }

    public String getUserType()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("apptype")  )

            {
                uname=cn.getPhone_number();
            }

        }
        Log.e("User Type", "User Type "+uname);
        return uname;
    }

    public String getUrl()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("Custom_url")  )
            {
                uname=cn.getPhone_number();
            }

        }
        Log.e("API URL ","The URL is "+uname);
        return uname;
    }

    public String getAboutText()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("aboutText")  )

            {
                uname=cn.getPhone_number();
            }

        }
        return uname;
    }

    public String getPassword()
    {
        List<Contact> contacts = db.getAdminNumber();
        String upass=null;
        for (Contact cn : contacts)
        {
             if (cn.getName().equals("password")  )

            {
                upass=cn.getPhone_number();
            }
        }
        return upass;
    }

    public String getCallblock()
    {
        List<Contact> contacts = db.getAdminNumber();
        String stat=null;
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("callblock")  )

            {
                stat=cn.getPhone_number();

            }

        }
        return stat;
    }

    public String getIncallCount()
    {
        List<Contact> contacts = db.getAdminNumber();
        String stat="0";
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("couuntIncall")  )

            {
                stat=cn.getPhone_number();

            }

        }
        return stat;
    }

    public String getPullCount()
    {
        List<Contact> contacts = db.getAdminNumber();
        String stat="0";
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("pullcount")  )

            {
                stat=cn.getPhone_number();

            }

        }
        return stat;
    }

    public String getOutcallCount()
    {
        List<Contact> contacts = db.getAdminNumber();
        String stat="0";
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("couuntOutcall")  )

            {
                stat=cn.getPhone_number();

            }

        }
        return stat;
    }

    public String getOutboxSentCount()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("setUP"))
            {
                Log.i("JhoroMain", "response found="+cn.getPhone_number());
                counts=cn.getPhone_number();
                //cn.getPhoneNumber().toString().equals("infoMsg")
            }

        }
        return counts;
    }

    public boolean isSmsIn()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("smsIn"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSmsOut()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("smsOut"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCallIn()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("callIn"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCallOut()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("callOut"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDeliveryReport()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("report"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRecorder()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("recorder"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isReceiver()
    {
        Database db = new Database(context);
        String counts=null;
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("receiver"))
            {
                if(cn.getPhone_number().equals("on"))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public String getImageCount()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("imgcount")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }

    public String getSmsInQuota()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname="0";
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("smsInQuota")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }

    public String getSmsOutQuota()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname="0";
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("smsOutQuota")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }

    public String getCallOutQuota()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname="0";
        for (Contact cn : contacts)
        {


            if (cn.getName().equals("callOutQuota")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }

    public String getCallInQuota()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname="0";
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("callInQuota"))
            {
                uname=cn.getPhone_number();
            }
        }
        return uname;
    }

    public String getNotifyout()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("nout")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }

    public String getNotifycall()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("ncall")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }

    public String getLastSent()
    {
        List<Contact> contacts = db.getAdminNumber();
        String uname=null;
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("response")  )

            {
                uname=cn.getPhone_number();

            }

        }
        return uname;
    }
}
