package com.roamprocess1.roaming4world.ui;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.ISipService;
import com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;
import com.roamprocess1.roaming4world.roaming4world.R4WChatUserImage;
import com.roamprocess1.roaming4world.service.SipService;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.utils.CallHandlerPlugin;
import com.roamprocess1.roaming4world.utils.Compatibility;
import com.roamprocess1.roaming4world.utils.DialingFeedback;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.PreferencesWrapper;
import com.roamprocess1.roaming4world.utils.CallHandlerPlugin.OnLoadListener;
import com.roamprocess1.roaming4world.widgets.DialerCallBar.OnDialActionListener;

public class R4wFriendsProfile extends SherlockFragmentActivity implements OnDialActionListener{

	private static String TAG="R4wFriendsProfile",r4wCallingNumber,r4wCallingName, r4wCallingStatus, r4wCallingUri;
	public static  ImageButton btnr4wfreecall,btnfreeMessage,btnR4WFreeVideo;
	private TextView txtR4WFrdNumber,txtR4WFrdStatus,txtR4WFrdName,txtphNumber;
	ImageView userPic;
	DBContacts dbContacts;
	private Drawable pic;
	private String stored_user_bal, stored_min_call_credit, stored_chatuserNumber , stored_server_ipaddress;
	private SharedPreferences prefs;
	private Typeface robotoRegular,robotoBold;
	private LinearLayout linerOutCall;
	private PreferencesWrapper prefsWrapper;
	
	Timer timer;
	TimerTask timerTask;
	
	boolean fromchat = false;
	
	//we are going to use a handler to be able to run in our TimerTask
	final Handler handler = new Handler();
	private ISipService service;
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			service = ISipService.Stub.asInterface(arg1);
			Log.d("R4wFriendsProfile service==", service + "");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			service = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences("com.roamprocess1.roaming4world",Context.MODE_PRIVATE);
		
		setContentView(R.layout.r4wfrdprofile);
		robotoRegular = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Regular.ttf");
		robotoBold = Typeface.createFromAsset(this.getAssets(),"fonts/Roboto-Bold.ttf");
		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";
		stored_min_call_credit = "com.roamprocess1.roaming4world.min_call_credit";
		stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
  		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";

		Initialize();		
		Bundle bundle = getIntent().getExtras();
		   
	     if(bundle!=null){
	    	 r4wCallingNumber= bundle.getString("R4wCallingNumber");
	    	 r4wCallingName= bundle.getString("R4wCallingName");
	    	 Log.d(TAG,"r4wCallingNumber :r4wCallingName"+r4wCallingNumber+":"+r4wCallingName+"");
	    	 //r4wInviteNumber= InviteContact(r4wInviteNumber);
	    	 txtR4WFrdNumber.setText(r4wCallingNumber);
	    	// txtR4WFrdName.setText(r4wCallingName);
	    	 fromchat = bundle.getBoolean(MessageActivity.FROMCHAT); 
	    	 
	    
	     }
	   
	     fillValue();
	     setActionBar();
	     
	     
	 //    Intent serviceIntent = new Intent(SipManager.INTENT_SIP_SERVICE);
	//	 R4wFriendsProfile.this.bindService(serviceIntent, connection,Context.BIND_AUTO_CREATE);
		 btnr4wfreecall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			//	placeCall();
				
		//		startTimer();
		     	
