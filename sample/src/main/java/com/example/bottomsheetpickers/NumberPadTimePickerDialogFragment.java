package com.example.bottomsheetpickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import com.philliphsu.bottomsheetpickers.view.numberpad.NumberPadTimePickerDialog;

/**
 * Created by Phillip Hsu on 4/16/2017.
 */
public class NumberPadTimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = NumberPadTimePickerDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new NumberPadTimePickerDialog(getContext(), this, DateFormat.is24HourFormat(getContext()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.d(TAG, "Time set: " + String.format("%02d:%02d", hourOfDay, minute));
    }
}
