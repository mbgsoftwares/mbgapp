package com.roamprocess1.roaming4world.roaming4world;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.roamprocess1.roaming4world.R;
public class R4wPaymentWebView extends Activity {

	private WebView webView;
	int pid, pinNo, pinNumnberFromHome;
	String pinNumberforVoiceMail, getPinNumber;
	private SharedPreferences prefs;
	ProgressBar progressBar;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		Bundle paymentIntent = getIntent().getExtras();
		pid = paymentIntent.getInt("Pid", 0);
		pinNo = paymentIntent.getInt("PinNo", 1);
		pinNumnberFromHome = paymentIntent.getInt("PinNofrom", 55);

		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);
		pinNumberforVoiceMail = "com.roamprocess1.roaming4world.pinNumberforVoiceMail";
		getPinNumber = prefs.getString(pinNumberforVoiceMail, "No PinNumber");
		Log.d("get Pin in side the paymentWebView", getPinNumber + "");

		Log.d("paymentIntentView  ==", pid + "");

		View customView = getLayoutInflater().inflate(
				R.layout.r4wvarificationheader, null);
		ImageButton imgbackbtn = (ImageButton) customView
				.findViewById(R.id.imgbackbtn);
		// imgbackbtn.setClickable(false);
		// imgbackbtn.setVisibility(View.INVISIBLE);
		actionBar.setCustomView(customView);

		setContentView(R.layout.paymentwebview);

		imgbackbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});
		webView = (WebView) findViewById(R.id.paymentWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());

		if (pinNo != 1 || pinNumnberFromHome != 55) {

			String pinNumnberfromCurrentFrag = CurrentFragment.pinNumber
					.toString();

			webView.loadUrl("http://ip.roaming4world.com/esstel/buy-a-pin/recharge.php?pid="
					+ pid + "&pin_no=" + getPinNumber);
		} else if (pid != 0) {
			webView.loadUrl("http://ip.roaming4world.com/esstel/buy-a-pin/buy-a-pin-1.php?pid="
					+ pid);
		}

		webView.setWebViewClient(new myWebClient());
		webView.setWebChromeClient(new WebChromeClient() {
			// The undocumented magic method override
			// Eclipse will swear at you if you try to put @Override here
			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {

				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				R4wPaymentWebView.this.startActivityForResult(
						Intent.createChooser(i, "File Chooser"),
						FILECHOOSER_RESULTCODE);

			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg,
					String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				R4wPaymentWebView.this.startActivityForResult(
						Intent.createChooser(i, "File Browser"),
						FILECHOOSER_RESULTCODE);
			}

			// For Android 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				R4wPaymentWebView.this.startActivityForResult(
						Intent.createChooser(i, "File Chooser"),
						R4wPaymentWebView.FILECHOOSER_RESULTCODE);

			}

		});

		setContentView(webView);

	}

	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub

			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

			// progressBar.setVisibility(View.GONE);
		}
	}

	// flipscreen not loading again
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
