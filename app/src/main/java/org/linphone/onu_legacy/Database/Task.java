package org.linphone.onu_legacy.Database;

import java.io.Serializable;

public class Task implements Serializable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public Task(String id, String callTime, String callSummary, String taskStatus, String callPurpose, String asigneeEmail, String estimatedDate, String estimatedTIme) {
//        this.id = id;
//        this.callTime = callTime;
//        this.callSummary = callSummary;
//        this.taskStatus = taskStatus;
//        this.callPurpose = callPurpose;
//        this.asigneeEmail = asigneeEmail;
//        this.estimatedDate = estimatedDate;
//        this.estimatedTIme = estimatedTIme;
//    }

    private String callTime;
    private String callSummary;
    private String taskStatus;
    private String callPurpose;
    private String asigneeEmail;
    private String estimatedDate;
    private String estimatedTIme;


    public Task(String callTime, String callSummary, String taskStatus) {

        this.callTime = callTime;
        this.callSummary = callSummary;
        this.taskStatus = taskStatus;
    }



    public Task(String id,String callTime, String callPurpose, String taskStatus, String callSummary, String asigneeEmail, String estimatedDate, String estimatedTIme) {
        this.id = id;
        this.callTime = callTime;
        this.callSummary = callSummary;
        this.taskStatus = taskStatus;
        this.callPurpose = callPurpose;
        this.asigneeEmail = asigneeEmail;
        this.estimatedDate = estimatedDate;
        this.estimatedTIme = estimatedTIme;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallSummary() {
        return callSummary;
    }

    public void setCallSummary(String callSummary) {
        this.callSummary = callSummary;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCallPurpose() {
        return callPurpose;
    }

    public void setCallPurpose(String callPurpose) {
        this.callPurpose = callPurpose;
    }

    public String getAsigneeEmail() {
        return asigneeEmail;
    }

    public void setAsigneeEmail(String asigneeEmail) {
        this.asigneeEmail = asigneeEmail;
    }

    public String getEstimatedDate() {
        return estimatedDate;
    }

    public void setEstimatedDate(String estimatedDate) {
        this.estimatedDate = estimatedDate;
    }

    public String getEstimatedTIme() {
        return estimatedTIme;
    }

    public void setEstimatedTIme(String estimatedTIme) {
        this.estimatedTIme = estimatedTIme;
    }
}
