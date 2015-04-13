package com.roamprocess1.roaming4world.contactlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.roamprocess1.roaming4world.db.DBContacts;

public class ExampleDataSource {
	
	public static DBContacts sqliteprovider;
	public static List<String> indexSearch;

	public static List<ContactItemInterface> getSampleContactList(
			Context context) {

		List<ContactItemInterface> list = new ArrayList<ContactItemInterface>();
		indexSearch = new ArrayList<String>();

		sqliteprovider = new DBContacts(context);
		sqliteprovider.openToRead();
		Cursor r4wcursor = sqliteprovider.fetch_contact_from_R4W();
		r4wcursor.moveToFirst();
		String index = "#";
		indexSearch.add(index);
		while (r4wcursor.isAfterLast() == false) {

			String numberR4W = r4wcursor.getString(r4wcursor
					.getColumnIndex(DBContacts.R4W_CONTACT_NUMBER));
			String NameR4W = r4wcursor.getString(r4wcursor
					.getColumnIndex(DBContacts.R4W_CONTACT_NAME));
			// String
			// ServerName=r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_USERNAME));
			// String
			// ServerStatus=r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_CONTACT_STATUS));
			// String
			// ServerURI=r4wcursor.getString(r4wcursor.getColumnIndex(DBContacts.R4W_CONTACT_URI));
			// System.out.println("R4wContact Number ="+numberR4W
			// +":"+NameR4W+":"+":"+ServerName+ServerURI+":"+ServerStatus+":"+ServerName);
			list.add(new ExampleContactItem(NameR4W, numberR4W));
			try {

				String indexName = NameR4W.trim().substring(0, 1).toUpperCase();
				System.out.println("ExampleDataSource :index name" + indexName);

				if (indexSearch.contains(indexName)) {
				} else {
					indexSearch.add(indexName);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			r4wcursor.moveToNext();
		}

		r4wcursor.close();
		sqliteprovider.close();
		return list;
	}

}
