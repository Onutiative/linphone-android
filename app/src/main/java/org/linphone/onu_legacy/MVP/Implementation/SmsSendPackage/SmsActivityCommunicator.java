package org.linphone.onu_legacy.MVP.Implementation.SmsSendPackage;

import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;

import java.util.List;

public interface SmsActivityCommunicator {
    public interface SmsActivityView{
        public void updateSelectedData(List<ContactDetails> contactDetailsList);
    }
    public interface SmsActivityViewPresenter{
        //public void handelBulkSMS(List<ContactDetails> contactDetailsList,String text);
        public void handleSelectContactList(List<ContactDetails> contactDetailsList);
        public void handelScheduleSMS(List<ContactDetails>contactDetailsList,String text,String schTitle, String schTime,int type,boolean schFlag);
    }
}
