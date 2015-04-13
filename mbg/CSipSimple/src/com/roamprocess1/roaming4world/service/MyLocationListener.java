package com.roamprocess1.roaming4world.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener  implements LocationListener{

	Location loc;
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		loc = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public Location getLocation(){
		return loc;
	}
	
}
