package org.linphone.activities

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.linphone.LinphoneApplication
import org.linphone.R
import org.linphone.activities.main.MainActivity
import org.linphone.onuspecific.OnuFunctions
import org.linphone.utils.PermissionHelper

class WelcomeScreen : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.SYSTEM_ALERT_WINDOW
    )

    private val REQUEST_CODE_PERMISSIONS = 0
    private var currentPermissionIndex = 0
    private var hasNotificationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (areAllPermissionsGranted()) {
            // All permissions are granted, proceed to next activity
            if (LinphoneApplication.coreContext.core.accountList.isEmpty()) {
                val intent = Intent(this, OnuFunctions::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            // requestPermissionsIfRequired()
            OnuFunctions.dontKillMyApp(this).run()
            showBatteryOptimizationDialog()
            showCurrentLayout()
            showNextLayout()
        }

        val callback = this.onBackPressedDispatcher.addCallback(this) {
            Log.i("OnuFunctions", "Back button pressed, killing app")
            finishAffinity()
            System.exit(0)
        }
    }

    private fun showBatteryOptimizationDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Battery Optimization")
                    .setMessage("Battery optimization is needed to receive calls consistently. Please tap Allow when prompted.")
                    .setPositiveButton("OK") { _, _ -> // Launch the battery optimization settings
                        val intent =
                            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intent.data = Uri.parse("package:$packageName")
                        startActivity(intent)
                    }
                    .setNegativeButton(
                        "Cancel"
                    ) { dialog, _ -> // User clicked "Cancel," do nothing or handle as required
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasNotificationPermissionGranted = isGranted
        if (isGranted) {
            currentPermissionIndex++
            showCurrentLayout()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionRationale()
                } else {
                    showSettingDialog()
                }
            }
        }
    }

    private fun showSettingDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission")
            .setMessage("Notification permission is required, Please allow notification permission from settings")
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Alert")
            .setMessage("Notification permission is required to show notifications")
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNextLayout() {
        if (currentPermissionIndex < permissions.size) {
            when (val permission = permissions[currentPermissionIndex]) {
                Manifest.permission.POST_NOTIFICATIONS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                        if (hasNotificationPermissionGranted) {
//                            currentPermissionIndex++
//                            showCurrentLayout()
//                        } else {
//                            showNotificationPermissionRationale()
//                        }
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
//                        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
//                            currentPermissionIndex++
//                            showCurrentLayout()
//                        } else {
//                            requestPermission(permission)
//                        }
                        // no need for notification permission below Android 13
                        currentPermissionIndex++
                        showCurrentLayout()
                    }
                }
                Manifest.permission.READ_PHONE_STATE -> {
                    if (PermissionHelper.get().hasReadPhoneStateOrPhoneNumbersPermission()) {
                        currentPermissionIndex++
                        showNextLayout()
                    } else {
                        requestPermission(permission)
                    }
                }
                Manifest.permission.READ_SMS -> {
                    if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                        currentPermissionIndex++
                        showNextLayout()
                    } else {
                        requestPermission(permission)
                    }
                }
                Manifest.permission.READ_CONTACTS -> {
                    if (PermissionHelper.get().hasReadContactsPermission()) {
                        currentPermissionIndex++
                        showNextLayout()
                    } else {
                        requestPermission(permission)
                    }
                }
                Manifest.permission.RECORD_AUDIO -> {
                    if (PermissionHelper.get().hasRecordAudioPermission()) {
                        currentPermissionIndex++
                        showNextLayout()
                    } else {
                        requestPermission(permission)
                    }
                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                    if (PermissionHelper.get().hasWriteExternalStoragePermission()) {
                        currentPermissionIndex++
                        showNextLayout()
                    } else {
                        requestPermission(permission)
                    }
                }
                Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                    if (Settings.canDrawOverlays(this) || checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED) {
                        currentPermissionIndex++
                        showNextLayout()
                    } else {
                        Toast.makeText(
                            this,
                            R.string.overlay_permission_toast,
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.fromParts("package", packageName, null)
                        startActivity(intent)
                    }
                }
            }
        } else {
            // All permissions are granted, proceed to next activity
            if (LinphoneApplication.coreContext.core.accountList.isEmpty()) {
                val intent = Intent(this, OnuAuthentication::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showCurrentLayout() {
        // Show the layout corresponding to the current permission index
        val layoutResId = when (currentPermissionIndex) {
            0 -> R.layout.welcome_slide1
            1 -> R.layout.welcome_slide2
            2 -> R.layout.welcome_slide3
            3 -> R.layout.welcome_slide4
            4 -> R.layout.welcome_slide5
            5 -> R.layout.welcome_slide6
            6 -> R.layout.welcome_slide7
            else -> R.layout.welcome_slide1 // Default layout if index is out of bounds
        }
        setContentView(layoutResId)
    }

    private fun requestPermission(permission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (permission == Manifest.permission.POST_NOTIFICATIONS) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
            }
        } else {
            requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun areAllPermissionsGranted(): Boolean {
        return permissions.all { permission ->
            PermissionHelper.get().hasPermission(permission)
        }
    }

    private fun requestPermissionsIfRequired() {
        val permissionsToRequest = permissions.filter { permission ->
            !PermissionHelper.get().hasPermission(permission)
        }

        // if (Version.sdkAboveOrEqual(Version.API33_ANDROID_13_TIRAMISU) && permissionsToRequest.isNotEmpty()) {
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            var allPermissionsGranted = true
            var needOverlayPermission = false

            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]

                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    allPermissionsGranted = false

                    if (shouldShowRequestPermissionRationale(permission)) {
                        // User denied the permission request, but did not select "never ask again"
                        when (permission) {
                            Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                                needOverlayPermission = true
                            }
                            else -> {
                                // showRationaleDialog(permission)
                                showOverlayPermissionDialog()
                            }
                        }
                    } else {
                        // User denied the permission request and selected "never ask again"
                        when (permission) {
                            Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                                showOverlayPermissionDialog()
                            }
                            else -> {
                                // ask for permission
                                requestPermission(permission)
                            }
                        }
                    }
                }
            }

