package org.linphone.onu_legacy.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import org.linphone.onu_legacy.Activities.PopupCallListActivity;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.SaveNewTask;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.NewTask;
import org.linphone.onu_legacy.Database.Task;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.Helper;
import org.linphone.onu_legacy.Utility.NewMessageEvent;
import org.linphone.onu_legacy.Utility.SharedPrefManager;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFormFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFormFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private static final String[] CALL_TYPE = {"Complain", "Customer Support", "Query", "Others"};
    private static final String[] STATUS_TYPE = {"N/A", "Pending", "Solved"};
    private String[] assignee_email;
    private ArrayAdapter<String> callTypeAdapter, statusTypeAdapter, assigneeEmailAdapter;
    private MaterialSpinner callTypeSpinner, statusTypeSpinner;
    private String phoneNo, timeStamp;
    private TextView textViewPhoneNo, textViewTimeStamp;
    private EditText editTextAssigneeEmail, editTextDatePicker, editTextTimePicker, editTextCallerName, editTextCallSummary, editTextOtherAssignee;
    private MaterialSpinner assigneeEmailSpinner;
    private String assigneeEmailString;
    private JSONArray assigneeEmailArray;
    private ArrayList<String> assigneeEmailList = new ArrayList<>();
    private String url = null, uname = null, upass = null, deviceID = null;
    private ProgressDialog progressBar;
    private final String TAG = "TaskFormFragment";
    private String form;
    private Button saveTaskButton, cancelButton;
    private boolean is24HoursMode = true;
    private String datePickerData, timePickerData;
    private String callerName;


    private String callerNameData, callPurposeData, taskStatusData, callSummaryData,
            callerMsisdnData, deviceIdData, callTypeData, callerIdData,
            callTimeData, employeeEmailData, estimatedTimeData;

    private Database db;

    private Context context;

    private String  emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private AutoCompleteTextView otherEmailAutoComplete;

    private String dateDefault, timeDefault;
    private SharedPrefManager sharedPrefManager;
    private Task task;
    private String message,from;
    private ViewPager viewPager;
    private int arrayIndex;
    private Helper helper;
    //private PassDateLiListener passDateLiListener;


    public TaskFormFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static TaskFormFragment newInstance(String param1, String param2) {
        TaskFormFragment fragment = new TaskFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//onActivityCreated(savedInstanceState);
        Log.d(TAG,"Task Form Created!");
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"Task Form Created 2 !");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_form,container,false);

        callTypeSpinner = view.findViewById(R.id.call_purpose_spinner);
        statusTypeSpinner = view.findViewById(R.id.task_status_spinner);

        textViewPhoneNo = (TextView) view.findViewById(R.id.phoneNo);
        textViewTimeStamp = (TextView) view.findViewById(R.id.timeStamp);

        editTextDatePicker = (EditText) view.findViewById(R.id.date_picker);
        editTextTimePicker = (EditText) view.findViewById(R.id.time_picker);

        assigneeEmailSpinner = (MaterialSpinner) view.findViewById(R.id.assignee_email);

        editTextCallerName = (EditText) view.findViewById(R.id.caller_name);
        editTextCallSummary = (EditText) view.findViewById(R.id.call_summary);

