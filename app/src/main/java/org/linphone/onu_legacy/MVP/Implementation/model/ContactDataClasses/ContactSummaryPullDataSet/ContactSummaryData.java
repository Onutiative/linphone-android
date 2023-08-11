package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactSummaryData {
    @SerializedName("total_contact")
    @Expose
    private String totalContact;
    @SerializedName("personal_contact")
    @Expose
    private String personalContact;
    @SerializedName("organization_contact")
    @Expose
    private String organizationContact;
    @SerializedName("group")
    @Expose
    private List<ContactSummeryGroup> group = null;

    public String getTotalContact() {
        return totalContact;
    }

    public void setTotalContact(String totalContact) {
        this.totalContact = totalContact;
    }

    public String getPersonalContact() {
        return personalContact;
    }

    public void setPersonalContact(String personalContact) {
        this.personalContact = personalContact;
    }

    public String getOrganizationContact() {
        return organizationContact;
    }

    public void setOrganizationContact(String organizationContact) {
        this.organizationContact = organizationContact;
    }

    public List<ContactSummeryGroup> getGroup() {
        return group;
    }

    public void setGroup(List<ContactSummeryGroup> group) {
        this.group = group;
    }
}
