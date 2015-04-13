/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.roamprocess1.roaming4world.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.widgets.MessageEditSipUri;
import com.roamprocess1.roaming4world.widgets.MessageEditSipUri.ToCall;

public class MessageSipUri extends Activity implements OnClickListener {

	private MessageEditSipUri sipUri;
	private Button okBtn;
	public RelativeLayout relativeLayout;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.msgpickup_uri);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN );
		
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	    ab.setHomeButtonEnabled(true);
	    ab.setDisplayShowHomeEnabled(false);
	    ab.setDisplayShowTitleEnabled(false);
		ab.setCustomView(R.layout.r4wactionbarcustom);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		relativeLayout =(RelativeLayout)ab.getCustomView().findViewById(R.id.relativeLayout);
		relativeLayout.setBackgroundColor(Color.parseColor("#F05A2B"));
		
		LinearLayout fin = (LinearLayout) ab.getCustomView().findViewById(R.id.ll_header_finish);
		fin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	
		/*
		ImageView userPic = (ImageView) ab.getCustomView().findViewById(R.id.ab_userpic);
        TextView name  = (TextView) ab.getCustomView().findViewById(R.id.ab_userName);
      
        LinearLayout backLinearLayout=(LinearLayout)ab.getCustomView().findViewById(R.id.ll_backFromChat); 
        userPic.setImageResource(R.drawable.roaminglogo);
        name.setText("Roaming4world");
		name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		userPic.setOnClickListener(new OnClickListener() {
			@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});			
		
		*/
		okBtn = (Button) findViewById(R.id.ok);
		okBtn.setOnClickListener(this);
		Button btn = (Button) findViewById(R.id.cancel);
		btn.setOnClickListener(this);

		
		sipUri = (MessageEditSipUri) findViewById(R.id.sip_uri);
		
		sipUri.getTextField().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView tv, int action, KeyEvent arg2) {
				System.out.println("PickupSipUri.java inonCreate() in onEditorAction()");
				// By Amit For Go Button in SoftKeyBoard
				if(action == EditorInfo.IME_ACTION_GO) {
					/*
					System.out.println("PickupSipUri.java inonCreate() in Before sendPositiveResult()");
					sendPositiveResult();
					*/
					hideSoftKeyboard();
					return true;
				}
				return false;
			}
		});
		
		
		sipUri.setShowExternals(false);
		
		
	}
	
	
	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.ok) {
			sendPositiveResult();
		} else if (vId == R.id.cancel) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	


	public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

	private void sendPositiveResult() {
		System.out.println("PickupSipUri.java in sendPositiveResult");
		Intent resultValue = new Intent();
		
		 ToCall result = sipUri.getValue();
		 
		 
		 System.out.println("PickupSipUri.java in sendPositiveResult result value is "+result);
		 if(result != null) {
		     // Restore existing extras.
		     Intent it = getIntent();
		     if(it != null) {
		         Bundle b = it.getExtras();
		         if(b != null) {
		             resultValue.putExtras(b);
		         }
		     }
			 resultValue.putExtra(Intent.EXTRA_PHONE_NUMBER,result.getCallee());
			 resultValue.putExtra(SipProfile.FIELD_ID,result.getAccountId());
			 setResult(RESULT_OK, resultValue);
		 }else {
			 System.out.println("PickupSipUri.java in sendPositiveResult else part");
			setResult(RESULT_CANCELED);
		 }
		finish();
	}
	
	
	
}
