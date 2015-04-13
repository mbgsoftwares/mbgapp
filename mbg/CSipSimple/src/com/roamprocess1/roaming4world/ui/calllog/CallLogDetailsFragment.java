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

import java.io.File;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipManager;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.api.SipUri;
import com.roamprocess1.roaming4world.models.CallerInfo;
import com.roamprocess1.roaming4world.roaming4world.ImageHelperCircular;
import com.roamprocess1.roaming4world.roaming4world.R4WChatUserImage;
import com.roamprocess1.roaming4world.ui.R4wFriendsProfile;
import com.roamprocess1.roaming4world.ui.RContactlist;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.utils.ContactsAsyncHelper;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.contacts.ContactsWrapper;
import com.roamprocess1.roaming4world.widgets.AccountChooserButton;

/**
 * Displays the details of a specific call log entry.
 * <p>
 * This activity can be either started with the URI of a single call log entry,
 * or with the {@link #EXTRA_CALL_LOG_IDS} extra to specify a group of call log
 * entries.
 */
public class CallLogDetailsFragment extends SherlockFragment implements View.OnClickListener{

    private static final String THIS_FILE = "CallLogDetailsFragment";
    /** A long array extra containing ids of call log entries to display. */
    public static final String EXTRA_CALL_LOG_IDS = "EXTRA_CALL_LOG_IDS";

    private PhoneCallDetailsHelper mPhoneCallDetailsHelper;
    private TextView mHeaderTextView;
    private View mHeaderOverlayView;
    private ImageView mMainActionView, mContactBackgroundView;
    private ImageButton mMainActionPushLayerView;
    private AccountChooserButton mAccountChooserButton;
    private String[] SaveNo;
    private Button btnAddToContact,btnMesssage,btnCall,btnInvite;
    String cantactnumber,finalNumber;
    private SharedPreferences prefs;
	private String stored_lastdialnumber;
	String usernumber , stored_chatuserNumber;
	PhoneCallDetails firstDetails;
	
	
    /* package */Resources mResources;
    private LayoutInflater mInflater;

    public interface OnQuitListener {
        public void onQuit();

        public void onShowCallLog(long[] callIds);
    }

    private OnQuitListener quitListener;

    public void setOnQuitListener(OnQuitListener l) {
        quitListener = l;
    }

    private static final String[] CALL_LOG_PROJECTION = new String[] {
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            SipManager.CALLLOG_PROFILE_ID_FIELD,
            SipManager.CALLLOG_STATUS_CODE_FIELD,
            SipManager.CALLLOG_STATUS_TEXT_FIELD
    };

    private static final int DATE_COLUMN_INDEX = 0;
    private static final int DURATION_COLUMN_INDEX = 1;
    private static final int NUMBER_COLUMN_INDEX = 2;
    private static final int CALL_TYPE_COLUMN_INDEX = 3;
    private static final int PROFILE_ID_COLUMN_INDEX = 4;
    private static final int STATUS_CODE_COLUMN_INDEX = 5;
    private static final int STATUS_TEXT_COLUMN_INDEX = 6;


    /**
     * Action when the call icon is pressed
     */
    private final View.OnClickListener mPrimaryActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        	
        	System.out.println("CallLogDetailsFragment");
        	
            String nbr = (String) view.getTag();
        	
            System.out.println("CallLog=" + nbr);
            prefs.edit().putString(stored_lastdialnumber, nbr).commit();
            stored_chatuserNumber = "com.roamprocess1.roaming4world.stored_chatuserNumber";
            
