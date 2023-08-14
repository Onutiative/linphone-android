package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Database.Contact;
import org.linphone.onu_legacy.Database.Database;
import org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest.RetrofitUrlMapper;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskRequestBody;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskResponseData;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskList;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskSummaryData;

import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskSummaryData;

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

public class TaskSummaryPullRepository {
    private Context context;
    private RetrofitUrlMapper urlMapper;
    private String baseURI;
    private String username;
    private String password;
    private String TAG="TaskSummaryPullRepository";
    private TaskSummaryListener listener;

    public TaskSummaryPullRepository(Context context, String baseURI, String username, String password) {
        this.context = context;
        listener= (TaskSummaryListener) context;
        this.baseURI=baseURI;
        this.username=username;
        this.password=password;
    }

    public void pullTaskSummary() {
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
                .baseUrl(baseURI)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        urlMapper = retrofit.create(RetrofitUrlMapper.class);
        final Call<TaskSummaryData> infraQueListCall = urlMapper.getTaskSummaryData();

        infraQueListCall.enqueue(new Callback<TaskSummaryData>(){
            @Override
            public void onResponse(Call<TaskSummaryData> call, Response<TaskSummaryData>response){
                TaskSummaryData taskSummaryData = response.body();
                try
                {
                    listener.getUpdateDashboard(taskSummaryData);
                }
                catch (Exception e)
                {
                    Log.i(TAG,"Exception: "+e.toString());
                }
            }
            @Override
            public void onFailure(Call<TaskSummaryData> call, Throwable t) {
                Log.i(TAG,"Task List Response Failure");
            }
        });
    }
    public interface TaskSummaryListener{
        public void getUpdateDashboard(TaskSummaryData taskSummaryData);
    }

}
