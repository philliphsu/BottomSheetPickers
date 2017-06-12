package com.philliphsu.bottomsheetpickers.time.numberpad;

import android.support.annotation.NonNull;

import java.text.DateFormatSymbols;

/**
 * Provides the texts for buttons in the number pad.
 */
final class ButtonTextModel {
    private static final String[] NUMBERS_TEXTS = new String[10];
    private static final int[] ALT_DIGITS_00 = {0,0};
    private static final int[] ALT_DIGITS_30 = {3,0};

    static {
        for (int i = 0; i < 10; i++) {
            NUMBERS_TEXTS[i] = String.format("%d", i);
        }
    }

    private final String[] mAltButtonsTexts = new String[2];
    private final boolean mIs24HourMode;

    ButtonTextModel(@NonNull LocaleModel localeModel, boolean is24HourMode) {
        final String timeSeparator = localeModel.getTimeSeparator(is24HourMode);
        String leftAltText, rightAltText;
        if (is24HourMode) {
            leftAltText = String.format("%02d", 0);
            rightAltText = String.format("%02d", 30);
            leftAltText = localeModel.isLayoutRtl() ?
                    (leftAltText + timeSeparator) : (timeSeparator + leftAltText);
            rightAltText = localeModel.isLayoutRtl() ?
                    (rightAltText + timeSeparator) : (timeSeparator + rightAltText);
        } else {
            String[] amPm = new DateFormatSymbols().getAmPmStrings();
            // TODO: Get localized. Or get the same am/pm strings as the framework.
            leftAltText = amPm[0].length() > 2 ? "AM" : amPm[0];
            rightAltText = amPm[1].length() > 2 ? "PM" : amPm[1];
        }
        mAltButtonsTexts[0] = leftAltText;
        mAltButtonsTexts[1] = rightAltText;
        mIs24HourMode = is24HourMode;
    }

    /**
     * @return A string representing the digit.
     */
    static String text(int digit) {
        return NUMBERS_TEXTS[digit];
    }

    /**
     * @return The integer value of the given key's text.
     */
    static int digit(String text) {
        for (int i = 0; i < NUMBERS_TEXTS.length; i++) {
            if (NUMBERS_TEXTS[i].equals(text)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Cannot convert \""+text+"\" to digit");
    }

    /**
     * @param leftOrRight left (0) or right (1)
     * @return A string for the left or right alt button.
     */
    String altText(int leftOrRight) {
        return mAltButtonsTexts[leftOrRight];
    }

    /**
     * @param altText Only necessary if this model is configured for 24-hour time.
     * @return The sequence of digits represented by the given alt key's text.
     */
    int[] altDigits(String altText) {
        if (!mIs24HourMode || mAltButtonsTexts[0].equals(altText)) {
            return ALT_DIGITS_00;
        } else if (mAltButtonsTexts[1].equals(altText)) {
            return ALT_DIGITS_30;
        }
        throw new IllegalArgumentException("Cannot convert \""+altText+"\" to alt digits");
    }
}
