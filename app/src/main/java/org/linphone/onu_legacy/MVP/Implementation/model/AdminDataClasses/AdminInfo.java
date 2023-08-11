package org.linphone.onu_legacy.MVP.Implementation.model.AdminDataClasses;

public class AdminInfo {
    private int id;
    private String dataKey;
    private String firstValue;
    private String secondValue;

    public AdminInfo() {
    }

    public AdminInfo(int id, String dataKey, String firstValue, String secondValue) {
        this.id = id;
        this.dataKey = dataKey;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getFirstValue() {
        return firstValue;
    }

    public void setFirstValue(String firstValue) {
        this.firstValue = firstValue;
    }

    public String getSecondValue() {
        return secondValue;
    }

    public void setSecondValue(String secondValue) {
        this.secondValue = secondValue;
    }
}
