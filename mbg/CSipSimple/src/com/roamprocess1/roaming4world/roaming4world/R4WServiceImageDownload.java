package com.roamprocess1.roaming4world.roaming4world;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.utils.Log;

public class R4WServiceImageDownload extends Service{

	String chooseClass = "0", prefChooseclass, stored_user_mobile_no, stored_user_country_code, no_image = "No image",
			username, number, user_status, user_urlpath ;
	SharedPreferences pref, prefs;
	Intent i;
	public static String[] phone_no, url_image, image_path, name, status;
	ArrayList<String> updated_url,  updated_number;
	DBContacts dbContacts;
	Cursor cursor; 
			
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		super.onStartCommand(intent, flags, startId);
		

		// TODO Auto-generated method stub
		pref = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		prefChooseclass = "com.roamprocess1.roaming4world.chooseClass";
		pref.getString(prefChooseclass, "0");
		
		prefs = getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		
		updated_number = new ArrayList<String>();
		updated_url = new ArrayList<String>();
		
		new AsyncTaskDownloadImage().execute();
		
		// insertIconinContacts();
		
		return Service.START_NOT_STICKY;
	}

	private void insertIconinContacts() {
		// TODO Auto-generated method stub
		Cursor cursor = null;
		String idContact , account;
	    try {
	        cursor = getContentResolver().query(Phone.CONTENT_URI, null, null, null, null);
	        int contactIdIdx = cursor.getColumnIndex(Phone._ID);
	        int nameIdx = cursor.getColumnIndex(Phone.DISPLAY_NAME);
	        int phoneNumberIdx = cursor.getColumnIndex(Phone.NUMBER);
	        int photoIdIdx = cursor.getColumnIndex(Phone.PHOTO_ID);
	        int acc = cursor.getColumnIndex(Phone.DATA);
	        cursor.moveToFirst();
	        do {
	             idContact = cursor.getString(contactIdIdx);
	             account = cursor.getString(acc);
	             updateIMContactField(getContentResolver(), idContact, chooseClass);
	            //...
	        } while (cursor.moveToNext());  
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (cursor != null) {
	            cursor.close();
	        }
	    }
	}

	
	public void updateIMContactField(ContentResolver contentResolver,
	        String uid, String account) {

	    ContentValues contentValues = new ContentValues();

	    contentValues.put(ContactsContract.Data.RAW_CONTACT_ID,
	            Integer.parseInt(uid));
	    contentValues.put(ContactsContract.Data.MIMETYPE,
	            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
	    contentValues.put(ContactsContract.CommonDataKinds.Im.TYPE,
	            ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM);
	    contentValues.put(ContactsContract.CommonDataKinds.Im.LABEL, "Roaming4World");
	    contentValues.put(ContactsContract.CommonDataKinds.Im.PROTOCOL,
	            ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM);
	    contentValues.put(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL,
	    		"Roaming4World");

	    contentValues.put(ContactsContract.CommonDataKinds.Im.DATA, account);

	    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
	    ops.add(ContentProviderOperation
	            .newInsert(ContactsContract.Data.CONTENT_URI)
	            .withValues(contentValues).build());

	    try {
	        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
	    } catch (Exception e) {
	        Log.d("updateIMContactField", "Can't update Contact's IM field.");
	    }
	}

	
	
	public void updateContactDetails(){
		
		if(dbContacts == null ){
			dbContacts = new DBContacts(getApplicationContext());
		}
		
		dbContacts.openToRead();
		
		cursor = dbContacts.fetch_contact_from_R4W();
		cursor.moveToFirst();
		Log.d("testCursor", cursor.getColumnCount()+" !");
		do {
			/*
			Log.d(" 0 value ", cursor.getString(0).toString() + " !");
			Log.d(" 1 value ", cursor.getString(1).toString() + " !");
			Log.d(" 2 value ", cursor.getString(5).toString() + " !");
			Log.d(" 3 value ", cursor.getString(3).toString() + " !");
			Log.d(" 4 value ", cursor.getString(4).toString() + " !");
			*/
		} while (cursor.moveToNext());
		
		dbContacts.close();
		
		
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	class AsyncTaskDownloadImage extends AsyncTask<Void, Void, Boolean> {

		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			if (result == true){
//				Toast.makeText(getApplicationContext(),"Contact Images Downloaded", Toast.LENGTH_SHORT).show();
			}else{
//				Toast.makeText(getApplicationContext(),"Error Downloading Contact Images", Toast.LENGTH_SHORT).show();
			}

			updateContactDetails();
			stopSelf();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// userPic =
			// getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
			Log.d("doInBackgroud", "doInBackground");

			if (webServiceImageUrl()) {

				for (int i = 0; i < updated_url.size(); i++) {
					Log.d(i + " updated_number ", updated_number.get(i) + "  !");
					Log.d(i + " updated_url ", updated_url.get(i) + "  !");
					String path = getImages(updated_url.get(i),
							updated_number.get(i));
					image_path = new String[url_image.length];
					image_path[i] = path;

					Log.d(i + " image_path ", image_path[i] + "  !");

					if (path != "") {

					} else {
					}
				}
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
			//			  +"918860357393"
						  +"&type=thumb";
			
			Log.d("url", url + " #");
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
				JSONObject json = new JSONObject(responseBody);
				JSONArray response = json.getJSONArray("contacts");
				String path = json.getString("path");
				Log.d("pathtst", path + " #");
				Log.d("response", response + " #");
				phone_no = new String[response.length()];
				url_image = new String[response.length()];
				name = new String[response.length()];
				status = new String[response.length()];
				for(int i = 0 ; i < response.length() ; i++)
				{
					JSONObject position = response.getJSONObject(i);
					Log.d("position", position + " #");
					phone_no[i] = position.getString("p");
					
					String url_update = position.getString("t");
					
					if(!url_update.equals("No image")){
						url_image[i] = path + url_update;
					}else{
					url_image[i] = position.getString("t");
					}
					
					name[i] = position.getString("un");
					name[i] = Uri.decode(name[i]);
					status[i] = Uri.decode(position.getString("s"));
					
					username = position.getString("un");
					number = position.getString("p");
					user_status = position.getString("s");
					user_urlpath = url_image[i];
					
					
					Log.d(i + " phone_no  ", phone_no[i] + "   !");
					Log.d(i + " name ", name[i] + "   !");
					Log.d(i + " status ", status[i] + "   !");
					Log.d(i + " url", url_image[i] + "   !");

					dbContacts = new DBContacts(getApplicationContext());
					
					dbContacts.openToWrite();
					
					
					
					String check = dbContacts.insert_or_update_R4Wcontact_detail_in_db(number, username, user_status, user_urlpath);
					
					if(check.equals("update")){
						updated_number.add(number);
						updated_url.add(user_urlpath);
					}
					
					//dbContacts.insert_R4Wcontact_detail_in_db(phone_no[i], name[i], status[i], url_image[i]);
				}
				
				dbContacts.close();
				return true;
		} catch (Exception t) {

			t.printStackTrace();

			return false;
		}

	
	}
	
	
	 public String getImages(String uri, String number)
 {
		File file = null;
		if (!uri.equals(no_image)) {

			String filepath = "";
			try {
				URL url = new URL(uri);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);
				urlConnection.connect();
				String filename = number + ".png";

				File imageDirectory = new File("/sdcard/R4W/");

				if (!imageDirectory.exists()) {
					imageDirectory.mkdir();
				}

				File imageDirectoryprofile = new File("/sdcard/R4W/ProfilePic");

				if (!imageDirectoryprofile.exists()) {
					imageDirectoryprofile.mkdir();
				}

				file = new File(imageDirectoryprofile.getAbsolutePath(),
						filename);

				if (file.createNewFile()) {
					file.createNewFile();
				}

				FileOutputStream fileOutput = new FileOutputStream(file);
				InputStream inputStream = urlConnection.getInputStream();
				int totalSize = urlConnection.getContentLength();
				int downloadedSize = 0;
				byte[] buffer = new byte[1024];
				int bufferLength = 0;
				while ((bufferLength = inputStream.read(buffer)) > 0) {
					fileOutput.write(buffer, 0, bufferLength);
					downloadedSize += bufferLength;
					Log.i("Progress:", "downloadedSize:" + downloadedSize
							+ "totalSize:" + totalSize);
				}
				fileOutput.close();
				if (downloadedSize == totalSize)
					filepath = file.getPath();
			} catch (Exception e) {
				filepath = "";
				e.printStackTrace();
				if (file.exists()) {
					file.delete();
				}
			}
			Log.i("filepath:", " " + filepath);
			return filepath;

		}

		else {
			return no_image;
		}

	}

}
