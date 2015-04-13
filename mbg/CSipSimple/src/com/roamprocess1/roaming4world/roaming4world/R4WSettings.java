package com.roamprocess1.roaming4world.roaming4world;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.utils.Log;

public class R4WSettings extends SherlockFragment implements View.OnClickListener{

	View rootView;
	Button removeProfilePic, updateStatus;
	SharedPreferences prefs, prefUserInfo;
	String stored_user_mobile_no, stored_user_country_code, userInfo = "UserInfo";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.r4wsettings, container, false);	
		
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		prefUserInfo = getActivity().getSharedPreferences(userInfo, Context.MODE_PRIVATE);
		
		Initializer();
		removeProfilePic.setOnClickListener(this);
		updateStatus.setOnClickListener(this);
		return rootView;
	}

	private void Initializer() {
		// TODO Auto-generated method stub
		removeProfilePic = (Button) rootView.findViewById(R.id.btn_removeProfilePic);
		updateStatus = (Button) rootView.findViewById(R.id.btn_updateStatus);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_removeProfilePic:
			dialogboxRemoveImage();
			break;
		case R.id.btn_updateStatus:
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new R4WStatusUpdate()).commit();
			break;
		
		}
		
	}

	
	public void dialogboxRemoveImage() {

		new AlertDialog.Builder(getActivity())
	    .setTitle("Remove Profile Image")
	    .setMessage("Are you sure you want to delete Profile Image?")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	prefUserInfo.edit().putString("userImagePath","NoAddress").commit();
	        	new AsyncTaskRemoveImage().execute();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     }).show();
	     
	
	
		
	}
	
	class AsyncTaskRemoveImage extends AsyncTask<Void, Void, Boolean> {
		
		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

	protected void onPostExecute(Boolean result) {
		if(result == true)
			Toast.makeText(getActivity(), "Contact Images successfully Removed", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getActivity(), "Error Removing Contact Images", Toast.LENGTH_SHORT).show();
			
		
	}


	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
	//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
		Log.d("doInBackgroud", "doInBackground");
		
		if(webServiceRemoveImageUrl()){
			return true;
		}else{
			return false;
			}
		}
	
	}


	public boolean webServiceRemoveImageUrl() {
		// TODO Auto-generated method stub

		


		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_delete.php?self_contact="
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
	
	
	
}
