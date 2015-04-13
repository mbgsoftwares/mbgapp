package com.roamprocess1.roaming4world.roaming4world;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.roamprocess1.roaming4world.R;
public class MyBroadcastReceiver extends BroadcastReceiver{
	String s_fetch_pin="",s_fetch_country_code,s_fetch_self_no,str_map_no;
	String s_validity,s_value,s_activ_on,s_expiry;
	boolean unknownhost=false;
	Context context1;
	Intent intent1;
	float float_val;
	Cursor data_check_pin,data_check_status,data_check_expiry;
	SQLiteAdapter mySQLiteAdapter;
	int fetch_flag;
	int fetch_status,fetch_status_1;
	String notification_status_value,notification_status_value_1;
	String notification_type_value,notification_type_value_1;
	String notification_status_expiry,notification_status_expiry_8;
	String notification_type_expiry,notification_type_expiry_8;
	String expiry_date;
	String noti,heading,msg,url_disp,url_show;
	long diffHours,diffHours_8;
	boolean noti_validity_24=false;
	boolean noti_value_5=false;
	boolean noti_validity_8=false;
	boolean noti_value_1=false;
	boolean promotional_offer_check_pin_id;
@Override
public void onReceive(Context context, Intent intent) {
	// TODO Auto-generated method stub
	put_context_intent(context,intent);
	Log.d("in mybroadcastreceiver start","step 1");
	mySQLiteAdapter=new SQLiteAdapter(this.get_context());
	mySQLiteAdapter.openToRead();
	try{
	data_check_pin = mySQLiteAdapter.fetch_check_last_pin();
	}
	catch(Exception e){
		e.printStackTrace();
	}
	data_check_pin.moveToFirst();
	
	//System.out.println("count : "+ data_check_pin.getCount());
	Log.d("in mybroadcast","step 2");
	Boolean inWhile=false;
	
	while(!data_check_pin.isAfterLast()){    	
		inWhile=true;
		fetch_flag = data_check_pin.getInt(data_check_pin.getColumnIndex(SQLiteAdapter.FN_FLAG));
		Log.d("in mybroadcast","step 3");
		if(fetch_flag==0){    	
			promotional_offer_check_pin_id=false;
			Log.d("in mybroadcast","step 4");
			new MyAsyncTaskPromo_Offer().execute();
		}
		else{
			promotional_offer_check_pin_id=true;
			Log.d("in mybroadcast","step 5");
			s_fetch_pin = data_check_pin.getString(data_check_pin.getColumnIndex(SQLiteAdapter.FN_PIN_NO));
			s_fetch_country_code = data_check_pin.getString(data_check_pin.getColumnIndex(SQLiteAdapter.FN_COUNTRY_CODE));
			s_fetch_self_no = data_check_pin.getString(data_check_pin.getColumnIndex(SQLiteAdapter.FN_SELF_NO));
			
			new MyAsyncTaskMapNoGet().execute();
			new MyAsyncTaskPromo_Offer().execute();
		}
		
		
		data_check_pin.moveToNext();    	
		Log.d("in mybroadcast","step 6");
	}
	//mySQLiteAdapter.close();
	if(!inWhile)
	{
		promotional_offer_check_pin_id=false;
		new MyAsyncTaskPromo_Offer().execute();
	}
	Log.d("in mybroadcast","step 7");
}

public void put_context_intent(Context context,Intent intent)
{
	context1=context;
	intent1=intent;
}

public Intent get_intent()
{
	return intent1;
}

public Context get_context()
{
	return context1;
}

public void put_value(float float_val1)
{
	float_val=float_val1;
}

public void put_expiry_date(String date)
{
	expiry_date=date;
}

public String get_expiry_date()
{
	return expiry_date;
}
public float get_value()
{
	return float_val;
}

public boolean webservreqMAPNOGET(){
	try {        
        HttpParams p = new BasicHttpParams();
        p.setParameter("user", "1");
        HttpClient httpclient = new DefaultHttpClient(p);
        
        String url = "http://ip.roaming4world.com/esstel/receive.php?pinno="+s_fetch_pin;
        HttpPost httppost = new HttpPost(url);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("user", "1"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httppost, responseHandler);
         
            JSONObject json = new JSONObject(responseBody);
            
            //System.out.println(json);
            
            JSONArray jArray = json.getJSONArray("responses");
            
            for (int i = 0; i < jArray.length(); i++) {
            	
                HashMap<String, String> map = new HashMap<String, String>();
                JSONObject e = jArray.getJSONObject(i);
                
                String s = e.getString("response");
                JSONObject jObject = new JSONObject(s);
                str_map_no=jObject.getString("map_no");
                
                Log.d("Divert No", jObject.getString("map_no"));
                
                JSONObject e1 = jArray.getJSONObject(i+1);
                String s1 = e1.getString("info");
                JSONObject jObject1 = new JSONObject(s1);
                
                //s_validity = jObject1.getString("sum_validity");
                float int_value=Integer.parseInt(jObject1.getString("sum_value"));
                
                Log.d("int value",String.valueOf(int_value));
                float float_value=int_value/100;
                put_value(float_value);
                Log.d("float value",String.valueOf(float_value));
                
                
                s_value    = "Balance   : $ "+String.format("%.2f", float_value);
                Log.d("string value",s_value);
                
                s_activ_on = "Activated : "+jObject1.getString("activated_on");
                s_activ_on = s_activ_on.substring(0,  s_activ_on.length() - 3);
                
                s_expiry   = "Expiry : " + jObject1.getString("expiry_date");
                s_expiry   = s_expiry.substring(0,  s_expiry.length() - 3);
                
                Log.d("Info Str - validity", jObject1.getString("sum_validity"));
                Log.d("Info Str - value", jObject1.getString("sum_value"));
                Log.d("Info Str - activated on", jObject1.getString("activated_on"));
                Log.d("Info Str - expiry date", jObject1.getString("expiry_date"));
                put_expiry_date(jObject1.getString("expiry_date"));
            }
            return true;
        } 
        catch(UnknownHostException e)
        {
        	e.printStackTrace();
        	unknownhost=true;
        	return false;
        }
        catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } 
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    } 
	catch (Throwable t) {
        //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
		t.printStackTrace();
        return false;
    }    	
}

