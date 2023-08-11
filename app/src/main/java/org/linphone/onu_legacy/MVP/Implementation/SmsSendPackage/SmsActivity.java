package org.linphone.onu_legacy.MVP.Implementation.SmsSendPackage;
//<!--used in 6v3-->

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.onutiative.onukit.Activities.DashBoard_Activity;
import com.onutiative.onukit.MVP.Implementation.ContactPackage.ContactActivity;
import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.PushScheduledSMS;
import com.onutiative.onukit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SmsActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,SmsActivityCommunicator.SmsActivityView,
        PushScheduledSMS.RequestResponseListener {


    private static final int CONTACT_PICKER_REQUEST = 991;

    private ImageView selectContacts,dateTimePicker;
    private EditText selectedContacts,smsEditText,scheduleTitle, scheduleTime;
    private Button sendSMSButton;
    private String TAG = "SmsActivity";
    private static final int PERMISSION_REQUEST_CODE = 11;
    public ProgressDialog progressBar;
    private String url = null, uname = null, upass = null;
    private String smsText,schTitle,schTime,schDate,schDateTime="";
    private View view;
    private Context context=this;
    private List<ContactDetails> contactList;
    private SmsActivityPresenter presenter;
    private Switch schSwitch;
    private LinearLayout scheduleDetails;
    private Calendar calendar,GMTcalendar;
    private int year, month, day, hour, minute, scheduleTypeID;
    private String mDate,mTime,dateTime;
    private boolean flag=false;
    private int smsCounter=0,firstSmsChar,afterFirstSmsChar;
    private TextView smsCounterShow;
    private boolean unicodeFlag=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        view = (View) findViewById(R.id.main_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selectContacts = (ImageView) findViewById(R.id.select_contacts);
        selectedContacts = (EditText) findViewById(R.id.selected_contacts);
        smsEditText = (EditText) findViewById(R.id.smsEditText);
        sendSMSButton = (Button) findViewById(R.id.sendSMSButton);
        smsCounterShow=findViewById(R.id.smsCounterShow);
        //textWatcher
        smsEditText.addTextChangedListener(textWatcher);

        presenter=new SmsActivityPresenter(this);

        schSwitch=findViewById(R.id.schduleSwitch);
        scheduleTitle=findViewById(R.id.scheduleTitle);
        scheduleTime=findViewById(R.id.scheduleTime);
        scheduleDetails=findViewById(R.id.scheduleDetails);
        scheduleDetails.setVisibility(View.GONE);
        dateTimePicker=findViewById(R.id.pickDateTime);
        //////////////////
        calendar = Calendar.getInstance();
        GMTcalendar=Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        ////////////////////////////////////////
        try{
            contactList= (List<ContactDetails>) getIntent().getSerializableExtra("contacts");
            Log.i(TAG,"Total contact: "+contactList.size());
            selectedContacts.setText("Total selected: "+contactList.size());
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DashBoard_Activity.class));
            }
        });

        selectContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"Clicked",Toast.LENGTH_SHORT).show();
                //new PullServerContact(context,true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Intent intent = new Intent(SmsActivity.this,ContactActivity.class);
                intent.putExtra("selection",true);
                startActivity(intent);

            }
        });
        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsText=smsEditText.getText().toString();
                //schTime=scheduleTime.getText().toString();
                schTitle=scheduleTitle.getText().toString();
                schDateTime=schDate+" "+schTime;
                Log.i(TAG,"Schedule time: "+schDateTime);
                //schDateTime=scheduleTime.getText().toString();
                ///////try//////////////
//                Uri smsUri = Uri.parse("01744555875");
//                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", "01744555875", null));
//                intent.putExtra("sms_body", "sms text");
//                //intent.setType("vnd.android-dir/mms-sms");
//                startActivity(intent);
                /////////////////////////
                presenter.handelScheduleSMS(contactList,smsText,schTitle,schDateTime,scheduleTypeID,flag);
            }
        });

        ////////////////////spinner operation
        Spinner spinner = (Spinner) findViewById(R.id.scheduleSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.schedule_type_arrays, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        ///////////spinner operation end///////////
        schSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    scheduleDetails.setVisibility(View.VISIBLE);
                    flag=true;
                    Log.i(TAG,"Flag true");
                }else {
                    scheduleDetails.setVisibility(View.GONE);
                    flag=false;
                    Log.i(TAG,"Flag false");
                }
            }
        });
        dateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

    }//onCreate close

