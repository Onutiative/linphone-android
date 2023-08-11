package org.linphone.onu_legacy.Activities.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.onutiative.onukit.Adapters.InboxRecycleAdapter;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.R;

public class RecentInbox_Activity extends AppCompatActivity {


    private String id= null;

    private static final int READ_SMS_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    private SharedPreferences.Editor editor;

    private SharedPreferences sharedPreferences;
    private String prefName="onuPref";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_inbox);
        getSupportActionBar().hide();

        sharedPreferences=getSharedPreferences(prefName,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        context=RecentInbox_Activity.this;

        requestInboxReadPermission();
    }

    private void PostAllsms(TextView postall) {
        postall.setBackgroundResource(R.drawable.clickpostbtn);

        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor c = RecentInbox_Activity.this.getContentResolver().query(inboxUri , null, null, null, null);
        Toast.makeText(RecentInbox_Activity.this, "All sms is being posted", Toast.LENGTH_LONG).show();
        while (c.moveToNext()) {
            try {

                String senderNumber=c.getString(c.getColumnIndex("address"));
                String smsBody=c.getString(c.getColumnIndex("body"));
                String smid=c.getString(c.getColumnIndex("date"));
                String sid=c.getString(c.getColumnIndex("_id"));

                Database db = new Database(RecentInbox_Activity.this);
                db.addsms(new Contact(smsBody,senderNumber, smid));

            } catch (Exception e) {

            }
        }
    }

    private Cursor inbox(){
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        return cursor;

    }

    private void readMyInbox()
    {
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.rrrr);
        final TextView postall= (TextView) findViewById(R.id.postall);

        RecyclerView.Adapter  adapter = new InboxRecycleAdapter(inbox(),RecentInbox_Activity.this);
        rvContacts.setAdapter(adapter);

        // Set layout manager to position the items0
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageResource(R.drawable.lock);
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecentInbox_Activity.this);
                alertDialog.setTitle("Lock Posted_Inbox_Activity Posted !");
                alertDialog.setMessage("Enter Password:");
                final EditText input = new EditText(RecentInbox_Activity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);
                alertDialog.setPositiveButton("LOCK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String pass = input.getText().toString();
                                Database db = new Database(RecentInbox_Activity.this);
                                if(db.password(pass)) {
                                    db.deleteAdmin("lockrecent", "jhorotek");
                                    db.addAdminNumber(new Contact("lockrecent","posted", "jhorotek"));
                                    Toast.makeText(RecentInbox_Activity.this, "LOCKED", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                }
                                else
                                {
                                    Toast.makeText(RecentInbox_Activity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                alertDialog.setNegativeButton("UNLOCK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String pass = input.getText().toString();
                                Database db = new Database(RecentInbox_Activity.this);
                                if(db.password(pass))
                                {
                                    db.deleteAdmin("lockrecent", "jhorotek");
                                    Toast.makeText(RecentInbox_Activity.this, "UNLOCKED", Toast.LENGTH_LONG).show();
                                    dialog.cancel();
                                }
                                else
                                {
                                    Toast.makeText(RecentInbox_Activity.this, "Invalid Password", Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                alertDialog.setNeutralButton("Cancle",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });


        postall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecentInbox_Activity.this);
                alertDialog.setTitle("OnuKit");
                alertDialog.setMessage("You want to post all sms from inbox to cloud server ?");
                final TextView input = new TextView(RecentInbox_Activity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.drawable.onukit_logo2);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PostAllsms(postall);
                            }
                        });
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });
    }

    private void requestInboxReadPermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        readMyInbox();
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



    private void showSettingsDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Need SMS Permissions");
        builder.setMessage("This app needs SMS permission to use this feature. You can grant them in app settings.");
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


}
