package org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaskSummaryData {
    @SerializedName("Pending_Task")
    @Expose
    private String pendingTask;
    @SerializedName("Total_Task")
    @Expose
    private String totalTask;
    @SerializedName("Solved_Task")
    @Expose
    private String solvedTask;
    @SerializedName("Na_Task")
    @Expose
    private String naTask;

    public String getPendingTask() {
        return pendingTask;
    }

    public void setPendingTask(String pendingTask) {
        this.pendingTask = pendingTask;
    }

    public String getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(String totalTask) {
        this.totalTask = totalTask;
    }

    public String getSolvedTask() {
        return solvedTask;
    }

    public void setSolvedTask(String solvedTask) {
        this.solvedTask = solvedTask;
    }

    public String getNaTask() {
        return naTask;
    }

    public void setNaTask(String naTask) {
        this.naTask = naTask;
    }
}
