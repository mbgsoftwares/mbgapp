

package com.roamprocess1.roaming4world.ui.messages;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.ISipService;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.models.CallerInfo;
import com.roamprocess1.roaming4world.roaming4world.CurrentFragment;
import com.roamprocess1.roaming4world.roaming4world.SetSenderUriInfo;
import com.roamprocess1.roaming4world.roaming4world.UserChatImageFullscreen;
import com.roamprocess1.roaming4world.service.ChatSocket;
import com.roamprocess1.roaming4world.service.SipNotifications;
import com.roamprocess1.roaming4world.service.SipService;
import com.roamprocess1.roaming4world.ui.MessageSipUri;
import com.roamprocess1.roaming4world.ui.RContactlist;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.PreferencesWrapper;
import com.roamprocess1.roaming4world.utils.SmileyParser;
import com.roamprocess1.roaming4world.utils.clipboard.ClipboardWrapper;
import com.roamprocess1.roaming4world.widgets.AccountChooserButton;
import com.rockerhieu.emojicon.EmojiconsFragment;

public class MessageFragment extends SherlockListFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnClickListener 
{
    private static final String THIS_FILE = "ComposeMessage";
    private static String remoteFrom;
	private String stored_account_register_status,preRegValue;
	
    private TextView fromText,fullFromText;
    private static EditText bodyInput;
    private static AccountChooserButton accountChooserButton;
    private Button sendButton;
    private ImageButton ib_enable_emoticon_frame;
    private SipNotifications notifications;
    private MessageAdapter mAdapter;
    private static String forwardMessageTextData;
	public static boolean bforwardMessage;
	PreferencesWrapper prefs_wrapper;
    int i;
    FrameLayout Fl_Emoticon_Holder;
    MenuItem addContactMenu;
    ActionBar ab; 
    Uri senderPicUri;
    Drawable senderPic;
    Button btnChatMenu,btnAttechementMenu;
    private SharedPreferences prefs;
    private String chatNumber,stored_supportnumber, supportnum;
	public static String multiMedia_msg = "R4WIMGTOCONTACTCHATSEND@@";
	public static String FORWARD_MSG = "";

    DBContacts dbContacts;
    
    String stored_senderUriImage, stored_chatuserName, stored_chatuserNumber, stored_user_country_code;
	private PopupWindow selectWindow = null;
	 public static boolean keyboard_flag = false;
    public interface OnQuitListener {
        public void onQuit();
    }

    private OnQuitListener quitListener;
   
    public void OnConnectionChanged() {
    	try{
			if(SetSenderUriInfo.onlineStatus == false){
				sendButton.setTextColor(Color.parseColor("#FFF"));
			}else{
				sendButton.setTextColor(Color.parseColor("#FFF"));
			}
    	}catch(Exception e){
    		
    	}
	}
    
    private ClipboardWrapper clipboardManager;
	public void setOnQuitListener(OnQuitListener l) {
        quitListener = l;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        System.out.println("MessageFragments.java in onActivityCreated ");
        ListView lv = getListView();
        lv.setOnCreateContextMenuListener(this);
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
        SmileyParser.init(getActivity());
        System.out.println("MessageFragments.java in onCreate()");
        notifications = new SipNotifications(getActivity());
        clipboardManager = ClipboardWrapper.getInstance(getActivity());
    }
    
    

