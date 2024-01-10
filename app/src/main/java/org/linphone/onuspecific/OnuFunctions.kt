package org.linphone.onuspecific

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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.jakewharton.processphoenix.ProcessPhoenix
import java.io.File
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale
import okhttp3.Call
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject
import org.linphone.LinphoneApplication
import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.activities.main.MainActivity
import org.linphone.activities.main.recordings.data.RecordingData
import org.linphone.core.Call.Dir
import org.linphone.utils.FileUtils

open class OnuFunctions() {
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

    fun getUserCredentials(
        context: Context = LinphoneApplication.coreContext.context
    ): Map<String, String?> {
        val sharedPreferences = context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
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

    class GetSavedCredentials(
        private val context: Context = LinphoneApplication.coreContext.context
    ) {
        fun get(): Map<String, String?> {
            val gg = OnuFunctions().getUserCredentials(context)
            var username = gg["username"]
            var password = gg["password"]
            return mapOf("username" to username, "password" to password)
        }

        fun getUserName(): String? {
            Log.i("OnuFunctions", "getUserName: " + OnuFunctions().getUserCredentials(context)["username"])
            return OnuFunctions().getUserCredentials(context)["username"]
        }

        fun getPassword(): String? {
            return OnuFunctions().getUserCredentials(context)["password"]
        }
    }

    public fun getPhoneNumber(context: Context = LinphoneApplication.coreContext.context): String {
        val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        // return phoneNumber if it is not null. Otherwise, return "0"
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //  call  ActivityCompat to request the missing permissions
                ActivityCompat.requestPermissions(
                    context as MainActivity,
                    arrayOf(Manifest.permission.READ_PHONE_NUMBERS),
                    1
                )

                // check if the permission is granted
                if (ActivityCompat.checkSelfPermission(
                        context,
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
                            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
                val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
        private val mobile_number: String? = "0",
        private val fcmToken: String? = "null",
        private val context: Context = LinphoneApplication.coreContext.context
    ) {
        fun performActivation(): Request {

            val json = JSONObject()
            json.put(
                "device_id",
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            )
            json.put("accountCreateFlag", "0")
            json.put("version", android.os.Build.VERSION.RELEASE)
            json.put("brand", android.os.Build.BRAND)
            json.put("model", android.os.Build.MODEL)
            json.put("email", username)
            json.put("password", password)
            json.put("thirdPartyUserData", "null")
            json.put("oid", fcmToken)
            json.put("mobile", OnuFunctions().getPhoneNumber(context))

            // check mobile number
            if (mobile_number != "0") {
                json.put("mobile", mobile_number)
            }

            // print the number
            Log.d("OnuFunctions", "Number: ${OnuFunctions().getPhoneNumber(context)}")

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
        private val password: String?,
        private val fcmToken: String? = "null",
        private val context: Context = LinphoneApplication.coreContext.context
    ) {
        fun performLogin(): Request {

            val json = JSONObject()
            json.put(
                "device_id",
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            )
            json.put("accountCreateFlag", "0")
            json.put("version", android.os.Build.VERSION.RELEASE)
            json.put("brand", android.os.Build.BRAND)
            json.put("model", android.os.Build.MODEL)
            json.put("email", username)
            json.put("password", password)
            json.put("thirdPartyUserData", "null")

            // Get Firebase token synchronously and add it to JSON object
//            val latch = CountDownLatch(1)
//            var token = ""
//
//            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    token = task.result ?: ""
//                } else {
//                    Log.w("Firebase", "Fetching FCM registration token failed", task.exception)
//                }
//                latch.countDown()
//            }
//
//            latch.await()

            json.put("oid", fcmToken)
            json.put("mobile", OnuFunctions().getPhoneNumber(context))

            // print the number
            Log.d("OnuFunctions", "Number: ${OnuFunctions().getPhoneNumber(context)}")

            // check if phone number is empty
            if (OnuFunctions().getPhoneNumber(context) == "") {
                json.put("mobile", "null")
            }

            // log the full json body
            // Log.d("OnuFunctions", "UserLogin JSON: $json")

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

    fun checkSavedCredentials(
        kill_app: Int,
        context: Context = LinphoneApplication.coreContext.context
    ) {
        // kill_app FLAGS
        // 0 = If saved credentials aren't found, nothing happens
        // 1 = If saved credentials aren't found, the app will be closed
        Log.i("OnuFunctions", "OnuAuthentication checkSavedCredentials function called")
        val userCredentials = OnuFunctions().getUserCredentials(context)
        if (userCredentials != null) {
            // get the username and password from the credentials
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            // check if the credentials are not null
            if (username != "0" && password != "0") {
                // login the user
                Log.i("OnuFunctions", "OnuAuthentication Logging in")

                Log.i("OnuFunctions", "OnuAuthentication before request")

                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("Firebase", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new FCM registration token
                        val fcmToken = task.result
                        val request = UserLogin(username, password, fcmToken).performLogin()
                        val client = OkHttpClient()

                        // Log.i("OnuFunctions", "OnuAuthentication before sharedPreferences")

                        val sharedPreferences = context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        // Log.i("OnuFunctions", "OnuAuthentication after sharedPreferences")

                        client.newCall(request).enqueue(object : okhttp3.Callback {
                            override fun onFailure(call: okhttp3.Call, e: IOException) {
                                Handler(Looper.getMainLooper()).post {
                                    // show a toast
                                    Toast.makeText(context, "Failed to check credentials: $e", Toast.LENGTH_SHORT).show()
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
                                        Toast.makeText(context, "Server error! ${response.code}", Toast.LENGTH_SHORT).show()
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
                                                Toast.makeText(context, "Onukit Login Failed", Toast.LENGTH_SHORT).show()
                                                // kill the app
                                                android.os.Process.killProcess(android.os.Process.myPid())
                                            }
                                            return
                                        } else {
                                            Handler(Looper.getMainLooper()).post {
                                                Toast.makeText(context, "Onukit Login Successful", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.d("OnuFunctions", "Exception: $e")
                                        // log the exception line number
                                        e.printStackTrace()
                                        Handler(Looper.getMainLooper()).post {
                                            Toast.makeText(context, "Exception: $e", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    editor.putString("username", null)
                                    editor.putString("password", null)
                                    editor.apply()
                                    // The request failed
                                    Log.d("OnuFunctions", "response.isSuccessful == false | Failed to activate user")
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(context, "Wrong usename or password", Toast.LENGTH_SHORT).show()
                                        // Toast.makeText(context, "Request Error! Try Again!", Toast.LENGTH_SHORT).show()
                                        // kill the app
                                        android.os.Process.killProcess(android.os.Process.myPid())
                                    }
                                }
                            }
                        })
                    }
                )
            } else {
                if (kill_app == 0) {
                    Log.d("OnuFunctions", "Saved credentials not found")
                } else if (kill_app == 1) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "User Not Logged in", Toast.LENGTH_SHORT).show()
                        // kill the app
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
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
            context: Context = LinphoneApplication.coreContext.context
        ) {

            // get user credentials
            val userCredentials = OnuFunctions().getUserCredentials(context)
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("trxid", trxId ?: "")
                .addFormDataPart("uploaded_file", file.name, file.asRequestBody("audio/amr".toMediaTypeOrNull()))
                .addFormDataPart("calltype", callType ?: "")
                .addFormDataPart("username", username ?: "")
                .addFormDataPart("password", password ?: "")
                .addFormDataPart("device_id", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
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
        private val context: Context = LinphoneApplication.coreContext.context
    ) {

        fun send() {
            val json = JSONObject()
            json.put("callerMsisdn", callerId)
            json.put("inTime", formattedTime)
            json.put("device_id", Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
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
            val userCredentials = OnuFunctions().getUserCredentials(context)
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

    class RestartApp(
        private val context: Context = LinphoneApplication.coreContext.context
    ) {
        fun start() {
            ProcessPhoenix.triggerRebirth(context)
        }
    }

    // check if call recordings are 1 day old
    class CallRecordingCleanUp() {
        // file patterns are like this: 09611265560_07-03-2023-01-57-37.amr
        // 1st part is the caller id 09611265560
        // 2nd part is the date 07-03-2023
        // 3rd part is the time 01-57-37
        fun check() {
            for (file in FileUtils.getFileStorageDir().listFiles().orEmpty()) {
                Log.d("OnuFunctions", "CallRecordingCleanUp: ${file.name}")
                if (RecordingData.RECORD_PATTERN.matcher(file.path).matches()) {
                    val fileDate = file.name.split("_")[1].split(".")[0]
                    val fileDateTimeFormat = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault())
                    val fileDateTimeDate = fileDateTimeFormat.parse(fileDate)
                    val currentDate = Date()
                    val diff = currentDate.time - fileDateTimeDate.time
                    val diffDays = diff / (24 * 60 * 60 * 1000)

                    // Log these variables
                    Log.d("OnuFunctions", "CallRecordingCleanUp: fileDate: $fileDate")
                    Log.d("OnuFunctions", "CallRecordingCleanUp: fileDateTimeDate: $fileDateTimeDate")
                    Log.d("OnuFunctions", "CallRecordingCleanUp: currentDate: $currentDate")
                    Log.d("OnuFunctions", "CallRecordingCleanUp: diff: $diff")
                    Log.d("OnuFunctions", "CallRecordingCleanUp: diffDays: $diffDays")

                    if (diffDays >= 1) {
                        try {
                            Log.d("OnuFunctions", "CallRecordingCleanUp: ${file.name} is 1 day old, deleting...")
                            file.delete()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    class GetSIPConfigs(
        private val context: Context = LinphoneApplication.coreContext.context
    ) {
        fun go(): Request {
            val userCredentials = OnuFunctions().getUserCredentials(context)
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            val url = "https://api.onukit.com/6v1/sipmatch"
            val client = OkHttpClient()

            val payload = mapOf("username" to username)
            val json = Gson().toJson(payload)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", Credentials.basic(username!!, password!!))
                .build()
        }
    }

    class GetSummary(
        private val context: Context = LinphoneApplication.coreContext.context,
        private var device_id: String? = null,
        private var phone_number: String? = null
    ) {
        fun go(): Request {

            if (device_id == null) {
                device_id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            }

            if (phone_number == null) {
                phone_number = OnuFunctions().getPhoneNumber(context)
            }

            val userCredentials = OnuFunctions().getUserCredentials(context)
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            val url = "https://api.onukit.com/6v4/getSummary"
            val client = OkHttpClient()

            // add device id and phone number to payload as a map in an array
            val payload = mapOf("device_id" to device_id, "caller_msisdn" to phone_number)
            // add the payload to an array as the first element
            val array = arrayOf(payload)
            val json = Gson().toJson(array)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", Credentials.basic(username!!, password!!))
                .build()
        }
    }

    class EditTask(
        private val context: Context = LinphoneApplication.coreContext.context,
        private val task_data: Map<String, String>
    ) {
        fun go(): Request {
            val userCredentials = OnuFunctions().getUserCredentials(context)
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            val url = "https://api.onukit.com/6v4/task/taskEditApi"
            val client = OkHttpClient()

            // add device id and phone number to payload as a map in an array
            val payload = task_data
            val json = Gson().toJson(payload)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            return Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", Credentials.basic(username!!, password!!))
                .build()
        }
    }

    // get call logs as dictionary
    class GetCallLogs(
        private val type_of_call: String? = null,
        private val till_date: String? = null,
    ) {
        fun get_all_calls(): ArrayList<Map<String, String>> {
            val companion = LinphoneApplication
            val callLogs = coreContext.core.callLogs
            var data = ArrayList<Map<String, String>>()

            for (callLog in callLogs) {
                val remoteAddress = callLog.remoteAddress
                val phoneNumber = remoteAddress.username
                val callerName = remoteAddress.displayName
                val timestamp = callLog.startDate
                val callDate = Date(timestamp * 1000L)

                // The timestamp is in seconds, convert it to milliseconds

                val direction = callLog.dir
                val callType: String = when (direction) {
                    Dir.Incoming -> "INCOMING"
                    Dir.Outgoing -> "OUTGOING"
                    else -> "MISSED"
                }

                // Check if type_of_call is not null and the call type matches
                // Also, check if till_date is not null. If it is not null, check if the call date is less than or equal to till_date

                if ((type_of_call == null || callType == type_of_call) &&
                    (till_date == null || callDate >= SimpleDateFormat("yyyy-MM-dd").parse(till_date))
                ) {
                    // Add the call log data to the result
                    val callData = mapOf(
                        "phoneNumber" to phoneNumber,
                        "callerName" to callerName,
                        "timestamp" to timestamp.toString(),
                        "callType" to callType
                    )
                    data.add(callData as Map<String, String>)
                }
            }
            return data
        }

        fun get_number_of_incoming_and_outgoing_calls(): Map<String, Int> {
            val callLogs = coreContext.core.callLogs
            var incoming = 0
            var outgoing = 0

            for (callLog in callLogs) {
                val direction = callLog.dir
                val callType: String = when (direction) {
                    Dir.Incoming -> "INCOMING"
                    Dir.Outgoing -> "OUTGOING"
                    else -> "MISSED"
                }

                // Check if type_of_call is not null and the call type matches.
                // Also, check if till_date is not null.
                // If it is not null, parse the till_date and compare it with the call date
                val tillDate = till_date?.let { SimpleDateFormat("yyyy-MM-dd").parse(it) }

                if ((type_of_call == null || callType == type_of_call) &&
                    (tillDate == null || Date(callLog.startDate * 1000L) >= tillDate)
                ) {
                    // Increment incoming and outgoing counts based on call type
                    when (direction) {
                        Dir.Incoming -> incoming++
                        Dir.Outgoing -> outgoing++
                    }
                }
            }
            return mapOf("incoming" to incoming, "outgoing" to outgoing)
        }
    }
}
