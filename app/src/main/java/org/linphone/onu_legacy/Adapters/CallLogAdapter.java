package org.linphone.onu_legacy.Adapters;
//<!--used in 6v3-->

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.linphone.onu_legacy.Database.Fruit;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by mamba on 3/14/2016.
 */
public class CallLogAdapter extends RecyclerView.Adapter <CallLogAdapter.Holder>{
    ArrayList<Fruit> arrayList=new ArrayList<>();
    private int mPreviousPosition = 0;

    private String STATUS_CODE;

    public void clearData() {
        int size = this.arrayList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayList.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public CallLogAdapter(ArrayList<Fruit> arrayList)
    {
        this.arrayList=arrayList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row,parent,false);

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_call,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }



    private void delete(int position){
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Fruit fruit=arrayList.get(position);
        holder.index.setText(fruit.getName());

        STATUS_CODE=fruit.getAge();
//        holder.surah.setText(fruit.getAge());

        holder.lines.setText(fruit.getGame());

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

     public   void  notifyData(ArrayList<Fruit> myList){

        this.arrayList=myList;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView index,surah,lines;
        public Holder(View itemView)
        {
            super(itemView);
            index= (TextView) itemView.findViewById(R.id.t1);
//            surah= (TextView) itemView.findViewById(R.id.t2);
            lines= (TextView) itemView.findViewById(R.id.t3);
//            surah.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            AlertDialog dialog = new AlertDialog.Builder(v.getContext()).create();
            dialog.setTitle(index.getText().toString());
            dialog.setIcon(R.drawable.onukit_logo2);
//            dialog.setMessage(surah.getText().toString());
            final Context context=v.getContext();

            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "DELETE",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Database db = new Database(context);
//                            db.deleteposted(surah.getText().toString(),lines.getText().toString());
                            db.deleteposted(STATUS_CODE,lines.getText().toString());
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
