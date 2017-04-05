package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.philliphsu.bottomsheetpickers.view.Preconditions.checkNotNull;
import static java.util.Calendar.AM;
import static java.util.Calendar.PM;

/**
 * Controller that manages the UI states of a number pad time picker.
 */
final class NumberPadTimePickerController {

    // Determines how the time should be formatted for display.
    @IntDef({AM, PM, NOT_APPLICABLE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface HalfDay {}

    private static final int NOT_APPLICABLE = -1;

    // (M)odel: Simply stores data and provides us access to that data.
    private final TimeModel mTimeModel = new TimeModel();

    // Used by the (C)ontroller to format/process the data from the model for
    // consumption by the end user.
    // We do not set an initial capacity because the length of a complete time
    // string is locale-dependent.
    private final StringBuilder mTimeFormatter = new StringBuilder();

    // (V)iews
    private final @NonNull NumberPadTimePickerView mNumberPadView;
    private final @NonNull TextView mTimeDisplayView;
    private final @Nullable TextView mAmPmDisplayView;
    private final @NonNull View mBackspaceButton;
    private final @NonNull View mOkButton;

    private @HalfDay int mChosenHalfDay;

    public NumberPadTimePickerController(@NonNull NumberPadTimePickerView numberPadView,
                                         @NonNull TextView timeDisplayView,
                                         @Nullable TextView ampmDisplayView,
                                         @NonNull View backspaceButton,
                                         @NonNull View okButton) {
        mNumberPadView = checkNotNull(numberPadView);
        mTimeDisplayView = checkNotNull(timeDisplayView);
        mBackspaceButton = checkNotNull(backspaceButton);
        mOkButton = checkNotNull(okButton);
        mAmPmDisplayView = ampmDisplayView;
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
        final int[] mDigits = new int[4];

        void storeDigit(int digit) {
            int i = -1; // TODO: keep track of our "cursor"
            mDigits[i] = digit;
            i++;
        }

        int getDigit(int at) {
            return mDigits[at];
        }

        void removeDigit() {
            int i = -1; // TODO: keep track of our "cursor"
            if (i > 0) {
                i--; // move the cursor back
                mDigits[i] = -1;
            }
        }
    }
}
