package org.linphone.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.linphone.R

class WelcomeScreen : AppCompatActivity() {

    private var Permission_Rejected = 0

    val layouts = intArrayOf(
        R.layout.welcome_slide1,
        R.layout.welcome_slide2,
        R.layout.welcome_slide3,
        R.layout.welcome_slide4,
        R.layout.welcome_slide5,
        R.layout.welcome_slide6,
        R.layout.welcome_slide7
    )

    private val REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_slide1)
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

                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false

                    if (shouldShowRequestPermissionRationale(permission)) {
                        // User denied the permission request, but did not select "never ask again"
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
                        AlertDialog.Builder(this)
                            .setTitle(R.string.permission_needed_title)
                            .setMessage(R.string.permission_needed_message)
                            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.data = Uri.fromParts("package", packageName, null)
                                startActivity(intent)
                            }
                            .setNegativeButton(R.string.cancel) { _, _ ->
                                Permission_Rejected = 1
                            }
                            .show()
                    }
                }
            }

            if (allPermissionsGranted) {
                Permission_Rejected = PackageManager.PERMISSION_GRANTED
                setContentView(layouts[2])
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    fun acceptPrivacyPolicy(view: View) {
        setContentView(R.layout.welcome_slide2)
    }

    fun acceptCallPermission(view: View) {
        // show call permission android.Manifest.permission.READ_PHONE_STATE and android.Manifest.permission.RECORD_AUDIO
        requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.RECORD_AUDIO), REQUEST_CODE)
    }

    fun acceptSMSPermission(view: View) {
        // check if sms permission is granted
        if (checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.welcome_slide2)
        } else {
            // show SMS permission android.Manifest.permission.READ_SMS
            requestPermissions(arrayOf(android.Manifest.permission.READ_SMS), REQUEST_CODE)
        }
        // show SMS permission android.Manifest.permission.READ_SMS
        requestPermissions(arrayOf(android.Manifest.permission.READ_SMS), REQUEST_CODE)
    }
}
