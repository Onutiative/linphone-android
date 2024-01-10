package org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NumberOfTask {
    @SerializedName("Total_Task")
    @Expose
    private String totalTask;

    public String getTotalTask() {
        return totalTask;
    }

    public void setTotalTask(String totalTask) {
        this.totalTask = totalTask;
    }
}
