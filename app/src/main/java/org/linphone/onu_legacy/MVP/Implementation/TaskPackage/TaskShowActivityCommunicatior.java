package org.linphone.onu_legacy.MVP.Implementation.TaskPackage;

import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskList;

import java.util.List;

public interface TaskShowActivityCommunicatior {
    public interface TashShowView{
        public void toTaskListAdapter(List<TaskList> taskLists);
    }
    public interface TaskShowPresenter{
        public void startExecution(String taskType);
    }
}