            if (!TextUtils.isEmpty(nbr)) {
            	
                SipProfile acc = mAccountChooserButton.getSelectedAccount();
                Intent it = new Intent(Intent.ACTION_CALL);
                it.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_CSIP, nbr));
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtra(SipProfile.FIELD_ACC_ID, acc.id);
                startActivity(it);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_detail, container, false);
        mResources = getResources();
        mInflater = inflater;
        mPhoneCallDetailsHelper = new PhoneCallDetailsHelper(mResources);
        mHeaderTextView = (TextView) v.findViewById(R.id.header_text);
        mHeaderOverlayView = v.findViewById(R.id.photo_text_bar);
        mMainActionView = (ImageView) v.findViewById(R.id.main_action);
        mMainActionPushLayerView = (ImageButton) v.findViewById(R.id.main_action_push_layer);
        mContactBackgroundView = (ImageView) v.findViewById(R.id.contact_background);
        mAccountChooserButton = (AccountChooserButton) v.findViewById(R.id.call_choose_account);
        btnAddToContact=(Button)v.findViewById(R.id.btnAddToContact);
        btnMesssage=(Button)v.findViewById(R.id.btnR4WMessage);
        btnCall=(Button)v.findViewById(R.id.btnR4WCall);
        btnInvite=(Button)v.findViewById(R.id.btnInvite);
        prefs = getActivity().getSharedPreferences("com.roamprocess1.roaming4world",Context.MODE_PRIVATE);
		stored_lastdialnumber = "com.roamprocess1.roaming4world.lastdialnumber";
		
		btnCall.setOnClickListener(this);
		btnMesssage.setOnClickListener(this);
		mContactBackgroundView.setOnClickListener(this);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateData(getCallLogEntryUris());
    }

    /**
     * Returns the list of URIs to show.
     * <p>
     * There are two ways the URIs can be provided to the activity: as the data
     * on the intent, or as a list of ids in the call log added as an extra on
     * the URI.
     * <p>
     * If both are available, the data on the intent takes precedence.
     */
    private Uri[] getCallLogEntryUris() {
    	
    	System.out.println("getCallLogEntryUris");
        long[] ids = getArguments().getLongArray(EXTRA_CALL_LOG_IDS);
        Uri[] uris = new Uri[ids.length];
        for (int index = 0; index < ids.length; ++index) {
            uris[index] = ContentUris.withAppendedId(SipManager.CALLLOG_ID_URI_BASE, ids[index]);
        }
        return uris;
    }

    /**
     * Update user interface with details of given call.
     * 
     * @param callUris URIs into {@link CallLog.Calls} of the calls to be
     *            displayed
     */
    private void updateData(final Uri... callUris) {
    	
    	System.out.println("updateData()");

        final int numCalls = callUris.length;
        if(numCalls == 0) {
            Log.w(THIS_FILE, "No calls logs as parameters");
            return;
        }
        
        PhoneCallDetails[] details = new PhoneCallDetails[numCalls];
        for (int index = 0; index < numCalls; ++index) {
            details[index] = getPhoneCallDetailsForUri(callUris[index]);
        }

        // We know that all calls are from the same number and the same contact,
        // so pick the
        // first.
        firstDetails = details[0];
        final Uri contactUri = firstDetails.contactUri;
        final Uri photoUri = firstDetails.photoUri;

        // Set the details header, based on the first phone call.
        mPhoneCallDetailsHelper.setCallDetailsHeader(mHeaderTextView, firstDetails);

        // Cache the details about the phone number.
        // final Uri numberCallUri = mPhoneNumberHelper.getCallUri(mNumber);
        // final boolean canPlaceCallsTo =
        // mPhoneNumberHelper.canPlaceCallsTo(mNumber);
        // final boolean isVoicemailNumber =
        // mPhoneNumberHelper.isVoicemailNumber(mNumber);
        // final boolean isSipNumber = mPhoneNumberHelper.isSipNumber(mNumber);

        // Let user view contact details if they exist, otherwise add option to
        // create new
        // contact from this number.
        final Intent mainActionIntent;
        final int mainActionIcon;
        final String mainActionDescription;

        final CharSequence nameOrNumber;
        if (!TextUtils.isEmpty(firstDetails.name)) {
            nameOrNumber = firstDetails.name;
        } else {
            nameOrNumber = firstDetails.number;
        }

        if (contactUri != null) {
            mainActionIntent = new Intent(Intent.ACTION_VIEW, contactUri);
            mainActionIcon = R.drawable.ic_contacts_holo_dark;
            mainActionDescription = nameOrNumber.toString();
        } else if(!TextUtils.isEmpty(firstDetails.number)){
           String saveNumber=firstDetails.number.toString().replace("<sip:","");
        	SaveNo = saveNumber.split("@");
        	mainActionIntent = ContactsWrapper.getInstance().getAddContactIntent((String) firstDetails.name, SaveNo[0]);
        	mainActionIcon = R.drawable.add_user;
            mainActionDescription = getString(R.string.menu_add_to_contacts);
            
            if(TextUtils.isEmpty(firstDetails.name)) {
            	
            	finalNumber=cantactnumber;
            	if(SaveNo[0].startsWith("011"))
                mHeaderTextView.setText(SaveNo[0].replace("011", ""));
            	else
            	mHeaderTextView.setText(SaveNo[0]);
                btnAddToContact.setVisibility(View.VISIBLE);
                btnInvite.setVisibility(View.VISIBLE);
                btnCall.setVisibility(View.GONE);
                btnMesssage.setVisibility(View.GONE);
                
            }else {
            	
            	 btnAddToContact.setVisibility(View.GONE);
                 btnInvite.setVisibility(View.GONE);
                 btnCall.setVisibility(View.VISIBLE);
                 btnMesssage.setVisibility(View.VISIBLE);
                 
                 
            	System.out.println("firstDetails.name:"+firstDetails.name);
            	mHeaderTextView.setText(getString(R.string.menu_add_address_to_contacts, firstDetails.name));
            }
        }else {
            // If we cannot call the number, when we probably cannot add it as a
            // contact either.
            // This is usually the case of private, unknown, or payphone
            // numbers.
            mainActionIntent = null;
            mainActionIcon = 0;
            mainActionDescription = null;
        }

        if (mainActionIntent == null) {
            mMainActionView.setVisibility(View.VISIBLE);
            mMainActionPushLayerView.setVisibility(View.GONE);
            mHeaderTextView.setVisibility(View.VISIBLE);
            mHeaderOverlayView.setVisibility(View.INVISIBLE);
        } else {
        	mMainActionView.setVisibility(View.VISIBLE);
            mMainActionView.setImageResource(mainActionIcon);
            mMainActionPushLayerView.setVisibility(View.VISIBLE);
            
            mMainActionPushLayerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                
                	if(contactUri==null){
                		Intent intent = new Intent(Intent.ACTION_INSERT);
						intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
						if(SaveNo[0].startsWith("011"))
							intent.putExtra(ContactsContract.Intents.Insert.PHONE,SaveNo[0].replace("011", ""));
						else
							intent.putExtra(ContactsContract.Intents.Insert.PHONE,SaveNo[0].toString());	
						startActivity(intent);
                		
                	}else{
                		startActivity(mainActionIntent);	
                	}
                }
            });
            mMainActionPushLayerView.setContentDescription(mainActionDescription);
            mHeaderTextView.setVisibility(View.VISIBLE);
            mHeaderOverlayView.setVisibility(View.VISIBLE);
        }
        
        
        btnAddToContact.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(contactUri==null){
					
					
            		Intent intent = new Intent(Intent.ACTION_INSERT);
					intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					intent.putExtra(ContactsContract.Intents.Insert.PHONE,removeOutCallNum( "+" + SaveNo[0].toString()));
					startActivity(intent);
            		
            	}			
				
			}
		});
        
        

        // This action allows to call the number that places the call.
        // final CharSequence displayNumber = firstDetails.formattedNumber;
        CharSequence displayNumber;
        if (!TextUtils.isEmpty(firstDetails.number)) {
        	displayNumber = SipUri.getCanonicalSipContact(firstDetails.number.toString(), false);
        } else {
        	displayNumber = mResources.getString(R.string.unknown);
        }

        String callText = getString(R.string.description_call, displayNumber);
        configureCallButton(callText, firstDetails.numberLabel, firstDetails.number);

        ListView historyList = (ListView) getView().findViewById(R.id.history);
        historyList.setAdapter(new CallDetailHistoryAdapter(getActivity(), mInflater, details));

        mAccountChooserButton.setTargetAccount(firstDetails.accountId);
        loadContactPhotos(photoUri, contactUri);
    }

    public String removeOutCallNum(String number){
    	String num = number ;
    	if(num.startsWith("011")){
    		num = num.substring(3);
    	}
    	return num;
    }
    /** Return the phone call details for a given call log URI. */
    private PhoneCallDetails getPhoneCallDetailsForUri(Uri callUri) {
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor callCursor = resolver.query(callUri, CALL_LOG_PROJECTION, null, null, null);
        try {
            if (callCursor == null || !callCursor.moveToFirst()) {
                throw new IllegalArgumentException("Cannot find content: " + callUri);
            }

            // Read call log specifics.
            String number = callCursor.getString(NUMBER_COLUMN_INDEX);
            long date = callCursor.getLong(DATE_COLUMN_INDEX);
            long duration = callCursor.getLong(DURATION_COLUMN_INDEX);
            int callType = callCursor.getInt(CALL_TYPE_COLUMN_INDEX);
            Long accountId = callCursor.getLong(PROFILE_ID_COLUMN_INDEX);
            int statusCode = callCursor.getInt(STATUS_CODE_COLUMN_INDEX);
            String statusText = callCursor.getString(STATUS_TEXT_COLUMN_INDEX);

            // Formatted phone number.
            final CharSequence formattedNumber;
            // Read contact specifics.
            final CharSequence nameText;
            final int numberType;
            final CharSequence numberLabel;
            final Uri photoUri;
            final Uri lookupUri;
            // If this is not a regular number, there is no point in looking it
            // up in the contacts.
            CallerInfo info = CallerInfo.getCallerInfoFromSipUri(getActivity(), number);
            if (info == null) {
                formattedNumber = number;
                nameText = "";
                numberType = 0;
                numberLabel = "";
                photoUri = null;
                lookupUri = null;
            } else {
                formattedNumber = info.phoneNumber;
                nameText = info.name;
                numberType = info.numberType;
                numberLabel = info.phoneLabel;
                photoUri = info.photoUri;
                lookupUri = info.contactContentUri;
            }
            return new PhoneCallDetails(number, formattedNumber,
                    new int[] {
                            callType
                    }, date, duration,
                    accountId, statusCode, statusText,
                    nameText, numberType, numberLabel, lookupUri, photoUri);
        } finally {
            if (callCursor != null) {
                callCursor.close();
            }
        }
    }

    /** Load the contact photos and places them in the corresponding views. */
    @SuppressLint("SdCardPath") 
    private void loadContactPhotos(Uri photoUri, Uri contactUri) {

    	System.out.println("loadContactPhotos");
    	int defaultLargePhoto = R.drawable.ic_contact_picture_180_holo_light;
    	
    	String path = "/sdcard/R4W/ProfilePic/" + usernumber +".png";
        System.out.println("msg adp out - path="+path);
        Bitmap bm = null;
        File imageFile = new File(path);
		if(imageFile.exists()){
			try {
    			bm = BitmapFactory.decodeFile(path);
    			bm = ImageHelperCircular.getRoundedCornerBitmap(bm, bm.getWidth());
    			mContactBackgroundView.setImageBitmap(bm);
    		} catch (Exception e) {
				// TODO: handle exception
				try {
					mContactBackgroundView.setImageURI(Uri.parse(path));
				        			
	    		} catch (Exception e2) {

	    	        if (photoUri != null) {
	    	            // Android 4.0 - high res photo
	    	            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(getActivity(), mContactBackgroundView,photoUri, defaultLargePhoto);
	    	        } else if (contactUri != null) {
	    	            CallerInfo person = new CallerInfo();
	    	            person.contactContentUri = contactUri;
	    	            // Android < 4.0 - low res picture
	    	            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(getActivity(),
	    	                    mContactBackgroundView, person, defaultLargePhoto);
	    	        } else {
	    	            // Default picture
	    	            mContactBackgroundView.setImageResource(defaultLargePhoto);
	    	        }
			 	}
			}
            }else{
       

                if (photoUri != null) {
                    // Android 4.0 - high res photo
                    ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(getActivity(), mContactBackgroundView,photoUri, defaultLargePhoto);
                } else if (contactUri != null) {
                    CallerInfo person = new CallerInfo();
                    person.contactContentUri = contactUri;
                    // Android < 4.0 - low res picture
                    ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(getActivity(),
                            mContactBackgroundView, person, defaultLargePhoto);
                } else {
                    // Default picture
                    mContactBackgroundView.setImageResource(defaultLargePhoto);
                }
            
            }
       
    	/*orignal code
    	
        if (photoUri != null) {
            // Android 4.0 - high res photo
            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(getActivity(), mContactBackgroundView,photoUri, defaultLargePhoto);
        } else if (contactUri != null) {
            CallerInfo person = new CallerInfo();
            person.contactContentUri = contactUri;
            // Android < 4.0 - low res picture
            ContactsAsyncHelper.updateImageViewWithContactPhotoAsync(getActivity(),
                    mContactBackgroundView, person, defaultLargePhoto);
        } else {
            // Default picture
            mContactBackgroundView.setImageResource(defaultLargePhoto);
        }
        */
    }

    /** Configures the call button area using the given entry. */
    private void configureCallButton(String callText, CharSequence nbrLabel, CharSequence number) {
        View convertView = getView().findViewById(R.id.call_and_sms);
        convertView.setVisibility(TextUtils.isEmpty(number) ? View.GONE : View.VISIBLE);

        TextView text = (TextView) convertView.findViewById(R.id.call_and_sms_text);

        View mainAction = convertView.findViewById(R.id.call_and_sms_main_action);
        mainAction.setOnClickListener(mPrimaryActionListener);
        mainAction.setContentDescription(callText);
        if(TextUtils.isEmpty(number)) {
        	number = "";
        }
        mainAction.setTag(SipUri.getCanonicalSipContact(number.toString(), false));
        /*
        String data=callText.substring('c','@'); 
        System.out.println(" CallLogDetailsFragment  data " +data);
        */
      
        String[] call1 = callText.split("@");
        System.out.println(" CallLogDetailsFragment  call1 " +call1[0]);
        
        cantactnumber = call1[0];
        if(cantactnumber.contains(" ")){
        	String[] arr = cantactnumber.split(" ");
        	if(arr[1].startsWith("011")){
        		arr[1] = arr[1].substring(3) + " (R4W Out call)";
        	}
        	usernumber = arr[1];
        	cantactnumber = arr[0] + " +" + arr[1] ;
        }
        
//        text.setText(call1[0]);
        System.out.println(" CallLogDetailsFragment  c value " +cantactnumber);
        text.setText(cantactnumber);

        TextView label = (TextView) convertView.findViewById(R.id.call_and_sms_label);
        if (TextUtils.isEmpty(nbrLabel)) {
            label.setVisibility(View.GONE); 
        } else {
            label.setText(nbrLabel);
            label.setVisibility(View.VISIBLE);
        }
    }

    public void onMenuRemoveFromCallLog(MenuItem menuItem) {
        final StringBuilder callIds = new StringBuilder();
        for (Uri callUri : getCallLogEntryUris()) {
            if (callIds.length() != 0) {
                callIds.append(",");
            }
            callIds.append(ContentUris.parseId(callUri));
        }

        getActivity().getContentResolver().delete(SipManager.CALLLOG_URI,
                Calls._ID + " IN (" + callIds + ")", null);
        if (quitListener != null) {
            quitListener.onQuit();
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.setLogLevel(6);
		Log.d("usernumber",usernumber + " @");
		String num = getFormattedNumber(usernumber);
		Log.d("num",num + " @");
		switch(v.getId()){
		
		case R.id.btnR4WCall:
			prefs.edit().putString(stored_lastdialnumber, num).commit();
			RContactlist rContactlist= new RContactlist();
			rContactlist.r4wCall_Chat(num);
			break;
		
		case R.id.btnR4WMessage:
			prefs.edit().putString(stored_chatuserNumber, num).commit();
			callMessage(num);
			break;
			
		case R.id.contact_background:
			Intent i = new Intent(getActivity(), R4WChatUserImage.class);
			i.putExtra("R4wCallingNumber",getFormattedNumber(usernumber));
			i.putExtra("R4wCallingName",firstDetails.name);
			getActivity().startActivity(i);
			break;
		}
		
	}
	
	private String getFormattedNumber(String nbr2) {
		// TODO Auto-generated method stub
		if(nbr2.contains("@@")){
			String[] ar = nbr2.split("@@");
			nbr2 = ar[0];
			if(nbr2.contains(":")){
				ar = nbr2.split(":");
				nbr2 = ar[1];		
			}
		}
		return nbr2;
	}

	public void callMessage(String numberget ) {
		// TODO Auto-generated method stub
		String fromFull = "<sip:"+ numberget+"@208.43.85.86>";
		String number = "sip:"+ numberget +"@208.43.85.86";
        Bundle b = MessageFragment.getArguments(number, fromFull);
        
            Intent it = new Intent(getActivity(), MessageActivity.class);
            it.putExtras(b);
            it.putExtra("call", true);
            getActivity().startActivity(it);
    	       
	}

}
