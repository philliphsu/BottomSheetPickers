package com.philliphsu.bottomsheetpickers.view;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

/**
 * View to pick a minute (00 - 59) from a 4 x 3 grid of preset options
 * and buttons to increment and decrement.
 */
public class MinutePickerView extends GridLayout {

    private static final @IdRes int[] PRESET_OPTIONS_IDS = {
            R.id.bsp_text0,  R.id.bsp_text1,   R.id.bsp_text2,
            R.id.bsp_text3,  R.id.bsp_text4,   R.id.bsp_text5,
            R.id.bsp_text6,  R.id.bsp_text7,   R.id.bsp_text8,
            R.id.bsp_text9,  R.id.bsp_text10,  R.id.bsp_text11,
    };

    private static final @IdRes int[] TUNER_BUTTONS_IDS =
            { R.id.bsp_dec_min,  R.id.bsp_inc_min };

    private static final String[] MINUTES_TEXTS = new String[12];

    static {
        for (int i = 0; i < 12; i++) {
            MINUTES_TEXTS[i] = String.format("%02d", 5 * i);
        }
    }

    private final TextView[] PRESET_OPTIONS = new TextView[12];
    private final ImageButton[] TUNER_BUTTONS = new ImageButton[2];

    public MinutePickerView(Context context) {
        this(context, null);
    }

    public MinutePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MinutePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setColumnCount(5);
        inflate(context, R.layout.bsp_minute_picker_view, this);

        for (int i = 0; i < 12; i++) {
            PRESET_OPTIONS[i] = (TextView) findViewById(PRESET_OPTIONS_IDS[i]);
            PRESET_OPTIONS[i].setText(MINUTES_TEXTS[i]);
        }
        TUNER_BUTTONS[0] = (ImageButton) findViewById(TUNER_BUTTONS_IDS[0]);
        TUNER_BUTTONS[1] = (ImageButton) findViewById(TUNER_BUTTONS_IDS[1]);
    }

}
