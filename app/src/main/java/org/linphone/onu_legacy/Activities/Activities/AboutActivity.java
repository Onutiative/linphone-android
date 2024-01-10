
//used on 6v3
package org.linphone.onu_legacy.Activities.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.linphone.R;
import org.linphone.onu_legacy.Utility.Info;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    private Info info;
    private String TAG="AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        } catch (Exception e){
            e.printStackTrace();
        }
        info = new Info(AboutActivity.this);
        TextView aboutTextview = (TextView)findViewById(R.id.abouttext);
        WebView aboutWebView = (WebView) findViewById(R.id.aboutWebView);
        //aboutTextview.setText(info.getAboutText());

        Log.i(TAG,info.getAboutText());

        if (isNetworkConnected()){
            aboutTextview.setVisibility(View.GONE);
            aboutWebView.setVisibility(View.VISIBLE);
            aboutWebView.loadUrl("https://onukit.com/about-us/");
        } else {
            aboutTextview.setVisibility(View.VISIBLE);
            aboutWebView.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
