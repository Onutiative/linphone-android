package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.SharedPrefManager;

import java.util.List;

public class RuntimePermissionActivity extends AppCompatActivity {



    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext, btnSkip;
    private Context context;
    private MyViewPagerAdapter myViewPagerAdapter;
    private String TAG = "RuntimePermissionActivity";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private SharedPrefManager sharedPrefManager;
    private int current;
    private int requestPermissionscall_count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

       setContentView(R.layout.activity_runtime_permission);


        context = RuntimePermissionActivity.this;
        sharedPrefManager=new SharedPrefManager(context);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        linearLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
                R.layout.welcome_slide5,
                R.layout.welcome_slide6,
                R.layout.welcome_slide7
        };

        addBottomDots(0);
        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        Intent intent = getIntent();
        int slideNo=intent.getIntExtra("sliderNo",0);
        String from="";
        from=intent.getStringExtra("from");
        viewPager.setCurrentItem(slideNo);
        if (slideNo>0){
            switch (slideNo){
                case 1:{
                    int callPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                    if (callPermissionResult==0){
                        permissionResult("Call",true);
                    }else {
                        permissionResult("Call",false);
                    }
                    break;
                }
                case 2:{
                    int smsPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                    if (smsPermissionResult==0){
                        permissionResult("SMS",true);
                    }else {
                        permissionResult("SMS",false);
                    }
                    break;
                }

                case 3:{
                    int contactPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
                    if (contactPermissionResult==0){
                        permissionResult("Contact",true);
                    }else permissionResult("Contact",false);
                    break;
                }

                case 4:{
                    int microphonePermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
                    if (microphonePermissionResult==0){
                        permissionResult("Microphone",true);
                    }else {
                        permissionResult("Microphone",false);
                    }
                    break;
                }

                case 5:{
                    int storagePermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (storagePermissionResult==0){
                        permissionResult("Storage",true);
                    }else {
                        permissionResult("Storsge",false);
                    }
                    break;
                }

                case 6: {
                    boolean popupPermissionResult;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        popupPermissionResult = Settings.canDrawOverlays(context);
                    } else {
                        popupPermissionResult = true;
                    }
                    if (popupPermissionResult) {
                        permissionResult("Pop-Up", true);
                    } else {
                        permissionResult("Pop-Up", false);
                    }
                    break;
                }
            }
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = getItem(+1);
                //Toast.makeText(context, "Current is " + current, Toast.LENGTH_LONG).show();
                if (current <= layouts.length) {
                    //viewPager.setCurrentItem(current);

                    Intent intent = getIntent();
                    String from="def";
                    from=intent.getStringExtra("from");

                    Log.i(TAG,"Come From: "+from);
                    if (current == 1) {
                        viewPager.setCurrentItem(current);
                    } else if (current == 2) {
                        int callPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                        Log.i(TAG,"callPermissionResult: "+callPermissionResult);

                        if (from.equals("permissionResult") && callPermissionResult==-1){
                            rePermissionTake(2);
                            Log.i(TAG,"Through permissionResult");
                        }else {
                            requestPhoneCallPermission();
                            Log.i(TAG,"Through Normal");
                        }
                    } else if (current == 3) {
                        int smsPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
                        Log.i(TAG,"smsPermissionResult: "+smsPermissionResult);

                        if (from.equals("permissionResult") && smsPermissionResult==-1){
                            rePermissionTake(3);
                            Log.i(TAG,"Through permissionResult");
                        }else{
                            requestSendSMSPermission();
                            Log.i(TAG,"Through Normal");
                        }
                    } else if (current == 4) {
                        int contactPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
                        Log.i(TAG,"contactPermissionResult: "+contactPermissionResult);

                        if (from.equals("permissionResult") && contactPermissionResult==-1){
                            rePermissionTake(4);
                            Log.i(TAG,"Through permissionResult");
                        }else {
                            requestContactPermission();
                            Log.i(TAG,"Through Normal");
                        }
                    } else if (current == 5) {
                        int audioPermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
                        Log.i(TAG,"audioPermissionResult: "+audioPermissionResult);

                        if (from.equals("permissionResult") && audioPermissionResult==-1){
                            rePermissionTake(5);
                            Log.i(TAG,"Through permissionResult");
                        }else{
                            requestRecordAudioPermission();
                            Log.i(TAG,"Through Normal");
                        }
                    } else if (current == 6) {
                        int storagePermissionResult= ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        Log.i(TAG,"storagePermissionResult: "+storagePermissionResult);

                        if (from.equals("permissionResult") && storagePermissionResult==-1){
                            rePermissionTake(6);
                            Log.i(TAG,"Through permissionResult");
                        }else{
                            requestReadWriteStoragePermission();
                            Log.i(TAG,"Through Normal");
                        }
                    } else if (current == 7) {
                        boolean popupPermissionResult;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            popupPermissionResult = Settings.canDrawOverlays(context);
                        } else {
                            popupPermissionResult = true;
                        }
                        if (from.equals("permissionResult") &&!popupPermissionResult){
                            requestDrawOverAllAppsPermission();
                            Log.i(TAG,"Through permissionResult");
                        }else {
                            requestDrawOverAllAppsPermission();
                            Log.i(TAG,"Through Normal");
                        }
                    }
                } else {
//                    launchHomeScreen();
//                    requestDrawOverAllAppsPermission();
                }
            }
        });
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            current = position;
            if(position>0)
            {
                btnNext.setTextColor(Color.WHITE);
            }
            if (position == 0) {
                btnNext.setTextColor(Color.BLACK);
            } else if (position == 1) {

            } else if (position == 2) {
                requestPhoneCallPermission();
            } else if (position == 3) {
                requestSendSMSPermission();
            } else if (position == 4) {
                requestContactPermission();
            } else if (position == 5) {
                requestRecordAudioPermission();
            } else if (position == 6) {
                requestReadWriteStoragePermission();
            } else {
                requestDrawOverAllAppsPermission();
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    void rePermissionTake(int position){
        switch (position){
            case 0:
                break;
            case 1:
                break;
            case 2:
                int PERMISSION_REQUEST_CALL=302;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_CALL);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE,
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.PROCESS_OUTGOING_CALLS,
                                Manifest.permission.READ_CALL_LOG},
                        PERMISSION_REQUEST_CALL);
                break;
            case 3:
                int PERMISSION_REQUEST_SMS=303;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_SMS);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS},
                        PERMISSION_REQUEST_SMS);
                break;
            case 4:
                int PERMISSION_REQUEST_CONTACT=304;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_CONTACT);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS,
                                Manifest.permission.GET_ACCOUNTS},
                        PERMISSION_REQUEST_CONTACT);
                break;
            case 5:
                int PERMISSION_REQUEST_AUDIO=305;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_AUDIO);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST_AUDIO);
                break;
            case 6:
                int PERMISSION_REQUEST_STORAGE=306;
                Log.i(TAG,"PERMISSION CODE: "+PERMISSION_REQUEST_STORAGE);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_STORAGE);
                break;
            case 7:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG,"Resulted code: "+requestCode);
        requestPermissionscall_count=requestCode;
        if (requestPermissionscall_count==requestCode){
            switch (requestCode){
                case 302:
                    viewPager.setCurrentItem(2);
                    break;
                case 303:
                    viewPager.setCurrentItem(3);
                    break;
                case 304:
                    viewPager.setCurrentItem(4);
                    break;
                case 305:
                    viewPager.setCurrentItem(5);
                    break;
                case 306:
                    viewPager.setCurrentItem(6);
                    break;
            }
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.dot_active);
        int[] colorInactive = getResources().getIntArray(R.array.dot_inactive);

        linearLayout.removeAllViews();
        for (int i = 0; i < layouts.length; ++i) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive[currentPage]);
            linearLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private void launchHomeScreen() {
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    private void requestSendSMSPermission() {
        Log.i(TAG,"requestSendSMSPermission");

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.RECEIVE_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            showSuccessMessage("Send SMS");
                            viewPager.setCurrentItem(current);
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            //showSettingsDialog();
                            viewPager.setCurrentItem(current);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        viewPager.setCurrentItem(current);
                        token.cancelPermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void requestRecordAudioPermission() {
        Log.i(TAG,"requestRecordAudioPermission");
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        showSuccessMessage("Record Audio");
                        viewPager.setCurrentItem(current);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            //showSettingsDialog();
                            viewPager.setCurrentItem(current);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        viewPager.setCurrentItem(current);
                        token.cancelPermissionRequest();
                    }
                }).check();

    }

    private void requestReadWriteStoragePermission() {
        Log.i(TAG,"requestReadWriteStoragePermission");
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            showSuccessMessage("External Storage");
                            viewPager.setCurrentItem(current);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            //showSettingsDialog();
                            viewPager.setCurrentItem(current);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        viewPager.setCurrentItem(current);
                        token.cancelPermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void requestPhoneCallPermission() {
        Log.i(TAG,"requestPhoneCallPermission");
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.READ_CALL_LOG
                            )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            showSuccessMessage("Phone Call");
                            viewPager.setCurrentItem(current);
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            //showSettingsDialog();
                            viewPager.setCurrentItem(current);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        viewPager.setCurrentItem(current);
                        token.cancelPermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void requestContactPermission() {
        Log.i(TAG,"requestContactPermission");
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.GET_ACCOUNTS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            showSuccessMessage("Read Contacts");
                            viewPager.setCurrentItem(current);
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            //showSettingsDialog();
                            viewPager.setCurrentItem(current);
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        viewPager.setCurrentItem(current);
                        token.cancelPermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void requestDrawOverAllAppsPermission() {
        Log.i(TAG,"requestDrawOverAllAppsPermission");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            Intent intent = new Intent(context, PermissionResultActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            //Check if the permission is granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                showSuccessMessage("Draw all over the apps");
                sharedPrefManager.setPopupStatus(true);
                Log.d(TAG,"Draw all over the apps permission granted!");

            } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) { //Permission is not available
                //Toast.makeText(this, "Draw all over the apps permission not granted!", Toast.LENGTH_LONG).show();
                sharedPrefManager.setPopupStatus(false);
                Log.d(TAG,"Draw all over the apps permission not granted!");
            }
            Intent intent = new Intent(context, PermissionResultActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showSuccessMessage(String permissionType) {
        //Toast.makeText(context, permissionType + " permission granted", Toast.LENGTH_SHORT).show();
        Log.i(TAG, permissionType+" permission granted");
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
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
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    void permissionResult(String permission, boolean status){
        View view =findViewById(R.id.activity_permission);
        if (status){
            //Toast.makeText(this,permission+" permission granted!",Toast.LENGTH_SHORT).show();
            Snackbar.make(view,permission+" permission granted!",Snackbar.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this,permission+" permission not granted!",Toast.LENGTH_SHORT).show();
            Snackbar.make(view,permission+" permission not granted!",Snackbar.LENGTH_SHORT).show();
        }
    }

}
