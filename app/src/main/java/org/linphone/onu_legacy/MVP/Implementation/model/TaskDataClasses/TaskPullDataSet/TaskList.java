package org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskList {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("call_type")
    @Expose
    private String callType;
    @SerializedName("call_time")
    @Expose
    private String callTime;
    @SerializedName("caller_id")
    @Expose
    private String callerId;
    @SerializedName("caller_name")
    @Expose
    private String callerName;
    @SerializedName("caller_gender")
    @Expose
    private String callerGender;
    @SerializedName("call_summery")
    @Expose
    private String callSummery;
    @SerializedName("call_reason")
    @Expose
    private String callReason;
    @SerializedName("insert_time")
    @Expose
    private String insertTime;
    @SerializedName("summery_status")
    @Expose
    private String summeryStatus;
    @SerializedName("status_summery")
    @Expose
    private Object statusSummery;
    @SerializedName("onu_user_id")
    @Expose
    private Object onuUserId;
    @SerializedName("call_summery_id")
    @Expose
    private Object callSummeryId;
    @SerializedName("employee_id")
    @Expose
    private Object employeeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerGender() {
        return callerGender;
    }

    public void setCallerGender(String callerGender) {
        this.callerGender = callerGender;
    }

    public String getCallSummery() {
        return callSummery;
    }

    public void setCallSummery(String callSummery) {
        this.callSummery = callSummery;
    }

    public String getCallReason() {
        return callReason;
    }

    public void setCallReason(String callReason) {
        this.callReason = callReason;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getSummeryStatus() {
        return summeryStatus;
    }

    public void setSummeryStatus(String summeryStatus) {
        this.summeryStatus = summeryStatus;
    }

    public Object getStatusSummery() {
        return statusSummery;
    }

    public void setStatusSummery(Object statusSummery) {
        this.statusSummery = statusSummery;
    }

    public Object getOnuUserId() {
        return onuUserId;
    }

    public void setOnuUserId(Object onuUserId) {
        this.onuUserId = onuUserId;
    }

    public Object getCallSummeryId() {
        return callSummeryId;
    }

    public void setCallSummeryId(Object callSummeryId) {
        this.callSummeryId = callSummeryId;
    }

    public Object getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Object employeeId) {
        this.employeeId = employeeId;
    }
}
