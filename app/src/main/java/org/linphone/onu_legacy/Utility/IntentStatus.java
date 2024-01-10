package org.linphone.onu_legacy.Utility;

public class IntentStatus {
    private int id;
    private String smsID;
    private int status;
    private String time;

    public IntentStatus(int id, String smsID, int status, String time) {
        this.id = id;
        this.smsID = smsID;
        this.status = status;
        this.time = time;
    }

    public IntentStatus(String smsID, int status, String time) {
        this.smsID = smsID;
        this.status = status;
        this.time = time;
    }

    public int getId() {
        return id;
    }


    public String getSmsID() {
        return smsID;
    }

    public void setSmsID(String smsID) {
        this.smsID = smsID;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
