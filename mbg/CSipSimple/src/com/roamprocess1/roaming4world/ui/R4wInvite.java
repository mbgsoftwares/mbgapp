package com.roamprocess1.roaming4world.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.utils.Compatibility;
import com.roamprocess1.roaming4world.utils.Log;

public class R4wInvite extends SherlockFragmentActivity {
	
	private static String TAG="R4wInvite";
	private Button btnR4WFrdInvite,btnR4WOutCalls,btnMbtelcomout;
	private TextView txtInviteNumber,txtInviteName,txtphNumber;
	public SharedPreferences prefs;
    private PopupWindow selectWindow = null;
    private LinearLayout linerOutCall;
    
	private String stored_user_country_code,user_countrycode,r4wInviteNumber,stored_user_mobile_no,User_mobile_no, stored_user_bal, stored_min_call_credit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r4wfriendinvite);
		prefs = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
		user_countrycode=prefs.getString(stored_user_country_code, "");
		stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		User_mobile_no = prefs.getString(stored_user_mobile_no, "no");
		Log.d(TAG,"User_mobile_no:"+User_mobile_no);
		
		stored_user_bal = "com.roamprocess1.roaming4world.user_bal";
		stored_min_call_credit = "com.roamprocess1.roaming4world.min_call_credit";
	
		
		linerOutCall=(LinearLayout) findViewById(R.id.linerOutCall);
		btnR4WFrdInvite= (Button)findViewById(R.id.btnR4WFrdInvite);
		//btnR4WOutCalls=(Button)findViewById(R.id.btnR4WFrdoutCalls);
		txtInviteName= (TextView)findViewById(R.id.txtInviteName);
		txtInviteNumber= (TextView)findViewById(R.id.txtfrdInviteNumber);
		txtphNumber=(TextView)findViewById(R.id.invitePhNumber);
		
		//btnMbtelcomout=(Button)findViewById(R.id.btnMbtelcomout);
		

	    
		setActionBar();
		Bundle bundle = getIntent().getExtras();
		   
	     if(bundle!=null){
	    	 r4wInviteNumber= bundle.getString("InviteNumber");
	    	 String r4wInviteName= bundle.getString("InviteName");
	    	 Log.d(TAG,"$$$ r4wInviteNumber :r4wInviteName"+r4wInviteNumber+":"+r4wInviteName+"");
	    	 System.out.println("r4wInviteNumber:"+r4wInviteNumber);
	    	 if(r4wInviteNumber.equals(r4wInviteName)){
	    		 
	    		 System.out.println("Invite Number :"+r4wInviteNumber);
	    		 txtInviteName.setText(r4wInviteNumber);
	    	 }else{
	    		 txtInviteNumber.setText(r4wInviteNumber);
	    		 if(r4wInviteName.length()>14)
	    		 txtInviteName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
	    		 
	    		 txtInviteName.setText(r4wInviteName);
	    	 }
	    	
	    	 txtphNumber.setText(r4wInviteNumber);
	    
	     }
	   
	     btnR4WFrdInvite.setOnClickListener(new View.OnClickListener() {
	    	 @Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		
			
				if ( !r4wInviteNumber.startsWith("*")&& !r4wInviteNumber.startsWith("#")) {
					
					if (r4wInviteNumber.startsWith("+")) {
						r4wInviteNumber=r4wInviteNumber.substring(1);
					} else if (r4wInviteNumber.startsWith("00")) {
						String modify_contact_no = r4wInviteNumber.substring(2);
						modify_contact_no = user_countrycode+ modify_contact_no;
						r4wInviteNumber=modify_contact_no.toString();
					
						
					} else if (r4wInviteNumber.startsWith("0")) {
						String modify_contact_no = r4wInviteNumber.substring(1);
						modify_contact_no = user_countrycode+ modify_contact_no;
						r4wInviteNumber= modify_contact_no.toString();
						
						
					} else if (!r4wInviteNumber.startsWith("+") && !r4wInviteNumber.startsWith("0")) {
						r4wInviteNumber=user_countrycode+ r4wInviteNumber.toString();
						
					}
				}
				
				
				System.out.println("final r4wInviteNumber:"+r4wInviteNumber);
				r4wInviteNumber = r4wInviteNumber.replaceAll("[-+^]*", "");
				System.out.println("final r4wInviteNumber:"+r4wInviteNumber);
				
			new AsynctaskInvite().execute();
				
			}
		});
	   
	     linerOutCall.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String bal = prefs.getString(stored_user_bal, "0");
				String min = prefs.getString(stored_min_call_credit, "0");
				double bala , minimun;
				bala = Double.parseDouble(bal) *100;
				minimun = Double.parseDouble(min);
				
				Log.d("baal", bala + " !");
				Log.d("minn", minimun + " !");			
			
				
				if(bala > minimun){
					r4wOutCall();
				}else{
					dialogboxR4WOut(R4wInvite.this);
						
				}
				
			}
		});  
	     
	     /*
	     btnMbtelcomout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogboxR4WOut();
				//r4wOutCall();
			}
		});*/
	     
	     /*
	     linerGSMCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				callGSM(r4wInviteNumber);
			}
		});  
		*/
	}
	
	public void callImageActivity(View v){
		
	}
	
	public String InviteContact(String inviteNumber) {
	
		return "number";
	}
	
	public  void callGSM(String gsmCallingNumber) {
		Intent phoneIntent = new Intent(Intent.ACTION_CALL);
	    String n = gsmCallingNumber;
	      if(gsmCallingNumber.contains("@")){
	    	  String[] arr = gsmCallingNumber.split("@");
	    	  n = arr[0];
	    	  if(n.contains(":")){
	    		  String[] arre = n.split(":");
	    		  n = arre[1];
	    	  }
	      }
	      phoneIntent.setData(Uri.parse("tel:+" + n));
	      try {
	         startActivity(phoneIntent);
	         this.finish();
	      } catch (android.content.ActivityNotFoundException ex) {
	         Toast.makeText(getApplicationContext(),"Call faild, please try again later.", Toast.LENGTH_SHORT).show();
	      }
	}
	
	
	@SuppressLint("NewApi")
	private void setActionBar() {
	ActionBar ab = getActionBar();
	ab.setDisplayHomeAsUpEnabled(true);
    ab.setHomeButtonEnabled(true);
    ab.setDisplayShowHomeEnabled(false);
    ab.setDisplayShowTitleEnabled(false);
	ab.setCustomView(R.layout.r4wactionbarcustom);
	ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
	
	
	LinearLayout fin = (LinearLayout) ab.getCustomView().findViewById(R.id.ll_header_finish);
	fin.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	});
	
	}
	
	public class AsynctaskInvite extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			System.out.println("onPreExecute preExcute");
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result == true) {

			//	Toast.makeText(getApplicationContext(),"Invitation sent" + r4wInviteNumber, Toast.LENGTH_SHORT).show();
				Log.d(TAG, "sent invite successfully");

			} else {
				Log.d(TAG, "not send invite");
			}
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub

			Log.d("doInBackgroud", "doInBackground");
			if (webServiceInvite()) {
				return true;
			} else {
				return false;
			}

		}

		public boolean webServiceInvite() {

			// TODO Auto-generated method stub
			try {

				HttpParams p = new BasicHttpParams();
				p.setParameter("user", "1");
				android.util.Log.d(TAG, "r4wInviteNumber" + r4wInviteNumber);
				HttpClient httpclient = new DefaultHttpClient(p);
				// https://ip.roaming4world.com/esstel/subscriber_invite.php?self_contact=4545&invite_number=4545
				String url = "http://ip.roaming4world.com/esstel/subscriber_invite.php?"
						+ "self_contact="
						+ user_countrycode
						+ User_mobile_no
						+ "&invite_number="
						+ r4wInviteNumber.replaceAll("[-+.^:,]", "")
								.replaceAll("\\s+", "") + "&type=r4w";

				System.out.println("invite url:" + url);
				HttpPost httppost = new HttpPost(url);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("user", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,
						responseHandler);
				JSONObject jsonObjRecv = new JSONObject(responseBody);
				String returnMessage = jsonObjRecv.getString("response");
				System.out.println("Response Body is " + responseBody + ":"
						+ returnMessage);

				if (returnMessage.equalsIgnoreCase("Invitation sent")) {
					return true;
				} else {
					return false;
				}

			} catch (Throwable t) {

				t.printStackTrace();

				return false;
			}

		}
	}
	 
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        if (item.getItemId() == Compatibility.getHomeMenuId()) {
	            finish();
	            return true;
	        }
	        
	        return super.onOptionsItemSelected(item);
	    }
	 
		
		private void r4wOutCall(){
			
			if ( !r4wInviteNumber.startsWith("*")&& !r4wInviteNumber.startsWith("#")) {
				if (r4wInviteNumber.startsWith("+")) {
					r4wInviteNumber=r4wInviteNumber.substring(1);
				}else if(r4wInviteNumber.startsWith(user_countrycode)){
					
				}
				else if (r4wInviteNumber.startsWith("00")) {
					String modify_contact_no = r4wInviteNumber.substring(2);
					modify_contact_no = user_countrycode+ modify_contact_no;
					r4wInviteNumber=modify_contact_no.toString();
				
					
				} else if (r4wInviteNumber.startsWith("0")) {
					String modify_contact_no = r4wInviteNumber.substring(1);
					modify_contact_no = user_countrycode+ modify_contact_no;
					r4wInviteNumber= modify_contact_no.toString();
					
					
				} else if (!r4wInviteNumber.startsWith("+") && !r4wInviteNumber.startsWith("0")) {
					r4wInviteNumber=user_countrycode+ r4wInviteNumber.toString();
					
				}
			}
			String r4woutCallingNumber="";
			r4woutCallingNumber="011"+r4wInviteNumber;
			Log.d(TAG,"OutCalling number:"+r4woutCallingNumber);
			RContactlist rContactlist= new RContactlist();
			rContactlist.r4wCall_Chat(r4woutCallingNumber);
		}
		
		private void hideWindow() {
			if (selectWindow != null){
				selectWindow.dismiss();
			}
	   }
 
		public void dialogboxR4WOut(Context context) {
		
			final Dialog dialogR4WOut = new Dialog(context);
			//dialogMBTelcomOut.setTitle("MBTelcom Out Credit");
			dialogR4WOut.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogR4WOut.setContentView(R.layout.r4wout);
			dialogR4WOut.show();
			Button btn$10 = (Button) dialogR4WOut.findViewById(R.id.btn10);
			Button btn$20 = (Button) dialogR4WOut.findViewById(R.id.btn20);
			Button btn$30 = (Button) dialogR4WOut.findViewById(R.id.btn30);
			Button btnGetRates = (Button) dialogR4WOut.findViewById(R.id.btngetrates);
			Button btnMaybeLater = (Button) dialogR4WOut.findViewById(R.id.btnmaybelater);
		
			btnMaybeLater.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogR4WOut.dismiss();
				}
			});
			

		
		}
	}
