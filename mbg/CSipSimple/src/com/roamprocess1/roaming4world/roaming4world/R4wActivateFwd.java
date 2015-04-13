package com.roamprocess1.roaming4world.roaming4world;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.SipHome;
public class R4wActivateFwd extends SherlockFragment {

	SQLiteAdapter mySQLiteAdapter;
	Cursor dataCheckPin;
	String fwd_no,flight_no_search_query,signalStrength_str,flight_dep_date,flight_full_name,Carrier_code,destination_flight_no,DIDNumberForVoiceMail;
	boolean unknownhost = false;
	final String encodedHash = Uri.encode("#"),ussd = "**21*";
	Button btnActivatePin,btnget_info_prompt,btnAppTour, btnSkip;
	TelephonyManager tm;
	private PopupWindow flightPopup;
	int x = 1,fetch_flag;
	
	TextView homenetworkWrng;
	
	boolean loading = false;
	
	private Typeface nexaBold, nexaNormal;
	LocationManager locationManager;
	SharedPreferences prefs;
	EditText edtFlightNumber,edtCarrierCode,edtDepYear,edtDepMonth,edtDepDay;
	TelephonyManager telephonyManager;
	MyPhoneStateListener mpsl;
	String flightdeparture_airport_code,flightarrival_airport_code,departure_city,arrival_city;
	String departure_date,arrival_date,departure_day,departure_time,arrival_day,arrival_time,user_departure_country_local_time,user_departure_country_local_day,user_departure_country_local_timing;
	String dayOfTheWeek_dep,dayOfTheWeek_arr,dep_month_name_in_numeric,arv_month_name_in_numeric,dep_month_name,arival_month_name;
	String Departure_terminal,Arrival_terminal,dep_day_name_in_numeric,arv_day_name_in_numeric;
	String hms,get_info_show_status,get_info_status_value,shared_pref_flight_number_search_query,shared_pref_dep_date,Estimated_Gate_departure;
	String shared_pref_flight_number_search_query_value,shared_pref_dep_date_value,shared_pref_carrier_code,shared_pref_flight_id;
	Dialog dialog;
	Thread thread;
	View rootView;
	Button btnrefresh_flight_details, btnedit_flight_details;
	String shared_pref_dep_month,shared_pref_dep_day,shared_pref_dep_year,overlayScreenValueR4wActivationFwd;
	LinearLayout get_info_layout,lleditflightinfo,refresh_butt;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		
		rootView = inflater.inflate(R.layout.r4wregisterpin, container, false);
		CurrentFragment.name="ActivationFwd";
		Log.d("layout from  4wActivationFWD", "");
		
		prefs=getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		
		get_info_show_status="com.roamprocess1.roaming4world.get_info_show";
		shared_pref_flight_number_search_query="com.roamprocess1.roaming4world.flight_number_search_query";
		shared_pref_dep_date="com.roamprocess1.roaming4world.dep_date";
		shared_pref_carrier_code="com.roamprocess1.roaming4world.carrier_code";
		get_info_status_value=prefs.getString(get_info_show_status, "0");
		shared_pref_dep_date_value=prefs.getString(shared_pref_dep_date, "no");
		shared_pref_flight_number_search_query_value=prefs.getString(shared_pref_flight_number_search_query, "no");
		shared_pref_flight_id="com.roamprocess1.roaming4world.flight_id";
		
		shared_pref_dep_month="com.roamprocess1.roaming4world.pref_dep_month";
		shared_pref_dep_day="com.roamprocess1.roaming4world.pref_dep_day";
		shared_pref_dep_year="com.roamprocess1.roaming4world.pref_dep_year";
		
		DIDNumberForVoiceMail ="com.roamprocess1.roaming4world.DIDNumberForVoiceMail";
		
		
		btnActivatePin= (Button) rootView.findViewById(R.id.btRegisterPin);
		loading = CurrentFragment.flightLoading;
		
