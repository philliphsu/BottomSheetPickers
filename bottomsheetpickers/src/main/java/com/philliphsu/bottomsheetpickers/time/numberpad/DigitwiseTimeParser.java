package com.philliphsu.bottomsheetpickers.time.numberpad;

/**
 * Helper class that parses the hour and minute from {@link DigitwiseTimeModel}.
 */
final class DigitwiseTimeParser {

    private final DigitwiseTimeModel mModel;

    DigitwiseTimeParser(DigitwiseTimeModel model) {
        mModel = model;
    }

    /** Returns the hour of day (0-23) regardless of clock system */
    int getHour(@AmPmState int amPmState) {
        if (!checkTimeValid(amPmState))
            throw new IllegalStateException("Cannot call hourOfDay() until legal time inputted");
        int hours = count() < 4 ? valueAt(0) : valueAt(0) * 10 + valueAt(1);
        if (hours == 12) {
            switch (amPmState) {
                case AmPmState.AM:
                    return 0;
                case AmPmState.PM:
                case AmPmState.HRS_24:
                    return 12;
                default:
                    break;
            }
        }

        // AM/PM clock needs value offset
        return hours + (amPmState == AmPmState.PM ? 12 : 0);
    }

    int getMinute(@AmPmState int amPmState) {
        if (!checkTimeValid(amPmState))
            throw new IllegalStateException("Cannot call minute() until legal time inputted");
        return count() < 4 ? valueAt(1) * 10 + valueAt(2) : valueAt(2) * 10 + valueAt(3);
    }

    /**
     * Checks if the input stored so far qualifies as a valid time.
     * For this to return {@code true}, the hours, minutes AND AM/PM
     * state must be set.
     */
    boolean checkTimeValid(@AmPmState int amPmState) {
        // While the test looks bare, it is actually comprehensive.
        // mAmPmState will remain UNSPECIFIED until a legal
        // sequence of digits is inputted, no matter the clock system in use.
        // TODO: So if that's the case, do we actually need 'count() < 3' here? Or better yet,
        // can we simplify the code to just 'return mAmPmState != UNSPECIFIED'?
        if (amPmState == AmPmState.UNSPECIFIED || amPmState == AmPmState.HRS_24 && count() < 3) {
            return false;
        }
        // AM or PM can only be set if the time was already valid previously, so we don't need
        // to check for them.
        return true;
    }

    private int valueAt(int i) {
        return mModel.getDigit(i);
    }

    private int count() {
        return mModel.count();
    }
}
