package com.roamprocess1.roaming4world.ui.dialpad;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.AsyncTaskReceiverAwake;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.ISipService;
import com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi;
import com.roamprocess1.roaming4world.api.SipCallSession;
import com.roamprocess1.roaming4world.api.SipConfigManager;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.contactlist.AllExampleDataSource;
import com.roamprocess1.roaming4world.contactlist.ContactItemInterface;
import com.roamprocess1.roaming4world.contactlist.ExampleContactItem;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.db.DBProvider;
import com.roamprocess1.roaming4world.roaming4world.CustomAdapterSelectCountry;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;
import com.roamprocess1.roaming4world.roaming4world.SQLiteAdapter;
import com.roamprocess1.roaming4world.roaming4world.SpinnerModel;
import com.roamprocess1.roaming4world.ui.R4wFriendsProfile;
import com.roamprocess1.roaming4world.ui.SipHome.ViewPagerVisibilityListener;
import com.roamprocess1.roaming4world.utils.CallHandlerPlugin;
import com.roamprocess1.roaming4world.utils.CallHandlerPlugin.OnLoadListener;
import com.roamprocess1.roaming4world.utils.DialingFeedback;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.PreferencesWrapper;
import com.roamprocess1.roaming4world.utils.Theme;
import com.roamprocess1.roaming4world.utils.contacts.ContactsUtils5;
import com.roamprocess1.roaming4world.widgets.AccountChooserButton;
import com.roamprocess1.roaming4world.widgets.DialerCallBar;
import com.roamprocess1.roaming4world.widgets.DialerCallBar.OnDialActionListener;
import com.roamprocess1.roaming4world.widgets.Dialpad;
import com.roamprocess1.roaming4world.widgets.Dialpad.OnDialKeyListener;

