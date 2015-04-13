package com.roamprocess1.roaming4world.roaming4world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.utils.Log;

public class R4WStatusUpdate extends SherlockFragmentActivity implements View.OnClickListener{

	View rootView;
	Button updateStatus;
	SharedPreferences prefs, prefUserInfo;
	String stored_user_mobile_no, stored_user_country_code, userInfo = "UserInfo", stored_user_status, user_status = "Hey There! I am using R4W",
			stored_user_status_1, stored_user_status_2, stored_user_status_3;
	ArrayList<String> dataArray_left = new ArrayList<String>();
	ListView status;
	TextView showStatus;
	String valueStatus1, valueStatus2, valueStatus3;
	
	
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.r4wstatusupdate);	
		
		prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_user_status = "com.roamprocess1.roaming4world.user_status";
		prefUserInfo = getSharedPreferences(userInfo, Context.MODE_PRIVATE);
		stored_user_status_1 = "com.roamprocess1.roaming4world.user_status_1";
		stored_user_status_2 = "com.roamprocess1.roaming4world.user_status_2";
		stored_user_status_3 = "com.roamprocess1.roaming4world.user_status_3";

		setActionBar();
		Initializer();
		updateStatus.setOnClickListener(this);
		fill_status_data();
		
		showStatus.setText(prefs.getString(stored_user_status, user_status));
		
		final StatusArrayAdapter status_adapter = new StatusArrayAdapter(R4WStatusUpdate.this, R.layout.simple_list_item_custom, dataArray_left);
		status.setAdapter(status_adapter);
		status.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view,
		          int position, long id) {
		    	  
		    	  String value = (String) parent.getItemAtPosition(position);
		    	  prefs.edit().putString(stored_user_status, value).commit();
		    	  showStatus.setText(value);
		    	  new AsyncTaskUpdateStatus().execute();
		      }

		    });
		
	}
	
	public void setActionBar() {
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
	}

	private void Initializer() {
		// TODO Auto-generated method stub
//		storedStatus = new StoredStatus();
		status = (ListView) findViewById(R.id.lv_statusValues);
		updateStatus = (Button) findViewById(R.id.btn_updateStatusValue);
		showStatus  = (TextView) findViewById(R.id.tv_showStatus);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_updateStatusValue:
			dialogboxUpdateStatus();
			break;
		
		}
		
	}

	
	
	
	
	public void dialogboxUpdateStatus() {

		
	     
	
		final Dialog dialog = new Dialog(R4WStatusUpdate.this, android.R.style.Theme_Light); 
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		dialog.setContentView(R.layout.update_status_dialog); 
		dialog.show();
		
		Button btnOk = (Button) dialog.findViewById(R.id.db_btn_ok);
		Button btnCancel = (Button) dialog.findViewById(R.id.db_btn_cancel);
		Button btn_updatestatus = (Button) dialog.findViewById(R.id.db_btn_updateStatus);
		final EditText edtstatusValue = (EditText) dialog.findViewById(R.id.db_et_statusValue);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				//Toast.makeText(getActivity(), "done", Toast.LENGTH_SHORT).show();
				if(edtstatusValue.getText() != null)
				{
					String value = edtstatusValue.getText().toString();
					prefs.edit().putString(stored_user_status, value).commit();
					showStatus.setText(value);
					dialog.dismiss();
					updateStatus(value);
					new AsyncTaskUpdateStatus().execute();
				}else{
					Toast.makeText(R4WStatusUpdate.this, "Please enter the status", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(R4WStatusUpdate.this, "Cancel", Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});

		btn_updatestatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				Toast.makeText(R4WStatusUpdate.this, "update", Toast.LENGTH_SHORT).show();
			}
		});
	
		
	
		
	}
	
	class AsyncTaskUpdateStatus extends AsyncTask<Void, Void, Boolean> {
		
		ProgressDialog mProgressDialog;

		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(R4WStatusUpdate.this);
			mProgressDialog.setMessage("Progressing...");
			mProgressDialog.show();

		}

	protected void onPostExecute(Boolean result) {
		if(result == true)
			Toast.makeText(R4WStatusUpdate.this, "Status successfully updated", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(R4WStatusUpdate.this, "Error in updating the status", Toast.LENGTH_SHORT).show();
			
		try {
			if(mProgressDialog != null){
				if(mProgressDialog.isShowing()){
					mProgressDialog.dismiss();
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}


	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
	//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
		Log.d("doInBackgroud", "doInBackground");
		
		if(webServiceStatusUpdate()){
			return true;
		}else{
			return false;
			}
		}
	
	}


	public boolean webServiceStatusUpdate() {
		// TODO Auto-generated method stub

		


		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/profile-data/profile_status_name.php?"
						  +"self_contact="
						  +prefs.getString(stored_user_country_code, "NoValue")
						  +prefs.getString(stored_user_mobile_no, "NoValue")
						  +"&type=status"
						  +"&action=update"
						  +"&value="+Uri.encode(prefs.getString(stored_user_status, user_status));
						
						  
			Log.d("url", url + " #");
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			
			if(json.getString("response").equalsIgnoreCase("Success"))
			{
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
	
	 private class StatusArrayAdapter extends ArrayAdapter<String> {

		    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		    public StatusArrayAdapter(Context context, int textViewResourceId,
		        List<String> objects) {
		      super(context, textViewResourceId, objects);
		      for (int i = 0; i < objects.size(); ++i) {
		        mIdMap.put(objects.get(i), i);
		      }
		    }

		    @Override
		    public long getItemId(int position) {
		      String item = getItem(position);
		      return mIdMap.get(item);
		    }

		    @Override
		    public boolean hasStableIds() {
		      return true;
		    }

		  }

	
	
	public void fill_status_data() {

		dataArray_left.clear();
		
		
		valueStatus1 = prefs.getString(stored_user_status_1, "");
		valueStatus2 = prefs.getString(stored_user_status_2, "");
		valueStatus3 = prefs.getString(stored_user_status_3, "");
		
		Log.d("valueStatus1", valueStatus1+" !");
		Log.d("valueStatus2", valueStatus2+" !");
		Log.d("valueStatus3", valueStatus3+" !");
			if(!valueStatus3.equalsIgnoreCase(""))
		{
			dataArray_left.add(valueStatus3);
		}	
		if(!valueStatus2.equalsIgnoreCase(""))
		{	
			dataArray_left.add(valueStatus2);
		}
		if(!valueStatus1.equalsIgnoreCase(""))
		{
			dataArray_left.add(valueStatus1);
		}
		
		dataArray_left.add("Available");
		dataArray_left.add("Busy");
		dataArray_left.add("At school");
		dataArray_left.add("At movies");
		dataArray_left.add("At work");
		dataArray_left.add("In a meeting");
		dataArray_left.add("Urgents calls only");
		dataArray_left.add("Sleeping");

	
	}
	
	public void updateStatus(String value) {
		
		valueStatus1 = prefs.getString(stored_user_status_1, "");
		valueStatus2 = prefs.getString(stored_user_status_2, "");
		valueStatus3 = prefs.getString(stored_user_status_3, "");
		
		
		
		if(!valueStatus1.equals("") && !valueStatus2.equals("") && !valueStatus3.equals("")){
			Log.d("ifcalled", "1");
			prefs.edit().putString(stored_user_status_1, valueStatus2).commit();
			prefs.edit().putString(stored_user_status_2, valueStatus3).commit();
			prefs.edit().putString(stored_user_status_3, value).commit();
		}else if(!valueStatus1.equals("") && !valueStatus2.equals(""))
			{
			Log.d("ifcalled", "2");
			prefs.edit().putString(stored_user_status_3, value).commit();
		}else if(!valueStatus1.equals("")){
			Log.d("ifcalled", "3");
			prefs.edit().putString(stored_user_status_2, value).commit();
		}else{
			Log.d("ifcalled", "4");
			prefs.edit().putString(stored_user_status_1, value).commit();
			
		}
	}
	
}
