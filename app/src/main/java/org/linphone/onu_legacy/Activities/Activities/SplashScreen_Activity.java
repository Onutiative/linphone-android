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
import com.onutiative.onukit.AsyncTasking.CheckOnline;
import com.onutiative.onukit.AsyncTasking.FetchImage;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.Info;
import com.onutiative.onukit.Utility.SharedPrefManager;
import com.skyfishjy.library.RippleBackground;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen_Activity extends AppCompatActivity {

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashyo);

        ActionBar bar=getSupportActionBar();
        bar.hide();

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

                Intent i = new Intent(SplashScreen_Activity.this, DashBoard_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        }

        if(sharedPrefManager.getPermissionSlideStatus()&& flagCheck)
        {
            Log.d(TAG,"Runtime Permission Logging!");

            Intent i = new Intent(SplashScreen_Activity.this, RuntimePermissionActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("from","splash");
            startActivity(i);
            finish();
        }
        else if(!sharedPrefManager.getPermissionSlideStatus() && flagCheck)
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