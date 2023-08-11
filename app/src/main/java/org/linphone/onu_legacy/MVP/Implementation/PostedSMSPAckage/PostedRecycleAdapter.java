package org.linphone.onu_legacy.MVP.Implementation.PostedSMSPAckage;
//<!--used in 6v3-->

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.TaskConversion;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.MVP.Implementation.model.SMSDataClasses.InboxSMSDetails;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by mamba on 3/14/2016.
 */
public class PostedRecycleAdapter extends RecyclerView.Adapter<PostedRecycleAdapter.Holder>{
    ArrayList<InboxSMSDetails> arrayList=new ArrayList<>();
    private int mPreviousPosition = 0;
    private TaskConversion taskConversion;
    private String TAG="PostedRecycleAdapter";
    private Context context;

    public void clearData() {
        int size = this.arrayList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }
    public PostedRecycleAdapter(Context context,ArrayList<InboxSMSDetails> arrayList)
    {
        this.arrayList=arrayList;
        this.context=context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_sms,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }
    private void delete(int position){

        arrayList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.contact.setText(arrayList.get(position).getContact());
        holder.date.setText(arrayList.get(position).getDate());
        holder.smsBody.setText(arrayList.get(position).getSmsBody());

        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animate1(holder, true);
//            AnimationUtils.animate(holder,true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animate1(holder, false);
//            AnimationUtils.animate(holder, false);
        }
        mPreviousPosition = position;

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public   void  notifyData(ArrayList<InboxSMSDetails> myList){

        this.arrayList=myList;
        notifyDataSetChanged();
    }




    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView contact,date,smsBody;
        public Holder(View itemView)
        {
            super(itemView);
            Log.i(TAG,"Holder called");
            contact=itemView.findViewById(R.id.contactSMSpost);
            date=itemView.findViewById(R.id.dateSMSpost);
            smsBody=itemView.findViewById(R.id.bodySMSpost);
//            surah.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
            dialog.setTitle(contact.getText().toString());
            dialog.setIcon(R.drawable.icon);
            dialog.setMessage(date.getText().toString());
            final Context context=v.getContext();

            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "SET AS TASK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    taskConversion=new TaskConversion(context);
                    Log.i(TAG,"clicked");
                    taskConversion.taskMaking(contact.getText().toString(),smsBody.getText().toString(),date.getText().toString(),"","inbox");
                    dialog.dismiss();
                    return;
                }
            });

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "DELETE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Database db = new Database(context);
                            db.deleteposted(date.getText().toString(),smsBody.getText().toString());
                            dialog.dismiss();

                            delete(getLayoutPosition());
                            return;
                        }
                    });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            return;
                        }
                    });
            dialog.show();

        }
    }
}