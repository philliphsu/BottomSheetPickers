package com.philliphsu.bottomsheetpickers.date;

import android.text.format.Time;

import java.util.Calendar;
import java.util.Date;

/**
 * A convenience class to represent a specific date.
 */
final class CalendarDay {
    private Calendar calendar;
    private Time time;
    int year;
    int month;
    int day;

    CalendarDay() {
        setTime(System.currentTimeMillis());
    }

    CalendarDay(long timeInMillis) {
        setTime(timeInMillis);
    }

    CalendarDay(Calendar calendar) {
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    CalendarDay(int year, int month, int day) {
        setDay(year, month, day);
    }

    void set(CalendarDay date) {
        year = date.year;
        month = date.month;
        day = date.day;
    }

    void setDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    Date getDate() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    synchronized void setJulianDay(int julianDay) {
        if (time == null) {
            time = new Time();
        }
        time.setJulianDay(julianDay);
        setTime(time.toMillis(false));
    }

    private void setTime(long timeInMillis) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        calendar.setTimeInMillis(timeInMillis);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
}
