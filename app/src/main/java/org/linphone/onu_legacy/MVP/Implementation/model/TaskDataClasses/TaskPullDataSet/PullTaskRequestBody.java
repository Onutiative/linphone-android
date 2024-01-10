package org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PullTaskRequestBody {
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("pullCount")
    @Expose
    private String pullCount;

    public PullTaskRequestBody(String start, String pullCount, String type) {
        this.start = start;
        this.type = type;
        this.pullCount = pullCount;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPullCount() {
        return pullCount;
    }

    public void setPullCount(String pullCount) {
        this.pullCount = pullCount;
    }
}
