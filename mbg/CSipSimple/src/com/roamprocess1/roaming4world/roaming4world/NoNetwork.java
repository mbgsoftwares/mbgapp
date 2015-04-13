package com.roamprocess1.roaming4world.roaming4world;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.roamprocess1.roaming4world.R;

public class NoNetwork extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nonetwork);
	}
	public void retry(View v){
		switch(v.getId()){
			case R.id.btnRetry:
				boolean xx=isNetworkAvailable();
		    	if(xx==true){
		    		//Intent intent_main=new Intent(NoNetwork.this, MainActivity.class);
	    			//startActivity(intent_main);
	    			finish();
		    	}
		    	else{
		    		Log.d("network","false");
		    	}
				break;
		}
	}
	private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
	@Override
    public void onBackPressed() {
    	Log.d("CDA", "onBackPressed Called");
    	Intent setIntent = new Intent(Intent.ACTION_MAIN);
    	setIntent.addCategory(Intent.CATEGORY_HOME);
    	setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(setIntent);
    }
}
