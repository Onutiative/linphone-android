package org.linphone.onu_legacy.MVP.Implementation.PostedSMSPAckage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.TaskConversion;
import org.linphone.onu_legacy.MVP.Implementation.model.SMSDataClasses.InboxSMSDetails;
import org.linphone.R;

import java.util.ArrayList;

public class PostedSMSAdapter extends RecyclerView.Adapter<PostedSMSAdapter.Holder>{

    Context context;
    ArrayList<InboxSMSDetails> inboxSMSDetails;
    String TAG = "PostedSMSAdapter";
    private TaskConversion taskConversion;

    public PostedSMSAdapter(Context context, ArrayList<InboxSMSDetails> inboxSMSDetails) {
        this.context = context;
        this.inboxSMSDetails = inboxSMSDetails;
        Log.i(TAG,"Constructor Set: "+inboxSMSDetails.get(0).getContact());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.i(TAG,"onCreat called");
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_row_sms,viewGroup,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Log.i(TAG,"Set: "+inboxSMSDetails.get(i).getContact());
        holder.smsBody.setText(inboxSMSDetails.get(i).getSmsBody());
        Log.i(TAG,"From view: "+holder.smsBody.getText().toString());
    }

    @Override
    public int getItemCount() {
        Log.i(TAG,"getCount called: "+inboxSMSDetails.size());
        return inboxSMSDetails.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        public TextView contact,date,smsBody;
        public Holder(View itemView) {
            super(itemView);
            Log.i(TAG,"Holder called");
            contact=itemView.findViewById(R.id.contactSMSpost);
            date=itemView.findViewById(R.id.dateSMSpost);
            smsBody=itemView.findViewById(R.id.bodySMSpost);
        }
    }

    private void delete(int position){
        inboxSMSDetails.remove(position);
        notifyItemRemoved(position);
    }
    public   void  notifyData(ArrayList<InboxSMSDetails> inboxSMSDetails){

        this.inboxSMSDetails=inboxSMSDetails;
        notifyDataSetChanged();
    }
}
