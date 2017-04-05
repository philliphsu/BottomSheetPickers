package com.philliphsu.bottomsheetpickers.view.numberpad;

import java.util.Arrays;

/**
 * Model that encapsulates data pertaining to the inputted time
 * in a number pad time picker.
 */
final class DigitwiseTimeModel {
    private final int[] digits = new int[4];

    private int count;

    DigitwiseTimeModel() {
        Arrays.fill(digits, -1);
    }

    void storeDigit(int digit) {
        digits[count] = digit;
        count++;
    }

    int getDigit(int at) {
        return digits[at];
    }

    void removeDigit() {
        if (count > 0) {
            count--; // move the cursor back
            digits[count] = -1;
        }
    }

    int count() {
        return count;
    }
}
