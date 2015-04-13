package com.roamprocess1.roaming4world.roaming4world;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

public class StoredStatus extends SherlockFragment {

	SharedPreferences prefs;
	private String stored_user_status_1, stored_user_status_2, stored_user_status_3;
	
	
	public void StoredStatus(){
		
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_status_1 = "com.roamprocess1.roaming4world.user_status_1";
		stored_user_status_2 = "com.roamprocess1.roaming4world.user_status_2";
		stored_user_status_3 = "com.roamprocess1.roaming4world.user_status_3";
		
	}
	
	public void updateStatus(String value) {
		
		if(!prefs.getString(stored_user_status_3, "").equals("")){
			prefs.edit().putString(stored_user_status_1, prefs.getString(stored_user_status_2, "")).commit();
			prefs.edit().putString(stored_user_status_2, prefs.getString(stored_user_status_3, "")).commit();
			prefs.edit().putString(stored_user_status_3, value).commit();
		}else if(!prefs.getString(stored_user_status_3, "").equals(""))
			{
			prefs.edit().putString(stored_user_status_1, prefs.getString(stored_user_status_2, "")).commit();
			prefs.edit().putString(stored_user_status_2, value).commit();
		}else {
			prefs.edit().putString(stored_user_status_1, value).commit();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_status_1 = "com.roamprocess1.roaming4world.user_status_1";
		stored_user_status_2 = "com.roamprocess1.roaming4world.user_status_2";
		stored_user_status_3 = "com.roamprocess1.roaming4world.user_status_3";

		return super.onCreateView(inflater, container, savedInstanceState);
	}



	
}
