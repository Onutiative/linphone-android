package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

public class Address {

    private String holding_no;
    private String road_area;
    private String city;
    private String state;
    private String zip_code;
    private String post_code;
    private String address_type_id;
    private String country_id;
    private String latitude;
    private String longitude;

    public Address(String holding_no, String road_area, String city, String state, String zip_code, String post_code, String address_type_id, String country_id, String latitude, String longitude) {
        this.holding_no = holding_no;
        this.road_area = road_area;
        this.city = city;
        this.state = state;
        this.zip_code = zip_code;
        this.post_code = post_code;
        this.address_type_id = address_type_id;
        this.country_id = country_id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getHolding_no() {
        return holding_no;
    }

    public String getRoad_area() {
        return road_area;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getPost_code() {
        return post_code;
    }

    public String getAddress_type_id() {
        return address_type_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
