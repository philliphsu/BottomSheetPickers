package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

/**
 * A derivative of {@link TextViewWithCircularIndicator} for use in {@link
 * YearPickerView}, that matches the style in the Material Design spec.
 */
public class TextViewWithHighlightIndicator extends TextViewWithIndicator {

    // --------------------------------------------------------------------------------------------
    // TODO: Consider moving this to Utils so other classes may make use of the light typeface.
    private static final Typeface HIGHLIGHT_TYPEFACE;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            HIGHLIGHT_TYPEFACE = Typeface.create("sans-serif-light", Typeface.BOLD);
        } else {
            HIGHLIGHT_TYPEFACE = Typeface.DEFAULT_BOLD;
        }
    }
    // --------------------------------------------------------------------------------------------

    private final int    mHighlightColor;
    private final String mItemIsSelectedText;
    private final float  mDefaultTextSize;
    private final float  mSelectedTextSize;

    private boolean mDrawHighlight;

    public TextViewWithHighlightIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mHighlightColor = Utils.getThemeAccentColor(context);
        mItemIsSelectedText = context.getResources().getString(R.string.item_is_selected);
        mDefaultTextSize = getTextSize();
        mSelectedTextSize = res.getDimension(R.dimen.year_label_selected_text_size);
    }

    public void drawIndicator(boolean drawHighlight) {
        mDrawHighlight = drawHighlight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setTextColor(mDrawHighlight ? mHighlightColor : mDefaultTextColor);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mDrawHighlight ? mSelectedTextSize : mDefaultTextSize);
        setTypeface(mDrawHighlight ? HIGHLIGHT_TYPEFACE : Typeface.DEFAULT);
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

}
