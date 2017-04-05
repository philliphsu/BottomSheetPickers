package com.philliphsu.bottomsheetpickers.view;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

/**
 * Controller that manages the UI states of a number pad time picker.
 */
final class NumberPadTimePickerController {

    // Determines how the time should be formatted for display.
    private static final int AM = 0;
    private static final int PM = 1;
    private static final int NOT_APPLICABLE = -1;

    private static final int MAX_CHARS = 8;

    // (M)odel: Simply stores data and provides us access to that data.
    private final TimeModel mTimeModel = new TimeModel();

    // Objects used by the (C)ontroller to format/process the data from the model
    // for consumption by the end user.
    private final StringBuilder mTimeFormatter = new StringBuilder(MAX_CHARS);

    // (V)iews
    private final NumberPadTimePickerView mNumberPadView;
    private final TextView mTimeDisplayView;
    private final View mBackspaceButton;
    private final View mOkButton;

    private int mTimeState;

    public NumberPadTimePickerController(@NonNull NumberPadTimePickerView numberPadView,
                                         @NonNull TextView timeDisplayView,
                                         @NonNull View backspaceButton,
                                         @NonNull View okButton) {
        mNumberPadView = numberPadView;
        mTimeDisplayView = timeDisplayView;
        mBackspaceButton = backspaceButton;
        mOkButton = okButton;
    }

    void onButtonClick(TextView button) {
        final String text = button.getText().toString();
        try {
            mTimeModel.storeDigit(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            // TODO: This is either an AM/PM state, or an alt button.
        }
        updateViews();
    }

    void onBackspaceClick() {
        mTimeModel.removeDigit();
        updateViews();
    }

    private void updateViews() {
        // TODO: Update number key states.
        // TODO: Update backspace state.
        // TODO: Update ok button state.
        // TODO: Format text for display and update time display view.
    }

    /**
     * Model that encapsulates data pertaining to the inputted time.
     * The data is insignificant enough that we can make the model
     * an inner class of the controller.
     */
    private static class TimeModel {
        private final int[] mDigits = new int[4];

        private void storeDigit(int digit) {
            int i = -1; // TODO: keep track of our "cursor"
            mDigits[i] = digit;
            i++;
        }

        private int getDigit(int at) {
            return mDigits[at];
        }

        private void removeDigit() {
            int i = -1; // TODO: keep track of our "cursor"
            if (i > 0) {
                i--; // move the cursor back
                mDigits[i] = -1;
            }
        }
    }
}
