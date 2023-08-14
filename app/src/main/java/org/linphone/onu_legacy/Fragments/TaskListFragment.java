package org.linphone.onu_legacy.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.linphone.onu_legacy.Activities.Activities.PopupTaskActivity;
import org.linphone.onu_legacy.Adapters.TaskListAdapter;
import org.linphone.onu_legacy.Database.Task;
import org.linphone.R;
import org.linphone.onu_legacy.Utility.SharedPrefManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView timeStamp1,timeStamp2,timeStamp3,callSummary1,callSummary2,callSummary3,
            status1,status2,status3;

    private String TAG="TaskListFragment";

    //private String taskSummary;

    private TaskListAdapter taskListAdapter;
    private RecyclerView taskListRecyclerView;

    JSONArray taskArray;

    ArrayList<Task> taskList;
    private TextView extraText;
    private LinearLayout extraLayout;

    private String callerName, phoneNo, taskID,from;

    private Context context;
    private ViewPager viewPager;
    private SharedPrefManager sharedPrefManager;

    public TaskListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskListFragment newInstance(String param1, String param2) {
        TaskListFragment fragment = new TaskListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_task_list, container, false);

        taskListRecyclerView = (RecyclerView) view.findViewById(R.id.task_list_recycler_view);
        extraText=(TextView)view.findViewById(R.id.extra_text);
        extraLayout=(LinearLayout)view.findViewById(R.id.extra_layout);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context=getActivity();

        //taskSummary=getActivity().getIntent().getStringExtra("summary").toString();
        taskList= (ArrayList<Task>) getActivity().getIntent().getSerializableExtra("summaryObj");
        viewPager=(ViewPager)getActivity().findViewById(R.id.pager);

        //Toast.makeText(context,taskSummary,Toast.LENGTH_LONG).show();
        ///Log.d(TAG,taskSummary);
        try{
            from=getActivity().getIntent().getStringExtra("from");
            taskID=getActivity().getIntent().getStringExtra("taskID");
            phoneNo = getActivity().getIntent().getStringExtra("phoneNo");
            callerName=getActivity().getIntent().getStringExtra("callerName");
        }catch (Exception e){
            Log.i(TAG,"Exception: "+e.toString());
        }

        if (!from.equals("newTask")){
            if(callerName.equals(""))
            {
                callerName="No Name Found";
            }
            if(taskList==null||taskList.size()<=0) {
                extraLayout.setVisibility(View.VISIBLE);
                extraText.setVisibility(View.VISIBLE);
                extraText.setText("No Task to Show!");
            } else if(taskList.size()>0)
            {
                if (PopupTaskActivity.newTask!=null){
                    Task newTask = PopupTaskActivity.newTask;
                    Log.i(TAG,"New Task:\n\nAssEmail:"+newTask.getAsigneeEmail()+"\nCallTime: "+newTask.getCallTime()
                            +"\nCallPurpose: "+newTask.getCallPurpose()+"\nCallSummary: "+newTask.getCallSummary()
                            +"\nCallStatus: "+newTask.getTaskStatus()+"\nEst.Date: "+newTask.getEstimatedDate()
                            +"\nEst.Time: "+newTask.getEstimatedTIme());
                    Collections.reverse(taskList);
                    taskList.add(newTask);
                    Collections.reverse(taskList);
                    PopupTaskActivity.newTask=null;
                }
                taskListAdapter = new TaskListAdapter(taskList,viewPager,from,taskID);
                taskListRecyclerView.setAdapter(taskListAdapter);
                taskListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }
    }
}
