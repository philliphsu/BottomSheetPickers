package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
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

    private final DigitwiseTimeModel mDigitwiseTimeModel = new DigitwiseTimeModel();

    // We do not set an initial capacity because the length of a complete time
    // string is locale-dependent.
    private final StringBuilder mTimeFormatter = new StringBuilder();

    private final @NonNull NumberPadTimePicker mTimePicker;
    private final @NonNull View mOkButton;

    private @HalfDay int mChosenHalfDay;

    public NumberPadTimePickerController(@NonNull NumberPadTimePicker timePicker,
                                         @NonNull View okButton) {
        mTimePicker = checkNotNull(timePicker);
        mOkButton = checkNotNull(okButton);
    }

    void onButtonClick(TextView button) {
        final String text = button.getText().toString();
        try {
            mDigitwiseTimeModel.storeDigit(Integer.parseInt(text));
        } catch (NumberFormatException e) {
            // TODO: This is either an AM/PM state, or an alt button.
        }
        updateViews();
    }

    void onBackspaceClick() {
        mDigitwiseTimeModel.removeDigit();
        updateViews();
    }

    private void updateViews() {
        // TODO: Update number key states.
        // TODO: Update backspace state.
        // TODO: Update ok button state.
        // TODO: Format text for display and update time display view.
    }

}
