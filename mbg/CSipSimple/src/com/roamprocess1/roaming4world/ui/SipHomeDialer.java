/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.roamprocess1.roaming4world.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.calllog.CallLogListFragment;
import com.roamprocess1.roaming4world.ui.dialpad.DialerFragment;

public class SipHomeDialer extends SherlockFragment  {
	
	FragmentTabHost mTabHost;
	 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        mTabHost = new FragmentTabHost(getActivity());
 
        mTabHost.setup(getActivity(), getChildFragmentManager(),
                R.layout.r4wdialerhome);
        
      
 
        // Create Child Tab1
        mTabHost.addTab(mTabHost.newTabSpec("child1").setIndicator("Child1"),
                DialerFragment.class, null);
 
        // Create Child Tab2
        mTabHost.addTab(mTabHost.newTabSpec("child2").setIndicator("Child2"),
                CallLogListFragment.class, null);
        return mTabHost;
    }
    /*
 
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
	
	*/
	
	
}
