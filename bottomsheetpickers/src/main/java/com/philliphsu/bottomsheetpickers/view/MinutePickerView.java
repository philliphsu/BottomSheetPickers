package com.philliphsu.bottomsheetpickers.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.philliphsu.bottomsheetpickers.R;

/**
 * View to pick a minute (00 - 59) from a 4 x 3 grid of preset options
 * and buttons to increment and decrement.
 */
class MinutePickerView extends LinearLayout {

    private static final @IdRes int[] TUNER_BUTTONS_IDS =
            { R.id.bsp_dec_min,  R.id.bsp_inc_min };

    private static final String[] MINUTES_TEXTS = new String[12];

    static {
        for (int i = 0; i < 12; i++) {
            MINUTES_TEXTS[i] = String.format("%02d", 5 * i);
        }
    }

    private final ImageButton[] TUNER_BUTTONS = new ImageButton[2];

    private GridPickerView mPresetOptions;

    public MinutePickerView(Context context) {
        this(context, null);
    }

    public MinutePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinutePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @TargetApi(21)
    public MinutePickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.bsp_minute_picker_view, this);
        mPresetOptions = (GridPickerView) findViewById(R.id.bsp_preset_options);
        for (int i = 0; i < 12; i++) {
            mPresetOptions.setTextForPosition(i, MINUTES_TEXTS[i]);
        }
        TUNER_BUTTONS[0] = (ImageButton) findViewById(TUNER_BUTTONS_IDS[0]);
        TUNER_BUTTONS[1] = (ImageButton) findViewById(TUNER_BUTTONS_IDS[1]);
    }
}
