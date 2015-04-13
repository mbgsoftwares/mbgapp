package com.roamprocess1.roaming4world.roaming4world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;


public class R4wActivation extends SherlockFragment {
	final Context context = getActivity();

	TelephonyManager telephonyManager;
	LocationManager location_Manager;
	private static final String TAG = "MainActivity";
	private String getDIDNo = "", countryCode, countryName, country_nam, stored_leftTab,
			CountryID, CountryZipCode, errorPayment, pinNumber, stored_tabselection,
			stored_user_mobile_no = "",result,latitude, longitude, stored_user_country, present_country_name;
	private TextView txtVEnter, txtVPin, txtVChooseYour, txtVCountry,txtVEnteryour, txtVMobileNo,txtCountryName,txtService;
	private EditText edtPinNo1, edtPinNo2, edtPinNo3, edtPinNo4,edtPhoneNo;
	private Typeface nexaBold, nexaNormal;
	
	private ImageView imgCountryiCon;
	private Spinner spnSelectCountry;
	Location location;
	Cursor dataCheckPin, getAllCountryCode, getCountryCode, getCountryName;
	private int fetch_flag, pin, status,alertValue=0;
	public Boolean chek;
	private JSONObject proofOfPayment;
	ArrayList arrayList;
	String overlayScreenValueR4wActivation,pinNumberforVoiceMail;
	View rootView;
	TextView overlay;
	
	ViewPager vp_apptour;
	ImageAdapter gallery_adap;

	
	private CustomAdapter countryAdapter;
	SharedPreferences prefs;
	private Button btnActivate,btnAppTour, btnSkip;
	final String encodedHash = Uri.encode("#"),ussd = "**21*";
	private SQLiteAdapter mySQLiteAdapter;
	int CountryInListFlag = 0;
	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	//EnterPinDrawer activity = null;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.r4w_activation, container, false);
		CurrentFragment.name = "EnterAPIN";
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		System.out.println("alertValue =="+alertValue);
		pinNumberforVoiceMail= "com.roamprocess1.roaming4world.pinNumberforVoiceMail";
		/*Screen overlay check here start*/
		
		overlayScreenValueR4wActivation = "com.roamprocess1.roaming4world.overlayScreenValue";
		stored_tabselection = "com.roamprocess1.roaming4world.tabselection";
		
		int getOverlayScreenValue = prefs.getInt(overlayScreenValueR4wActivation,555 );
		Log.d("getOverlayScreenValue  in side the R4wActivation", getOverlayScreenValue+"");
		
		if( getOverlayScreenValue==0||getOverlayScreenValue==555){
			prefs.edit().putInt(overlayScreenValueR4wActivation, 1).commit();
			//dialogbox();	
			
		}
		/*Screen overlay check End*/
		
		latitude = "com.roamprocess1.roaming4world.latitude";
		longitude = "com.roamprocess1.roaming4world.longitude";
		stored_user_country = "com.roamprocess1.roaming4world.user_country";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		present_country_name = prefs.getString(stored_user_country, "no");
		stored_leftTab = "com.roamprocess1.roaming4world.left_tabselection";
        
		countryName = present_country_name;
		NetworkCheck();

		System.out.println("user country on roaming4world:"+present_country_name);
		
