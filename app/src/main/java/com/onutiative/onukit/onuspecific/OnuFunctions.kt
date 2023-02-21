package com.onutiative.onukit.onuspecific

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.onutiative.onukit.LinphoneApplication
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import org.json.JSONObject
import org.linphone.activities.main.MainActivity

open class OnuFunctions {

    private fun String.toBase64(): String {
        return String(
            android.util.Base64.encode(this.toByteArray(), android.util.Base64.DEFAULT),
            StandardCharsets.UTF_8
        )
    }

    private fun String.fromBase64(): String {
        return String(
            android.util.Base64.decode(this, android.util.Base64.DEFAULT),
            StandardCharsets.UTF_8
        )
    }

    fun getUserCredentials(): Map<String, String?> {
        val sharedPreferences = LinphoneApplication.coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        var password = sharedPreferences.getString("password", null)

        // if username or password is null, generate a random string and save it to shared preferences
        if (username == null || password == null) {
            username = "0"
            password = "0"
            // Log.d("OnuFunctions", "Username(default): $username")
            // Log.d("OnuFunctions", "Username(default): $password")
        } else {
            // Log.d("OnuFunctions", "Username(base64): $username")
            // Log.d("OnuFunctions", "Username(base64): $password")
            username = username.fromBase64()
            password = password.fromBase64()
            // Log.d("OnuFunctions", "Username: $username")
            // Log.d("OnuFunctions", "Password: $password")
        }

        // return as dictionary
        return mapOf("username" to username, "password" to password)
    }

