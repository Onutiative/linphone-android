package org.linphone.onu_legacy.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Rabby on 24/01/2016.
 */
public class BackgroundTaskBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RabbyBroadcast", "Service Stops! Oooooooooooooppppssssss!!!!");

        context.startService(new Intent(context, BackgroundService.class));
    }

}
