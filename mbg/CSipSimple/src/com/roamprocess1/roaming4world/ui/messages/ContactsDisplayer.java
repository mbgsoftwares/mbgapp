package com.roamprocess1.roaming4world.ui.messages;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.api.SipProfile;
import com.roamprocess1.roaming4world.widgets.EditSipUriContactsFragment;
import com.roamprocess1.roaming4world.widgets.EditSipUriContactsFragment.ToCall;
import com.roamprocess1.roaming4world.wizards.NoNetwork;

public class ContactsDisplayer extends SherlockFragment implements OnClickListener {

	private EditSipUriContactsFragment sipUri;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("PickupSipUri.java inonCreate() befor setting layout");
		View rootView=null;
		ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			rootView = inflater.inflate(R.layout.pickup_uri_contacts_fragment, container, false);
			System.out.println("PickupSipUri.java inonCreate() after setting layout");
			
			//Set window size
//			LayoutParams params = getWindow().getAttributes();
//			params.width = LayoutParams.FILL_PARENT;
//			getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
			
			//Set title
			// TODO -- use dialog instead
//			((TextView) findViewById(R.id.my_title)).setText(R.string.pickup_sip_uri);
//			((ImageView) findViewById(R.id.my_icon)).setImageResource(android.R.drawable.ic_menu_call);
			
			
			
			sipUri = (EditSipUriContactsFragment) rootView.findViewById(R.id.sip_uri);
			sipUri.getTextField().setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView tv, int action, KeyEvent arg2) {
					System.out.println("PickupSipUri.java inonCreate() in onEditorAction()");
					if(action == EditorInfo.IME_ACTION_GO) {
						System.out.println("PickupSipUri.java inonCreate() in Before sendPositiveResult()");
						sendPositiveResult();
						return true;
					}
					return false;
				}
			});
			sipUri.setShowExternals(false);
			
	    }
	    else
	    {
	    	Intent intent=new Intent(getActivity(),NoNetwork.class);
			startActivity(intent);
	    }
		
		return rootView;
	}
	
	
	

	private void sendPositiveResult() {
		System.out.println("PickupSipUri.java in sendPositiveResult");
		Intent resultValue = new Intent();
		 ToCall result = sipUri.getValue();
		 System.out.println("PickupSipUri.java in sendPositiveResult result value is "+result);
		 if(result != null) {
		     // Restore existing extras.
		     Intent it = getActivity().getIntent();
		     if(it != null) {
		         Bundle b = it.getExtras();
		         if(b != null) {
		             resultValue.putExtras(b);
		         }
		     }
			 resultValue.putExtra(Intent.EXTRA_PHONE_NUMBER,
						result.getCallee());
			 resultValue.putExtra(SipProfile.FIELD_ID,
						result.getAccountId());
			 getActivity().setResult(FragmentActivity.RESULT_OK, resultValue);
		 }else {
			 System.out.println("PickupSipUri.java in sendPositiveResult else part");
			getActivity().setResult(FragmentActivity.RESULT_CANCELED);
		 }
		getActivity().finish();
	}




	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
