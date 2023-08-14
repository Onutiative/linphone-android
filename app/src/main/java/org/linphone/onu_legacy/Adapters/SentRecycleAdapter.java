package org.linphone.onu_legacy.Adapters;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.linphone.onu_legacy.Database.ServerSms;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by mamba on 3/14/2016.
 */
public class SentRecycleAdapter extends RecyclerView.Adapter <SentRecycleAdapter.Holder>{
//    ArrayList<Fruit> arrayList=new ArrayList<>();
    ArrayList<ServerSms> serverSmsList;
    private int mPreviousPosition = 0;
    String TAG = "SentRecycleAdapter";

    public SentRecycleAdapter(ArrayList<ServerSms> serverSmsList)
    {
        this.serverSmsList=serverSmsList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_sms,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }
    private void delete(int position){

//        arrayList.remove(position);
        serverSmsList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public void onBindViewHolder(Holder holder, int position) {
//        Fruit fruit=arrayList.get(position);
//        holder.index.setText(fruit.getName());
//        holder.surah.setText(fruit.getAge());
//        holder.lines.setText(fruit.getGame());


        ServerSms serverSms=serverSmsList.get(position);
        holder.contact.setText(serverSms.getSmsTo());
        holder.body.setText(serverSms.getSmsBody());
        holder.date.setText(serverSms.getSubmissionTime());
        Log.i(TAG,"Out number: "+serverSms.getSmsTo());
        //index=contact;sura=body;line=date

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
//        return arrayList.size();
        Log.i(TAG,"getCount called: "+serverSmsList.size());
        return serverSmsList.size();
    }

    public void clearData() {
//        int size = this.arrayList.size();
        int size=this.serverSmsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
//                this.arrayList.remove(0);
                this.serverSmsList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView contact,date,body;
        public Holder(View itemView)
        {
            super(itemView);
            contact= (TextView) itemView.findViewById(R.id.contactSMSpost);
            date= (TextView) itemView.findViewById(R.id.dateSMSpost);
            body= (TextView) itemView.findViewById(R.id.bodySMSpost);
//            surah.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
            dialog.setTitle(contact.getText().toString());
            dialog.setIcon(R.drawable.onukit_logo2);
            dialog.setMessage(body.getText().toString());
            final Context context=v.getContext();

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "DELETE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Database db = new Database(context);
                            db.deletesent(body.getText().toString(),date.getText().toString());
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
