package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * A derivative of {@link TextViewWithCircularIndicator} for use in {@link
 * YearPickerView}, that matches the style in the Material Design spec.
 */
public class TextViewWithHighlightIndicator extends TextViewWithIndicator {

    private final String mItemIsSelectedText;
    private final float  mDefaultTextSize;
    private final float  mSelectedTextSize;

    private int     mHighlightColor;
    private int     mDisabledTextColor;
    private boolean mDrawHighlight;

    public TextViewWithHighlightIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mHighlightColor = Utils.getThemeAccentColor(context);
        mItemIsSelectedText = context.getResources().getString(R.string.bsp_item_is_selected);
        mDefaultTextSize = getTextSize();
        mSelectedTextSize = res.getDimension(R.dimen.bsp_year_label_selected_text_size);
        mDisabledTextColor = getColor(context, R.color.bsp_text_color_disabled_light);
    }

    public void drawIndicator(boolean drawHighlight) {
        mDrawHighlight = drawHighlight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setTextColor(isEnabled() ? (mDrawHighlight ? mHighlightColor : mDefaultTextColor) :
                mDisabledTextColor);
        boolean drawHighlight = isEnabled() && mDrawHighlight;
        setTextSize(TypedValue.COMPLEX_UNIT_PX, drawHighlight ? mSelectedTextSize : mDefaultTextSize);
        setTypeface(drawHighlight ? Utils.SANS_SERIF_LIGHT_BOLD : Typeface.DEFAULT);
    }

    @Override
    public CharSequence getContentDescription() {
        CharSequence itemText = getText();
        if (mDrawHighlight) {
            return String.format(mItemIsSelectedText, itemText);
        } else {
            return itemText;
        }
    }

    @Override
    void setTheme(Context context, boolean themeDark) {
        super.setTheme(context, themeDark);
        mDisabledTextColor = getColor(context, themeDark?
                R.color.bsp_text_color_disabled_dark : R.color.bsp_text_color_disabled_light);
    }

    void setHighlightIndicatorColor(int highlightColor) {
        mHighlightColor = highlightColor;
    }
}