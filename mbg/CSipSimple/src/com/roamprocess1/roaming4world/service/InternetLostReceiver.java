package com.roamprocess1.roaming4world.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class InternetLostReceiver extends BroadcastReceiver{
@Override
public void onReceive(Context context, Intent intent) {
		
		String network = "INTERNET_LOST";
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            	network = "INTERNET_CONNECTED";
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            	network = "INTERNET_CONNECTED";
            
        }else{
        	network = "INTERNET_LOST";
        } 
        
        Log.d("InternetLostReceiver", "onReceive " + network);
    	context.sendBroadcast(new Intent(network));
}
}
