package com.roamprocess1.roaming4world.roaming4world;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;


public class MissedCallDetailsFragment extends SherlockFragment{

	View v;
	ImageView iv_contact_background;
	TextView tv_usernumber, tv_userlabel;;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		
		v = inflater.inflate(R.layout.call_detail, container, false);
		
		initilizer();
		
		setValue();
		
		return v;
	}

	private void setValue() {
		// TODO Auto-generated method stub
	}

	private void initilizer() {
		// TODO Auto-generated method stub
		iv_contact_background = (ImageView) v.findViewById(R.id.contact_background);
		tv_usernumber = (TextView) v.findViewById(R.id.call_and_sms_label);
		tv_userlabel = (TextView) v.findViewById(R.id.call_and_sms_text);
			
	}

	
	
}
