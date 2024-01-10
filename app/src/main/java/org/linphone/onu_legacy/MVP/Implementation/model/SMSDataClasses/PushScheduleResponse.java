package org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushScheduleResponse {
    //"status":4000,"detail":"successful","message":"valid sms data found.","count":1,"trxId":"462789001561379892"
    int status;
    String detail;
    String message;
    int count;
    String trxId;

    public PushScheduleResponse(int status, String detail, String message, int count, String trxId) {
        this.status = status;
        this.detail = detail;
        this.message = message;
        this.count = count;
        this.trxId = trxId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }
}
