package org.linphone.onu_legacy.Database;

public class NewTask {

    private int id;
    private String callerName, callPurpose, taskStatus, callSummary,
            callerMsisdn, deviceId, callType, callerId, callTime, employeeEmail, estimatedTime;


    public NewTask()
    {

    }

    public NewTask( String callerName, String callPurpose, String taskStatus,
                   String callSummary, String callerMsisdn, String deviceId,
                   String callType, String callerId, String callTime, String employeeEmail, String estimatedTime) {

        this.callerName = callerName;
        this.callPurpose = callPurpose;
        this.taskStatus = taskStatus;
        this.callSummary = callSummary;
        this.callerMsisdn = callerMsisdn;
        this.deviceId = deviceId;
        this.callType = callType;
        this.callerId = callerId;
        this.callTime = callTime;
        this.employeeEmail = employeeEmail;
        this.estimatedTime = estimatedTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallPurpose() {
        return callPurpose;
    }

    public void setCallPurpose(String callPurpose) {
        this.callPurpose = callPurpose;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCallSummary() {
        return callSummary;
    }

    public void setCallSummary(String callSummary) {
        this.callSummary = callSummary;
    }

    public String getCallerMsisdn() {
        return callerMsisdn;
    }

    public void setCallerMsisdn(String callerMsisdn) {
        this.callerMsisdn = callerMsisdn;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}
