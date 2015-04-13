package com.roamprocess1.roaming4world.api;

import android.app.Application;

public class CommonVariable extends Application {

	  private String registrationStatus="offline";
	  public String getRegistrationStatus() {
	        return registrationStatus;
	    }
	    public void setRegistrationStatus(String registrationStatus) {
	        this.registrationStatus = registrationStatus;
	    }
	
}
