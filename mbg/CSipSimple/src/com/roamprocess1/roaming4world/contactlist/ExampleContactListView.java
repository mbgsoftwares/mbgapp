package com.roamprocess1.roaming4world.contactlist;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

public class ExampleContactListView extends ContactListView{

	public ExampleContactListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void createScroller(){
		
		mScroller = new IndexScroller(getContext(), this);
		mScroller.setAutoHide(autoHide);
		
		// style 1
		//mScroller.setShowIndexContainer(false);
		//mScroller.setIndexPaintColor(Color.argb(255, 49, 64, 91));
		
		// style 2 
		mScroller.setShowIndexContainer(true);
		mScroller.setIndexPaintColor(Color.GRAY);

		
		if(autoHide)
			mScroller.hide();
		else
			mScroller.show();
		
		
	}
}
