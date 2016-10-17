package com.philliphsu.bottomsheetpickers.date;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;
import com.philliphsu.bottomsheetpickers.Utils;

/**
 * Alias for {@link TextViewWithHighlightIndicator} and {@link TextViewWithCircularIndicator}
 * for increased modularity.
 *
 * By holding references to this type instead of the more specific subtypes,
 * we don't need to change much code in {@link YearPickerView} if we ever
 * revert to using the latter subtype.
 */
abstract class TextViewWithIndicator extends TextView {

    int mDefaultTextColor;

    public TextViewWithIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public abstract void drawIndicator(boolean drawIndicator);

    void setTheme(Context context, boolean themeDark) {
        mDefaultTextColor = ContextCompat.getColor(context, themeDark?
                R.color.text_color_primary_dark : R.color.text_color_primary_light);
        setTextColor(mDefaultTextColor);
        if (themeDark) {
            int selectableItemBg = ContextCompat.getColor(context, R.color.selectable_item_background_dark);
            Utils.setColorControlHighlight(this, selectableItemBg);
        }
    }
}
