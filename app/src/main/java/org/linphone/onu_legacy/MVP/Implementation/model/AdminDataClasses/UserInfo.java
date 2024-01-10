package org.linphone.onu_legacy.MVP.Implementation.model.AdminDataClasses;

public class UserInfo {
    private String base_url;
    private String username;
    private String password;
    private String parent_id;
    private String id;
    private String deviceID;

    public UserInfo(String base_url, String id, String username, String password, String parent_id,String deviceID) {
        this.base_url = base_url;
        this.username = username;
        this.password = password;
        this.parent_id = parent_id;
        this.id=id;
        this.deviceID=deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getBase_url() {
        return base_url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getParent_id() {
        return parent_id;
    }

    public String getId() {
        return id;
    }
}
