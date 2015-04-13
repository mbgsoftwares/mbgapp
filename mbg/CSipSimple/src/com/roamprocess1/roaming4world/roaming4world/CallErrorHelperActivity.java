package com.roamprocess1.roaming4world.roaming4world;

import java.io.File;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.ui.RContactlist;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;

public class CallErrorHelperActivity extends SherlockFragmentActivity {

	private SharedPreferences prefs;
	private String stored_lastdialnumber, user_countrycode,stored_server_ipaddress,
			stored_user_country_code, User_mobile_no, stored_user_mobile_no;
	TextView tv_last_dialnumber, name, phNumber;
	ImageView imgcallerpic;
	String number;
	RContactlist rContactlist;
	private String balance;
	double bal, minbal;
	DBContacts dbContacts;
	LinearLayout ll_r4win, linerGSMCall;
	ImageButton redial;
	private Drawable pic;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.callerrorhelper);
		prefs = getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		stored_lastdialnumber = "com.roamprocess1.roaming4world.lastdialnumber";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		user_countrycode = prefs.getString(stored_user_country_code, "");
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		User_mobile_no = prefs.getString(stored_user_mobile_no, "no");
   		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";
		dbContacts = new DBContacts(CallErrorHelperActivity.this);
		initializer();
		setValues();
		setActionBar();
		linerGSMCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("GSM Calling Number:" + number);
				callGSM(number);
			}
		});

		new MyAsyncTaskGetBalance().execute();

	}

	private void setValues() {
		// TODO Auto-generated method stub
		String num = prefs.getString(stored_lastdialnumber, "Not Found");
		Bitmap bmp;
		if (num.contains("@")) {
			String[] n = num.split("@");
			num = n[0];
			if (num.contains(":")) {
				n = num.split(":");
				num = n[1];
			}
		}

		if (num.startsWith("011")) {
			num = num.substring(3);
			ll_r4win.setVisibility(LinearLayout.GONE);
			//redial.setText("Out call");
		}
		number = num;
		tv_last_dialnumber.setText("+" + num);
		phNumber.setText("+" + num);
		String cc = prefs.getString(stored_user_country_code, "");
		/*
		 * if(!cc.equals("")){ if(number.startsWith("00")){
		 * 
		 * }else if(num.startsWith("+")){ number = num.substring(1); }else
		 * if(num.startsWith("0")){ number = cc + num.substring(1); }else{
		 * number = cc + num; }
		 * 
		 * }
		 */
		String fileuri = "/sdcard/R4W/ProfilePic/" + num + ".png";
		File imageDirectoryprofile = new File(fileuri);
		if (imageDirectoryprofile.exists()) {

			Bitmap img = BitmapFactory.decodeFile(fileuri);
			pic = new BitmapDrawable(getResources(), img);
			bmp = ((BitmapDrawable) pic).getBitmap();
			bmp = ImageHelperCircular.getRoundedCornerBitmap(bmp,bmp.getWidth());

			imgcallerpic.setImageBitmap(bmp);

		} else {
			imgcallerpic.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
		}

		try {
			dbContacts.openToRead();
			Cursor c = dbContacts.fetch_contact_from_R4W(number);
			if (c.getCount() > 0) {
				c.moveToFirst();
				Log.d("nameerr", c.getString(2) + " $");
				name.setText(c.getString(2));
			} else {
				Log.d("nameerr", number + " $");
				name.setText("+" + number);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializer() {
		// TODO Auto-generated method stub
		tv_last_dialnumber = (TextView) findViewById(R.id.tv_callerror_number);
		name = (TextView) findViewById(R.id.tv_callerror_name);
		phNumber = (TextView) findViewById(R.id.phNumber);
		imgcallerpic = (ImageView) findViewById(R.id.imgcallerpic);
		linerGSMCall = (LinearLayout) findViewById(R.id.linerGSMCall);

		rContactlist = new RContactlist();
		redial = (ImageButton) findViewById(R.id.btn_freecall);
	}

	public void callCheck(View v) {
		rContactlist.r4wCall_Chat(number);
		finish();

	}

	public void OnclickAction(View v) {

		switch (v.getId()) {

		case R.id.btn_outcall:
			r4wOutCall();
			finish();
			break;

		case R.id.btn_freemsg:
			callMessage(number);
			finish();
			break;

		default:
			break;
		}

	}

	private void dialogBoxVideoCall() {
		new AlertDialog.Builder(CallErrorHelperActivity.this)
				.setTitle("Video Support")
				.setMessage(
						"Video feature is not installed. Click proceed to download.")
				.setPositiveButton("Proceed",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
								Intent browserIntent = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("https://play.google.com/store/apps/details?id=com.roamprocess1.roaming4world.plugins.video"));
								startActivity(browserIntent);
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

	}

	private boolean isAppInstalled(String packageName) {
		PackageManager pm = getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

	private void r4wOutCall() {

		if (!number.startsWith("*") && !number.startsWith("#")) {
			if (number.startsWith("+")) {
				number = number.substring(1);
			} else if (number.startsWith(user_countrycode)) {

			} else if (number.startsWith("00")) {
				String modify_contact_no = number.substring(2);
				modify_contact_no = user_countrycode + modify_contact_no;
				number = modify_contact_no.toString();

			} else if (number.startsWith("0")) {
				String modify_contact_no = number.substring(1);
				modify_contact_no = user_countrycode + modify_contact_no;
				number = modify_contact_no.toString();

			} else if (!number.startsWith("+") && !number.startsWith("0")) {
				number = user_countrycode + number.toString();

			}
		}
		String r4woutCallingNumber = "";
		r4woutCallingNumber = "011" + number;
		Log.d("OutCalling number:", r4woutCallingNumber);

		minbal = 10;

		if (balance == null) {
			dialogboxMBTelcomOut();
		} else {
			bal = Double.parseDouble(balance) * 100;

			Log.d("balance ", bal + " #");

			if (minbal > bal) {
				dialogboxMBTelcomOut();
			} else {
				rContactlist.r4wCall_Chat(r4woutCallingNumber);

			}
		}

	}

	public void callMessage(String numberget) {
		// TODO Auto-generated method stub
		String fromFull = "<sip:"+ numberget+"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, "")) + ">";
		String number = "sip:"+ numberget +"@" + StaticValues.getServerIPAddress(prefs.getString(stored_server_ipaddress, ""));
		Bundle b = MessageFragment.getArguments(number, fromFull);

		Intent it = new Intent(CallErrorHelperActivity.this,
				MessageActivity.class);
		it.putExtras(b);
		it.putExtra("call", true);
		startActivity(it);

	}

	public void dialogboxMBTelcomOut() {

		final Dialog dialogMBTelcomOut = new Dialog(
				CallErrorHelperActivity.this);
		// dialogMBTelcomOut.setTitle("MBTelcom Out Credit");
		dialogMBTelcomOut.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogMBTelcomOut.setContentView(R.layout.r4wout);
		dialogMBTelcomOut.show();
		Button btn$10 = (Button) dialogMBTelcomOut.findViewById(R.id.btn10);
		Button btn$20 = (Button) dialogMBTelcomOut.findViewById(R.id.btn20);
		Button btn$30 = (Button) dialogMBTelcomOut.findViewById(R.id.btn30);
		Button btnGetRates = (Button) dialogMBTelcomOut
				.findViewById(R.id.btngetrates);
		Button btnMaybeLater = (Button) dialogMBTelcomOut
				.findViewById(R.id.btnmaybelater);

		btnMaybeLater.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Toast.makeText(CallErrorHelperActivity.this, "Coming soon",
						Toast.LENGTH_SHORT).show();
				dialogMBTelcomOut.dismiss();
			}
		});

		btn$10.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Toast.makeText(CallErrorHelperActivity.this, "Coming soon",
						Toast.LENGTH_SHORT).show();
				dialogMBTelcomOut.dismiss();
			}
		});

		btn$20.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Toast.makeText(CallErrorHelperActivity.this, "Coming soon",
						Toast.LENGTH_SHORT).show();
				dialogMBTelcomOut.dismiss();
			}
		});
		btn$30.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Toast.makeText(CallErrorHelperActivity.this, "Coming soon",
						Toast.LENGTH_SHORT).show();
				dialogMBTelcomOut.dismiss();
			}
		});

	}

	public class MyAsyncTaskGetBalance extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true)
				Toast.makeText(CallErrorHelperActivity.this,
						"Balance is updated", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webservreqGetBalance()) {
				return true;
			} else {
				return false;
			}
		}

	}

	public boolean webservreqGetBalance() {
		try {
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/balance-info/"
					+ "balance.php?contact=" + user_countrycode
					+ User_mobile_no;

			HttpPost httppost = new HttpPost(url);
			// Instantiate a GET HTTP method
			try {
				Log.i(getClass().getSimpleName(), "send  task - start");

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);
				balance = responseBody;
				// Parse
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

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	public void callGSM(String gsmCallingNumber) {
		Log.i("Make call", "");
		Intent phoneIntent = new Intent(Intent.ACTION_CALL);
		String n = gsmCallingNumber;
		if (gsmCallingNumber.contains("@")) {
			String[] arr = gsmCallingNumber.split("@");
			n = arr[0];
			if (n.contains(":")) {
				String[] arre = n.split(":");
				n = arre[1];
			}
		}
		phoneIntent.setData(Uri.parse("tel:+" + n));
		try {
			startActivity(phoneIntent);
			this.finish();
			Log.i("Finished making a call...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(),"Call faild, please try again later.", Toast.LENGTH_SHORT).show();
		}
	}

	private void setActionBar() {
		// TODO Auto-generated method stub
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

}