//        editTextOtherAssignee = (EditText) view.findViewById(R.id.other_assignee);
        otherEmailAutoComplete=(AutoCompleteTextView)view.findViewById(R.id.other_assignee_auto_complete);


        saveTaskButton = (Button) view.findViewById(R.id.save_button);
        cancelButton = (Button) view.findViewById(R.id.cancel_button);




        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context=getActivity();
        db = new Database(getActivity());
        sharedPrefManager=new SharedPrefManager(context);
        viewPager=(ViewPager)getActivity().findViewById(R.id.pager);
            String taskString=sharedPrefManager.getTask();
            Gson gson = new Gson();
            task=gson.fromJson(taskString,Task.class);
        if(task!=null)
        Log.i(TAG,task.getCallSummary());
        datePickerData="";
        timePickerData="";

        try {
            from=getActivity().getIntent().getStringExtra("from");
            Log.i(TAG,"From: "+ from);
            if (from.equals("inbox")){
                callTypeData=from;
                editTextCallSummary.setText(message);
            }else if (from.equals("newTask")){
                callTypeData = from;
            }else if (from.equals("contact")){
                callTypeData = from;
            }else {
                callTypeData = "incoming";
            }
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }
        
     //   requestManualPermission();

        ArrayList<String> contactEmails=getContactEmail();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, contactEmails);
        otherEmailAutoComplete.setAdapter(adapter);
        otherEmailAutoComplete.setThreshold(1);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateDefault = simpleDateFormat.format(calendar.getTime());

        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        timeDefault = simpleTimeFormat.format(calendar.getTime());

        editTextDatePicker.setText(dateDefault);
        editTextTimePicker.setText(timeDefault);
        datePickerData=dateDefault;
        timePickerData=timeDefault;

        set_app_url();

        callPurposeData=null;
        taskStatusData=null;
        employeeEmailData=null;

        callTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, CALL_TYPE);
        callTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        callTypeSpinner.setAdapter(callTypeAdapter);
        callTypeSpinner.setPaddingSafe(0, 0, 0, 0);

        statusTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, STATUS_TYPE);
        statusTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusTypeSpinner.setAdapter(statusTypeAdapter);
        statusTypeSpinner.setPaddingSafe(0, 0, 0, 0);

        phoneNo = getActivity().getIntent().getStringExtra("phoneNo");
        timeStamp = getActivity().getIntent().getStringExtra("timeStamp");
        callerName=getActivity().getIntent().getStringExtra("callerName");
        message = getActivity().getIntent().getStringExtra("message");
        helper=new Helper(getContext());
// && !phoneNo.isEmpty() && !callerName.isEmpty()
    if (!from.equals("newTask")){
        assigneeEmailString = getActivity().getIntent().getStringExtra("employee");
        try {
            assigneeEmailArray = new JSONArray(assigneeEmailString);
            for (int i = 0; i < assigneeEmailArray.length(); ++i) {
                JSONObject assigneeObject = assigneeEmailArray.getJSONObject(i);
                String assigneeEmail = assigneeObject.getString("email");
                assigneeEmailList.add(assigneeEmail);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        editTextCallerName.setText(callerName.toString());
    }else {
        timeStamp=helper.getTime();
    }


        assignee_email = new String[assigneeEmailList.size() + 1];
        assignee_email = assigneeEmailList.toArray(assignee_email);
        assignee_email[assigneeEmailList.size()] = "Others";

        assigneeEmailAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, assignee_email);
        assigneeEmailAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assigneeEmailSpinner.setAdapter(assigneeEmailAdapter);
        assigneeEmailSpinner.setPaddingSafe(0, 0, 0, 0);

        //get all intent data

        try {
            if (!isNetworkAvailable()){
                editTextCallSummary.setText(message);
            }
            if (phoneNo.isEmpty()){
                Log.i(TAG,"Empty phone no.");
                editTextCallSummary.setText(message);
            }
            List<Task>taskList= (ArrayList<Task>) getActivity().getIntent().getSerializableExtra("summaryObj");
            arrayIndex=getActivity().getIntent().getIntExtra("arrayIndex",0);
            String from=getActivity().getIntent().getStringExtra("from");
            Log.i(TAG,"Array Index: "+arrayIndex);
            if (from.equals("reassign")){
                task=taskList.get(arrayIndex);
                if(task!=null && isNetworkAvailable()) {
                    Log.d(TAG, task.getCallSummary());
                    callTypeSpinner.setSelection(callTypeAdapter.getPosition(task.getCallPurpose())+1);
                    statusTypeSpinner.setSelection(statusTypeAdapter.getPosition(task.getTaskStatus())+1);
                    editTextCallSummary.setText(task.getCallSummary());
                    editTextDatePicker.setText(task.getEstimatedDate());
                    editTextTimePicker.setText(task.getEstimatedTIme());
                }
            }
            if (from.equals("reassignTaskList")){
                task=taskList.get(arrayIndex);
                if(task!=null && isNetworkAvailable()) {
                    Log.d(TAG, task.getCallSummary());
                    callTypeSpinner.setSelection(callTypeAdapter.getPosition(task.getCallPurpose())+1);
                    statusTypeSpinner.setSelection(statusTypeAdapter.getPosition(task.getTaskStatus())+1);
                    editTextCallSummary.setText(task.getCallSummary());
                    editTextDatePicker.setText(task.getEstimatedDate());
                    editTextTimePicker.setText(task.getEstimatedTIme());
                }
            }
            if (from.equals("inbox")){
                editTextCallSummary.setText(message);
            }

        }
        catch (Exception e) {
            Log.i(TAG,"Exception: "+e.toString());
        }

        Log.i(TAG,"Intent PN: "+phoneNo);
        Log.i(TAG,"Intent time:"+timeStamp);
        Log.i(TAG,"Intent CallerName"+callerName);

        textViewPhoneNo.setText("Mobile No: " + phoneNo);
        textViewTimeStamp.setText("("+timeStamp+")");

        editTextDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(PopupTaskActivity.this,"edit text clicked!",Toast.LENGTH_LONG).show();
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        TaskFormFragment.this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");


            }
        });

        editTextTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        TaskFormFragment.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        now.get(Calendar.SECOND),
                        is24HoursMode
                );

                tpd.show(getActivity().getFragmentManager(), "TimePickerDialog");
            }
        });
