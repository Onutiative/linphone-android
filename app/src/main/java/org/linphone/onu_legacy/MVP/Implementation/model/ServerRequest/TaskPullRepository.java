package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import org.linphone.onu_legacy.MVP.Implementation.TaskPackage.PresenterTaskShow;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactDetails;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactPullDataSet.ContactList;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactPullDataSet.ContactPulledData;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.RetrofitUrlMapper;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskRequestBody;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskResponseData;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskList;

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

public class TaskPullRepository {
    private Context context;
    private RetrofitUrlMapper urlMapper;
    private String baseURI;
    private String username;
    private String password;
    private String TAG="TaskPullRepository";
    private TaskListener listener;
    private PullTaskRequestBody taskRequestBody;
    private ProgressDialog progressBar;
    private PresenterTaskShow instant;

    public TaskPullRepository(PresenterTaskShow instant,Context context, String baseURI, String username, String password, PullTaskRequestBody requestBody) {
        this.context = context;
        this.instant=instant;
        listener= (TaskListener) instant;
        this.baseURI=baseURI;
        this.username=username;
        this.password=password;
        this.taskRequestBody=requestBody;
    }
    public TaskPullRepository(Context context, String baseURI, String username, String password, PullTaskRequestBody requestBody) {
        this.context = context;
        listener= (TaskListener) context;
        this.baseURI=baseURI;
        this.username=username;
        this.password=password;
        this.taskRequestBody=requestBody;
    }


    public void pullTask() {
        Log.i(TAG,"Base URL: "+baseURI);
        //Log.i(TAG,"Request body: "+taskRequestBody.toString());

        progressBar = ProgressDialog.show(context,"Pull task","Pulling ......");
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
                .addInterceptor(loggingInterceptor)
                .build();

        //Preparing Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURI+"/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        urlMapper = retrofit.create(RetrofitUrlMapper.class);
        final Call<PullTaskResponseData> infraQueListCall = urlMapper.getAllTaskDAta(taskRequestBody);

        infraQueListCall.enqueue(new Callback<PullTaskResponseData>(){
            @Override
            public void onResponse(Call<PullTaskResponseData> call, Response<PullTaskResponseData>response){
                PullTaskResponseData taskResponseData = response.body();
                try
                {
                    //Log.i(TAG,"Total Task: "+taskResponseData.getNumberOfTask().getTotalTask()+ " Total pending: "+taskResponseData.getNumberOfPendingTask().getPendingTask());
                    listener.toTaskListPresenter(taskResponseData.getTaskList());
                }
                catch (Exception e)
                {
                    Log.i(TAG,"Exception: "+e.toString());
                }
                progressBar.cancel();
            }
            @Override
            public void onFailure(Call<PullTaskResponseData> call, Throwable t) {
                Log.i(TAG,"Task List Response Failure: "+t.getMessage());
                progressBar.cancel();
            }
        });
    }
    public interface TaskListener{
        public void toTaskListPresenter(List<TaskList> taskLists);
    }
}
