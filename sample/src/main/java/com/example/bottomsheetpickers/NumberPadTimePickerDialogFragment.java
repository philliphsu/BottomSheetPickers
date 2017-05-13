package com.example.bottomsheetpickers;

import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.philliphsu.bottomsheetpickers.view.numberpad.BottomSheetNumberPadTimePickerDialog;
import com.philliphsu.bottomsheetpickers.view.numberpad.NumberPadTimePickerDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Phillip Hsu on 4/16/2017.
 */
public class NumberPadTimePickerDialogFragment extends DialogFragment {
    private static final String KEY_DIALOG_MODE = "dialog_mode";

    public static final int MODE_ALERT = 1;
    public static final int MODE_BOTTOM_SHEET = 2;

    @IntDef({MODE_ALERT, MODE_BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DialogMode {}

    @DialogMode
    private int dialogMode;

    private OnTimeSetListener listener;

    public static NumberPadTimePickerDialogFragment newInstance(OnTimeSetListener listener,
                                                                @DialogMode int dialogMode) {
        NumberPadTimePickerDialogFragment f = new NumberPadTimePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_DIALOG_MODE, dialogMode);
        f.setArguments(args);
        f.listener = listener;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            dialogMode = args.getInt(KEY_DIALOG_MODE, MODE_ALERT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (dialogMode) {
            case MODE_BOTTOM_SHEET:
                return new BottomSheetNumberPadTimePickerDialog(getContext(),
                        listener, DateFormat.is24HourFormat(getContext()));
            case MODE_ALERT:
            default:
                return new NumberPadTimePickerDialog(getContext(),
                        listener, DateFormat.is24HourFormat(getContext()));
        }
    }
}