    public fun getPhoneNumber(): String? {
        val subscriptionManager = LinphoneApplication.coreContext.context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        // return phoneNumber if it is not null. Otherwise, return "0"
        try {
            if (ActivityCompat.checkSelfPermission(
                    LinphoneApplication.coreContext.context,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //  call  ActivityCompat to request the missing permissions
                ActivityCompat.requestPermissions(
                    LinphoneApplication.coreContext.context as MainActivity,
                    arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
                    1
                )

                // check if the permission is granted
                if (ActivityCompat.checkSelfPermission(
                        LinphoneApplication.coreContext.context,
                        Manifest.permission.READ_PHONE_NUMBERS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // if the permission is not granted, return "0"
                    return "0"
                } else {
                    // if the permission is granted, return the phone number
                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        subscriptionManager.getPhoneNumber(0) ?: "0"
                    } else {
                        try {
                            val telephonyManager = LinphoneApplication.coreContext.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            telephonyManager.line1Number ?: "0"
                        } catch (e: Exception) {
                            Log.d("OnuFunctions", "Error: ${e.message}")
                            "0"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("OnuFunctions", "Error: ${e.message}")
            return try {
                val telephonyManager = LinphoneApplication.coreContext.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager.line1Number ?: "0"
            } catch (e: Exception) {
                Log.d("OnuFunctions", "Error: ${e.message}")
                "0"
            }
        }
        return "0"
    }

    class UserActivation(
        private val username: String?,
        private val password: String?,
        private val mobile_number: String? = "0"
    ) {
        fun performActivation(): Request {
            val json = JSONObject()
            json.put(
                "device_id",
                Settings.Secure.getString(LinphoneApplication.coreContext.context.contentResolver, Settings.Secure.ANDROID_ID)
            )
            json.put("accountCreateFlag", "0")
            json.put("version", android.os.Build.VERSION.RELEASE)
            json.put("brand", android.os.Build.BRAND)
            json.put("model", android.os.Build.MODEL)
            json.put("email", username)
            json.put("password", password)
            json.put("thirdPartyUserData", "null")
            json.put("oid", "null")
            json.put("mobile", OnuFunctions().getPhoneNumber())

            // check mobile number
            if (mobile_number != "0") {
                json.put("mobile", mobile_number)
            }

            // print the number
            Log.d("OnuFunctions", "Number: ${OnuFunctions().getPhoneNumber()}")

            // print the json
            // Log.d("OnuFunctions", "UserActivation JSON: $json")
            val url = "https://api.onukit.com/6v1/userActivation"

            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", Credentials.basic("Onu\$erVe9", "p#@\$aS\$"))
                .build()
        }

        // @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    }

    class UserLogin(
        private val username: String?,
        private val password: String?
    ) {
        fun performLogin(): Request {
            val json = JSONObject()
            json.put(
                "device_id",
                Settings.Secure.getString(LinphoneApplication.coreContext.context.contentResolver, Settings.Secure.ANDROID_ID)
            )
            json.put("accountCreateFlag", "0")
            json.put("version", android.os.Build.VERSION.RELEASE)
            json.put("brand", android.os.Build.BRAND)
            json.put("model", android.os.Build.MODEL)
            json.put("email", username)
            json.put("password", password)
            json.put("thirdPartyUserData", "null")
            json.put("oid", "null")
            json.put("mobile", OnuFunctions().getPhoneNumber())

            // print the number
            Log.d("OnuFunctions", "Number: ${OnuFunctions().getPhoneNumber()}")

            // check if phone number is empty
            if (OnuFunctions().getPhoneNumber() == "") {
                json.put("mobile", "null")
            }

            // print the json
            // Log.d("OnuFunctions", "UserActivation JSON: $json")
            val url = "https://api.onukit.com/6v1/login"

            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", Credentials.basic(username!!, password!!))
                .build()
        }
    }

    fun checkSavedCredentials() {
        Log.i("OnuFunctions", "OnuAuthentication checkSavedCredentials function called")
        val userCredentials = OnuFunctions().getUserCredentials()
        if (userCredentials != null) {
            // get the username and password from the credentials
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            // check if the credentials are not null
            if (username != "0" && password != "0") {
                // login the user
                Log.i("OnuFunctions", "OnuAuthentication Logging in")

                val request = UserLogin(username, password).performLogin()
                val client = OkHttpClient()

                val sharedPreferences = LinphoneApplication.coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        Handler(Looper.getMainLooper()).post {
                            // show a toast
                            Toast.makeText(LinphoneApplication.coreContext.context, "Failed to check credentials: $e", Toast.LENGTH_SHORT).show()
                        }
                        Log.d("OnuFunctions", "onFailure - Failed to login user: $e")
                    }

                    override fun onResponse(call: Call, response: okhttp3.Response) {

                        val requestBody = response.body

                        // log status code
                        Log.d("OnuFunctions", "Response code: ${response.code}")

                        // check status code
                        if (response.code != 200) {
                            editor.putString("username", null)
                            editor.putString("password", null)
                            editor.apply()
                            // Show a toast message
                            Log.d("OnuFunctions", "response.code != 200 | Failed to login user")
                            // thread
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, "Server error! ${response.code}", Toast.LENGTH_SHORT).show()
                                // kill the app
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }
                        }

                        // Handle response
                        if (response.isSuccessful) {
                            try {
                                // load the json data
                                val json = JSONObject(requestBody?.string())

                                // [REMEMBER] - Leave this commented out
                                // https://stackoverflow.com/a/40709867
                                // "you can only receive the body string once"
                                // Log.d("OnuFunctions", "Response body: ${requestBody.string()}")

                                // get the status and reason from json data
                                val status = json.getString("status")
                                Log.d("OnuFunctions", "status: $status")

                                // show in a toast message
                                if (status.toInt() > 4000) {
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(LinphoneApplication.coreContext.context, "Onukit Login Failed", Toast.LENGTH_SHORT).show()
                                        // kill the app
                                        android.os.Process.killProcess(android.os.Process.myPid())
                                    }
                                    return
                                } else {
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(LinphoneApplication.coreContext.context, "Onukit Login Successful", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("OnuFunctions", "Exception: $e")
                                // log the exception line number
                                e.printStackTrace()
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(LinphoneApplication.coreContext.context, "Exception: $e", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            editor.putString("username", null)
                            editor.putString("password", null)
                            editor.apply()
                            // The request failed
                            Log.d("OnuFunctions", "response.isSuccessful == false | Failed to activate user")
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, "Wrong usename or password", Toast.LENGTH_SHORT).show()
                                // Toast.makeText(LinphoneApplication.coreContext.context, "Request Error! Try Again!", Toast.LENGTH_SHORT).show()
                                // kill the app
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }
                        }
                    }
                })
            } else {
                Log.i("OnuFunctions", "OnuAuthentication No saved credentials")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(LinphoneApplication.coreContext.context, "User Not Logged in", Toast.LENGTH_SHORT).show()
                    // kill the app
                    android.os.Process.killProcess(android.os.Process.myPid())
                }
            }
        }
    }

    class CallRecordSender {
        private val client = OkHttpClient()
        private val url = "https://api.onukit.com/6v1/callRecordSave"

        @RequiresApi(Build.VERSION_CODES.O)
        fun send(
            trxId: String?,
            file: File,
            callType: String?,
        ) {

            // get user credentials
            val userCredentials = OnuFunctions().getUserCredentials()
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("trxid", trxId ?: "")
                .addFormDataPart("uploaded_file", file.name, file.asRequestBody("audio/amr".toMediaTypeOrNull()))
                .addFormDataPart("calltype", callType ?: "")
                .addFormDataPart("username", username ?: "")
                .addFormDataPart("password", password ?: "")
                .addFormDataPart("device_id", Settings.Secure.getString(LinphoneApplication.coreContext.context.contentResolver, Settings.Secure.ANDROID_ID))
                .build()

            // print the request body
            Log.d("OnuFunctions", "CallRecordSender request body: $requestBody")

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseJson = response.body?.string()
                    // print the status code and success message
                    Log.d("OnuFunctions", "CallRecordSender status code: ${response.code}")
                    if (responseJson != null) {
                        Log.d("OnuFunctions", "CallRecordSender response: $responseJson")
                    } else {
                        Log.d("OnuFunctions", "CallRecordSender response: null")
                    }
                } else {
                    // print the status code and error message
                    Log.d("OnuFunctions", "CallRecordSender status code: ${response.code}")
                    Log.d("OnuFunctions", "CallRecordSender response: ${response.body?.string()}")
                }
            }
        }
    }

    class CallDataSender(
        private val callerId: String,
        private val formattedTime: String,
        private val transId: String,
        private val callType: String,
    ) {

        fun send() {
            val json = JSONObject()
            json.put("callerMsisdn", callerId)
            json.put("inTime", formattedTime)
            json.put("device_id", Settings.Secure.getString(LinphoneApplication.coreContext.context.contentResolver, Settings.Secure.ANDROID_ID))
            json.put("transactionId", transId)
            json.put("callType", callType.lowercase(Locale.ROOT))

            // print the json
            Log.d("OnuFunctions", "CallDataSender JSON: $json")
            val url = "https://api.onukit.com/6v1/callLog"
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )

            // get user credentials
            val userCredentials = OnuFunctions().getUserCredentials()
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", Credentials.basic(username!!, password!!))
                .build()

            val client = OkHttpClient()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    // Handle failure
                    Log.d("OnuFunctions", "onFailure - Failed to send Call Data: $e")
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    // check status code
                    if (response.code != 200) {
                        // Show a toast message
                        Log.d("OnuFunctions", "Server error! ${response.code} | Failed to send Call Data")
                    }

                    // Handle response
                    if (response.isSuccessful) {
                        // The request was successful
                        Log.d("OnuFunctions", "${response.code} | Successfully sent Call Data")
                        // print the response
                        try {
                            Log.d(
                                "OnuFunctions",
                                "CallDataSender Response: ${response.body?.string()}"
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        // The request failed
                        Log.d("OnuFunctions", "${response.code} | Failed to send Call Data")
                    }
                }
            })
        }
    }

    // create call recording class
    // fix user login/activation
    // fix CallRecordResponse
}
