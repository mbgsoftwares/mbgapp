package com.roamprocess1.roaming4world.roaming4world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.SipHome;

public class R4wDeactivate extends Activity {

	public String result,signalStrength_str,map_no_from_pref, stored_leftTab,stored_user_map_no,storedDeactivateStatus,map_desti_Country_pref;
	private Cursor getCountryNamebyContryCode,dataCheckPin;
	private static final String TAG = "R4wDeactivate";
	private String getPin, getCountryCode, s_fetch_self_no, stored_tabselection, errorPayment = "",mapNumber="",countryName="",stored_user_Desti_Country,overlayScreenValueDeActivation;
	static int service_count = 0, fetch_flag;
	boolean unknownhost = false, serviceavail = false;
	private LocationManager locationManager;
	private Location location;
	private TelephonyManager telephonyManager;
	JSONObject proofOfPayment;
	private Typeface BebasNeue,NexaBold;
	private SQLiteAdapter mySQLiteAdapter;

	final String encodedHash = Uri.encode("#");
	final String ussd = "21";
	private Button btnDeactivate,btnSkip,btnAppTour;
	SharedPreferences prefs;
	MyPhoneStateListener mpsl;
	ImageButton imgbackbtn;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
		
		android.app.ActionBar actionBar = getActionBar();

		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		
		actionBar.setDisplayShowTitleEnabled(false);
		View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		imgbackbtn=(ImageButton)customView.findViewById(R.id.imgbackbtn);
		//imgbackbtn.setVisibility(View.VISIBLE);
		//imgbackbtn.setClickable(false);
		
		actionBar.setCustomView(customView);
		setContentView(R.layout.r4wdeactivate);
		
		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world",Context.MODE_PRIVATE);
		stored_user_map_no = "com.roamprocess1.roaming4world.map_mobile_no";
		stored_user_Desti_Country="com.roamprocess1.roaming4world.map_Destination_Country";
		stored_tabselection = "com.roamprocess1.roaming4world.tabselection";
        map_no_from_pref = prefs.getString(stored_user_map_no, "No Number");
		map_desti_Country_pref=prefs.getString(stored_user_Desti_Country, "No Country");
		stored_leftTab = "com.roamprocess1.roaming4world.left_tabselection";
        
		Log.d("map no from Home ", map_no_from_pref);
		
		/*Screen overlay check here start*/
		
		overlayScreenValueDeActivation = "com.roamprocess1.roaming4world.overlayScreenValueDeActivation";
		int getOverlayScreenValue = prefs.getInt(overlayScreenValueDeActivation,555 );
		Log.d("getOverlayScreenValue  in side the R4wDeactivation", getOverlayScreenValue+"");
		if( getOverlayScreenValue==0||getOverlayScreenValue==555)
		{
			prefs.edit().putInt(overlayScreenValueDeActivation, 1).commit();
			//dialogbox();	
		}
		/*Screen overlay check End*/
		
		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();

		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener,Looper.getMainLooper());
		}
		btnDeactivate = (Button) findViewById(R.id.btnDeactivate);
		setFont();
		imgbackbtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				//Intent intent_home =  new Intent(R4wDeactivate.this, R4wActivation.class);
				//startActivity(intent_home);
				finish();
			}
		});
		
		
		dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		dataCheckPin.moveToFirst();
		telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(mpsl ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		mpsl = new MyPhoneStateListener();
		
		boolean isNetworkAvailable = isNetworkAvailable();
		if (isNetworkAvailable == true) {
			Log.d(TAG, "isNetworkAvailable = true");
		} else {
			Intent intent_main = new Intent(R4wDeactivate.this, NoNetwork.class);
			startActivity(intent_main);
			finish();
		}
		while (!dataCheckPin.isAfterLast()) {
			fetch_flag = dataCheckPin.getInt(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_FLAG));
			if (fetch_flag == 0) {
				Intent intent_activ = new Intent(R4wDeactivate.this,R4wHome.class);
				startActivity(intent_activ);
				finish();
			} else {
				getPin = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_PIN_NO));
				getCountryCode = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_COUNTRY_CODE));
				s_fetch_self_no = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_SELF_NO));

				new MyAsyncTaskMapNoGet().execute();
			}
			dataCheckPin.moveToNext();
		}
