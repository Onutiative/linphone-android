package org.linphone.onu_legacy.Adapters;
//<!--used in 6v3-->

import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.linphone.onu_legacy.Database.Fruits;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by mamba on 3/14/2016.
 */
public class NotificationRecycleAdapter extends RecyclerView.Adapter <NotificationRecycleAdapter.Holder>{
    ArrayList<Fruits> arrayList=new ArrayList<>();
    Cursor cursor;
    private int mPreviousPosition = 0;


    public NotificationRecycleAdapter(ArrayList<Fruits> arrayList)
    {
        this.arrayList=arrayList;
    }

    private void delete(int position){

        arrayList.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_notify,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Fruits fruit=arrayList.get(position);
       // holder.index.setText(Integer.toString(position));

        holder.surah.setText(fruit.getAge());
        holder.status.setText(fruit.getStat());

        //holder.dlt.setImageResource(R.drawable.deleteicon);

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



    public class Holder extends RecyclerView.ViewHolder  {
        TextView index,surah,lines,status;

        public Holder(View itemView)
        {
            super(itemView);
            surah= (TextView) itemView.findViewById(R.id.t2);
            status= (TextView) itemView.findViewById(R.id.status);

        }

    }
}
