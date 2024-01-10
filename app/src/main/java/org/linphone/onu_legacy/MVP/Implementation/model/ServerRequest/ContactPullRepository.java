package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.ColorSpace;
import android.util.Log;
import android.widget.Toast;

import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactPullDataSet.ContactList;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactPullDataSet.ContactPulledData;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.ContactObject;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Contacts;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.RetrofitUrlMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactPullRepository {

    private Context context;
    private RetrofitUrlMapper urlMapper;
    private String baseURI;
    private String username;
    private String password;
    private String userID;
    private String parentID;
    private String TAG="ContactPullRepository";
    private List<ContactDetails> contacts=new ArrayList<>();
    private ContactListener listener;
    private ProgressDialog progressBar;

    public ContactPullRepository(Context context, String baseURI, String username, String password, String userID, String parentID) {
        this.context = context;
        this.baseURI=baseURI;
        this.username=username;
        this.password=password;
        this.userID=userID;
        this.parentID=parentID;
    }

    public void pullContacts(final boolean selectionOption) {
        progressBar = ProgressDialog.show(context,"Pull contact","Pulling ......");
        progressBar.setCancelable(true);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final String authToken = Credentials.basic(username, password);
        // authentication and userId interceptor
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Headers headers = request.headers().newBuilder().add("Authorization", authToken).build();
                        request = request.newBuilder().headers(headers).build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Headers headers = request.headers().newBuilder().add("Userid", userID).build();
                        request = request.newBuilder().headers(headers).build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Headers headers = request.headers().newBuilder().add("Parentid", parentID).build();
                        request = request.newBuilder().headers(headers).build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(loggingInterceptor)
                .build();

        //Preparing Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURI)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        urlMapper = retrofit.create(RetrofitUrlMapper.class);
        final Call<ContactPulledData> infraQueListCall = urlMapper.getContactData();

        infraQueListCall.enqueue(new Callback<ContactPulledData>(){
            @Override
            public void onResponse(Call<ContactPulledData> call, Response<ContactPulledData>response){
                ContactPulledData contactPulledData = response.body();
                try
                {
                    for (ContactList contactList : contactPulledData.getContactList()){
                        String contactId = contactList.getProfile().getId();
                        String firstName= contactList.getProfile().getFirstName();
                        String lastName= contactList.getProfile().getLastName();
                        String contactName;
                        if (contactList.getProfile().getLastName()==null || contactList.getProfile().getLastName().equals("null")){
                            contactName = firstName;
                        }else {
                            contactName = firstName+lastName;
                        }
                        String contactValue = contactList.getProfile().getContactValue();
                        String imagePath= contactList.getProfile().getImagePath();
                        String gender= contactList.getProfile().getGender();
                        String nickName = contactList.getProfile().getNickName();
                        String createdAt=contactList.getProfile().getCreatedAt();
                        String createdBy=contactList.getProfile().getCreatedBy();
                        String updatedAt=contactList.getProfile().getCreatedAt();
                        String updatedBy = contactList.getProfile().getUpdatedBy();
                        String contactTypeId=contactList.getProfile().getContactTypeId();
                        String sourceContactRefID=contactList.getProfile().getSourceContactRefID();
                        String source=contactList.getProfile().getSource();
                        String ownerId=contactList.getProfile().getOwnerId();
                        String isPrivate=contactList.getProfile().getIsPrivate();
                        String phoneContactId = contactList.getProfile().getPhoneContactId();

                        Log.i(TAG, contactId + "; " + contactName + "; " + contactValue);
                        ContactDetails contact;
                        if (selectionOption){
                            contact=new ContactDetails(contactId,contactName,firstName,lastName,contactValue,imagePath,
                                    gender,nickName,createdAt,createdBy,updatedAt,updatedBy,contactTypeId,sourceContactRefID,
                                    source,ownerId,isPrivate,phoneContactId,false);
                        }else {
                            contact=new ContactDetails(contactId,contactName,firstName,lastName,contactValue,imagePath,
                                    gender,nickName,createdAt,createdBy,updatedAt,updatedBy,contactTypeId,sourceContactRefID,
                                    source,ownerId,isPrivate,phoneContactId);
                        }
                        contacts.add(contact);
                    }
                    Log.i(TAG,"Total contact: "+contacts.size());
                    listener = (ContactListener) context;
                    listener.toContactAdapter(contacts,selectionOption);
                    progressBar.cancel();
                }
                catch (Exception e)
                {
                    Log.i(TAG,"Exception: "+e.toString());
                    progressBar.cancel();
                }
            }
            @Override
            public void onFailure(Call<ContactPulledData> call, Throwable t) {
                Log.i(TAG,"Contact List Response Failure");
                progressBar.cancel();
            }
        });
    }
    public interface ContactListener{
        public void toContactAdapter(List<ContactDetails> contactList, boolean selectionOption);
    }
}
