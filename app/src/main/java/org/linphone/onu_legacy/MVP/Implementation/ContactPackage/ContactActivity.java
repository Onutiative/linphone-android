package org.linphone.onu_legacy.MVP.Implementation.ContactPackage;

import android.app.SearchManager;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.ContactPullRepository;
import com.onutiative.onukit.R;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements ContactCommunicator.ContactView, ContactPullRepository.ContactListener {

    private ContactPresenter presenter;
    private List<ContactDetails> contactList;
    private boolean selectionOption;
    private String TAG= "ContactActivity";
    private ContactAdapter adapter;
    private RecyclerView contactRecyclerView;
    private TextView doneTextBTN;
    private String uname = null, upass = null, url = null, urlForEdition = null,userId=null,parentID="0";
    private ContactPullRepository pullRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"OnCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactRecyclerView = findViewById(R.id.contactRecyclerList);
        presenter=new ContactPresenter(this);
        doneTextBTN=findViewById(R.id.doneTextBTN);

        Toolbar toolbar = findViewById(R.id.contactToolBar);
        setSupportActionBar(toolbar);
        getAdminInfo();

        try{
            //contactList= (List<ContactDetails>) getIntent().getSerializableExtra("contacts");
            selectionOption= (boolean) getIntent().getBooleanExtra("selection",false);
            if (selectionOption){
                doneTextBTN.setVisibility(View.VISIBLE);
            }else {
                doneTextBTN.setVisibility(View.GONE);
            }
            if (selectionOption){
                Log.i(TAG,"Selected");
            }else {
                Log.i(TAG,"Not Selected");
            }
            //new PullServerContact(this,selectionOption).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            pullRepository= new ContactPullRepository(this,"https://api.onukit.com/contact/0v1/",uname,upass,userId,parentID);
            pullRepository.pullContacts(selectionOption);
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }

        doneTextBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.toBulkSimsOperations();
                adapter.toBulkSmsActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchItem:
                Log.i(TAG,"Clicked on search view");
                break;
            case R.id.selectAllContactItem:
                Log.i(TAG,"Clicked on all");
                adapter.toBulkSsmActivityWithAllContact();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchItem).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Contact");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                Log.i(TAG,"Query: "+query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                Log.i(TAG,"Query: "+newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public void inflateAdapterView(List<ContactDetails> contactDetailsList,boolean selection) {

    }

    @Override
    public void toContactAdapter(List<ContactDetails> contactList, boolean selectionOption) {
        if (contactList.size()>0){
            Log.i(TAG,"Inflate called");
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            contactRecyclerView.setLayoutManager(layoutManager);
            adapter=new ContactAdapter(this,contactList,selectionOption);
            contactRecyclerView.setAdapter(adapter);
        }else {
            showSnackBar("You don't have any contacts! Pleas sync your contact first.");
        }
    }

    public void getAdminInfo() {
        Database db = new Database(ContactActivity.this);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("email"))
            {
                uname = cn.getPhone_number();

            } else if (cn.getName().equals("password"))
            {
                upass = cn.getPhone_number();
            }else if (cn.getName().equals("user_id"))
            {
                userId = cn.getPhone_number();
            }else if (cn.getName().equals("parent_id")){
                parentID=cn.getPhone_number();
            }
        }
    }
    void showSnackBar(String msg){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, msg, Snackbar.LENGTH_INDEFINITE)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }
}
