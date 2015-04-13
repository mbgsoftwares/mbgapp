package com.roamprocess1.roaming4world.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.ISipService;
import com.roamprocess1.roaming4world.service.SipService;

public class Typing extends Activity implements OnClickListener{

	
	TextView tv_send, tv_rec;
	Button bt_send, bt_rec;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.typing);
		initilizer();
		bindService(new Intent(Typing.this, SipService.class), connection, Context.BIND_AUTO_CREATE);
		bt_rec.setOnClickListener(this);
		bt_send.setOnClickListener(this);
	}

	   private static ISipService service;
	    private ServiceConnection connection = new ServiceConnection() {
	        @Override
	        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
	        	service = ISipService.Stub.asInterface(arg1);
	        	System.out.println("ServiceConnection Service:"+service);   
	        }

	        @Override
	        public void onServiceDisconnected(ComponentName arg0) {
	            service = null;
	        }
	    };

	
	private void initilizer() {
		// TODO Auto-generated method stub
		tv_rec = (TextView) findViewById(R.id.tv_ty_rec);
		tv_send = (TextView) findViewById(R.id.tv_ty_send);
		bt_rec = (Button) findViewById(R.id.bt_ty_rec);
		bt_send = (Button) findViewById(R.id.bt_ty_send);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.d("onClick", "called");
		Log.d("service", service + " !");
		if(v.getId() == R.id.bt_ty_send){
			String re = "sip:912002@208.43.85.86";
			if(service != null){
				Log.d("sendTyping", "called");
			try {
				service.sendTyping("ff", re, 1);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			}
		}else{
			
		
		}
	}
	

	
	
}
