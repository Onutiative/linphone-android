package org.linphone.onu_legacy.Adapters;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import org.linphone.onu_legacy.Database.Task;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.NewMessageEvent;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter <TaskListAdapter.Holder> {

    private ArrayList<Task> taskList=new ArrayList<>();
    private ViewPager viewPager;
    private SharedPrefManager sharedPrefManager;
    private Task task;
    private String from,taskID;
    private String TAG="TaskListAdapter";

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_row,parent,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        task=taskList.get(position);
        holder.callTime.setText(task.getCallTime());
        holder.callSummary.setText(task.getCallSummary());
        holder.taskStatus.setText(task.getTaskStatus());
    }

    public TaskListAdapter(ArrayList<Task> taskList, ViewPager viewPager,String from,String taskID)
    {
        this.taskList=taskList;
        this.viewPager=viewPager;
        this.from=from;
        this.taskID=taskID;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView callTime, callSummary, taskStatus;
        ImageButton taskReassign;
        public Holder(View itemView)
        {
            super(itemView);
            callTime= (TextView) itemView.findViewById(R.id.call_time);
            callSummary= (TextView) itemView.findViewById(R.id.call_summary);
            taskStatus=(TextView)itemView.findViewById(R.id.task_status);
            taskReassign=itemView.findViewById(R.id.taskReassign);
            taskReassign.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            sharedPrefManager=new SharedPrefManager(v.getContext());
            Gson gson = new Gson();
            String json = gson.toJson(taskList.get(this.getLayoutPosition()));
            sharedPrefManager.setTask(json);
            EventBus.getDefault().post(new NewMessageEvent(json));
            myViewPager();
        }
        private void myViewPager()
        {
            viewPager.setCurrentItem(1);
        }
    }
}
