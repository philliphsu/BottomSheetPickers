package com.philliphsu.bottomsheetpickers.date;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.BottomSheetPickerDialog;
import com.philliphsu.bottomsheetpickers.HapticFeedbackController;
import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

import static com.philliphsu.bottomsheetpickers.date.DateFormatHelper.formatDate;
import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.DAY_PICKER_INDEX;

/**
 * Dialog allowing users to select a date.
 */
public class DatePickerDialog extends BottomSheetPickerDialog implements
        View.OnClickListener, 
        DatePickerController, 
        View.OnTouchListener, 
        AbsListView.OnScrollListener {
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
    private static final String KEY_MIN_DATE_MILLIS = "min_date_millis";
    private static final String KEY_MAX_DATE_MILLIS = "max_date_millis";
    private static final String KEY_HEADER_TEXT_COLOR_SELECTED = "header_text_color_selected";
    private static final String KEY_HEADER_TEXT_COLOR_UNSELECTED = "header_text_color_unselected";
    private static final String KEY_DAY_OF_WEEK_HEADER_TEXT_COLOR_SELECTED = "day_of_week_header_text_color_selected";
    private static final String KEY_DAY_OF_WEEK_HEADER_TEXT_COLOR_UNSELECTED = "day_of_week_header_text_color_unselected";

    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private static final int ANIMATION_DURATION = 300;
    private static final int ANIMATION_DELAY = 500;

    private static SimpleDateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());
    private static SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());

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
    private @Nullable Calendar mMinDate;
    private @Nullable Calendar mMaxDate;

    private HapticFeedbackController mHapticFeedbackController;
    private CalendarDay mSelectedDay;

    private boolean mDelayAnimation = true;

    // Accessibility strings.
    private String mDayPickerDescription;
    private String mSelectDay;
    private String mYearPickerDescription;
    private String mSelectYear;

    // Relative positions of (MD) and Y in the locale's date formatting style.
    private int mLocaleMonthDayIndex;
    private int mLocaleYearIndex;

    private int mHeaderTextColorSelected;
    private int mHeaderTextColorUnselected;
    private int mDayOfWeekHeaderTextColorSelected;
    private int mDayOfWeekHeaderTextColorUnselected;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param dialog The dialog associated with this listener.
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

    public DatePickerDialog() {
        // Empty constructor required for dialog fragment.
    }

    /**
     * @param callBack    How the parent is notified that the date is set.
     * @param year        The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth  The initial day of the dialog.
     */
    public static DatePickerDialog newInstance(OnDateSetListener callBack, int year, 
                                               int monthOfYear, int dayOfMonth) {
        DatePickerDialog ret = new DatePickerDialog();
        ret.initialize(callBack, year, monthOfYear, dayOfMonth);
        return ret;
    }

    void initialize(OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
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
        if (mMinDate != null) {
            outState.putLong(KEY_MIN_DATE_MILLIS, mMinDate.getTimeInMillis());
        }
        if (mMaxDate != null) {
            outState.putLong(KEY_MAX_DATE_MILLIS, mMaxDate.getTimeInMillis());
        }
        outState.putInt(KEY_HEADER_TEXT_COLOR_SELECTED, mHeaderTextColorSelected);
        outState.putInt(KEY_HEADER_TEXT_COLOR_UNSELECTED, mHeaderTextColorUnselected);
        outState.putInt(KEY_DAY_OF_WEEK_HEADER_TEXT_COLOR_SELECTED, mDayOfWeekHeaderTextColorSelected);
        outState.putInt(KEY_DAY_OF_WEEK_HEADER_TEXT_COLOR_UNSELECTED, mDayOfWeekHeaderTextColorUnselected);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        mDayOfWeekView = (TextView) view.findViewById(R.id.bsp_date_picker_header);
        mDayOfWeekView.setTypeface(Utils.SANS_SERIF_LIGHT_BOLD);
        mMonthDayYearView = (LinearLayout) view.findViewById(R.id.bsp_date_picker_month_day_year);
        mFirstTextView = (TextView) view.findViewById(R.id.bsp_date_picker_first_textview);
        mFirstTextView.setOnClickListener(this);
        mFirstTextView.setTypeface(Utils.SANS_SERIF_LIGHT_BOLD);
        mSecondTextView = (TextView) view.findViewById(R.id.bsp_date_picker_second_textview);
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
            mHeaderTextColorSelected = savedInstanceState.getInt(KEY_HEADER_TEXT_COLOR_SELECTED);
            mHeaderTextColorUnselected = savedInstanceState.getInt(KEY_HEADER_TEXT_COLOR_UNSELECTED);
            mDayOfWeekHeaderTextColorSelected = savedInstanceState.getInt(
                    KEY_DAY_OF_WEEK_HEADER_TEXT_COLOR_SELECTED);
            mDayOfWeekHeaderTextColorUnselected = savedInstanceState.getInt(
                    KEY_DAY_OF_WEEK_HEADER_TEXT_COLOR_UNSELECTED);

            // Don't restore both in one block because it may well be that only one was set.
            if (savedInstanceState.containsKey(KEY_MIN_DATE_MILLIS)) {
                mMinDate = Calendar.getInstance();
                mMinDate.setTimeInMillis(savedInstanceState.getLong(KEY_MIN_DATE_MILLIS));
            }
            if (savedInstanceState.containsKey(KEY_MAX_DATE_MILLIS)) {
                mMaxDate = Calendar.getInstance();
                mMaxDate.setTimeInMillis(savedInstanceState.getLong(KEY_MAX_DATE_MILLIS));
            }
        }

        final Activity activity = getActivity();
        mDayPickerView = new PagingDayPickerView(activity, this, mThemeDark, mAccentColor);
        mYearPickerView = new YearPickerView(activity, this);
        mYearPickerView.setTheme(activity, mThemeDark);
        mYearPickerView.setAccentColor(mAccentColor);

        // Listen for touches so that we can enable/disable the bottom sheet's cancelable
        // state based on the location of the touch event.
        //
        // Both views MUST have the listener set. Why? Consider each call individually.
        // If we only set the listener on the first call, touch events on the ListView would
        // not be detected since it handles and consumes scroll events on its own.
        // If we only set the listener on the second call, touch events would only be detected
        // within the ListView and not in other views in our hierarchy.
        view.setOnTouchListener(this);
        mYearPickerView.setOnTouchListener(this);
        // Listen for scroll end events, so that we can restore the cancelable state immediately.
        mYearPickerView.setOnScrollListener(this);

        Resources res = getResources();
        mDayPickerDescription = res.getString(R.string.bsp_day_picker_description);
        mSelectDay = res.getString(R.string.bsp_select_day);
        mYearPickerDescription = res.getString(R.string.bsp_year_picker_description);
        mSelectYear = res.getString(R.string.bsp_select_year);

        mAnimator = (AccessibleDateAnimator) view.findViewById(R.id.bsp_animator);
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

        mDoneButton = (Button) view.findViewById(R.id.bsp_done);
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryVibrate();
                if (mCallBack != null) {
                    mCallBack.onDateSet(DatePickerDialog.this, mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                }
                dismiss();
            }
        });

        mCancelButton = (Button) view.findViewById(R.id.bsp_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Setup action button text colors.
        mCancelButton.setTextColor(mAccentColor);
        mDoneButton.setTextColor(mAccentColor);

        mAnimator.setBackgroundColor(mBackgroundColor);
        mDayPickerView.setAccentColor(mAccentColor);
        view.findViewById(R.id.bsp_day_picker_selected_date_layout).setBackgroundColor(mHeaderColor);

        if (mThemeDark) {
            final int selectableItemBg = ContextCompat.getColor(activity,
                    R.color.bsp_selectable_item_background_dark);
            Utils.setColorControlHighlight(mCancelButton, selectableItemBg);
            Utils.setColorControlHighlight(mDoneButton, selectableItemBg);
        }

        // Before setting any custom header text colors, check if the dark header text theme was
        // requested and apply it.
        if (mHeaderTextDark) {
            final ColorStateList colors = ContextCompat.getColorStateList(activity,
                    R.color.bsp_date_picker_selector_light);
            mDayOfWeekView.setTextColor(colors);
            mFirstTextView.setTextColor(colors);
            mSecondTextView.setTextColor(colors);
        }

        // Prepare default header text colors.
        final int defaultSelectedColor = getDefaultHeaderTextColorSelected();
        final int defaultUnselectedColor = getDefaultHeaderTextColorUnselected();

        // Apply the custom colors for the header texts, if applicable.
        if (mHeaderTextColorSelected != 0 || mHeaderTextColorUnselected != 0) {
            final int selectedColor = mHeaderTextColorSelected != 0
                    ? mHeaderTextColorSelected : defaultSelectedColor;
            final int unselectedColor = mHeaderTextColorUnselected != 0
                    ? mHeaderTextColorUnselected : defaultUnselectedColor;
            final ColorStateList stateColors = createColorStateList(selectedColor, unselectedColor);
            mFirstTextView.setTextColor(stateColors);
            mSecondTextView.setTextColor(stateColors);
        }

        // Apply the custom colors for the day-of-week header text, if applicable.
        if (mDayOfWeekHeaderTextColorSelected != 0 || mDayOfWeekHeaderTextColorUnselected != 0) {
            final int selectedColor = mDayOfWeekHeaderTextColorSelected != 0
                    ? mDayOfWeekHeaderTextColorSelected : defaultSelectedColor;
            final int unselectedColor = mDayOfWeekHeaderTextColorUnselected != 0
                    ? mDayOfWeekHeaderTextColorUnselected : defaultUnselectedColor;
            mDayOfWeekView.setTextColor(createColorStateList(selectedColor, unselectedColor));
        }

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
                setCancelable(true);
                if (mCurrentView != viewIndex) {
                    updateHeaderSelectedView(MONTH_AND_DAY_VIEW);
                    mAnimator.setDisplayedChild(MONTH_AND_DAY_VIEW);
                    mCurrentView = viewIndex;
                }

                String dayString = formatDate(mCalendar, DateUtils.FORMAT_SHOW_DATE);
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
                mDayOfWeekView.setSelected(true);  // Not dependent on locale ordering of (MD) and Y
                mFirstTextView.setSelected(mLocaleMonthDayIndex == 0);
                mSecondTextView.setSelected(mLocaleMonthDayIndex != 0);
                break;
            case YEAR_VIEW:
                mDayOfWeekView.setSelected(false); // Not dependent on locale ordering of (MD) and Y
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
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_YEAR;
        return formatDate(calendar, flags);
    }

    private static String formatMonthAndDay(Calendar calendar) {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_NO_YEAR;
        return formatDate(calendar, flags);
    }

    private String extractYearFromFormattedDate(String formattedDate, String monthAndDay) {
        final String year = YEAR_FORMAT.format(mCalendar.getTime());
        for (String part : formattedDate.split(monthAndDay)) {
            if (part.contains(year)) {
                return part;
            }
        }
        // We will NEVER reach here, as long as the parameters are valid strings.
        // We don't want this because it is not localized.
        return year;
    }

    private void updateDisplay(boolean announce) {
        if (mDayOfWeekView != null) {
            mDayOfWeekView.setText(mCalendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.LONG, Locale.getDefault()));
        }
        String fullDate = formatMonthDayYear(mCalendar);
        String monthAndDay = formatMonthAndDay(mCalendar);
        String year = YEAR_FORMAT.format(mCalendar.getTime());

        int yearStart = fullDate.indexOf(year);
        int yearEnd = yearStart + year.length();
        int monthDayStart = fullDate.indexOf(monthAndDay);
        int monthDayEnd = monthDayStart + monthAndDay.length();

        boolean processed = false;
        if (monthDayStart != -1 && yearStart != -1) {
            if (mLocaleMonthDayIndex < mLocaleYearIndex) {
                if (yearStart - monthDayEnd <= 2) {
                    monthAndDay = fullDate.substring(0, yearStart);
                    year = fullDate.substring(yearStart, fullDate.length());
                    processed = true;
                }
            } else {
                if (monthDayStart - yearEnd <= 2) {
                    year = fullDate.substring(0, monthDayStart);
                    monthAndDay = fullDate.substring(monthDayStart, fullDate.length());
                    processed = true;
                }
            }
        } else {
            // Some locales format the standalone month-day or standalone year differently
            // than it appears in the full date. For instance, Turkey is one such locale.
            // TODO: You may want to consider making localized string resources of the
            // pattern strings used to format the (MD) and (Y) parts separately.
            //
            // We can't compare the relative indices of (MD) and (Y) determined earlier,
            // because the results are dubious if we're here.
            // It is appropriate to assume yearStart != -1. The case where the raw year
            // is NOT present in the full date string is hard to imagine. As such,
            // even though monthDayStart == -1, we can still determine the relative indices
            // of (MD) and (Y) as follows.
            //
            // If yearStart is non-zero positive, then we can probably guess monthDayStart
            // comes before the former.
            if (yearStart > 0) {
                monthAndDay = fullDate.substring(0, yearStart);
                year = fullDate.substring(yearStart, fullDate.length());
                mLocaleMonthDayIndex = 0;
                mLocaleYearIndex = 1;
            } else {
                year = fullDate.substring(0, yearEnd);
                monthAndDay = fullDate.substring(yearEnd, fullDate.length());
                mLocaleYearIndex = 0;
                mLocaleMonthDayIndex = 1;
            }
            processed = true;
        }

        // Year delimiters longer than 2 characters, fall back on pre-2.1.1 implementation.
        if (!processed) {
            // The month-day is already formatted appropriately
            year = extractYearFromFormattedDate(fullDate, monthAndDay);
        }

        mFirstTextView.setText(mLocaleMonthDayIndex == 0 ? monthAndDay : year);
        mSecondTextView.setText(mLocaleMonthDayIndex == 0 ? year : monthAndDay);

        // Accessibility.
        long millis = mCalendar.getTimeInMillis();
        mAnimator.setDateMillis(millis);
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
        String monthAndDayText = formatDate(millis, flags);
        mMonthDayYearView.setContentDescription(monthAndDayText);

        if (announce) {
            flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR;
            String fullDateText = formatDate(millis, flags);
            Utils.tryAccessibilityAnnounce(mAnimator, fullDateText);
        }
    }

    /**
     * Use this to set the day that a week should start on.
     * @param startOfWeek A value from {@link Calendar#SUNDAY SUNDAY}
     *                    through {@link Calendar#SATURDAY SATURDAY}
     */
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

    /**
     * Sets the range of years to be displayed by this date picker. If a {@link #setMinDate(Calendar)
     * minimal date} and/or {@link #setMaxDate(Calendar) maximal date} were set, dates in the
     * specified range of years that lie outside of the minimal and maximal dates will be disallowed
     * from being selected.
     * <em>This does NOT change the minimal date's year or the maximal date's year.</em>
     *
     * @param startYear the start of the year range
     * @param endYear the end of the year range
     */
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
     * Sets the minimal date that can be selected in this date picker. Dates before (but not including)
     * the specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object set to the year, month, day desired as the mindate.
     */
    public void setMinDate(Calendar calendar) {
        mMinDate = calendar;

        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    /**
     * @return The minimal date supported by this date picker. Null if it has not been set.
     */
    @Nullable
    @Override
    public Calendar getMinDate() {
        return mMinDate;
    }

    /**
     * Sets the maximal date that can be selected in this date picker. Dates after (but not including)
     * the specified date will be disallowed from being selected.
     *
     * @param calendar a Calendar object set to the year, month, day desired as the maxdate.
     */
    public void setMaxDate(Calendar calendar) {
        mMaxDate = calendar;

        if (mDayPickerView != null) {
            mDayPickerView.onChange();
        }
    }

    /**
     * @return The maximal date supported by this date picker. Null if it has not been set.
     */
    @Nullable
    @Override
    public Calendar getMaxDate() {
        return mMaxDate;
    }

    public void setOnDateSetListener(OnDateSetListener listener) {
        mCallBack = listener;
    }

    /**
     * Set the color of the header text when it is selected.
     */
    public final void setHeaderTextColorSelected(@ColorInt int color) {
        mHeaderTextColorSelected = color;
    }

    /**
     * Set the color of the header text when it is not selected.
     */
    public final void setHeaderTextColorUnselected(@ColorInt int color) {
        mHeaderTextColorUnselected = color;
    }

    /**
     * Set the color of the day-of-week header text when it is selected.
     */
    public final void setDayOfWeekHeaderTextColorSelected(@ColorInt int color) {
        mDayOfWeekHeaderTextColorSelected = color;
    }

    /**
     * Set the color of the day-of-week header text when it is not selected.
     */
    public final void setDayOfWeekHeaderTextColorUnselected(@ColorInt int color) {
        mDayOfWeekHeaderTextColorUnselected = color;
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
        if (v.getId() == R.id.bsp_date_picker_second_textview) {
            setCurrentView(mLocaleMonthDayIndex == 0 ? YEAR_VIEW : MONTH_AND_DAY_VIEW);
        } else if (v.getId() == R.id.bsp_date_picker_first_textview) {
            setCurrentView(mLocaleMonthDayIndex == 0 ? MONTH_AND_DAY_VIEW : YEAR_VIEW);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mCurrentView == YEAR_VIEW && v == mYearPickerView
                && event.getY() >= mYearPickerView.getTop()
                && event.getY() <= mYearPickerView.getBottom()) {
            setCancelable(false);
            return mYearPickerView.onTouchEvent(event);
        }
        setCancelable(true);
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        setCancelable(scrollState == SCROLL_STATE_IDLE);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Do nothing.
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
        if (mSelectedDay == null) {
            mSelectedDay = new CalendarDay(mCalendar);
        } else {
            mSelectedDay.setDay(mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        return mSelectedDay;
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
        return R.layout.bsp_date_picker_dialog;
    }

    private static ColorStateList createColorStateList(int selectedColor, int unselectedColor) {
        final int[][] states = {
                { android.R.attr.state_selected },
                { -android.R.attr.state_selected },
                { /* default state */}
        };
        final int[] colors = { selectedColor, unselectedColor, unselectedColor };
        return new ColorStateList(states, colors);
    }

    public static class Builder extends BottomSheetPickerDialog.Builder {
        final OnDateSetListener mListener;
        final int mYear, mMonthOfYear, mDayOfMonth;

        private int mWeekStart = Calendar.getInstance().getFirstDayOfWeek();
        private int mMinYear = DEFAULT_START_YEAR;
        private int mMaxYear = DEFAULT_END_YEAR;
        private @Nullable Calendar mMinDate;
        private @Nullable Calendar mMaxDate;
        
        private int mHeaderTextColorSelected;
        private int mHeaderTextColorUnselected;
        private int mDayOfWeekHeaderTextColorSelected;
        private int mDayOfWeekHeaderTextColorUnselected;

        /**
         * @param listener    How the parent is notified that the date is set.
         * @param year        The initial year of the dialog.
         * @param monthOfYear The initial month of the dialog.
         * @param dayOfMonth  The initial day of the dialog.
         */
        public Builder(OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
            mListener = listener;
            mYear = year;
            mMonthOfYear = monthOfYear;
            mDayOfMonth = dayOfMonth;
        }
        
        /**
         * Use this to set the day that a week should start on.
         * @param startOfWeek A value from {@link Calendar#SUNDAY SUNDAY}
         *                    through {@link Calendar#SATURDAY SATURDAY}
         */
        public Builder setFirstDayOfWeek(int startOfWeek) {
            mWeekStart = startOfWeek;
            return this;
        }

        /**
         * Sets the range of years to be displayed by this date picker. If a {@link #setMinDate(Calendar)
         * minimal date} and/or {@link #setMaxDate(Calendar) maximal date} were set, dates in the
         * specified range of years that lie outside of the minimal and maximal dates will be disallowed
         * from being selected.
         * <em>This does NOT change the minimal date's year or the maximal date's year.</em>
         *
         * @param startYear the start of the year range
         * @param endYear the end of the year range
         */
        public Builder setYearRange(int startYear, int endYear) {
            mMinYear = startYear;
            mMaxYear = endYear;
            return this;
        }

        /**
         * Sets the minimal date that can be selected in this date picker. Dates before (but not including)
         * the specified date will be disallowed from being selected.
         *
         * @param calendar a Calendar object set to the year, month, day desired as the mindate.
         */
        public Builder setMinDate(Calendar calendar) {
            mMinDate = calendar;
            return this;
        }

        /**
         * Sets the maximal date that can be selected in this date picker. Dates after (but not including)
         * the specified date will be disallowed from being selected.
         *
         * @param calendar a Calendar object set to the year, month, day desired as the maxdate.
         */
        public Builder setMaxDate(Calendar calendar) {
            mMaxDate = calendar;
            return this;
        }

        /**
         * Set the color of the header text when it is selected.
         */
        public Builder setHeaderTextColorSelected(@ColorInt int color) {
            mHeaderTextColorSelected = color;
            return this;
        }

        /**
         * Set the color of the header text when it is not selected.
         */
        public Builder setHeaderTextColorUnselected(@ColorInt int color) {
            mHeaderTextColorUnselected = color;
            return this;
        }

        /**
         * Set the color of the day-of-week header text when it is selected.
         */
        public Builder setDayOfWeekHeaderTextColorSelected(@ColorInt int color) {
            mDayOfWeekHeaderTextColorSelected = color;
            return this;
        }

        /**
         * Set the color of the day-of-week header text when it is not selected.
         */
        public Builder setDayOfWeekHeaderTextColorUnselected(@ColorInt int color) {
            mDayOfWeekHeaderTextColorUnselected = color;
            return this;
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
        protected final void super_build(@NonNull BottomSheetPickerDialog dialog) {
            super.super_build(dialog);
            // This is here instead of in build() so that BottomSheetDatePickerDialog
            // can call up to set these attributes.
            build(dialog);
        }

        @Override
        public DatePickerDialog build() {
            DatePickerDialog dialog = newInstance(mListener, mYear, mMonthOfYear, mDayOfMonth);
            super_build(dialog);
            return dialog;
        }

        /** Builds this class's attributes. */
        private void build(@NonNull BottomSheetPickerDialog dialog) {
            DatePickerDialog datePickerDialog = (DatePickerDialog) dialog;
            datePickerDialog.setHeaderTextColorSelected(mHeaderTextColorSelected);
            datePickerDialog.setHeaderTextColorUnselected(mHeaderTextColorUnselected);
            datePickerDialog.setDayOfWeekHeaderTextColorSelected(mDayOfWeekHeaderTextColorSelected);
            datePickerDialog.setDayOfWeekHeaderTextColorUnselected(mDayOfWeekHeaderTextColorUnselected);
            datePickerDialog.setFirstDayOfWeek(mWeekStart);
            if (mMinDate != null) {
                datePickerDialog.setMinDate(mMinDate);
            }
            if (mMaxDate != null) {
                datePickerDialog.setMaxDate(mMaxDate);
            }
            datePickerDialog.setYearRange(mMinYear, mMaxYear);
        }
    }
}
