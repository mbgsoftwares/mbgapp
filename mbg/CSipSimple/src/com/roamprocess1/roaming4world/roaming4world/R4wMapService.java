package com.roamprocess1.roaming4world.roaming4world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

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
import org.json.JSONException;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;


public class R4wMapService extends SherlockFragment {

	private Cursor getCountryCodebyContryName, data_cc_name1,
			getAllCountryCode, dataCheckPin;
	private static final String TAG = "R4wMapService";
	private String getPin, getCountryCode, s_fetch_self_no, mapNumber,
			countryName, countryCode, country_nam = "", errorPayment = "",
			destinationPhNo,result, stored_user_map_no, map_no_from_pref,
			stored_user_Desti_Country, map_desti_Country_pref,overlayScreenValueR4wMapService;
	static int service_count = 0, fetch_flag;
	boolean unknownhost = false, serviceavail = false;
	SharedPreferences prefs;
	private LocationManager locationManager;
	private Location location;
	private TelephonyManager telephonyManager;
	TextView txtVChooseYour, txtVCountry, txtVEnterDesti, txtVDestiPhone,
			txtMapPhone, txtMapcountry;

	private ImageView imgMapCountryLogo;
	private ArrayList arrayList;
	JSONObject proofOfPayment;
	private EditText edtMapCountryCode, edtMapPhoneNo;
	private Spinner spnSelectCountry;
	private Typeface fontBold, fontNormal;
	private SQLiteAdapter mySQLiteAdapter;
	private CustomDestAdapter countryAdapter;

