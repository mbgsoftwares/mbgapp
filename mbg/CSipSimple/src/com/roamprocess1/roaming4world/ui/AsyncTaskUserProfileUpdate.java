package com.roamprocess1.roaming4world.ui;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.roamprocess1.roaming4world.utils.Log;

public class AsyncTaskUserProfileUpdate extends AsyncTask<Void, Void, Boolean> {
	
	Context context;
	String number, actiontype, name;
	int action;
	
	public AsyncTaskUserProfileUpdate(Context cont, String num, String username, int i) {
        super();
        // do stuff
        context = cont;
        number = num;
        name = username;
        action = i;
    }
	
	
	ProgressDialog mProgressDialog;

	@SuppressWarnings("static-access")
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Progressing...");
		mProgressDialog.show();
		mProgressDialog.setCancelable(false);

	}

protected void onPostExecute(Boolean result) {
	
	if(result == true)
		Toast.makeText(context, "Username successfully updated", Toast.LENGTH_SHORT).show();
	//else
		//Toast.makeText(context, "Error in updating the Username", Toast.LENGTH_SHORT).show();
		
	mProgressDialog.dismiss();
	
}


@Override
protected Boolean doInBackground(Void... params) {
	// TODO Auto-generated method stub
	
//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
	Log.d("doInBackgroud", "doInBackground");
	
	if(webServiceUsernameUpdate()){
		return true;
	}else{
		return false;
		}
	}


	public boolean webServiceUsernameUpdate() {
	

		// TODO Auto-generated method stub

		if(action == 0)
			actiontype = "create";
		else 
			actiontype = "update";


		try {
			Log.d("webServiceFlightInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/profile-data/profile_status_name.php?"
						  + "self_contact=" + number
						  + "&type=name"
						  + "&action=" + actiontype
						  + "&value=" + Uri.encode(name);
						
						  
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
