package com.roamprocess1.roaming4world.roaming4world;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;
public class CDRDetail extends Activity{
	
	private int position;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cdr_detail);
		
		position = getIntent().getIntExtra("position", 0);
		Toast.makeText(getApplicationContext(), "position is "+ String.valueOf(position), Toast.LENGTH_LONG).show();
		
	}

}
