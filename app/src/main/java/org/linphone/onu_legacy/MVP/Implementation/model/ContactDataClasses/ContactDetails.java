package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Comparator;

public class ContactDetails implements Serializable {

    public String contactId;
    public String contactName;
    private String firstName;
    private String lastName;
    public String contactValue;
    private String imagePath;
    private String gender;
    private String nickName;
    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
    private String contactTypeId;
    private String sourceContactRefID;
    private String source;
    private String ownerId;
    private String isPrivate;
    private String phoneContactId;
    public boolean select;

//    public ContactDetails(String id, String userId, String contactId, String contactName, String contactValue, String syncData,boolean select) {
//        this.id = id;
//        this.userId = userId;
//        this.contactId = contactId;
//        this.contactName = contactName;
//        this.contactValue = contactValue;
//        SyncData = syncData;
//        this.select=select;
//    }
//    public ContactDetails(String id, String userId, String contactId, String contactName, String contactValue, String syncData) {
//        this.id = id;
//        this.userId = userId;
//        this.contactId = contactId;
//        this.contactName = contactName;
//        this.contactValue = contactValue;
//        SyncData = syncData;
//    }

    public ContactDetails(String contactId, String contactName,String firstName,String lastName,String contactValue, String imagePath, String gender, String nickName, String createdAt, String createdBy, String updatedAt, String updatedBy, String contactTypeId, String sourceContactRefID, String source, String ownerId, String isPrivate, String phoneContactId, boolean select) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.firstName=firstName;
        this.lastName=lastName;
        this.contactValue=contactValue;
        this.imagePath = imagePath;
        this.gender = gender;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.contactTypeId = contactTypeId;
        this.sourceContactRefID = sourceContactRefID;
        this.source = source;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.phoneContactId = phoneContactId;
        this.select = select;
    }

    public ContactDetails(String contactId, String contactName,String firstName,String lastName, String contactValue, String imagePath, String gender, String nickName, String createdAt, String createdBy, String updatedAt, String updatedBy, String contactTypeId, String sourceContactRefID, String source, String ownerId, String isPrivate, String phoneContactId) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.firstName=firstName;
        this.lastName=lastName;
        this.contactValue=contactValue;
        this.imagePath = imagePath;
        this.gender = gender;
        this.nickName = nickName;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.contactTypeId = contactTypeId;
        this.sourceContactRefID = sourceContactRefID;
        this.source = source;
        this.ownerId = ownerId;
        this.isPrivate = isPrivate;
        this.phoneContactId = phoneContactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getContactTypeId() {
        return contactTypeId;
    }

    public void setContactTypeId(String contactTypeId) {
        this.contactTypeId = contactTypeId;
    }

    public String getSourceContactRefID() {
        return sourceContactRefID;
    }

    public void setSourceContactRefID(String sourceContactRefID) {
        this.sourceContactRefID = sourceContactRefID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getPhoneContactId() {
        return phoneContactId;
    }

    public void setPhoneContactId(String phoneContactId) {
        this.phoneContactId = phoneContactId;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }


    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactValue() {
        return contactValue;
    }

    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }


    public class CustomComparator implements Comparator<ContactDetails> {
        @Override
        public int compare(ContactDetails o1, ContactDetails o2) {
            return o1.getContactName().compareTo(o2.getContactName());
        }
    }
}
