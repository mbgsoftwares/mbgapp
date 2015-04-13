/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.plus;

import com.google.android.gms.plus.PlusOneButton;

import android.app.Activity;
import android.os.Bundle;

/**
 * Example usage of the +1 button.
 */
public class PlusOneActivity extends Activity {
    private static final String URL = "https://play.google.com/store/apps/details?id=com.roamprocess1.roaming4world";

    // The request code must be 0 or higher.
    private static final int PLUS_ONE_REQUEST_CODE = 0;

  
    private PlusOneButton mPlusOneTallButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plus_one_activity);
        mPlusOneTallButton = (PlusOneButton) findViewById(R.id.plus_one_tall_button);
      
        
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time we receive focus.
        
        mPlusOneTallButton.initialize(URL, PLUS_ONE_REQUEST_CODE);
        
    }
}