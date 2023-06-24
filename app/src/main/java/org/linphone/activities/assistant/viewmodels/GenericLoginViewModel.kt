/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
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
package org.linphone.activities.assistant.viewmodels

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.processphoenix.ProcessPhoenix
import java.util.logging.Handler
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.activities.main.MainActivity
import org.linphone.core.*
import org.linphone.onuspecific.OnuFunctions
import org.linphone.utils.Event

class GenericLoginViewModelFactory(private val accountCreator: AccountCreator) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GenericLoginViewModel(accountCreator) as T
    }
}

class GenericLoginViewModel(private val accountCreator: AccountCreator) : ViewModel() {

    val onukit_username = MutableLiveData<String>()

    val onukit_password = MutableLiveData<String>()

    val onukit_logged_in = false

    val username = MutableLiveData<String>()

    val password = MutableLiveData<String>()

    val domain = MutableLiveData<String>()

    val displayName = MutableLiveData<String>()

    val transport = MutableLiveData<TransportType>()

    val loginEnabled: MediatorLiveData<Boolean> = MediatorLiveData()

    val onuLoginEnabled: MediatorLiveData<Boolean> = MediatorLiveData()

    val waitForServerAnswer = MutableLiveData<Boolean>()

    val leaveAssistantEvent = MutableLiveData<Event<Boolean>>()

    val invalidCredentialsEvent: MutableLiveData<Event<Boolean>> by lazy {
        MutableLiveData<Event<Boolean>>()
    }

    val onErrorEvent: MutableLiveData<Event<String>> by lazy {
        MutableLiveData<Event<String>>()
    }

    private var proxyConfigToCheck: ProxyConfig? = null

    private val coreListener = object : CoreListenerStub() {
        @Deprecated("Deprecated in Java")
        override fun onRegistrationStateChanged(
            core: Core,
            cfg: ProxyConfig,
            state: RegistrationState,
            message: String
        ) {
            if (cfg == proxyConfigToCheck) {
                Log.i("[Assistant]", "[Assistant] [Generic Login] Registration state is $state: $message")
                if (state == RegistrationState.Ok) {
                    waitForServerAnswer.value = false
                    leaveAssistantEvent.value = Event(true)
                    core.removeListener(this)
                } else if (state == RegistrationState.Failed) {
                    waitForServerAnswer.value = false
                    invalidCredentialsEvent.value = Event(true)
                    core.removeListener(this)
                }
            }
        }
    }

    init {
        transport.value = TransportType.Udp

        loginEnabled.value = false
        loginEnabled.addSource(username) {
            loginEnabled.value = isLoginButtonEnabled()
        }
        loginEnabled.addSource(password) {
            loginEnabled.value = isLoginButtonEnabled()
        }
        loginEnabled.addSource(domain) {
            loginEnabled.value = isLoginButtonEnabled()
        }

//        onuLoginEnabled.value = false
//        onuLoginEnabled.addSource(onukit_username) {
//            onuLoginEnabled.value = isOnuLoginButtonEnabled()
//        }
//        onuLoginEnabled.addSource(onukit_password) {
//            onuLoginEnabled.value = isOnuLoginButtonEnabled()
//        }
    }

    fun setTransport(transportType: TransportType) {
        transport.value = transportType
    }

    fun removeInvalidProxyConfig() {
        val cfg = proxyConfigToCheck
        cfg ?: return
        val authInfo = cfg.findAuthInfo()
        if (authInfo != null) coreContext.core.removeAuthInfo(authInfo)
        coreContext.core.removeProxyConfig(cfg)
        proxyConfigToCheck = null
    }

