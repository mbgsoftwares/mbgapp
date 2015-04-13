package com.roamprocess1.roaming4world.wizards;

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
 
/***** Adapter class extends with ArrayAdapter ******/
public class CustomAdapter extends ArrayAdapter<String>{
     
    private Activity activity;
    private ArrayList data;
    public Resources res;
    SpinnerModel tempValues=null;
    LayoutInflater inflater;
    private final Context mContext;
    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(
                          Activity activitySpinner, 
                          int textViewResourceId,   
                          ArrayList objects,
                          Resources resLocal,
                          Context context
                         ) 
     {
        super(activitySpinner, textViewResourceId, objects);
        mContext = context;
        /********** Take passed values **********/
        activity = activitySpinner;
        data     = objects;
        res      = resLocal;
    
        /***********  Layout inflator to call external xml layout () **********************/
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
 
        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = inflater.inflate(R.layout.spinner_rows, parent, false);
         
        /***** Get each Model object from Arraylist ********/
        tempValues = null;
        tempValues = (SpinnerModel) data.get(position);
         
        TextView label        = (TextView)row.findViewById(R.id.company);
        ImageView companyLogo = (ImageView)row.findViewById(R.id.image);
        
        if(position==0){
            // Default selected Spinner item 
            label.setText("Countries");
        }
        else
        {
        	//Log.d("test area 1","test area 1 - success");
            // Set values for spinner each row 
            label.setText(mContext.getResources().getString(mContext.getResources().getIdentifier(tempValues.getCompanyName(), "string", "com.roamprocess1.roaming4world")));
            //Log.d("test area 2","test area 2 - success");
            //sub.setText(tempValues.getUrl());
            companyLogo.setImageResource(res.getIdentifier(tempValues.getImage(), "drawable-xhdpi/32", "com.roamprocess1.roaming4world"));
            //Log.d("test area 3","test area 3 - success");
        }
        return row;
     }
 }