/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.philliphsu.bottomsheetpickers.date;

import android.support.annotation.ColorInt;

import java.util.Calendar;

/**
 * Wrapper around {@link DatePickerDialog} to maintain backward compatibility with existing client code.
 *
 * @deprecated Use {@link DatePickerDialog} instead.
 */
public class BottomSheetDatePickerDialog extends DatePickerDialog {

    public static BottomSheetDatePickerDialog newInstance(OnDateSetListener callBack, int year,
                                               int monthOfYear, int dayOfMonth) {
        BottomSheetDatePickerDialog ret = new BottomSheetDatePickerDialog();
        ret.initialize(callBack, year, monthOfYear, dayOfMonth);
        return ret;
    }

    /**
     * Wrapper around {@link com.philliphsu.bottomsheetpickers.date.DatePickerDialog.Builder
     * DatePickerDialog.Builder} to maintain forward compatibility with potential future client code.
     */
    public static final class Builder extends DatePickerDialog.Builder {
        /**
         * @param listener    How the parent is notified that the date is set.
         * @param year        The initial year of the dialog.
         * @param monthOfYear The initial month of the dialog.
         * @param dayOfMonth  The initial day of the dialog.
         */
        public Builder(OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            super(listener, year, monthOfYear, dayOfMonth);
        }

        @Override
        public Builder setFirstDayOfWeek(int startOfWeek) {
            return (Builder) super.setFirstDayOfWeek(startOfWeek);
        }

        @Override
        public Builder setYearRange(int startYear, int endYear) {
            return (Builder) super.setYearRange(startYear, endYear);
        }

        @Override
        public Builder setMinDate(Calendar calendar) {
            return (Builder) super.setMinDate(calendar);
        }

        @Override
        public Builder setMaxDate(Calendar calendar) {
            return (Builder) super.setMaxDate(calendar);
        }

        @Override
        public Builder setHeaderTextColorSelected(@ColorInt int color) {
            return (Builder) super.setHeaderTextColorSelected(color);
        }

        @Override
        public Builder setHeaderTextColorUnselected(@ColorInt int color) {
            return (Builder) super.setHeaderTextColorUnselected(color);
        }

        @Override
        public Builder setDayOfWeekHeaderTextColorSelected(@ColorInt int color) {
            return (Builder) super.setDayOfWeekHeaderTextColorSelected(color);
        }

        @Override
        public Builder setDayOfWeekHeaderTextColorUnselected(@ColorInt int color) {
            return (Builder) super.setDayOfWeekHeaderTextColorUnselected(color);
        }

        @Override
        public Builder setAccentColor(int accentColor) {
            return (Builder) super.setAccentColor(accentColor);
        }

        @Override
        public Builder setBackgroundColor(int backgroundColor) {
            return (Builder) super.setBackgroundColor(backgroundColor);
        }

        @Override
        public Builder setHeaderColor(int headerColor) {
            return (Builder) super.setHeaderColor(headerColor);
        }

        @Override
        public Builder setHeaderTextDark(boolean headerTextDark) {
            return (Builder) super.setHeaderTextDark(headerTextDark);
        }

        @Override
        public Builder setThemeDark(boolean themeDark) {
            return (Builder) super.setThemeDark(themeDark);
        }

        @Override
        public BottomSheetDatePickerDialog build() {
            BottomSheetDatePickerDialog dialog = newInstance(mListener, mYear, mMonthOfYear, mDayOfMonth);
            super_build(dialog);
            return dialog;
        }
    }
}