    fun continueEvenIfInvalidCredentials() {
        leaveAssistantEvent.value = Event(true)
    }

//    fun checkOnukitCredentials() {
//        waitForServerAnswer.value = true
//        val userActivation = UserActivation(
//            onukit_username?.value, onukit_password?.value
//        )
//
//        val request = userActivation.performActivation()
//        val client = OkHttpClient()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                // Handle failure
//                Log.d("OnuFunctions", "onFailure - Failed to activate user: $e")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                val requestBody = response.body
//
//                // log status code
//                Log.d("OnuFunctions", "Response code: ${response.code}")
//
//                // check status code
//                if (response.code != 200) {
//                    // Show a toast message
//                    Log.d("OnuFunctions", "response.code != 200 | Failed to activate user")
//                    Toast.makeText(coreContext.context, "Server error! ${response.code}", Toast.LENGTH_SHORT).show()
//                }
//
//                // Handle response
//                if (response.isSuccessful) {
//                    Handler(Looper.getMainLooper()).post {
//                        // check when client is done, then set waitForServerAnswer's value to false
//                        waitForServerAnswer.value = false
//                    }
//
//                    try {
//                        // load the json data
//                        val json = JSONObject(requestBody?.string())
//
//                        // [REMEMBER] - Leave this commented out
//                        // https://stackoverflow.com/a/40709867
//                        // "you can only receive the body string once"
//                        // Log.d("OnuFunctions", "Response body: ${requestBody.string()}")
//
//                        // get the status and reason from json data
//                        val status = json.getString("status")
//                        val reason = json.getString("reason")
//
//                        Log.d("OnuFunctions", "status: $status")
//                        Log.d("OnuFunctions", "reason: $reason")
//
//                        // show in a toast message
//                        if (status.toInt() > 4000) {
//                            Handler(Looper.getMainLooper()).post {
//                                Toast.makeText(coreContext.context, reason, Toast.LENGTH_SHORT).show()
//                            }
//                            return
//                        } else {
//                            // The request was successful
//                            // Log.d("OnuFunctions", "User activated successfully")
//
//                            val sharedPreferences = coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
//                            val editor = sharedPreferences.edit()
//                            // save username in base64
//                            editor.putString("username", Base64.encodeToString(onukit_username.value?.toByteArray(), Base64.DEFAULT))
//                            editor.putString("password", Base64.encodeToString(onukit_password.value?.toByteArray(), Base64.DEFAULT))
//                            editor.apply()
//
//                            Log.d("OnuFunctions", "Saved username and password in SharedPreferences")
//                            Log.d("OnuFunctions", "inputted username: ${onukit_username.value}")
//                            Log.d("OnuFunctions", "inputted password: ${onukit_password.value}")
//                            Log.d("OnuFunctions", "base64 username: ${Base64.encodeToString(onukit_username.value?.toByteArray(), Base64.DEFAULT)}")
//                            Log.d("OnuFunctions", "base64 password: ${Base64.encodeToString(onukit_password.value?.toByteArray(), Base64.DEFAULT)}")
//                            Log.d("OnuFunctions", "saved username: ${sharedPreferences.getString("username", "")}")
//                            Log.d("OnuFunctions", "saved password: ${sharedPreferences.getString("password", "")}")
//
//                            Handler(Looper.getMainLooper()).post {
//                                Toast.makeText(coreContext.context, reason, Toast.LENGTH_SHORT).show()
//                                createProxyConfig()
//                            }
//                        }
//                    } catch (e: Exception) {
//                        Log.d("OnuFunctions", "Exception: $e")
//                        // log the exception line number
//                        e.printStackTrace()
//                        Handler(Looper.getMainLooper()).post {
//                            Toast.makeText(coreContext.context, "Exception: $e", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    // The request failed
//                    Log.d("OnuFunctions", "response.isSuccessful == false | Failed to activate user")
//                    Handler(Looper.getMainLooper()).post {
//                        Toast.makeText(coreContext.context, "Request Error! Try Again!", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        })
//    }

    fun createProxyConfig() {
        waitForServerAnswer.value = true
        coreContext.core.addListener(coreListener)

        accountCreator.username = username.value
        accountCreator.password = password.value
        accountCreator.domain = domain.value
        accountCreator.displayName = displayName.value
        accountCreator.transport = transport.value

        val proxyConfig: ProxyConfig? = accountCreator.createProxyConfig()
        proxyConfigToCheck = proxyConfig

        if (proxyConfig == null) {
            Log.e("[Assistant] [Generic Login]", "[Assistant] [Generic Login] Account creator couldn't create proxy config")
            coreContext.core.removeListener(coreListener)
            onErrorEvent.value = Event("Error: Failed to create account object")
            waitForServerAnswer.value = false
            return
        }

        Log.i("[Assistant] [Generic Login]", "[Assistant] [Generic Login] Proxy config created")
        // thread
        object : Thread() {
            override fun run() {
                android.os.Handler(Looper.getMainLooper()).post {
                    Toast.makeText(coreContext.context, "Connecting to SIP. Please wait!", Toast.LENGTH_LONG).show()
                }

                sleep(1000)
                // OnuFunctions.RestartApp().start()
                ProcessPhoenix.triggerRebirth(coreContext.context, Intent(coreContext.context, MainActivity::class.java))
            }
        }.start()
    }

