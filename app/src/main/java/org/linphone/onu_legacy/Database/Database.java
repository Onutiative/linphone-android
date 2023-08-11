package org.linphone.onu_legacy.Database;

/**
 * Created by jhorotek on 7/6/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.onutiative.onukit.MVP.Implementation.model.AdminDataClasses.AdminInfo;
import com.onutiative.onukit.MVP.Implementation.model.SMSDataClasses.InboxSMSDetails;
import com.onutiative.onukit.Utility.Constants;
import com.onutiative.onukit.Utility.IntentStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jhorotek on 7/6/2015.
 * //4444444-0736=778
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 23;
    private static final String DATABASE_NAME = "OnuKit_DB";

    public static final String USER_TABLE = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASS = "password";

    //  table name
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_INBOX = "inbox";
    private static final String TABLE_OUTBOX = "outbox";
    private static final String TABLE_OUTBOX_TOW = "outboxtow";
    private static final String TABLE_SENT = "sent";
    private static final String TABLE_CONFIG = "admin";
    private static final String TABLE_CALL_LOG = "call_log";
    //private static final String TABLE_CALL_LOG = "del";
    private static final String TABLE_MSG = "msg";
    private static final String TABLE_THREAD = "thread";
    private static final String CALLS_QUEUE = "calls_queue";
    private static final String TABLE_TASK="task";

    // two tables for bulk sms.
    private static final String TABLE_OUTBOXSMS = "outboxsms";
    private static final String TABLE_SENTSMS="sentsms";

    //  Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "phone_number";
    private static final String KEY_TIME= "time";
    private static final String KEY_TXID= "txid";
    private static final String KEY_TYPE= "type";
    private static final String KEY_STAT= "status";
    private static final String KEY_STATUS_LOG= "status_log";
    private static final String KEY_STATUS_AUDIO= "status_audio";
    private static final String KEY_STATUS= "firstpart";

    // Columns for TASK TABLE
    private static final String KEY_CALLER_NAME = "caller_name";
    private static final String KEY_CALL_PURPOSE = "call_purpose";
    private static final String KEY_TASK_STATUS= "task_status";
    private static final String KEY_CALL_SUMMARY= "call_summary";
    private static final String KEY_CALLER_MSISDN= "caller_msisdn";
    private static final String KEY_DEVICE_ID = "device_id";
    private static final String KEY_CALL_TYPE = "call_type";
    private static final String KEY_CALLER_ID = "caller_id";
    private static final String KEY_CALL_TIME= "call_time";
    private static final String KEY_EMPLOYEE_EMAIL= "employee_email";
    private static final String KEY_ESTIMATED_TIME= "estimated_time";

    private static final String KEY_CALL_STATUS="status"; ///For Call Pop log with their status


    // columns for outboxssms table.

//    private static final String COLUMN_ID="id";
    private static final String COLUMN_SMS_ID="smsid";
    private static final String COLUMN_SMS_TO="smsto";
    private static final String COLUMN_SMS_BODY="smsbody";
    private static final String COLUMN_PULL_TIME="pulltime";
    private static final String COLUMN_SUBMISSION_TIME="submissiontime";
    private static final String COLUMN_DELIVERY_TIME="deliverytime";
    private static final String COLUMN_SMS_STATUS="smsstatus";

    //intent table
    private static final String TABLE_INTENT = "intent_status";
    private static final String COL_INTENT_INDEX="_id";
    private static final String COL_INTENT_SMSID="sms_id";
    private static final String COL_INTENT_STATUS="status";
    private static final String COL_INTENT_TIME="time";



    private static final String TAG="Database";


    public Database(Context context) {



        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Log.d(TAG,"Constructor Called");
    }



    @Override
    public void onCreate(SQLiteDatabase db) {



        Log.d(TAG,"onCreate Called");
        String CREATE_TABLE_INTENT = "create table "+ TABLE_INTENT+ "(" +
                COL_INTENT_INDEX +" integer primary key, " +
                COL_INTENT_SMSID + " text, " +
                COL_INTENT_STATUS + " integer, " +
                COL_INTENT_TIME  + " text);";

//End of TASK TABLE

        String CREATE_TABLE_USERS = "CREATE TABLE " + USER_TABLE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASS + " TEXT);";

        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";

        String CREATE_CALL_TABLE = "CREATE TABLE " + CALLS_QUEUE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO  + " TEXT," + KEY_TIME + " TEXT," + KEY_TYPE + " TEXT," + KEY_TXID + " TEXT,"
                + KEY_STATUS_LOG + " TEXT," + KEY_STATUS_AUDIO+ " TEXT" + ")";


        String CREATE_INBOX_TABLE = "CREATE TABLE " + TABLE_INBOX + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"  + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";



        String CREATE_OUTBOX_TABLE = "CREATE TABLE " + TABLE_OUTBOX + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT,"+ KEY_TYPE + " TEXT," + KEY_STAT + " TEXT" + ")";

        String CREATE_OUTBOX_TOW_TABLE = "CREATE TABLE " + TABLE_OUTBOX_TOW + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT,"+ KEY_TYPE + " TEXT," + KEY_STAT + " TEXT" + ")";

        String CREATE_ADMIN_TABLE = "CREATE TABLE " + TABLE_CONFIG + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"  + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";

        String CREATE_SENT_TABLE = "CREATE TABLE " + TABLE_SENT + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"  + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT," + KEY_STATUS + " TEXT" + ")";


//        String CREATE_DELETE_TABLE = "CREATE TABLE " + TABLE_CALL_LOG + "("
//                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT,"
//                + KEY_PH_NO + " TEXT" + ")";


        String CREATE_DELETE_TABLE = "CREATE TABLE " + TABLE_CALL_LOG + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT unique,"
                + KEY_PH_NO + " TEXT,"+KEY_CALL_STATUS + " TEXT" + ")";



        String CREATE_THREAD_TABLE = "CREATE TABLE " + TABLE_THREAD + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT" + ")";


        String CREATE_MSG_TABLE = "CREATE TABLE " + TABLE_MSG + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " TEXT,"
                + KEY_PH_NO + " TEXT,"+ KEY_TYPE + " TEXT," + KEY_STAT + " TEXT" + ")";


        String CREATE_TASK_TABLE= "CREATE TABLE " + TABLE_TASK + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_CALLER_NAME + " TEXT," + KEY_CALL_PURPOSE + " TEXT,"
                + KEY_TASK_STATUS + " TEXT,"+ KEY_CALL_SUMMARY + " TEXT," + KEY_CALLER_MSISDN + " TEXT,"
                + KEY_DEVICE_ID + " TEXT," + KEY_CALL_TYPE + " TEXT,"
                + KEY_CALLER_ID + " TEXT,"+ KEY_CALL_TIME + " TEXT," + KEY_EMPLOYEE_EMAIL + " TEXT," +KEY_ESTIMATED_TIME + " TEXT" +")" ;




        //sql query for creating outboxsms table.
        String CREATE_OUTBOXSMS_TABLE="CREATE TABLE " + TABLE_OUTBOXSMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_SMS_ID + " TEXT UNIQUE,"  + COLUMN_SMS_TO + " TEXT,"
                + COLUMN_SMS_BODY + " TEXT," +COLUMN_PULL_TIME + " TEXT,"+COLUMN_SUBMISSION_TIME + " TEXT,"+
                COLUMN_DELIVERY_TIME + " TEXT,"+COLUMN_SMS_STATUS + " INTEGER"+ ")";

        String CREATE_SENTSMS_TABLE="CREATE TABLE " + TABLE_SENTSMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_SMS_ID + " TEXT UNIQUE,"  + COLUMN_SMS_TO + " TEXT,"
                + COLUMN_SMS_BODY + " TEXT," +COLUMN_SUBMISSION_TIME + " TEXT "+")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CALL_TABLE);
        db.execSQL(CREATE_INBOX_TABLE);
        db.execSQL(CREATE_OUTBOX_TABLE);
        db.execSQL(CREATE_OUTBOX_TOW_TABLE);
        db.execSQL(CREATE_ADMIN_TABLE);
        db.execSQL(CREATE_SENT_TABLE);
        db.execSQL(CREATE_DELETE_TABLE);
        db.execSQL(CREATE_THREAD_TABLE);
        db.execSQL(CREATE_MSG_TABLE);
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_OUTBOXSMS_TABLE);
        db.execSQL(CREATE_SENTSMS_TABLE);
        db.execSQL(CREATE_TABLE_INTENT);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        Log.d(TAG,"onUpgrade Called");


        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + CALLS_QUEUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INBOX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTBOX);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTBOX_TOW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THREAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MSG);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_OUTBOXSMS);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_SENTSMS);
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_INTENT);


        //cretae tables inside

        onCreate(db);

    }


    //  TASK TABLE


    public void addNewTask(NewTask newTask)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CALLER_NAME, newTask.getCallerName()); // Contact Name
        values.put(KEY_CALL_PURPOSE, newTask.getCallPurpose()); // Contact Phone Number
        values.put(KEY_TASK_STATUS, newTask.getTaskStatus());
        values.put(KEY_CALL_SUMMARY, newTask.getCallSummary());
        values.put(KEY_CALLER_MSISDN, newTask.getCallerMsisdn());
        values.put(KEY_DEVICE_ID, newTask.getDeviceId()); // Contact Name
        values.put(KEY_CALL_TYPE,newTask.getCallType()); // Contact Phone Number
        values.put(KEY_CALLER_ID, newTask.getCallerId());
        values.put(KEY_CALL_TIME, newTask.getCallTime());
        values.put(KEY_EMPLOYEE_EMAIL,newTask.getEmployeeEmail() );
        values.put(KEY_ESTIMATED_TIME,newTask.getEstimatedTime());
        // Inserting Row
        db.insert(TABLE_TASK, null, values);
        db.close(); // Closing Database connection
    }



    public JSONArray getAllTask() {

        List<Contact> contacts = getAdminNumber();
        String parentID="";
        for (Contact cn : contacts)
        {
            if (cn.getName().equals("parent_id")){
                parentID=cn.getPhone_number();
            }
        }

        JSONArray jsonArray=new JSONArray();

        List<Sms> contactList = new ArrayList<Sms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASK +" ORDER BY " + KEY_ID + "  ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                try{
                    JSONObject jsonObject=new JSONObject();

                    jsonObject.accumulate("caller_name", cursor.getString(1));
                    jsonObject.accumulate("call_purpose",cursor.getString(2));
                    jsonObject.accumulate("task_status", cursor.getString(3));
                    jsonObject.accumulate("call_summary", cursor.getString(4));
                    jsonObject.accumulate("caller_msisdn", cursor.getString(5));
                    jsonObject.accumulate("device_id", cursor.getString(6));
                    jsonObject.accumulate("call_type", cursor.getString(7));
                    jsonObject.accumulate("caller_id", cursor.getString(8));
                    jsonObject.accumulate("call_time", cursor.getString(9));
                    jsonObject.accumulate("employee_email", cursor.getString(10));
                    jsonObject.accumulate("estimated_time", cursor.getString(11));
                    jsonObject.accumulate("parent_id", parentID);

                    jsonArray.put(jsonObject);

                }catch(JSONException ex)
                {
                    ex.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return jsonArray;
    }

    public void clearAllTask()
    {
        Log.d(TAG,"Clear all Task called");

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_TASK);
        db.close();
    }


    public int getTaskCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TASK ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    //MSG table/////////////////////////////////////////////////////////////////////////////////////
    public void addMSG(Sms contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_STAT, contact.getStatus());
        // Inserting Row
        db.insert(TABLE_MSG, null, values);
        db.close(); // Closing Database connection

    }
    public int checkmsgid( String id){
        String countQuery = "SELECT  * FROM " + TABLE_MSG + " WHERE "+ KEY_NAME +" = '"+id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public void deleteallMsg(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_MSG);
        db.close();
    }

    /**
     * Storing user details in Database
     * */
    public void addUser(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASS, password);

        long id = db.insert(USER_TABLE, null, values);
        db.close();

        Log.d("Add User", "user inserted" + id);
    }

    // for getUser

    public boolean getUser(String email, String pass){
        //HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "select * from  " + USER_TABLE + " where " +
                COLUMN_EMAIL + " = " + "'"+email+"'" + " and " + COLUMN_PASS + " = " + "'"+pass+"'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {

            return true;
        }
        cursor.close();
        db.close();

        return false;
    }
    public int getMSGCount() {

        String selectQuery = "SELECT  * FROM " + TABLE_MSG ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }
    public void deleteFrombigining() {
        //DELETE FROM SuccessfullCalls WHERE id IN (SELECT id FROM SuccessfulCalls ORDER BY id DESC LIMIT 2)

        String selectQuery = "DELETE FROM " + TABLE_MSG + " WHERE "+ KEY_ID + " IN (SELECT id FROM "+ TABLE_MSG +" ORDER BY id ASC LIMIT 1)";
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(selectQuery);
        db.close();
    }
    public List<Sms> getAllNotification() {

        List<Sms> contactList = new ArrayList<Sms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MSG +" ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Sms contact = new Sms();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setStatus(cursor.getString(5));

                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;
    }



    //thread table/////////////////////////////////////////////////////////////////////////////////
    public void addthread(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());

        // Inserting Row
        db.insert(TABLE_THREAD, null, values);
        db.close(); // Closing Database connection

    }
    public List<Contact> getallthread() {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_THREAD;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        return contactList;
    }
    public void deleteThread (String smsid){

        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + TABLE_THREAD + " where "+KEY_NAME+"='" + smsid + "'");
        db.close();
    }
    public void removeallthread(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_THREAD);
        db.close();
    }

    public int getThreadCount() {
        String countQuery = "SELECT  * FROM " + TABLE_THREAD;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    //DELETE table////////////////////////////////////////////////
    public void add_in_out_calls(Contact contact) {
        Log.i(TAG,"add_in_out_calls called");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());

        // Inserting Row
        db.insert(TABLE_CALL_LOG, null, values);
        db.close(); // Closing Database connection

    }

    public void add_in_out_calls_with_status(Contact contact) {
        Log.i(TAG,"add_in_out_calls_with_status caled");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());
        values.put(KEY_CALL_STATUS,contact.getStatus());

        // Inserting Row
        db.insert(TABLE_CALL_LOG, null, values);
        db.close(); // Closing Database connection

    }

    public void removedelete (String body,String smsid){
        //  String countQuery = "DELETE FROM "+ TABLE_INBOX +" WHERE "+ KEY_PH_NO +"="+number+" AND "+ KEY_NAME + "="+body;
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CALL_LOG,
                KEY_NAME + " = ? AND " + KEY_TIME + " = ?",
                new String[]{body, smsid + ""});
        db.close();
    }

    public List<Contact> getAll_calls(String type) {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_LOG + " WHERE "+KEY_NAME +" = '"+type+"' ORDER BY " + KEY_ID + "  DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return contactList;

    }


    public List<Contact> getAll_calls_with_status(String type) {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CALL_LOG + " WHERE "+KEY_NAME +" = '"+type+"' ORDER BY " + KEY_ID + "  DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        Log.d(TAG,"Call list from TABLE DELETE");

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                contact.setStatus(cursor.getString(4));
                // Adding contact to list
                contactList.add(contact);

                Log.d(TAG,Integer.parseInt(cursor.getString(0))+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+
                        cursor.getString(3)+" "+cursor.getString(4)+"\n");


            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return contactList;
    }

    public void updateCallLogWithStatusForMissed(String timeStamp, String callStatus)
    {

        Log.i(TAG,"Call from TABLE_CALL_LOG - updateCallLogWithStatusForMissed");

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CALL_LOG + " SET " + KEY_CALL_STATUS + " = '"+callStatus+"' WHERE " + KEY_TIME + " = '"+timeStamp+"'");
        db.close();
    }


    public void changeCallStatusBeforeClear()
    {
//        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query

        Log.d(TAG,"changeCallStatusBeforeClear Called");

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASK +" ORDER BY " + KEY_ID + "  ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                try{

                    String phoneNo=cursor.getString(5);
                    updateCallStatus("+"+phoneNo);

                    Log.d(TAG, "Phone No "+phoneNo);

                }catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public void updateCallStatus (String phoneNo){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CALL_LOG + " SET " + KEY_CALL_STATUS + " = 'submitted' " + " WHERE " + KEY_PH_NO + " = '"+phoneNo+"'");
        db.close();
    }

    public void removeall_calls(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CALL_LOG);
        db.close();
    }
    public void removeAll_calls_queue(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CALLS_QUEUE);
        db.close();
    }
    public void delete_call_log (String id){

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_OUTBOX,
                KEY_NAME + " = ?",
                new String[]{id + ""});
        db.close();
    }

    //Admin table/////////////////////////////////////////////////
    public void addAdminNumber(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());

        // Inserting Row
        db.insert(TABLE_CONFIG, null, values);
        db.close(); // Closing Database connection
    }

    public void updateAdminNumber (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG + " SET " + KEY_PH_NO + " ='" + i + "' WHERE " + KEY_NAME + " = 'password'");
        db.close();
    }
    public void updateAdminInbox (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'inbox'");
        db.close();
    }
    public void updateIncallCount(int count)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+count+"' WHERE "+KEY_NAME+" = 'couuntIncall'");
        db.close();
    }

    public void updateOutcallCount(int count)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+count+"' WHERE "+KEY_NAME+" = 'couuntOutcall'");
        db.close();
    }

    public void updateAdminReport (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'report'");
        db.close();
    }

    public void updateCallBlock (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'callblock'");
        db.close();
    }

    public void updateCheckOut (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'checkOut'");
        db.close();
    }
    public void updateSmsId (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'AsmsId'");
        db.close();
    }

    public void updateDeliveredSmsId (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG + " SET " + KEY_PH_NO + " ='" + i + "' WHERE " + KEY_NAME + " = 'DsmsId'");
        db.close();
    }
    public int AdminSmsId( String id){
        String countQuery = "SELECT  * FROM " + TABLE_CONFIG + " WHERE "+ KEY_PH_NO +" = '"+id+"' AND "+KEY_NAME+" = 'AsmsId'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int AdminDeliverySmsId( String id){
        String countQuery = "SELECT  * FROM " + TABLE_CONFIG + " WHERE "+ KEY_PH_NO +" = '"+id+"' AND "+KEY_NAME+" = 'DsmsId'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public void updateNotifyOut (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'nout'");
        db.close();
    }

    public void updateLastIncoming (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'lastin'");
        db.close();
    }

    public void updateNotifyCall (String i){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG + " SET " + KEY_PH_NO + " ='" + i + "' WHERE " + KEY_NAME + " = 'ncall'");
        db.close();
    }
    public List<Contact> getAdminNumber() {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONFIG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        return contactList;
    }

    ////added by bidyut for getting admin info

    public List<AdminInfo> getAdminInformation() {

        List<AdminInfo> adminInfos = new ArrayList<AdminInfo>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONFIG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AdminInfo adminInfo=new AdminInfo();
                adminInfo.setId(Integer.parseInt(cursor.getString(0)));
                adminInfo.setDataKey(cursor.getString(1));
                adminInfo.setFirstValue(cursor.getString(2));
                adminInfo.setSecondValue(cursor.getString(3));
                // Adding contact to list
                adminInfos.add(adminInfo);
            } while (cursor.moveToNext());
        }

        // return contact list
        cursor.close();
        db.close();
        return adminInfos;
    }


    public int getAdminCount() {

        String countQuery = "SELECT  * FROM " + TABLE_CONFIG;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }
    public boolean password(String pass){
        String countQuery = "SELECT  * FROM " + TABLE_CONFIG + " WHERE " + KEY_PH_NO + " = '" + pass +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        if(cursor.getCount() >0) {
            cursor.close();
            db.close();
            return true;
        }
        else
        {
            cursor.close();
            db.close();
            return false;
        }


    }
    public boolean checklock(String pass){
        String countQuery = "SELECT  * FROM " + TABLE_CONFIG + " WHERE " + KEY_NAME + " = '" + pass +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        if(cursor.getCount() >0) {
            cursor.close();
            db.close();
            return true;
        }
        else
        {
            cursor.close();
            db.close();
            return false;
        }


    }
    public void deleteAllAdmin(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONFIG);
        db.close();


    }
    public void deleteAdmin (String body,String smsid){

        //  String countQuery = "DELETE FROM "+ TABLE_INBOX +" WHERE "+ KEY_PH_NO +"="+number+" AND "+ KEY_NAME + "="+body;
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CONFIG,
                KEY_NAME + " = ? AND " + KEY_TIME + " = ?",
                new String[]{body, smsid + ""});
        db.close();



    }
    public void updateSent(String i) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'setUP'");
        db.close();
    }
