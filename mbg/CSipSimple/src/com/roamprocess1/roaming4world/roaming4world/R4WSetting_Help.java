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

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.utils.Compatibility;

public class R4WSetting_Help extends Activity{
	ListView list;
	String[] web = { "About", "FAQ" };
	Integer[] imageId = { R.drawable.roaminglogo, R.drawable.ic_button_contacts};
	private SharedPreferences prefs;
	private String stored_user_country_code, stored_user_mobile_no;
	private TextView tv_show_user_balance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r4wsetting);
		
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);

		
		setActionBar();
		
		TextView tv_showUserNumber = (TextView)findViewById(R.id.tv_setting_userNumber);
		String number = prefs.getString(stored_user_country_code, "Not Saved") + " - " + prefs.getString(stored_user_mobile_no, "Not Saved");
		tv_showUserNumber.setText("My Number : "+number.toString());
		
		tv_show_user_balance = (TextView)findViewById(R.id.tv_setting_userbalance);
		
		new MyAsyncTaskGetBalance(number.toString()).execute();
		
		CustomListAdapterSetting adapter = new CustomListAdapterSetting(R4WSetting_Help.this, web, imageId);
		list = (ListView) findViewById(R.id.lv_setting_options_one);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(web[+position].equals("About")){
					startActivity(new Intent(R4WSetting_Help.this, R4WAboutUs.class));
				}else if(web[+position].equals("FAQ")){
					Intent i = new Intent(R4WSetting_Help.this , HowToUse.class);
					i.putExtra("view", "FAQ");
					startActivity(i);
					
				}
			}
		});
	}
	
	
	@SuppressLint("NewApi") private void setActionBar() {
		// TODO Auto-generated method stub
		ActionBar ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
//		ab.setBackgroundDrawable(getResources().getDrawable(Color.parseColor("#189AD1")));
		ab.setTitle("Help");
		ab.setIcon(getResources().getDrawable(R.drawable.roaminglogo));
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
	
	public class MyAsyncTaskGetBalance extends AsyncTask<Void, Void, Boolean>{		
	    
		String user_number, balance = "0";
		
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