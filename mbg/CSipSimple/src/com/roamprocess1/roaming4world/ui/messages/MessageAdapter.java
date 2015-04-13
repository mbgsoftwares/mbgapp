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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.widget.ResourceCursorAdapter;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipMessage;
import com.roamprocess1.roaming4world.models.CallerInfo;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;
import com.roamprocess1.roaming4world.utils.ContactsAsyncHelper;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.SmileyParser;
import com.roamprocess1.roaming4world.widgets.contactbadge.QuickContactBadge;
import com.roamprocess1.roaming4world.widgets.contactbadge.QuickContactBadge.ArrowPosition;

public class MessageAdapter extends ResourceCursorAdapter {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    TextAppearanceSpan mTextSmallSpan;
    private CallerInfo personalInfo;
    TextView text_view , tv_msg_info;
    ImageView iv_recievedfile;
    View v;
    String subject , imageUrl , downloadedimageuri;
    Context context;
    String multimedia_msg = "R4WIMGTOCONTACTCHATSEND@@";
    String fileDir = Environment.getExternalStorageDirectory() + "/R4W/SharingImage/";
    MessageActivity messageActivity ;
    Bitmap bm;
    ProgressBar pb_uploading;
    
    static int RECIEVED_IMAGE = 1111;
    static int RECIEVED_AUDIO = 1112;
    static int RECIEVED_VIDEO = 1113;

    public MessageAdapter(Context context, Cursor c) {
        super(context, R.layout.message_list_item, c, 0);
        System.out.println("MessageAdapter.java inconstructor MessageAdapter() ");
        mTextSmallSpan = new TextAppearanceSpan(context, android.R.style.TextAppearance_Small);
        messageActivity = new MessageActivity();
        personalInfo = CallerInfo.getCallerInfoForSelf(mContext);
        System.out.println("MessageAdapter.java inconstructor MessageAdapter() personalInfo is "+personalInfo);
    }


    public static final class MessageListItemViews {
        TextView contentView;
        TextView errorView;
        ImageView deliveredIndicator;
        TextView dateView;
        QuickContactBadge quickContactView;
        public LinearLayout containterBlock;
        ImageView imagefile;
    }
    
