package com.roamprocess1.roaming4world.roaming4world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.utils.Compatibility;

public class R4WChatUserImage extends SherlockFragmentActivity {
	/** Called when the activity is first created. */

	public SharedPreferences prefs;
	String stored_chatuserName, stored_chatuserNumber, noImage = "No image",
			stored_user_mobile_no, stored_user_country_code;
	Bitmap userPic;
	ImageView imageDetail;
	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	PointF startPoint = new PointF();
	PointF midPoint = new PointF();
	float oldDist = 1f;
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	String name = "", number = "";
	String num;
	String fileuri;
	ProgressBar pb_user_full_img;
	
	
	@SuppressLint("SdCardPath") @Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		prefs = getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_chatuserName = "com.roamprocess1.roaming4world.stored_chatuserName";
		stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		
	     
		
		setActionBar();

		setContentView(R.layout.r4wchatuserimage);
		imageDetail = (ImageView) findViewById(R.id.iv_zoom);
		imageDetail.setImageBitmap(SetSenderUriInfo.senderImageBitmap);
		pb_user_full_img = (ProgressBar) findViewById(R.id.pb_user_full_img);
		pb_user_full_img.setVisibility(ProgressBar.GONE);
		
		//stripContact();
		
		num = stripNumber(number);
		Log.d("num", num + " !");
		setPreUri();
		fileuri = "/sdcard/R4W/ProfilePic/FullImage/" + num + ".png";
		File file = new File(fileuri);
		if(file.exists()){
			imageDetail.setImageURI(Uri.parse(fileuri));
		}else{
			new AsyncTaskDownloadImage().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		/**
		 * set on touch listner on image
		 */
		/*
		imageDetail.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				ImageView view = (ImageView) v;
				System.out.println("matrix=" + savedMatrix.toString());
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:

					savedMatrix.set(matrix);
					startPoint.set(event.getX(), event.getY());
					mode = DRAG;
					break;

				case MotionEvent.ACTION_POINTER_DOWN:

					oldDist = spacing(event);

					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(midPoint, event);
						mode = ZOOM;
					}
					break;

				case MotionEvent.ACTION_UP:

				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;

					break;

				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - startPoint.x,
								event.getY() - startPoint.y);
					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, midPoint.x,
									midPoint.y);
						}
					}
					break;

				}
				view.setImageMatrix(matrix);

				return true;
			}

			@SuppressLint("FloatMath")
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});
		*/

	}
	 @SuppressLint("SdCardPath") 
	 private void setPreUri() {
		// TODO Auto-generated method stub
		 fileuri = "/sdcard/R4W/ProfilePic/" + num + ".png";
			File file = new File(fileuri);
			if(file.exists()){
				imageDetail.setImageURI(Uri.parse(fileuri));
			}
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
	
	@SuppressLint({ "NewApi", "SdCardPath" })
	private void setActionBar() {
		// TODO Auto-generated method stub
		
		
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
	    	 number= bundle.getString("R4wCallingNumber");
	    	 name= bundle.getString("R4wCallingName");
	    	   }
		
		Drawable pic;
		
		if(number.contains("@"))
		{
			String[] arr = number.split("@");
			number = arr[0];
			arr = number.split(":");
			number = arr[1];
		}
		
			String fileuri = "/sdcard/R4W/ProfilePic/" + number + ".png";
			File imageDirectoryprofile = new File(fileuri);
			if (imageDirectoryprofile.exists()) {
				Bitmap img = BitmapFactory.decodeFile(fileuri);
		        pic = new BitmapDrawable(getResources(),img);
			}else{
		        pic = getResources().getDrawable(R.drawable.ic_contact_picture_180_holo_light);
			}
			
			ActionBar ab = getActionBar();
			ab.setDisplayHomeAsUpEnabled(true);
		    ab.setHomeButtonEnabled(true);
		    ab.setDisplayShowHomeEnabled(false);
		    ab.setDisplayShowTitleEnabled(false);
			ab.setCustomView(R.layout.r4wactionbarcustom);
			ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			
	        LinearLayout fin = (LinearLayout) ab.getCustomView().findViewById(R.id.ll_header_finish);
			fin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
			
	        
	//       userPic.setImageDrawable(pic) ;
	       if(name == null){
	//		nameUser.setText(number);	
	       }else{
	//		nameUser.setText(name);
	       }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == Compatibility.getHomeMenuId()) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public Bitmap getBitmapFromURL(String src) {
		try {
			java.net.URL url = new java.net.URL(src);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	class AsyncTaskDownloadImage extends AsyncTask<Void, Void, Boolean> {

		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pb_user_full_img.setVisibility(ProgressBar.VISIBLE);
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(Boolean result) {

		
			pb_user_full_img.setVisibility(ProgressBar.GONE);
			File file = new File(fileuri);
			if(file.exists()){
				imageDetail.setImageURI(Uri.parse(fileuri));
			}else{
				Toast.makeText(R4WChatUserImage.this, "Failed to download", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.d("doInBackgroud", "doInBackground");
			
			String pathuri = webServiceImageUrl(num);
			if (pathuri.equals("")) {
				return false;
			} else {
				getImage(pathuri);
			}
			return false;
		}
	
		public String webServiceImageUrl(String numm) {
		
			String urll="";

		try {
			Log.d("webServiceImageUrl", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			 String uri = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_download.php?image_contact="
						+ numm
						+ "&self_contact="
						+ prefs.getString(stored_user_country_code, "")
						+ prefs.getString(stored_user_mobile_no, "")
						+ "&type=image";
				
			Log.d("uri", uri + " #");
			HttpGet httpget = new HttpGet(uri);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
				JSONObject json = new JSONObject(responseBody);
				JSONArray response = json.getJSONArray("response");
				for(int i = 0 ; i < response.length() ; i++)
				{
					JSONObject position = response.getJSONObject(i);
					urll = position.getString("url");
						}
				
				return urll;
		} catch (Throwable t) {

			t.printStackTrace();

			return urll;
		}

	
	}
	
	@SuppressLint("SdCardPath") 
	public boolean getImage(String uri) {

		 File file = null;
		 Log.d("urii", uri);

		   String filepath = "";
		try
		   {   
		     URL url = new URL(uri);
		     HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		     urlConnection.setRequestMethod("GET");
		     urlConnection.setDoOutput(true);                   
		     urlConnection.connect();                  
		     String filename= num+".png";
		     
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
		        

		     File imageDirectoryprofiledi = new File("/sdcard/R4W/ProfilePic/FullImage");
		     
		     if(!imageDirectoryprofiledi.exists())
		     {
		    	 imageDirectoryprofiledi.mkdir();
		     }
		     
		     file = new File(imageDirectoryprofiledi.getAbsolutePath(),filename);
		     
		     if(!file.exists())
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
		   catch (Exception e) 
		   {
			     filepath="";
			     e.printStackTrace();
			     if(file.exists()){
			    	 file.delete();
			     }
			     
			     return false;
		   } 
		   Log.i("filepath:"," "+filepath) ;
		   return true;
    	}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}