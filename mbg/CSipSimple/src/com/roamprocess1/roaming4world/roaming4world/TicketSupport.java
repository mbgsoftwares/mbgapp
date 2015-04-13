package com.roamprocess1.roaming4world.roaming4world;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.roamprocess1.roaming4world.R;
public class TicketSupport extends Activity {


	private WebView webView;
	private View rootView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.howtousewebview);
		
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
		
		webView = (WebView) findViewById(R.id.webviewhowtouse);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.roaming4world.com/support/");
        
	}
	




}
