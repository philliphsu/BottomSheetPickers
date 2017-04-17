package com.example.bottomsheetpickers;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;

import com.philliphsu.bottomsheetpickers.view.numberpad.NumberPadTimePickerDialog;

/**
 * Created by Phillip Hsu on 4/16/2017.
 */
public class NumberPadTimePickerDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new NumberPadTimePickerDialog(getContext(), DateFormat.is24HourFormat(getContext()));
    }
}
