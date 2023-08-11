package org.linphone.onu_legacy.MVP.Implementation.SmsSendPackage;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import com.onutiative.onukit.MVP.Implementation.model.SMSDataClasses.ScheduleSMSDetails;
import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.PushScheduledSMS;
import com.onutiative.onukit.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsActivityPresenter extends ScheduleSMSDetails implements SmsActivityCommunicator.SmsActivityViewPresenter{
    private Context context;
    private String TAG="SmsActivityPresenter";
    private String date_time = "";
    private SmsActivityCommunicator.SmsActivityView view;

    public SmsActivityPresenter(Context context) {
        this.context = context;
        this.view= (SmsActivityCommunicator.SmsActivityView) context;
    }

    public String makeSMSID(){
        Date date= new Date();
        long time = date.getTime();
        return String.valueOf(time);
    }
    public String getTime(){
        String dateTime;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dateTime = dateFormat.format(date);
        return dateTime;
    }

//    @Override
//    public void handelBulkSMS(List<ContactDetails> contactDetailsList, String text) {
//        if (text.isEmpty()||contactDetailsList==null){
//            Toast.makeText(context,"Please input SMS body or select contact",Toast.LENGTH_SHORT).show();
//            return;
//        }else {
//            List<SendSMSDetails> smsDetails=new ArrayList<>();
//            for (ContactDetails contact:contactDetailsList) {
//                SendSMSDetails details = new SendSMSDetails(getTime(),text,contact.getContactValue(),makeSMSID());
//                Log.i(TAG,"Name: "+contact.getContactName()+"; Value: "+contact.getContactValue());
//                smsDetails.add(details);
//            }
//            new PushSMStoServerForSending(context,smsDetails).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        }
//    }

    @Override
    public void handleSelectContactList(List<ContactDetails> contactDetailsList) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Selected contacts");
        LayoutInflater inflater = LayoutInflater.from(context);
        final LinearLayout customRoot= (LinearLayout) inflater.inflate(R.layout.select_contact_view,null);
        RecyclerView recyclerView= customRoot.findViewById(R.id.selectedContact);
        Log.i(TAG,"Selected contact: "+contactDetailsList.size());
        SelectedContactAdapter adapter = new SelectedContactAdapter(contactDetailsList,context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        builder.setView(customRoot);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
//        builder.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void handelScheduleSMS(List<ContactDetails> contactDetailsList, String text, String schTitle, String schTime,int type,boolean schFlag) {
        ScheduleSMSDetails scheduleSMSDetails=new ScheduleSMSDetails();

        if (contactDetailsList!=null){
            List<ScheduleSMSDetails.Message> messages=new ArrayList<>();
            for (ContactDetails contact:contactDetailsList) {
                ScheduleSMSDetails.Message message = new ScheduleSMSDetails.Message();
                message.setMobile(contact.getContactValue());
                if (schFlag){
                    message.setSentTime(schTime);
                }else {
                    message.setSentTime(getTime());
                }
                message.setSmsId(makeSMSID());
                message.setSmsText(text);
                //Log.i(TAG,"Name: "+contact.getContactName()+"; Value: "+contact.getContactValue());
                messages.add(message);
            }
            scheduleSMSDetails.setMessages(messages);
        }else {
            Toast.makeText(context,"Please select contact!",Toast.LENGTH_SHORT).show();
            return;
        }

        if (text.isEmpty()){
            Toast.makeText(context,"Please write your message!",Toast.LENGTH_SHORT).show();
            return;
        }
        ///////////////////////if schedule//////////
        if (schFlag){
            if (schTime.isEmpty()){
                Toast.makeText(context,"Please set send time!",Toast.LENGTH_SHORT).show();
                return;
            }else {
                Log.i(TAG,"Flag false");
                scheduleSMSDetails.setScheduleTime(schTime);
            }
        }
        if (type>0){
            scheduleSMSDetails.setScheduleType(String.valueOf(type-1));
        }else {
            if (contactDetailsList.size()>1){
                scheduleSMSDetails.setScheduleType(String.valueOf(1));
            }else {
                scheduleSMSDetails.setScheduleType(String.valueOf(0));
            }
        }
        scheduleSMSDetails.setTxid(makeSMSID());
        scheduleSMSDetails.setScheduleTitle(schTitle);
        new PushScheduledSMS(context,scheduleSMSDetails).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
