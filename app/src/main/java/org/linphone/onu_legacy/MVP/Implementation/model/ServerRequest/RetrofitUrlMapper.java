package org.linphone.onu_legacy.MVP.Implementation.model.ServerRequest;

import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactPullDataSet.ContactPulledData;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSummaryPullDataSet.ContactSummaryData;
import org.linphone.onu_legacy.MVP.Implementation.model.ContactDataClasses.ContactSyncDataSet.ContactObject;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskRequestBody;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskResponseData;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskSummaryData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitUrlMapper {

    @POST("SyncContact")
    Call<ContactObject> createPost(@Body ContactObject contactObj);

    @GET("ContactList")
    Call<ContactPulledData> getContactData();

    @GET("ContactSummary")
    Call<ContactSummaryData> getContactSummery();

    @POST("task/taskListApi")
    Call<PullTaskResponseData> getAllTaskDAta(@Body PullTaskRequestBody requestBody);
    @GET("task/taskSummeryApi")
    Call<TaskSummaryData> getTaskSummaryData();
}
