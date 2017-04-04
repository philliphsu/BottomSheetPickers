package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;

import com.philliphsu.bottomsheetpickers.R;

/**
 * View to pick an hour (00 - 23) from a 4 x 3 grid.
 */
public final class TwentyFourHourPickerView extends LinearLayout {

    private static final @IdRes int[] TEXT_SWITCHER_IDS = {
            R.id.switcher0,
            R.id.switcher1,
            R.id.switcher2,
            R.id.switcher3,
            R.id.switcher4,
            R.id.switcher5,
            R.id.switcher6,
            R.id.switcher7,
            R.id.switcher8,
            R.id.switcher9,
            R.id.switcher10,
            R.id.switcher11,
    };

    private static final int NUM_TEXT_SWITCHERS = TEXT_SWITCHER_IDS.length;

    private static final TextSwitcher[] TEXT_SWITCHERS = new TextSwitcher[NUM_TEXT_SWITCHERS];

    private static final String[] HOURS_TEXTS_00_11 = new String[12];
    private static final String[] HOURS_TEXTS_12_23 = new String[12];

    static {
        for (int i = 0; i < 12; i++) {
            HOURS_TEXTS_00_11[i] = String.format("%02d", i);
            HOURS_TEXTS_12_23[i] = String.format("%02d", i + 12);
        }
    }

    public TwentyFourHourPickerView(Context context) {
        this(context, null);
    }

    public TwentyFourHourPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwentyFourHourPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.bsp_twentyfour_hour_picker_view, this);

        for (int i = 0; i < NUM_TEXT_SWITCHERS; i++) {
            TEXT_SWITCHERS[i] = (TextSwitcher) findViewById(TEXT_SWITCHER_IDS[i]);
            if (i < 12) {
                TEXT_SWITCHERS[i].setCurrentText(HOURS_TEXTS_00_11[i]);
                TEXT_SWITCHERS[i].setOnClickListener(mOnClickListener);
            }
        }
    }

//    @TargetApi(21)
//    public TwentyFourHourPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    // TODO: Proper implementation--not this anonymous inner class.
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            for (int i = 0; i < 12; i++) {
                TEXT_SWITCHERS[i].setText(HOURS_TEXTS_12_23[i]);
            }
        }
    };
}
