package com.roamprocess1.roaming4world.roaming4world;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.utils.Compatibility;


public class R4WMissedcallDetails extends FragmentActivity{

	
	ListView lv_history;
	TextView tv_header_text, tv_call_and_sms_label;
	LinearLayout ll_call_and_sms_main_action;
	String name , number;
	ImageView iv_contact_background;
	ArrayList<String> dataArray_left = new ArrayList<String>();
	DBContacts dbContacts;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.call_detail);
		initializer();
		setDetails();
		attachAdapter();
	}

	private void attachAdapter() {
		// TODO Auto-generated method stub
		dbContacts = new DBContacts(R4WMissedcallDetails.this);
		
		dbContacts.openToRead();
		Cursor c = dbContacts.fetch_details_from_MIssedCall_Table(number);
		if(c.getCount() > 0)
		{
			c.moveToFirst();
			do{
				CharSequence time = c.getString(c.getColumnIndex(dbContacts.R4W_MissedCall_Time));
//				time = DateUtils.getRelativeTimeSpanString(Long.parseLong(time.toString()) * 1000,System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE);
				
				Log.d("nnnn", time + " !");
				dataArray_left.add(getDate(time.toString()));
			}while(c.moveToNext());
		}
		c.close();
		final StatusArrayAdapter status_adapter = new StatusArrayAdapter(R4WMissedcallDetails.this, R.layout.simple_list_item_misscall, dataArray_left);
		lv_history.setAdapter(status_adapter);
		
	}

	private class StatusArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StatusArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }

	@SuppressLint("SimpleDateFormat") 
	private String getDate(String timestamp){
		
		String time = "";
		
		DateFormat format_value = new SimpleDateFormat("h:mm a");
        String str = format_value.format(Long.parseLong(timestamp) * 1000);
        
        Log.d("trr", timestamp + " @");
        Log.d("strr", str + " @");
        
		
		Calendar calendar = Calendar.getInstance();
        TimeZone tz = TimeZone.getDefault();
        calendar.setTimeInMillis( Integer.parseInt(timestamp) * 1000L);
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dd =formatter.format(calendar.getTime());
         
        String datetrip[] = dd.split("-"); 
        String WeekdayIs= new SimpleDateFormat("EEE").format(calendar.getTime());
        String monthName = new SimpleDateFormat("MMM").format(calendar.getTime());
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        String d1 = formatter1.format(cal.getTime());
        String datetrip1[] = d1.split("-"); 
        
    	Log.d("dd", dd + " @");
    	Log.d("d1", d1 + " @");
        
    	Log.d("dateDay", datetrip[2] + " @");
    	Log.d("year", datetrip[0] + " @");
    	Log.d("WeekdayIs", WeekdayIs + " @");
    	Log.d("monthName", monthName + " @");
        
    	/*
        if(dd.equalsIgnoreCase(d1)){
        	time = "today " + str;
        	Log.d("tiime", time + " @");
        }else if(datetrip[1].equalsIgnoreCase(datetrip[1])){
        	if(datetrip[2].equalsIgnoreCase(datetrip1[2])){
        		time = "yesterday " + str;
            	Log.d("tiime", time + " @");
                   	}
        }else{
        	
        	time = WeekdayIs + ", " + datetrip[2] + " " + monthName  + ", " + datetrip[0]+ str.toString();
        	Log.d("tiime", time + " @");
        }
        */
    	
    	if(Integer.parseInt(datetrip1[0]) == Integer.parseInt(datetrip[0]))
    	{
    	
    		if(Integer.parseInt(datetrip1[1]) == Integer.parseInt(datetrip[1]))
        	{
    		        	
    			if(Integer.parseInt(datetrip1[2]) == Integer.parseInt(datetrip[2]))
    	    	{
    			   	time = "today ," + str;
    			     			    	
    	    	}else if(Integer.parseInt(datetrip1[2]) == Integer.parseInt(datetrip[2]) + 1){

    	    		time = "yesterday ," + str;
    	    		
    	    	}else{
    	    	 	time = WeekdayIs + ", " + datetrip[2] + " " + monthName  + ", " + datetrip[0] + ", " + str.toString();
    	    	       
    	    	}
        	}else{
        	 	time = WeekdayIs + ", " + datetrip[2] + " " + monthName  + ", " + datetrip[0] + ", " + str.toString();
        	           
	    	}	
    	}else{
    		time = WeekdayIs + ", " + datetrip[2] + " " + monthName  + ", " + datetrip[0] +", " + str.toString();
    	       	
    	}
        
        return time;
	}
	
	@SuppressLint({ "NewApi", "SdCardPath" }) 
	private void setDetails() {
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
       
        
		Intent intent = getIntent();
		name  = intent.getStringExtra("name");
		number  = intent.getStringExtra("number");
		Log.d("res ", "4");
		
		String path = "/sdcard/R4W/ProfilePic/"+number+".png";
		File userimage = new File(path); 
		if(userimage.exists()){
			try {
				Bitmap bmp = BitmapFactory.decodeFile(path);
				iv_contact_background.setImageBitmap(ImageHelperCircular.getRoundedCornerBitmap(bmp, bmp.getWidth()));
			} catch (Exception e) {
				// TODO: handle exception
				userimage.delete();
				iv_contact_background.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
			}
			
		}else{
			iv_contact_background.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
		}
		Log.d("res ", "5");
		tv_call_and_sms_label.setText("call +" + number);
		tv_header_text.setText(name);
	}

	private void initializer() {
		// TODO Auto-generated method stub
		lv_history = (ListView) findViewById(R.id.history);
		tv_call_and_sms_label = (TextView) findViewById(R.id.call_and_sms_label);
		tv_header_text = (TextView) findViewById(R.id.header_text);
		ll_call_and_sms_main_action = (LinearLayout) findViewById(R.id.call_and_sms_main_action);
		iv_contact_background = (ImageView) findViewById(R.id.contact_background);
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == Compatibility.getHomeMenuId()) {
			finish();
		return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	
}
