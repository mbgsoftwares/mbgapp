package com.roamprocess1.roaming4world.contactlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;

public class ContactListActivity extends Activity implements TextWatcher {

	private ExampleContactListView listview;
	
	private EditText searchBox;
	private String searchString;
	
	private Object searchLock = new Object();
	boolean inSearchMode = false;

	private final static String TAG = "com.ngohung.view.ContactListActivity";
	
	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;
	public DBContacts sqliteprovider;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testcontact_list);
        
       // final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);

		//actionBar.setTitle("All Contacts");
		//actionBar.setHomeLogo(R.drawable.ic_launcher);
		
        /*
		List<ContactItemInterface>  list = new  ArrayList<ContactItemInterface> ();
		
		sqliteprovider=new DBContacts(ContactListActivity.this);
		sqliteprovider.openToRead();
		Cursor	r4wcursor= sqliteprovider.fetch_contact_from_R4W();
		
		r4wcursor.moveToFirst();
	 	while(r4wcursor.isAfterLast() == false){
	 		
	 		 String numberR4W = r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_CONTACT_NUMBER));
			 String NameR4W = r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_CONTACT_NAME));
			 String ServerName=r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_USERNAME));
			 String ServerStatus=r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_CONTACT_STATUS));
			 String ServerURI=r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_CONTACT_URI));
			 System.out.println("R4wContact Number ="+numberR4W +":"+NameR4W+":"+":"+ServerName+ServerURI+":"+ServerStatus+":"+ServerName);
			list.add(new ExampleContactItem(NameR4W ,numberR4W  ) );
			 r4wcursor.moveToNext();
	 	}
		*/
		
		
		filterList = new ArrayList<ContactItemInterface>();
		contactList = ExampleDataSource.getSampleContactList(this);
		//contactList = list;
		
		
		ExampleContactAdapter adapter = new ExampleContactAdapter(this, R.layout.testexample_contact_item, contactList);
		
		listview = (ExampleContactListView) this.findViewById(R.id.listview);
		listview.setFastScrollEnabled(true);
		listview.setAdapter(adapter);
		
		// use this to process individual clicks
		// cannot use OnClickListener as the touch event is overrided by IndexScroller
		// use last touch X and Y if want to handle click for an individual item within the row
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position,long id) {
				List<ContactItemInterface> searchList = inSearchMode ? filterList : contactList ;
				
				float lastTouchX = listview.getScroller().getLastTouchDownEventX();
				if(lastTouchX < 45 && lastTouchX > -1){
					Toast.makeText(ContactListActivity.this, "User image is clicked ( " + searchList.get(position).getItemForIndex()  + ")", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(ContactListActivity.this, "Nickname: " + searchList.get(position).getItemForIndex() , Toast.LENGTH_SHORT).show();
			}
		});
		
		
		searchBox = (EditText) findViewById(R.id.input_search_query);
		searchBox.addTextChangedListener(this);
    }

    @Override
	public void afterTextChanged(Editable s) {
		searchString = searchBox.getText().toString().trim().toUpperCase();
		System.out.println("afterTextChanged:"+searchString);
	
		if(curSearchTask!=null && curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			try{
				curSearchTask.cancel(true);
			}
			catch(Exception e){
				Log.i(TAG, "Fail to cancel running search task");
			}
			
		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString); // put it in a task so that ui is not freeze
    }
    
    
    @Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
    	// do nothing
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// do nothing
	}
    
	private class SearchListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			filterList.clear();
			String keyword = params[0];
			System.out.println("keyWord:"+keyword);
			
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				// get all the items matching this
				for (ContactItemInterface item : contactList) {
					
					ExampleContactItem contact = (ExampleContactItem)item;
					System.out.println("contact.getFullName():"+contact.getFullName());
					if ((contact.getFullName().toUpperCase().indexOf(keyword) > -1) ) {
						System.out.println("SearchListTask:"+item);
						filterList.add(item);
					}
				}
			} 
			return null;
		}
		
		protected void onPostExecute(String result) {
			
			synchronized(searchLock)
			{
			if(inSearchMode){
					
					ExampleContactAdapter adapter = new ExampleContactAdapter(ContactListActivity.this, R.layout.testexample_contact_item, filterList);
					adapter.setInSearchMode(true);
					listview.setInSearchMode(true);
					listview.setAdapter(adapter);
				}
				else{
					ExampleContactAdapter adapter = new ExampleContactAdapter(ContactListActivity.this, R.layout.testexample_contact_item, contactList);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}
			
		}
	}
	
	

	
}
