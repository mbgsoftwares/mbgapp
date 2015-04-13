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

package com.roamprocess1.roaming4world.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.contactlist.ContactItemInterface;
import com.roamprocess1.roaming4world.contactlist.ExampleContactAdapter;
import com.roamprocess1.roaming4world.contactlist.ExampleContactItem;
import com.roamprocess1.roaming4world.contactlist.ExampleContactListView;
import com.roamprocess1.roaming4world.contactlist.ExampleDataSource;
import com.roamprocess1.roaming4world.models.Filter;
import com.roamprocess1.roaming4world.ui.messages.MessageActivity;
import com.roamprocess1.roaming4world.ui.messages.MessageFragment;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.contacts.ContactsWrapper;
import com.roamprocess1.roaming4world.utils.contacts.R4wContactAdapter;

public class MessageEditSipUri extends LinearLayout implements TextWatcher, OnItemClickListener {

    protected static final String THIS_FILE = "MessageEditSipUri";
    private AutoCompleteTextView dialUser;
    private AccountChooserButton accountChooserButtonText;
    private TextView domainTextHelper;
    private ExampleContactListView completeList;
    private ContactAdapter contactsAdapter;
    private R4wContactAdapter autoCompleteAdapter;
    public static String chatNumber=null;
    private EditText digits,editSearchText;
    boolean inSearchMode = false;
    private SearchListTask curSearchTask = null;
	private Object searchLock = new Object();
	private String searchString;
	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	
    public MessageEditSipUri(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.msgedit_sip_uri, this, true);
    	
        digits = (EditText)findViewById(R.id.input_search_query);
        
        dialUser = (AutoCompleteTextView) findViewById(R.id.dialtxt_user);
        accountChooserButtonText = (AccountChooserButton) findViewById(R.id.accountChooserButtonText);
        domainTextHelper = (TextView) findViewById(R.id.dialtxt_domain_helper);
        completeList = (ExampleContactListView) findViewById(R.id.autoCompleteList);
    	editSearchText=(EditText)findViewById(R.id.input_search_query);
    	
