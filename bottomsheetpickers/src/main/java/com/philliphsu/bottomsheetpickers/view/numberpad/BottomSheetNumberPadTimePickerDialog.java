package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */

public class BottomSheetNumberPadTimePickerDialog extends BottomSheetDialog
        implements INumberPadTimePicker.View {

    private final NumberPadTimePicker mTimePicker;
    private final View mOkButton;

    private final INumberPadTimePicker.Presenter mPresenter;

    public BottomSheetNumberPadTimePickerDialog(@NonNull Context context) {
        super(context);

        setContentView(R.layout.bsp_bottomsheet_numberpad_time_picker);
        mTimePicker = (NumberPadTimePicker) findViewById(R.id.bsp_numberpad_time_picker);
        mOkButton = findViewById(R.id.bsp_ok_button);
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mPresenter = new NumberPadTimePickerPresenter(this);
        mTimePicker.setOnBackspaceClickListener(new OnBackspaceClickListener(mPresenter));
        mTimePicker.setOnNumberKeyClickListener(new OnNumberKeyClickListener(mPresenter));
        setOnShowListener(new OnShowTimePickerListener(mPresenter));
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {

    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {

    }

    @Override
    public void updateTimeDisplay(CharSequence time) {

    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {

    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        mOkButton.setEnabled(enabled);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {

    }

    @Override
    public void setAmPmDisplayIndex(int index) {

    }

    @Override
    public void setIs24HourMode(boolean is24HourMode) {

    }
}
