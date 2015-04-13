package com.roamprocess1.roaming4world.roaming4world;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class FlightdelayReceiver extends BroadcastReceiver{

	
	Context context1;
	Intent intent1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		System.out.println("on receive in flightdelayreceiver.java");
		
		
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
	
	
	
	class MyAsyncTaskMapFlightInfo extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog mProgressDialog4;

		@Override
		protected void onPostExecute(Boolean result) {		
			Log.d("update_mao_text", "starting");
			
		}

		@Override
		protected void onPreExecute() {
		
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webServiceFlightInfo()) {
				Log.d("doInBackgroud", "doInBackground");
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public boolean webServiceFlightInfo() {
		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);
			
			String url = "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/status/";
			url += "?appId=8b9a9ea9";
			url += "&appKey=c54d8100294cbeca21178e69b2184662&utc=false";
			
			Log.d("Service URL - Flight Info",url);
			
			
			
			HttpGet httpget= new HttpGet(url);
			//HttpPost httppost = new HttpPost(url);

			try {

				//List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						//2);
				//nameValuePairs.add(new BasicNameValuePair("user", "1"));
				//httpget.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler;
				String responseBody;
				
				responseHandler = new BasicResponseHandler();
				responseBody = httpclient.execute(httpget,responseHandler);
				Log.d("Flight Response", responseBody);
				
				
				JSONObject json = new JSONObject(responseBody);
				JSONObject flightRequest = json.getJSONObject("request");
				
				Log.d("flightRequest", flightRequest.toString());
				JSONObject flightappendix = json.getJSONObject("appendix");
				Log.d("flightRequestappendix", flightappendix.toString());
				
				JSONArray flightappendixAirlinesArray = flightappendix.getJSONArray("airlines");
				Log.d("flightRequestairlines", flightappendixAirlinesArray.toString());
				
				for(int i=0;i<flightappendixAirlinesArray.length();i++)
				{
					JSONObject flight_appendix_airline_array_first_index=flightappendixAirlinesArray.getJSONObject(i);
					
				}				

				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				//prefs.edit().putString(get_info_show_status, "0").commit();
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//prefs.edit().putString(get_info_show_status, "0").commit();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//prefs.edit().putString(get_info_show_status, "0").commit();
				return false;
			}catch(JSONException e)
			{
				e.printStackTrace();
				//prefs.edit().putString(get_info_show_status, "0").commit();
				return false;
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			t.printStackTrace();
			//prefs.edit().putString(get_info_show_status, "0").commit();
			return false;
		}
	}
	
}
