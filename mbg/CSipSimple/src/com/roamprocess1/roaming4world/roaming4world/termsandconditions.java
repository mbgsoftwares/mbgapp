package com.roamprocess1.roaming4world.roaming4world;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.roamprocess1.roaming4world.R;
public class termsandconditions extends Activity{
	private WebView webView;

    @SuppressLint("NewApi") public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

		android.app.ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);

		View customView = getLayoutInflater().inflate(R.layout.r4wvarificationheader, null);
		ImageButton imgbackbtn = (ImageButton) customView.findViewById(R.id.imgbackbtn);
		//imgbackbtn.setClickable(false);
		//imgbackbtn.setVisibility(View.INVISIBLE);
		actionBar.setCustomView(customView);
		
		imgbackbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			finish();
			}
		});
		
		
        setContentView(R.layout.termsandconditions);

        webView = (WebView) findViewById(R.id.termsandconditions);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://www.roaming4world.com/web/terms-and-conditions.htm");
    }
}
