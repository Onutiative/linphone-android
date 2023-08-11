package org.linphone.onu_legacy.MVP.Implementation.SmsSendPackage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import com.onutiative.onukit.R;

import java.util.List;

public class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.Holder>{

    private  List<ContactDetails> contactDetailsList;
    private Context context;

    public SelectedContactAdapter(List<ContactDetails> contactDetailsList, Context context) {
        this.contactDetailsList = contactDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_contact_row,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.contactName.setText(contactDetailsList.get(position).getContactName());
        holder.contactValue.setText(contactDetailsList.get(position).getContactValue());
    }

    @Override
    public int getItemCount() {
        return contactDetailsList.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView contactName;
        TextView contactValue;
        public Holder(View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.selectContactName);
            contactValue=itemView.findViewById(R.id.selectContactValue);
        }
    }
}
