package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

/**
 * Component that install {@link NumberPadTimePicker#LAYOUT_ALERT alert dialog} functionality
 * to a {@link NumberPadTimePicker}.
 */
final class NumberPadTimePickerAlertComponent extends
        NumberPadTimePicker.NumberPadTimePickerComponent {

    NumberPadTimePickerAlertComponent(NumberPadTimePicker timePicker, Context context,
            AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(timePicker, context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    View inflate(Context context, NumberPadTimePicker root) {
        return View.inflate(context, R.layout.bsp_numberpad_time_picker, root);
    }
}
