package com.roamprocess1.roaming4world.ui.recents;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.roaming4world.MissedcallFragment;
import com.roamprocess1.roaming4world.ui.calllog.CallLogListFragment;


public class Recents extends SherlockFragment{

	View v;
	TextView all_calls, missed_calls;
	ViewPager pager;
	private LinearLayout linerRAll,linerRMissed;
	
	CallLogListFragment frg_allcalls;
	MissedcallFragment frg_misscall;
	private ImageButton imgClearList;
	private Typeface mediumAllCaps,robotoBold,robotoRegular;
    public static boolean ICON_DELETE_SHOW = true;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		
	    v = inflater.inflate(R.layout.recents, container, false);
	    mediumAllCaps = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Medium.ttf");
		robotoBold = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Bold.ttf");
		robotoRegular=Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Regular.ttf");
		
	    pager = (ViewPager) v.findViewById(R.id.vp_recents);
	    linerRAll=(LinearLayout)v.findViewById(R.id.lnrlRAll);
	    linerRMissed=(LinearLayout)v.findViewById(R.id.lnrlRMiss);
	    
	    //   pager.setAdapter(new MyPagerAdapter(getFragmentManager()));
       
	    pager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
	    pager.setCurrentItem(0);
        
		all_calls = (TextView) v.findViewById(R.id.btn_callLog_allCalls);
		missed_calls = (TextView) v.findViewById(R.id.btn_callLog_MissedCalls);
		
		all_calls.setTypeface(mediumAllCaps);
		missed_calls.setTypeface(mediumAllCaps);

		
		
		linerRAll.setVisibility(View.VISIBLE);
		linerRMissed.setVisibility(View.INVISIBLE);
		
		frg_allcalls = new CallLogListFragment();
		frg_misscall = new MissedcallFragment();
		imgClearList=(ImageButton) getActivity().getActionBar().getCustomView().findViewById(R.id.imgClearList);

		
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				if(arg0 == 0){
					linerRAll.setVisibility(View.VISIBLE);
					linerRMissed.setVisibility(View.INVISIBLE);
					imgClearList.setVisibility(View.VISIBLE);
				        
						
				}else if(arg0 == 1){
					linerRAll.setVisibility(View.INVISIBLE);
					linerRMissed.setVisibility(View.VISIBLE);
					imgClearList.setVisibility((ICON_DELETE_SHOW) ? View.VISIBLE : View.GONE);
						
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		all_calls.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				linerRAll.setVisibility(View.VISIBLE);
				linerRMissed.setVisibility(View.INVISIBLE);
				imgClearList.setVisibility(View.VISIBLE);
				pager.setCurrentItem(0);
			}
		});
		
		missed_calls.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				linerRAll.setVisibility(View.INVISIBLE);
				linerRMissed.setVisibility(View.VISIBLE);
				pager.setCurrentItem(1);
				imgClearList.setVisibility((ICON_DELETE_SHOW) ? View.VISIBLE : View.GONE);
			}
		});
		
		
		imgClearList.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pager.getCurrentItem() == 0){
					deleteAllCalls();
				}else{
					frg_misscall.dialogBoxDeleteMissedCall();
				}
			}
		});
		
		Bundle bundle = getActivity().getIntent().getExtras();
		   
	     if(bundle != null){
		    	 boolean fl= bundle.getBoolean("MissedCall");
		    	 if(fl == true){
		    		 	linerRAll.setVisibility(View.INVISIBLE);
						linerRMissed.setVisibility(View.VISIBLE);
				 		pager.setCurrentItem(1);
		    	 }
	     }
   
		
		return v;
	}
	
	private void deleteAllCalls() {
    	AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.callLog_delDialog_title);
        alertDialog.setMessage(getString(R.string.callLog_delDialog_message));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.callLog_delDialog_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	getActivity().getContentResolver().delete(SipManager.CALLLOG_URI, null,null);
                    	
                    	
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.callLog_delDialog_no),
                (DialogInterface.OnClickListener) null);
        try {
            alertDialog.show();
        } catch (Exception e) {
  //          Log.e(THIS_FILE, "error while trying to show deletion yes/no dialog");
        }
	
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        
		@Override
        public Fragment getItem(int pos) {
            switch(pos) {

            case 0: return frg_allcalls;
            case 1: return frg_misscall;
            default: return frg_allcalls;
            
         /*   
            case 0: return CallLogListFragment.newInstance("FirstFragment, Instance 1");
            case 1: return MissedcallFragment.newInstance("SecondFragment, Instance 1");
            default: return CallLogListFragment.newInstance("ThirdFragment, Default");
         */
            
            }
        }

        @Override
        public int getCount() {
            return 2;
        }       
    }
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		

	}
	
	
	
	
	
}
