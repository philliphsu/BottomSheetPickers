package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.philliphsu.bottomsheetpickers.R;

/**
 * View to pick an hour (00 - 23) from a 4 x 3 grid.
 */
final class TwentyFourHourPickerView extends LinearLayout {

    public TwentyFourHourPickerView(Context context) {
        this(context, null);
    }

    public TwentyFourHourPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwentyFourHourPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.bsp_twentyfour_hour_picker_view, this);
    }

//    @TargetApi(21)
//    public TwentyFourHourPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
}
