package com.roamprocess1.roaming4world.roaming4world;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.ui.recents.Recents;

public class MissedcallFragment extends SherlockFragment{

	private DBContacts dbContacts;
	private Cursor cursor, cursor_name;;
	private String[] number, name, count, time;
	MissedCallAdapter missedCallAdapter = null;
	private ListView lv_missedcall;
	View v;
	Button btn_callLog_allCalls, btn_callLog_MissedCalls;
	Intent i;
	TextView tv_no_missed_call;
	int row = 0;
	private ImageButton imgClearList;

	public SharedPreferences prefs;
	private String stored_user_mobile_no, stored_user_country_code;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.missedcall_fragment, container, false);

		prefs = getActivity().getSharedPreferences(
				"com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";

		btn_callLog_allCalls = (Button) v
				.findViewById(R.id.btn_callLog_allCalls);
		btn_callLog_MissedCalls = (Button) v
				.findViewById(R.id.btn_callLog_MissedCalls);
		tv_no_missed_call = (TextView) v.findViewById(R.id.tv_noMissed_call);
		lv_missedcall = (ListView) v
				.findViewById(R.id.lv_missed_Calls_fragment);
		dbContacts = new DBContacts(getActivity());

		lv_missedcall.setVisibility(ListView.VISIBLE);
		tv_no_missed_call.setVisibility(TextView.GONE);

		
		updateMissedcallDetails();
		add_missed_call_adapter();

		btn_callLog_allCalls.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				btn_callLog_allCalls.setBackgroundColor(Color.parseColor("#189AD1"));
				btn_callLog_MissedCalls.setBackgroundColor(Color.GRAY);

				// getFragmentManager().beginTransaction().replace(R.id.Frame_Layout,
				// new CallLogListFragment()).commitAllowingStateLoss();

			}
		});

		btn_callLog_MissedCalls.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				btn_callLog_MissedCalls.setBackgroundColor(Color.parseColor("#189AD1"));
				btn_callLog_allCalls.setBackgroundColor(Color.GRAY);

			}
		});

		lv_missedcall
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							final int position, long id) {

						/*
						 * 
						 * ImageView imgbtn=(ImageView)
						 * view.findViewById(R.id.ib_missedcall_createcall);
						 * imgbtn.setOnClickListener(new View.OnClickListener()
						 * {
						 * 
						 * @Override public void onClick(View arg0) { // TODO
						 * Auto-generated method stub RContactlist rContactlist=
						 * new RContactlist();
						 * rContactlist.r4wCall_Chat(number[position]); } });
						 * 
						 * LinearLayout linerlayDetails =(LinearLayout)
						 * view.findViewById(R.id.linerlayDetails);
						 * linerlayDetails.setOnClickListener(new
						 * View.OnClickListener() {
						 * 
						 * @Override public void onClick(View v) { // TODO
						 * Auto-generated method stub i = new
						 * Intent(getActivity(), R4WMissedcallDetails.class);
						 * i.putExtra("name", name[position]);
						 * i.putExtra("number", number[position]);
						 * getActivity().startActivity(i); } }); ImageView
						 * imageView1= (ImageView)
						 * view.findViewById(R.id.imageView1);
						 * imageView1.setOnClickListener(new
						 * View.OnClickListener() {
						 * 
						 * @Override public void onClick(View v) { // TODO
						 * Auto-generated method stub i = new
						 * Intent(getActivity(), R4WMissedcallDetails.class);
						 * i.putExtra("name", name[position]);
						 * i.putExtra("number", number[position]);
						 * getActivity().startActivity(i); } });
						 */
						i = new Intent(getActivity(),
								R4WMissedcallDetails.class);
						i.putExtra("name", name[position]);
						i.putExtra("number", number[position]);
						getActivity().startActivity(i);
						
						System.out.println("list is cleked");
					}
				});

		btn_callLog_MissedCalls.setBackgroundColor(Color.parseColor("#189AD1"));
		btn_callLog_allCalls.setBackgroundColor(Color.GRAY);

		return v;
	}

	/*
	 * public static MissedcallFragment newInstance(String text) {
	 * 
	 * MissedcallFragment f = new MissedcallFragment(); Bundle b = new Bundle();
	 * b.putString("msg", text);
	 * 
	 * f.setArguments(b);
	 * 
	 * return f; }
	 */

	private void updateMissedcallDetails() {

		dbContacts.openToRead();
		cursor = dbContacts.fetch_details_from_MIssedCall_Offline_Table();
		row = cursor.getCount();
		Log.d("CursorRow", cursor.getCount() + " !");
		if (row > 0) {
			cursor.moveToFirst();
			number = new String[row];
			name = new String[row];
			count = new String[row];
			time = new String[row];
			int i = 0;
			do {
				number[i] = cursor.getString(1).toString();
				time[i] = cursor.getString(2).toString();
				count[i] = cursor.getString(3).toString();

				Log.d(" 1 number " + i, number[i] + " !");
				Log.d(" 2 time " + i, time[i] + " !");
				Log.d(" 3 count " + i, count[i] + " !");

				cursor_name = dbContacts.fetch_contact_from_R4W(number[i]);

				if (cursor_name != null && cursor_name.getCount() > 0) {
					cursor_name.moveToFirst();
					name[i] = cursor_name.getString(cursor_name
							.getColumnIndex(DBContacts.R4W_CONTACT_NAME));
				} else {
					name[i] = number[i];
				}
				cursor_name.close();
				
				i++;
				

				
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		
		dbContacts.close();
	}

	private void add_missed_call_adapter() {
		if (row > 0) {
			if (missedCallAdapter == null) {
				missedCallAdapter = new MissedCallAdapter(getActivity(),
						number, name, count, time);
			} else {
				missedCallAdapter.clear();
				missedCallAdapter = new MissedCallAdapter(getActivity(),
						number, name, count, time);

			}
			lv_missedcall.setAdapter(missedCallAdapter);
			lv_missedcall.setItemsCanFocus(true);
			Recents.ICON_DELETE_SHOW = true;
		} else {
			lv_missedcall.setVisibility(ListView.GONE);
			tv_no_missed_call.setVisibility(TextView.VISIBLE);
			Recents.ICON_DELETE_SHOW = false;
		}
	}

	@SuppressWarnings("static-access")
	private void delete_missed_call_data() {
		lv_missedcall.setVisibility(ListView.GONE);
		tv_no_missed_call.setVisibility(TextView.VISIBLE);
		imgClearList.setVisibility(View.GONE);
		Recents.ICON_DELETE_SHOW = false;
		dbContacts.openToRead();

		Cursor cursor = dbContacts
				.fetch_details_from_MIssedCall_Offline_Table();
		String t = "";
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			t = cursor.getString(cursor
					.getColumnIndex(dbContacts.MissedCall_LAST_TIMESTAMP));
		}
		String nu = prefs.getString(stored_user_country_code, "")
				+ prefs.getString(stored_user_mobile_no, "");

		Log.d("t", t + " !");
		Log.d("nu", nu + " !");

		if (nu.equals("") || t.equals("")) {
			// Toast.makeText(getActivity(),
			// "Error in deleting, please try again",
			// Toast.LENGTH_SHORT).show();
		} else {
			new AsyncTaskDeleteMissedCallData(nu, "").execute();
		}
		dbContacts.delete_MissedCall_Data_from_Table();
		dbContacts.close();

	}

	public void dialogBoxDeleteMissedCall() {
		try {
			new AlertDialog.Builder(getActivity())
			.setTitle("Alert!")
			.setMessage(
					"Are you sure you want to delete missed call details")
			.setPositiveButton("Delete Anyway",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							delete_missed_call_data();
						}
					})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					}).show();

		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		imgClearList = (ImageButton) getActivity().getActionBar()
				.getCustomView().findViewById(R.id.imgClearList);
		if (tv_no_missed_call.getVisibility() == TextView.VISIBLE) {
			imgClearList.setVisibility(View.GONE);
		}
		/*
		 * imgClearList.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub dialogBoxDeleteMissedCall();
		 * 
		 * } });
		 */
	}

	class AsyncTaskDeleteMissedCallData extends AsyncTask<Void, Void, Boolean> {

		String number = "", timestamp = "";

		public AsyncTaskDeleteMissedCallData(String num, String time) {
			number = num;
			timestamp = time;
		}

		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		protected void onPostExecute(Boolean result) {
			if (result == true) {
				Toast.makeText(getActivity(),
						"Missed call details has been deleted",
						Toast.LENGTH_SHORT).show();
			}

			else {
				// Toast.makeText(getActivity(),
				// "Error in deleting, please try again",
				// Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub

			// userPic =
			// getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
			Log.d("doInBackgroud", "doInBackground");

			boolean flagurl = deleteMissedCallData(number, timestamp);

			if (flagurl == true) {
				return true;
			} else {
				return false;
			}
		}

	}

	public boolean deleteMissedCallData(String number2, String timestamp) {
		try {
			Log.d("deleteMissedCallData", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/missed_call_delete.php?contact="
					+ number2 + "&time=" + timestamp;

			Log.d("url", url + " #");
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
			JSONObject json = new JSONObject(responseBody);
			String response = json.getString("response");
			if (response.equals("Error")) {
				return false;
			} else if (response.equals("success")) {
				return true;
			} else {
				return false;
			}
		} catch (Throwable t) {

			t.printStackTrace();

			return false;
		}

	}

}
