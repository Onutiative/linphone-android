package org.linphone.onu_legacy.MVP.Implementation.TaskPackage;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.linphone.onu_legacy.Activities.Activities.DashBoard_Activity;
import org.linphone.onu_legacy.Activities.Activities.PopupTaskActivity;
import org.linphone.onu_legacy.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskList;
import org.linphone.R;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TaskShowActivity extends AppCompatActivity implements TaskShowActivityCommunicatior.TashShowView{
    private Context context;
    private RecyclerView tasklistView;
    private String TAG="TaskShowActivity";
    private TaskShowAdapter adapter;
    private PresenterTaskShow presenterTaskShow;
    private ImageView taskToHome,addNewTask;
    private String taskType="All";
    private TextView taskTitleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_show);
        try {
            Objects.requireNonNull(getSupportActionBar()).hide();
        } catch (Exception e){
            // e.printStackTrace();
        }
        context= TaskShowActivity.this;
        presenterTaskShow=new PresenterTaskShow(context);
        taskToHome=findViewById(R.id.taskToHome);
        addNewTask=findViewById(R.id.addNewTask);
        taskTitleName=findViewById(R.id.taskTitleName);

        taskToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getBaseContext(),DashBoard_Activity.class);
                startActivity(intent);
            }
        });

        addNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TaskShowActivity.this, PopupTaskActivity.class);
                intent.putExtra("from","newTask");
                startActivity(intent);
            }
        });
        Intent intent=getIntent();
        taskType=intent.getStringExtra("taskType");
        taskTitleName.setText(taskType + " Task");
        Log.i(TAG,"Task type: "+taskType);
        presenterTaskShow.startExecution(taskType);
    }
    @Override
    public void toTaskListAdapter(List<TaskList> taskLists) {
        Log.i(TAG,"Total Task: "+taskLists.size());
        Collections.reverse(taskLists);
        tasklistView=findViewById(R.id.tasklistView);
        adapter=new TaskShowAdapter(context,taskLists);
        tasklistView.setLayoutManager(new LinearLayoutManager(this));
        tasklistView.setAdapter(adapter);
    }
}
