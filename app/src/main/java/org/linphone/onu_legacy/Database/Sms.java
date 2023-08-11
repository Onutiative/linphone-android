package org.linphone.onu_legacy.Database;



/**
 * Created by jhorotek on 7/6/2015.
 */
public class Sms {


    //private variables
    int _id;
    String _name;
    String _phone_number;
    String _time;
    String _type;
    String _status;

    // Empty constructor
    public Sms() {

    }

    // constructor
    public Sms(int id, String name, String _phone_number,String time,String type,String status) {
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
        this._time = time;
        this._type=type;
        this._status=status;
    }

    // constructor
    public Sms(String name, String _phone_number,String time,String type,String status) {
        this._name = name;
        this._phone_number = _phone_number;
        this._time = time;
        this._type=type;
        this._status=status;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }

    public String getTime(){
        return  this._time;
    }

    public void setTime(String time) {
        this._time = time;
    }


    public String getType(){
        return  this._type;
    }

    public void setType(String type) {
        this._type = type;
    }


    public String getStatus(){
        return  this._status;
    }

    public void setStatus(String status) {
        this._status = status;
    }

    // getting phone number
    public String getPhoneNumber() {
        return this._phone_number;
    }

    // setting phone number
    public void setPhoneNumber(String phone_number) {
        this._phone_number = phone_number;
    }



}
