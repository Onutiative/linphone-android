package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.content.Context;
import android.util.Log;

import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet.ContactSummaryData;

import java.io.IOException;
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

public class ContactSummaryPullRepository {
    private Context context;
    private String baseUrl;
    private String username;
    private String password;
    private String userID;
    private String parentID;
    private RetrofitUrlMapper urlMapper;
    private ContactSummaryListener listener;
    private String TAG = "ContactSummaryPullRepository";

    public ContactSummaryPullRepository(Context context) {
        this.context = context;
        listener= (ContactSummaryListener) context;
    }
    public void pullContactSummary() {
        setUserInfo();
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
                }).addInterceptor(new Interceptor() {
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
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        urlMapper = retrofit.create(RetrofitUrlMapper.class);
        final Call<ContactSummaryData> infraQueListCall = urlMapper.getContactSummery();

        infraQueListCall.enqueue(new Callback<ContactSummaryData>(){
            @Override
            public void onResponse(Call<ContactSummaryData> call, Response<ContactSummaryData> response){
                ContactSummaryData summaryData = response.body();
                try
                {
                    listener.getContactSummeryDashboardUpdate(summaryData);
                    Log.i(TAG,"Total Contact: "+summaryData.getPersonalContact());
                }
                catch (Exception e)
                {
                    Log.i(TAG,"Exception: "+e.toString());
                }
            }
            @Override
            public void onFailure(Call<ContactSummaryData> call, Throwable t) {
                Log.i(TAG,"Contact summery Response Failure");
            }
        });
    }

    public interface ContactSummaryListener{
        public void getContactSummeryDashboardUpdate(ContactSummaryData summaryData);
    }

    private void setUserInfo() {
        Database db = new Database(context);
        List<Contact> contacts = db.getAdminNumber();
        for (Contact cn : contacts) {
            if (cn.getName().equals("contact_url")) {
                if (!cn.getPhone_number().equals(""))
                    baseUrl = cn.getPhone_number();
            } else if (cn.getName().equals("email"))
            {
                username = cn.getPhone_number();

            } else if (cn.getName().equals("password"))
            {
                password = cn.getPhone_number();
            }else if (cn.getName().equals("user_id"))
            {
                 userID = cn.getPhone_number();
            }
            else if (cn.getName().equals("parent_id"))
            {
                parentID = cn.getPhone_number();
            }
        }

        Log.i(TAG,"Base URL: "+baseUrl);
        Log.i(TAG,"User Name : "+username);
        Log.i(TAG,"Password: "+password);
        Log.i(TAG,"User ID: "+userID);
        Log.i(TAG,"Parent ID: "+parentID);
    }
}
