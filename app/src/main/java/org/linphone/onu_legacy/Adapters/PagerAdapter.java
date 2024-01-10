package org.linphone.onu_legacy.Adapters;
//<!--used in 6v3-->

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.linphone.onu_legacy.Fragments.TaskFormFragment;
import org.linphone.onu_legacy.Fragments.TaskListFragment;
//<!--used in 6v3-->

public class PagerAdapter extends FragmentStatePagerAdapter {


    private int mNoOfTabs;
    private String TAG = "PageAdapter";



    public PagerAdapter(FragmentManager fm, int NumberOfTabs) {
        super(fm);
        this.mNoOfTabs=NumberOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
//        return null;


        switch(position)
        {
            case 0:
                TaskListFragment taskListFragment=new TaskListFragment();
                return taskListFragment;

            case 1:
                TaskFormFragment taskFormFragment=new TaskFormFragment();
                return taskFormFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
