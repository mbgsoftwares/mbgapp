/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.roamprocess1.roaming4world.ui.messages;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.roaming4world.MessageCopyHandler;
import com.roamprocess1.roaming4world.roaming4world.R4WAboutUs;
import com.roamprocess1.roaming4world.service.ChatSocket;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.ui.R4wFriendsProfile;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment.OnQuitListener;
import com.roamprocess1.roaming4world.utils.Compatibility;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class MessageActivity extends SherlockFragmentActivity implements
		OnQuitListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
		EmojiconGridFragment.OnEmojiconClickedListener {

	private SharedPreferences prefs;
	String stored_chatuserNumber, stored_user_mobile_no,
			stored_user_country_code;
	MessageFragment detailFragment;
	EditText bodyInput;
	View vv;
	private FrameLayout Fl_Emoticon_Holder;
	ImageButton btn_emoticon_show;
	TextView tv_userStatus, name;
	Button btn_send_button;

	static String usernum;
	int first_timestamp = 0, second_timestamp = 0;
	String typing_status = "", stored_supportnumber, supportnum , stored_server_ipaddress;
	private Thread thread;
	private boolean flag = true;
	ImageView pic;

	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	int serverResponseCode = 0;
	private static final int FILE_SELECT_CODE = 0;
	Button btn_filetransfer;

	private PopupWindow selectWindow = null;
	DBContacts dbContacts;
	private static final int CAMERA_REQUEST = 1888;
	private static final int REQUEST_CONTACT_NUMBER = 1234;
	private static final int VIDEO_REQUEST = 1235;
	private static final int AUDIO_REQUEST = 1236;
	public static String FROMCHAT = "fromchat";

	private BroadcastReceiver broadcastReceiver_socket_userStatus , broadcastReceiver_lost ;
	int i = 0;
	LocationManager locationManager;
	
	String multimediaMsg = "R4WIMGTOCONTACTCHATSEND@@";
	public static String MULTIMEDIA_MSG_INIT = "MULTIMEDIA_MSG_INIT";
	int FILE_SIZE_ERROR = 30;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefs = getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_supportnumber = "com.roamprocess1.roaming4world.support_no";
   		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";

		usernum = prefs.getString(stored_user_country_code, "")
				+ prefs.getString(stored_user_mobile_no, "");
		
		

		
		System.out
				.println("MessageActivity.java before if statement of savedInstanceState ");
		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.
			System.out
					.println("MessageActivity.java after if statement of savedInstanceState ");
			detailFragment = new MessageFragment();
			detailFragment.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, detailFragment).commit();
			detailFragment.setOnQuitListener(this);
		}
		
		if(getConnectivityStatus(getApplicationContext()) == TYPE_NOT_CONNECTED) 
				dialogBoxNoInternet() ; 
		
		broadcastReceiver_socket_userStatus = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				 String action = intent.getAction();

				  Log.d("Receiver", "Broadcast received: " + action);

				  if(action.equals(ChatSocket.USER_STATUS)){
				     String state = intent.getExtras().getString(ChatSocket.USER_STATUS);
				     Log.d("Receiver state", state + " @");
				     setStatus(state);
				  }
				
			}
		};
		
		broadcastReceiver_lost = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d("broadcastReceiver_lost", "called");
				tv_userStatus.setText("");
			}
		};
		
		registerReceiver(broadcastReceiver_lost, new IntentFilter("INTERNET_LOST"));
		registerReceiver(broadcastReceiver_socket_userStatus, new IntentFilter(ChatSocket.USER_STATUS));

	}

	
	public String getStripNumber(String num){
		String nu = num;
		if (nu.contains("@")) {
			String[] r = nu.split("@");
			nu = r[0];
			r = nu.split(":");
			nu = r[1];
		}
		return nu;
	}

	protected void setStatus(String ee) {
		// TODO Auto-generated method stub
		if (ee.equals("er") || ee.equals("err") || ee.contains("connected")) {
			ee = "";
		}else if (ee.equals("Typing...")) {
		} else if (ee.equals("connected")) {
		} else if (ee.equals("Online")) {
		} else if(ee.matches("\\d+")){
			ee = "Last seen " + getDate(ee);
		}
		
		Log.d("setStatus ee", ee + " @");

		if (i == 0) {
			String po = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-st";
			if (ChatSocket.socket != null) {
				ChatSocket.ps.print(po);
			}

			Log.d("po after conneted", po + " @");

		} else if (i > 0 && !ee.equals("null") && !ee.equals("")) {
			tv_userStatus.setText(ee);
		}
		
		i++;

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

	private void dialogBoxNoInternet() {
		new AlertDialog.Builder(MessageActivity.this).setTitle("No Network")
				.setMessage("There is no internet connection")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();

	}

	public void cameraImage() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, CAMERA_REQUEST);
	}

	private void showFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					FILE_SELECT_CODE);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void commonPopupWindowDisplay(PopupWindow popupWindow,
			View tabMenu, int x, int y) {
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setIgnoreCheekPress();
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(tabMenu, x, y);

	}

	private void viewAttechmentPopup(View v) {

		PopupWindow attechmentOptionsPopup = null;
		View menu_layout = getLayoutInflater().inflate(
				R.layout.attechmentlayout, null);
		menu_layout.setVisibility(View.VISIBLE);
		LinearLayout linLayGallery = (LinearLayout) menu_layout
				.findViewById(R.id.linLayGallery);
		LinearLayout linLayPhoto = (LinearLayout) menu_layout
				.findViewById(R.id.linLayPhoto);
		LinearLayout linLayVideo = (LinearLayout) menu_layout
				.findViewById(R.id.linLayVideo);
		LinearLayout linLayAudio = (LinearLayout) menu_layout
				.findViewById(R.id.linLayAudio);
		LinearLayout linLayLocation = (LinearLayout) menu_layout
				.findViewById(R.id.linLayLocation);
		LinearLayout linLayContact = (LinearLayout) menu_layout
				.findViewById(R.id.linLayContact);

		linLayGallery.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Toast.makeText(MessageActivity.this,"coming soon",Toast.LENGTH_SHORT).show();
				selectWindow.dismiss();
				cameraImage();
			}
		});
		linLayPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectWindow.dismiss();
				showFileChooser();

			}
		});

		linLayVideo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectWindow.dismiss();
				showVideoFileChooser();
			}
		});

		linLayAudio.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectWindow.dismiss();
				showAudioFileChooser();
			}
		});

		linLayLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectWindow.dismiss();
				sendLocationMessage();
			}
		});

		linLayContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				selectWindow.dismiss();
				onBrowseForNumbersButtonClicked();
			}
		});

		attechmentOptionsPopup = new PopupWindow(menu_layout,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		selectWindow = attechmentOptionsPopup;
		commonPopupWindowDisplay(attechmentOptionsPopup, v, 0, 15);

	}

	private void showVideoFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");

		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					VIDEO_REQUEST);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void showAudioFileChooser() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);

		try {
			startActivityForResult(
					Intent.createChooser(intent, "Select a File to Upload"),
					AUDIO_REQUEST);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(this, "Please install a File Manager.",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void onBrowseForNumbersButtonClicked() {
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				Phone.CONTENT_URI);
		startActivityForResult(contactPickerIntent, REQUEST_CONTACT_NUMBER);
	}

	public void handlecontactBrowseData(Intent data) {
		Uri uriOfPhoneNumberRecord = data.getData();
		String idOfPhoneRecord = uriOfPhoneNumberRecord.getLastPathSegment();
		Cursor cursor = getContentResolver().query(Phone.CONTENT_URI,
				new String[] { Phone.NUMBER, Phone.DISPLAY_NAME },
				Phone._ID + "=?", new String[] { idOfPhoneRecord }, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				String formattedPhoneNumber = cursor.getString(cursor
						.getColumnIndex(Phone.NUMBER));
				Log.d("Contact Selected", formattedPhoneNumber + " &");
				String formattedName = cursor.getString(cursor
						.getColumnIndex(Phone.DISPLAY_NAME));
				Log.d("Contact Selected", formattedName + " &");
				String contact_msg = "CON-" + formattedName + "-"
						+ formattedPhoneNumber;
				MessageFragment.sendImage(contact_msg);
			}
			cursor.close();
		}

	}

	protected void sendLocationMessage() {
		// TODO Auto-generated method stub
		if (checkGPSEnabled()) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			if (location == null) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

			if (location == null) {
				try {
					if (locationManager
							.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					}
				} catch (Exception ex) {
				}
			}

			if (location != null) {
				String longitude = String.valueOf(location.getLongitude());
				String latitude = String.valueOf(location.getLatitude());
				String location_msg = "LOC-" + longitude + "-" + latitude;
				MessageFragment.sendImage(location_msg);
			} else {
				Toast.makeText(
						getApplicationContext(),
						"GPS is searching your coordinates. Please try again later",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			dialogBoxGPSDisabled();
		}
	}

	private boolean checkGPSEnabled() {
		// TODO Auto-generated method stub
		try {
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			return locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

		} catch (Exception ex) {
			return false;
		}
	}

	private void dialogBoxGPSDisabled() {
		new AlertDialog.Builder(MessageActivity.this)
				.setTitle("GPS Disabled")
				.setMessage(
						"GPS service is disabled . Please try again after enabling the GPS.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();

	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(broadcastReceiver_socket_userStatus);
		unregisterReceiver(broadcastReceiver_lost);
	}

	@Override
	protected void onStart() {
		super.onStart();
	 
		try{
		bodyInput.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					// TODO Auto-generated method stub
					typingcall();
					String msg = s.toString().trim();
					if(isMultiMediaMsg(msg)){
						bodyInput.getText().clear();
						Intent intent = new Intent(MessageActivity.this,MessageCopyHandler.class);
						intent.putExtra("message", msg);
				     	intent.putExtra("user_number", stripNumber(prefs.getString(stored_chatuserNumber, "")));
						startActivity(intent);
	}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void setActionBar(){
		try {
			com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setCustomView(R.layout.chatactionbar);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

			pic = (ImageView) actionBar.getCustomView().findViewById(
					R.id.ab_userpic);
			name = (TextView) actionBar.getCustomView().findViewById(
					R.id.ab_userName);
			LinearLayout backfrChat = (LinearLayout) actionBar.getCustomView()
					.findViewById(R.id.ll_backFromChat);
			tv_userStatus = (TextView) actionBar.getCustomView().findViewById(
					R.id.ab_userStatus);
			LinearLayout ll_userprofile = (LinearLayout) actionBar.getCustomView()
					.findViewById(R.id.ll_calluserProfile);

			btn_filetransfer = (Button) actionBar.getCustomView().findViewById(
					R.id.btnattechment);

			btn_filetransfer.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewAttechmentPopup(v);

				}
			});
			
			backfrChat.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					callBack();
				}
			});

			ll_userprofile.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					callUserProfileActivity();
				}
			});
			
			
				vv = detailFragment.getView();
				bodyInput = (EditText) vv.findViewById(R.id.embedded_text_editor);
				Fl_Emoticon_Holder = (FrameLayout) vv
						.findViewById(R.id.fl_emojicons);
				btn_emoticon_show = (ImageButton) vv
						.findViewById(R.id.ib_enable_emoticon_frame);

				String user_number = prefs.getString(stored_chatuserNumber,
						"No Value");
				if (user_number.contains("@")) {
					String[] nu = user_number.split("@");
					nu = nu[0].split(":");
					user_number = nu[1];
				}

				supportnum = prefs.getString(stored_supportnumber, "");
				String nu = detailFragment.stripNumber(user_number);
				String fileuri = Environment.getExternalStorageDirectory()
						+ "/R4W/ProfilePic/" + nu + ".png";
				Log.d("fileuri", fileuri + " !");
				Log.d("supportnum", supportnum);
				Log.d("nu", nu);

				if (nu.equals(supportnum)) {
					pic.setImageResource(R.drawable.roaminglogo);
				} else {
					File imageDirectoryprofile = new File(fileuri);
					if (imageDirectoryprofile.exists()) {
						pic.setImageURI(Uri.parse(fileuri));
					} else {
						pic.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
					}
				}

				String username = nu;

				if (dbContacts == null) {
					dbContacts = new DBContacts(MessageActivity.this);
				}
				String nameServer = "", nameContact = "";
				dbContacts.openToRead();
				Cursor cursor = dbContacts.fetch_contact_from_R4W(nu);
				if (cursor.getCount() > 0) {
					cursor.moveToFirst();
					nameServer = cursor.getString(5).toString();
					nameContact = cursor.getString(2).toString();
					cursor.close();
					dbContacts.close();

					Log.d("nameServer", nameServer + " in");
					Log.d("nameContact", nameContact + " in");

					if (!nameServer.equals("***no name***")) {
						username = nameServer;
					} else {
						username = nameContact;
					}
					name.setText(username);
				} else {
					name.setText(nu);
				}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void callUserProfileActivity() {
		// TODO Auto-generated method stub
		String num = detailFragment.stripNumber(prefs.getString(
				stored_chatuserNumber, ""));
		Log.d("supportnum", supportnum);
		Log.d("num", num);
		if (num.equals(supportnum)) {
			startActivity(new Intent(MessageActivity.this,
					R4WAboutUs.class));
		} else {
			callUserProfile();
		}
	
	}




	public boolean isMultiMediaMsg(String msg){
		Log.d("s.toString()", msg + " @");
		if(msg.startsWith(multimediaMsg)){
			String[] arr = msg.split("@@");
			msg = arr[1];
			String[] arre = msg.split("-");
			if(arre.length > 2){
				return true;
			}
		}
		
		return false;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case FILE_SELECT_CODE:
			if (resultCode == RESULT_OK) {
				imageSharing(data, "image");
			}
			break;
		case CAMERA_REQUEST:
			if (resultCode == RESULT_OK) {
				imageSharing(data, "image");
			}
			break;
		case VIDEO_REQUEST:
			if (resultCode == RESULT_OK) {
				imageSharing(data, "video");
			}
			break;
		case AUDIO_REQUEST:
			if (resultCode == RESULT_OK) {
				imageSharing(data, "audio");
			}
			break;
		case REQUEST_CONTACT_NUMBER:
			Log.d("REQUEST_CONTACT_NUMBER", "called");
			if (data != null) {
				handlecontactBrowseData(data);
			}
		}

	}

	public void imageSharing(Intent data, String type) {

		// Get the Uri of the selected file
		Uri uri = data.getData();

		Log.d("imageSharing - type", type + " @");

		Log.d("File Uri: ", uri.toString() + " #");
		// Get the path
		String path = null;
		try {
			path = getPath(this, uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("File Path: ", path + " #");
		if (path != null) {
			String userNum = prefs.getString(stored_user_country_code, "")
					+ prefs.getString(stored_user_mobile_no, "");
			String fileName = System.currentTimeMillis() + getFileFormat(path);
			String msg;
			if (type.equals("video")) {
				msg = "VID-" + userNum + "-" + fileName;
			} else if (type.equals("audio")) {
				msg = "AUD-" + userNum + "-" + fileName;
			} else {
				msg = "IMG-" + userNum + "-" + fileName;
			}

			String numb = prefs.getString(stored_chatuserNumber, "");
			Log.d("nnumb", numb + " #");
			
			String savefileuri = saveImage(path, fileName, stripNumber(numb));
			if (savefileuri.equals(FILE_SIZE_ERROR + "")) {
				Toast toast = Toast.makeText(getApplicationContext(),"you upload file size is exceed to 30 MB",Toast.LENGTH_SHORT); 
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			} else if (!savefileuri.equals("")) {
				Log.d("msg1", msg + " !");
				sendInitmsg(msg, numb);
				new AsyncTaskUploadFile(savefileuri, msg).execute();
			} else {
				Toast.makeText(getApplicationContext(), "File not found",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void sendInitmsg(String msg , String numb) {
		// TODO Auto-generated method stub
		if (!numb.contains("@")) {
			numb = "sip:" + numb +"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, ""));
		}
		
		msg = multimediaMsg +  msg + "-" + MULTIMEDIA_MSG_INIT;
		
		SipMessage sipmsg = new SipMessage(
				SipMessage.SELF,
				SipUri.getCanonicalSipContact(numb),
				null, 
				msg,
				"text/plain",
				System.currentTimeMillis(), 
				SipMessage.MESSAGE_TYPE_QUEUED,
				null,
				0);
		sipmsg.setRead(true);
		getContentResolver().insert(SipMessage.MESSAGE_URI, sipmsg.getContentValues());
		
		Log.d("numb ", numb + " #");
		
	}

	public String stripNumber(String nu) {
		if (nu.contains("@")) {
			String[] arr = nu.split("@");
			nu = arr[0];
			if (nu.contains(":")) {
				arr = nu.split(":");
				nu = arr[1];
			}
		}
		return nu;
	}

	public String saveImage(String path, String fileName, String userNum) {
		// TODO Auto-generated method stub
		File sourceFile = new File(path);
		
			
		if (!sourceFile.exists()) {
			Log.d("File", "Source File not copied");
			return "";
		} else {
			FileInputStream fileInputStream;
			double size = 0;
			try {
				fileInputStream = new FileInputStream(sourceFile);
				size = fileInputStream.available();
				fileInputStream.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			double fsize = FILE_SIZE_ERROR * 1024 * 1024;
			Log.d("size", size + " @");
			Log.d("fsize", fsize + " @");
			String fileuri = copyfile(fileName, userNum, sourceFile);
			Log.d("File", "Source File copied");
			
			
			
			if(size > fsize){
				return FILE_SIZE_ERROR + "";
			}else
			{
				return fileuri;
			}
		}

	}

	public String getFileFormat(String uriFile) {

		String[] formatOfFile = uriFile.split("\\.");
		return "." + formatOfFile[formatOfFile.length - 1];
	}

	public String copyfile(String fileName, String userNum, File src) {
		// TODO Auto-generated method stub
		String numb = userNum;
		Log.d("numb", numb);
		if (numb.contains(".")) {
			String[] arr = numb.split(".");
			numb = arr[0];
		}

		File imageDirector = new File(
				Environment.getExternalStorageDirectory(), "R4W");

		if (!imageDirector.exists()) {
			imageDirector.mkdir();
		}

		File imageDirectoryprofile = new File(imageDirector.getAbsolutePath(),
				"SharingImage");

		if (!imageDirectoryprofile.exists()) {
			imageDirectoryprofile.mkdir();
		}

		File imageDirectory = new File(imageDirectoryprofile.getAbsolutePath(),
				userNum);

		if (!imageDirectory.exists()) {
			imageDirectory.mkdir();
		}

		File imageDirectoryNum = new File(imageDirectory.getAbsolutePath(),
				"send");

		if (!imageDirectoryNum.exists()) {
			imageDirectoryNum.mkdir();
		}
		// File file = new File(imageDirectoryprofile.getAbsolutePath(),fileName
		// + ".png");
		File file = new File(imageDirectoryNum.getAbsolutePath(), fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(file);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();

			// file compression code
			/*
			 * String fileformat = getFileFormat(src.getAbsolutePath()); Bitmap
			 * bmp = BitmapFactory.decodeFile(file.getAbsolutePath()); int[]
			 * sizeOfImage = getBitmapSize(bmp); bmp =
			 * Bitmap.createScaledBitmap(bmp, sizeOfImage[0], sizeOfImage[1],
			 * false); File Imagefile = new
			 * File(imageDirectoryNum.getAbsolutePath(), fileName);
			 * FileOutputStream fOut; try { fOut = new
			 * FileOutputStream(Imagefile); if(fileformat.contains("png")){
			 * bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut); }else
			 * if(fileformat.contains("jpg")){
			 * bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); }
			 * fOut.flush(); fOut.close(); bmp.recycle();
			 * 
			 * } catch (Exception e) { // TODO
			 * 
			 * }
			 */
			// file compression code

			return file.getAbsolutePath();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return "";

	}

	public int[] getBitmapSize(Bitmap bmpi) {
		int length = bmpi.getHeight();
		int width = bmpi.getWidth();

		int[] size = new int[2];

		if (length > width) {
			if (length > 1024) {
				int per = (int) (1024 * 100 / length);
				size[1] = 1024;
				size[0] = (int) (width * (per / 100.0f));
			} else {
				size[0] = width;
				size[1] = length;
			}
		} else if (width > length) {
			if (width > 1024) {
				int per = (int) (1024 * 100 / width);
				size[0] = 1024;
				size[1] = (int) (length * (per / 100.0f));
			} else {
				size[0] = width;
				size[1] = length;
			}

		} else {
			if (width > 1024) {
				int per = (int) (1024 * 100 / width);
				size[0] = 1024;
				size[1] = (int) (length * (per / 100.0f));
			} else {
				size[0] = width;
				size[1] = length;
			}

		}
		return size;

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

	class AsyncTaskUploadFile extends AsyncTask<Void, Void, Boolean> {

		String imagePathUri, filename , msg;
//		ProgressDialog mProgressDialog;
		boolean filesize_flag = false;

		AsyncTaskUploadFile(String uriImage, String fileName) {
			imagePathUri = uriImage;
			filename = fileName;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
	}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean result) {
	//		mProgressDialog.dismiss();
			if (result == true) {
			}else {
				Toast.makeText(getApplicationContext(),"File is not uploaded ", Toast.LENGTH_LONG).show();
			}
			Log.d("msg3", filename + " !");
			
			msg = multimediaMsg +  filename + "-" + MULTIMEDIA_MSG_INIT;
		    getContentResolver().delete(SipMessage.MESSAGE_URI, "body=?" , new String[]{msg});
		    
			MessageFragment.sendImage(filename);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			int response = 0;
			Log.d("doInBackgroud ", imagePathUri + " path ");
			if (imagePathUri.equals("")) {
				return false;
			} else {
				response = uploadFile(imagePathUri, filename);
			}
			Log.d("response ", response + "  d");

			if (response == 200 ) {
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else if(response == FILE_SIZE_ERROR){
				filesize_flag = true;
				return false;
			}else {
				return false;
			}

		}
	}

	public int uploadFile(String sourceFileUri, String fileName) {

		String upLoadServerUri = "";
		upLoadServerUri = "http://ip.roaming4world.com/esstel/file-transfer/file_upload.php";

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
			conn = (HttpURLConnection) url.openConnection(); // Open a HTTP
																// connection to
																// the URL
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", sourceFileUri);

			dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
					+ multimediaMsg + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			bytesAvailable = fileInputStream.available(); // create a buffer of
															// maximum size
			
			
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

			Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage
					+ ": " + serverResponseCode);

			// close the streams //
			fileInputStream.close();
			dos.flush();
			dos.close();

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("Upload file to server Exception",
					"Exception : " + e.getMessage(), e);
		}
		return serverResponseCode;

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		btn_emoticon_show.setImageResource(R.drawable.smily_icon);

		String d = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-nt";
		if (ChatSocket.socket != null) {
			ChatSocket.ps.print(d);
		}
		Log.d("ps d on Backpressed", d + " @");

		MessageFragment.keyboard_flag = false;
		if (Fl_Emoticon_Holder.getVisibility() == 0) {
			Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
		} else {
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:

				String d = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-nt";
				if (ChatSocket.socket != null) {
					ChatSocket.ps.print(d);
				}

				Log.d("ps d onKeyDown", d + " @");

				MessageFragment.keyboard_flag = false;
				btn_emoticon_show.setImageResource(R.drawable.smily_icon);
				if (Fl_Emoticon_Holder.getVisibility() == 0) {
					Fl_Emoticon_Holder.setVisibility(FrameLayout.GONE);
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("SdCardPath")
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
		setActionBar();
		if (ChatSocket.socket != null) {
			String f = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-st";
			ChatSocket.ps.print(f);
			Log.d("ps f onResume ", f + " @");
		}
		
		
		// new MyAsyncTaskGetStatus(user_number).execute();
	}

	protected void typingcall() {
		// TODO Auto-generated method stub

		first_timestamp = (int) (System.currentTimeMillis());

		Log.d("first_timestamp", first_timestamp + " typingcall");

		if (thread == null) {
			thread = new SystemtypeCall();
			thread.start();

			String oo = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-ty";
			typing_status = "ty";
			if (ChatSocket.socket != null) {
				ChatSocket.ps.print(oo);
			}
			Log.d("ps.print(oo)	", oo + " @");
		}

		if (typing_status.equals("nt")) {
			String oo = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-ty";
			typing_status = "ty";
			if (ChatSocket.socket != null) {
				ChatSocket.ps.print(oo);
			}
			Log.d("ps.print(oo)	", oo + " @");

		}
	}

	class SystemtypeCall extends Thread {

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (flag) {
				try {
					Thread.currentThread().sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("SystemtypeCall after sleep", " SystemtypeCall");
				int current_timestamp = (int) (System.currentTimeMillis());

				Log.d("current_timestamp", first_timestamp + " SystemtypeCall");
				int diff = current_timestamp - first_timestamp;
				Log.d("diff", diff + " SystemtypeCall");
				if (diff > 3000) {
					String oo = usernum + "-" + getStripNumber(prefs.getString(stored_chatuserNumber, "")) + "-nt";
					if (!typing_status.equals("nt"))
						if (ChatSocket.socket != null) {
							ChatSocket.ps.print(oo);
							Log.d("oo	", oo + " @");
							typing_status = "nt";

						}
				}

			}
			if (!flag) {
				try {
					Thread.currentThread().sleep(999999999);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void interrupt() {
			// TODO Auto-generated method stub
			super.interrupt();
			try {
				Thread.currentThread().wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("Inside method ", "interrupt");
		}

		@Override
		public boolean isInterrupted() {
			// TODO Auto-generated method stub
			Log.d("Inside method ", "is interrupted");
			return super.isInterrupted();
		}
	}

	private String getDate(String timestamp) {

		String time = "";

		DateFormat format_value = new SimpleDateFormat("h:mm a");
		String str = format_value.format(Long.parseLong(timestamp) * 1000);

		Calendar calendar = Calendar.getInstance();
		TimeZone tz = TimeZone.getDefault();
		calendar.setTimeInMillis(Integer.parseInt(timestamp) * 1000L);
		calendar.add(Calendar.MILLISECOND,
				tz.getOffset(calendar.getTimeInMillis()));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dd = formatter.format(calendar.getTime());

		String datetrip[] = dd.split("-");
		String WeekdayIs = new SimpleDateFormat("EEE").format(calendar
				.getTime());
		String monthName = new SimpleDateFormat("MMM").format(calendar
				.getTime());

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
		String d1 = formatter1.format(cal.getTime());
		String datetrip1[] = d1.split("-");
		if (Integer.parseInt(datetrip1[0]) == Integer.parseInt(datetrip[0])) {

			if (Integer.parseInt(datetrip1[1]) == Integer.parseInt(datetrip[1])) {

				if (Integer.parseInt(datetrip1[2]) == Integer
						.parseInt(datetrip[2])) {
					time = "today, " + str;

				} else if (Integer.parseInt(datetrip1[2]) == Integer
						.parseInt(datetrip[2]) + 1) {

					time = "yesterday, " + str;

				} else {
					time = WeekdayIs + ", " + datetrip[2] + " " + monthName
							+ ", " + datetrip[0] + ", " + str.toString();

				}
			} else {
				time = WeekdayIs + ", " + datetrip[2] + " " + monthName + ", "
						+ datetrip[0] + ", " + str.toString();

			}
		} else {
			time = WeekdayIs + ", " + datetrip[2] + " " + monthName + ", "
					+ datetrip[0] + ", " + str.toString();

		}

		return time;
	}

	public void callBack() {
		finish();
	}

	public void callUserProfile() {
		Intent i = new Intent(this, R4wFriendsProfile.class);
		i.putExtra("R4wCallingNumber",
				prefs.getString(stored_chatuserNumber, "No Value"));
		i.putExtra("R4wCallingName", name.getText().toString());
		i.putExtra(FROMCHAT, true);
		startActivity(i);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Compatibility.getHomeMenuId()) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();

	}

	@Override
	public void onQuit() {
		finish();
	}

	@Override
	public void onEmojiconBackspaceClicked(View v) {
		// TODO Auto-generated method stub
		EmojiconsFragment.backspace(bodyInput);
	}

	@Override
	public void onEmojiconClicked(Emojicon emojicon) {
		// TODO Auto-generated method stub
		EmojiconsFragment.input(bodyInput, emojicon);
	}

	public class MyAsyncTaskGetStatus extends AsyncTask<Void, Void, Boolean> {

		String user_number, status = "error";

		MyAsyncTaskGetStatus(String number) {
			user_number = number;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			String st;

			Log.d("statnum", user_number);
			Log.d("statuss", status);

			if (status.equals("online")) {
				st = status;
			} else if (status.equals("no data")) {
				st = "";
			} else if (status.equals("error")) {
				st = "offline";
			} else {
				st = "Last seen  " + getDate(status);
			}
			// tv_userStatus.setText(st);
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqGetStatus()) {
				return true;
			} else {
				return false;
			}
		}

		public boolean webservreqGetStatus() {

			try {
				HttpParams p = new BasicHttpParams();
				p.setParameter("user", "1");
				HttpClient httpclient = new DefaultHttpClient(p);
				// http://ip.roaming4world.com/esstel/balance-info/balance.php?contact=9132

				String url = "http://ip.roaming4world.com/esstel/user-status/user_presence_status.php?"
						+ "contact=" + user_number;

				Log.d("staturl", url);
				try {

					HttpGet httpget = new HttpGet(url);
					ResponseHandler<String> responseHandler;
					String responseBody;
					responseHandler = new BasicResponseHandler();
					responseBody = httpclient.execute(httpget, responseHandler);
					// Instantiate a GET HTTP method
					JSONObject json = new JSONObject(responseBody);
					System.out.println("JSON response:balance" + json);
					status = json.getString("status");
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
	}
}
