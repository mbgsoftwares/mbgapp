package com.roamprocess1.roaming4world.roaming4world;


import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
public class R4WFlightSearch extends SherlockFragment{

	View rootView;
	private EditText edtFlightNumber, edtCarrierCode,edtDepYear, edtDepMonth, edtDepDay;
	private Button btnFlightInfo, btnSkip;
	String get_info_status_value;
	private SharedPreferences prefs;
	String shared_pref_dep_month,shared_pref_dep_day,shared_pref_carrier_code,shared_pref_flight_id, shared_pref_dep_year, shared_pref_update, shared_pref_flight_number_search_query, shared_pref_dep_date, shared_pref_dep_date_value, shared_pref_flight_number_search_query_value;
	private String destination_flight_no, flight_no_search_query, flight_dep_date;
	String get_info_show_status;
	
	static final int DATE_PICKER_ID = 1111;
	private int dpyear, dpmonth, dpday;
	private int mYear, mMonth, mDay;
	 TextView et_from_date;
	 OnDateSetListener ondate;
	 FragmentManager frgmngr;
	 
	 Button ibdatepicker;
	 boolean isOkayClicked;
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.r4wflightwindow, container, false);
		Initilizer();
		onclickCheck();
		edtDepYear.setEnabled(false);
		if(get_info_status_value.equals("0")){
			dateOnCreate();
		}else{
			dateOnSharedPref();
		}
		
		ibdatepicker.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				datepickershow();
			}
		});
		
		
		
		OnDateSetListener ondate = new OnDateSetListener() {
			  @Override
			  public void onDateSet(DatePicker view, int year, int monthOfYear,
			    int dayOfMonth) {
				  
				  
				if (isOkayClicked) {
					et_from_date.setText(year + (monthOfYear + 1)
                              + dayOfMonth);
                      dpyear = year;
                      dpmonth = monthOfYear;
                      dpday = dayOfMonth;
                  }
                  isOkayClicked = false;
			  }
			 };
		
		
		return rootView;
	}
	
	private void datepickershow(){
		
		

        
		final DatePickerDialog dpdFromDate = new DatePickerDialog(getActivity(), ondate, mYear, mMonth,
                  mDay);
        dpdFromDate.show();

        dpdFromDate.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
               if (which == DialogInterface.BUTTON_NEGATIVE) {
 
                   
               }
            }
        });
		
        dpdFromDate.setButton(DialogInterface.BUTTON_POSITIVE, "Set", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                	
                	
                	//isOkayClicked = true;
                    DatePicker datePicker = dpdFromDate.getDatePicker();
                    dpyear = datePicker.getYear();
                    dpmonth = datePicker.getMonth() + 1;
                    dpday = datePicker.getDayOfMonth();
                    Log.d("yearqq", dpyear + " ^");
                    edtDepYear.setText(dpyear + "-" + (dpmonth) + "-" + dpday);
                	                }
             }
         });
        
	}

	
	
	
	
	
	private void onclickCheck() {
		// TODO Auto-generated method stub
		

		// TODO Auto-generated method stub
			
		
		try {
			
			if(CurrentFragment.flightsearchCheck == 1){
			btnFlightInfo.setText("Edit");
			edtFlightNumber.setText(prefs.getString(shared_pref_flight_id, ""));
			edtCarrierCode.setText(prefs.getString(shared_pref_carrier_code, ""));
			edtDepDay.setText(prefs.getString(shared_pref_dep_day, ""));
			edtDepMonth.setText(prefs.getString(shared_pref_dep_month, ""));
			edtDepYear.setText(prefs.getString(shared_pref_dep_year, ""));
			
		}
		}catch (Exception e){
			e.printStackTrace();
			Log.d("Prefs error in geting", "12");
		}
		
		
		
		btnSkip.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(get_info_status_value.equals("0")){
					CurrentFragment.flightinfo = 1;
					prefs.edit().putString(get_info_show_status, "2").commit();	
				}else{
					CurrentFragment.flightLoading = false;
						
				}
				
				
				getFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new R4wFlightDetails()).commit();
			
				
			}
		});

		btnFlightInfo.setOnClickListener(new View.OnClickListener() {

			

			@Override
			public void onClick(View arg0) {
				
				//prefs.edit().putString(get_info_show_status, "1").commit();
				// TODO Auto-generated method stub
				// flight information page call by intent
				String Carrier_code = edtCarrierCode.getText().toString().toUpperCase();
				prefs.edit().putString(shared_pref_carrier_code,Carrier_code).commit();
				destination_flight_no=edtFlightNumber.getText().toString();
				prefs.edit().putString(shared_pref_flight_id,destination_flight_no).commit();
				flight_no_search_query=edtCarrierCode.getText().toString().toUpperCase()+"/"+edtFlightNumber.getText().toString()+"/dep/";
				
				/*
				String temp_dep_year=edtDepYear.getText().toString();
				String temp_dep_month=edtDepMonth.getText().toString();
				String temp_dep_day=edtDepDay.getText().toString();
				*/
				
				String temp_dep_year=dpyear + "";
				String temp_dep_month=dpmonth + "";
				String temp_dep_day=dpday + "";
				
				
				int temp_dep_month_length=temp_dep_month.length();
				if(temp_dep_month_length==2)
				{
					boolean temp_dep_month_check_first_char_value=temp_dep_month.startsWith("0");
					if(temp_dep_month_check_first_char_value)
					{
						temp_dep_month=temp_dep_month.substring(1);
					}
					
					boolean temp_dep_day_check_first_char_value=temp_dep_day.startsWith("0");
					if(temp_dep_day_check_first_char_value)
					{
						temp_dep_day=temp_dep_day.substring(1);
					}
				}
				flight_dep_date=temp_dep_year+"/"+temp_dep_month+"/"+temp_dep_day;
				
				Log.d("temp_dep_day1", temp_dep_day);
				Log.d("temp_dep_month1", temp_dep_month);
				Log.d("temp_dep_year1", temp_dep_year);
				
				
				prefs.edit().putString(shared_pref_flight_number_search_query, flight_no_search_query).commit();
				prefs.edit().putString(shared_pref_dep_date, flight_dep_date).commit()	;
				
				try {
					
					Log.d("Day Before",temp_dep_day);					
					Log.d("Month Before",temp_dep_month);
					Log.d("Year Before Commit",temp_dep_year);
					prefs.edit().putString(shared_pref_dep_year, temp_dep_year).commit();
					Log.d("Year After Commit",temp_dep_year);
					Log.d("Month Before Commit",temp_dep_month);
					prefs.edit().putString(shared_pref_dep_month, temp_dep_month).commit();
					Log.d("Month After Commit",temp_dep_month);
					Log.d("Day Before Commit",temp_dep_day);
					prefs.edit().putString(shared_pref_dep_day, temp_dep_day).commit();
					Log.d("Day After Commit",temp_dep_day);
					
				} catch (Exception e) {
					Log.d("Prefs error in seting", "11");
					
				}
				
				
				
				
				
				prefs.edit().putString(get_info_show_status, "1").commit();
				
				getFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new R4wFlightDetails()).commit();
				
				
				
			}
		});

	}
	
	
	
    	
	

	private void Initilizer() {
		// TODO Auto-generated method stub
		
		edtFlightNumber = (EditText) rootView.findViewById(R.id.flight_number);
		edtCarrierCode= (EditText) rootView.findViewById(R.id.carrier_code);
		edtDepYear= (EditText) rootView.findViewById(R.id.dep_year);
		//edtDepMonth= (EditText) rootView.findViewById(R.id.dep_month);
		//edtDepDay= (EditText) rootView.findViewById(R.id.dep_day);
		btnFlightInfo = (Button) rootView.findViewById(R.id.btnflight);
		btnSkip = (Button) rootView.findViewById(R.id.btnskip);
		
		et_from_date = (TextView) rootView.findViewById(R.id.tvdatePicker);
		ibdatepicker = (Button) rootView.findViewById(R.id.ibcallDatePicker);
		
		prefs=getActivity().getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		
		shared_pref_flight_number_search_query="com.roamprocess1.roaming4world.flight_number_search_query";
		shared_pref_dep_date="com.roamprocess1.roaming4world.dep_date";
		shared_pref_carrier_code="com.roamprocess1.roaming4world.carrier_code";
		shared_pref_dep_date_value=prefs.getString(shared_pref_dep_date, "no");
		shared_pref_flight_number_search_query_value=prefs.getString(shared_pref_flight_number_search_query, "no");
		shared_pref_flight_id="com.roamprocess1.roaming4world.flight_id";
		get_info_show_status="com.roamprocess1.roaming4world.get_info_show";
		
		shared_pref_dep_month="com.roamprocess1.roaming4world.pref_dep_month";
		shared_pref_dep_day="com.roamprocess1.roaming4world.pref_dep_day";
		shared_pref_dep_year="com.roamprocess1.roaming4world.pref_dep_year";
		
		
		get_info_status_value = prefs.getString(get_info_show_status, "0");
		
		
		
	}
	
	
	private void  dateOnCreate() {
	
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
	}
	
	private void dateOnSharedPref(){
		
		mDay = Integer.parseInt((prefs.getString(shared_pref_dep_day, "")));
		mMonth = Integer.parseInt((prefs.getString(shared_pref_dep_month, "")));
		mYear = Integer.parseInt((prefs.getString(shared_pref_dep_year, "")));
		edtDepYear.setText(mYear+"-"+mMonth+"-"+mDay);
		 dpyear = mYear;
         dpmonth = mMonth;
         dpday = mDay;
		
	}

	
	
}
