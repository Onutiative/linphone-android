package org.linphone.onu_legacy.MVP.Implementation.ContactPackage;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import org.linphone.onu_legacy.Activities.Activities.CallActivity;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ContactPresenter implements ContactCommunicator.PresenterContact {

    private ContactCommunicator.ContactView view;
    private Context context;
    private String TAG = "ContactPresenter";
    private String setMessage="";

   public ContactPresenter(Context context){
       this.context=context;
    }

    @Override
    public void handelAdapterView(List<ContactDetails> contactDetailsList,boolean selection) {
        this.view= (ContactCommunicator.ContactView) context;
        Collections.sort(contactDetailsList, new Comparator<ContactDetails>(){
            public int compare(ContactDetails obj1, ContactDetails obj2) {
                // ## Ascending order
                return obj1.contactName.compareToIgnoreCase(obj2.contactName); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(Integer.valueOf(obj2.empId)); // To compare integer values
                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
            }
        });

        view.inflateAdapterView(contactDetailsList,selection);
    }

    @Override
    public void handelCallMaker(String number) {
        Intent in=new Intent(context, CallActivity.class);
        in.putExtra("number",number);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(in);
//        Uri uri = Uri.parse(number);
//        context.startActivity(new Intent(Intent.ACTION_DIAL, uri));
    }

//    @Override
//    public void smsSenderOperation(String number) {
//        getTextFromAlart(number);
//    }

    public long makeSMSID(){
        Date date= new Date();
        long time = date.getTime();
        return time;
    }

    public String getTime(){
       String dateTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dateTime = dateFormat.format(date);
       return dateTime;
    }

//    private void getTextFromAlart(final String number){
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Write your message here.");
//        // Set up the input
////        final EditText input = new EditText(context);
//////        input.setInputType(InputType.TYPE_CLASS_TEXT);
//////        builder1.setView(input);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        final LinearLayout customRoot= (LinearLayout) inflater.inflate(R.layout.sms_send_window,null);
//        final EditText editText = customRoot.findViewById(R.id.sms_textBox);
//        builder.setView(customRoot);
//
//        builder.setCancelable(true);
//        builder.setPositiveButton(
//                "Sent",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        setMessage =editText.getText().toString();
//                        if (setMessage.isEmpty()){
//                            Log.i(TAG,"No text set");
//                        }else {
//                            Log.i(TAG,"Send sms to "+number+"; SMS id: "+makeSMSID()+"; Sending time: "+getTime());
//                            List<SendSMSDetails> sendSMSDetailsList = new ArrayList<>();
//                            SendSMSDetails smsDetails = new SendSMSDetails(getTime(),setMessage,number,String.valueOf(makeSMSID()));
//                            sendSMSDetailsList.add(smsDetails);
//                            new PushSMStoServerForSending(context,sendSMSDetailsList).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        }
//                        dialog.cancel();
//                    }
//                });
//        builder.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

}
