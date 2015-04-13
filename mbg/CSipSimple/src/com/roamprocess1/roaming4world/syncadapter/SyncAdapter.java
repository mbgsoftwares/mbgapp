/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.roamprocess1.roaming4world.syncadapter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.accounts.Account;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.db.DBContacts;
import com.roamprocess1.roaming4world.roaming4world.DesktopVerificationcode_Activity;
import com.roamprocess1.roaming4world.roaming4world.NoNetwork;
import com.roamprocess1.roaming4world.roaming4world.R4WServiceMissedcallDownload;
import com.roamprocess1.roaming4world.ui.RContactlist;
import com.roamprocess1.roaming4world.ui.SipHome;
import com.roamprocess1.roaming4world.ui.RContactlist.AsyncTaskUploadContacts;
import com.roamprocess1.roaming4world.utils.contacts.ContactsWrapper;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in {@link SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    
    public Context mcontext ;
    /**
     * URL to fetch content from during a sync.
     *
     * <p>This points to the Android Developers Blog. (Side note: We highly recommend reading the
     * Android Developer Blog to stay up to date on the latest Android platform developments!)
     */
    private static final String FEED_URL = "http://android-developers.blogspot.com/atom.xml";

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    
	String stored_user_mobile_no, stored_user_country_code, no_image = "No image", signUpProcess , prefSignUpProcess ,
			username, number, user_status, user_urlpath , stored_user_bal , stored_min_call_credit , stored_server_ipaddress ;
	SharedPreferences prefs;
	public static String[] phone_no, url_image, image_path, name, status;
	ArrayList<String> updated_url,  updated_number;
	DBContacts dbContacts;
	Cursor cursor , allContactsCursor; 

	Intent resultIntent;
	NotificationManager mNotificationManager;
	final static String GROUP_KEY_MISSEDCALLS = "group_key_missedcalls";
	String selfNumber ;
	public String[] contact_array , r4wNameArray , r4wUserStatus , allConcat_contact_list_Name , 
					r4wUserImageArray , allContactsPhone , phoneContacts , getAllContacts;
	
	
	public List<String> r4wContactListName;
	private File contactFile;
	private String defaultPath;

    
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
        mcontext = context;
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mcontext = context;
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
    	
    	Log.d("SyncAdapter", "onPerformSync");
    	
        Log.i(TAG, "Beginning network synchronization");
        try {
            final URL location = new URL(FEED_URL);
            InputStream stream = null;

            try {
                Log.i(TAG, "Streaming data from network: " + location);
         //       stream = downloadUrl(location);
         //       updateLocalFeedData(stream, syncResult);
      
                
        		// TODO Auto-generated method stub
        		prefs = mcontext.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
        		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
        		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
        		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";
        		stored_min_call_credit = "com.roamprocess1.roaming4world.min_call_credit";
        		stored_server_ipaddress = "com.roamprocess1.roaming4world.server_ip";
    			signUpProcess = "com.roamprocess1.roaming4world.signUpProcess";

        		resultIntent = new Intent(mcontext, SipHome.class);
        
        		mNotificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        		selfNumber = prefs.getString(stored_user_country_code, "") + prefs.getString(stored_user_mobile_no, "");
                
				if (getConnectivityStatus(mcontext) != SipHome.TYPE_NOT_CONNECTED) {
					updateUserContacts();
					
					updateUserImages();
					updateOfflineMissedCalls();

				}
        // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
            	// mcontext.startActivity(new Intent(mcontext , SipHome.class));
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

	private void updateUserContacts() {

		System.out.println("User_mobile_no =="+selfNumber);
				
		signUpProcess = "com.roamprocess1.roaming4world.signUpProcess";
		prefSignUpProcess =prefs.getString(signUpProcess, "NotCompleted");
		
			allContactsCursor = ContactsWrapper.getInstance().getContactsPhones(mcontext,null);
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			getAllContacts = fetch_contact_list(allContactsCursor);
			r4wContactListName=	Arrays.asList(phoneContacts);
			r4wContactListName.size();			
			if(prefSignUpProcess.equals("NotCompleted")){ 
				defaultPath=Environment.getExternalStorageDirectory().getAbsolutePath();
				System.out.println("defaultPath:"+defaultPath);
				System.out.println("selfNumber:"+selfNumber);
				
				try {
					contactFile = new File(defaultPath, selfNumber+".txt");
					if (contactFile.exists()) {
						contactFile.delete();
						System.out.println("file Exist");
						sendContacts(contactFile);
						
					}else{
						System.out.println("file does not Exist");
							sendContacts(contactFile);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
	
	}
	
	public void  sendContacts(File contactFile) throws IOException{
		Log.d("sendContacts", "called");
		
		Log.d("sendContacts", "1");
		FileOutputStream fOut = new FileOutputStream(contactFile);
		Log.d("sendContacts", "2");
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
		Log.d("sendContacts", "3");
		String lastContacts=getAllContacts[getAllContacts.length-1];
		
		Log.d("sendContacts", "4");
		
		for(int x = 0; x <= getAllContacts.length ; x++ ){
			if(x == 0){
				myOutWriter.append(selfNumber);
			}else if( getAllContacts[ x - 1 ] != null ){
				if(getAllContacts[ x - 1 ].contains(",")){
					getAllContacts[ x - 1 ] = getAllContacts[ x - 1 ].substring(0, getAllContacts[ x - 1 ].length() - 1);
				}
				myOutWriter.append( "," + getAllContacts[ x - 1 ] );
			}
		
			
		}
            
        myOutWriter.close();
        fOut.close();
		int response = 0;
		
		String imagePathUri = defaultPath + "/" + selfNumber + ".txt";
		if (!imagePathUri.equals("")) {
			Log.d("sendContacts", "if (!imagePathUri.equals())");
			response = uploadFile(imagePathUri);
			Log.d("response ", response + "  d");
			if (response == 200) {
				Log.d("doInBackgroud", "doInBackground");
				contactFile.delete();
				
				if (websericeR4WContacts()) {
					try {
						CharSequence constraint ="str";
						if(RContactlist.r4wCompleteContactList.length!=0){
						ContactsWrapper.getInstance().getContactsPhonesR4W(mcontext, constraint);
							//c1.close();
							
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				
			}
			
			
			
		} 
	
	}
	
	public boolean websericeR4WContacts() {

		
		String contacts_returned = "",r4wName="",r4wUserImage="",r4wstatus="";
		System.out.println("getAllContacts:"+getAllContacts.length);
	
			HttpParams p = new BasicHttpParams();
            p.setParameter("user", "1");
            
            HttpClient httpclient = new DefaultHttpClient(p);
            
            String url = "http://ip.roaming4world.com/esstel/fetch_contacts_file.php?" + 
                    	 "self_contact="+selfNumber;
         
            HttpPost httppost = new HttpPost(url);
            System.out.println("URL being sent is "+url);
          
            try {
                Log.i(getClass().getSimpleName(), "send  task - start");
                
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", "1"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String responseBody = httpclient.execute(httppost, responseHandler);
                System.out.println("Response Body is "+responseBody);  
                
                // Parse
                JSONObject jsonObjRecv;
				try {
					JSONArray jsonArray = null;
					jsonObjRecv = new JSONObject(responseBody);
					Log.d(TAG,"jsonObjRecv :"+jsonObjRecv);
					
					String contacts = jsonObjRecv.getString("contacts");
					if(contacts.equals("Error")||contacts.equals("No Contacts")){
						Log.d(TAG,"No contacts Error");
						prefs.edit().putString(signUpProcess, "R4wContactInserted").commit();		
					}
					else{
						jsonArray = jsonObjRecv.getJSONArray("contacts");
						String path = jsonObjRecv.getString("path");
						Log.d(TAG, "jsonArray :"+jsonArray +"path:"+path);
			            if(jsonArray.length()!=0){
			                for (int i = 0; i < jsonArray.length(); i++) {
			                	JSONObject position = jsonArray.getJSONObject(i);
			                		contacts_returned += position.getString("p") + ",";
									r4wName+= position.getString("un")+",";
									r4wstatus+= position.getString("s")+","; 
									String url_image=position.getString("t");
									
									if(!url_image.equals("No image")){
										r4wUserImage +=path + url+ ",";
									}else{
										r4wUserImage += position.getString("t")+ ",";
									}
									
									if(jsonArray.getString(i)==""){
										
									}
								}
			                System.out.println("data=:contacts_returned :r4wName:r4wstatus :r4wUserImage"+contacts_returned+":"+r4wName+":"+r4wstatus+""+r4wUserImage+"");
			                System.out.println();
						}
					}

			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                //return true;
            } 
            catch(UnknownHostException e)
            {
            	e.printStackTrace();
            	return false;
            }
            catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            } 
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            catch(Exception e){
            	  //e.printStackTrace();
                  return false;
            }
        
		if(contacts_returned.length()!=0){
			contacts_returned = contacts_returned.substring(0,contacts_returned.length() - 1);
			r4wName=r4wName.substring(0, r4wName.length()-1);
			r4wstatus=r4wstatus.substring(0, r4wstatus.length()-1);
			r4wUserImage=r4wUserImage.substring(0, r4wUserImage.length()-1);
			contact_array = contacts_returned.split(",");
			r4wNameArray=r4wName.split(",");
			r4wUserStatus=r4wstatus.split(",");
			r4wUserImageArray=r4wUserImage.split(",");
			
			RContactlist.r4wCompleteContactList=new String[contact_array.length];
			
			com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi.r4wContacts = contact_array;
			for (int i = 0; i < com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi.r4wContacts.length; i++) {			
				String number=	com.roamprocess1.roaming4world.api.Roaming4WorldCustomApi.r4wContacts[i];
				//System.out.println("number :"+number);
				int index = r4wContactListName.indexOf(number.trim());
				//System.out.println("index ="+index);
				
				try {
					if(index!=-1){
						System.out.println("index ="+index);
						String r4wContactName =allContactsPhone[index];
						String[] parts = r4wContactName.split(",");
						String r4wNamePhone = parts[0];
						String r4wNumberName = parts[1];
						String datacomplte=r4wNamePhone+","+r4wNumberName+","+r4wUserStatus[i]+","+r4wUserImageArray[i]+","+r4wNameArray[i];
						System.out.println("datacomplte-" + datacomplte);
						RContactlist.r4wCompleteContactList[i]=datacomplte;
						}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			System.out.println("r4wCompleteContactList.length:"+ RContactlist.r4wCompleteContactList.length);
		
		}else{
			System.out.println("No Contact");
		}
		
		return true;
	
	}
	
	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";
		TelephonyManager manager = (TelephonyManager)mcontext.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		String[] rl = mcontext.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}
	
	public  String[] fetch_contact_list(Cursor c) {
		String concat_contact_list = "";
		String[] allConcat_contact_list=new String[c.getCount()];
		phoneContacts=new String[c.getCount()];
		
		allConcat_contact_list_Name=new String[c.getCount()];
		allContactsPhone= new String[c.getCount()];
		//System.out.println("Cccccc"+c.getCount());
		
		String Country_zip_code = GetCountryZipCode();
		int i=0;
		while (c.moveToNext()) {
			
			String value = c.getString(c.getColumnIndex(Data.DATA1));
			String Name =c.getString(c.getColumnIndex(Data.DISPLAY_NAME));
			//System.out.println("Data:"+_id+":"+contact_Id +":"+value+":"+Name+":");
			value= value.replaceAll("\\s+","");
			value= value.replaceAll("-","");
			//System.out.println("Number =="+value);
			
			
			if (!c.isLast()) {
				
					if ( !value.startsWith("*")&& !value.startsWith("#")) {
						
							if (value.startsWith("+")) {
								allConcat_contact_list[i]=value.substring(1) + ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
							
								
							} else if (value.startsWith("00")) {
								String modify_contact_no = value.substring(2);
								modify_contact_no = Country_zip_code+ modify_contact_no;
								allConcat_contact_list[i]=modify_contact_no.toString()+ ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
								
							} else if (value.startsWith("0")) {
								String modify_contact_no = value.substring(1);
								modify_contact_no = Country_zip_code+ modify_contact_no;
								allConcat_contact_list[i]= modify_contact_no.toString()+ ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
								
							} else if (!value.startsWith("+") && !value.startsWith("0")) {
								allConcat_contact_list[i]=Country_zip_code+ value.toString() + ",";
								allContactsPhone[i]=allConcat_contact_list[i]+Name;
							}
						}
			
			} else {
				if (!value.startsWith("*")&& !value.startsWith("#")) {
					
						if (value.startsWith("+")) {
							allConcat_contact_list[i]=value.substring(1);
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
							
						} else if (value.startsWith("00")) {
							String modify_contact_no = value.substring(2);
							modify_contact_no = Country_zip_code+ modify_contact_no;
							allConcat_contact_list[i]=modify_contact_no.toString();
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
									
						} else if (value.startsWith("0")) {
							String modify_contact_no = value.substring(1);
							modify_contact_no = Country_zip_code+ modify_contact_no;
							allConcat_contact_list[i]=modify_contact_no.toString();
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
							
						} else if (!value.startsWith("+") && !value.startsWith("0")) {
							allConcat_contact_list[i]=Country_zip_code+ value.toString();
							allContactsPhone[i]=allConcat_contact_list[i]+","+Name;
						}
				}
			}
			
			i++;
		}
		c.close();
		
		for(int x=0;x<allConcat_contact_list.length;x++)
		{	
			String number;
			if(allConcat_contact_list[x]!=null||allContactsPhone[x]!=null){
				number=allConcat_contact_list[x].replace(",", "");
				phoneContacts[x]=number;
				//System.out.println("number without ,:"+number+":"+allContactsPhone[x]);
			}else
			{
				phoneContacts[x]=allConcat_contact_list[x];
				//System.out.println("number without ,:"+allConcat_contact_list[x]);
			}
		concat_contact_list+=allConcat_contact_list[x]; 
			
			
		}
		//System.out.println("phoneContacts:length:allConcat_contact_list:length: "+phoneContacts.length+":"+allConcat_contact_list.length+":"+allContactsPhone.length);
		//System.out.println("allConcat_contact_list:length:"+allConcat_contact_list.length);
	
		return allConcat_contact_list;
	}

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return SipHome.TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return SipHome.TYPE_MOBILE;
		}
		return SipHome.TYPE_NOT_CONNECTED;
	}
	
	private void updateOfflineMissedCalls() {
		
	 String from_summary = "", noc = "", from_detailed = "", lt = "", ct = "";
	 Cursor c;
	 String time = "", value;
		try {
		
			dbContacts = new DBContacts(mcontext);
		//	dbContacts.openToWrite();
			dbContacts.openToRead();
			c = dbContacts.fetch_details_from_MIssedCall_Offline_Table();
			
			if(c.getCount() > 0)
			{
				c.moveToFirst();
				value = c.getString(2);
				time = "&time=" + value;
		    //  time = "&time=" + "1408434902";
			}
			
			c.close();
			dbContacts.close();
			
			Log.d("webService App Resume", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/app_resume_info.php?contact="
						  +prefs.getString(stored_user_country_code, "NoValue")
						  +prefs.getString(stored_user_mobile_no, "NoValue")
						  +time;
			
			Log.d("url", url + " #");
			
			HttpGet httpget = new HttpGet(url);
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			responseBody = httpclient.execute(httpget, responseHandler);
			
			dbContacts.openToWrite();
			
			JSONObject json = new JSONObject(responseBody);
				
			Log.d("response11", json + " #");
			
			if(json.getString("verify_from_pc").equals("true")){
				Intent i = new Intent(mcontext, DesktopVerificationcode_Activity.class);
			    i.putExtra("verify_code", json.getString("verify_code"));
			    i.putExtra("countdown_time", json.getString("countdown_time"));
			    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    mcontext.startActivity(i);  
			}
			
			prefs.edit().putString(stored_min_call_credit, json.getString("min_call_credit")).commit();
			prefs.edit().putString(stored_user_bal, json.getString("user_bal")).commit();
			prefs.edit().putString(stored_server_ipaddress, json.getString("server_ip")).commit();
			
			Log.d("stored_server_ip address", json.getString("server_ip") + " #");
			Log.d("stored_user_bal", json.getString("user_bal") + " #");
			
			if(json.getString("valid").equals("true")){
				
			
				JSONArray summary = json.getJSONArray("summary");
				JSONArray detailed = json.getJSONArray("detailed");

			
				for(int i = 0 ; i < summary.length() ; i++)
				{
					JSONObject summarydata = summary.getJSONObject(i);
					from_summary = summarydata.getString("from");
					noc = summarydata.getString("noc");
					lt = summarydata.getString("lt");
					
					Log.d("from_summary", from_summary + " #");
					Log.d("noc", noc + " #");
					Log.d("lt", lt + " #");
					
					dbContacts.insert_MIssedCall_Offline_detail_in_db(from_summary, lt , noc);
					
					/*
					mBuilder.setSmallIcon(R.drawable.notification_icon);
					mBuilder.setContentTitle("Missed call (" + noc +")");
					mBuilder.setContentText(from_summary + "   " + lt);
					mBuilder.setContentIntent(resultPendingIntent);
					mNotificationManager.notify(Integer.parseInt(lt), mBuilder.build());
					*/
					String count = "Missed call (" + noc +")" ;
					
					CharSequence dateText =
			                DateUtils.getRelativeTimeSpanString(Long.parseLong(lt)*1000,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE);
					
					String num_date = from_summary + "   last on " + dateText.toString();
					
					
					showNotification(count , num_date, Integer.parseInt(lt));
					
				}
				
				for(int i = 0 ; i < detailed.length() ; i++)
				{
					JSONObject detaileddata = detailed.getJSONObject(i);
					from_detailed = detaileddata.getString("from");
					ct = detaileddata.getString("ct");
				
					Log.d("from_detailed", from_detailed + " #");
					Log.d("ct", ct + " #");
				
					dbContacts.insert_MIssedCall_detail_in_db(from_detailed, ct);
				}

				dbContacts.close();
				
				
			}
			} catch (Throwable t) {
			t.printStackTrace();
		}
	}

    private void showNotification(String count, String number_date, int j) {

	//	Notification notification = new Notification(R.drawable.r4wlogowhitefill,count, System.currentTimeMillis());
		Intent in = new Intent(mcontext, SipHome.class);
		in.putExtra("MissedCall", true);
		PendingIntent contentIntent = PendingIntent.getActivity(mcontext, 0,in, PendingIntent.FLAG_CANCEL_CURRENT);
//		notification.setLatestEventInfo(mcontext, count, number_date, contentIntent);
    	Notification notification = new NotificationCompat.Builder(mcontext)
											        .setContentTitle(count)
											        .setContentText(number_date)
											        .setSmallIcon(R.drawable.r4wlogowhitefill)
//											        .setGroup(GROUP_KEY_MISSEDCALLS)
											        .setContentIntent(contentIntent)
											        .build();
											 
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(j, notification);

	}
	private void updateUserImages() {
		// TODO Auto-generated method stub
    	
    	updated_number = new ArrayList<String>();
		updated_url = new ArrayList<String>();
    	
    	if (webServiceImageUrl()) {

			for (int i = 0; i < updated_url.size(); i++) {
				Log.d(i + " updated_number ", updated_number.get(i) + "  !");
				Log.d(i + " updated_url ", updated_url.get(i) + "  !");
				String path = getImages(updated_url.get(i),
						updated_number.get(i));
				image_path = new String[url_image.length];
				image_path[i] = path;

				Log.d(i + " image_path ", image_path[i] + "  !");

				if (path != "") {

				} else {
				}
			}
		}
	}
    
    public String getImages(String uri, String number)
    {
    	String sdcardDir = Environment.getExternalStorageDirectory() + "";
   		File file = null;
   		if (!uri.equals(no_image)) {

   			String filepath = "";
   			try {
   				URL url = new URL(uri);
   				HttpURLConnection urlConnection = (HttpURLConnection) url
   						.openConnection();
   				urlConnection.setRequestMethod("GET");
   				urlConnection.setDoOutput(true);
   				urlConnection.connect();
   				String filename = number + ".png";

   				File imageDirectory = new File(sdcardDir + "/R4W/");

   				if (!imageDirectory.exists()) {
   					imageDirectory.mkdir();
   				}

   				File imageDirectoryprofile = new File(sdcardDir + "/R4W/ProfilePic");

   				if (!imageDirectoryprofile.exists()) {
   					imageDirectoryprofile.mkdir();
   				}

   				file = new File(imageDirectoryprofile.getAbsolutePath(),
   						filename);

   				if (file.createNewFile()) {
   					file.createNewFile();
   				}

   				FileOutputStream fileOutput = new FileOutputStream(file);
   				InputStream inputStream = urlConnection.getInputStream();
   				int totalSize = urlConnection.getContentLength();
   				int downloadedSize = 0;
   				byte[] buffer = new byte[1024];
   				int bufferLength = 0;
   				while ((bufferLength = inputStream.read(buffer)) > 0) {
   					fileOutput.write(buffer, 0, bufferLength);
   					downloadedSize += bufferLength;
   					Log.i("Progress:", "downloadedSize:" + downloadedSize
   							+ "totalSize:" + totalSize);
   				}
   				fileOutput.close();
   				if (downloadedSize == totalSize)
   					filepath = file.getPath();
   			} catch (Exception e) {
   				filepath = "";
   				e.printStackTrace();
   				if (file.exists()) {
   					file.delete();
   				}
   			}
   			Log.i("filepath:", " " + filepath);
   			return filepath;

   		}

   		else {
   			return no_image;
   		}

   	}

	private boolean webServiceImageUrl() {
		


		try {
			Log.d("webServiceUserInfo", "called");
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://ip.roaming4world.com/esstel/profile-data/profile_pic_download.php?self_contact="
						  +prefs.getString(stored_user_country_code, "NoValue")
						  +prefs.getString(stored_user_mobile_no, "NoValue")
			//			  +"918860357393"
						  +"&type=thumb";
			
			Log.d("url", url + " #");
			ResponseHandler<String> responseHandler;
			String responseBody;
			responseHandler = new BasicResponseHandler();
			
			 HttpPost httppost = new HttpPost(url);
		     List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		     nameValuePairs.add(new BasicNameValuePair("user", "1"));
		     httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			 responseBody = httpclient.execute(httppost, responseHandler);
			
			 JSONObject json = new JSONObject(responseBody);
				JSONArray response = json.getJSONArray("contacts");
				String path = json.getString("path");
				Log.d("pathtst", path + " #");
				Log.d("response", response + " #");
				phone_no = new String[response.length()];
				url_image = new String[response.length()];
				name = new String[response.length()];
				status = new String[response.length()];
				for(int i = 0 ; i < response.length() ; i++)
				{
					JSONObject position = response.getJSONObject(i);
					Log.d("position", position + " #");
					phone_no[i] = position.getString("p");
					
					String url_update = position.getString("t");
					
					if(!url_update.equals("No image")){
						url_image[i] = path + url_update;
					}else{
					url_image[i] = position.getString("t");
					}
					
					name[i] = position.getString("un");
					name[i] = Uri.decode(name[i]);
					status[i] = Uri.decode(position.getString("s"));
					
					username = position.getString("un");
					number = position.getString("p");
					user_status = position.getString("s");
					user_urlpath = url_image[i];
					
					
					Log.d(i + " phone_no  ", phone_no[i] + "   !");
					Log.d(i + " name ", name[i] + "   !");
					Log.d(i + " status ", status[i] + "   !");
					Log.d(i + " url", url_image[i] + "   !");

					dbContacts = new DBContacts(mcontext);
					
					dbContacts.openToWrite();
					
					
					
					String check = dbContacts.insert_or_update_R4Wcontact_detail_in_db(number, username, user_status, user_urlpath);
					
					if(check.equals("update")){
						updated_number.add(number);
						updated_url.add(user_urlpath);
					}
					
					//dbContacts.insert_R4Wcontact_detail_in_db(phone_no[i], name[i], status[i], url_image[i]);
				}
				
				dbContacts.close();
				return true;
		} catch (Exception t) {

			t.printStackTrace();

			return false;
		}

	
	}

	/**
     * Read XML from an input stream, storing it into the content provider.
     *
     * <p>This is where incoming data is persisted, committing the results of a sync. In order to
     * minimize (expensive) disk operations, we compare incoming data with what's already in our
     * database, and compute a merge. Only changes (insert/update/delete) will result in a database
     * write.
     *
     * <p>As an additional optimization, we use a batch operation to perform all database writes at
     * once.
     *
     * <p>Merge strategy:
     * 1. Get cursor to all items in feed<br/>
     * 2. For each item, check if it's in the incoming data.<br/>
     *    a. YES: Remove from "incoming" list. Check if data has mutated, if so, perform
     *            database UPDATE.<br/>
     *    b. NO: Schedule DELETE from database.<br/>
     * (At this point, incoming database only contains missing items.)<br/>
     * 3. For any items remaining in incoming list, ADD to database.
     */
    public void updateLocalFeedData(final InputStream stream, final SyncResult syncResult)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
    	/*
        final FeedParser feedParser = new FeedParser();
        final ContentResolver contentResolver = getContext().getContentResolver();

        Log.i(TAG, "Parsing stream as Atom feed");
        final List<FeedParser.Entry> entries = feedParser.parse(stream);
        Log.i(TAG, "Parsing complete. Found " + entries.size() + " entries");


        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        // Build hash table of incoming entries
        HashMap<String, FeedParser.Entry> entryMap = new HashMap<String, FeedParser.Entry>();
        for (FeedParser.Entry e : entries) {
            entryMap.put(e.id, e);
        }

        // Get list of all items
        Log.i(TAG, "Fetching local entries for merge");
        Uri uri = FeedContract.Entry.CONTENT_URI; // Get all entries
        Cursor c = contentResolver.query(uri, PROJECTION, null, null, null);
        assert c != null;
        Log.i(TAG, "Found " + c.getCount() + " local entries. Computing merge solution...");

        // Find stale data
        int id;
        String entryId;
        String title;
        String link;
        long published;
        while (c.moveToNext()) {
            syncResult.stats.numEntries++;
            id = c.getInt(COLUMN_ID);
            entryId = c.getString(COLUMN_ENTRY_ID);
            title = c.getString(COLUMN_TITLE);
            link = c.getString(COLUMN_LINK);
            published = c.getLong(COLUMN_PUBLISHED);
            FeedParser.Entry match = entryMap.get(entryId);
            if (match != null) {
                // Entry exists. Remove from entry map to prevent insert later.
                entryMap.remove(entryId);
                // Check to see if the entry needs to be updated
                Uri existingUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                if ((match.title != null && !match.title.equals(title)) ||
                        (match.link != null && !match.link.equals(link)) ||
                        (match.published != published)) {
                    // Update existing record
                    Log.i(TAG, "Scheduling update: " + existingUri);
                    batch.add(ContentProviderOperation.newUpdate(existingUri)
                            .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, title)
                            .withValue(FeedContract.Entry.COLUMN_NAME_LINK, link)
                            .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, published)
                            .build());
                    syncResult.stats.numUpdates++;
                } else {
                    Log.i(TAG, "No action: " + existingUri);
                }
            } else {
                // Entry doesn't exist. Remove it from the database.
                Uri deleteUri = FeedContract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();
                Log.i(TAG, "Scheduling delete: " + deleteUri);
                batch.add(ContentProviderOperation.newDelete(deleteUri).build());
                syncResult.stats.numDeletes++;
            }
        }
        c.close();

        // Add new items
        for (FeedParser.Entry e : entryMap.values()) {
            Log.i(TAG, "Scheduling insert: entry_id=" + e.id);
            batch.add(ContentProviderOperation.newInsert(FeedContract.Entry.CONTENT_URI)
                    .withValue(FeedContract.Entry.COLUMN_NAME_ENTRY_ID, e.id)
                    .withValue(FeedContract.Entry.COLUMN_NAME_TITLE, e.title)
                    .withValue(FeedContract.Entry.COLUMN_NAME_LINK, e.link)
                    .withValue(FeedContract.Entry.COLUMN_NAME_PUBLISHED, e.published)
                    .build());
            syncResult.stats.numInserts++;
        }
        Log.i(TAG, "Merge solution ready. Applying batch update");
        mContentResolver.applyBatch(FeedContract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                FeedContract.Entry.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);                         // IMPORTANT: Do not sync to network
        // This sample doesn't support uploads, but if *your* code does, make sure you set
        // syncToNetwork=false in the line above to prevent duplicate syncs.
        
        */
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    public int uploadFile(String sourceFileUri) {
	  	  String upLoadServerUri = "";
	  	  upLoadServerUri = "http://ip.roaming4world.com/esstel/fetch_contacts_upload.php";
	  	  String fileName = sourceFileUri;
	  	  Log.d("file upload url", upLoadServerUri);
	  	  HttpURLConnection conn = null;
	  	  DataOutputStream dos = null;  
	  	  String lineEnd = "\r\n";
	  	  String twoHyphens = "--";
	  	  String boundary = "*****";
	  	  int bytesRead, bytesAvailable, bufferSize;
	  	  byte[] buffer;
	  	  int maxBufferSize = 1 * 1024 * 1024; 
	  	  File sourceFile = new File(sourceFileUri); 
	  	  if (!sourceFile.isFile()) {
	  	   Log.d("uploadFile", "Source File Does not exist");
	  	   return 0;
	  	  }
		    	  int serverResponseCode = 0;
				try { // open a URL connection to the Servlet
		    	   FileInputStream fileInputStream = new FileInputStream(sourceFile);
		    	   URL url = new URL(upLoadServerUri);
		    	   conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
		    	   conn.setDoInput(true); // Allow Inputs
		    	   conn.setDoOutput(true); // Allow Outputs
		    	   conn.setUseCaches(false); // Don't use a Cached Copy
		    	   conn.setRequestMethod("POST");
		    	   conn.setRequestProperty("Connection", "Keep-Alive");
		    	   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		    	   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		    	   conn.setRequestProperty("uploaded_file", fileName);
		    	  
		    	   dos = new DataOutputStream(conn.getOutputStream());
		
		    	   dos.writeBytes(twoHyphens + boundary + lineEnd); 
		    	   dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+selfNumber+"-"+fileName + "\"" + lineEnd);
		    	   dos.writeBytes(lineEnd);
		
		    	   bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size
		
		    	   bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	   buffer = new byte[bufferSize];
		
		    	   // read file and write it into form...
		    	   bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
		    	    
		    	   while (bytesRead > 0) {
		    	     dos.write(buffer, 0, bufferSize);
		    	     bytesAvailable = fileInputStream.available();
		    	     bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	     bytesRead = fileInputStream.read(buffer, 0, bufferSize);	    	    
		    	    }
		
		    	   // send multipart form data necesssary after file data...
		    	   dos.writeBytes(lineEnd);
		    	   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
		    	   // Responses from the server (code and message)
		    	   serverResponseCode  = conn.getResponseCode();
		    	   String serverResponseMessage = conn.getResponseMessage();
		    	   Log.d("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
		    	   //close the streams //
		    	   fileInputStream.close();
		    	   dos.flush();
		    	   dos.close();
		    	   Log.d("Contact file ", "uploaded");
		      } catch (MalformedURLException ex) {  
		    	  ex.printStackTrace();
		    	  Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
	  	  } catch (Exception e) {
	  		  e.printStackTrace();
	  		  Log.e("Upload file to server Exception", "Exception : " + e.getMessage(), e);  
	  	  }
	  	  return serverResponseCode;  
	  	 
	}
 
    

}
