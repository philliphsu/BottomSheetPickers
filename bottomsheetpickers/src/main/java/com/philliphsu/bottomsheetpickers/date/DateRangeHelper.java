package com.philliphsu.bottomsheetpickers.date;

import java.util.Calendar;

/**
 * Utility class for determining if a date given by its year, month, and day
 * is within the range set by a {@link DatePickerController}.
 */
final class DateRangeHelper {

    private final DatePickerController mController;

    DateRangeHelper(DatePickerController controller) {
        mController = controller;
    }

    /**
     * @return true if the specified year/month/day are within the range set by minDate and maxDate.
     * If one or either have not been set, they are considered as Integer.MIN_VALUE and
     * Integer.MAX_VALUE.
     */
    boolean isOutOfRange(int year, int month, int day) {
        if (isBeforeMin(year, month, day)) {
            return true;
        } else if (isAfterMax(year, month, day)) {
            return true;
        }

        return false;
    }

    private boolean isBeforeMin(int year, int month, int day) {
        if (mController == null) {
            return false;
        }
        Calendar minDate = mController.getMinDate();
        if (minDate == null) {
            return false;
        }

        if (year < minDate.get(Calendar.YEAR)) {
            return true;
        } else if (year > minDate.get(Calendar.YEAR)) {
            return false;
        }

        if (month < minDate.get(Calendar.MONTH)) {
            return true;
        } else if (month > minDate.get(Calendar.MONTH)) {
            return false;
        }

        if (day < minDate.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isAfterMax(int year, int month, int day) {
        if (mController == null) {
            return false;
        }
        Calendar maxDate = mController.getMaxDate();
        if (maxDate == null) {
            return false;
        }

        if (year > maxDate.get(Calendar.YEAR)) {
            return true;
        } else if (year < maxDate.get(Calendar.YEAR)) {
            return false;
        }

        if (month > maxDate.get(Calendar.MONTH)) {
            return true;
        } else if (month < maxDate.get(Calendar.MONTH)) {
            return false;
        }

        if (day > maxDate.get(Calendar.DAY_OF_MONTH)) {
            return true;
        } else {
            return false;
        }
    }

}
