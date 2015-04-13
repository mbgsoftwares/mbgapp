package com.roamprocess1.roaming4world.widgets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.ContactsContract.Data;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.models.Filter;
import com.roamprocess1.roaming4world.utils.Log;
import com.roamprocess1.roaming4world.utils.contacts.ContactsAutocompleteAdapter;
import com.roamprocess1.roaming4world.utils.contacts.ContactsWrapper;
import com.roamprocess1.roaming4world.widgets.AccountChooserButton.OnAccountChangeListener;

public class EditSipUriContactsFragment extends LinearLayout implements TextWatcher, OnItemClickListener{

	 protected static final String THIS_FILE = "EditSipUri";
	    private AutoCompleteTextView dialUser;
	    private AccountChooserButton accountChooserButtonText;
	    private TextView domainTextHelper;
	    private ListView completeList;
	    private ContactAdapter contactsAdapter;
	    private ContactsAutocompleteAdapter autoCompleteAdapter;
	    public String[] contact_array;
	    public String s;
	    public Cursor c,result_contacts_cursor;
	    public DBContacts sqliteprovider;
	    public EditSipUriContactsFragment(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        setGravity(Gravity.CENTER_HORIZONTAL);
	        setOrientation(VERTICAL);
	        LayoutInflater inflater = LayoutInflater.from(context);
	        inflater.inflate(R.layout.edit_sip_uri_contact_fragment, this, true);

	        dialUser = (AutoCompleteTextView) findViewById(R.id.dialtxt_user);
	        accountChooserButtonText = (AccountChooserButton) findViewById(R.id.accountChooserButtonText);
	        domainTextHelper = (TextView) findViewById(R.id.dialtxt_domain_helper);
	        completeList = (ListView) findViewById(R.id.autoCompleteList);

	        // Map events
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
	        
	    }
	    