        editSearchText.setCursorVisible(false);
		editSearchText.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				editSearchText.setCursorVisible(true);
				return false;
			}
		});
        // Map events
        
      /*
        accountChooserButtonText.setOnAccountChangeListener(new OnAccountChangeListener() {
            @Override
            public void onChooseAccount(SipProfile account) {
                updateDialTextHelper();
                long accId = SipProfile.INVALID_ID;
                if (account != null) {
                    accId = account.id;
                }
                autoCompleteAdapter.setSelectedAccount(accId);
            }
        });
        dialUser.addTextChangedListener(this);
     
        */
        
    }
    
    /* (non-Javadoc)
     * @see android.view.View#onAttachedToWindow()
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(isInEditMode()) {
            // Don't bind cursor in this case
            return;
        }
        /*
        System.out.println("MessageEditSipUri.java in onAttachedToWindow");
        Cursor c = ContactsWrapper.getInstance().getContactsPhonesR4W(getContext(), null);
        System.out.println("MessageEditSipUri.java in onAttachedToWindow after calling getContactsPhones");
       
        contactsAdapter = new ContactAdapter(getContext(), c);
        completeList.setAdapter(contactsAdapter);
        completeList.setOnItemClickListener(this);

        autoCompleteAdapter = new R4wContactAdapter(getContext());
        */
        
        filterList = new ArrayList<ContactItemInterface>();
        contactList = ExampleDataSource.getSampleContactList(getContext());
        ExampleContactAdapter adapter = new ExampleContactAdapter(getContext(), R.layout.testexample_contact_item, contactList);
        completeList.setFastScrollEnabled(true);	
        completeList.setAdapter(adapter);
        
     
        
        completeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View view, int position,long id) {

			      final TextView txtNumber = (TextView) view.findViewById(R.id.txtr4wNumber);
			      	TextView txtName=(TextView) view.findViewById(R.id.txtr4wName);
			    		  
			      	final String forwardMessageTo=txtName.getText().toString();
			        System.out.println("EditUri onItemClick number"+txtNumber.getText());
			        SipProfile account = accountChooserButtonText.getSelectedAccount();
			        String rewritten = Filter.rewritePhoneNumber(getContext(), 1, txtNumber.getText().toString());
			        setTextValue(rewritten);
			        if(MessageFragment.bforwardMessage==true){
			    	   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
			    			alertDialogBuilder
			    				.setMessage("Foward to "+forwardMessageTo+"?")
			    				.setCancelable(false)
			       				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			    					public void onClick(DialogInterface dialog,int id) {
			    						// if this button is clicked, just close
			    						// the dialog box and do nothing
			    						dialog.cancel(); 
			    					}
			    				})
			    			.setPositiveButton("ok",new DialogInterface.OnClickListener() {
			    				public void onClick(DialogInterface dialog,int id) {
			    					messageFragment(txtNumber.getText().toString());		
			    				}
			    			  });
			     
			    			AlertDialog alertDialog = alertDialogBuilder.create();
			    			alertDialog.show();
			           }else{
			        	   callMessageActivity(txtNumber.getText().toString());
			           }
			       
			       Log.d(THIS_FILE, "Clicked contact ::: " + rewritten);
			    
				
			}
		});
		
        digits.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				searchString = digits.getText().toString().trim().toUpperCase();

				if (curSearchTask != null
						&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
					try {
						curSearchTask.cancel(true);
					} catch (Exception e) {
						Log.i("MessageEditSip", "Fail to cancel running search task");
					}

				}
				curSearchTask = new SearchListTask();
				curSearchTask.execute(searchString); // put it in a task so
														// that ui is not
														// freeze

			}
		});
        
        
        //dialUser.setAdapter(autoCompleteAdapter);
    }
    
    /* (non-Javadoc)
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if(isInEditMode()) {
            // Don't bind cursor in this case
            return;
        }
        if(contactsAdapter != null) {
            contactsAdapter.changeCursor(null);
        }
        if(autoCompleteAdapter != null) {
            autoCompleteAdapter.changeCursor(null);
        }
    }

    private class ContactAdapter extends SimpleCursorAdapter implements SectionIndexer {

        private AlphabetIndexer alphaIndexer;

        public ContactAdapter(Context context, Cursor c) {
            super(context, R.layout.r4wcontacts_row, c, new String[] {}, new int[] {});
            alphaIndexer = new AlphabetIndexer(c, ContactsWrapper.getInstance()
                    .getContactIndexableColumnIndexR4W(c),
                    " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            super.bindView(view, context, cursor);
            System.out.println("MessageEditSipUri.java in bindView ");
            ContactsWrapper.getInstance().bindContactPhoneViewR4W(view, context, cursor);
        }

        
        @Override
        public int getPositionForSection(int arg0) {
            return alphaIndexer.getPositionForSection(arg0);
        }

        @Override
        public int getSectionForPosition(int arg0) {
            return alphaIndexer.getSectionForPosition(arg0);
        }

        @Override
        public Object[] getSections() {
            return alphaIndexer.getSections();
        }

    }

    public class ToCall {
        private Long accountId;
        private String callee;

        public ToCall(Long acc, String uri) {
            accountId = acc;
            callee = uri;
        }

        /**
         * @return the pjsipAccountId
         */
        public Long getAccountId() {
            return accountId;
        }

        /**
         * @return the callee
         */
        public String getCallee() {
            return callee;
        }
    };

    private void updateDialTextHelper() {

        String dialUserValue = dialUser.getText().toString();

        accountChooserButtonText.setChangeable(TextUtils.isEmpty(dialUserValue));

        SipProfile acc = accountChooserButtonText.getSelectedAccount();
        if (!Pattern.matches(".*@.*", dialUserValue) && acc != null
                && acc.id > SipProfile.INVALID_ID) {
            domainTextHelper.setText("@" + acc.getDefaultDomain());
        } else {
            domainTextHelper.setText("");
        }

    }

    /**
     * Retrieve the value of the selected sip uri
     * 
     * @return the contact to call as a ToCall object containing account to use
     *         and number to call
     */
    public ToCall getValue() {
    	
    	
    	
        String userName = dialUser.getText().toString();
        System.out.println("UserName::::: "+userName);
        
        String toCall = "";
        Long accountToUse = null;
        if (TextUtils.isEmpty(userName)) {
            return null;
        }
        userName = userName.replaceAll("[ \t]", "");
        SipProfile acc = accountChooserButtonText.getSelectedAccount();
        if (acc != null) {
            accountToUse = acc.id;
            // If this is a sip account
            if (accountToUse > SipProfile.INVALID_ID) {
                if (Pattern.matches(".*@.*", userName)) {
                    toCall = "sip:" + userName + "";
                } else if (!TextUtils.isEmpty(acc.getDefaultDomain())) {
                    toCall = "sip:" + userName + "@" + acc.getDefaultDomain();
                } else {
                    toCall = "sip:" + userName;
                }
            } else {
                toCall = userName;
            }
        } else {
            toCall = userName;
        }

        return new ToCall(accountToUse, toCall);
    }

    public SipProfile getSelectedAccount() {
        return accountChooserButtonText.getSelectedAccount();
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        updateDialTextHelper();
    }

    @Override
    public void afterTextChanged(Editable s) {
        updateDialTextHelper();
    }

    /**
     * Reset content of the field
     * @see Editable#clear()
     */
    public void clear() {
        dialUser.getText().clear();
    }
    
    /**
     * Set the content of the field
     * @param number The new content to set in the field 
     */
    public void setTextValue(String number) {
        clear();
        dialUser.getText().append(number);
    }

    /**
     * Retrieve the underlying text field of this widget to modify it's behavior directly
     * @return the underlying widget
     */
    public EditText getTextField() {
        return dialUser;
    }


    @Override
    public void onItemClick(AdapterView<?> ad, View view, int position, long arg3) {
      
    	List<ContactItemInterface> searchList = inSearchMode ? filterList : contactList ;
		
		float lastTouchX = completeList.getScroller().getLastTouchDownEventX();
		
    	final String number = (String) view.getTag();
      	TextView txtName=(TextView) view.findViewById(R.id.name);
    		  
      	String forwardMessageTo=txtName.getText().toString();
        System.out.println("EditUri onItemClick number"+number);
        SipProfile account = accountChooserButtonText.getSelectedAccount();
        String rewritten = Filter.rewritePhoneNumber(getContext(), account.id, number);
        setTextValue(rewritten);
        if(MessageFragment.bforwardMessage==true){
    	   AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
    			alertDialogBuilder
    				.setMessage("Foward to "+forwardMessageTo+"?")
    				.setCancelable(false)
       				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog,int id) {
    						// if this button is clicked, just close
    						// the dialog box and do nothing
    						dialog.cancel(); 
    					}
    				})
    			.setPositiveButton("ok",new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,int id) {
    					messageFragment(number);
    				}
    			  });
     
    			AlertDialog alertDialog = alertDialogBuilder.create();
    			alertDialog.show();
           }else{
        	   callMessageActivity(number);
           }
       
       Log.d(THIS_FILE, "Clicked contact ::: " + rewritten);
    }

    public void setShowExternals(boolean b) {
        accountChooserButtonText.setShowExternals(b);
    }    
    
    public void callMessageActivity(String number){
    	number = number.replaceAll("[ \t]", "");
        SipProfile acc = accountChooserButtonText.getSelectedAccount();
        String messageNumber = "sip:"+ number + "@" + acc.getDefaultDomain();
        String fromFull = "<sip:"+ number+"@"+acc.getDefaultDomain()+">";
        
        System.out.println("MessageEditSipUri:messageNumber:"+messageNumber+":"+fromFull);
        Bundle b = MessageFragment.getArguments(messageNumber, fromFull);
        
		Intent it = new Intent(getContext(), MessageActivity.class);
        it.putExtras(b);
        it.putExtra("call", true);
        getContext().startActivity(it);
        ((Activity)getContext()).finish();
    } 
    
    public void messageFragment(String number)
    {
    	
		if(!MessageFragment.FORWARD_MSG.equals("")){
			MessageFragment.messageforwardText(MessageFragment.FORWARD_MSG, number);
		}
		MessageFragment.FORWARD_MSG = "";
		MessageFragment.bforwardMessage=false;

	    ((Activity)getContext()).finish();
             
    }
    
    
	private class SearchListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			filterList.clear();
			String keyword = params[0];
			inSearchMode = (keyword.length() > 0);
			if (inSearchMode) {
				// get all the items matching this
				for (ContactItemInterface item : contactList) {
					ExampleContactItem contact = (ExampleContactItem)item;
					System.out.println("contact for search :"+contact);
					if ((contact.getNickName().toUpperCase().indexOf(keyword) > -1) ) {
						System.out.println("Search item:"+item);
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
					try {
						ExampleContactAdapter adapter = new ExampleContactAdapter(getContext(), R.layout.testexample_contact_item, filterList);
						adapter.setInSearchMode(true);
						completeList.setInSearchMode(true);
						completeList.setAdapter(adapter);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
				else{
					try {
						
						ExampleContactAdapter adapter = new ExampleContactAdapter(getContext(), R.layout.testexample_contact_item, contactList);
						adapter.setInSearchMode(false);
						completeList.setInSearchMode(false);
						completeList.setAdapter(adapter);	
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			}
			
		}
	}
	
}
