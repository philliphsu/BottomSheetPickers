package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

import java.text.DateFormatSymbols;
import java.util.Locale;

import static com.philliphsu.bottomsheetpickers.date.PagingDayPickerView.MONTH_NAVIGATION_BAR_SIZE;

/**
 * Created by Phillip Hsu on 12/6/2016.
 */

final class MonthPickerView extends View {
    private static final String TAG = "MonthPickerView";
    
    private static final int NUM_COLUMNS = 3;
    private static final int NUM_ROWS = 4;
    
    private static int MONTH_LABEL_TEXT_SIZE;
    private static int MONTH_SELECTED_CIRCLE_SIZE;

    private DatePickerController mController;

    // affects the padding on the sides of this view
    private int mEdgePadding = 0;
    
    private String mMonthLabelTypeface;
    private Paint mMonthLabelPaint;

    // Quick reference to the width of this view, matches parent
    private int mWidth;
    private int mRowHeight;
    private int mSelectedMonth = -1;
    
    private final DateFormatSymbols mSymbols;

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

        mSymbols = new DateFormatSymbols(Locale.getDefault());
        mMonthLabelTypeface = res.getString(R.string.sans_serif);

        MONTH_LABEL_TEXT_SIZE = res.getDimensionPixelSize(R.dimen.month_label_size);
        MONTH_SELECTED_CIRCLE_SIZE = res.getDimensionPixelSize(R.dimen.month_select_circle_radius);

        mRowHeight = (res.getDimensionPixelOffset(R.dimen.date_picker_view_animator_height)
                - MONTH_NAVIGATION_BAR_SIZE) / NUM_ROWS;
    }
}
