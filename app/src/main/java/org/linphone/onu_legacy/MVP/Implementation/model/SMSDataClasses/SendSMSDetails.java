package org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses;

public class SendSMSDetails {

    private String sent_time;
    private String sms_text;
    private String mobile;
    private String smsId;

    public SendSMSDetails(String sent_time, String sms_text, String mobile, String smsId) {
        this.sent_time = sent_time;
        this.sms_text = sms_text;
        this.mobile = mobile;
        this.smsId = smsId;
    }

    public String getSent_time() {
        return sent_time;
    }

    public void setSent_time(String sent_time) {
        this.sent_time = sent_time;
    }

    public String getSms_text() {
        return sms_text;
    }

    public void setSms_text(String sms_text) {
        this.sms_text = sms_text;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsId() {
        return smsId;
    }

    public void setSmsId(String smsId) {
        this.smsId = smsId;
    }
}
