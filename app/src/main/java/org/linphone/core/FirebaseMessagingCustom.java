/*
 * Copyright (c) 2010-2022 Belledonne Communications SARL.
 *
 * This file is part of Liblinphone
 * (see https://gitlab.linphone.org/BC/public/liblinphone).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.core;

import static org.linphone.LinphoneApplication.coreContext;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Looper;

import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;
import org.linphone.core.tools.Log;
import org.linphone.core.tools.service.CoreManager;
import org.linphone.core.tools.service.AndroidDispatcher;

import java.lang.StringBuilder;
import java.util.List;
import android.os.Handler;
import java.util.Map;
import java.util.Objects;

import org.linphone.onuspecific.OnuFunctions;

/**
 * Firebase cloud messaging service implementation used to received push notification.
 */
public class FirebaseMessagingCustom extends FirebaseMessagingService {
    public void FirebaseMessaging() {
    }

    @Override
    public void onNewToken(final String token) {
        android.util.Log.i("FirebaseIdService", "[Push Notification] Refreshed token: " + token);
        if (CoreManager.isReady()) {
            CoreManager.instance().setPushToken(token);
        }
        // thread
        new Handler(Looper.getMainLooper()).post(() -> {
            android.util.Log.i("OnuFunctions", "OnuAuthentication Logging in");
            try {
                new OnuFunctions().checkSavedCredentials(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        android.util.Log.i("FirebaseMessaging", "[Push Notification] Received");
        // print the notification payload
        Log.i("[Push Notification] Received: " + remoteMessageToString(remoteMessage));
        Log.i("[Push Notification] getNotification");
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String icon = remoteMessage.getNotification().getIcon();

            Log.i("[Push Notification] Title: " + title);
            Log.i("[Push Notification] Body: " + body);
            Log.i("[Push Notification] Icon: " + icon);
        }
        Runnable pushRunnable = new Runnable() {
            @Override
            public void run() {
                onPushReceived(remoteMessage);
            }
        };
        AndroidDispatcher.dispatchOnUIThread(pushRunnable);
    }

    private void onPushReceived(RemoteMessage remoteMessage) {
        Log.d("[Push Notification] Inside onPushReceived()");

        String callId = null;
        String command = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            callId = remoteMessage.getData().getOrDefault("number", "");
            command = remoteMessage.getData().getOrDefault("info", "");
        } else {
            callId = remoteMessage.getData().get("number");
            command = remoteMessage.getData().get("info");
        }

        Log.i("[Push Notification] Received: number" + callId);
        Log.i("[Push Notification] Received: info" + command);

        if(callId != null && !callId.isEmpty() && Objects.equals(command, "makeCall")) {
            coreContext.startCall(callId);
        }

        if (!CoreManager.isReady()) {
            storePushRemoteMessage(remoteMessage);
            notifyAppPushReceivedWithoutCoreAvailable();
        } else {
            Log.i("[Push Notification] Received: " + remoteMessageToString(remoteMessage));
            if (CoreManager.instance() != null) {
                Core core = CoreManager.instance().getCore();
                if (core != null) {
                    String payload = remoteMessage.getData().toString();
                    Log.i("[Push Notification] Notifying Core we have received a push for Call-ID [" + callId + "]");
                    CoreManager.instance().processPushNotification(callId, payload, false);
                } else {
                    Log.w("[Push Notification] No Core found, notifying application directly");
                    storePushRemoteMessage(remoteMessage);
                    notifyAppPushReceivedWithoutCoreAvailable();
                }
            }
        }
    }

    private void storePushRemoteMessage(RemoteMessage remoteMessage) {
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences("push_notification_storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String callId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            callId = remoteMessage.getData().getOrDefault("call-id", "");
        }
        editor.putString("call-id", callId);
        String payload = remoteMessage.getData().toString();
        editor.putString("payload", payload);
        editor.apply();
        android.util.Log.i("FirebaseMessaging", "[Push Notification] Push information stored for Call-ID [" + callId + "]");
    }

    private void notifyAppPushReceivedWithoutCoreAvailable() {
        Intent intent = new Intent();
        intent.setAction("org.linphone.core.action.PUSH_RECEIVED");

        PackageManager pm = getPackageManager();
        List<ResolveInfo> matches = pm.queryBroadcastReceivers(intent, 0);

        for (ResolveInfo resolveInfo : matches) {
            String packageName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (packageName.equals(getPackageName())) {
                Intent explicit = new Intent(intent);
                ComponentName cn = new ComponentName(packageName, resolveInfo.activityInfo.name);
                explicit.setComponent(cn);
                sendBroadcast(explicit);
                break;
            }
        }
    }

    private String remoteMessageToString(RemoteMessage remoteMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("From [");
        builder.append(remoteMessage.getFrom());
        builder.append("], Message Id [");
        builder.append(remoteMessage.getMessageId());
        builder.append("], TTL [");
        builder.append(remoteMessage.getTtl());
        builder.append("], Original Priority [");
        builder.append(remoteMessage.getOriginalPriority());
        builder.append("], Received Priority [");
        builder.append(remoteMessage.getPriority());
        builder.append("], Sent Time [");
        builder.append(remoteMessage.getSentTime());
        builder.append("], Data [");
        builder.append(remoteMessage.getData());
        builder.append("]");
        builder.append("], Notification [");
        builder.append(remoteMessage.getNotification());
        builder.append("]");

        return builder.toString();
    }
}
