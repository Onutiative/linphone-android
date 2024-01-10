package org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PullTaskResponseData {
    @SerializedName("numberOfPendingTask")
    @Expose
    private NumberOfPendingTask numberOfPendingTask;
    @SerializedName("numberOfTask")
    @Expose
    private NumberOfTask numberOfTask;
    @SerializedName("TaskList")
    @Expose
    private List<TaskList> taskList = null;

    public NumberOfPendingTask getNumberOfPendingTask() {
        return numberOfPendingTask;
    }

    public void setNumberOfPendingTask(NumberOfPendingTask numberOfPendingTask) {
        this.numberOfPendingTask = numberOfPendingTask;
    }

    public NumberOfTask getNumberOfTask() {
        return numberOfTask;
    }

    public void setNumberOfTask(NumberOfTask numberOfTask) {
        this.numberOfTask = numberOfTask;
    }

    public List<TaskList> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskList> taskList) {
        this.taskList = taskList;
    }
}
