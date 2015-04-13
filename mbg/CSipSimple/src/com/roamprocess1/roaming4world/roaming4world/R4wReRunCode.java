package com.roamprocess1.roaming4world.roaming4world;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
public class R4wReRunCode extends SherlockFragment {
	
	SQLiteAdapter mySQLiteAdapter;
	Cursor dataCheckPin;
	String fwd_no,flight_no_search_query,signalStrength_str;
	boolean unknownhost = false;
	final String encodedHash = Uri.encode("#"),ussd = "**21*";
	Button btnReRunCode;
	TelephonyManager tm;
	private PopupWindow flightPopup;
	int x = 1,accountPage,callRecords,reRunCode;;
	private Typeface nexaBold, nexaNormal;
	LocationManager locationManager;
	EditText edtFlightNumber;
	TelephonyManager telephonyManager;
	MyPhoneStateListener mpsl;
	public ImageButton imgbackbtn;
	private View rootView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.r4wreruncode, container, false);
		/*
		android.app.ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(false);
		View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		imgbackbtn=(ImageButton)customView.findViewById(R.id.imgbackbtn);
		imgbackbtn.setVisibility(View.INVISIBLE);
		imgbackbtn.setClickable(false);
		
		actionBar.setCustomView(customView);
		
		setContentView(R.layout.r4wreruncode);
		
		
		Bundle mapIntent = getIntent().getExtras();
		accountPage=mapIntent.getInt("backPageAccount",5);
		callRecords=mapIntent.getInt("backPageCallRecord",5);
		reRunCode=mapIntent.getInt("backPageReRunCode",5);
		
		Log.d("home callRecords  reRunCode", accountPage+" "+callRecords+""+reRunCode+"");
	*/
		
		btnReRunCode= (Button) rootView.findViewById(R.id.btnReRunCode);
		
		telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(mpsl ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		
		mpsl = new MyPhoneStateListener();
		
		mySQLiteAdapter= new SQLiteAdapter(getActivity());
	    
    	mySQLiteAdapter.openToRead();
    	dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
    
    	dataCheckPin.moveToFirst();
    
    	while(!dataCheckPin.isAfterLast()){
    		fwd_no = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_FWD_NO));
    		dataCheckPin.moveToNext();
    	}
    	
    	btnReRunCode.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				reRunCode();
			}
		});
    	
    	return rootView;
	}

	public void reRunCode(){
			int sim_state = telephonyManager.getSimState();
			switch (sim_state) {
				case TelephonyManager.SIM_STATE_ABSENT:
					Toast.makeText(getActivity(), "No SIM Card", Toast.LENGTH_LONG).show();
					break;
	
				case TelephonyManager.SIM_STATE_READY:
					telephonyManager.listen(mpsl ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
					if(Integer.parseInt(signalStrength_str)==0||Integer.parseInt(signalStrength_str)==99){
						Toast.makeText(getActivity(), "No Network Coverage", Toast.LENGTH_LONG).show();
					}
					else{
						startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:"+ussd+fwd_no+encodedHash)), 1);
						//break;
						//Toast.makeText(R4wActivateFwd.this, "Network coverage is : "+signalStrength_str, Toast.LENGTH_LONG).show();
					}
			}
		}
	
	
	private PhoneStateListener mPhoneListener = new PhoneStateListener() {
		 public void onCallForwardingIndicatorChanged(boolean cfi) {
			 if(cfi==true){
	    		x=2;
	    		Log.d("CFI", "TRUE");
	    		String check_first_time = getActivity().getIntent().getExtras().getString("FIRST_TIME_SCREEN2_OPENS");
	    		if(check_first_time.equalsIgnoreCase("YES")){
	    			//No check runs
	    			Log.d("10","10");
	    		}
	    		else{
	    			Log.d("20","20");
	    			Toast.makeText(getActivity(), "FWD ACTIVE", Toast.LENGTH_SHORT).show();
		    		mySQLiteAdapter.openToWrite();
					//mySQLiteAdapter.update_flag(2);
					mySQLiteAdapter.close();
		    		Intent intent_R4wMapService = new Intent(getActivity(), R4wMapService.class);
		    		startActivity(intent_R4wMapService);
		    		
		    		getActivity().finish();
	    		}
	    	}
	    	else{
	    		x=3;
	            Log.d("CFI", "FALSE");
	            Toast.makeText(getActivity(), "FWD DEACTIVE", Toast.LENGTH_SHORT).show();
	            
	            //Show alert
	    	}
	    }
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		mySQLiteAdapter.openToWrite();
		mySQLiteAdapter.update_flag(2);
		mySQLiteAdapter.close();
		Intent intent = new Intent(getActivity(), R4wHome.class);
		startActivity(intent);
		/*
		if(accountPage==1||callRecords==2||reRunCode==3)
		{
			Intent intent_R4wMapService = new Intent(R4wReRunCode.this, R4wMapGUI.class);
			startActivity(intent_R4wMapService);
		}else
		{
			Intent intent = new Intent(R4wReRunCode.this, R4wHome.class);
			startActivity(intent);
		}*/
		getActivity().finish();
	}
	
	public void setFont() {
		nexaBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");
		
		btnReRunCode.setTypeface(nexaBold);
	}
	private class MyPhoneStateListener extends PhoneStateListener
   {
     /* Get the Signal strength from the provider, each tiome there is an update */
     @Override
     public void onSignalStrengthsChanged(SignalStrength signalStrength)
     {
        super.onSignalStrengthsChanged(signalStrength);
        signalStrength_str=String.valueOf(signalStrength.getGsmSignalStrength());
        //Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ signalStrength, Toast.LENGTH_SHORT).show();
     }

   };
   @Override
   public void onPause()
    {
      super.onPause();
      telephonyManager.listen(mpsl, PhoneStateListener.LISTEN_NONE);
   }

    /* Called when the application resumes */
   @Override
   public void onResume()
   {
      super.onResume();
      telephonyManager.listen(mpsl,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
   }
   
	/*
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if(accountPage==1||callRecords==2||reRunCode==3)
		{
			Intent intent = new Intent(this, R4wMapGUI.class);
			startActivity(intent);
			
		}else{
		
		Intent intent = new Intent(this, R4wHome.class);
		startActivity(intent);
		}
		finish();
	}
	*/
}
