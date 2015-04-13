package com.roamprocess1.roaming4world.stripepayment;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
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

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class PaymentActivity extends FragmentActivity {

    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://manage.stripe.com/account/apikeys
     */
    public static final String PUBLISHABLE_KEY = "pk_test_uEfAjN8yMIbPU02wvV6aJkiK";
    public String Mytokenid="",stored_user_mobile_no,stored_user_country_code,number,rechargeValue;
    public SharedPreferences prefs;
    String resultPayment;
    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    	prefs = this.getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
    	stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
		stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
        
		number = prefs.getString(stored_user_country_code, "")+ prefs.getString(stored_user_mobile_no, "");
		System.out.println("UserNumber:"+number);
		
	   	Bundle extras =this.getIntent().getExtras();
        rechargeValue = extras.getString("paymentValue");
        System.out.println("rechargeValue:::: in payment Activity:"+rechargeValue+"EmailId:"+PaymentFormFragment.emailId);
        
        //System.out.println("rechargeValue::::"+rechargeValue +"Email Id:"+edtEmaildId.getText());
		
    	ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	    ab.setHomeButtonEnabled(true);
	    ab.setDisplayShowHomeEnabled(false);
	    ab.setDisplayShowTitleEnabled(false);
		ab.setCustomView(R.layout.r4wactionbarcustom);
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		LinearLayout ll_header_finish=(LinearLayout)ab.getCustomView().findViewById(R.id.ll_header_finish);
		
		ll_header_finish.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PaymentActivity.this.finish();
			}
		});
    }

    public void saveCreditCard(PaymentForm form) {

        Card card = new Card(
                form.getCardNumber(),
                form.getExpMonth(),
                form.getExpYear(),
                form.getCvc()
                	
        		
        		);

        System.out.println("Card value :"+card);
        System.out.println("Email Id:"+PaymentFormFragment.emailId);
        
        boolean validation = card.validateCard();
        if (validation) {
            startProgress();
            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                    public void onSuccess(Token token) {
                    	 getTokenList().addToList(token);
                    	
                    	Mytokenid=token.getId()+"";
                    	new MyAsyncTask_offMessageAcknowledgement().execute();
                        finishProgress();
                        }
                    public void onError(Exception error) {
                            handleError(error.getLocalizedMessage());
                            finishProgress();
                        }
                    });
        } else if (!card.validateNumber()) {
        	handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
        	handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
        	handleError("The CVC code that you entered is invalid");
        } else {
        	handleError("The card details that you entered are invalid");
        }
    }

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }

    private TokenList getTokenList() {
        return (TokenList)(getSupportFragmentManager().findFragmentById(R.id.token_list));
    }
    


private class MyAsyncTask_offMessageAcknowledgement extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected void onPostExecute(Boolean result) {
			
			if(resultPayment.equals("success")){
			Toast.makeText(getApplicationContext(), "Recharge successfully ", Toast.LENGTH_LONG).show();
			//SipHome siphome= new SipHome();	
			//siphome.new MyAsyncTaskGetBalance().execute();
		    finish();
			}else
					Toast.makeText(getApplicationContext(), "Invalid card number ", Toast.LENGTH_LONG).show();
			
		}

		@Override
		protected void onPreExecute() {}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (webserviceAcknowledgement()) {
				Log.d("return", "SUCCESS");
				return true;
			} else {
				Log.d("return", "ERROR");
				return false;
			}
		}
	}

public boolean webserviceAcknowledgement() {
	try {
		HttpParams p = new BasicHttpParams();
		p.setParameter("user", "1");
		HttpClient httpclient = new DefaultHttpClient(p);
	
		
		String	url = "https://ip.roaming4world.com/esstel/sprite_payment.php?stripeToken="
					+Mytokenid
					+"&stripeEmail="+PaymentFormFragment.emailId
					+"&final_amt="+rechargeValue
					+"&user_no="+number;
	

		System.out.println("Paymenturl:"+url);
		HttpPost httppost = new HttpPost(url);
		System.out.println("payment url:"+url);
		try {
			Log.i(getClass().getSimpleName(), "send  task - start");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("user", "1"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			String responseBody = httpclient.execute(httppost,responseHandler);
			JSONObject jsonObject= new JSONObject(responseBody);
			resultPayment=jsonObject.getString("response");
			 System.out.println("result:"+resultPayment);
									
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	} catch (Exception t) {
		
		t.printStackTrace();
		return false;
	}
}
    
    
}



