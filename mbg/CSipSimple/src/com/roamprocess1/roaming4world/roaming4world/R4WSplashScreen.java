package com.roamprocess1.roaming4world.roaming4world;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.roamprocess1.roaming4world.syncadapter.SyncUtils;
import com.roamprocess1.roaming4world.ui.RContactlist;
import com.roamprocess1.roaming4world.ui.SipHome;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.utils.Log;
public class R4WSplashScreen extends SherlockFragmentActivity{

	public static String TAG="R4WSplashScreen";
	String prefChooseclass,signUpProcess,prefSignUpProcess,stored_upgrade_apk,screen2_status,screen1_status,
	r4wContactsLoad,prefR4wContactsLoad;
	String chooseClass = "0";
	Thread mThread;
	SharedPreferences pref;
	Intent i = null;
	public int TYPE_WIFI = 1;
	public int TYPE_MOBILE = 2;
	public int TYPE_NOT_CONNECTED = 0;
	int connectivity_status = TYPE_NOT_CONNECTED;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		pref = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		prefChooseclass = "com.roamprocess1.roaming4world.chooseClass";
		stored_upgrade_apk = "com.roamprocess1.roaming4world.upgrade_apk_version";
		signUpProcess = "com.roamprocess1.roaming4world.signUpProcess";
		prefSignUpProcess =pref.getString(signUpProcess, "NotCompleted");
		screen2_status = "com.roamprocess1.roaming4world.screen2_status";
		screen1_status = "com.roamprocess1.roaming4world.screen1";
		
		
	//	setContentView(R.layout.r4wsplashsreen);
		Log.setLogLevel(6);
		Log.d(TAG ,"prefSignUpProcess =="+prefSignUpProcess);
		Log.d("connectivity_status before", connectivity_status + " @");
		Context context= R4WSplashScreen.this;
	     Bundle bundle = getIntent().getExtras();
	     connectivity_status = getConnectivityStatus(getApplicationContext());
	     Log.d("connectivity_status", connectivity_status + " @");
	     
	     if(bundle!=null && connectivity_status != TYPE_NOT_CONNECTED){
	    	 Log.d("R4WSplashScreen", " if(bundle!=null && connectivity_status != TYPE_NOT_CONNECTED)");
	    	 System.out.println("bundle");
	    	 String r4wContactsStatus= (String) bundle.get("R4wContactStatus");
	    	 Log.d(TAG,"R4wContactsStatus"+r4wContactsStatus);
	    	 pref.edit().putString(prefSignUpProcess, "R4wContactsCompleted");
	    	 
	    	 SyncUtils.TriggerRefresh();
	    	 i = new Intent(R4WSplashScreen.this, SipHome.class);
				startActivity(i);
				finish();
	    //	 RContactlist rContactlist= new RContactlist();
	    //	 rContactlist.r4wContacts(context);
	    	 }
	     else{
	    	 Log.d("R4WSplashScreen", " else (bundle!=null && connectivity_status != TYPE_NOT_CONNECTED)");
	    		startTransaction();
	    }
	}
	
	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return MessageActivity.TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return MessageActivity.TYPE_MOBILE;
		}
		return MessageActivity.TYPE_NOT_CONNECTED;
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	
	public void startTransaction(){

		// TODO Auto-generated method stub
		
		try{

			 Log.setLogLevel(6);
			 Log.d("thread run", "Called");
	    	 Log.d("R4WSplashScreen pref.getString(stored_upgrade_apk, )", pref.getString(stored_upgrade_apk, "0") + " @");
	    	 Log.d("R4WSplashScreen pref.getString(prefChooseclass, )",  pref.getString(prefChooseclass, "") + " @");
	    		
		     if(pref.getString(stored_upgrade_apk, "0").equals("0")){
		    	 
					pref.edit().putLong(screen2_status, 0).commit();
					pref.edit().putLong(screen1_status, 0).commit();
		    	    Log.d("R4WSplashScreen", "if upgrade apk");
					i = new Intent(R4WSplashScreen.this, RegChooseLogin.class);
					startActivity(i);
					finish();
				}
		     /*
		     else if(pref.getString(stored_upgrade_apk, "0").equals("1")){

					Log.d("R4WSplashScreen", "else if upgrade apk");
					i = new Intent(R4WSplashScreen.this, RegChooseLogin.class);
					startActivity(i);
					
				}
				*/
		    if(connectivity_status == TYPE_NOT_CONNECTED && pref.getString(prefChooseclass, "").equals("SipHome")){
				i = new Intent(R4WSplashScreen.this, SipHome.class);
				startActivity(i);
				finish();
		    } else if(chooseClass.equals("0") || chooseClass.equalsIgnoreCase("com.roamprocess1.roaming4world.chooseClass") ){
				Log.d("R4WSplashScreen", "else if");
				i = new Intent(R4WSplashScreen.this, RegChooseLogin.class);
				startActivity(i);
				finish();
			}else if (chooseClass.equals("1")){
				Log.d("R4WSplashScreen", "else if");
				i = new Intent(R4WSplashScreen.this, SipHome.class);
				startActivity(i);
				finish();
				
			}
       
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		
	
	}
	
}
