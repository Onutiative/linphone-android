//UNUSED ACTIVITY
package org.linphone.onu_legacy.Activities.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.style.ThreeBounce;

import org.linphone.activities.main.MainActivity;
import org.linphone.onu_legacy.AsyncTasking.CheckOnline;
import org.linphone.onu_legacy.AsyncTasking.FetchImage;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.Info;
import org.linphone.onu_legacy.Utility.SharedPrefManager;
import com.skyfishjy.library.RippleBackground;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen_Activity extends AppCompatActivity {
    public static final String ACTIVITY_CLUE = "activity_clue";
    public Timer myTimer;
    public int i = 0;
    private RippleBackground rippleBackground;
    private Info info;

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    private Context context;
    private SharedPrefManager sharedPrefManager;
    private String TAG=SplashScreen_Activity.class.getSimpleName();
    private boolean flagCheck=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashyo);

        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        } catch (Exception e){
            e.printStackTrace();
        }

        context= SplashScreen_Activity.this;
        sharedPrefManager=new SharedPrefManager(context);
        info = new Info(this);

        myScreen();

        ProgressBar progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        ThreeBounce threeBounce=new ThreeBounce();
        progressBar.setIndeterminateDrawable(threeBounce);

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);
    }

    private void initializeView() {
    }


    public void TimerMethod() {
        i++;
        if (i == 4) {
            //rippleBackground.stopRippleAnimation();
            checkstatus();
        }
    }

    public void checkstatus() {

        Log.d(TAG,"Check Status Logging!");

        Database db = new Database(getApplicationContext());
        List<Contact> contacts = db.getAdminNumber();

        Log.e("Contact", "Testing Phase");

        for (Contact cn : contacts) {

            Log.d(TAG,"Loop Logging!");

            Log.e("Contact List", " Contact List " + cn.getName() + " " + cn.getPhone_number() + " " + cn.getTime());
            Log.i("Contact List", " Contact List " + cn.getName() + " " + cn.getPhone_number() + " " + cn.getTime());

            if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true")) {
                Log.e("Contact List Special", cn.getName() + " " + cn.getPhone_number() + " " + cn.getTime());

                try {
                    //new FetchImage(this).execute();
                    new CheckOnline(this, info.getImei()).execute();
                    new FetchImage(SplashScreen_Activity.this).execute();
                } catch (Exception e) {
                    Log.i("Jhoro", "Splash Exception: " + e);
                }

                Log.d(TAG,"Account is logged in.");
                flagCheck=false;

                Intent i = new Intent(SplashScreen_Activity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }

        Log.i("Jhoro", "SplashScreen_Activity: " + sharedPrefManager.getPermissionSlideStatus() + " | flagCheck: " + flagCheck);

        if(!sharedPrefManager.getPermissionSlideStatus())
        {
            Log.d(TAG,"Runtime Permission Logging!");

            Intent i = new Intent(SplashScreen_Activity.this, RuntimePermissionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("from","splash");
            startActivity(i);
            finish();
        }
        else if(sharedPrefManager.getPermissionSlideStatus())
        {

            Log.d(TAG,"SignUpGoogle Logging!");

            Intent i = new Intent(SplashScreen_Activity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    public void myScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;
        Log.i("JhoroScreen", "widthPixels  = " + widthPixels);
        Log.i("JhoroScreen", "heightPixels = " + heightPixels);
        Log.i("JhoroScreen", "densityDpi   = " + densityDpi);
        Log.i("JhoroScreen", "xdpi         = " + xdpi);
        Log.i("JhoroScreen", "ydpi         = " + ydpi);
    }
}