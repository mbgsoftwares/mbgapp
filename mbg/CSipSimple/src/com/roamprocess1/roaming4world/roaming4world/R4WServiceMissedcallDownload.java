package com.roamprocess1.roaming4world.roaming4world;

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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateUtils;
import android.util.Log;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.ui.SipHome;

public class R4WServiceMissedcallDownload extends Service{

	String chooseClass = "0", prefChooseclass, stored_user_mobile_no, stored_user_country_code,stored_min_call_credit, no_image = "No image",
			username, number, user_status, user_urlpath , stored_user_bal, stored_server_ipaddress;
	SharedPreferences pref, prefs;
	Intent i;
	public static String[] phone_no, url_image, image_path, name, status;
	ArrayList<String> updated_url,  updated_number;
	DBContacts dbContacts;
	Cursor cursor; 
	NotificationCompat.Builder mBuilder;
	Intent resultIntent;
	TaskStackBuilder stackBuilder;
	PendingIntent resultPendingIntent;
	NotificationManager mNotificationManager;
	
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
		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";
		stored_min_call_credit = "com.roamprocess1.roaming4world.min_call_credit";
		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";

		updated_number = new ArrayList<String>();
		updated_url = new ArrayList<String>();
		
		mBuilder = new NotificationCompat.Builder(this);
		resultIntent = new Intent(this, SipHome.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(SipHome.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		resultPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, SipHome.class), 0);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
//		showNotification();
		
		new AsyncTaskUpdateDetails().execute();
		
		Log.d("R4WServiceMissedcallDownload", "onStartCommand");
		
		return Service.START_NOT_STICKY;
	}


	private void showNotification(String count, String number_date, int j) {

		Notification notification = new Notification(R.drawable.r4wlogowhitefill,count, System.currentTimeMillis());
		Intent in = new Intent(this, SipHome.class);
		in.putExtra("MissedCall", true);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,in, PendingIntent.FLAG_CANCEL_CURRENT);
		notification.setLatestEventInfo(this, count, number_date, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(j, notification);

	}
	
	public void updateContactDetails(){
		
		
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
	
	
	
	class AsyncTaskUpdateDetails extends AsyncTask<Void, Void, Boolean> {
		

		
		
		
		
		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	   
	
	

	protected void onPostExecute(Boolean result) {
		if(result == true){
//			Toast.makeText(getApplicationContext(), "missed call successfully updated", Toast.LENGTH_SHORT).show();
		}
		else{
//			Toast.makeText(getApplicationContext(), "No missed call", Toast.LENGTH_SHORT).show();
			
		}
			
	//	updateContactDetails();
		stopSelf();
	}




	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
	//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
		Log.d("doInBackgroud", "doInBackground");
		
		if(webServiceMissedCallUrl()){
			
		return true;
		}else{
			return false;
		}
		
		
		
	}

   
	}
	
	
	
	
	
	
	 public boolean webServiceMissedCallUrl() {
			
		 String from_summary = "", noc = "", from_detailed = "", lt = "", ct = "";
		 Cursor c;
		 String time = "", value;
			try {
			
				dbContacts = new DBContacts(getApplicationContext());
			//	dbContacts.openToWrite();
				dbContacts.openToRead();
				c = dbContacts.fetch_details_from_MIssedCall_Offline_Table();
				
				if(c.getCount() > 0)
				{
					c.moveToFirst();
					value = c.getString(2);
					time = "&time=" + value;
			    //  time = "&time=" + "1408434902";
				}
				
				c.close();
				dbContacts.close();
				
				Log.d("webServiceMissedCallUrl", "called");
				HttpParams p = new BasicHttpParams();
				p.setParameter("user", "1");
				HttpClient httpclient = new DefaultHttpClient(p);
				String url = "http://ip.roaming4world.com/esstel/app_resume_info.php?contact="
							  +prefs.getString(stored_user_country_code, "NoValue")
							  +prefs.getString(stored_user_mobile_no, "NoValue")
							  +time;
				
				Log.d("url", url + " #");
				
				HttpGet httpget = new HttpGet(url);
				ResponseHandler<String> responseHandler;
				String responseBody;
				responseHandler = new BasicResponseHandler();
				responseBody = httpclient.execute(httpget, responseHandler);
				
				dbContacts.openToWrite();
				
				JSONObject json = new JSONObject(responseBody);
					
				Log.d("response11", json + " #");
				
				if(json.getString("verify_from_pc").equals("true")){
					Intent i = new Intent(R4WServiceMissedcallDownload.this, DesktopVerificationcode_Activity.class);
				    i.putExtra("verify_code", json.getString("verify_code"));
				    i.putExtra("countdown_time", json.getString("countdown_time"));
				    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    getApplication().startActivity(i);  
				}
				
				prefs.edit().putString(stored_min_call_credit, json.getString("min_call_credit")).commit();
				prefs.edit().putString(stored_user_bal, json.getString("user_bal")).commit();
				prefs.edit().putString(stored_server_ipaddress, json.getString("server_ip")).commit();
				
				Log.d("stored_server_ip address", json.getString("server_ip") + " #");
				Log.d("stored_user_bal", json.getString("user_bal") + " #");
				
				if(json.getString("valid").equals("true")){
					
				
					JSONArray summary = json.getJSONArray("summary");
					JSONArray detailed = json.getJSONArray("detailed");
	
				
					for(int i = 0 ; i < summary.length() ; i++)
					{
						JSONObject summarydata = summary.getJSONObject(i);
						from_summary = summarydata.getString("from");
						noc = summarydata.getString("noc");
						lt = summarydata.getString("lt");
						
						Log.d("from_summary", from_summary + " #");
						Log.d("noc", noc + " #");
						Log.d("lt", lt + " #");
						
						dbContacts.insert_MIssedCall_Offline_detail_in_db(from_summary, lt , noc);
						
						/*
						mBuilder.setSmallIcon(R.drawable.notification_icon);
						mBuilder.setContentTitle("Missed call (" + noc +")");
						mBuilder.setContentText(from_summary + "   " + lt);
						mBuilder.setContentIntent(resultPendingIntent);
						mNotificationManager.notify(Integer.parseInt(lt), mBuilder.build());
						*/
						String count = "Missed call (" + noc +")" ;
						
						CharSequence dateText =
				                DateUtils.getRelativeTimeSpanString(Long.parseLong(lt)*1000,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE);
						
						String num_date = from_summary + "   last on " + dateText.toString();
						
						
						showNotification(count , num_date, Integer.parseInt(lt));
					}
					
					for(int i = 0 ; i < detailed.length() ; i++)
					{
						JSONObject detaileddata = detailed.getJSONObject(i);
						from_detailed = detaileddata.getString("from");
						ct = detaileddata.getString("ct");
					
						Log.d("from_detailed", from_detailed + " #");
						Log.d("ct", ct + " #");
					
						dbContacts.insert_MIssedCall_detail_in_db(from_detailed, ct);
					}

					dbContacts.close();
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
	
}
