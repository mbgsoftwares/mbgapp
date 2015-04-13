package com.roamprocess1.roaming4world.roaming4world;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;

public class CustomAdapterSelectCountry  extends ArrayAdapter<String>{
     
	    private Activity activity;
	    private ArrayList data, countryCodedata;
	    public Resources res;
	    SpinnerModel tempValues=null, tempCountryCode = null;
	    LayoutInflater inflater;
	    private final Context mContext;
	   
	    public CustomAdapterSelectCountry(
	                          Activity activitySpinner, 
	                          int textViewResourceId,   
	                          ArrayList objects,
	                          ArrayList countryCode,
	                          Resources resLocal,
	                          Context context
	                         ) 
	     {
	        super(activitySpinner, textViewResourceId, objects);
	        mContext = context;
	        activity = activitySpinner;
	        data     = objects;
	        res      = resLocal;
	        countryCodedata = countryCode;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	         
	      }


		@Override
	    public View getDropDownView(int position, View convertView,ViewGroup parent) {
	        return getCustomView( position, convertView, parent);
	    }
	 
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getCustomView(position, convertView, parent);
	    }
	 
	    // This function called for each row ( Called data.size() times )
	    public View getCustomView(int position, View convertView, ViewGroup parent) {
	    	
	    	View row = inflater.inflate(R.layout.spinner_row_country_selectcountry, parent, false);
	         
	      
	        tempValues = null;
	        tempValues = (SpinnerModel) data.get(position);
//	        tempCountryCode = null;
//	        tempCountryCode = (SpinnerModel) countryCodedata.get(position);
	         
	        TextView txtCountryName  = (TextView)row.findViewById(R.id.txtVcountryName);
	        ImageView imgCountryLogo = (ImageView)row.findViewById(R.id.imgCountryLogo);
	        TextView CountryCode = (TextView)row.findViewById(R.id.txtVcountryCode);
	        	CountryCode.setText( "(+" + tempValues.getCountryCode().toString() + ")");
	        	txtCountryName.setText( tempValues.getCountryName().toString() );
	         //txtCountryName.setText(mContext.getResources().getString(mContext.getResources().getIdentifier(tempValues.getCountryName(), "string", "com.roamprocess1.roaming4world")));
	        	imgCountryLogo.setImageResource(res.getIdentifier("com.roamprocess1.roaming4world:drawable/"+tempValues.getImage() + "copy",null,null));
	           
	        return row;
	     }
	 
}
