package com.roamprocess1.roaming4world.contactlist;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;

import com.roamprocess1.roaming4world.utils.contacts.ContactsUtils5;

public class AllExampleDataSource {
	public static List<String> indexSearch ;
	
	public static List<ContactItemInterface> getSampleContactList(Context context){
		
		List<ContactItemInterface>  list = new  ArrayList<ContactItemInterface> ();
		indexSearch = new ArrayList<String>(); 
		
			ContactsUtils5 contactsUtils5= new ContactsUtils5();
			Cursor cursorAll=contactsUtils5.getContactsPhonesAll(context, null);
			cursorAll.moveToFirst();
			String index="#";
			indexSearch.add(index);
			while(cursorAll.isAfterLast() == false){
			String value = cursorAll.getString(cursorAll.getColumnIndex(Data.DATA1));
		    String displayName = cursorAll.getString(cursorAll.getColumnIndex(Data.DISPLAY_NAME));
		    Long contactId = cursorAll.getLong(cursorAll.getColumnIndex(Data.CONTACT_ID));
		    Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
		 	list.add(new ExampleContactItem(displayName ,value) );
		 	String indexName= displayName.trim().substring(0, 1).toUpperCase();
			
		 	if(indexSearch.contains(indexName)){					
			 }else{
				 indexSearch.add(indexName);
			 }
		 	
			 
		 	cursorAll.moveToNext();
			}
			
			cursorAll.close();
			
		return list;
	}
}