private class MyAsyncTaskMapNoGet extends AsyncTask<Void, Void, Boolean>
{		
    //ProgressDialog mProgressDialog3;
    @SuppressWarnings("deprecation")
	@Override
    protected void onPostExecute(Boolean result) {
    	
    	if(unknownhost)
    	{
    		unknownhost=false;
    		Log.d("in unknownhost","yes");
    		new MyAsyncTaskMapNoGet().execute();
    	}
    	else
    	{
    		
    		//mySQLiteAdapter.update_flag_notify_expiry_24("Set0");
			//mySQLiteAdapter.update_flag_notify_value_5("Set0");
			
    		/*
    		 * START NOTIFICATION FOR 5 DOLLAR VALUE
    		 */
    		Log.d("test","1");
    		data_check_status = mySQLiteAdapter.get_notification_status(); 
    		Log.d("test","2");
    		data_check_status.moveToFirst();
    		
    		Log.d("test","3");    	
			Log.d("test","4");
				fetch_status = data_check_status.getInt(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_VALUE));
     		Log.d("test","5");
    		//System.out.println(fetch_status);
    		
    		notification_type_value=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_TYPE));
			notification_status_value=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_VALUE));
			Log.d("notification_type",notification_type_value);
    		if(fetch_status==0){
    			Log.d("test","6a");
    			float val=500/100;
    			if(get_value()<=val){
    				//get_notification_value();
    				noti_value_5=true;
    			}
    		}
    		else{
    			Log.d("test","6b");
    			
    			Log.d("notification_type",notification_type_value);
    			//System.out.println(notification_status_value);
    			float val=500/100;
    			if(get_value()>val)
    			{
    				mySQLiteAdapter.update_flag_notify_value_5("Set0");
    			}
    			else if(notification_status_value.equals("0") && (get_value()>val)){
    				//get_notification_value();
    				noti_value_5=true;
    			}
    			
    		}
    		/*
    		 * END NOTIFICATION FOR 5 DOLLAR VALUE
    		 */
    		
    		/*
    		 * START NOTIFICATION FOR 1 DOLLAR VALUE
    		 * 
    		 */
    		Log.d("test","1"); 
    		Log.d("test","2");
				fetch_status_1 = data_check_status.getInt(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_VALUE_1));
     		Log.d("test","5");
    		//System.out.println(fetch_status_1);
    		
    		notification_type_value_1=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_TYPE));
			notification_status_value_1=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_VALUE_1));
			Log.d("notification_type",notification_type_value_1);
    		if(notification_status_value_1.equals("0")){
    			Log.d("test","6a");
    			float val=100/100;
    			if(get_value()<=val){
    				//get_notification_value();
    				noti_value_1=true;
    			}
    		}
    		else{
    			Log.d("test","6b");
    			
    			Log.d("notification_type",notification_type_value_1);
    			//System.out.println(notification_status_value_1);
    			float val=100/100;
    			if(get_value()>val)
    			{
    				mySQLiteAdapter.update_flag_notify_value_1("Set0");
    			}
    			else if(notification_status_value_1.equals("0") && (get_value()>val)){
    				//get_notification_value();
    				noti_value_1=true;
    			}
    			
    		}
    		
    		
    		
    		
    		/*
    		 * END NOTIFICATION FOR 1 DOLLAR VALUE
    		 * 
    		 */
    		
    		
    		/*
    		 * START NOTIFICATION FOR VALIDITY 1 DAY 
    		 */
    		Log.d("after data check expiry", "yes");
    		notification_type_expiry=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_EXPIRY));
    		Log.d("after notification type expiry", "yes");
			Log.d("notification_type",notification_type_expiry);
			notification_status_expiry=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_EXPIRY));
			Log.d("after noti status exiry", "yes");
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateInString = get_expiry_date();
		 
			Calendar time_24 = Calendar.getInstance();
			time_24.add(Calendar.MILLISECOND, -time_24.getTimeZone().getOffset(time_24.getTimeInMillis()));
			Date dNow_24 = time_24.getTime();
			
			try {
				Date date = formatter.parse(dateInString);
				//System.out.println(date);
				//System.out.println(formatter.format(date));
				long diff=date.getTime()-dNow_24.getTime();
				//System.out.println(diff);
				diffHours = diff / (60 * 60 * 1000);
				//System.out.println(diffHours);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			if(notification_status_expiry.equals("0"))
			{
				Log.d("entered in status expire","yo");
				if(diffHours<24)
				{
					noti_validity_24=true;
					//get_notification_expiry();
				}
			}
			else
				{
				//System.out.println(notification_status_expiry);
				if(diffHours>24)
				{
					mySQLiteAdapter.update_flag_notify_expiry_24("Set0");
				}
				else if(notification_status_expiry.equals("0")){
					Log.d("test","6c");
					//get_notification_expiry();
					noti_validity_24=true;
				}
			}
			Log.d("test","6d");
			/*
    		 * END NOTIFICATION FOR VALIDITY 1 DAY
    		 */
			
			/*
			 * START NOTIFICATION FOR VALIDITY 8 HOURS
			 * 
			 */
			
			Log.d("after data check expiry", "yes");
    		notification_type_expiry_8=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_EXPIRY_8));
    		Log.d("after notification type expiry", "yes");
			Log.d("notification_type",notification_type_expiry_8);
			notification_status_expiry_8=data_check_status.getString(data_check_status.getColumnIndex(SQLiteAdapter.BT_STATUS_EXPIRY_8));
			Log.d("after noti status exiry", "yes");
			SimpleDateFormat formatter_8 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			String dateInString_8 = get_expiry_date();
			
			
			Calendar time_8 = Calendar.getInstance();
			time_8.add(Calendar.MILLISECOND, -time_8.getTimeZone().getOffset(time_8.getTimeInMillis()));
			Date dNow = time_8.getTime();
			
			try {
				Date date = formatter_8.parse(dateInString_8);
				//System.out.println(date);
				//System.out.println(formatter.format(date));
				//System.out.println(dNow);
				long diff=date.getTime()-dNow.getTime();
				//System.out.println(diff);
				diffHours_8 = diff / (60 * 60 * 1000);
				//System.out.println(diffHours_8);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if(notification_status_expiry_8.equals("0"))
			{
				Log.d("entered in status expire","yo");
				if(diffHours_8<8)
				{
					noti_validity_8=true;
					//get_notification_expiry();
				}
			}
			else
				{
				//System.out.println(notification_status_expiry_8);
				if(diffHours_8>8)
				{
					mySQLiteAdapter.update_flag_notify_expiry_8("Set0");
				}
				else if(notification_status_expiry_8.equals("0")){
					Log.d("test","6c");
					//get_notification_expiry();
					noti_validity_8=true;
				}
			}
			
			
			/*
			 * END NOTIFICATION FOR VALIDITY 8 HOURS
			 * 
			 */
			
			get_notification();
		}
	}

    @Override
    protected void onPreExecute() {
        
    }
    
    @Override
    protected Boolean doInBackground(Void... params) {
    	if(webservreqMAPNOGET()){
    		Log.d("yay","SUCCESS");
    		return true;
		}
		else{
			Log.d("err","ERROR");
			return false;
		}
    }
}
private String getDateInTimeZone(String dateStr, DateFormat fullDateFormat, TimeZone targetTimeZone) throws ParseException{
	String newDtStr = "";
	fullDateFormat.setTimeZone(targetTimeZone);
	java.util.Date et2 = fullDateFormat.parse(dateStr);
	newDtStr = fullDateFormat.format(et2);
	return newDtStr;
}



	public void get_notification()
	{
		String noti_msg="";
		boolean check=true;
		if(noti_value_1&&noti_validity_8){
			mySQLiteAdapter.update_flag_notify_expiry_8("Set1");
			mySQLiteAdapter.update_flag_notify_value_1("Set1");
			Log.d("inNoti","both");
			noti_msg="PIN Balance and Validity is Expiring";
			check=false;
			show_notification(noti_msg);
		}
		else if(noti_value_1){
			mySQLiteAdapter.update_flag_notify_value_1("Set1");
			Log.d("inNoti","value");
			noti_msg="PIN balance is low $"+get_value();
			
			if(noti_validity_24){
				mySQLiteAdapter.update_flag_notify_expiry_24("Set1");
				Log.d("inNoti","validity");
				noti_msg="PIN balance is low & validity expire in 24 hrs";
			}
			check=false;
			show_notification(noti_msg);
		}
		else if(noti_validity_8){
			mySQLiteAdapter.update_flag_notify_expiry_8("Set1");
			Log.d("inNoti","validity");
			noti_msg="PIN validity is expiring in 8 hours";
			
			if(noti_value_5){
				mySQLiteAdapter.update_flag_notify_value_5("Set1");
				Log.d("inNoti","value");
				noti_msg="8 hrs left in PIN expiry & PIN balance $"+get_value();
			}
			check=false;
			show_notification(noti_msg);
		}
		
		if(check){
			if(noti_value_5&&noti_validity_24){
				mySQLiteAdapter.update_flag_notify_expiry_24("Set1");
				mySQLiteAdapter.update_flag_notify_value_5("Set1");
				Log.d("inNoti","both");
				noti_msg="PIN validity & balance running low";
				show_notification(noti_msg);
			}
			else if(noti_value_5){
				mySQLiteAdapter.update_flag_notify_value_5("Set1");
				Log.d("inNoti","value");
				noti_msg="PIN balance is $"+get_value();
				show_notification(noti_msg);
			}
			else if(noti_validity_24){
				mySQLiteAdapter.update_flag_notify_expiry_24("Set1");
				Log.d("inNoti","validity");
				noti_msg="24hrs left in PIN Expiry";
				show_notification(noti_msg);
			}
		}
	}
	public void show_notification(String noti_msg){
		Log.d("in notification", "yes");
		Context context2=get_context();
		Intent intent2=get_intent();
		String svcName = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager;
		notificationManager = (NotificationManager)context2.getSystemService(svcName);
		Log.d("after notification manager", "yes");
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context2)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Recharge - Roaming4World")
		        .setContentText(noti_msg);
		
		NotificationCompat.BigTextStyle BigStyle =
		        new NotificationCompat.BigTextStyle();
		BigStyle.bigText(noti_msg);
		
		mBuilder.setStyle(BigStyle);
		Log.d("after builder", "yes");
		
		mBuilder.setDefaults(-1);
		mBuilder.setTicker("Recharge needed");
		// Intent to launch an activity when the extended text is clicked
		Intent intent1 = new Intent(context2,R4wMapService.class);
		PendingIntent launchIntent = PendingIntent.getActivity(context2, 0, intent1, 0);
		mBuilder.setContentIntent(launchIntent);
		mBuilder.setAutoCancel(true);
		Log.d("after mbuilderauto", "yes");
	
		int notificationRef = 1;
		notificationManager.notify(notificationRef, mBuilder.build());
		
	}
	
	
	
	
	/*
	 * Block for promotional offers called by asynctask 
	 */
	
	public boolean webservreqPromo_Offer(){
		try {        
	        HttpParams p = new BasicHttpParams();
	        p.setParameter("user", "1");
	        HttpClient httpclient = new DefaultHttpClient(p);
	        
	        TelephonyManager mngr = (TelephonyManager) get_context().getSystemService(Context.TELEPHONY_SERVICE); 
            String imei=mngr.getDeviceId();
            String url;
	        if(promotional_offer_check_pin_id)
	        {
	         url = "http://ip.roaming4world.com/esstel/promotional_offers.php?pinno="+s_fetch_pin+"&device_id="+imei;
	        }
	        else
	        {
	        	 url = "http://ip.roaming4world.com/esstel/promotional_offers.php?device_id="+imei;
	        }
	        HttpPost httppost = new HttpPost(url);
	        try {
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("user", "1"));
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            String responseBody = httpclient.execute(httppost, responseHandler);
	         
	            JSONObject json = new JSONObject(responseBody);
	            
	            //System.out.println(json);
	            
	            noti=json.getString("noti");
	            heading=json.getString("heading");
	            msg=json.getString("msg");
	            url_disp=json.getString("url_disp");
	            if(url_disp.equals("yes"))
	            {
	            	url_show=json.getString("url");
	            	Log.d("url_show",url_show);
	            }
	            else
	            {
	            	url_show="nothing";
	            	Log.d("url_show",url_show);
	            }
	            return true;
	        } 
	        catch(UnknownHostException e)
	        {
	        	e.printStackTrace();
	        	unknownhost=true;
	        	return false;
	        }
	        catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            return false;
	        } 
	        catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            return false;
	        }
	    } 
		catch (Throwable t) {
	        //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
			t.printStackTrace();
	        return false;
	    }    	
	}
	
	
	
	
	private class MyAsyncTaskPromo_Offer extends AsyncTask<Void, Void, Boolean>
	{	
		
		
		
		 @Override
		    protected Boolean doInBackground(Void... params) {
		    	if(webservreqPromo_Offer()){
		    		Log.d("yay","SUCCESS");
		    		return true;
				}
				else{
					Log.d("err","ERROR");
					return false;
				}
		    }
		 
		 
		 @Override
		    protected void onPostExecute(Boolean result) {
			 
			 if(result)
			 {
				 if(noti.equals("yes"))
				 {
					if(url_disp.equals("yes"))
					{
						show_notification_promo(msg, heading,url_disp,url_show);
					}
					else if(url_disp.equals("no"))
					{
						show_notification_promo(msg, heading,"1",url_show);
					}
				 }
				 else if(noti.equals("no"))
				 {
					 
				 }
				 
			 }
			 else
			 {
				 
			 }
			 
		 }
		
	}
	
	
	
	public void show_notification_promo(String noti_msg,String heading,String url_disp,String url_show){
		Log.d("in notification", "yes");
		Context context2=get_context();
		Intent intent2=get_intent();
		String svcName = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager;
		notificationManager = (NotificationManager)context2.getSystemService(svcName);
		Log.d("after notification manager", "yes");
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context2)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(heading)
		        .setContentText(noti_msg);
		
		NotificationCompat.BigTextStyle BigStyle =
		        new NotificationCompat.BigTextStyle();
		Log.d("url_disp_noti",url_disp);
		Log.d("url_show_noti",url_show);
		
		BigStyle.bigText(noti_msg);
		
		mBuilder.setStyle(BigStyle);
		Log.d("after builder", "yes");
		
		mBuilder.setDefaults(-1);
		mBuilder.setTicker("Promotional Offer");
		// Intent to launch an activity when the extended text is clicked
		Intent intent1;
		if(!url_show.equals("nothing"))
		{
			
			intent1 = new Intent(Intent.ACTION_VIEW);
			intent1.setData(Uri.parse(url_show));
		}
		else
		{
			intent1 = new Intent(context2,R4wHome.class);
		}
		PendingIntent launchIntent = PendingIntent.getActivity(context2, 0, intent1, 0);
		mBuilder.setContentIntent(launchIntent);
		mBuilder.setAutoCancel(true);
		Log.d("after mbuilderauto", "yes");
	
		int notificationRef = 1;
		notificationManager.notify(notificationRef, mBuilder.build());
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}