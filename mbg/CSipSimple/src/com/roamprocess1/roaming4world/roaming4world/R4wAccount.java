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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;

public class R4wAccount extends SherlockFragment {
	public String result, s_value = "", s_expiry = "", daysValidity = "",
			stored_user_map_no, map_no_from_pref, activatedOn = "",
			expiryDate = "", s_activ_on, cndest = "", ccdest = "",
			cndestS = "";
	private Button btnDeactivate, btnChnageNumber;
	private TextView txtVTopUpValue, txtVDays, txtVHours, txtVBalance,
			txtVdaysText, txtVHoursText, txtVTimeLeft, txtVSourceCountryName,
			txtVSourceTemp, txtVDestiCountryName, txtVDestiTemp, txtVPhoneReg,
			txtVFwdPhoneNo;

	private Cursor getCountryNamebyContryCode, data_cc_name1,
			getAllCountryCode, dataCheckPin;
	private static final String TAG = "R4wMapService";
	private String getPin, getCountryCode, s_fetch_self_no, mapNumber,
			countryName, country_nam = "", errorPayment = "";
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

	private SQLiteAdapter mySQLiteAdapter;
	private CustomAdapter countryAdapter;
	float float_value, temperatureHome;
	SharedPreferences prefs;
	int validityHours,validityDays,accountPage,callRecords,reRunCode;
	TextView tvAcDetPIN,tvAcDetSourceCountry,tvAcDetSourcePhone,tvAcDetMapCountry,tvAcDetMapPhone,tvAcDetActOn,
	tvAcDetValLeft,tvAcDetExpOn,tvAcDetCurrentBalance,txtVPhoneNumberMappedCurrently,
	txtVCurrentyMapCountry;
	View rootView;

	
	private Typeface nexaBold, nexaNormal,BebasNeue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.r4waccount, container, false);
		
		//android.app.ActionBar actionBar = getActionBar();
		//actionBar.setDisplayShowCustomEnabled(true);
		//actionBar.setDisplayShowHomeEnabled(false);
		//actionBar.setDisplayUseLogoEnabled(false);
		//actionBar.setDisplayShowTitleEnabled(false);

		//View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		//ImageButton imgbackbtn = (ImageButton) customView.findViewById(R.id.imgbackbtn);
		//imgbackbtn.setClickable(false);
		//imgbackbtn.setVisibility(View.INVISIBLE);
		//actionBar.setCustomView(customView);
		//setContentView(R.layout.r4waccount);

		nexaBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/NexaBold.otf");
		nexaNormal = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaLight.otf");
		BebasNeue=Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue.otf");
		
		
		tvAcDetPIN = (TextView) rootView.findViewById(R.id.tvAcDetPIN);
		tvAcDetPIN.setTypeface(nexaNormal);
		tvAcDetSourceCountry = (TextView) rootView.findViewById(R.id.tvAcDetSourceCountry);
		tvAcDetSourceCountry.setTypeface(nexaNormal);
		tvAcDetSourcePhone = (TextView) rootView.findViewById(R.id.tvAcDetSourcePhone);
		tvAcDetSourcePhone.setTypeface(nexaNormal);

		tvAcDetActOn = (TextView) rootView.findViewById(R.id.tvAcDetActOn);
		tvAcDetActOn.setTypeface(nexaNormal);
		
		tvAcDetValLeft = (TextView) rootView.findViewById(R.id.tvAcDetValLeft);
		tvAcDetValLeft.setTypeface(nexaNormal);
		tvAcDetExpOn = (TextView) rootView.findViewById(R.id.tvAcDetExpOn);
		tvAcDetExpOn.setTypeface(nexaNormal);
		tvAcDetCurrentBalance = (TextView) rootView.findViewById(R.id.tvAcDetCurrentBalance);
		tvAcDetCurrentBalance.setTypeface(nexaNormal);
		txtVPhoneNumberMappedCurrently = (TextView) rootView.findViewById(R.id.txtVPhoneNumberMappedCurrently);
		txtVPhoneNumberMappedCurrently.setTypeface(nexaNormal);
		txtVCurrentyMapCountry = (TextView) rootView.findViewById(R.id.txtVCurrentyMapCountry);
		txtVCurrentyMapCountry.setTypeface(nexaNormal);

		setFont();
		
		//Bundle mapIntent = getActivity().getIntent().getExtras();
		//accountPage=mapIntent.getInt("backPageAccount");
		//callRecords=mapIntent.getInt("backPageCallRecord");
		//reRunCode=mapIntent.getInt("backPageReRunCode");
		
		Log.d("home callRecords  reRunCode", accountPage+" "+callRecords+""+reRunCode+"");

		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_user_map_no = "com.roamprocess1.roaming4world.map_mobile_no";
		map_no_from_pref = prefs.getString(stored_user_map_no, "No Number");
		map_no_from_pref = map_no_from_pref.replaceFirst("^0*", "");
	
		Log.d("map number in Accout ", map_no_from_pref);
		txtVPhoneNumberMappedCurrently.setText(map_no_from_pref);

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
				Intent intent_MainActivity = new Intent(getActivity(),
						R4wActivation.class);
				startActivity(intent_MainActivity);
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

		}

		tvAcDetPIN.setText(getPin);
		tvAcDetSourceCountry.setText(countryName);
		tvAcDetSourcePhone.setText(s_fetch_self_no);
		mySQLiteAdapter.close();
		
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

				Intent intent_NoNetWork = new Intent(getActivity(),NoNetwork.class);
				startActivity(intent_NoNetWork);

			} else {
				Log.d("update_mao_text", "starting");
				// update_map_texts();

				tvAcDetActOn.setText(activatedOn);
				tvAcDetValLeft.setTypeface(nexaNormal);
				tvAcDetValLeft.setText(validityDays + " Days " + validityHours
						+ " Hours");
				tvAcDetExpOn.setText(s_expiry);
				tvAcDetCurrentBalance.setText(s_value);
			}

			mProgressDialog3.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog3 = ProgressDialog.show(getActivity(),getResources().getString(R.string.loading), getResources()
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

			String url = "http://ip.roaming4world.com/esstel/"
					+ "receive.php?pinno=" + getPin + "&lat=" + lat1 + "&lng="
					+ lng1;
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
				Log.d("response ", responseBody);
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

					Intent intent = new Intent(getActivity(), MyBroadcastReceiver.class);
					/*
					 * Temporary Comment PendingIntent pendingIntent =
					 * PendingIntent.getBroadcast(this, 0,
					 * intent,PendingIntent.FLAG_UPDATE_CURRENT); AlarmManager
					 * alarmManager = (AlarmManager)
					 * getSystemService(ALARM_SERVICE); long recurring = (30 *
					 * 60000); alarmManager.setRepeating(AlarmManager.RTC,
					 * Calendar.getInstance().getTimeInMillis(),
					 * recurring,pendingIntent); Temporary Comment
					 */

					//

					s_value = "$ " + String.format("%.2f", float_value);

					s_expiry = jObject1.getString("expiry_date");
					s_expiry = s_expiry.substring(0, s_expiry.length() - 3);
					Log.d("s_expiry ===", s_expiry);

					daysValidity = jObject1.getString("sum_validity");
					Log.d("daysValidity ===", daysValidity);

					activatedOn = jObject1.getString("activated_on");
					Log.d("activatedOn ===", activatedOn);

					expiryDate = jObject1.getString("expiry_date");
					Log.d("expiryDate ===", expiryDate);

					String[] splits = jObject1.getString("remaining")
							.split(":");

					String s_expiry_left = splits[0];
					validityHours = Integer.parseInt(s_expiry_left);
					validityDays = validityHours / 24;

					validityHours = validityHours - (validityDays * 24);

					s_activ_on = jObject1.getString("activated_on");

					s_activ_on = s_activ_on.substring(0,
							s_activ_on.length() - 3);

					s_expiry = jObject1.getString("expiry_date");
					s_expiry = s_expiry.substring(0, s_expiry.length() - 3);

					Log.d("Info Str - validity",
							jObject1.getString("sum_validity"));

					Log.d("Info Str - value", jObject1.getString("sum_value"));
					Log.d("Info Str - activated on",
							jObject1.getString("activated_on"));
					Log.d("Info Str - expiry date",
							jObject1.getString("expiry_date"));

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
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			t.printStackTrace();
			return false;
		}
	}

	class MyAsyncTaskMapCountryDest extends AsyncTask<Void, Void, Boolean> {
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

			} else {

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

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/receivemap.php?"
					+ "pinno=" + getPin;

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
				Log.d("service response", responseBody);
				JSONObject destObj = json.getJSONObject("response");
				cndest = destObj.getString("cndest");
				ccdest = destObj.getString("ccdest");
				Log.d("service response", "cc : " + ccdest + " cn : " + cndest);
				Log.d("service response country", cndest);

				cndestS = cndest.toLowerCase();
				cndestS = cndestS.replaceAll("\\s+", "");

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

	public void updatInfoDest() {

		txtVCurrentyMapCountry.setText(cndest);
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
	


	public void setFont() {
		TextView txtVAccountDetails=(TextView) rootView.findViewById(R.id.txtVAccountDetails);
		txtVAccountDetails.setTypeface(BebasNeue);
		TextView textVYourPin = (TextView) rootView.findViewById(R.id.textVYourPin);
		textVYourPin.setTypeface(nexaBold);
		TextView txtYourCountry = (TextView) rootView.findViewById(R.id.txtYourCountry);
		txtYourCountry.setTypeface(nexaBold);
		TextView txtyourPhoneno = (TextView) rootView.findViewById(R.id.txtyourPhoneno);
		txtyourPhoneno.setTypeface(nexaBold);
		TextView txtVCurrentyMCountry = (TextView) rootView.findViewById(R.id.txtVCurrentyMCountry);
		txtVCurrentyMCountry.setTypeface(nexaBold);
		TextView txtVPhoneNumberMCurrently = (TextView) rootView.findViewById(R.id.txtVPhoneNumberMCurrently);
		txtVPhoneNumberMCurrently.setTypeface(nexaBold);
		TextView txtvActivateOn = (TextView) rootView.findViewById(R.id.txtvActivateOn);
		txtvActivateOn.setTypeface(nexaBold);
		TextView tvAcDetActOn = (TextView) rootView.findViewById(R.id.tvAcDetActOn);
		tvAcDetActOn.setTypeface(nexaBold);
		TextView txtValidityLefft = (TextView) rootView.findViewById(R.id.txtValidityLefft);
		txtValidityLefft.setTypeface(nexaBold);
		TextView txtVExpiringOn = (TextView) rootView.findViewById(R.id.txtVExpiringOn);
		txtVExpiringOn.setTypeface(nexaBold);
		TextView txtCurrentBalance = (TextView) rootView.findViewById(R.id.txtCurrentBalance);
		txtCurrentBalance.setTypeface(nexaBold);
	}
	
	/*
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if(accountPage==1||callRecords==2||reRunCode==3)
		{
			Intent intent = new Intent(getActivity(), R4wMapGUI.class);
			startActivity(intent);
			
		}else{
		
		Intent intent = new Intent(this, R4wHome.class);
		startActivity(intent);
		}
		finish();
	}
	*/
}
