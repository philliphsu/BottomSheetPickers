package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.NonNull;

import static com.philliphsu.bottomsheetpickers.time.numberpad.Preconditions.checkNotNull;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.AM;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.HRS_24;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.PM;
import static com.philliphsu.bottomsheetpickers.time.numberpad.AmPmState.UNSPECIFIED;
import static com.philliphsu.bottomsheetpickers.time.numberpad.DigitwiseTimeModel.MAX_DIGITS;

class NumberPadTimePickerPresenter implements
        INumberPadTimePicker.Presenter,
        DigitwiseTimeModel.OnInputChangeListener {
    private static final int MAX_CHARS = 5;  // 4 digits + time separator

    private final StringBuilder mFormattedInput = new StringBuilder(MAX_CHARS);
    private final String[] mAltTexts = new String[2];

    private final @NonNull LocaleModel mLocaleModel;
    private final ButtonTextModel mTextModel;
    private final String mTimeSeparator;
    private final boolean mIs24HourMode;

    private INumberPadTimePicker.View mView;

    private boolean mAltKeysDisabled;
    private boolean mAllNumberKeysDisabled;
    private boolean mHeaderDisplayFocused;

    final DigitwiseTimeModel mTimeModel = new DigitwiseTimeModel(this);

    @AmPmState
    int mAmPmState = UNSPECIFIED;

    NumberPadTimePickerPresenter(@NonNull INumberPadTimePicker.View view,
                                 @NonNull LocaleModel localeModel,
                                 boolean is24HourMode) {
        mView = checkNotNull(view);
        mLocaleModel = checkNotNull(localeModel);
        mTimeSeparator = localeModel.getTimeSeparator(is24HourMode);
        mIs24HourMode = is24HourMode;

        final ButtonTextModel textModel = new ButtonTextModel(localeModel, is24HourMode);
        mAltTexts[0] = textModel.altText(0);
        mAltTexts[1] = textModel.altText(1);
        mTextModel = textModel;
    }

    @Override
    public void onNumberKeyClick(CharSequence numberKeyText) {
        mTimeModel.storeDigit(ButtonTextModel.digit(numberKeyText.toString()));
    }

    @Override
    public void onAltKeyClick(CharSequence altKeyText) {
        final String altKeyString = altKeyText.toString();
        final int[] altDigits = mTextModel.altDigits(altKeyString);
        if (count() <= 2) {
            insertDigits(altDigits);
        }
        if (!is24HourFormat()) {
            mAmPmState = altKeyString.equalsIgnoreCase(mAltTexts[0]) ? AM : PM;
            mView.updateAmPmDisplay(altKeyString);
        } else {
            mAmPmState = HRS_24;
        }

        updateViewEnabledStates();
    }

    @Override
    public void onBackspaceClick() {
        if (!mIs24HourMode && mAmPmState != UNSPECIFIED) {
            mAmPmState = UNSPECIFIED;
            mView.updateAmPmDisplay(null);
            /* No digit was actually deleted, so there is no need to 
             * update the time display. */
            updateViewEnabledStates();
        } else {
            mTimeModel.removeDigit();
        }
    }

    @Override
    public boolean onBackspaceLongClick() {
        return mTimeModel.clearDigits();
    }

    @Override
    public void onCreate(@NonNull INumberPadTimePicker.State state) {
        // If any digits are inserted, onDigitStored() will be called
        // for each digit and the time display will be updated automatically.
        initialize(state);
        if (!mIs24HourMode) {
            mView.setAmPmDisplayIndex(mLocaleModel.isAmPmWrittenBeforeTime() ? 0 : 1);
            final CharSequence amPmDisplayText;
            switch (state.getAmPmState()) {
                case AM:
                    amPmDisplayText = mAltTexts[0];
                    break;
                case PM:
                    amPmDisplayText = mAltTexts[1];
                    break;
                default:
                    amPmDisplayText = null;
                    break;
            }
            mView.updateAmPmDisplay(amPmDisplayText);
        }
        mView.setAmPmDisplayVisible(!mIs24HourMode);
        setAltKeysTexts();
        updateViewEnabledStates();
    }

    @Override
    public void onStop() {
        // Release our hold on the view so that it may be GCed.
        // This presenter will be GCed with its view, so there
        // is no need for us to dereference any other members.
        mView = null;
    }

    @Override
    public INumberPadTimePicker.State getState() {
        // The model returns the digits defensively copied.
        return new NumberPadTimePickerState(mTimeModel.getDigits(), mTimeModel.count(), mAmPmState);
    }

    @Override
    public void onDigitStored(int digit) {
        // Append the new digit(s) to the formatter
        updateFormattedInputOnDigitInserted(digit);
        mView.updateTimeDisplay(mFormattedInput.toString());
        updateViewEnabledStates();
    }

    @Override
    public void onDigitRemoved(int digit) {
        updateFormattedInputOnDigitDeleted();
        mView.updateTimeDisplay(mFormattedInput.toString());
        updateViewEnabledStates();
    }

    @Override
    public void onDigitsCleared() {
        mFormattedInput.delete(0, mFormattedInput.length());
        mAmPmState = UNSPECIFIED;
        updateViewEnabledStates(); // TOneverDO: before resetting mAmPmState to UNSPECIFIED
        mView.updateTimeDisplay(null);
        if (!mIs24HourMode) {
            mView.updateAmPmDisplay(null);
        }
    }

    private void initialize(@NonNull INumberPadTimePicker.State savedInstanceState) {
        insertDigits(savedInstanceState.getDigits());
        mAmPmState = savedInstanceState.getAmPmState();
    }

    private int count() {
        return mTimeModel.count();
    }

    private boolean is24HourFormat() {
        return mIs24HourMode;
    }
    
    private int getDigitsAsInteger() {
        return mTimeModel.getDigitsAsInteger();
    }

    private void enable(int start, int end) {
        mView.setNumberKeysEnabled(start, end);
        mAllNumberKeysDisabled = start == 0 && end == 0;
    }

    private void insertDigits(int... digits) {
        mTimeModel.storeDigits(digits);
    }

    private void setAltKeysTexts() {
        mView.setLeftAltKeyText(mAltTexts[0]);
        mView.setRightAltKeyText(mAltTexts[1]);
    }

    void updateViewEnabledStates() {
        updateNumberKeysStates();
        updateAltKeysStates();
        updateBackspaceState();
        // TOneverDO: Call before both updateAltKeysStates() and updateNumberKeysStates().
        updateHeaderDisplayFocus();
    }

    private void updateHeaderDisplayFocus() {
        final boolean showHeaderDisplayFocused = !(mAllNumberKeysDisabled && mAltKeysDisabled);
        if (mHeaderDisplayFocused != showHeaderDisplayFocused) {
            mView.setHeaderDisplayFocused(showHeaderDisplayFocused);
            mHeaderDisplayFocused = showHeaderDisplayFocused;
        }
    }

    private void updateBackspaceState() { 
        mView.setBackspaceEnabled(count() > 0);
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
            final int time = getDigitsAsInteger();
            enabled = is24HourFormat() ? time <= 23 : time >= 10 && time <= 12;
        } else if (count() == 3 || count() == MAX_DIGITS) {
            // For the 24-hour clock, no access at all because
            // two more digits (00 or 30) cannot be added without
            // exceeding MAX_DIGITS.
            // For the 12-hour clock, any 3-digit or 4-digit times have
            // complete need of the alt buttons, if AM/PM not already entered.
            enabled = !is24HourFormat() && mAmPmState == UNSPECIFIED;
        }
        mView.setLeftAltKeyEnabled(enabled);
        mView.setRightAltKeyEnabled(enabled);

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

        int time = getDigitsAsInteger();
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
        mFormattedInput.append(String.format("%d", newDigit));
        // Add time separator if necessary, depending on how many digits entered so far
        if (count() == 3) {
            // Insert a time separator
            int digits = getDigitsAsInteger();
            if (digits >= 60 && digits < 100 || digits >= 160 && digits < 200) {
                // From 060-099 (really only to 095, but might as well go up to 100)
                // From 160-199 (really only to 195, but might as well go up to 200),
                // time does not exist if time separator goes at pos. 1
                mFormattedInput.insert(2, mTimeSeparator);
                // These times only apply to the 24-hour clock, and if we're here,
                // the time is not legal yet. So we can't set mAmPmState here for
                // either clock.
                // The 12-hour clock can only have mAmPmState set when AM/PM are clicked.
            } else {
                // A valid time exists if time separator is at pos. 1
                mFormattedInput.insert(1, mTimeSeparator);
                // We can set mAmPmState here (and not in the above case) because
                // the time here is legal in 24-hour clock
                if (is24HourFormat()) {
                    mAmPmState = HRS_24;
                }
            }
        } else if (count() == MAX_DIGITS) {
            int timeSeparatorAt = mFormattedInput.indexOf(mTimeSeparator);
            // Since we now batch update the formatted input whenever
            // digits are inserted, the time separator may legitimately not be
            // present in the formatted input when this is initialized.
            if (timeSeparatorAt != -1) {
                // Time separator needs to move, so remove the time separator previously added
                mFormattedInput.deleteCharAt(timeSeparatorAt);
            }
            mFormattedInput.insert(2, mTimeSeparator);

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
            int value = getDigitsAsInteger();
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
                mFormattedInput.deleteCharAt(mFormattedInput.indexOf(mTimeSeparator));
                mFormattedInput.insert(1, mTimeSeparator);
            } else {
                // previously [06:00, 09:59] or [16:00, 19:59]
                mAmPmState = UNSPECIFIED;
            }
        } else if (count() == 2) {
            // Remove the time separator
            mFormattedInput.deleteCharAt(mFormattedInput.indexOf(mTimeSeparator));
            // No time can be valid with only 2 digits in either system.
            // I don't think we actually need this, but it can't hurt?
            mAmPmState = UNSPECIFIED;
        }
    }
}
