package org.linphone.onu_legacy.MVP.Implementation.AgreementPolicy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import org.linphone.onu_legacy.Activities.Activities.RuntimePermissionActivity;
import org.linphone.R;

public class AgreementActivity extends AppCompatActivity implements View.OnClickListener {

    private Button cancelButton, nextButton;
    private CheckBox iAgreeCheckbox;
    private WebView policyWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);

//        ActionBar bar=getSupportActionBar();
//        bar.hide();

        iAgreeCheckbox = findViewById(R.id.agreeCheckBox);
        cancelButton = findViewById(R.id.button_cancel);
        nextButton = findViewById(R.id.button_next);
        policyWebview=findViewById(R.id.policyWebview);

        if (isNetworkAvailable()){
            policyWebview.loadUrl("https://onukit.com/user-policy-2/");
        }else {
            Toast.makeText(this,"No internet cpnnection!",Toast.LENGTH_SHORT).show();
        }

        iAgreeCheckbox.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.agreeCheckBox){
            if(nextButton.isEnabled()){
                nextButton.setEnabled(false);
            }else{
                nextButton.setEnabled(true);
            }
        }else if(v.getId() == R.id.button_cancel){
            appExitWarning();
        }else if(v.getId() == R.id.button_next){
            Intent intent = new Intent(this, RuntimePermissionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void appExitWarning(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Warning");
        dialog.setMessage(getResources().getString(R.string.agree_msg));
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
