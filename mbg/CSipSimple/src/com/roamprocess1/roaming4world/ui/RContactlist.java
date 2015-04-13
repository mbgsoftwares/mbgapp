package com.roamprocess1.roaming4world.ui;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent.CanceledException;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.ContactsContract.Data;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.ISipService;
import com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi;
import com.roamprocess1.roaming4world.api.SipCallSession;
import com.roamprocess1.roaming4world.api.SipConfigManager;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.contactlist.AllExampleDataSource;
import com.roamprocess1.roaming4world.contactlist.ContactItemInterface;
import com.roamprocess1.roaming4world.contactlist.ExampleContactAdapter;
import com.roamprocess1.roaming4world.contactlist.ExampleContactItem;
import com.roamprocess1.roaming4world.contactlist.ExampleContactListView;
import com.roamprocess1.roaming4world.contactlist.ExampleDataSource;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.roaming4world.NoNetwork;
import com.roamprocess1.roaming4world.roaming4world.R4WAboutUs;
import com.roamprocess1.roaming4world.service.SipService;
import com.roamprocess1.roaming4world.syncadapter.GenericAccountService;
import com.roamprocess1.roaming4world.syncadapter.SyncUtils;
import com.roamprocess1.roaming4world.ui.SipHome.ViewPagerVisibilityListener;
import com.roamprocess1.roaming4world.ui.dialpad.DialerAutocompleteDetailsFragment;
import com.roamprocess1.roaming4world.ui.dialpad.DialerLayout.OnAutoCompleteListVisibilityChangedListener;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.utils.CallHandlerPlugin;
import com.roamprocess1.roaming4world.utils.CallHandlerPlugin.OnLoadListener;
import com.roamprocess1.roaming4world.utils.DialingFeedback;
import com.roamprocess1.roaming4world.utils.PreferencesWrapper;
import com.roamprocess1.roaming4world.utils.contacts.ContactsWrapper;
import com.roamprocess1.roaming4world.widgets.AccountChooserButton;
import com.roamprocess1.roaming4world.widgets.DialerCallBar.OnDialActionListener;


