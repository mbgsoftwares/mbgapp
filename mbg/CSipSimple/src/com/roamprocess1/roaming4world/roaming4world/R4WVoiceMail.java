package com.roamprocess1.roaming4world.roaming4world;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
public class R4WVoiceMail extends SherlockFragment implements MediaPlayerControl {
	private View rootView;
	MySwitch swtActivateOrDeactivate;
	private String pinNumnberfromCurrentFrag, voiceDownload,
			getVoiceMailDownload, getCountryCodePref, didWithoutZeo,
			stored_user_country_code, getDIDNumberPref, DIDNumberForVoiceMail,
			ActivationStatus, DeActivationStatus, pinNumberforVoiceMail,
			getPinNumber, GetCurrentVoiceMailStatus;
	private SharedPreferences prefs;
	private MediaPlayer mMediaPlayer;
	private MediaController mMediaController;
	private boolean b;
	private TextView txtVoiceCalls,txtVoicemail;
	private Typeface bebas, nexaNormal;

	private File voicemail, roaming4worldfolder;
	ArrayList<String> dataArray_left = new ArrayList<String>();
	ArrayList<Object> objectArray_left = new ArrayList<Object>();

	ArrayList<String> dataArray_download = new ArrayList<String>();
	ArrayList<Object> objectArray_download = new ArrayList<Object>();

	ArrayList<String> dataArray_duration = new ArrayList<String>();
	ArrayList<Object> objectArray_duration = new ArrayList<Object>();

	ArrayList<String> dataArray_callerid = new ArrayList<String>();
	ArrayList<Object> objectArray_callerid = new ArrayList<Object>();
	
	
	ArrayList<String> dataArray_VoiceMailTime = new ArrayList<String>();
	ArrayList<Object> objectArray_VoiceMailTime = new ArrayList<Object>();
	

	// ArrayList<Integer> downloadVoiceArrayList=new ArrayList<Integer>();

	int[] list = new int[10000];
	StringBuilder strbuilder = new StringBuilder();
	String[] downloadlist;

	Editor editor;
	private Handler mHandler = new Handler();
	DownloadAknowlegeMent downloadAknow;

	ListView VoiceMailListView;
	ListItemsAdapter_Voice voiceMailAdapter;
	ProgressDialog mProgressDialog3;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

	private ProgressDialog mProgressDialog;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ListItemsAdapter_Voice VoiceMailAdapter;
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		voiceDownload = "com.roamprocess1.roaming4world.voiceDownload";

		pinNumberforVoiceMail = "com.roamprocess1.roaming4world.pinNumberforVoiceMail";
		DIDNumberForVoiceMail = "com.roamprocess1.roaming4world.DIDNumberForVoiceMail";

		getPinNumber = prefs.getString(pinNumberforVoiceMail, "No PinNumber");

		Log.d("get Pin in side the r4wVoiceMail", getPinNumber + "");
		getDIDNumberPref = prefs.getString(DIDNumberForVoiceMail,"No DIDNUMBER");

		Log.d("fwd number inside the voice mail=", getDIDNumberPref);

		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		getCountryCodePref = prefs.getString(stored_user_country_code, "");
		Log.d("getCountryCodePref in side the VoiceMail", getCountryCodePref);

		
		
		rootView = inflater.inflate(R.layout.r4wvoicemail, container, false);
		VoiceMailListView = (ListView) rootView.findViewById(R.id.listVoicemail);

		bebas = Typeface.createFromAsset(getActivity().getAssets(),"fonts/BebasNeue.otf");
		//nexaNormal = Typeface.createFromAsset(getActivity().getAssets(),"fonts/NexaLight.otf");
		 txtVoicemail= (TextView)rootView.findViewById(R.id.txtVoicemail);
		txtVoiceCalls=(TextView) rootView.findViewById(R.id.txtVoiceCalls);
		
		setFont();
		swtActivateOrDeactivate = (MySwitch) rootView.findViewById(R.id.switchActivationOrDeActivation);

		
		
		NetworkCheck();

		new AsyncTaskGetVoiceMailStatus().execute();
		new webserviceGetAllVoiceMail().execute();
		
		
		
		
		