		mySQLiteAdapter = new SQLiteAdapter(getActivity());
		mySQLiteAdapter.openToRead();
		try {
			dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dataCheckPin.moveToFirst();
		
		while (!dataCheckPin.isAfterLast()) {
			fetch_flag = dataCheckPin.getInt(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_FLAG));
			
			Log.d("fetch flag in side  the r4wactionFWD", fetch_flag+"");
			if (fetch_flag !=1 || fetch_flag == 3) {
				System.out.println("fetch_flag !=1");
				LinearLayout linearLayoutActivation= (LinearLayout) rootView.findViewById(R.id.layoutBtnActivation);
				linearLayoutActivation.setVisibility(LinearLayout.GONE);
			TextView textsimMessage= (TextView) rootView.findViewById(R.id.tvhomeNetworkWrng);
			textsimMessage.setVisibility(TextView.GONE);
				btnActivatePin.setVisibility(Button.GONE);
				
			} 
			dataCheckPin.moveToNext();
		}
		mySQLiteAdapter.close();
		//dialogbox();
		
		((SipHome) getActivity()).Fill_LeftList();
		((SipHome) getActivity()).RefreshListView();
		
		btnActivatePin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				click_method(v);
				
			}
		});
		
		


			
		
			Log.d("get_info_status_value", get_info_status_value+"");
		String check_first_time="YES";
		Log.d("check_first_time value ==", check_first_time);
		
		if(check_first_time.equalsIgnoreCase("YES")){
			//No check runs
		}
		else{
			
			tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
		}
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
    	try {
		
    	 Log.d("fwd number inside the activation FWD=", fwd_no);
    	    prefs.edit().putString(DIDNumberForVoiceMail, fwd_no).commit();
    	} catch (Exception e) {
		// TODO: handle exception
    	}
     	
		
		return rootView;
		
		
	}

	

	public void flightsearch_meth(View v) {
		switch (v.getId()) {
		case R.id.btnflight:
			Log.d("Button Click","OK");
			break;
		}
	}



	public void click_method(View v){
		//prefs.edit().remove(shared_pref_carrier_code).commit();
		//prefs.edit().remove(shared_pref_dep_date).commit();
		//prefs.edit().remove(shared_pref_flight_id).commit();
		//prefs.edit().remove(shared_pref_flight_number_search_query).commit();
		//prefs.edit().remove(get_info_show_status).commit();
		
		try{
			
		
		switch(v.getId()){
		case R.id.btRegisterPin:
			telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			telephonyManager.listen(mpsl ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
			int sim_state = telephonyManager.getSimState();
			switch (sim_state) {
				
				case TelephonyManager.SIM_STATE_ABSENT:
					Toast.makeText(getActivity(), "No SIM Card", Toast.LENGTH_LONG).show();
					break;
	
				case TelephonyManager.SIM_STATE_READY:
						startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:"+ussd+fwd_no+encodedHash)), 1);
						break;
			}
			//tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			//tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR);
			//startActivityForResult(new Intent("android.intent.action.CALL", Uri.parse("tel:"+ussd+"918800221008"+encodedHash)), 1);
			break;
		}
	}catch(Exception e){
		
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
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.Frame_Layout, new R4wMapService()).setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).commit();
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
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.Frame_Layout, new R4wMapService()).commit();
		
		/*
		Intent intent_R4wMapService = new Intent(R4wActivateFwd.this, R4wMapGUI.class);
		startActivity(intent_R4wMapService);
		finish();*/
	}
	
	public void setFont() {
		nexaBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");
		btnActivatePin.setTypeface(nexaBold);
	}
	private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each tiome there is an update */
      @SuppressLint("NewApi") @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         signalStrength_str=String.valueOf(signalStrength.getGsmSignalStrength());
        
      }

    };
    @Override
    public void onPause()
     {
       super.onPause();
       //telephonyManager.listen(mpsl, PhoneStateListener.LISTEN_NONE);
    }

     /* Called when the application resumes */
    @Override
    public void onResume()
    {
       super.onResume();
       //telephonyManager.listen(mpsl,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
      //new GetFlight_info_after_given_interval().execute();
    }

    
    
    
    public void NetworkCheck(){
		boolean isNetworkAvailable = isNetworkAvailable();
		if (isNetworkAvailable == true) {
			
		} else {
			Intent intent_main = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent_main);
			//replaceContentView("NoNetwork", intent_main);
		}
	}
    
    private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

 

    
    
    
    
    public void dialogbox() {

		final Dialog dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.transprentdialog);
		dialog.setCancelable(false);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.show();
		btnSkip = (Button) dialog.findViewById(R.id.btnSkip);
		btnAppTour = (Button) dialog.findViewById(R.id.btnAppTour);
		btnAppTour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				
			}
		});
		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
    } 
}