    @SuppressLint("SdCardPath") @Override
    public void bindView(View view, Context context, Cursor cursor) {
    	System.out.println("MessageAdapter.java in bindView() ");
        final MessageListItemViews tagView = (MessageListItemViews) view.getTag();
        v = view;
        this.context = context;
        SipMessage msg = new SipMessage(cursor);
       // System.out.println("msg:"+msg);
        
        
        System.out.println("Cursor row count:"+cursor.getCount());
        
        String number = msg.getRemoteNumber();
        long date = msg.getDate();
        String message = msg.getBodyContent();
        subject=message; 
        System.out.println("subject--" + subject);
        if(message.contains("[Offline message -")&&!number.equals("sip:registrar@kamailio.org")){
        	System.out.println("offline message");
        	String[] finalMessage=message.split("]");
            subject = finalMessage [ finalMessage.length - 1 ];
        }else{
        	subject=message;
        }
       
        String errorTxt = msg.getErrorContent();
        String mimeType = msg.getMimeType();
        int type = msg.getType();

        
        Log.setLogLevel(6);
		Log.d("Col count",cursor.getColumnCount() + " @");
				
        String[] columnname = cursor.getColumnNames();
        for(int i = 0 ; i < columnname.length ; i++){
            Log.d("columnname " + i, columnname[i] + " @");
            Log.d(columnname[i] , cursor.getString(cursor.getColumnIndex(columnname[i])) + " @");
            
        }
        
        
        
        
        tv_msg_info = (TextView) view.findViewById(R.id.tv_msg_info);
        pb_uploading = (ProgressBar) view.findViewById(R.id.pb_uploading);
        
        String timestamp = "";
        if (System.currentTimeMillis() - date > 1000 * 60 * 60 * 24) {
            // If it was recieved one day ago or more display relative
            // timestamp - SMS like behavior
            int flags = DateUtils.FORMAT_ABBREV_RELATIVE;
            timestamp = (String) DateUtils.getRelativeTimeSpanString(date,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, flags);
        } else {
            // If it has been recieved recently show time of reception - IM
            // like behavior
            timestamp = dateFormatter.format(new Date(date));
        }
        
       
        tagView.dateView.setText(timestamp);
        

        // Delivery state
        if ( type == SipMessage.MESSAGE_TYPE_QUEUED ) {
            tagView.deliveredIndicator.setVisibility(View.VISIBLE);
            tagView.deliveredIndicator.setImageResource(R.drawable.error_watch_not_send);
            tagView.deliveredIndicator.setContentDescription("");
        } else if (type == SipMessage.MESSAGE_TYPE_FAILED ) {
            tagView.deliveredIndicator.setVisibility(View.VISIBLE);
            tagView.deliveredIndicator.setImageResource(R.drawable.error_watch_not_send);
            tagView.deliveredIndicator.setContentDescription("");
        } else {
            tagView.deliveredIndicator.setVisibility(View.VISIBLE);
            tagView.deliveredIndicator.setImageResource(R.drawable.todo_send);
            tagView.deliveredIndicator.setContentDescription("");
        }

        if (TextUtils.isEmpty(errorTxt)) {
            tagView.errorView.setVisibility(View.GONE);
        } else {
            tagView.errorView.setVisibility(View.GONE);
            tagView.errorView.setText(errorTxt);
        }

        // Subject
       tagView.contentView.setText(formatMessage(number, subject, mimeType));
       if(msg.isOutgoing()) {
        	setPhotoSide(tagView, ArrowPosition.LEFT);
        	  LinearLayout linerLayout=	(LinearLayout) view.findViewById(R.id.message_block);
        	  linerLayout.setBackgroundResource(R.drawable.chatedittextdesign);
        	  text_view=(TextView) view.findViewById(R.id.text_view);
              text_view.setTextColor(Color.BLACK);
              
            // Photo
            tagView.quickContactView.assignContactUri(personalInfo.contactContentUri);
            /*
            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                    tagView.quickContactView.getImageView(),
                    personalInfo,
                    R.drawable.ic_contact_picture_holo_dark);
          	*/
            
            System.out.println("msg adp out - 0");
            String path = "/sdcard/R4W/ProfilePic/ProfilePic.png";
            File user_imageFile = new File(path);
    		if(user_imageFile.exists()){
    			try {
        			if(bm != null){
        		//		bm = null;
        			}
    				bm = BitmapFactory.decodeFile(path);
        			bm = ImageHelperCircular.getRoundedCornerBitmap(bm, bm.getWidth());
        			tagView.quickContactView.getImageView().setImageBitmap(bm);
				} catch (Exception e) {
					// TODO: handle exception
					try {
		    			tagView.quickContactView.getImageView().setImageURI(Uri.parse(path));
						
					} catch (Exception e2) {
			            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
	                            tagView.quickContactView.getImageView(),
	                            personalInfo,
	                            R.drawable.ic_contact_picture_holo_dark);
	        					// TODO: handle exception
					}
				}
                }else{
                    ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                            tagView.quickContactView.getImageView(),
                            personalInfo,
                            R.drawable.ic_contact_picture_holo_dark);
                	
                }
            
