package com.philliphsu.bottomsheetpickers.view.numberpad;

import static com.philliphsu.bottomsheetpickers.view.numberpad.AmPmStates.UNSPECIFIED;

/**
 * Created by Phillip Hsu on 4/5/2017.
 */
final class NumberPadTimePickerPresenter implements INumberPadTimePicker.Presenter {
    // TODO: Delete this if we're not setting a capacity.
    // Formatted time string has a maximum of 8 characters
    // in the 12-hour clock, e.g 12:59 AM. Although the 24-hour
    // clock should be capped at 5 characters, the difference
    // is not significant enough to deal with the separate cases.
    private static final int MAX_CHARS = 8;

    // Constant for converting text digits to numeric digits in base-10.
    private static final int BASE_10 = 10;

    private final DigitwiseTimeModel timeModel = new DigitwiseTimeModel();
    
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

    }

    @Override
    public void onAltKeyClick(CharSequence altKeyText) {

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
        } else {
            timeModel.removeDigit();
            // TODO: subscribe to the model that tells us when to
            // update the formatted input..
            view.updateTimeDisplay(mFormattedInput.toString());
        }
        updateNumpadStates();
    }

    @Override
    public void onShowTimePicker(/*TODO: Require is24HourMode param*/) {
        view.updateTimeDisplay(null);
        view.updateAmPmDisplay(null);
        updateNumpadStates();
        // TODO: Update number key states.
        // TODO: Set the alt button texts according to is24HourMode.
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
        } else if (count() == DigitwiseTimeModel.MAX_DIGITS) {
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
        } else if (count() == DigitwiseTimeModel.MAX_DIGITS) {
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
}
