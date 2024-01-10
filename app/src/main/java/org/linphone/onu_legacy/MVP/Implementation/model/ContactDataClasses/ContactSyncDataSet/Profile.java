package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

public class Profile {

    private String first_name;
    private String last_name;
    private String image_path;
    private String gender;
    private String nick_name;
    private String created_at;
    private String created_by;
    private String updated_at;
    private String updated_by;
    private String contact_type_id;
    private String sourceContactRefID;
    private String source;
    private String owner_id;
    private String is_private;

    public Profile(String first_name, String last_name, String image_path, String gender, String nick_name, String created_at, String created_by, String updated_at, String updated_by, String contact_type_id, String sourceContactRefID, String source, String owner_id, String is_private) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_path = image_path;
        this.gender = gender;
        this.nick_name = nick_name;
        this.created_at = created_at;
        this.created_by = created_by;
        this.updated_at = updated_at;
        this.updated_by = updated_by;
        this.contact_type_id = contact_type_id;
        this.sourceContactRefID = sourceContactRefID;
        this.source = source;
        this.owner_id = owner_id;
        this.is_private = is_private;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getGender() {
        return gender;
    }

    public String getNick_name() {
        return nick_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public String getContact_type_id() {
        return contact_type_id;
    }

    public String getSourceContactRefID() {
        return sourceContactRefID;
    }

    public String getSource() {
        return source;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public String getIs_private() {
        return is_private;
    }
}
