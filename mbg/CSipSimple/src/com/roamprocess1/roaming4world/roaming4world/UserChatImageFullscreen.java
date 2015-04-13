package com.roamprocess1.roaming4world.roaming4world;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.roamprocess1.roaming4world.R;

public class UserChatImageFullscreen extends Activity{


	String path = Environment.getExternalStorageDirectory()+ "/R4W/SharingImage/";
	
	 String imageuri , recuri, senduri, user_num;
	 private SharedPreferences prefs;

	private String stored_chatuserNumber;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		  prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
	      stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
	      
		setContentView(R.layout.userchatimagefullscreen);
		ImageView im = (ImageView) findViewById(R.id.iv_userchatimage);

		Bundle extras = getIntent().getExtras();
		if (extras.getString("imageuri") != null) {
			imageuri = getIntent().getStringExtra("imageuri");
			user_num = getIntent().getStringExtra("number");
		}
		
		if(imageuri.contains("\n")){
			String arr[] = imageuri.split("\n");
			imageuri = arr[0];
		}
		
		Log.d("imageuri", imageuri + " !");
		String numb = prefs.getString(stored_chatuserNumber, "");
		numb = stripNumber(numb);
		if (imageuri != null) {
			try{
				String[] arre = imageuri.toString().split("@@");
				String[] arr = arre[1].trim().split("-");
				recuri = path + user_num + "/Recieved/" +arr[2];
				senduri = path + user_num + "/send/" +arr[2];
				
				Log.d("recuri", recuri + " !");
				Log.d("senduri", senduri + " !");
				
				
				String imgpath = "";
				File fi = new File(recuri);
				if (fi.exists()) {
					imgpath = recuri;
				} else {
					File file = new File(senduri);
					if (file.exists()) {
						imgpath = senduri;
					}
				}
				if (!imgpath.equals("")) {
					im.setImageURI(Uri.parse(imgpath));
				}else{
					im.setImageResource(R.drawable.logo);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	   public String stripNumber(String nu){
	    	if(nu.contains("@")){
	    		String[] arr = nu.split("@");
	    		nu = arr[0];
	    		if(nu.contains(":")){
	    			arr = nu.split(":");
	    			nu = arr[1];
	    		}
	    	}
	    	return nu;
	    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	
	
	

}
