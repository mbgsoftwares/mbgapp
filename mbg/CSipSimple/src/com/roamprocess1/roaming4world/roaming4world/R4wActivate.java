package com.roamprocess1.roaming4world.roaming4world;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;

public class R4wActivate extends SherlockFragment {

	public String result, s_value = "", s_expiry = "", daysValidity = "",cndest="",ccdest="",cndestS="",
			activatedOn = "", expiryDate = "",overlayScreenValueR4wActivation;
	private Button btnDeactivate, btnChnageNumber,btnAppTour, btnSkip;
	private TextView txtVTopUpValue, txtVDays, txtVHours, txtVBalance,txtVdaysText, txtVHoursText, txtVTimeLeft, txtVSourceCountryName,txtVSourceTemp, txtVDestiCountryName, txtVDestiTemp, txtVPhoneReg,
			txtVFwdPhoneNo;
	private Typeface BebasNeue;
	private Cursor getCountryNamebyContryCode, data_cc_name1,getAllCountryCode, dataCheckPin;
	private static final String TAG = "R4wMapService";
	private String getPin, getCountryCode, s_fetch_self_no, mapNumber,countryName, country_nam = "", errorPayment = "";
	static int service_count = 0, fetch_flag;
	boolean unknownhost = false, serviceavail = false;
	private LocationManager locationManager;
	private Location location;
	private TelephonyManager telephonyManager;
	TextView txtVChooseYour, txtVCountry, txtVEnterDesti, txtVDestiPhone;
	private ImageView flag, flag1;
	private ArrayList arrayList;
	JSONObject proofOfPayment;
	private EditText edtMapCountryCode, edtMapPhoneNo;
	private Spinner spnSelectCountry;
	private Typeface fontBold, fontNormal;
	private SQLiteAdapter mySQLiteAdapter;
	private CustomAdapter countryAdapter;
	float float_value, temperatureHome;
	int validityHours;
	int validityDays;
	String stored_user_map_no,stored_user_Desti_Country;
	SharedPreferences prefs;
	ImageView imgDestFlag;
	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.r4wactivate, container, false);
		CurrentFragment.name="Home";
		CurrentFragment.Activate="Activate";
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		
		/*Screen overlay check here start*/
		
		overlayScreenValueR4wActivation = "com.roamprocess1.roaming4world.overlayScreenValueR4wActivation";
		int getOverlayScreenValue = prefs.getInt(overlayScreenValueR4wActivation,555 );
		Log.d("getOverlayScreenValue  in side the R4wActionFWD", getOverlayScreenValue+"");
		
		if( getOverlayScreenValue==0||getOverlayScreenValue==555)
		{
			prefs.edit().putInt(overlayScreenValueR4wActivation, 1).commit();
			//dialogbox();	
			
		}
		/*Screen overlay check End*/
		
		stored_user_map_no = "com.roamprocess1.roaming4world.map_mobile_no";
		Log.d("destination country in pref ", ccdest);
		stored_user_Desti_Country="com.roamprocess1.roaming4world.map_Destination_Country";
		
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		btnDeactivate = (Button) rootView.findViewById(R.id.btnDeActivate);
		btnChnageNumber = (Button) rootView.findViewById(R.id.btnChnageNumber);
		/* Set Value in Text Views */

		txtVTopUpValue = (TextView) rootView.findViewById(R.id.txtVTopUpValue);
		txtVDays = (TextView) rootView.findViewById(R.id.txtVdays);
		txtVHours = (TextView) rootView.findViewById(R.id.txtVHours);

		txtVSourceCountryName = (TextView) rootView
				.findViewById(R.id.txtVSourceCountryName);
		//txtVSourceTemp = (TextView) rootView.findViewById(R.id.txtVSourceTemp);

		txtVDestiCountryName = (TextView) rootView
				.findViewById(R.id.txtVDestiCountryName);
		txtVDestiTemp = (TextView) rootView.findViewById(R.id.txtVDestiTemp);

		txtVPhoneReg = (TextView) rootView.findViewById(R.id.txtVPhoneReg);
		txtVFwdPhoneNo = (TextView) rootView.findViewById(R.id.txtVFwdPhoneNo);

		/* End set value */

		txtVdaysText = (TextView) rootView.findViewById(R.id.txtVdaysText);

		txtVHoursText = (TextView) rootView.findViewById(R.id.txtVHoursText);

		txtVBalance = (TextView) rootView.findViewById(R.id.txtVBalance);
		txtVTimeLeft = (TextView) rootView.findViewById(R.id.txtVTimeLeft);
		
		
		 imgDestFlag = (ImageView) rootView.findViewById(R.id.imgDistiFlag);

		mySQLiteAdapter = new SQLiteAdapter(getActivity());
		mySQLiteAdapter.openToRead();

		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getActivity().getSystemService(context);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestSingleUpdate(
					LocationManager.NETWORK_PROVIDER, locationListener,
					Looper.getMainLooper());
		}

		dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		dataCheckPin.moveToFirst();
		while (!dataCheckPin.isAfterLast()) {
			fetch_flag = dataCheckPin.getInt(dataCheckPin
					.getColumnIndex(SQLiteAdapter.FN_FLAG));
			if (fetch_flag == 0) {
				//Intent intent_MainActivity = new Intent(getActivity(),MainActivityGUI.class);
				//startActivity(intent_MainActivity);
				// replaceContentView("MainActivity", intent_MainActivity);
				// finish();
			} else {
				Log.d("INSIDE FETCH PIN", "1");
				getPin = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_PIN_NO));
				getCountryCode = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_COUNTRY_CODE));
				s_fetch_self_no = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_SELF_NO));

				
				new MyAsyncTaskMapNoGet().execute();
				new MyAsyncTaskMapCountryDest().execute();
				new MyAsyncTaskWeatherHome().execute();
			
				
				
			}
			dataCheckPin.moveToNext();
		}

		getCountryNamebyContryCode = mySQLiteAdapter
				.fetch_country_name(getCountryCode);
		getCountryNamebyContryCode.moveToFirst();

		while (!getCountryNamebyContryCode.isAfterLast()) {
			countryName = getCountryNamebyContryCode
					.getString(getCountryNamebyContryCode
							.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			getCountryNamebyContryCode.moveToNext();
			Log.d("Country Names",countryName);
		}

		ImageView imgSourceFlag = (ImageView) rootView
				.findViewById(R.id.imgSourceFlag);
		int id = getResources().getIdentifier(countryName.toLowerCase() + "copy" ,
				"drawable", "com.roamprocess1.roaming4world");
		imgSourceFlag.setImageDrawable(getResources().getDrawable(id));
		// countryName=countryName.replaceAll("\\s", "");
		// String uri = "@drawable/"+countryName.toLowerCase();
		mySQLiteAdapter.close();

		setFont();
		btnDeactivate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			
				Intent intentDeactivate = new Intent(getActivity(),R4wDeactivate.class);
				startActivity(intentDeactivate);
				getActivity().finish();
			}
		});

		btnChnageNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, new R4wMapService()).commit();
		    }
		});
	return rootView;
	}

	private class MyAsyncTaskMapNoGet extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog3;

		@Override
		protected void onPostExecute(Boolean result) {
			mProgressDialog3.dismiss();
			mProgressDialog3.dismiss();

			if (unknownhost) {
				unknownhost = false;
				Log.d(TAG, "UnknownHost");

				Intent intent_NoNetWork = new Intent(getActivity(),
						NoNetwork.class);
				startActivity(intent_NoNetWork);
			
			} else {
				Log.d("update_mao_text", "starting");
				update_map_texts();
	
			}

			mProgressDialog3.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog3 = ProgressDialog.show(getActivity(),
					getResources().getString(R.string.loading), getResources()
							.getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqMAPNOGET()) {
				Log.d(TAG, "doInBackground");
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean webservreqMAPNOGET() {
		try {

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);

			double lat = 0;
			double lng = 0;
			if (location != null) {
				lat = location.getLatitude();
				lng = location.getLongitude();

			}

			String lng1, lat1;

			lat1 = String.valueOf(lat);
			lng1 = String.valueOf(lng);

			String url = "url";
			HttpPost httppost = new HttpPost(url);

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);

				JSONObject json = new JSONObject(responseBody);
				Log.d("response ",responseBody);
				JSONArray jArray = json.getJSONArray("responses");

				for (int i = 0; i < jArray.length(); i++) {

					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject e = jArray.getJSONObject(i);

					String s = e.getString("response");
					JSONObject jObject = new JSONObject(s);
					mapNumber = jObject.getString("map_no");

					JSONObject e1 = jArray.getJSONObject(i + 1);
					String s1 = e1.getString("info");
					JSONObject jObject1 = new JSONObject(s1);

					float int_value = Integer.parseInt(jObject1
							.getString("sum_value"));
					float_value = int_value / 100;
					if(float_value<0)
					{
						float_value=0;	
					}
					
					Intent intent = new Intent(getActivity(),MyBroadcastReceiver.class);
					s_value = "$ " + String.format("%.2f", float_value);
					s_expiry = jObject1.getString("remaining");
					String[] splits = s_expiry.split(":");
					s_expiry=splits[0];
					validityHours=Integer.parseInt(s_expiry);
					validityDays=validityHours/24;
					
					validityHours=validityHours-(validityDays*24);
					Log.d("remaining  data from server", s_expiry);
				}
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				unknownhost = true;
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			
			t.printStackTrace();
			return false;
		}
	}

	private class MyAsyncTaskWeatherHome extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog4;

		@Override
		protected void onPostExecute(Boolean result) {
			mProgressDialog4.dismiss();
			mProgressDialog4.dismiss();

			if (unknownhost) {
				unknownhost = false;
				Log.d(TAG, "UnknownHost");

				Intent intent_NoNetWork = new Intent(getActivity(),
						NoNetwork.class);
				startActivity(intent_NoNetWork);
				// replaceContentView("NoNetWork", intent_NoNetWork);

			} else {
				updateWeatherInfo();
				//new MyAsyncTaskMapCountryDest().execute();
			}

			mProgressDialog4.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog4 = ProgressDialog.show(getActivity(),
					getResources().getString(R.string.loading), getResources()
							.getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqWeatherHome()) {
				Log.d(TAG, "doInBackground");
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean webservreqWeatherHome() {
		try {
			Log.d("Weather Service", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);

			double lat = 0;
			double lon = 0;
			if (location != null) {
				lat = location.getLatitude();
				lon = location.getLongitude();
			}

			String lng1, lat1;

			lat1 = String.valueOf(lat);
			lng1 = String.valueOf(lon);

			String url = "url"";
			Log.d("Weather URL", url);
			HttpPost httppost = new HttpPost(url);

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);

				JSONObject json = new JSONObject(responseBody);
				JSONObject weatherMainObj = json.getJSONObject("main");

				temperatureHome = Float.parseFloat(weatherMainObj
						.getString("temp"));
				temperatureHome -= 273.15;
				temperatureHome = Math.round(temperatureHome);
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				unknownhost = true;
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			
			t.printStackTrace();
			return false;
		}
	}

	public void updateWeatherInfo() {
		txtVDestiTemp.setText(temperatureHome + " C");
	}

	
	public void updatInfoDest() {

		try{
			int id = getResources().getIdentifier(cndestS.toLowerCase() + "copy","drawable", "com.roamprocess1.roaming4world");
			imgDestFlag.setImageDrawable(getResources().getDrawable(id));
			txtVDestiCountryName.setText(cndest);
		}
		catch(Exception e){
			
		}
		
	
		
		mapNumber = mapNumber.replaceFirst("^0*", "");
		mapNumber = mapNumber.replaceFirst(ccdest, "");
		
		Log.d("map number in activate ", mapNumber);
		
		
		txtVFwdPhoneNo.setText(mapNumber);
		if(!mapNumber.equals(null))
			prefs.edit().putString(stored_user_map_no, mapNumber).commit();	
			prefs.edit().putString(stored_user_Desti_Country, cndest).commit();	
	}
	
 class MyAsyncTaskMapCountryDest extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog4;

		@Override
		protected void onPostExecute(Boolean result) {
			mProgressDialog4.dismiss();
			mProgressDialog4.dismiss();

			if (unknownhost) {
				unknownhost = false;
			Intent intent_NoNetWork = new Intent(getActivity(),NoNetwork.class);
				startActivity(intent_NoNetWork);
				
			} 
			else {
				updatInfoDest();
			}

			mProgressDialog4.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog4 = ProgressDialog.show(getActivity(),
					getResources().getString(R.string.loading), getResources()
							.getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqMapDest()) {
				Log.d(TAG, "doInBackground");
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean webservreqMapDest() {
		try {
			Log.d("Weather Service", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);

			double lat = 0;
			double lon = 0;
			if (location != null) {
				lat = location.getLatitude();
				lon = location.getLongitude();
			}

			String lng1, lat1;

			lat1 = String.valueOf(lat);
			lng1 = String.valueOf(lon);

			String url = "url"";
			
			HttpPost httppost = new HttpPost(url);

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);

				JSONObject json = new JSONObject(responseBody);
				Log.d("service response",responseBody);
				JSONObject destObj = json.getJSONObject("response");
				cndest=destObj.getString("cndest");
				ccdest=destObj.getString("ccdest");
				Log.d("service response","cc : "+ccdest+" cn : "+cndest);
				Log.d("service response country",cndest);
				
				
				cndestS= cndest.toLowerCase();
				cndestS	=cndestS.replaceAll("\\s+","");
				
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				unknownhost = true;
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			t.printStackTrace();
			return false;
		}
	}

	public void updateDestCountry() {
		txtVDestiCountryName.setText(cndest);
		
		
	}

	@SuppressLint("NewApi") public void update_map_texts() {
		Log.d(TAG, "update_map_texts()");
		if (mapNumber.equals(null)) {

			Log.d(TAG, "update_map_texts(1)");
			
			/*
			 * tv_mapped_no.setText(getResources().getString(R.string.not_mapped)
			 * ); tv_mappingtext.setText(getResources().getString(R.string.
			 * enter_travel_destin));
			 */

		} else if (mapNumber.isEmpty()) {
			
			Log.d(TAG, "update_map_texts(2)");
			/*
			 * tv_mapped_no.setText(getResources().getString(R.string.not_mapped)
			 * ); tv_mappingtext.setText(getResources().getString(R.string.
			 * enter_travel_destin));
			 */
		} else if (mapNumber == null) {

			
			Log.d(TAG, "update_map_texts(3)");
			/*
			 * tv_mapped_no.setText(getResources().getString(R.string.not_mapped)
			 * ); tv_mappingtext.setText(getResources().getString(R.string.
			 * enter_travel_destin));
			 */
		} else if (mapNumber.equals("null")) {
			
			
			Log.d(TAG, "update_map_texts(4)");
			/*
			 * tv_mapped_no.setText(getResources().getString(R.string.not_mapped)
			 * ); tv_mappingtext.setText(getResources().getString(R.string.
			 * enter_travel_destin));
			 */

		} else {
			Log.d(TAG, "update_map_texts(5)");
			txtVTopUpValue.setText("$" + " "+ String.format("%.2f", float_value));
			txtVDays.setText(validityDays+"");
			txtVHours.setText(validityHours+"");
			
			txtVSourceCountryName.setText(countryName);
			txtVPhoneReg.setText(s_fetch_self_no);
			
		}

		if ((mapNumber == "expired") || (mapNumber.equalsIgnoreCase("expired"))) {

			boolean isNetworkAvailable = isNetworkAvailable();
			if (isNetworkAvailable == true) {
				Log.d(TAG, "isNetworkAvailable = true");
			} else {
				Intent intent_NoNetwork = new Intent(getActivity(),
						NoNetwork.class);
				// replaceContentView("NoNetWork", intent_NoNetwork);
				startActivity(intent_NoNetwork);

			}

			AlertDialog.Builder alertDialogBuilder_deact_exp = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder_deact_exp.setTitle(getResources().getString(
					R.string.alert));
			alertDialogBuilder_deact_exp
					.setMessage(getResources().getString(R.string.pin_expired))
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									LocationManager locationManage;
									String context = Context.LOCATION_SERVICE;
									locationManage = (LocationManager) getActivity()
											.getSystemService(context);
									boolean isGPSEnabled = locationManage
											.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
									if (isGPSEnabled == true) {
										start_get_location();
										Intent intent_deactivate = new Intent(
												getActivity(),
												R4wDeactivate.class);
										startActivity(intent_deactivate);
										getActivity().finish();
										// new MyAsyncTaskDeact().execute();
									} else {

										AlertDialog.Builder alertDialogBuilder_gps = new AlertDialog.Builder(
												getActivity());
										alertDialogBuilder_gps
												.setTitle(getResources()
														.getString(
																R.string.no_sim_network));
										alertDialogBuilder_gps
												.setMessage(
														getResources()
																.getString(
																		R.string.sim_network_not_registered))
												.setCancelable(false)
												.setPositiveButton(
														getResources()
																.getString(
																		R.string.ok),
														new DialogInterface.OnClickListener() {
															public void onClick(
																	DialogInterface dialog,
																	int id) {
																dialog.cancel();
															}
														});
										AlertDialog alertDialog_gps = alertDialogBuilder_gps.create();
										alertDialog_gps.show();
									}
								}
							});
			AlertDialog alertDialog_deact_exp = alertDialogBuilder_deact_exp
					.create();
			alertDialog_deact_exp.show();
		} else {

		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@SuppressLint("NewApi") public void start_get_location() {
		locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
				locationListener, Looper.getMainLooper());
	}

	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location1) {
			// Called when a new location is found by the network location
			// provider.
			location = location1;
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

	};

	void setFont() {
		BebasNeue = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/BebasNeue.otf");
		txtVTopUpValue.setTypeface(BebasNeue);

		txtVDays.setTypeface(BebasNeue);
		txtVdaysText.setTypeface(BebasNeue);
		txtVHours.setTypeface(BebasNeue);
		txtVHoursText.setTypeface(BebasNeue);

		txtVBalance.setTypeface(BebasNeue);
		txtVTimeLeft.setTypeface(BebasNeue);
		txtVSourceCountryName.setTypeface(BebasNeue);
		//txtVSourceTemp.setTypeface(BebasNeue);
		txtVDestiCountryName.setTypeface(BebasNeue);

		txtVDestiTemp.setTypeface(BebasNeue);
		txtVPhoneReg.setTypeface(BebasNeue);
		txtVFwdPhoneNo.setTypeface(BebasNeue);
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
