package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactSummeryGroup {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("group_name")
    @Expose
    private String groupName;
    @SerializedName("number_of_contact")
    @Expose
    private String numberOfContact;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNumberOfContact() {
        return numberOfContact;
    }

    public void setNumberOfContact(String numberOfContact) {
        this.numberOfContact = numberOfContact;
    }
}