//    @Override
//    public void toContactActivity(List<ContactDetails> contactList, boolean selectionOption) {
//        this.contactList=contactList;
//        Log.i(TAG,"Contact Name: "+contactList.get(0).getContactName());
//        Intent intent = new Intent(SmsActivity.this, ContactActivity.class);
//        intent.putExtra("contacts", (Serializable) contactList);
//        intent.putExtra("selection",selectionOption);
//        startActivity(intent);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"Spinner Position: "+position);
        scheduleTypeID=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void selectDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        calendar.set(i,i1,i2);
                        String date = sdf.format(calendar.getTime());
                        ///////////////gmt////////////////////////
                        Date localTime = calendar.getTime();
                        //DateFormat converter = new SimpleDateFormat("yyyy-MM-dd");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        schDate=sdf.format(localTime);
                        Log.i(TAG,"Local Date : " + localTime);
                        Log.i(TAG,"Date in GMT : " + schDate);

                        ///////////////////////test end//////////////
                        Log.i(TAG,"Date: "+date);
                        mDate=date;
                        selectTime();
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Log.i(TAG,"I: "+i+"; I1: "+i1);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        calendar.set(0,0,0,i,i1);
                        String time = sdf.format(calendar.getTime());

                        ///////////////gmt////////////////////////
                        //DateFormat converter = new SimpleDateFormat("HH:mm");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                        int hour=i;
                        int min=i1;
                        if((min+30)>60){
                            hour+=1;
                            min=(i1+30)-60;
                        }else {
                            min+=30;
                        }
                        GMTcalendar.set(0,0,0,hour,min);
                        Date localTime = GMTcalendar.getTime();
                        schTime=sdf.format(localTime);
                        Log.i(TAG,"time in GMT : " + schTime);
                        ///////////////////////test end//////////////

                        Log.i(TAG,"Time: "+time);
                        mTime=time;
                        scheduleTime.setText(mDate+" "+mTime);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    public void showSelectedList(View view) {
        presenter.handleSelectContactList(contactList);
    }

    @Override
    public void updateSelectedData(List<ContactDetails> contactDetailsList) {
        contactList=contactDetailsList;
        selectedContacts.setText("Total selected: "+contactList.size());

    }
    private final TextWatcher textWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textView to the current length
            /*
            #get textCount
            #if textCount equal=0 then no.of smsCount= 0
            #if textCount >0 and textCount<160 then no.of smsCount= 1
            #if (textCount>160) and (textCount/150)==smsCount+1 then no.of smsCount++
            #if (textCount>160) and (textCount/150)==smsCount+1 then no.of smsCount++
            if count
             */

            int textCount =s.length();
            Log.i(TAG,"Length of the SMS: "+textCount);
            if (textCount>0){
                //check unicode or ASCII
                if (textCount>0) {
                    //check unicode or ASCII
                    for(int i=0;i<s.length();i++){
                        Log.i(TAG, "Last Char: " + s.charAt(i));
                        if (Character.UnicodeBlock.of(s.charAt(s.length() - 1)) != Character.UnicodeBlock.BASIC_LATIN) {
                            unicodeFlag=true;
                        } else {
                            Log.i(TAG, "It's ASCII");
                        }
                    }
                }
                //////////////////////////////
                //count number of SMS///////////////
                if (unicodeFlag){
                    firstSmsChar=80;
                    afterFirstSmsChar=75;
                }else {
                    firstSmsChar=160;
                    afterFirstSmsChar=150;
                }
                if (textCount<=firstSmsChar){
                    smsCounter=1;
                }
                //Log.i(TAG,"Calculation: "+(textCount/150)+" ; "+(smsCounter+1));
                if (textCount>firstSmsChar){
                    //smsCounter=(textCount/150)+1;
                    if (textCount<smsCounter*afterFirstSmsChar){
                        smsCounter=(textCount/afterFirstSmsChar)+1;

                        if (textCount<=(smsCounter-1)*afterFirstSmsChar){
                            smsCounter=(textCount/afterFirstSmsChar);
                        }
                    }
                    if (textCount>smsCounter*afterFirstSmsChar){
                        smsCounter++;
                    }
                }
            }else {
                smsCounter=0;
                unicodeFlag=false;
            }
            smsCounterShow.setText(String.valueOf(smsCounter)+"/"+String.valueOf(textCount));
            Log.i(TAG,"Number of SMS: "+smsCounter);
            //mTextView.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public void onSuccessRequest(String msg) {
        Log.i(TAG,msg);
        showSnackBar(msg);
    }

    @Override
    public void onFailureRequest(String msg) {
        Log.i(TAG,msg);
        showSnackBar(msg);
    }
    ///////////////////////////

    void showSnackBar(String msg){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))

                .show();
    }
}
