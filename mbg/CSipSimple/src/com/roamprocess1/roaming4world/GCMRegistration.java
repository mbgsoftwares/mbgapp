/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.roamprocess1.roaming4world;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Main UI for the demo app.
 */
public class GCMRegistration {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "658327371868",stored_user_mobile_no,User_mobile_no,stored_user_country_code,user_countrycode,selfNumber;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCM Demo";
    SharedPreferences prefs;
 //   TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context;

    String regid;
    

  
    public GCMRegistration(Context activityContext) {
        //Remove title bar
    //    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
   //     this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    //    setContentView(R.layout.gcmmain);
    //    mDisplay = (TextView) findViewById(R.id.display);

        context = activityContext ;
        
        prefs = context.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
    	stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
    	User_mobile_no = prefs.getString(stored_user_mobile_no, "no");
    	stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
    	user_countrycode=prefs.getString(stored_user_country_code, "");
    	System.out.println("User_mobile_no =="+User_mobile_no);
    	selfNumber=user_countrycode+User_mobile_no;
    	System.out.println("DemoActivity :self number:"+selfNumber);

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
            else{
            	/*
            	Intent intent = new Intent(context, SipHome.class);
            	context.startActivity(intent);
    			finish();
    			*/
            }
        } else {
        	/*
            Log.i(TAG, "No valid Google Play Services APK found.");
            Intent intent = new Intent(GCMRegistration.this, SipHome.class);
			startActivity(intent);
			finish();
			*/
        }
        
        checkPlayServices();
        onClick();
    }

        /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            //    GooglePlayServicesUtil.getErrorDialog(resultCode, context,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    System.out.println("registraton DemoActivity: regid :"+regid);
                    
                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend(regid);

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
     //           mDisplay.append(msg + "\n");
                
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        String msg = "";
                        try {
                            Bundle data = new Bundle();
                            data.putString("my_message", "Hello World");
                            data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                            String id = Integer.toString(msgId.incrementAndGet());
                            gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                            msg = "Sent message";
                        } catch (IOException ex) {
                            msg = "Error :" + ex.getMessage();
                        }
                        return msg;
                    }

                    @Override
                    protected void onPostExecute(String msg) {
         //               mDisplay.append(msg + "\n");
                    	/*
                        Intent intent = new Intent(GCMRegistration.this, SipHome.class);
            			startActivity(intent);
            			finish();
            			*/
                    }
                }.execute(null, null, null);
            }
        }.execute(null, null, null);
    }

    // Send an upstream message.
    public void onClick() {

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        Bundle data = new Bundle();
                        data.putString("my_message", "Hello World");
                        data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
                        String id = Integer.toString(msgId.incrementAndGet());
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                        msg = "Sent message";
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(String msg) {
       //             mDisplay.append(msg + "\n");
                }
            }.execute(null, null, null);
    }

     /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(GCMRegistration.class.getSimpleName(),Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend(String registrationId) {
      // Your implementation here.
    	System.out.println("sendRegistrationIdToBackend :"+registrationId);
    	ServerUtilities.register(context, selfNumber, "testing@gmail.com", registrationId);
    	 
    	 
    	 
//    	 URI url = null;
//    	  try {
//    	   url = new URI("http://10.0.2.2/GCMDemo/register.php?regId=" + regid);
//    	  } catch (URISyntaxException e) {
//    	   // TODO Auto-generated catch block
//    	   e.printStackTrace();
//    	  } 
//    	  HttpClient httpclient = new DefaultHttpClient();
//    	  HttpGet request = new HttpGet();
//    	  request.setURI(url);
//    	  try {
//    	   httpclient.execute(request);
//    	  } catch (ClientProtocolException e) {
//    	   // TODO Auto-generated catch block
//    	   e.printStackTrace();
//    	  } catch (IOException e) {
//    	   // TODO Auto-generated catch block
//    	   e.printStackTrace();
//    	  }
//    	
    
    }
}