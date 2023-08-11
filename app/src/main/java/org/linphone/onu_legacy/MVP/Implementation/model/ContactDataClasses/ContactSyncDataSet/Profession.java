package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

public class Profession {

    private String designation;
    private String department;
    private String company;

    public Profession(String designation, String department, String company) {
        this.designation = designation;
        this.department = department;
        this.company = company;
    }

    public String getDesignation() {
        return designation;
    }

    public String getDepartment() {
        return department;
    }

    public String getCompany() {
        return company;
    }
}
