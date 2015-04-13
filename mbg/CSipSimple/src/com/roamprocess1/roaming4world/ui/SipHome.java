package com.roamprocess1.roaming4world.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout.LayoutParams;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi;
import com.roamprocess1.roaming4world.api.SipConfigManager;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.api.SipProfileState;
import com.roamprocess1.roaming4world.db.DBProvider;
import com.roamprocess1.roaming4world.models.Filter;
import com.roamprocess1.roaming4world.roaming4world.AsynctaskUpdateUsername;
import com.roamprocess1.roaming4world.roaming4world.CallRecords;
import com.roamprocess1.roaming4world.roaming4world.CurrentFragment;
import com.roamprocess1.roaming4world.roaming4world.HowToUse;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;
import com.roamprocess1.roaming4world.roaming4world.NoNetwork;
import com.roamprocess1.roaming4world.roaming4world.R4WAddCredit;
import com.roamprocess1.roaming4world.roaming4world.R4WSetting;
import com.roamprocess1.roaming4world.roaming4world.R4WVoiceMail;
import com.roamprocess1.roaming4world.roaming4world.R4WWeatherInfo;
import com.roamprocess1.roaming4world.roaming4world.R4wAccount;
import com.roamprocess1.roaming4world.roaming4world.R4wActivate;
import com.roamprocess1.roaming4world.roaming4world.R4wActivation;
import com.roamprocess1.roaming4world.roaming4world.R4wFlightDetails;
import com.roamprocess1.roaming4world.roaming4world.R4wMapService;
import com.roamprocess1.roaming4world.roaming4world.R4wPaymentWebView;
import com.roamprocess1.roaming4world.roaming4world.R4wReRunCode;
import com.roamprocess1.roaming4world.roaming4world.R4wflightActivity;
import com.roamprocess1.roaming4world.roaming4world.SQLiteAdapter;
import com.roamprocess1.roaming4world.service.ChatSocket;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.stripepayment.PaymentActivity;
import com.roamprocess1.roaming4world.ui.account.AccountsEditList;
import com.roamprocess1.roaming4world.ui.dialpad.DialerFragment;
import com.roamprocess1.roaming4world.ui.messages.ConversationsListFragment;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.ui.recents.Recents;
import com.roamprocess1.roaming4world.utils.CustomDistribution;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.PreferencesProviderWrapper;
import com.roamprocess1.roaming4world.utils.PreferencesWrapper;
import com.roamprocess1.roaming4world.utils.backup.BackupWrapper;
import com.roamprocess1.roaming4world.wizards.BasePrefsWizard;
import com.roamprocess1.roaming4world.wizards.WizardIface;
import com.roamprocess1.roaming4world.wizards.WizardUtils;
import com.roamprocess1.roaming4world.wizards.WizardUtils.WizardInfo;