public class DialerFragment extends SherlockFragment implements
		OnClickListener, OnLongClickListener, OnDialKeyListener, TextWatcher,
		OnDialActionListener, ViewPagerVisibilityListener, OnKeyListener {

	private static final String TAG = "DialerFragment";
	protected static final int PICKUP_PHONE = 0;

	private DigitsEditText digits;
	private String initText = null, stored_user_mobile_no,
			stored_supportnumber, supportnum, User_mobile_no,
			stored_user_country_code, stored_min_call_credit,
			stored_user_bal, user_countrycode,
			store_userCountryName, preRegValue, balance,
			storeTopSelectedCountrys, stored_account_register_status;
	public DBContacts sqliteprovider;
	PreferencesWrapper prefs_wrapper;
	SharedPreferences prefs;
	public ImageButton imgRecharge;
	public AccountChooserButton accountChooserButton;
	private Boolean isDigit = null;
	private Animation slideUp, slideDown;
	public Cursor c;
	private DialingFeedback dialFeedback;
	private ImageView r4wUserImg, imgVAddcontact;

	public SipProfile account = null;
	long accountId = -1;
	ArrayList<SipProfile> result_profile, result_profile_login;
	String getnumber = "";
	TextView setCallRate, tv_dialpad_userbalance, txtr4wName;
	public String registrationStatus, searchString;
	private PopupWindow selectWindow = null;
	public View v;
	int no_country_found = 0;
	RelativeLayout relativeContactInfo;
	private LinearLayout LnrImgRightAddCont;

	private final int[] buttonsToLongAttach = new int[] { R.id.button0,
			R.id.button1 };

	public ArrayList<String> topSelectedCountry, topSelectedCountryCode;
	private Button callDialogbox_CountrySelect , downbtn;
	static String numbercall_true = " ", countryCode = "";
	static boolean number_call = false;
	private ISipService service;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			service = ISipService.Stub.asInterface(arg1);
			Roaming4WorldCustomApi.r4wISipService = service;
			Log.d(" DialerFragment service==", service + "");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Roaming4WorldCustomApi.r4wISipService = null;
			service = null;
		}
	};
	private Dialpad dialPad;
	private PreferencesWrapper prefsWrapper;
	private AlertDialog missingVoicemailDialog;
	private DialerCallBar callBar;
	private boolean mDualPane;
	private DialerAutocompleteDetailsFragment autoCompleteFragment;
	private PhoneNumberFormattingTextWatcher digitFormater;
	private DialerLayout dialerLayout;
	List<ContactItemInterface> AllContactList;

	private CustomAdapterSelectCountry countryAdapter;
	private CustomAdapterToCountry TopCountryAdapter;

	public ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	public ArrayList<SpinnerModel> CustomListViewValuesCountryCode = new ArrayList<SpinnerModel>();

	public ArrayList<SpinnerModel> CustomListViewValuesArrTop = new ArrayList<SpinnerModel>();
	public ArrayList<SpinnerModel> CustomListViewValuesCountryCodeTop = new ArrayList<SpinnerModel>();

	private SQLiteAdapter mySQLiteAdapter;
	private Cursor getAllCountryCode;
	ArrayList arrayListCountryCode, arrayList;
	TextView tv_setCountryCode, txtVUserName;
	String countryCodeget;
	public ImageButton imgVedioCall;
	private EditText db_et_searchcountry;
	private boolean isVedioCall = false, isOutCall = true;
	private boolean videoCall = false;
	private ContactsUtils5 contactsUtils5;
	private View actionBarView;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		onVisibilityChanged_custom(true);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		digitFormater = new PhoneNumberFormattingTextWatcher();
		slideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
		slideDown = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_down);
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		
		stored_supportnumber = "com.roamprocess1.roaming4world.support_no";
		stored_account_register_status = "com.roamprocess1.roaming4world.account_register_status";
		store_userCountryName = "com.roamprocess1.roaming4world.user_countryname";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		User_mobile_no = prefs.getString(stored_user_mobile_no, "no");
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		user_countrycode = prefs.getString(stored_user_country_code, "");
		stored_min_call_credit = "com.roamprocess1.roaming4world.min_call_credit";
		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";

		storeTopSelectedCountrys = "com.roamprocess1.roaming4world.topSelectedCountrys";
	
		String StoreCountyNameCode = prefs.getString(storeTopSelectedCountrys, "No County");
		
		if(StoreCountyNameCode.equals("No County")){
			String [] countryName= getResources().getStringArray(R.array.CountryCodesWithname);
			for(int i=0;i<countryName.length;i++)
			{
				String[] countryCodeName=countryName[i].split(",");
				if(countryCodeName[1].equals(user_countrycode)){
				StoreCountyNameCode=StoreCountyNameCode.replace("No County", "");
				System.out.println("default country name:"+countryCodeName[0].toLowerCase());	
				StoreCountyNameCode=StoreCountyNameCode+user_countrycode+":"+countryCodeName[0].toLowerCase().replace(" ", "")+",";
				prefs.edit().putString(storeTopSelectedCountrys,StoreCountyNameCode).commit();
				break;
				}
			}
			
		}
		
		
		
		topSelectedCountry = new ArrayList<>();
		topSelectedCountryCode = new ArrayList<>();
		countryCodeget = prefs.getString(stored_user_country_code, "");
		AllContactList = AllExampleDataSource.getSampleContactList(getActivity());

		System.out.println("User_mobile_no ==" + User_mobile_no);

		if (isDigit == null) {
			isDigit = !prefsWrapper.getPreferenceBooleanValue(SipConfigManager.START_WITH_TEXT_DIALER);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			v = inflater.inflate(R.layout.dialer_digit, container, false);

			LinearLayout onlinelayout = (LinearLayout) v.findViewById(R.id.onlineStatus);
			LinearLayout header_onlinelayout = (LinearLayout) v.findViewById(R.id.ll_dialer_onlineStatus);
			
			TextView status_register_account = (TextView) v.findViewById(R.id.status);
			preRegValue = prefs.getString(stored_account_register_status,"Connecting");
			System.out.println("oncreate prefValue" + preRegValue);
			System.out.println("oncreate registerStatus: "+ prefs.getString(stored_account_register_status,"Connecting"));
			relativeContactInfo = (RelativeLayout) v.findViewById(R.id.relativeContactInfo);

			txtr4wName = (TextView) v.findViewById(R.id.txtr4wName);
			r4wUserImg = (ImageView) v.findViewById(R.id.r4wUserImg);
			setHasOptionsMenu(true);

			ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {

				if (status_register_account != null) {
					if (preRegValue.equals("Online")) {
						status_register_account.invalidate();
						onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
						status_register_account.setText("Online");
						status_register_account.invalidate();
						onlinelayout.setVisibility(LinearLayout.GONE);
						header_onlinelayout.setBackgroundColor(Color.parseColor("#8CC63F"));
					} else if (preRegValue.equals("Connecting")) {
						onlinelayout.setBackgroundColor(Color
								.parseColor("#FFA500"));
						status_register_account.setText("Connecting");
						status_register_account.invalidate();
						onlinelayout.invalidate();
						header_onlinelayout.setBackgroundColor(Color.parseColor("#FFA500"));
						onlinelayout.setVisibility(LinearLayout.GONE);
				}
				}
			} else {
				System.out.println("updateRegistrationsState() : No network");
				status_register_account.invalidate();
				onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				status_register_account.setText("No Internet Connection");
				status_register_account.invalidate();
				onlinelayout.invalidate();
				onlinelayout.setVisibility(LinearLayout.GONE);
				header_onlinelayout.setBackgroundColor(Color.parseColor("#EB3D35"));
				
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}

		try {
			digits = (DigitsEditText) v.findViewById(R.id.digitsText);
			dialPad = (Dialpad) v.findViewById(R.id.dialPad);
			callBar = (DialerCallBar) v.findViewById(R.id.dialerCallBar);

			actionBarView = getActivity().getActionBar().getCustomView();
			imgVAddcontact = (ImageView) actionBarView.findViewById(R.id.imgVAddcontact);
			
			//LnrImgRightAddCont=(LinearLayout) actionBarView.findViewById(R.id.LnrImgRightAddCont);
			
			imgRecharge = (ImageButton) actionBarView.findViewById(R.id.imgOutCredit);
			
			imgRecharge.setVisibility(View.VISIBLE);
			imgVAddcontact.setVisibility(View.GONE);

			System.out.println("user_countrycode in dialerfragment:"+ user_countrycode);

			LinearLayout linearLayoutCountryCode = (LinearLayout) v.findViewById(R.id.linearLayoutCountryCode);
			LinearLayout linerlayoutCounty = (LinearLayout) v.findViewById(R.id.linerlayoutCounty);

			callDialogbox_CountrySelect = (Button) v.findViewById(R.id.btn_setcallcountrycode);
			callDialogbox_CountrySelect.setBackgroundResource(getResources().getIdentifier(
									"com.roamprocess1.roaming4world:drawable/"
											+ prefs.getString(
													store_userCountryName, "")
											+ "copy", null, null));
			
			callDialogbox_CountrySelect.setOnClickListener(this);
			tv_setCountryCode = (TextView) v.findViewById(R.id.tv_setcallcountrycode);
			downbtn = (Button) v.findViewById(R.id.downbtn);
			downbtn.setOnClickListener(this);
			
			tv_setCountryCode.setText("+"+ prefs.getString(stored_user_country_code, ""));
			setCallRate = (TextView) v.findViewById(R.id.tv_setCallRate);
			txtVUserName = (TextView) v.findViewById(R.id.txtVUserName);
			tv_dialpad_userbalance = (TextView) v.findViewById(R.id.tv_dialpad_Userbalance);

			accountChooserButton = (AccountChooserButton) v.findViewById(R.id.accountChooserButton);
			dialerLayout = (DialerLayout) v.findViewById(R.id.top_digit_dialer);
			
			imgVAddcontact.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub			
					saveNumber();
				}
			});

		
			linerlayoutCounty.setOnClickListener(new OnClickListener(){
		 		 @Override public void onClick(View v) {
			 // TODO Auto-generated
		 			 dialogboxselectContryCode(); 
			 } });
			

			tv_setCountryCode.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogboxselectContryCode();
					
				}
			});

			new MyAsyncTaskGetBalance().execute();
		} catch (Exception e) {
		}

		if (savedInstanceState != null) {
			isDigit = savedInstanceState.getBoolean(TEXT_MODE_KEY, isDigit);
		}
		account = SipProfile.getProfileFromDbId(getActivity(), accountId,DBProvider.ACCOUNT_FULL_PROJECTION);
		result_profile = SipProfile.getAllProfiles(getActivity(), false);
		prefs_wrapper = new PreferencesWrapper(getActivity());

		digits.setOnEditorActionListener(keyboardActionListener);
		dialerLayout.setForceNoList(mDualPane);
		accountChooserButton.setShowExternals(true);
		dialPad.setOnDialKeyListener(this);
		callBar.setOnDialActionListener(this);
		callBar.setVideoEnabled(prefsWrapper.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO));
		initButtons(v);

		setTextDialing(!isDigit, true);
		if (initText != null) {
			digits.setText(initText);
			initText = null;
		}
		applyTheme(v);
		imgVedioCall = (ImageButton) v.findViewById(R.id.dialVideoButton);
		imgVedioCall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isVedioCall = true;

				System.out.println("Dialer Fragment :video calling");
				if (videoCall == false) {
					Toast.makeText(getActivity(),
							"A video call can only be placed to a R4W contact",
							Toast.LENGTH_SHORT).show();
				} else {
					if (isAppInstalled("com.roamprocess1.roaming4world.plugins.video")) {
						// TODO Auto-generated method stub
						System.out.println("Dialer Fragment :video calling");

						String num = removeZero(digits.getText().toString());
						String countryCodeGet = tv_setCountryCode.getText()
								.toString().substring(1);
						if (num.startsWith(countryCodeGet))
							dialogBoxWrongNumber(tv_setCountryCode.getText()
									.toString() + "-" + num, 0);
						else
							placeVideoCall();
						videoCall = false;
						/*
						 * if(num.startsWith("00")){ i++; }
						 * if(num.startsWith("0") || num.startsWith("+")){ i++;
						 * } if(num.startsWith(countryCode)){ i++; } if(i == 0){
						 * 
						 * placeVideoCall();
						 * 
						 * 
						 * }else{ dialogBoxWrongNumber("+" + countryCode + "-" +
						 * num, 1); }
						 */
					} else {
						dialogBoxVideoCall();
					}
				}
			}
		});

		v.setOnKeyListener(this);
		setTextDialing(Roaming4WorldCustomApi.textordigit);
		Log.setLogLevel(6);

		sqliteprovider = new DBContacts(getActivity());
		sqliteprovider.openToRead();
		digits.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {

				txtVUserName.invalidate();

				if (s.length() > 0) {
					txtVUserName.setText("Save");
					imgRecharge.setVisibility(View.GONE);
					imgVAddcontact.setVisibility(View.VISIBLE);
					relativeContactInfo.setVisibility(View.VISIBLE);
					txtr4wName.setText("Search Contact ...");
					//r4wUserImg.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/ic_contact_picture_holo_dark", null, null));
					//imageView.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/"+ CodeName[1] + "copy", null, null));
					
				} else {
					imgRecharge.setVisibility(View.VISIBLE);
					imgVAddcontact.setVisibility(View.GONE);
					txtr4wName.setText("Search Contact ...");
					//r4wUserImg.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/ic_contact_picture_holo_dark", null, null));
					
				}
				String number = tv_setCountryCode.getText()
						+ removeZero(s.toString());
				number = number.replaceAll("\\+", "");
				number = number.replaceAll("[^\\d]", "");
				System.out.println("final Number:" + number);
				
				if (number.length() > 0) {
					contactinfoView(removeZero(s.toString()), tv_setCountryCode.getText().toString(), s.toString());

				}
				if (s.length() == 0) {
					imgRecharge.setVisibility(View.VISIBLE);
					imgVAddcontact.setVisibility(View.GONE);
				}
				Log.d("onTextChanged", s.length() + " !");

				int length_one = numbercall_true.length();
				int length_two = s.length();

				String numb = "", onclickvalue;
				onclickvalue = countryCodeget + s.toString();

				if (length_two == 0) {
					setCallRate.setText("");
				} else if (number_call == true
						&& numbercall_true.equals(onclickvalue.substring(0,
								length_two - 1))) {

				} else if (number_call == true
						&& !numbercall_true.equals(onclickvalue.substring(0,
								length_two - 1))) {
					setCallRate.setText("");
					numb = onclickvalue;
					numb = numb.replace(" ", "").replace("-", "");

					if (getnumber.equals("")) {
						getnumber = numb;
						if (numb.length() > 3) {
							// new AsynctaskGetCallRate(numb).execute();
						}

					} else if (!getnumber.equals(numb)) {
						getnumber = numb;
						if (numb.length() > 3) {
							// new AsynctaskGetCallRate(numb).execute();

						}
					}

				} else if (number_call == false) {
					setCallRate.setText("");
					numb = onclickvalue;
					numb = numb.replace(" ", "").replace("-", "");
					if (getnumber.equals("")) {
						getnumber = numb;
						if (numb.length() > 3) {
							// new AsynctaskGetCallRate(numb).execute();
						}

					} else if (!getnumber.equals(numb)) {
						getnumber = numb;
						if (numb.length() > 3) {
							// new AsynctaskGetCallRate(numb).execute();

						}
					}

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		return v;
	}

	private void dialogBoxWrongNumber(String number, final int calltype) {
		new AlertDialog.Builder(getActivity())
				.setTitle("Alert!")
				.setMessage(
						"The number you have entered seems to be incorrect.\n"
								+ number + "\nPlease check again.")
				.setPositiveButton("Proceed Anyway",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								if (calltype == 0) {
									placeCallWithOption(null);
								} else {
									placeVideoCall();
								}
							}
						})
				.setNegativeButton("Edit",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).show();

	}

	public void saveNumber() {

		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		String country_code = tv_setCountryCode.getText().toString();
		String num = removeZero(digits.getText().toString());
		String num_code = (String) ((country_code != null) ? country_code + num : num);	
		intent.putExtra(ContactsContract.Intents.Insert.PHONE,num_code);
		startActivity(intent);
	
		
		
		/*
		String number = tv_setCountryCode.getText()
				+ digits.getText().toString();
		number = number.replaceAll("\\+", "");
		number = number.replaceAll("[^\\d]", "");
		System.out.println("Number:" + number);

		if (number.length() >= 7) {
			Cursor r4wCursor = sqliteprovider.fetch_contact_from_R4W(number);
			if (r4wCursor != null && r4wCursor.getCount() > 0) {
				r4wCursor.moveToFirst();
				String Name = r4wCursor.getString(r4wCursor
						.getColumnIndex(DBContacts.R4W_CONTACT_NAME));
				txtVUserName.setText("R4W " + Name);
				System.out
						.println("calling name :" + number + ":" + ":" + Name);
				videoCall = true;
				imgVedioCall.setClickable(true);
			} else {
				videoCall = false;
				imgVedioCall.setClickable(true);
				txtVUserName.setText("Save");
				imgVAddcontact.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(Intent.ACTION_INSERT);
						intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
						String country_code = tv_setCountryCode.getText().toString();
						String num_code = (String) ((country_code != null) ? country_code + digits.getText().toString() : digits.getText().toString());						
						intent.putExtra(ContactsContract.Intents.Insert.PHONE,num_code);
						startActivity(intent);
					}
				});
			}
			r4wCursor.close();
		}*/
	}

	private boolean isAppInstalled(String packageName) {
		PackageManager pm = getActivity().getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	public void dialogboxselectContryCode() {
		final Dialog dialog = new Dialog(getActivity() , android.R.style.Theme);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.r4wselectcountrycode);
		dialog.show();
		ListView selectCountryCode = (ListView) dialog.findViewById(R.id.lv_selectcontrycode);
		ListView TopSelectCountryCode = (ListView) dialog.findViewById(R.id.top_lv_selectcontrycode);

		db_et_searchcountry = (EditText) dialog.findViewById(R.id.db_et_searchcountry);
		arrayList = new ArrayList();
		arrayListCountryCode = new ArrayList();
		final TextView no_Country_Found = (TextView) dialog.findViewById(R.id.db_tv_no_country_found);

		mySQLiteAdapter = new SQLiteAdapter(getActivity());
		setListDataRegchooseLogin();

		countryAdapter = new CustomAdapterSelectCountry(getActivity(),R.layout.spinner_row_country_reg, CustomListViewValuesArr,CustomListViewValuesCountryCode, getResources(), getActivity());
		selectCountryCode.setAdapter(countryAdapter);
		mySQLiteAdapter.close();
		String StoreCountyNameCode = prefs.getString(storeTopSelectedCountrys,"No County");
		String[] countrySplit = StoreCountyNameCode.split(",");
		System.out.println("Total String is :" + countrySplit.length);

		if (countrySplit.length < 1) {
			TopSelectCountryCode.setVisibility(View.GONE);

		} else {
			System.out.println("Total String is :else");
			TopSelectCountryCode.setVisibility(View.VISIBLE);
			CustomAdapterToCountry adapter = new CustomAdapterToCountry(getActivity(), countrySplit);
			TopSelectCountryCode.setAdapter(adapter);
			TopSelectCountryCode.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,int arg2, long arg3) {
							// TODO Auto-generated method stub

							TextView txtCountryName = (TextView) view.findViewById(R.id.txtVcountryName);
							TextView txtCountyCode = (TextView) view.findViewById(R.id.txtVcountryCode);
							String CountyCode = txtCountyCode.getText().toString().substring(1,txtCountyCode.getText().length() - 1);
							System.out.println("");

							tv_setCountryCode.setText(CountyCode);

							contactinfoView(removeZero(digits.getText().toString()), CountyCode, removeZero(digits.getText().toString()));

							callDialogbox_CountrySelect
									.setBackgroundResource(getResources()
											.getIdentifier(
													"com.roamprocess1.roaming4world:drawable/"
															+ txtCountryName
																	.getText()
																	.toString()
																	.toLowerCase()
															+ "copy", null,
													null));

							dialog.dismiss();
							
						}
					});

		}

		selectCountryCode.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				countryCodeget = arrayListCountryCode.get(position).toString();
				String countryNameget = arrayList.get(position).toString();
				tv_setCountryCode.setText("+" + countryCodeget);

				contactinfoView(removeZero(digits.getText().toString()),countryCodeget, removeZero(digits.getText().toString()));

				String countryNameCode = countryCodeget + ":" + countryNameget+ ",";
				String StoreCountyNameCode = prefs.getString(storeTopSelectedCountrys, "No County");
				
				if (!StoreCountyNameCode.contains(countryNameCode)) {
					String[] countrySplit = StoreCountyNameCode.split(",");
					System.out.println("Total String is :"+ countrySplit.length);
					if (countrySplit.length >= 3) {
						StoreCountyNameCode = StoreCountyNameCode.replace(countrySplit[0] + ",", "");
					}
					StoreCountyNameCode = StoreCountyNameCode + countryNameCode;
					
					prefs.edit().putString(storeTopSelectedCountrys,StoreCountyNameCode).commit();

				}

				callDialogbox_CountrySelect.setBackgroundResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/"
										+ countryNameget + "copy", null, null));
				dialog.dismiss();

				

			}
		});

		db_et_searchcountry.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				no_Country_Found.setVisibility(View.GONE);

				if (s.length() == 0 || s.toString().equals(" ")) {
					setListDataRegchooseLogin();
					countryAdapter.notifyDataSetChanged();

				} else {
					setListDataRegchooseLoginSearch(s.toString());
					countryAdapter.notifyDataSetChanged();
					if (no_country_found == 0) {
						no_Country_Found.setVisibility(View.VISIBLE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void setListDataRegchooseLogin() {
		no_country_found = 0;
		arrayList.clear();
		arrayListCountryCode.clear();
		CustomListViewValuesArr.clear();
		mySQLiteAdapter.openToRead();
		getAllCountryCode = mySQLiteAdapter.fetch_country_codes_dest();
		getAllCountryCode.moveToFirst();
		while (!getAllCountryCode.isAfterLast()) {
			String spnRowData = getAllCountryCode.getString(getAllCountryCode
					.getColumnIndex(SQLiteAdapter.CD_COUNTRY_NAME));
			String spnRowDataCountryCode = getAllCountryCode
					.getString(getAllCountryCode
							.getColumnIndex(SQLiteAdapter.CD_COUNTRY_CODE));
			arrayListCountryCode.add(spnRowDataCountryCode);
			getAllCountryCode.moveToNext();
			final SpinnerModel spnModel = new SpinnerModel();
			spnModel.setCountyName(spnRowData);
			spnModel.setImage(spnRowData.toLowerCase().replaceAll("\\s+", ""));
			arrayList.add(spnRowData.toLowerCase().replaceAll("\\s+", ""));
			spnModel.setUrl(spnRowData);
			spnModel.setCountyCode(spnRowDataCountryCode);
			CustomListViewValuesArr.add(spnModel);
		}
		getAllCountryCode.close();
		mySQLiteAdapter.close();
	}

	public void setListDataRegchooseLoginSearch(String pretext) {
		no_country_found = 0;
		arrayList.clear();
		arrayListCountryCode.clear();
		CustomListViewValuesArr.clear();
		mySQLiteAdapter.openToRead();
		getAllCountryCode = mySQLiteAdapter.fetch_country_codes_dest();

		getAllCountryCode.moveToFirst();
		while (!getAllCountryCode.isAfterLast()) {
			String spnRowData = getAllCountryCode.getString(getAllCountryCode
					.getColumnIndex(SQLiteAdapter.CD_COUNTRY_NAME));
			String spnRowDataCountryCode = getAllCountryCode
					.getString(getAllCountryCode
							.getColumnIndex(SQLiteAdapter.CD_COUNTRY_CODE));
			getAllCountryCode.moveToNext();
			final SpinnerModel spnModel = new SpinnerModel();
			boolean v = spnRowData.toLowerCase()
					.contains(pretext.toLowerCase());
			Log.setLogLevel(6);
			Log.d("searchvalue", v + " !");
			if (v == true) {
				arrayListCountryCode.add(spnRowDataCountryCode);
				spnModel.setCountyName(spnRowData);
				spnModel.setImage(spnRowData.toLowerCase().replaceAll("\\s+",
						""));
				arrayList.add(spnRowData.toLowerCase().replaceAll("\\s+", ""));
				spnModel.setUrl(spnRowData);
				spnModel.setCountyCode(spnRowDataCountryCode);
				CustomListViewValuesArr.add(spnModel);
				no_country_found = 1;
			}

		}
		getAllCountryCode.close();
		mySQLiteAdapter.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (callBar != null) {
			callBar.setVideoEnabled(prefsWrapper
					.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO));
		}
	}

	private void applyTheme(View v) {
		Theme t = Theme.getCurrentTheme(getActivity());
		if (t != null) {
			dialPad.applyTheme(t);

			View subV;
			// Delete button
			subV = v.findViewById(R.id.deleteButton);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_dial_delete");
				t.applyLayoutMargin(subV, "btn_dial_delete_margin");
				t.applyImageDrawable((ImageView) subV, "ic_dial_action_delete");
			}

			// Dial button
			subV = v.findViewById(R.id.dialButton);
			if (subV != null) {
				System.out.println("Dialbutton calling");
				t.applyBackgroundDrawable(subV, "btn_dial_action");
				t.applyLayoutMargin(subV, "btn_dial_action_margin");
				t.applyImageDrawable((ImageView) subV, "ic_dial_action_call");
			}

			/*
			 * Additional button subV = v.findViewById(R.id.dialVideoButton);
			 * 
			 * if(subV != null) { t.applyBackgroundDrawable(subV,
			 * "btn_add_action"); t.applyLayoutMargin(subV,
			 * "btn_dial_add_margin"); }
			 */

			// Action dividers
			subV = v.findViewById(R.id.divider1);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_bar_divider");
				t.applyLayoutSize(subV, "btn_dial_divider");
			}
			subV = v.findViewById(R.id.divider2);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_bar_divider");
				t.applyLayoutSize(subV, "btn_dial_divider");
			}

			// Dialpad background
			subV = v.findViewById(R.id.dialPad);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "dialpad_background");
			}

			// Callbar background
			subV = v.findViewById(R.id.dialerCallBar);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "dialer_callbar_background");
			}

			// Top field background
			subV = v.findViewById(R.id.topField);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "dialer_textfield_background");
			}

			subV = v.findViewById(R.id.digitsText);
			if (subV != null) {
				t.applyTextColor((TextView) subV, "textColorPrimary");
			}

		}

		// Fix dialer background
		if (callBar != null) {
			Theme.fixRepeatableBackground(callBar);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
		// Optional, but here we bundle so just ensure we are using csipsimple
		// package
		serviceIntent.setPackage(activity.getPackageName());
		getActivity().bindService(serviceIntent, connection,Context.BIND_AUTO_CREATE);
		// timings.addSplit("Bind asked for two");
		if (prefsWrapper == null) {
			prefsWrapper = new PreferencesWrapper(getActivity());
		}
		if (dialFeedback == null) {
			dialFeedback = new DialingFeedback(getActivity(), false);
		}

		dialFeedback.resume();

	}

	@Override
	public void onDetach() {
		try {
			getActivity().unbindService(connection);
		} catch (Exception e) {
			// Just ignore that
			Log.w(TAG, "Unable to un bind", e);
		}
		dialFeedback.pause();
		super.onDetach();
	}

	private final static String TEXT_MODE_KEY = "text_mode";

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(TEXT_MODE_KEY, isDigit);
		super.onSaveInstanceState(outState);
	}

	private OnEditorActionListener keyboardActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView tv, int action, KeyEvent arg2) {
			System.out.println("$$$$$ keyboardActionListener :");
			System.out.println("textview:action:keyEvent: ->" + tv + "_"
					+ action + "-" + arg2);

			if (action == EditorInfo.IME_ACTION_GO) {
				System.out.println("calling #######");

				// placeCall();
				return true;
			}
			return false;
		}
	};

	private void initButtons(View v) {
		for (int buttonId : buttonsToLongAttach) {
			// attachButtonListener(v, buttonId, true);
		}
		digits.setOnClickListener(this);
		digits.setKeyListener(DialerKeyListener.getInstance());
		digits.addTextChangedListener(this);
		digits.setCursorVisible(false);
		afterTextChanged(digits.getText());
	}

	private void keyPressed(int keyCode) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		digits.onKeyDown(keyCode, event);
	}

	public void onClick(View view) {
		// ImageButton b = null;
		int viewId = view.getId();
		if (viewId == digits.getId()) {
			if (digits.length() != 0) {
				digits.setCursorVisible(true);
			}
		} else if (viewId == R.id.btn_setcallcountrycode) {
			dialogboxselectContryCode();
		}else if (viewId == R.id.downbtn) {
			dialogboxselectContryCode();
		}
	}

	public boolean onLongClick(View view) {
		// ImageButton b = (ImageButton)view;
		int vId = view.getId();
		if (vId == R.id.button0) {
			dialFeedback.hapticFeedback();
			keyPressed(KeyEvent.KEYCODE_PLUS);
			return true;
		} else if (vId == R.id.button1) {
			if (digits.length() == 0) {
				System.out.println("onLongClick:digits.length() == 0");

				// placeVMCall();
				return true;
			}
		}
		return false;
	}

	public void afterTextChanged(Editable input) {

		final boolean notEmpty = digits.length() != 0;
		callBar.setEnabled(notEmpty);

		if (!notEmpty && isDigit) {
			digits.setCursorVisible(false);
		}
	}

	public void setTextDialing(boolean textMode) {
		Log.d(TAG, "Switch to mode " + textMode);
		setTextDialing(textMode, false);
	}

	public void setTextDialing(boolean textMode, boolean forceRefresh) {
		if (!forceRefresh && (isDigit != null && isDigit == !textMode)) {
			// Nothing to do
			return;
		}
		isDigit = !textMode;
		if (digits == null) {
			return;
		}
		if (isDigit) {

			digits.getText().clear();
			digits.addTextChangedListener(digitFormater);
		} else {
			digits.removeTextChangedListener(digitFormater);
		}
		digits.setCursorVisible(!isDigit);
		digits.setIsDigit(isDigit, true);

		// Update views visibility
		dialPad.setVisibility(isDigit ? View.VISIBLE : View.GONE);
		getSherlockActivity().supportInvalidateOptionsMenu();
	}

	/**
	 * Set the value of the text field and put caret at the end
	 * 
	 * @param value
	 *            the new text to see in the text field
	 */
	public void setTextFieldValue(CharSequence value) {
		if (digits == null) {
			initText = value.toString();
			return;
		}
		if (value.toString().startsWith("+")) {
			value = value.toString().replace("+", "");
		} else if (value.toString().startsWith("00")) {
			value = value.toString().replaceFirst("00", "");
		}
		digits.setText(value);
		Editable spannable = digits.getText();
		Selection.setSelection(spannable, spannable.length());
	}

	// @Override
	public void onTrigger(int keyCode, int dialTone) {
		dialFeedback.giveFeedback(dialTone);
		keyPressed(keyCode);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// Nothing to do here

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		afterTextChanged(digits.getText());
		accountChooserButton.setChangeable(TextUtils.isEmpty(digits.getText()
				.toString()));
	}

	@Override
	public void placeCall() {
		// TODO Auto-generated method stub
		System.out.println("placeCall() is calling");
		String num = removeZero(digits.getText().toString());
		String countryCode = tv_setCountryCode.getText().toString().substring(1);

		if (num.startsWith(countryCode))
			dialogBoxWrongNumber(tv_setCountryCode.getText().toString() + "-"
					+ num, 0);
		else
			placeCallWithOption(null);

	}

	@Override
	public void placeVideoCall() {

		System.out.println("DialerFragment :: placeVideoCall()");
		videoCall = false;
		Bundle b = new Bundle();
		b.putBoolean(SipCallSession.OPT_CALL_VIDEO, true);
		placeCallWithOption(b);
	}

	private void dialogBoxNoInternet() {
		new AlertDialog.Builder(getActivity()).setTitle("No Network")
				.setMessage("There is no internet connection")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();

	}

	private void dialogBoxVideoCall() {
		new AlertDialog.Builder(getActivity())
				.setTitle("Video Support")
				.setMessage(
						"Video feature is not installed. Click proceed to download.")
				.setPositiveButton("Proceed",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent browserIntent = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("https://play.google.com/store/apps/details?id=com.roamprocess1.roaming4world.plugins.video"));
								startActivity(browserIntent);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).show();

	}

	private void placeCallWithOption(Bundle b) {

	 	Toast.makeText(getActivity(), "Call in progress", Toast.LENGTH_LONG).show();
   		
		
		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.noDataConnectiontitle);
			builder.setMessage(R.string.noDataConnectionMessage)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// FIRE ZE MISSILES!

								}
							});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			return;
		}

		if (service == null) {
			return;
		}

		final Bundle bundleforcall = b;

		if (isVedioCall) {

			System.out.println("DialerFragment :: placeCallWithOption");
			String toCall = "";
			Long accountToUse = SipProfile.INVALID_ID;
			SipProfile acc = accountChooserButton.getSelectedAccount();
			if (acc != null) {
				accountToUse = acc.id;
				Log.d("placeCallWithOption  accountToUse===", accountToUse + "");
			}

			if (isDigit) {
				toCall = PhoneNumberUtils.stripSeparators(tv_setCountryCode
						.getText() + removeZero(digits.getText().toString()));
			} else {
				toCall = tv_setCountryCode.getText()
						+ removeZero(digits.getText().toString());
			}
			if (TextUtils.isEmpty(toCall)) {
				return;
			}

			toCall = toCall.replaceAll("\\+", "");
			System.out.println("video Calling number :" + toCall);
			digits.getText().clear();
			if (accountToUse >= 0) {
				try {
			//		new AsyncTaskReceiverAwake(toCall).execute();
					service.makeCallWithOptions(toCall,
							accountToUse.intValue(), bundleforcall);
				} catch (RemoteException e) {
				}
			} else if (accountToUse != SipProfile.INVALID_ID) {
				CallHandlerPlugin ch = new CallHandlerPlugin(getActivity());
				ch.loadFrom(accountToUse, toCall, new OnLoadListener() {
					@Override
					public void onLoad(CallHandlerPlugin ch) {
						placePluginCall(ch);
					}
				});
			}
			isVedioCall = false;
		} else if (isOutCall) {
			System.out.println("OutCallButton :balance ++" + balance);
			balance = prefs.getString(stored_user_bal, "");
			if (balance != null && !balance.equals("")) {
				double bal = Double.parseDouble(balance);
				bal = bal * 100;
				double min_bal = Double.parseDouble(prefs.getString(
						stored_min_call_credit, "10"));
				Log.setLogLevel(6);
				Log.d("balance", bal + " @");
				Log.d("Minbalance", min_bal + " @");

				if (bal > min_bal) {
					String toCall = "";
					Long accountToUse = SipProfile.INVALID_ID;
					SipProfile acc = accountChooserButton.getSelectedAccount();
					if (acc != null) {
						accountToUse = acc.id;
						Log.d("placeCallWithOption  accountToUse===",
								accountToUse + "");
					}

					if (isDigit) {
						toCall = PhoneNumberUtils.stripSeparators(
								+ tv_setCountryCode.getText()
								+ digits.getText().toString());
					} else {
						toCall =  + tv_setCountryCode.getText()
								+ digits.getText().toString();
					}
					if (TextUtils.isEmpty(toCall)) {
						return;
					}

					toCall = toCall.replaceAll("\\+", "");
					System.out.println("Calling number is" + toCall);
					digits.getText().clear();
					if (accountToUse >= 0) {
						try {
							service.makeCallWithOptions(toCall,
									accountToUse.intValue(), bundleforcall);
						} catch (RemoteException e) {
							Log.e(TAG,
									"Service can't be called to make the call");
						}
					} else if (accountToUse != SipProfile.INVALID_ID) {

						CallHandlerPlugin ch = new CallHandlerPlugin(
								getActivity());
						ch.loadFrom(accountToUse, toCall, new OnLoadListener() {
							@Override
							public void onLoad(CallHandlerPlugin ch) {
								placePluginCall(ch);
							}
						});
					}

				} else {
					digits.setText("");
					dialogboxR4WOut();
				}
			} else {
				Toast.makeText(getActivity(), "Balance not updated",
						Toast.LENGTH_SHORT).show();
			}

		} else if (!isOutCall) {
			final Dialog dialogusercheckcalltype = new Dialog(getActivity());
			dialogusercheckcalltype
					.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogusercheckcalltype
					.setContentView(R.layout.dialogbox_checkcalltype);
			dialogusercheckcalltype.show();

			LinearLayout freecall, r4wout, dismiss;

			freecall = (LinearLayout) dialogusercheckcalltype
					.findViewById(R.id.ll_db_freecall);
			r4wout = (LinearLayout) dialogusercheckcalltype
					.findViewById(R.id.ll_db_r4wout);
			dismiss = (LinearLayout) dialogusercheckcalltype
					.findViewById(R.id.ll_db_dismiss);

			dismiss.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogusercheckcalltype.dismiss();
				}
			});

			freecall.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogusercheckcalltype.dismiss();
					String toCall = "";
					Long accountToUse = SipProfile.INVALID_ID;
					SipProfile acc = accountChooserButton.getSelectedAccount();
					if (acc != null) {
						accountToUse = acc.id;
						Log.d("placeCallWithOption  accountToUse===",
								accountToUse + "");
					}

					if (isDigit) {
						
							
						toCall = PhoneNumberUtils.stripSeparators(tv_setCountryCode.getText()+ removeZero(digits.getText().toString()));
					} else {
						toCall = tv_setCountryCode.getText()+ removeZero(digits.getText().toString());
					}
					if (TextUtils.isEmpty(toCall)) {
						return;
					}

					toCall = toCall.replaceAll("\\+", "");
					System.out.println("Calling number is" + toCall);
					digits.getText().clear();
					if (accountToUse >= 0) {
						try {
					//		new AsyncTaskReceiverAwake(toCall).execute();
						
							service.makeCallWithOptions(toCall,
									accountToUse.intValue(), bundleforcall);
						} catch (RemoteException e) {
							Log.e(TAG,
									"Service can't be called to make the call");
						}
					} else if (accountToUse != SipProfile.INVALID_ID) {

						CallHandlerPlugin ch = new CallHandlerPlugin(
								getActivity());
						ch.loadFrom(accountToUse, toCall, new OnLoadListener() {
							@Override
							public void onLoad(CallHandlerPlugin ch) {
								placePluginCall(ch);
							}
						});
					}
					isOutCall = true;
				}
			});

			r4wout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					dialogusercheckcalltype.dismiss();

					System.out.println("OutCallButton :balance ++" + balance);
					if (balance != null) {
						double bal = Double.parseDouble(balance);
						bal = bal * 100;
						double min_bal = Double.parseDouble(prefs.getString(
								stored_min_call_credit, "10"));
						Log.setLogLevel(6);
						Log.d("balance", bal + " @");
						Log.d("Minbalance", min_bal + " @");

						if (bal > min_bal) {
							String toCall = "";
							Long accountToUse = SipProfile.INVALID_ID;
							SipProfile acc = accountChooserButton
									.getSelectedAccount();
							if (acc != null) {
								accountToUse = acc.id;
								Log.d("placeCallWithOption  accountToUse===",
										accountToUse + "");
							}

							if (isDigit) {
								toCall = PhoneNumberUtils.stripSeparators(
										+ tv_setCountryCode.getText()+  removeZero(digits.getText().toString()));
							} else {
								toCall =  tv_setCountryCode.getText()+removeZero(digits.getText().toString());
							}
							if (TextUtils.isEmpty(toCall)) {
								return;
							}

							toCall = toCall.replaceAll("\\+", "");
							System.out.println("Calling number is" + toCall);
							digits.getText().clear();
							if (accountToUse >= 0) {
								try {
									service.makeCallWithOptions(toCall,
											accountToUse.intValue(),
											bundleforcall);
								} catch (RemoteException e) {
									Log.e(TAG,
											"Service can't be called to make the call");
								}
							} else if (accountToUse != SipProfile.INVALID_ID) {

								CallHandlerPlugin ch = new CallHandlerPlugin(
										getActivity());
								ch.loadFrom(accountToUse, toCall,
										new OnLoadListener() {
											@Override
											public void onLoad(
													CallHandlerPlugin ch) {
												placePluginCall(ch);
											}
										});
							}

						} else {
							digits.setText("");
							dialogboxR4WOut();
						}
					} else {
						Toast.makeText(getActivity(), "Balance not updated",
								Toast.LENGTH_SHORT).show();
					}

				}
			});
		}
	}

	public void placeVMCall() {
		Long accountToUse = SipProfile.INVALID_ID;
		SipProfile acc = null;
		acc = accountChooserButton.getSelectedAccount();
		if (acc != null) {
			accountToUse = acc.id;
		}

		if (accountToUse >= 0) {
			SipProfile vmAcc = SipProfile.getProfileFromDbId(getActivity(),
					acc.id, new String[] { SipProfile.FIELD_VOICE_MAIL_NBR });
			if (!TextUtils.isEmpty(vmAcc.vm_nbr)) {
				// Account already have a VM number
				try {
					service.makeCall(vmAcc.vm_nbr, (int) acc.id);
				} catch (RemoteException e) {
					Log.e(TAG, "Service can't be called to make the call");
				}
			} else {
				// Account has no VM number, propose to create one
				final long editedAccId = acc.id;
				LayoutInflater factory = LayoutInflater.from(getActivity());
				final View textEntryView = factory.inflate(
						R.layout.alert_dialog_text_entry, null);

				missingVoicemailDialog = new AlertDialog.Builder(getActivity())
						.setTitle(acc.display_name)
						.setView(textEntryView)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										if (missingVoicemailDialog != null) {
											TextView tf = (TextView) missingVoicemailDialog
													.findViewById(R.id.vmfield);
											if (tf != null) {
												String vmNumber = tf.getText()
														.toString();
												if (!TextUtils
														.isEmpty(vmNumber)) {
													ContentValues cv = new ContentValues();
													cv.put(SipProfile.FIELD_VOICE_MAIL_NBR,
															vmNumber);

													int updated = getActivity()
															.getContentResolver()
															.update(ContentUris
																	.withAppendedId(
																			SipProfile.ACCOUNT_ID_URI_BASE,
																			editedAccId),
																	cv, null,
																	null);
													Log.d(TAG,
															"Updated accounts "
																	+ updated);
												}
											}
											missingVoicemailDialog.hide();
										}
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (missingVoicemailDialog != null) {
											missingVoicemailDialog.hide();
										}
									}
								}).create();

				// When the dialog is up, completely hide the in-call UI
				// underneath (which is in a partially-constructed state).
				missingVoicemailDialog.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_DIM_BEHIND);

				missingVoicemailDialog.show();
			}
		} else if (accountToUse == CallHandlerPlugin
				.getAccountIdForCallHandler(
						getActivity(),
						(new ComponentName(
								getActivity(),
								com.roamprocess1.roaming4world.plugins.telephony.CallHandler.class)
								.flattenToString()))) {
			// Case gsm voice mail
			TelephonyManager tm = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String vmNumber = tm.getVoiceMailNumber();

			if (!TextUtils.isEmpty(vmNumber)) {
				if (service != null) {
					try {
						service.ignoreNextOutgoingCallFor(vmNumber);
					} catch (RemoteException e) {
						Log.e(TAG, "Not possible to ignore next");
					}
				}
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts(
						"tel", vmNumber, null));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {

				missingVoicemailDialog = new AlertDialog.Builder(getActivity())
						.setTitle(R.string.gsm)
						.setMessage(R.string.no_voice_mail_configured)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (missingVoicemailDialog != null) {
											missingVoicemailDialog.hide();
										}
									}
								}).create();

				// When the dialog is up, completely hide the in-call UI
				// underneath (which is in a partially-constructed state).
				missingVoicemailDialog.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				missingVoicemailDialog.show();
			}
		}
		// TODO : manage others ?... for now, no way to do so cause no vm stored
	}

	private void placePluginCall(CallHandlerPlugin ch) {
		try {
			String nextExclude = ch.getNextExcludeTelNumber();
			if (service != null && nextExclude != null) {
				try {
					service.ignoreNextOutgoingCallFor(nextExclude);
				} catch (RemoteException e) {
					Log.e(TAG, "Impossible to ignore next outgoing call", e);
				}
			}
			ch.getIntent().send();
		} catch (CanceledException e) {
			Log.e(TAG, "Pending intent cancelled", e);
		}
	}

	@Override
	public void deleteChar() {
		keyPressed(KeyEvent.KEYCODE_DEL);
	}

	@Override
	public void deleteAll() {
		digits.getText().clear();
	}

	private final static String TAG_AUTOCOMPLETE_SIDE_FRAG = "autocomplete_dial_side_frag";

	@Override
	public void onVisibilityChanged(boolean visible) {
		if (visible && getResources().getBoolean(R.bool.use_dual_panes)) {
			if (digits != null) {
				Bundle bundle = new Bundle();
				bundle.putCharSequence(
						DialerAutocompleteDetailsFragment.EXTRA_FILTER_CONSTRAINT,
						removeZero(digits.getText().toString()));
			}

			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.details, autoCompleteFragment,
					TAG_AUTOCOMPLETE_SIDE_FRAG);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commitAllowingStateLoss();
		}
	}

	public void onVisibilityChanged_custom(boolean visible) {
		if (visible && getResources().getBoolean(R.bool.use_dual_panes)) {

			if (digits != null) {
				Bundle bundle = new Bundle();
				bundle.putCharSequence(
						DialerAutocompleteDetailsFragment.EXTRA_FILTER_CONSTRAINT,
						removeZero(digits.getText().toString()));
			}
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.details, autoCompleteFragment,
					TAG_AUTOCOMPLETE_SIDE_FRAG);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commitAllowingStateLoss();

		}
	}

	@Override
	public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);

		return digits.onKeyDown(keyCode, event);
	}

	public class AsynctaskGetCallRate extends AsyncTask<Void, Void, Boolean> {

		Context context;
		String number, returnValue, valid;

		public AsynctaskGetCallRate(String number) {
			super();
			// do stuff
			this.number = number;
		}

		ProgressDialog mProgressDialog;

		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			number = number.replaceAll("[-+.^:,]", "");
		}

		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Log.d("context", "Successfully updated Call-Rate");
				String toCall=removeZero(digits.getText().toString());
				if (toCall.length() <= 1) {
					setCallRate.setText("");
				} else {
					if (valid.equals("true")) {
						int len = returnValue.length();
						Log.d("returnValuelength", len + " !");
						if (len <= 2) {
							setCallRate.setText("\u00A2 " + returnValue + " "
									+ " / min");
						} else if (len == 3) {
							setCallRate.setText("$ " + returnValue.substring(0)
									+ " \u00A2 " + returnValue.substring(1, 3)
									+ " / min");

						} else if (len == 4) {
							setCallRate.setText("$ "
									+ returnValue.substring(0, 2) + " \u00A2 "
									+ returnValue.substring(2, 4) + " / min");
						}
					} else
						Log.d("context", "Error in updated Call-Rate");

				}
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.d("doInBackgroud", "doInBackground");
			if (webServiceGetCallRate()) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

		public boolean webServiceGetCallRate() {
			// TODO Auto-generated method stub
			try {
				Log.d("webServiceFlightInfo", "called");
				HttpParams p = new BasicHttpParams();
				p.setParameter("user", "1");
				HttpClient httpclient = new DefaultHttpClient(p);
		
				Log.d("url", url + " #");
				HttpGet httpget = new HttpGet(url);
				ResponseHandler<String> responseHandler;
				String responseBody;
				responseHandler = new BasicResponseHandler();
				responseBody = httpclient.execute(httpget, responseHandler);
				JSONObject json = new JSONObject(responseBody);
				returnValue = json.getString("response");
				valid = json.getString("valid");

				if (valid.equals("true")) {
					number_call = true;
					numbercall_true = json.getString("phone_no");
				}

				return true;

			} catch (Throwable t) {

				t.printStackTrace();

				return false;
			}

		}
	}

	public class MyAsyncTaskGetBalance extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			if (balance == null)
				balance = "0";
			tv_dialpad_userbalance.setText("Bal : " + balance + " $");
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
			LayoutInflater mInflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

	private void hideWindow() {
		if (selectWindow != null) {
			selectWindow.dismiss();
		}
	}

	public void dialogboxR4WOut() {
		final Dialog dialogR4WOut = new Dialog(getActivity());
		// dialogMBTelcomOut.setTitle("MBTelcom Out Credit");
		dialogR4WOut.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogR4WOut.setContentView(R.layout.r4wout);
		dialogR4WOut.show();
		Button btn$10 = (Button) dialogR4WOut.findViewById(R.id.btn10);
		Button btn$20 = (Button) dialogR4WOut.findViewById(R.id.btn20);
		Button btn$30 = (Button) dialogR4WOut.findViewById(R.id.btn30);
		Button btnGetRates = (Button) dialogR4WOut
				.findViewById(R.id.btngetrates);
		Button btnMaybeLater = (Button) dialogR4WOut
				.findViewById(R.id.btnmaybelater);

		btnMaybeLater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogR4WOut.dismiss();
			}
		});

		btn$10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT)
						.show();
				dialogR4WOut.dismiss();
			}
		});
		btn$20.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT)
						.show();
				dialogR4WOut.dismiss();
			}
		});
		btn$30.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT)
						.show();
				dialogR4WOut.dismiss();
			}
		});

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (sqliteprovider != null)
			sqliteprovider.close();
	}

	public String removeZero(String phNumber) {
		
		phNumber=phNumber.replaceAll("[^0-9]", "");
		
		if(phNumber.contains(" "))
		phNumber=phNumber.replaceAll("\\s+", "");
		
		
		if(phNumber.contains("-"))		
		phNumber=phNumber.replaceAll(Pattern.quote("-"), "");
		else if(phNumber.contains("("))
		phNumber=phNumber.replaceAll(Pattern.quote("("), "");
		else if(phNumber.contains(")"))
		phNumber=phNumber.replaceAll(Pattern.quote(")"), "");
		
	
		
		if (phNumber.startsWith("00"))
			phNumber = phNumber.substring(2);
		else if (phNumber.startsWith("0"))
			phNumber = phNumber.substring(1);
		return phNumber;
}

	public class CustomAdapterToCountry extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;

		public CustomAdapterToCountry(Context context, String[] values) {
			super(context, R.layout.spinner_row_country_reg, values);
			this.context = context;
			this.values = values;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.spinner_row_country_selectcountry, parent, false);
		
			String[] CodeName = values[position].split(":");
			TextView textView = (TextView) rowView.findViewById(R.id.txtVcountryName);
			TextView txtCountyCode = (TextView) rowView.findViewById(R.id.txtVcountryCode);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.imgCountryLogo);
			try {

				imageView.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/"+ CodeName[1] + "copy", null, null));
				txtCountyCode.setText("(+" + CodeName[0] + ")");
				String CountryName = Character.toUpperCase(CodeName[1]
						.charAt(0)) + CodeName[1].substring(1);

				textView.setText(CountryName);

			} catch (Exception e) {
				// TODO: handle exception
			}
			return rowView;
		}
	}

	public void contactinfoView(String number, String countryCode, String s) {
		final String finalNumber = countryCode.replace("+", "")+ removeZero(s.toString());
		System.out.println("finalNumber in contactinfoView" + finalNumber + ":"+ countryCode + ":" + s);

		Cursor r4wCursor = sqliteprovider.fetch_contact_from_R4W(finalNumber);
		if (r4wCursor != null && r4wCursor.getCount() > 0) {
			r4wCursor.moveToFirst();
			relativeContactInfo.setVisibility(View.VISIBLE);
			final String Name = r4wCursor.getString(r4wCursor.getColumnIndex(DBContacts.R4W_CONTACT_NAME));
			System.out.println("Name is r4w Name :" + Name);
			txtVUserName.setText("R4W " + Name);
			txtr4wName.setText(Name + "");
			setSearchData(Name, finalNumber);
			System.out.println("calling name :" + number + ":" + ":" + Name);
			
			videoCall = true;
			isOutCall = false;
			imgVedioCall.setClickable(true);
			imgRecharge.setVisibility(View.VISIBLE);
			imgVAddcontact.setVisibility(View.GONE);
			relativeContactInfo.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=	new Intent(getActivity(), R4wFriendsProfile.class);
					intent.putExtra("R4wCallingNumber",finalNumber );
					intent.putExtra("R4wCallingName",Name );
					startActivity(intent);
			 		
				}
			});
	 		
			

		} else {
			r4wUserImg.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/ic_contact_picture_holo_dark", null, null));
			System.out.println("Number not register");
			String keyword = countryCode.toString().replace("+", "") + s;
			
			for (ContactItemInterface item : AllContactList) {
				ExampleContactItem contact = (ExampleContactItem) item;
				if ((contact.getFullName().equals(keyword))) {

					System.out.println("number not found in all contact with country code");
					relativeContactInfo.setVisibility(View.VISIBLE);
					txtr4wName.setText(contact.getNickName() + "");
					setSearchData(contact.getNickName(), keyword);
					imgRecharge.setVisibility(View.VISIBLE);
					imgVAddcontact.setVisibility(View.GONE);

				} else if ((contact.getFullName().equals(s.toString()))) {
					// System.out.println("number  found in all contact without country code");
					// System.out.println("Default country code:"+user_countrycode);
					// System.out.println("selected country code:"+countryCode.toString().replace("+",""));
					if (user_countrycode.equals(countryCode.toString().replace("+", ""))) {
						relativeContactInfo.setVisibility(RelativeLayout.VISIBLE);
						System.out.println("number  found in all contact without country code and default coutry code");
						txtr4wName.setText(contact.getNickName() + "");
						setSearchData(contact.getNickName(), s.toString());
						imgRecharge.setVisibility(View.VISIBLE);
						imgVAddcontact.setVisibility(View.GONE);

					} else {
						System.out.println("number not found in all contact without country code and default coutry code");
						relativeContactInfo.setVisibility(RelativeLayout.VISIBLE);
						txtr4wName.setText("Search Contact ...");
						imgRecharge.setVisibility(View.VISIBLE);
						imgVAddcontact.setVisibility(View.GONE);
						//System.out.println("drawable value in contact :"+r4wUserImg.getBackground());
											}

				}
			
				if (!contact.getFullName().equals(keyword)) {
					imgRecharge.setVisibility(View.GONE);
					imgVAddcontact.setVisibility(View.VISIBLE);
					relativeContactInfo.setVisibility(View.VISIBLE);
					txtr4wName.setText("Search Contact ...");
				//	System.out.println("drawable value in contact :1"+r4wUserImg.getBackground());
					//r4wUserImg.setImageResource(getResources().getIdentifier("com.roamprocess1.roaming4world:drawable/ic_contact_picture_holo_dark", null, null));

				}

			}

			videoCall = false;
			imgVedioCall.setClickable(true);
			txtVUserName.setText("Save");
			isOutCall = true;
			txtVUserName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_INSERT);
					intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					intent.putExtra(ContactsContract.Intents.Insert.PHONE,
							removeZero(digits.getText().toString()));
					startActivity(intent);
				}
			});
		}
		if (!r4wCursor.isClosed())
			r4wCursor.close();

	}

	public void setSearchData(String userName, String userNumber) {
		Drawable pic;
		Bitmap bmp = null, img = null;
		supportnum = prefs.getString(stored_supportnumber, "");

		String fileuri = "/sdcard/R4W/ProfilePic/" + userNumber + ".png";
		File imageDirectoryprofile = new File(fileuri);
		if (imageDirectoryprofile.exists()) {
			img = BitmapFactory.decodeFile(fileuri);
			pic = new BitmapDrawable(getActivity().getResources(), img);

		} else if (supportnum.equals(userName)) {
			pic = getActivity().getResources().getDrawable(
					R.drawable.roaminglogo);
		} else {
			pic = getActivity().getResources().getDrawable(
					R.drawable.ic_contact_picture_holo_dark);
		}
		bmp = ((BitmapDrawable) pic).getBitmap();

		try {
			bmp = ImageHelperCircular.getRoundedCornerBitmap(bmp,
					bmp.getWidth());
		} catch (Exception e) {
			pic = getActivity().getResources().getDrawable(
					R.drawable.ic_contact_picture_holo_dark);
			bmp = ((BitmapDrawable) pic).getBitmap();
			e.printStackTrace();
		} finally {
			r4wUserImg.setImageBitmap(bmp);
		}
	}

}
