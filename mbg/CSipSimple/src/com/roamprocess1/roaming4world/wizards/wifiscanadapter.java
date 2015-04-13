package com.roamprocess1.roaming4world.wizards;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;


public class wifiscanadapter extends ArrayAdapter<String> {
	  private final Context context;
	  private final String[] name; 
	  private final String[] strength; 
	  private final String[] security; 
	  private final int mode;

	  public wifiscanadapter(Context context, String[] name, String[] strength, String[] security, int mode) {
		 
	    super(context, R.layout.allwifilistview, name);
	    this.context = context;
	    this.name = name;
	    this.strength = strength;
	    this.security = security;
	    this.mode = mode;
	  }


	  public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.allwifilistview, parent, false);
	    TextView namelist = (TextView) rowView.findViewById(R.id.tvwifissidlist);
	    TextView strengthlist = (TextView) rowView.findViewById(R.id.tvwifistrengthlist);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.ivwifisecuritylist);
	    
	    
	
	    namelist.setText(name[position]);
	    
	    strengthlist.setText(strength[position]);
	    
	    if(mode == 0){
	    if (security[position].contains("Open")) {

		      imageView.setImageResource(R.drawable.unlock);
	    } else {
	      imageView.setImageResource(R.drawable.lock);
	    		}
	    }
	   
	  else {
		  try {
			  
				boolean valuecheck = false;
			  	  valuecheck = security[position].equals(null);
			  	  Log.d("valuecheck", valuecheck+"");
				  if(valuecheck == false)
			  	  imageView.setImageResource(R.drawable.unlock);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		  
		 
	  }
	    return rowView;
	  }
	} 