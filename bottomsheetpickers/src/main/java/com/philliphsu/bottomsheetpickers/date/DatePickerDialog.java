package com.philliphsu.bottomsheetpickers.date;

import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;

import java.util.Calendar;

/**
 * Reserved class name for future use.
 */
public abstract class DatePickerDialog extends BottomSheetPickerDialog {

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *            with {@link Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth);
    }

    /**
     * The callback used to notify other date picker components of a change in selected date.
     */
    public interface OnDateChangedListener {

        public void onDateChanged();
    }

}
