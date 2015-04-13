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


package com.roamprocess1.roaming4world.ui.messages;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.db.DBProvider;
import com.roamprocess1.roaming4world.models.CallerInfo;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;
import com.roamprocess1.roaming4world.service.StaticValues;
import com.roamprocess1.roaming4world.utils.ContactsAsyncHelper;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.widgets.contactbadge.QuickContactBadge;

public class ConversationsAdapter extends SimpleCursorAdapter {

	private Context mContext;
	
    public ConversationsAdapter(Context context, Cursor c) {
        super(context, R.layout.conversation_list_item, c, new String[] {
                SipMessage.FIELD_BODY
        },
                new int[] {
                        R.id.subject
                }, 0);
        mContext = context;
    }

    public static final class ConversationListItemViews {
        TextView fromView;
        TextView dateView;
        QuickContactBadge quickContactView;
        int position;
        String to;
        String from;
        String fromFull;
        
        String getRemoteNumber() {
            String number = from;
            if (SipMessage.SELF.equals(number)) {
                number = to;
            }
            return number;
        }
    }
    
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        ConversationListItemViews tagView = new ConversationListItemViews();
        tagView.fromView = (TextView) view.findViewById(R.id.from);
        tagView.dateView = (TextView) view.findViewById(R.id.date);
        tagView.quickContactView = (QuickContactBadge) view.findViewById(R.id.quick_contact_photo);
        view.setTag(tagView);
        //view.setOnClickListener(mPrimaryActionListener);

        return view;
    }

    @SuppressLint("SdCardPath") @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        System.out.println("Conversationlist:bindView");
        final ConversationListItemViews tagView = (ConversationListItemViews) view.getTag();
        String nbr = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM));
        String fromFull = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM_FULL));
        String to_number = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_TO));
        int read = cursor.getInt(cursor.getColumnIndex(SipMessage.FIELD_READ));
        long date = cursor.getLong(cursor.getColumnIndex(SipMessage.FIELD_DATE));
       
        
        
        DBProvider db=new DBProvider();        
        Cursor  cr= db.allMessage(to_number,fromFull,context);
        cr.moveToLast();
        SipMessage msg1 = new SipMessage(cr);
        String lastMessage = msg1.getBodyContent();
        
        if(lastMessage.contains("[Offline message -")&&!nbr.equals("sip:registrar@kamailio.org")){
        	System.out.println("offline message");
        	String[] finalMessage=lastMessage.split("]");
        	lastMessage = finalMessage [ finalMessage.length - 1 ] ;
        }
        
        TextView txtview =(TextView)view.findViewById(R.id.subject);
        
        if(lastMessage.startsWith("R4WIMGTOCONTACTCHATSEND@@")){
        	txtview.setText("Image");
        }else{
        	txtview.setText(lastMessage);
        }
        
        tagView.fromFull = fromFull;
        tagView.to = to_number;
        tagView.from = nbr;
        tagView.position = cursor.getPosition();
        
        /*
        Drawable background = (read == 0)?
                context.getResources().getDrawable(R.drawable.conversation_item_background_unread) :
                context.getResources().getDrawable(R.drawable.conversation_item_background_read);
        
        view.setBackgroundDrawable(background);
         */
        String number = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM_FULL));
        CallerInfo info = CallerInfo.getCallerInfoFromSipUri(mContext, number);
      
        // Photo
        tagView.quickContactView.assignContactUri(info.contactContentUri);
        Log.setLogLevel(6);
        Log.d("Conversation adapter number", number);
        String nu = StaticValues.getStripNumber(number);
        String path = "/sdcard/R4W/ProfilePic/" + nu +".png";
        System.out.println("msg adp out - path="+path);
        File imageFile = new File(path);
		if(imageFile.exists()){
			try {
    			Bitmap bm = BitmapFactory.decodeFile(path);
    			bm = ImageHelperCircular.getRoundedCornerBitmap(bm, bm.getWidth());
    			tagView.quickContactView.getImageView().setImageBitmap(bm);
			} catch (Exception e) {
				// TODO: handle exception
				try {
	    			tagView.quickContactView.getImageView().setImageURI(Uri.parse(path));
					
				} catch (Exception e2) {
	            	ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext,tagView.quickContactView.getImageView(),info,R.drawable.ic_contact_picture_holo_dark);
					// TODO: handle exception
				}
			}
            }else{
       
            	ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext,tagView.quickContactView.getImageView(),info,R.drawable.ic_contact_picture_holo_dark);
            }
        
        // From
        System.out.println("formatMessage(cursor):"+formatMessage(cursor));
        tagView.fromView.setText(formatMessage(cursor));

        //Date
        // Set the date/time field by mixing relative and absolute times.
        int flags = DateUtils.FORMAT_ABBREV_RELATIVE;
        tagView.dateView.setText(DateUtils.getRelativeTimeSpanString(date, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags));
   
    }
    
    private static final StyleSpan STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    
    private CharSequence formatMessage(Cursor cursor) {
        SpannableStringBuilder buf = new SpannableStringBuilder();
        String remoteContactFull = cursor.getString(cursor.getColumnIndex(SipMessage.FIELD_FROM_FULL));
        CallerInfo callerInfo = CallerInfo.getCallerInfoFromSipUri(mContext, remoteContactFull);
      
        System.out.println("remoteContactFull:"+remoteContactFull);
        
        if (callerInfo != null && callerInfo.contactExists) {
        	buf.append(callerInfo.name);
        	} else {
            buf.append(SipUri.getDisplayedSimpleContact(remoteContactFull));
        }
        
        int counter = cursor.getInt(cursor.getColumnIndex("counter"));
        
        if (counter > 1) {
            buf.append(" (" + counter + ") ");
        }
       
        int read = cursor.getInt(cursor.getColumnIndex(SipMessage.FIELD_READ));
        // Unread messages are shown in bold
        if (read == 0) {
            buf.setSpan(STYLE_BOLD, 0, buf.length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        System.out.println("return:"+buf);
        return buf;
    }
}
