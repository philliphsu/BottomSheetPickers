package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog implements INumberPadTimePicker.View {

    private final NumberPadTimePicker mTimePicker;

    public NumberPadTimePickerDialog(@NonNull Context context) {
        super(context);
        mTimePicker = new NumberPadTimePicker(context);
        setView(mTimePicker);
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        // TODO: Delegate to NumberPadTimePicker#setNumberKeysEnabled()
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        // TODO: Delegate to NumberPadTimePicker#setBackspaceEnabled()
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        // TODO: Delegate to NumberPadTimePicker#updateTimeDisplay()
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        // TODO: Delegate to NumberPadTimePicker#updateAmPmDisplay()
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        getButton(BUTTON_POSITIVE).setEnabled(enabled);
    }
}
