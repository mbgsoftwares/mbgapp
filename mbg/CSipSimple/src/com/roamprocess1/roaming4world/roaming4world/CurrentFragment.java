package com.roamprocess1.roaming4world.roaming4world;

public class CurrentFragment {
	public static String name="EnterAPIN";
	public static String Activate="NotActivate";
	public static String pinNumber="NoPinNumber";
	public static String fragment_status="0";
	public static int flightsearchCheck = 0;
	public static boolean flightLoading = false;
	public static int flightinfo = 0;
	public static boolean progressContainerLayout = false;
	public static boolean Call_Log_Type = false;
	
	
	
	public static String stripContactNumber (String value, String Country_zip_code)
	{
		String modify_contact_no = value;
		if ( !value.startsWith("*")&& !value.startsWith("#")) {
			
			if (value.startsWith("+")) {
				modify_contact_no = value.substring(1);
			} else if (value.startsWith("00")) {
				modify_contact_no = value.substring(2);
				modify_contact_no = Country_zip_code+ modify_contact_no;
			} else if (value.startsWith("0")) {
				modify_contact_no = value.substring(1);
				modify_contact_no = Country_zip_code+ modify_contact_no;
			} else if (!value.startsWith("+") && !value.startsWith("0")) {
			}
		}
		
		return modify_contact_no; 
	}
	
}
