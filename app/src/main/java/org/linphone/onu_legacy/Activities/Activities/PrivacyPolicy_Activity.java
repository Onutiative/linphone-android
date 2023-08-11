package org.linphone.onu_legacy.Activities.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.Info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PrivacyPolicy_Activity extends AppCompatActivity {

    public int im=0;
    Animation zoom;
    private Info info;
    private Context context;
    private Activity activity;
    private static final int request_code = 11;
    ArrayList<String> permissions=new ArrayList<>();
    ArrayList<String> listPermissionsNeeded;
    private View view;
   // private Typeface typeface2;


    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        zoom = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadin);
        info =new Info(this);
        getSupportActionBar().hide();
        //typeface2 = Typeface.createFromAsset(this.getAssets(),"fonts/nasalization.ttf");
        view=(View) findViewById(R.id.linis);
        context=PrivacyPolicy_Activity.this;

        linearLayout=(LinearLayout)findViewById(R.id.linis);



        activity=this;
        checkstatus();






//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        permissions.add(Manifest.permission.RECORD_AUDIO);
//        permissions.add(Manifest.permission.READ_CONTACTS);
//        permissions.add(Manifest.permission.GET_ACCOUNTS);
//        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS);
//        permissions.add(Manifest.permission.SEND_SMS);
//        permissions.add(Manifest.permission.READ_PHONE_STATE);
//
//
//        if(Build.VERSION.SDK_INT >= 23)
//        {
//            checkAndRequestPermissions(permissions, request_code);
//        }













        TextView details= (TextView) findViewById(R.id.details);
        TextView privacy_name= (TextView) findViewById(R.id.privacy_name);
        Button accept= (Button) findViewById(R.id.accept);
        Button reject= (Button) findViewById(R.id.reject);
        details.startAnimation(zoom);

        SpannableString content = new SpannableString("Privacy Policy");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        privacy_name.setText(content);

        //details.setTypeface(typeface2);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                checkstatus();
                if (info.isNetworkAvailable())
                {
                    if (im != 1)
                    {
                        Intent i = new Intent(PrivacyPolicy_Activity.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
                else
                {


                    Snackbar snackbar = Snackbar
                            .make(linearLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar snackbar1 = Snackbar.make(linearLayout, getResources().getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
                                    snackbar1.show();
                                }
                            });

                    snackbar.show();




                   // Toast.makeText(getApplication(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    public void checkstatus() {
        Database db = new Database(getApplicationContext());
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {

            if (cn.getName().equals("isActive") && cn.getPhone_number().equals("true") )

            {
                Intent i = new Intent(PrivacyPolicy_Activity.this, DashBoard_Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                im=1;
            }


        }

    }

    private  boolean checkAndRequestPermissions(ArrayList<String> permissions,int request_code) {

        if(permissions.size()>0)
        {
            listPermissionsNeeded = new ArrayList<>();

            for(int i=0;i<permissions.size();i++)
            {
                int hasPermission = ContextCompat.checkSelfPermission(activity,permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }

            }

            if (!listPermissionsNeeded.isEmpty())
            {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),request_code);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case 23:
                if(grantResults.length>0)
                {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++)
                    {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions=new ArrayList<>();

                    for (int i = 0; i < listPermissionsNeeded.size(); i++)
                    {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED)
                        {
                            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,listPermissionsNeeded.get(i)))
                            {
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            }

                            else
                            {
                                Log.i("Go to settings","and enable permissions");
                                Toast.makeText(activity, "Go to settings and enable permission :"+listPermissionsNeeded.get(i), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }

                    if(pending_permissions.size()>0)
                    {
                        ActivityCompat.requestPermissions(activity, pending_permissions.toArray(new String[pending_permissions.size()]),request_code);

                    }
                    else
                    {
                        Toast.makeText(activity, "All Permissions Granted .", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


}
