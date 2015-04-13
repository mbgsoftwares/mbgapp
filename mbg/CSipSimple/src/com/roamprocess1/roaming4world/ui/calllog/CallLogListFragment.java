

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

package com.roamprocess1.roaming4world.ui.calllog;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.roaming4world.MissedCallAdapter;
import com.roamprocess1.roaming4world.roaming4world.MissedcallFragment;
import com.roamprocess1.roaming4world.roaming4world.R4WChatUserImage;
import com.roamprocess1.roaming4world.ui.SipHome.ViewPagerVisibilityListener;
import com.roamprocess1.roaming4world.ui.calllog.CallLogAdapter.OnCallLogAction;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.widgets.CSSListFragment;

/**
 * Displays a list of call log entries.
 */
public class CallLogListFragment extends CSSListFragment implements ViewPagerVisibilityListener,
        CallLogAdapter.CallFetcher, OnCallLogAction {

    private static final String THIS_FILE = "CallLogFragment";

    private boolean mShowOptionsMenu;
    private CallLogAdapter mAdapter;


    private boolean mDualPane;

    private ActionMode mMode;
    
    ListView lv_missedcall;
    View v;
    Button btn_callLog_allCalls, btn_callLog_MissedCalls;
    
    DBContacts dbContacts;
    Cursor cursor;
    private MissedCallAdapter missedCallAdapter = null;
    private String[] number, name, count, time;
    
    SharedPreferences prefs;
	String stored_lastdialnumber = "com.roamprocess1.roaming4world.lastdialnumber";

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
	private void updateMissedcallDetails() {

		dbContacts.openToRead();
		cursor = dbContacts.fetch_details_from_MIssedCall_Offline_Table();
		int row = cursor.getCount();
		Log.d("testCursor", cursor.getCount() + " !");
		if (row > 0) {
			cursor.moveToFirst();
			number = new String[row];
			name = new String[row];
			count = new String[row];
			time = new String[row];
			int i = 0;
			do {
				number[i] = cursor.getString(1).toString();
				time[i] = cursor.getString(2).toString();
				// name[row-1] = cursor.getString(1).toString();
				count[i] = cursor.getString(3).toString();
				
				Log.d(" 1 number " + i, number[i] + " !");
				Log.d(" 2 time " + i, time[i] + " !");
				Log.d(" 3 count " + i, count[i] + " !");
						
				i++;
				

				
			} while (cursor.moveToNext());
		}
		cursor.close();
		dbContacts.close();
	}
	/*

	 public static CallLogListFragment newInstance(String text) {

		 CallLogListFragment f = new CallLogListFragment();
	        Bundle b = new Bundle();
	        b.putString("msg", text);

	        f.setArguments(b);

	        return f;
	    }
	*/    
	
    private void add_missed_call_adapter() {
    	if(missedCallAdapter == null){
    		missedCallAdapter = new MissedCallAdapter(getActivity(), number, number, count, time);
    		
    	}
		lv_missedcall.setAdapter(mAdapter);
	}
    
    private void attachAdapter() {
        if(getListAdapter() == null) {
            if(mAdapter == null) {
                Log.d(THIS_FILE, "Attach call log adapter now");
                // Adapter
               System.out.println("CallLogListFragment 1"); 
                mAdapter = new CallLogAdapter(getActivity(), this);
                System.out.println("CallLogListFragment 2"); 
                mAdapter.setOnCallLogActionListener(this);
                System.out.println("CallLogListFragment 3"); 
                
            }
            setListAdapter(mAdapter);
            
            System.out.println("CallLogListFragment 3"); 
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
    	
    	System.out.println("CallLogListFragment onCreateView"); 
        v = inflater.inflate(R.layout.call_log_fragment, container, false);
        
        dbContacts = new DBContacts(getActivity());
        lv_missedcall = (ListView) v.findViewById(R.id.lv_missed_Calls);
        btn_callLog_allCalls = (Button) v.findViewById(R.id.btn_callLog_allCalls);
        btn_callLog_MissedCalls = (Button) v.findViewById(R.id.btn_callLog_MissedCalls);
        
		prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world",
				Context.MODE_PRIVATE);

        
        btn_callLog_allCalls.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				btn_callLog_allCalls.setBackgroundColor(Color.parseColor("#189AD1"));
				btn_callLog_MissedCalls.setBackgroundColor(Color.GRAY);
				
			}
		});
        

        btn_callLog_MissedCalls.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				btn_callLog_MissedCalls.setBackgroundColor(Color.parseColor("#189AD1"));
				btn_callLog_allCalls.setBackgroundColor(Color.GRAY);
				getFragmentManager().beginTransaction().replace(R.id.Frame_Layout, new MissedcallFragment()).commitAllowingStateLoss();

				
			}
		});
        
      
        btn_callLog_allCalls.setBackgroundColor(Color.parseColor("#189AD1"));
        btn_callLog_MissedCalls.setBackgroundColor(Color.GRAY);
        
        
        return v;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        System.out.println("CallLogListFragment oncreate View"); 
        
        // View management
        mDualPane = getResources().getBoolean(R.bool.use_dual_panes);

        System.out.println("CallLogListFragment Step 3"); 
        
        // Modify list view
        ListView lv = getListView();
        System.out.println("CallLogListFragment Step 4"); 
        
        lv.setVerticalFadingEdgeEnabled(true);
        
        System.out.println("CallLogListFragment Step 4"); 
 
        lv_missedcall = (ListView) v.findViewById(R.id.lv_missed_Calls);
        
        
        // lv.setCacheColorHint(android.R.color.transparent);
        if (mDualPane) {
            lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            System.out.println("CallLogListFragment Step 5"); 
            
            lv.setItemsCanFocus(false);
        } else {
            lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
            System.out.println("CallLogListFragment Step 6"); 
            
            lv.setItemsCanFocus(true);
        }
        
        // Map long press
        lv.setLongClickable(true);/*
        lv.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> ad, View v, int pos, long id) {
                turnOnActionMode();
                
           	 System.out.println("CallLogListFragment Step 8"); 
                
                getListView().setItemChecked(pos, true);
                mMode.invalidate();
                return true;
            }
        });*/
    }
    
   
    @Override
    public void onResume() {
        super.onResume();
        fetchCalls();
        updateMissedcallDetails();
        add_missed_call_adapter();
        Log.setLogLevel(6);
        Log.d("CallLoglistfragment onResume", "called");
        
      
        /*
        
        imgClearList.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
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
		});

        */
    }
   

    @Override
    public void fetchCalls() {
        attachAdapter();
        if(isResumed()) {
       	 System.out.println("CallLogListFragment Step 9"); 
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    boolean alreadyLoaded = false;
    
    @SuppressLint("NewApi")
	@Override
    public void onVisibilityChanged(boolean visible) {
        if (mShowOptionsMenu != visible) {
            mShowOptionsMenu = visible;
            // Invalidate the options menu since we are changing the list of
            // options shown in it.
            
       	 System.out.println("CallLogListFragment Step 10"); 
            SherlockFragmentActivity activity = getSherlockActivity();
            if (activity != null) {
            	
            	 System.out.println("CallLogListFragment Step 11"); 
                activity.invalidateOptionsMenu();
            }
        }
        

        if(visible) {
            attachAdapter();
            // Start loading
            if(!alreadyLoaded) {
                getLoaderManager().initLoader(0, null, this);
                alreadyLoaded = true;
            }
        }
        
        
        if (visible && isResumed()) {
            //getLoaderManager().restartLoader(0, null, this);
            ListView lv = getListView();
            if (lv != null && mAdapter != null) {
                final int checkedPos = lv.getCheckedItemPosition();
                if (checkedPos >= 0) {
                    // TODO post instead
                    Thread t = new Thread() {
                        public void run() {
                            final long[] selectedIds = mAdapter.getCallIdsAtPosition(checkedPos);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    viewDetails(checkedPos, selectedIds);  
                                }
                            });
                        };
                    };
                    t.start();
                }
            }
        }
        
        
        if(!visible && mMode != null) {
            mMode.finish();
        }
    }

    /*
    // Options
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        int actionRoom = getResources().getBoolean(R.bool.menu_in_bar) ? MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER;
        MenuItem delMenu = menu.add(R.string.callLog_delete_all);
        delMenu.setIcon(R.drawable.ic_ab_trash_dark).setShowAsAction(actionRoom);
        menu.removeItem(3);
        delMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteAllCalls();
                return true;
            }
        });
    }
    */

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
            Log.e(THIS_FILE, "error while trying to show deletion yes/no dialog");
        }
    }

    // Loader
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(), SipManager.CALLLOG_URI, new String[] {
                CallLog.Calls._ID, CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL,
                CallLog.Calls.CACHED_NUMBER_TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE,
                CallLog.Calls.NEW, CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
                SipManager.CALLLOG_PROFILE_ID_FIELD
        },
                null, null,
                Calls.DEFAULT_SORT_ORDER);
    }


    @Override
    public void viewDetails(int position, long[] callIds) {
        ListView lv = getListView();
        if(mMode != null) {
            lv.setItemChecked(position, !lv.isItemChecked(position));
            mMode.invalidate();
            // Don't see details in this case
            
            System.out.println("CallLogListFragment Step 12"); 
            return;
        }
        
        if (mDualPane) {
        	
        	System.out.println("CallLogListFragment  === mDualPane");
        	
        	 System.out.println("CallLogListFragment Step 13"); 
        	 
            // If we are not currently showing a fragment for the new
            // position, we need to create and install a new one.
            CallLogDetailsFragment df = new CallLogDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putLongArray(CallLogDetailsFragment.EXTRA_CALL_LOG_IDS, callIds);
            df.setArguments(bundle);
            // Execute a transaction, replacing any existing fragment
            // with this one inside the frame.
            System.out.println("CallLogListFragment Step 14"); 
       	 
            try {
            	
            	 FragmentTransaction ft = getFragmentManager().beginTransaction();
                 ft.replace(R.id.details, df, null);
                 ft.addToBackStack(null);
                 System.out.println("CallLogListFragment Step 15"); 
            	 ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                 ft.commit();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
           

            getListView().setItemChecked(position, true);
        } else {
            Intent it = new Intent(getActivity(), CallLogDetailsActivity.class);
            it.putExtra(CallLogDetailsFragment.EXTRA_CALL_LOG_IDS, callIds);
            System.out.println("CallLogListFragment Step 16"); 
          	 try {
          		  getActivity().startActivity(it);
			} catch (Exception e) {
				// TODO: handle exception
			}
          
        }
    }

    @Override
    public void placeCall(String number, Long accId) {
        if(!TextUtils.isEmpty(number)) {
        	
        	System.out.println("CallLogListFragment Step 18"); 
        	System.out.println("Call list Fragment ==== placeCall ");
        	prefs.edit().putString(stored_lastdialnumber, number).commit();
            Intent it = new Intent(Intent.ACTION_CALL);
            it.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_CSIP, SipUri.getCanonicalSipContact(number, false)));
            it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(accId != null) {
            	  System.out.println("CallLogListFragment Step 19"); 
                	 
                it.putExtra(SipProfile.FIELD_ACC_ID, accId);
            }
            getActivity().startActivity(it);
        }
    }

    
    // Action mode
    
    private void turnOnActionMode() {
        Log.d(THIS_FILE, "Long press");
       // mMode = getSherlockActivity().startActionMode(new CallLogActionMode());
        ListView lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
    }
    /*
    private class CallLogActionMode  implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Log.d(THIS_FILE, "onCreateActionMode");
            System.out.println("CallLogListFragment Step 20"); 
          	 
            getSherlockActivity().getSupportMenuInflater().inflate(R.menu.call_log_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            Log.d(THIS_FILE, "onPrepareActionMode");
            ListView lv = getListView();
            int nbrCheckedItem = 0;

            for (int i = 0; i < lv.getCount(); i++) {
                if (lv.isItemChecked(i)) {
                    nbrCheckedItem++;
                }
            }
            menu.findItem(R.id.delete).setVisible(nbrCheckedItem > 0);
            menu.findItem(R.id.dialpad).setVisible(nbrCheckedItem == 1);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int itemId = item.getItemId();
            if(itemId == R.id.delete) {
                actionModeDelete();
                return true;
            }else if(itemId == R.id.invert_selection) {
                actionModeInvertSelection();
                return true;
            }else if(itemId == R.id.dialpad) {
                actionModeDialpad();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            Log.d(THIS_FILE, "onDestroyActionMode");

            ListView lv = getListView();
            // Uncheck all
            int count = lv.getAdapter().getCount();
            for (int i = 0; i < count; i++) {
                lv.setItemChecked(i, false);
            }
            mMode = null;
        }
        
    }
    */
    private void actionModeDelete() {
        ListView lv = getListView();
        
        ArrayList<Long> checkedIds = new ArrayList<Long>();
        
        for(int i = 0; i < lv.getCount(); i++) {
            if(lv.isItemChecked(i)) {
                long[] selectedIds = mAdapter.getCallIdsAtPosition(i);
                
                for(long id : selectedIds) {
                    checkedIds.add(id);
                }
                
            }
        }
        if(checkedIds.size() > 0) {
            String strCheckedIds = TextUtils.join(", ", checkedIds);
            Log.d(THIS_FILE, "Checked positions ("+ strCheckedIds +")");
            getActivity().getContentResolver().delete(SipManager.CALLLOG_URI, CallLog.Calls._ID + " IN ("+strCheckedIds+")", null);
            mMode.finish();
        }
    }
    
    private void actionModeInvertSelection() {
        ListView lv = getListView();

        for(int i = 0; i < lv.getCount(); i++) {
            lv.setItemChecked(i, !lv.isItemChecked(i));
        }
        mMode.invalidate();
    }
    
    private void actionModeDialpad() {
        
        ListView lv = getListView();

        for(int i = 0; i < lv.getCount(); i++) {
            if(lv.isItemChecked(i)) {
                mAdapter.getItem(i);
                /*
                System.out.println("CallLogListFragment =====actionModeDialpad");		
                System.out.println("CallLogListFragment Step 22"); 
              	 */	
                		
                String number = mAdapter.getCallRemoteAtPostion(i);
                if(!TextUtils.isEmpty(number)) {
                    Intent it = new Intent(Intent.ACTION_DIAL);
                    it.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_SIP, number));
                    startActivity(it);
                }
                break;
            }
        }
        mMode.invalidate();
        
    }
    
    @Override
    public void changeCursor(Cursor c) {
        mAdapter.changeCursor(c);
    }
    
}
