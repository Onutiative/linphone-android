package org.linphone.activities

import android.content.Context
import android.content.Intent
import android.icu.text.IDNA
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.github.ybq.android.spinkit.style.ThreeBounce
import com.jakewharton.processphoenix.ProcessPhoenix
import com.skyfishjy.library.RippleBackground
import java.util.Timer
import java.util.TimerTask
import org.linphone.R
import org.linphone.activities.main.MainActivity

class SplashScreen_Activity : AppCompatActivity() {
    var myTimer: Timer? = null
    var i = 0
    private val rippleBackground: RippleBackground? = null
    private val info: IDNA.Info? = null
    private val context: Context? = null

    // private SharedPrefManager sharedPrefManager;
    private val TAG = SplashScreen_Activity::class.java.simpleName
    private val flagCheck = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splashyo)

//        ActionBar bar=getSupportActionBar();
//        bar.hide();

//        context= SplashScreen_Activity.this;
//        sharedPrefManager=new SharedPrefManager(context);
//        info = new IDNA.Info(SplashScreen_Activity.this);
        myScreen()
        val progressBar = findViewById<View>(R.id.spin_kit) as ProgressBar
        val threeBounce = ThreeBounce()
        progressBar.indeterminateDrawable = threeBounce
        myTimer = Timer()
        myTimer!!.schedule(
            object : TimerTask() {
                override fun run() {
                    TimerMethod()
                }
            },
            0, 1000
        )
    }

    private fun initializeView() {}
    fun TimerMethod() {
        i++
        if (i == 4) {
            // rippleBackground.stopRippleAnimation();
            // checkstatus();
            // go to MainActivity
//            val intent = Intent(this@SplashScreen_Activity, MainActivity::class.java)
//            startActivity(intent)
            ProcessPhoenix.triggerRebirth(this, Intent(this@SplashScreen_Activity, MainActivity::class.java))
        }
    }

    fun myScreen() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val heightPixels = metrics.heightPixels
        val widthPixels = metrics.widthPixels
        val densityDpi = metrics.densityDpi
        val xdpi = metrics.xdpi
        val ydpi = metrics.ydpi
        Log.i("JhoroScreen", "widthPixels  = $widthPixels")
        Log.i("JhoroScreen", "heightPixels = $heightPixels")
        Log.i("JhoroScreen", "densityDpi   = $densityDpi")
        Log.i("JhoroScreen", "xdpi         = $xdpi")
        Log.i("JhoroScreen", "ydpi         = $ydpi")
    }

    companion object {
        private const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    }
}
