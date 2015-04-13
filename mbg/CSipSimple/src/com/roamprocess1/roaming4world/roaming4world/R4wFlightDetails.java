package com.roamprocess1.roaming4world.roaming4world;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
public class R4wFlightDetails extends SherlockFragment {

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

		rootView = inflater.inflate(R.layout.r4w_activatefwd, container, false);
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
		
		btnedit_flight_details=(Button) rootView.findViewById(R.id.edit_flight_details);
		btnrefresh_flight_details=(Button) rootView.findViewById(R.id.refresh_flight_details);
		
		lleditflightinfo=(LinearLayout) rootView.findViewById(R.id.editflightinfo_layout);
		refresh_butt=(LinearLayout) rootView.findViewById(R.id.refresh_layout);
		
		lleditflightinfo.setVisibility(LinearLayout.GONE);
		refresh_butt.setVisibility(LinearLayout.GONE);
		
		
		get_info_layout=(LinearLayout) rootView.findViewById(R.id.get_info_layout);
		btnget_info_prompt=(Button) rootView.findViewById(R.id.get_info_prompt);
		btnActivatePin= (Button) rootView.findViewById(R.id.btnActivateFwd);
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
				
			} else {
				System.out.println("fetch_flag !=else");
				TextView textsimMessage= (TextView) rootView.findViewById(R.id.tvhomeNetworkWrng);
				textsimMessage.setVisibility(View.VISIBLE);
				LinearLayout linearLayoutActivation= (LinearLayout) rootView.findViewById(R.id.layoutBtnActivation);
				linearLayoutActivation.setVisibility(View.VISIBLE);
				btnActivatePin.setVisibility(Button.VISIBLE);
			}
			dataCheckPin.moveToNext();
		}
		mySQLiteAdapter.close();
		//dialogbox();
		
		btnActivatePin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				click_method(v);
				
			}
		});
		
		btnget_info_prompt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				get_info_prompt(arg0);
			}
		});
	
			btnrefresh_flight_details.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					refresh_flight_details();
				}
			});
			
			btnedit_flight_details.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					edit_flight_details();
				}
			});
		
		
		if(CurrentFragment.flightsearchCheck == 2){
			LinearLayout get_info_layout=(LinearLayout) rootView.findViewById(R.id.get_info_layout);
			//get_info_layout.setVisibility(LinearLayout.VISIBLE);
			get_info_layout.setVisibility(LinearLayout.VISIBLE);
			}
		
		
		if(get_info_status_value.equals("0"))
		{
			Log.d("get_info_status_value", get_info_status_value+"");
			getFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new R4WFlightSearch()).commit();
	    	
			
		
		}
		
		
		else if(get_info_status_value.equals("1"))
		{
			
		
			Log.d("get_info_status_value", get_info_status_value+"");
			flight_no_search_query=prefs.getString(shared_pref_flight_number_search_query, "no");
	    	flight_dep_date=prefs.getString(shared_pref_dep_date, "no");
	    	Carrier_code=prefs.getString(shared_pref_carrier_code, "no");
	    	destination_flight_no=prefs.getString(shared_pref_flight_id, "no");
			new MyAsyncTaskMapFlightInfo().execute();
			
			refresh_butt.setVisibility(LinearLayout.VISIBLE);
			lleditflightinfo.setVisibility(LinearLayout.VISIBLE);
			
			//btnrefresh_flight_details.setVisibility(View.GONE);
			get_info_layout=(LinearLayout) rootView.findViewById(R.id.get_info_layout);
			get_info_layout.setVisibility(LinearLayout.GONE);
		//}
		
		
		
	
		//String check_first_time = getActivity().getIntent().getExtras().getString("FIRST_TIME_SCREEN2_OPENS");
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

	protected void showCustomDialog(String s) {
		// TODO Auto-generated method stub
			
		dialog = new Dialog(getActivity());
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.r4wflightwindow);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		edtFlightNumber = (EditText) dialog.findViewById(R.id.flight_number);
		edtCarrierCode= (EditText) dialog.findViewById(R.id.carrier_code);
		edtDepYear= (EditText) dialog.findViewById(R.id.dep_year);
		//edtDepMonth= (EditText) dialog.findViewById(R.id.dep_month);
		//edtDepDay= (EditText) dialog.findViewById(R.id.dep_day);
		Button btnFlightInfo = (Button) dialog.findViewById(R.id.btnflight);
		Button btnSkip = (Button) dialog.findViewById(R.id.btnskip);
		
		try {
			
			if(s.equals("edit")){
			btnFlightInfo.setText(s);
			edtFlightNumber.setText(prefs.getString(shared_pref_flight_id, ""));
			edtCarrierCode.setText(prefs.getString(shared_pref_carrier_code, ""));
			edtDepDay.setText(prefs.getString(shared_pref_dep_day, ""));
			edtDepMonth.setText(prefs.getString(shared_pref_dep_month, ""));
			edtDepYear.setText(prefs.getString(shared_pref_dep_year, ""));
			
		}
		}catch (Exception e){
			e.printStackTrace();
			Log.d("Prefs error in geting", "12");
		}
		
		
		btnSkip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// editText.setText(editText.getText().toString());
				if(get_info_status_value.equals("0")){
				LinearLayout get_info_layout=(LinearLayout) rootView.findViewById(R.id.get_info_layout);
				//get_info_layout.setVisibility(LinearLayout.VISIBLE);
				get_info_layout.setVisibility(LinearLayout.VISIBLE);
				}
				dialog.dismiss();
			}
		});

		btnFlightInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				prefs.edit().putString(get_info_show_status, "1").commit();
				// TODO Auto-generated method stub
				// flight information page call by intent
				Carrier_code=edtCarrierCode.getText().toString().toUpperCase();
				prefs.edit().putString(shared_pref_carrier_code,Carrier_code).commit();
				destination_flight_no=edtFlightNumber.getText().toString();
				prefs.edit().putString(shared_pref_flight_id,destination_flight_no).commit();
				flight_no_search_query=edtCarrierCode.getText().toString().toUpperCase()+"/"+edtFlightNumber.getText().toString()+"/dep/";
				
				String temp_dep_year=edtDepYear.getText().toString();
				String temp_dep_month=edtDepMonth.getText().toString();
				String temp_dep_day=edtDepDay.getText().toString();
				
				int temp_dep_month_length=temp_dep_month.length();
				if(temp_dep_month_length==2)
				{
					boolean temp_dep_month_check_first_char_value=temp_dep_month.startsWith("0");
					if(temp_dep_month_check_first_char_value)
					{
						temp_dep_month=temp_dep_month.substring(1);
					}
					
					boolean temp_dep_day_check_first_char_value=temp_dep_day.startsWith("0");
					if(temp_dep_day_check_first_char_value)
					{
						temp_dep_day=temp_dep_day.substring(1);
					}
				}
				flight_dep_date=temp_dep_year+"/"+temp_dep_month+"/"+temp_dep_day;
				
				Log.d("temp_dep_day1", temp_dep_day);
				Log.d("temp_dep_month1", temp_dep_month);
				Log.d("temp_dep_year1", temp_dep_year);
				
				
				prefs.edit().putString(shared_pref_flight_number_search_query, flight_no_search_query).commit();
				prefs.edit().putString(shared_pref_dep_date, flight_dep_date).commit()	;
				
				try {
					
					Log.d("Day Before",temp_dep_day);					
					Log.d("Month Before",temp_dep_month);
					Log.d("Year Before Commit",temp_dep_year);
					prefs.edit().putString(shared_pref_dep_year, temp_dep_year).commit();
					Log.d("Year After Commit",temp_dep_year);
					Log.d("Month Before Commit",temp_dep_month);
					prefs.edit().putString(shared_pref_dep_month, temp_dep_month).commit();
					Log.d("Month After Commit",temp_dep_month);
					Log.d("Day Before Commit",temp_dep_day);
					prefs.edit().putString(shared_pref_dep_day, temp_dep_day).commit();
					Log.d("Day After Commit",temp_dep_day);
					
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("Prefs error in seting", "11");
					
				}
				String check = prefs.getString(shared_pref_dep_day, "");
				Log.d("CHeck pref d", check+" @");
				String check1 = prefs.getString(shared_pref_dep_month, "");
				Log.d("CHeck pref m", check1+" @");
				String check2 = prefs.getString(shared_pref_dep_year, "");
				Log.d("CHeck pref y", check2+" @");
				
				new MyAsyncTaskMapFlightInfo().execute();
				//new GetFlight_info_after_given_interval().execute();
			
				dialog.dismiss();
				
			}
		});

		dialog.show();

	}

	class MyAsyncTaskMapFlightInfo extends AsyncTask<Void, Void, Boolean> {
		
		
		
			//ProgressDialog mProgressDialog4;
		
		
		

		@Override
		protected void onPostExecute(Boolean result) {
			//mProgressDialog4.dismiss();
			//mProgressDialog4.dismiss();

			if (unknownhost) {
				unknownhost = false;
				Intent intent_NoNetWork = new Intent(getActivity(),NoNetwork.class);
				startActivity(intent_NoNetWork);
				// replaceContentView("NoNetWork", intent_NoNetWork);

			} else {
				Log.d("update_mao_text", "starting");
				
				get_info_status_value=prefs.getString(get_info_show_status, "0");
				if(get_info_status_value.equals("1"))
				{
					LinearLayout refresh_butt=(LinearLayout) rootView.findViewById(R.id.refresh_layout);
					refresh_butt.setVisibility(LinearLayout.VISIBLE);
					LinearLayout get_info_layout=(LinearLayout) rootView.findViewById(R.id.get_info_layout);
					get_info_layout.setVisibility(LinearLayout.GONE);
					LinearLayout flight_layout=(LinearLayout) rootView.findViewById(R.id.flight_layout);
					flight_layout.setVisibility(LinearLayout.VISIBLE);
					LinearLayout editflight_layout=(LinearLayout) rootView.findViewById(R.id.editflightinfo_layout);
					editflight_layout.setVisibility(LinearLayout.VISIBLE);
				}
				else
				{
					get_info_layout.setVisibility(LinearLayout.VISIBLE);
					Toast.makeText(getActivity(), "Flight details are wrong",  Toast.LENGTH_LONG).show();
					btnget_info_prompt.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							get_info_prompt(arg0);
						}
					});
				}
				
				
				TextView flight_name_and_id=(TextView) rootView.findViewById(R.id.flight_name_and_id);
				TextView status=(TextView) rootView.findViewById(R.id.status);
				TextView status_details=(TextView) rootView.findViewById(R.id.status_details);
				TextView dep_airport_code=(TextView) rootView.findViewById(R.id.dep_airport_code);
				TextView arrival_airport_code=(TextView) rootView.findViewById(R.id.arrival_airport_code);
				TextView dep_city_name=(TextView) rootView.findViewById(R.id.dep_city_name);
				TextView arrival_city_name=(TextView) rootView.findViewById(R.id.arrival_city_name);
				TextView dep_date=(TextView) rootView.findViewById(R.id.dep_date);
				TextView arrival_date=(TextView) rootView.findViewById(R.id.arrival_date);
				TextView dep_time=(TextView) rootView.findViewById(R.id.dep_time);
				TextView dep_terminal=(TextView) rootView.findViewById(R.id.dep_terminal);
				TextView arrival_time=(TextView) rootView.findViewById(R.id.arrival_time);
				TextView arrival_terminal=(TextView) rootView.findViewById(R.id.arrival_terminal);
				
				flight_name_and_id.setText(flight_full_name+" "+destination_flight_no);
				
				status.setText("ON TIME ");
				status_details.setText("Departs in "+hms );
				dep_airport_code.setText(flightdeparture_airport_code);
				arrival_airport_code.setText(flightarrival_airport_code);
				dep_city_name.setText("Departs "+departure_city+", ");
				arrival_city_name.setText("Arrives "+arrival_city+", ");
				dep_date.setText(dayOfTheWeek_dep+", "+dep_day_name_in_numeric+" "+dep_month_name);
				arrival_date.setText(dayOfTheWeek_arr+", "+arv_day_name_in_numeric+" " +arival_month_name);
				dep_time.setText(departure_time);
				dep_terminal.setText(Departure_terminal);
				arrival_time.setText(R4wFlightDetails.this.arrival_time);
				arrival_terminal.setText(Arrival_terminal);
				}
			//mProgressDialog4.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
			//mProgressDialog4 = ProgressDialog.show(getActivity(),getResources().getString(R.string.loading), getResources().getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webServiceFlightInfo()) {
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else {
				return false;
			}
		}
	}

	@SuppressLint("NewApi") @SuppressWarnings("deprecation")
	public boolean webServiceFlightInfo() {
		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/status/";
			url += flight_no_search_query+flight_dep_date;
			url += "?appId=8b9a9ea9";
			url += "&appKey=c54d8100294cbeca21178e69b2184662&utc=false";
			
			Log.d("Service URL - Flight Info",url);
			
			boolean network_check = isNetworkAvailable();
			HttpGet httpget= new HttpGet(url);
			//HttpPost httppost = new HttpPost(url);

			try {
				ResponseHandler<String> responseHandler;
				String responseBody;
				if (network_check) {
				responseHandler = new BasicResponseHandler();
				responseBody = httpclient.execute(httpget,responseHandler);
				Log.d("Flight Response", responseBody);
				
				}
				else {
					Intent intent = new Intent(getActivity(), NoNetwork.class);
					startActivity(intent);
					//replaceContentView("NoNetwork", intent);
					return false;
				}
				JSONObject json = new JSONObject(responseBody);
				JSONObject flightRequest = json.getJSONObject("request");
				
				JSONObject flightappendix = json.getJSONObject("appendix");
				JSONArray flightappendixAirlinesArray = flightappendix.getJSONArray("airlines");
				
				for(int i=0;i<flightappendixAirlinesArray.length();i++)
				{
					JSONObject flight_appendix_airline_array_first_index=flightappendixAirlinesArray.getJSONObject(i);
					
					if(Carrier_code.equals(flight_appendix_airline_array_first_index.getString("fs")))
					{
					flight_full_name=flight_appendix_airline_array_first_index.getString("name");
					}
				}
				

				JSONArray flightstatuses = json.getJSONArray("flightStatuses");
				
				for(int i=0;i<flightstatuses.length();i++)
				{
					JSONObject flignstatuses_innerobject=flightstatuses.getJSONObject(i);
					flightdeparture_airport_code=flignstatuses_innerobject.getString("departureAirportFsCode");
					flightarrival_airport_code=flignstatuses_innerobject.getString("arrivalAirportFsCode");
				}
				JSONArray flightappendixAirportArray = flightappendix.getJSONArray("airports");
				for(int i=0;i<flightappendixAirportArray.length();i++)
				{
					JSONObject flight_appendix_airport_array_first_index=flightappendixAirportArray.getJSONObject(i);
					String countryfs_code=flight_appendix_airport_array_first_index.getString("fs");
					if(countryfs_code.equals(flightdeparture_airport_code))
					{
						departure_city=flight_appendix_airport_array_first_index.getString("city");
						user_departure_country_local_time=flight_appendix_airport_array_first_index.getString("localTime");
						String[] arraystring_local_departure_country_time=user_departure_country_local_time.split("T");
						user_departure_country_local_day=arraystring_local_departure_country_time[0];
						user_departure_country_local_timing=arraystring_local_departure_country_time[1];
					
					}
					else if(countryfs_code.equals(flightarrival_airport_code))
					{
						arrival_city=flight_appendix_airport_array_first_index.getString("city");
										}
				}
				
				for(int i=0;i<flightstatuses.length();i++)
				{
					JSONObject flignstatuses_innerobject=flightstatuses.getJSONObject(i);
					flightdeparture_airport_code=flignstatuses_innerobject.getString("departureAirportFsCode");
					flightarrival_airport_code=flignstatuses_innerobject.getString("arrivalAirportFsCode");
					JSONObject departure_date_json=flignstatuses_innerobject.getJSONObject("departureDate");
					departure_date=departure_date_json.getString("dateLocal");
					
					String[] stringarray_departure=departure_date.split("T");
					departure_day=stringarray_departure[0];
					departure_time=stringarray_departure[1];
					
					String[] stringarray_split_on_dash=departure_day.split("-");
					
					for(int k=0;k<stringarray_split_on_dash.length;k++)
					{
						//System.out.println("value is after splitting is "+stringarray_split_on_dash[k]);
					}
					dep_month_name_in_numeric=stringarray_split_on_dash[1];
					dep_day_name_in_numeric=stringarray_split_on_dash[2];
					Calendar zCalendarL = Calendar.getInstance(); 
					dep_month_name=zCalendarL.getDisplayName(zCalendarL.get(Integer.parseInt(dep_month_name_in_numeric)), 2, Locale.US);
					JSONObject arrival_date_json=flignstatuses_innerobject.getJSONObject("arrivalDate");
					arrival_date=arrival_date_json.getString("dateLocal");
					String[] stringarray_arrival=arrival_date.split("T");
					arrival_day=stringarray_arrival[0];
					arrival_time=stringarray_arrival[1];
					
					
					
					String[] stringarray_split_on_dash_arrive=arrival_day.split("-");
					
					arv_month_name_in_numeric=stringarray_split_on_dash_arrive[1];
					arv_day_name_in_numeric=stringarray_split_on_dash_arrive[2];
					arival_month_name=zCalendarL.getDisplayName(zCalendarL.get(Integer.parseInt(arv_month_name_in_numeric)), 2, Locale.US);
					SimpleDateFormat zDateFormatL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

					String DateTime_local_departure=departure_day+" "+departure_time;
					String DateTime_local_country_time=user_departure_country_local_day+" "+user_departure_country_local_timing;
					String DateTime_local_arr_country_time=arrival_day+" "+arrival_time;
					
					Date d = zDateFormatL.parse(DateTime_local_departure);
					Date e=  zDateFormatL.parse(DateTime_local_country_time);
					Date f= zDateFormatL.parse(DateTime_local_arr_country_time);
					long diff = d.getTime() - e.getTime();
					
					if(diff<=0)
					{
						hms="Departed";
						System.out.println("after diff<0");
						long diff_bw_arr_and_dep_time=	f.getTime() - d.getTime();
						long mid_time_bw_arr_and_dep=diff_bw_arr_and_dep_time/2;
						long current_time_bw_arr_and_dep=f.getTime()-e.getTime();
						if(mid_time_bw_arr_and_dep==current_time_bw_arr_and_dep)
						{
							ImageView img= (ImageView) rootView.findViewById(R.id.imageView1);
							img.setImageResource(R.drawable.arrow);
						}
						else if(current_time_bw_arr_and_dep<=0)
						{
							ImageView img= (ImageView) rootView.findViewById(R.id.imageView1);
							img.setImageResource(R.drawable.ic_drawer);
						}
					}
					else
					{
				
					hms=String.format("%02d hours %02d minutes %02d seconds", 
							TimeUnit.MILLISECONDS.toHours(diff),
							TimeUnit.MILLISECONDS.toMinutes(diff) -  
							TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diff)), // The change is in this line
							TimeUnit.MILLISECONDS.toSeconds(diff) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diff)));
					}
					
					Log.d("time is ",hms);
					
					SimpleDateFormat zDateFormat_week_day = new SimpleDateFormat("EEEE");
					dayOfTheWeek_dep = zDateFormat_week_day.format(d);
					dayOfTheWeek_arr= zDateFormat_week_day.format(f);
					Log.d("week_day",dayOfTheWeek_dep);
					
					
					 String monthName_departure = new SimpleDateFormat("MMMM").format(d);
					
					 String monthName_arrival = new SimpleDateFormat("MMMM").format(f);
					 dep_month_name=monthName_departure;
					 arival_month_name=monthName_arrival;
					JSONObject flight_codesShares=flignstatuses_innerobject.getJSONObject("airportResources");
					Departure_terminal = flight_codesShares.getString("departureTerminal");
					Arrival_terminal = flight_codesShares.getString("arrivalTerminal");
					JSONObject operational_time=flignstatuses_innerobject.getJSONObject("operationalTimes");
					JSONObject gate_departure=operational_time.getJSONObject("estimatedGateDeparture");
					Estimated_Gate_departure=gate_departure.getString("dateLocal");
					
					Log.d("estimated_gate_departure ",Estimated_Gate_departure);
					
				}
				
				
				//JSONArray flightAppendixAirlinesArray=flightappendixAirlines.getJSONArray("0");
				//Log.d("flightRequest", flightAppendixAirlinesArray.toString());
				JSONObject flightAirline = flightRequest.getJSONObject("airline");
				Log.d("request", flightAirline.toString());
				
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return false;
			}catch(JSONException e)
			{
				e.printStackTrace();
				prefs.edit().putString(get_info_show_status, "0").commit();
				return false;
			}
		} catch (Throwable t) {
			
			t.printStackTrace();
			return false;
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
		case R.id.btnActivateFwd:
			
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
		Toast.makeText(getActivity(), "case SIM_STATE_READY", Toast.LENGTH_LONG).show();
		Log.d("onActivityResult", 0+"");
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

    public void refresh_flight_details()
    {
    	CurrentFragment.flightLoading = true;
    	loading = true;
    	flight_no_search_query=prefs.getString(shared_pref_flight_number_search_query, "no");
    	flight_dep_date=prefs.getString(shared_pref_dep_date, "no");
    	System.out.println("Flight date "+flight_dep_date);
    	
    	Carrier_code=prefs.getString(shared_pref_carrier_code, "no");
    	destination_flight_no=prefs.getString(shared_pref_flight_id, "no");
    	new MyAsyncTaskMapFlightInfo().execute();
   }
   
    public void edit_flight_details()
    {
    	//showCustomDialog("edit");
    	CurrentFragment.flightsearchCheck = 1;
    	getFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new R4WFlightSearch()).commit();
		
    }
    public void get_info_prompt(View v)
    {
    	prefs.edit().remove(shared_pref_carrier_code).commit();
    	prefs.edit().remove(shared_pref_dep_date).commit();
    	prefs.edit().remove(shared_pref_flight_id).commit();
    	prefs.edit().remove(shared_pref_flight_number_search_query).commit();
    	prefs.edit().remove(get_info_show_status).commit();
    	getFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new R4WFlightSearch()).commit();
    	
    }
    
    public class MyAsyncTaskMapFlightInfo_without_dialog extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog4;

		@Override
		protected void onPostExecute(Boolean result) {
			

			if(result)
			{
								
				TextView flight_name_and_id=(TextView) rootView.findViewById(R.id.flight_name_and_id);
				TextView status=(TextView) rootView.findViewById(R.id.status);
				TextView status_details=(TextView) rootView.findViewById(R.id.status_details);
				TextView dep_airport_code=(TextView) rootView.findViewById(R.id.dep_airport_code);
				TextView arrival_airport_code=(TextView) rootView.findViewById(R.id.arrival_airport_code);
				TextView dep_city_name=(TextView) rootView.findViewById(R.id.dep_city_name);
				TextView arrival_city_name=(TextView) rootView.findViewById(R.id.arrival_city_name);
				TextView dep_date=(TextView) rootView.findViewById(R.id.dep_date);
				TextView arrival_date=(TextView) rootView.findViewById(R.id.arrival_date);
				TextView dep_time=(TextView) rootView.findViewById(R.id.dep_time);
				TextView dep_terminal=(TextView) rootView.findViewById(R.id.dep_terminal);
				TextView arrival_time=(TextView) rootView.findViewById(R.id.arrival_time);
				TextView arrival_terminal=(TextView) rootView.findViewById(R.id.arrival_terminal);
				flight_name_and_id.setText(flight_full_name+" "+destination_flight_no);
				status.setText("ON TIME ");
				status_details.setText("Departs in "+hms );
				dep_airport_code.setText(flightdeparture_airport_code);
				arrival_airport_code.setText(flightarrival_airport_code);
				dep_city_name.setText("Departs "+departure_city+", ");
				arrival_city_name.setText("Arrives "+arrival_city+", ");
				dep_date.setText(dayOfTheWeek_dep+", "+dep_day_name_in_numeric+" "+dep_month_name);
				arrival_date.setText(dayOfTheWeek_arr+", "+arv_day_name_in_numeric+" " +arival_month_name);
				dep_time.setText(departure_time);
				dep_terminal.setText(Departure_terminal);
				arrival_time.setText(R4wFlightDetails.this.arrival_time);
				arrival_terminal.setText(Arrival_terminal);
			}	
			
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			System.out.println("in get flight_info before calling web serviceflightinfo");
			if (webServiceFlightInfo()) {
				System.out.println("in get flight_info without dialog after calling webserviceflightinfo ");
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else {
				return false;
			}
		}
	}
    
    class GetFlight_info_after_given_interval extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog4;

		@Override
		protected void onPostExecute(Boolean result) {
			System.out.println("in get flight_info after given interval - post exec");
		}

		@Override
		protected void onPreExecute() {			
			System.out.println("in get flight_info after given interval - pre exec");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try{
				System.out.println("in get flight_info after given interval - dib exec");
				new MyAsyncTaskMapFlightInfo_without_dialog().execute();				
				return true;
			}
			catch(Exception e){
				return false;
			}
		}
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
