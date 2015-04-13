package com.roamprocess1.roaming4world.roaming4world;


import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;

public class MissedCallAdapter extends ArrayAdapter<String> {
	private final Activity context;
	 private final String[] number, name, count, time;

	  public MissedCallAdapter(Activity mcontext, String[] numbers, String[] names, String[] counts, String[] times) {
		    super(mcontext, R.layout.missedcall_adaptor_row);
		    this.context = mcontext;
		    this.number = numbers;
		    this.name = names;
		    this.count = counts;
		    this.time = times;

		  }
	  
	  
	  @Override
	  public int getCount() {
		  return number.length; 
	  };
	  
	@SuppressLint("SdCardPath") @Override
	public View getView(int position, View view, ViewGroup parent) {
		
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.missedcall_adaptor_row, null, true);
		TextView tv_number = (TextView) rowView.findViewById(R.id.tv_number_missedcall);
		TextView tv_name = (TextView) rowView.findViewById(R.id.tv_name_missedcall);
		TextView tv_time = (TextView) rowView.findViewById(R.id.tv_missedcall_date);

		String path = "/sdcard/R4W/ProfilePic/"+number[position]+".png";
		File userimage = new File(path); 
		if(userimage.exists()){
			ImageView iv_missedcall_adapter_userpic = (ImageView) rowView.findViewById(R.id.iv_missedcall_adapter_userpic);
			try {
				Bitmap bmp = BitmapFactory.decodeFile(path);
				iv_missedcall_adapter_userpic.setImageBitmap(ImageHelperCircular.getRoundedCornerBitmap(bmp, bmp.getWidth()));

			} catch (Exception e) {
				// TODO: handle exception
				userimage.delete();
				iv_missedcall_adapter_userpic.setImageResource(R.drawable.ic_contact_picture_180_holo_light);
			}
			
		}
		
		tv_number.setText("+" + number[position] + " (" + count[position] + ")");
		tv_name.setText(name[position]);

		CharSequence dateText = DateUtils.getRelativeTimeSpanString(
				Long.parseLong(time[position]) * 1000,
				System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
				DateUtils.FORMAT_ABBREV_RELATIVE);
		// change the icon for Windows and iPhone

		tv_time.setText(dateText);

		Log.d(" 1 number " + position, number[position] + " !");
		Log.d(" 2 time " + position, time[position] + " !");
		Log.d(" 2 date " + position, dateText + " !");
		Log.d(" 3 count " + position, count[position] + " !");

		
		
		return rowView;
	}
}

