package com.roamprocess1.roaming4world.roaming4world;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;

public class Promo_activity extends Activity{

	/*
	 * Global Variables declaration Starts
	 */
	
	EditText email,phone_no;
	Spinner country;
	CustomAdapter adapter;
	public  ArrayList<SpinnerModel> CustomListViewValuesArr = new ArrayList<SpinnerModel>();
	Promo_activity activity = null;
	String country_name;
	String promo_email, promo_phone_no;
	Boolean backbutton_status=true;      //This variable is used for denoting whether to disable back button or not and true because disable only on notvalid
	ArrayList al;
	Cursor data_cc;
	String valid,msg,pin_no;
	private SQLiteAdapter mySQLiteAdapter;
	
	/*
	 * Global Variables declaration Stops
	 */
	
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
	        setContentView(R.layout.promo_activity);
	        
	        /*
	         * Start of promotional block working  
	         */
	        mySQLiteAdapter = new SQLiteAdapter(this);
	    	mySQLiteAdapter.openToRead();
	    	
	    	data_cc = mySQLiteAdapter.fetch_country_codes();
	    	
	        email=(EditText) findViewById(R.id.promo_email_edittext);
	        phone_no=(EditText) findViewById(R.id.promo_phone_no_edittext);
	        activity  = this;
	        Spinner  SpinnerExample = (Spinner) findViewById(R.id.promo_spinner);
	        setListData();
	        Resources res = getResources();
	        adapter = new CustomAdapter(activity, R.layout.spinner_rows, CustomListViewValuesArr,res, this);	        
	        // Set adapter to spinner
	        SpinnerExample.setAdapter(adapter);
	        Log.d("testing","step 4 ok");
	        // Listener called when spinner item selected
	        SpinnerExample.setOnItemSelectedListener(new OnItemSelectedListener() {
	            @Override
	            public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
	               
	            	country_name     = ((TextView) v.findViewById(R.id.txtVcountryName)).getText().toString();
	                
	            }
	            @Override
	            public void onNothingSelected(AdapterView<?> parentView) {
	                //your code here
	            }
	        });       
	     
	        
	        //
	        
	 }
	 
	 
	 /*
	  * Block called after clicking on get your pin button
	  */
	 
	 public void getpin(View v)
	 {
		 
		 promo_email=email.getText().toString();
		 promo_phone_no=phone_no.getText().toString();
		 
		 if(promo_phone_no.equals(""))
		 {
			 Toast.makeText(Promo_activity.this,getResources().getString(R.string.promo_not_entered_phoneno), Toast.LENGTH_LONG).show();
		 }
		 else if(promo_email.equals(""))
		 {
			
			 Toast.makeText(Promo_activity.this,getResources().getString(R.string.promo_not_entered_email), Toast.LENGTH_LONG).show();
		 }
		 else if(country_name.equals("Countries"))
		 {
			 Toast.makeText(Promo_activity.this,getResources().getString(R.string.Promo_not_selected_country), Toast.LENGTH_LONG).show();
		 }
		 else if(!promo_email.equals(""))
		 {
			 String pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		      // Create a Pattern object
		      Pattern r = Pattern.compile(pattern);

		      // Now create matcher object.
		      Matcher m = r.matcher(promo_email);
		      
		      if(m.find())
		      {
		    	  new MyAsyncTaskGetPromoPin().execute();
		      }
		      else
		      {
		    	  Toast.makeText(Promo_activity.this,getResources().getString(R.string.promo_not_valid_email), Toast.LENGTH_LONG).show();
		      }
		 }
		 
		 
	 }
	 
	 /*
	  * Method called by getpin method
	  */

	 public class MyAsyncTaskGetPromoPin extends AsyncTask<Void, Void, Boolean>
	    {        
		 ProgressDialog mProgressDialog3;
			private String json1;

			
	        @Override
	        public void onPreExecute() {
	            mProgressDialog3 = ProgressDialog.show(Promo_activity.this, getResources().getString(R.string.loading), getResources().getString(R.string.data_loading));
	        }

	        @Override
	        public Boolean doInBackground(Void... params) {
	            if(webservreqGetPromoPin()){
	                Log.d("yay","SUCCESS");
	               
	                return true;
	            }
	            else{
	                Log.d("err","ERROR");
	                return false;
	            }
	        }
	            public void onPostExecute(Boolean result) { 
	                // dismiss the dialog once done 
	            	super.onPostExecute(result);
	            	mProgressDialog3.dismiss();
	            if(result)
	            {
	            	/*
	            	 * Block to be implemented for the on success result 
	            	 */
	            	if(valid.equals("no"))
	            	{
	            		backbutton_status=false;
		            	AlertDialog.Builder alertDialogBuilder_promo_result = new AlertDialog.Builder(Promo_activity.this);
		            	alertDialogBuilder_promo_result.setTitle(getResources().getString(R.string.message));
		            	alertDialogBuilder_promo_result
		      				.setMessage(msg)
		      				.setCancelable(false)
		      				.setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
		      					public void onClick(DialogInterface dialog,int id) {
		      						Intent intent1=new Intent(Promo_activity.this,R4wHome.class);
		      						intent1.putExtra("valid", false);
		      						Log.d("put extra valid","yes");
		      						setResult(RESULT_OK, intent1);
		      						finish();
		      					}
		      		        });
		      				AlertDialog alertDialog_send = alertDialogBuilder_promo_result.create();
		      				alertDialog_send.show();
	            	}
	            	else if(valid.equals("yes"))
	            	{
	            		Intent intent1=new Intent(Promo_activity.this,R4wHome.class);
	            		intent1.putExtra("valid", true);
	            		intent1.putExtra("pin_no", pin_no);
	            		setResult(RESULT_OK,intent1);
	            		finish();
	            	}
	            	else if(valid.equals("noyes"))
	            	{
	            		backbutton_status=false;
		            	AlertDialog.Builder alertDialogBuilder_promo_result = new AlertDialog.Builder(Promo_activity.this);
		            	alertDialogBuilder_promo_result.setTitle(getResources().getString(R.string.message));
		            	alertDialogBuilder_promo_result
		      				.setMessage(msg)
		      				.setCancelable(false)
		      				.setPositiveButton(getResources().getString(R.string.ok),new DialogInterface.OnClickListener() {
		      					public void onClick(DialogInterface dialog,int id) {
		      						Intent intent1=new Intent(Promo_activity.this,R4wHome.class);
		      						intent1.putExtra("valid", false);
		      						Log.d("put extra valid","yes");
		      						setResult(RESULT_OK, intent1);
		      						finish();
		      					}
		      		        });
		      				AlertDialog alertDialog_send = alertDialogBuilder_promo_result.create();
		      				alertDialog_send.show();
	            	}
		        		} 
	            else
	            {
	            	
	            }
	        
	    }
	    }
	 
	 
	 /*
	  * Block Called by MyAsyncTaskGetPromoPin
	  */
	    
	 public boolean webservreqGetPromoPin(){
	    	try {
	            //HttpParams httpParams = new BasicHttpParams();
	            //HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
	            //HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);            
	            HttpParams p = new BasicHttpParams();
	            p.setParameter("user", "1");
	            
	           
	            // Instantiate an HttpClient
	            HttpClient httpclient = new DefaultHttpClient(p);
	            
	            TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
	            String imei=mngr.getDeviceId();
	            
	            String country_name1_name=country_name.replaceAll("\\s","%20");
				String url = "http://ip.roaming4world.com/esstel/" + 
	                         "promotional_details.php?email="+promo_email+"&phone_no="+promo_phone_no+"&cname="+URLEncoder.encode(country_name,"UTF-8")+"&phone_type=AND"+"&phone_id="+imei;
	            Log.d("The URL",url);
				HttpPost httppost = new HttpPost(url);

	            // Instantiate a GET HTTP method
	            try {
	                Log.i(getClass().getSimpleName(), "send  task - start");
	                
	                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	                nameValuePairs.add(new BasicNameValuePair("user", "1"));
	                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	                ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                String responseBody = httpclient.execute(httppost, responseHandler);
	             
	                Log.d("deact",responseBody);
	                // Parse
	                JSONObject json = new JSONObject(responseBody);
	                valid=json.getString("valid");
	                msg=json.getString("msg");
	                pin_no=json.getString("pin_no");
	                Log.d("deact","c");
	                return true;
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
	            //Toast.makeText(this, "Request failed: " + t.toString(),Toast.LENGTH_LONG).show();
	            return false;
	        }    	
	    }
	 
	 
	 /*
	  * Block for implementing back pressed button details
	  */
	 public void onBackPressed() {
		 if(!backbutton_status)
		 {
			 
		 }
		 else
		 {
			 super.onBackPressed();
		 }
	 }
	 public void setListData()
	    {
	         
	        // Now i have taken static values by loop.
	        // For further inhancement we can take data by webservice / json / xml;
	    	al = new ArrayList();
		    
		    data_cc.moveToFirst();
		    while(!data_cc.isAfterLast()) {	    
		        String mTitleRaw = data_cc.getString(data_cc.getColumnIndex(SQLiteAdapter.CC_COUNTRY_NAME));
		        al.add(mTitleRaw);
		        Log.d("mtitle", mTitleRaw);
		        data_cc.moveToNext();
		        Log.d("mtitle 1", "Test 1");
		        final SpinnerModel sched = new SpinnerModel();
		        Log.d("mtitle 1", "Test 2");
	            /******* Firstly take data in model object ******/
	             sched.setCountyName(mTitleRaw);
	             Log.d("mtitle 1", "Test 3");
	             sched.setImage(mTitleRaw);
	             Log.d("mtitle 1", "Test 4");
	             sched.setUrl(mTitleRaw);
	             Log.d("mtitle 1", "Test 5");
	              
	          /******** Take Model Object in ArrayList **********/
	          CustomListViewValuesArr.add(sched);
		    }
		    /*
		    String[] countries_string = (String[]) al.toArray(new String[al.size()]);
		    */
	         
	    }
}
