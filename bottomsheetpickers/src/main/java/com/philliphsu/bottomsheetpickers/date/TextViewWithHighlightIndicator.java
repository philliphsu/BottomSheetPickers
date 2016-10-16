package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

/**
 * A derivative of {@link TextViewWithCircularIndicator} for use in {@link
 * YearPickerView}, that matches the style in the Material Design spec.
 */
public class TextViewWithHighlightIndicator extends TextViewWithIndicator {

    private final Rect   mTextBounds = new Rect();
    private final Paint  mHighlightPaint = new Paint();

    private final int    mHighlightColor;
    private final String mItemIsSelectedText;
    private final float  mSelectedTextSize;

    private boolean mDrawHighlight;

    public TextViewWithHighlightIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = context.getResources();
        mHighlightColor = Utils.getThemeAccentColor(context);
        mItemIsSelectedText = context.getResources().getString(R.string.item_is_selected);
        mSelectedTextSize = res.getDimension(R.dimen.year_label_selected_text_size);
        // TODO: Dark variant for dark theme.
        int defaultTextColor = ContextCompat.getColor(context, R.color.text_color_primary_light);

        int[][] states = {
                {  android.R.attr.state_pressed },
                { -android.R.attr.state_pressed, -android.R.attr.state_selected }
        };
        int[] colors = { mHighlightColor, defaultTextColor};
        setTextColor(new ColorStateList(states, colors));

        init();
    }

    private void init() {
        mHighlightPaint.setFakeBoldText(true);
        mHighlightPaint.setAntiAlias(true);
        mHighlightPaint.setColor(mHighlightColor);
        mHighlightPaint.setTextAlign(Paint.Align.CENTER);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setTextSize(mSelectedTextSize);
    }

    public void drawIndicator(boolean drawHighlight) {
        mDrawHighlight = drawHighlight;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawHighlight) {
            final String text = getText().toString();
            // See http://stackoverflow.com/a/24969713/5055032
            mHighlightPaint.getTextBounds(text, 0, text.length(), mTextBounds);
            float cx = getWidth() / 2 - mTextBounds.exactCenterX();
            float cy = getHeight() / 2 - mTextBounds.exactCenterY();
            canvas.drawText(text, cx, cy, mHighlightPaint);
        }
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
