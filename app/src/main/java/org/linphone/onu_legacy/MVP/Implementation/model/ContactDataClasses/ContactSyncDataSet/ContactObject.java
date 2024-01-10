package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactObject {

    private String sourceContactRefID;

    @SerializedName("Source")
    private String source;

    @SerializedName("Contacts")
    private List<Contacts> contacts;

    public ContactObject(String sourceContactRefID, String source, List<Contacts> contacts) {
        this.sourceContactRefID = sourceContactRefID;
        this.source = source;
        this.contacts = contacts;
    }

    public String getSourceContactRefID() {
        return sourceContactRefID;
    }

    public String getSource() {
        return source;
    }

    public List<Contacts> getContacts() {
        return contacts;
    }
}
