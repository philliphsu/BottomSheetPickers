package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */

public class BottomSheetNumberPadTimePickerDialog extends BottomSheetDialog {

    private final NumberPadTimePicker mTimePicker;
    private final View mOkButton;

    public BottomSheetNumberPadTimePickerDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.bsp_bottomsheet_numberpad_time_picker);
        mTimePicker = (NumberPadTimePicker) findViewById(R.id.bsp_numberpad_time_picker);
        mOkButton = findViewById(R.id.bsp_ok_button);
    }
}
