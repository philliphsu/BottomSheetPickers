package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.NonNull;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import java.text.DateFormatSymbols;

import static com.philliphsu.bottomsheetpickers.view.Preconditions.checkNotNull;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.AM;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.HRS_24;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.PM;
import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.UNSPECIFIED;
import static com.philliphsu.bottomsheetpickers.view.numberpad.DigitwiseTimeModel.MAX_DIGITS;

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

    private final @NonNull INumberPadTimePicker.View view;
    private final @NonNull LocaleModel localeModel;

    private final String timeSeparator;
    private final boolean mIs24HourMode;
    
    private @AmPmStates.AmPmState int mAmPmState = UNSPECIFIED;
    private boolean mAltKeysDisabled;
    private boolean mAllNumberKeysDisabled;
    private boolean mHeaderDisplayFocused;

    @Deprecated // TODO: Delete this! THis should not make it into release.
    NumberPadTimePickerPresenter(INumberPadTimePicker.View view) {
        this(view, null, false);
    }

    NumberPadTimePickerPresenter(@NonNull INumberPadTimePicker.View view,
                                 @NonNull LocaleModel localeModel,
                                 boolean is24HourMode) {
        this.view = checkNotNull(view);
        this.localeModel = checkNotNull(localeModel);
        timeSeparator = localeModel.getTimeSeparator(is24HourMode);
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
                // The time separator is inserted for you
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
            // charAt(0) is the time separator, so skip i = 0.
            // We are only interested in storing the digits.
            for (int i = 1; i < altKeyText.length(); i++) {
                // The array and the text do not have the same lengths,
                // so the iterator value does not correspond to the
                // array index directly
                digits[i - 1] = Character.digit(altKeyText.charAt(i), BASE_10);
            }
            // Time separator is added for you
            insertDigits(digits);
            mAmPmState = HRS_24;
        }

        updateViewEnabledStates();
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
            updateViewEnabledStates();
        } else {
            timeModel.removeDigit();
        }
    }

    @Override
    public boolean onBackspaceLongClick() {
        return timeModel.clearDigits();
    }

    @Override
    public void onShowTimePicker() {
        view.updateTimeDisplay(null);
        view.updateAmPmDisplay(null);
        view.setAmPmDisplayVisible(!mIs24HourMode);
        view.setIs24HourMode(mIs24HourMode);
        if (!mIs24HourMode) {
            view.setAmPmDisplayIndex(localeModel.isAmPmWrittenBeforeTime() ? 0 : 1);
        }
        updateViewEnabledStates();
    }

    @Override
    public void onDigitStored(int digit) {
        // Append the new digit(s) to the formatter
        updateFormattedInputOnDigitInserted(digit);
        view.updateTimeDisplay(mFormattedInput.toString());
        updateViewEnabledStates();
    }

    @Override
    public void onDigitRemoved(int digit) {
        updateFormattedInputOnDigitDeleted();
        view.updateTimeDisplay(mFormattedInput.toString());
        updateViewEnabledStates();
    }

    @Override
    public void onDigitsCleared() {
        mFormattedInput.delete(0, mFormattedInput.length());
        mAmPmState = UNSPECIFIED;
        updateViewEnabledStates(); // TOneverDO: before resetting mAmPmState to UNSPECIFIED
        view.updateTimeDisplay(null);
        if (!mIs24HourMode) {
            view.updateAmPmDisplay(null);
        }
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
        mAllNumberKeysDisabled = start == 0 && end == 0;
    }

    private void insertDigits(int... digits) {
        timeModel.storeDigits(digits);
    }

    private void updateViewEnabledStates() {
        updateNumberKeysStates();
        updateAltKeysStates();
        updateBackspaceState();
        updateOkButtonState();
        // TOneverDO: Call before both updateAltKeysStates() and updateNumberKeysStates().
        updateHeaderDisplayFocus();
    }

    private void updateHeaderDisplayFocus() {
        final boolean showHeaderDisplayFocused = !(mAllNumberKeysDisabled && mAltKeysDisabled);
        if (mHeaderDisplayFocused != showHeaderDisplayFocused) {
            view.setHeaderDisplayFocused(showHeaderDisplayFocused);
            mHeaderDisplayFocused = showHeaderDisplayFocused;
        }
    }

    private void updateOkButtonState() {
        view.setOkButtonEnabled(timeParser.checkTimeValid(mAmPmState));
    }

    private void updateBackspaceState() { 
        view.setBackspaceEnabled(count() > 0);
    }

    private void updateAltKeysStates() {
        boolean enabled = false;
        if (count() == 0) {
            // No input, no access!
            enabled = false;
        } else if (count() == 1) {
            // Any of 0-9 inputted, always have access in either clock.
            enabled = true;
        } else if (count() == 2) {
            // Any 2 digits that make a valid hour for either clock are eligible for access
            final int time = getInput();
            enabled = is24HourFormat() ? time <= 23 : time >= 10 && time <= 12;
        } else if (count() == 3 || count() == MAX_DIGITS) {
            // For the 24-hour clock, no access at all because
            // two more digits (00 or 30) cannot be added without
            // exceeding MAX_DIGITS.
            // For the 12-hour clock, any 3-digit or 4-digit times have
            // complete need of the alt buttons, if AM/PM not already entered.
            enabled = !is24HourFormat() && mAmPmState == UNSPECIFIED;
        }
        view.setLeftAltKeyEnabled(enabled);
        view.setRightAltKeyEnabled(enabled);

        mAltKeysDisabled = !enabled;
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
        // Add time separator if necessary, depending on how many digits entered so far
        if (count() == 3) {
            // Insert a time separator
            int digits = getInput();
            if (digits >= 60 && digits < 100 || digits >= 160 && digits < 200) {
                // From 060-099 (really only to 095, but might as well go up to 100)
                // From 160-199 (really only to 195, but might as well go up to 200),
                // time does not exist if time separator goes at pos. 1
                mFormattedInput.insert(2, timeSeparator);
                // These times only apply to the 24-hour clock, and if we're here,
                // the time is not legal yet. So we can't set mAmPmState here for
                // either clock.
                // The 12-hour clock can only have mAmPmState set when AM/PM are clicked.
            } else {
                // A valid time exists if time separator is at pos. 1
                mFormattedInput.insert(1, timeSeparator);
                // We can set mAmPmState here (and not in the above case) because
                // the time here is legal in 24-hour clock
                if (is24HourFormat()) {
                    mAmPmState = HRS_24;
                }
            }
        } else if (count() == MAX_DIGITS) {
            int timeSeparatorAt = mFormattedInput.indexOf(timeSeparator);
            // Since we now batch update the formatted input whenever
            // digits are inserted, the time separator may legitimately not be
            // present in the formatted input when this is initialized.
            if (timeSeparatorAt != -1) {
                // Time separator needs to move, so remove the time separator previously added
                mFormattedInput.deleteCharAt(timeSeparatorAt);
            }
            mFormattedInput.insert(2, timeSeparator);

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
            // Move the time separator from its 4-digit position to its 3-digit position, unless doing
            // so would give an invalid time (e.g. 17:55 becomes 1:75, which is invalid).
            // This could possibly be an issue only when using 24-hour time.
            //
            // 4-digit times in the 24-hour clock must be within one of the following ranges
            // to become valid 3-digit times:
            //     [00:00, 05:59] to become [0:00, 0:55] or
            //     [10:00, 15:59] to become [1:00, 1:55] or
            //     [20:00, 23:59] to become [2:00, 2:35].
            // These 3-digit times are represented within the limits below.
            //
            // All 3-digit times in the 12-hour clock at this point are valid times.
            // They are represented within the range [100, 125].
            if (value >= 0 && value <= 55
                    || value >= 100 && value <= 155
                    || value >= 200 && value <= 235) {
                mFormattedInput.deleteCharAt(mFormattedInput.indexOf(timeSeparator));
                mFormattedInput.insert(1, timeSeparator);
            } else {
                // previously [06:00, 09:59] or [16:00, 19:59]
                mAmPmState = UNSPECIFIED;
            }
        } else if (count() == 2) {
            // Remove the time separator
            mFormattedInput.deleteCharAt(mFormattedInput.indexOf(timeSeparator));
            // No time can be valid with only 2 digits in either system.
            // I don't think we actually need this, but it can't hurt?
            mAmPmState = UNSPECIFIED;
        }
    }
}
