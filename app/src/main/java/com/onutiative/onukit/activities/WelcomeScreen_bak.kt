package com.onutiative.onukit.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.linphone.R
import org.linphone.activities.assistant.AssistantActivity
import org.linphone.utils.PermissionHelper

class WelcomeScreen_bak : AppCompatActivity() {

    private var Permission_Rejected = 0

    private val permissions = arrayOf(
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.WRITE_CONTACTS,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.POST_NOTIFICATIONS
    )
    private val REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_slide1)

        // Log permissions.all { PermissionHelper.get().hasPermission(it) })
        Log.i("Permissions", permissions.all { PermissionHelper.get().hasPermission(it) }.toString())

        // check if all permissions are granted
        if (PermissionHelper.get().hasReadContactsPermission() && PermissionHelper.get().hasWriteContactsPermission() && PermissionHelper.get().hasReadPhoneStatePermission() && PermissionHelper.get().hasReadExternalStoragePermission() && PermissionHelper.get().hasWriteExternalStoragePermission() && PermissionHelper.get().hasCameraPermission() && PermissionHelper.get().hasRecordAudioPermission() && PermissionHelper.get().hasBluetoothConnectPermission() && PermissionHelper.get().hasPostNotificationsPermission()) {
            // all permissions are granted, start the app
            Log.i("Permissions", "[onCreate] All permissions granted")
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
            setContentView(R.layout.welcome_slide2)
        } else {
            // some or all permissions are denied
            // request permissions
            Log.i("Permissions", "[onCreate] Requesting permissions")
            requestPermissions(permissions, REQUEST_CODE)
        }
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i("Permissions", "[onRequestPermissionsResult] Request code: $requestCode")
        if (requestCode == REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // all permissions are granted, start the app
                Log.i("Permissions", "[onRequestPermissionsResult] All permissions granted")
//                val intent = Intent(this, AssistantActivity::class.java)
//                startActivity(intent)
//                finish()
                setContentView(R.layout.welcome_slide2)
            } else {
                Permission_Rejected += 1
                if (Permission_Rejected < 3) {
                    // some or all permissions are denied, restart the activity
                    Log.i("Permissions", "[onRequestPermissionsResult] Some or all permissions denied")
                    val intent = Intent(this, WelcomeScreen_bak::class.java)
                    startActivity(intent)
                } else {
                    // some or all permissions are denied, restart the activity
                    Log.i("Permissions", "[onRequestPermissionsResult] Some or all permissions denied")
                    val intent = Intent(this, AssistantActivity::class.java)
                    startActivity(intent)
                }
            }
        } else {
            Log.i("Permissions", "[onRequestPermissionsResult] Request code not recognized")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
