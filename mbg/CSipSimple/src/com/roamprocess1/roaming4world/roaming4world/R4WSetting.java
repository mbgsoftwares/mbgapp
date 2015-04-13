package com.roamprocess1.roaming4world.roaming4world;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;

public class R4WSetting extends Activity{
	ListView list;
	String[] web = { "Help", "Profile", "Status"};
	Integer[] imageId = { R.drawable.ic_button_contacts, R.drawable.ic_button_contacts, R.drawable.ic_button_contacts};

	View v;
	private String stored_user_mobile_no, stored_user_country_code;
	SharedPreferences prefs;
	TextView tv_show_user_balance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.r4wsetting);
		
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);

		android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		ImageButton imgbackbtn = (ImageButton) customView.findViewById(R.id.imgbackbtn);
		imgbackbtn.setClickable(true);
		imgbackbtn.setVisibility(View.VISIBLE);
		actionBar.setCustomView(customView);
		imgbackbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		
		
		TextView tv_showUserNumber = (TextView) findViewById(R.id.tv_setting_userNumber);
		String number = prefs.getString(stored_user_country_code, "Not Saved") + " - " + prefs.getString(stored_user_mobile_no, "Not Saved");
		tv_showUserNumber.setText("My Number : "+number.toString());
		tv_show_user_balance = (TextView)findViewById(R.id.tv_setting_userbalance);
		
		String num = prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "Not Saved");
		
		if(!num.equals("")){
			new MyAsyncTaskGetBalance(num).execute();
		}
		CustomListAdapterSetting adapter = new CustomListAdapterSetting(this, web, imageId);
		list = (ListView) findViewById(R.id.lv_setting_options_one);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(web[+position].equals("Help")){
				startActivity(new Intent(R4WSetting.this, R4WSetting_Help.class));
					//startActivityForResult(new Intent(SipManager.ACTION_UI_PREFS_GLOBAL), 1);
				}else if(web[+position].equals("Profile")){
					startActivity(new Intent(R4WSetting.this, R4WSetting_Profile.class));
				}else if(web[+position].equals("Status")){
					startActivity(new Intent(R4WSetting.this, R4WStatusUpdate.class));
				}
			}
		});
		
		
	}
	
	
	public class MyAsyncTaskGetBalance extends AsyncTask<Void, Void, Boolean>{		
	    
		String user_number, balance="0";
		
		MyAsyncTaskGetBalance(String number){
			user_number = number;
		}
		
	    @Override
	    protected void onPostExecute(Boolean result) {
	    	
	    tv_show_user_balance.setText("Balance : " + balance + " $");
	    }
	    
	    @Override
	    protected void onPreExecute() {}

	    @Override
	    protected Boolean doInBackground(Void... params) {
	    	if(webservreqGetBalance()){
	    		return true;
			}
			else{
				return false;
			}
	    }
	    
	    public boolean webservreqGetBalance(){

	    	try {
	            HttpParams p = new BasicHttpParams();
	            p.setParameter("user", "1");
	            HttpClient httpclient = new DefaultHttpClient(p);
	            //http://ip.roaming4world.com/esstel/balance-info/balance.php?contact=9132
	            
	            String url = "http://ip.roaming4world.com/esstel/balance-info/" + 
	                    "balance.php?contact="+user_number;
	            
	            
	            HttpPost httppost = new HttpPost(url);
	            // Instantiate a GET HTTP method
	            try {
	                Log.i(getClass().getSimpleName(), "send  task - start");
	                
	                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	                nameValuePairs.add(new BasicNameValuePair("user", "1"));
	                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                String responseBody = httpclient.execute(httppost, responseHandler);
	                balance=responseBody;
	                // Parse
	                JSONObject json = new JSONObject(responseBody);
	                System.out.println("JSON response:balance"+json);
	              return true;
	            } 
	            catch(UnknownHostException e)
	            {
	            	e.printStackTrace();
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
	    	catch (Exception t) {
	    		t.printStackTrace();
	            return false;
	        }    	
	    
	    }
	    
	}

	
	
}