//        callTypeSpinner.setOnItemSelectedListener();
        assigneeEmailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (parentView.getItemAtPosition(position).equals("Others")) {
//                    editTextOtherAssignee.setVisibility(View.VISIBLE);
                    otherEmailAutoComplete.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callerNameData = editTextCallerName.getText().toString();

                if(callTypeSpinner != null && callTypeSpinner.getSelectedItem() !=null ) {
                    callPurposeData = (String)callTypeSpinner.getSelectedItem();
                }
                if(statusTypeSpinner != null && statusTypeSpinner.getSelectedItem() !=null ) {
                    taskStatusData = (String)statusTypeSpinner.getSelectedItem();
                }
                if(assigneeEmailSpinner != null && assigneeEmailSpinner.getSelectedItem() !=null ) {
                    if (assigneeEmailSpinner.getSelectedItem().toString().equals("Others")) {
                        employeeEmailData = otherEmailAutoComplete.getText().toString();
                    } else {
                        employeeEmailData = assigneeEmailSpinner.getSelectedItem().toString();
                    }
                }
                if (from.equals("newTask") || phoneNo.isEmpty()){
                    //callerNameData="";
                    callerMsisdnData="";
                }else {
                    callerMsisdnData = phoneNo.substring(1);


                }
                callSummaryData = editTextCallSummary.getText().toString();
                deviceIdData = deviceID;
                callTypeData = "incoming";
                callerIdData = getDate("ymmkkss");
                callTimeData = timeStamp;
                Log.i(TAG,"Calling time: "+callTimeData);
                estimatedTimeData = datePickerData + " " + timePickerData;
                Log.i(TAG, "On Save Button: "+callerNameData + " " + callPurposeData + " " + taskStatusData + " " + callSummaryData + " " +
                        callerMsisdnData + " " + deviceIdData + " " + callTypeData + " " + callerIdData + " " +
                        callTimeData + " " + employeeEmailData + " " + estimatedTimeData
                );

//                if(callerNameData.trim().equals(""))
//                {
//                    Toast.makeText(context, "Caller name cannot be empty! ", Toast.LENGTH_SHORT).show();
//                }
//                else
                if(callPurposeData==null)
                {
                    Toast.makeText(context, "Call Purpose cannot be empty! ", Toast.LENGTH_SHORT).show();
                }
                else if(taskStatusData==null)
                {
                    Toast.makeText(context, "Task Status cannot be empty! ", Toast.LENGTH_SHORT).show();
                }
                else if(callSummaryData.trim().equals(""))
                {
                    Toast.makeText(context, "Call Summary cannot be empty! ", Toast.LENGTH_SHORT).show();
                }
                else if(employeeEmailData==null )
                {
                    Toast.makeText(context, "Employee email cannot be empty!", Toast.LENGTH_SHORT).show();
                }

                else if(datePickerData.trim().equals(""))
                {
                    Toast.makeText(context, "Date field cannot be empty! ", Toast.LENGTH_SHORT).show();
                }
                else if(timePickerData.trim().equals(""))
                {
                    Toast.makeText(context, "Time field cannot be empty! ", Toast.LENGTH_SHORT).show();
                }
                else {

                     if((employeeEmailData.trim().equals("") || !(employeeEmailData.trim().toLowerCase().matches(emailPattern)))&& !employeeEmailData.equals("N/A") && !employeeEmailData.equals("Self"))
                    {
                        Toast.makeText(context, "Employee email cannot be empty or invalid email!", Toast.LENGTH_SHORT).show();
                    }

                //&& employeeEmailData.trim().equals("") && !(employeeEmailData.trim().matches(emailPattern))
                    else
                     {
                         //Task savedTaskItm=new Task(callTimeData,callPurposeData,taskStatusData,callSummaryData,employeeEmailData,datePickerData,timePickerData);

                        if (isNetworkAvailable()) {

                            db.addNewTask(new NewTask(callerNameData, callPurposeData, taskStatusData, callSummaryData,
                                    callerMsisdnData, deviceIdData, callTypeData, callerIdData,
                                    callTimeData, employeeEmailData, estimatedTimeData));

                            Log.i(TAG,"Save check Come From: "+from);
                            if (phoneNo==null){
                                phoneNo="";
                            }
                            if (from.equals("reassignTaskList")){
                                from="reassignTaskListSave";
                            }else if (from.equals("inbox")){
                                from="inboxSave";
                            }

                            new SaveNewTask(getActivity(), getTaskArray(),phoneNo,timeStamp,from).execute();
                            Toast.makeText(getActivity(), "Data is sending... ", Toast.LENGTH_LONG).show();
                            //passDateLiListener.sendTaskObject(savedTaskItm);
                        } else if (!isNetworkAvailable()) {

                            db.addNewTask(new NewTask(callerNameData, callPurposeData, taskStatusData, callSummaryData,
                                    callerMsisdnData, deviceIdData, callTypeData, callerIdData,
                                    callTimeData, employeeEmailData, estimatedTimeData));

                            Toast.makeText(getActivity(), "No Internet Connection! Task is saved locally. ", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(context, PopupCallListActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //passDateLiListener.sendTaskObject(savedTaskItm);
                            //viewPager.setCurrentItem(0);
                            //context.startActivity(intent);

                        }

                     }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PopupCallListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                viewPager.setCurrentItem(0);
                //getActivity().startActivity(intent);

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear += 1;
        String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String date = "You picked the following date: " + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        String newDate = year + "-" + month + "-" + day;
        editTextDatePicker.setText(newDate);
        datePickerData = newDate;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        String time = "You picked the following time: " + hourString + "h" + minuteString + "m" + secondString + "s";
        String newTime = hourString + ":" + minuteString + ":" + secondString;
        editTextTimePicker.setText(newTime);
        timePickerData = newTime;
    }

    private JSONArray getTaskArray() {
       JSONArray jsonArray = db.getAllTask();
       Log.d(TAG,jsonArray.toString());
        return jsonArray;
    }


    public void set_app_url() {
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("Custom_url")) {
                if (!cn.getPhone_number().equals(""))
                    url = cn.getPhone_number() + "/inAppPopUP";
                //http://api1.onukit.com:8085/v3/incomingSms
            } else if (cn.getName().equals("email"))

            {
                uname = cn.getPhone_number();

            } else if (cn.getName().equals("password"))

            {
                upass = cn.getPhone_number();
            } else if (cn.getName().equals("did")) {
                deviceID = cn.getPhone_number();
            }
        }
    }


    private static String getDate(String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)  getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public ArrayList<String> getContactEmail() {
        ArrayList<String> emlRecs = new ArrayList<String>();
        HashSet<String> emlRecsHS = new HashSet<String>();

        ContentResolver cr = context.getContentResolver();
        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };
        String order = "CASE WHEN "
                + ContactsContract.Contacts.DISPLAY_NAME
                + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                + ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";
        String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
        if (cur.moveToFirst()) {
            do {
                // names comes in hand sometimes
                String name = cur.getString(1);
                String emlAddr = cur.getString(3);

                // keep unique only
                if (emlRecsHS.add(emlAddr.toLowerCase())) {
                    emlRecs.add(emlAddr);
                }
            } while (cur.moveToNext());
        }

        cur.close();
        return emlRecs;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NewMessageEvent event) {
        Log.i(TAG, "From Fragment two : " + event.message);

        Gson gson = new Gson();
        task=gson.fromJson(event.message,Task.class);

        if(task!=null) {
            Log.d(TAG, task.getCallSummary());
            callTypeSpinner.setSelection(callTypeAdapter.getPosition(task.getCallPurpose())+1);
            statusTypeSpinner.setSelection(statusTypeAdapter.getPosition(task.getTaskStatus())+1);
            editTextCallSummary.setText(task.getCallSummary());
            // Not assignEmployee filled with data.
            editTextDatePicker.setText(task.getEstimatedDate());
            editTextTimePicker.setText(task.getEstimatedTIme());

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

//    public interface PassDateLiListener{
//        public void sendTaskObject(Task task);
//    }
}