//======================================================================================
    public void updateSentbyNotify () {
        int i=check_sent_count();
        i=i+100;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG +" SET "+ KEY_PH_NO +" ='"+i+"' WHERE "+KEY_NAME+" = 'setUP'");
        db.close();
    }

    public int check_sent_count() {
        int sent_sms_counts = 0;
        List<Contact> contacts = getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("setUP")) {
                Log.v("Jhoro", "response found=" + cn.getPhone_number());
                sent_sms_counts = Integer.parseInt(cn.getPhone_number()) + 1;
            }
        }
        return sent_sms_counts;
    }

    //===================================================================================
    public void updatePost(String i) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONFIG + " SET " + KEY_PH_NO + " ='" + i + "' WHERE " + KEY_NAME + " = 'PostedUP'");
        db.close();
    }

   //OUTBOX  Table
    public void addOutbox(Sms contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_STAT, contact.getStatus());

        // Inserting Row
        db.insert(TABLE_OUTBOX, null, values);
        db.close(); // Closing Database connection

    }
    public List<Sms> getAllOutboxSent() {

        List<Sms> contactList = new ArrayList<Sms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOX + " WHERE "+ KEY_STAT +" ='sent'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Sms contact = new Sms();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setStatus(cursor.getString(5));

                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;

    }
    public int outboxPendingCount(){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX + " WHERE "+ KEY_STAT +" ='pending'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int checkSmsId( String id){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX + " WHERE "+ KEY_TIME +" = '"+id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int outboxSentCount(){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX + " WHERE "+ KEY_STAT +" ='sent'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int getOutboxCount() {

        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }
    public void deleteOutbox (String id){

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_OUTBOX,
                KEY_TIME + " = ?",
                new String[]{id + ""});
        db.close();


    }
    public List<Sms> getAllOutbox() {

        List<Sms> contactList = new ArrayList<Sms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOX +" ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Sms contact = new Sms();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setStatus(cursor.getString(5));

                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;

    }
    public void deletealloutbox(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_OUTBOX);
        db.close();

    }
    public void updateOutbox(String time){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_OUTBOX +" SET "+ KEY_STAT +" ='sent' WHERE "+KEY_TIME+" = '"+time+"'");
        db.close();

    }
    public void updateOutboxEror(String time){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_OUTBOX +" SET "+ KEY_STAT +" ='pending' WHERE "+KEY_TIME+" = '"+time+"'");
        db.close();

    }
    public void UpdateOutboxProcessed(String time){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_OUTBOX +" SET "+ KEY_STAT +" ='processed' WHERE "+KEY_TIME+" = '"+time+"'");
        db.close();
    }
    public Cursor outboxcursor(){
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOX + " ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.close();
        db.close();
        return cursor;


    }

    //OUTBOX  Table TOW
    public void addOutboxtow(Sms contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhoneNumber()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_STAT, contact.getStatus());

        // Inserting Row
        db.insert(TABLE_OUTBOX_TOW, null, values);
        db.close(); // Closing Database connection

    }
    public List<Sms> getAllOutboxSenttow() {

        List<Sms> contactList = new ArrayList<Sms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW + " WHERE "+ KEY_STAT +" ='sent'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Sms contact = new Sms();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setStatus(cursor.getString(5));

                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return contactList;

    }
    public int outboxPendingCounttow(){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW + " WHERE "+ KEY_STAT +" ='pending'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int outboxErorCounttow(){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW + " WHERE "+ KEY_STAT +" ='error'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int checkSmsTowId( String id){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW + " WHERE "+ KEY_TIME +" = '"+id+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int outboxSentCounttow(){
        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW + " WHERE "+ KEY_STAT +" ='sent'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
    public int getOutboxCounttow() {

        String countQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }
    public void deleteOutboxtow (String id){
        deleteOutbox(id);
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_OUTBOX_TOW,
                KEY_TIME + " = ?",
                new String[]{id + ""});
        db.close();
    }
    public List<Sms> getAllOutboxtow() {
        List<Sms> contactList = new ArrayList<Sms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW +" ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Sms contact = new Sms();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhoneNumber(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setStatus(cursor.getString(5));

                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }
    public void deletealloutboxtow(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_OUTBOX_TOW);
        db.close();
    }
    public void updateOutboxtow(String time){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_OUTBOX_TOW +" SET "+ KEY_STAT +" ='sent' WHERE "+KEY_TIME+" = '"+time+"'");
        db.close();
    }
    public void updateOutboxErortow(String time){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_OUTBOX_TOW +" SET "+ KEY_STAT +" ='error' WHERE "+KEY_TIME+" = '"+time+"'");
        db.close();

    }
    public void UpdateOutboxProcessedtow(String time){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_OUTBOX +" SET "+ KEY_STAT +" ='processed' WHERE "+KEY_TIME+" = '"+time+"'");
        db.close();
    }
    public Cursor outboxcursortow(){
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOX_TOW + " ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.close();
        db.close();
        return cursor;
    }
   //supper man
    public void super_shot(int smscount,String smsid) {
        updateSent(Integer.toString(smscount));
        deleteOutboxtow(smsid);

    }
  //SENT table
    public void addSent(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());

        // Inserting Row
        db.insert(TABLE_SENT, null, values);
        db.close(); // Closing Database connection

    }

    public List<Contact> getAllSents() {

        List<Contact> sentlist = new ArrayList<Contact>();
        //Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SENT +" ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                sentlist.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return sentlist;

    }

    public int getSentCount() {

        String countQuery = "SELECT  * FROM " + TABLE_SENT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void delSentOne() {
        //DELETE FROM SuccessfullCalls WHERE id IN (SELECT id FROM SuccessfulCalls ORDER BY id DESC LIMIT 2)

        String selectQuery = "DELETE FROM " + TABLE_SENT + " WHERE "+ KEY_ID + " IN (SELECT id FROM "+ TABLE_SENT +" ORDER BY id ASC LIMIT 1)";
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(selectQuery);
        db.close();


    }

    public void deleteallsent(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SENT);
        db.close();

    }

    public void deletesent (String body,String smsid){


        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_SENT,
                KEY_NAME + " = ? AND " + KEY_TIME + " = ?",
                new String[] {body, smsid+""});
        db.close();

    }


    //=========inser/delete/update/get  @ TABLE_CONTACTS==================

    // Adding new contact to TABLE_CONTACTS
    public void addContact(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing Database connection

    }

    public Cursor inboxCursor(){
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_ID + "  DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.close();
        db.close();
        return cursor;


    }
    // Getting All Contacts TABLE_CONTACTS
    public ArrayList<Contact> getAllContacts() {

        ArrayList<Contact> contactList = new ArrayList<Contact>();
        // Select All QuerySELECT * FROM COMPANY ORDER BY SALARY ASC;
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
               //Log.i(TAG,contact.getPhoneNumber()+"; "+contact.getName()+"; "+contact.getTime());
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        Log.i(TAG, contactList.get(0).getPhone_number()+"; "+ contactList.get(0).getName()+"; "+ contactList.get(0).getTime());
        return contactList;
    }
    /////////////get all posted inbox sms
    public ArrayList<InboxSMSDetails> getAllPostedInbox() {

        ArrayList<InboxSMSDetails> inboxSMSDetails = new ArrayList<InboxSMSDetails>();
        // Select All QuerySELECT * FROM COMPANY ORDER BY SALARY ASC;
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS + " ORDER BY " + KEY_ID + "  DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String smsBody=cursor.getString(1);
                String date=cursor.getString(2);
                String contact=cursor.getString(3);
                // Adding contact to list
                InboxSMSDetails inboxSMSDetail=new InboxSMSDetails(id,contact,date,smsBody);
                inboxSMSDetails.add(inboxSMSDetail);
                //Log.i(TAG,contact.getPhoneNumber()+"; "+contact.getName()+"; "+contact.getTime());
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        //Log.i(TAG, InboxSMSDetails.get(0).getPhoneNumber()+"; "+ contactList.get(0).getName()+"; "+ contactList.get(0).getTime());
        return inboxSMSDetails;
    }
    // Getting contacts Count TABLE_CONTACTS
    public int getContactsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }
    public void deletemsg (String body,String number){

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CONTACTS,
                KEY_NAME + " = ? AND " + KEY_PH_NO + " = ?",
                new String[] {body, number+""});
        db.close();
    }
    // Updating single contact TABLE_CONTACTS
    public int updateContact(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhone_number());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });


    }
    // Deleting single contact TABLE_CONTACTS
    public void deleteContact(Contact contact) {


        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_NAME + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }
    //Deleting all contact from TABLE_CONTACTS
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS);
        db.close();

    }
    //Finding single contact row from TABLE_CONTACTS
    public int find(String body,String number){

        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS +" WHERE "+ KEY_PH_NO +" = "+number +" AND " + KEY_NAME + "="+body;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }
    public void deleteposted (String body,String smsid){

        //  String countQuery = "DELETE FROM "+ TABLE_INBOX +" WHERE "+ KEY_PH_NO +"="+number+" AND "+ KEY_NAME + "="+body;
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CONTACTS,
                KEY_NAME + " = ? AND " + KEY_TIME + " = ?",
                new String[] {body, smsid+""});
        db.close();
    }

    //add sms to TABLE_INBOX
    public void addsms(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME, contact.getTime());

        // Inserting Row
        db.insert(TABLE_INBOX, null, values);
        db.close(); // Closing Database connection

    }
    //Search sms from TABLE_INBOX
    public void deletesms (String body,String smsid){

        //  String countQuery = "DELETE FROM "+ TABLE_INBOX +" WHERE "+ KEY_PH_NO +"="+number+" AND "+ KEY_NAME + "="+body;
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_INBOX,
                KEY_NAME + " = ? AND " + KEY_TIME + " = ?",
                new String[] {body, smsid+""});
        db.close();
    }

    public int getSmsCount() {

        String countQuery = "SELECT  * FROM " + TABLE_INBOX;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public List<Contact> getAllsms() {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_INBOX;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
        }
        cursor.close();
        db.close();
        // return contact list
        return contactList;
    }

    public List<Contact> getAllsmsPending() {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_INBOX;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            Contact contact = new Contact();
            contact.setId(Integer.parseInt(cursor.getString(0)));
            contact.setName(cursor.getString(1));
            contact.setTime(cursor.getString(2));
            contact.setPhone_number(cursor.getString(3));
            // Adding contact to list
            contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return contactList;
    }

    public void deleteAllsms(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_INBOX);
        db.close();
    }
    //table calls inside . . . .
