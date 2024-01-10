package org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ScheduleSMSDetails {
    @SerializedName("txid")
    @Expose
    private String txid;
    @SerializedName("schedule_time")
    @Expose
    private String scheduleTime;
    @SerializedName("schedule_title")
    @Expose
    private String scheduleTitle;
    @SerializedName("schedule_type")
    @Expose
    private String scheduleType;
    @SerializedName("messages")
    @Expose
    private List<Message> messages = null;

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getScheduleTitle() {
        return scheduleTitle;
    }

    public void setScheduleTitle(String scheduleTitle) {
        this.scheduleTitle = scheduleTitle;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    ///////////////////////////
    public class Message {

        @SerializedName("sent_time")
        @Expose
        private String sentTime;
        @SerializedName("sms_text")
        @Expose
        private String smsText;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("smsId")
        @Expose
        private String smsId;

        public String getSentTime() {
            return sentTime;
        }

        public void setSentTime(String sentTime) {
            this.sentTime = sentTime;
        }

        public String getSmsText() {
            return smsText;
        }

        public void setSmsText(String smsText) {
            this.smsText = smsText;
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
}
