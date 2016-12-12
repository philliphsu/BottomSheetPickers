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

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.HapticFeedbackController;
import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.date.MonthAdapter.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.DAY_PICKER_INDEX;

/**
 * Dialog allowing users to select a date.
 */
public class BottomSheetDatePickerDialog extends DatePickerDialog implements
        OnClickListener, DatePickerController {

    private static final String TAG = "DatePickerDialog";

    private static final int UNINITIALIZED = -1;
    private static final int MONTH_AND_DAY_VIEW = 0;
    private static final int YEAR_VIEW = 1;

    private static final String KEY_SELECTED_YEAR = "year";
    private static final String KEY_SELECTED_MONTH = "month";
    private static final String KEY_SELECTED_DAY = "day";
    private static final String KEY_LIST_POSITION = "list_position";
    private static final String KEY_WEEK_START = "week_start";
    private static final String KEY_YEAR_START = "year_start";
    private static final String KEY_YEAR_END = "year_end";
    private static final String KEY_CURRENT_VIEW = "current_view";
    private static final String KEY_LIST_POSITION_OFFSET = "list_position_offset";
    private static final String KEY_DAY_PICKER_CURRENT_INDEX = "day_picker_current_index";

    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private static final int ANIMATION_DURATION = 300;
    private static final int ANIMATION_DELAY = 500;

    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());

    /* Used for formatting dates via DateUtils. If we don't use these, the internal
     * methods of DateUtils would instantiate new instances each time we format a date. */
    private static final StringBuilder STRING_BUILDER = new StringBuilder(50);
    private static final Formatter FORMATTER = new Formatter(STRING_BUILDER, Locale.getDefault());

    private final Calendar mCalendar = Calendar.getInstance();
    private OnDateSetListener mCallBack;
    private HashSet<OnDateChangedListener> mListeners = new HashSet<OnDateChangedListener>();

    private AccessibleDateAnimator mAnimator;

    private TextView mDayOfWeekView;
    private LinearLayout mMonthDayYearView;
    private TextView mFirstTextView;
    private TextView mSecondTextView;
    private PagingDayPickerView mDayPickerView;
    private YearPickerView mYearPickerView;
    private Button mDoneButton;
    private Button mCancelButton;

    private int mCurrentView = UNINITIALIZED;

    private int mWeekStart = mCalendar.getFirstDayOfWeek();
    private int mMinYear = DEFAULT_START_YEAR;
    private int mMaxYear = DEFAULT_END_YEAR;
    private Calendar mMinDate;
    private Calendar mMaxDate;

    private HapticFeedbackController mHapticFeedbackController;

    private boolean mDelayAnimation = true;

    // Accessibility strings.
    private String mDayPickerDescription;
    private String mSelectDay;
    private String mYearPickerDescription;
    private String mSelectYear;

    // Relative positions of (MD) and Y in the locale's date formatting style.
    private int mLocaleMonthDayIndex;
    private int mLocaleYearIndex;

    public BottomSheetDatePickerDialog() {
        // Empty constructor required for dialog fragment.
    }

    /**
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public static BottomSheetDatePickerDialog newInstance(OnDateSetListener callBack, int year,
                                                          int monthOfYear,
                                                          int dayOfMonth) {
        BottomSheetDatePickerDialog ret = new BottomSheetDatePickerDialog();
        ret.initialize(callBack, year, monthOfYear, dayOfMonth);
        return ret;
    }

    public void initialize(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        mCallBack = callBack;
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = getActivity();
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setCancelable(false);
        if (savedInstanceState != null) {
            mCalendar.set(Calendar.YEAR, savedInstanceState.getInt(KEY_SELECTED_YEAR));
            mCalendar.set(Calendar.MONTH, savedInstanceState.getInt(KEY_SELECTED_MONTH));
            mCalendar.set(Calendar.DAY_OF_MONTH, savedInstanceState.getInt(KEY_SELECTED_DAY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SELECTED_YEAR, mCalendar.get(Calendar.YEAR));
        outState.putInt(KEY_SELECTED_MONTH, mCalendar.get(Calendar.MONTH));
        outState.putInt(KEY_SELECTED_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
        outState.putInt(KEY_WEEK_START, mWeekStart);
        outState.putInt(KEY_YEAR_START, mMinYear);
        outState.putInt(KEY_YEAR_END, mMaxYear);
        outState.putInt(KEY_CURRENT_VIEW, mCurrentView);
        int listPosition = -1;
        if (mCurrentView == MONTH_AND_DAY_VIEW) {
            listPosition = mDayPickerView.getPagerPosition();
            outState.putInt(KEY_DAY_PICKER_CURRENT_INDEX, mDayPickerView.getCurrentView());
        } else if (mCurrentView == YEAR_VIEW) {
            listPosition = mYearPickerView.getFirstVisiblePosition();
            outState.putInt(KEY_LIST_POSITION_OFFSET, mYearPickerView.getFirstPositionOffset());
        }
        outState.putInt(KEY_LIST_POSITION, listPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        mDayOfWeekView = (TextView) view.findViewById(R.id.date_picker_header);
        mDayOfWeekView.setTypeface(Utils.SANS_SERIF_LIGHT_BOLD);
        mMonthDayYearView = (LinearLayout) view.findViewById(R.id.date_picker_month_day_year);
        mFirstTextView = (TextView) view.findViewById(R.id.date_picker_first_textview);
        mFirstTextView.setOnClickListener(this);
        mFirstTextView.setTypeface(Utils.SANS_SERIF_LIGHT_BOLD);
        mSecondTextView = (TextView) view.findViewById(R.id.date_picker_second_textview);
        mSecondTextView.setOnClickListener(this);
        mSecondTextView.setTypeface(Utils.SANS_SERIF_LIGHT_BOLD);

        int listPosition = -1;
        int listPositionOffset = 0;
        int currentView = MONTH_AND_DAY_VIEW;
        int dayPickerCurrentView = DAY_PICKER_INDEX;
        if (savedInstanceState != null) {
            mWeekStart = savedInstanceState.getInt(KEY_WEEK_START);
            mMinYear = savedInstanceState.getInt(KEY_YEAR_START);
            mMaxYear = savedInstanceState.getInt(KEY_YEAR_END);
            currentView = savedInstanceState.getInt(KEY_CURRENT_VIEW);
            listPosition = savedInstanceState.getInt(KEY_LIST_POSITION);
            listPositionOffset = savedInstanceState.getInt(KEY_LIST_POSITION_OFFSET);
            dayPickerCurrentView = savedInstanceState.getInt(KEY_DAY_PICKER_CURRENT_INDEX);
        }

        final Activity activity = getActivity();
        mDayPickerView = new PagingDayPickerView(activity, this, mThemeDark);
        mYearPickerView = new YearPickerView(activity, this);
        mYearPickerView.setTheme(activity, mThemeDark);

        Resources res = getResources();
        mDayPickerDescription = res.getString(R.string.day_picker_description);
        mSelectDay = res.getString(R.string.select_day);
        mYearPickerDescription = res.getString(R.string.year_picker_description);
        mSelectYear = res.getString(R.string.select_year);

        mAnimator = (AccessibleDateAnimator) view.findViewById(R.id.animator);
        mAnimator.addView(mDayPickerView);
        mAnimator.addView(mYearPickerView);
        mAnimator.setDateMillis(mCalendar.getTimeInMillis());
        // TODO: Replace with animation decided upon by the design team.
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(ANIMATION_DURATION);
        mAnimator.setInAnimation(animation);
        // TODO: Replace with animation decided upon by the design team.
        Animation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(ANIMATION_DURATION);
        mAnimator.setOutAnimation(animation2);

        mDoneButton = (Button) view.findViewById(R.id.done);
        mDoneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                if (mCallBack != null) {
                    mCallBack.onDateSet(BottomSheetDatePickerDialog.this, mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                }
                dismiss();
            }
        });

        mCancelButton = (Button) view.findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Theme-specific configurations.
        if (mThemeDark) {
            // This is so the margin gets colored as well.
            view.setBackgroundColor(mDarkGray);
            mAnimator.setBackgroundColor(mDarkGray);
            int selectableItemBg = ContextCompat.getColor(activity, R.color.selectable_item_background_dark);
            Utils.setColorControlHighlight(mCancelButton, selectableItemBg);
            Utils.setColorControlHighlight(mDoneButton, selectableItemBg);
        } else {
            // Setup action button text colors.
            mCancelButton.setTextColor(mAccentColor);
            mDoneButton.setTextColor(mAccentColor);
        }

        // Configurations for both themes.
        View selectedDateLayout = view.findViewById(R.id.day_picker_selected_date_layout);
        selectedDateLayout.setBackgroundColor(mThemeDark ? mLightGray : mAccentColor);

        determineLocale_MD_Y_Indices();
        updateDisplay(false);
        setCurrentView(currentView);

        if (listPosition != -1) {
            if (currentView == MONTH_AND_DAY_VIEW) {
                mDayPickerView.postSetSelection(listPosition, false);
            } else if (currentView == YEAR_VIEW) {
                mYearPickerView.postSetSelectionFromTop(listPosition, listPositionOffset);
            }
        }
        mDayPickerView.postSetupCurrentView(dayPickerCurrentView, false);

        mHapticFeedbackController = new HapticFeedbackController(activity);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mHapticFeedbackController.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mHapticFeedbackController.stop();
    }

    private void setCurrentView(final int viewIndex) {
        long millis = mCalendar.getTimeInMillis();

        switch (viewIndex) {
            case MONTH_AND_DAY_VIEW:
                mDayPickerView.onDateChanged();
                if (mCurrentView != viewIndex) {
                    updateHeaderSelectedView(MONTH_AND_DAY_VIEW);
                    mAnimator.setDisplayedChild(MONTH_AND_DAY_VIEW);
                    mCurrentView = viewIndex;
                }

                int flags = DateUtils.FORMAT_SHOW_DATE;
                String dayString = DateUtils.formatDateTime(getActivity(), millis, flags);
                mAnimator.setContentDescription(mDayPickerDescription + ": " + dayString);
                Utils.tryAccessibilityAnnounce(mAnimator, mSelectDay);
                break;
            case YEAR_VIEW:
                mYearPickerView.onDateChanged();
                if (mCurrentView != viewIndex) {
                    updateHeaderSelectedView(YEAR_VIEW);
                    mAnimator.setDisplayedChild(YEAR_VIEW);
                    mCurrentView = viewIndex;
                }

                CharSequence yearString = YEAR_FORMAT.format(millis);
                mAnimator.setContentDescription(mYearPickerDescription + ": " + yearString);
                Utils.tryAccessibilityAnnounce(mAnimator, mSelectYear);
                break;
        }
    }

    private void updateHeaderSelectedView(final int viewIndex) {
        switch (viewIndex) {
            case MONTH_AND_DAY_VIEW:
                mFirstTextView.setSelected(mLocaleMonthDayIndex == 0);
                mSecondTextView.setSelected(mLocaleMonthDayIndex != 0);
                break;
            case YEAR_VIEW:
                mFirstTextView.setSelected(mLocaleYearIndex == 0);
                mSecondTextView.setSelected(mLocaleYearIndex != 0);
                break;
        }
    }

    /**
     * Determine the relative positions of (MD) and Y according to the formatting style
     * of the current locale.
     */
    private void determineLocale_MD_Y_Indices() {
        String formattedDate = formatMonthDayYear(mCalendar);
        // Get the (MD) and Y parts of the formatted date in the current locale,
        // so that we can compare their relative positions.
        //
        // You may be wondering why we need this method at all.
        // "Just split() the formattedDate string around the year delimiter
        // to get the two parts in an array already positioned correctly!
        // Then setText() on mFirstTextView and mSecondTextView with the contents of that array!"
        // That is harder than it sounds.
        // Different locales use different year delimiters, and some don't use one at all.
        // For example, a fully formatted date in the French locale is "30 juin 2009".
        String monthAndDay = formatMonthAndDay(mCalendar);
        String year = extractYearFromFormattedDate(formattedDate, monthAndDay);

        // All locales format the M and D together; which comes
        // first is not a necessary consideration for the comparison.
        if (formattedDate.indexOf(monthAndDay) < formattedDate.indexOf(year/*not null*/)) {
            mLocaleMonthDayIndex = 0;
            mLocaleYearIndex = 1;
        } else {
            mLocaleYearIndex = 0;
            mLocaleMonthDayIndex = 1;
        }
    }

    private static String formatMonthDayYear(Calendar calendar) {
        // TODO: Use the STRING_BUILDER and FORMATTER.
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR;
        return formatDateTime(calendar, flags);
    }

    private static String formatMonthAndDay(Calendar calendar) {
        // TODO: Use the STRING_BUILDER and FORMATTER.
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_NO_YEAR;
        return formatDateTime(calendar, flags);
    }

    private String extractYearFromFormattedDate(String formattedDate, String monthAndDay) {
        String[] parts = formattedDate.split(monthAndDay);
        for (String part : parts) {
            // If the locale's date format is (MD)Y, then split(MD) = {"", Y}.
            // If it is Y(MD), then split(MD) = {Y}. "Trailing empty strings are
            // [...] not included in the resulting array."
            if (!part.isEmpty()) {
                return part;
            }
        }
        // We will NEVER reach here, as long as the parameters are valid strings.
        // We don't want this because it is not localized.
        return YEAR_FORMAT.format(mCalendar.getTime());
    }

    private void updateDisplay(boolean announce) {
        if (mDayOfWeekView != null) {
            mDayOfWeekView.setText(mCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.LONG, Locale.getDefault()));
        }
        String monthAndDay = formatMonthAndDay(mCalendar);
        String year = extractYearFromFormattedDate(formatMonthDayYear(mCalendar), monthAndDay);
        mFirstTextView.setText(mLocaleMonthDayIndex == 0 ? monthAndDay : year);
        mSecondTextView.setText(mLocaleMonthDayIndex == 0 ? year : monthAndDay);

        // Accessibility.
        long millis = mCalendar.getTimeInMillis();
        mAnimator.setDateMillis(millis);
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
        String monthAndDayText = DateUtils.formatDateTime(getActivity(), millis, flags);
        mMonthDayYearView.setContentDescription(monthAndDayText);

        if (announce) {
            flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
            String fullDateText = DateUtils.formatDateTime(getActivity(), millis, flags);
            Utils.tryAccessibilityAnnounce(mAnimator, fullDateText);
        }
    }

    private static String formatDateTime(Calendar calendar, int flags) {
        return DateUtils.formatDateTime(null, calendar.getTimeInMillis(), flags);
    }

    public void setFirstDayOfWeek(int startOfWeek) {
        if (startOfWeek < Calendar.SUNDAY || startOfWeek > Calendar.SATURDAY) {
            throw new IllegalArgumentException("Value must be between Calendar.SUNDAY and " +
                    "Calendar.SATURDAY");
        }
        mWeekStart = startOfWeek;
        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    public void setYearRange(int startYear, int endYear) {
        if (endYear <= startYear) {
            throw new IllegalArgumentException("Year end must be larger than year start");
        }
        mMinYear = startYear;
        mMaxYear = endYear;
        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    /**
     * Sets the minimal date supported by this DatePicker. Dates before (but not including) the
     * specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object set to the year, month, day desired as the mindate.
     */
    public void setMinDate(Calendar calendar) {
        mMinDate = calendar;
        setYearRange(calendar.get(Calendar.YEAR), mMaxYear);
    }

    /**
     * @return The minimal date supported by this DatePicker. Null if it has not been set.
     */
    @Override
    public Calendar getMinDate() {
        return mMinDate;
    }

    /**
     * Sets the minimal date supported by this DatePicker. Dates after (but not including) the
     * specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object set to the year, month, day desired as the maxdate.
     */
    public void setMaxDate(Calendar calendar) {
        mMaxDate = calendar;
        setYearRange(mMinYear, calendar.get(Calendar.YEAR));
    }

    /**
     * @return The maximal date supported by this DatePicker. Null if it has not been set.
     */
    @Override
    public Calendar getMaxDate() {
        return mMaxDate;
    }

    public void setOnDateSetListener(OnDateSetListener listener) {
        mCallBack = listener;
    }

    // If the newly selected month / year does not contain the currently selected day number,
    // change the selected day number to the last day of the selected month or year.
    //      e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
    //      e.g. Switching from 2012 to 2013 when Feb 29, 2012 is selected -> Feb 28, 2013
    private void adjustDayInMonthIfNeeded(int month, int year) {
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        int daysInMonth = Utils.getDaysInMonth(month, year);
        if (day > daysInMonth) {
            mCalendar.set(Calendar.DAY_OF_MONTH, daysInMonth);
        }
    }

    @Override
    public void onClick(View v) {
        tryVibrate();
        if (v.getId() == R.id.date_picker_second_textview) {
            setCurrentView(mLocaleMonthDayIndex == 0 ? YEAR_VIEW : MONTH_AND_DAY_VIEW);
        } else if (v.getId() == R.id.date_picker_first_textview) {
            setCurrentView(mLocaleMonthDayIndex == 0 ? MONTH_AND_DAY_VIEW : YEAR_VIEW);
        }
    }

    @Override
    public void onYearSelected(int year) {
        adjustDayInMonthIfNeeded(mCalendar.get(Calendar.MONTH), year);
        mCalendar.set(Calendar.YEAR, year);
        updatePickers();
        setCurrentView(MONTH_AND_DAY_VIEW);
        updateDisplay(true);
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        updatePickers();
        updateDisplay(true);
    }

    @Override
    public void onMonthYearSelected(int month, int year) {
        adjustDayInMonthIfNeeded(month, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.YEAR, year);
        updatePickers();
        // Even though the MonthPickerView is already contained in this index,
        // keep this call here for accessibility announcement of the new selection.
        setCurrentView(MONTH_AND_DAY_VIEW);
        updateDisplay(true);
    }

    private void updatePickers() {
        Iterator<OnDateChangedListener> iterator = mListeners.iterator();
        while (iterator.hasNext()) {
            iterator.next().onDateChanged();
        }
    }

    @Override
    public CalendarDay getSelectedDay() {
        return new CalendarDay(mCalendar);
    }

    @Override
    public int getMinYear() {
        return mMinYear;
    }

    @Override
    public int getMaxYear() {
        return mMaxYear;
    }

    @Override
    public int getFirstDayOfWeek() {
        return mWeekStart;
    }

    @Override
    public void registerOnDateChangedListener(OnDateChangedListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void unregisterOnDateChangedListener(OnDateChangedListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void tryVibrate() {
        mHapticFeedbackController.tryVibrate();
    }

    @Override
    protected int contentLayout() {
        return R.layout.date_picker_dialog;
    }
}
