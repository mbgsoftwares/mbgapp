package com.roamprocess1.roaming4world.ui;

import java.net.URISyntaxException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.ISipService;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.contactlist.ContactItemInterface;
import com.roamprocess1.roaming4world.contactlist.ExampleContactAdapter;
import com.roamprocess1.roaming4world.contactlist.ExampleDataSource;
import com.roamprocess1.roaming4world.service.SipService;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;


public class ImageFileSharingHandler extends SherlockFragmentActivity{

	MessageActivity messageActivity;
	ListView listView;
	Intent intent;
	List<ContactItemInterface> contactList;
	ExampleContactAdapter adapter;
	String usernumber = "";
	private String stored_user_mobile_no, stored_user_country_code , stored_server_ipaddress;
	private SharedPreferences prefs;
	String userNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imagefilesharinghandler);
		 
		prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
	    stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
	    stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";
		setActionBar();
		bindService(new Intent(ImageFileSharingHandler.this, SipService.class), connection, Context.BIND_AUTO_CREATE);
		intent = getIntent();
		
		listView = (ListView) findViewById(R.id.lv_imagefilesharing);
		attachAdapter();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view,
		          int position, long id) {
		    	  TextView txtr4wCallNumber = (TextView) view.findViewById(R.id.txtr4wNumber);
		    	  usernumber = txtr4wCallNumber.getText().toString();
		    	  Log.d("usernumber", usernumber + " !");
		    	  
		    	  if (intent.getType().indexOf("image/") != -1) {
		  	    	  messageActivity = new MessageActivity();
		  	    	  imageSharing(intent);
		  	    	} 
		    	  finish();
		   }

		    });
		
		
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

	private void attachAdapter() {
		// TODO Auto-generated method stub
		contactList = ExampleDataSource.getSampleContactList(ImageFileSharingHandler.this);
		adapter = new ExampleContactAdapter(ImageFileSharingHandler.this, R.layout.testexample_contact_item, contactList);
		listView.setFastScrollEnabled(true);
		listView.setAdapter(adapter);
	}

	private void setActionBar() {
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		ab.setDisplayShowHomeEnabled(false);
		ab.setDisplayShowTitleEnabled(false);
		ab.setCustomView(R.layout.r4wactionbarcustom);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

		RelativeLayout relativeLayout = (RelativeLayout) ab.getCustomView()
				.findViewById(R.id.relativeLayout);
		relativeLayout.setBackgroundColor(Color.parseColor("#F05A2B"));

		LinearLayout fin = (LinearLayout) ab.getCustomView().findViewById(
				R.id.ll_header_finish);
		fin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	public void imageSharing(Intent data){

        // Get the Uri of the selected file 
		Log.d( "File data: " , data.toString() + " #");
        
		Uri uri = (Uri) data.getParcelableExtra(Intent.EXTRA_STREAM);

        Log.d( "File Uri: " , uri+ " #");
        if(uri != null){
        // Get the path
        String path = null;
		try {
			path = messageActivity.getPath(this, uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Log.d("File Path: " , path + " #");
        if(path != null){
         	 userNum = prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "");
			 String fileName = System.currentTimeMillis() + messageActivity.getFileFormat(path);
     	  	 String savefileuri = messageActivity.saveImage(path, fileName, usernumber);
     	  	 String message = "IMG-" + userNum + "-" + fileName ;
    	  	 sendInitmsg(message, usernumber);
      	  	 new AsyncTaskUploadFile(savefileuri, message).execute();
  	    }
        
        }
     }

	private void sendInitmsg(String msg , String numb) {
		// TODO Auto-generated method stub
		if (!numb.contains("@")) {
			numb = "sip:" + numb +"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, ""));
		}
		msg = MessageFragment.multiMedia_msg +  msg + "-" + MessageActivity.MULTIMEDIA_MSG_INIT;
		
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
	class AsyncTaskUploadFile extends AsyncTask<Void, Void, Boolean> {

		String imagePathUri, filename;
		AsyncTaskUploadFile(String uriImage, String fileName){
			imagePathUri = uriImage;
			filename = fileName;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d("onPreExecute", "onPreExecute -in");
		}
	   
		
	

	@SuppressLint("NewApi") @Override
	protected void onPostExecute(Boolean result) {
		String msg = MessageFragment.multiMedia_msg +  filename + "-" + MessageActivity.MULTIMEDIA_MSG_INIT;
	    getContentResolver().delete(SipMessage.MESSAGE_URI, "body=?" , new String[]{msg});
	    String numb = usernumber;
		if (!numb .contains("@")) {
			numb = "sip:" + numb +"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, ""));
		}
		MessageFragment.sendShareImage(MessageFragment.multiMedia_msg +  filename, service , numb);
	
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		int response = 0;
		Log.d("doInBackgroud ", imagePathUri + " path ");
		if(imagePathUri.equals(""))
		{	return false;
		}else{
			response = messageActivity.uploadFile(imagePathUri, filename);
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
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unbindService(connection);
	}

	
	
}
