package org.linphone.onuspecific

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

class OnuFunctions {

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
            Log.d("OnuFunctions", "JSON: $json")
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
}
