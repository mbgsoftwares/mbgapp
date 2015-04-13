package com.roamprocess1.roaming4world.syncadapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import com.roamprocess1.roaming4world.R;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ContactShow extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private ListView lv_contact;
	private SimpleCursorAdapter mAdapter;
	private Menu mOptionsMenu;
	private Object mSyncObserverHandle;
	
	Cursor cursor;
	
	private static final String[] FROM_COLUMNS = new String[]{
		ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
		ContactsContract.CommonDataKinds.Phone.NUMBER
};

/**
 * List of Views which will be populated by Cursor data.
 */
private static final int[] TO_FIELDS = new int[]{
        android.R.id.text1,
        android.R.id.text2};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
  //      View rootView = inflater.inflate(R.layout.fragment_main, container, false);
       
        return null;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		
//		 lv_contact = ((ListView) view.findViewById(R.id.lv_contact));
			setAdapter();
			lv_contact.setAdapter(mAdapter);
		
		
		
	}

	private void setAdapter() {
		// TODO Auto-generated method stub
		cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
		mAdapter = new SimpleCursorAdapter(
                getActivity(),       // Current context
                android.R.layout.simple_list_item_activated_2,  // Layout for individual rows
                cursor,                // Cursor
                FROM_COLUMNS,        // Cursor columns to use
                TO_FIELDS,           // Layout fields to use
                0                    // No flags
        );	
	}

	  @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);

	        // Create account, if needed
	        SyncUtils.CreateSyncAccount(activity);
	    }

	
	  @Override
	    public void onResume() {
	        super.onResume();
	        mSyncStatusObserver.onStatusChanged(0);

	        // Watch for sync state changes
	        final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING |
	                ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
	        mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        if (mSyncObserverHandle != null) {
	            ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
	            mSyncObserverHandle = null;
	        }
	    }

	    @Override
	    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	        super.onCreateOptionsMenu(menu, inflater);
	        mOptionsMenu = menu;
	        inflater.inflate(R.menu.main, menu);
	    }

	    /**
	     * Respond to user gestures on the ActionBar.
	     */
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	/*
	        switch (item.getItemId()) {
	            // If the user clicks the "Refresh" button.
	            case R.id.menu_refresh:
	                SyncUtils.TriggerRefresh();
	                return true;
	        }
	        */
	        return super.onOptionsItemSelected(item);
	    }

	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// TODO Auto-generated method stub
		 mAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.changeCursor(null);
	}
	
	public void createFile(Cursor cursorContact){
		try {

			String path = Environment.getExternalStorageDirectory().getAbsolutePath() ;
			File file = new File(path , "Contacts.txt");
			
			if(file.exists()){
				Log.d("Contact File ", "exist");
				file.delete();
				file = new File(path , "Contacts.txt");
			}
			
			FileOutputStream fOut = new FileOutputStream(file);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);				

			myOutWriter.append("91971720221");
			
			cursorContact.moveToFirst();
			while(cursorContact.moveToNext()){
				String numbers = cursorContact.getString(cursorContact.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if(numbers != null){
					if(!numbers.contains("*") && !numbers.contains("#")){
						if(numbers.contains("+")){
							numbers = numbers.replace("+", "");
						}
						numbers = numbers.replaceAll("\\s+","");
						myOutWriter.append( "," + numbers);
					}
				}
			}
			
	            
	        myOutWriter.close();
	        fOut.close();
	 	
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	 private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
	        /** Callback invoked with the sync adapter status changes. */
	        @Override
	        public void onStatusChanged(int which) {
	            getActivity().runOnUiThread(new Runnable() {
	                /**
	                 * The SyncAdapter runs on a background thread. To update the UI, onStatusChanged()
	                 * runs on the UI thread.
	                 */
	                @Override
	                public void run() {
	                    // Create a handle to the account that was created by
	                    // SyncService.CreateSyncAccount(). This will be used to query the system to
	                    // see how the sync status has changed.
	                    Account account = GenericAccountService.GetAccount();
	                    if (account == null) {
	                        // GetAccount() returned an invalid value. This shouldn't happen, but
	                        // we'll set the status to "not refreshing".
	                        setRefreshActionButtonState(false);
	                        return;
	                    }

	                    // Test the ContentResolver to see if the sync adapter is active or pending.
	                    // Set the state of the refresh button accordingly.
	                    boolean syncActive = ContentResolver.isSyncActive(
	                            account, SyncUtils.CONTENT_AUTHORITY);
	                    boolean syncPending = ContentResolver.isSyncPending(
	                            account, SyncUtils.CONTENT_AUTHORITY);
	                    setRefreshActionButtonState(syncActive || syncPending);
	                }
	            });
	        }
	    };
	
	    public void setRefreshActionButtonState(boolean refreshing) {
	        if (mOptionsMenu == null) {
	            return;
	        }
	        /*
	        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.menu_refresh);
	        if (refreshItem != null) {
	            if (refreshing) {
	                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
	            } else {
	                refreshItem.setActionView(null);
	            }
	        }
	        */
	    }
		
}
