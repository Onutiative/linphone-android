package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.ContactObject;
import com.onutiative.onukit.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.Contacts;

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

public class ContactSyncRepository {

    private Context context;
    private RetrofitUrlMapper urlMapper;
    private String baseURI;
    private String username;
    private String password;
    private String userID;
    private String TAG="ContactSyncRepository";

    public ContactSyncRepository(Context context,String baseURI,String username, String password,String userID) {
        this.context = context;
        this.baseURI=baseURI;
        this.username=username;
        this.password=password;
        this.userID=userID;
    }

    public void postContacts(List<Contacts> contacts) {

        // preparing interceptor for retrofit
        // interceptor for runtime data checking
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
                        Headers headers = request.headers().newBuilder().add("userId", userID).build();
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

        Call<ContactObject> contactObjCall = urlMapper.createPost(new ContactObject("", "phone", contacts));

        contactObjCall.enqueue(new Callback<ContactObject>() {
            @Override
            public void onResponse(Call<ContactObject> call, Response<ContactObject> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Error " + response.code(), Toast.LENGTH_LONG).show();
                    Log.i(TAG,"Error " + response.code());
                    return;
                }
                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"Success");
            }
            @Override
            public void onFailure(Call<ContactObject> call, Throwable t) {
                Toast.makeText(context, "onfailure called", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"onFailure called");
            }
        });
    }
}
