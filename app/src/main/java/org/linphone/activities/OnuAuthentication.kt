package org.linphone.activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import org.linphone.LinphoneApplication
import org.linphone.R
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.onuspecific.OnuFunctions

class OnuAuthentication : AppCompatActivity() {
    val waitForServerAnswer = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        waitForServerAnswer.value = false
        setContentView(R.layout.onu_login_activity)

        // check if the user is already logged in
        val userCredentials = OnuFunctions().getUserCredentials()
        if (userCredentials != null) {
            // get the username and password from the credentials
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            // check if the credentials are not null
            if (username != "0" && password != "0") {
                // login the user
                login(username!!, password!!)
            }
        }
    }

    fun loginBtn() {
        login("0", "0")
    }
    fun login(onukit_username: String, onukit_password: String) {
        // check if username and password are null
        if (onukit_username == "0" || onukit_password == "0") {
            // get the username and password from the EditTexts
            val onukit_username = findViewById<android.widget.EditText>(R.id.email).text.toString()
            val onukit_password = findViewById<android.widget.EditText>(R.id.password).text.toString()
        }

        if (onukit_username.isEmpty() || onukit_password.isEmpty()) {
            Toast.makeText(LinphoneApplication.coreContext.context, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        val request = OnuFunctions.UserLogin(onukit_username, onukit_password).performLogin()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                Log.d("OnuFunctions", "onFailure - Failed to login user: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val requestBody = response.body

                // log status code
                Log.d("OnuFunctions", "Response code: ${response.code}")

                // check status code
                if (response.code != 200) {
                    // Show a toast message
                    Log.d("OnuFunctions", "response.code != 200 | Failed to login user")
                    Toast.makeText(LinphoneApplication.coreContext.context, "Server error! ${response.code}", Toast.LENGTH_SHORT).show()
                }

                Handler(Looper.getMainLooper()).post {
                    // check when client is done, then set waitForServerAnswer's value to false
                    waitForServerAnswer.value = false
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
                        val reason = json.getString("reason")

                        Log.d("OnuFunctions", "status: $status")
                        Log.d("OnuFunctions", "reason: $reason")

                        // show in a toast message
                        if (status.toInt() > 4000) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, reason, Toast.LENGTH_SHORT).show()
                            }
                            return
                        } else {
                            // The request was successful
                            Log.d("OnuFunctions", "Login successful")

                            val sharedPreferences = LinphoneApplication.coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            // save username in base64
                            editor.putString("username", Base64.encodeToString(onukit_username?.toByteArray(), Base64.DEFAULT))
                            editor.putString("password", Base64.encodeToString(onukit_password?.toByteArray(), Base64.DEFAULT))
                            editor.apply()

                            Log.d("OnuFunctions", "Saved username and password in SharedPreferences")
                            Log.d("OnuFunctions", "inputted username: $onukit_username")
                            Log.d("OnuFunctions", "inputted password: $onukit_password")
                            Log.d("OnuFunctions", "base64 username: ${Base64.encodeToString(onukit_username?.toByteArray(), Base64.DEFAULT)}")
                            Log.d("OnuFunctions", "base64 password: ${Base64.encodeToString(onukit_password?.toByteArray(), Base64.DEFAULT)}")
                            Log.d("OnuFunctions", "saved username: ${sharedPreferences.getString("username", "")}")
                            Log.d("OnuFunctions", "saved password: ${sharedPreferences.getString("password", "")}")

                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, reason, Toast.LENGTH_SHORT).show()
                                // open the assistant activity
                                val intent = android.content.Intent(LinphoneApplication.coreContext.context, AssistantActivity::class.java)
                                startActivity(intent)
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
                    // The request failed
                    Log.d("OnuFunctions", "response.isSuccessful == false | Failed to activate user")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(LinphoneApplication.coreContext.context, "Request Error! Try Again!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun registration() {
        val onukit_username = findViewById<android.widget.EditText>(R.id.email).text.toString()
        val onukit_password = findViewById<android.widget.EditText>(R.id.password).text.toString()
        val retype_password = findViewById<android.widget.EditText>(R.id.retype_password).text.toString()
        val mobile_number = findViewById<android.widget.EditText>(R.id.mobile_number).text.toString()

        if (onukit_username.isEmpty() || onukit_password.isEmpty() || retype_password.isEmpty() || mobile_number.isEmpty()) {
            Toast.makeText(LinphoneApplication.coreContext.context, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        // check if two passwords match
        if (onukit_password != retype_password) {
            Toast.makeText(LinphoneApplication.coreContext.context, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }

        val request = OnuFunctions.UserActivation(onukit_username, onukit_password, mobile_number).performActivation()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                Log.d("OnuFunctions", "onFailure - Failed to login user: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                val requestBody = response.body

                // log status code
                Log.d("OnuFunctions", "Response code: ${response.code}")

                // check status code
                if (response.code != 200) {
                    // Show a toast message
                    Log.d("OnuFunctions", "response.code != 200 | Failed to login user")
                    Toast.makeText(LinphoneApplication.coreContext.context, "Server error! ${response.code}", Toast.LENGTH_SHORT).show()
                }

                // Handle response
                if (response.isSuccessful) {
                    Handler(Looper.getMainLooper()).post {
                        // check when client is done, then set waitForServerAnswer's value to false
                        waitForServerAnswer.value = false
                    }

                    try {
                        // load the json data
                        val json = JSONObject(requestBody?.string())

                        // [REMEMBER] - Leave this commented out
                        // https://stackoverflow.com/a/40709867
                        // "you can only receive the body string once"
                        // Log.d("OnuFunctions", "Response body: ${requestBody.string()}")

                        // get the status and reason from json data
                        val status = json.getString("status")
                        val reason = json.getString("reason")

                        Log.d("OnuFunctions", "status: $status")
                        Log.d("OnuFunctions", "reason: $reason")

                        // show in a toast message
                        if (status.toInt() > 4000) {
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, reason, Toast.LENGTH_SHORT).show()
                            }
                            return
                        } else {
                            // The request was successful
                            Log.d("OnuFunctions", "Login successful")

                            val sharedPreferences = LinphoneApplication.coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            // save username in base64
                            editor.putString("username", Base64.encodeToString(onukit_username?.toByteArray(), Base64.DEFAULT))
                            editor.putString("password", Base64.encodeToString(onukit_password?.toByteArray(), Base64.DEFAULT))
                            editor.apply()

                            Log.d("OnuFunctions", "Saved username and password in SharedPreferences")
                            Log.d("OnuFunctions", "inputted username: $onukit_username")
                            Log.d("OnuFunctions", "inputted password: $onukit_password")
                            Log.d("OnuFunctions", "base64 username: ${Base64.encodeToString(onukit_username?.toByteArray(), Base64.DEFAULT)}")
                            Log.d("OnuFunctions", "base64 password: ${Base64.encodeToString(onukit_password?.toByteArray(), Base64.DEFAULT)}")
                            Log.d("OnuFunctions", "saved username: ${sharedPreferences.getString("username", "")}")
                            Log.d("OnuFunctions", "saved password: ${sharedPreferences.getString("password", "")}")

                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, reason, Toast.LENGTH_SHORT).show()
                                // open the assistant activity
                                val intent = android.content.Intent(LinphoneApplication.coreContext.context, AssistantActivity::class.java)
                                startActivity(intent)
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
                    // The request failed
                    Log.d("OnuFunctions", "response.isSuccessful == false | Failed to activate user")
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(LinphoneApplication.coreContext.context, "Request Error! Try Again!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    fun switchToRegistration() {
        setContentView(R.layout.onu_activation_activity)
    }

    fun forgotPass(view: View) {
        // toast message
        Toast.makeText(LinphoneApplication.coreContext.context, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }
}
