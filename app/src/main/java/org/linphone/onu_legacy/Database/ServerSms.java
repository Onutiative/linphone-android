package org.linphone.onu_legacy.Database;

public class ServerSms {

    private String smsId, smsTo, smsBody, pullTime, submissionTime, deliveryTime;
    private int smsStatus;

    public ServerSms(String smsId, String smsTo, String smsBody, String pullTime, String submissionTime, String deliveryTime, int smsStatus) {
        this.smsId = smsId;
        this.smsTo = smsTo;
        this.smsBody = smsBody;
        this.pullTime = pullTime;
        this.submissionTime = submissionTime;
        this.deliveryTime = deliveryTime;
        this.smsStatus = smsStatus;
    }

    public ServerSms()
    {}


    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }

    public String getSmsTo() {
        return smsTo;
    }

    public void setSmsTo(String smsTo) {
        this.smsTo = smsTo;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public String getPullTime() {
        return pullTime;
    }

    public void setPullTime(String pullTime) {
        this.pullTime = pullTime;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getSmsStatus() {
        return smsStatus;
    }

    public void setSmsStatus(int smsStatus) {
        this.smsStatus = smsStatus;
    }



}
