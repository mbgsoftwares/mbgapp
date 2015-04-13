package com.roamprocess1.roaming4world.contactlist;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;

public class ExampleContactAdapter extends ContactListAdapter{

	Context context;
	public SharedPreferences prefs;
	private String stored_user_country_code, stored_flagimage, stored_supportnumber, supportnum;

	private Typeface robotoMedium,robotoRegular;
	
	public ExampleContactAdapter(Context _context, int _resource,
			List<ContactItemInterface> _items) {
		super(_context, _resource, _items);
		context = _context;
		robotoMedium = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Medium.ttf");
		robotoRegular=Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Regular.ttf");
	}
	
	// override this for custom drawing
	@SuppressLint("SdCardPath") 
	public void populateDataForRow(View parentView, ContactItemInterface item , int position){
		// default just draw the item only
		View infoView = parentView.findViewById(R.id.infoRowContainer);
		
		prefs = context.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);	
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		stored_flagimage = "com.roamprocess1.roaming4world.flagimage";
		stored_supportnumber = "com.roamprocess1.roaming4world.support_no";
		
		TextView fullNameView = (TextView)infoView.findViewById(R.id.txtr4wNumber);
		TextView nicknameView = (TextView)infoView.findViewById(R.id.txtr4wName);
		TextView txtr4wLastName=(TextView)infoView.findViewById(R.id.txtr4wLastName);
		ImageView userImg=(ImageView)infoView.findViewById(R.id.userImg);
		

		nicknameView.setTypeface(robotoRegular);
		txtr4wLastName.setTypeface(robotoMedium);
		
		//nicknameView.setText(item.getItemForIndex());
		System.out.println("Full Name: "+item.getItemForIndex());
		if(item.getItemForIndex()!=null){
	
			if(item.getItemForIndex().matches(".*\\s+.*")){
				//System.out.println("populateDataForRow  inside if");
				//System.out.println("populateDataForRow :"+item.getItemForIndex());
				String[] fullName = item.getItemForIndex().split("\\s");
				//System.out.println("populateDataForRow :"+fullName[1]);
				nicknameView.setText(fullName[0]);
				
				//System.out.println("fullName length :"+fullName.length);
				String lastName="";
				if(fullName.length==3){
					lastName=fullName[1]+" "+fullName[2]; 
				}else if(fullName.length==2){
					lastName=fullName[1]; 
				}
				
				txtr4wLastName.setText(lastName);	
				//fullName=null;
			}else{
				//System.out.println("populateDataForRow  inside else");
				nicknameView.setText(item.getItemForIndex());
				txtr4wLastName.setText("");
			}
		
		}	
		
			
		
		if(item instanceof ExampleContactItem){
			ExampleContactItem contactItem = (ExampleContactItem)item;
		
		
			Drawable pic;
			
			String cc = prefs.getString(stored_user_country_code, "");
			boolean flag = prefs.getBoolean(stored_flagimage, true);
			String num = contactItem.getFullName();
			
			if(!cc.equals("") && flag == false){
				if(num.startsWith("00")){
					
				}else if(num.startsWith("+")){
					num = num.substring(1);
				}else if(num.startsWith("0")){
					num = cc + num.substring(1);
				}else{
					num = cc + num;
				}
				
			}
		
			supportnum = prefs.getString(stored_supportnumber, "");
			fullNameView.setText("" + contactItem.getFullName());	
			fullNameView.setVisibility(View.GONE);
			Bitmap bmp=null,img=null;
			
			String fileuri = "/sdcard/R4W/ProfilePic/" + num + ".png";
			File imageDirectoryprofile = new File(fileuri);
			if (imageDirectoryprofile.exists()) {
				 img = BitmapFactory.decodeFile(fileuri);
				pic = new BitmapDrawable(getContext().getResources(),img);
			
			}else if (supportnum.equals(num)) {
				pic = getContext().getResources().getDrawable(R.drawable.roaminglogo);
			}else{
				pic = getContext().getResources().getDrawable(R.drawable.ic_contact_picture_holo_dark);
			}
			 bmp = ((BitmapDrawable)pic).getBitmap();
			 
			try{
				bmp = ImageHelperCircular.getRoundedCornerBitmap(bmp, bmp.getWidth());
			}catch(Exception e){
				pic = getContext().getResources().getDrawable(R.drawable.ic_contact_picture_holo_dark);
				bmp = ((BitmapDrawable)pic).getBitmap();
				e.printStackTrace();
			}finally{
				userImg.setImageBitmap(bmp);
			}
			
			
			
		}
		
	}

}
