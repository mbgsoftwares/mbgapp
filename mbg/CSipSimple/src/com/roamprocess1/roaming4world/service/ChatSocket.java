package com.roamprocess1.roaming4world.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;


public class ChatSocket extends Service{

	static public Socket socket;
	private static final int SERVERPORT = 5003;
	private static final String SERVER_IP = "74.86.100.192";
	BroadcastReceiver broadcastReceiver_lost, broadcastReceiver_connect;
	static public PrintStream ps;
	private SharedPreferences prefs;
	private String stored_user_mobile_no, stored_user_country_code , stored_chatSocket_connect;
	static public InputStream inputStream;
	static public String serverResponse = "";
	static public String USER_STATUS = "user_status";
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		super.onStartCommand(intent, flags, startId);
		
        prefs = getSharedPreferences("com.roamprocess1.roaming4world", Context.MODE_PRIVATE);		
        stored_user_mobile_no = "com.roamprocess1.roaming4world.user_mobile_no";
        stored_user_country_code = "com.roamprocess1.roaming4world.user_country_code";
        stored_chatSocket_connect = "com.roamprocess1.roaming4world.chatSocket_connect";
        Log.d("ChatSocket onStartCommand() ", "called");
        new AsyncTaskStartSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        
		broadcastReceiver_lost = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d("Call", "closeSocket");
		    	closeSocket();
			}
		};
		
		broadcastReceiver_connect = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d("Call", "initSocket");
				new AsyncTaskStartSocket().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		};
		
		
	    registerReceiver(broadcastReceiver_lost, new IntentFilter("INTERNET_LOST"));
	    registerReceiver(broadcastReceiver_connect, new IntentFilter("INTERNET_CONNECTED"));
	    return Service.START_NOT_STICKY;
	
	}
	
	
	public boolean initSocket() {
		boolean con = prefs.getBoolean(stored_chatSocket_connect, false);
		Log.d("initSocket", con + " @");
		if (con) {
			InetAddress serverAddr;
			try {
				serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);
				Log.d("Socket", socket + " !");
				socket.setKeepAlive(true);
				inputStream = socket.getInputStream();
				ps = new PrintStream(socket.getOutputStream());
				String number = prefs.getString(stored_user_country_code, "")
						+ prefs.getString(stored_user_mobile_no, "") + "-onl";
				if (!number.equals("")) {
					ps.print(number);
					Log.d("print", number);
				}
				return true;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public Socket getSocket(){
		return socket;
	}
	
	public InputStream getInputStream(){
		return inputStream;
	}

	private void startSocket() {
		// TODO Auto-generated method stub
		String number = prefs.getString(stored_user_country_code, "")
				+ prefs.getString(stored_user_mobile_no, "");
		try {
			if (ChatSocket.socket != null && ChatSocket.socket.isClosed() == false) {
				byte buffer[] = new byte[1024 * 4];
				int temp = 0, i = 0, j = 0;

				String res = null;
				while ((temp = ChatSocket.inputStream.read(buffer)) != -1) {
					serverResponse = new String(buffer, 0, temp);
					Log.d("response ChatSocket", serverResponse + " @");

					if (serverResponse.equals("alive") || serverResponse.equals("err")) {
						ChatSocket.ps.print(number + "-" + number + "-yes");
					}else{
						Intent intent = new Intent(USER_STATUS);
						intent.putExtra(USER_STATUS, serverResponse);
						sendBroadcast(intent);
					}
					res += new String(buffer, 0, temp);
					i++;
				}
				Log.d("response ChatSocket ", res + " !");
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		} 

	}
	
	public void closeSocket(){
		try {
			if(socket != null)
				{
				  
				    try {
				//	    ps.print("cl");
					    Log.d("Socket", " @");
						
					} catch (Exception e) {
						// TODO: handle exception
						Log.d("Exception", "Exception " + e.getMessage());
					}
				    Log.d("Socket", "Close " + socket.isClosed());
					Log.d("Socket", "Connected " + socket.isConnected());
		//		    socket.shutdownInput();
		//		    socket.shutdownOutput();
					socket.close();
					
					
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class AsyncTaskStartSocket extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d("AsyncTaskStartSocket", "onPreExecute" );
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			boolean flag = false;
			try {
				flag = initSocket();
				startSocket();
			} catch (Exception e) {
				// TODO: handle exception
				flag = false;
			}
			return flag;
			
		}
		
		 protected void onPostExecute(Boolean result) {
	     }


	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			unregisterReceiver(broadcastReceiver_connect);
			unregisterReceiver(broadcastReceiver_lost);
			stopSelf();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
