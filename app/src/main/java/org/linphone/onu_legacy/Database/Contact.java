package org.linphone.onu_legacy.Database;


import android.util.Log;

/**
 * Created by jhorotek on 7/6/2015.
 */
public class Contact {


    //private variables
    int id;
    String name;
    String phone_number;
    String time;
    String type;
    String status_log; //This for call-popup logging
    String status_audio;
    String status;
    String trxid;
    // Empty constructor
    public Contact() {

    }

    // constructor
    public Contact(int id, String name, String _phone_number,String time) {
        this.id = id;
        this.name = name;
        this.phone_number = _phone_number;
        this.time = time;
    }

    // constructor
    public Contact(String name, String _phone_number,String time) {
        this.name = name;
        this.phone_number = _phone_number;
        this.time = time;
    }

    // constructor for call pop-up logging.
    public Contact(String name, String _phone_number,String time,String status) {
        this.name = name;
        this.phone_number = _phone_number;
        this.time = time;
        this.status=status;
    }

    public Contact(String _name, String _phone_number, String _time, String _type, String trxid, String status_log, String status_audio) {
        this.name = _name;
        this.phone_number = _phone_number;
        this.time = _time;
        this.type = _type;
        this.status_log = status_log;
        this.status_audio = status_audio;
        this.trxid = trxid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        // Log.i("Contact","User's Phone Number: "+phone_number);
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus_log() {
        return status_log;
    }

    public void setStatus_log(String status_log) {
        this.status_log = status_log;
    }

    public String getStatus_audio() {
        return status_audio;
    }

    public void setStatus_audio(String status_audio) {
        this.status_audio = status_audio;
    }

    public String getTrxid() {
        return trxid;
    }

    public void setTrxid(String trxid) {
        this.trxid = trxid;
    }
}
