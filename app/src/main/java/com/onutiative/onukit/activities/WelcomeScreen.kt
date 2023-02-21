package com.onutiative.onukit.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.linphone.R
import org.linphone.utils.PermissionHelper

class WelcomeScreen : AppCompatActivity() {

    private var Permission_Rejected = 0

//    val layouts = intArrayOf(
//        R.layout.welcome_slide1,
//        R.layout.welcome_slide2,
//        R.layout.welcome_slide3,
//        R.layout.welcome_slide4,
//        R.layout.welcome_slide5,
//        R.layout.welcome_slide6,
//        R.layout.welcome_slide7
//    )

    private val permissions = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.POST_NOTIFICATIONS
    )

    private val REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.BLUETOOTH
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.POST_NOTIFICATIONS
//                ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permissions are already granted, do your work here
//        } else {
//            // Request for permissions
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.BLUETOOTH, android.Manifest.permission.POST_NOTIFICATIONS),
//                REQUEST_CODE
//            )
//        }

        Log.i("Permissions", PermissionHelper.get().hasReadContactsPermission().toString())
        Log.i("Permissions", PermissionHelper.get().hasReadPhoneStatePermission().toString())
        // Log.i("Permissions", PermissionHelper.get().hasReadExternalStoragePermission().toString())
        Log.i("Permissions", PermissionHelper.get().hasRecordAudioPermission().toString())

        // check if all permissions are granted
        if (PermissionHelper.get().hasReadContactsPermission() && PermissionHelper.get().hasReadPhoneStatePermission() && PermissionHelper.get().hasRecordAudioPermission() && PermissionHelper.get().hasWriteExternalStoragePermission()) {
            // all permissions are granted, start the app
//            if (LinphoneApplication.coreContext.core.accountList.isEmpty()) {
//                Log.i("Permissions", "[onCreate] All permissions granted")
//                val intent = Intent(this, OnuAuthentication::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                Log.i("Permissions", "[onCreate] All permissions granted")
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
            val intent = Intent(this, OnuAuthentication::class.java)
            startActivity(intent)
            finish()
        } else {
            // some or all permissions are denied
            // request permissions
            Log.i("Permissions", "[onCreate] Requesting permissions")
            // requestPermissions(permissions, REQUEST_CODE)
            setContentView(R.layout.welcome_slide1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            var allPermissionsGranted = true

            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]

                if (permissions[0] == android.Manifest.permission.BLUETOOTH || permissions[0] == android.Manifest.permission.POST_NOTIFICATIONS) {
                    return
                }

                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false

                    if (shouldShowRequestPermissionRationale(permission)) {
                        // User denied the permission request, but did not select "never ask again"
                        if (permissions[0] == android.Manifest.permission.SYSTEM_ALERT_WINDOW) {
                            // show a toast
                            Toast.makeText(this, "Please enable overlay permission", Toast.LENGTH_LONG).show()
                            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                            intent.data = Uri.fromParts("package", packageName, null)
                            startActivity(intent)
                            break
                        }

                        AlertDialog.Builder(this)
                            .setTitle(R.string.permission_needed_title)
                            .setMessage(R.string.permission_needed_message)
                            .setPositiveButton(R.string.retry) { _, _ ->
                                requestPermissions(arrayOf(permission), REQUEST_CODE)
                            }
                            .setNegativeButton(R.string.cancel) { _, _ ->
                                Permission_Rejected += 1
                            }
                            .show()
                    } else {
                        // User denied the permission request and selected "never ask again"
                        if (permissions.contains(android.Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                            AlertDialog.Builder(this)
                                .setTitle(R.string.permission_needed_title)
                                .setMessage(R.string.permission_needed_message)
                                .setPositiveButton(R.string.go_to_settings) { _, _ ->
                                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                    intent.data = Uri.fromParts("package", packageName, null)
                                    startActivity(intent)
                                }
                                .setNegativeButton(R.string.cancel) { _, _ ->
                                    Permission_Rejected += 1
                                }
                                .show()
                        } else {
                            AlertDialog.Builder(this)
                                .setTitle(R.string.permission_needed_title)
                                .setMessage(R.string.permission_needed_message)
                                .setPositiveButton(R.string.go_to_settings) { _, _ ->
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    intent.data = Uri.fromParts("package", packageName, null)
                                    startActivity(intent)
                                }
                                .setNegativeButton(R.string.cancel) { _, _ ->
                                    Permission_Rejected += 1
                                }
                                .show()
                        }
                    }
                }
            }

            // check which permission is granted
            if (permissions[0] == android.Manifest.permission.READ_PHONE_STATE) {
                setContentView(R.layout.welcome_slide3)
            } else if (permissions[0] == android.Manifest.permission.READ_SMS) {
                setContentView(R.layout.welcome_slide4)
            } else if (permissions[0] == android.Manifest.permission.READ_CONTACTS) {
                setContentView(R.layout.welcome_slide5)
            } else if (permissions[0] == android.Manifest.permission.RECORD_AUDIO) {
                setContentView(R.layout.welcome_slide6)
            } else if (permissions[0] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE || allPermissionsGranted) {
                setContentView(R.layout.welcome_slide7)
            } else if (permissions[0] == android.Manifest.permission.SYSTEM_ALERT_WINDOW || allPermissionsGranted) {
                // start MainActivity
                startActivity(Intent(this, OnuAuthentication::class.java))
                finish()
            }
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed();
//        // kill the app
//        android.os.Process.killProcess(android.os.Process.myPid())
//    }

    override fun onDestroy() {
        super.onDestroy()
        // android.os.Process.killProcess(android.os.Process.myPid())
    }

    // welcome_slide1
    fun acceptPrivacyPolicy(view: View) {
        setContentView(R.layout.welcome_slide2)
    }

    // welcome_slide2
    fun acceptCallPermission(view: View) {
        // show call permission android.Manifest.permission.READ_PHONE_STATE and android.Manifest.permission.RECORD_AUDIO
        // check first if call permissions are granted
        if (PermissionHelper.get().hasReadPhoneStatePermission()) {
            setContentView(R.layout.welcome_slide3)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), REQUEST_CODE)
        }
    }

    // welcome_slide3
    fun acceptSMSPermission(view: View) {
        // check if sms permission is granted
        if (checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.welcome_slide4)
        } else {
            // show SMS permission android.Manifest.permission.READ_SMS
            requestPermissions(arrayOf(android.Manifest.permission.READ_SMS), REQUEST_CODE)
        }
    }

    fun checkContactPermission(view: View) {
        // check if contact permission is granted
        if (PermissionHelper.get().hasReadContactsPermission()) {
            setContentView(R.layout.welcome_slide5)
        } else {
            // show contact permission android.Manifest.permission.READ_CONTACTS
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), REQUEST_CODE)
        }
    }

    fun checkMicrophonePermission(view: View) {
        // check if microphone permission is granted
        if (PermissionHelper.get().hasRecordAudioPermission()) {
            setContentView(R.layout.welcome_slide6)
        } else {
            // show microphone permission android.Manifest.permission.RECORD_AUDIO
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
        }
    }

    fun checkStoragePermission(view: View) {
        Log.d("checkStoragePermission", "checkStoragePermission")
        // check if storage permission is granted
        Log.d("checkStoragePermission", "checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED: " + (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
        Log.d("checkStoragePermission", "checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED: " + (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
        if (PermissionHelper.get().hasWriteExternalStoragePermission()) {
            setContentView(R.layout.welcome_slide7)
        } else {
            // show storage permission android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
        }
    }

    fun checkPopUpPermission(view: View) {
        Log.d("checkPopUpPermission", "checkPopUpPermission")
        // check if pop up permission is granted
        Log.d("checkPopUpPermission", "checkSelfPermission(android.Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED: " + (checkSelfPermission(android.Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED))
        Log.d("checkPopUpPermission", "Settings.canDrawOverlays(this): " + (Settings.canDrawOverlays(this)))

        if (checkSelfPermission(android.Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED || Settings.canDrawOverlays(this)) {
            // open main activity
            Log.i("Permission", "Pop up permission granted, Opening main activity")
            val intent = Intent(this, OnuAuthentication::class.java)
            startActivity(intent)
            finish()
        } else {
            // show pop up permission android.Manifest.permission.SYSTEM_ALERT_WINDOW
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Show alert dialog to the user saying a separate permission is needed
                // Launch the settings activity if the user prefers
                Toast.makeText(this, "Please enable overlay permission", Toast.LENGTH_LONG).show()
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
            }
        }
    }
}
