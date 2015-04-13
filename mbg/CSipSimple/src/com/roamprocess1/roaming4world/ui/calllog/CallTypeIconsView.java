

package com.roamprocess1.roaming4world.ui.calllog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.provider.CallLog.Calls;
import android.util.AttributeSet;
import android.view.View;

import com.roamprocess1.roaming4world.R;
import com.roamprocess1.roaming4world.utils.Theme;

/**
 * View that draws one or more symbols for different types of calls (missed
 * calls, outgoing etc). The symbols are set up horizontally. As this view
 * doesn't create subviews, it is better suited for ListView-recycling that a
 * regular LinearLayout using ImageViews.
 */
public class CallTypeIconsView extends View {
    private final List<Integer> mCallTypes;
    private final Resources mResources;
    private int mWidth;
    private int mHeight;

    public CallTypeIconsView(Context context) {
        this(context, null);
    }

    public CallTypeIconsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = new Resources(context, this);
        mCallTypes = new ArrayList<Integer>();
    }

    public void clear() {
        mCallTypes.clear();
        mWidth = 0;
        mHeight = 0;
        invalidate();
    }

    public void add(int callType) {
        mCallTypes.add(callType);

        final Drawable drawable = getCallTypeDrawable(callType);
        mWidth += drawable.getIntrinsicWidth() + mResources.iconMargin;
        mHeight = Math.max(mHeight, drawable.getIntrinsicHeight());
        invalidate();
    }

    public int getCount() {
        return mCallTypes.size();
    }

    public int getCallType(int index) {
        return mCallTypes.get(index);
    }

    private Drawable getCallTypeDrawable(int callType) {
        switch (callType) {
            case Calls.INCOMING_TYPE:
                return mResources.incoming;
            case Calls.OUTGOING_TYPE:
                return mResources.outgoing;
            case Calls.MISSED_TYPE:
                return mResources.missed;
            default:
                throw new IllegalArgumentException("invalid call type: " + callType);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = 0;
        for (Integer callType : mCallTypes) {
            final Drawable drawable = getCallTypeDrawable(callType);
            final int right = left + drawable.getIntrinsicWidth();
            drawable.setBounds(left, 0, right, drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            left = right + mResources.iconMargin;
        }
    }

    private static class Resources {
        public Drawable incoming = null;
        public Drawable outgoing = null;
        public Drawable missed = null;
        public Integer iconMargin = null;

        public Resources(Context context, View v) {
            final android.content.res.Resources r = context.getResources();
            Theme t = null;
            if(!v.isInEditMode()) {
                t = Theme.getCurrentTheme(context);
            }
            if(t != null) {
                incoming = t.getDrawableResource("ic_call_incoming");
                outgoing = t.getDrawableResource("ic_call_outgoing");
                missed = t.getDrawableResource("ic_call_missed");
                iconMargin = t.getDimension("call_log_icon_margin");
            }
            if(incoming == null) {
                incoming = r.getDrawable(R.drawable.ic_call_incoming_holo_dark);
            }
            if(outgoing == null) {
                outgoing = r.getDrawable(R.drawable.ic_call_outgoing_holo_dark);
            }
            if(missed == null) {
                missed = r.getDrawable(R.drawable.ic_call_missed_holo_dark);
            }
            if(iconMargin == null) {
                iconMargin = r.getDimensionPixelSize(R.dimen.call_log_icon_margin);
            }
        }
    }
}
