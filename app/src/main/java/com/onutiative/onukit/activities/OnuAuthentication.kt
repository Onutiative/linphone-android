package com.onutiative.onukit.activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.onutiative.onukit.LinphoneApplication
import com.onutiative.onukit.R
import com.onutiative.onukit.activities.assistant.AssistantActivity
import com.onutiative.onukit.onuspecific.OnuFunctions
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class OnuAuthentication : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.onu_login_activity)
        // make "@layout/wait_layout" visible
        findViewById<View>(R.id.wait_layout_onulogin).visibility = View.GONE

        // check if the user is already logged in
        val userCredentials = OnuFunctions().getUserCredentials()
        if (userCredentials != null) {
            // get the username and password from the credentials
            val username = userCredentials["username"]
            val password = userCredentials["password"]

            Log.i("OnuFunctions", "OnuAuthentication username: $username")
            Log.i("OnuFunctions", "OnuAuthentication password: $password")
            // print username and password type
            Log.i("OnuFunctions", "OnuAuthentication username type: ${username?.javaClass?.canonicalName}")
            Log.i("OnuFunctions", "OnuAuthentication password type: ${password?.javaClass?.canonicalName}")
            // check if the credentials are not null

            if (username != "0" && password != "0") {
                // login the user
                Log.i("OnuFunctions", "OnuAuthentication Logging in")
                login(username!!, password!!)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // android.os.Process.killProcess(android.os.Process.myPid())
    }

    public fun loginBtn(view: View) {
        Log.i("OnuFunctions", "OnuAuthentication loginBtn function called")
        login()
    }

    public fun login(onukit_username_: String = "", onukit_password_: String = "") {
        Log.i("OnuFunctions", "OnuAuthentication login function called")

        val onukit_username = if (onukit_username_.isEmpty()) {
            findViewById<EditText>(R.id.email).text.toString().trim()
        } else {
            onukit_username_
        }

        val onukit_password = if (onukit_password_.isEmpty()) {
            findViewById<EditText>(R.id.password).text.toString().trim()
        } else {
            onukit_password_
        }

        // print username and password
        Log.i("OnuFunctions", "OnuAuthentication onukit_username: .$onukit_username.")
        Log.i("OnuFunctions", "OnuAuthentication onukit_password: .$onukit_password.")
        // check if username and password are empty
        Log.i("OnuFunctions", "onukit_username.isEmpty() = ${onukit_username.isEmpty()}")
        Log.i("OnuFunctions", "onukit_password.isEmpty() = ${onukit_password.isEmpty()}")

        if (onukit_username.isEmpty() || onukit_password.isEmpty()) {
            Log.i("OnuFunctions", "OnuAuthentication login function called - checking if username and password are empty")
            Toast.makeText(LinphoneApplication.coreContext.context, "Please fill in all fields", Toast.LENGTH_LONG).show()
            return
        }

        findViewById<View>(R.id.wait_layout_onulogin).visibility = View.VISIBLE
        val request = OnuFunctions.UserLogin(onukit_username, onukit_password).performLogin()
        val client = OkHttpClient()

        val sharedPreferences = LinphoneApplication.coreContext.context.getSharedPreferences("onukit_creds", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    // check when client is done, then set waitForServerAnswer's value to false
                    findViewById<View>(R.id.wait_layout_onulogin).visibility = View.GONE
                }
                Log.d("OnuFunctions", "onFailure - Failed to login user: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                editor.putString("username", null)
                editor.putString("password", null)
                editor.apply()

                Handler(Looper.getMainLooper()).post {
                    // check when client is done, then set waitForServerAnswer's value to false
                    findViewById<View>(R.id.wait_layout_onulogin).visibility = View.GONE
                }
                val requestBody = response.body

                // log status code
                Log.d("OnuFunctions", "Response code: ${response.code}")

                // check status code
                if (response.code != 200) {
                    // Show a toast message
                    Log.d("OnuFunctions", "response.code != 200 | Failed to login user")
                    // thread
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(LinphoneApplication.coreContext.context, "Server error! ${response.code}", Toast.LENGTH_SHORT).show()
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
                            }
                            return
                        } else {
                            // The request was successful
                            Log.d("OnuFunctions", "Login successful")

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
                                Toast.makeText(LinphoneApplication.coreContext.context, "Onukit Login Successful", Toast.LENGTH_SHORT).show()
                                // open the assistant activity
                                val intent = android.content.Intent(LinphoneApplication.coreContext.context, AssistantActivity::class.java)
                                startActivity(intent)
                                finish()
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

    public fun registration(view: View) {
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

        findViewById<View>(R.id.wait_layout_onuactivation).visibility = View.VISIBLE
        val request = OnuFunctions.UserActivation(onukit_username, onukit_password, mobile_number).performActivation()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                // Handle failure
                Log.d("OnuFunctions", "onFailure - Failed to login user: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                Handler(Looper.getMainLooper()).post {
                    // check when client is done, then set waitForServerAnswer's value to false
                    findViewById<View>(R.id.wait_layout_onuactivation).visibility = View.GONE
                }
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

//                            Log.d("OnuFunctions", "Saved username and password in SharedPreferences")
//                            Log.d("OnuFunctions", "inputted username: $onukit_username")
//                            Log.d("OnuFunctions", "inputted password: $onukit_password")
//                            Log.d("OnuFunctions", "base64 username: ${Base64.encodeToString(onukit_username?.toByteArray(), Base64.DEFAULT)}")
//                            Log.d("OnuFunctions", "base64 password: ${Base64.encodeToString(onukit_password?.toByteArray(), Base64.DEFAULT)}")
//                            Log.d("OnuFunctions", "saved username: ${sharedPreferences.getString("username", "")}")
//                            Log.d("OnuFunctions", "saved password: ${sharedPreferences.getString("password", "")}")

                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(LinphoneApplication.coreContext.context, reason, Toast.LENGTH_SHORT).show()
                                // open the assistant activity
                                val intent = android.content.Intent(LinphoneApplication.coreContext.context, AssistantActivity::class.java)
                                startActivity(intent)
                                finish()
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

    fun switchToRegistration(view: View) {
        setContentView(R.layout.onu_activation_activity)
    }

    fun forgotPass(view: View) {
        // toast message
        Toast.makeText(LinphoneApplication.coreContext.context, "Not implemented yet", Toast.LENGTH_SHORT).show()
    }
}
