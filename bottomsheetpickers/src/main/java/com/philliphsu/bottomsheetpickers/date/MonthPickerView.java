package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;
import com.philliphsu.bottomsheetpickers.date.MonthAdapter.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import static android.support.v4.content.ContextCompat.getColor;
import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.MONTH_NAVIGATION_BAR_SIZE;

/**
 * Grid view of selectable months.
 */
final class MonthPickerView extends View {
    private static final String TAG = "MonthPickerView";
    
    private static final int NUM_COLUMNS = 3;
    private static final int NUM_ROWS = 4;
    private static final int MONTH_SEPARATOR_WIDTH = 1;

    private static int MONTH_LABEL_TEXT_SIZE;
    private static int MONTH_SELECTED_CIRCLE_SIZE;

    // affects the padding on the sides of this view
    private int mEdgePadding = 0;
    
    private Paint mMonthLabelPaint;
    private Paint mSelectedCirclePaint;

    // Quick reference to the width of this view, matches parent
    private int mWidth;
    private int mRowHeight;
    private int mSelectedMonth = -1;
    // The presently selected year in the date picker
    private int mYear;
    // The presently selected day in the date picker
    private int mDayOfMonth;
    // The month of the current date
    private final int mCurrentMonth;
    // The year of the current date
    private final int mCurrentYear;

    private final String[] mShortMonthLabels;

    @Nullable  // Created only when a DatePickerController is set.
    private DateRangeHelper mDateRangeHelper;
    @Nullable
    private OnMonthClickListener mOnMonthClickListener;

    private int mNormalTextColor;
    private int mCurrentMonthTextColor;
    private int mDisabledMonthTextColor;
    private int mSelectedMonthTextColor;

    public MonthPickerView(Context context) {
        this(context, null);
    }

    public MonthPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();

        mShortMonthLabels = new DateFormatSymbols().getShortMonths();

        mNormalTextColor = getColor(context, R.color.text_color_primary_light);
        // Same as background color
        mSelectedMonthTextColor = getColor(context, R.color.date_picker_view_animator);
        mCurrentMonthTextColor = Utils.getThemeAccentColor(context);
        mDisabledMonthTextColor = getColor(context, R.color.text_color_disabled_light);

        Calendar now = Calendar.getInstance();
        mCurrentMonth = now.get(Calendar.MONTH);
        mCurrentYear = now.get(Calendar.YEAR);

