package com.roamprocess1.roaming4world.roaming4world;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.roamprocess1.roaming4world.GCMRegistration;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.AsyncTaskProfilePicUpload;
import com.roamprocess1.roaming4world.ui.SipHome;

public class RegChooseLogin extends Activity {

	private Cursor getCountryCodebyContryName,getAllCountryCode,get_country_name;
	private String simCountry,countryName, countryCode,user_mobile_no,screen1_status, CountryID = "", userInfo = "UserInfo",wait_screen,
			screen2_status,stored_user_mobile_no,stored_user_country,stored_user_country_code,Stored_current_time, stored_supportnumber;
	TextView txtVChooseYour, txtVCountry, txtVEnterDesti, txtVDestiPhone;
	private ArrayList arrayList;
	JSONObject proofOfPayment;
	private EditText edtMapPhoneNo;
	private Spinner spnSelectCountry;
	private SQLiteAdapter mySQLiteAdapter;
	long screen1,screen_1_value, current_time;
	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	SharedPreferences prefs;
	CheckBox checkbox;
	TelephonyManager telephonyManager;
	private CustomAdapterReg countryAdapter;
	private Typeface nexaBold, nexaNormal;
	Button btnAppTour, btnSkip, btnverify;
	ImageView callbackPage;
	
	
	public static String TAG="RegChooseLogin";
	// Reg Sign up variables
	TextView tv;
	String registration_level, four_digit_code, stored_four_digit_code, stored_user_dialer_password,stored_username,stored_userimagepath,
			signUpProcess,prefSignUpProcess,stored_upgrade_apk , prefChooseclass;
	long screen2_value;
	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	Handler mHandler;
	String smsCode, store_userCountryName;
	String value;
	int loop_counter;
	ProgressDialog mProgressDialog_ReadSMS;
	String Returned_password;
	private static int myProgress;
	private ProgressDialog progressDialog;
	private Handler myHandler=new Handler();
	private int progressStatus=0;
	int read_from_thread=0, selectBrowse = 0;
	ViewFlipper flipper1;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_GALLERY = 2;
	ImageView imgview;
	Uri selectedImage;
	public static String imageaddres = ""; 
	TextView tvuserName;
	boolean waitScrean = true;
	
	int serverResponseCode = 0;
	ProgressDialog dialog = null;
	private String stored_server_ipaddress;
	
	public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
	   
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("RegChooseLogin  step1");
		prefs = RegChooseLogin.this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		System.out.println("RegChooseLogin  step2");
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country = "com.roamprocess1.roaming4world.user_country";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		screen1_status = "com.roamprocess1.roaming4world.screen1";
		screen2_status = "com.roamprocess1.roaming4world.screen2_status";
		stored_supportnumber = "com.roamprocess1.roaming4world.support_no";
		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";
		wait_screen = "com.roamprocess1.roaming4world.waitscreen";
		//regSign up screens
		stored_four_digit_code = "com.roamprocess1.roaming4world.four_digit_code";
		stored_user_dialer_password = "com.roamprocess1.roaming4world.dialer_password";
		store_userCountryName = "com.roamprocess1.roaming4world.user_countryname";
		Stored_current_time = "com.roamprocess1.roaming4world.current_time";
		screen_1_value = prefs.getLong(screen1_status, 0);
		Long screen2_value = prefs.getLong(screen2_status, 0);
		
		prefChooseclass = "com.roamprocess1.roaming4world.chooseClass";
		
		stored_username = "com.roamprocess1.roaming4world.username";
		stored_userimagepath = "com.roamprocess1.roaming4world.userimagepath";
		
		
		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		System.out.println("RegChooseLogin  step3");
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		CountryID = telephonyManager.getSimCountryIso().toUpperCase();
		simCountry = getCountryZipCodeRegchooseLogin();
		String cname = get_country_name_via_codeRegchooseLogin();
		
		
		stored_upgrade_apk = "com.roamprocess1.roaming4world.upgrade_apk_version";

