package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Phillip Hsu on 4/4/2017.
 */

class HourPickerView extends GridPickerView {

    private static final int[] HOURS = { 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

    public HourPickerView(Context context) {
        this(context, null);
    }

    public HourPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HourPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        for (int i = 0; i < 12; i++) {
            setTextForPosition(i, String.format("%d", HOURS[i]));
        }
    }
}
