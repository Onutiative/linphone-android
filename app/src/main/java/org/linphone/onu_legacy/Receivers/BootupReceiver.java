package org.linphone.onu_legacy.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;

import java.util.List;

/**
 * Created by jhorotek on 7/7/2015.
 */


public class BootupReceiver extends BroadcastReceiver {







    @Override
    public void onReceive(Context context, Intent intent) {




        Database db = new Database(context);


        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {

            if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true") )

            {

                Intent i = new Intent(context, DashBoard_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }



        }






    }
}
