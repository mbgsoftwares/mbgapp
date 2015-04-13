package com.roamprocess1.roaming4world.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartMyServiceAtBootReceiver  extends BroadcastReceiver {

 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		 if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
	            Intent serviceIntent = new Intent(context, SipHome.class);
	            context.startService(serviceIntent);
	        }
	}
}