            outgoingImage(number);
            
        }else {
        	
            setPhotoSide(tagView, ArrowPosition.RIGHT);
            LinearLayout linerLayout=	(LinearLayout) view.findViewById(R.id.message_block);
            linerLayout.setBackgroundResource(R.drawable.messagebodyleft);
            
           // Contact
            CallerInfo info = CallerInfo.getCallerInfoFromSipUri(mContext, msg.getFullFrom());
    
            text_view=(TextView) view.findViewById(R.id.text_view);
            text_view.setTextColor(Color.BLACK);
        	pb_uploading.setVisibility(ProgressBar.GONE);
            
            // Photo
            tagView.quickContactView.assignContactUri(info.contactContentUri);
            /*
            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                    tagView.quickContactView.getImageView(),
                    info,
                    R.drawable.ic_contact_picture_holo_dark);
            	*/
            System.out.println("msg adp in - 1");
            String nu = stripNumber(number);
            String path = "/sdcard/R4W/ProfilePic/" + nu +".png";
            System.out.println("msg adp out - path="+path);
            File user_imageFile = new File(path);
    		if(user_imageFile.exists()){
    			try {
    				if(bm != null){
        		//		bm = null;
        			}
        			bm = BitmapFactory.decodeFile(path);
        			bm = ImageHelperCircular.getRoundedCornerBitmap(bm, bm.getWidth());
        			tagView.quickContactView.getImageView().setImageBitmap(bm);
				} catch (Exception e) {
					// TODO: handle exception
					try {
		    			tagView.quickContactView.getImageView().setImageURI(Uri.parse(path));
						
					} catch (Exception e2) {
						ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
	                            tagView.quickContactView.getImageView(),
	                            info,
	                            R.drawable.ic_contact_picture_holo_dark);				// TODO: handle exception
					}
		}
                }else{
                    ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(mContext, 
                            tagView.quickContactView.getImageView(),
                            info,
                            R.drawable.ic_contact_picture_holo_dark);
                }
            	incomingImage();
        }

    }
    
    private void outgoingImage(String numb) {
    	Log.setLogLevel(6);
    	Log.d("outgoingImage", "call");
        iv_recievedfile = (ImageView) v.findViewById(R.id.iv_recievedfile);

    	if(isimagemsginit(subject)){
        	pb_uploading.setVisibility(ProgressBar.VISIBLE);
    	}else{
        	pb_uploading.setVisibility(ProgressBar.GONE);
      }

       if(isvideoMsg(subject)){
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_video_icon);
        	iv_recievedfile.setImageBitmap(b);
        }else if(isaudioMsg(subject)){
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_audio_icon);
        	iv_recievedfile.setImageBitmap(b);
        }else if(islocationMsg(subject)){
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_map_icon);
        	iv_recievedfile.setImageBitmap(b);
        }else if(iscontactMsg(subject)){
        	tv_msg_info.setText("1");
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_contact_icon);
        	iv_recievedfile.setImageBitmap(b);
       }else if(isimageMsg(subject)){
        	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Log.d("subject outgoingImage", subject);
        	
        	if(subject.contains("@@")){
        		String[] fileuriarr = subject.split("@@");
        		String fileuri = fileuriarr[1];
        		String[] namm = fileuri.split("-");
        		String path = Environment.getExternalStorageDirectory()+ "/R4W/SharingImage/" + stripNumber(numb) + "/send/"+ namm[2];
        		Log.d("fileuri1", fileuri);
        		Log.d("path1", path);
        		File user_imageFile = new File(path);
        		if(user_imageFile.exists()){
        			Bitmap b = BitmapFactory.decodeFile(path);
        			
        			int[] size = messageActivity.getBitmapSize(b);
        			b = Bitmap.createScaledBitmap(b, size[0], size[1], false);
        			Log.d("imageFile.exists", "true");
            		Log.d("bitmap", b.getWidth() + " @");
            		iv_recievedfile.setImageBitmap(b);
    				/*
        			iv_recievedfile.setImageURI(Uri.parse(imageFile.getAbsolutePath()));
        			Log.d("imageFile.getAbsolutePath()", imageFile.getAbsolutePath());
        			*/
        		}
        	}
        	
        }else{
          	text_view.setVisibility(TextView.VISIBLE);
        	iv_recievedfile.setVisibility(ImageView.GONE);
        	pb_uploading.setVisibility(ProgressBar.GONE);
        }
	}
    
    private boolean isimagemsginit(String subject2) {
		// TODO Auto-generated method stub
     	
    	boolean flag = false;
    	
    	Log.setLogLevel(6);
    	
    	if(subject2.contains("@@")){
    		String[] fileuriarr = subject2.split("@@");
    		String fileuri = fileuriarr[1];
    		String[] namm = fileuri.split("-");
    	 	if(namm.length > 3)
    			flag = true;
    		
    	}
    	
    	
    	Log.d("isimagemsginit", subject2 );
    	Log.d("flag", flag + " #");
		return flag;
	}

	protected String getNumber(String nu) {
		// TODO Auto-generated method stub
		
        	if(nu.contains("@@")){
	    		String[] arr = nu.split("@@");
	    		nu = arr[1];
	    		if(nu.contains("-")){
	    			arr = nu.split("-");
	    			nu = arr[1];
	    		}
	    	}
	    	return nu;
	}

	protected String getTimestamp(String nu) {
		// TODO Auto-generated method stub
    	if(nu.contains("@@")){
    		String[] arr = nu.split("@@");
    		nu = arr[1];
    		if(nu.contains("-")){
    			arr = nu.split("-");
    			nu = arr[2];
    		}
    	}
    	return nu;
	}
    
    private boolean iscontactMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multimedia_msg + "CON"))
    		return true;
    	else
    		return false;
	}
    
    private boolean isimageMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multimedia_msg + "IMG"))
    		return true;
    	else
    		return false;
	}
    
    private boolean islocationMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multimedia_msg + "LOC"))
    		return true;
    	else
    		return false;
	}
    
    private boolean isvideoMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multimedia_msg + "VID"))
    		return true;
    	else
    		return false;
	}
    
    private boolean isaudioMsg(String msg) {
		// TODO Auto-generated method stub
    	if(msg.startsWith(multimedia_msg + "AUD"))
    		return true;
    	else
    		return false;
	}


    
    public String stripNumber(String nu){
    	if(nu.contains("@")){
    		String[] arr = nu.split("@");
    		nu = arr[0];
    		if(nu.contains(":")){
    			arr = nu.split(":");
    			nu = arr[1];
    		}
    	}
    	return nu;
    }

    
    private void incomingImage() {
    	Log.setLogLevel(6);
    	Log.d("incomingImage", "call");
    	iv_recievedfile = (ImageView) v.findViewById(R.id.iv_recievedfile);
    	pb_uploading.setVisibility(ProgressBar.GONE);
    	
    	if(isvideoMsg(subject)){
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	downloadFile(subject , RECIEVED_VIDEO);
        }else if(isaudioMsg(subject)){
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	downloadFile(subject , RECIEVED_AUDIO);
        }else if(islocationMsg(subject)){
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_map_icon);
        	iv_recievedfile.setImageBitmap(b);
        }else if(iscontactMsg(subject)){
        	tv_msg_info.setText("0");
          	text_view.setVisibility(TextView.GONE);
        	iv_recievedfile.setVisibility(ImageView.VISIBLE);
        	Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.google_contact_icon);
        	iv_recievedfile.setImageBitmap(b);
        }else if(isimageMsg(subject)){
        	downloadFile(subject , RECIEVED_IMAGE);
       		text_view.setVisibility(TextView.GONE);
       		iv_recievedfile.setVisibility(ImageView.VISIBLE);
            		
        }else{
          	text_view.setVisibility(TextView.VISIBLE);
        	iv_recievedfile.setVisibility(ImageView.GONE);
        	
        }
	}
    
		public void downloadFile(String subject3, int flag) {
			String imageName, imageDirNumber;
	
			imageDirNumber = getNumber(subject3);
			imageName = getTimestamp(subject3);
			if (checkFile(imageDirNumber, imageName) == true) {
				if (flag == RECIEVED_IMAGE) {
					String p = fileDir + imageDirNumber + "/Recieved/" + imageName;
					Log.d("pp", p);
					try {
						Bitmap b = BitmapFactory.decodeFile(p);
						int[] size = messageActivity.getBitmapSize(b);
						b = Bitmap.createScaledBitmap(b, size[0], size[1], false);
						iv_recievedfile.setImageBitmap(b);
						// iv_recievedfile.setImageURI(Uri.parse(p));
					} catch (Exception e) {
						// TODO: handle exception
						iv_recievedfile.setImageResource(R.drawable.download_icon);
						setProgressBar(subject3);
					}
				} else if (flag == RECIEVED_VIDEO) {
					try {
						Bitmap b = BitmapFactory.decodeResource(
								context.getResources(),
								R.drawable.google_video_icon);
						iv_recievedfile.setImageBitmap(b);
					} catch (Exception e) {
						// TODO: handle exception
						iv_recievedfile.setImageResource(R.drawable.download_icon);
						setProgressBar(subject3);
					}
				} else if (flag == RECIEVED_AUDIO) {
					try {
						Bitmap b = BitmapFactory.decodeResource(
								context.getResources(),
								R.drawable.google_audio_icon);
						iv_recievedfile.setImageBitmap(b);
					} catch (Exception e) {
						// TODO: handle exception
						iv_recievedfile.setImageResource(R.drawable.download_icon);
						setProgressBar(subject3);
					}
				}
			} else {
				setProgressBar(subject3);
			}
	
		}
    
    private void setProgressBar(String subject3){
		new AsyncTaskDownloadImage(subject3, false).execute();
		pb_uploading.setVisibility(ProgressBar.VISIBLE);
		ContentValues contentValues = new ContentValues();
		contentValues.put(SipMessage.FIELD_DATA_DOWNLOADED, SipMessage.MESSAGE_TYPE_DOWNLOAD_START);
		
		int i = context.getContentResolver().update(SipMessage.MESSAGE_URI, contentValues, "body=?", new String[]{subject3});

		Log.setLogLevel(6);
		Log.d("affected rows", i + " @");

    }
    
    private boolean checkFile(String num , String timest){
    	String p = fileDir + num + "/Recieved/"+ timest;
    	File user_imageFile = new File(p);
		if(user_imageFile.exists()){
			return true;
		}else{
			return false;
		}
    }
    
    public void setImageOnRow(String msgSubject) {
    	String imageName, imageDirNumber; 
        imageDirNumber = getNumber(msgSubject);
 		imageName = getTimestamp(msgSubject);
    	if(checkFile(imageDirNumber, imageName) == true){
				Bitmap b = BitmapFactory.decodeFile(fileDir + imageDirNumber + "/Recieved/"+imageName);
				iv_recievedfile.setImageBitmap(b);
			}else{
				iv_recievedfile.setImageResource(R.drawable.logo);
				Log.d("error in download", "true");
			}
    	try {
    		if(pb_uploading != null){
				pb_uploading.setVisibility(ProgressBar.GONE);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
    	
   }
    
    private void setPhotoSide(MessageListItemViews tagView, ArrowPosition pos) {
    	System.out.println("MessageAdapter.java in setPhotoSide() ");
        LayoutParams lp = (RelativeLayout.LayoutParams) tagView.quickContactView.getLayoutParams();
        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.ALIGN_PARENT_RIGHT
                : RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.ALIGN_PARENT_LEFT
                : RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        
        lp = (RelativeLayout.LayoutParams) tagView.containterBlock.getLayoutParams();
        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF,
                R.id.quick_contact_photo);
        lp.addRule((pos == ArrowPosition.LEFT) ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF,
                0);
        tagView.quickContactView.setPosition(pos);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
    	System.out.println("MessageAdapter.java in newView() ");
        View view = super.newView(context, cursor, parent);

        MessageListItemViews tagView = new MessageListItemViews();
        tagView.containterBlock = (LinearLayout) view.findViewById(R.id.message_block);
        tagView.imagefile = (ImageView) view.findViewById(R.id.iv_recievedfile);
        tagView.contentView = (TextView) view.findViewById(R.id.text_view);
        tagView.errorView = (TextView) view.findViewById(R.id.error_view);
        tagView.dateView = (TextView) view.findViewById(R.id.date_view);
        tagView.quickContactView = (QuickContactBadge) view.findViewById(R.id.quick_contact_photo);
        tagView.deliveredIndicator = (ImageView) view.findViewById(R.id.delivered_indicator);

        view.setTag(tagView);

        return view;
    }


    private CharSequence formatMessage(String contact, String body,
            String contentType) {
    	System.out.println("MessageAdapter.java in formatMessage() ");
        SpannableStringBuilder buf = new SpannableStringBuilder();
        if (!TextUtils.isEmpty(body)) {
            // Converts html to spannable if ContentType is "text/html".
            if (contentType != null && "text/html".equals(contentType)) {
                buf.append("\n");
                buf.append(Html.fromHtml(body));
            } else {
                SmileyParser parser = SmileyParser.getInstance();
                buf.append(parser.addSmileySpans(body));
            }
        }

        // We always show two lines because the optional icon bottoms are
        // aligned with the
        // bottom of the text field, assuming there are two lines for the
        // message and the sent time.
        buf.append("\n");
        int startOffset = buf.length();
        startOffset = buf.length();
        buf.setSpan(mTextSmallSpan, startOffset, buf.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        System.out.println("formatMessage:"+buf);
        return buf;
    }
    
    
    	class AsyncTaskDownloadImage extends AsyncTask<Void, Void, Boolean> {
		
    		String msgSubject = "";
    		boolean resume ; 
    		public AsyncTaskDownloadImage(String msgData , boolean flag){
    			msgSubject = msgData;
    			resume = flag;
    		}
		
		
		
		@SuppressWarnings("static-access")
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}
	   
	
	

	protected void onPostExecute(Boolean result) {
		if(result == true)
			{
				setImageOnRow(msgSubject);
				
				ContentValues contentValues = new ContentValues();
				contentValues.put(SipMessage.FIELD_DATA_DOWNLOADED, SipMessage.MESSAGE_TYPE_DOWNLOAD_FINISH);
				
				int i = context.getContentResolver().update(SipMessage.MESSAGE_URI, contentValues, "body=?", new String[]{msgSubject});

			}
			
		else{
			
		}
			
		
		
	}




	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		
	//	userPic = getBitmapFromURL("https://www.roaming4world.com/images/logo.png");
		Log.d("doInBackgroud", "doInBackground");
		
		boolean flagurl = webServiceImageUrl(msgSubject);
		
		
		
		if(flagurl == true){
				boolean fl =  getImages(msgSubject);
				if(fl == true){
					return webServiceAcknowledge(msgSubject);
				}else{
					return fl;
				}
				
		}else{
			return false;
		}
	}

   
	}
    
    	public boolean webServiceImageUrl(String msgSubject) {
    		try {
    			Log.d("webServiceImageUrl", "called");
    			HttpParams p = new BasicHttpParams();
    			p.setParameter("user", "1");
    			HttpClient httpclient = new DefaultHttpClient(p);
    			String url = "http://ip.roaming4world.com/esstel/file-transfer/file_download.php?msg=" + msgSubject;
    			
    			Log.d("url", url + " #");
    			HttpGet httpget = new HttpGet(url);
    			ResponseHandler<String> responseHandler;
    			String responseBody;
    			responseHandler = new BasicResponseHandler();
    			responseBody = httpclient.execute(httpget, responseHandler);
    			JSONObject json = new JSONObject(responseBody);
    			imageUrl = json.getString("response");
    			if(imageUrl.equals("Error")){
    				return false;
    			}else{
    				return true;
    			}
    		} catch (Throwable t) {

    			t.printStackTrace();

    			return false;
    		}
    	}

    	public boolean webServiceAcknowledge(String msgSubject) {
    		try {
    			Log.d("webServiceImageUrl", "called");
    			HttpParams p = new BasicHttpParams();
    			p.setParameter("user", "1");
    			HttpClient httpclient = new DefaultHttpClient(p);
    			String url = "http://ip.roaming4world.com/esstel/file-transfer/file_download_ack.php?msg=" + msgSubject;
    			
    			Log.d("url", url + " #");
    			HttpGet httpget = new HttpGet(url);
    			ResponseHandler<String> responseHandler;
    			String responseBody;
    			responseHandler = new BasicResponseHandler();
    			responseBody = httpclient.execute(httpget, responseHandler);
    			JSONObject json = new JSONObject(responseBody);
    			imageUrl = json.getString("response");
    			if(imageUrl.equals("Error")){
    				return false;
    			}else{
    				return true;
    			}
    		} catch (Throwable t) {

    			t.printStackTrace();

    			return false;
    		}
    	}

    	
    	@SuppressLint("SdCardPath") 
    	public boolean getImages(String msgSubject)
 	   {
    		File file = null;
    		
      		try
   		   {   
      		  
      		 String filename= "", num = "";
      		   
      		 URL url = new URL(imageUrl);
   		     HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
   		     urlConnection.setRequestMethod("GET");
   		     urlConnection.setDoOutput(true);                   
   		     urlConnection.connect();                  
   		     
   		     File imageDirectory = new File(Environment.getExternalStorageDirectory() , "R4W");
   		     
   		     if(!imageDirectory.exists())
   		     {
   		    	 imageDirectory.mkdir();
   		     }
   		     
	   		 File imageDirectoryprofil = new File(imageDirectory.getAbsolutePath() , "SharingImage");
	 	     
	 	     if(!imageDirectoryprofil.exists())
	 	     {
	 	    	 imageDirectoryprofil.mkdir();
	 	     }
 	     
   		     
   		     File imageDirectoryprofile = new File( imageDirectoryprofil.getAbsolutePath(), getNumber(msgSubject));
   		     
   		     if(!imageDirectoryprofile.exists())
   		     {
   		    	 imageDirectoryprofile.mkdir();
   		     }
   		    
	   		  File imageDirectorypro= new File( imageDirectoryprofile.getAbsolutePath() ,"Recieved");
			     
			     if(!imageDirectorypro.exists())
			     {
			    	 imageDirectorypro.mkdir();
			     }
		     
   		     
   		     num = getNumber(msgSubject);
   		     filename = getTimestamp(msgSubject);

   		     file = new File(imageDirectorypro.getAbsolutePath(),filename);
   		     if(!file.exists())
   		     {
   		       file.createNewFile();
   		     }   
   		     
   		     FileOutputStream fileOutput = new FileOutputStream(file);
   		     InputStream inputStream = urlConnection.getInputStream();
   		     int totalSize = urlConnection.getContentLength();
   		     int downloadedSize = 0;   
   		     byte[] buffer = new byte[1024];
   		     int bufferLength = 0;
   		     while ( (bufferLength = inputStream.read(buffer)) > 0 ) 
   		     {                 
   		       fileOutput.write(buffer, 0, bufferLength);                  
   		       downloadedSize += bufferLength;                 
   		       Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
   		     }             
   		     fileOutput.close();
   		     Log.d("downloadedSize", downloadedSize+" @");
   		     if(downloadedSize==totalSize) 
   		    	 {
   		    	 	downloadedimageuri = file.getAbsolutePath();
    		    	return true;
   		    	 }    
   		   } 
      		catch (Exception e) 
 		   {
 			     e.printStackTrace();
 			     if(file.exists()){
 			    	 file.delete();
 			     }
 			     return false;
 		   } 
   		   return false;
   	   
 	   }
    	
    	public boolean resumeImages(String msgSubject)
 	   {
    		File file = null;
    		long fileLength;
    		
      		try
   		   {   
      		  
      		 String filename= "", num = "";
      		   
      		 URL url = new URL(imageUrl);
   		     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
   		     connection.setRequestMethod("GET");
   		     connection.setDoOutput(true);                   
   		     connection.connect();                  
   		  
   		     num = getNumber(msgSubject);
   		     filename = getTimestamp(msgSubject);

   		     file = new File(fileDir + "/" + getNumber(msgSubject) + "/Recieved",filename);
   		     if(!file.exists())
   		     {
   		       file.createNewFile();
   		     }   
   		     
   		     FileOutputStream fileOutput = new FileOutputStream(file);
   		     InputStream inputStream = connection.getInputStream();
   		     int totalSize = connection.getContentLength();
   		     Long downloadedSize = (long) 0;   
   		     byte[] buffer = new byte[1024];
   		     int bufferLength = 0;
   		     while ( (bufferLength = inputStream.read(buffer)) > 0 ) 
   		     {                 
   		       fileOutput.write(buffer, 0, bufferLength);                  
   		       downloadedSize += bufferLength;                 
   		       Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
   		     }             
   		     fileOutput.close();
   		     Log.d("downloadedSize", downloadedSize+" @");
   		     if(downloadedSize==totalSize) 
   		    	 {
   		    	 	downloadedimageuri = file.getAbsolutePath();
    		    	return true;
   		    	 } 
   		     
   		     
   		     
   		  if (file.exists())
          {
              connection.setAllowUserInteraction(true);
              connection.setRequestProperty("Range", "bytes=" + file.length() + "-");
          }

          connection.setConnectTimeout(14000);
          connection.setReadTimeout(20000);
          connection.connect();

          if (connection.getResponseCode() / 100 != 2)
              throw new Exception("Invalid response code!");
          else
          {
              String connectionField = connection.getHeaderField("content-range");

              if (connectionField != null)
              {
                  String[] connectionRanges = connectionField.substring("bytes=".length()).split("-");
                  downloadedSize = Long.valueOf(connectionRanges[0]);
              }

              if (connectionField == null && file.exists())
                  file.delete();

              fileLength = connection.getContentLength() + downloadedSize;
              BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
              RandomAccessFile output = new RandomAccessFile(file, "rw");
              output.seek(downloadedSize);

              byte data[] = new byte[1024];
              int count = 0;
              int __progress = 0;

              while ((count = input.read(data, 0, 1024)) != -1 
                      && __progress != 100) 
              {
                  downloadedSize += count;
                  output.write(data, 0, count);
                  __progress = (int) ((downloadedSize * 100) / fileLength);
              }

              output.close();
              input.close();
         
          	}
   		     
   		   } 
      		catch (Exception e) 
 		   {
 			     e.printStackTrace();
 				 return false;
 		   } 
   		   return false;
   	   
 	   }
    	

}
