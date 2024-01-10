package org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet;

public class Relation {

    private String relation_id;
    private String relation_remark;

    public Relation(String relation_id, String relation_remark) {
        this.relation_id = relation_id;
        this.relation_remark = relation_remark;
    }

    public String getRelation_id() {
        return relation_id;
    }

    public String getRelation_remark() {
        return relation_remark;
    }
}
