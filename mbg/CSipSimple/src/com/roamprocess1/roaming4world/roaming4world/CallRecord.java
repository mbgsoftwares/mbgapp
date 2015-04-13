package com.roamprocess1.roaming4world.roaming4world;

import java.util.ArrayList;

import android.util.Log;

public class CallRecord {
	public ArrayList<String> t1,t2,t3,t4,t5,t6;
	
	
	
    public CallRecord(){
        super();
    }
    
    public CallRecord(ArrayList<String> s1,ArrayList<String> s2,ArrayList<String> s3,ArrayList<String> s4,ArrayList<String> s5,ArrayList<String> s6) {
        super();
        
        t1=new ArrayList<String>();
    	t2=new ArrayList<String>();
    	t3=new ArrayList<String>();
    	t4=new ArrayList<String>();
    	t5=new ArrayList<String>();
    	t6=new ArrayList<String>();
        t1 = s1;
        t2 = s2;
        t3 = s3;
        t4 = s4;
        t5 = s5;
        t6 = s6;
    	Log.v("array list a1", t2.toString());
    }
}
