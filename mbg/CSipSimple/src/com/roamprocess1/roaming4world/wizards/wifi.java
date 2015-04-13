package com.roamprocess1.roaming4world.wizards;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.roamprocess1.roaming4world.R;

public class wifi extends Activity {

	View rootView;
	String[] s = new String[20];
	String q;
	String networkSSID;

	WifiManager mainWifiObj;
	WifiScanReceiver wifiReciever;
	WifiConfiguration config;
	String wifis[], wifiConfigArray[], signallevel[], wifiSSID[],
			wifiSecurity[];
	String openwifi[], opensignallevel[], openwifiSSID[], openwifiSecurity[];

	String openwifis[], allOpenwifi[];
	Button allWifi, openWifi;
	LinearLayout wifiscanlist, noOpenwifi;
	String ssidofopenwifi;
	static String wifiSecurityMode[];
	static boolean wifiMode[];
	private int wifiModeScan = 2;

	int arraylengthopen;
	static int checkwifiopen = 0;
	public static final String PSK = "PSK";
	public static final String WEP = "WEP";
	public static final String EAP = "EAP";
	public static final String OPEN = "Open";
	private static final String ENTERPRISE_CAPABILITY = "-EAP-";
	ListView wifiscanlistall;
	String checkwifimode;
	wifiscanadapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r4wwifiscanner);
		// TODO Auto-generated method stub

		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		allWifi = (Button) findViewById(R.id.btAll_wifi_Scan);
		openWifi = (Button) findViewById(R.id.btopenwifi);
		wifiscanlist = (LinearLayout) findViewById(R.id.llwifiscanlist);
		noOpenwifi = (LinearLayout) findViewById(R.id.llNoOpenWifi);
		wifiscanlistall = (ListView) findViewById(R.id.lvwifiscan);

		Log.d("wifiscanlistall", "0");

		Log.d("wifiscanlistall", "1");
		allWifi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wifiscanlist.setVisibility(LinearLayout.VISIBLE);
				noOpenwifi.setVisibility(LinearLayout.GONE);
				wifiModeScan = 0;
				checkwifi();
				getopenwifi();
				Log.d("testinglist", "000");

			}
		});

		openWifi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkwifiopen = 0;
				noOpenwifi.setVisibility(LinearLayout.VISIBLE);
				wifiscanlist.setVisibility(LinearLayout.GONE);

				wifiModeScan = 1;
				checkwifi();
				getopenwifi();

			}
		});

	}

	private void checkwifi() {

		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		Log.d("wifienable", String.valueOf(wifi.isWifiEnabled()));
		if (wifi.isWifiEnabled()) {
			// wifi is enabled
			Log.d("testq", "00");
			startWifiScan();
			register();

			wifiscanlistall
					.setOnItemClickListener(new AdapterView.OnItemClickListener() {

						public void onItemClick(AdapterView<?> parentAdapter,
								View view, int position, long id) {

							if (wifiModeScan == 1) {
								ssidofopenwifi = (String) parentAdapter
										.getItemAtPosition(position);
								Log.d("ssidofopenwifi", ssidofopenwifi + " ");
								startConnectivity(position);
							} else {
								startConnectivity(position);
							}

						}

					});

		} else {
			Toast.makeText(getApplicationContext().getApplicationContext(),
					"Turn On your Wi-Fi", Toast.LENGTH_SHORT).show();
			/*
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			*/
		}

	}

	private void getopenwifi() {

		
		try {
			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
			

			int j = 0, k = 0;
			for (int i = 0; i <= wifiScanList.size(); i++) {
				try {

					int level = WifiManager.calculateSignalLevel(mainWifiObj
							.getConnectionInfo().getRssi(),
							wifiScanList.get(i).level);
					int difference = level * 100 / wifiScanList.get(i).level;
					if (difference >= 100)
						signallevel[i] = "Excellent";
					else if (difference >= 75)
						signallevel[i] = "Very Good";
					else if (difference >= 50)
						signallevel[i] = "Good";
					else if (difference >= 25)
						signallevel[i] = "Poor";
				} catch (Exception e) {

				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		checkwifi();

	}

	protected void startConnectivity(int position) {
		// TODO Auto-generated method stub
		String mode;
		List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
		mode = wifiSecurityMode[position];
		if (wifiModeScan == 1) {

			connect(ssidofopenwifi);

		} else {

			if (mode.equals(OPEN)) {
				connect(wifiScanList.get(position).SSID);

			} else {
				Toast.makeText(getApplicationContext(),
						"This Network is Secured", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void startWifiScan() {
		mainWifiObj = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		wifiReciever = new WifiScanReceiver();
		mainWifiObj.startScan();

	}

	private void register() {
		this.registerReceiver(wifiReciever, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	}

	private void unRegister() {
		this.unregisterReceiver(wifiReciever);
	}

	public static String getScanResultSecurity(ScanResult scanResult) {
		final String cap = scanResult.capabilities;
		final String[] securityModes = { WEP, PSK, EAP };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				wifiSecurityMode[i] = securityModes[i];
				return "Secured-" + securityModes[i];
			}
		}
		return OPEN;
	}

	public static String getScanOpenwifi(ScanResult scanResult) {
		Log.d("getScanOpenwifi", "called");
		final String cap = scanResult.capabilities;
		int modewifi = 0;
		final String[] securityModes = { WEP, PSK, EAP };
		for (int i = securityModes.length - 1; i >= 0; i--) {
			if (cap.contains(securityModes[i])) {
				Log.d("modewifi", modewifi + "");
				modewifi = 1;
				checkwifiopen++;
			}

		}

		if (modewifi == 0)
			return OPEN;

		else
			return "Secured";
	}

	public static boolean isEnterprise(ScanResult scanResult) {
		return scanResult.capabilities.contains(ENTERPRISE_CAPABILITY);
	}

	private void connect(String wifissid) {
		networkSSID = wifissid;
		config = new WifiConfiguration();
		config.SSID = "\"" + networkSSID + "\"";
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		mainWifiObj = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		mainWifiObj.addNetwork(config);

		boolean to = false;
		Toast.makeText(this, "Network is Connecting with " + wifissid + " .",
				Toast.LENGTH_SHORT).show();

		List<WifiConfiguration> list = mainWifiObj.getConfiguredNetworks();
		for (WifiConfiguration i : list) {
			if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
				mainWifiObj.disconnect();
				mainWifiObj.enableNetwork(i.networkId, true);
				to = mainWifiObj.reconnect();

				break;
			}
		}
		if (to)
			Toast.makeText(this,
					"Network is Connected with " + wifissid + " .",
					Toast.LENGTH_LONG).show();

	}

	private class WifiScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub

			List<ScanResult> wifiScanList = mainWifiObj.getScanResults();

			wifis = new String[wifiScanList.size()];
			wifiSecurityMode = new String[wifiScanList.size()];
			signallevel = new String[wifiScanList.size()];
			wifiMode = new boolean[wifiScanList.size()];
			wifiSSID = new String[wifiScanList.size()];
			openwifis = new String[wifiScanList.size()];
			wifiSecurity = new String[wifiScanList.size()];

			openwifiSSID = new String[wifiScanList.size()];
			openwifiSecurity = new String[wifiScanList.size()];
			opensignallevel = new String[wifiScanList.size()];

			int j = 0, k = 0;
			if (wifiModeScan == 0) {
				for (int i = 0; i < wifiScanList.size(); i++) {
					Log.d("wifiModeScan", wifiModeScan + "");
					wifis[i] = ((wifiScanList.get(i)).toString());
					wifiSecurityMode[i] = getScanResultSecurity(wifiScanList
							.get(i));
					int level = WifiManager.calculateSignalLevel(mainWifiObj
							.getConnectionInfo().getRssi(),
							wifiScanList.get(i).level);
					int difference = level * 100 / wifiScanList.get(i).level;
					if (difference >= 100)
						signallevel[i] = "Excellent";
					else if (difference >= 75)
						signallevel[i] = "Very Good";
					else if (difference >= 50)
						signallevel[i] = "Good";
					else if (difference >= 25)
						signallevel[i] = "Poor";

					wifiSSID[i] = wifiScanList.get(i).SSID;
					wifiSecurity[i] = getScanResultSecurity(wifiScanList.get(i));
					wifis[i] = "Name : " + wifiScanList.get(i).SSID + "\n"
							+ " Mode : "
							+ getScanResultSecurity(wifiScanList.get(i))
							+ "\nStregth : " + signallevel[i];
				}

				adapter = new wifiscanadapter(getApplicationContext(),
						wifiSSID, signallevel, wifiSecurity, 0);
				wifiscanlistall.setAdapter(adapter);

			} else if (wifiModeScan == 1) {
				Log.d("wifiModeScan", wifiModeScan + "");
				try {

					arraylengthopen = 0;
					for (int i = 0; i < wifiScanList.size(); i++) {

						wifis[i] = ((wifiScanList.get(i)).toString());

						checkwifimode = getScanOpenwifi(wifiScanList.get(i));

						int qq = 0;
						if (checkwifimode == OPEN) {
							arraylengthopen++;
							int level = WifiManager.calculateSignalLevel(
									mainWifiObj.getConnectionInfo().getRssi(),
									wifiScanList.get(i).level);
							int difference = level * 100
									/ wifiScanList.get(i).level;
							if (difference >= 100)
								opensignallevel[qq] = "Excellent";
							else if (difference >= 75)
								opensignallevel[qq] = "Very Good";
							else if (difference >= 50)
								opensignallevel[qq] = "Good";
							else if (difference >= 25)
								opensignallevel[qq] = "Poor";

							openwifiSSID[qq] = wifiScanList.get(i).SSID;
							openwifiSecurity[qq] = getScanOpenwifi(wifiScanList
									.get(i));
							openwifis[i] = "Name : " + wifiScanList.get(i).SSID;
							wifiscanlist.setVisibility(LinearLayout.VISIBLE);
							noOpenwifi.setVisibility(LinearLayout.GONE);
							qq++;
						} else if (checkwifimode == "Secured") {

							// openwifis[i] = "No Open Wifi";

						}
						checkwifimode = "Secured";

					}
				} catch (Exception e) {

					e.printStackTrace();
					// openwifis[0] = "No Open Wifi";

				}
				if (checkwifiopen == 0) {
					wifiscanlist.setVisibility(LinearLayout.GONE);
					noOpenwifi.setVisibility(LinearLayout.VISIBLE);
				} else {
					adapter = new wifiscanadapter(getApplicationContext(),
							openwifiSSID, opensignallevel, openwifiSecurity, 1);
					wifiscanlistall.setAdapter(adapter);
				}

			}
		}
	}

}
