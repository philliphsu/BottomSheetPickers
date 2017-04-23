package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.support.annotation.NonNull;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import java.text.DateFormatSymbols;

/**
 * Provides the texts for buttons in the number pad.
 */
final class ButtonTextModel {
    private static final String[] NUMBERS_TEXTS = new String[10];

    static {
        for (int i = 0; i < 10; i++) {
            NUMBERS_TEXTS[i] = String.format("%d", i);
        }
    }

    private final String[] mAltButtonsTexts = new String[2];

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
    }

    /**
     * @return A string representing the digit.
     */
    static String text(int digit) {
        return NUMBERS_TEXTS[digit];
    }

    /**
     * @param leftOrRight left (0) or right (1)
     * @return A string for the left or right alt button.
     */
    String altText(int leftOrRight) {
        return mAltButtonsTexts[leftOrRight];
    }
}
