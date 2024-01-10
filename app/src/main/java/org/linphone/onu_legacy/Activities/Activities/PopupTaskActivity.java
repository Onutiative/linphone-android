package org.linphone.onu_legacy.Activities.Activities;
//<!--used in 6v3-->

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import org.linphone.onu_legacy.Adapters.PagerAdapter;
import org.linphone.onu_legacy.Database.Task;
import org.linphone.onu_legacy.Fragments.TaskFormFragment;
import org.linphone.onu_legacy.Fragments.TaskListFragment;
import org.linphone.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import fr.ganfra.materialspinner.MaterialSpinner;

public class PopupTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TaskFormFragment.OnFragmentInteractionListener, TaskListFragment.OnFragmentInteractionListener
        //SaveNewTask.ViewPagerListener
        //TaskFormFragment.PassDateLiListener
{


    private static final String[] CALL_TYPE = {"Complain", "Customer Support", "Query", "Others"};
    private static final String[] STATUS_TYPE = {"N/A", "Pending", "Solved", "Archived"};


    private ArrayAdapter<String> callTypeAdapter, statusTypeAdapter;

    MaterialSpinner callTypeSpinner, statusTypeSpinner;

    private String phoneNo="",timeStamp,callerName="";

    private TextView textViewPhoneNo, textViewTimeStamp;

    private EditText editTextAssigneeEmail, editTextEstimatedTime;
    private Context context;
    private String TAG = "PopupTaskActivity";
    public static Task newTask = null;
    private PagerAdapter adapter;

    private TextView textViewCallerPhoneNo, textViewCallerName;
    private ImageView callIcon;
    private ImageView homeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_task);

        //try {
        //            Objects.requireNonNull(getSupportActionBar()).hide();
        //        } catch (Exception e){
        //            e.printStackTrace();
        //        }

//        this.getSupportActionBar().setDisplayOptions(Toolbar.);
//
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.custom_pop_task_actionbar);
//        //getSupportActionBar().setElevation(1);
//        View view = getSupportActionBar().getCustomView();

        Toolbar toolbar = findViewById(R.id.popup_toolbar);
        setSupportActionBar(toolbar);

        textViewCallerPhoneNo = (TextView) toolbar.findViewById(R.id.callerPhoneNo);
        textViewCallerName = (TextView) toolbar.findViewById(R.id.callerNameTaskList);
        callIcon = (ImageView) toolbar.findViewById(R.id.icon_call);
        homeIcon = toolbar.findViewById(R.id.icon_home);

        context = PopupTaskActivity.this;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("New Task"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        Intent intent = getIntent();

        // Log all raw data from the intent
//        Bundle bundle = intent.getExtras();
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Object value = bundle.get(key);
//                // if value isn't null, print it
//                if (value != null){
//                    Log.d(TAG, String.format("%s %s (%s)", key,
//                            value.toString(), value.getClass().getName()));
//                }
//            }
//        }

        try {
            String from = intent.getStringExtra("from");
            //Log.i(TAG,"From: "+ from);
            //if (!from.equals("newTask"))
            {
                phoneNo = intent.getStringExtra("phoneNo");
                callerName=intent.getStringExtra("callerName");
                // Log.i(TAG,"From new task");

                if(from == null) {
                    from = "";
                }

                Log.i(TAG,"From: "+ from);

                switch (from) {
                    case "inbox":
                    case "newTask":
                    case "reassignTaskList":
                    case "contact":
                        callIcon.setVisibility(View.GONE);
                        viewPager.setCurrentItem(1);
                        break;
                    case "reassign":
                        callIcon.setVisibility(View.GONE);
                        String taskID = intent.getStringExtra("taskID");
                        Log.i(TAG, "From reassign and for: " + taskID);
                        viewPager.setCurrentItem(1);
                        break;
                    case "inboxSave":
                        callIcon.setVisibility(View.GONE);
                        viewPager.setCurrentItem(0);
                        break;
                    default:
                        viewPager.setCurrentItem(0);
                        break;
                }
            }
//            else {
//                viewPager.setCurrentItem(1);
//            }

        } catch (Exception e) {
            //Log.i(TAG,e.toString());
        }

        //set action bar values and set operation
//        if (phoneNo.isEmpty()){
//            Log.i(TAG,"No phone number");
//            callIcon.setVisibility(View.GONE);
//        }else {
//            callIcon.setVisibility(View.VISIBLE);
//        }
//        if (callerName.isEmpty()){
//            Log.i(TAG,"No caller name");
//        }
        textViewCallerName.setText(callerName);
        textViewCallerPhoneNo.setText(phoneNo);

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCallPermission();
            }
        });

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupTaskActivity.this, DashBoard_Activity.class);
                startActivity(intent);
            }
        });

        textViewCallerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1);
            }
        });

        //tab layout operation
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //viewPager.setCurrentItem(tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
//              Toast.makeText(context,"Tab Selection for Number "+tab.getPosition(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        String newDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        editTextEstimatedTime.setText(newDate);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    private void requestCallPermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        makePhoneCall(phoneNo);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public void makePhoneCall(String phoneNumber) {
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:" + phoneNumber));
        call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        startActivity(call);
//        finish();
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant it in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