/*
		getCountryNamebyContryCode = mySQLiteAdapter.fetch_country_name(getCountryCode);
		getCountryNamebyContryCode.moveToFirst();

		while (!getCountryNamebyContryCode.isAfterLast()) {
			countryName = getCountryNamebyContryCode.getString(getCountryNamebyContryCode.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			getCountryNamebyContryCode.moveToNext();
		}
		mySQLiteAdapter.close();
		*/
		//TextView tv_deactivate=(TextView) findViewById(R.id.tv_DeactivateText);
		//String text="On Deactivation \n you will start receiving calls \n  on your origin number.\n Remember to have \n your <font color=#f0696e>origin country SIM</font> \n in your phone \n during deactivation.";
		//tv_deactivate.setText(Html.fromHtml(text),TextView.BufferType.SPANNABLE);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void sharedpref(String key, int value) {
		float int_value = value;
		Log.d("int value", String.valueOf(int_value));

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void sharedpref1(String key, String value) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public int shardpref_return() {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		int text = app_preferences.getInt("key", 0);
		return text;
	}

	public String shardpref_return1() {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String text = app_preferences.getString("proof_of_payment", "nothing");
		return text;
	}

	public void infodisp(View v) {
		AlertDialog.Builder alertDialogBuilder_deact_exp = new AlertDialog.Builder(
				this);
		alertDialogBuilder_deact_exp.setTitle(getResources().getString(
				R.string.alert));
		alertDialogBuilder_deact_exp
				.setMessage(getResources().getString(R.string.deactivate_info))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								dialog.cancel();

							}
						});
		AlertDialog alertDialog_deact_exp = alertDialogBuilder_deact_exp
				.create();
		alertDialog_deact_exp.show();

	}

	public void deActivate(View v) {
		switch (v.getId()) {
		case R.id.btnDeactivate:
			boolean isNetworkAvailable = isNetworkAvailable();
			if (isNetworkAvailable == true) {
				Log.d(TAG, "isNetworkAvailable= true");
			} else {
				Intent intent_main_1 = new Intent(R4wDeactivate.this,NoNetwork.class);
				startActivity(intent_main_1);
			}

			AlertDialog.Builder alertDialogBuilder_deact = new AlertDialog.Builder(
					this);
			alertDialogBuilder_deact.setTitle(getResources().getString(
					R.string.alert));
			alertDialogBuilder_deact
					.setMessage(
							getResources().getString(R.string.deactivate_pin))
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									LocationManager locationManage;
									String context = Context.LOCATION_SERVICE;
									locationManage = (LocationManager) getSystemService(context);
									boolean isGPSEnabled = locationManage.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
									if (isGPSEnabled == true) {
										new AsyncTaskDeActivateService().execute();
									} 
									else {
										int sim_state = telephonyManager.getSimState();

										switch (sim_state) {
										case TelephonyManager.SIM_STATE_ABSENT:
											Log.d("country_name","country name not equal to country name sim state absent");
											AlertDialog.Builder alertDialogBuilder_gps = new AlertDialog.Builder(R4wDeactivate.this);
											alertDialogBuilder_gps.setTitle(getResources().getString(R.string.no_sim_network));
											alertDialogBuilder_gps
													.setMessage(getResources().getString(R.string.sim_network_not_registered))
													.setCancelable(false)
													.setPositiveButton(getResources().getString(R.string.ok),
															new DialogInterface.OnClickListener() {
																public void onClick(DialogInterface dialog,int id) {
																	dialog.cancel();
																}
															});
											AlertDialog alertDialog_gps = alertDialogBuilder_gps.create();
											alertDialog_gps.show();
											break;
										case TelephonyManager.SIM_STATE_READY:
											telephonyManager.listen(mpsl ,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
											/*
											if(Integer.parseInt(signalStrength_str)==0||Integer.parseInt(signalStrength_str)==99){
												Toast.makeText(R4wDeactivate.this, "No Network Coverage", Toast.LENGTH_LONG).show();
											}
											else{
											
												String deActivatStatus= prefs.getString(storedDeactivateStatus, "false");
												if (deActivatStatus.equals("1")){
												
													startActivityForResult(new Intent("android.intent.action.CALL",
																	Uri.parse("tel:" + encodedHash + encodedHash
																			+ ussd + encodedHash)), 1);		
											/*	}*/
											/*	else{*/
													new AsyncTaskDeActivateService().execute();	
												//break;
												/*}*/
												
										//	}
											
											
											
											break;
										}

									}

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog_deact = alertDialogBuilder_deact.create();
			alertDialog_deact.show();

			break;

		}
	}

	public boolean webservreqDeact() {
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

			/*
			String url = "http://ip.roaming4world.com/esstel/"
					+ "receive.php?from=" + getCountryCode + s_fetch_self_no
					+ "&message=ETEL%20C%20" + getPin + "&format=json"
					+ "&lat=" + lat1 + "&lng=" + lng1;
			HttpPost httppost = new HttpPost(url);
			
			*/
			/*testing*/
			String url = "http://ip.roaming4world.com/esstel/"
					+ "receive_voicemail.php?from=" + getCountryCode + s_fetch_self_no
					+ "&message=ETEL%20C%20" + getPin + "&format=json"
					+ "&lat=" + lat1 + "&lng=" + lng1;
			HttpPost httppost = new HttpPost(url);
			
			/*testing*/

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);

				return true;
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
			return false;
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
					float float_value = int_value / 100;

					Intent intent = new Intent(this, MyBroadcastReceiver.class);

					PendingIntent pendingIntent = PendingIntent.getBroadcast(
							this.getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
					long recurring = (30 * 60000);
					alarmManager.setRepeating(AlarmManager.RTC, Calendar
							.getInstance().getTimeInMillis(), recurring,
							pendingIntent);

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

	private class AsyncTaskDeActivateService extends AsyncTask<Void, Void, Boolean> {
	
		ProgressDialog mProgressDialog2;

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {
				mProgressDialog2.dismiss();
				mProgressDialog2.dismiss();
				mySQLiteAdapter.openToWrite();
				mySQLiteAdapter.update_flag_notify_expiry_24("Set0");
				mySQLiteAdapter.update_flag_notify_expiry_8("Set0");
				mySQLiteAdapter.update_flag_notify_value_1("Set0");
				mySQLiteAdapter.update_flag_notify_value_5("Set0");
				mySQLiteAdapter.close();
					
				//prefs.edit().putString(storedDeactivateStatus, "1").commit();
				Log.d("DeActivate Value1", prefs.getString(storedDeactivateStatus, "1"));
				imgbackbtn.setVisibility(View.INVISIBLE);
				
				prefs.edit().remove(stored_user_Desti_Country).commit();
				prefs.edit().remove(stored_user_map_no).commit();
				prefs.edit().putLong(stored_tabselection, 0).commit();
				prefs.edit().putBoolean(stored_leftTab, false).commit();
				String	map_no_from_pref1 = prefs.getString(stored_user_map_no, "No Number");
				String	map_destination_pref2 = prefs.getString(stored_user_Desti_Country, "No Country");
			
				Log.d("After  Clear mao no pref value  in background", map_no_from_pref1);
				Log.d("After  Clear destination pref value  in background", map_destination_pref2);
				
				
			
				
				startActivityForResult(
						new Intent("android.intent.action.CALL",
								Uri.parse("tel:" + encodedHash + encodedHash
										+ ussd + encodedHash)), 1);
				mProgressDialog2.dismiss();
			} else {
				mProgressDialog2.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog2 = ProgressDialog.show(R4wDeactivate.this,
					getResources().getString(R.string.loading), getResources()
							.getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqDeact()) {
				Log.d("yay", "SUCCESS");
				return true;
			} else {
				// Toast.makeText(this,
				// "Request failed",Toast.LENGTH_LONG).show();
				Log.d("err", "ERROR");
				return false;
			}
		}
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

				Intent intent = new Intent(R4wDeactivate.this, NoNetwork.class);
				startActivity(intent);

			} else {
				Log.d("update_mao_text", "starting");
				// update_map_texts();

			}

			mProgressDialog3.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog3 = ProgressDialog.show(R4wDeactivate.this,
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		
		case 1:
			mySQLiteAdapter.openToWrite();
			mySQLiteAdapter.update_flag(0);
			mySQLiteAdapter.close();
			
			Intent intent = new Intent(R4wDeactivate.this, SipHome.class);
			startActivity(intent);
			finish();
		}

	}

	public class AsyncTaskMapNoGet extends AsyncTask<Void, Void, Boolean> {
		@Override
		public Boolean doInBackground(Void... params) {
			if (webservreqMAPNOGET1()) {
				Log.d(TAG, "AsyncTaskMapNoGet  SUCCESS");
				return true;
			} else {
				Log.d("err", "ERROR");
				return false;
			}
		}

		public void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			if (result) {
				Toast.makeText(R4wDeactivate.this,
						getResources().getString(R.string.payment_made1),
						Toast.LENGTH_LONG).show();
			} else {
				if (errorPayment != "") {
					new AsyncTaskMapNoGet().execute();

				}
			}
		}
	}

	public boolean webservreqMAPNOGET1() {
		String URL = null;
		if (errorPayment != "") {
			URL = "http://ip.roaming4world.com/esstel/payment.php?email="
					+ result + "&pin=" + getPin.toString() + "&tokenrefresh="
					+ errorPayment;

		} else {
			URL = "http://ip.roaming4world.com/esstel/payment.php?email="
					+ result + "&pin=" + getPin.toString();
			// Log.i(TAG,"not in error_payment");
		}

		boolean network_check = isNetworkAvailable();
		JSONObject jsonObjRecv = null;
		if (network_check) {
			jsonObjRecv = SendHttpPost(URL, proofOfPayment);
		} else {
			Intent intent = new Intent(this, NoNetwork.class);
			startActivity(intent);
		}
		boolean status = false, success = false;

		try {

			success = jsonObjRecv.getBoolean("success");
			errorPayment = jsonObjRecv.getString("error");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity stringEntity = new StringEntity(jsonObjSend.toString());
			httpPostRequest.setEntity(stringEntity);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient
					.execute(httpPostRequest);
			Log.i(TAG,
					"HTTPResponse received in ["
							+ (System.currentTimeMillis() - t) + "ms]");

			HttpEntity entity = response.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				String resultString = convertStreamToString(instream);
				instream.close();
				resultString = resultString.substring(0,
						resultString.length() - 1); // remove wrapping "[" and
													// "]"

				JSONObject jsonObjRecv = new JSONObject(resultString);
				return jsonObjRecv;
			}

		} catch (Exception e) {
			e.printStackTrace();
			String json2 = "{\"success\":\"false\",\"error\":\"noresponse\"}";
			try {
				JSONObject jsonobj = new JSONObject(json2);
				return jsonobj;
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return null;
	}

	private static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (StringIndexOutOfBoundsException e) {

		} finally {

			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (StringIndexOutOfBoundsException e2) {
				// TODO: handle exception

			}
		}
		return sb.toString();
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
		
		BebasNeue = Typeface.createFromAsset(getAssets(),"fonts/BebasNeue.otf");
		NexaBold=Typeface.createFromAsset(getAssets(),"fonts/NexaBold.otf");
		btnDeactivate.setTypeface(NexaBold);
		
		TextView tv_DeactivateTextLine1=(TextView)findViewById(R.id.tv_DeactivateTextLine1);
		tv_DeactivateTextLine1.setTypeface(BebasNeue);
		TextView tv_DeactivateTextLine2=(TextView)findViewById(R.id.tv_DeactivateTextLine2);
		tv_DeactivateTextLine2.setTypeface(BebasNeue);
		TextView tv_DeactivateTextLine3=(TextView)findViewById(R.id.tv_DeactivateTextLine3);
		tv_DeactivateTextLine3.setTypeface(BebasNeue);
		TextView tv_DeactivateTextLine4=(TextView)findViewById(R.id.tv_DeactivateTextLine4);
		tv_DeactivateTextLine4.setTypeface(BebasNeue);
		TextView tv_DeactivateTextLine5=(TextView)findViewById(R.id.tv_DeactivateTextLine5);
		tv_DeactivateTextLine5.setTypeface(BebasNeue);
		TextView tv_DeactivateTextLine6=(TextView)findViewById(R.id.tv_DeactivateTextLine6);
		tv_DeactivateTextLine6.setTypeface(BebasNeue);
		TextView tv_DeactivateTextLine7=(TextView)findViewById(R.id.tv_DeactivateTextLine7);
		tv_DeactivateTextLine7.setTypeface(BebasNeue);
		
		

	}

	@SuppressLint("NewApi") public void start_get_location() {
		locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,
				locationListener, Looper.getMainLooper());
	}

	@SuppressLint("NewApi") @Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each tiome there is an update */
      @SuppressLint("NewApi") @Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         signalStrength_str=String.valueOf(signalStrength.getGsmSignalStrength());
         //Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "+ signalStrength, Toast.LENGTH_SHORT).show();
      }

    };
    @Override
    protected void onPause()
     {
       super.onPause();
       telephonyManager.listen(mpsl, PhoneStateListener.LISTEN_NONE);
    }

     /* Called when the application resumes */
    @Override
    protected void onResume()
    {
       super.onResume();
       telephonyManager.listen(mpsl,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    
    public void dialogbox() {

		final Dialog dialog = new Dialog(R4wDeactivate.this);
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