//            if (areAllPermissionsGranted()) {
//                val intent = Intent(this, OnuAuthentication::class.java)
//                startActivity(intent)
//                finish()
//            } else
            if (needOverlayPermission) {
                showOverlayPermissionDialog()
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
                if (LinphoneApplication.coreContext.core.accountList.isEmpty()) {
                    startActivity(Intent(this, OnuAuthentication::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun showRationaleDialog(permission: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_needed_title)
            .setMessage(R.string.permission_needed_message)
            .setPositiveButton(R.string.retry) { _, _ ->
                requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // Do nothing or handle rejection
            }
            .show()
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_needed_title)
            .setMessage(R.string.permission_needed_message)
            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // Do nothing or handle rejection
            }
            .show()
    }

    private fun showOverlayPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.permission_needed_title)
            .setMessage(R.string.overlay_permission_message)
            .setPositiveButton(R.string.go_to_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                // Do nothing or handle rejection
            }
            .show()
    }

    override fun onBackPressed() {
        Log.d("OnuFunctions", "WelcomeScreen onBackPressed, killing app")
        Toast.makeText(this, "Closing...", Toast.LENGTH_SHORT).show()
        finishAffinity()
        System.exit(0)
    }

    // Welcome Slide 1
    fun acceptPrivacyPolicy(view: View) {
        setContentView(R.layout.welcome_slide2)
    }

    // Welcome Slide 2
    fun acceptCallPermission(view: View) {
        if (PermissionHelper.get().hasReadPhoneStatePermission()) {
            setContentView(R.layout.welcome_slide3)
        } else {
            requestPermission(Manifest.permission.READ_PHONE_STATE)
        }
    }

    // Welcome Slide 3
    fun acceptSMSPermission(view: View) {
        if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            setContentView(R.layout.welcome_slide4)
        } else {
            requestPermission(Manifest.permission.READ_SMS)
        }
    }

    fun checkContactPermission(view: View) {
        if (PermissionHelper.get().hasReadContactsPermission()) {
            setContentView(R.layout.welcome_slide5)
        } else {
            requestPermission(Manifest.permission.READ_CONTACTS)
        }
    }

    fun checkMicrophonePermission(view: View) {
        if (PermissionHelper.get().hasRecordAudioPermission()) {
            setContentView(R.layout.welcome_slide6)
        } else {
            requestPermission(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun checkStoragePermission(view: View) {
        if (PermissionHelper.get().hasWriteExternalStoragePermission()) {
            setContentView(R.layout.welcome_slide7)
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    fun checkPopUpPermission(view: View) {
        if (checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_GRANTED || Settings.canDrawOverlays(
                this
            )
        ) {
            if (LinphoneApplication.coreContext.core.accountList.isEmpty()) {
                val intent = Intent(this, OnuAuthentication::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(this, R.string.overlay_permission_toast, Toast.LENGTH_LONG).show()
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
            }
        }
    }
}
