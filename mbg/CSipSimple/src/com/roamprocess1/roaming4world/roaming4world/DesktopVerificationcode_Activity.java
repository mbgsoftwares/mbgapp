package com.roamprocess1.roaming4world.roaming4world;



import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.roamprocess1.roaming4world.R;


public class DesktopVerificationcode_Activity extends SherlockFragmentActivity {

	MyCount timerCount;
	TextView tv_remaining_time , tv_verification_code;
	Button bt_quit;
	
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.verificationcode_desktop_app);
    initilizer();
    
    Bundle bundle = getIntent().getExtras();

    if(bundle.getString("verify_code")!= null && bundle.getString("countdown_time")!= null)
    {
        tv_verification_code.setText(bundle.getString("verify_code"));
        tv_remaining_time.setText(bundle.getString("countdown_time"));
    } 

    bt_quit.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	});
    
    timerCount = new MyCount(Integer.parseInt(bundle.getString("countdown_time")) * 1000, 1000);
    timerCount.start();
    }

  	private void initilizer() {
		// TODO Auto-generated method stub
		tv_remaining_time = (TextView) findViewById(R.id.tv_verificationtime_desktop_app);
		tv_verification_code = (TextView) findViewById(R.id.tv_verificationcode_desktop_app);
		bt_quit = (Button) findViewById(R.id.btn_verification_activity_quit);
	}
	
	public class MyCount extends CountDownTimer {
      
		public MyCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        
      }

      @Override
      public void onFinish() {
        //some script here
    	  finish();
      }

      @Override
      public void onTick(long millisUntilFinished) {
        //some script here 
    	  int remainingtime = Integer.parseInt(tv_remaining_time.getText().toString());
          tv_remaining_time.setText(remainingtime - 1 + "");
      }   
    } 
}