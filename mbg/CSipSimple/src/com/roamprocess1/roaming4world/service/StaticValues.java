package com.roamprocess1.roaming4world.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import com.roamprocess1.roaming4world.R;

public class StaticValues {

	public static boolean outCallPayment(String bal , String minBal){
		 double balance_user = Double.parseDouble(bal);
		 double min_balance_user = Double.parseDouble(minBal);
		 return (balance_user > min_balance_user) ? true : false;
	}

	public static String getStripNumber(String num){
		String nu = num;
		if (nu.contains("@")) {
			String[] r = nu.split("@");
			nu = r[0];
			if(nu.contains(":")){
				r = nu.split(":");
				nu = r[1];
			}
		}
		return nu;
	}
	
	public static String getServerIPAddress(String ip){
		if (ip.contains(":")) {
			String[] r = ip.split(":");
			ip = r[0];
		}
		return ip;
	}
	
	public static Intent onIntentChooseClick(Intent intent , Context context) {
	 
		Resources resources = context.getResources();
	    PackageManager pm = context.getPackageManager();
	    Intent sendIntent = new Intent();     
	  	Intent openInChooser = Intent.createChooser(intent, resources.getString(R.string.share_chooser_text));

	    List<ResolveInfo> resInfo = pm.queryIntentActivities(StaticValues.setImageTypeIntent(sendIntent), 0);
	    List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();        
	    for (int i = 0; i < resInfo.size(); i++) {
	        // Extract the label, append it, and repackage it in a LabeledIntent
	        ResolveInfo ri = resInfo.get(i);
	        String packageName = ri.activityInfo.packageName;
	        	if(!packageName.contains("drive") || !packageName.contains("dropbox") || !packageName.contains("photos") || !packageName.contains("samsung")){
	        	Intent intent_package = new Intent();
	        	intent_package.setComponent(new ComponentName(packageName, ri.activityInfo.name));
	     	            intentList.add(new LabeledIntent(StaticValues.setImageTypeIntent(intent_package), packageName, ri.loadLabel(pm), ri.icon));
	        }
	    }

	    // convert intentList to array
	    LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

	    openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
	    return (openInChooser);       
	}
	
	public static Intent setImageTypeIntent(Intent intent){
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);

		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 400);
		intent.putExtra("return-data", true);
		return intent;
	}
	
}
