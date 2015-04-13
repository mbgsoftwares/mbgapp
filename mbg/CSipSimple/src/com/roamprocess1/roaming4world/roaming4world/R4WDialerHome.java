package com.roamprocess1.roaming4world.roaming4world;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.calllog.CallLogListFragment;
import com.roamprocess1.roaming4world.ui.dialpad.DialerFragment;

public class R4WDialerHome extends SherlockFragment  {
	
	FragmentTabHost mTabHost;
	 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
    	 mTabHost = new FragmentTabHost(getSherlockActivity());
    	 
         mTabHost.setup(getSherlockActivity(), getChildFragmentManager(),
                 R.layout.r4wdialerhome);
        
      
 
        // Create Child Tab1
        mTabHost.addTab(mTabHost.newTabSpec("child1").setIndicator("Child1"),
                DialerFragment.class, null);
 
        // Create Child Tab2
        mTabHost.addTab(mTabHost.newTabSpec("child2").setIndicator("Child2"),
                CallLogListFragment.class, null);
        return mTabHost;
    }
  
 
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
	
	
	
	
	

	

}
