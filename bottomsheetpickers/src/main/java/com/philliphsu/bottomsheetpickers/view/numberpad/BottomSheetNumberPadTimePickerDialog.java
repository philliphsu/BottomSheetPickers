package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

public class BottomSheetNumberPadTimePickerDialog extends BottomSheetDialog {

    private final NumberPadTimePickerDialogViewDelegate mViewDelegate;

    public BottomSheetNumberPadTimePickerDialog(@NonNull Context context,
                                                @Nullable OnTimeSetListener listener,
                                                boolean is24HourMode) {
        super(context);
        final View root = getLayoutInflater().inflate(
                R.layout.bsp_bottomsheet_numberpad_time_picker, null);
        final NumberPadTimePicker timePicker = (NumberPadTimePicker)
                root.findViewById(R.id.bsp_numberpad_time_picker);
        final View okButton = root.findViewById(R.id.bsp_ok_button);
        mViewDelegate = new NumberPadTimePickerDialogViewDelegate(this,
                context, timePicker, okButton, listener, is24HourMode);
        setContentView(root);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewDelegate.getPresenter().onOkButtonClick();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewDelegate.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        return mViewDelegate.onSaveInstanceState(super.onSaveInstanceState());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewDelegate.onStop();
    }
}
