package org.linphone.onu_legacy.MVP.Implementation.TaskPackage;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.onutiative.onukit.MVP.Implementation.model.ServerRequest.TaskConversion;
import com.onutiative.onukit.MVP.Implementation.model.TaskDataClasses.TaskPullDataSet.TaskList;
import com.onutiative.onukit.R;

import java.util.List;

public class TaskShowAdapter extends RecyclerView.Adapter<TaskShowAdapter.Holder>{

    private Context context;
    private List<TaskList> taskLists;
    private String TAG="TaskShowAdapter";
    private TaskConversion taskConversion;

    public TaskShowAdapter(Context context, List<TaskList> taskLists) {
        this.context = context;
        this.taskLists = taskLists;
        taskConversion=new TaskConversion(context);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.task_list_row,viewGroup,false);
        Holder holder=new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int i) {
        holder.callTime.setText(taskLists.get(i).getCallTime());
        holder.callSummary.setText(taskLists.get(i).getCallSummery());
        holder.taskStatus.setText(taskLists.get(i).getSummeryStatus());
        holder.callerNAME.setText(taskLists.get(i).getCallerName());

        holder.taskReassign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Task id: "+taskLists.get(i).getId());
                taskConversion.taskMaking(taskLists.get(i).getMobileNo(),taskLists.get(i).getCallSummery(),taskLists.get(i).getCallTime(),taskLists.get(i).getId(),"reassignTaskList");
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskLists.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        TextView callTime, callSummary, taskStatus,callerNAME;
        ImageButton taskReassign;
        public Holder(View itemView) {
            super(itemView);
            callTime= (TextView) itemView.findViewById(R.id.call_time);
            callSummary= (TextView) itemView.findViewById(R.id.call_summary);
            taskStatus=(TextView)itemView.findViewById(R.id.task_status);
            taskReassign=itemView.findViewById(R.id.taskReassign);
            callerNAME=itemView.findViewById(R.id.callerNAME);
        }
    }
}
