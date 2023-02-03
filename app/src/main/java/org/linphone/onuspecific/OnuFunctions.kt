package org.linphone.onuspecific

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.Base64
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okio.IOException
import org.json.JSONObject
import org.linphone.LinphoneApplication
import retrofit2.Callback
import retrofit2.Response

open class OnuFunctions {

    fun String.toBase64(): String {
        return String(
            android.util.Base64.encode(this.toByteArray(), android.util.Base64.DEFAULT),
            StandardCharsets.UTF_8
        )
    }

    fun String.fromBase64(): String {
        return String(
            android.util.Base64.decode(this, android.util.Base64.DEFAULT),
            StandardCharsets.UTF_8
        )
    }

    fun getUserCredentials(): Map<String, String> {
        val sharedPreferences = LinphoneApplication.coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        var password = sharedPreferences.getString("password", null)

        // if username or password is null, generate a random string and save it to shared preferences
        if (username == null || password == null) {
            username = "Onu\$erVe9"
            password = "p#@\$aS\$"
            Log.d("OnuFunctions", "Username(default): $username")
            Log.d("OnuFunctions", "Username(default): $password")
        } else {
            Log.d("OnuFunctions", "Username(base64): $username")
            Log.d("OnuFunctions", "Username(base64): $password")
            username = username.fromBase64()
            password = password.fromBase64()
            Log.d("OnuFunctions", "Username: $username")
            Log.d("OnuFunctions", "Password: $password")
        }

        // return as dictionary
        return mapOf("username" to username, "password" to password)
    }

    class UserActivation(
        private val username: String?,
        private val password: String?,
        private val context: Context
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
            json.put("password", username)
            json.put("email", password)
            json.put("thirdPartyUserData", "null")
            json.put("oid", "null")
            json.put("mobile", getPhoneNumber())

            // print the number
            Log.d("OnuFunctions", "Number: ${getPhoneNumber()}")

            // print the json
            Log.d("OnuFunctions", "UserActivation JSON: $json")
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

        @SuppressLint("MissingPermission")
        private fun getPhoneNumber(): String? {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            // return phoneNumber if it is not null. Otherwise, return "0"
            return subscriptionManager.activeSubscriptionInfoList[0].number ?: "0"
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
