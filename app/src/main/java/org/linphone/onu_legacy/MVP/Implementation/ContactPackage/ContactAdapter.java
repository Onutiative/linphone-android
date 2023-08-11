package org.linphone.onu_legacy.MVP.Implementation.ContactPackage;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.TaskConversion;
import com.onutiative.onukit.MVP.Implementation.SmsSendPackage.SmsActivity;
import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import com.onutiative.onukit.R;
import com.onutiative.onukit.Utility.Helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> implements Filterable,ContactCommunicator.AdapterHandelar {

    private Context context;
    private List<ContactDetails>contactDetailsList;
    private List<ContactDetails> filteredList;
    private String TAG = "ContactAdapter";
    private ContactPresenter presenter;
    private List<ContactDetails> selectedContactList=new ArrayList<>();
    private SparseBooleanArray itemStateArray= new SparseBooleanArray();
    private boolean selectionOption;
    private TaskConversion taskConversion;
    private Helper helper;

    public ContactAdapter(Context context, List<ContactDetails> contactDetailsList,boolean selectionOption) {
        this.context = context;
        this.contactDetailsList = contactDetailsList;
        this.filteredList = contactDetailsList;
        this.selectionOption=selectionOption;
        Log.i(TAG,"Contact size: "+contactDetailsList.size());
        presenter=new ContactPresenter(context);
        taskConversion=new TaskConversion(context);
        helper=new Helper(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact_row,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.contactName.setText(filteredList.get(position).getContactName());
        holder.contactValue.setText(filteredList.get(position).getContactValue());
        if (selectionOption){
            if (filteredList.get(position).isSelect()) {
                holder.contactCheckBox.setChecked(true);
            }
            else  {
                holder.contactCheckBox.setChecked(false);
            }
            holder.contactCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!filteredList.get(position).isSelect()) {
                        holder.contactCheckBox.setChecked(true);
                        filteredList.get(position).setSelect(true);
                        selectedContactList.add(filteredList.get(position));
                        Log.i(TAG,"Selected: "+filteredList.get(position).getContactName()+" Position:"+filteredList.get(position).getContactId());
                        notifyDataSetChanged();
                    }
                    else  {
                        holder.contactCheckBox.setChecked(false);
                        filteredList.get(position).setSelect(false);
                        Log.i(TAG,"Unselected: "+filteredList.get(position).getContactName());
                        //////////////////////////////////
                        for (int count =0;count<selectedContactList.size();count++) {
                            if (filteredList.get(position).getContactId().equals(selectedContactList.get(count).getContactId())){
                                selectedContactList.remove(count);
                            }
                        }
                        ///////////////////////////////////
                        notifyDataSetChanged();
                    }
                }
            });
        }else {
            holder.contactCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();
                Log.i(TAG,"Query: "+query);
                if(query.isEmpty()){
                    filteredList = contactDetailsList;
                }else{
                    List<ContactDetails>tempList = new ArrayList<>();
                    for(ContactDetails contact : contactDetailsList){
                        if(contact.getContactName().toLowerCase().contains(query.toLowerCase()) ||
                                contact.getContactValue().toLowerCase().contains(query.toLowerCase())){
                            Log.i(TAG,"match found");
                            tempList.add(contact);
                        }
                    }
                    filteredList = tempList;
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (List<ContactDetails>) filterResults.values;
                Log.i(TAG,"tempList size: "+filteredList.size());
                Log.i(TAG,"Notify Called");
                if (filteredList.size()<=0){
                    filteredList=contactDetailsList;
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void toBulkSmsActivity() {
        Intent intent = new Intent(context, SmsActivity.class);
        intent.putExtra("contacts", (Serializable) selectedContactList);
        context.startActivity(intent);
        Log.i(TAG,"Clicked done");
    }

    @Override
    public void toBulkSsmActivityWithAllContact() {
        Intent intent = new Intent(context, SmsActivity.class);
        intent.putExtra("contacts", (Serializable) contactDetailsList);
        context.startActivity(intent);
        Log.i(TAG,"Clicked done");
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView contactName;
        TextView contactValue;
        CardView contactContainer;
        CheckBox contactCheckBox;

        public Holder(final View itemView) {
            super(itemView);
            contactName=itemView.findViewById(R.id.contactName);
            contactValue=itemView.findViewById(R.id.contactValue);
            contactContainer=itemView.findViewById(R.id.contactContainer);
            contactCheckBox=itemView.findViewById(R.id.contactCheckBox);
            //////////////////////////////////////////////////////////////////
            contactContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Clicked on contact: "+itemView.getId()+" Contact: "+contactValue.getText().toString());
                    PopupMenu popupMenu = new PopupMenu(context,contactContainer);
                    popupMenu.inflate(R.menu.contact_operation_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.call_fromContact:
                                    presenter.handelCallMaker(contactValue.getText().toString());
                                    break;
                                case R.id.sms_fromContact:
                                    //presenter.smsSenderOperation(contactValue.getText().toString());
                                    Log.i(TAG,filteredList.get(getPosition()).getContactName());
                                    if (!selectionOption){
                                        selectedContactList.add(filteredList.get(getPosition()));
                                    }
                                    Intent intent = new Intent(context, SmsActivity.class);
                                    intent.putExtra("contacts", (Serializable) selectedContactList);
                                    context.startActivity(intent);
                                    Log.i(TAG,"Clicked done");
                                    break;
                                case R.id.task_fromContact:
                                    taskConversion=new TaskConversion(context);
                                    taskConversion.taskMaking(contactValue.getText().toString(),"",helper.getTime(),"","contact");
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }
    }
}
