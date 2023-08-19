package org.linphone.onu_legacy.WebViews;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.Info;

public class WebViews extends AppCompatActivity {

    private WebView web;
    public ProgressDialog progressBar;
    public AlertDialog alertDialog;
    private String url="";
    private String user_name="", user_pass="";
    public static final String USER_AGENT_FAKE = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    private RelativeLayout relativeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_views);
        getSupportActionBar().hide();
        url = getIntent().getStringExtra("url");
        //  alertDialog = new AlertDialog.Builder(this.create());
        progressBar = ProgressDialog.show(this, "OnuKit web", "Loading...");
        relativeLayout=(RelativeLayout)findViewById(R.id.activity_web_views);

        // Here is the Power. Just made an object of Info class passing the Context and get the informations you need!

        Info info=new Info(WebViews.this);
        user_name=info.getUsername();
        user_pass=info.getPassword();

//        Toast.makeText(this,user_name+" "+user_pass,Toast.LENGTH_SHORT).show();

        if(!isNetworkAvailable())
        {
         //   Toast.makeText(this,getResources().getString(R.string.no_internet),Toast.LENGTH_LONG).show();





            Intent i=new Intent(WebViews.this,DashBoard_Activity.class);
            i.putExtra("noInternet","NoInternetConnection");
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

            Snackbar snackbar = Snackbar
                    .make(relativeLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Snackbar snackbar1 = Snackbar.make(relativeLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                            snackbar1.show();
                        }
                    });

            snackbar.show();

        }
        ImageButton btn= (ImageButton) findViewById(R.id.homebtn);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i=new Intent(WebViews.this,DashBoard_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        web = (WebView) findViewById(R.id.webs);


        WebSettings setting= web.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setUserAgentString(USER_AGENT_FAKE);
        web.setFocusable(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        web.loadUrl(url);
        CookieManager.getInstance().setAcceptCookie(true);



        web.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
               handler.proceed(user_name,user_pass);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.endsWith(".mp4")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                    startActivity(intent);
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }



            public void onPageFinished(WebView view, String url) {
                //Log.i(TAG, "Finished loading URL: " +url);

           //     Toast.makeText(WebViews.this,"Page has finished loading!", Toast.LENGTH_SHORT).show();

                if (progressBar.isShowing()) {
                    progressBar.dismiss(); }
            }





        });





//        web.setWebViewClient(new MyWebViewClient());


    }

//    private class MyWebViewClient extends WebViewClient{
//
//        @Override
//        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//            handler.proceed("promotion@onukit.com","welcome@2018");
//        }
//    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(web.canGoBack())
                    {
                        web.goBack();
                    }else{
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