		String context = Context.LOCATION_SERVICE;
		location_Manager = (LocationManager) getActivity().getSystemService(context);
		location_Manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER,locationListener, Looper.getMainLooper());

		/*
		 * Start of broadcast
		 */
		Intent intent_broadcast = new Intent(getActivity(),MyBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), 0, intent_broadcast,PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
		long recurring = (30 * 60000);
		alarmManager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(), recurring, pendingIntent);
		/*
		 * Finish of broadcast
		 */

		imgCountryiCon=(ImageView)rootView.findViewById(R.id.imgCountryiCon);
		
		
		edtPinNo1 = (EditText) rootView.findViewById(R.id.edtPinNo1);
		edtPinNo2 = (EditText) rootView.findViewById(R.id.edtPinNo2);
		edtPinNo3 = (EditText) rootView.findViewById(R.id.edtPinNo3);
		edtPinNo4 = (EditText) rootView.findViewById(R.id.edtPinNo4);
		edtPhoneNo = (EditText) rootView.findViewById(R.id.edtPhoneNo);
		btnActivate = (Button) rootView.findViewById(R.id.btnActivate);
		btnActivate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callservice();

			}
		});

		String userphno = prefs.getString(stored_user_mobile_no, "no");
		if (!userphno.equals("no")) {
			edtPhoneNo.setText(userphno);
			edtPhoneNo.setEnabled(false);
		}
		
		setFont();

		/* Pin number Code Start From here */
		edtPinNo1.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				if (edtPinNo1.length() == 2) {
					edtPinNo2.requestFocus();
				}
			}
		});
		edtPinNo2.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (edtPinNo2.length() == 2) {
					edtPinNo3.requestFocus();
				}
			}
		});
		edtPinNo3.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (edtPinNo3.length() == 2) {
					edtPinNo4.requestFocus();
				}
			}
		});
		edtPinNo2.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (edtPinNo2.length() == 0) {
					if (keyCode == KeyEvent.KEYCODE_DEL) {
						edtPinNo1.requestFocus();
						edtPinNo1.setSelection(edtPinNo1.length());
					}
				}
				return false;
			}
		});
		edtPinNo3.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (edtPinNo3.length() == 0) {
					if (keycode == KeyEvent.KEYCODE_DEL) {
						edtPinNo2.requestFocus();
						edtPinNo2.setSelection(edtPinNo2.length());
					}
				}

				return false;
			}
		});
		edtPinNo4.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub

				if (edtPinNo4.length() == 0) {
					if (keycode == KeyEvent.KEYCODE_DEL) {
						edtPinNo3.requestFocus();
						edtPinNo3.setSelection(edtPinNo3.length());
					}
				}

				return false;
			}
		});
		/* Pin number Code End */

		/* pin code logic */

		edtPinNo2.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((edtPinNo2.length() == 2 && edtPinNo2.getSelectionStart() == 0)
						|| edtPinNo2.length() == 0) {
					if (keycode == KeyEvent.KEYCODE_DEL) {
						edtPinNo1.requestFocus();
					}
				}
				return false;
			}
		});

		edtPinNo3.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((edtPinNo3.length() == 2 && edtPinNo3.getSelectionStart() == 0)
						|| edtPinNo3.length() == 0) {
					if (keycode == KeyEvent.KEYCODE_DEL) {
						edtPinNo2.requestFocus();
						edtPinNo2.setSelection(edtPinNo2.length());
					}
				}
				return false;
			}
		});
		edtPinNo4.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((edtPinNo4.length() == 2 && edtPinNo4.getSelectionStart() == 0)
						|| (edtPinNo4.length() == 0)) {
					if (keycode == KeyEvent.KEYCODE_DEL) {
						edtPinNo3.requestFocus();
						edtPinNo3.setSelection(edtPinNo3.length());
					}
				}
				return false;
			}
		});

		edtPinNo1.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keycode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((edtPinNo1.length() == 2
						&& edtPinNo1.getSelectionStart() == 0 && edtPinNo1
						.getSelectionEnd() == 0) || (edtPinNo1.length() <= 0)) {
					{
						edtPinNo1.setText("");
						if (keycode == KeyEvent.KEYCODE_0
								|| keycode == KeyEvent.KEYCODE_1
								|| keycode == KeyEvent.KEYCODE_2
								|| keycode == KeyEvent.KEYCODE_3
								|| keycode == KeyEvent.KEYCODE_4
								|| keycode == KeyEvent.KEYCODE_5
								|| keycode == KeyEvent.KEYCODE_6
								|| keycode == KeyEvent.KEYCODE_7
								|| keycode == KeyEvent.KEYCODE_8
								|| keycode == KeyEvent.KEYCODE_9) {
							// edtPinNo1.setSelection(edtPinNo1.length());

							edtPinNo1.requestFocus();
							edtPinNo1.setSelection(edtPinNo1.length());
						}
					}
				}
				return false;
			}
		});

		/* pin code logic */

		mySQLiteAdapter = new SQLiteAdapter(getActivity());
		mySQLiteAdapter.openToRead();
		dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		dataCheckPin.moveToFirst();
		while (!dataCheckPin.isAfterLast()) {
			fetch_flag = dataCheckPin.getInt(dataCheckPin
					.getColumnIndex(SQLiteAdapter.FN_FLAG));			
			if (fetch_flag == 1) {
				prefs.edit().putBoolean(stored_leftTab, true).commit();
				Log.d("Activity Change", "flag was 1");
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, new R4wActivateFwd())
						.commit();
			} else if (fetch_flag == 2) {
				prefs.edit().putBoolean(stored_leftTab, true).commit();
				Log.d("Activity Change", "flag was 2");
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, new R4wMapService())
						.commit();
			} else if (fetch_flag == 3) {
				prefs.edit().putBoolean(stored_leftTab, true).commit();
				Log.d("Activity Change", "flag was 3");
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, new R4wActivate()).commit();
			}
			dataCheckPin.moveToNext();
		}
		getAllCountryCode = mySQLiteAdapter.fetch_country_codes();
		spnSelectCountry = (Spinner) rootView.findViewById(R.id.spnCountry);
		// new MyAsyncTaskCheckPromoEnable().execute();
		setListData();
		if (CountryInListFlag == 1) {
			txtVChooseYour.setText("Home Network ");
			countryAdapter = new CustomAdapter(getActivity(),R.layout.spinner_rows, CustomListViewValuesArr,
					getResources(), getActivity());
			
			
			
			txtService.setVisibility(View.GONE);
			//spnSelectCountry.setAdapter(countryAdapter);
			Log.d("when Country not In List", present_country_name);
			int location2id = getPostiton(present_country_name);
			
			imgCountryiCon.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/"+present_country_name.toLowerCase().replaceAll("\\s+","") + "copy" ,null,null));
			txtCountryName.setText(""+present_country_name);
			
			System.out.println("location2id value:"+location2id);
			//spnSelectCountry.setSelection(location2id);
			//spnSelectCountry.setEnabled(false);
			//spnSelectCountry.setClickable(false);

		} else {
			edtPinNo1.setEnabled(false);
			edtPinNo2.setEnabled(false);
			edtPinNo3.setEnabled(false);
			edtPinNo4.setEnabled(false);
			btnActivate.setEnabled(false);
			
			txtCountryName.setVisibility(View.GONE);
			txtService.setVisibility(View.VISIBLE);
			
			imgCountryiCon.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/"+present_country_name.toLowerCase().replaceAll("\\s+","") + "copy" ,null,null));
			txtCountryName.setText(""+"This Service is not available in your Country");
		
			System.out.println("R4wActivation service not available");
			
			AlertDialog.Builder alertDialogBuilder_send = new AlertDialog.Builder(getActivity());
			alertDialogBuilder_send.setMessage("This Service is not available in your Country")
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					btnActivate.setEnabled(false);
				}
			  });

	}
		if (country_nam == null) {
			//currentLocation.setText(getResources().getString(R.string.countryname)+" Not available");
		} else {
			//currentLocation.setText(getResources().getString(R.string.countryname)+country_nam);
		}
		return rootView;
	}
	
	
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	
	private class MyAsyncTaskAct extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog;
		@Override
		protected void onPostExecute(Boolean result) {
			mProgressDialog.dismiss();
			if (getDIDNo.equalsIgnoreCase("nodid"))
				alertDialogBox(getResources().getString(R.string.message),
						getResources().getString(R.string.service_unavailable));
			else if (getDIDNo.equalsIgnoreCase("nocountry"))
				alertDialogBox(getResources().getString(R.string.message),
						getResources().getString(R.string.service_unavailable));
			else if (getDIDNo.equalsIgnoreCase("wrongpin"))
				alertDialogBox(getResources().getString(R.string.message),
						getResources().getString(R.string.wrong_pin));
			else if (getDIDNo.equalsIgnoreCase("pininuse")) {
				alertDialogBox(getResources().getString(R.string.message),
						getResources().getString(R.string.pin_already_use));
			} else {
				prefs.edit().putLong(stored_tabselection, 1).commit();
				prefs.edit().putBoolean(stored_leftTab, true).commit();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, new R4wActivateFwd())
						.commit();
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(getActivity(), getResources()
					.getString(R.string.loading),
					getResources().getString(R.string.data_loading));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqAct()) {
				Log.d(TAG, " SUCCESS");
				if (getDIDNo.equalsIgnoreCase("nodid")) {
					// Log.d("","SUCCESS for nodid");
					return false;
				} else if (getDIDNo.equalsIgnoreCase("nocountry")) {
					// /Log.d("","SUCCESS for nocountry");
					return false;
				} else if (getDIDNo.equalsIgnoreCase("wrongpin")) {
					// Log.d("","SUCCESS for wrongpin");
					return false;
				} else if (getDIDNo.equalsIgnoreCase("pininuse")) {
					// Log.d("","SUCCESS for pininuse");
					return false;
				} else {
					mySQLiteAdapter.openToWrite();
					prefs.edit().putLong(stored_tabselection, 1).commit();
					mySQLiteAdapter.ins_pin_did(pinNumber, getDIDNo,
							countryCode, prefs.getString(stored_user_mobile_no, "no"));
					mySQLiteAdapter.close();
					chek = true;
					return true;
				}
			} else {
				// Log.d(TAG,"ERROR");
				return false;
			}
		}
	}

	public void callservice() {
		Log.d(TAG, "web services call ");
		mySQLiteAdapter.openToRead();
		getCountryCode = mySQLiteAdapter.fetch_country_code(countryName);
		getCountryCode.moveToFirst();
		while (!getCountryCode.isAfterLast()) {
			countryCode = getCountryCode.getString(getCountryCode.getColumnIndex(SQLiteAdapter.CC_COUNTRY_CODE));
			getCountryCode.moveToNext();
		}
		mySQLiteAdapter.close();

		NetworkCheck();
		pinNumber = edtPinNo1.getText().toString() + edtPinNo2.getText()+ edtPinNo3.getText() + edtPinNo4.getText();
		if (countryName == "COUNTRIES") {
			alertDialogBox(getResources().getString(R.string.alert),getResources().getString(R.string.country_select));
		} else if (pinNumber.length() < 8) {
			Toast.makeText(getActivity().getApplicationContext(),"Please Enter Pin", Toast.LENGTH_SHORT).show();
		} else {
			
			CurrentFragment.pinNumber=pinNumber.toString();
			prefs.edit().putString(pinNumberforVoiceMail, pinNumber).commit();
			Log.d("CurrentFragment.pinNumber in side Activation", CurrentFragment.pinNumber);		
			AlertDialog.Builder alertDialogBuilder_send = new AlertDialog.Builder(getActivity());
			alertDialogBuilder_send.setTitle(getResources().getString(R.string.alert));
			alertDialogBuilder_send.setMessage(
							getResources().getString(R.string.source_country)
									+ " "
									+ countryName
									+ "("
									+ countryCode
									+ ") "
									+ getResources().getString(
											R.string.phone_no)
									+ " "
									+ prefs.getString(stored_user_mobile_no, "no")

									+ getResources().getString(
											R.string.confirm_proceed) + "!")
					.setCancelable(false)
					.setPositiveButton(getResources().getString(R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									String context = Context.LOCATION_SERVICE;
									LocationManager locationManage = (LocationManager) getActivity().getSystemService(context);
									boolean isGPSEnabled = locationManage.isProviderEnabled(LocationManager.NETWORK_PROVIDER);								
									if (isGPSEnabled == true) {
										Log.d("register1", "1");
										try {
											new MyAsyncTaskAct().execute();
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else {
									try {
											Log.d("register1", "0");
											new MyAsyncTaskAct().execute();
											int sim_state = telephonyManager.getSimState();
											Log.d("error hear===== ", sim_state+"");
											switch (sim_state) {											
											case TelephonyManager.SIM_STATE_ABSENT:
												alertDialogBox(getResources().getString(R.string.no_sim_network),getResources().getString(R.string.sim_network_not_registered));
												break;
											case TelephonyManager.SIM_STATE_READY:
												Log.d("country_name","country name not equal to country name sim state ready");
												new MyAsyncTaskAct().execute();
												break;
											}
											
										} catch (Exception e) {
											// TODO: handle exception
										}
									}
								}
							})
					.setNegativeButton(getResources().getString(R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog_send = alertDialogBuilder_send.create();
			alertDialog_send.show();
		}
	}

	public boolean webservreqAct() {
		try {

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			// Instantiate an HttpClient
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
			prefs.edit().putString(latitude, lat1).commit();
			prefs.edit().putString(longitude, lng1).commit();
			String country_name1_name = countryName.replaceAll("\\s", "%20");
			/*
			String url = "http://ip.roaming4world.com/esstel/"
					+ "receive.php?from=" + countryCode
					+ edtPhoneNo.getText().toString() + "&message=ETEL%20A%20"
					+ pinNumber + "&format=json&cc=" + countryCode + "&cname="
					+ URLEncoder.encode(countryName, "UTF-8") + "&lat=" + lat1
					+ "&lng=" + lng1;
			Log.d(TAG, url);
			*/
			
			/*testing */
			String url = "null";
			Log.d(TAG, url);
			
			/*testing */
			
			HttpPost httppost = new HttpPost(url);
			boolean network_check = isNetworkAvailable();
			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");
				if (network_check) {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("user", "1"));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String responseBody = httpclient.execute(httppost,responseHandler);
					// Parse
					JSONObject json = new JSONObject(responseBody);
					JSONArray jArray = json.getJSONArray("responses");
					for (int i = 0; i < jArray.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						JSONObject e = jArray.getJSONObject(i);
						String s = e.getString("response");
						JSONObject jObject = new JSONObject(s);
						getDIDNo = jObject.getString("did_no");
						Log.d("Divert No", jObject.getString("did_no"));
					}

				} else {
					Intent intent = new Intent(getActivity(), NoNetwork.class);
					startActivity(intent);
					return false;
				}
				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return false;
			}
		} catch (Exception t) {
			t.printStackTrace();
			alertDialogBox(getResources().getString(R.string.alert),getResources().getString(R.string.incorrect_pin));
			return false;
		}
	}

	public void onBackPressed() {
		getActivity().finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 2:
			if (resultCode == Activity.RESULT_OK) {
				Boolean promo_value = data.getBooleanExtra("valid", false);
				if (promo_value) {

					// String promotional_pin_no=data.getStringExtra("pin_no");
					// edtPinNo.setText(promotional_pin_no);
				}
			}

		}

	}

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

	public void setListData() {
		arrayList = new ArrayList();
		getAllCountryCode.moveToFirst();
		while (!getAllCountryCode.isAfterLast()) {
			String mTitleRaw = getAllCountryCode.getString(getAllCountryCode.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			arrayList.add(mTitleRaw);
			getAllCountryCode.moveToNext();
			final SpinnerModel sched = new SpinnerModel();
			String countryNameValue = getResources().getString(getResources().getIdentifier(mTitleRaw, "string", "com.roamprocess1.roaming4world"));
			Log.d("countryNameValue", countryNameValue+" @");
			sched.setCountyName(mTitleRaw);
			if (countryNameValue.equals(present_country_name)) {
				CountryInListFlag = 1;
			}
			sched.setImage(mTitleRaw.toLowerCase());
			sched.setUrl(mTitleRaw);
			CustomListViewValuesArr.add(sched);
		}
	}

	/*
	 * Check Start
	 */
	public class MyAsyncTaskGetMapNo extends AsyncTask<Void, Void, Boolean> {
		@Override
		public Boolean doInBackground(Void... params) {
			if (webServReqGetMAPNO()) {
				Log.d(TAG, "MyAsyncTaskGetMapNo  Success");
				return true;
			} else {
				Log.d(TAG, "MyAsyncTaskGetMapNo  Error");
				return false;
			}
		}

		public void onPostExecute(Boolean result) {

			super.onPostExecute(result);
			if (result) {
				// EditText addpin = (EditText) findViewById(R.id.pin_no);
				// addpin.setText(Integer.toString(pin));

				Toast.makeText(getActivity(),
						getResources().getString(R.string.payment_made),
						Toast.LENGTH_LONG).show();
			} else {

				if (errorPayment != "") {
					new MyAsyncTaskGetMapNo().execute();

				}
			}

		}
	}

	public boolean webServReqGetMAPNO() {
		boolean network_check = isNetworkAvailable();
		JSONObject jsonObjRecv;
		if (network_check) {
			String URL = "null"
					+ result;
			jsonObjRecv = SendHttpPost(URL, proofOfPayment);
		} else {
			Intent intent = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent);
			return false;
		}
		boolean success = false, status = false;
		try {
			success = jsonObjRecv.getBoolean("success");
			if (success == false) {
				errorPayment = jsonObjRecv.getString("error");
			}
			if (jsonObjRecv.getBoolean("success") == true) {
				pin = jsonObjRecv.getInt("pin");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	/*
	 * Check End
	 */
	public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);
			StringEntity se = new StringEntity(jsonObjSend.toString());
			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");

			// long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			// Log.i(TAG, "HTTPResponse received in [" +
			// (System.currentTimeMillis()-t) + "ms]");
			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {

				InputStream instream = entity.getContent();
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					instream = new GZIPInputStream(instream);
				}

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
				//e1.printStackTrace();
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
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/*
	 * start the promo_activity.class by clicking on android:click=promoservice
	 */
	public void promoservice(View v) {
		Intent intent = new Intent(getActivity(), Promo_activity.class);
		startActivityForResult(intent, 2);
	}

	public class MyAsyncTaskCheckPromoEnable extends
			AsyncTask<Void, Void, Boolean> {
		// ProgressDialog mProgressDialog3;

		@Override
		public Boolean doInBackground(Void... params) {
			if (webservreqCheckPromoEnable()) {
				// Log.d("yay","SUCCESS");

				return true;
			} else {
				// Log.d("err","ERROR");
				return false;
			}
		}

		public void onPostExecute(Boolean result) {
			// dismiss the dialog once done
			super.onPostExecute(result);
			if (result) {
				if (status == 1) {
					// showButton();
				} else {
					// hideButton();
				}
			} else {
				// hideButton();

			}
		}
	}

	/*
	 * Block to be called by MyAsyncTaskCheckPromoEnable
	 */
	public boolean webservreqCheckPromoEnable() {
		try {

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			// Instantiate an HttpClient
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "null"";
			HttpPost httppost = new HttpPost(url);
			boolean network_check = isNetworkAvailable();
			// Instantiate a GET HTTP method
			try {

				if (network_check) {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("user", "1"));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String responseBody = httpclient.execute(httppost,responseHandler);
					JSONObject json = new JSONObject(responseBody);
					String type = json.getString("type");
					status = json.getInt("status");

				} else {
					Intent intent = new Intent(getActivity(), NoNetwork.class);
					startActivity(intent);
					return false;
				}
				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return false;
			}
		} catch (Exception t) {
			//t.printStackTrace();

			return false;
		}
	}

	public void setFont() {
		nexaBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaBold.otf");
		nexaNormal = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaLight.otf");
		
		txtVEnter = (TextView) rootView.findViewById(R.id.txtVEnter);
		txtVPin = (TextView) rootView.findViewById(R.id.txtVPin);
		txtVCountry = (TextView) rootView.findViewById(R.id.txtVCountry);
		txtVChooseYour = (TextView) rootView.findViewById(R.id.txtVChooseYour);
		txtVEnteryour = (TextView) rootView.findViewById(R.id.txtVEnteryour);
		txtVMobileNo = (TextView) rootView.findViewById(R.id.txtVMobileNo);
		
		txtCountryName=(TextView) rootView.findViewById(R.id.txtCountryName);
		txtService=(TextView) rootView.findViewById(R.id.txtService);
		
		txtService.setTypeface(nexaNormal);
		txtCountryName.setTypeface(nexaNormal);
		edtPinNo1.setTypeface(nexaNormal);
		edtPinNo2.setTypeface(nexaNormal);
		edtPinNo3.setTypeface(nexaNormal);
		edtPinNo4.setTypeface(nexaNormal);
		txtVEnter.setTypeface(nexaNormal);
		edtPhoneNo.setTypeface(nexaNormal);
		txtVPin.setTypeface(nexaBold);
		txtVCountry.setTypeface(nexaBold);
		txtVChooseYour.setTypeface(nexaNormal);
		txtVEnteryour.setTypeface(nexaNormal);
		txtVMobileNo.setTypeface(nexaBold);
		btnActivate.setTypeface(nexaBold);

	}

		LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location1) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		public void onProviderEnabled(String provider) {}
		public void onProviderDisabled(String provider) {}

	};

	
	public void NetworkCheck() {
		boolean isNetworkAvailable = isNetworkAvailable();
		if (isNetworkAvailable == true) {
			Log.d(TAG, "isNetworkAvailable true");
		} else {
			Intent intent_main = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent_main);
		}
	}

	private int getPostiton(String locationid) {
		int i, position = 0;
		getAllCountryCode.moveToFirst();
		for (i = 0; i < getAllCountryCode.getCount() - 1; i++) {
			getAllCountryCode.moveToNext();
			String locationVal = getAllCountryCode.getString(getAllCountryCode.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			if (locationVal.equals(locationid)) {
				position = i + 1;
				break;
			} else {
				position = 0;
			}
		}
		return position;
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
