package org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses;

public class InboxSMSDetails {
    int id;
    String contact;
    String date;
    String smsBody;

    public InboxSMSDetails(int id, String contact, String date, String smsBody) {
        this.id = id;
        this.contact = contact;
        this.date = date;
        this.smsBody = smsBody;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }
}
