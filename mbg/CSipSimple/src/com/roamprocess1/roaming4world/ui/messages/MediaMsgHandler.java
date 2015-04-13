package com.roamprocess1.roaming4world.ui.messages;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.roamprocess1.roaming4world.R;

public class MediaMsgHandler extends Activity {
	   private VideoView mVideoView;

	   String path = Environment.getExternalStorageDirectory()+ "/R4W/SharingImage/";
	   
	   @Override
	   public void onCreate(Bundle icicle) {
	     super.onCreate(icicle);
	     setContentView(R.layout.videoview);
	     
	     String msg = "", user_num = "";
	     
	     if(getIntent().getStringExtra("msg") != null){
	    	 msg = getIntent().getStringExtra("msg").trim();
	    	 user_num = getIntent().getStringExtra("number").trim();
	     }
	     Log.d("msg ", msg + " @");
	     Log.d("user_num ", user_num + " @");
	     
	     String path = getPath(msg, user_num);
	     if(!path.equals("")){
	    	 Uri uri = Uri.parse(path);
	 	     mVideoView = (VideoView) findViewById(R.id.vv_videoview);
	 	     mVideoView.setVideoURI(uri);
		     mVideoView.setMediaController(new MediaController(this));
		     mVideoView.requestFocus();
	    	 
	     }else{
	    	 Toast.makeText(getApplicationContext(), "Error in fIle loading.", Toast.LENGTH_SHORT).show();
	     }
	   }

	private String getPath(String msg, String user_num) {
		// TODO Auto-generated method stub
		try {

			String[] arre = msg.toString().split("@@");
			String[] arr = arre[1].split("-");
			String recuri = path + user_num + "/Recieved/" +arr[2];
			String senduri = path + user_num + "/send/" +arr[2];
			
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
			return imgpath;
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	}