		signUpProcess = "com.roamprocess1.roaming4world.signUpProcess";
		prefSignUpProcess =prefs.getString(signUpProcess, "NotCompleted");
		System.out.println("RegChoose Login prefSignUpProcess value == "+prefSignUpProcess);
		Log.d("screen 1 # 2", screen2_value + " " + screen_1_value);
		System.out.println("RegChooseLogin  step4");
		String present_mobile_no = prefs.getString(stored_user_mobile_no, "no");
		String present_country_name = prefs.getString(stored_user_country, "");
	
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.r4wregistration_viewflipper_parallax);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN );
		flipper1 = (ViewFlipper) findViewById(R.id.flipper);
		
		//dialogbox();
		btnverify = (Button) findViewById(R.id.btnSign_in);
		
		btnverify.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SignInCall();
				
			}
		});

		prefs.edit().putString(stored_upgrade_apk, "1").commit();
		
		System.out.println("RegChooseLogin  step5");
		nexaBold = Typeface.createFromAsset(getAssets(), "fonts/NexaBold.otf");
		nexaNormal = Typeface.createFromAsset(getAssets(),"fonts/NexaLight.otf");
		
		TextView tventer4digitcode = (TextView) findViewById(R.id.tvEnter4digitcode);
		tventer4digitcode.setTypeface(nexaBold);
		tvuserName = (TextView) findViewById(R.id.tvuserNameReg);
		imgview = (ImageView) findViewById(R.id.ivreg_userpic);
		callbackPage = (ImageView) findViewById(R.id.ivcallBackPage);
		
		if (!present_mobile_no.equals("no")) {
			EditText mobile_no = (EditText) findViewById(R.id.et_entermobileno);
			mobile_no.setText(present_mobile_no);
		}

	
		System.out.println("RegChooseLogin  step6");
		spnSelectCountry = (Spinner) findViewById(R.id.spnloginCountrySpinner);
		getAllCountryCode = mySQLiteAdapter.fetch_country_codes_dest();
		// getAllCountryCode = mySQLiteAdapter.fetch_country_codes_dest();
		setListDataRegchooseLogin();
		countryAdapter = new CustomAdapterReg(RegChooseLogin.this,R.layout.spinner_row_country_reg, CustomListViewValuesArr, getResources(),this);
		spnSelectCountry.setAdapter(countryAdapter);
		
		Bitmap bb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_contact_picture_180_holo_light);
		
		if(imageaddres.equals("")){
			imgview.setImageBitmap(bb);
		}else{
			Bitmap bitmap = BitmapFactory.decodeFile(imageaddres);
			imgview.setImageBitmap(bitmap);
			
		}
		
		try {
			if (!cname.equals("")) {
				int location2id = getPostitonRegchooseLogin(cname);
				spnSelectCountry.setSelection(location2id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RegChooseLogin  step7");
		spnSelectCountry
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,View v, int position, long id) {
						TextView cName = (TextView) v.findViewById(R.id.txtVcountryName);
						cName.setTypeface(nexaNormal);
						countryName = cName.getText().toString();
						String store_cn = countryName.toLowerCase().replaceAll("\\s+", "");
						prefs.edit().putString(store_userCountryName, store_cn).commit();
						mySQLiteAdapter.openToRead();
						getCountryCodebyContryName = mySQLiteAdapter.fetch_country_code_dest(countryName);
						getCountryCodebyContryName.moveToFirst();

						while (!getCountryCodebyContryName.isAfterLast()) {
							countryCode = getCountryCodebyContryName.getString(getCountryCodebyContryName.getColumnIndex(SQLiteAdapter.CC_COUNTRY_CODE));
							getCountryCodebyContryName.moveToNext();
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
					}
				});
		System.out.println("RegChooseLogin  step8");
		try {
			if (!present_country_name.equals("")) {
				int location2id = getPostitonRegchooseLogin(present_country_name);
				spnSelectCountry.setSelection(location2id);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RegChooseLogin  step9");
		
		if (screen2_value == 1) {
			
			System.out.println(" ******* see prefSignUpProcess value  1== "+prefSignUpProcess);
			if(prefSignUpProcess.equals("NotCompleted")){
				/*
				Intent r4wContactsLoading = new Intent(this,R4wContactsLoading.class);
				startActivity(r4wContactsLoading);
				finish();
				*/
				Log.d(TAG,"prefSignUpProcess ==NotCompleted");
				Intent r4wSplashScreen= new Intent(this,R4WSplashScreen.class);
				r4wSplashScreen.putExtra("R4wContactStatus", "unloaded");
				startActivity(r4wSplashScreen);
				finish();
				
			}else
			{	
				prefs.edit().putString(prefChooseclass, "SipHome").commit();
				Log.d(TAG,"prefSignUpProcess ==R4wCompleted");
				Intent intent = new Intent(this, SipHome.class);
				startActivity(intent);
				finish();
			}
			/*
			Intent intent = new Intent(this, SipHome.class);
			startActivity(intent);
			finish();*/
	
			
		
		
		} else if (screen_1_value == 1) {
		//	beginYourTask();
			flipper_leftTOright();
		}
		
		 else if (screen_1_value == 2) {
			flipper_leftTOright_second();
		}
	
		
	}

	protected void SignInCall() {
		// TODO Auto-generated method stub
		int i = 0;
		String countryCode = GetCountryZipCode();
		EditText mobile_no = (EditText) findViewById(R.id.et_entermobileno);
		String num = mobile_no.getText().toString();
		
		if(num.startsWith("00")){
//			num = num.substring(2);	
			i++;
		}
		if(num.startsWith("0") || num.startsWith("+")){
//			num = num.substring(1);	
			i++;
		}
		
		
		
		if(num.startsWith(countryCode)){
//			num = num.substring(countryCode.length());	
			i++;
		}
		//if(num != null)
		//mobile_no.setText(num);
	
		if(i == 0){
			//checkCountry();
			callsmsverificationRegchooseLogin();
		}else{
			dialogBoxWrongNumber("+" + countryCode + "-" + num);
		}
	
	}

	public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
 
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
             
            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        } 
        return TYPE_NOT_CONNECTED;
    }

	protected void checkCountry() {
		// TODO Auto-generated method stub
		String countryCode = GetCountryZipCode();
		EditText mobile_no = (EditText) findViewById(R.id.et_entermobileno);
		String num = mobile_no.getText().toString();
		
		if(num.startsWith("00")){
			num = num.substring(2);	
			
		}
		if(num.startsWith("0") || num.startsWith("+")){
			num = num.substring(1);	
			}
		
		
		
		if(num.startsWith(countryCode)){
			num = num.substring(countryCode.length());	
			
		}
		if(num != null)
		mobile_no.setText(num);
	
	}

	public void callBackPage(View v) {
		
		Long check = prefs.getLong(screen1_status, 0);
			flipper_rightTOleft_second();
			callbackPage.setVisibility(ImageView.GONE);
			EditText fouredit = (EditText) findViewById(R.id.et_enterfourdigit);
			fouredit.setText("");
	}
	
	public void call(View v) {
		

		
		//new Senduserdetail().execute();
		
	//new CallingService().execute();
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegChooseLogin.this);
		alertDialog.setTitle("Get code via call");
        alertDialog.setMessage("Click 'Proceed' to receive the verification code via phone call.");
        alertDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog,int which) {
 
            	new CallingService().execute();
           
            }
        });
 

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
			public void onClick(DialogInterface dialog, int which) {
          
            dialog.cancel();
            }
        });

        alertDialog.show();	
	
	
	}
	
	private class CallingService extends AsyncTask<Void, Void, String> {

		ProgressDialog mProgressDialog1;

		@Override
		protected void onPostExecute(String result) {
			//mProgressDialog1.dismiss();
			
		}

		@Override
		protected void onPreExecute() {
		//	mProgressDialog1 = ProgressDialog.show(RegSignUp.this,
				//	"Loading...", "Data is Loading...");
		}

		@Override
		protected String doInBackground(Void... params) {
			String val = callService();
			if (val.equals("true")) {
				Log.d("yay", "SUCCESS");
				return val;
			} else {
				
				Log.d("err", "ERROR");
				return "false";
			}
		}

	}
	
	public String callService() {

		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			
			boolean network_check = isNetworkAvailable();
			String url = "null;
			HttpPost httppost = new HttpPost(url);

			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");
				if (network_check) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				}
				else {
					Intent intent = new Intent(this, NoNetwork.class);
					
					startActivity(intent);
					return "false";
				}

				return "true";
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return "false";
		}

	}
	
	
	public void callSMSRend(View v) {
		prefs.edit().putBoolean(wait_screen, true).commit();
		
		new Senduserdetail().execute();
	}
	
	private class Senduserdetail extends AsyncTask<Void, Void, String> {

		ProgressDialog mProgressDialog1;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog1.dismiss();
			String value = result;
			if (value.equals("true")) {
			/*
				Intent intent = new Intent(RegChooseLogin.this, RegChooseLogin.class);
				intent.putExtra("mobile_no", user_mobile_no);
				startActivity(intent);
				
				finish();
				
				*/
				flipper_leftTOright();
				
			//	beginYourTask();
			} 
			else {
				Log.d("ERROR", "Bug Handling needs to be done here");
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog1 = ProgressDialog.show(RegChooseLogin.this,"Loading...", "Data is Loading...");
		}

		@Override
		protected String doInBackground(Void... params) {
			String val = sending_user_details();
			if (val.equals("true")) {
				Log.d("yay", "SUCCESS");
				return val;
			} else {
				
				Log.d("err", "ERROR");
				return "false";
			}
		}

		
		
	}
	
	public String sending_user_details() {
	String cname=prefs.getString(stored_user_country, "").replaceAll("\\s+","");

		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			long millis=System.currentTimeMillis();
			prefs.edit().putLong(Stored_current_time, millis).commit();
			boolean network_check = isNetworkAvailable();
			String url = "null");
		
			HttpPost httppost = new HttpPost(url);

			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");
				if (network_check) {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				}
				else {
					Intent intent = new Intent(this, NoNetwork.class);
					
					startActivity(intent);
					//replaceContentView("NoNetwork", intent);

					return "false";
				}

				return "true";
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return "false";
		}

	}
	
	public void sendmatchdetails(View v) {
		EditText fouredit = (EditText) findViewById(R.id.et_enterfourdigit);
		four_digit_code = fouredit.getText().toString();
		System.out.println("four_digit_value is " + four_digit_code);
		if(four_digit_code.length() > 3)
			new Send_verification_code2().execute();
		else
			Toast.makeText(RegChooseLogin.this, "Please Enter the valid four digit code.", Toast.LENGTH_SHORT).show();

	}
	
	private class Send_verification_code2 extends AsyncTask<Void, Void, String> {

		ProgressDialog mProgressDialog1;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog1.dismiss();

			if (result.equals("true")) {
				try {
					prefs.edit()
							.putString(stored_four_digit_code, four_digit_code)
							.commit();
					prefs.edit().putLong(screen2_status, 1).commit();
					prefs.edit().putString(stored_user_dialer_password, Returned_password).commit();
					Log.d("stored user dialer password in regsignup ",Returned_password);
				} catch (Exception e) {
					e.printStackTrace();
				}

				//FragmentManager fragmentManager = getFragmentManager();
				//fragmentManager.beginTransaction().replace(R.id.Frame_Layout, new R4wActivation()).commit();
				/*
				Intent intent = new Intent(RegChooseLogin.this, R4wHome.class);
				startActivity(intent);
				finish();
				*/
				
				flipper_leftTOright_second();
				
				new GCMRegistration(RegChooseLogin.this);
				
			} else {

				Toast.makeText(getApplicationContext(), "Incorrect Details",
						Toast.LENGTH_LONG).show();

				//TextView entered_country_name = (TextView) findViewById(R.id.entered_country_name);
				//entered_country_name.setTypeface(nexaNormal);
				//TextView entered_country_code = (TextView) findViewById(R.id.entered_country_code);
				//entered_country_code.setTypeface(nexaNormal);
				//TextView entered_mobile_no = (TextView) findViewById(R.id.entered_mobile_no);
				//entered_mobile_no.setTypeface(nexaNormal);
				
				
				//entered_country_name.setText("Country = "+ prefs.getString(stored_user_country, ""));
			//	entered_country_code.setText("Country code = "+ prefs.getString(stored_user_country_code, ""));
			//	entered_mobile_no.setText("Mobile No. = "+ prefs.getString(stored_user_mobile_no, ""));
			
			}
		}

		@Override
		protected void onPreExecute() {
			System.out.println("in onpreexecute");
			mProgressDialog1 = ProgressDialog.show(RegChooseLogin.this,"Loading...", "Data is Loading...");
		}

		@Override
		protected String doInBackground(Void... params) {
			String val = sending_verification_code();
			if (val.equals("true")) {
				value = "true";
			} else {
				value = "false";
			}

			return value;
		}
	
	}
	
	private void flipper_leftTOright() {
		callbackPage.setVisibility(ImageView.VISIBLE);
		flipper1.setDisplayedChild(flipper1.indexOfChild(findViewById(R.id.vf_first)));
		flipper1.setInAnimation(inFromRightAnimation());
        flipper1.setOutAnimation(outToLeftAnimation());
        flipper1.showNext();
        prefs.edit().putLong(screen1_status, 1).commit();
        prefs.edit().putBoolean(wait_screen, false).commit();
        
	}
	
	private void flipper_leftTOright_second() {
		prefs.edit().putLong(screen1_status, 2).commit();
		flipper1.setDisplayedChild(flipper1.indexOfChild(findViewById(R.id.vf_second)));
		flipper1.setInAnimation(inFromRightAnimation());
        flipper1.setOutAnimation(outToLeftAnimation());
        flipper1.showNext();
        callbackPage.setVisibility(ImageView.GONE);
        prefs.edit().putLong(screen1_status, 2).commit();
	}
	private void flipper_rightTOleft() {
		flipper1.setDisplayedChild(flipper1.indexOfChild(findViewById(R.id.vf_second)));
		flipper1.setInAnimation(inFromLeftAnimation());
        flipper1.setOutAnimation(outToRightAnimation());
        flipper1.showNext();

        prefs.edit().putLong(screen1_status, 0).commit();
	}
	
	private void flipper_rightTOleft_second() {
		flipper1.setInAnimation(inFromRightAnimation());
        flipper1.setOutAnimation(outToLeftAnimation());
        flipper1.showPrevious();
        prefs.edit().putLong(screen2_status, 0).commit();
        prefs.edit().putLong(screen1_status, 1).commit();
	}

	private Animation inFromRightAnimation() {

	Animation inFromRight = new TranslateAnimation(
	Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
	);
	
	inFromRight.setDuration(500);
	inFromRight.setInterpolator(new AccelerateInterpolator());
	return inFromRight;
	}
	private Animation outToLeftAnimation() {
	Animation outtoLeft = new TranslateAnimation(
	  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f,
	  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
	);
	outtoLeft.setDuration(500);
	outtoLeft.setInterpolator(new AccelerateInterpolator());
	return outtoLeft;
	}

	private Animation inFromLeftAnimation() {
	Animation inFromLeft = new TranslateAnimation(
	Animation.RELATIVE_TO_PARENT,  -1.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
	Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
	);
	inFromLeft.setDuration(500);
	inFromLeft.setInterpolator(new AccelerateInterpolator());
	return inFromLeft;
	}
	private Animation outToRightAnimation() {
	Animation outtoRight = new TranslateAnimation(
	  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f,
	  Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
	);
	outtoRight.setDuration(500);
	outtoRight.setInterpolator(new AccelerateInterpolator());
	return outtoRight;
	}

	
	public void uploadUserImage() {
		
		String path = prefs.getString(stored_userimagepath, "");
		if(path.equals(""))
		{
			Toast.makeText(getApplicationContext(), "Please Browse the Image", Toast.LENGTH_LONG).show();
		}else{
			new AsyncTaskProfilePicUpload(path, getApplicationContext() , prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "")).execute();
		}
	}

	private void dialogBoxNoInternet(){
		new AlertDialog.Builder(RegChooseLogin.this)
		.setTitle("No Network")
		.setMessage("There is no internet connection")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}).show();
	
	}
	
	private void dialogBoxWrongNumber(String number){
		new AlertDialog.Builder(RegChooseLogin.this)
		.setTitle("Alert!")
		.setMessage("The number you have entered seems to be incorrect.\n" + number + "\nPlease check again.")
		.setPositiveButton("Proceed Anyway", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				callsmsverificationRegchooseLogin();
				
			}
		})
		.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		.show();
	
	}
	
	
	public void browse_userPic(View v){
		
		if(getConnectivityStatus(getApplicationContext()) == TYPE_NOT_CONNECTED){
			dialogBoxNoInternet();
		}else{

			selectBrowse = 1;
			
			
		    Intent intent = new Intent(); 

		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
	
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 400);
		try {
			intent.putExtra("return-data", true);
			startActivityForResult(Intent.createChooser(intent,"Complete action using"), 2);

			} catch (ActivityNotFoundException e) {
				// Do nothing for now
			}

		}
	}
	public void callSubmit(View v){
		if(selectBrowse == 0)
		{
			Toast.makeText(getApplicationContext(), "Please Browse first", Toast.LENGTH_LONG).show();
		}else{
			prefs.edit().putLong(screen2_status, 1).commit();
			prefs.edit().putString(prefChooseclass, "SipHome").commit();
			
			
			System.out.println("Continue btn click");
			if(prefSignUpProcess.equals("NotCompleted")){
				/*
				Intent r4wContactsLoading = new Intent(this,R4wContactsLoading.class);
				startActivity(r4wContactsLoading);
				finish();
				*/
				
				Log.d(TAG,"prefSignUpProcess ==NotCompleted");
				Intent r4wSplashScreen= new Intent(this,R4WSplashScreen.class);
				r4wSplashScreen.putExtra("R4wContactStatus", "unloaded");
				startActivity(r4wSplashScreen);
				finish();
				
			
			}
			else{
				Log.d(TAG,"prefSignUpProcess ==callSubmit");
				Intent intent = new Intent(this, SipHome.class);
				startActivity(intent);
				finish();
			}
			/*
			System.out.println("***prefSignUpProcess value 2== "+prefSignUpProcess);
			Intent intent = new Intent(RegChooseLogin.this, SipHome.class);
			startActivity(intent);
			finish();
			*/
		
		}
		
	}
	
	public void editusername(View v) {
		if(getConnectivityStatus(getApplicationContext()) == TYPE_NOT_CONNECTED){
			dialogBoxNoInternet();
		}else{
			dialogboxUsreName();
		}
	}
	
	public void callSkip(View v){
		prefs.edit().putLong(screen2_status, 1).commit();
		prefs.edit().putString(prefChooseclass, "SipHome").commit();
			
		System.out.println("***prefSignUpProcess valuen 3 == "+prefSignUpProcess);
		
		/* code r4wContacts list */
		if(prefSignUpProcess.equals("NotCompleted")){
			System.out.println(" $$$$$$ status not completed");
			Intent intent = new Intent(RegChooseLogin.this, R4WSplashScreen.class);
			intent.putExtra("R4wContactStatus", "unloaded");
			startActivity(intent);
			finish();
			
		}else
		{	
			Intent intent = new Intent(RegChooseLogin.this, SipHome.class);
			startActivity(intent);
			finish();
		}
		/*
		Intent intent = new Intent(RegChooseLogin.this, SipHome.class);
		startActivity(intent);
		finish();
		*/
		
	}
	
	public void dialogboxUsreName() {


		final String userName = prefs.getString("userNameKey", "");
		
		final String number; 
		number = prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "");

		
		final Dialog dialoguserName = new Dialog(RegChooseLogin.this);
	//	dialoguserName.setTitle("Edit User Name");
		dialoguserName.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialoguserName.setContentView(R.layout.dialogboxforusername);
		dialoguserName.show();
		Button btnOk = (Button) dialoguserName.findViewById(R.id.btnUserNameOk);
		Button btnCancel = (Button) dialoguserName
				.findViewById(R.id.btnUserNameCancel);
		final EditText edtUserName = (EditText) dialoguserName.findViewById(R.id.EdtUserName);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				if(edtUserName.getText().length() > 0){
						
				int action = 0;
				
				if(!userName.equalsIgnoreCase(""))
					action = 1;
	
				
				String getUserName = edtUserName.getText().toString();
				tvuserName.setText(getUserName);  
				prefs.edit().putString(stored_username, getUserName).commit();
				selectBrowse = 1;
				TextView tv_skip = (TextView) findViewById(R.id.tv_reg_skip);
				tv_skip.setVisibility(TextView.INVISIBLE);
				tv_skip.setClickable(false);
				
				Log.d("UserName inside the dialogbox", getUserName);
				dialoguserName.dismiss();
				new AsynctaskUpdateUsername(RegChooseLogin.this, number, getUserName, action).execute();
			}else {
				Toast.makeText(RegChooseLogin.this, "Please enter Valid name", Toast.LENGTH_SHORT).show();
			}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialoguserName.dismiss();
			}
		});

	
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("onActivityResult", " in");
		Log.d("resultCode", resultCode + " in");
	    if(requestCode == RESULT_CANCELED && resultCode == 0 && resultCode == -1  )
        {
        	Toast.makeText(RegChooseLogin.this, "Please try again", Toast.LENGTH_SHORT).show();
        }else if (requestCode == 2 && data != null) {
			try{
        	Bundle extras2 = data.getExtras();
        	Log.d("selectedImage", " before");
			String picturePath ="";
			if (extras2 != null) {
				Bitmap bmp = extras2.getParcelable("data");
				Drawable imd = new BitmapDrawable(getResources(),bmp);
				picturePath = saveUserImage(bmp);
				Log.d("picturePath", picturePath + " !" );
				
				prefs.edit().putString(stored_userimagepath, picturePath).commit();
				
				bmp = ImageHelperCircular.getRoundedCornerBitmap(bmp, 200);
				imgview.setImageBitmap(bmp);

			}
			
			}catch (Exception e){
				
			}
			
		}
	    
	    uploadUserImage();
        super.onActivityResult(requestCode, resultCode, data);
        
	
	}
	
	@SuppressLint("SdCardPath") 
	private String saveUserImage(Bitmap userBitmap) {

		// TODO Auto-generated method stub
		
		File imageDirectory = new File("/sdcard/R4W/");
	     
	     if(!imageDirectory.exists()){
	    	 imageDirectory.mkdir();
	     }
	     File imageDirectoryprofile = new File("/sdcard/R4W/ProfilePic");
	     if(!imageDirectoryprofile.exists()){
	    	 imageDirectoryprofile.mkdir();
	     }
	        
	     File file = new File(imageDirectoryprofile.getAbsolutePath(),"ProfilePic.png");
	     try {
			if(file.exists())
			 {
			   file.delete();
			 }
		 file.createNewFile();
		 String usernum = prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "");
		 File userfile = null;
		 if(!usernum.equals("")){
		 userfile = new File(imageDirectoryprofile.getAbsolutePath(), usernum + ".png");
			if(userfile.exists())
			 {
				userfile.delete();
			 }
			userfile.createNewFile();
	     }
		//Convert bitmap to byte array
		Bitmap bitmap = userBitmap;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		if(userfile != null){
			FileOutputStream fosuser = new FileOutputStream(userfile);
			fosuser.write(bitmapdata);
			fosuser.close();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bitmapdata);
		fos.close();
	     } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     
	     return file.getAbsolutePath();
	
	}
	
		class AsyncTaskDownloadImage extends AsyncTask<Void, Void, Boolean> {
		String imagePathUri;
		ProgressDialog mProgressDialog;
		AsyncTaskDownloadImage(String uriImage){
			imagePathUri = uriImage;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d("onPreExecute", "onPreExecute -in");
			
			mProgressDialog = new ProgressDialog(RegChooseLogin.this);
			mProgressDialog.setMessage("loading");
			mProgressDialog.show();
			mProgressDialog.setCancelable(false);
			Log.d("onPreExecute", "onPreExecute out");
			
		}
	   
	
	

	@SuppressLint("NewApi") @Override
	protected void onPostExecute(Boolean result) {
		if(result == true){
			Toast.makeText(getApplicationContext(), "File Upload Completed", Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(getApplicationContext(), "File is not uploaded ", Toast.LENGTH_LONG).show();
		}
		mProgressDialog.dismiss();
		
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		int response = 0;
		Log.d("doInBackgroud ", imagePathUri + " path ");
		if(imagePathUri.equals(""))
		{	return false;
		}else{
			response = uploadFile(imagePathUri);
		}
		Log.d("response ", response + "  d");
		
		if (response == 200) {
			Log.d("doInBackgroud", "doInBackground");
			return true;
		} else {
			return false;
		}
		
	}


	
	}
	
	public Bitmap ShrinkBitmap(String file, int width, int height)
	{
	    BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
	    bmpFactoryOptions.inJustDecodeBounds = true;
	    Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

	    int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
	    int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

	    if(heightRatio > 1 || widthRatio > 1)
	    {
	        if(heightRatio > widthRatio)
	        {
	            bmpFactoryOptions.inSampleSize = heightRatio;
	        }
	        else
	        {
	            bmpFactoryOptions.inSampleSize = widthRatio;
	        }
	    }

	    bmpFactoryOptions.inJustDecodeBounds = false;
	    bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
	    return bitmap;
	}
	
	 public void beginYourTask()
	 {
		    user_mobile_no = prefs.getString(stored_user_mobile_no, "");
			callbackPage.setVisibility(ImageView.VISIBLE);
		    progressDialog=new ProgressDialog(this);
		    progressDialog.setCancelable(false);
		    progressDialog.setMessage("Please wait for 30 seconds while we read an SMS sent to your phone.");
		    progressDialog.setTitle("Reading Verification Code...");
		    progressDialog.setIcon(R.drawable.ic_launcher);
		    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		    progressDialog.setProgressPercentFormat(null);
		    progressDialog.setProgress(0);
		    progressDialog.setMax(30);
		    progressDialog.show();
		    progressStatus=0;
		    new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						while(progressStatus<7)
						{
							progressStatus=performTask();
						    progressDialog.setProgress(progressStatus*4);	

						}
						/*Hides the Progress bar*/
						myHandler.post(new Runnable() {

							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();//dismiss the dialog
							  // Toast.makeText(getBaseContext(),"SMS not read",Toast.LENGTH_LONG).show();
			                   progressStatus=0;
			                   Log.d("callflipper-right", "1");
								flipper_leftTOright();
			                   myProgress=0;

							}
						});

					}
					/* Do some task*/
					private int performTask()
					{
						try {
							//---simulate doing some work---
						//	four_digit_code = readSMS();
							if(four_digit_code!="0"){
								if(read_from_thread==0){
									new Send_verification_code().execute();
									read_from_thread=1;
									Log.d("callflipper-right", "1");
									flipper_leftTOright();
								}
								Thread.interrupted();
							}
							
							waitScrean = prefs.getBoolean(wait_screen, true);
							if(waitScrean == true)
							{
								Thread.sleep(4000);
								}
							
						} 
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						return ++myProgress;	
					}
				}).start();
		    
		 
	 }
		 
	 private String readSMS() {

			Uri uriSMSURI = Uri.parse("content://sms/inbox");
			Cursor cur = getContentResolver().query(uriSMSURI, null, null, null,
					null);

			smsCode = "0";
			String st[] = cur.getColumnNames();
			int count=0;
			while (cur.moveToNext()) {
				if(count>6)
				{
					return smsCode;
				}
				//if (cur.getString(cur.getColumnIndex("address")).equals("ROAMING4WOR")) {
					try {

						current_time = prefs.getLong(Stored_current_time, 0);
						String msg = cur.getString(cur.getColumnIndex("body"));
						
						String pattern = "(\\d{4})";
						
						Pattern r = Pattern.compile(pattern);
						Matcher m = r.matcher(msg);
						
						if (current_time < cur.getLong(cur.getColumnIndex("date"))) {
							
							if (m.find()) {
								smsCode = m.group(0);
								four_digit_code = smsCode;

								return smsCode;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				//}
					count++;
			}

			int counter = 1;
			while (cur.moveToNext()) {
				if (counter > 5) {
					break;
				}
				/*
				 * MESSAGE CURSOR COLUMN ID DETAILS :
				 * 
				 * 1. long threadId 2. String address 3. long contactId 4. long
				 * timeStamp 5. String body
				 */
				System.out.println("before roaming4world");
				if (cur.getString(2).equals("ROAMING4WOR")) {
					current_time = prefs.getLong(Stored_current_time, 0);

					String msg = cur.getString(cur.getColumnIndex("body"));
					String pattern = "(\\d{4})";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(msg);
					if (current_time < cur.getLong(cur.getColumnIndex("date"))) {
						if (m.find()) {
							smsCode = m.group(0);
							four_digit_code = smsCode;
							System.out.println("four digit code is "
									+ four_digit_code);
							return smsCode;
						}
					}
				}
				counter++;
			}
			return smsCode;
		
	 }
		 
	 
	 private class Send_verification_code extends AsyncTask<Void, Void, String> {

			//ProgressDialog mProgressDialog1;

			@Override
			protected void onPostExecute(String result) {
				//mProgressDialog1.dismiss();

				Log.d("onPost0","0");
				
				
				if (result.equals("true")) {
					try {
						prefs.edit().putString(stored_four_digit_code, four_digit_code).commit();
						prefs.edit().putLong(screen2_status, 1).commit();
						prefs.edit().putString(stored_user_dialer_password, Returned_password).commit();
						Log.d("stored user dialer password in regsignup ",Returned_password);
						
					} catch (Exception e) {
						
						e.printStackTrace();
					}

	/*  change here for auto read sms  code*/
					
					AlertDialog.Builder alertDialogBuilder_send = new AlertDialog.Builder(RegChooseLogin.this);
					alertDialogBuilder_send.setTitle("Congratulations !! ");
					alertDialogBuilder_send
							.setMessage("Your phone number has been successfully verified by automatically reading an SMS sent to your phone. Press OK to proceed.")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int id) {
										
											progressDialog.dismiss();
											
											System.out.println("  prefSignUpProcess value 4 == "+prefSignUpProcess);
											prefs.edit().putString(prefChooseclass, "SipHome").commit();
											Intent intent = new Intent(RegChooseLogin.this, SipHome.class);
											startActivity(intent);
											finish();
											dialog.cancel();
											
										}
									});
					AlertDialog alertDialog_send = alertDialogBuilder_send.create();
					alertDialog_send.show();
					
					
					
				} else {

					prefs.edit().putLong(screen1_status, 0).commit();
					//flipper_rightTOleft();
				}
			}

			@Override
			protected void onPreExecute() {
				// System.out.println("in onpreexecute");
				//mProgressDialog1 = ProgressDialog.show(RegSignUp.this,"Loading...", "Data is Loading...");
			}

			@Override
			protected String doInBackground(Void... params) {

				String val = sending_verification_code();
				if (val.equals("true")) {
					value = "true";
				} else {
					value = "false";
				}

				return value;
			}
		
	 }
	 
	 
	 public String sending_verification_code() {
		 

			try {

				HttpParams p = new BasicHttpParams();

				p.setParameter("user", "1");

				HttpClient httpclient = new DefaultHttpClient(p);

				String url = "null"
						+ user_mobile_no + "&four_digit_code=" + four_digit_code+ "&type=r4w";

				HttpPost httppost = new HttpPost(url);

				try {
					Log.i(getClass().getSimpleName(), "send  task - start");

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair("user", "1"));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String responseBody = httpclient.execute(httppost,
							responseHandler);

					JSONObject json = new JSONObject(responseBody);
					System.out.println(json);
					Returned_password=json.getString("password");
					String Match_status = json.getString("status");
					String SupportNumber = json.getString("support_no");
					prefs.edit().putString(stored_server_ipaddress, json.getString("server_ip")).commit();
					prefs.edit().putString(stored_supportnumber, SupportNumber).commit();
					Log.d("SupportNumber", SupportNumber);
					return Match_status;
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "false";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "false";
				}
			} catch (Throwable t) {
				// Toast.makeText(this, "Request failed: " +
				// t.toString(),Toast.LENGTH_LONG).show();
				t.printStackTrace();
				return "false";
			}
		
	 }
	 
	
		public int uploadFile(String sourceFileUri) {

			
		  	  String upLoadServerUri = "";
		  	  upLoadServerUri = "null";
		  	  String fileName = sourceFileUri;

		  	  HttpURLConnection conn = null;
		  	  DataOutputStream dos = null;  
		  	  String lineEnd = "\r\n";
		  	  String twoHyphens = "--";
		  	  String boundary = "*****";
		  	  int bytesRead, bytesAvailable, bufferSize;
		  	  byte[] buffer;
		  	  int maxBufferSize = 1 * 1024 * 1024; 
		  	  File sourceFile = new File(sourceFileUri); 
		  	  if (!sourceFile.isFile()) {
		  	   Log.e("uploadFile", "Source File Does not exist");
		  	   return 0;
		  	  }
			    	  try { // open a URL connection to the Servlet
			    	   FileInputStream fileInputStream = new FileInputStream(sourceFile);
			    	   URL url = new URL(upLoadServerUri);
			    	   conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
			    	   conn.setDoInput(true); // Allow Inputs
			    	   conn.setDoOutput(true); // Allow Outputs
			    	   conn.setUseCaches(false); // Don't use a Cached Copy
			    	   conn.setRequestMethod("POST");
			    	   conn.setRequestProperty("Connection", "Keep-Alive");
			    	   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			    	   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			    	   conn.setRequestProperty("uploaded_file", fileName);
			    	  
			    	   dos = new DataOutputStream(conn.getOutputStream());
			
			    	   dos.writeBytes(twoHyphens + boundary + lineEnd); 
			    	   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "-" + prefs.getString(stored_user_country_code, "") +prefs.getString(stored_user_mobile_no, "") + "\"" + lineEnd);
			    	   dos.writeBytes(lineEnd);
			
			    	   bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
			
			    	   bufferSize = Math.min(bytesAvailable, maxBufferSize);
			    	   buffer = new byte[bufferSize];
			
			    	   // read file and write it into form...
			    	   bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
			    	    
			    	   while (bytesRead > 0) {
			    	     dos.write(buffer, 0, bufferSize);
			    	     bytesAvailable = fileInputStream.available();
			    	     bufferSize = Math.min(bytesAvailable, maxBufferSize);
			    	     bytesRead = fileInputStream.read(buffer, 0, bufferSize);	    	    
			    	    }
			
			    	   // send multipart form data necesssary after file data...
			    	   dos.writeBytes(lineEnd);
			    	   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			    	   // Responses from the server (code and message)
			    	   serverResponseCode = conn.getResponseCode();
			    	   String serverResponseMessage = conn.getResponseMessage();
			    	   
			    	   Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
			    	   
			    	  
			    	   //close the streams //
			    	   fileInputStream.close();
			    	   dos.flush();
			    	   dos.close();
			    	   
			      } catch (MalformedURLException ex) {  
			    	  ex.printStackTrace();
			    	  Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
		  	  } catch (Exception e) {
		  		  e.printStackTrace();
		  		  Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
		  	  }
		  	  return serverResponseCode;  
		  	 } 

	    
			
	 
	 
	public void setListDataRegchooseLogin() {
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

	public void callsmsverificationRegchooseLogin() {
		boolean isempty = check_empty_fieldsRegchooseLogin();
		if (isempty) {
			new SenduserdetailRegchooseLogin().execute();
		}
	}

	private class SenduserdetailRegchooseLogin extends AsyncTask<Void, Void, String> {
		ProgressDialog mProgressDialog1;
		@Override
		protected void onPostExecute(String result) {
			mProgressDialog1.dismiss();
			String value = result;
			if (value.equals("true")) {
				prefs.edit().putLong(screen1_status, 1).commit();
				prefs.edit().putString(stored_user_mobile_no, user_mobile_no).commit();
				prefs.edit().putString(stored_user_country, countryName).commit();
				prefs.edit().putString(stored_user_country_code, countryCode).commit();
				prefs.edit().putBoolean(wait_screen, true);
			//	beginYourTask();
				flipper_leftTOright();
			}
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog1 = ProgressDialog.show(RegChooseLogin.this,
					"Loading...", "Data is Loading...");
		}

		@Override
		protected String doInBackground(Void... params) {
			String val = sending_user_detailsRegchooseLogin();
			if (val.equals("true")) {
				return val;
			} else {
				return "false";
			}
		}
	}

	@SuppressWarnings("deprecation")
	public String sending_user_detailsRegchooseLogin() {
		Log.d("sending_user_detailsRegchooseLogin", "call");
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			EditText mobile_no = null;
			try {
				mobile_no = (EditText) findViewById(R.id.et_entermobileno);
				user_mobile_no = mobile_no.getText().toString();
				user_mobile_no = user_mobile_no.replaceFirst("^0*", "");
				Log.d("user Map number without Zero", user_mobile_no);
			} catch (Exception e) {
				e.printStackTrace();
			}

		
			HttpClient httpclient = new DefaultHttpClient(p);
			long millis = System.currentTimeMillis();
			prefs.edit().putLong(Stored_current_time, millis).commit();
			boolean network_check = isNetworkAvailable();
			String cname= countryName.replaceAll("\\s+","");
			
			String url = null;
			Log.d("url", url);
			HttpPost httppost = new HttpPost(url);
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");
				if (network_check) {
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
					nameValuePairs.add(new BasicNameValuePair("user", "1"));
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String responseBody = httpclient.execute(httppost,responseHandler);
				} else {
					Intent intent = new Intent(this, NoNetwork.class);
					startActivity(intent);
					return "false";
				}

				return "true";
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			}
		} catch (Throwable t) {
			return "false";
		}
	}

	private int getPostitonRegchooseLogin(String locationid) {
		int i, position = 0;
		getAllCountryCode.moveToFirst();
		for (i = 0; i < getAllCountryCode.getCount() - 1; i++) {
			getAllCountryCode.moveToNext();
			String locationVal = getAllCountryCode.getString(getAllCountryCode
					.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			if (locationVal.equals(locationid)) {
				position = i + 1;
				break;
			} else {
				position = 0;
			}
		}
		return position;
	}

	private int getPostitonDestRegchooseLogin(String locationid) {
		int i, position = 0;
		getAllCountryCode.moveToFirst();
		for (i = 0; i < getAllCountryCode.getCount() - 1; i++) {
			getAllCountryCode.moveToNext();
			String locationVal = getAllCountryCode.getString(getAllCountryCode
					.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			if (locationVal.equals(locationid)) {
				position = i + 1;
				break;
			} else {
				position = 0;
			}
		}
		return position;
	}

	public boolean check_empty_fieldsRegchooseLogin() {
		EditText mobile_no = (EditText) findViewById(R.id.et_entermobileno);
		String user_mobile_no = mobile_no.getText().toString();
		user_mobile_no = user_mobile_no.replaceFirst("^0*", "");
	
		
		if (countryName.equals("COUNTRIES")) {
			Toast.makeText(RegChooseLogin.this, "Please select a Country",
					Toast.LENGTH_LONG).show();

		} else if (user_mobile_no.equals("")) {
			Toast.makeText(
					RegChooseLogin.this,
					getResources()
							.getString(R.string.promo_not_entered_phoneno),
					Toast.LENGTH_LONG).show();
		} 
		
		else if (!countryName.equals("COUNTRIES")&&!user_mobile_no.equals("")) {
			
				return true;
			
		}
		return false;
	}

	public void calltermsviewRegchooseLogin(View v) {
		Intent intent = new Intent(this, termsandconditions.class);
		startActivity(intent);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public String getCountryZipCodeRegchooseLogin() {
		String CountryZipCode = "";
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}
	
	

	public String get_country_name_via_codeRegchooseLogin() {
		get_country_name = mySQLiteAdapter.fetch_country_name_dest(simCountry);
		String country_name = "";
		get_country_name.moveToFirst();
		while (!get_country_name.isAfterLast()) {
			country_name = get_country_name.getString(get_country_name.getColumnIndex(SQLiteAdapter.CD_COUNTRY_NAME));
			get_country_name.moveToNext();
		}
		return country_name;
	}

	public void dialogboxRegchooseLogin() {

		final Dialog dialog = new Dialog(RegChooseLogin.this);
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
		btnSkip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}
	
	String GetCountryZipCode(){

        String CountryID="";
        String[] rl=this.getResources().getStringArray(R.array.CountryCodesWithname);
        for(int i=0;i<rl.length;i++){
        	
        	
                                    String[] g=rl[i].split(",");
                                    System.out.println("Array is =="+g);
                                    
                                    
                                    
                                    
                                    if(g[0].trim().equals(countryName)){
                                    	
                                                        String CountryZipCode=g[1];
                                                        System.out.println("  =="+CountryZipCode);
                                                        return CountryZipCode;
                                                     
                                                        
                                    }
                                    
        }
        return "";
  }
	
}
