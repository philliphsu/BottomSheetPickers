package com.philliphsu.bottomsheetpickers.view.numberpad;

import java.text.DateFormatSymbols;

import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.AM;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.HRS_24;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.PM;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.UNSPECIFIED;
import static com.philliphsu.bottomsheetpickers.view.numberpad.DigitwiseTimeModel.MAX_DIGITS;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */
final class NumberPadTimePickerPresenter implements
        INumberPadTimePicker.Presenter,
        DigitwiseTimeModel.OnInputChangeListener {
    // TODO: Delete this if we're not setting a capacity.
    // Formatted time string has a maximum of 8 characters
    // in the 12-hour clock, e.g 12:59 AM. Although the 24-hour
    // clock should be capped at 5 characters, the difference
    // is not significant enough to deal with the separate cases.
    private static final int MAX_CHARS = 8;

    // Constant for converting text digits to numeric digits in base-10.
    private static final int BASE_10 = 10;

    private final DigitwiseTimeModel timeModel = new DigitwiseTimeModel(this);
    
    private final DigitwiseTimeParser timeParser = new DigitwiseTimeParser(timeModel);

    // TODO: Delete setting of capacity.
    private final StringBuilder mFormattedInput = new StringBuilder(MAX_CHARS);

    private final INumberPadTimePicker.View view;
    
    private @AmPmStates.AmPmState int mAmPmState = UNSPECIFIED;

    private final boolean mIs24HourMode;

    @Deprecated // TODO: Delete this! THis should not make it into release.
    NumberPadTimePickerPresenter(INumberPadTimePicker.View view) {
        this(view, false);
    }

    NumberPadTimePickerPresenter(INumberPadTimePicker.View view, boolean is24HourMode) {
        this.view = view;
        mIs24HourMode = is24HourMode;
    }

    @Override
    public void onNumberKeyClick(CharSequence numberKeyText) {
        timeModel.storeDigit(Integer.parseInt(numberKeyText.toString()));
    }

    @Override
    public void onAltKeyClick(CharSequence altKeyText) {
        // Manually insert special characters for 12-hour clock
        if (!is24HourFormat()) {
            if (count() <= 2) {
                // The colon is inserted for you
                insertDigits(0, 0);
            }
            // text is AM or PM, so include space before
            String ampm = altKeyText.toString();
            // TODO: When we're finalizing the code, we probably don't need to
            // format this in anymore; just tell the view to update its am/pm
            // display directly.
            // However, we currently need to leave this in for the backspace
            // logic to work correctly.
            mFormattedInput.append(' ').append(ampm);
            String am = new DateFormatSymbols().getAmPmStrings()[0];
            mAmPmState = ampm.equalsIgnoreCase(am) ? AM : PM;
            // Digits will be shown for you on insert, but not AM/PM
            view.updateAmPmDisplay(ampm);
        } else {
            // Assuming the text is one of ":00" or ":30", this
            // evaluates to 2.
            final int numDigits = altKeyText.length() - 1;
            int[] digits = new int[numDigits];
            // charAt(0) is the colon, so skip i = 0.
            // We are only interested in storing the digits.
            for (int i = 1; i < altKeyText.length(); i++) {
                // The array and the text do not have the same lengths,
                // so the iterator value does not correspond to the
                // array index directly
                digits[i - 1] = Character.digit(altKeyText.charAt(i), BASE_10);
            }
            // Colon is added for you
            insertDigits(digits);
            mAmPmState = HRS_24;
        }

        updateNumpadStates();
    }

    @Override
    public void onBackspaceClick() {
        final int len = mFormattedInput.length();
        if (!mIs24HourMode && mAmPmState != UNSPECIFIED) {
            mAmPmState = UNSPECIFIED;
            mFormattedInput.delete(mFormattedInput.indexOf(" "), len);
            view.updateAmPmDisplay(null);
            /* No digit was actually deleted, so there is no need to 
             * update the time display. */
            updateNumpadStates();
        } else {
            timeModel.removeDigit();
        }
    }

    @Override
    public void onShowTimePicker(/*TODO: Require is24HourMode param*/) {
        view.updateTimeDisplay(null);
        view.updateAmPmDisplay(null);
        updateNumpadStates();
        // TODO: Update number key states.
        // TODO: Set the alt button texts according to is24HourMode.
    }

    @Override
    public void onDigitStored(int digit) {
        // Append the new digit(s) to the formatter
        updateFormattedInputOnDigitInserted(digit);
        view.updateTimeDisplay(mFormattedInput.toString());
        updateNumpadStates();
    }

    @Override
    public void onDigitRemoved(int digit) {
        updateFormattedInputOnDigitDeleted();
        view.updateTimeDisplay(mFormattedInput.toString());
        updateNumpadStates();
    }

    @Override
    public void onDigitsCleared() {
        mFormattedInput.delete(0, mFormattedInput.length());
        mAmPmState = UNSPECIFIED;
        updateNumpadStates(); // TOneverDO: before resetting mAmPmState to UNSPECIFIED
        view.updateTimeDisplay(null);
    }

    private int count() {
        return timeModel.count();
    }

    private boolean is24HourFormat() {
        return mIs24HourMode;
    }
    
    private int getInput() {
        return timeModel.getInput();
    }

    private void enable(int start, int end) {
        view.setNumberKeysEnabled(start, end);
    }

    private void insertDigits(int... digits) {
        timeModel.storeDigits(digits);
    }
    
    private void updateNumpadStates() {
        // TOneverDO: after updateNumberKeysStates(), esp. if clock is 12-hour,
        // because it calls enable(0, 0), which checks if the alt buttons have been
        // disabled as well before firing the onInputDisabled().
        updateAltButtonStates();

        updateBackspaceState();
        updateNumberKeysStates();
        updateFabState();
    }

    private void updateFabState() {
        view.setOkButtonEnabled(timeParser.checkTimeValid(mAmPmState));
    }

    private void updateBackspaceState() { 
        view.setBackspaceEnabled(count() > 0);
    }

    private void updateAltButtonStates() {
        if (count() == 0) {
            // No input, no access!
            view.setLeftAltKeyEnabled(false);
            view.setRightAltKeyEnabled(false);
        } else if (count() == 1) {
            // Any of 0-9 inputted, always have access in either clock.
            view.setLeftAltKeyEnabled(true);
            view.setRightAltKeyEnabled(true);
        } else if (count() == 2) {
            // Any 2 digits that make a valid hour for either clock are eligible for access
            int time = getInput();
            boolean validTwoDigitHour = is24HourFormat() ? time <= 23 : time >= 10 && time <= 12;
            view.setLeftAltKeyEnabled(validTwoDigitHour);
            view.setRightAltKeyEnabled(validTwoDigitHour);
        } else if (count() == 3) {
            if (is24HourFormat()) {
                // For the 24-hour clock, no access at all because
                // two more digits (00 or 30) cannot be added to 3 digits.
                view.setLeftAltKeyEnabled(false);
                view.setRightAltKeyEnabled(false);
            } else {
                // True for any 3 digits, if AM/PM not already entered
                boolean enabled = mAmPmState == UNSPECIFIED;
                view.setLeftAltKeyEnabled(enabled);
                view.setRightAltKeyEnabled(enabled);
            }
        } else if (count() == MAX_DIGITS) {
            // If all 4 digits are filled in, the 24-hour clock has absolutely
            // no need for the alt buttons. However, The 12-hour clock has
            // complete need of them, if not already used.
            boolean enabled = !is24HourFormat() && mAmPmState == UNSPECIFIED;
            view.setLeftAltKeyEnabled(enabled);
            view.setRightAltKeyEnabled(enabled);
        }
    }
    
    private void updateNumberKeysStates() {
        int cap = 10; // number of buttons
        boolean is24hours = is24HourFormat();

        if (count() == 0) {
            enable(is24hours ? 0 : 1, cap);
            return;
        } else if (count() == MAX_DIGITS) {
            enable(0, 0);
            return;
        }

        int time = getInput();
        if (is24hours) {
            if (count() == 1) {
                enable(0, time < 2 ? cap : 6);
            } else if (count() == 2) {
                enable(0, time % 10 >= 0 && time % 10 <= 5 ? cap : 6);
            } else if (count() == 3) {
                if (time >= 236) {
                    enable(0, 0);
                } else {
                    enable(0, time % 10 >= 0 && time % 10 <= 5 ? cap : 0);
                }
            }
        } else {
            if (count() == 1) {
                if (time == 0) {
                    throw new IllegalStateException("12-hr format, zeroth digit = 0?");
                } else {
                    enable(0, 6);
                }
            } else if (count() == 2 || count() == 3) {
                if (time >= 126) {
                    enable(0, 0);
                } else {
                    if (time >= 100 && time <= 125 && mAmPmState != UNSPECIFIED) {
                        // Could legally input fourth digit, if not for the am/pm state already set
                        enable(0, 0);
                    } else {
                        enable(0, time % 10 >= 0 && time % 10 <= 5 ? cap : 0);
                    }
                }
            }
        }
    }

    private void updateFormattedInputOnDigitInserted(int newDigit) {
        mFormattedInput.append(newDigit);
        // Add colon if necessary, depending on how many digits entered so far
        if (count() == 3) {
            // Insert a colon
            int digits = getInput();
            if (digits >= 60 && digits < 100 || digits >= 160 && digits < 200) {
                // From 060-099 (really only to 095, but might as well go up to 100)
                // From 160-199 (really only to 195, but might as well go up to 200),
                // time does not exist if colon goes at pos. 1
                mFormattedInput.insert(2, ':');
                // These times only apply to the 24-hour clock, and if we're here,
                // the time is not legal yet. So we can't set mAmPmState here for
                // either clock.
                // The 12-hour clock can only have mAmPmState set when AM/PM are clicked.
            } else {
                // A valid time exists if colon is at pos. 1
                mFormattedInput.insert(1, ':');
                // We can set mAmPmState here (and not in the above case) because
                // the time here is legal in 24-hour clock
                if (is24HourFormat()) {
                    mAmPmState = HRS_24;
                }
            }
        } else if (count() == MAX_DIGITS) {
            int colonAt = mFormattedInput.indexOf(":");
            // Since we now batch update the formatted input whenever
            // digits are inserted, the colon may legitimately not be
            // present in the formatted input when this is initialized.
            if (colonAt != -1) {
                // Colon needs to move, so remove the colon previously added
                mFormattedInput.deleteCharAt(colonAt);
            }
            mFormattedInput.insert(2, ':');

            // Time is legal in 24-hour clock
            if (is24HourFormat()) {
                mAmPmState = HRS_24;
            }
        }
    }

    private void updateFormattedInputOnDigitDeleted() {
        int len = mFormattedInput.length();
        mFormattedInput.delete(len - 1, len);
        if (count() == 3) {
            int value = getInput();
            // Move the colon from its 4-digit position to its 3-digit position,
            // unless doing so gives an invalid time.
            // e.g. 17:55 becomes 1:75, which is invalid.
            // All 3-digit times in the 12-hour clock at this point should be
            // valid. The limits <=155 and (>=200 && <=235) are really only
            // imposed on the 24-hour clock, and were chosen because 4-digit times
            // in the 24-hour clock can only go up to 15:5[0-9] or be within the range
            // [20:00, 23:59] if they are to remain valid when they become three digits.
            // The is24HourFormat() check is therefore unnecessary.
            if (value <= 155 || value >= 200 && value <= 235) {
                mFormattedInput.deleteCharAt(mFormattedInput.indexOf(":"));
                mFormattedInput.insert(1, ":");
            } else {
                // previously [16:00, 19:59]
                mAmPmState = UNSPECIFIED;
            }
        } else if (count() == 2) {
            // Remove the colon
            mFormattedInput.deleteCharAt(mFormattedInput.indexOf(":"));
            // No time can be valid with only 2 digits in either system.
            // I don't think we actually need this, but it can't hurt?
            mAmPmState = UNSPECIFIED;
        }
    }
}
