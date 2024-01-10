package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactPullDataSet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactPulledData {
    @SerializedName("contact_list")
    @Expose
    private List<ContactList> contactList = null;
    public List<ContactList> getContactList() {
        return contactList;
    }
    public void setContactList(List<ContactList> contactList) {
        this.contactList = contactList;
    }
}
