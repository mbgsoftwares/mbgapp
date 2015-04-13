package com.roamprocess1.roaming4world.roaming4world;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
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

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi;
import com.roamprocess1.roaming4world.api.SipConfigManager;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.api.SipProfileState;
import com.roamprocess1.roaming4world.db.DBProvider;
import com.roamprocess1.roaming4world.models.Filter;
import com.roamprocess1.roaming4world.ui.account.AccountsEditList;
import com.roamprocess1.roaming4world.ui.dialpad.DialerFragment;
import com.roamprocess1.roaming4world.ui.messages.ConversationsListFragment;
import com.roamprocess1.roaming4world.utils.CustomDistribution;
import com.roamprocess1.roaming4world.utils.PreferencesProviderWrapper;
import com.roamprocess1.roaming4world.utils.PreferencesWrapper;
import com.roamprocess1.roaming4world.utils.backup.BackupWrapper;
import com.roamprocess1.roaming4world.wizards.BasePrefsWizard;
import com.roamprocess1.roaming4world.wizards.WizardIface;
import com.roamprocess1.roaming4world.wizards.WizardUtils;
import com.roamprocess1.roaming4world.wizards.WizardUtils.WizardInfo;
public  class R4wHome extends SherlockFragmentActivity   {

	private Cursor getCountryNamebyContryCode, getAllCountryCode, dataCheckPin;
	private static final String TAG = "r4wHome";
	
	private static FragmentTransaction ft = null;
	private static SherlockFragment fragment = null;
	
    private ConversationsListFragment mMessagesFragment;
	@SuppressWarnings("unused")
	private String getPin, getCountryCode, s_fetch_self_no, mapNumber,
			countryName, country_nam = "", userName = "User Name", result,
			deActivatStatus_pref, storedDeactivateStatus,
			userInfo = "UserInfo", imageaddres, userImagePath = "",
			userNameKey = "", stored_user_dialer_password,
			get_user_dialer_password, stored_account_register_status,
			stored_registered_account, prefChooseclass, stored_user_leftDrawer_status;
	static int service_count = 0, fetch_flag, pinNo, called_this_time=0;
	boolean unknownhost = false, serviceavail = false;
	private LocationManager locationManager;
	private Location location;
	private TelephonyManager telephonyManager;
	private TextView txtVChooseYour, txtVCountry, txtVEnterDesti,
			txtVDestiPhone;
	private EditText edtUserName;
	JSONObject proofOfPayment;
	private boolean hasTriedOnceActivateAcc = false;
	private SQLiteAdapter mySQLiteAdapter;
	public SharedPreferences prefs, prefUserInfo;
	private final static String APP_TITLE = "Roaming4world";
	private final static String APP_PNAME = "com.roamprocess1.roaming4world";
	private Context mContext;
	private Uri selectedImage;
	final int PIC_CROP = 2;
	AccountStatusContentObserver statusObserver = null;
	EditText dial4world_username, dial4world_password;
	String username, password, stored_user_mobile_no, stored_user_country_code;
	String account_name, user_name, server, password_response, show;
	ArrayList<SipProfile> result_profile, result_profile_login;
	public SipProfile account = null;
	long accountId = -1;
	PreferencesWrapper prefs_wrapper;
	private WizardIface wizard = null;
	private String wizardId = "";
	int read_val;
	android.support.v4.app.Fragment fragment_dialer;
	DialerFragment fragment_dialer1;
	private static android.support.v4.app.FragmentManager fragmentManager;
	Fragment fragment_oncreateoptions = null;
	ArrayList<String> dataArray_right = new ArrayList<String>();
	ArrayList<Object> objectArray_right = new ArrayList<Object>();
	ArrayList<String> dataArray_left = new ArrayList<String>();
	ArrayList<Object> objectArray_left = new ArrayList<Object>();
	DrawerLayout mDrawerlayout;
	ListView mDrawerList_Left, mDrawerList_Right;
	ActionBarDrawerToggle mDrawerToggle;
	ImageButton imgLeftMenu, imgRightMenu;
	PreferencesProviderWrapper prefProviderWrapper;
	private static int RESULT_LOAD_IMAGE = 1;

	ImageView userImageView, lluserImageView;
	Bitmap bm, userProfilepic;
	Button btnOk, btnCancel;
	Editor editorUserInfo;
	FragmentTabHost mTabHost;
	
	Bundle savBundle;
	
	static int userbitmap = 0;
	
	ListItemsAdapter_Left Left_Adapter;
	ListItemsAdapter_Right Right_Adapter;
	LinearLayout layout_left_drawer;
	
	ImageView statusleftListView_enable, statusleftListView_disable;
	static int checkstatusleftListView = 1;

	private Animation slideUp,slideDown;
	
	public static final int DIALER_MENU = Menu.FIRST + 1;
	public static final int CHAT_MENU = Menu.FIRST + 2;
	public static final int CONTACT_MENU = Menu.FIRST + 3;
	public static final int R4W_MENU = Menu.FIRST + 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// System.out.println("R4wHome  step1");
		savBundle = savedInstanceState;
	
		
		userbitmap = 0;
		
		ViewServer.get(this).addWindow(this);
		// Debug.startMethodTracing("calc");
		prefProviderWrapper = new PreferencesProviderWrapper(this);
		com.roamprocess1.roaming4world.utils.Log.setLogLevel(6);
		prefChooseclass = "com.roamprocess1.roaming4world.chooseClass";

		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_account_register_status = "com.roamprocess1.roaming4world.account_register_status";
		stored_registered_account = "com.roamprocess1.roaming4world.account_registered";
		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		// System.out.println("R4wHome  step2");
		stored_user_leftDrawer_status = "com.roamprocess1.roaming4world.leftDrawer_status";
		stored_user_dialer_password = "com.roamprocess1.roaming4world.dialer_password";
		get_user_dialer_password = prefs.getString(stored_user_dialer_password, "no password");
		Log.d("user dialer password in r4whome", get_user_dialer_password);
		prefUserInfo = getSharedPreferences(userInfo, Context.MODE_PRIVATE);
		// System.out.println("R4wHome  step3");
		editorUserInfo = prefUserInfo.edit();
		prefs.edit().putString(prefChooseclass, "1");
		
