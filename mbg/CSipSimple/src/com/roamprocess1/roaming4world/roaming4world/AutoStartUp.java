package com.roamprocess1.roaming4world.roaming4world;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartUp extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent intent1 = new Intent(context, MyBroadcastReceiver.class);
        Log.d("in auto startup","yes");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        long recurring=(30*60000);
        Log.d("auto starting","yes");
        alarmManager.setRepeating(AlarmManager.RTC, Calendar.getInstance().getTimeInMillis(),recurring,pendingIntent);
        Log.d("auto start complete","yes");
	}

}
