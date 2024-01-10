package org.linphone.onu_legacy.Activities

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.InputType
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import at.grabner.circleprogress.CircleProgressView
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.TextSliderView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.timqi.sectorprogressview.SectorProgressView
import org.linphone.R
import org.linphone.onu_legacy.Activities.Activities.AboutActivity
import org.linphone.onu_legacy.Activities.Activities.LoginActivity
import org.linphone.onu_legacy.Activities.Activities.PrivacyPolicy_Activity
import org.linphone.onu_legacy.Activities.Activities.RecentInbox_Activity
import org.linphone.onu_legacy.Activities.Activities.Setting_Activity
import org.linphone.onu_legacy.AsyncTasking.CheckOnline
import org.linphone.onu_legacy.AsyncTasking.FetchPermissions
import org.linphone.onu_legacy.AsyncTasking.PullServerSms
import org.linphone.onu_legacy.Database.Contact
import org.linphone.onu_legacy.Database.Database
import org.linphone.onu_legacy.MVP.Implementation.ContactPackage.ContactActivity
import org.linphone.onu_legacy.MVP.Implementation.PostedSMSPAckage.Posted_Inbox_Activity
import org.linphone.onu_legacy.MVP.Implementation.SmsSendPackage.SmsActivity
import org.linphone.onu_legacy.MVP.Implementation.TaskPackage.TaskShowActivity
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet.ContactSummaryData
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet.ContactSummeryGroup
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Address
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Contacts
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Profession
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Profile
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Relation
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.VirtualContact
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.ContactSummaryPullRepository
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.ContactSyncRepository
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.TaskSummaryPullRepository
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskSummaryData
import org.linphone.onu_legacy.Services.BackgroundService
import org.linphone.onu_legacy.Utility.Constants
import org.linphone.onu_legacy.Utility.Helper
import org.linphone.onu_legacy.Utility.Info
import org.linphone.onu_legacy.Utility.SharedPrefManager
import org.linphone.onu_legacy.WebViews.WebViews