		swtActivateOrDeactivate
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub

						if (isChecked) {

							//Toast.makeText(getActivity(), getPinNumber,Toast.LENGTH_LONG).show();
							VoiceMailActivativation();

						} else {

							VoiceMailDeActivate();
						//	Toast.makeText(getActivity(), getPinNumber,Toast.LENGTH_LONG).show();
						}

					}
				});

		// Fill_LeftList();
		// RefreshListView();

		return rootView;
	}

	public void VoiceMailActivativation() {
		new AsyncTaskVoiceMailActivate().execute();
	}

	public void VoiceMailDeActivate() {
		new webserviceDeActivateVoiceMail().execute();

	}

	public void VoiceMailStatus() {
		new AsyncTaskGetVoiceMailStatus().execute();

	}

	/* Activate Voice Mail Web Service Call from here Start */

	private class AsyncTaskVoiceMailActivate extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webserviceActivateMail()) {
				Log.d("webserviceActivateMail  in voice mail", "SUCCESS");
				return true;
			} else {

				Log.d("webserviceActivateMail  in voice mail", "Error");
				return false;
			}
		}
	}

	public boolean webserviceActivateMail() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);

			// String
			// urlActivate="https://ip.roaming4world.com/esstel/voicemail_service.php?msg=VOICEMAIL_(PINNUMBER)_A";

			String url = "https://ip.roaming4world.com/esstel/"
					+ "voicemail_service.php?msg=VOICEMAIL_" + getPinNumber
					+ "_A";

			Log.d("url  in side", url);
			HttpPost httppost = new HttpPost(url);
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				Log.d("response in side webserviceActivateMail   ",
						responseBody);
				JSONObject json = new JSONObject(responseBody);
				Log.d("json webserviceActivateMail ", json + "");

				ActivationStatus = json.getString(getPinNumber);
				Log.d("getPin Number ", getPinNumber);
				Log.d("json ActivationStatus ", ActivationStatus);

				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/* Activate Voice Mail Web Service Call from here End */

	/* DeActivate Voice Mail Web Service Call from here Start */
	private class webserviceDeActivateVoiceMail extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webserviceDeActivateMail()) {
				Log.d("webserviceDeActivateMail  in voice mail", "SUCCESS");
				return true;
			} else {
				// Toast.makeText(this,
				// "Request failed",Toast.LENGTH_LONG).show();
				Log.d("webserviceDeActivateMail  in voice mail", "Error");
				return false;
			}
		}
	}

	public boolean webserviceDeActivateMail() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);

			String url = "https://ip.roaming4world.com/esstel/"
					+ "voicemail_service.php?msg=VOICEMAIL_" + getPinNumber
					+ "_D";
			HttpPost httppost = new HttpPost(url);
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);

				JSONObject json = new JSONObject(responseBody);
				Log.d("json webserviceDeActivateMail ", json + "");

				DeActivationStatus = json.getString(getPinNumber);
				Log.d("json DeActivationStatus ", DeActivationStatus + "");

				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/* DeActivate Voice Mail Web Service Call from here End */

	/* Get Voice Mail Status Web Service Call from here Start */

	private class AsyncTaskGetVoiceMailStatus extends
			AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {

			try
			{
			if (GetCurrentVoiceMailStatus.equals("Voicemail Active")) {
				// swtActivateOrDeactivate.notify();
				// swtActivateOrDeactivate.setActivated(true);
				switchValue(true);
			} else {
				// swtActivateOrDeactivate.notify();
				// swtActivateOrDeactivate.setActivated(false);
				switchValue(false);
			}
			}
			catch(Exception e)
			{
				
			}

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webserviceGetVoiceMailStatus()) {
				Log.d("webserviceGetVoiceMailStatus  in voice mail", "SUCCESS");
				return true;
			} else {

				Log.d("webserviceGetVoiceMailStatus  in voice mail", "Error");
				return false;
			}
		}
	}

	public boolean webserviceGetVoiceMailStatus() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");

			HttpClient httpclient = new DefaultHttpClient(p);
			// String
			// urlActivate="https://ip.roaming4world.com/esstel/voicemail_service.php?msg=VOICEMAIL_(PINNUMBER)_A";

			String url = "https://ip.roaming4world.com/esstel/"
					+ "voicemail_service.php?msg=VOICEMAIL_" + getPinNumber
					+ "_S";
			Log.d("url in side getall voice status", url);

			HttpPost httppost = new HttpPost(url);

			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);
				JSONObject json = new JSONObject(responseBody);
				GetCurrentVoiceMailStatus = json.getString(getPinNumber);
				Log.d("Current Status in side the getCurrent Status ",GetCurrentVoiceMailStatus);

				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return false;
		}
	}

	public void switchValue(boolean b) {

		Log.d("boolean  value in side switch", b + "");

		if (b) {
			Log.d("inside if", b + "");
			swtActivateOrDeactivate.setChecked(true);
			//Toast.makeText(getActivity(), getPinNumber, Toast.LENGTH_LONG).show();
			VoiceMailActivativation();

		} else {
			Log.d("inside else", b + "");
			swtActivateOrDeactivate.setChecked(false);
			VoiceMailDeActivate();
			// Toast.makeText(getActivity(),
			// getPinNumber,Toast.LENGTH_LONG).show();
		}

	}

	/* Activate Voice Mail Web Service Call from here End */

	/* GetAllVoiceMail Web Service Call from here Start */
	private class webserviceGetAllVoiceMail extends
			AsyncTask<Void, Void, Boolean> {

		ProgressDialog mProgressDialog;
		@Override
		protected void onPostExecute(Boolean result) {
			
			mProgressDialog.dismiss();
			RefreshListView();

			if((dataArray_left.size()<1))
			{
				Log.d("in side array if", "no Voice ");
				txtVoiceCalls.setText("NO VOICE  CALLS");
			}else
			{
				Log.d("in side array if", "Voice ");
				txtVoiceCalls.setText("VOICE  CALLS");
			}
				
			Log.d("array size in post execute", dataArray_left.size() + "");
			
			
			Log.d("array size in post execute", dataArray_download.size() + "");

		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(getActivity(), getResources()
					.getString(R.string.loading),
					getResources().getString(R.string.data_loading));

		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webserviceGetAllVoiceMail()) {
				Log.d("webserviceGetAllVoiceMail  in voice mail", "SUCCESS");
				return true;
			} else {

				Log.d("webserviceGetAllVoiceMail  in voice mail", "Error");
				return false;
			}
		}
	}

	public boolean webserviceGetAllVoiceMail() {
		try {

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			if (getCountryCodePref != "1") {
				didWithoutZeo = getDIDNumberPref.substring(1);
			}
			String url = "http://208.43.85.68/esstel/voicemail_services/voicemail_details.php?msg=VOICEMAIL_"
					+ getCountryCodePref + didWithoutZeo + "_DETAILS";

			HttpUriRequest httppost = new HttpPost(url);

			Log.d("part", "0");
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);

				JSONObject json = new JSONObject(responseBody);
				Log.d("json object", json + "");

				JSONArray jsonresponse = json.getJSONArray("response");

				Log.d("json jsonresponse", jsonresponse + "");

				dataArray_left.clear();
				dataArray_duration.clear();
				dataArray_callerid.clear();
				dataArray_VoiceMailTime.clear();
						
				for (int i = 0; i < jsonresponse.length(); i++) {
					JSONObject jsonresponse2 = jsonresponse.getJSONObject(i);
					System.out.println("jsonresponse2 is " + jsonresponse2);

					if (i == 0) {

						JSONArray jsonresponse3 = jsonresponse2.getJSONArray("messages");
						for (int j = 0; j < jsonresponse3.length(); j++) {
							String jsonresponse4 = jsonresponse3.getString(j);

							System.out.println("jsonresponse4 is "+ jsonresponse4);

							dataArray_left.add(j, jsonresponse4);

							String downloadLink = "http://208.43.85.68/esstel/voicemail_services/voicemail_details.php?msg=VOICEMAIL_"
									+ getCountryCodePref
									+ didWithoutZeo
									+ "_DOWNLOAD_" + jsonresponse4;
							dataArray_download.add(j, downloadLink);

							Log.d("download link==", downloadLink);
							Log.d("array value in service",dataArray_left.get(i));

							// dataArray_left.add(jsonresponse4);
						
						}
						System.out.println("jsonresponse3 is " + jsonresponse3);
						Log.d("array size", dataArray_left.size() + "");

					}

					if (i == 1) {
						
						
						for (int j = 0; j < dataArray_left.size(); j++) {
							String jsonresponse44 = dataArray_left.get(j);

							JSONObject jsonresponse3 = jsonresponse2.getJSONObject("message_details");
							Log.d("jsonresponse3 in i==1", jsonresponse3 + "");
							
							
							JSONObject jsonresponse6 = jsonresponse3.getJSONObject(jsonresponse44);

							Log.d("jsonresponse6 in i==1", jsonresponse6 + "");

							String duration = jsonresponse6.getString("duration");
							String callerid = jsonresponse6.getString("callerid");
							String origtime = jsonresponse6.getString("origtime");

							dataArray_callerid.add(callerid);
							dataArray_duration.add(duration);
							
							

							origtime = origtime.replaceAll("\\n", "");

							String voicemail_time_to_print = "";

							long l = Long.parseLong(origtime);
							long tsLong = System.currentTimeMillis() / 1000;

							long diff = tsLong - l;

							long day = TimeUnit.SECONDS.toDays(diff);
							long hours = TimeUnit.SECONDS.toHours(diff)
									- (day * 24);
							long minute = TimeUnit.SECONDS.toMinutes(diff)
									- (TimeUnit.SECONDS.toHours(diff) * 60);

							boolean flag_today = false;

							if (day > 0) {
								if (day == 1) {
									voicemail_time_to_print += "1 Day";
								} else {
									voicemail_time_to_print += day + " Days";
								}
							} else {
								voicemail_time_to_print += "Today";
								flag_today = true;
							}

							if (flag_today) {

								if (hours == 0) {
									voicemail_time_to_print += "";
								} else if (hours == 1) {
									voicemail_time_to_print += ", 1 Hour";
								} else {
									voicemail_time_to_print += ", " + hours
											+ " Hours";
								}

								if (minute == 0) {
									voicemail_time_to_print += "";
								} else if (minute == 1) {
									voicemail_time_to_print += ", 1 Min";
								} else {
									voicemail_time_to_print += ", " + minute
											+ " Mins";
								}
							}
							voicemail_time_to_print += " ago";

							dataArray_VoiceMailTime.add(voicemail_time_to_print);

							System.out.println("voicemail_time_to_print "+ voicemail_time_to_print);
							System.out.println("jsonresponse3 duration value  "+ duration);
							System.out.println("jsonresponse3 caller id  "+ callerid);
							System.out.println("jsonresponse3 origtime  "+ origtime);

							System.out.println("jsonresponse3 in side 1 "+ jsonresponse3);
							System.out.println("jsonresponse3 in side 1 "+ jsonresponse6);

						}						
					}
				}

				return true;
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				return false;
			}
		} catch (Throwable t) {
			// Toast.makeText(this, "Request failed: " +
			// t.toString(),Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/* GetAllVoiceMaill Web Service Call from here End */

	public void Fill_LeftList() {

		Log.d("Fill_LeftList", dataArray_left.size() + "");

		dataArray_left.add("Pic");
		dataArray_left.add("Home");
		dataArray_left.add("My Account");
		dataArray_left.add("Call Details");
		dataArray_left.add("Re-Call Activation");
		dataArray_left.add("How to use");
		dataArray_left.add("Rate Us");
		dataArray_left.add("Like and Share");
		dataArray_left.add("Ticket Support");
		dataArray_left.add("Get Flight Info");
		dataArray_left.add("Voice Mail");
		dataArray_left.add("Exit");

	}

	public void RefreshListView() {

		Log.d("array size inside refresh ", dataArray_left.size() + "");
		for (int i = 0; i < dataArray_left.size(); i++) {

			Object obj = new Object();
			objectArray_left.add(obj);

			Object obj2 = new Object();
			objectArray_download.add(obj2);

			Object obj3 = new Object();
			objectArray_duration.add(obj3);

			Object obj4 = new Object();
			objectArray_callerid.add(obj4);
			
			Object obj5 = new Object();
			objectArray_VoiceMailTime.add(obj5);
			

			Log.d("array value in refresh", dataArray_left.get(i));
			Log.d("array value in refresh", dataArray_download.get(i));
			Log.d("array value in refresh", dataArray_duration.get(i));
			Log.d("array value in refresh", dataArray_callerid.get(i));
			Log.d("array value in refresh", dataArray_VoiceMailTime.get(i));

		}
		voiceMailAdapter = new ListItemsAdapter_Voice(objectArray_left, 1);
		VoiceMailListView.setAdapter(voiceMailAdapter);
		VoiceMailListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
			}
		});
	}

	private class ListItemsAdapter_Voice extends ArrayAdapter<Object> {

		ViewHolder holder1;

		public ListItemsAdapter_Voice(List<Object> items, int x) {

			super(getActivity(),
					android.R.layout.simple_list_item_single_choice, items);
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return dataArray_left.get(position);
		}

		public int getItemInteger(int pos) {
			return pos;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return dataArray_left.size();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			final ImageView imageViewdownload;
			ImageView imageViewPlay;

			LayoutInflater inflator = getActivity().getLayoutInflater();
			holder1 = new ViewHolder();

			convertView = inflator.inflate(R.layout.r4wvoicemailrow, null);
			// holder1.text = (TextView)
			// convertView.findViewById(R.id.txtmsgdata);
			holder1.textVcallerid = (TextView) convertView.findViewById(R.id.txtVCallerId);
			holder1.textDuration = (TextView) convertView.findViewById(R.id.txtVduration);
			holder1.textVoiceMailTime = (TextView) convertView.findViewById(R.id.txtVtime);
			
			imageViewdownload = (ImageView) convertView.findViewById(R.id.imgicndownload);
			imageViewPlay = (ImageView) convertView.findViewById(R.id.imgPlayvoicemail);

			imageViewdownload.setClickable(true);
			imageViewPlay.setClickable(true);
			imageViewPlay.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				
					Boolean	Alreadydownloaded = false;
					getVoiceMailDownload = prefs.getString(voiceDownload,"No voicemail downloaded ,");
					Log.d(" After append value ", getVoiceMailDownload);
					downloadlist = getVoiceMailDownload.split(",");
					
					for (int i = 1; i < downloadlist.length; i++) {
						Log.d("download list==" + i, downloadlist[i]);
						Log.d("dataarry left value ===", dataArray_left.get(position));
						
						if (downloadlist[i].equals(dataArray_left.get(position))) {
							Log.d("searching loop", "in side if");
							Alreadydownloaded = true;
							break;
													
						} else {
							
							Alreadydownloaded = false;
							//break;

						}
					}
					
					if (Alreadydownloaded) {

						Alreadydownloaded = false;
						playVoiceMail(position);
					} else {

						Toast.makeText(getActivity(), "Please Download First",Toast.LENGTH_SHORT).show();
					}
					
				}
			});

			imageViewdownload.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Boolean	Alreadydownloaded = false;
					Log.d("Alreadydownloaded value inside download view", Alreadydownloaded+"");
					Log.d("  datarray_left  value in  download===", dataArray_left.get(position));
					
					getVoiceMailDownload = prefs.getString(voiceDownload,"No voicemail downloaded ,");

					//prefs.edit().putString(voiceDownload,getVoiceMailDownload+ dataArray_left.get(position)+ ",").commit();
					getVoiceMailDownload = prefs.getString(voiceDownload,"No voicemail downloaded ,");

					Log.d(" After append value ", getVoiceMailDownload);
					downloadlist = getVoiceMailDownload.split(",");
					

					Log.d("download list array length", downloadlist + "");

					for (int i = 1; i < downloadlist.length; i++) {
						Log.d("download list==" + i, downloadlist[i]);
						Log.d("dataarry left value ===", dataArray_left.get(position));
						
						if (downloadlist[i].equals(dataArray_left.get(position))) {
							Log.d("searching loop", "in side if");
							Alreadydownloaded = false;
							break;
						
							
						} else {
							Log.d("searching loop", "in side else");
							Alreadydownloaded = true;
							//break;

						}
					}
				
					if (Alreadydownloaded) {
						Log.d("Alreadydownloaded value =", Alreadydownloaded+ "");
						Alreadydownloaded = false;
						downloadlink(position);
					} else {

						Toast.makeText(getActivity(), "Already  downloaded",Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			
			
			String string = dataArray_callerid.get(position);
			String[] parts = string.split("<");
			String part1 = parts[0];
		
			
			convertView.setTag(holder1);
			String text = dataArray_left.get(position);
			holder1.textVcallerid.setText(part1.replace('"', ' '));
			holder1.textDuration.setText(dataArray_duration.get(position));
			holder1.textVoiceMailTime.setText(dataArray_VoiceMailTime.get(position));
			
			
			return convertView;
		}
	}

	public class ViewHolder {
		public TextView textVoiceMailTime;
		public TextView textDuration;
		public TextView textVcallerid;
		TextView text, textcounter;
	}

	public void downloadlink(int position) {
		
		
		//Toast.makeText(getActivity(),"voicemail " + dataArray_left.get(position), Toast.LENGTH_LONG).show();

		if (getCountryCodePref != "1") {
			didWithoutZeo = getDIDNumberPref.substring(1);
		}

		String downloadLink = "http://208.43.85.68/esstel/voicemail_services/voicemail_details.php?msg=VOICEMAIL_"
				+ getCountryCodePref
				+ didWithoutZeo
				+ "_DOWNLOAD_"
				+ dataArray_left.get(position);
		Log.d("donload link=", downloadLink);

		new DownloadFileAsync().execute(downloadLink,dataArray_left.get(position));

	}

	public void playVoiceMail(int position) {
		//Toast.makeText(getActivity(),"voicemail " + dataArray_left.get(position), Toast.LENGTH_LONG).show();
		Log.d("play voice mail===", "playvoicemail ");
		
		mMediaPlayer = new MediaPlayer();
		mMediaController = new MediaController(getActivity());
		mMediaController.setMediaPlayer(R4WVoiceMail.this);
		// mMediaController.setMediaPlayer(mMediaPlayer);

		mMediaController.setAnchorView(rootView.findViewById(R.id.audioView));
		String audioFile = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+ "/roaming4world/voicemail/"
				+ dataArray_left.get(position)
				+ ".wav";

		Log.d("audio file path", audioFile);

		try {
			mMediaPlayer.setDataSource(audioFile);
			mMediaPlayer.prepare();
			
		} catch (IOException e) {
			// Log.e(PLAY_AUDIO, "Could not open file " + audioFile +
			// " for playback.", e);
		}

		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mHandler.post(new Runnable() {
					public void run() {

						mMediaController.setEnabled(true);

						mMediaController.show(0);
						mMediaPlayer.start();
					}
				});
			}
		});
		
		
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
				mMediaController.setEnabled(false);
				mMediaController.hide();
			}
		});
		
		

	}

	public Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onCreateDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;

			try {

				URL url = new URL(aurl[0]);
				String VoicemailName = aurl[1];

				downloadAknow = new DownloadAknowlegeMent();
				downloadAknow.downloadComplete = aurl[0];

				downloadAknow.downloadVoiceMailName = aurl[1];

				Log.d("voice mail ==", VoicemailName);
				URLConnection conexion = url.openConnection();
				conexion.connect();
				int lenghtOfFile = conexion.getContentLength();
				Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

				InputStream input = new BufferedInputStream(url.openStream(),8192);

				roaming4worldfolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/", "roaming4world");

				if (roaming4worldfolder.exists()) {
					Log.d("roming4world", "FOLDER EXISTS");

					voicemail = new File(roaming4worldfolder, "voicemail");
					if (voicemail.exists()) {
						Log.d("voicemail", "FOLDER EXISTS");
					} else {
						voicemail.mkdir();
					}

				} else {
					roaming4worldfolder.mkdir();
					voicemail = new File(roaming4worldfolder, "voicemail");
					Log.d("voicemail  save path", voicemail + "");

					if (voicemail.exists()) {

					} else {
						voicemail.mkdir();
					}

				}
			
				OutputStream output = new FileOutputStream(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
								+ "/roaming4world/voicemail/"
								+ VoicemailName
								+ ".wav");
								
				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

				prefs.edit().putString(voiceDownload,getVoiceMailDownload+ VoicemailName+ ",").commit();
				getVoiceMailDownload = prefs.getString(voiceDownload,"No voicemail downloaded ,");
				Log.d("getVoiceMailDownload in side download  ", getVoiceMailDownload);
				
				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
			}
			return null;

		}

		protected void onProgressUpdate(String... progress) {
			// Log.d("ANDRO_ASYNC",progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected void onPostExecute(String unused) {
			mProgressDialog.dismiss();
			
			Toast.makeText(getActivity(), "Download has been completed",Toast.LENGTH_LONG).show();
			
			Log.d("value in post execute==", downloadAknow.downloadComplete+ " voicemail name==="+ downloadAknow.downloadVoiceMailName);
			
			// new
			// webserviceDeleteVoiceMail().execute(downloadAknow.downloadComplete,downloadAknow.downloadVoiceMailName);

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if(mMediaPlayer.isPlaying())
			{
				mMediaPlayer.stop();
				mMediaPlayer.release();
			} 
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}
	@Override
	public boolean canPause() {
		return true;
	}
	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	@Override
	
	public int getBufferPercentage() {
		int percentage = (mMediaPlayer.getCurrentPosition() * 100)/ mMediaPlayer.getDuration();

		return percentage;
	}
	
	@Override
	
	public int getCurrentPosition() {
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	
	public int getDuration() {
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mMediaPlayer.isPlaying();
	}

	public void pause() {
		if (mMediaPlayer.isPlaying())
			mMediaPlayer.pause();
		
	}

	@Override
	public void seekTo(int pos) {
		mMediaPlayer.seekTo(pos);
	}

	public void start() {
		mMediaPlayer.start();
	}

	public boolean onTouchEvent(MotionEvent event) {
		mMediaController.show();

		return true;
	}

	public void hide() {
				// TODO Auto-generated method stub
		//mMediaController.show();
	}

	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* webserviceDeleteVoiceMail Web Service Call from here Start */
	private class webserviceDeleteVoiceMail extends
			AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected String doInBackground(String... aurl) {

			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);

			try {
				String url = aurl[0];
				String VoicemailName = aurl[1];

				String durl = "http://208.43.85.68/esstel/voicemail_services/voicemail_details.php?msg=VOICEMAIL_61399997322_DOWNLOADED_"
						+ VoicemailName;

				Log.d("delete url in webserviceDeleteVoiceMail ", url);
				Log.d("delete url in VoicemailName ", VoicemailName);

				HttpUriRequest httppost = new HttpPost(durl);

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				Log.d("responseBody In side Deletevoicemail", responseBody + "");
				JSONObject json = new JSONObject(responseBody);
				Log.d("JSON In side Deletevoicemail", json + "");

			} catch (Exception e) {
			}
			return null;

		}

		@Override
		protected void onPostExecute(String unused) {

			Toast.makeText(getActivity(), "deleted", Toast.LENGTH_LONG).show();

		}

	}

	public void NetworkCheck() {
		boolean isNetworkAvailable = isNetworkAvailable();
		if (isNetworkAvailable == true) {
			Log.d("VoiceMail", "isNetworkAvailable true");
		} else {
			Intent intent_main = new Intent(getActivity(), NoNetwork.class);
			startActivity(intent_main);
		}
	}

	public class DownloadAknowlegeMent {
		public String downloadComplete;
		public String downloadVoiceMailName;

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void setFont() {
		txtVoiceCalls.setTypeface(bebas);
		txtVoicemail.setTypeface(bebas);
	}
	
	
}
