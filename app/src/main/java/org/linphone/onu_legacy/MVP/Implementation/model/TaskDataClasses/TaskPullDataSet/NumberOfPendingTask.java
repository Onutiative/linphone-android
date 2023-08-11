package org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NumberOfPendingTask {

    @SerializedName("Pending_Task")
    @Expose
    private String pendingTask;

    public String getPendingTask() {
        return pendingTask;
    }

    public void setPendingTask(String pendingTask) {
        this.pendingTask = pendingTask;
    }
}
