package com.roamprocess1.roaming4world.syncadapter;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.db.DBAdapter.DatabaseHelper;
import com.roamprocess1.roaming4world.db.DBContacts;

public class ContactProvider extends ContentProvider {

    private static final int CONTACTS = 1;
	private DatabaseHelper mOpenHelper;
	
	  /**
     * Project used when querying content provider. Returns all known fields.
     */
    public static final String[] PROJECTION = new String[] {
            DBContacts.CONTACT_CONTACT_NUMBER,
            DBContacts.CONTACT_CONTACT_NAME,
            DBContacts.CONTACT_CONTACT_URI,
            DBContacts.CONTACT_CONTACT_STATUS,
            DBContacts.CONTACT_USERNAME,
            };

    /**
     * A UriMatcher instance
     */
    private static final UriMatcher URI_MATCHER;
    static {
    	 // Create and initialize URI matcher.
    	URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        
        URI_MATCHER.addURI(SipManager.AUTHORITY, DBContacts.CONTACT_SYNC_TABLE, CONTACTS);
    }


	 
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		mOpenHelper = new DatabaseHelper(getContext());

		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		String finalSortOrder = DBContacts.CONTACT_UNIQ_ID + " ASC";
        String[] finalSelectionArgs = selectionArgs;
        String finalGrouping = null;
        String finalHaving = null;
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        qb.setTables(DBContacts.CONTACT_SYNC_TABLE);
        Cursor c = qb.query(db, PROJECTION, selection, finalSelectionArgs,
                finalGrouping, finalHaving, finalSortOrder);

        return c;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.insert(DBContacts.CONTACT_SYNC_TABLE, null, values);
	    return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
       
        if(URI_MATCHER.match(uri) == CONTACTS)
        	return  db.delete(DBContacts.CONTACT_SYNC_TABLE, selection, selectionArgs);
        
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
	       
        if(URI_MATCHER.match(uri) == CONTACTS)
	            return db.update(DBContacts.CONTACT_SYNC_TABLE, values, selection, selectionArgs);

		
		return 0;
	}

}
