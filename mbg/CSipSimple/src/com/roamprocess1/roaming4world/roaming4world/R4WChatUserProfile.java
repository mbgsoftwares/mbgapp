package com.roamprocess1.roaming4world.roaming4world;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.utils.Compatibility;

public class R4WChatUserProfile extends SherlockFragmentActivity {

	TextView userName, userNumber, userStatus;
	ImageView userPic;
	Drawable pic;
	public SharedPreferences prefs;
	String stored_chatuserName, stored_chatuserNumber, stored_user_country_code ;
	DBContacts dbContacts;
	
	String number, fromFull;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r4wchatuserprofile);
		prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
		stored_chatuserName = "com.roamprocess1.roaming4world.stored_chatuserName";
		stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		Initializer();
		fillValue();
		setActionBar();
		stripContact();
		
	}
	
	
	public void stripContact() {
		String value = prefs.getString(stored_chatuserNumber, "NoValue");
		String concat_contact_list = "", modify_contact_no = "", Country_zip_code = prefs.getString(stored_user_country_code, "NoValue");
		if ( !value.startsWith("*")
				&& !value.startsWith("#")) {
				if (value.startsWith("+")) {
				concat_contact_list += value.substring(1) + ",";
				} else if (value.startsWith("00")) {
				modify_contact_no = value.substring(2);
				modify_contact_no = Country_zip_code+ modify_contact_no;
				concat_contact_list += modify_contact_no.toString()+ ",";
				} else if (value.startsWith("0")) {
				modify_contact_no = value.substring(1);
				modify_contact_no = Country_zip_code+ modify_contact_no;
				concat_contact_list += modify_contact_no.toString()+ ",";
				} else if (!value.startsWith("+") && !value.startsWith("0")) {
				concat_contact_list += Country_zip_code+ value.toString() + ",";
				}
				}
		Log.d("value ", concat_contact_list+"  aaa");
		prefs.edit().putString(stored_chatuserNumber, concat_contact_list).commit();
	}

	@SuppressLint("NewApi")
	private void setActionBar() {
		// TODO Auto-generated method stub
		ActionBar ab = getActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
       	ab.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar));
       	ab.setIcon(pic);
       	ab.setTitle(prefs.getString(stored_chatuserName, "User"));
	}

	public void callfinish(View v) {
		
        Bundle b = MessageFragment.getArguments(prefs.getString(stored_chatuserName, "User"), fromFull);
        Intent it = new Intent(this, MessageActivity.class);
        it.putExtras(b);
        startActivity(it);

		
	}
	
	public void callImageActivity(View v) {
		startActivity(new Intent(this, R4WChatUserImage.class));
	}
	

	@SuppressLint("NewApi") private void fillValue() {
		// TODO Auto-generated method stub
		
		if(SetSenderUriInfo.senderImageBitmap == null)
		{
			pic = getResources().getDrawable(R.drawable.ic_contact_picture_180_holo_light);
		}else{
			Bitmap img = SetSenderUriInfo.senderImageBitmap;
           // img = ImageHelperCircular.getRoundedCornerBitmap(img, 400);
            pic = new BitmapDrawable(getResources(),img);
		}
		
		Bitmap bmp = ((BitmapDrawable)pic).getBitmap();
		//Bitmap photo = Bitmap.createBitmap(bmp, 0, 0, 150, 150);
		Bitmap photo = Bitmap.createScaledBitmap(bmp, 150, 150, false);
		
		userPic.setImageBitmap(photo);
		//userPic.setImageBitmap(SetSenderUriInfo.senderImageBitmap);
		userName.setText(prefs.getString(stored_chatuserName, "User"));
		userNumber.setText(prefs.getString(stored_chatuserNumber, "Not Found"));
		
		dbContacts = new DBContacts(this);
		dbContacts.openToRead();
		Cursor cursor = dbContacts.fetch_contact_from_R4W(prefs.getString(stored_chatuserNumber, "Not Found"));
		cursor.moveToFirst();
		
		
		do {
			CharSequence db_status = cursor.getString(3).toString(); 
			userStatus.setText(db_status);
		} while (cursor.moveToNext());
		cursor.close();
		
		dbContacts.close();
		
		
		
		
		
	}

	private void Initializer() {
		// TODO Auto-generated method stub
		userName = (TextView) findViewById(R.id.tv_chatuserName);
		userPic = (ImageView) findViewById(R.id.iv_chatuserPic);
		userNumber = (TextView) findViewById(R.id.tv_chatuserNumber);
		userStatus = (TextView) findViewById(R.id.tv_userStatusShow);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == Compatibility.getHomeMenuId()) {
            finish();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
	
	

}