public class SipHome extends SherlockFragmentActivity implements
		View.OnClickListener {

	public static String TAG = "SipHome";
	String onClick_leftDrawer_Value = "Home";
	private PreferencesProviderWrapper prefProviderWrapper;
	private boolean hasTriedOnceActivateAcc = false;
	private ViewPager mViewPager;
	ActionBar ab;
	FragmentTabHost mTabHost;
	ImageButton imgLeftMenu, imgRightMenu, imgRightMenuChat,
			imgRightMenu_refresh, imgClearList, imgOutCredit;
	private static FragmentTransaction ft = null;
	private static SherlockFragment fragment = null;
	PreferencesWrapper prefs_wrapper;
	public SipProfile account = null;
	long accountId = -1;
	private WizardIface wizard = null;
	private String wizardId = "";
	public SharedPreferences prefs;
	boolean frgmentreplace = false;
	ListItemsAdapter_Left Left_Adapter;
	ListItemsAdapter_Right Right_Adapter;
	ImageView userImageView, lluserImageView;
	static int service_count = 0, fetch_flag, pinNo, called_this_time = 0;
	private Context mContext;
	private Cursor dataCheckPin;
	private final static String APP_TITLE = "Roaming4world";
	private PopupWindow selectWindow = null;
	public ArrayList<String> dataArray_right = new ArrayList<String>();
	ArrayList<Object> objectArray_right = new ArrayList<Object>();
	ArrayList<String> dataArray_left = new ArrayList<String>();
	ArrayList<Object> objectArray_left = new ArrayList<Object>();
	DrawerLayout mDrawerlayout;
	ListView mDrawerList_Left, mDrawerList_Right;
	private static android.support.v4.app.FragmentManager fragmentManager;
	private final static String APP_PNAME = "com.roamprocess1.roaming4world";
	private SQLiteAdapter mySQLiteAdapter;
	public SipProfile account_info = null;
	ActionBarDrawerToggle mDrawerToggle;
	int serverResponseCode = 0;
	String imagePathUri = "", stored_username, stored_userimagepath,
			stored_user_bal, stored_chatSocket_connect;
	public Boolean isR4wfragment = false;
	public ImageView imgVAddcontact;
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	private Typeface BebasNeue;
	public RelativeLayout relativeHeader;
	String stored_supportnumber, supportnum;
	public LinearLayout LnrImgLeftMenu, LnrImgRightRef, LnrImgRightAddCont,
			LnrImgRightChat, LnrImgRightOutCredit;
	Intent chatSocket_intent;

	private String getPin, stored_user_dialer_password, stored_leftTab,
			get_user_dialer_password, stored_account_register_status,
			stored_registered_account, prefChooseclass,
			stored_server_ipaddress, stored_user_leftDrawer_status,
			stored_user_mobile_no, stored_user_country_code, account_name,
			user_name, server, password_response, show, lastOfflineMessage,
			getlastOfflineMessage;
	ArrayList<SipProfile> result_profile, result_profile_login;
	public LinearLayout onlinelayout;
	AccountStatusContentObserver statusObserver = null;
	static int checkstatusleftListView = 1;

	public interface ViewPagerVisibilityListener {
		void onVisibilityChanged(boolean visible);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		prefProviderWrapper = new PreferencesProviderWrapper(this);
		super.onCreate(savedInstanceState);
		prefChooseclass = "com.roamprocess1.roaming4world.chooseClass";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_account_register_status = "com.roamprocess1.roaming4world.account_register_status";
		stored_registered_account = "com.roamprocess1.roaming4world.account_registered";
		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_user_leftDrawer_status = "com.roamprocess1.roaming4world.leftDrawer_status";
		stored_user_dialer_password = "com.roamprocess1.roaming4world.dialer_password";
		stored_supportnumber = "com.roamprocess1.roaming4world.support_no";
		stored_chatSocket_connect = "com.roamprocess1.roaming4world.chatSocket_connect";

		lastOfflineMessage = "com.roamprocess1.roaming4world.lastOfflineMessage";
		get_user_dialer_password = prefs.getString(stored_user_dialer_password,
				"no password");

		getlastOfflineMessage = prefs.getString(lastOfflineMessage, "noMsgId");

		System.out.println(" oncreateView  getlastOfflineMessage:"
				+ getlastOfflineMessage);

		Log.d("user dialer password in r4whome", get_user_dialer_password);
		prefs.edit().putString(prefChooseclass, "1");
		String tempstatus = prefs.getString(stored_user_leftDrawer_status, "1");
		checkstatusleftListView = Integer.parseInt(tempstatus);
		stored_leftTab = "com.roamprocess1.roaming4world.left_tabselection";
		stored_username = "com.roamprocess1.roaming4world.username";
		stored_userimagepath = "com.roamprocess1.roaming4world.userimagepath";
		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";
		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";

		BebasNeue = Typeface
				.createFromAsset(getAssets(), "fonts/BebasNeue.otf");

		setContentView(R.layout.home);

		// setTimeinPrefs();

		System.out
				.println("Sip Home oncreate stored_account_register_status :"
						+ prefs.getString(stored_account_register_status,
								"Connecting"));
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.Frame_Layout);
		mTabHost.getTabWidget().setBackgroundColor(Color.parseColor("#D6D6D6"));
		mTabHost.getTabWidget().setStripEnabled(false);
		onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
		AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
		AnimationUtils
				.loadAnimation(getApplicationContext(), R.anim.slide_down);

		Roaming4WorldCustomApi.textordigit = true;
		mTabHost.addTab(
				mTabHost.newTabSpec("Contacts")
						.setIndicator(
								null,
								getResources().getDrawable(
										R.drawable.contact_selector)),
				RContactlist.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec("Recents")
						.setIndicator(
								null,
								getResources().getDrawable(
										R.drawable.recents_selector)),
				Recents.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec("Chat").setIndicator(null,
						getResources().getDrawable(R.drawable.chat_selector)),
				ConversationsListFragment.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec("Dialer").setIndicator(null,
						getResources().getDrawable(R.drawable.dialer_selector)),
				DialerFragment.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec("R4W").setIndicator(null,
						getResources().getDrawable(R.drawable.r4w_selector)),
				R4wActivation.class, null);

		mTabHost.getTabWidget().getChildAt(0)
				.setBackgroundColor(Color.parseColor("#FFFFFF"));
		mTabHost.getTabWidget().getChildAt(1)
				.setBackgroundColor(Color.parseColor("#D6D6D6"));
		mTabHost.getTabWidget().getChildAt(2)
				.setBackgroundColor(Color.parseColor("#D6D6D6"));
		mTabHost.getTabWidget().getChildAt(3)
				.setBackgroundColor(Color.parseColor("#D6D6D6"));
		mTabHost.getTabWidget().getChildAt(4)
				.setBackgroundColor(Color.parseColor("#D6D6D6"));
		startSipService();
		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				// TODO Auto-generated method stub

				if (tabId.equalsIgnoreCase("Dialer")) {
					isR4wfragment = false;
					relativeHeader.setBackgroundColor(Color
							.parseColor("#4555a5"));
					imgVAddcontact.setVisibility(View.GONE);
					imgOutCredit.setVisibility(View.VISIBLE);
					imgRightMenu.setVisibility(View.GONE);
					imgRightMenuChat.setVisibility(View.GONE);
					imgRightMenu_refresh.setVisibility(View.GONE);
					imgClearList.setVisibility(View.GONE);
					mTabHost.setCurrentTab(3);
				}

				if (tabId.equalsIgnoreCase("Recents")) {
					isR4wfragment = false;
					relativeHeader.setBackgroundColor(Color
							.parseColor("#8BC249"));
					imgVAddcontact.setVisibility(View.GONE);
					imgOutCredit.setVisibility(View.GONE);
					imgRightMenu.setVisibility(View.GONE);
					imgRightMenuChat.setVisibility(View.GONE);
					imgRightMenu_refresh.setVisibility(View.GONE);
					imgClearList.setVisibility(View.VISIBLE);
					mTabHost.setCurrentTab(1);
				}

				if (tabId.equalsIgnoreCase("Chat")) {
					isR4wfragment = false;
					relativeHeader.setBackgroundColor(Color
							.parseColor("#F05A2B"));
					imgVAddcontact.setVisibility(View.GONE);
					imgOutCredit.setVisibility(View.GONE);
					imgRightMenuChat.setVisibility(View.VISIBLE);
					imgRightMenu.setVisibility(View.GONE);
					imgRightMenu_refresh.setVisibility(View.GONE);
					imgClearList.setVisibility(View.GONE);
					mTabHost.setCurrentTab(2);
				}

				if (tabId.equalsIgnoreCase("Contacts")) {
					isR4wfragment = false;
					imgVAddcontact.setVisibility(View.GONE);
					relativeHeader.setBackgroundColor(Color
							.parseColor("#1EBCD4"));
					imgOutCredit.setVisibility(View.GONE);
					imgRightMenuChat.setVisibility(View.GONE);
					imgRightMenu.setVisibility(View.GONE);
					imgClearList.setVisibility(View.GONE);
					imgRightMenu_refresh.setVisibility(View.VISIBLE);
					mTabHost.setCurrentTab(0);
				}

				if (tabId.equalsIgnoreCase("R4W")) {

					isR4wfragment = true;
					imgOutCredit.setVisibility(View.VISIBLE);
					imgVAddcontact.setVisibility(View.GONE);
					imgRightMenu_refresh.setVisibility(View.GONE);
					imgRightMenuChat.setVisibility(View.GONE);
					imgClearList.setVisibility(View.GONE);
					imgRightMenu.setVisibility(View.GONE);
					mTabHost.setCurrentTab(4);
					// startActivity(new Intent(SipHome.this, Typing.class));
					// startActivityForResult(new
					// Intent(SipManager.ACTION_UI_PREFS_GLOBAL), 1);

				}

				int position = mTabHost.getCurrentTab();
				for (int i = 0; i < 5; i++) {
					if (i == position)
						mTabHost.getTabWidget()
								.getChildAt(position)
								.setBackgroundColor(Color.parseColor("#FFFFFF"));
					else
						mTabHost.getTabWidget().getChildAt(i)
								.setBackground(null);
				}
				if (position == 0)
					Roaming4WorldCustomApi.textordigit = true;
				else if (position == 3)
					Roaming4WorldCustomApi.textordigit = false;
			}
		});

		new MyAsyncTaskGetBalance().execute();

		/* Drawer code start from here */

		mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList_Left = (ListView) findViewById(R.id.drawer_list_left);
		mDrawerList_Right = (ListView) findViewById(R.id.drawer_list_right);
		imgLeftMenu = (ImageButton) findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) findViewById(R.id.imgRightMenu);

		mDrawerlayout.setDrawerListener(mDrawerToggle);
		mDrawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				findViewById(R.id.drawer_list_right));

		LayoutInflater inflator = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.paymentheader, null);

		relativeHeader = (RelativeLayout) v.findViewById(R.id.relativeHeader);
		// LnrImgLeftMenu=(LinearLayout)v.findViewById(R.id.LnrImgLeftMenu);
		// LnrImgRightRef=(LinearLayout)v.findViewById(R.id.LnrImgRightRef);

		// LnrImgRightOutCredit=(LinearLayout)v.findViewById(R.id.LnrImgRightOutCredit);

		imgLeftMenu = (ImageButton) v.findViewById(R.id.imgLeftMenu);
		imgRightMenu = (ImageButton) v.findViewById(R.id.imgRightMenu);
		imgRightMenuChat = (ImageButton) v.findViewById(R.id.imgRightMenu_chat);
		imgRightMenu_refresh = (ImageButton) v
				.findViewById(R.id.imgRightMenu_refresh);
		LnrImgLeftMenu = (LinearLayout) v.findViewById(R.id.LnrImgLeftMenu);

		imgClearList = (ImageButton) v.findViewById(R.id.imgClearList);
		imgOutCredit = (ImageButton) v.findViewById(R.id.imgOutCredit);
		imgVAddcontact = (ImageView) v.findViewById(R.id.imgVAddcontact);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		mContext = this;
		mySQLiteAdapter = new SQLiteAdapter(this);
		mySQLiteAdapter.openToRead();

		Log.setLogLevel(6);
		System.out.println("dataCheckPin---" + dataCheckPin + "-");
		dataCheckPin = mySQLiteAdapter.fetch_check_last_pin();
		Log.d("dataCheckPin Length", dataCheckPin.getCount() + "");
		Log.d("dataCheckPin Columns", dataCheckPin.getColumnCount() + "");

		getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setCustomView(v);

		imgRightMenuChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Intent intent= new
				// Intent(SipHome.this,ContactListActivity.class);
				Intent intent = new Intent(SipHome.this, MessageSipUri.class);
				startActivity(intent);
			}
		});

		LnrImgLeftMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Log.setLogLevel(6);
				Log.d("mDrawerlayout.isDrawerOpen(mDrawerList_Left)",
						mDrawerlayout.isDrawerOpen(mDrawerList_Left) + " @");
				if (mDrawerlayout.isDrawerOpen(mDrawerList_Left)) {
					mDrawerlayout.closeDrawer(mDrawerList_Left);
				} else {
					mDrawerlayout.openDrawer(mDrawerList_Left);
				}
				/*
				 * if (mDrawerlayout.isDrawerOpen(mDrawerList_Right)) {
				 * mDrawerlayout.closeDrawer(mDrawerList_Right); }
				 * mDrawerlayout.openDrawer(mDrawerList_Left);
				 */
			}
		});

		imgLeftMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Log.setLogLevel(6);
				Log.d("mDrawerlayout.isDrawerOpen(mDrawerList_Left)",
						mDrawerlayout.isDrawerOpen(mDrawerList_Left) + " @");

				if (mDrawerlayout.isDrawerOpen(mDrawerList_Left)) {
					mDrawerlayout.closeDrawer(mDrawerList_Left);
				} else {
					mDrawerlayout.openDrawer(mDrawerList_Left);
				}
				/*
				 * if (mDrawerlayout.isDrawerOpen(mDrawerList_Right)) {
				 * mDrawerlayout.closeDrawer(mDrawerList_Right); }
				 * mDrawerlayout.openDrawer(mDrawerList_Left);
				 */
			}
		});

		imgRightMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.setLogLevel(6);
				Log.d("mDrawerlayout.isDrawerOpen(mDrawerList_Left)",
						mDrawerlayout.isDrawerOpen(mDrawerList_Left) + " @");

				if (mDrawerlayout.isDrawerOpen(mDrawerList_Right)) {
					mDrawerlayout.closeDrawer(mDrawerList_Right);
				} else {
					mDrawerlayout.openDrawer(mDrawerList_Right);
				}

				/*
				 * if (mDrawerlayout.isDrawerOpen(mDrawerList_Left)) {
				 * mDrawerlayout.closeDrawer(mDrawerList_Left); }
				 * mDrawerlayout.openDrawer(mDrawerList_Right);
				 */
			}
		});

		imgOutCredit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				outCallRechargePopup(v, "123566");
			}
		});

		account = SipProfile.getProfileFromDbId(this, accountId,
				DBProvider.ACCOUNT_FULL_PROJECTION);
		result_profile = SipProfile.getAllProfiles(getApplicationContext(),
				false);
		prefs_wrapper = new PreferencesWrapper(getApplicationContext());
		if (prefs.getString(stored_registered_account, "0").equals("0")) {
			System.out.println("R4wHome  step19");
			login();
		}
		try {
			final LinearLayout onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
			TextView status_account = (TextView) findViewById(R.id.status);
			System.out.println("status_account != null");
			System.out.println("status_account != null :Reg:"
					+ prefs.getString(stored_account_register_status,
							"Connecting"));
			String regStatus = prefs.getString(stored_account_register_status,
					"Connecting");
			Typeface tf = Typeface.createFromAsset(this.getAssets(),
					"fonts/NexaBold.otf");
			status_account.setTypeface(tf);

			if (regStatus.equals("Online")) {
				System.out.println("SipHome Online if  ");
				status_account.setText("Online");
				onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
				status_account.invalidate();
				onlinelayout.invalidate();
			} else if (regStatus.equals("Connecting")) {
				System.out.println("SipHome Online elses ");
				status_account.setText("Connecting");
				onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				status_account.invalidate();
				onlinelayout.invalidate();
			}

		} catch (Exception e) {
		}
		if (statusObserver == null) {
			System.out.println("statusObserver == null");
			statusObserver = new AccountStatusContentObserver(serviceHandler);
			getContentResolver().registerContentObserver(
					SipProfile.ACCOUNT_STATUS_URI, true, statusObserver);
		}
		dataCheckPin.moveToFirst();
		while (!dataCheckPin.isAfterLast()) {

			fetch_flag = dataCheckPin.getInt(dataCheckPin
					.getColumnIndex(SQLiteAdapter.FN_FLAG));
			if (fetch_flag == 0) {

			} else {
				getPin = dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_PIN_NO));
				dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_COUNTRY_CODE));
				dataCheckPin.getString(dataCheckPin
						.getColumnIndex(SQLiteAdapter.FN_SELF_NO));

			}
			dataCheckPin.moveToNext();
		}

		// startService(new Intent(this, R4WServiceImageDownload.class));
		// startService(new
		// Intent(SipHome.this,R4WServiceMissedcallDownload.class));
		prefs.edit().putBoolean(stored_chatSocket_connect, true).commit();
		chatSocket_intent = new Intent(this, ChatSocket.class);
		// startService(chatSocket_intent);

	}

	public void setTimeinPrefs() {
		PreferencesWrapper.STRING_PREFS.put(SipConfigManager.TSX_T1_TIMEOUT,
				"10");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		prefs.edit().putBoolean(stored_chatSocket_connect, false).commit();
		stopService(chatSocket_intent);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.setLogLevel(6);

	}

	public void composeChat(View v) {
		ConversationsListFragment cv = new ConversationsListFragment();
		cv.onClickAddMessage();
	}

	public void callrightDrawer(View v) {
		/*
		 * if (mDrawerlayout.isDrawerOpen(mDrawerList_Right)) {
		 * mDrawerlayout.closeDrawer(mDrawerList_Right); }
		 * mDrawerlayout.openDrawer(mDrawerList_Right);
		 */

	}

	private class ViewHolder {
		TextView text, text_bal;
		ImageView enable_status, enable_icon;
		RelativeLayout rl_enable;
		Button btnaddcredit;
	}

	private class ListItemsAdapter_Right extends ArrayAdapter<Object> {

		ViewHolder holder1;

		public ListItemsAdapter_Right(List<Object> items, int x) {
			super(SipHome.this,
					android.R.layout.simple_list_item_single_choice, items);
		}

		@Override
		public String getItem(int position) {
			return dataArray_right.get(position);
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
			dataArray_right.get(position);
			holder1.text.setText(dataArray_right.get(position));
			return convertView;
		}

	}

	public void editusername(View v) {
		if (getConnectivityStatus(getApplicationContext()) == TYPE_NOT_CONNECTED) {
			dialogBoxNoInternet();
		} else {
			dialogboxUsreName(v);
		}
	}

	public void dialogboxUsreName(final View v) {

		final String userName = prefs.getString(stored_username, "");

		final String number;
		number = prefs.getString(stored_user_country_code, "")
				+ prefs.getString(stored_user_mobile_no, "");

		final Dialog dialoguserName = new Dialog(SipHome.this);
		dialoguserName.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialoguserName.setContentView(R.layout.dialogboxforusername);
		dialoguserName.show();
		Button btnOk = (Button) dialoguserName.findViewById(R.id.btnUserNameOk);
		Button btnCancel = (Button) dialoguserName
				.findViewById(R.id.btnUserNameCancel);
		final EditText edtUserName = (EditText) dialoguserName
				.findViewById(R.id.EdtUserName);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				int action = 0;

				if (!userName.equalsIgnoreCase(""))
					action = 1;

				String getUserName = edtUserName.getText().toString();
				findViewById(R.id.txtData);
				System.out.println("UserName:" + getUserName);

				TextView txtUserName = (TextView) findViewById(R.id.txtData_username);
				txtUserName.setText(getUserName);

				prefs.edit().putString(stored_username, getUserName).commit();
				dialoguserName.dismiss();
				Fill_LeftList();
				RefreshListView();
				new AsynctaskUpdateUsername(SipHome.this, number, getUserName,
						action).execute();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialoguserName.dismiss();
			}
		});

	}

	private class ListItemsAdapter_Left extends ArrayAdapter<Object> {
		ViewHolder holder1;

		public ListItemsAdapter_Left(List<Object> items, int x) {
			super(SipHome.this,
					android.R.layout.simple_list_item_single_choice, items);
		}

		@Override
		public String getItem(int position) {
			return dataArray_left.get(position);
		}

		@Override
		public int getCount() {
			return dataArray_left.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("SdCardPath")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflator = getLayoutInflater();
			holder1 = new ViewHolder();
			// ImageView imageView = null;

			if (CurrentFragment.name.equals("EnterAPIN")
					|| CurrentFragment.name.equals("ActivationFwd")
					|| CurrentFragment.name.equals("MapService")
					|| CurrentFragment.name.equals("Home")) {

				if (position == 0) {
					// convertView =
					// inflator.inflate(R.layout.r4wuserimagerow,null);
					convertView = inflator.inflate(R.layout.r4wusername, null);
					holder1.text = (TextView) convertView
							.findViewById(R.id.txtData_username);

					holder1.text_bal = (TextView) convertView
							.findViewById(R.id.tv_ld_balance);

					holder1.btnaddcredit = (Button) convertView
							.findViewById(R.id.btnaddcredit);
					holder1.btnaddcredit.setOnClickListener(SipHome.this);

					String imgaddress = prefs.getString(stored_userimagepath,
							"");
					try {
						Log.d("stored_user_bal holder",
								prefs.getString(stored_user_bal, "0") + " @");
						holder1.text_bal.setTypeface(BebasNeue);
						holder1.text_bal.setText("Balance $ "
								+ prefs.getString(stored_user_bal, "0"));
						holder1.text.setText(dataArray_left.get(1));
						lluserImageView = (ImageView) convertView
								.findViewById(R.id.iv_userImageView);
						if (imgaddress.equals("")) {
							lluserImageView
									.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
						} else {
							Bitmap bmp = BitmapFactory.decodeFile(imgaddress);
							bmp = ImageHelperCircular.getRoundedCornerBitmap(
									bmp, 200);
							lluserImageView.setImageBitmap(bmp);
						}

						String fileuri = "/sdcard/R4W/ProfilePic/ProfilePic.png";
						File imageDirectoryprofile = new File(fileuri);
						if (imageDirectoryprofile.exists()) {
							Bitmap img = BitmapFactory.decodeFile(fileuri);
							img = ImageHelperCircular.getRoundedCornerBitmap(
									img, 200);
							lluserImageView.setImageBitmap(img);

						}

					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				else {

					convertView = inflator.inflate(
							R.layout.r4wleftdrawerenable, null);
					holder1.text = (TextView) convertView
							.findViewById(R.id.txtData);

					holder1.rl_enable = (RelativeLayout) convertView
							.findViewById(R.id.rlleftdrawer_enable);

					if (dataArray_left.get(position) == "Home") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.home_icon_left);
					} else if (dataArray_left.get(position) == "My Account") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.mycontacts);
					} else if (dataArray_left.get(position) == "Call Details") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.call_details);
					} else if (dataArray_left.get(position) == "Re-Call Activation") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.recall_icon_left);
					} else if (dataArray_left.get(position) == "Voice Mail") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.voicemail);
					} else if (dataArray_left.get(position) == "How to use") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.howto_use);
					} else if (dataArray_left.get(position) == "Rate Us") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.rate_icon_left);
					} else if (dataArray_left.get(position) == "Like and Share") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.share_icon_left);
					} else if (dataArray_left.get(position) == "Chat Support") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.support_icon_left);
					} else if (dataArray_left.get(position) == "Get Flight Info") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.flightinfo);
					} else if (dataArray_left.get(position) == "Get Weather Info") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.weather);
					} else if (dataArray_left.get(position) == "Settings") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.support_icon_left);
					} else if (dataArray_left.get(position) == "Exit") {
						holder1.enable_icon = (ImageView) convertView
								.findViewById(R.id.enable_leftdrawer_icon);
						holder1.enable_icon
								.setBackgroundResource(R.drawable.exit_icon_left);
					}

					holder1.enable_status = (ImageView) convertView
							.findViewById(R.id.tgl_status_enable);
					if (position == checkstatusleftListView) {
						// holder1.rl_enable.setBackgroundResource(R.drawable.backband);
						// holder1.enable_status.setVisibility(ImageView.VISIBLE);
					}
				}

			}
			convertView.setTag(holder1);
			dataArray_left.get(position);
			try {
				holder1.text.setText(dataArray_left.get(position));

			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

	}

	// Service monitoring stuff
	private void startSipService() {
		Thread t = new Thread("StartSip") {
			public void run() {
				Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
				// Optional, but here we bundle so just ensure we are using
				// csipsimple package
				serviceIntent.setPackage(SipHome.this.getPackageName());
				serviceIntent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY,
						new ComponentName(SipHome.this, SipHome.class));
				startService(serviceIntent);
				postStartSipService();
			};
		};
		t.start();

	}

	private void postStartSipService() {
		// If we have never set fast settings
		if (CustomDistribution.showFirstSettingScreen()) {
			if (!prefProviderWrapper.getPreferenceBooleanValue(
					PreferencesWrapper.HAS_ALREADY_SETUP, false)) {
				Intent prefsIntent = new Intent(SipManager.ACTION_UI_PREFS_FAST);
				prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(prefsIntent);
				return;
			}
		} else {
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
					Log.e(TAG,
							"Something went wrong while retrieving the account",
							e);
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

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (getIntent().getBooleanExtra("MissedCall", false)) {
			isR4wfragment = false;
			relativeHeader.setBackgroundColor(Color.parseColor("#8BC249"));
			imgVAddcontact.setVisibility(View.GONE);
			imgOutCredit.setVisibility(View.GONE);
			imgRightMenu.setVisibility(View.GONE);
			imgRightMenuChat.setVisibility(View.GONE);
			imgRightMenu_refresh.setVisibility(View.GONE);
			imgClearList.setVisibility(View.VISIBLE);
			mTabHost.setCurrentTab(1);
		}

		Fill_LeftList();
		Fill_RightList();
		RefreshListView();
	}

	Integer initTabId = null;

	@Override
	protected void onDestroy() {

		super.onDestroy();
		Log.d(TAG, "---DESTROY SIP HOME END---");
		prefs.edit().putBoolean(stored_chatSocket_connect, false).commit();

	}

	private final static int CHANGE_PREFS = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CHANGE_PREFS) {
			sendBroadcast(new Intent(SipManager.ACTION_SIP_REQUEST_RESTART));
			BackupWrapper.getInstance(this).dataChanged();
		}

		Log.d("onActivityResult", " in");
		Log.d("requestCode", requestCode + " in");
		Log.d("resultCode", resultCode + " in");
		Log.d("data", data + " in");
		if (requestCode == RESULT_CANCELED && resultCode == 0
				&& resultCode == -1) {
			Toast.makeText(SipHome.this, "Please try again", Toast.LENGTH_SHORT)
					.show();
		} else if (requestCode == 2 && data != null) {
			try {

				Bundle extras2 = data.getExtras();
				Log.d("selectedImage", " before");
				String picturePath = "";
				if (extras2 != null) {
					Bitmap bmp = extras2.getParcelable("data");
					picturePath = saveUserImage(bmp);
					imagePathUri = picturePath;
					Log.d("picturePath", picturePath + " !");

					prefs.edit().putString(stored_userimagepath, picturePath)
							.commit();

					bmp = ImageHelperCircular.getRoundedCornerBitmap(bmp,
							bmp.getWidth());
					lluserImageView.setImageBitmap(bmp);
					new AsyncTaskProfilePicUpload(
							imagePathUri,
							getApplicationContext(),
							prefs.getString(stored_user_country_code, "")
									+ prefs.getString(stored_user_mobile_no, ""))
							.execute();
				}

			} catch (Exception e) {

			}

		}

	}

	@SuppressLint("SdCardPath")
	private String saveUserImage(Bitmap userBitmap) {

		// TODO Auto-generated method stub

		File imageDirectory = new File("/sdcard/R4W/");

		if (!imageDirectory.exists()) {
			imageDirectory.mkdir();
		}
		File imageDirectoryprofile = new File("/sdcard/R4W/ProfilePic");
		if (!imageDirectoryprofile.exists()) {
			imageDirectoryprofile.mkdir();
		}

		File file = new File(imageDirectoryprofile.getAbsolutePath(),
				"ProfilePic.png");
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			// Convert bitmap to byte array
			Bitmap bitmap = userBitmap;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
			byte[] bitmapdata = bos.toByteArray();

			// write the bytes in file
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bitmapdata);

			bos.close();
			fos.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return file.getAbsolutePath();

	}

	public void userimagechange(View v) {

		if (getConnectivityStatus(getApplicationContext()) == TYPE_NOT_CONNECTED) {
			dialogBoxNoInternet();
		} else {

			Intent intent = new Intent();

			try {
				startActivityForResult(Intent.createChooser(
						StaticValues.setImageTypeIntent(intent),
						"Complete action using"), 2);

			} catch (ActivityNotFoundException e) {
				// Do nothing for now
			}

		}
	}

	public String getPath(Context context, Uri uri) throws URISyntaxException {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	private void dialogBoxNoInternet() {
		new AlertDialog.Builder(SipHome.this).setTitle("No Network")
				.setMessage("There is no internet connection")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();

	}

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

	public Bitmap ShrinkBitmap(String file, int width, int height) {
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
		int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight
				/ (float) height);
		int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth
				/ (float) width);
		if (heightRatio > 1 || widthRatio > 1) {
			if (heightRatio > widthRatio) {
				bmpFactoryOptions.inSampleSize = heightRatio;
			} else {
				bmpFactoryOptions.inSampleSize = widthRatio;
			}
		}
		bmpFactoryOptions.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
		return bitmap;
	}

	public void create_file(String value) {
		String FILENAME = "user_pref";
		String string = value;

		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			try {
				fos.write(string.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void switchToFragment(int val) {
		mViewPager.setCurrentItem(val);
	}

	public void Fill_LeftList() {

		String getPrefUserName = prefs.getString(stored_username, "User Name");

		Log.d("UserName inside the Fill_LeftList", getPrefUserName);
		boolean left_tabselection = false;
		left_tabselection = prefs.getBoolean(stored_leftTab, false);

		dataArray_left.clear();

		if (getPrefUserName.equals("User Name")) {
			dataArray_left.add("User Name");
		} else {
			dataArray_left.add(getPrefUserName);
		}

		dataArray_left.add("Home");
		if (left_tabselection == true) {
			dataArray_left.add("My Account");
			dataArray_left.add("Call Details");
			dataArray_left.add("Re-Call Activation");
			dataArray_left.add("Voice Mail");
		}
		dataArray_left.add("How to use");
		dataArray_left.add("Rate Us");
		dataArray_left.add("Like and Share");
		dataArray_left.add("Chat Support");
		dataArray_left.add("Get Flight Info");
		dataArray_left.add("Get Weather Info");
		dataArray_left.add("Settings");
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

	private void displayView(int position, String left_onClick) {

		RefreshListView();

		switch (left_onClick) {
		/*
		 * case 0:
		 * 
		 * startActivity(new Intent(this, AccountsEditList.class)); break;
		 */
		case "0":
			// dialogboxUsreName();

			startActivityForResult(
					new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), CHANGE_PREFS);
			break;

		case "Home":

			// startActivityForResult(new
			// Intent(SipManager.ACTION_UI_PREFS_GLOBAL), CHANGE_PREFS);
			if (isR4wfragment) {
				ft = getSupportFragmentManager().beginTransaction();
				if (fetch_flag == 0)
					ft.replace(R.id.Frame_Layout, new R4wActivation()).commit();
				else if (fetch_flag == 1)
					ft.replace(R.id.Frame_Layout, new R4wFlightDetails())
							.commit();
				else if (fetch_flag == 2)
					ft.replace(R.id.Frame_Layout, new R4wMapService()).commit();
				else if (fetch_flag == 3)
					ft.replace(R.id.Frame_Layout, new R4wActivate()).commit();
			} else
				frgmentreplace = false;

			break;

		case "My Account":
			ft = getSupportFragmentManager().beginTransaction();
			if (!(CurrentFragment.name.equals("EnterAPIN")
					|| CurrentFragment.name.equals("ActivationFwd") || CurrentFragment.name
						.equals("MapService")))
				ft.replace(R.id.Frame_Layout, new R4wAccount()).commit();

			else if (CurrentFragment.name.equals("MapService"))
				ft.replace(R.id.Frame_Layout, new R4wAccount()).commit();
			break;
		case "Call Details":
			ft = getSupportFragmentManager().beginTransaction();
			if (!(CurrentFragment.name.equals("EnterAPIN")
					|| CurrentFragment.name.equals("ActivationFwd") || CurrentFragment.name
						.equals("MapService")))
				ft.replace(R.id.Frame_Layout, new CallRecords()).commit();
			else if (CurrentFragment.name.equals("MapService"))
				ft.replace(R.id.Frame_Layout, new CallRecords()).commit();
			break;
		case "Re-Call Activation":
			ft = getSupportFragmentManager().beginTransaction();
			if (!(CurrentFragment.name.equals("EnterAPIN") || CurrentFragment.name
					.equals("ActivationFwd")))
				ft.replace(R.id.Frame_Layout, new R4wReRunCode()).commit();
			else if (CurrentFragment.name.equals("MapService")
					|| CurrentFragment.name.equals("ActivationFwd"))
				ft.replace(R.id.Frame_Layout, new R4wReRunCode()).commit();

			break;
		case "How to use":
			frgmentreplace = false;
			Intent howToUse = new Intent(this, HowToUse.class);
			startActivity(howToUse);

			// ft = getSupportFragmentManager().beginTransaction();
			// ft.replace(R.id.Frame_Layout, new HowToUse()).commit();

			break;
		case "Rate Us":
			showRateDialog(mContext);
			break;
		case "Like and Share":
			// ft = getSupportFragmentManager().beginTransaction();
			// ft.replace(R.id.Frame_Layout, new MainActivity()).commit();

			break;
		case "Chat Support":

			frgmentreplace = false;
			// Intent ticketSupport= new Intent(this,TicketSupport.class);
			// startActivity(ticketSupport);
			supportnum = prefs.getString(stored_supportnumber, "");
			if (!supportnum.equals("") && supportnum != null) {
				callMessage(supportnum);
			}
			// ft = getSupportFragmentManager().beginTransaction();
			// ft.replace(R.id.Frame_Layout, new TicketSupport()).commit();

			break;
		case "Get Flight Info":
			// ft = getSupportFragmentManager().beginTransaction();
			// ft.replace(R.id.Frame_Layout, new R4wFlightDetails()).commit();
			// fragment = new R4wActivateFwd();
			frgmentreplace = false;
			Intent r4wFlightIntent = new Intent(this, R4wflightActivity.class);
			startActivity(r4wFlightIntent);
			break;

		case "Voice Mail":
			ft = getSupportFragmentManager().beginTransaction();
			if (fetch_flag != 0) {
				if (!(CurrentFragment.name.equals("EnterAPIN")))
					ft.replace(R.id.Frame_Layout, new R4WVoiceMail()).commit();
			}
			break;
		case "Get Weather Info":
			frgmentreplace = false;
			Intent r4WWeatherInfoIntent = new Intent(this, R4WWeatherInfo.class);
			startActivity(r4WWeatherInfoIntent);

			// ft = getSupportFragmentManager().beginTransaction();
			// ft.replace(R.id.Frame_Layout, new R4WWeatherInfo()).commit();
			break;

		case "Settings":
			// ft = getSupportFragmentManager().beginTransaction();
			// ft.replace(R.id.Frame_Layout, new R4WSettings()).commit();
			frgmentreplace = false;
			Intent r4WSetting = new Intent(this, R4WSetting.class);
			startActivity(r4WSetting);

			break;

		case "Exit":
			finish();
			break;

		default:
			break;
		}

		if (frgmentreplace != false) {
			try {
				fragmentManager = getSupportFragmentManager();
				List<android.support.v4.app.Fragment> allFragments = fragmentManager
						.getFragments();
				if (allFragments == null || allFragments.isEmpty()) {

				} else {
					for (android.support.v4.app.Fragment fragment1 : allFragments) {
						if (fragment1.isVisible()) {
							fragmentManager.beginTransaction()
									.remove(fragment1).commit();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (fragment != null) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.Frame_Layout, fragment).commit();
				mDrawerlayout.closeDrawer(mDrawerList_Left);

			}

		}

	}

	public void callMessage(String numberget) {
		// TODO Auto-generated method stub
		String fromFull = "<sip:"
				+ numberget
				+ "@"
				+ StaticValues.getServerIPAddress(prefs.getString(
						stored_server_ipaddress, "")) + ">";
		String number = "sip:"
				+ numberget
				+ "@"
				+ StaticValues.getServerIPAddress(prefs.getString(
						stored_server_ipaddress, ""));
		Bundle b = MessageFragment.getArguments(number, fromFull);

		Intent it = new Intent(SipHome.this, MessageActivity.class);
		it.putExtras(b);
		it.putExtra("call", true);
		startActivity(it);

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

	public void RefreshListView() {

		objectArray_left.clear();
		for (int i = 0; i < dataArray_left.size(); i++) {
			Object obj = new Object();
			objectArray_left.add(obj);
			Log.d("objectArray_left", objectArray_left.toString());
		}
		Left_Adapter = new ListItemsAdapter_Left(objectArray_left, 2);
		mDrawerList_Left.setAdapter(Left_Adapter);
		new LayoutParams();
		mDrawerList_Left.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				onClick_leftDrawer_Value = (String) arg0
						.getItemAtPosition(position);
				displayView(position, onClick_leftDrawer_Value);
				checkstatusleftListView = position;
				prefs.edit().putString(stored_user_leftDrawer_status,
						position + "");
				Log.d("checkstatusleftListView", checkstatusleftListView + "");

				if (mDrawerlayout.isDrawerOpen(mDrawerList_Left)) {
					mDrawerlayout.closeDrawer(mDrawerList_Left);
				}
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

	public void payPalWebView(int pinId, int PinNo) {

		Intent intent = new Intent(SipHome.this, R4wPaymentWebView.class);
		intent.putExtra("Pid", pinId);
		intent.putExtra("PinNofrom", PinNo);
		startActivity(intent);
	}

	public void payPalWebViewBuyPin(int pinId) {

		Intent intent = new Intent(SipHome.this, R4wPaymentWebView.class);
		intent.putExtra("Pid", pinId);
		startActivity(intent);
	}

	public void login() {

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			new MyAsyncTask_CheckForRegisteration().execute();
		} else {
			Intent intent = new Intent(this, NoNetwork.class);
			startActivity(intent);
		}

	}

	private class MyAsyncTask_CheckForRegisteration extends
			AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				Log.d("onpostexecute", "yes");
				if (show.equals("true")) {
					if (result_profile.size() < 1) {
						setWizardId_login(WizardUtils.BASIC_WIZARD_TAG);
						saveAccount_login(WizardUtils.BASIC_WIZARD_TAG,
								account_name, user_name, server,
								password_response);
						prefs.edit().putString(stored_registered_account, "1")
								.commit();

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
								prefs.edit()
										.putString(stored_registered_account,
												"1").commit();

							} else {
								System.out
										.println("in checking default values else part");
								setWizardId_login(WizardUtils.BASIC_WIZARD_TAG);
								saveAccount_login(WizardUtils.BASIC_WIZARD_TAG,
										account_name, user_name, server,
										password_response);
								prefs.edit()
										.putString(stored_registered_account,
												"1").commit();

							}
						} else {
							for (int i = 0; i < result_profile.size(); i++) {
								account = result_profile.get(i);
								boolean del_account = delete_account_from_db();
								if (del_account) {

								} else {
									System.out
											.println("error in deleting the account");
								}
							}
							setWizardId_login(WizardUtils.BASIC_WIZARD_TAG);
							saveAccount_login(WizardUtils.BASIC_WIZARD_TAG,
									account_name, user_name, server,
									password_response);
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
			} else {
				Log.d("R4wHome",
						"Error in generating json response. So Sorry! Try Again");
			}
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqGetValue()) {
				Log.d("return", "SUCCESS");
				return true;
			} else {
				Log.d("return", "ERROR");
				return false;
			}
		}
	}

	public boolean webservreqGetValue() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "url"
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
			prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			applyNewAccountDefault(account);
			Uri uri = getContentResolver().insert(SipProfile.ACCOUNT_URI,
					account.getDbContentValues());
			account.id = ContentUris.parseId(uri);
			List<Filter> filters = wizard.getDefaultFilters(account);
			if (filters != null) {
				for (Filter filter : filters) {
					filter.account = (int) account.id;
					getContentResolver().insert(SipManager.FILTER_URI,
							filter.getDbContentValues());
				}
			}
			// Check if we have to restart
			needRestart = wizard.needRestart();

		} else {
			prefs.startEditing();
			wizard.setDefaultParams(prefs);
			prefs.endEditing();
			getContentResolver().update(
					ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE,
							account.id), account.getDbContentValues(), null,
					null);
		}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				prefs.edit().putBoolean(stored_chatSocket_connect, false)
						.commit();
				String num = prefs.getString(stored_user_country_code, "")
						+ prefs.getString(stored_user_mobile_no, "");
				String d = num + "-" + num + "-cl";
				if (ChatSocket.socket != null) {
					ChatSocket.ps.print(d);
				}
				Log.d("ps d onKeyDown", d + " @");
				finish();
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean delete_account_from_db() {
		int rows_del = getContentResolver().delete(
				ContentUris.withAppendedId(SipProfile.ACCOUNT_ID_URI_BASE,
						account.id), null, null);
		// System.out.println("no. of rows deleted " + rows_del);
		if (rows_del > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void updateRegistrationsState() {
		System.out.println("SipHome in updateRegistrationsState()");
		Log.d("SipHome updateRegistrationsState", "Update registration state");
		ArrayList<SipProfileState> activeProfilesState = new ArrayList<SipProfileState>();
		Cursor c = getContentResolver().query(SipProfile.ACCOUNT_STATUS_URI,
				null, null, null, null);

		System.out.println("Sip Home updateRegistrationsState c:" + c);

		if (c != null) {
			System.out.println("$$ updateRegistrationsState C!=null");
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
				Log.e("SipHome updateRegistrationsState",
						"Error on looping over sip profiles", e);
			} finally {
				c.close();
			}
		}

		Collections.sort(activeProfilesState, SipProfileState.getComparator());

		if (activeProfilesState.size() > 0
				&& prefs_wrapper
						.getPreferenceBooleanValue(SipConfigManager.ICON_IN_STATUS_BAR)) {
			try {

				LinearLayout onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
				TextView status_register_account = (TextView) findViewById(R.id.status);
				status_register_account.setText("Online");
				onlinelayout.invalidate();
				onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
				onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
				onlinelayout.invalidate();
				prefs.edit()
						.putString(stored_account_register_status, "Online")
						.commit();

				// new MyAsyncTask_InsertOfflinMessage().execute();

			} catch (Exception e) {
			}
		} else {
			try {
				LinearLayout onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
				onlinelayout.invalidate();
				TextView status_register_account = (TextView) findViewById(R.id.status);
				status_register_account.invalidate();
				prefs.edit()
						.putString(stored_account_register_status, "Connecting")
						.commit();
				status_register_account.setText("Connecting");
				onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
				onlinelayout.setBackgroundColor(Color.parseColor("#FFA500"));
				onlinelayout.invalidate();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		ConnectivityManager connMgr = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
		} else {
			try {

				System.out.println("updateRegistrationsState() : No network");
				System.out.println("");
				LinearLayout onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
				onlinelayout.invalidate();
				TextView status_register_account = (TextView) findViewById(R.id.status);
				status_register_account.invalidate();
				// prefs.edit().putString(stored_account_register_status,
				// "offline").commit();
				status_register_account.setText("No Internet Connection");
				onlinelayout = (LinearLayout) findViewById(R.id.onlineStatus);
				onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				onlinelayout.invalidate();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	class AccountStatusContentObserver extends ContentObserver {
		public AccountStatusContentObserver(Handler h) {
			super(h);
		}

		public void onChange(boolean selfChange) {
			Log.d("SipHome", "Accounts status.onChange( " + selfChange + ")");
			updateRegistrationsState();

		}
	}

	private static final int TOAST_MESSAGE = 0;
	private Handler serviceHandler = new ServiceHandler(this);
	private String balance = null;

	private static class ServiceHandler extends Handler {
		WeakReference<SipHome> s;

		public ServiceHandler(SipHome sipHome) {
			s = new WeakReference<SipHome>(sipHome);
		}

		@Override
		public void handleMessage(Message msg) {
			System.out.println("SipHomejava inhandleMessage()");
			super.handleMessage(msg);
			SipHome sipService = s.get();
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

	private void outCallRechargePopup(View v, String from) {
		PopupWindow slideShowOptionsPopup = null;
		View menu_layout = getLayoutInflater()
				.inflate(R.layout.menu_list, null);
		menu_layout.setVisibility(View.VISIBLE);
		String[] menuItem = getResources().getStringArray(
				R.array.outCallRecharge);
		ItemAdapter adapter = new ItemAdapter(this, R.layout.menu_list_item,
				menuItem);
		((ListView) menu_layout).setAdapter(adapter);
		((ListView) menu_layout)
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long id) {
						// TODO Auto-generated method stub
						Log.d("slideShowOptionsPopup  item position", position
								+ "");

						if (position == 1) {
							Intent paymentIntent = new Intent(SipHome.this,
									PaymentActivity.class);
							paymentIntent.putExtra("paymentValue", "10");
							startActivity(paymentIntent);

						} else if (position == 2) {

							Intent paymentIntent = new Intent(SipHome.this,
									PaymentActivity.class);
							paymentIntent.putExtra("paymentValue", "20");
							startActivity(paymentIntent);

						} else if (position == 3) {
							Intent paymentIntent = new Intent(SipHome.this,
									PaymentActivity.class);
							paymentIntent.putExtra("paymentValue", "30");
							startActivity(paymentIntent);

						} else if (position == 4) {
							Intent paymentIntent = new Intent(SipHome.this,
									PaymentActivity.class);
							paymentIntent.putExtra("paymentValue", "40");
							startActivity(paymentIntent);
						} else if (position == 5) {
							Intent paymentIntent = new Intent(SipHome.this,
									PaymentActivity.class);
							paymentIntent.putExtra("paymentValue", "50");
							startActivity(paymentIntent);
						}

						hideWindow();
					}
				});

		slideShowOptionsPopup = new PopupWindow(menu_layout, 350,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		selectWindow = slideShowOptionsPopup;
		commonPopupWindowDisplay(slideShowOptionsPopup, v, -45, -45);
	}

	public class MyAsyncTaskGetBalance extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			if (balance == null)
				balance = "0";
			prefs.edit().putString(stored_user_bal, balance).commit();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqGetBalance()) {
				Log.d(TAG, "getBalanceService return ture");
				return true;
			} else {
				Log.d(TAG, "getBalanceService return false");
				return false;
			}
		}
	}

	public boolean webservreqGetBalance() {

		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
		

			Log.setLogLevel(6);
			Log.d("uurl", url);
			HttpPost httppost = new HttpPost(url);
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
				balance = responseBody;
				// Parse
				// JSONObject json = new JSONObject(responseBody);
				System.out.println("JSON response:balance" + balance);
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
			}
		} catch (Exception t) {
			t.printStackTrace();
			return false;
		}

	}

	class ItemAdapter extends ArrayAdapter<String> {

		public ItemAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View viewToReturn;
			TextView txtView;

			if (convertView != null) {
				viewToReturn = convertView;
			} else {
				viewToReturn = mInflater.inflate(R.layout.menu_list_item, null);
			}

			txtView = (TextView) viewToReturn.findViewById(R.id.itemName);
			txtView.setText(this.getItem(position));
			txtView.setSelected(true);

			return viewToReturn;
		}

	}

	@SuppressWarnings("deprecation")
	private void commonPopupWindowDisplay(PopupWindow popupWindow,
			View tabMenu, int x, int y) {
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setIgnoreCheekPress();
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(tabMenu, x, y);

	}

	private void hideWindow() {
		if (selectWindow != null) {
			selectWindow.dismiss();
		}
	}

	/*
	 * private class MyAsyncTask_InsertOfflinMessage extends AsyncTask<Void,
	 * Void, Boolean> {
	 * 
	 * @Override protected void onPostExecute(Boolean result) { new
	 * MyAsyncTask_offMessageAcknowledgement().execute();
	 * 
	 * }
	 * 
	 * @Override protected void onPreExecute() { }
	 * 
	 * @Override protected Boolean doInBackground(Void... params) { if
	 * (webserviceOfflineMessage()) { Log.d("return", "SUCCESS"); return true; }
	 * else { Log.d("return", "ERROR"); return false; } } }
	 */
	public boolean webserviceOfflineMessage() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "";

			System.out.println("getlastofflineMessageId pref:"
					+ prefs.getString(lastOfflineMessage, "noMsgId"));

			if (prefs.getString(lastOfflineMessage, "noMsgId")
					.equals("noMsgId")) {


			System.out.println("offline url Message:" + url);
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
				// System.out.println("offMessage JSON Message: "+json);

				JSONArray offlineMessageArray = json.getJSONArray("off_msg");
				ContentResolver cr = this.getContentResolver();

				String msgid = "";

				for (int i = 0; i < offlineMessageArray.length(); i++) {

					JSONObject summarydata = offlineMessageArray
							.getJSONObject(i);
					String fromStr = "<" + summarydata.getString("sender")
							+ ">";
					msgid = summarydata.getString("msg_id");
					String msgdate = summarydata.getString("time");
					long date = Long.parseLong(msgdate) * 1000;
					String mimeStr = summarydata.getString("mime_type");
					String bodyStr = summarydata.getString("msg");
					String contactStr = "";
					String toStr = "<sip:"
							+ prefs.getString(stored_user_country_code,
									"NoValue")
							+ prefs.getString(stored_user_mobile_no, "NoValue")
							+ "@"
							+ StaticValues.getServerIPAddress(prefs.getString(
									stored_server_ipaddress, "")) + ">";
					String canonicFromStr = summarydata.getString("sender");

					SipMessage msg = new SipMessage(canonicFromStr, toStr,
							contactStr, bodyStr, mimeStr, date,
							SipMessage.MESSAGE_TYPE_INBOX, fromStr, 0);
					System.out.println("SipMessafe return:" + msg);
					System.out.println("msg.getContentValues"
							+ msg.getContentValues());

					cr.insert(SipMessage.MESSAGE_URI, msg.getContentValues());
					System.out.println("After Insert in data base");
				}
				System.out.println("lastMessage ID" + msgid);
				prefs.edit().putString(lastOfflineMessage, msgid).commit();

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

			t.printStackTrace();
			return false;
		}
	}

	/*
	 * private class MyAsyncTask_offMessageAcknowledgement extends
	 * AsyncTask<Void, Void, Boolean> {
	 * 
	 * @Override protected void onPostExecute(Boolean result) { }
	 * 
	 * @Override protected void onPreExecute() { }
	 * 
	 * @Override protected Boolean doInBackground(Void... params) { if
	 * (webserviceAcknowledgement()) { Log.d("return", "SUCCESS"); return true;
	 * } else { Log.d("return", "ERROR"); return false; } } }
	 */

	public boolean webserviceAcknowledgement() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);


			

			
			HttpPost httppost = new HttpPost(url);
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				httpclient.execute(httppost, responseHandler);

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

			t.printStackTrace();
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnaddcredit) {
			if (mDrawerlayout.isDrawerOpen(mDrawerList_Left)) {
				mDrawerlayout.closeDrawer(mDrawerList_Left);
			}
			startActivity(new Intent(SipHome.this, R4WAddCredit.class));
		}
	}

}