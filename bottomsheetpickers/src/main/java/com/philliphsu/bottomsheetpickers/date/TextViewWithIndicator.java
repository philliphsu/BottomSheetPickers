package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Alias for {@link TextViewWithHighlightIndicator} and {@link TextViewWithCircularIndicator}
 * for increased modularity.
 *
 * By holding references to this type instead of the more specific subtypes,
 * we don't need to change much code in {@link YearPickerView} if we ever
 * revert to using the latter subtype.
 */
abstract class TextViewWithIndicator extends TextView {

    public TextViewWithIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void drawIndicator(boolean drawIndicator);
}