//    String CREATE_CALL_TABLE = "CREATE TABLE " + CALLS_QUEUE + "("
//            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
//            + KEY_PH_NO  + " TEXT," + KEY_TIME + " TEXT," + KEY_TYPE + " TEXT," + KEY_TXID + " TEXT,"
//            + KEY_STATUS_LOG + " TEXT," + KEY_STATUS_AUDIO+ " TEXT" + ")";
    public void addCallInQueue(Contact contact) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone_number()); // Contact Phone Number
        values.put(KEY_TIME,contact.getTime());
        values.put(KEY_TYPE,contact.getType());
        values.put(KEY_TXID,contact.getTrxid());
        values.put(KEY_STATUS_LOG, contact.getStatus_log());
        values.put(KEY_STATUS_AUDIO, contact.getStatus_audio());
        // Inserting Row
        db.insert(CALLS_QUEUE, null, values);
        db.close(); // Closing Database connection
        Log.i(TAG, "add call");
    }

    public void updateCall()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + CALLS_QUEUE +" SET "+ KEY_TIME +" ='1'");
        db.close();
        Log.i("RabbysDB", "update call");
    }

    public int lastCallfind(String trnId){
        String countQuery = "SELECT  * FROM " + CALLS_QUEUE + " WHERE "+ KEY_NAME +" = "+trnId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();
        db.close();
        Log.i("RabbysDB", "Last call:"+count);
        return count;
    }

    public int getCallCount() {
        String countQuery = "SELECT  * FROM " + CALLS_QUEUE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        int count=cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public boolean deleteCallQueue (String trxid){
        //  String countQuery = "DELETE FROM "+ TABLE_INBOX +" WHERE "+ KEY_PH_NO +"="+number+" AND "+ KEY_NAME + "="+body;
        Log.i(TAG,"Call queue deleted: "+trxid);
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRow = db.delete(CALLS_QUEUE, KEY_TXID+" = ? ", new String[] { trxid });
        db.close();
        if(deletedRow > 0){
            return true;
        }else{
            return false;
        }

//        String deleteQuery = "DELETE FROM " + CALLS_QUEUE + " WHERE "+ KEY_TXID +" = '"+trxid+"';";
//        Cursor cursor = db.rawQuery(deleteQuery, null);
////        db.delete(CALLS_QUEUE,
////                KEY_NAME + " = ? AND " + KEY_PH_NO + " = ?",
////                new String[] {body, body+""});
//        cursor.close();
//        db.close();
    }

    public List<Contact> getAllCall() {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        //String selectQuery = "SELECT  * FROM " + CALLS_QUEUE + " WHERE "+ KEY_STATUS_AUDIO +" = '0'";
        String selectQuery = "SELECT  * FROM " + CALLS_QUEUE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        Log.d(TAG,"Call list from TABLE CALL");

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                //contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhone_number(cursor.getString(2));
                contact.setTime(cursor.getString(3));
                contact.setType(cursor.getString(4));
                contact.setTrxid(cursor.getString(5));
                contact.setStatus_log(cursor.getString(6));
                contact.setStatus_audio(cursor.getString(7));
                // Adding contact to list
                contactList.add(contact);
                Log.i(TAG,Integer.parseInt(cursor.getString(0))+" "+cursor.getString(1)+" "+cursor.getString(2)+" "+cursor.getString(3)+"\n");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.i("RabbysDB", "get 0 call");
        // return contact list
        return contactList;
    }

    public void deleteAllCall(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CALLS_QUEUE);
        db.close();
    }

    public List<Contact> getAllCalls() {

        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + CALLS_QUEUE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
                Contact contact = new Contact();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setTime(cursor.getString(2));
                contact.setPhone_number(cursor.getString(3));
                // Adding contact to list
                contactList.add(contact);
        }

        cursor.close();
        db.close();
        // return contact list
        return contactList;
    }

    public void updateCalllogForMissed(String trID, String callType)
    {
        Log.d(TAG,"Call from CALLS_QUEUE - updateCalllogForMissed");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + CALLS_QUEUE +" SET "+ KEY_TYPE +" ='"+ callType+"' WHERE "+KEY_TXID+" = '"+trID+"'");
        db.close();
    }
    public void updateCallQueueForAudioUp(String trID, String status)
    {
        Log.d(TAG,"Call from CALLS_QUEUE - updateCalllogForMissed");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + CALLS_QUEUE +" SET "+ KEY_STATUS_AUDIO +" ='"+ status+"' WHERE "+KEY_TXID+" = '"+trID+"'");
        db.close();
    }
    public void updateCallQueueForLogUp(String trID, String status)
    {
        Log.d(TAG,"Call from CALLS_QUEUE - updateCalllogForMissed");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + CALLS_QUEUE +" SET "+ KEY_STATUS_LOG +" ='"+ status+"' WHERE "+KEY_TXID+" = '"+trID+"'");
        db.close();
    }

    // funtions for outboxsms table
    public void addNewSms(ServerSms serverSms)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SMS_ID,serverSms.getSmsId());
        values.put(COLUMN_SMS_TO,serverSms.getSmsTo());
        values.put(COLUMN_SMS_BODY,serverSms.getSmsBody());
        values.put(COLUMN_PULL_TIME,serverSms.getPullTime());
        values.put(COLUMN_SUBMISSION_TIME,serverSms.getSubmissionTime());
        values.put(COLUMN_DELIVERY_TIME,serverSms.getDeliveryTime());
        values.put(COLUMN_SMS_STATUS,serverSms.getSmsStatus());

        sqLiteDatabase.insert(TABLE_OUTBOXSMS, null, values);
        sqLiteDatabase.close();
    }

    public ArrayList<ServerSms> getAllServerSmsData()
    {
        ArrayList<ServerSms> serverSmsList=new ArrayList<ServerSms>();

//        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOXSMS +" ORDER BY " + COLUMN_ID + "  DESC";
        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOXSMS ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst())
        {
            do {
                ServerSms serverSms=new ServerSms();
                serverSms.setSmsId(cursor.getString(1));
                serverSms.setSmsTo(cursor.getString(2));
                serverSms.setSmsBody(cursor.getString(3));
                serverSms.setPullTime(cursor.getString(4));
                serverSms.setSubmissionTime(cursor.getString(5));
                serverSms.setDeliveryTime(cursor.getString(6));
                serverSms.setSmsStatus(cursor.getInt(7));

                serverSmsList.add(serverSms);


            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return serverSmsList;
    }

    public ArrayList<ServerSms> getProcessedServerSmsData()
    {
        ArrayList<ServerSms> processedServerSmsList=new ArrayList<ServerSms>();
        SQLiteDatabase db = this.getWritableDatabase();

        String[] columnNames={COLUMN_SMS_ID,COLUMN_SMS_TO,COLUMN_SMS_BODY,COLUMN_PULL_TIME,COLUMN_SUBMISSION_TIME,COLUMN_DELIVERY_TIME,COLUMN_SMS_STATUS};
        String[] whereArgs={String.valueOf(Constants.PROCESSED)};

        Cursor cursor=db.query(TABLE_OUTBOXSMS,columnNames,COLUMN_SMS_STATUS+"=?",whereArgs,null,null,null,null);

        if(cursor.moveToFirst())
        {
            do {
                ServerSms serverSms=new ServerSms();
                serverSms.setSmsId(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_ID)));
                serverSms.setSmsTo(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_TO)));
                serverSms.setSmsBody(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_BODY)));
                serverSms.setPullTime(cursor.getString(cursor.getColumnIndex(COLUMN_PULL_TIME)));
                serverSms.setSubmissionTime(cursor.getString(cursor.getColumnIndex(COLUMN_SUBMISSION_TIME)));
                serverSms.setDeliveryTime(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_TIME)));
                serverSms.setSmsStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_SMS_STATUS)));

                processedServerSmsList.add(serverSms);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return processedServerSmsList;

    }




    public ArrayList<ServerSms> getSentAndDeliveredServerSmsData()
    {
        ArrayList<ServerSms> sentAndDeliveredServerSmsList=new ArrayList<ServerSms>();
        SQLiteDatabase database = this.getWritableDatabase();

        String[] columnNames={COLUMN_SMS_ID,COLUMN_SMS_TO,COLUMN_SMS_BODY,COLUMN_PULL_TIME,COLUMN_SUBMISSION_TIME,COLUMN_DELIVERY_TIME,COLUMN_SMS_STATUS};

        String selection=COLUMN_SMS_STATUS+"=? OR "+COLUMN_SMS_STATUS+"=? OR "+COLUMN_SMS_STATUS+"=? OR "+
                COLUMN_SMS_STATUS+"=? OR "+COLUMN_SMS_STATUS+"=? OR "+COLUMN_SMS_STATUS+"=? OR "+COLUMN_SMS_STATUS+"=?"
                +"=? OR "+COLUMN_SMS_STATUS+"=?";

        String[] whereArgs={String.valueOf(Constants.SENT_RESULT_OK),String.valueOf(Constants.SENT_RESULT_ERROR_GENERIC_FAILURE),
                String.valueOf(Constants.SENT_RESULT_ERROR_NO_SERVICE),String.valueOf(Constants.SENT_RESULT_ERROR_NULL_PDU),
                String.valueOf(Constants.SENT_RESULT_ERROR_RADIO_OFF),String.valueOf(Constants.DELIVERY_RESULT_OK),
                String.valueOf(Constants.DELIVERY_RESULT_CANCELED),String.valueOf(Constants.SMS_SUBMMIT)};

        Cursor cursor=database.query(TABLE_OUTBOXSMS,columnNames,selection,whereArgs,null,null,null,null);

        if(cursor.moveToFirst())
        {
            do {
                ServerSms serverSms=new ServerSms();
                serverSms.setSmsId(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_ID)));
                serverSms.setSmsTo(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_TO)));
                serverSms.setSmsBody(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_BODY)));
                serverSms.setPullTime(cursor.getString(cursor.getColumnIndex(COLUMN_PULL_TIME)));
                serverSms.setSubmissionTime(cursor.getString(cursor.getColumnIndex(COLUMN_SUBMISSION_TIME)));
                serverSms.setDeliveryTime(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_TIME)));
                serverSms.setSmsStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_SMS_STATUS)));

                sentAndDeliveredServerSmsList.add(serverSms);
            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return sentAndDeliveredServerSmsList;

    }




    public ArrayList<ServerSms> getDeliveredServerSmsData()
    {
        ArrayList<ServerSms> deliveredServerSmsList=new ArrayList<ServerSms>();
        SQLiteDatabase database = this.getWritableDatabase();

        String[] columnNames={COLUMN_SMS_ID,COLUMN_SMS_TO,COLUMN_SMS_BODY,COLUMN_PULL_TIME,COLUMN_SUBMISSION_TIME,COLUMN_DELIVERY_TIME,COLUMN_SMS_STATUS};
        String selection=COLUMN_SMS_STATUS+"=? OR "+COLUMN_SMS_STATUS+"=?";
        String[] whereArgs={String.valueOf(Constants.DELIVERY_RESULT_OK),String.valueOf(Constants.DELIVERY_RESULT_CANCELED)};

        Cursor cursor=database.query(TABLE_OUTBOXSMS,columnNames,selection,whereArgs,null,null,null,null);

        if(cursor.moveToFirst())
        {
            do {
                ServerSms serverSms=new ServerSms();
                serverSms.setSmsId(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_ID)));
                serverSms.setSmsTo(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_TO)));
                serverSms.setSmsBody(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_BODY)));
                serverSms.setPullTime(cursor.getString(cursor.getColumnIndex(COLUMN_PULL_TIME)));
                serverSms.setSubmissionTime(cursor.getString(cursor.getColumnIndex(COLUMN_SUBMISSION_TIME)));
                serverSms.setDeliveryTime(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_TIME)));
                serverSms.setSmsStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_SMS_STATUS)));

                deliveredServerSmsList.add(serverSms);


            }while(cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return deliveredServerSmsList;

    }

    public void updateSmsStatus(String smsId,int statusCode)
    {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SMS_STATUS,statusCode);
        //values.put(COLUMN_DELIVERY_TIME,time);
        String[] whereArgs={smsId};
        sqLiteDatabase.update(TABLE_OUTBOXSMS,values,COLUMN_SMS_ID+"=?",whereArgs);
       sqLiteDatabase.close();
    }

    public void deleteSmsWhenPull()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] whereArgs={Integer.toString(Constants.DELIVERY_RESULT_OK)};
        sqLiteDatabase.delete(TABLE_OUTBOXSMS,COLUMN_SMS_STATUS+"=?", whereArgs);
        sqLiteDatabase.close();
    }

    public void deleteSmsWithSmsId(String smsId)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String[] whereArgs={smsId};
        sqLiteDatabase.delete(TABLE_OUTBOXSMS,COLUMN_SMS_ID+"=?", whereArgs);
        sqLiteDatabase.close();
    }

    public void updateSmsSubmissionTime(String smsId, String timeStamp)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBMISSION_TIME,timeStamp);
        String[] whereArgs={smsId};
        sqLiteDatabase.update(TABLE_OUTBOXSMS,values,COLUMN_SMS_ID+"=?",whereArgs);
       sqLiteDatabase.close();
    }

    public void updateSmsDeliveryTime(String smsId, String timeStamp)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DELIVERY_TIME,timeStamp);
        String[] whereArgs={smsId};
        sqLiteDatabase.update(TABLE_OUTBOXSMS,values,COLUMN_SMS_ID+"=?",whereArgs);
        sqLiteDatabase.close();
    }

    public void deleteAllOutBoxSmsData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_OUTBOXSMS);
        db.close();
    }


    public int getPendingSmsCount()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query="SELECT * FROM "+TABLE_OUTBOXSMS+" WHERE "+COLUMN_SMS_STATUS+" =?";
        String[] selectionArgs={String.valueOf(Constants.PENDING)};
        Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);
        int count=cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return count;
    }


    // all functions ended for outboxsms table





    // all functions for SENT_SMS table;

    public void addNewSentSms(ServerSms serverSms)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SMS_ID,serverSms.getSmsId());
        values.put(COLUMN_SMS_TO,serverSms.getSmsTo());
        values.put(COLUMN_SMS_BODY,serverSms.getSmsBody());
        values.put(COLUMN_SUBMISSION_TIME,serverSms.getSubmissionTime());

        sqLiteDatabase.insert(TABLE_SENTSMS, null, values);
        sqLiteDatabase.close();
    }


    public ArrayList<ServerSms> getAllSentSmsData() {
        ArrayList<ServerSms> serverSmsList = new ArrayList<ServerSms>();

//        String selectQuery = "SELECT  * FROM " + TABLE_OUTBOXSMS +" ORDER BY " + COLUMN_ID + "  DESC";
        String selectQuery = "SELECT * FROM " + TABLE_SENTSMS+" ORDER BY " + COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ServerSms serverSms = new ServerSms();
                serverSms.setSmsId(cursor.getString(1));
                serverSms.setSmsTo(cursor.getString(2));
                serverSms.setSmsBody(cursor.getString(3));
                serverSms.setSubmissionTime(cursor.getString(4));

                serverSmsList.add(serverSms);


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return serverSmsList;

    }

    // this function pull all the sms those are in sentsms table.
    public int getSentSmsCount()
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query="SELECT * FROM "+TABLE_SENTSMS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int count=cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();
        return count;
    }


    // add first row sent sms

    public void addFirstRowSentSms(ServerSms serverSms)
    {

    }


    // delete last row sent sms

    public void deleteLastRowSentSms()
    {

    }

    // delete all sent sms data from TABLE_SENT_SMS

    public void deleteALlSentSms()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_SENTSMS);
        db.close();
    }

    // all functions ended for sentsms table.



    //____________________Rafi_______________

    public boolean isemptyprocessed()
    {
        SQLiteDatabase database = this.getWritableDatabase();

        String[] columnNames={COLUMN_SMS_ID,COLUMN_SMS_TO,COLUMN_SMS_BODY,COLUMN_PULL_TIME,COLUMN_SUBMISSION_TIME,COLUMN_DELIVERY_TIME,COLUMN_SMS_STATUS};

        String selection=COLUMN_SMS_STATUS+"=? ";
        String[] whereArgs={String.valueOf(Constants.PROCESSED)};
        Cursor cursor=database.query(TABLE_OUTBOXSMS,columnNames,selection,whereArgs,null,null,null,null);

        if(cursor.moveToFirst())
        {
            database.close();
            cursor.close();
            return true;
        }
        database.close();
        cursor.close();
        return false;
    }


    public int notprocessedsms()
    { int count;
        SQLiteDatabase database = this.getWritableDatabase();

        String[] columnNames={COLUMN_SMS_ID,COLUMN_SMS_TO,COLUMN_SMS_BODY,COLUMN_PULL_TIME,COLUMN_SUBMISSION_TIME,COLUMN_DELIVERY_TIME,COLUMN_SMS_STATUS};
        String selection=COLUMN_SMS_STATUS+"!=? ";
        String[] whereArgs={String.valueOf(Constants.PROCESSED)};

        //Log.i(TAG,String.valueOf(Constants.PROCESSED));

        Cursor cursor=database.query(TABLE_OUTBOXSMS,columnNames,selection,whereArgs,null,null,null,null);
        count=cursor.getCount();
        cursor.close();
        database.close();
        return count;
    }


    //////////////////////////by bidyut
    //insert to IntentStatus
    public boolean insertIntentStatus(IntentStatus status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_INTENT_SMSID, status.getSmsID());
        values.put(COL_INTENT_STATUS, status.getStatus());
        values.put(COL_INTENT_TIME, status.getTime());

        long insertedRow = db.insert(TABLE_INTENT,null, values);

        db.close();

        if (insertedRow > 0){
            return true;
        }else {
            return false;
        }
    }

    //retrieve all IntentStatus

    public ArrayList<IntentStatus> getAllIntentStatus(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<IntentStatus>intentStatusList=new ArrayList<>();

        Cursor c = db.query(TABLE_INTENT, null, null,null,null, null,null);
        if (c!=null && c.getCount()>0){
            c.moveToFirst();
            do {
                int inedx =c.getInt(c.getColumnIndex(COL_INTENT_INDEX));
                String smsID = c.getString(c.getColumnIndex(COL_INTENT_SMSID));
                int status = c.getInt(c.getColumnIndex(COL_INTENT_STATUS));
                String time = c.getString(c.getColumnIndex(COL_INTENT_TIME));
                IntentStatus intentStatus = new IntentStatus(inedx,smsID,status,time);
                intentStatusList.add(intentStatus);
            }while (c.moveToNext());
        }
        c.close();

        db.close();
        return intentStatusList;
    }

    //delete IntentStatus by index number
    public boolean deleteIntentStatusBySmsID(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRow = db.delete(TABLE_INTENT, COL_INTENT_SMSID+" = ? ", new String[] { id });
        db.close();
        if(deletedRow > 0){
            return true;
        }else{
            return false;
        }
    }
    //check IntentStatus size
    public int countIntentStatus(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_INTENT, null, null,null,null, null,null);
        int size = c.getCount();
        c.close();
        db.close();
        return size;
    }
    //get first IntentStatus index number
    public String firstMessageIndex(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_INTENT, null, null,null,null, null,null);
        c.moveToFirst();
        String index = String.valueOf(c.getInt(c.getColumnIndex(COL_INTENT_INDEX)));
        c.close();
        db.close();
        return index;
    };


    //bidyut
    public int getCountProcessedSms()
    {
        ArrayList<ServerSms> processedServerSmsList=new ArrayList<ServerSms>();
        SQLiteDatabase database = this.getWritableDatabase();

        String[] columnNames={COLUMN_SMS_ID,COLUMN_SMS_TO,COLUMN_SMS_BODY,COLUMN_PULL_TIME,COLUMN_SUBMISSION_TIME,COLUMN_DELIVERY_TIME,COLUMN_SMS_STATUS};
        String[] whereArgs={String.valueOf(Constants.PROCESSED)};

        Cursor cursor=database.query(TABLE_OUTBOXSMS,columnNames,COLUMN_SMS_STATUS+"=?",whereArgs,null,null,null,null);

        int count=cursor.getCount();
        cursor.close();
        database.close();
        return count;

    }

    public ArrayList<ServerSms> getAllOutBox(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ServerSms>serverSmsArrayList=new ArrayList<>();

        Cursor cursor = db.query(TABLE_OUTBOXSMS, null, null,null,null, null,null);
        if (cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {
                ServerSms serverSms=new ServerSms();
                serverSms.setSmsId(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_ID)));
                serverSms.setSmsTo(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_TO)));
                serverSms.setSmsBody(cursor.getString(cursor.getColumnIndex(COLUMN_SMS_BODY)));
                serverSms.setPullTime(cursor.getString(cursor.getColumnIndex(COLUMN_PULL_TIME)));
                serverSms.setSubmissionTime(cursor.getString(cursor.getColumnIndex(COLUMN_SUBMISSION_TIME)));
                serverSms.setDeliveryTime(cursor.getString(cursor.getColumnIndex(COLUMN_DELIVERY_TIME)));
                serverSms.setSmsStatus(cursor.getInt(cursor.getColumnIndex(COLUMN_SMS_STATUS)));

                serverSmsArrayList.add(serverSms);
            }while (cursor.moveToNext());
        }
        cursor.close();

        db.close();
        return serverSmsArrayList;
    }
}

