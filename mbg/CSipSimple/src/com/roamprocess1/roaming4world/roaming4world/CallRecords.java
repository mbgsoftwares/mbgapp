package com.roamprocess1.roaming4world.roaming4world;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;

public class CallRecords extends SherlockFragment {
	private ListView listView1;
	String s_fetch_pin;
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	int accountPage,callRecords,reRunCode;
	private View rootView ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		 rootView = inflater.inflate(R.layout.callrecords, container, false);
		
		/*android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		ImageButton imgbackbtn = (ImageButton) customView.findViewById(R.id.imgbackbtn);
		imgbackbtn.setClickable(false);
		imgbackbtn.setVisibility(View.INVISIBLE);
		actionBar.setCustomView(customView);
		setContentView(R.layout.r4waccount);
		setContentView(R.layout.callrecords);

		s_fetch_pin = getIntent().getStringExtra("string_pin");

		new MyAsyncTaskCDR().execute();
		TextView tv=new TextView(getApplicationContext());
		tv.setText("No Records Found");
		
		Bundle mapIntent = getIntent().getExtras();
		accountPage=mapIntent.getInt("backPageAccount");
		callRecords=mapIntent.getInt("backPageCallRecord");
		reRunCode=mapIntent.getInt("backPageReRunCode");
		
		Log.d("home callRecords  reRunCode", accountPage+" "+callRecords+""+reRunCode+"");
*/
		
		
		/*
		 * CallRecord weather_data[] = new CallRecord[] {
		 * 
		 * new CallRecord("Cloudy"), new CallRecord("Showers"), new
		 * CallRecord("Snow"), new CallRecord("Storm"), new CallRecord("Sunny")
		 * };
		 */

		// Log.v("array list a1", al1.toString());
		// CallRecord weather_data = new
		// CallRecord(getApplicationContext(),al1,al2,al3,al4,al5,al6);
		return rootView;
	}

	private class MyAsyncTaskCDR extends AsyncTask<Void, Void, String> {
		ProgressDialog mProgressDialog1;

		@Override
		protected void onPostExecute(String result) {
			mProgressDialog1.dismiss();
			String value = result;
			prepareListData(value);
			if (value.isEmpty()) {
				Toast.makeText(getActivity(), "No Records Found", Toast.LENGTH_SHORT).show();
			}
			
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog1 = ProgressDialog.show(getActivity(),"Loading...", "Data is Loading...");
		}

		@Override
		protected String doInBackground(Void... params) {
			String val = webservreqGETCDR();
			if (val != "") {
				
				return val;
			} else {
				// Toast.makeText(this,
				// "Request failed",Toast.LENGTH_LONG).show();
				return "false";
			}
		}
	}

	public String webservreqGETCDR() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);

			String url = "http://ip.roaming4world.com/esstel/serv_viewcdr_pin.php?pin_no="
					+ s_fetch_pin;// 14954461";
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

				// Parse
				JSONObject json = new JSONObject(responseBody);
				JSONArray jArray = json.getJSONArray("call_log");
				System.out.println(jArray);
				return responseBody;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "false";
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return "false";
		}
	}

	private void prepareListData(String val) {
		String value = val;
		if(val.isEmpty())
		{
			TextView txtNoRecords=(TextView) rootView.findViewById(R.id.txtNoRecords);
			txtNoRecords.setText("No Records Found");
		}
		System.out.println(value);
		expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
		// preparing list data
		prepareListData1(value);
		listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader,
				listDataChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);
	}

	private void prepareListData1(String val) {
		String value = val;
		System.out.println(value);
		JSONObject json1;
		try {
			json1 = new JSONObject(value);
			JSONArray jArray = json1.getJSONArray("call_log");
			listDataHeader = new ArrayList<String>();
			listDataChild = new HashMap<String, List<String>>();

			// Adding child dat
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject jArray1 = jArray.getJSONObject(i);
				String s1 = jArray1.getString("src_username");
				String s2 = jArray1.getString("dst_username");
				String s3 = jArray1.getString("call_start_time");
				String s4 = jArray1.getString("duration");
				String s5 = jArray1.getString("cost");
				String s6 = jArray1.getString("rated");
				Log.d("value", s4);

				int s4int = Integer.parseInt(s4);
				int mins = s4int / 60;
				int secs = s4int - mins * 60;

				String lenChkStr = Integer.toString(secs);
				int lengthOfSecs = lenChkStr.length();

				if (lengthOfSecs == 1) {
					s4 = mins + ":0" + secs;
				} else {
					s4 = mins + ":" + secs;
				}

				int s5int = Integer.parseInt(s5);
				Log.d("CostInt", Integer.toString(s5int));
				float costFloat = (float) s5int / 100;
				Log.d("CostInt", Float.toString(costFloat));
				s5 = "$ " + String.format("%.2f", costFloat);
				;
				Log.d("CostInt", s5);
				listDataHeader.add("Destination : " + s2 + "\n" + "Date : "
						+ s3);

				List<String> top250 = new ArrayList<String>();
				top250.add("Source   " + s1);

				top250.add("Duration   " + s4);
				top250.add("Cost   " + s5);
				top250.add("Rated   " + s6);

				listDataChild.put(listDataHeader.get(i), top250); // Header,
																	// Child
																	// data
			}

			/*
			 * List<String> top250 = new ArrayList<String>();
			 * top250.add("The Shawshank Redemption");
			 * top250.add("The Godfather");
			 * top250.add("The Godfather: Part II"); top250.add("Pulp Fiction");
			 * top250.add("The Good, the Bad and the Ugly");
			 * top250.add("The Dark Knight"); top250.add("12 Angry Men");
			 * 
			 * List<String> nowShowing = new ArrayList<String>();
			 * nowShowing.add("The Conjuring");
			 * nowShowing.add("Despicable Me 2"); nowShowing.add("Turbo");
			 * nowShowing.add("Grown Ups 2"); nowShowing.add("Red 2");
			 * nowShowing.add("The Wolverine");
			 * 
			 * List<String> comingSoon = new ArrayList<String>();
			 * comingSoon.add("2 Guns"); comingSoon.add("The Smurfs 2");
			 * comingSoon.add("The Spectacular Now");
			 * comingSoon.add("The Canyons"); comingSoon.add("Europa Report");
			 * 
			 * listDataChild.put(listDataHeader.get(0), top250); // Header,
			 * Child data listDataChild.put(listDataHeader.get(1), nowShowing);
			 * listDataChild.put(listDataHeader.get(2), comingSoon);
			 */

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Adding child data

	}
	
	/*
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if(accountPage==1||callRecords==2||reRunCode==3)
		{
			Intent intent = new Intent(this, R4wMapGUI.class);
			startActivity(intent);
			
		}else{
		
		Intent intent = new Intent(this, R4wHome.class);
		startActivity(intent);
		}
		finish();
	}
	*/
}
