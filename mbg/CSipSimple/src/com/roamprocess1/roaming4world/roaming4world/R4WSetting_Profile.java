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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.ui.SipHome;
import com.roamprocess1.roaming4world.utils.Compatibility;



public class R4WSetting_Profile extends FragmentActivity implements View.OnClickListener{

	
	
	
	LinearLayout lv_showUserImage, lv_editUserName, lv_editUserProfileImage ;
	TextView tv_showuserName;
	SipHome sipHome;
	public SharedPreferences prefs;
	private String stored_user_mobile_no, stored_user_country_code, userInfo = "UserInfo", stored_userimagepath, stored_username;
	private Uri selectedImage;
	int serverResponseCode = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		
		setContentView(R.layout.r4wsetting_profile);
		setActionBar();
		InitializeView();
		setValues();
		lv_editUserName.setOnClickListener(this);
		lv_editUserProfileImage.setOnClickListener(this);
		
	}
	
	@SuppressLint({ "NewApi", "SdCardPath" })
	private void setValues() {
		// TODO Auto-generated method stub
		File file = new File("/sdcard/R4W/ProfilePic/ProfilePic.png");
		if(file.exists())
		{
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			lv_showUserImage.setBackground(new BitmapDrawable(getResources(), bitmap));
		}
		
		String getPrefUserName = prefs.getString(stored_username, "UserName");
		if(!getPrefUserName.equals("UserName"))
		{
			tv_showuserName.setText(getPrefUserName);
		}
		
	}

	private void InitializeView() {
		// TODO Auto-generated method stub

		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_userimagepath = "com.roamprocess1.roaming4world.userimagepath";
		stored_username = "com.roamprocess1.roaming4world.username";

		
		lv_editUserName = (LinearLayout) findViewById(R.id.lv_setting_editUserName);
		lv_editUserProfileImage = (LinearLayout) findViewById(R.id.lv_setting_editUserImage);
		lv_showUserImage = (LinearLayout) findViewById(R.id.lv_setting_showUserImage);
		tv_showuserName = (TextView) findViewById(R.id.tv_setting_showUserName);

	}
	
	

	@SuppressLint("NewApi")
	private void setActionBar() {
		// TODO Auto-generated method stub
		ActionBar ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		try{
			ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar));
		}catch(Exception e){
			
		}
		ab.setTitle("Profile");
		ab.setIcon(getResources().getDrawable(R.drawable.roaminglogo));
	}

	
	public void userimagechange() {
		
		
		    Intent intent = new Intent(); 
	
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
		
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 0);
			intent.putExtra("aspectY", 0);
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			try {
				intent.putExtra("return-data", true);
				startActivityForResult(Intent.createChooser(intent,"Complete action using"), 2);

			} catch (ActivityNotFoundException e) {
				// Do nothing for now
			}
		    

			}
	
	
	@SuppressLint("NewApi") @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("onActivityResult", " in");
		Log.d("resultCode", resultCode + " in");
	    if(requestCode == RESULT_CANCELED && resultCode == 0 && resultCode == -1  )
        {
        	Toast.makeText(R4WSetting_Profile.this, "Please try again", Toast.LENGTH_SHORT).show();
        }else if (requestCode == 2 && data != null) {
			try{
        	Bundle extras2 = data.getExtras();
        	Log.d("selectedImage", " before");
			/*
        	selectedImage = data.getData();
			Log.d("selectedImage",  selectedImage + " --after");
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			prefUserInfo.edit().putString("userImagePath", picturePath);
			prefUserInfo.edit().commit();
			cursor.close();
			*/
        	String picturePath ="";
			if (extras2 != null) {
			//	Bitmap bmp = BitmapFactory.decodeFile(picturePath);
				Bitmap bmp = extras2.getParcelable("data");
			//	Drawable imd = Drawable.createFromPath(picturePath);
				Drawable imd = new BitmapDrawable(getResources(),bmp);
				picturePath = saveUserImage(bmp);
	        	
				Log.d("picturePath", picturePath + " !" );
				
				prefs.edit().putString(stored_userimagepath, picturePath).commit();
				
				lv_showUserImage.setBackground(imd);
				lv_showUserImage.invalidate();

			}
			
			 new AsyncTaskDownloadImage(picturePath).execute();
			}catch (Exception e){
				
			}
			
		}
        super.onActivityResult(requestCode, resultCode, data);
    
	}
	
	
	public void dialogboxUsreprofilePic(Context context) {
	
		final Dialog dialoguserProfilePic = new Dialog(context);
	//	dialoguserProfilePic.setTitle("Profile Photo");
		dialoguserProfilePic.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialoguserProfilePic.setContentView(R.layout.dialogboxuserprofilepic);
		dialoguserProfilePic.show();
		
		LinearLayout RemoveProfilePic = (LinearLayout) dialoguserProfilePic.findViewById(R.id.db_lv_remove_profilePic);
		LinearLayout ProfilePicUpdate = (LinearLayout) dialoguserProfilePic.findViewById(R.id.db_lv_select_profilePic);
		
		RemoveProfilePic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialoguserProfilePic.dismiss();
				dialogboxRemoveImage();
			}
		});
		
		ProfilePicUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialoguserProfilePic.dismiss();
				userimagechange();
			}
		});

	
	}
	
	
	@SuppressLint("SdCardPath") 
	public void dialogboxRemoveImage() {

		new AlertDialog.Builder(R4WSetting_Profile.this)
	    .setTitle("Remove Profile Image")
	    .setMessage("Are you sure you want to delete Profile Image?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        @SuppressLint("NewApi") public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	prefs.edit().putString(stored_userimagepath,"").commit();
	        	lv_showUserImage.setBackground(getResources().getDrawable(R.drawable.ic_contact_picture_180_holo_light));
	        	File f = new File("/sdcard/R4W/ProfilePic/ProfilePic.png");
				if(f.exists())
				{
				f.delete();
				}
	        	new AsyncTaskRemoveImage().execute();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     }).show();
	}

	
	class AsyncTaskRemoveImage extends AsyncTask<Void, Void, Boolean> {

		
		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	protected void onPostExecute(Boolean result) {
		if(result == true)
			Toast.makeText(R4WSetting_Profile.this, "Contact Images successfully Removed", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(R4WSetting_Profile.this, "Error Removing Contact Images", Toast.LENGTH_SHORT).show();
			
		
	}


	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
	//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
		Log.d("doInBackgroud", "doInBackground");
		
		if(webServiceRemoveImageUrl()){
			return true;
		}else{
			return false;
			}
		}
	
	
	}
	
	public boolean webServiceRemoveImageUrl() {
		// TODO Auto-generated method stub
		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_delete.php?self_contact="
						  +prefs.getString(stored_user_country_code, "NoValue")
						  +prefs.getString(stored_user_mobile_no, "NoValue")
			//			  +"918860357393"
						  +"&type=thumb";
			
			Log.d("url", url + " #");
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			
			if(json.getString("response").equalsIgnoreCase("Success"))
			{
				return true;
			}
			else{
				return false;
			}
				
				
		} catch (Throwable t) {

			t.printStackTrace();

			return false;
		}
	}

	@SuppressLint("SdCardPath") 
	private String saveUserImage(Bitmap userBitmap) {
		// TODO Auto-generated method stub
		
		File imageDirectory = new File("/sdcard/R4W/");
	     
	     if(!imageDirectory.exists())
	     {
	    	 imageDirectory.mkdir();
	     }
	     
	     File imageDirectoryprofile = new File("/sdcard/R4W/ProfilePic");
	     
	     if(!imageDirectoryprofile.exists())
	     {
	    	 imageDirectoryprofile.mkdir();
	     }
	        
	     File file = new File(imageDirectoryprofile.getAbsolutePath(),"ProfilePic.png");
	     try {
			if(file.exists())
			 {
			   file.deleteOnExit();
			 }
		 file.createNewFile();
	    
		//Convert bitmap to byte array
		Bitmap bitmap = userBitmap;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bitmapdata);
		
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
			
			mProgressDialog = new ProgressDialog(R4WSetting_Profile.this);
			mProgressDialog.setMessage("loading");
			mProgressDialog.show();
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
		
		Log.d("doInBackgroud", "doInBackground");
		int response = 0;
		
	//	String path = prefs.getString("userImagePath", "NoValue");
		
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
	
	public int uploadFile(String sourceFileUri) {

		
		
	  	  String upLoadServerUri = "";
	  	  upLoadServerUri = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_upload.php";
	  	  String fileName = sourceFileUri;
	  	  System.out.println("fileName"+fileName);
	  	  
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
		    	   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "-" + prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "") + "\"" + lineEnd);
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
	
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == Compatibility.getHomeMenuId()) {
			finish();
		return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	

	@SuppressLint("SdCardPath") @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch (v.getId()) {
		case R.id.lv_setting_editUserName:
			dialogboxUsreName(R4WSetting_Profile.this);
			break;
		case R.id.lv_setting_editUserImage:
			File f = new File("/sdcard/R4W/ProfilePic/ProfilePic.png");
			if(f.exists())
			{
			dialogboxUsreprofilePic(R4WSetting_Profile.this);
			}else{
				userimagechange();
			}
			break;
		}
		
		
	}
	
	
	public void dialogboxUsreName(Context context) {
		

		
		final String userName = prefs.getString(stored_username, "UserName");
		
		final String number; 
		
		number = prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "");
		
		final Dialog dialoguserName = new Dialog(context);
//		dialoguserName.setTitle("Edit User Name");
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

				String getUserName = edtUserName.getText().toString();
				
				if(getUserName.length() > 0){
				
				int action = 0;
				
				if(!userName.equalsIgnoreCase(""))
					action = 1;
				
				
				tv_showuserName.setText(getUserName);  
				prefs.edit().putString(stored_username, getUserName).commit();
				dialoguserName.dismiss();
				new AsynctaskUpdateUsername(R4WSetting_Profile.this, number, getUserName, action).execute();
				}else {
					Toast.makeText(R4WSetting_Profile.this, "Please enter Valid name", Toast.LENGTH_SHORT).show();
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
	

	
	
}