        MONTH_LABEL_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.month_picker_month_label_size);
        MONTH_SELECTED_CIRCLE_SIZE = res.getDimensionPixelSize(R.dimen.month_select_circle_radius);

        mRowHeight = (res.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height)
                - MONTH_NAVIGATION_BAR_SIZE) / NUM_ROWS;
        mEdgePadding = res.getDimensionPixelSize(R.dimen.month_view_edge_padding);

        // TODO: Set up accessibility components.
        // Sets up any standard paints that will be used
        initView();
    }

    /**
     * Sets all the parameters for displaying the months.
     * <p>
     * Parameters have a default value and will only update if a new value is
     * included, except for focus month, which will always default to no focus
     * month if no value is passed in. The only required parameter is the week
     * start.
     */
    void setDisplayParams(CalendarDay day) {
        adjustDayInMonthIfNeeded(day);
        mSelectedMonth = day.month;
        mYear = day.year;
        mDayOfMonth = day.day;

        // Invalidate cached accessibility information.
//        mTouchHelper.invalidateRoot();
    }

    // If the newly selected month / year does not contain the currently selected day number,
    // change the selected day number to the last day of the selected month or year.
    //      e.g. Switching from Mar to Apr when Mar 31 is selected -> Apr 30
    //      e.g. Switching from 2012 to 2013 when Feb 29, 2012 is selected -> Feb 28, 2013
    private void adjustDayInMonthIfNeeded(CalendarDay cd) {
        int daysInMonth = Utils.getDaysInMonth(cd.month, cd.year);
        if (cd.day > daysInMonth) {
            cd.day = daysInMonth;
        }
    }

    public void setDatePickerController(DatePickerController controller) {
        mDateRangeHelper = new DateRangeHelper(controller);
    }

    public void setOnMonthClickListener(@Nullable OnMonthClickListener onMonthClickListener) {
        mOnMonthClickListener = onMonthClickListener;
    }

    void setTheme(Context context, boolean themeDark) {
        if (themeDark) {
            mNormalTextColor = getColor(context, R.color.text_color_primary_dark);
            mSelectedMonthTextColor = getColor(context, R.color.dark_gray);
            mDisabledMonthTextColor = getColor(context, R.color.text_color_disabled_dark);
            initView();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                final int month = getMonthFromLocation(event.getX(), event.getY());
                if (month >= 0) {
                    onMonthClick(month);
                }
                break;
        }
        return true;
    }

    /**
     * Sets up the text and style properties for painting. Override this if you
     * want to use a different paint.
     */
    protected void initView() {
        mMonthLabelPaint = new Paint();
        mMonthLabelPaint.setAntiAlias(true);
        mMonthLabelPaint.setTextSize(MONTH_LABEL_TEXT_SIZE);
        mMonthLabelPaint.setStyle(Style.FILL);
        mMonthLabelPaint.setTextAlign(Align.CENTER);
        mMonthLabelPaint.setFakeBoldText(false);

        mSelectedCirclePaint = new Paint();
        mSelectedCirclePaint.setFakeBoldText(true);
        mSelectedCirclePaint.setAntiAlias(true);
        mSelectedCirclePaint.setColor(mCurrentMonthTextColor);
        mSelectedCirclePaint.setTextAlign(Align.CENTER);
        mSelectedCirclePaint.setStyle(Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mRowHeight * NUM_ROWS
                + MONTH_NAVIGATION_BAR_SIZE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;

        // Invalidate cached accessibility information.
//        mTouchHelper.invalidateRoot();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMonthLabels(canvas);
    }

    public int getYear() {
        return mYear;
    }

    public int getDayOfMonth() {
        return mDayOfMonth;
    }

    private void drawMonthLabels(Canvas canvas) {
        int y = (((mRowHeight + MONTH_LABEL_TEXT_SIZE) / 2) - MONTH_SEPARATOR_WIDTH);
        final float monthWidthHalf = (mWidth - mEdgePadding * 2) / (NUM_COLUMNS * 2.0f);
        int col = 0;
        for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
            final int x = (int)((2 * col + 1) * monthWidthHalf + mEdgePadding);
            drawMonthLabel(canvas, mYear, month, mDayOfMonth, x, y);
            col++;
            if (col == NUM_COLUMNS) {
                col = 0;
                y += mRowHeight;
            }
        }
    }

    private void drawMonthLabel(Canvas canvas, int year, int month, int day, int x, int y) {
        if (mSelectedMonth == month) {
            canvas.drawCircle(x , y - (MONTH_LABEL_TEXT_SIZE / 3), MONTH_SELECTED_CIRCLE_SIZE,
                    mSelectedCirclePaint);
        }

        // TODO: From MonthPicker, navigate to and select a year in year picker. You will see that
        // the navigation arrows are made visible again! Make sure they stay invisible.

        // If we have a mindate or maxdate, gray out the month if it's outside the range.
        // If the date range helper has not been created, just let the runtime throw an NPE.
        if (mDateRangeHelper != null && mDateRangeHelper.isOutOfRange(year, month, day)) {
            mMonthLabelPaint.setColor(mDisabledMonthTextColor);
        } else if (mCurrentYear == year && mCurrentMonth == month) {
            mMonthLabelPaint.setFakeBoldText(true);
            mMonthLabelPaint.setColor(mSelectedMonth == month ? mSelectedMonthTextColor : mCurrentMonthTextColor);
        } else {
            mMonthLabelPaint.setFakeBoldText(mSelectedMonth == month);
            mMonthLabelPaint.setColor(mSelectedMonth == month ? mSelectedMonthTextColor : mNormalTextColor);
        }
        canvas.drawText(mShortMonthLabels[month], x, y, mMonthLabelPaint);
    }

    /**
     * Calculates the month that the given x position is in. 
     * Returns the month or -1 if the position wasn't in a month.
     *
     * @param x The x position of the touch event
     * @return The month number, or -1 if the position wasn't in a month
     */
    public int getMonthFromLocation(float x, float y) {
        final int month = getInternalMonthFromLocation(x, y);
        if (month < Calendar.JANUARY || month > Calendar.DECEMBER) {
            return -1;
        }
        return month;
    }

    /**
     * Calculates the month that the given x position is in, accounting for week
     * number.
     *
     * @param x The x position of the touch event
     * @return The month number
     */
    protected int getInternalMonthFromLocation(float x, float y) {
        int monthStart = mEdgePadding;
        if (x < monthStart || x > mWidth - mEdgePadding) {
            // Out of edge boundaries
            return -1;
        }
        int row = (int) (y / mRowHeight);
        // Selection is (x - start) / (pixels/month) == (x -s) * month / pixels
        int column = (int) ((x - monthStart) * NUM_COLUMNS / (mWidth - mEdgePadding * 2));

        int month = column;
        month += row * NUM_COLUMNS;
        return month;
    }
    
    /**
     * Called when the user clicks on a month. Handles callbacks to the
     * {@link MonthPickerView.OnMonthClickListener} if one is set.
     * <p/>
     * If the month is out of the range set by minDate and/or maxDate, this is a no-op.
     *
     * @param month The month that was clicked
     */
    private void onMonthClick(int month) {
        // If the min / max date are set, only process the click if it's a valid selection.
        if (mDateRangeHelper != null && mDateRangeHelper.isOutOfRange(mYear, month, mDayOfMonth)) {
            return;
        }

        if (mOnMonthClickListener != null) {
            // TODO: I don't think we need to pass back every single field.
            mOnMonthClickListener.onMonthClick(this, month);
        }

        // This is a no-op if accessibility is turned off.
//        mTouchHelper.sendEventForVirtualView(month, AccessibilityEvent.TYPE_VIEW_CLICKED);
    }
    
    interface OnMonthClickListener {
        void onMonthClick(MonthPickerView view, int month);
    }
}