public class RContactlist extends SherlockFragment implements OnClickListener,
		OnLongClickListener, TextWatcher,
		OnDialActionListener, ViewPagerVisibilityListener, OnKeyListener,
		OnAutoCompleteListVisibilityChangedListener {

	private static final String THIS_FILE = "DialerFragment";
	private static final String TAG = "RContactlist";
	public List<String> r4wContactListName;
	public DBContacts sqliteprovider;  
	protected static final int PICKUP_PHONE = 0;
	private EditText digits;
	private String initText = null,signUpProcess,prefSignUpProcess,selfNumber,callingNumber="",stored_account_register_status,stored_user_mobile_no,
			User_mobile_no,stored_user_country_code,user_countrycode,r4wContactsLoad,prefR4wContactsLoad,defaultPath;
	private Context r4wContext;
	SharedPreferences prefs;
	public AccountChooserButton accountChooserButton;
	private Boolean isDigit = null,isR4wContact=true;
	
	public String[] contact_array ,r4wNameArray,phoneContacts,r4wUserImageArray,r4wUserStatus,allConcat_contact_list_Name,allContactsPhone;
	public String getAllContacts[];
	public Cursor allContactsCursor;
	private DialingFeedback dialFeedback;
	private final int[] buttonsToLongAttach = new int[] { R.id.button0,R.id.button1 };
	private ISipService service;
	private PreferencesWrapper prefsWrapper;
	private AlertDialog missingVoicemailDialog;
	public static String[] r4wCompleteContactList;
	private TextView btnR4Wcontacts,btnR4WOut;
	private EditText editSearchText;
	private boolean mDualPane;
	private DialerAutocompleteDetailsFragment autoCompleteFragment;
	private LinearLayout lnrRoaming,lnrAll,LnrImgRightRef;
	ImageButton imgButton_Refresh, imgRightMenu;
	private File contactFile;
	int serverResponseCode = 0;

	public static boolean isRomaing4worldContact=true;
	private ExampleContactListView r4wListview,allListview;
	private String searchString;
	
	private ExampleContactAdapter allAdapter,adapter;
	private Object searchLock = new Object();
	private Object AllsearchLock = new Object();
	private View actionBarView; 
	private Typeface mediumAllCaps,robotoBold,robotoRegular;
	
	boolean inSearchMode = false;
	boolean AllInSearchMode = false;

	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	
	List<ContactItemInterface> AllContactList;
	List<ContactItemInterface> AllFilterList;
	
	
	
	private SearchListTask curSearchTask = null;
	private SearchListTaskAll allCurSearchTask = null;
	
	String stored_flagimage = "", stored_supportnumber, supportnum ;
	
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
	
	public static View v = null;
	@Override
	public void onAutoCompleteListVisibiltyChanged() {
	
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
			service =Roaming4WorldCustomApi.r4wISipService;
			prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
			stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
			User_mobile_no = prefs.getString(stored_user_mobile_no, "no");
			stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
			user_countrycode=prefs.getString(stored_user_country_code, "");
			System.out.println("User_mobile_no =="+User_mobile_no);
			selfNumber=stored_user_country_code+user_countrycode;
			stored_flagimage = "com.roamprocess1.roaming4world.flagimage";
			r4wContactsLoad= "com.roamprocess1.roaming4world.r4wContactsLoad";
			prefR4wContactsLoad =prefs.getString(r4wContactsLoad, "NotR4wContactsLoad");
			stored_supportnumber = "com.roamprocess1.roaming4world.support_no";			
			signUpProcess = "com.roamprocess1.roaming4world.signUpProcess";
			prefSignUpProcess =prefs.getString(signUpProcess, "NotCompleted");
			stored_account_register_status = "com.roamprocess1.roaming4world.account_register_status";
			if (isDigit == null) {
				isDigit = !prefsWrapper.getPreferenceBooleanValue(SipConfigManager.START_WITH_TEXT_DIALER);
			}
			mediumAllCaps = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Medium.ttf");
			robotoBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Bold.ttf");
			robotoRegular=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Regular.ttf");
		
			setHasOptionsMenu(true);
	}

	private Object mSyncObserverHandle;
	 private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
	        /** Callback invoked with the sync adapter status changes. */
	        @Override
	        public void onStatusChanged(int which) {
	            getActivity().runOnUiThread(new Runnable() {
	                /**
	                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
	                 * runs on the UI thread.
	                 */
	                @Override
	                public void run() {
	                    // Create a handle to the account that was created by
	                    // SyncService.CreateSyncAccount(). This will be used to query the system to
	                    // see how the sync status has changed.
	                    Account account = GenericAccountService.GetAccount();
	                    if (account == null) {
	                        // GetAccount() returned an invalid value. This shouldn't happen, but
	                        // we'll set the status to "not refreshing".
	       //                 setRefreshActionButtonState(false);
	                        return;
	                    }

	                    // Test the ContentResolver to see if the sync adapter is active or pending.
	                    // Set the state of the refresh button accordingly.
	                    boolean syncActive = ContentResolver.isSyncActive(
	                            account, SyncUtils.CONTENT_AUTHORITY);
	                    boolean syncPending = ContentResolver.isSyncPending(
	                            account, SyncUtils.CONTENT_AUTHORITY);
	       //             setRefreshActionButtonState(syncActive || syncPending);
	                }
	            });
	        }
	    };
	
	
	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	
		try{
				v = inflater.inflate(R.layout.rcontacts, container, false);
				getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN );
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getCause());
				}
		setHasOptionsMenu(true);
		
		
		
		digits = (EditText) v.findViewById(R.id.input_search_query);
		btnR4Wcontacts = (TextView) v.findViewById(R.id.btnR4WContacts);
		btnR4WOut=(TextView) v.findViewById(R.id.btnR4Wout);
		lnrRoaming=(LinearLayout)v.findViewById(R.id.lnrlRoaming);
		lnrAll=(LinearLayout)v.findViewById(R.id.lnrlAll);
		editSearchText=(EditText)v.findViewById(R.id.input_search_query);
		editSearchText.setTypeface(robotoRegular);
		editSearchText.setCursorVisible(false);
		editSearchText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				editSearchText.setCursorVisible(true);
				return false;
			}
		});
		
		btnR4Wcontacts.setTypeface(mediumAllCaps);
		btnR4WOut.setTypeface(mediumAllCaps);
		accountChooserButton = (AccountChooserButton) v.findViewById(R.id.accountChooserButton);
		service =Roaming4WorldCustomApi.r4wISipService;
		System.out.println("Rcontacts oncreate view");
		if (savedInstanceState != null) {
			isDigit = savedInstanceState.getBoolean(TEXT_MODE_KEY, isDigit);
		}
		
		
		
		filterList = new ArrayList<ContactItemInterface>();
		AllFilterList = new ArrayList<ContactItemInterface>();
		
	 	contactList = ExampleDataSource.getSampleContactList(getActivity());
	 	AllContactList= AllExampleDataSource.getSampleContactList(getActivity());
	 	
	 	r4wListview = (ExampleContactListView) v.findViewById(R.id.listview);
	 	allListview= (ExampleContactListView) v.findViewById(R.id.allcontactList);
	 	
	 	adapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, contactList);
	 	
	 
	 	digits.addTextChangedListener(this);
		btnR4Wcontacts.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editSearchText.setCursorVisible(false);
				isR4wContact=true;	
				isRomaing4worldContact=true;				
				adapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, contactList);
				r4wListview.setAdapter(adapter);				
				allListview.setVisibility(View.GONE);
				r4wListview.setVisibility(View.VISIBLE);
				lnrRoaming.setVisibility(View.VISIBLE);
				lnrAll.setVisibility(View.INVISIBLE);
				prefs.edit().putBoolean(stored_flagimage, true).commit();
			}
		});

		btnR4WOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				editSearchText.setCursorVisible(false);
				r4wListview.setVisibility(View.GONE);
			
				isRomaing4worldContact=false;
				allListview.setVisibility(View.VISIBLE);
				allAdapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, AllContactList);
				allListview.setAdapter(allAdapter);
				lnrRoaming.setVisibility(View.INVISIBLE);
				lnrAll.setVisibility(View.VISIBLE);
				
				isR4wContact=false;
				prefs.edit().putBoolean(stored_flagimage, false).commit();
			}
		});
		
		r4wListview.setFastScrollEnabled(true);
		allListview.setFastScrollEnabled(true);
		r4wListview.setAdapter(adapter);
		
		sqliteprovider = new DBContacts(getActivity());
		sqliteprovider.openToRead();
		
		allListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				List<ContactItemInterface> AllSearchList = AllInSearchMode ? AllFilterList: AllContactList ;
				
				float lastTouchX = allListview.getScroller().getLastTouchDownEventX();
				if(lastTouchX < 45 && lastTouchX > -1){
					//Toast.makeText(getActivity(), "User image is clicked ( " + AllSearchList.get(position).getItemForIndex()  + ")", Toast.LENGTH_SHORT).show();
				}
				else{
					//Toast.makeText(getActivity(), "Nickname: " + searchList.get(position).getItemForIndex() , Toast.LENGTH_SHORT).show();
					TextView txtName=(TextView)v.findViewById(R.id.txtr4wName);
					TextView txtNameLast=(TextView)v.findViewById(R.id.txtr4wLastName); 
					
			 		String InviteUserName=txtName.getText().toString()+" "+txtNameLast.getText().toString();
			 		TextView txtr4wCallNumber = (TextView)v.findViewById(R.id.txtr4wNumber);
			 		String InviteNumber=txtr4wCallNumber.getText().toString();
			 		System.out.println("Rcontact number invite number:"+InviteNumber);
			 	
			 		String finalNo=	removeZero(InviteNumber);
			 		
			 		if(finalNo.startsWith(user_countrycode)){
			 			
			 		}else{
			 			finalNo=user_countrycode+finalNo;
			 		}
			 		System.out.println("Rcontact number invite number: "+finalNo);
			 		
			 		Cursor r4wCursor = sqliteprovider.fetch_contact_from_R4W(removeZero(finalNo));
			 		if (r4wCursor != null && r4wCursor.getCount() > 0) {
						r4wCursor.moveToFirst();
						final String Name = r4wCursor.getString(r4wCursor.getColumnIndex(DBContacts.R4W_CONTACT_NAME));
						Intent intent=	new Intent(getActivity(), R4wFriendsProfile.class);
						intent.putExtra("R4wCallingNumber",finalNo );
						intent.putExtra("R4wCallingName",Name );
						startActivity(intent);
						
						
					}else{
						Intent intent=	new Intent(getActivity(), R4wInvite.class);
						intent.putExtra("InviteNumber",InviteNumber );
						intent.putExtra("InviteName",InviteUserName );
						startActivity(intent);
					}
					
				}
					
			}
		});
		
		r4wListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				List<ContactItemInterface> searchList = inSearchMode ? filterList : contactList ;
				
				float lastTouchX = r4wListview.getScroller().getLastTouchDownEventX();
				if(lastTouchX < 45 && lastTouchX > -1){
					//Toast.makeText(getActivity(), "User image is clicked ( " + searchList.get(position).getItemForIndex()  + ")", Toast.LENGTH_SHORT).show();
				}
				else{
					TextView txtName=(TextView)v.findViewById(R.id.txtr4wName);
			 		String R4wCallerName=txtName.getText().toString();
			 		TextView txtr4wCallNumber = (TextView)v.findViewById(R.id.txtr4wNumber);
			 		String r4wCallNumber=txtr4wCallNumber.getText().toString();
			 		supportnum = prefs.getString(stored_supportnumber, "");
			 		String nm = stripNumber(r4wCallNumber);
			 		if(supportnum.equals(nm)){
			 			getActivity().startActivity(new Intent(getActivity(), R4WAboutUs.class));
			 		}else{
			 			System.out.println("RcontactList intent info :"+R4wCallerName+":"+r4wCallNumber);
				 		Intent intent=	new Intent(getActivity(), R4wFriendsProfile.class);
						intent.putExtra("R4wCallingNumber",r4wCallNumber );
						intent.putExtra("R4wCallingName",R4wCallerName );
						startActivity(intent);
				 		}
					
				}
					
			}
		});
		
		lnrAll.setVisibility(View.INVISIBLE);
		lnrRoaming.setVisibility(View.VISIBLE);
		
		// starting

		// TODO Auto-generated method stub
		editSearchText.setCursorVisible(false);
		r4wListview.setVisibility(View.GONE);
	
		isRomaing4worldContact=false;
		allListview.setVisibility(View.VISIBLE);
		allAdapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, AllContactList);
		allListview.setAdapter(allAdapter);
		lnrRoaming.setVisibility(View.INVISIBLE);
		lnrAll.setVisibility(View.VISIBLE);
		
		isR4wContact=false;
		prefs.edit().putBoolean(stored_flagimage, false).commit();
		
		//finished
		
		if (initText != null) {
			digits.setText(initText);
			//initText = null;
		}

		v.setOnKeyListener(this);
		actionBarView= getActivity().getActionBar().getCustomView();
		imgButton_Refresh = (ImageButton) actionBarView.findViewById(R.id.imgRightMenu_refresh);
        imgRightMenu = (ImageButton) actionBarView.findViewById(R.id.imgRightMenu);		
        imgButton_Refresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent= new Intent(getActivity(),ContactListActivity.class);
				//startActivity(intent);
				prefs.edit().putString(signUpProcess, "NotCompleted").commit();
		//		r4wContacts(getActivity());
				SyncUtils.TriggerRefresh();
				Toast.makeText(getActivity(), "Contact Refreshing", Toast.LENGTH_SHORT).show();
			}
	
		});
        return v;
	}

	public String stripNumber(String nu){
    	if(nu.contains("@")){
    		String[] arr = nu.split("@");
    		nu = arr[0];
    		if(nu.contains(":")){
    			arr = nu.split(":");
    			nu = arr[1];
    		}
    	}
    	return nu;
    }
	
	 @Override
		public void afterTextChanged(Editable s) {
			searchString = digits.getText().toString().trim().toUpperCase();
			if(isR4wContact)
			{
				if(curSearchTask!=null && curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
				{
					try{
						curSearchTask.cancel(true);
					}
					catch(Exception e){
						Log.i(TAG, "Fail to cancel running search task");
					}
					
				}
				curSearchTask = new SearchListTask();
				curSearchTask.execute(searchString); // put it in a task so that ui is not freeze
			}else{
					if(allCurSearchTask!=null && allCurSearchTask.getStatus() != AsyncTask.Status.FINISHED)
					{
					try{
						allCurSearchTask.cancel(true);
					}
					catch(Exception e){
						Log.i(TAG, "Fail to cancel running search task");
					}
					
				}
				allCurSearchTask = new SearchListTaskAll();
				allCurSearchTask.execute(searchString);
			}
				
	 }
	    
	    
	    @Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
	    	// do nothing
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// do nothing
		}
	    
		
	public void callMessage(String numberget) {
		// TODO Auto-generated method stub
		
	
		String fromFull = "<sip:"+ numberget+"@208.43.85.86>";
		String number = "sip:"+ numberget +"@208.43.85.86";
        Bundle b = MessageFragment.getArguments(number, fromFull);
        
        if (mDualPane)    {
            // If we are not currently showing a fragment for the new
            // position, we need to create and install a new one.
            MessageFragment df = new MessageFragment();
            df.setArguments(b);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.Frame_Layout, df, null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();

        } else {
            Intent it = new Intent(r4wContext, MessageActivity.class);
            it.putExtras(b);
            it.putExtra("call", true);
            startActivity(it);
        }
		       
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onDestroy();
		prefs.edit().putBoolean(stored_flagimage, true).commit();

		if (mSyncObserverHandle != null) {
            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
            mSyncObserverHandle = null;
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		mSyncStatusObserver.onStatusChanged(0);

        // Watch for sync state changes
        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
		getActivity().bindService(serviceIntent, connection,Context.BIND_AUTO_CREATE);
		if (prefsWrapper == null) {
			prefsWrapper = new PreferencesWrapper(getActivity());
		}
		if (dialFeedback == null) {
			dialFeedback = new DialingFeedback(getActivity(), false);
		}
		dialFeedback.resume();
		
		SyncUtils.CreateSyncAccount(activity);
		
	}

	@Override
	public void onDetach() {
		try {
			getActivity().unbindService(connection);
		} catch (Exception e) {
			// Just ignore that
			Log.w(THIS_FILE, "Unable to un bind", e);
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
			if (action == EditorInfo.IME_ACTION_GO) {
				placeCall();
				return true;
			}
			return false;
		}
	};


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
				placeVMCall();
				return true;
			}
		}
		return false;
	}




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

	@Override
	public void placeCall() {
		placeCallWithOption(null);
	}

	@Override
	public void placeVideoCall() {
		Bundle b = new Bundle();
		b.putBoolean(SipCallSession.OPT_CALL_VIDEO, true);
		placeCallWithOption(b);
	}

	private void placeCallWithOption(Bundle b) {
		System.out.println("Service value="+Roaming4WorldCustomApi.r4wISipService);
		service= Roaming4WorldCustomApi.r4wISipService;
		if (service == null) {
			System.out.println("service == null");
			return;
		}

		System.out.println(" inside placeCallWithOption  calling number="+callingNumber);
		
		String toCall = "";
		Long accountToUse = (long) 1;
		toCall = PhoneNumberUtils.stripSeparators(callingNumber.toString());
		if (TextUtils.isEmpty(toCall)) {
			return;
		}
	
		if (accountToUse >= 0) {
			// It is a SIP account, try to call service for that
			try {

				Log.d(TAG, "placeCallWithOption -> Service : accountToUse : aaccountToUse.intValue() ->"+service +" : "+ accountToUse+":"+accountToUse.intValue()+"" );
				service.makeCallWithOptions(toCall, accountToUse.intValue(), b);
			} catch (RemoteException e) {
				Log.e(TAG, "Service can't be called to make the call");
			}
		} else if (accountToUse != SipProfile.INVALID_ID) {
			// It's an external account, find correct external account

			Log.d(TAG ,"accountToUse>= 0 : accountToUse : aaccountToUse.intValue() : -> "+accountToUse +":"+accountToUse.intValue());
			
			CallHandlerPlugin ch = new CallHandlerPlugin(getActivity());
			ch.loadFrom(accountToUse, toCall, new OnLoadListener() {
				@Override
				public void onLoad(CallHandlerPlugin ch) {
					placePluginCall(ch);
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
					Log.e(THIS_FILE, "Service can't be called to make the call");
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
													Log.d(THIS_FILE,
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
				.getAccountIdForCallHandler(getActivity(), (new ComponentName(
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
						Log.e(THIS_FILE, "Not possible to ignore next");
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
	
	}

	private void placePluginCall(CallHandlerPlugin ch) {
		try {
			String nextExclude = ch.getNextExcludeTelNumber();
			if (service != null && nextExclude != null) {
				try {
					service.ignoreNextOutgoingCallFor(nextExclude);
				} catch (RemoteException e) {
					Log.e(THIS_FILE, "Impossible to ignore next outgoing call",
							e);
				}
			}
			ch.getIntent().send();
		} catch (CanceledException e) {
			Log.e(THIS_FILE, "Pending intent cancelled", e);
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
			// That's far to be optimal we should consider uncomment tests for
			// reusing fragment
			// if (autoCompleteFragment == null) {
			autoCompleteFragment = new DialerAutocompleteDetailsFragment();

			if (digits != null) {
				Bundle bundle = new Bundle();
				bundle.putCharSequence(
						DialerAutocompleteDetailsFragment.EXTRA_FILTER_CONSTRAINT,
						digits.getText().toString());
				autoCompleteFragment.setArguments(bundle);
			}
			
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.details, autoCompleteFragment,
					TAG_AUTOCOMPLETE_SIDE_FRAG);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commitAllowingStateLoss();

			// }
		}
	}

	@Override
	public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);

		return digits.onKeyDown(keyCode, event);
	}

	public  String[] fetch_contact_list(Cursor c) {
		String concat_contact_list = "";
		String[] allConcat_contact_list=new String[c.getCount()];
		phoneContacts=new String[c.getCount()];
		allConcat_contact_list_Name=new String[c.getCount()];
		allContactsPhone= new String[c.getCount()];
		//System.out.println("Cccccc"+c.getCount());
		
		String Country_zip_code = GetCountryZipCode();
		int i=0;
		while (c.moveToNext()) {
			
			String value = c.getString(c.getColumnIndex(Data.DATA1));
			String Name =c.getString(c.getColumnIndex(Data.DISPLAY_NAME));
			//System.out.println("Data:"+_id+":"+contact_Id +":"+value+":"+Name+":");
			value= value.replaceAll("\\s+","");
			value= value.replaceAll("-","");
			//System.out.println("Number =="+value);
			
			
			if (!c.isLast()) {
				
					if ( !value.startsWith("*")&& !value.startsWith("#")) {
						
							if (value.startsWith("+")) {
								allConcat_contact_list[i]=value.substring(1) + ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
							
								
							} else if (value.startsWith("00")) {
								String modify_contact_no = value.substring(2);
								modify_contact_no = Country_zip_code+ modify_contact_no;
								allConcat_contact_list[i]=modify_contact_no.toString()+ ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
								
							} else if (value.startsWith("0")) {
								String modify_contact_no = value.substring(1);
								modify_contact_no = Country_zip_code+ modify_contact_no;
								allConcat_contact_list[i]= modify_contact_no.toString()+ ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
								
							} else if (!value.startsWith("+") && !value.startsWith("0")) {
								allConcat_contact_list[i]=Country_zip_code+ value.toString() + ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
							}
						}
			
			} else {
				if (!value.startsWith("*")&& !value.startsWith("#")) {
					
						if (value.startsWith("+")) {
							allConcat_contact_list[i]=value.substring(1);
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
							
						} else if (value.startsWith("00")) {
							String modify_contact_no = value.substring(2);
							modify_contact_no = Country_zip_code+ modify_contact_no;
							allConcat_contact_list[i]=modify_contact_no.toString();
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
									
						} else if (value.startsWith("0")) {
							String modify_contact_no = value.substring(1);
							modify_contact_no = Country_zip_code+ modify_contact_no;
							allConcat_contact_list[i]=modify_contact_no.toString();
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
							
						} else if (!value.startsWith("+") && !value.startsWith("0")) {
							allConcat_contact_list[i]=Country_zip_code+ value.toString();
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
						}
				}
			}
			
			i++;
		}
		c.close();
		
		for(int x=0;x<allConcat_contact_list.length;x++)
		{	
			String number;
			if(allConcat_contact_list[x]!=null||allContactsPhone[x]!=null){
				number=allConcat_contact_list[x].replace(",", "");
				phoneContacts[x]=number;
				//System.out.println("number without ,:"+number+":"+allContactsPhone[x]);
			}else
			{
				phoneContacts[x]=allConcat_contact_list[x];
				//System.out.println("number without ,:"+allConcat_contact_list[x]);
			}
		concat_contact_list+=allConcat_contact_list[x]; 
			
			
		}
		//System.out.println("phoneContacts:length:allConcat_contact_list:length: "+phoneContacts.length+":"+allConcat_contact_list.length+":"+allContactsPhone.length);
		//System.out.println("allConcat_contact_list:length:"+allConcat_contact_list.length);
		return allConcat_contact_list;
	}

	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";
		TelephonyManager manager = (TelephonyManager)r4wContext.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = r4wContext.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	public class asynctask_contact_fetch extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialogContact;
		private String json1;
		Context context;
		
		@Override
		protected void onPostExecute(Boolean result) {
			System.out.println("OnPost execute");
			CharSequence constraint ="str";
			try {
				if(r4wCompleteContactList.length!=0){
				ContactsWrapper.getInstance().getContactsPhonesR4W(r4wContext, constraint);
					//c1.close();
					
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			System.out.println("inside postExecute"+prefs.getString(prefSignUpProcess,"NotComplete"));
			r4wContext.startActivity(new Intent(r4wContext, SipHome.class));
		
			try {
				if (mProgressDialogContact!=null) {
				    if (mProgressDialogContact.isShowing()) {
				    	mProgressDialogContact.dismiss();       
				    }
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
	};

		@Override
		public Boolean doInBackground(Void... params) {
			if (websericeR4WContacts()) {
				Log.d("websericeR4WContacts return=", "SUCCESS");
				
				try {
					Looper.prepare();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			} else {
				Log.d("websericeR4WContacts return=", "Error");
				return false;
			}
		}
		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			try{
			mProgressDialogContact = ProgressDialog.show(context,
			"Contacts loading...","Have patience it will take some time.");
			}catch(Exception e){
				
			}
		}
	}
	@SuppressWarnings("deprecation")
	public boolean websericeR4WContacts() {
		
		String contacts_returned = "",r4wName="",r4wUserImage="",r4wstatus="";
		System.out.println("getAllContacts:"+getAllContacts.length);
	
			HttpParams p = new BasicHttpParams();
            p.setParameter("user", "1");
            
            HttpClient httpclient = new DefaultHttpClient(p);
            
            String url = "http://ip.roaming4world.com/esstel/fetch_contacts_file.php?" + 
                    	 "self_contact="+user_countrycode+User_mobile_no;
         
            HttpPost httppost = new HttpPost(url);
            System.out.println("URL being sent is "+url);
          
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                System.out.println("Response Body is "+responseBody);  
                
                // Parse
                JSONObject jsonObjRecv;
				try {
					JSONArray jsonArray = null;
					jsonObjRecv = new JSONObject(responseBody);
					Log.d(TAG,"jsonObjRecv :"+jsonObjRecv);
					
					String contacts = jsonObjRecv.getString("contacts");
					if(contacts.equals("Error")||contacts.equals("No Contacts")){
						Log.d(TAG,"No contacts Error");
						prefs.edit().putString(signUpProcess, "R4wContactInserted").commit();		
					}
					else{
						jsonArray = jsonObjRecv.getJSONArray("contacts");
						String path = jsonObjRecv.getString("path");
						Log.d(TAG, "jsonArray :"+jsonArray +"path:"+path);
			            if(jsonArray.length()!=0){
			                for (int i = 0; i < jsonArray.length(); i++) {
			                	JSONObject position = jsonArray.getJSONObject(i);
			                		contacts_returned += position.getString("p") + ",";
									r4wName+= position.getString("un")+",";
									r4wstatus+= position.getString("s")+","; 
									String url_image=position.getString("t");
									
									if(!url_image.equals("No image")){
										r4wUserImage +=path + url+ ",";
									}else{
										r4wUserImage += position.getString("t")+ ",";
									}
									
									if(jsonArray.getString(i)==""){
										
									}
								}
			                System.out.println("data=:contacts_returned :r4wName:r4wstatus :r4wUserImage"+contacts_returned+":"+r4wName+":"+r4wstatus+""+r4wUserImage+"");
			                System.out.println();
						}
					}

			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //return true;
            } 
            catch(UnknownHostException e)
            {
            	e.printStackTrace();
            	return false;
            }
            catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            } 
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            catch(Exception e){
            	  //e.printStackTrace();
                  return false;
            }
        
		if(contacts_returned.length()!=0){
			contacts_returned = contacts_returned.substring(0,contacts_returned.length() - 1);
			r4wName=r4wName.substring(0, r4wName.length()-1);
			r4wstatus=r4wstatus.substring(0, r4wstatus.length()-1);
			r4wUserImage=r4wUserImage.substring(0, r4wUserImage.length()-1);
			contact_array = contacts_returned.split(",");
			r4wNameArray=r4wName.split(",");
			r4wUserStatus=r4wstatus.split(",");
			r4wUserImageArray=r4wUserImage.split(",");
			
			r4wCompleteContactList=new String[contact_array.length];
			
			com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi.r4wContacts = contact_array;
			for (int i = 0; i < com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi.r4wContacts.length; i++) {			
				String number=	com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi.r4wContacts[i];
				//System.out.println("number :"+number);
				int index = r4wContactListName.indexOf(number.trim());
				//System.out.println("index ="+index);
				
				try {
					if(index!=-1){
						System.out.println("index ="+index);
						String r4wContactName =allContactsPhone[index];
						String[] parts = r4wContactName.split(",");
						String r4wNamePhone = parts[0];
						String r4wNumberName = parts[1];
						String datacomplte=r4wNamePhone+","+r4wNumberName+","+r4wUserStatus[i]+","+r4wUserImageArray[i]+","+r4wNameArray[i];
					
						r4wCompleteContactList[i]=datacomplte;
						}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			System.out.println("r4wCompleteContactList.length:"+ r4wCompleteContactList.length);
		
		}else{
			System.out.println("No Contact");
		}
		
		return true;
	}
	
	public void r4wCall_Chat( String R4wCallingNumber)
	{
		callingNumber=R4wCallingNumber;
		placeCall();
	}
	
	public void r4wCall_Video( String R4wCallingNumber)
	{
		callingNumber=R4wCallingNumber;
		placeVideoCall();
	}
	
	
	
	public void r4wContacts( Context context)
	{
		r4wContext=context;
		prefs = r4wContext.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		user_countrycode=prefs.getString(stored_user_country_code, "");
		User_mobile_no = prefs.getString(stored_user_mobile_no, "no");
		selfNumber=user_countrycode+User_mobile_no;
		System.out.println("User_mobile_no =="+selfNumber);
				
		signUpProcess = "com.roamprocess1.roaming4world.signUpProcess";
		prefSignUpProcess =prefs.getString(signUpProcess, "NotCompleted");
		
		Log.d(TAG, "prefSignUpProcess ="+prefSignUpProcess);
		Log.d(TAG, "prefSignUpProcess ="+prefs.getString(signUpProcess, "NotCompleted"));
			
		ConnectivityManager connMgr = (ConnectivityManager) r4wContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			allContactsCursor = ContactsWrapper.getInstance().getContactsPhones(r4wContext,null);
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			getAllContacts = fetch_contact_list(allContactsCursor);
			r4wContactListName=	Arrays.asList(phoneContacts);
			r4wContactListName.size();			
			if(prefSignUpProcess.equals("NotCompleted")){ 
				defaultPath=Environment.getExternalStorageDirectory().getAbsolutePath();
				System.out.println("defaultPath:"+defaultPath);
				System.out.println("selfNumber:"+selfNumber);
				
				try {
					contactFile = new File(defaultPath, selfNumber+".txt");
					if (contactFile.exists()) {
						contactFile.delete();
						System.out.println("file Exist");
						sendContacts(contactFile);
						
					}else{
						System.out.println("file does not Exist");
							sendContacts(contactFile);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
	
		} else {
			Intent intent = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent);
		}		
	}
	
	
	public void  sendContacts(File contactFile) throws IOException{

		FileOutputStream fOut = new FileOutputStream(contactFile);
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);				
		String lastContacts=getAllContacts[getAllContacts.length-1];
		//System.out.println("lastContacts:"+lastContacts);
		
		for(int x = 0; x <= getAllContacts.length ; x++ ){
			
			/*
			String value=getAllContacts[x];
			//System.out.println("Value:"+getAllContacts[x]);		
			if(value==null||value.length()<1){
				//System.out.println("inside null");
				
			}else{

				if(x==0){
					System.out.println("selfNumber:"+selfNumber);
					getAllContacts[x] =selfNumber+","+getAllContacts[x];
				}
				if(x==getAllContacts.length-1){
					getAllContacts[x] = getAllContacts[x].substring(0, getAllContacts[x].length());
				}
				
			*/
		//	}
			if(x == 0){
				myOutWriter.append(selfNumber);
			}else if( getAllContacts[ x - 1 ] != null ){
				if(getAllContacts[ x - 1 ].contains(",")){
					getAllContacts[ x - 1 ] = getAllContacts[ x - 1 ].substring(0, getAllContacts[ x - 1 ].length() - 1);
				}
				myOutWriter.append( "," + getAllContacts[ x - 1 ] );
			}
		
			
		}
            
        myOutWriter.close();
        fOut.close();
        
        new AsyncTaskUploadContacts().execute();
        /*
       	ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    		if (networkInfo != null && networkInfo.isConnected()) {
    			 new AsyncTaskUploadContacts().execute();
    		}
    		else{
    			Toast.makeText(getActivity(), "No Internet Connection",Toast.LENGTH_SHORT).show();
    		}*/
	}
	
	
	public class AsyncTaskUploadContacts extends AsyncTask<Void, Void, Boolean>{

		String imagePathUri;
		ProgressDialog mProgressDialogContact;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialogContact = ProgressDialog.show(r4wContext,
					"Contacts loading...","Have patience it will take some time.");
			
		}
	   
	
	

	@SuppressLint("NewApi") @Override
	protected void onPostExecute(Boolean result) {
		if(result == true){
			new asynctask_contact_fetch().execute();
			
			//Toast.makeText(r4wContext, "File Upload Completed", Toast.LENGTH_LONG).show();
		}else{
			//Toast.makeText(r4wContext, "File is not uploaded ", Toast.LENGTH_LONG).show();
		}

		try {
			if (mProgressDialogContact!=null) {
			    if (mProgressDialogContact.isShowing()) {
			    	mProgressDialogContact.dismiss();       
			    }
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		int response = 0;
		
		imagePathUri=defaultPath+"/"+selfNumber+".txt";
		if(imagePathUri.equals(""))
		{	return false;
		}else{
			response = uploadFile(imagePathUri);
		}
		Log.d("response ", response + "  d");
		
		if (response == 200) {
			Log.d("doInBackgroud", "doInBackground");
			contactFile.delete();
			return true;
		} else {
			return false;
		}
	}


	}


	public int uploadFile(String sourceFileUri) {
	  	  String upLoadServerUri = "";
	  	  upLoadServerUri = "http://ip.roaming4world.com/esstel/fetch_contacts_upload.php";
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
		    	   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+selfNumber+"-"+fileName + "\"" + lineEnd);
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
	
	private class SearchListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			filterList.clear();
			String keyword = params[0];
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				// get all the items matching this
				for (ContactItemInterface item : contactList) {
					ExampleContactItem contact = (ExampleContactItem)item;
					if ((contact.getNickName().toUpperCase().indexOf(keyword) > -1) ) {
						filterList.add(item);
					}
				}
			} 
			return null;
		}
		
		protected void onPostExecute(String result) {
			
			synchronized(searchLock)
			{
			if(inSearchMode){
					try {
						ExampleContactAdapter adapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, filterList);
						adapter.setInSearchMode(true);
						r4wListview.setInSearchMode(true);
						r4wListview.setAdapter(adapter);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
				else{
					try {
						
						ExampleContactAdapter adapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, contactList);
						adapter.setInSearchMode(false);
						r4wListview.setInSearchMode(false);
						r4wListview.setAdapter(adapter);	
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			}
			
		}
	}
	
	
	private class SearchListTaskAll extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			AllFilterList.clear();
			String keyword = params[0];
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				// get all the items matching this
				for (ContactItemInterface item : AllContactList) {
					ExampleContactItem contact = (ExampleContactItem)item;
					if ((contact.getNickName().toUpperCase().indexOf(keyword) > -1) ) {
						AllFilterList.add(item);
					}
				}
			} 
			return null;
		}
		
		protected void onPostExecute(String result) {
			
			synchronized(AllsearchLock)
			{
			if(inSearchMode){
					
					ExampleContactAdapter allAdapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, AllFilterList);
					allAdapter.setInSearchMode(true);
					allListview.setInSearchMode(true);
					allListview.setAdapter(allAdapter);
				}
				else{
					ExampleContactAdapter allAdapter = new ExampleContactAdapter(getActivity(), R.layout.testexample_contact_item, AllContactList);
					allAdapter.setInSearchMode(false);
					allListview.setInSearchMode(false);
					allListview.setAdapter(allAdapter);
				}
			}
			
		}
	}
	
	public String removeZero(String phNumber) {
		
		if(phNumber.contains("+")){
			phNumber=phNumber.replaceAll(Pattern.quote("+"), "");
		}
		if(phNumber.contains(" "))
		phNumber=phNumber.replaceAll("\\s+", "");
	
		if (phNumber.startsWith("00"))
			phNumber = phNumber.substring(2);
		else if (phNumber.startsWith("0"))
			phNumber = phNumber.substring(1);
		
		System.out.println("return number :"+phNumber);
		return phNumber;
	}

}