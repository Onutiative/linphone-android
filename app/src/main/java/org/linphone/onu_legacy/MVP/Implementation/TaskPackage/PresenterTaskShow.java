package org.linphone.onu_legacy.MVP.Implementation.TaskPackage;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.util.ArrayUtils;
import com.onutiative.onukit.Database.Contact;
import com.onutiative.onukit.Database.Database;
import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.TaskPullRepository;
import com.onutiative.onukit.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.PullTaskRequestBody;
import com.onutiative.onukit.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.reverse;

public class PresenterTaskShow implements TaskShowActivityCommunicatior.TaskShowPresenter, TaskPullRepository.TaskListener {
    Context context;
    TaskShowActivityCommunicatior.TashShowView view;
    String TAG ="PresenterTaskShow";
    private String uname = null, upass = null, baseUrl = null, urlForEdition = null,userId=null,parentID="0";

    public PresenterTaskShow(Context context) {
        this.context = context;
        this.view = (TaskShowActivityCommunicatior.TashShowView) context;
    }

    @Override
    public void startExecution(String taskType) {
        getAdminInfo();
        PullTaskRequestBody requestBody=new PullTaskRequestBody("","",taskType);
        Log.i(TAG, requestBody.toString());
        TaskPullRepository pullRepository=new TaskPullRepository(this,context,baseUrl,uname,upass,requestBody);
        pullRepository.pullTask();
    }

    @Override
    public void toTaskListPresenter(List<TaskList> taskLists) {
        Collections.reverse(taskLists);
        view.toTaskListAdapter(taskLists);
    }

    public void getAdminInfo() {
        Database db = new Database(context);
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
            }else if (cn.getName().equals("Custom_url")){
                baseUrl=cn.getPhone_number();
            }
        }
    }
}
