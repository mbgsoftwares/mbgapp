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

package com.roamprocess1.roaming4world.ui.calllog;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.calllog.CallLogDetailsFragment.OnQuitListener;
import com.roamprocess1.roaming4world.utils.Compatibility;

public class CallLogDetailsActivity extends SherlockFragmentActivity implements OnQuitListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("CallLogDetailsActivity");

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            CallLogDetailsFragment detailFragment = new CallLogDetailsFragment();
            detailFragment.setArguments(getIntent().getExtras());
            detailFragment.setOnQuitListener(this);
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, detailFragment).commit();
        }
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		super.onStart();
       
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	    ab.setHomeButtonEnabled(true);
	    ab.setDisplayShowHomeEnabled(false);
	    ab.setDisplayShowTitleEnabled(false);
		ab.setCustomView(R.layout.r4wactionbarcustom);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
		relativeLayout.setBackgroundColor(Color.parseColor("#8BC249"));
		
		LinearLayout fin = (LinearLayout) ab.getCustomView().findViewById(R.id.ll_header_finish);
		fin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		/*
		ImageView userPic = (ImageView) ab.getCustomView().findViewById(R.id.ab_userpic);
        TextView name  = (TextView) ab.getCustomView().findViewById(R.id.ab_userName);
        userPic.setImageResource(R.drawable.roaminglogo);
        LinearLayout backLinearLayout=(LinearLayout)ab.getCustomView().findViewById(R.id.ll_backFromChat); 
        name.setText("Roaming4world");
        
   	
        name.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        userPic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        backLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				finish();
			}
		});
      */
		
	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == Compatibility.getHomeMenuId()) {
	         finish();
	         return true;
	    }

        return super.onOptionsItemSelected(item);
	}

	@Override
	public void onQuit() {
		finish();
	}

	@Override
	public void onShowCallLog(long[] callsId) {
		
	}
}