	    /* (non-Javadoc)
	     * @see android.view.View#onAttachedToWindow()
	     */
	    @SuppressLint("NewApi")
		@Override
	    protected void onAttachedToWindow() {
	        super.onAttachedToWindow();

	        if(isInEditMode()) {
	            // Don't bind cursor in this case
	            return;
	        }
	        System.out.println("EditSipUri.java in onAttachedToWindow");
	        c = ContactsWrapper.getInstance().getContactsPhonesR4W(getContext(), null);
	        System.out.println("EditSipUri.java in onAttachedToWindow after calling getContactsPhones");
	        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

	        StrictMode.setThreadPolicy(policy); 
	        s=fetch_contact_list(c);
	        System.out.println("EditSipUriContacts S data value =="+s);
	        JSONObject jsonObjRecv=null;
	        JSONObject obj = new JSONObject();
	        String refresh_status = null;
	        try {
				obj.put("contact_string", s);
				System.out.println(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        sqliteprovider=new DBContacts(getContext());
	        sqliteprovider.openToRead();
	        String URL="http://ip.roaming4world.com/esstel/suscriber_contact_list_fetch.php";
	        try
	        {
	        	Cursor show_refresh=sqliteprovider.get_refresh_value();
	        	
	        	show_refresh.moveToFirst();
	        	refresh_status=show_refresh.getString(1);
	        	refresh_status="no";
	        	sqliteprovider.close();
	        	if(refresh_status.equals("yes"))
	        	{
	        		System.out.println("fetch data");
	        		sqliteprovider.openToWrite();
	        		
	        		sqliteprovider.close();
	        		jsonObjRecv = SendHttpPost(URL, obj);
	        		contact_array=format_json_response(jsonObjRecv).split(",");
	        	}
	        	else
	        	{
	        		System.out.println("not to fetch data");
	        	}
	        
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        	String json2="{\"contacts\":\"[\"false\"]\"}";
				try {
					jsonObjRecv=new JSONObject(json2);
					
					
					
				} 
				catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
	        
	        //sqliteprovider.delete_all_contacts_in_db();
	        //System.out.println(contacts_returned);
	        
	        //System.out.println(contact_array[0]);
	        String del_contact_list="";
	        Cursor[] cur = new Cursor[300];
	        sqliteprovider.openToWrite();
	        if(refresh_status.equals("yes"))
	        {
	        	System.out.println("in system refresh status equals yes");
	        for(int i=0;i<contact_array.length;i++)
	        {
	        	cur[i]=ContactsWrapper.getInstance().getContactsPhonesR4W(getContext(), contact_array[i].replace(GetCountryZipCode(), ""));
	        	//String value = cur[i].getString(cur[i].getColumnIndex(Data.DATA1));
	        	//System.out.println("in main_cur "+value);
	        	cur[i].moveToFirst();
	        	System.out.println("no of rows in cur "+cur[i].getCount());
	        	try
	        	{
	        	sqliteprovider.insert_contact_in_db(contact_array[i].replace(GetCountryZipCode(), ""));
	        	}
	        	catch(SQLiteConstraintException e)
	        	{
	        		
	        	}
	        	del_contact_list+="\""+contact_array[i].replace(GetCountryZipCode(), "")+"\""+",";
	        }
	        sqliteprovider.delete_contacts_in_db(del_contact_list.substring(0, del_contact_list.length()-1));
	        sqliteprovider.close();
	        }
	        else
	        {
	        	System.out.println("in system refresh status equals no");
	        	try
	        	{
	        	sqliteprovider.openToRead();
	        	result_contacts_cursor=sqliteprovider.fetch_contact_from_db();
	        	int j=0;
	        	if(result_contacts_cursor.getCount()<1)
	        	{
	        		System.out.println("count is less than one");
	        		sqliteprovider.close();
	        		sqliteprovider.openToWrite();
	        		//sqliteprovider.close();
	        		jsonObjRecv = SendHttpPost(URL, obj);
	        		contact_array=format_json_response(jsonObjRecv).split(",");
	        		
	        		for(int i=0;i<contact_array.length;i++)
	    	        {
	    	        	cur[i]=ContactsWrapper.getInstance().getContactsPhonesR4W(getContext(), contact_array[i].replace(GetCountryZipCode(), ""));
	    	        	//String value = cur[i].getString(cur[i].getColumnIndex(Data.DATA1));
	    	        	//System.out.println("in main_cur "+value);
	    	        	cur[i].moveToFirst();
	    	        	System.out.println("no of rows in cur "+cur[i].getCount());
	    	        	try
	    	        	{
	    	        	sqliteprovider.insert_contact_in_db(contact_array[i].replace(GetCountryZipCode(), ""));
	    	        	}
	    	        	catch(SQLiteConstraintException e)
	    	        	{
	    	        		
	    	        	}
	    	        	del_contact_list+="\""+contact_array[i].replace(GetCountryZipCode(), "")+"\""+",";
	    	        }
	        		sqliteprovider.delete_contacts_in_db(del_contact_list.substring(0, del_contact_list.length()-1));
	        		sqliteprovider.close();
	        	}
	        	else
	        	{
	        		System.out.println("count is greater than one");
	        	while(result_contacts_cursor.moveToNext() && result_contacts_cursor!=null)
	        	{
	        		cur[j]=ContactsWrapper.getInstance().getContactsPhonesR4W(getContext(), result_contacts_cursor.getString(1));
	        		cur[j].moveToFirst();
	        		j++;
	        	}
	        	}
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        	
	        }
	        Cursor main_cur = null;
	        try
	        {
	        	
	        main_cur=new MergeCursor(cur);
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        //System.out.println("no of rows in main_cur "+main_cur.getCount());
	        //new asynctask_contact_fetch().execute();
	        contactsAdapter = new ContactAdapter(getContext(), main_cur);
	        System.out.println("after contactadapter");
	        completeList.setAdapter(contactsAdapter);
	        //completeList.setOnItemClickListener(EditSipUriContactsFragment.this);
	        System.out.println("after clicklistener");
	        autoCompleteAdapter = new ContactsAutocompleteAdapter(getContext());
	        System.out.println("after contactautocompleteadapter");
	        dialUser.setAdapter(autoCompleteAdapter);
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
	            super(context, R.layout.serarch_contacts_list_items_contacts_fragment, c, new String[] {}, new int[] {});
	            alphaIndexer = new AlphabetIndexer(c, ContactsWrapper.getInstance()
	                    .getContactIndexableColumnIndex(c),
	                    " ABCDEFGHIJKLMNOPQRSTUVWXYZ");
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cursor) {
	            super.bindView(view, context, cursor);
	            
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
	            domainTextHelper.setText("");
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
	        String number = (String) view.getTag();
	        SipProfile account = accountChooserButtonText.getSelectedAccount();
	        String rewritten = Filter.rewritePhoneNumber(getContext(), account.id, number);
	        setTextValue(rewritten);
	        Log.d(THIS_FILE, "Clicked contact " + number);
	    }

	    public void setShowExternals(boolean b) {
	        accountChooserButtonText.setShowExternals(b);
	    }

	    public String fetch_contact_list(Cursor c)
	    {
	    	String concat_contact_list="";
	    	String Country_zip_code=GetCountryZipCode();
	    	while(c.moveToNext())
	    	{
	    		String value = c.getString(c.getColumnIndex(Data.DATA1));
	    		if(!c.isLast())
	    		{
	    			if(value.length()>=8 && !value.startsWith("*") && !value.startsWith("#"))
	    			{
	    			if(value.startsWith("+"))
	    			{
	    				concat_contact_list+=value.substring(1)+",";
	    			}
	    			else if(value.startsWith("00"))
	    			{
	    				String modify_contact_no=value.substring(2);
	    				modify_contact_no=Country_zip_code+modify_contact_no;
	    				concat_contact_list+=modify_contact_no.toString()+",";
	    			}
	    			else if(value.startsWith("0"))
	    			{
	    				String modify_contact_no=value.substring(1);
	    				modify_contact_no=Country_zip_code+modify_contact_no;
	    				concat_contact_list+=modify_contact_no.toString()+",";
	    			}
	    			else if(!value.startsWith("+") && !value.startsWith("0"))
	    			{
	    				concat_contact_list+=Country_zip_code+value.toString()+",";
	    			}
	    			}
	    		//System.out.println("EditSipUri.java in fetch_contact_list "+concat_contact_list);
	    		}
	    		else
	    		{
	    			if(value.length()>=8 && !value.startsWith("*") && !value.startsWith("#"))
	    			{
	    			if(value.startsWith("+"))
	    			{
	    				concat_contact_list+=value.substring(1);
	    			}
	    			else if(value.startsWith("00"))
	    			{
	    				String modify_contact_no=value.substring(2);
	    				modify_contact_no=Country_zip_code+modify_contact_no;
	    				concat_contact_list+=modify_contact_no.toString();
	    			}
	    			else if(value.startsWith("0"))
	    			{
	    				String modify_contact_no=value.substring(1);
	    				modify_contact_no=Country_zip_code+modify_contact_no;
	    				concat_contact_list+=modify_contact_no.toString();
	    			}
	    			else if(!value.startsWith("+") && !value.startsWith("0"))
	    			{
	    				concat_contact_list+=Country_zip_code+value.toString();
	    			}
	    		}
	    		}
	    	}
	    	System.out.println("EditSipUri.java in fetch_contact_list "+concat_contact_list);
	    	return concat_contact_list;
	    }
	   
	   public String GetCountryZipCode(){

	        String CountryID="";
	        String CountryZipCode="";

	       TelephonyManager manager = (TelephonyManager) this.getContext().getSystemService(Context.TELEPHONY_SERVICE);
	              //getNetworkCountryIso
	        CountryID= manager.getSimCountryIso().toUpperCase();
	        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
	        for(int i=0;i<rl.length;i++){
	                                    String[] g=rl[i].split(",");
	                                    if(g[1].trim().equals(CountryID.trim())){
	                                                        CountryZipCode=g[0];break;  }
	        }
	        return CountryZipCode;
	   }
	   
	   
	   public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) {

			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httpPostRequest = new HttpPost(URL);

				StringEntity se;
				se = new StringEntity(jsonObjSend.toString());

				// Set HTTP parameters
				httpPostRequest.setEntity(se);
				httpPostRequest.setHeader("Accept", "application/json");
				httpPostRequest.setHeader("Content-type", "application/json");
				//httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

				long t = System.currentTimeMillis();
				HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
				//Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

				// Get hold of the response entity (-> the data):
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					// Read the content stream
					InputStream instream = entity.getContent();
					Header contentEncoding = response.getFirstHeader("Content-Encoding");
					if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
						instream = new GZIPInputStream(instream);
					}

					// convert content stream to a String
					String resultString= convertStreamToString(instream);
					instream.close();
					resultString = resultString.substring(0,resultString.length()-1); // remove wrapping "[" and "]"
					//System.out.println(resultString);
					// Transform the String into a JSONObject
					
					JSONObject jsonObjRecv = new JSONObject(resultString);
					// Raw DEBUG output of our received JSON object:
					//Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");
					
					return jsonObjRecv;
					
					
				} 

			}
			catch (Exception e)
			{
				// More about HTTP exception handling in another tutorial.
				// For now we just print the stack trace
				
				//System.out.println("The problem is :"+e.toString()); 
				e.printStackTrace();
				String json2="{\"contacts\":\"[\"false\"]\"}";
				try {
					JSONObject jsonobj=new JSONObject(json2);
					return jsonobj;
				} 
				catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}
		private static String convertStreamToString(InputStream is) {
			/*
			 * To convert the InputStream to String we use the BufferedReader.readLine()
			 * method. We iterate until the BufferedReader return null which means
			 * there's no more data to read. Each line will appended to a StringBuilder
			 * and returned as String.
			 * 
			 * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
			 */
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			catch(StringIndexOutOfBoundsException e)
			{
				//System.out.println(e.toString());
			}
				finally {
			
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				catch (StringIndexOutOfBoundsException e2) {
					// TODO: handle exception
					//System.out.println(e2.toString());
				}
			}
			return sb.toString();
		}
	   
		public class asynctask_contact_fetch extends AsyncTask<Void, Void, Boolean>
	    {        
	        
			private String json1;

	        /*@Override
	        public void onPreExecute() {
	            mProgressDialog3 = ProgressDialog.show(Dashboard.this, "Loading...", "Data is Loading...");
	        }*/

	        @Override
	        public Boolean doInBackground(Void... params) {
	            if(webservreqMAPNOGET1()){
	                Log.d("yay","SUCCESS");
	               System.out.println("yes");
	               System.out.println("befor contactadapter");
	               try
	               {
	            	   Looper.prepare();
	            	  
			        contactsAdapter = new ContactAdapter(EditSipUriContactsFragment.this.getContext(), c);
			        System.out.println("after contactadapter");
			        completeList.setAdapter(contactsAdapter);
			        //completeList.setOnItemClickListener(EditSipUriContactsFragment.this);
			        System.out.println("after clicklistener");
			        autoCompleteAdapter = new ContactsAutocompleteAdapter(EditSipUriContactsFragment.this.getContext());
			        System.out.println("after contactautocompleteadapter");
			        dialUser.setAdapter(autoCompleteAdapter);
	               }
	               catch(Exception e)
	               {
	            	   e.printStackTrace();
	               }
	                return true;
	            }
	            else{
	                Log.d("err","ERROR");
	                return false;
	            }
	        }
	        /*public void onPostExecute(Boolean result) { 
	            // dismiss the dialog once done 
	        	try
	        	{
	        	super.onPostExecute(result);
	        	try
	        	{
		        if(result)
		        {
		        	
			       
				}
		        else
		        {
		        	
		        }
	        }
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	    }*/
	    }
		
		public boolean webservreqMAPNOGET1(){
	    	
			System.out.println(s);
	        JSONObject jsonObjRecv=null;
	        JSONObject obj = new JSONObject();
	        try {
				obj.put("contact_string", s);
				System.out.println(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        String URL="http://ip.roaming4world.com/esstel/suscriber_contact_list_fetch.php";
	        
	        jsonObjRecv = SendHttpPost(URL, obj);
	        System.out.println(jsonObjRecv);
	        String contacts_returned="";
	        JSONArray returned_contacts = null;
	        try {
				returned_contacts=jsonObjRecv.getJSONArray("contacts");
				for(int i=0;i<returned_contacts.length();i++)
				{
					if(returned_contacts.getString(0).equals("false"))
					{
						return false;
					}
					else
					{
					contacts_returned+=returned_contacts.getString(i)+",";
					}
				}
				contacts_returned=contacts_returned.substring(0, contacts_returned.length()-1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        System.out.println(contacts_returned);
	        contact_array=contacts_returned.split(",");
	        System.out.println(contact_array[0]);
	    	return true;
	    }
	 
		public String format_json_response(JSONObject jsonObjRecv)
		{
			System.out.println(jsonObjRecv);
	        String contacts_returned="";
	        JSONArray returned_contacts = null;
	        boolean stats=false;
	        try {
				returned_contacts=jsonObjRecv.getJSONArray("contacts");
				for(int i=0;i<returned_contacts.length();i++)
				{
					if(returned_contacts.getString(0).equals("false"))
					{
						stats=false;
						break;
					}
					else
					{
						stats=true;
					contacts_returned+=returned_contacts.getString(i)+",";
					}
				}
				if(stats)
				contacts_returned=contacts_returned.substring(0, contacts_returned.length()-1);
				else
				{
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return contacts_returned;
		}
}
