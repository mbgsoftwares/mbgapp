package com.roamprocess1.roaming4world.roaming4world;

import android.content.SharedPreferences;

public class Sharedpref {

	public SharedPreferences prefs;
	String stored_chatuserNumber;
	
	
	public String getNumber(){
		 
	//	prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
        stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
        return stored_chatuserNumber;
	}
}
