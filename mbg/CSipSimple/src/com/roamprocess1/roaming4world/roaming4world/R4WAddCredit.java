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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.stripepayment.PaymentActivity;
import com.roamprocess1.roaming4world.utils.Log;

public class R4WAddCredit extends Activity implements OnClickListener{

	
	Button  btn_get_rates , btn_ffty_addcredit , btn_frty_addcredit , 
			btn_thrty_addcredit , btn_twnty_addcredit , btn_ten_addcredit;
	TextView tv_bal_add_credit;
	ProgressBar pb_bal_add_credit;
	private String balance , stored_user_bal , stored_user_country_code , stored_user_mobile_no ;
	public SharedPreferences prefs;
	AsyncTaskBal asyncTaskBal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addcredit);
		setActionBar();
		init();
		setOnClick();
	}

	private void setOnClick() {
		// TODO Auto-generated method stub
		btn_get_rates.setOnClickListener(this);
		btn_ffty_addcredit.setOnClickListener(this);
		btn_frty_addcredit.setOnClickListener(this);
		btn_thrty_addcredit.setOnClickListener(this);
		btn_twnty_addcredit.setOnClickListener(this);
		btn_ten_addcredit.setOnClickListener(this);
	}

	private void init() {
		// TODO Auto-generated method stub
		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world",Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";
		
		btn_get_rates = (Button) findViewById(R.id.btn_get_rates);
		btn_ffty_addcredit = (Button) findViewById(R.id.btn_ffty_addcredit);
		btn_frty_addcredit = (Button) findViewById(R.id.btn_frty_addcredit);
		btn_thrty_addcredit = (Button) findViewById(R.id.btn_thrty_addcredit);
		btn_twnty_addcredit = (Button) findViewById(R.id.btn_twnty_addcredit);
		btn_ten_addcredit = (Button) findViewById(R.id.btn_ten_addcredit);
		tv_bal_add_credit = (TextView) findViewById(R.id.tv_bal_add_credit);
		pb_bal_add_credit = (ProgressBar) findViewById(R.id.pb_bal_add_credit);
		
		asyncTaskBal = new AsyncTaskBal();
		asyncTaskBal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent  paymentIntent;
		
		if(v.getId() == R.id.btn_ten_addcredit){
			paymentIntent= new Intent(R4WAddCredit.this,PaymentActivity.class);
			paymentIntent.putExtra("paymentValue", "10");
			startActivity(paymentIntent);
			
		}else if(v.getId() == R.id.btn_twnty_addcredit){
			
			paymentIntent= new Intent(R4WAddCredit.this,PaymentActivity.class);
			paymentIntent.putExtra("paymentValue", "20");
			startActivity(paymentIntent);
			
		}else if(v.getId() == R.id.btn_thrty_addcredit){
			paymentIntent= new Intent(R4WAddCredit.this,PaymentActivity.class);
			paymentIntent.putExtra("paymentValue", "30");
			startActivity(paymentIntent);
			
		}else if(v.getId() == R.id.btn_frty_addcredit){
			paymentIntent= new Intent(R4WAddCredit.this,PaymentActivity.class);
			paymentIntent.putExtra("paymentValue", "40");
			startActivity(paymentIntent);
		}else if(v.getId() == R.id.btn_ffty_addcredit){
			paymentIntent= new Intent(R4WAddCredit.this,PaymentActivity.class);
			paymentIntent.putExtra("paymentValue", "50");
			startActivity(paymentIntent);
		}else if(v.getId() == R.id.btn_get_rates){
			paymentIntent= new Intent(R4WAddCredit.this,HowToUse.class);
			paymentIntent.putExtra("view", "getCredit");
			startActivity(paymentIntent);
		}
		
	}
	
	private void setActionBar() {
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
		
		
	}
	
	public class AsyncTaskBal extends AsyncTask<Void, Void, Boolean>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

        	pb_bal_add_credit.setVisibility(ProgressBar.VISIBLE);
        	tv_bal_add_credit.setVisibility(TextView.GONE);
        	Log.setLogLevel(6);
			Log.d("AsyncTaskBal onPreExecute", "called");
        
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pb_bal_add_credit.setVisibility(ProgressBar.GONE);
			tv_bal_add_credit.setVisibility(TextView.VISIBLE);
			if(balance == null)
        		balance = "0";
        	prefs.edit().putString(stored_user_bal, "0").commit();
        	tv_bal_add_credit.setText("$ " + Double.parseDouble(balance));
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.setLogLevel(6);
			Log.d("AsyncTaskBal doInBackground", "Called");
			return (webservreqGetBalance()) ?  true : false;
		}
		
	}
	

	public class AsyncTaskGetBalance extends AsyncTask<Void, Void, Boolean>{		

				
		        @Override
		        protected void onPreExecute() {
		        	pb_bal_add_credit.setVisibility(ProgressBar.VISIBLE);
		        	tv_bal_add_credit.setVisibility(TextView.GONE);
		        	Log.setLogLevel(6);
					Log.d("AsyncTaskGetBalance onPreExecute", "called");
		        }
		
			@Override
			protected Boolean doInBackground(Void... params) {
				Log.setLogLevel(6);
				Log.d("AsyncTaskGetBalance doInBackground", "Called");
				return (webservreqGetBalance()) ?  true : false;
			}
			
			@Override
	        protected void onPostExecute(Boolean result) {
				pb_bal_add_credit.setVisibility(ProgressBar.GONE);
				tv_bal_add_credit.setVisibility(TextView.VISIBLE);
				if(balance == null)
	        		balance = "0";
	        	prefs.edit().putString(stored_user_bal, "0").commit();
	        	tv_bal_add_credit.setText("$ " + Double.parseDouble(balance));
	        }
	        
		}

	public boolean webservreqGetBalance() {

		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/balance-info/"
					+ "balance.php?contact="
					+ prefs.getString(stored_user_country_code, "0")
					+ prefs.getString(stored_user_mobile_no, "");

			Log.setLogLevel(6);
			Log.d("uurl", url);
			HttpPost httppost = new HttpPost(url);
			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				balance = responseBody;
				// Parse
				// JSONObject json = new JSONObject(responseBody);
				System.out.println("JSON response:balance" + balance);
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} catch (Exception t) {
			t.printStackTrace();
			return false;
		}

	}

}
