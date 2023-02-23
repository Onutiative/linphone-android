/*
 * Copyright (c) 2010-2021 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.telecom

import android.annotation.TargetApi
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telecom.*
import androidx.annotation.RequiresApi
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import okhttp3.OkHttpClient
import org.linphone.BasicAuthInterceptor.BasicAuthInterceptor
import org.linphone.LinphoneApplication
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.LinphoneApplication.Companion.ensureCoreExists
import org.linphone.`interface`.callpopupinterface
import org.linphone.core.*
import org.linphone.core.Call
import org.linphone.core.tools.Log
import org.linphone.onuspecific.OnuFunctions
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@TargetApi(29)
class TelecomConnectionService : ConnectionService() {
    private val listener: CoreListenerStub = object : CoreListenerStub() {
        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            Log.i("[Telecom Connection Service] call [${call.callLog.callId}] state changed: $state")
            when (call.state) {
                Call.State.OutgoingProgress -> {
                    for (connection in TelecomHelper.get().connections) {
                        if (connection.callId.isEmpty()) {
                            Log.i("[Telecom Connection Service] Updating connection with call ID: ${call.callLog.callId}")
                            connection.callId = core.currentCall?.callLog?.callId ?: ""
                        }
                    }
                }
                Call.State.Error -> onCallError(call)
                Call.State.End, Call.State.Released -> onCallEnded(call)
                Call.State.Paused, Call.State.Pausing, Call.State.PausedByRemote -> onCallPaused(call)
                Call.State.Connected, Call.State.StreamsRunning -> onCallConnected(call)
                else -> {}
            }
        }

        override fun onLastCallEnded(core: Core) {
            val connectionsCount = TelecomHelper.get().connections.size
            if (connectionsCount > 0) {
                Log.w("[Telecom Connection Service] Last call ended, there is $connectionsCount connections still alive")
                for (connection in TelecomHelper.get().connections) {
                    Log.w("[Telecom Connection Service] Destroying zombie connection ${connection.callId}")
                    connection.setDisconnected(DisconnectCause(DisconnectCause.OTHER))
                    connection.destroy()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        Log.i("[Telecom Connection Service] onCreate()")
        // thread
        Handler(Looper.getMainLooper()).post {
            android.util.Log.i("OnuFunctions", "OnuAuthentication Logging in")
            OnuFunctions().checkSavedCredentials()
        }

        ensureCoreExists(applicationContext)
        coreContext.core.addListener(listener)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (LinphoneApplication.contextExists()) {
            Log.i("[Telecom Connection Service] onUnbind()")
            coreContext.core.removeListener(listener)
        }

        return super.onUnbind(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection {
        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor("demo@vendy.xyz", "d3m0@v3n6y"))
            .build()

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.onukit.com/6v1/")
            .client(client)
            .build()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedtime = current.format(formatter)
        val trans = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val transid = current.format(trans) + "01"
        val callloginterface = retrofitBuilder.create(callpopupinterface::class.java)
        val diviceid: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (coreContext.core.callsNb == 0) {
            Log.w("[Telecom Connection Service] No call in Core, aborting outgoing connection!")
            return Connection.createCanceledConnection()
        }

        val accountHandle = request.accountHandle
        val componentName = ComponentName(applicationContext, this.javaClass)
        return if (accountHandle != null && componentName == accountHandle.componentName) {
            Log.i("[Telecom Connection Service] Creating outgoing connection")

            val extras = request.extras
            var callId = extras.getString("Call-ID")
            val displayName = extras.getString("DisplayName")
            if (callId == null) {
                callId = coreContext.core.currentCall?.callLog?.callId ?: ""
            }
            Log.i("[Telecom Connection Service] Outgoing connection is for call [$callId] with display name [$displayName]")

            // Prevents user dialing back from native dialer app history
            if (callId.isEmpty() && displayName.isNullOrEmpty()) {
                Log.e("[Telecom Connection Service] Looks like a call was made from native dialer history, aborting")
                return Connection.createFailedConnection(DisconnectCause(DisconnectCause.OTHER))
            }

            val connection = NativeCallWrapper(callId)
            val call = coreContext.core.calls.find { it.callLog.callId == callId }
            if (call != null) {
                val callState = call.state
                Log.i("[Telecom Connection Service] Found outgoing call from ID [$callId] with state [$callState]")

                // convert calltime to human readable format in Asia/Dhaka timezone like `20210331120000`
                var calltime1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault()))
                // val calltime = Date(call.callLog.startDate).toInstant().atZone(ZoneId.of("Asia/Dhaka")).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                val calltime = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault()))

                Log.i("[Telecom Connection Service] call time [$calltime] with state [$calltime1]")

                // get caller id
                val providedHandle = request.address
                val callerId = providedHandle.schemeSpecificPart.split("@")[0]
                Log.i("[Telecom Connection Service] Phone Number is $callerId")

                Thread {
                    val callDataSender = OnuFunctions.CallDataSender(
                        callerId,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault())),
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault())),
                        call?.dir.toString() ?: "outgoing",
                    )
                    callDataSender.send()
                }.start()

                when (callState) {
                    Call.State.OutgoingEarlyMedia, Call.State.OutgoingInit, Call.State.OutgoingProgress, Call.State.OutgoingRinging -> connection.setDialing()
                    Call.State.Paused, Call.State.PausedByRemote, Call.State.Pausing -> connection.setOnHold()
                    Call.State.End, Call.State.Error, Call.State.Released -> connection.setDisconnected(DisconnectCause(DisconnectCause.ERROR))
                    else -> connection.setActive()
                }
            } else {
                Log.w("[Telecom Connection Service] Outgoing call not found for cal ID [$callId], assuming it's state is dialing")
                connection.setDialing()
            }

            val providedHandle = request.address
            connection.setAddress(providedHandle, TelecomManager.PRESENTATION_ALLOWED)
            connection.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED)
            Log.i("[Telecom Connection Service] Address is $providedHandle")

//            var jsonexample = callpopupjson(callerid, formattedtime.toString(), diviceid, transid, "outgoing")
//            var callgetback = callloginterface.calllogpost(jsonexample)
//            callgetback.enqueue(object : Callback<callpopupjson> {
//                override fun onResponse(
//                    call: retrofit2.Call<callpopupjson>,
//                    response: Response<callpopupjson>
//                ) {
//                    Log.i("outgoing call popup done ${response.code()}")
//                }
//
//                override fun onFailure(call: retrofit2.Call<callpopupjson>, t: Throwable) {
//                    Log.i("outgoing call popup falied $t")
//                }
//            })

            TelecomHelper.get().connections.add(connection)
            connection
        } else {
            Log.e("[Telecom Connection Service] Error: $accountHandle $componentName")
            Connection.createFailedConnection(
                DisconnectCause(
                    DisconnectCause.ERROR,
                    "Invalid inputs: $accountHandle $componentName"
                )
            )
        }
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest
    ): Connection {

        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor("demo@vendy.xyz", "d3m0@v3n6y"))
            .build()

        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.onukit.com/6v1/")
            .client(client)
            .build()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedtime = current.format(formatter)
        val trans = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val transid = current.format(trans) + "01"
        val callloginterface = retrofitBuilder.create(callpopupinterface::class.java)
        val diviceid: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        if (coreContext.core.callsNb == 0) {
            Log.w("[Telecom Connection Service] No call in Core, aborting incoming connection!")
            return Connection.createCanceledConnection()
        }

        val accountHandle = request.accountHandle
        val componentName = ComponentName(applicationContext, this.javaClass)
        return if (accountHandle != null && componentName == accountHandle.componentName) {
            Log.i("[Telecom Connection Service] Creating incoming connection")

            val extras = request.extras
            val incomingExtras = extras.getBundle(TelecomManager.EXTRA_INCOMING_CALL_EXTRAS)
            var callId = incomingExtras?.getString("Call-ID")
            val displayName = incomingExtras?.getString("DisplayName")
            if (callId == null) {
                callId = coreContext.core.currentCall?.callLog?.callId ?: ""
            }
            Log.i("[Telecom Connection Service] Incoming connection is for call [$callId] with display name [$displayName]")

            val connection = NativeCallWrapper(callId)
            val call = coreContext.core.calls.find { it.callLog.callId == callId }
            if (call != null) {
                val callState = call.state
                Log.i("[Telecom Connection Service] Found incoming call from ID [$callId] with state [$callState]")

                val callerId = call.remoteAddress.asStringUriOnly().split("@")[0].split(":")[1]
                Log.i("[Telecom Connection Service] Phone Number is $callerId")

//                var jsonexample = callpopupjson("$callerId", formattedtime.toString(), diviceid, transid, "incoming")
//                var callgetback = callloginterface.calllogpost(jsonexample)
//                callgetback.enqueue(object : Callback<callpopupjson> {
//                    override fun onResponse(
//                        call: retrofit2.Call<callpopupjson>,
//                        response: Response<callpopupjson>
//                    ) {
//                        Log.i("incoming call popup done ${response.code()}")
//                    }
//
//                    override fun onFailure(call: retrofit2.Call<callpopupjson>, t: Throwable) {
//                        Log.i("incoming call popup falied $t")
//                    }
//                })

                Thread {
                    val callDataSender = OnuFunctions.CallDataSender(
                        callerId,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault())),
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault())),
                        call.dir.toString() ?: "incoming",
                    )
                    callDataSender.send()
                }.start()

                when (callState) {
                    Call.State.IncomingEarlyMedia, Call.State.IncomingReceived -> connection.setRinging()
                    Call.State.Paused, Call.State.PausedByRemote, Call.State.Pausing -> connection.setOnHold()
                    Call.State.End, Call.State.Error, Call.State.Released -> connection.setDisconnected(DisconnectCause(DisconnectCause.ERROR))
                    else -> connection.setActive()
                }
            } else {
                Log.w("[Telecom Connection Service] Incoming call not found for cal ID [$callId], assuming it's state is ringing")
                connection.setRinging()
            }

            val providedHandle =
                incomingExtras?.getParcelable<Uri>(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS)
            connection.setAddress(providedHandle, TelecomManager.PRESENTATION_ALLOWED)
            connection.setCallerDisplayName(displayName, TelecomManager.PRESENTATION_ALLOWED)
            Log.i("[Telecom Connection Service] Address is $providedHandle")

            TelecomHelper.get().connections.add(connection)
            connection
        } else {
            Log.e("[Telecom Connection Service] Error: $accountHandle $componentName")
            Connection.createFailedConnection(
                DisconnectCause(
                    DisconnectCause.ERROR,
                    "Invalid inputs: $accountHandle $componentName"
                )
            )
        }
    }

    private fun onCallError(call: Call) {
        val callId = call.callLog.callId
        val connection = TelecomHelper.get().findConnectionForCallId(callId.orEmpty())
        if (connection == null) {
            Log.e("[Telecom Connection Service] Failed to find connection for call id: $callId")
            return
        }

        TelecomHelper.get().connections.remove(connection)
        Log.i("[Telecom Connection Service] Call [$callId] is in error, destroying connection currently in ${connection.stateAsString()}")
        connection.setDisconnected(DisconnectCause(DisconnectCause.ERROR))
        connection.destroy()
    }

    private fun onCallEnded(call: Call) {
        val callId = call.callLog.callId
        val connection = TelecomHelper.get().findConnectionForCallId(callId.orEmpty())
        if (connection == null) {
            Log.e("[Telecom Connection Service] Failed to find connection for call id: $callId")
            return
        }

        val params = coreContext.core.createCallParams(call)
        // params?.recordFile = LinphoneUtils.getRecordingFilePathForAddress(call.remoteAddress) ?: ""
        Log.i("[Telecom Connection Service] [OnuFunctions] recording path ${params?.recordFile}  ${call.core.recordFile}")
        // rename the file extension from .mkv to .amr
        var recordingFilePath = File(params?.recordFile?.replace(".mkv", ".amr")).absolutePath
        Log.i("[Telecom Connection Service] [OnuFunctions] recording path $recordingFilePath")
        File(params?.recordFile).renameTo(File(recordingFilePath)).toString()
        Log.i("[Telecom Connection Service] [OnuFunctions] recording path $recordingFilePath")

        // get the call start time from call object
        val callStartTime = call.callLog.startDate
        Log.i("[Telecom Connection Service] [OnuFunctions] callStartTime $callStartTime")

        // replace `/storage/emulated/0/` with `/sdcard/` in recordingFilePath
        // recordingFilePath = recordingFilePath.replace("/storage/emulated/0/", "/sdcard/")

        Log.i("[Telecom Connection Service] [OnuFunctions] recordingFilePath $recordingFilePath")
        Thread {
            // sleep for 5 second to make sure the recording file is ready
            Thread.sleep(5000)
            // check if recording file exists
            if (!File(recordingFilePath).exists()) {
                Log.e("[OnuFunctions] Recording file does not exist")
                return@Thread
            }

            try {
                // get the trxId from recordingFile name in like `2023-02-01 23:19:28` to `20230201231928` format
                val trxId = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSS").format(Instant.ofEpochMilli(call.callLog.startDate * 1000L).atZone(ZoneId.systemDefault()))
                val file = File(recordingFilePath)
                val callType = call.callLog.dir.toString()
                val callRecordService = OnuFunctions.CallRecordSender()
                Log.i("[OnuFunctions] Call record request: $trxId, $file, $callType")
                val response = callRecordService.send(
                    trxId,
                    file,
                    callType,
                )
                println(response)
                Log.d("[OnuFunctions] Call record response: $response")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("[OnuFunctions] Call record error: $e")
            }
        }.start()

        TelecomHelper.get().connections.remove(connection)
        val reason = call.reason
        Log.i("[Telecom Connection Service] Call [$callId] ended with reason: $reason, destroying connection currently in ${connection.stateAsString()}")
        connection.setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        connection.destroy()
    }

    private fun onCallPaused(call: Call) {
        val callId = call.callLog.callId
        val connection = TelecomHelper.get().findConnectionForCallId(callId.orEmpty())
        if (connection == null) {
            Log.e("[Telecom Connection Service] Failed to find connection for call id: $callId")
            return
        }
        Log.i("[Telecom Connection Service] Setting connection as on hold, currently in ${connection.stateAsString()}")
        connection.setOnHold()
    }

    private fun onCallConnected(call: Call) {
        val callId = call.callLog.callId
        val connection = TelecomHelper.get().findConnectionForCallId(callId.orEmpty())
        if (connection == null) {
            Log.e("[Telecom Connection Service] Failed to find connection for call id: $callId")
            return
        }

        Log.i("[Telecom Connection Service] Setting connection as active, currently in ${connection.stateAsString()}")
        connection.setActive()
    }
}