    @SuppressLint("NewApi") @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.compose_message_activity, container, false);
        getActivity().bindService(new Intent(getActivity(), SipService.class), connection, Context.BIND_AUTO_CREATE);
        prefs_wrapper = new PreferencesWrapper(getActivity());
		
        prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
        stored_senderUriImage = "com.roamprocess1.roaming4world.stored_senderUriImage";
        stored_chatuserName = "com.roamprocess1.roaming4world.stored_chatuserName";
        stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_account_register_status = "com.roamprocess1.roaming4world.account_register_status";
		preRegValue=prefs.getString(stored_account_register_status, "Connecting"); 
		System.out.println("oncreate prefValue" + preRegValue);
		
		LinearLayout onlinelayout = (LinearLayout) v.findViewById(R.id.onlineStatus);
		LinearLayout header_onlinelayout = (LinearLayout) v.findViewById(R.id.ll_dialer_onlineStatus);
		
		TextView status_register_account = (TextView) v.findViewById(R.id.status);
	
		ConnectivityManager connMgr1 = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo1 = connMgr1.getActiveNetworkInfo();
		if (networkInfo1 != null && networkInfo1.isConnected()) {

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
		
		
		stored_supportnumber = "com.roamprocess1.roaming4world.support_no";
		
        System.out.println("MessageFragments.java inonCreateView() ");
        fullFromText = (TextView) v.findViewById(R.id.subject);
        fromText = (TextView) v.findViewById(R.id.subjectLabel);
        bodyInput = (EditText) v.findViewById(R.id.embedded_text_editor);
        accountChooserButton = (AccountChooserButton) v.findViewById(R.id.accountChooserButton);
        sendButton = (Button) v.findViewById(R.id.send_button);
        accountChooserButton.setShowExternals(false);
       
        ib_enable_emoticon_frame = (ImageButton) v.findViewById(R.id.ib_enable_emoticon_frame);
        Fl_Emoticon_Holder = (FrameLayout) v.findViewById(R.id.fl_emojicons);
        System.out.println("inonCreateView() Serive:"+service);

    	ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	/*
		if (networkInfo != null && networkInfo.isConnected()) {
				if(preRegValue.equals("Online")){	
	    		sendButton.invalidate();
				sendButton.setTextColor(Color.parseColor("#2AB1E0"));
				sendButton.invalidate();
			} else if(preRegValue.equals("Connecting")){
				sendButton.setTextColor(Color.parseColor("#A9A9A9"));
				sendButton.invalidate();
			} 
		}
		else{
			sendButton.setTextColor(Color.parseColor("#A9A9A9"));
			sendButton.invalidate();
		}*/

    	getFragmentManager().beginTransaction().add(R.id.fl_emojicons, new EmojiconsFragment()).commitAllowingStateLoss();
        i = 0;
        ab = getActivity().getActionBar();
        return v;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDivider(null);
        fromText.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        ib_enable_emoticon_frame.setOnClickListener(this);
		bodyInput.setOnTouchListener(new View.OnTouchListener() {
		@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
	        	emoticon_holder(1);
				return false;
			}
		});
	
		/*
		bodyInput.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				dialogBoxpaste();
				return false;
			}
		});
		*/

		String number = getArguments().getString(SipMessage.FIELD_FROM);
		prefs.edit().putString(stored_chatuserNumber, number).commit();
		Log.setLogLevel(6);
		Log.d("SipMessage.FIELD_FROM", number + "");
		
        System.out.println("MessageFragments.java inonViewCreated() ");
        mAdapter = new MessageAdapter(getActivity(), null);
      
        getListView().setAdapter(mAdapter);
	    getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				System.out.print("Postion"+position);
				TextView text_view = (TextView) arg1.findViewById(R.id.text_view);
				String msg = text_view.getText().toString();
				
				/*
				QuickContactBadge quickContactBadge = (QuickContactBadge) arg1.findViewById(R.id.quick_contact_photo);
				LayoutParams layoutParams = (LayoutParams) quickContactBadge.getLayoutParams();
				layoutParams.g
				*/
				
				if(isvideoMsg(msg) || isaudioMsg(msg)){
					Intent intent = new Intent(getActivity(),MediaMsgHandler.class);
					intent.putExtra("msg", msg);
			     	intent.putExtra("number", stripNumber(prefs.getString(stored_chatuserNumber, "")));
					startActivity(intent);
				}else if(iscontactMsg(msg)){
					TextView tv_msg_info = (TextView) arg1.findViewById(R.id.tv_msg_info);
					String msg_info = tv_msg_info.getText().toString();
					Log.setLogLevel(6);
					Log.d("msg info ", msg_info + " @");
					if(msg_info != null){
						int flag = Integer.parseInt(msg_info);
						dialogBoxContact(getLongitude(msg), getLatitute(msg) , flag);
					}else{
						dialogBoxContact(getLongitude(msg), getLatitute(msg) , 1);
					}
				}else if(msg.contains(multiMedia_msg + "LOC")){
					Log.d("getLatitute(msg)", getLatitute(msg) + " @" );
					Log.d("getLongitude(msg)", getLongitude(msg) + " @" );
					String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.parseFloat(getLatitute(msg).trim()), Float.parseFloat(getLongitude(msg).trim()));
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
					startActivity(intent);

				}else if(msg.contains(multiMedia_msg)){
					Intent intent = new Intent(getActivity(),UserChatImageFullscreen.class);
					intent.putExtra("imageuri", msg + "");
			     	intent.putExtra("number", stripNumber(prefs.getString(stored_chatuserNumber, "")));
					startActivity(intent);

				}
				
			}
		});
        // Setup from args
        String from = getArguments().getString(SipMessage.FIELD_FROM);
        String fullFrom = getArguments().getString(SipMessage.FIELD_FROM_FULL);
        if (fullFrom == null) {
            fullFrom = from;
        }
        setupFrom(from, fullFrom);
        if (remoteFrom == null) {
            chooseSipUri();
        }
  }
    
    private boolean isvideoMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multiMedia_msg + "VID"))
    		return true;
    	else
    		return false;
	}
    
    
    
    private boolean isaudioMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multiMedia_msg + "AUD"))
    		return true;
    	else
    		return false;
	}

    private void dialogBoxpaste(){
		AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
		.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

    
    private void dialogBoxContact(final String name, final String num, int flag){
		AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity())
		.setTitle("Contact Info")
		.setMessage(" Contact Name :\n" + name + "\n Contact Number : \n" + num)
		.setPositiveButton("Close", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		if(flag == 0){
			builder.setNegativeButton("Save", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.dismiss();
			//		saveContact(name , num);
					saveContactIntent(name, num);
				}
			});
			
			
		}
		builder.show();
	}
    
        private void saveContactIntent(String name, String num){
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		intent.putExtra(ContactsContract.Intents.Insert.PHONE,num);
		intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
		startActivity(intent);

    }
    
	private boolean iscontactMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multiMedia_msg + "CON"))
    		return true;
    	else
    		return false;
	}
    
    protected String getLongitude(String nu) {
		// TODO Auto-generated method stub
		
        	if(nu.contains("@@")){
	    		String[] arr = nu.split("@@");
	    		nu = arr[1];
	    		if(nu.contains("-")){
	    			arr = nu.split("-");
	    			nu = arr[1];
	    		}
	    	}
	    	return nu;
	}

	protected String getLatitute(String nu) {
		// TODO Auto-generated method stub
    	if(nu.contains("@@")){
    		String[] arr = nu.split("@@");
    		nu = arr[1];
    		if(nu.contains("-")){
    			arr = nu.split("-");
    			nu = arr[2];
    		}
    	}
    	return nu;
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("MessageFragments.java inonAttach() ");
       getActivity().bindService(new Intent(getActivity(), SipService.class), connection, Context.BIND_AUTO_CREATE);
        
    }
    
    @Override
    public void onDetach() {
        try {
            getActivity().unbindService(connection);
            System.out.println("MessageFragments.java inonDetach() ");
        } catch (Exception e) {
            // Just ignore that
        }
        service = null;
        super.onDetach();
    }
    
    
    @SuppressLint("NewApi")
    public synchronized void setActionBarProp(){
    	
        btnChatMenu=(Button)ab.getCustomView().findViewById(R.id.btnchatmenu);
        btnChatMenu.setOnClickListener(new View.OnClickListener() {
        		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				viewChatMenuPopup(v ,chatNumber);
			}
		});
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
    public void onResume() {
    	
        super.onResume();
        notifications.setViewingMessageFrom(remoteFrom);
        setActionBarProp();
        Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
}

    @Override
    public void onPause() {
        super.onPause();
        notifications.setViewingMessageFrom(null);
        ib_enable_emoticon_frame.setImageResource(R.drawable.smily_icon);
       }

    private final static int PICKUP_SIP_URI = 0;
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(THIS_FILE, "On activity result " + requestCode);
        System.out.println("MessageFragments.java inonActivityResult() ");
        if (requestCode == PICKUP_SIP_URI) {
            if (resultCode == Activity.RESULT_OK) {
                String from = data.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                setupFrom(from, from);
                System.out.println("MessageFragments.java :"+from);
         }
            if (TextUtils.isEmpty(remoteFrom)) {
                if(quitListener != null) {
                    quitListener.onQuit();
                }
            }else {
                loadMessageContent();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Service connection
    private static ISipService service;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
        	service = ISipService.Stub.asInterface(arg1);
        	System.out.println("ServiceConnection Service:"+service);   
        	/*
        	if(forwardMessageTextData!=null){
        		System.out.println("InMessageFragment 1");
         	   	messageforwordText(forwardMessageTextData);
            }
            */
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            service = null;
        }
    };

    private void loadMessageContent() {
    	System.out.println("MessageFragments.java inloadMessageContent() ");
        getLoaderManager().restartLoader(0, getArguments(), this);
        String from = getArguments().getString(SipMessage.FIELD_FROM);

        if (!TextUtils.isEmpty(from)) {
            ContentValues args = new ContentValues();
            args.put(SipMessage.FIELD_READ, true);
            getActivity().getContentResolver().update(SipMessage.MESSAGE_URI, args,SipMessage.FIELD_FROM + "=?", new String[] {from});
        }
    }
    

    public static Bundle getArguments(String from, String fromFull) {
        Bundle bundle = new Bundle();
        if (from != null) {
        	System.out.println("MessageFragment.java in getArguments()  from is: "+SipMessage.FIELD_FROM+" fromfull is "+SipMessage.FIELD_FROM_FULL);
            bundle.putString(SipMessage.FIELD_FROM, from);
            bundle.putString(SipMessage.FIELD_FROM_FULL, fromFull);
        }

        return bundle;
    }
    

    private void setupFrom(String from, String fullFrom) {
    	System.out.println("MessageFragment.java in setupFrom()");        
        if (from != null) {
       
        	String number = CurrentFragment.stripContactNumber(from, prefs.getString(stored_user_country_code, "No Value"));
        	chatNumber=number;
         	
        	if (remoteFrom != from) {
                remoteFrom = from;
                //fromText.setText(remoteFrom);
                System.out.println("sendMessage():from:"+from);
                
                CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(getActivity(), fullFrom);
                if (callerInfo != null && callerInfo.contactExists) {
                   
                	System.out.println("SetupFrom:"+callerInfo.name);
                    fullFromText.setText(callerInfo.name);
                	
                } else {
                	System.out.println("SetupFrom : else"+SipUri.getDisplayedSimpleContact(fullFrom));
                	fullFromText.setText(SipUri.getDisplayedSimpleContact(fullFrom));
                	}
                loadMessageContent();
              notifications.setViewingMessageFrom(remoteFrom);
            }
        }
    }

    private void chooseSipUri() {
    	System.out.println("MessageFragments.java inchooseSipUri() ");
        Intent pickupIntent = new Intent(getActivity(), MessageSipUri.class);
        startActivityForResult(pickupIntent, PICKUP_SIP_URI);
    }

    private void sendMessage() {
    	System.out.println("sendMessage()");
    	 System.out.println("sendMessage() Serive:"+service);
    	
    	if (service != null) {
		 	System.out.println("service != null");
			SetSenderUriInfo.senderImage = false;
            SipProfile acc = accountChooserButton.getSelectedAccount();
            System.out.println("sendMessage():acc:"+acc);
            
            if (acc != null && acc.id != SipProfile.INVALID_ID) {
                try {
                	System.out.println("acc != null && acc.id != SipProfile.INVALID_ID");
                	String textToSend = bodyInput.getText().toString();
                    System.out.println("MessageFragment:textToSend"+textToSend);                    
                    if(!TextUtils.isEmpty(textToSend)) {
                    	System.out.println("!TextUtils.isEmpty(textToSend)");
                    	System.out.println("remoteFrom:"+remoteFrom);
                    	System.out.println("(int) acc.id:"+(int) acc.id);
                    	System.out.println("sendMessage():acc"+acc);                    	  
                    	service.sendMessage(textToSend, remoteFrom, (int) acc.id);
                        bodyInput.getText().clear();
                        forwardMessageTextData="";
                    }
                } catch (RemoteException e) {
                    Log.e(THIS_FILE, "Not able to send message");
                }
            }
        }
        
    	SetSenderUriInfo.senderImage = true;
        
    }
    
     public static void sendImage(String filename) {
        //	Toast.makeText(getActivity(), "valuecheck	"+ SetSenderUriInfo.onlineStatus, Toast.LENGTH_LONG).show();	
        	System.out.println("sendMessage()");
        	 System.out.println("sendMessage() Serive:"+service);
        	
        	if (service != null) {
    		 	System.out.println("service != null");
    			SetSenderUriInfo.senderImage = false;
                SipProfile acc = accountChooserButton.getSelectedAccount();
                System.out.println("sendMessage():acc:"+acc);
                
                if (acc != null && acc.id != SipProfile.INVALID_ID) {
                    try {
                    	System.out.println("acc != null && acc.id != SipProfile.INVALID_ID");
                    	String textToSend = multiMedia_msg + filename;
                        System.out.println("MessageFragment:textToSend"+textToSend);                    
                        System.out.println("remoteFrom--" + remoteFrom);
                        if(!TextUtils.isEmpty(textToSend)) {
                        	service.sendMessage(textToSend, remoteFrom, (int) acc.id);
                        }
                    } catch (RemoteException e) {
                        Log.e(THIS_FILE, "Not able to send message");
                    }
                }
            }
            
        	SetSenderUriInfo.senderImage = true;
            
        }
     
     public static void sendShareImage(String message , ISipService iSipService , String usernumber) {

		System.out.println("$$$$ messageforwordText:$$$$" + message);
		if(MessageFragment.service == null){
			MessageFragment.service = iSipService ;
		}

		if (MessageFragment.service != null) {
			System.out.println("service != null");
			SetSenderUriInfo.senderImage = false;
			try {
				String textToSend = message;
				System.out.println("MessageFragment:textToSend" + textToSend);
				if (!TextUtils.isEmpty(textToSend)) {
					service.sendMessage(textToSend, usernumber, 1);
					forwardMessageTextData = "";

				}
			} catch (RemoteException e) {
				Log.e(THIS_FILE, "Not able to send message");
			}
			
	}

         }
        

    public void hideSoftKeyboard() {
        if(getActivity().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
    
    private void emoticon_holder(int flag) {
    	Log.setLogLevel(6);
    	
 //   	InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    	
		if (flag == 0 ) {
			
			if (Fl_Emoticon_Holder.getVisibility() == FrameLayout.GONE) {
				hideSoftKeyboard();
				ib_enable_emoticon_frame.setImageResource(R.drawable.keyboard);
				Fl_Emoticon_Holder.setVisibility(FrameLayout.VISIBLE);
			} else {
				ib_enable_emoticon_frame.setImageResource(R.drawable.smily_icon);
				Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
				showSoftKeyboard(bodyInput);
			}
		
			/*
			if (MessageFragment.keyboard_flag == true ) {
				if (Fl_Emoticon_Holder.getVisibility() != 0) {
					hideSoftKeyboard();
					ib_enable_emoticon_frame.setImageResource(R.drawable.keyboard);
					Fl_Emoticon_Holder.setVisibility(FrameLayout.VISIBLE);
				} else {
					ib_enable_emoticon_frame.setImageResource(R.drawable.smily_icon);
					Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
					showSoftKeyboard(bodyInput);
				}
				MessageFragment.keyboard_flag = true;
			} else {
				hideSoftKeyboard();
				if (Fl_Emoticon_Holder.getVisibility() != 0) {
					ib_enable_emoticon_frame.setImageResource(R.drawable.arrow_down);
					Fl_Emoticon_Holder.setVisibility(FrameLayout.VISIBLE);
				} else {
					ib_enable_emoticon_frame.setImageResource(R.drawable.smily_icon);
					Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
				}
			}
			
			*/
		} else if (flag == 1 ) {
	
			MessageFragment.keyboard_flag = true;
			ib_enable_emoticon_frame.setImageResource(R.drawable.smily_icon);
			if (Fl_Emoticon_Holder.getVisibility() == 0) {
				Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
			}
		}
   	}
    
  
    
    @Override
    public void onClick(View v) {
        int clickedId = v.getId();
        if (clickedId == R.id.subject) {
        	MessageFragment.keyboard_flag = false;
            chooseSipUri();
        } else if (clickedId == R.id.send_button) {
        	String r = MessageActivity.usernum + "-" + stripNumber(prefs.getString(stored_chatuserNumber, "")) + "-nt" ;
        	if(ChatSocket.socket != null){
        		ChatSocket.ps.print(r);
        	}
        	Log.setLogLevel(6);
        	Log.d("ps send button ", r + " @");

        	sendMessage();
            MessageFragment.keyboard_flag = false;

        } else if(clickedId == R.id.ib_enable_emoticon_frame){
        	emoticon_holder(0);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    	System.out.println("MessageFragments.java inonCreateLoader() ");
        Builder toLoadUriBuilder = SipMessage.THREAD_ID_URI_BASE.buildUpon().appendEncodedPath(remoteFrom.replaceAll("/", "%2F"));
        Log.d("toLoadUriBuilder", toLoadUriBuilder.toString() + " @");
        return new CursorLoader(getActivity(), toLoadUriBuilder.build(), null, null, null,
                SipMessage.FIELD_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
 
    
    // Context menu
    public static final int MENU_COPY = ContextMenu.FIRST;
    public static final int FORWORD = ContextMenu.FIRST+1;
    public static final int DELETE = ContextMenu.FIRST+2;
    
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
    	System.out.print("test");
    		menu.add(0, MENU_COPY, 0, R.string.copy_message_text);
    	
        menu.add(0, FORWORD, 1, R.string.forward);
        menu.add(0, DELETE, 1, R.string.delete_message);
    }
    public int x=0;
    
    
    
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {
    	System.out.print("test1");
    	System.out.println("MessageFragments.java inonContextItemSelected() ");
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        System.out.println("info:"+info);	
        Cursor c = (Cursor) mAdapter.getItem(info.position);
        if (c != null) {
           SipMessage msg = new SipMessage(c);
           System.out.println("SipMessage:"+msg.getBody());
            
            switch (item.getItemId()) {
                case MENU_COPY: {
                	String message = msg.getBody();
                	if(msg.getBody().contains(" // Not Found")){
                	message = message.replace(" // Not Found", "");
                	}
                	clipboardManager.setText(msg.getDisplayName(), message);
                    
                	
                    break;
                }
			case FORWORD: {
							
					String forwardMessage =msg.getBody();

					if(isMultiMediaMsg(msg.getBody())){
                		Toast.makeText(getActivity(), "Cannot forward Media", Toast.LENGTH_SHORT).show();
                		break;
                	}
					
					System.out.println("forwardMessage:"+forwardMessage);
					Intent intent= new Intent(getActivity(),MessageSipUri.class);
					FORWARD_MSG = forwardMessage;
					bforwardMessage=true;
					forwardMessageTextData=forwardMessage;
					startActivity(intent);
					break;
			 }
			
			case DELETE: {
						    getActivity().getContentResolver().delete(SipMessage.MESSAGE_URI, "body=? AND date=?" , new String[]{msg.getBody() , String.valueOf(msg.getDate())});
			}
			
                default:
                    break;
            }

        }
        return super.onContextItemSelected(item);
    }
    
    private boolean isMultiMediaMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multiMedia_msg))
    		return true;
    	else
    		return false;
	}
    
    private void viewChatMenuPopup(View v,String from) {
    	final String fromNumber=from;
    	final String[] menuItem;
    	PopupWindow slideShowOptionsPopup=null;
		View menu_layout = getActivity().getLayoutInflater().inflate(R.layout.menu_list, null);
		menu_layout.setVisibility(View.VISIBLE);
		if(dbContacts == null){
			dbContacts = new DBContacts(getActivity());
		}
		dbContacts.openToRead();
		Cursor cursor = dbContacts.fetch_contact_from_R4W(stripNumber(chatNumber));
		if(cursor.getCount() > 0){
			menuItem = getActivity().getResources().getStringArray(R.array.ChatMenuSaved);
		}else{
			menuItem = getActivity().getResources().getStringArray(R.array.ChatMenu);
		}
		ItemAdapter adapter = new ItemAdapter(getActivity(),R.layout.menu_list_item, menuItem);
		((ListView) menu_layout).setAdapter(adapter);

		((ListView) menu_layout).setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id) {
						// TODO Auto-generated method stub
						Log.d("slideShowOptionsPopup  item position", position+ "");
						if(menuItem[position].equals("Add Contact") ){
							Intent intent = new Intent(Intent.ACTION_INSERT);
							intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
							startActivity(intent);
							
						}else if(menuItem[position].equals("Call") ){
						 	Toast.makeText(getActivity(), "Call in progress", Toast.LENGTH_LONG).show();
					   		RContactlist rContactlist= new RContactlist();
							rContactlist.r4wCall_Chat(chatUserNumber(fromNumber));
							
						}else if(menuItem[position].equals("Clear conversation")){
							confirmDeleteThread(fromNumber);
						}
							
						
						hideWindow();
					}
				});

		slideShowOptionsPopup = new PopupWindow(menu_layout, 350,ViewGroup.LayoutParams.WRAP_CONTENT, true);
		selectWindow = slideShowOptionsPopup;
		commonPopupWindowDisplay(slideShowOptionsPopup, v, -40,-40);
	}
    
    
    private void viewAttechmentPopup(View v,String from) {
    	final String fromNumber=from;
    	PopupWindow attechmentOptionsPopup=null;
		View menu_layout = getActivity().getLayoutInflater().inflate(R.layout.attechmentlayout, null);
		menu_layout.setVisibility(View.VISIBLE);
		LinearLayout linLayGallery= (LinearLayout) menu_layout.findViewById(R.id.linLayGallery);
		LinearLayout linLayPhoto= (LinearLayout) menu_layout.findViewById(R.id.linLayPhoto);
		LinearLayout linLayVideo= (LinearLayout) menu_layout.findViewById(R.id.linLayVideo);
		LinearLayout linLayAudio= (LinearLayout) menu_layout.findViewById(R.id.linLayAudio);
		LinearLayout linLayLocation= (LinearLayout) menu_layout.findViewById(R.id.linLayLocation);
		LinearLayout linLayContact= (LinearLayout) menu_layout.findViewById(R.id.linLayContact);
		
		linLayGallery.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(),"Gallery",Toast.LENGTH_SHORT).show();
			}
		});
		linLayPhoto.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		linLayVideo.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		
		linLayAudio.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			});
		
		linLayLocation.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	
		linLayContact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});
				
		

		attechmentOptionsPopup = new PopupWindow(menu_layout, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT, true);
		selectWindow = attechmentOptionsPopup;
		commonPopupWindowDisplay(attechmentOptionsPopup, v, 0, 15);
	}
    
    
    class ItemAdapter extends ArrayAdapter<String> {

		public ItemAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
    
    class AdapterAttechment extends ArrayAdapter<String> {

		public AdapterAttechment(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View viewToReturn;
			TextView txtView;

			if (convertView != null) {
				viewToReturn = convertView;
			} else {
				viewToReturn = mInflater.inflate(R.layout.attechment_menu, null);
			}

			txtView = (TextView) viewToReturn.findViewById(R.id.itemName);
			txtView.setText(this.getItem(position));
			txtView.setSelected(true);

			return viewToReturn;
		}

	}
    
    
    private void commonPopupWindowDisplay(PopupWindow popupWindow,View tabMenu, int x, int y) {
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setIgnoreCheekPress();
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(tabMenu, x, y);

	}
    
    private void hideWindow() {
		if (selectWindow != null){
			selectWindow.dismiss();
		}
			

	}
    
    private void confirmDeleteThread(final String from) {
    	
    	System.out.println("Delete UserChat:"+from);
    	
    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    	// set dialog message
			alertDialogBuilder
				.setMessage("Are you sure you wish to clear All messages in this conversation?")
				.setCancelable(false)
				
				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				})
			.setPositiveButton("ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
						Builder threadUriBuilder = SipMessage.THREAD_ID_URI_BASE.buildUpon();
						System.out.print("threadUriBuilder:"+threadUriBuilder);
					    threadUriBuilder.appendEncodedPath(from);
					    getActivity().getContentResolver().delete(threadUriBuilder.build(), null, null);
					
					
				}
			  });
 
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}
    
    public String chatUserNumber(String sipUri){
    	String CallingNumber=sipUri.replace("sip:","");
		String[] sCallingNuber = CallingNumber.split("@");
		System.out.println("CallingNumber"+sCallingNuber[0]);
    	return sCallingNuber[0];
    }
    
    public static void messageforwardText(String message , String number)
    {
    	System.out.println("$$$$ messageforwordText:$$$$"+message);
    	
    	if (service != null) {
		 	System.out.println("service != null");
			SetSenderUriInfo.senderImage = false;
            SipProfile acc = accountChooserButton.getSelectedAccount();
            System.out.println("sendMessage():acc"+acc);
            
            
                try {
                	System.out.println("acc != null && acc.id != SipProfile.INVALID_ID");
                	String textToSend = message;
                    System.out.println("MessageFragment:textToSend"+textToSend);                    
                    if(!TextUtils.isEmpty(textToSend)) {
                    	System.out.println("!TextUtils.isEmpty(textToSend)");
                    	System.out.println("remoteFrom:"+number);
                    	System.out.println("(int) acc.id:"+(int) acc.id);
                    	System.out.println("sendMessage():acc"+acc);                    	  
                    	service.sendMessage(textToSend, number, 1);
                        forwardMessageTextData="";
                      
                    }
                } catch (RemoteException e) {
                    Log.e(THIS_FILE, "Not able to send message");
                }
            
        }  
}
    
   
  
}
    
  