    fun showOkDialog(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false) // Prevent dialog from being canceled by touching outside or pressing back button
        builder.create().show()
    }

    fun loadSIPConfigFromServer() {
        waitForServerAnswer.value = true
        var getSIPConfigs = OnuFunctions.GetSIPConfigs()
        var request = getSIPConfigs.go()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                android.os.Handler(Looper.getMainLooper()).post {
                    waitForServerAnswer.postValue(false)
                }

                val responseData = response.body?.string()
                if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                    try {
                        val jsonArray = JSONArray(responseData)
                        if (jsonArray.length() > 0) {
                            // SIP accounts found
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val sipNumber = jsonObject.getString("sip_number")
                                val sipServer = jsonObject.getString("sip_server")
                                val sipUser = jsonObject.getString("sip_user")
                                val sipLogin = jsonObject.getString("sip_login")
                                val sipPort = jsonObject.getString("sip_port")
                                val isActive = jsonObject.getString("isActive")
                                val createdBy = jsonObject.getString("created_By")
                                val assignedTo = jsonObject.getString("assigned_To")

                                // if isActive is int(0),  then the account is inactive
//                                if (isActive.toInt() == 0) {
//                                    android.os.Handler(Looper.getMainLooper()).post {
// //                                        showOkDialog(
// //                                            coreContext.context,
// //                                            "Error",
// //                                            "The SIP account is inactive. Try adding SIP config manually or contact the admin."
// //                                        )
//                                        Toast.makeText(coreContext.context, "The SIP account is inactive. Try adding SIP config manually or contact the admin.", Toast.LENGTH_LONG).show()
//                                    }
//                                } else {
                                // Process the SIP account data as needed
                                username.postValue(sipNumber)
                                password.postValue(sipLogin)
                                // value of sipHost in a variable using sipUser and sipPort (if exists)
                                val sipHost = if (sipPort.isNotEmpty()) "$sipServer:$sipPort" else sipServer
                                domain.postValue(sipHost)
                                displayName.postValue(sipUser)
                                android.os.Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        coreContext.context,
                                        "SIP account found! Please proceed to login.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                break
                                // }
                            }
                        } else {
                            // No SIP accounts found
                            Log.i("result", "jsonArray.length() > 0")
                            val message = "No SIP accounts found for the given email, please contact the admin"
                            // Handle the message accordingly
                            android.os.Handler(Looper.getMainLooper()).post {
                                // showOkDialog(coreContext.context, "Error", message)
                                Toast.makeText(coreContext.context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // No SIP accounts found
                        Log.i("result", "e: Exception")
                        val message = "No SIP accounts found for the given email, please contact the admin"
                        // Handle the message accordingly
                        android.os.Handler(Looper.getMainLooper()).post {
                            // showOkDialog(coreContext.context, "Error", message)
                            Toast.makeText(coreContext.context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    // Handle the error response accordingly
                    android.os.Handler(Looper.getMainLooper()).post {
                        // showOkDialog(coreContext.context, "Sorry", "Failed to load SIP config from server!")
                        Toast.makeText(coreContext.context, "Failed to load SIP config from server!", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                android.os.Handler(Looper.getMainLooper()).post {
                    waitForServerAnswer.postValue(false)
                    // Handle the failure case accordingly
                    // showOkDialog(coreContext.context, "Sorry", "Failed to load SIP config from server!")
                    Toast.makeText(
                        coreContext.context,
                        "Failed to load SIP config from server!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun isLoginButtonEnabled(): Boolean {
        // return isOnuLoginButtonEnabled() && username.value.orEmpty().isNotEmpty() && domain.value.orEmpty().isNotEmpty() && password.value.orEmpty().isNotEmpty()
        return username.value.orEmpty().isNotEmpty() && domain.value.orEmpty().isNotEmpty() && password.value.orEmpty().isNotEmpty()
    }

//    private fun isOnuLoginButtonEnabled(): Boolean {
//        return onukit_username.value.orEmpty().isNotEmpty() && onukit_password.value.orEmpty().isNotEmpty()
//    }
}
