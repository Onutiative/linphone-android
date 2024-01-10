package org.linphone.onu_legacy.MVP.Implementation.ContactPackage;

import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;

import java.util.List;

public interface ContactCommunicator {
    public interface ContactView{
            void inflateAdapterView(List<ContactDetails> contactDetailsList,boolean selection);
    }
    public interface PresenterContact{
            void handelAdapterView(List<ContactDetails> contactDetailsList,boolean selection);
            void handelCallMaker(String number);
            //void smsSenderOperation(String number);
    }
    public interface AdapterHandelar{
        void toBulkSmsActivity();
        void toBulkSsmActivityWithAllContact();
    }
}