		String tempstatus = prefs.getString(stored_user_leftDrawer_status, "1");
		checkstatusleftListView = Integer.parseInt(tempstatus);
		
		 
		setContentView(R.layout.home);
		
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.Frame_Layout);
		
		mTabHost.getTabWidget().setBackgroundColor(Color.parseColor("#D6D6D6"));
		//mTabHost.getTabWidget().setBackgroundResource(R.drawable.bbg);
		
		//mTabHost.getTabWidget().setStripEnabled(false);
		
		int tabpostion =mTabHost.getCurrentTab();
		
		System.out.println("Tab Postion in=="+tabpostion);
		//mTabHost.setCurrentTab(mTabHost.getCurrentTab())
		
	   // Create Parent Tab1
		
		
		Roaming4WorldCustomApi.textordigit=false;
	
		
        slideUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slideDown =AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
       
        /*
        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setGravity(Gravity.BOTTOM);
            tv.setTextSize(8);
         
        }
         */  
        mTabHost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#FFFFFF"));
        
       
      
        
        tabpostion =mTabHost.getCurrentTab();
        System.out.println("Tab Postion in 6=="+tabpostion);
	
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		mContext = this;
		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();
		try {
			dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("R4wHome  step4");
		dataCheckPin.moveToFirst();
		getAllCountryCode = mySQLiteAdapter.fetch_country_codes();
		telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		boolean isNetworkAvailable = isNetworkAvailable();

		if (isNetworkAvailable == true) {
			Log.d(TAG, "isNetworkAvailable = true");
			//System.out.println("R4wHome  step5");
		} else {
			//System.out.println("R4wHome  step6");
			Intent intent_NoNetwork = new Intent(R4wHome.this, NoNetwork.class);
			startActivity(intent_NoNetwork);
			finish();
		}
		Log.d("R4wHome", "The stored account register status is in oncreate"
				+ prefs.getString(stored_account_register_status, "Connecting"));

		try
		{
			
		final LinearLayout onlinelayout= (LinearLayout) findViewById(R.id.onlineStatus);	
		TextView status_account = (TextView) findViewById(R.id.status);

		
		if (status_account != null) {
			
			Handler handler = new Handler();
			Runnable runnable=null;
			System.out.println("R4wHome");
			onlinelayout.invalidate();
			Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/NexaBold.otf");
			status_account.setTypeface(tf);
			status_account.setText(prefs.getString(stored_account_register_status, "Connecting"));
			
			if (prefs.getString(stored_account_register_status, "Connecting").equals("Online")) {
				onlinelayout.invalidate();
				onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideUp);
				onlinelayout.invalidate();
				
				/*Handler handler = new Handler();
	        	handler.postDelayed(new Runnable() 
	        	{
	        	@Override
	        	public void run()
	        	{
	        		System.out.println("Run method 1");
	        		onlinelayout.startAnimation(slideUp);
	        		onlinelayout.invalidate();
	        		onlinelayout.setVisibility(View.GONE);
	        
	        	}
	        	}, 10000);
				
				*/
				
				 runnable = new Runnable()
				 {
					 
					 public void run()
			        	{
			        		System.out.println("Run method 2");
			        		onlinelayout.invalidate();
			        		onlinelayout.startAnimation(slideDown);
			        		onlinelayout.invalidate();
			        		
			        		onlinelayout.setVisibility(View.GONE);
			        
			        	}
				 };handler.postDelayed(runnable, 10000); 
				
				
			} else if (prefs.getString(stored_account_register_status, "Connecting").equals("Connecting")) {
			
				handler.removeCallbacks(runnable);
				onlinelayout.invalidate();
				onlinelayout.setVisibility(View.VISIBLE);
				onlinelayout.setBackgroundColor(Color.parseColor("#F79141"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideUp);
				
			} else if (prefs.getString(stored_account_register_status,"Connecting").equals("Offline")) {
				handler.removeCallbacks(runnable);
				onlinelayout.invalidate();
				onlinelayout.setVisibility(View.VISIBLE);
				onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideUp);
				
			}
			onlinelayout.invalidate();
		}
		}
		catch(Exception e)
		{
			
		}
		//System.out.println("R4wHome  step8");
		if (statusObserver == null) {
			System.out.println("in after if statement of statusObserver");
			statusObserver = new AccountStatusContentObserver(serviceHandler);
			System.out.println("in after statusObserver object creation ");
			getContentResolver().registerContentObserver(SipProfile.ACCOUNT_STATUS_URI, true, statusObserver);
			System.out.println("in after registering content observer");
		}
		//System.out.println("R4wHome  step9");
		while (!dataCheckPin.isAfterLast()) {
			fetch_flag = dataCheckPin.getInt(dataCheckPin.getColumnIndex(SQLiteAdapter.FN_FLAG));
			if (fetch_flag == 0) {
				
			} else {
				getPin = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_PIN_NO));
				getCountryCode = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_COUNTRY_CODE));
				s_fetch_self_no = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_SELF_NO));

			}
			dataCheckPin.moveToNext();
		}

		//System.out.println("R4wHome  step10");
		hasTriedOnceActivateAcc = false;
		if (!prefProviderWrapper
				.getPreferenceBooleanValue(SipConfigManager.PREVENT_SCREEN_ROTATION)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		}
		getCountryNamebyContryCode = mySQLiteAdapter
				.fetch_country_name(getCountryCode);
		getCountryNamebyContryCode.moveToFirst();
		System.out.println("R4wHome  step11");
		while (!getCountryNamebyContryCode.isAfterLast()) {
			countryName = getCountryNamebyContryCode
					.getString(getCountryNamebyContryCode
							.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
			getCountryNamebyContryCode.moveToNext();
			//System.out.println("R4wHome  step12");
		}
		
		statusleftListView_enable = (ImageView) findViewById(R.id.tgl_status_enable);
		statusleftListView_disable = (ImageView) findViewById(R.id.tgl_status_disable);
		mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList_Left = (ListView) findViewById(R.id.drawer_list_left);
		mDrawerList_Right = (ListView) findViewById(R.id.drawer_list_right);
		imgLeftMenu = (ImageButton) findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) findViewById(R.id.imgRightMenu);

		mDrawerlayout.setDrawerListener(mDrawerToggle);
		//System.out.println("R4wHome  step13");
		
		
		if (savedInstanceState == null) {
			
			Roaming4WorldCustomApi.textordigit=false;
			/*
			fragment_dialer = new DialerFragment();
			if(fragment_dialer!=null)
			{
			
			fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().disallowAddToBackStack().replace(R.id.Frame_Layout, fragment_dialer).commitAllowingStateLoss();
			
			}
	*/
		
			
			//System.out.println("R4wHome  step14");
			
			/*
		
			Fragment fragment = new R4wActivation();
			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, fragment).commit();
			}
			*/
			
			//System.out.println("R4wHome  step14");
			
		
		}
		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.paymentheader, null);

		imgLeftMenu = (ImageButton) v.findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) v.findViewById(R.id.imgRightMenu);

		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setCustomView(v);
		//System.out.println("R4wHome  step15");
		imgLeftMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (mDrawerlayout.isDrawerOpen(mDrawerList_Right)) {
					mDrawerlayout.closeDrawer(mDrawerList_Right);
				}
				mDrawerlayout.openDrawer(mDrawerList_Left);
			}
		});
		//System.out.println("R4wHome  step16");
		imgRightMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDrawerlayout.isDrawerOpen(mDrawerList_Left)) {
					mDrawerlayout.closeDrawer(mDrawerList_Left);
				}
				mDrawerlayout.openDrawer(mDrawerList_Right);
			}
		});
		//System.out.println("R4wHome  step17");
		Fill_LeftList();
		Fill_RightList();
		RefreshListView();
		
		
		
		System.out.println("R4wHome  step18");
		account = SipProfile.getProfileFromDbId(this, accountId,
				DBProvider.ACCOUNT_FULL_PROJECTION);
		result_profile = SipProfile.getAllProfiles(getApplicationContext(),false);
		prefs_wrapper = new PreferencesWrapper(getApplicationContext());
		//System.out.println("R4wHome  step19");
		if (prefs.getString(stored_registered_account, "0").equals("0")) {
			System.out.println("R4wHome  step19");
			login();
		}
		//System.out.println("R4wHome  step20");
		
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_3G_IN, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_3G_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_GPRS_IN, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_GPRS_OUT, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_EDGE_IN, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_EDGE_OUT, true);
		
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_WIFI_IN, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_WIFI_OUT, true);
		
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_OTHER_IN, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_OTHER_OUT, true);
		
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.LOCK_WIFI, true);
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.USE_COMPACT_FORM, true);
		Log.d("R4wHomesdf",SipConfigManager.getPreferenceBooleanValue(this, SipConfigManager.USE_COMPACT_FORM, false)+"");
		SipConfigManager.setPreferenceBooleanValue(this, SipConfigManager.ENABLE_DNS_SRV, true);
		Log.d("R4wHomesdf",SipConfigManager.getPreferenceBooleanValue(this, SipConfigManager.ENABLE_DNS_SRV, false)+" dns srv");
		
	}
	
	

	// Filling the ArrayLists

	public boolean[] status = {
	        true,
	        false,
	        false,
	        false,
	        false,
	        false,
	        false,
	        false,
	       };
	public void RefreshListView() {
		objectArray_left.clear();
		for (int i = 0; i < dataArray_left.size(); i++) {
			Object obj = new Object();
			objectArray_left.add(obj);
			Log.d("objectArray_left", objectArray_left.toString());
		}
		Left_Adapter = new ListItemsAdapter_Left(objectArray_left, 2);
		mDrawerList_Left.setAdapter(Left_Adapter);
		LayoutParams params = new LayoutParams();
		mDrawerList_Left.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				displayView(position);
				checkstatusleftListView = position;
				prefs.edit().putString(stored_user_leftDrawer_status, position+"");
				Log.d("checkstatusleftListView", checkstatusleftListView+"");
			}

		});
		objectArray_right.clear();
		for (int i = 0; i < dataArray_right.size(); i++) {
			Object obj = new Object();
			objectArray_right.add(obj);
		}
		Right_Adapter = new ListItemsAdapter_Right(objectArray_right, 1);
		mDrawerList_Right.setAdapter(Right_Adapter);
		mDrawerList_Right.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) { 
				try {
					if (fetch_flag != 0) {
						pinNo = Integer.parseInt(getPin);
						Log.d("pin no in R4WMapGUI", pinNo + "");
					}
				} catch (Exception e) {
				
				}
				if (position == 1) {
					if ((CurrentFragment.name.equals("EnterAPIN")))
						payPalWebViewBuyPin(1);
				}
				if (position == 2) {
					if ((CurrentFragment.name.equals("EnterAPIN")))
						payPalWebViewBuyPin(2);
				}
				if (position == 3) {
					if ((CurrentFragment.name.equals("EnterAPIN")))
						payPalWebViewBuyPin(3);
				}
				if (position == 4) {
					if ((CurrentFragment.name.equals("EnterAPIN")))
						payPalWebViewBuyPin(4);
				}
				if (position == 6) {

					if (!(CurrentFragment.name.equals("EnterAPIN")))
						payPalWebView(8, pinNo);
				}
				if (position == 7) {
					if (!(CurrentFragment.name.equals("EnterAPIN")))
						payPalWebView(9, pinNo);
				}
				if (position == 8) {
					if (!(CurrentFragment.name.equals("EnterAPIN")))
						payPalWebView(10, pinNo);
				}
				if (position == 10) {
					if (!(CurrentFragment.name.equals("EnterAPIN")))
						payPalWebView(5, pinNo);
				}
				if (position == 11) {
					if (!(CurrentFragment.name.equals("EnterAPIN")))
						payPalWebView(6, pinNo);
				}
				if (position == 12) {
					if (!(CurrentFragment.name.equals("EnterAPIN")))
						payPalWebView(7, pinNo);
				}
			}

		});
	}

	
	
	public void userimagechange(View v) {
		
		Intent intent = new Intent();
		// call android default gallery
		intent.setDataAndType(selectedImage, "image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		// ******** code for crop image
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 0);
		intent.putExtra("aspectY", 0);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);

		try {

			intent.putExtra("return-data", true);
			startActivityForResult(Intent.createChooser(intent,"Complete action using"), 2);

		} catch (ActivityNotFoundException e) {
			// Do nothing for now
		}
		
	}
	
	public void editusername(View v) {
		dialogboxUsreName();
		//startActivityForResult(new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), CHANGE_PREFS);
		//Toast.makeText(getApplicationContext(), "still in progress - username", Toast.LENGTH_SHORT);
	}
	
	
	public void Fill_LeftList() {
		String getPrefUserName = prefUserInfo.getString("userNameKey", "Name");
		Log.d("UserName inside the Fill_LeftList", getPrefUserName);
		dataArray_left.clear();
		//dataArray_left.add("Pic");
		
		if (getPrefUserName.equals("Name"))
			dataArray_left.add("User Name");
		
	    else
			dataArray_left.add(getPrefUserName);
		dataArray_left.add("Home");
		dataArray_left.add("My Account");
		dataArray_left.add("Call Details");
		dataArray_left.add("Re-Call Activation");
		dataArray_left.add("How to use");
		dataArray_left.add("Rate Us");
		dataArray_left.add("Like and Share");
		dataArray_left.add("Ticket Support");
		dataArray_left.add("Get Flight Info");
		dataArray_left.add("Voice Mail");
		dataArray_left.add("Get Weather Info");
		dataArray_left.add("Exit");

	}

	public void Fill_RightList() {
		dataArray_right.clear();
		dataArray_right.add("Buy a PIN");
		dataArray_right.add("$15 Travel Card");
		dataArray_right.add("$25 Travel Card");
		dataArray_right.add("$40 Travel Card");
		dataArray_right.add("$70 Travel Card");
		dataArray_right.add("TopUp + Validity");
		dataArray_right.add("$5 - 1 Day & $5");
		dataArray_right.add("$10 - 5 Days & $10");
		dataArray_right.add("$15 - 15 Days & $15");
		dataArray_right.add("Validity");
		dataArray_right.add("$4 - 1 Day");
		dataArray_right.add("$15 - 5 Days");
		dataArray_right.add("$25 - 15 Days");

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CHANGE_PREFS) {
			sendBroadcast(new Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
			BackupWrapper.getInstance(this).dataChanged();
		}

		
		if (requestCode == 2) {
			
			
			selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			editorUserInfo.putString("userImagePath", picturePath);
			editorUserInfo.commit();
			cursor.close();
			imageaddres = picturePath;
			
			
			Bundle extras2 = data.getExtras();
			if (extras2 != null) {
				Bitmap photo = ShrinkBitmap(imageaddres, 150, 150);
				
				lluserImageView.setImageBitmap(photo);

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

	private class ListItemsAdapter_Left extends ArrayAdapter<Object> {
		ViewHolder holder1;
		public ListItemsAdapter_Left(List<Object> items, int x) {
			super(R4wHome.this,
					android.R.layout.simple_list_item_single_choice, items);
		}

		@Override
		public String getItem(int position) {
			return dataArray_left.get(position);
		}

		public int getItemInteger(int pos) {
			return pos;
		}

		@Override
		public int getCount() {
			return dataArray_left.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = getLayoutInflater();
			holder1 = new ViewHolder();
			//ImageView imageView = null;

			if (CurrentFragment.name.equals("EnterAPIN")
					|| CurrentFragment.name.equals("ActivationFwd")
					|| CurrentFragment.name.equals("MapService")
					|| CurrentFragment.name.equals("Home")) 
			{

				boolean ch = false;
				
				if (position == 0) {
					//convertView = inflator.inflate(R.layout.r4wuserimagerow,null);
					convertView = inflator.inflate(R.layout.r4wusername, null);
					holder1.text = (TextView) convertView.findViewById(R.id.txtData_username);
					String imgaddress = prefUserInfo.getString("userImagePath","NoAddress");
					
					lluserImageView = (ImageView) convertView.findViewById(R.id.iv_userImageView);
					//performCrop();
						if (!imgaddress.equals("NoAddress")) {
								//BitmapFactory.Options options = new BitmapFactory.Options();
								//options.inSampleSize = 8;
								//lluserImageView.setBackground(null);
								//GraphicsUtil graphicUtil = new GraphicsUtil();
		
								//bm = BitmapFactory.decodeFile(imgaddress, options);
							
								Bitmap bitmap = BitmapFactory.decodeFile(imgaddress);
								lluserImageView.setImageBitmap(bitmap);
								
							}
						}
				


				
				else if ((position == 2 && CurrentFragment.name
						.equals("EnterAPIN"))
						|| (position == 3 && CurrentFragment.name
								.equals("ActivationFwd"))) {
					
					
					convertView = inflator.inflate(R.layout.r4wleftdrawerdisable, null);
					//imageView = (ImageView) convertView.findViewById(R.id.icon);
					
					holder1.rl_disable = (RelativeLayout) convertView.findViewById(R.id.rlleftdrawer_disable);
					
					holder1.text = (TextView) convertView
							.findViewById(R.id.txtDatadisable);
					
					holder1.disable_status = (ImageView) convertView.findViewById(R.id.tgl_status_disable);
					holder1.disable_icon = (ImageView) convertView.findViewById(R.id.disable_leftdrawerlist_icon);
					holder1.disable_icon.setBackgroundResource(R.drawable.mycontacts);
					if(position == checkstatusleftListView)
					{
						holder1.rl_disable.setBackgroundResource(R.drawable.backband);
						holder1.disable_status.setVisibility(ImageView.VISIBLE);
					}
				}

				else if ((position == 3 && CurrentFragment.name
						.equals("EnterAPIN"))
						|| (position == 4 && CurrentFragment.name
								.equals("ActivationFwd"))) {
					
					
					convertView = inflator.inflate(
							R.layout.r4wleftdrawerdisable, null);
					holder1.rl_disable = (RelativeLayout) convertView.findViewById(R.id.rlleftdrawer_disable);
					
					holder1.text = (TextView) convertView
							.findViewById(R.id.txtDatadisable);
					holder1.disable_status = (ImageView) convertView.findViewById(R.id.tgl_status_disable);
					holder1.disable_icon = (ImageView) convertView.findViewById(R.id.disable_leftdrawerlist_icon);
					holder1.disable_icon.setBackgroundResource(R.drawable.call_details);
					
					if(position == checkstatusleftListView)
					{
						holder1.rl_disable.setBackgroundResource(R.drawable.backband);
						holder1.disable_status.setVisibility(ImageView.VISIBLE);
					}
					//imageView = (ImageView) convertView.findViewById(R.id.icon);

				} else if (position == 4
						&& CurrentFragment.name.equals("EnterAPIN")) {
					convertView = inflator.inflate(
							R.layout.r4wleftdrawerdisable, null);
					holder1.rl_disable = (RelativeLayout) convertView.findViewById(R.id.rlleftdrawer_disable);
					holder1.text = (TextView) convertView
							.findViewById(R.id.txtDatadisable);
					//imageView = (ImageView) convertView.findViewById(R.id.icon);

					holder1.text = (TextView) convertView
							.findViewById(R.id.txtDatadisable);
					holder1.disable_status = (ImageView) convertView.findViewById(R.id.tgl_status_disable);
					holder1.disable_icon = (ImageView) convertView.findViewById(R.id.disable_leftdrawerlist_icon);
					holder1.disable_icon.setBackgroundResource(R.drawable.recall_icon_left);
					if(position == checkstatusleftListView)
					{
						holder1.rl_disable.setBackgroundResource(R.drawable.backband);
						holder1.disable_status.setVisibility(ImageView.VISIBLE);
					}
				} 
				else if (position ==10&& CurrentFragment.name.equals("EnterAPIN")) {
					convertView = inflator.inflate(R.layout.r4wleftdrawerdisable,null);
					holder1.text = (TextView) convertView.findViewById(R.id.txtDatadisable);
					//imageView=(ImageView) convertView.findViewById(R.id.icon); 
					holder1.rl_disable = (RelativeLayout) convertView.findViewById(R.id.rlleftdrawer_disable);
					
					holder1.text = (TextView) convertView.findViewById(R.id.txtDatadisable);
					holder1.disable_status = (ImageView) convertView.findViewById(R.id.tgl_status_disable);
					holder1.disable_icon = (ImageView) convertView.findViewById(R.id.disable_leftdrawerlist_icon);
					holder1.disable_icon.setBackgroundResource(R.drawable.voicemail);
					if(position == checkstatusleftListView)
					{	
						holder1.rl_disable.setBackgroundResource(R.drawable.backband);
						holder1.disable_status.setVisibility(ImageView.VISIBLE);
					}
				} 
				else {
					
					convertView = inflator.inflate(
							R.layout.r4wleftdrawerenable, null);
					holder1.text = (TextView) convertView
							.findViewById(R.id.txtData);
					
					holder1.rl_enable = (RelativeLayout) convertView.findViewById(R.id.rlleftdrawer_enable);
					
					if(position == 1)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.home_icon_left);
					} else if(position == 5)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.howto_use);
					} else if(position == 6)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.rate_icon_left);
					} else if(position == 7)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.share_icon_left);
					} else if(position == 8)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.support_icon_left);
					} else if(position == 9)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.flightinfo);
					} else if(position == 11)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.weather);
					} else if(position == 12)
					{
						holder1.enable_icon = (ImageView) convertView.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon.setBackgroundResource(R.drawable.exit_icon_left);
					} 
					
						
				
					holder1.enable_status = (ImageView) convertView.findViewById(R.id.tgl_status_enable);
					if(position == checkstatusleftListView)
					{
						holder1.rl_enable.setBackgroundResource(R.drawable.backband);
						holder1.enable_status.setVisibility(ImageView.VISIBLE);
					}

					//imageView = (ImageView) convertView.findViewById(R.id.icon);
					

				}
			}
			//imageView.setVisibility(View.GONE);

			convertView.setTag(holder1);
			String text = dataArray_left.get(position);

			/*
			if (text.equals("Home")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("My Account")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("Call Details")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("Re-Call Activation")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("How to use")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("Rate Us")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("Like and Share")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("Ticket Support")) {
				imageView.setImageResource(R.drawable.ic_home);
			} else if (text.equals("Exit")) {
				imageView.setImageResource(R.drawable.ic_home);
			}
			
			
			*/

			try {
				holder1.text.setText(dataArray_left.get(position));

			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	private class ListItemsAdapter_Right extends ArrayAdapter<Object> {
		ViewHolder holder1;

		public ListItemsAdapter_Right(List<Object> items, int x) {
			super(R4wHome.this,
					android.R.layout.simple_list_item_single_choice, items);
		}

		@Override
		public String getItem(int position) {
			return dataArray_right.get(position);
		}

		public int getItemInteger(int pos) {
			return pos;

		}

		@Override
		public int getCount() {
			return dataArray_right.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = getLayoutInflater();
			holder1 = new ViewHolder();
			if (position == 0 || position == 5 || position == 9) {
				convertView = inflator.inflate(R.layout.paymentheading, null);
				holder1.text = (TextView) convertView
						.findViewById(R.id.txtDataHeading);

			} else if ((position == 1 || position == 2 || position == 3 || position == 4)
					&& (!CurrentFragment.name.equals("EnterAPIN"))) {
				convertView = inflator.inflate(R.layout.disablerightitem, null);
				holder1.text = (TextView) convertView
						.findViewById(R.id.txtDatadisable);
			}

			else if ((position == 6 || position == 7 || position == 8
					|| position == 9 || position == 10 || position == 11 || position == 12)
					&& CurrentFragment.name.equals("EnterAPIN")) {
				convertView = inflator.inflate(R.layout.disablerightitem, null);
				holder1.text = (TextView) convertView
						.findViewById(R.id.txtDatadisable);
			}

			else {
				convertView = inflator.inflate(R.layout.paymentrow, null);
				holder1.text = (TextView) convertView
						.findViewById(R.id.txtData);
			}
			convertView.setTag(holder1);
			String text = dataArray_right.get(position);
			holder1.text.setText(dataArray_right.get(position));
			return convertView;
		}

	}

	private class ViewHolder {
		TextView text, textcounter;
		ImageView enable_status, disable_status, enable_icon, disable_icon;
		RelativeLayout rl_disable, rl_enable;
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

			}
		}
		return sb.toString();
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

				Intent intent_NoNetWork = new Intent(R4wHome.this,
						NoNetwork.class);
				startActivity(intent_NoNetWork);
			} else {
				Log.d("update_mao_text", "starting");
				// update_map_texts();
			}
			mProgressDialog3.dismiss();
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog3 = ProgressDialog.show(R4wHome.this, getResources()
					.getString(R.string.loading),
					getResources().getString(R.string.data_loading));
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
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {

			t.printStackTrace();
			return false;
		}
	}

	public void payPalWebView(int pinId, int PinNo) {

		Intent intent = new Intent(R4wHome.this, R4wPaymentWebView.class);
		intent.putExtra("Pid", pinId);
		intent.putExtra("PinNofrom", PinNo);
		startActivity(intent);
	}

	public void payPalWebViewBuyPin(int pinId) {

		Intent intent = new Intent(R4wHome.this, R4wPaymentWebView.class);
		intent.putExtra("Pid", pinId);
		startActivity(intent);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	private void displayView(int position) {

		RefreshListView();
		
		switch (position) {
		/*
		case 0:
			
			startActivity(new Intent(this, AccountsEditList.class));
			break;
			*/
		case 0:
			//dialogboxUsreName();
			startActivityForResult(new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), CHANGE_PREFS);
			break;

		case 1:
			//startActivityForResult(new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), CHANGE_PREFS);
			ft = getSupportFragmentManager().beginTransaction();
			if (fetch_flag == 0)
				ft.replace(R.id.Frame_Layout,  new R4wActivation()).commit();
	    	else if (fetch_flag == 1)
	 			ft.replace(R.id.Frame_Layout,  new R4wFlightDetails()).commit();
			else if (fetch_flag == 2)
				ft.replace(R.id.Frame_Layout,  new R4wMapService()).commit();
			else if (fetch_flag == 3)
				ft.replace(R.id.Frame_Layout,  new R4wActivate()).commit();
			

			break;

		case 2:
			ft = getSupportFragmentManager().beginTransaction();
			if (!(CurrentFragment.name.equals("EnterAPIN")
					|| CurrentFragment.name.equals("ActivationFwd") || CurrentFragment.name
						.equals("MapService")))
				ft.replace(R.id.Frame_Layout,  new R4wAccount()).commit();
			
			else if (CurrentFragment.name.equals("MapService"))
				ft.replace(R.id.Frame_Layout,  new R4wAccount()).commit();
			break;
		case 3:
			ft = getSupportFragmentManager().beginTransaction();
			if (!(CurrentFragment.name.equals("EnterAPIN")
					|| CurrentFragment.name.equals("ActivationFwd") || CurrentFragment.name
						.equals("MapService")))
				ft.replace(R.id.Frame_Layout,  new CallRecords()).commit();
			else if (CurrentFragment.name.equals("MapService"))
				ft.replace(R.id.Frame_Layout,  new CallRecords()).commit();
			break;
		case 4:
			ft = getSupportFragmentManager().beginTransaction();
			if (!(CurrentFragment.name.equals("EnterAPIN") || CurrentFragment.name
					.equals("ActivationFwd")))
				ft.replace(R.id.Frame_Layout,  new R4wReRunCode()).commit();
			else if (CurrentFragment.name.equals("MapService")
					|| CurrentFragment.name.equals("ActivationFwd"))
				ft.replace(R.id.Frame_Layout,  new R4wReRunCode()).commit();

			break;
		case 5:
			//ft = getSupportFragmentManager().beginTransaction();
			//ft.replace(R.id.Frame_Layout,  new HowToUse()).commit();
			Intent howToUse= new Intent(this,HowToUse.class);
			startActivity(howToUse);
			break;
		case 6:
			showRateDialog(mContext);
			break;
		case 7:
			//ft = getSupportFragmentManager().beginTransaction();
			//ft.replace(R.id.Frame_Layout,  new MainActivity()).commit();

			break;
		case 8:
			//ft = getSupportFragmentManager().beginTransaction();
			//ft.replace(R.id.Frame_Layout,  new TicketSupport()).commit();
			Intent tickIntent= new Intent(this,TicketSupport.class);
			startActivity(tickIntent);
			
			break;
		case 9:
			ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.Frame_Layout,  new R4wFlightDetails()).commit();
			//fragment = new R4wActivateFwd();
			break;

		case 10:
			ft = getSupportFragmentManager().beginTransaction();
			if(fetch_flag!=0){			
				if (!(CurrentFragment.name.equals("EnterAPIN")))
					ft.replace(R.id.Frame_Layout,  new R4WVoiceMail()).commit();
			}
			break;
		case 11:
			
			Intent R4WWeatherInfo= new Intent(this,R4WWeatherInfo.class);
			startActivity(R4WWeatherInfo);
			
			//ft = getSupportFragmentManager().beginTransaction();
			//ft.replace(R.id.Frame_Layout,  new R4WWeatherInfo()).commit();
			break;	
	
		case 12:
			finish();
			break;

		default:
			break;
		}
		
		try
		{
		fragmentManager = getSupportFragmentManager();
		List<android.support.v4.app.Fragment> allFragments=fragmentManager.getFragments();
		if (allFragments == null || allFragments.isEmpty()) {
	       
	    }
		else
		{
			 for (android.support.v4.app.Fragment fragment1 : allFragments) {
			        if (fragment1.isVisible()) {
			            fragmentManager.beginTransaction().remove(fragment1).commit();
			        }
		}
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	   

		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.Frame_Layout, fragment).commit();
			mDrawerlayout.closeDrawer(mDrawerList_Left);

		}
	}

	private final static int CHANGE_PREFS = 1;

	public void dialogboxUsreName() {

		final Dialog dialoguserName = new Dialog(R4wHome.this);
		dialoguserName.setTitle("Edit User Name");

		dialoguserName.setContentView(R.layout.dialogboxforusername);
		dialoguserName.show();
		btnOk = (Button) dialoguserName.findViewById(R.id.btnUserNameOk);
		btnCancel = (Button) dialoguserName
				.findViewById(R.id.btnUserNameCancel);
		edtUserName = (EditText) dialoguserName.findViewById(R.id.EdtUserName);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String getUserName = edtUserName.getText().toString();
				Log.d("UserName inside the dialogbo", getUserName);
				editorUserInfo.putString("userNameKey", getUserName);
				editorUserInfo.commit();
				System.out.println(dataArray_left.get(1));
				dataArray_left.set(0, getUserName);
				Left_Adapter.notifyDataSetChanged();
				dialoguserName.dismiss();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialoguserName.dismiss();
			}
		});

	}

	public static void showRateDialog(final Context mContext) {
		final Dialog dialog = new Dialog(mContext);
		dialog.setTitle("Rate " + APP_TITLE);
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(mContext);
		tv.setText("If you enjoy using " + APP_TITLE
				+ ", please take a moment to rate it. Thanks for your support!");
		tv.setWidth(240);
		tv.setPadding(4, 0, 4, 10);
		ll.addView(tv);

		Button b1 = new Button(mContext);
		b1.setText("Rate " + APP_TITLE);
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id=" + APP_PNAME)));
				dialog.dismiss();
			}
		});
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText("Remind me later");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText("No, thanks");
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				dialog.dismiss();
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);
		dialog.show();
	}

	private void performCrop() {
		// take care of exceptions
		try {

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			cropIntent.setDataAndType(selectedImage, "image/*");
			cropIntent.putExtra("crop", "true");
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("outputX", 256);
			cropIntent.putExtra("outputY", 350);
			cropIntent.putExtra("return-data", true);
			startActivityForResult(cropIntent, PIC_CROP);
		}

		catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}



	
	

	 private void startSipService() {
			try {
				Thread t = new Thread("StartSip") {
					public void run() {
						Intent serviceIntent = new Intent(
								SipManager.INTENT_SIP_SERVICE);
						// Optional, but here we bundle so just ensure we are using
						// csipsimple package
						serviceIntent.setPackage(R4wHome.class.getPackage()
								.getName());
						serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY,
								new ComponentName(R4wHome.this, R4wHome.class));
						startService(serviceIntent);
						postStartSipService();
					};
				};
				t.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	 
	private void postStartSipService() {
		// If we have never set fast settings
		if (CustomDistribution.showFirstSettingScreen()) {
			if (!prefProviderWrapper.getPreferenceBooleanValue(
					PreferencesWrapper.HAS_ALREADY_SETUP, false)) {
				
				/*  System.out.println("value is going to set");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_3G_IN, true);
				  System.out.println("value is set for use 3g in");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_3G_OUT, true);
				  System.out.println("value is set for use 3g out");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_GPRS_IN, true);
				  System.out.println("value is set for use gprs in");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_GPRS_OUT, true);
				  System.out.println("value is set for use gprs out");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_EDGE_IN, true);
				  System.out.println("value is set for use edge in");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_EDGE_OUT, true);
				  System.out.println("value is set for use edge out");
				  
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_WIFI_IN, true);
				  System.out.println("value is set for use wifi in");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_WIFI_OUT, true);
				  System.out.println("value is set for use wifi out");
				  
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_OTHER_IN, true);
				  System.out.println("value is set for use_other_in in");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.USE_OTHER_OUT, true);
				  System.out.println("value is set for use_other_in out");
				  
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.LOCK_WIFI, true);
				  System.out.println("value is set for lock_wifi");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.INTEGRATE_WITH_DIALER, true);
				  System.out.println("value is set for dialerintegrate");
				  SipConfigManager.setPreferenceBooleanValue(this,
				  SipConfigManager.INTEGRATE_WITH_CALLLOGS, true);
				  System.out.println("value is set integrate calllog");
				  if(!SipConfigManager.getPreferenceBooleanValue(this,
				  PreferencesWrapper.HAS_ALREADY_SETUP, false) ) {
				  SipConfigManager.setPreferenceBooleanValue(this,
				  PreferencesWrapper.HAS_ALREADY_SETUP, true);
				  System.out.println("value is set in r4whome"); }
				 */
				
				/*
					Intent prefsIntent = new Intent(SipManager.ACTION_UI_PREFS_FAST);
					prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(prefsIntent);
					*/
					
				return;
			}
		} 
		else {
			boolean doFirstParams = !prefProviderWrapper
					.getPreferenceBooleanValue(
							PreferencesWrapper.HAS_ALREADY_SETUP, false);
			prefProviderWrapper.setPreferenceBooleanValue(
					PreferencesWrapper.HAS_ALREADY_SETUP, true);
			if (doFirstParams) {
				prefProviderWrapper.resetAllDefaultValues();
			}
		}

		// If we have no account yet, open account panel,
		if (!hasTriedOnceActivateAcc) {

			Cursor c = getContentResolver().query(SipProfile.ACCOUNT_URI,
					new String[] { SipProfile.FIELD_ID }, null, null, null);
			int accountCount = 0;
			if (c != null) {
				try {
					accountCount = c.getCount();
				} catch (Exception e) {
					// Log.e(THIS_FILE,
					// "Something went wrong while retrieving the account", e);
				} finally {
					c.close();
				}
			}

			if (accountCount == 0) {
				Intent accountIntent = null;
				WizardInfo distribWizard = CustomDistribution
						.getCustomDistributionWizard();
				if (distribWizard != null) {
					accountIntent = new Intent(this, BasePrefsWizard.class);
					accountIntent.putExtra(SipProfile.FIELD_WIZARD,
							distribWizard.id);
				} else {
					accountIntent = new Intent(this, AccountsEditList.class);
				}

				if (accountIntent != null) {
					accountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(accountIntent);
					hasTriedOnceActivateAcc = true;
					return;
				}
			}
			hasTriedOnceActivateAcc = true;
		}
	}

	public void login() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			// fetch data
			new MyAsyncTask_CheckForRegisteration().execute();
		} else {
			// display error

			Intent intent = new Intent(this, NoNetwork.class);
			startActivity(intent);
		}

	}


	private class MyAsyncTask_CheckForRegisteration extends
			AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog3;

		@Override
		protected void onPostExecute(Boolean result) {
			// mProgressDialog3.dismiss();
			// mProgressDialog3.dismiss();
			if (result) {
				Log.d("onpostexecute", "yes");
				if (show.equals("true")) {

					if (result_profile.size() < 1) {
						
						setWizardId_login("BASIC");
						saveAccount_login("BASIC", account_name, user_name,
								server, password_response);
						prefs.edit().putString(stored_registered_account, "1").commit();
						
					} else {
						
						if (result_profile.size() == 1) {
							result_profile_login = SipProfile
									.getAllProfiles_login(
											getApplicationContext(), false);
							account = result_profile_login.get(0);
							System.out.println(account.display_name);
							System.out.println(account.id);
							System.out.println(result_profile.size());
							
							System.out.println("display name is "
									+ account.display_name == account_name);
							System.out.println("user name is "
									+ account.username == user_name);
							System.out.println("server address is "
									+ account.reg_uri == server);
							System.out
									.println("password is " + account.data == password_response);
							if (account.display_name.equals(account_name)
									&& account.username.equals(user_name)
									&& account.reg_uri.equals("sip:" + server)
									&& account.data.equals(password_response)) {
								System.out
										.println("in checking default values");
								prefs.edit().putString(stored_registered_account, "1").commit();
								
							} else {
								System.out.println("in checking default values else part");
								setWizardId_login("BASIC");
								saveAccount_login("BASIC", account_name, user_name, server, password_response);
								prefs.edit().putString(stored_registered_account,"1").commit();
							
							}
						} else {
							for (int i = 0; i < result_profile.size(); i++) {
								account = result_profile.get(i);
								boolean del_account = delete_account_from_db();
								if (del_account) {

								} else {
									System.out.println("error in deleting the account");
								}
							}
							setWizardId_login("BASIC");
							saveAccount_login("BASIC", account_name, user_name,
									server, password_response);
							prefs.edit()
									.putString(stored_registered_account, "1")
									.commit();
							
						}
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Incorrect Username and Password",
							Toast.LENGTH_LONG).show();
				}
				// mProgressDialog3.dismiss();
			} else {
				Log.d("R4wHome",
						"Error in generating json response. So Sorry! Try Again");
			}
		}

		@Override
		protected void onPreExecute() {
			// mProgressDialog3 = ProgressDialog.show(R4wHome.this,
			// "Loading","Data is loading");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqGetValue()) {
				Log.d("yay", "SUCCESS");
				return true;
			} else {
				Log.d("err", "ERROR");
				Log.d("after error", "yes");
				return false;
			}
		}
	}

	public boolean webservreqGetValue() {
		try {
			// HttpParams httpParams = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(httpParams,
			// TIMEOUT_MILLISEC);
			// HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			// Instantiate an HttpClient
			HttpClient httpclient = new DefaultHttpClient(p);

			String url = "http://ip.roaming4world.com/esstel/"
					+ "dial4world.php?username="
					+ prefs.getString(stored_user_country_code, "")
					+ prefs.getString(stored_user_mobile_no, "") + "&password="
					+ prefs.getString(stored_user_dialer_password, "");
			HttpPost httppost = new HttpPost(url);
			Log.d("after url call", "yes");
			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);

				// Parse
				JSONObject json = new JSONObject(responseBody);
				System.out.println(json);
				account_name = json.getString("account_name");
				user_name = json.getString("username");
				server = json.getString("server");
				password_response = json.getString("password");
				show = json.getString("show");
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (Exception t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			t.printStackTrace();
			return false;
		}
	}

	public void saveAccount_login(String wizardId, String account_name,
			String user_name, String server, String password_response) {
		boolean needRestart = false;

		PreferencesWrapper prefs = new PreferencesWrapper(
				getApplicationContext());
		account = wizard.buildAccount_login(account, account_name, user_name,
				server, password_response);
		account.wizard = wizardId;
		if (account.id == SipProfile.INVALID_ID) {
			// This account does not exists yet
			prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			applyNewAccountDefault(account);
			Uri uri = getContentResolver().insert(SipProfile.ACCOUNT_URI,
					account.getDbContentValues());

			// After insert, add filters for this wizard
			account.id = ContentUris.parseId(uri);
			List<Filter> filters = wizard.getDefaultFilters(account);
			if (filters != null) {
				for (Filter filter : filters) {
					// Ensure the correct id if not done by the wizard
					filter.account = (int) account.id;
					getContentResolver().insert(SipManager.FILTER_URI,
							filter.getDbContentValues());
				}
			}
			// Check if we have to restart
			needRestart = wizard.needRestart();

		} else {
			// option to re-apply default params
			prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			getContentResolver().update(
					ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE,
							account.id), account.getDbContentValues(), null,
					null);
		}

		// Mainly if global preferences were changed, we have to restart sip
		// stack
		if (needRestart) {
			Intent intent = new Intent(SipManager.ACTION_SIP_REQUEST_RESTART);
			sendBroadcast(intent);
		}
	}

	private void applyNewAccountDefault(SipProfile account) {
		if (account.use_rfc5626) {
			if (TextUtils.isEmpty(account.rfc5626_instance_id)) {
				String autoInstanceId = (UUID.randomUUID()).toString();
				account.rfc5626_instance_id = "<urn:uuid:" + autoInstanceId
						+ ">";
			}
		}
	}

	public boolean setWizardId_login(String wId) {
		if (wizardId == null) {
			return setWizardId_login(WizardUtils.EXPERT_WIZARD_TAG);
		}

		WizardInfo wizardInfo = WizardUtils.getWizardClass(wId);
		if (wizardInfo == null) {
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId_login(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		}

		try {
			wizard = (WizardIface) wizardInfo.classObject.newInstance();
		} catch (IllegalAccessException e) {
			// Log.e(THIS_FILE, "Can't access wizard class", e);
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId_login(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		} catch (InstantiationException e) {
			// Log.e(THIS_FILE, "Can't access wizard class", e);
			if (!wizardId.equals(WizardUtils.EXPERT_WIZARD_TAG)) {
				return setWizardId_login(WizardUtils.EXPERT_WIZARD_TAG);
			}
			return false;
		}
		wizardId = wId;
		// wizard.setParent(this);

		return true;
	}

	public boolean delete_account_from_db() {
		int rows_del = getContentResolver().delete(
				ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE,
						account.id), null, null);
		System.out.println("no. of rows deleted " + rows_del);
		if (rows_del > 0) {
			return true;
		} else {
			return false;
		}
	}

	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
			
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(userbitmap == 1){
		
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
		
		 ViewServer.get(this).setFocusedWindow(this);
		statusObserver = new AccountStatusContentObserver(serviceHandler);
		getContentResolver().registerContentObserver(
				SipProfile.ACCOUNT_STATUS_URI, true, statusObserver);
		Log.d("R4wHome",
				"The stored account register status is in onResume is "
						+ prefs.getString(stored_account_register_status,
								"Connecting"));

		try
		{
			
		final LinearLayout onlinelayout= (LinearLayout) findViewById(R.id.onlineStatus);	
		TextView status_account = (TextView) findViewById(R.id.status);
		if (status_account != null) {

			Handler handler = new Handler();
			Runnable runnable=null;
			System.out.println("R4wHome");
			onlinelayout.invalidate();
			Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/NexaBold.otf");
			status_account.setTypeface(tf);
			status_account.setText(prefs.getString(stored_account_register_status, "Connecting"));
			onlinelayout.invalidate();
			
			if (prefs.getString(stored_account_register_status, "Connecting").equals("Online")) {
				onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideUp);
				
				/*
				Handler handler = new Handler();
	        	handler.postDelayed(new Runnable() 
	        	{
	        	@Override
	        	public void run()
	        	{
	        		System.out.println("Run method 2");
	        		onlinelayout.startAnimation(slideDown);
	        		onlinelayout.invalidate();
	        		
	        		onlinelayout.setVisibility(View.GONE);
	        
	        	}
	        	}, 10000);*/
				
				runnable = new Runnable()
				 {
					 
					 public void run()
			        	{
			        		System.out.println("Run method 2");
			        		
			        		onlinelayout.startAnimation(slideDown);
			        		onlinelayout.invalidate();
			        		
			        		onlinelayout.setVisibility(View.GONE);
			        
			        	}
				 };handler.postDelayed(runnable, 10000); 
				
				
			} else if (prefs.getString(stored_account_register_status, "Connecting").equals("Connecting")) {
				handler.removeCallbacks(runnable);
				onlinelayout.setBackgroundColor(Color.parseColor("#F79141"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideUp);
				
				
			} else if (prefs.getString(stored_account_register_status,"Connecting").equals("Offline")) {
				handler.removeCallbacks(runnable);
				onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideUp);
			}
			
			onlinelayout.invalidate();
		}
		}
		catch(Exception e)
		{
			
		}
		prefProviderWrapper.setPreferenceBooleanValue(
				PreferencesWrapper.HAS_BEEN_QUIT, false);
		startSipService();

	}

	public void updateRegistrationsState() {
		System.out.println("SipService.java in updateRegistrationsState()");
		Log.d("updateRegistrationsState", "Update registration state");
		ArrayList<SipProfileState> activeProfilesState = new ArrayList<SipProfileState>();
		Cursor c = getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI,
				null, null, null, null);
		if (c != null) {
			try {
				if (c.getCount() > 0) {
					c.moveToFirst();
					do {
						SipProfileState ps = new SipProfileState(c);
						if (ps.isValidForCall()) {
							activeProfilesState.add(ps);
						}
					} while (c.moveToNext());
				}
			} catch (Exception e) {
				Log.e("updateRegistrationsState", "Error on looping over sip profiles", e);
			} finally {
				c.close();
			}
		}

		Collections.sort(activeProfilesState, SipProfileState.getComparator());

		TextView status_register_account = (TextView) findViewById(R.id.status);
		// Handle status bar notification
		if (activeProfilesState.size() > 0
				&& prefs_wrapper
						.getPreferenceBooleanValue(SipConfigManager.ICON_IN_STATUS_BAR)) {
			// Testing memory / CPU leak as per issue 676
			// for(int i=0; i < 10; i++) {
			// Log.d(THIS_FILE, "Notify ...");
			// notificationManager.notifyRegisteredAccounts(activeProfilesState,
			// prefsWrapper.getPreferenceBooleanValue(SipConfigManager.ICON_IN_STATUS_BAR_NBR));
			// try {
			// Thread.sleep(6000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// }
			System.out.println("yes working fine my service code");
			prefs.edit().putString(stored_account_register_status, "Online")
					.commit();
			Log.d("R4wHome",
					"The stored account register status is in updateRegistrationsState if part is "
							+ prefs.getString(stored_account_register_status,
									"Connecting"));
			try
			{
				Handler handler = new Handler();
				Runnable runnable=null;
				System.out.println("R4wHome");
				final LinearLayout onlinelayout= (LinearLayout) findViewById(R.id.onlineStatus);	
				TextView status_account = (TextView) findViewById(R.id.status);
				if (status_account != null) {
					System.out.println("R4wHome");
					onlinelayout.invalidate();
					Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/NexaBold.otf");
					status_account.setTypeface(tf);
					status_account.setText(prefs.getString(stored_account_register_status, "Connecting"));
					onlinelayout.invalidate();
					if (prefs.getString(stored_account_register_status, "Connecting").equals("Online")) {
						onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
						onlinelayout.invalidate();
						onlinelayout.startAnimation(slideUp);
						runnable = new Runnable(){
						public void run()
					        	{
					        		System.out.println("Run method 2");
					        		onlinelayout.startAnimation(slideDown);
					        		onlinelayout.invalidate();
					        		onlinelayout.setVisibility(View.GONE);
					        
					        	}
						 };handler.postDelayed(runnable, 10000); 
						
						
						/*
						Handler handler = new Handler();
			        	handler.postDelayed(new Runnable() 
			        	{
			        	@Override
			        	public void run()
			        	{
			        		System.out.println("Run method 2");
			        		onlinelayout.startAnimation(slideDown);
			        		onlinelayout.invalidate();
			        		
			        		onlinelayout.setVisibility(View.GONE);
			        
			        	}
			        	}, 10000);
			        	
			        	*/
						
					} else if (prefs.getString(stored_account_register_status, "Connecting").equals("Connecting")) {
						handler.removeCallbacks(runnable);
						onlinelayout.setVisibility(View.VISIBLE);
						onlinelayout.setBackgroundColor(Color.parseColor("#F79141"));
						onlinelayout.invalidate();
						onlinelayout.startAnimation(slideUp);
						
					} else if (prefs.getString(stored_account_register_status,"Connecting").equals("Offline")) {
						handler.removeCallbacks(runnable);
						onlinelayout.invalidate();
						onlinelayout.setVisibility(View.VISIBLE);
						onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
						onlinelayout.invalidate();
						onlinelayout.startAnimation(slideDown);
						onlinelayout.invalidate();
					}
					
					onlinelayout.invalidate();
				}
				}
			catch(Exception e)
			{}
		} else {
			final LinearLayout onlinelayout= (LinearLayout) findViewById(R.id.onlineStatus);	
			if (prefs.getString(stored_account_register_status, "not available").equals("Offline")) {
				onlinelayout.setVisibility(View.VISIBLE);
				onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				onlinelayout.invalidate();
				onlinelayout.startAnimation(slideDown);
				

			} else {
				prefs.edit().putString(stored_account_register_status, "Connecting").commit();
			}
			Log.d("R4wHome",
					"The stored account register status is in updateRegistrationsState else part is "
							+ prefs.getString(stored_account_register_status,
									"Connecting"));
			try
			{
				
				TextView status_account = (TextView) findViewById(R.id.status);
				if (status_account != null) {
					Handler handler = new Handler();
					Runnable runnable=null;
					
					System.out.println("R4WHome");
					onlinelayout.invalidate();
					Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/NexaBold.otf");
					status_account.setTypeface(tf);
					status_account.setText(prefs.getString(stored_account_register_status, "Connecting"));
					onlinelayout.startAnimation(slideUp);
					if (prefs.getString(stored_account_register_status, "Connecting").equals("Online")) {
						onlinelayout.invalidate();
						onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
						onlinelayout.invalidate();
						onlinelayout.startAnimation(slideUp);
						onlinelayout.invalidate();
						
						
					 runnable = new Runnable()
						 {
							 
							 public void run()
					        	{
					        		System.out.println("Run method 2");
					        		onlinelayout.invalidate();
					        		onlinelayout.startAnimation(slideDown);
					        		onlinelayout.invalidate();
					        		
					        		onlinelayout.setVisibility(View.GONE);
					        		onlinelayout.invalidate();
					        	}
						 };handler.postDelayed(runnable, 10000); 
					
					/*	 
			        	handler.postDelayed(new Runnable() 
			        	{
			        	@Override
			        	public void run()
			        	{
			        		System.out.println("Run method 2");
			        		
			        		onlinelayout.startAnimation(slideDown);
			        		onlinelayout.invalidate();
			        		
			        		onlinelayout.setVisibility(View.GONE);
			        
			        	}
			        	}, 10000);*/
						
						
					} else if (prefs.getString(stored_account_register_status, "Connecting").equals("Connecting")) {
						
						onlinelayout.invalidate();
						handler.removeCallbacks(runnable);
						onlinelayout.setVisibility(View.VISIBLE);
						onlinelayout.setBackgroundColor(Color.parseColor("#F79141"));
						onlinelayout.invalidate();
						onlinelayout.startAnimation(slideUp);
						onlinelayout.invalidate();
						
					} else if (prefs.getString(stored_account_register_status,"Connecting").equals("Offline")) {
						onlinelayout.invalidate();
						handler.removeCallbacks(runnable);
						onlinelayout.setVisibility(View.VISIBLE);
						onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
						onlinelayout.invalidate();
						onlinelayout.startAnimation(slideUp);
						onlinelayout.invalidate();
					}
					onlinelayout.invalidate();
					
				}
				}
			catch(Exception e)
			{
				
			}

		}

	}

	class AccountStatusContentObserver extends ContentObserver {
		public AccountStatusContentObserver(Handler h) {
			super(h);
		}

		public void onChange(boolean selfChange) {
			Log.d("AccountStatusContentObserver", "Accounts status.onChange( "+ selfChange + ")");
			updateRegistrationsState();
		}
	}

	private static final int TOAST_MESSAGE = 0;

	private Handler serviceHandler = new ServiceHandler(this);

	private static class ServiceHandler extends Handler {
		WeakReference<R4wHome> s;

		public ServiceHandler(R4wHome r4wHome) {
			s = new WeakReference<R4wHome>(r4wHome);
		}

		@Override
		public void handleMessage(Message msg) {
			System.out.println("SipService.java inhandleMessage()");
			super.handleMessage(msg);
			R4wHome sipService = s.get();
			if (sipService == null) {
				return;
			}
			if (msg.what == TOAST_MESSAGE) {
				if (msg.arg1 != 0) {
					Toast.makeText(sipService, msg.arg1, Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(sipService, (String) msg.obj,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	 public void onDestroy() {
		        super.onDestroy();
	         ViewServer.get(this).removeWindow(this);
	         Debug.stopMethodTracing();
	     }

	
	
}