	private Button btnMap,btnAppTour, btnSkip;

	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.r4wmapservice, container,false);
		CurrentFragment.name="MapService";
		
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		
		/*Screen overlay check here start*/
		
		overlayScreenValueR4wMapService = "com.roamprocess1.roaming4world.overlayScreenValueR4wMapService";
		int getOverlayScreenValue = prefs.getInt(overlayScreenValueR4wMapService,555 );
		Log.d("getOverlayScreenValue  in side the MapService", getOverlayScreenValue+"");
		
		if( getOverlayScreenValue==0||getOverlayScreenValue==555)
		{
			prefs.edit().putInt(overlayScreenValueR4wMapService, 1).commit();
			//dialogbox();	
			
		}
		/*Screen overlay check End*/
		
		stored_user_map_no = "com.roamprocess1.roaming4world.map_mobile_no";
		stored_user_Desti_Country = "com.roamprocess1.roaming4world.map_Destination_Country";
		map_no_from_pref = prefs.getString(stored_user_map_no, "No Number");
		map_desti_Country_pref = prefs.getString(stored_user_Desti_Country,"No Country");

		fontBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaBold.otf");
		fontNormal = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaLight.otf");


		if (!map_no_from_pref.equals("No Number")) {
			map_no_from_pref = map_no_from_pref.replaceFirst("^0*", "");
			
			LinearLayout linerMapheading = (LinearLayout) rootView.findViewById(R.id.linerMapheading);
			LinearLayout linerMapText = (LinearLayout) rootView.findViewById(R.id.linerMapText);
			linerMapheading.setVisibility(View.VISIBLE);
			linerMapText.setVisibility(View.VISIBLE);

			txtMapPhone = (TextView) rootView.findViewById(R.id.txtMapPhone);
			TextView txtVCurrentlyMapCounty = (TextView) rootView.findViewById(R.id.txtVCurrentlyMapCounty);
			txtVCurrentlyMapCounty.setTypeface(fontBold);
			//imgMapCountryLogo= (ImageView) rootView.findViewById(R.id.imgMapCountryLogo);
			txtMapcountry = (TextView) rootView.findViewById(R.id.txtMapcountry);
			txtMapcountry.setTypeface(fontNormal);
			txtMapcountry.setText(map_desti_Country_pref);
			txtMapPhone.setTypeface(fontNormal);
			txtMapPhone.setText(map_no_from_pref);
			
			
			map_desti_Country_pref= map_desti_Country_pref.toLowerCase();
			map_desti_Country_pref	=map_desti_Country_pref.replaceAll("\\s+","");
			
			int id = getResources().getIdentifier(map_desti_Country_pref,"drawable", "com.roamprocess1.roaming4world");
			
			//imgMapCountryLogo.setImageDrawable(getResources().getDrawable(id));
			
		}

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		mySQLiteAdapter = new SQLiteAdapter(getActivity());
		mySQLiteAdapter.openToRead();

		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getActivity().getSystemService(
				context);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestSingleUpdate(
					LocationManager.NETWORK_PROVIDER, locationListener,
					Looper.getMainLooper());
		}
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras != null) {
			String priviousMapNo = extras.getString("mapedPhoneNumber");
			Log.d("mapnumber", priviousMapNo);
		}

		spnSelectCountry = (Spinner) rootView.findViewById(R.id.spnCountryMap);
		edtMapPhoneNo = (EditText) rootView.findViewById(R.id.edtMapPhoneNo);
		txtVChooseYour = (TextView) rootView.findViewById(R.id.txtVChooseYour);
		txtVCountry = (TextView) rootView.findViewById(R.id.txtVCountry);
		txtVEnterDesti = (TextView) rootView.findViewById(R.id.txtVEnterDesti);
		txtVDestiPhone = (TextView) rootView.findViewById(R.id.txtVDestiPhone);
		btnMap = (Button) rootView.findViewById(R.id.btnMap);
		btnMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				destinationPhNo = edtMapPhoneNo.getText().toString();
				destinationPhNo = destinationPhNo.replaceFirst("^0*", "");

				if (!destinationPhNo.equals(null) && countryName != "COUNTRIES") {
					mapService(v);
				} else {
					Toast.makeText(getActivity(),
							"Please Select Country or enter Phone No",
							Toast.LENGTH_LONG).show();
				}

			}
		});

		setFont();
		getAllCountryCode = mySQLiteAdapter.fetch_country_codes_dest();
		setListData();
		countryAdapter = new CustomDestAdapter(getActivity(),R.layout.spinner_rows, CustomListViewValuesArr, getResources(),getActivity());
		spnSelectCountry.setAdapter(countryAdapter);
		spnSelectCountry
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View v, int position, long id) {
						countryName = ((TextView) v.findViewById(R.id.txtVcountryName)).getText().toString();

						Log.d("On Item Selected Country Name", countryName);
						mySQLiteAdapter.openToRead();
						getCountryCodebyContryName = mySQLiteAdapter.fetch_country_code_dest(countryName);
						getCountryCodebyContryName.moveToFirst();

						while (!getCountryCodebyContryName.isAfterLast()) {
							countryCode = getCountryCodebyContryName.getString(getCountryCodebyContryName
									.getColumnIndex(SQLiteAdapter.CC_COUNTRY_CODE));
							getCountryCodebyContryName.moveToNext();
							Log.d("On Item Selected Country Code", countryCode);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
						// your code here
					}
				});

		telephonyManager = (TelephonyManager) getActivity().getSystemService(
				Context.TELEPHONY_SERVICE);
		boolean isNetworkAvailable = isNetworkAvailable();
		if (isNetworkAvailable == true) {
			Log.d(TAG, "isNetworkAvailable = true");
		} else {
			Intent intent_main = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent_main);
			getActivity().finish();
		}

		dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		dataCheckPin.moveToFirst();

		while (!dataCheckPin.isAfterLast()) {
			fetch_flag = dataCheckPin.getInt(dataCheckPin
					.getColumnIndex(SQLiteAdapter.FN_FLAG));
			if (fetch_flag == 0) {
				Intent intent_activ = new Intent(getActivity(),R4wActivation.class);
				startActivity(intent_activ);
				getActivity().finish();
			} else {
				getPin = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_PIN_NO));
				getCountryCode = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_COUNTRY_CODE));
				s_fetch_self_no = dataCheckPin.getString(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_SELF_NO));
				// new MyAsyncTaskMapNoGet().execute();
			}
			dataCheckPin.moveToNext();
		}
		mySQLiteAdapter.close();

		return rootView;
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void infodisp(View v) {
		AlertDialog.Builder alertDialogBuilder_deact_exp = new AlertDialog.Builder(
				getActivity());
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

	public void mapService(View v) {

		boolean isNetworkAvailable = isNetworkAvailable();
		if (isNetworkAvailable == true) {
			Log.d(TAG, "isNetworkAvailable =true");
		} else {
			Intent intent_NoNetwork = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent_NoNetwork);
		}

		if (countryName == "SELECT COUNTRIES") {
			alertDialogBox(getResources().getString(R.string.alert),
					getResources().getString(R.string.country_select));
		} else {

			Log.d(TAG, "dispatch()");
			AlertDialog.Builder alertDialogBuilder_map = new AlertDialog.Builder(
					getActivity());
			alertDialogBuilder_map.setTitle(getResources().getString(
					R.string.alert));
			alertDialogBuilder_map
					.setMessage(
							getResources().getString(
									R.string.activate_forwarding)
									+ " Country Code "
									+ countryCode
									+ " and Phone Number " + destinationPhNo)
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									LocationManager locationManage;
									String context = Context.LOCATION_SERVICE;
									locationManage = (LocationManager) getActivity()
											.getSystemService(context);
									boolean isSIMNetworkAvailable = locationManage
											.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
									if (isSIMNetworkAvailable == true) {
										start_get_location();
										new MyAsyncTaskMap().execute();
									} else {
										// new MyAsyncTaskMap().execute();

										new MyAsyncTaskMap().execute();
									}
								}
							})
					.setNegativeButton(
							getResources().getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog_map = alertDialogBuilder_map.create();
			alertDialog_map.show();
		}
	}

	public boolean webservreqMap() {
		try {
			Log.d("Testing Log", "Inside Map Service");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);
			Log.d("Testing Log", "Inside Map Service 1");
			double lat = 0;
			double lng = 0;
			if (location != null) {
				lat = location.getLatitude();
				lng = location.getLongitude();
			}
			Log.d("Testing Log", "Inside Map Service2");
			
			
			
			HttpPost httppost = new HttpPost(url);

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);

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

			return false;
		}
	}

	private class MyAsyncTaskMap extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog1;

		@Override
		protected void onPostExecute(Boolean result) {
			mProgressDialog1.dismiss();
			mySQLiteAdapter.openToWrite();
			mySQLiteAdapter.update_flag(3);
			mySQLiteAdapter.close();
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.Frame_Layout, new R4wActivate()).commit();

		}

		@Override
		protected void onPreExecute() {
			mProgressDialog1 = ProgressDialog.show(getActivity(),
					getResources().getString(R.string.loading), getResources()
							.getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqMap()) {
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

	public void setListData() {
		arrayList = new ArrayList();
		getAllCountryCode.moveToFirst();
		while (!getAllCountryCode.isAfterLast()) {
			String spnRowData = getAllCountryCode.getString(getAllCountryCode.getColumnIndex(SQLiteAdapter.CD_COUNTRY_NAME));
			arrayList.add(spnRowData);
			getAllCountryCode.moveToNext();
			final SpinnerModel spnModel = new SpinnerModel();
			spnModel.setCountyName(spnRowData);
			spnModel.setImage(spnRowData.toLowerCase().replaceAll("\\s+", ""));
			spnModel.setUrl(spnRowData);
			CustomListViewValuesArr.add(spnModel);
		}
	}

	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
		getActivity().finish();
	
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
			Log.i(TAG,"HTTPResponse received in ["+ (System.currentTimeMillis() - t) + "ms]");
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				org.apache.http.Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

				// convert content stream to a String
				String resultString = convertStreamToString(instream);
				instream.close();
				resultString = resultString.substring(0,resultString.length() - 1); // remove wrapping "[" and
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
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
		public void onProviderDisabled(String provider) {}

	};

	public void alertDialogBox(String title, String message) {
		AlertDialog.Builder alertDialogBuilder_send = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder_send.setTitle(title);
		alertDialogBuilder_send
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertDialog_send = alertDialogBuilder_send.create();
		alertDialog_send.show();

	}

	public void setFont() {
		txtVChooseYour.setTypeface(fontNormal);
		txtVCountry.setTypeface(fontBold);
		txtVEnterDesti.setTypeface(fontNormal);
		txtVDestiPhone.setTypeface(fontBold);
		edtMapPhoneNo.setTypeface(fontNormal);
	}

	@SuppressLint("NewApi") public void start_get_location() {
		locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener, Looper.getMainLooper());
	}

	@Override
	public void onDestroy() {

		//getActivity().stopService(new Intent(getActivity(), PayPalService.class));
				super.onDestroy();
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
