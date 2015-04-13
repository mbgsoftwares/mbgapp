package com.roamprocess1.roaming4world.roaming4world;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.roamprocess1.roaming4world.R;
public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	    setContentView(R.layout.splash);
		
		boolean xx=isNetworkAvailable();
    	if(xx==true){
    		Log.d("network","true");
    	}
    	else{
    		Intent intent_main=new Intent(Splash.this,NoNetwork.class);
			startActivity(intent_main);
			finish();
    	}
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
		  @Override
		  public void run() {
		    //Do something after 100ms
			 Intent intent=new Intent(Splash.this, R4wHome.class);
			 startActivity(intent);
			 finish();
		  }
		}, 1000);
	}
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