	        	Toast.makeText(R4wFriendsProfile.this, "Call in progress", Toast.LENGTH_LONG).show();
				RContactlist rContactlist= new RContactlist();
				rContactlist.r4wCall_Chat(r4wCallingNumber);
				
			}
		});
	     
	    btnfreeMessage.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!fromchat){
					System.out.println("nnnnn -"+r4wCallingNumber+"-");
					prefs.edit().putString(stored_chatuserNumber, r4wCallingNumber).commit();
					callMessage(r4wCallingNumber);
				}
				finish();
			}
		});
	    
	    
	    btnR4WFreeVideo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if(isAppInstalled("com.roamprocess1.roaming4world.plugins.video")){
				 	Toast.makeText(R4wFriendsProfile.this, "Call in progress", Toast.LENGTH_LONG).show();
					RContactlist rContactlist= new RContactlist();
					rContactlist.r4wCall_Video(r4wCallingNumber);
				}else{
					dialogBoxVideoCall();
				}
				
			}
		});
	    
	    linerOutCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				String bal = prefs.getString(stored_user_bal, "0");
				String min = prefs.getString(stored_min_call_credit, "0");
		
				if(StaticValues.outCallPayment(bal, min)){
				 	Toast.makeText(R4wFriendsProfile.this, "Call in progress", Toast.LENGTH_LONG).show();
			   		r4wOutCall();
				}else{
					R4wInvite r4wInvite= new R4wInvite();
					r4wInvite.dialogboxR4WOut(R4wFriendsProfile.this);
				
				}				
			
			}
		}) ;
	    
	    
	  }
	

    private void dialogBoxVideoCall(){
		new AlertDialog.Builder(R4wFriendsProfile.this)
		.setTitle("Video Support")
		.setMessage("Video feature is not installed. Click proceed to download.")
		.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.roamprocess1.roaming4world.plugins.video"));
				startActivity(browserIntent);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		})
		.show();
	
	}
	
    private void r4wOutCall(){		
		String r4woutCallingNumber="";
		r4woutCallingNumber="011"+r4wCallingNumber;
		Log.d(TAG,"OutCalling number:"+r4woutCallingNumber);
		RContactlist rContactlist= new RContactlist();
		rContactlist.r4wCall_Chat(r4woutCallingNumber);
	}
    
	
	private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
           pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
           installed = true;
        } catch (PackageManager.NameNotFoundException e) {
           installed = false;
        }
        return installed;
    }
	
	public void callMessage(String numberget ) {
		// TODO Auto-generated method stub
		String fromFull = "<sip:"+ numberget+"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, "")) + ">";
		String number = "sip:"+ numberget +"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, ""));
        Bundle b = MessageFragment.getArguments(number, fromFull);
        
            Intent it = new Intent(R4wFriendsProfile.this, MessageActivity.class);
            it.putExtras(b);
            it.putExtra("call", true);
            startActivity(it);
    	       
	}
	
	private void Initialize() {
		// TODO Auto-generated method stub
		
		linerOutCall=(LinearLayout) findViewById(R.id.linerOutCall);
		btnr4wfreecall= (ImageButton)findViewById(R.id.btnR4wFreeCall);
		btnR4WFreeVideo= (ImageButton)findViewById(R.id.btnR4WFreeVideo);
		btnfreeMessage= (ImageButton)findViewById(R.id.btnR4WFreeMessage);
		txtR4WFrdNumber= (TextView)findViewById(R.id.txtfriendNumber);
		txtR4WFrdName= (TextView)findViewById(R.id.txtfriendName);
		txtR4WFrdStatus=(TextView) findViewById(R.id.txtFrdStatus);
		txtphNumber=(TextView) findViewById(R.id.phNumber);
		userPic = (ImageView) findViewById(R.id.imgFriendPic);
		txtR4WFrdName.setTypeface(robotoRegular);
		txtR4WFrdNumber.setTypeface(robotoRegular);
		
	}
	
	private void fillValue() {
		// TODO Auto-generated method stub
		File imageDirectoryprofile = null;
		Log.d("fillvalue", "in");
		Bitmap bmp;
		if(r4wCallingNumber.contains("@")){
			String[] n = r4wCallingNumber.split("@");
			r4wCallingNumber = n[0];
			n = r4wCallingNumber.split(":");
			r4wCallingNumber = n[1];
		}
		
		dbContacts = new DBContacts(this);
		dbContacts.openToRead();
		Cursor cursor = dbContacts.fetch_contact_from_R4W(r4wCallingNumber);
		if (cursor.getCount() > 0) {
			try {

				String nameServer, nameContact;
				cursor.moveToFirst();
				r4wCallingStatus = cursor.getString(3).toString();
				r4wCallingUri = cursor.getString(4).toString();
				nameServer = cursor.getString(5).toString();
				nameContact = cursor.getString(2).toString();
				cursor.close();
				dbContacts.close();
				if (nameServer.equals("***no name***")) {
					r4wCallingName = nameContact;
				} else {
					r4wCallingName = nameServer;
				}

				if (r4wCallingUri.equals("No image")) {
					pic = getResources().getDrawable(
							R.drawable.ic_contact_picture_180_holo_light);
				} else {

					String fileuri = "/sdcard/R4W/ProfilePic/" + r4wCallingNumber+ ".png";
					imageDirectoryprofile = new File(fileuri);
					if (imageDirectoryprofile.exists()) {
						Bitmap img = BitmapFactory.decodeFile(fileuri);
						pic = new BitmapDrawable(getResources(), img);
					}

				}
			
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {
			pic = getResources().getDrawable(
					R.drawable.ic_contact_picture_180_holo_light);

		}
		try{
			bmp = ((BitmapDrawable)pic).getBitmap();
			bmp = ImageHelperCircular.getRoundedCornerBitmap(bmp, bmp.getWidth());
		}catch(Exception e)
		{
			pic = getResources().getDrawable(R.drawable.ic_contact_picture_180_holo_light);
			bmp = ((BitmapDrawable)pic).getBitmap();
			if(imageDirectoryprofile != null){
				if(imageDirectoryprofile.exists()){
					imageDirectoryprofile.deleteOnExit();
				}
				
			}
			e.printStackTrace();
		}
		userPic.setImageBitmap(bmp);
		 if(r4wCallingName.length()>14)
			 txtR4WFrdName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		txtR4WFrdName.setText(r4wCallingName);
		
		String phNumber=number_value(r4wCallingNumber);
		txtR4WFrdNumber.setText(phNumberFormate(phNumber));
		txtR4WFrdStatus.setText(r4wCallingStatus);
		txtphNumber.setText(phNumberFormate(phNumber));
		
	}
	
	@SuppressWarnings("static-access")
	public void callImageActivity(View v) {
		
		String number = r4wCallingNumber;
		if(number.contains("@"))
		{
			String[] arr = number.split("@");
			number = arr[0];
			if(number.contains(":")){
				arr = number.split(":");
				number = arr[1];
			}
		}
		
		Log.d("imagenumber", number + " !");
		if(dbContacts == null){
			dbContacts = new DBContacts(this);
			
		}
		dbContacts.openToRead();
		Cursor cursor = dbContacts.fetch_contact_from_R4W_Dialer(number);
		
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			String uri = cursor.getString(cursor.getColumnIndex(dbContacts.R4W_CONTACT_URI));
			Log.d("imageuri", uri + " !");
			if(!uri.equals("No image")){
				
				Intent i = new Intent(this, R4WChatUserImage.class);
				i.putExtra("R4wCallingNumber",r4wCallingNumber);
				i.putExtra("R4wCallingName",r4wCallingName);
				startActivity(i);
		}else{
			Toast.makeText(R4wFriendsProfile.this, "No image", Toast.LENGTH_SHORT).show();
		}
		}
		cursor.close();
		dbContacts.close();
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Compatibility.getHomeMenuId()) {
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
	
	
	private String number_value(String num)
	{
		if(num.contains("@"))
		{
			String[] temp = num.split("@");
			temp = temp[0].split(":");
			num = temp[1];
		}
		return num;
	}
	
	@SuppressLint("NewApi")
	private void setActionBar() {
		// TODO Auto-generated method stub
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	    ab.setHomeButtonEnabled(true);
	    ab.setDisplayShowHomeEnabled(false);
	    ab.setDisplayShowTitleEnabled(false);
		ab.setCustomView(R.layout.r4wactionbarcustom);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		/*
		ImageButton imgBtnBack=(ImageButton)ab.getCustomView().findViewById(R.id.imgBackBtn);
		imgBtnBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				R4wFriendsProfile.this.finish();
			}
		});
		*/
		LinearLayout fin = (LinearLayout) ab.getCustomView().findViewById(R.id.ll_header_finish);
		fin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
	}
	
	public String phNumberFormate(String phNo)
	{
		String[] countryCode = getResources().getStringArray(R.array.CountryCodesWithname);
		String[] contry={};
		String finalCountryCode="";
		for(int i=0;i<countryCode.length;i++){
			contry=countryCode[i].split(",");
			if(phNo.startsWith(contry[1])){
				finalCountryCode=contry[1];
			}
			
		}
		
		int j=0;
		String fNumber=" (+"+finalCountryCode+")";
		for(int i=finalCountryCode.length();i<phNo.length();i++)
		{
			System.out.println("String ["+i+"]"+phNo.charAt(i));
			fNumber=fNumber+phNo.charAt(i);
			if(j==4)
				fNumber=fNumber+"-";
			j++;
		}
		return fNumber;
	}
	
	public void startTimer() {
		btnr4wfreecall.setEnabled(false);
		//set a new Timer
		timer = new Timer();
		//initialize the TimerTask's job
		initializeTimerTask();
		//schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
		timer.schedule(timerTask, 500, 10000); //
	}

	public void stoptimertask(View v) {
		//stop the timer, if it's not already null
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	public void initializeTimerTask() {
	timerTask = new TimerTask() {
			public void run() {
				
				//use a handler to run a toast that shows the current timestamp
				handler.post(new Runnable() {
					public void run() {
						btnr4wfreecall.setEnabled(true);
						
					}
				});
			}
		};
	}


	@Override
	public void placeCall() {
		// TODO Auto-generated method stub
		placeCallWithOption(null);
	}


	@Override
	public void placeVideoCall() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteChar() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}
	
	private void placeCallWithOption(Bundle b) {
		if (service == null) {			
			System.out.println("service == null");
			return;
		}
		System.out.println("service value :"+service);
		System.out.println(" inside placeCallWithOption  calling number="+ r4wCallingNumber);
		String toCall = "";
		Long accountToUse = (long) 1;
		toCall = PhoneNumberUtils.stripSeparators(r4wCallingNumber.toString());
		if (TextUtils.isEmpty(toCall)) {
			return;
		}
		if (accountToUse >= 0) {
			// It is a SIP account, try to call service for that
			try {
				Log.d(TAG,"placeCallWithOption -> Service : accountToUse : aaccountToUse.intValue() ->"+ service + " : " + accountToUse + ":"+ accountToUse.intValue() + "");
				service.makeCallWithOptions(toCall, accountToUse.intValue(), b);
			} catch (RemoteException e) {
				Log.e(TAG, "Service can't be called to make the call");
			}
		} else if (accountToUse != SipProfile.INVALID_ID) {
			// It's an external account, find correct external account
			Log.d(TAG,"accountToUse>= 0 : accountToUse : aaccountToUse.intValue() : -> "+ accountToUse + ":" + accountToUse.intValue());
			CallHandlerPlugin ch = new CallHandlerPlugin(this);
			ch.loadFrom(accountToUse, toCall, new OnLoadListener() {
				@Override
				public void onLoad(CallHandlerPlugin ch) {
					placePluginCall(ch);
				}
			});
		}
	}
	
	private void placePluginCall(CallHandlerPlugin ch) {
		try {
			String nextExclude = ch.getNextExcludeTelNumber();
			if (service != null && nextExclude != null) {
				try {
					service.ignoreNextOutgoingCallFor(nextExclude);
				} catch (RemoteException e) {
					//Log.e(this, "Impossible to ignore next outgoing call",e);
				}
			}
			ch.getIntent().send();
		} catch (CanceledException e) {
			//Log.e(THIS_FILE, "Pending intent cancelled", e);
		}
	}
}
		