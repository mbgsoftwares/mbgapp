package com.roamprocess1.roaming4world.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipManager;

public class Setting extends SherlockFragment {
	private final static int CHANGE_PREFS = 1;
	
	Button btnsetting;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v= inflater.inflate(R.layout.setting, container, false);
		
	btnsetting= (Button) v.findViewById(R.id.btnSetting);
	btnsetting.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			 startActivityForResult(new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), CHANGE_PREFS);
				
			
		}
	});
		
		
		return v;
	}
	
}