// <!--used in 6v3-->
class Dashboard_Activity_bak() :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener,
    TaskSummaryPullRepository.TaskSummaryListener,
    ContactSummaryPullRepository.ContactSummaryListener {
    /******************************************************
     * All PreDefined Variables*********************
     */
    private var mDemoSlider: SliderLayout? = null

    //    private String url1 = "https://user.onukit.com/6v0/downloads/slider/1.jpg";
    //    private String url2 = "https://user.onukit.com/6v0/downloads/slider/2.jpg";
    //    private String url3 = "https://user.onukit.com/6v0/downloads/slider/3.jpg";
    private var indicator: ImageView? = null
    private var info: Info? = null
    private var db: Database? = null
    var progressBar: ProgressDialog? = null
    private val rocketAnimation: AnimationDrawable? = null
    private var mSensorService: BackgroundService? = null
    var mServiceIntent: Intent? = null
    private var appEdition: TextView? = null
    private var email: TextView? = null
    private var SmsinSuccess: TextView? = null
    private val SmsinFailed: TextView? = null
    private var SmsinPending: TextView? = null
    private var SmsOutSuccess: TextView? = null
    private val SmsOutFailed: TextView? = null
    private var SmsOutPending: TextView? = null
    private var CallinSuccess: TextView? = null
    private var CallOutSuccess: TextView? = null
    private var pendingTaskSubmission: TextView? = null
    private var totalTask: TextView? = null
    private var pendingTask: TextView? = null
    private var totalContact: TextView? = null
    private var clientContacts: TextView? = null
    private val callInQuota: ImageView? = null
    private val callOutQuota: ImageView? = null
    private val smsInQuota: ImageView? = null
    private val smsOutQuota: ImageView? = null
    private var url_maps: HashMap<String, String>? = null
    private var refreshBtn: ImageView? = null
    private var inSms: ImageView? = null
    private var inCall: ImageView? = null
    private var outCall: ImageView? = null
    private var outSms: ImageView? = null
    var id: String? = "2"
    private val activity: Activity? = null
    private var context: Context? = null
    var imei: String? = null
    private var uname: String? = null
    private var upass: String? = null
    private var url: String? = null
    private var urlForEdition: String? = null
    private var userId: String? = null
    private var parentID = "0"
    private val TAG = "DashBoard_Activity"
    var user_email: String? = null
    var user_pass: String? = null
    private val prefName = "onuPref"
    var prefEditor: SharedPreferences.Editor? = null
    var sharedPref: SharedPreferences? = null
    private var syncContact = false
    private var topBarNotification = false
    private var drawerLayout: DrawerLayout? = null
    private val outgoingSmsPieChart: SectorProgressView? = null
    private val incomingSmsPieChart: SectorProgressView? = null
    private val outgoingCallPieChart: SectorProgressView? = null
    private val incomingCallPieChart: SectorProgressView? = null
    private var outgoingSmsCircleProgressView: CircleProgressView? = null
    private var incomingSmsCircleProgressView: CircleProgressView? = null
    private var outgoingCallCircleProgressView: CircleProgressView? = null
    private var incomingCallCircleProgressView: CircleProgressView? = null
    private var taskProgressView: CircleProgressView? = null
    private var contactCircleProgressView: CircleProgressView? = null
    private var outgoingSmsCardView: CardView? = null
    private var incomingSmsCardView: CardView? = null
    private var outgoingCallCardView: CardView? = null
    private var incomingCallCardView: CardView? = null
    private var task_card_view: CardView? = null
    private var contact_card_view: CardView? = null
    private var sharedPrefManager: SharedPrefManager? = null
    private var pendingTaskClick: LinearLayout? = null
    private var helper: Helper? = null
    private var taskSummaryPullRepository: TaskSummaryPullRepository? = null
    private var contactSummaryPullRepository: ContactSummaryPullRepository? = null

    /******************************************************
     * All Override functions*********************
     */
    // The notification id for Notification is 1245. If you want to clear this, you've to use this id number.
    override fun onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider?.stopAutoCycle()
        super.onStop()
    }

    override fun onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.refreshbtn -> Button_click_refresh()
            R.id.incomingcall -> {
                //                Intent i_a = new Intent(DashBoard_Activity.this, CallLog_Activity.class);
                //  This part is commented for just a moment.
                val i_a = Intent(this, PopupCallListActivity::class.java)
                i_a.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                i_a.putExtra("type", "in")
                startActivity(i_a)
            }

            R.id.outgoingcall -> {
                val i_b = Intent(this, CallLog_Activity::class.java)
                i_b.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                i_b.putExtra("type", "out")
                startActivity(i_b)
            }

            R.id.outgoingsms -> {
                val i_d = Intent(this, OutgoingSent_Activity::class.java)
                i_d.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i_d)
            }

            R.id.indicator -> AlertInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_main)
        sharedPref = getSharedPreferences(prefName, MODE_PRIVATE)
        topBarNotification = sharedPref?.getBoolean("topBarNotification", true) ?: true

        context = this
        sharedPrefManager = SharedPrefManager(context)
        helper = Helper(context)

        // Database db=new Database(this);
        Log.e(TAG, "Dashboard Initialized!")
        InitDashBoard()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // ActivityCompat.requestPermissions(DashBoard_Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 210);
            // return;
            openAlarDialog()
        } else {
            CheckOnline(this, info?.getImei() ?: "").execute()
        }
        // new CheckOnline(this, info.getImei()).execute();
        // new CheckOnline(this, info.getImei()).execute();
        info = Info(this)
        prefEditor = getSharedPreferences(prefName, MODE_PRIVATE).edit() as SharedPreferences.Editor
        syncContact = sharedPref?.getBoolean("contactDialog", true) ?: true
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val intent = intent
        user_email = intent.getStringExtra(LoginActivity.USEREMAIL)
        user_pass = intent.getStringExtra(LoginActivity.USERPASS)
        outgoingSmsCardView = findViewById<View>(R.id.outgoing_sms_card_view) as CardView
        incomingSmsCardView = findViewById<View>(R.id.incoming_sms_card_view) as CardView
        outgoingCallCardView = findViewById<View>(R.id.outgoing_call_card_view) as CardView
        incomingCallCardView = findViewById<View>(R.id.incoming_call_card_view) as CardView
        task_card_view = findViewById<CardView>(R.id.task_card_view)
        pendingTaskClick = findViewById<LinearLayout>(R.id.pendingTaskClick)
        contact_card_view = findViewById<CardView>(R.id.contact_card_view)
        outgoingSmsCardView!!.setOnClickListener {
            val outoingSmsIntent = Intent(this, OutgoingSent_Activity::class.java)
            //                Intent outoingSmsIntent = new Intent(DashBoard_Activity.this, SmsLogActivity.class);
            outoingSmsIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(outoingSmsIntent)
        }
        incomingSmsCardView!!.setOnClickListener {
            val incomingSmsIntent = Intent(
                this,
                Posted_Inbox_Activity::class.java
            )
            incomingSmsIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(incomingSmsIntent)
            Log.i(TAG, "Incomming SMS card clicked")
        }
        outgoingCallCardView!!.setOnClickListener {
            val outgoingCallIntent = Intent(this, CallLog_Activity::class.java)
            outgoingCallIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            outgoingCallIntent.putExtra("type", "out")
            startActivity(outgoingCallIntent)
        }
        incomingCallCardView!!.setOnClickListener { //   Intent i_a = new Intent(DashBoard_Activity.this, CallLog_Activity.class);
            //  This part is commented for just a moment.
            val incomingCallIntent =
                Intent(this, PopupCallListActivity::class.java)
            incomingCallIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            incomingCallIntent.putExtra("type", "in")
            startActivity(incomingCallIntent)
        }
        task_card_view?.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this, TaskShowActivity::class.java)
                intent.putExtra("taskType", "All")
                startActivity(intent)
            }
        )
        pendingTaskClick?.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this, TaskShowActivity::class.java)
                intent.putExtra("taskType", "Pending")
                startActivity(intent)
            }
        )
        contact_card_view?.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this, ContactActivity::class.java)
                intent.putExtra("selection", false)
                startActivity(intent)
            }
        )
        if (intent.hasExtra("noInternet")) {
            val snackbar = Snackbar
                .make(
                    drawerLayout!!,
                    resources.getString(R.string.no_internet),
                    Snackbar.LENGTH_LONG
                )
                .setAction("RETRY") {
                    val snackbar1 = Snackbar.make(
                        (drawerLayout)!!,
                        resources.getString(R.string.no_internet),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar1.show()
                }
            snackbar.show()
        }
        if (intent.hasExtra("data")) {
            val extras = getIntent().extras
            id = extras!!.getString("data")
        }
        set_app_url()
        // First time after installing or Log in into app.
        // So, when syncContact is false alert dialog will not be prompted!
        if (syncContact) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Contact permission not granted")
                val PERMISSION_REQUEST_CONTACT = 304
                Log.i(TAG, "PERMISSION CODE: $PERMISSION_REQUEST_CONTACT")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS
                    ),
                    PERMISSION_REQUEST_CONTACT
                )
            } else {
                syncContact()
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                210
            )
        } else {
            val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            // imei = tm.getDeviceId();
            imei = Settings.Secure.getString(context!!.contentResolver, Settings.Secure.ANDROID_ID) as String
            Log.v("Device ID: ", imei ?: "")
            db?.deleteAdmin("did", "jhorotek")
            db?.addAdminNumber(Contact("did", imei, "jhorotek"))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 210) {
            // //////////////
            val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // return;
                openAlarDialog()
            } else {
                CheckOnline(this, info?.imei).execute()
                //                imei = tm.getDeviceId();
                imei =
                    Settings.Secure.getString(context!!.contentResolver, Settings.Secure.ANDROID_ID)
                Log.i(TAG, "Permission Result: IME $imei")
                db?.deleteAdmin("did", "jhorotek")
                db?.addAdminNumber(Contact("did", imei, "jhorotek"))
            }
        }
        if (requestCode == 304 && grantResults.size > 0) {
            Log.i(TAG, "Grant Result: " + grantResults[0])
            syncContact()
        }
    }

    private fun openAlarDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Onukit Account Policy")
        alertDialog.setMessage(R.string.account_policy_text)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(
            "I Accept"
        ) { dialog, which ->
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                210
            )
            dialog.cancel()
        }
        alertDialog.setNegativeButton(
            "CANCEL"
        ) { dialog, which ->
            System.exit(0)
            dialog.cancel()
        }
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        InitDashBoard()
    }

    fun syncContact() {
        Log.i(TAG, "Contact permission granted")
        val alertDialog = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("OnuKit")
        alertDialog.setMessage("Do you want to Sync Your Contacts?")
        alertDialog.setIcon(R.drawable.onukit_logo2)
        HttpEditInfo(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        alertDialog.setPositiveButton(
            "YES",
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    Log.i("JhoroContactList", "CList")
                    if (isNetworkAvailable) {
                        progressBar = ProgressDialog.show(
                            context as Activity?,
                            "Contacts",
                            "Contact Syncing..."
                        )
                        progressBar?.setCancelable(true)
                        progressBar?.show()
                        // getPhoneContactAndSyncProcess();
                        //getPhoneContactAndSyncProcess();
                        this.SendNumber(this).execute()
                        prefEditor!!.putBoolean("contactDialog", false)
                        prefEditor!!.commit()
                    } else {
                        val snackbar = Snackbar
                            .make(
                                drawerLayout!!,
                                resources.getString(R.string.no_internet),
                                Snackbar.LENGTH_LONG
                            )
                            .setAction("RETRY") {
                                val snackbar1 = Snackbar.make(
                                    (drawerLayout)!!,
                                    resources.getString(R.string.no_internet),
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar1.show()
                            }
                        snackbar.show()
                        // Toast.makeText(DashBoard_Activity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }
                }
            }
        )
        alertDialog.setNegativeButton(
            "NO"
        ) { dialog, which -> // I do 'false' here to stop the alert dialog in dashboard!
            prefEditor!!.putBoolean("contactDialog", false)
            prefEditor!!.commit()
            dialog.cancel()
        }
        alertDialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.dashboard) {
        } else if (id == R.id.setting) {
            val i = Intent(this, Setting_Activity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
        } else if (id == R.id.recentsms) {
            Recent_sms()
        } else if (id == R.id.contactDwr) {
            // Toast.makeText(this,"Click on contact",Toast.LENGTH_SHORT).show();
            // new PullServerContact(this,false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            val intent = Intent(this, ContactActivity::class.java)
            intent.putExtra("selection", false)
            startActivity(intent)
        } else if (id == R.id.smstoall) {
            val i = Intent(this, SmsActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(i)
        } else if (id == R.id.about) {
            val i = Intent(this, AboutActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
        } else if (id == R.id.webdashboard) {
            val i = Intent(this, WebViews::class.java)
            i.putExtra("url", "https://user.onukit.com/6v0/login_from_app/dashboard")
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
        } else if (id == R.id.logout) {
            val alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setTitle("OnuKit")
            alertDialog.setMessage("Do you want to Logout OnuKit ?")
            val input = TextView(this)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            input.layoutParams = lp
            alertDialog.setView(input)
            alertDialog.setIcon(R.drawable.onukit_logo2)
            alertDialog.setPositiveButton(
                "YES"
            ) { dialog, which ->
                Log.i("JhoroLogout", "Called")
                // new DeactivateDevice(DashBoard_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                val db = Database(this)
                db.deleteAllAdmin()
                db.deleteAll()
                db.deletealloutbox()
                db.deleteallsent()
                db.deleteallMsg()
                db.deleteAllCall()
                db.deletealloutboxtow()
                db.deleteAllsms()
                db.removeallthread() // Deleting all threads from TABLE_THREAD
                db.removeall_calls() // Deleting all incoming and outgoing calls from TABLE_DELETE
                db.clearAllTask() // Deleting all the tasks from TABLE_TASK
                db.removeAll_calls_queue()
                // these two line for Bulk Sms
                db.deleteAllOutBoxSmsData() // delete all ServerSms from TABLE_OUTBOXSMS
                db.deleteALlSentSms() // delete all ServerSms from TABLE_SENTSMS
                // Removing the notification from notification bar.
                val notificationManager =
                    applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(1245)
                sharedPrefManager?.logoutUser()
                // After clearing the shared preferances I'm setting the value false for not showing the RuntimePermissionActivity.
                sharedPrefManager?.setPermissionSlideStatus(false)
            }
            alertDialog.setNegativeButton(
                "NO"
            ) { dialog, which -> dialog.cancel() }
            alertDialog.show()
        } else if (id == R.id.syncData) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(TAG, "Contact permission not granted")
                val PERMISSION_REQUEST_CONTACT = 304
                Log.i(TAG, "PERMISSION CODE: $PERMISSION_REQUEST_CONTACT")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS
                    ),
                    PERMISSION_REQUEST_CONTACT
                )
            } else {
                syncContact()
            }
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    //    public void contactSync(){
    //        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashBoard_Activity.this);
    //        alertDialog.setTitle("OnuKit");
    //        alertDialog.setMessage("Do you want to Sync Your Contacts?");
    //        alertDialog.setIcon(R.drawable.onukit_logo2);
    //        new HttpEditInfo(DashBoard_Activity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    //        alertDialog.setPositiveButton("YES",
    //                new DialogInterface.OnClickListener() {
    //                    @Override
    //                    public void onClick(DialogInterface dialog, int which) {
    //                        Log.i("JhoroContactList", "CList");
    //                        if (isNetworkAvailable()) {
    //                            progressBar = ProgressDialog.show(DashBoard_Activity.this, "Contacts", "Sending...");
    //                            progressBar.setCancelable(true);
    //                            //getPhoneContactAndSyncProcess();
    //                            new SendNumber(DashBoard_Activity.this).execute();
    //                            //Toast.makeText(getApplicationContext(), "Successfully Send", Toast.LENGTH_LONG).show();
    //                        } else {
    //                            Snackbar snackbar = Snackbar
    //                                    .make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
    //                                    .setAction("RETRY", new View.OnClickListener() {
    //                                        @Override
    //                                        public void onClick(View view) {
    //                                            Snackbar snackbar1 = Snackbar.make(drawerLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
    //                                            snackbar1.show();
    //                                        }
    //                                    });
    //
    //                            snackbar.show();
    //                        }
    //                    }
    //                });
    //        alertDialog.setNegativeButton("NO",
    //                new DialogInterface.OnClickListener() {
    //                    @Override
    //                    public void onClick(DialogInterface dialog, int which) {
    //                        dialog.cancel();
    //                    }
    //                });
    //
    //        alertDialog.show();
    //    }
        override fun getUpdateDashboard(taskSummaryData: TaskSummaryData) {

//        Log.i(TAG,"Total: "+taskSummaryData.getTotalTask());
//        Log.i(TAG,"Pending: "+taskSummaryData.getPendingTask());
        val total: Int = taskSummaryData.totalTask.toInt()
        val pending: Int = taskSummaryData.pendingTask.toInt()
        totalTask = findViewById<TextView>(R.id.totalTask)
        pendingTask = findViewById<TextView>(R.id.pendingTask)
        var parcent = 100f
        try {
            parcent -= pending * 100 / total
        } catch (e: Exception) {
        }
        totalTask?.text = taskSummaryData.totalTask
        pendingTask?.text = taskSummaryData.pendingTask
        taskProgressView = findViewById(R.id.taskProgressView)
        taskProgressView?.setValue(parcent)
    }

    fun getContactSummeryDashboardUpdate(summaryData: ContactSummaryData) {
        Log.i(TAG, "Total contact: " + summaryData.totalContact)
        val total: Int = summaryData.totalContact.toInt()
        var client = 0
        var parcent = 0f
        for (group: ContactSummeryGroup in summaryData.group) {
            if (group.groupName.equals("Clients")) {
                client = group.numberOfContact.toInt()
                Log.i(TAG, "Total clients: " + group.groupName)
                Log.i(TAG, "Total clients: " + group.numberOfContact)
            }
        }
        try {
            parcent = (client * 100 / total).toFloat()
        } catch (e: Exception) {
            Log.i(TAG, "Contact Summery Excep: $e")
        }
        totalContact = findViewById<TextView>(R.id.totalContact)
        clientContacts = findViewById<TextView>(R.id.clientContacts)
        contactCircleProgressView = findViewById(R.id.contactCircleProgressView)
        totalContact?.text = total.toString()
        clientContacts?.text = client.toString()
        contactCircleProgressView?.setValue(parcent)
    }

    //    @Override
    //    public void toContactActivity(List<ContactDetails> contactList,boolean selectionOption) {
    //        this.contactList=contactList;
    //        Log.i(TAG,"Contact Name: "+contactList.get(0).getContactName());
    //        Log.i(TAG,"Contact Size: "+contactList.size());
    //
    //        Intent intent = new Intent(DashBoard_Activity.this, ContactActivity.class);
    //        intent.putExtra("contacts", (Serializable) contactList);
    //        intent.putExtra("selection",selectionOption);
    //        startActivity(intent);
    //    }
    inner class HttpEditInfo(var context: Context) :
        AsyncTask<Void?, Void?, String?>() {
        var TIMEOUT_MILLISEC = 5000
        var dialog: ProgressDialog? = null
        var activity: Activity? = null
        override fun onPreExecute() {
            Log.i("CList", "2")
            super.onPreExecute()
            // dialog = ProgressDialog.show(context, "Wait", "Please wait");
        }

        override fun onPostExecute(result: String?) {}
        protected override fun doInBackground(vararg params: Void?): String? {
            try {
                Log.i("CList", "3-1")
                val cr = contentResolver
                val cur = cr.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null
                )
                Log.i("CList", "3")
                if ((cur?.count ?: 0) > 0) {
                    while (cur?.moveToNext() == true) {
                        val idIndex = cur.getColumnIndex(ContactsContract.Contacts._ID)
                        val id = cur.getString(idIndex)?.takeIf { idIndex >= 0 } ?: continue

                        val nameIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        val name = cur.getString(nameIndex)?.takeIf { nameIndex >= 0 } ?: continue

                        val hasPhoneNumberIndex =
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                        if (hasPhoneNumberIndex >= 0 && (cur.getString(hasPhoneNumberIndex)
                                .toIntOrNull() ?: 0) > 0
                        ) {
                            val pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                arrayOf(id),
                                null
                            )
                            while (pCur?.moveToNext() == true) {
                                val phoneNoIndex =
                                    pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                val phoneNo = pCur.getString(phoneNoIndex)
                                if (phoneNo.length > 10) {
                                    Log.i("CList", "ID:$id  c:$name Phone No: $phoneNo Email: $email")
                                }
                            }
                            pCur?.close()
                        }
                    }
                    set_app_url()
                } else {
                    set_app_url()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    class SendNumber(private val context: Context) : AsyncTask<Void, Void, String>() {
        private val TAG = "SendNumber"
        private val progressBar: ProgressDialog = ProgressDialog(context)

        override fun onPreExecute() {
            super.onPreExecute()
            // progressBar = ProgressDialog.show(context, "Wait", "Please wait")
        }

        override fun onPostExecute(result: String) {
            if (progressBar.isShowing) {
                Log.i(TAG, "Progress dismiss")
                progressBar.dismiss()
            }
        }

        override fun doInBackground(vararg params: Void): String? {
            getAdminInfo()
            val cr: ContentResolver = context.contentResolver
            val cur: Cursor? = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            progressBar.setMessage("Total " + cur?.count + " contacts sending...")

            if (cur != null && cur.count > 0) {
                val contactsList: MutableList<Contacts?> = ArrayList()
                var counter = 0

                while (cur.moveToNext()) {
                    val idColumnIndex = cur.getColumnIndex(ContactsContract.Contacts._ID)
                    val id: String = if (idColumnIndex >= 0) cur.getString(idColumnIndex) else ""
                    val nameColumnIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val name: String =
                        if (nameColumnIndex >= 0) cur.getString(nameColumnIndex) else ""
                    val hasPhoneNumberColumnIndex =
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)
                    val hasPhoneNumber: String =
                        if (hasPhoneNumberColumnIndex >= 0) cur.getString(hasPhoneNumberColumnIndex) else ""

                    if (hasPhoneNumber.toInt() > 0) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )

                        val virtualContactList: MutableList<VirtualContact> = ArrayList()

                        while (pCur?.moveToNext() == true) {
                            val phoneNoColumnIndex =
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val phoneNo: String =
                                if (phoneNoColumnIndex >= 0) pCur.getString(phoneNoColumnIndex) else ""
                            Log.i(TAG, "Name: $name; Phone No.: $phoneNo")
                            val virtualContact = VirtualContact(phoneNo, "1")
                            virtualContactList.add(virtualContact)
                        }
                        pCur?.close()

                        val helper = Helper(context)
                        val userId = null
                        val parentID = "0"
                        val profile = Profile(
                            name, "", "", "", "", helper.getTime(), userId,
                            helper.getTime(), userId, "1", "", "0", parentID, "0"
                        )
                        val address = Address("", "", "", "", "", "", "", "", "", "")
                        val relation = Relation("", "")
                        val profession = Profession("", "", "")
                        val contacts = Contacts(profile, address, virtualContactList, relation, profession)
                        contactsList.add(contacts)
                    }
                }
                cur.close()

                val uname = null
                val upass = null
                val userId = null
                val contactSyncRepository = ContactSyncRepository(context, "https://api.onukit.com/contact/0v1/", uname, upass, userId)
                contactSyncRepository.postContacts(contactsList)
                if (progressBar.isShowing) {
                    Log.i(TAG, "Progress dismiss")
                    progressBar.dismiss()
                }

            } else {
                Log.i(TAG, "No contact exist!!!")
                if (progressBar.isShowing) {
                    Log.i(TAG, "Progress dismiss")
                    progressBar.dismiss()
                }
            }
            return null
        }
    }

    open fun set_app_url() {
        val db = Database(this)
        val contacts = db.adminNumber
        for (cn in contacts) {
            if (cn.name.equals("Custom_url")) {
                if (!cn.phone_number.equals("")) url = cn.phone_number + "/contactManagement"
                urlForEdition = cn.phone_number + "/getAppType"
                Log.e("From Dashboard ", "Url For Edition $urlForEdition")
                //url = "http://api.onukit.com/6v0/contactManagement";
            } else if (cn.name.equals("email")) {
                uname = cn.phone_number
            } else if (cn.name.equals("password")) {
                upass = cn.phone_number
            } else if (cn.name.equals("user_id")) {
                userId = cn.phone_number
            }
        }
    }

    open fun getAdminInfo() {
        val db = Database(this)
        val contacts = db.adminNumber
        for (cn in contacts) {
            if (cn.name.equals("email")) {
                uname = cn.phone_number
            } else if (cn.name.equals("password")) {
                upass = cn.phone_number
            } else if (cn.name.equals("user_id")) {
                userId = cn.phone_number
            } else if (cn.name.equals("parent_id")) {
                parentID = cn.phone_number
            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        Log.v("Jhoro", "request permission")
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.READ_CONTACTS
            )
        ) {
            Toast.makeText(
                context,
                "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                PERMISSION_REQUEST_CODE
            )
            Log.v("Jhoro", "request permission 1")
        }
    }

    private val isNetworkAvailable: Boolean
        private get() {
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

    /******************************************************
     * All Custom functions*********************
     */
    private fun Recent_sms() {
        if (db?.checklock("lockrecent") == true) {
            val alertDialog = android.app.AlertDialog.Builder(this)
            alertDialog.setTitle("Recent sms")
            alertDialog.setMessage("Enter Password:")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            input.layoutParams = lp
            alertDialog.setView(input)
            alertDialog.setIcon(R.drawable.onukit_logo2)
            alertDialog.setPositiveButton(
                "UNLOCK"
            ) { dialog, which ->
                val pass = input.text.toString()
                val db = Database(this)
                if (db.password(pass)) {
                    val i = Intent(this, RecentInbox_Activity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(i)
                    dialog.cancel()
                } else {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_LONG)
                        .show()
                }
            }
            alertDialog.setNeutralButton(
                "Cancle"
            ) { dialog, which -> dialog.cancel() }
            alertDialog.show()
        } else {
            Log.i(TAG, "Clicked")
            val i = Intent(this, RecentInbox_Activity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
        }
    }

    fun InitDashBoard() {
        info = Info(this)
        db = Database(this)
        if (topBarNotification) {
            TopNotification()
        }
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.setDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        val header = navigationView.getHeaderView(0)

        // Here I am Initiating PieChart instead of Image view of Battery.
        // Ending of initializing the PieCharts.
        outgoingSmsCircleProgressView =
            findViewById<View>(R.id.outgoingSmsCircleProgressView) as CircleProgressView
        incomingSmsCircleProgressView =
            findViewById<View>(R.id.incomingSmsCircleProgressView) as CircleProgressView
        outgoingCallCircleProgressView =
            findViewById<View>(R.id.outgoingCallCircleProgressView) as CircleProgressView
        incomingCallCircleProgressView =
            findViewById<View>(R.id.incomingCallCircleProgressView) as CircleProgressView

        // //
        mDemoSlider = findViewById<View>(R.id.slider) as SliderLayout
        refreshBtn = toolbar.findViewById<ImageView>(R.id.refreshbtn)
        //        infoBtn = (ImageButton) findViewById(R.id.infoButton);
        indicator = toolbar.findViewById<ImageView>(R.id.indicator)
        getAdminInfo()
        taskSummaryPullRepository =
            TaskSummaryPullRepository(context, "https://api.onukit.com/6v4/", uname, upass)
        taskSummaryPullRepository?.pullTaskSummary()
        contactSummaryPullRepository = ContactSummaryPullRepository(context)
        contactSummaryPullRepository?.pullContactSummary()
        inSms = findViewById<View>(R.id.incomingsms) as ImageView
        outSms = findViewById<View>(R.id.outgoingsms) as ImageView
        inCall = findViewById<View>(R.id.incomingcall) as ImageView
        outCall = findViewById<View>(R.id.outgoingcall) as ImageView
        SmsinSuccess = findViewById<View>(R.id.successInSms) as TextView

//        SmsinFailed = (TextView) findViewById(R.id.failedInSms);
        SmsinPending = findViewById<View>(R.id.pendingInSms) as TextView
        SmsOutPending = findViewById<View>(R.id.pendingOutSms) as TextView
        SmsOutSuccess = findViewById<View>(R.id.successOutsms) as TextView

//        SmsOutFailed = (TextView) findViewById(R.id.faildOutsms);
        CallinSuccess = findViewById<View>(R.id.successInCall) as TextView
        CallOutSuccess = findViewById<View>(R.id.successOutCall) as TextView
        pendingTaskSubmission = findViewById<TextView>(R.id.pendingTaskSubmission)

        //
//        clientContacts=findViewById(R.id.clientContacts);
//        totalContact=findViewById(R.id.totalContact);
        refreshBtn?.setOnClickListener(this)
        //        infoBtn.setOnClickListener(this);
        indicator?.setOnClickListener(this)
        StartBackgroundService()
        outSms!!.setOnClickListener(this)
        inCall!!.setOnClickListener(this)
        outCall!!.setOnClickListener(this)
        inSms!!.setOnClickListener {
            try {
                Log.i("error", "onclick")
                val i_c = Intent(this, Posted_Inbox_Activity::class.java)
                i_c.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(i_c)
            } catch (e: Exception) {
                Log.i("error", e.toString())
            }
        }
        db?.deleteAdmin("receiver", "jhorotek")
        db?.addAdminNumber(Contact("receiver", "on", "jhorotek"))

//        mAdView = findViewById<View>(R.id.adView) as AdView
//        adRequest = Builder().build()
//        mAdView.loadAd(adRequest)

        GetSliderImage()
        appEdition = header.findViewById<View>(R.id.edition) as TextView
        email = header.findViewById<View>(R.id.textView) as TextView
        SetSliderImages()
        updateInfo()
        updateQuota()
        updateDashboard()
        StartBroadcast()
        if (info?.isNetworkAvailable() == true) {
            FetchPermissions(this).execute()
        }
    }

    private fun updateQuota() {
        info?.getCallInQuota()?.toInt()
            ?.let { incomingCallCircleProgressView?.setValue(it.toFloat()) }
        info?.getCallOutQuota()?.toInt()
            ?.let { outgoingCallCircleProgressView?.setValue(it.toFloat()) }
        info?.getSmsInQuota()?.toInt()
            ?.let { incomingSmsCircleProgressView?.setValue(it.toFloat()) }
        info?.getSmsOutQuota()?.toInt()
            ?.let { outgoingSmsCircleProgressView?.setValue(it.toFloat()) }
    }

    fun StartBroadcast() {
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("updateData"))
    }

    // updateInfo() is for setting the navbar heading in the Dashboard.
    private fun updateInfo() {
        appEdition?.setText(info?.getUserType())
        email?.setText(info?.getUsername())
    }

    private fun GetSliderImage() {
        url_maps = HashMap()
        if (info?.isNetworkAvailable() == true) {
            val contacts: List<Contact> = db?.getAdminNumber()!!

            // This index variable is just for this function.
            var index = 0
            for (cn: Contact in contacts) {
                if (cn.getName().equals("img")) {

                    // Here I'm generating unique key (onuKit1, onuKit2, onuKit3) for the HashMap using the incremental
                    // 'index' variable!
                    url_maps!!["OnuKit" + (++index)] = cn.getPhone_number()
                }
            }
        }
    }

    private fun updateDashboard() {
        try {
            Log.i("Jhoro", "Update DashBoard")

            // //////////without animation ////////////////////////////
            if (info?.isNetworkAvailable() == true) {
                indicator!!.setImageResource(R.drawable.internet_connect_5)
            } else {
                indicator!!.setImageResource(R.drawable.cloud_computing_no_conn)
            }

            // //////tis animation by have some problem////////////////
//            indicator.setImageResource(R.drawable.listanim);
//            rocketAnimation= (AnimationDrawable)indicator.getDrawable();
//            if (info.isNetworkAvailable()) {
//                rocketAnimation.start();
//            } else {
//                if (rocketAnimation.isRunning()){
//                    rocketAnimation.stop();
//                }
//                indicator.setImageResource(R.drawable.cloud_computing_no_conn);
//            }
            // getSentCount
//        SmsOutSuccess.setText(info.getOutboxSentCount());
            SmsOutSuccess?.setText(java.lang.String.valueOf(db?.getSentSmsCount()))
            SmsinPending!!.text = db?.let { Integer.toString(it.getSmsCount()) }
            // SmsOutPending.setText(Integer.toString(db.getOutboxCounttow()));
            SmsOutPending?.setText(db?.let { java.lang.String.valueOf(it.getPendingSmsCount()).toString() })
            // SmsOutPending.setText(String.valueOf(String.valueOf(db.notprocessedsms())));
            // increible information
            Log.i("Jhoro", "sms success:" + (info?.getOutboxSentCount()))
            SmsinSuccess!!.text = db?.let { Integer.toString(it.getContactsCount()) }
            CallinSuccess?.setText(info?.getIncallCount())
            CallOutSuccess?.setText(info?.getOutcallCount())
            pendingTaskSubmission?.setText(java.lang.String.valueOf(db?.getTaskCount()))
            //        SmsOutFailed.setText("0");
//        SmsinFailed.setText("0");
//        Integer.toString(db.getTaskCount())
            updateQuota()
        } catch (e: Exception) {
            Log.i(TAG, "Exception on dashboard update: $e")
        }
    }

    private fun AlertInfo() {
        val alertDialog = android.app.AlertDialog.Builder(this)
        alertDialog.setTitle("OnuKit")
        alertDialog.setMessage(
            "This Green Signal indicates that you are connected to the internet. " +
                "If your internet is not connected please connect your wifi or mobile data"
        )
        alertDialog.setIcon(R.drawable.onukit_logo2)
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    private fun SetSliderImages() {
        for (name: String in url_maps!!.keys) {
            val textSliderView = TextSliderView(this)
            //            Toast.makeText(DashBoard_Activity.this,url_maps.get(name),Toast.LENGTH_LONG).show();
            // initialize a SliderLayout
            textSliderView
                .description(name)
                .image(url_maps!![name])
                .setScaleType(BaseSliderView.ScaleType.Fit)

            // add your extra information
            textSliderView.bundle(Bundle())
            textSliderView.getBundle()
                .putString("extra", name)
            mDemoSlider?.addSlider(textSliderView)
        }
        mDemoSlider?.setPresetTransformer(SliderLayout.Transformer.Accordion)
        mDemoSlider?.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
        mDemoSlider?.setCustomAnimation(DescriptionAnimation())
        mDemoSlider?.setDuration(4000)
    }

    private fun TopNotification() {
        val ii = Intent(this, PrivacyPolicy_Activity::class.java)
        ii.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val contentIntent = PendingIntent.getActivity(this, 0, ii, PendingIntent.FLAG_IMMUTABLE)
        val myNotification: Notification = NotificationCompat.Builder(this)
            .setContentTitle(getString(R.string.notification_heading))
            .setContentText(getString(R.string.notification_body))
            .setTicker("Notification!")
            .setContentIntent(contentIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setOngoing(true)
            .setSmallIcon(R.drawable.onukit_logo2)
            .build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1245, myNotification)
    }

    private fun Button_click_refresh() {
        // working for button effect
        refreshBtn!!.setBackgroundColor(Color.parseColor("#A900F0A4"))
        val handler = Handler()
        handler.postDelayed(
            { refreshBtn!!.setBackgroundColor(Color.parseColor("#A100AEF0")) },
            1000
        )
        // button effect end
        if (info?.isNetworkAvailable() == true) {
            progressBar =
                ProgressDialog.show(this, "Fetching Data", "Loading...")
            progressBar?.setCancelable(true)
            object : CountDownTimer(2000, 1000) {
                override fun onFinish() {
                    try {
                        if (progressBar?.isShowing() == true) {
                            progressBar?.dismiss()
                        }
                    } catch (e: Exception) {
                    }
                }

                override fun onTick(millisUntilFinished: Long) {}
            }.start()

            // commented for bulk sms
//            new PullSmsFromServer(DashBoard_Activity.this, info.getImei()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            PullServerSms(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            CheckOnline(
                this,
                info?.getImei()
            ).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            // new DeliveryReportPoster(DashBoard_Activity.this, info.getImei()).execute();
            // Toast.makeText(DashBoard_Activity.this, "Acknowledgement sent ", Toast.LENGTH_LONG).show();
        } else {
            val snackbar = Snackbar
                .make(
                    drawerLayout!!,
                    resources.getString(R.string.no_internet),
                    Snackbar.LENGTH_LONG
                )
                .setAction("RETRY") {
                    val snackbar1 = Snackbar.make(
                        (drawerLayout)!!,
                        resources.getString(R.string.no_internet),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar1.show()
                }
            snackbar.show()

            //   Toast.makeText(DashBoard_Activity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private fun Button_info() {
        val stMyWeb = SpannableString(
            "As your apps is installed now you can enjoy the functionality of " +
                "onuKit browsing the cloud panel, " +
                "user.onukit.com from your pc. " +
                "Use the same login credentials you used to login into this app.\n" +
                "\n" +
                "If you need more information please visit: onukit.com or\n" +
                "fb.com/onuKit\n" +
                "For live support please call: +8801714890252\n" +
                "Skype: onukit\n"
        )
        Linkify.addLinks(stMyWeb, Linkify.ALL)
        val aboutDialog: AlertDialog = AlertDialog.Builder(this)
            .setMessage(stMyWeb)
            .setIcon(R.drawable.onukit_logo2)
            .setTitle("OnuKit")
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    // TODO Auto-generated method stub
                }
            )
            .create()
        aboutDialog.show()
        (aboutDialog.findViewById<View>(R.id.message) as TextView?)
            .setMovementMethod(LinkMovementMethod.getInstance())
    }

    fun StartBackgroundService() {
        mSensorService = BackgroundService(context)
        mServiceIntent = Intent(baseContext, mSensorService.getClass())
        if (!isMyServiceRunning(mSensorService.getClass())) {
            Log.i("JhoroService", "BootupReceiver")
            startService(mServiceIntent)
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service: ActivityManager.RunningServiceInfo in manager.getRunningServices(Int.MAX_VALUE)) {
            if ((serviceClass.name == service.service.className)) {
                Log.i("isMyServiceRunning?", true.toString() + "")
                return true
            }
        }
        Log.i("isMyServiceRunning?", false.toString() + "")
        return false
    }

    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateDashboard()
        }
    }

    companion object {
        //    private String url = "http://demo.onukit.com/api/contactManagement";
        private val PERMISSION_REQUEST_CODE = 11
        fun updateExternalStorageState(): Int {
            val state = Environment.getExternalStorageState()
            if ((Environment.MEDIA_MOUNTED == state)) {
                return Constants.MEDIA_MOUNTED
            } else return if ((Environment.MEDIA_MOUNTED_READ_ONLY == state)) {
                Constants.MEDIA_MOUNTED_READ_ONLY
            } else {
                Constants.NO_MEDIA
            }
        }
    }
}
