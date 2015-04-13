package com.roamprocess1.roaming4world.roaming4world;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.roamprocess1.roaming4world.R;

public class HowToUse extends Activity{

	public WebView webView;
	PackageInfo pInfo;
	String version="";
	String versionName="";
	
		@SuppressLint("SetJavaScriptEnabled") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.howtousewebview);

		setActionBar();

		try {
			versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			Log.d("application version code", versionName);

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		webView = (WebView) findViewById(R.id.webviewhowtouse);
		webView.getSettings().setJavaScriptEnabled(true);
		// webView.loadUrl("http://www.roaming4world.com/howtouse/");
		String value = getIntent().getStringExtra("view");
		if (value != null) {
			if(value.equals("FAQ")){
				webView.loadUrl("http://roaming4world.com/faq/index.php?app=r4w");
			}else if(value.equals("getCredit")){
				webView.loadUrl("https://roaming4world.com/r4w_files/rates/rates.php");
			}
			
		} else {
			webView.loadUrl("http://www.roaming4world.com/howtouse/index.php?appversion="
					+ versionName + "&type=android");
		}

	}
		
		
		private void setActionBar() {
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
