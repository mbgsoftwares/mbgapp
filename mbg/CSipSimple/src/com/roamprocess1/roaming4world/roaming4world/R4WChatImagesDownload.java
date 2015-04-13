package com.roamprocess1.roaming4world.roaming4world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.roamprocess1.roaming4world.ui.SipHome;
import com.roamprocess1.roaming4world.utils.Log;

public class R4WChatImagesDownload extends FragmentActivity{

	String chooseClass = "0", prefChooseclass, stored_user_mobile_no, stored_user_country_code;
	SharedPreferences pref, prefs;
	Intent i;
	String[] phone_no, url_image;
	
	  
	   @Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		pref = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		prefChooseclass = "com.roamprocess1.roaming4world.chooseClass";
		pref.getString(prefChooseclass, "0");
		
		prefs = getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";

		
		
		if(chooseClass.equals("0") || chooseClass.equalsIgnoreCase("com.roamprocess1.roaming4world.chooseClass"))
		{
			
			Log.d("R4WSplashScreen", "if");
			i = new Intent(R4WChatImagesDownload.this, RegChooseLogin.class);
			startActivity(i);
		}else if (chooseClass.equals("1")){
			
			
			Log.d("R4WSplashScreen", "else if");
			i = new Intent(R4WChatImagesDownload.this, SipHome.class);
			startActivity(i);
			
		}
		
		Toast.makeText(getApplicationContext(), "calledddddddddd", Toast.LENGTH_LONG).show();
		new AsyncTaskDownloadImage().execute();
		
	}
	   
	   

	class AsyncTaskDownloadImage extends AsyncTask<Void, Void, Boolean> {
			
			
			
			
			@SuppressWarnings("static-access")
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
		   
		
		

		protected void onPostExecute(Boolean result) {
			if(result == true)
				Toast.makeText(getApplicationContext(), "doooooooooonnee", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), "Faillllllllllllllll", Toast.LENGTH_LONG).show();
				
			
		}




		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
		//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
			
			String path = getImages("https://www.roaming4world.com/images/logo.png");
			
			if (path != "") {
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else {
				return false;
			}
			
		}

	   }
	   
	
	
	
	
	
	public boolean webServiceImageUrl() {

		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_download.php?self_contact="
						  +prefs.getString(stored_user_country_code, "NoValue")
						  +prefs.getString(stored_user_mobile_no, "NoValue")
						  +"&type=thumb";
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
				JSONObject json = new JSONObject(responseBody);
				JSONArray response = json.getJSONArray("response");
				
				phone_no = new String[response.length()];
				url_image = new String[response.length()];
				for(int i = 0 ; i < response.length() ; i++)
				{
					JSONObject position = response.getJSONObject(i);
					phone_no[i] = position.getString("phone_no");
					url_image[i] = position.getString("url");
				}
				
				return true;
		} catch (Throwable t) {

			t.printStackTrace();

			return false;
		}

	}

	
	
	
	
	
	
	
	
	   public String getImages(String uri)
	   {
		   String filepath = "";
		try
		   {   
		     URL url = new URL(uri);
		     HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		     urlConnection.setRequestMethod("GET");
		     urlConnection.setDoOutput(true);                   
		     urlConnection.connect();                  
		     // File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
		     String filename="downloadedFile.png";
		     
		     File imageDirectory = new File("/sdcard/R4W/");
		     
		     if(!imageDirectory.exists())
		     {
		    	 imageDirectory.mkdir();
		     }
		     //File SDCardRoot  = new File(imageDirectory.getAbsolutePath(), filename);
		        
		     Log.i("Local filename:",""+filename);
		     File file = new File(imageDirectory.getAbsolutePath(),filename);
		     if(file.createNewFile())
		     {
		       file.createNewFile();
		     }                 
		     FileOutputStream fileOutput = new FileOutputStream(file);
		     InputStream inputStream = urlConnection.getInputStream();
		     int totalSize = urlConnection.getContentLength();
		     int downloadedSize = 0;   
		     byte[] buffer = new byte[1024];
		     int bufferLength = 0;
		     while ( (bufferLength = inputStream.read(buffer)) > 0 ) 
		     {                 
		       fileOutput.write(buffer, 0, bufferLength);                  
		       downloadedSize += bufferLength;                 
		       Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
		     }             
		     fileOutput.close();
		     if(downloadedSize==totalSize) filepath=file.getPath();    
		   } 
		   catch (MalformedURLException e) 
		   {
		     e.printStackTrace();
		   } 
		   catch (IOException e)
		   {
		     filepath="";
		     e.printStackTrace();
		   }
		   Log.i("filepath:"," "+filepath) ;
		   return filepath;
	   }

	
}
