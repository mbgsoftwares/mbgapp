package com.roamprocess1.roaming4world.roaming4world;

import android.annotation.SuppressLint;
import android.app.Activity;

public class VersionHelper {

	@SuppressLint("NewApi")
	public static void refreshActionBarMenu(Activity activity)
    {
        activity.invalidateOptionsMenu();
        
    }
}
