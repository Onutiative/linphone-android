package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

public class VirtualContact {

    private String contact_value;
    private String contact_type_id;

    public VirtualContact(String contact_value, String contact_type_id) {
        this.contact_value = contact_value;
        this.contact_type_id = contact_type_id;
    }

    public String getContact_value() {
        return contact_value;
    }

    public String getContact_type_id() {
        return contact_type_id;
    }
}
