package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Contacts {

    @SerializedName("profile")
    private Profile profile;

    @SerializedName("address")
    private Address address;

    @SerializedName("virtualContact")
    private List<VirtualContact> virtualContact;

    @SerializedName("relation")
    private Relation relation;

    @SerializedName("profession")
    private Profession profession;

    public Contacts(Profile profile, Address address, List<VirtualContact> virtualContact, Relation relation, Profession profession) {
        this.profile = profile;
        this.address = address;
        this.virtualContact = virtualContact;
        this.relation = relation;
        this.profession = profession;
    }

    public Profile getProfile() {
        return profile;
    }

    public Address getAddress() {
        return address;
    }

    public List<VirtualContact> getVirtualContact() {
        return virtualContact;
    }

    public Relation getRelation() {
        return relation;
    }

    public Profession getProfession() {
        return profession;
    }
}
