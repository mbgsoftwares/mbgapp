package com.roamprocess1.roaming4world;

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

import android.os.AsyncTask;

public class AsyncTaskReceiverAwake extends AsyncTask<Void, Void, Boolean> {

	String phoneNumberToCall;

	public AsyncTaskReceiverAwake(String number) {
		phoneNumberToCall = number;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		System.out.println("Make Awake Complete");
	}

	@Override
	protected void onPreExecute() {
		System.out.println("Make Awake Initiate");
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		if (webservReceiverAwake(phoneNumberToCall)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean webservReceiverAwake(String phoneNumber) {
		try {

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);

			String url = "http://ip.roaming4world.com/esstel/gcm/"
					+ "send_gcm_message.php?phoneNo=" + phoneNumber;
			System.out.println("Make Awake Processing1 " + url);
			HttpPost httppost = new HttpPost(url);
			System.out.println("Make Awake Processing2");
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				System.out.println("Make Awake Processing3");
				return true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
				// unknownhost = true;
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
		} catch (Throwable t) {

			t.printStackTrace();
			return false;
		}
	}

}
