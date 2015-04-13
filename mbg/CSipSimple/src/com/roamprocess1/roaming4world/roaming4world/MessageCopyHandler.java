package com.roamprocess1.roaming4world.roaming4world;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.utils.Compatibility;



public class MessageCopyHandler extends FragmentActivity implements OnClickListener{
	
	Button send , cancel;
	ImageView iv_copiedImage;
	private SharedPreferences prefs;
	public String stored_chatuserName , imgpath , userNum , stored_server_ipaddress;
	String message;
	String path = Environment.getExternalStorageDirectory()+ "/R4W/SharingImage/";
	String multimediaMsg = "R4WIMGTOCONTACTCHATSEND@@";
	public static String MULTIMEDIA_MSG_INIT = "MULTIMEDIA_MSG_INIT";
	int serverResponseCode = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.messagecopyhandler);
		
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		init();
		setOnClick();
		setImage();
	}

	private void setImage() {
		// TODO Auto-generated method stub
		if(getIntent() != null){
			message = getIntent().getStringExtra("message");
			userNum = getIntent().getStringExtra("user_number");
			
			Log.d("message", message + " !");
			Log.d("userNum", userNum + " !");
		
			
			setimagePath(message , userNum);
		}
	}

	@SuppressLint("NewApi") 
	private void setimagePath(String imageuri, String user_num) {
		// TODO Auto-generated method stub
	
		Log.d("imageuri", imageuri + " @");
		if (imageuri != null) {
			try{
				String[] arre = imageuri.toString().split("@@");
				String[] arr = arre[1].trim().split("-");
				String recuri = path + stripNumber(user_num) + "/Recieved/" +arr[2];
				String senduri = path + stripNumber(user_num) + "/send/" +arr[2];
				
				Log.d("recuri", recuri + " !");
				Log.d("senduri", senduri + " !");
				
				
				imgpath = "";
				File fi = new File(recuri);
				if (fi.exists()) {
					Log.d("recuri", "File");
					imgpath = recuri;
				} else {
					File file = new File(senduri);
					if (file.exists()) {
						Log.d("senduri", "File");
						imgpath = senduri;
					}
				}
				if (!imgpath.equals("")) {
					Drawable b = BitmapDrawable.createFromPath(imgpath);
					iv_copiedImage.setBackground(b);
				}else{
					iv_copiedImage.setImageResource(R.drawable.logo);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
	

	private void setOnClick() {
		// TODO Auto-generated method stub
		send.setOnClickListener(this);
		cancel.setOnClickListener(this);
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
	
	private void init() {
		// TODO Auto-generated method stub
	    prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
        stored_chatuserName = "com.roamprocess1.roaming4world.stored_chatuserName";
		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";

		
		iv_copiedImage = (ImageView) findViewById(R.id.iv_messagecopyhandler);
		send = (Button) findViewById(R.id.bt_messageCopyhandler_Send);
		cancel = (Button) findViewById(R.id.bt_messageCopyhandler_cancel);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == Compatibility.getHomeMenuId()) {
			finish();
		return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v.getId() == R.id.bt_messageCopyhandler_Send){
			Log.d("message", message);
			String[] ar = message.split("@@");
			sendInitmsg(ar[1], stripNumber(userNum));
			new AsyncTaskUploadImage(imgpath, ar[1]).execute();
			finish();
		}else{
			finish();
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
	
	class AsyncTaskUploadImage extends AsyncTask<Void, Void, Boolean> {


		String imagePathUri, filename , msg;
//		ProgressDialog mProgressDialog;

		AsyncTaskUploadImage(String uriImage, String fileName) {
			imagePathUri = uriImage;
			filename = fileName;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d("onPreExecute", "onPreExecute -in");
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean result) {
	//		mProgressDialog.dismiss();
			if (result == true) {
				
				Toast.makeText(getApplicationContext(),
						"File Upload Completed", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"File is not uploaded ", Toast.LENGTH_LONG).show();
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

			if (response == 200) {
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else {
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
	

}
